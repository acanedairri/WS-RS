package org.analysis.singletrial;

import java.util.List;

import org.analysis.rserve.manager.test.CsvToJsonModel;

public class FileResourceOutlierModel  {
	
	String folderResource;
	List<String> fileListResource;
	
	private String[] dataHeader;
	private List<String[]> data;
	
	public String getFolderResource() {
		return folderResource;
	}
	public void setFolderResource(String folderResource) {
		this.folderResource = folderResource;
	}
	public List<String> getFileListResource() {
		return fileListResource;
	}
	public void setFileListResource(List<String> fileListResource) {
		this.fileListResource = fileListResource;
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
	
	

	
	
	
	

}
