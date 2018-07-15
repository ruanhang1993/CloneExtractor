package cn.edu.fudan.se.clonedetector.util.test;

public class TestFileRename {
	public static void main(String[] args) {
		String name = "E:\\TestSVN\\TestCSharp\\XmlConvertor.cs.rn";
		new java.io.File(name).delete();
		new java.io.File(name+".rn").renameTo(new java.io.File(name));
	}
}
