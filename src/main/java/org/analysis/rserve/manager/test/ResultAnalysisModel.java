package org.analysis.rserve.manager.test;

import java.util.List;

public class ResultAnalysisModel {
	
	private String[] summaryStatsHeader;
	private List<String[]> summaryStatsData;
	private String[] sed;
	private double heritability;
	
	
	
	public String[] getSummaryStatsHeader() {
		return summaryStatsHeader;
	}
	public void setSummaryStatsHeader(String[] summaryStatsHeader) {
		this.summaryStatsHeader = summaryStatsHeader;
	}
	public List<String[]> getSummaryStatsData() {
		return summaryStatsData;
	}
	public void setSummaryStatsData(List<String[]> summaryStatsData) {
		this.summaryStatsData = summaryStatsData;
	}
	public String[] getSed() {
		return sed;
	}
	public void setSed(String[] sed) {
		this.sed = sed;
	}
	public double getHeritability() {
		return heritability;
	}
	public void setHeritability(double heritability) {
		this.heritability = heritability;
	}
	
	
	
	
	



}
