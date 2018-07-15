package main;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.edu.fudan.se.clonedetector.ccfinder.evolution.EvolutionAnalyse;

public class TestEloc {

	@Test
	public void test() {
		EvolutionAnalyse ea = new EvolutionAnalyse("E:\\TestSVN\\c-sampleCode\\trunk\\1-5.c", ".");
		int[] eloc = ea.countLine();
		System.out.println(eloc);
	}

}
