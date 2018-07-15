package cn.edu.fudan.se.clonedetector.differ.lineDiffImp;

import java.util.LinkedList;

import cn.edu.fudan.se.clonedetector.bean.Change;
import cn.edu.fudan.se.clonedetector.differ.ChangeFactory;

public class DiffAdapter {

	private final int UNREAL = Integer.MAX_VALUE;

	private ChangeFileInfo oldinfo, newinfo;

	private int blocklen[];
	
	private Change currentChange;
	
	private int endLine;
	private StringBuilder changeFragment;
	
	private LinkedList<Change> changeList;
	
	private ChangeFactory changeFactory;
	private int fileId, oldFileId;

	DiffAdapter() {
	}

	public void doDiff(String[] oldFile, String[] newFile) {
		oldinfo = new ChangeFileInfo(oldFile, true);
		newinfo = new ChangeFileInfo(newFile, false);
		
		changeList = new LinkedList<Change>();
		blocklen = new int[(oldinfo.maxLine > newinfo.maxLine ? oldinfo.maxLine : newinfo.maxLine) + 2];

		transform();
		printout();
	}

	public LinkedList<Change> getChangeList() {
		return changeList;
	}
	
	public void refresh(){
		Node.panchor = null;
	}
	
	public void setFileId(int fileId, int oldFileId){
		this.fileId = fileId;
		this.oldFileId = oldFileId;
	}
	
	public void setChangeFactory(ChangeFactory changeFactory){
		this.changeFactory = changeFactory;
	}

	private void transform() {
		int oldline, newline;
		int oldmax = oldinfo.maxLine + 2; /* Count pseudolines at */
		int newmax = newinfo.maxLine + 2; /* ..front and rear of file */

		for (oldline = 0; oldline < oldmax; oldline++)
			oldinfo.other[oldline] = -1;
		for (newline = 0; newline < newmax; newline++)
			newinfo.other[newline] = -1;

		scanunique(); 
		scanafter(); 
		scanbefore(); 
		scanblocks(); 
	}

	private void scanunique() {
		int oldline, newline;
		Node psymbol;

		for (newline = 1; newline <= newinfo.maxLine; newline++) {
			psymbol = newinfo.symbol[newline];
			if (psymbol.symbolIsUnique()) { // 1 use in each file
				oldline = psymbol.getLinenum();
				newinfo.other[newline] = oldline; // record 1-1 map
				oldinfo.other[oldline] = newline;
			}
		}
		newinfo.other[0] = 0;
		oldinfo.other[0] = 0;
		newinfo.other[newinfo.maxLine + 1] = oldinfo.maxLine + 1;
		oldinfo.other[oldinfo.maxLine + 1] = newinfo.maxLine + 1;
	}

	private void scanafter() {
		int oldline, newline;

		for (newline = 0; newline <= newinfo.maxLine; newline++) {
			oldline = newinfo.other[newline];
			if (oldline >= 0) { 
				for (;;) {
					if (++oldline > oldinfo.maxLine)
						break;
					if (oldinfo.other[oldline] >= 0)
						break;
					if (++newline > newinfo.maxLine)
						break;
					if (newinfo.other[newline] >= 0)
						break;

					if (newinfo.symbol[newline] != oldinfo.symbol[oldline])
						break; // not same

					newinfo.other[newline] = oldline; // record a match
					oldinfo.other[oldline] = newline;
				}
			}
		}
	}

	private void scanbefore() {
		int oldline, newline;

		for (newline = newinfo.maxLine + 1; newline > 0; newline--) {
			oldline = newinfo.other[newline];
			if (oldline >= 0) { /* unique in each */
				for (;;) {
					if (--oldline <= 0)
						break;
					if (oldinfo.other[oldline] >= 0)
						break;
					if (--newline <= 0)
						break;
					if (newinfo.other[newline] >= 0)
						break;

					if (newinfo.symbol[newline] != oldinfo.symbol[oldline])
						break; 
					newinfo.other[newline] = oldline; // record a match
					oldinfo.other[oldline] = newline;
				}
			}
		}
	}

	private void scanblocks() {
		int oldline, newline;
		int oldfront = 0; 
		int newlast = -1; 

		for (oldline = 1; oldline <= oldinfo.maxLine; oldline++)
			blocklen[oldline] = 0;
		blocklen[oldinfo.maxLine + 1] = UNREAL; // starts a mythical blk

		for (oldline = 1; oldline <= oldinfo.maxLine; oldline++) {
			newline = oldinfo.other[oldline];
			if (newline < 0)
				oldfront = 0;
			else { 
				if (oldfront == 0)
					oldfront = oldline;
				if (newline != (newlast + 1))
					oldfront = oldline;
				++blocklen[oldfront];
			}
			newlast = newline;
		}
	}

	public static final int idle = 0, delete = 1, insert = 2, movenew = 3, moveold = 4, same = 5, change = 6;
	private int printstatus;
	private int printoldline, printnewline; // line numbers in old & new file

	private void printout() {
		printstatus = idle;
		changeFragment = new StringBuilder();
		for (printoldline = printnewline = 1;;) {
			if (printoldline > oldinfo.maxLine) {
				newconsume();
				break;
			}
			if (printnewline > newinfo.maxLine) {
				oldconsume();
				break;
			}
			if (newinfo.other[printnewline] < 0) {
				if (oldinfo.other[printoldline] < 0)
					addDeleteLine();
				else
					addInsertLine();
			} else if (oldinfo.other[printoldline] < 0)
				addDeleteLine();
			else if (blocklen[printoldline] < 0)
				skipOld();
			else if (oldinfo.other[printoldline] == printnewline)
				addSame();
			else
				addMove();
		}
	}

	private void newconsume() {
		for (;;) {
			if (printnewline > newinfo.maxLine)
				break; /* end of file */
			if (newinfo.other[printnewline] < 0)
				addInsertLine();
			else
				addMove();
		}
	}

	private void oldconsume() {
		for (;;) {
			if (printoldline > oldinfo.maxLine)
				break; /* end of file */
			printnewline = oldinfo.other[printoldline];
			if (printnewline < 0)
				addDeleteLine();
			else if (blocklen[printoldline] < 0)
				skipOld();
			else
				addMove();
		}
	}
	
	private void produceChange(int type){
		int beginLine = type==insert? printnewline : printoldline;
		currentChange = changeFactory.produceChange(beginLine, type, fileId, oldFileId);
	}
	
	private void endChange(){
		if(currentChange == null){
			return;
		}
		if(changeFragment.length()-1>0)
			changeFragment.deleteCharAt(changeFragment.length()-1);
		else
			System.out.println("changeFragment.length() = 0");
			
		currentChange.setChangeFragment(changeFragment.toString());
		changeFragment.delete(0, changeFragment.length());
		currentChange.setEndLine(endLine);
		
		this.changeList.add(currentChange);
		currentChange = null;
	}
	
	private void addInsertLine(){
		if(printstatus != insert){
			this.endChange();
			this.produceChange(insert);
		}
		printstatus = insert;
		changeFragment.append(newinfo.symbol[printnewline].getSymbol()+"\n");
		this.endLine = printnewline;
		printnewline++;
	}
	
	private void addDeleteLine(){
		if(printstatus != delete){
			this.endChange();
			this.produceChange(delete);
		}
		printstatus = delete;
		changeFragment.append(oldinfo.symbol[printoldline].getSymbol()+"\n");
		this.endLine = printoldline;
		printoldline++;
	}

	private void skipOld() {
		printstatus = idle;
		this.endChange();
		for (;;) {
			if (++printoldline > oldinfo.maxLine)
				break;
			if (oldinfo.other[printoldline] < 0)
				break; 
			if (blocklen[printoldline] != 0)
				break;
		}
	}

	private void skipNew() {
		int oldline;
		printstatus = idle;
		for (;;) {
			if (++printnewline > newinfo.maxLine)
				break; 
			oldline = newinfo.other[printnewline];
			if (oldline < 0)
				break; 
			if (blocklen[oldline] != 0)
				break;
		}
	}

	private void addSame() {
		int count;
		printstatus = idle;
		this.endChange();
		if (newinfo.other[printnewline] != printoldline) {
			System.err.println("BUG IN LINE REFERENCING");
			System.exit(1);
		}
		count = blocklen[printoldline];
		printoldline += count;
		printnewline += count;
	}

	private void addMove() {
		int oldblock = blocklen[printoldline];
		int newother = newinfo.other[printnewline];
		int newblock = blocklen[newother];

		if (newblock < 0)
			skipNew();
		else if (oldblock >= newblock) { 
			blocklen[newother] = -1; 
			for (; newblock > 0; newblock--)
				this.addInsertLine();
			printstatus = idle;

		} else 
			skipOld(); 
	}

	class ChangeFileInfo {

		static final int MAXLINECOUNT = 20000;

		public int maxLine; 
		Node symbol[]; 
		int other[]; 
		private boolean isOldFile = false;

		ChangeFileInfo(String[] fileContent, boolean isOlderFile) {
			this.isOldFile = isOlderFile;
			maxLine = fileContent.length;
			symbol = new Node[maxLine+2];
			other = new int[symbol.length + 3];
			this.storeline(fileContent);
		}

		void storeline(String[] linebuffer) {
			for (int i = 0; i < linebuffer.length; i++) {
				symbol[i+1] = Node.addSymbol(linebuffer[i], isOldFile, i + 1);
			}
		}
	};
}