package org.analysis.rserve.manager.test;

import java.util.List;

public class OutlierParametersModel {
	
	private String resultFolderName;
	private String outFileName;
	private String dataFileName;
	private String analysisResultFolder;
	private String userAccount;
	
	private String[] dataHeader;
	private List<String[]> data;
	private String respvar;
	private String genotype;
	private String rep;
	
	
	
	
	public String getResultFolderName() {
		return resultFolderName;
	}
	public void setResultFolderName(String resultFolderName) {
		this.resultFolderName = resultFolderName;
	}
	public String getOutFileName() {
		return outFileName;
	}
	public void setOutFileName(String outFileName) {
		this.outFileName = outFileName;
	}
	public String getDataFileName() {
		return dataFileName;
	}
	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}
	public String getAnalysisResultFolder() {
		return analysisResultFolder;
	}
	public void setAnalysisResultFolder(String analysisResultFolder) {
		this.analysisResultFolder = analysisResultFolder;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String[] getDataHeader() {
		return dataHeader;
	}
	public void setDataHeader(String[] dataHeader) {
		this.dataHeader = dataHeader;
	}
	public List<String[]> getData() {
		return data;
	}
	public void setData(List<String[]> data) {
		this.data = data;
	}
	public String getRespvar() {
		return respvar;
	}
	public void setRespvar(String respvar) {
		this.respvar = respvar;
	}

	public String getGenotype() {
		return genotype;
	}
	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}
	public String getRep() {
		return rep;
	}
	public void setRep(String rep) {
		this.rep = rep;
	}
	
	

}
