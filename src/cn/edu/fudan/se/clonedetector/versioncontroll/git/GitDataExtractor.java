package cn.edu.fudan.se.clonedetector.versioncontroll.git;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.WorkingTreeOptions;
import org.eclipse.jgit.util.io.AutoCRLFInputStream;

import cn.edu.fudan.se.clonedetector.bean.Author;
import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.versioncontroll.DataExtractor;


/**
 * A class for extracting data from Git Repository
 * 
 * @author linyun
 * 
 */
public class GitDataExtractor extends DataExtractor {
	private Repository repo = null;
	private Git git = null;
	public GitDataExtractor(IDataAccessor dao, String repoLocation) throws IOException {
		super(dao, repoLocation);
		repo = new FileRepository(new File(this.repoLocation));
		git = new Git(repo);
	}

	/**
	 * Given a validate reporsitory path, this method extract all the relevant
	 * SCM information into database.
	 * 
	 * The relevant information includes those entities defined in
	 * cn.fudan.se.evolutionplayer.bean.
	 * 
	 * @param repoPath
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public void extract(String branch, int num) throws GitAPIException, IOException {
		List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();
		Ref targetBranch = null;
		for (Ref ref : branches) {
			if (ref.getName().equals(branch)) {
				targetBranch = ref;
				break;
			}
		}
		int count = 0;
		for (RevCommit commit : git.log().all().call()) {
			if (count == num) {
				break;
			}

			if (!isInBranch(commit, repo, targetBranch)) {
				continue;
			}
			String str = "200311111200";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			long millionSeconds = 0;
			try {
				 millionSeconds = sdf.parse(str).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//����zc
			if(commit.getCommitTime() > millionSeconds/1000)
				continue;
			Commit com = retrieveCommit(commit);
			
			retrieveChanges(commit, com);
			retrieveFiles(commit, com);
			count++;
		}
	}

	private void retrieveFiles(RevCommit commit, Commit com) throws GitAPIException, IOException {
		/*
		 * now use a TreeWalk to iterate over all files in the Tree recursively,
		 * we can set Filters to narrow down the results if needed
		 */
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);

		while (treeWalk.next()) {
			System.out.println("found: " + treeWalk.getPathString());
			@SuppressWarnings("static-access")
			TreeWalk w = treeWalk.forPath(repo, treeWalk.getPathString(), tree);
			ObjectId id = w.getObjectId(0);
			InputStream is = open(id, repo);
			byte[] byteArray = IOUtils.toByteArray(is);
			//saveSourceFile(com, treeWalk.getPathString(), byteArray);
		}
		
	}

	private void retrieveChanges(RevCommit commit, Commit com) throws IOException, GitAPIException {
		RevCommit prevCommit = getPreviousCommit(commit);

		if (prevCommit != null) {
			ObjectReader reader = repo.newObjectReader();
			CanonicalTreeParser currentTreeParser = new CanonicalTreeParser();
			currentTreeParser.reset(reader, commit.getTree());

			CanonicalTreeParser prevTreeParser = new CanonicalTreeParser();
			prevTreeParser.reset(reader, prevCommit.getTree());

			List<DiffEntry> diffs = git.diff().setNewTree(currentTreeParser).setOldTree(prevTreeParser).call();

			for (DiffEntry diff : diffs) {
				retrieveChange(com, diff);
			}
		}
	}

	private RevCommit getPreviousCommit(RevCommit commit) {
		RevCommit prevCommit = null;
		if (commit.getParentCount() > 0) {
			prevCommit = commit.getParent(0);
		}
		return prevCommit;
	}

	private Commit retrieveCommit(RevCommit commit) {
		String revisionID = commit.getName();

		RevCommit pre = getPreviousCommit(commit);
		String preRevisionID = pre == null ? null : pre.getName();

		String log = commit.getShortMessage();

		Date time = new Date(commit.getCommitTime() * 1000L);

		Author author = retrieveAuthor(commit);

		Commit result = saveCommit(revisionID, log, time, author, 0);
		
		result.setPreRevisionId(preRevisionID);
		dao.updateCommit(result);
		
		return result;
	}

	private void retrieveChange(Commit commit, DiffEntry diff) {
		if (!isValidFile(diff.getNewPath(), getSuffixes()))
			return;

		Change change = new Change();

		int type = 0;
		DiffEntry.ChangeType tp = diff.getChangeType();
		String oldFileName = null;
	
		// change.setCommit(commit);
		// change.setFileName(diff.getNewPath());
		// change.setOldFileName(oldFileName);

		dao.save(change);
	}
	
	private Author retrieveAuthor(RevCommit commit) {
		Author result = new Author();
	
		String name = commit.getAuthorIdent().getName();
		//String email = commit.getAuthorIdent().getEmailAddress();
		String email = system+"."+name;		
		// result.setName(name);
		// result.setId(email);
		// result.setSystem(system);
		return result;
	}
	
	/**
	 * This method is copied from eclipse EGit source code. Browsing its source
	 * code for a little bit long time, I find this method is the one I need.
	 * 
	 * @param blobId
	 * @param db
	 * @return
	 * @throws IOException
	 * @throws IncorrectObjectTypeException
	 */
	private InputStream open(ObjectId blobId, Repository db) throws IOException, IncorrectObjectTypeException {
		if (blobId == null)
			return new ByteArrayInputStream(new byte[0]);

		try {
			WorkingTreeOptions workingTreeOptions = db.getConfig().get(WorkingTreeOptions.KEY);
			switch (workingTreeOptions.getAutoCRLF()) {
			case INPUT:
				// When autocrlf == input the working tree could be either CRLF
				// or LF, i.e. the comparison
				// itself should ignore line endings.
			case FALSE:
				return db.open(blobId, Constants.OBJ_BLOB).openStream();
			case TRUE:
			default:
				return new AutoCRLFInputStream(db.open(blobId, Constants.OBJ_BLOB).openStream(), true);
			}
		} catch (MissingObjectException notFound) {
			return null;
		}
	}

	private boolean isInBranch(RevCommit commit, Repository repo, Ref targetBranch)
			throws RevisionSyntaxException, MissingObjectException, IncorrectObjectTypeException,
			AmbiguousObjectException, IOException {
		RevWalk walk = new RevWalk(repo);
		RevCommit targetCommit = walk.parseCommit(repo.resolve(commit.getName()));

		for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
			if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
				String foundInBranch = e.getValue().getName();
				if (targetBranch.getName().equals(foundInBranch)) {
					return true;
				}
			}
		}

		return false;
	}
	

}
