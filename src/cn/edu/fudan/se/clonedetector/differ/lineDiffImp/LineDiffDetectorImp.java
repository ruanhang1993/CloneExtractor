package cn.edu.fudan.se.clonedetector.differ.lineDiffImp;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import cn.edu.fudan.se.clonedetector.bean.File;
import cn.edu.fudan.se.clonedetector.differ.AbstractDiffDetector;

public class LineDiffDetectorImp extends AbstractDiffDetector{

	@Override
	protected void handleUpdateFile(LinkedHashMap<File, File> fileMap) {
		DiffAdapter diff = new DiffAdapter();
		diff.setChangeFactory(changeFactory);
		int i = 0;
		System.out.println("fileMap : "+fileMap.size());
		for(Entry<File, File> entry : fileMap.entrySet()){
			System.out.format("%d left : %s ; right : %s%n", i++, entry.getKey().getFileName(), entry.getValue().getFileName());
			String[] left = convertFileToStringArray(entry.getKey().getContent());
			String[] right = convertFileToStringArray(entry.getValue().getContent());
			diff.setFileId(entry.getValue().getFileId(), entry.getKey().getFileId());
			diff.refresh();
			diff.doDiff(left, right);
			changeList.addAll(diff.getChangeList());
		}
				
	}


}
