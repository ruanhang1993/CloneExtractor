package cn.edu.fudan.se.clonedetector.differ;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.dao.support.DaoSupport;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.bean.ChangeFile;
import cn.edu.fudan.se.clonedetector.bean.Commit;
import cn.edu.fudan.se.clonedetector.bean.CommitLanguage;
import cn.edu.fudan.se.clonedetector.bean.Compare;
import cn.edu.fudan.se.clonedetector.bean.CompareLanguage;
import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;
import cn.edu.fudan.se.clonedetector.comparecloneinchange.CloneInChangeExtractor;
import cn.edu.fudan.se.clonedetector.comparecloneinchange.ICloneInChange;
import cn.edu.fudan.se.clonedetector.dao.IDataAccessor;
import cn.edu.fudan.se.clonedetector.dao.hibernate.HibernateDao;

public class ChangeSaveNotify implements IDifferOutcomeNotify {

	@Override
	public void notifyChange(List<Change> changeList, int preCommitId, int commitId, IDataAccessor dataAccessor) {
		System.out.println("preCommitId is " + preCommitId);
		System.out.println("CommitId is " + commitId);
//		IDataAccessor dataAccessor = HibernateDao.getInstance();
		Compare compare = new Compare();
		Commit prec = dataAccessor.getCommitById(preCommitId);
		Commit commit = dataAccessor.getCommitById(commitId);
		int r1 = Integer.parseInt(prec.getRevisionId().substring(1));
		int r2 = Integer.parseInt(commit.getRevisionId().substring(1));
		compare.setVersionSpan(r2 - r1);
		long timeSpan = commit.getCommitDate().getTime() - prec.getCommitDate().getTime();
//		Date d = new Date(timeSpan);
		compare.setPreRevisionId(prec.getRevisionId());
		compare.setRevisionId(commit.getRevisionId());
//		compare.setCommitId(commit.getCommitId());
//		compare.setPreCommitId(prec.getCommitId());
		compare.setCommitId(commitId);
		compare.setPreCommitId(preCommitId);
		compare.setProjectId(commit.getProjectId());
		Date date = new Date();
//		compare.setCompareId(dataAccessor.getLastCompareId() + 1);
		compare.setTimeSpan(""+timeSpan);
		compare.setCompareDate(date);
		compare.setProcessDate(new Date());
		dataAccessor.saveCompare(compare);
		int compareId = compare.getCompareId();
		System.out.println("changeList size is " + changeList.size());
		int j  =0;
		for (Change c : changeList) {
//			int cId = dataAccessor.getLastChangeId() + 1;
			System.out.format("retrive change : %d%n", ++j);
//			c.setChangeId(cId);
			c.setCompareId(compareId);
			if (c.getChangeType() == Change.INSERT) {
				File file = dataAccessor.getFileById(c.getFileId());
				File preFile = dataAccessor.getFileOfCommitByIdByName(preCommitId, file.getFileName());
				if (preFile != null)
					c.setOldFileId(preFile.getFileId());
				int res[] = new EvolutionAnalyse("", "./"+preCommitId+"_"+commitId).countIncompleteLineByString(c.getBeginLine(), c.getEndLine(),
						new String(file.getContent()));
				int bchloc = res[0];
				int cmchloc=res[1];
				int echloc = res[2];
				c.setEchloc(echloc);
				c.setBchloc(bchloc);
				c.setCmchloc(cmchloc);

			} else if (c.getChangeType() == Change.DELETE) {
				File prefile = dataAccessor.getFileById(c.getOldFileId());
				File file = dataAccessor.getFileOfCommitByIdByName(commitId, prefile.getFileName());
				if (file != null) {
					c.setFileId(file.getFileId());
				}
				int res[] = new EvolutionAnalyse("", "./"+preCommitId+"_"+commitId).countIncompleteLineByString(c.getBeginLine(), c.getEndLine(),
						new String(prefile.getContent()));
				int bchloc = res[0];
				int cmchloc=res[1];
				int echloc = res[2];
				c.setEchloc(echloc);
				c.setBchloc(bchloc);
				c.setCmchloc(cmchloc);
			}
			dataAccessor.saveChange(c);
		}
		ICloneInChange icic = new CloneInChangeExtractor();
		icic.CloneInChange(preCommitId, commitId, compare.getCompareId(), dataAccessor);
		List<Integer> changeFiles = dataAccessor.getDistinctFileFromChange(compare.getCompareId());
		compare.setChangeFileNum(changeFiles.size());
		compare.setChangeId(icic.returnFinalLine());
		compare.setCloneInChangeId(icic.returnEfinalLine());
		for (Integer i : changeFiles) {
			if (i == -1)
				continue;
			//System.out.println("changefile insert : " + i);
			File file = dataAccessor.getFileById(i);
			ChangeFile cf = new ChangeFile();
			cf.setFileName(file.getFileName());
			cf.setFileType(file.getFileType());
//			cf.setChangeFileId(dataAccessor.getLastChangeFileId() + 1);
			cf.setFileId(i);
			cf.setCommitId(file.getCommitId());
			cf.setCompareId(compareId);
			cf.setContent(file.getContent());
			cf.setLoc(file.getLoc());
			cf.setEloc(file.getEloc());
			cf.setBloc(file.getBloc());
			cf.setCmloc(file.getCmloc());
//			cf.setBcloc(file.getBcloc());
//			cf.setCmcloc(file.getCmcloc());
			// dataAccessor.save(cf);
			dataAccessor.updateChangeFileLoc(cf);

		}
//		String[] languages = dataAccessor.getCommitLanguageById(commitId);
		List<CommitLanguage> cls = dataAccessor.getCommitLanguageById(commitId);
		for(CommitLanguage cl:cls){
			String language = cl.getLanguage();
			Collection<String> suffixes = new ArrayList<String>();
			if(language.equalsIgnoreCase("c++")){
				suffixes.add("cpp");
				suffixes.add("c");
			}else if(language.equals("c#")){
				suffixes.add("cs");
			}else{
				suffixes.add(language);
			}
			CompareLanguage clan = new CompareLanguage(compare.getCompareId(), compare.getRevisionId(), compare.getPreRevisionId(),
					compare.getCommitId(),compare.getPreCommitId(),language);
			dataAccessor.saveCompareLanguage(clan,suffixes);			
		}
		dataAccessor.updateCompareLoc(compare);
	}

}
