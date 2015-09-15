package org.analysis.model;

import java.util.List;

/**
 * This class contains setter/getters parameters of Single Trial result data
 * 
 * @author Alexander Cañeda
 */ 
public class SingleTrialResultModel {
	
	private String AnalyticalWebServiceVersion="AWS 1.0";
	private String Rversion="R version 3.0.2";
	private String elapsedtime;
	private String[] summaryStatsHeader;
	private List<String[]> summaryStatsData;
	private String[] predictedMeansHeader;
	private List<String[]> predicatedMeansData;
	private String[] sed;
	private double heritability;
	
	public String getElapsedtime() {
		return elapsedtime;
	}
	public void setElapsedtime(String elapsedtime) {
		this.elapsedtime = elapsedtime;
	}
	public String getAnalyticalWebServiceVersion() {
		return AnalyticalWebServiceVersion;
	}
	public void setAnalyticalWebServiceVersion(String analyticalWebServiceVersion) {
		AnalyticalWebServiceVersion = analyticalWebServiceVersion;
	}
	public String getRversion() {
		return Rversion;
	}
	public void setRversion(String rversion) {
		Rversion = rversion;
	}
	public String[] getPredictedMeansHeader() {
		return predictedMeansHeader;
	}
	public void setPredictedMeansHeader(String[] predictedMeansHeader) {
		this.predictedMeansHeader = predictedMeansHeader;
	}
	public List<String[]> getPredicatedMeansData() {
		return predicatedMeansData;
	}
	public void setPredicatedMeansData(List<String[]> predicatedMeansData) {
		this.predicatedMeansData = predicatedMeansData;
	}
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
