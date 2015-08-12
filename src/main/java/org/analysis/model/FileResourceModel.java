package org.analysis.model;

import java.util.List;

/**
 * This class contains model to handle value of folder and list of 
 * files created by the webservice
 * 
 * @author Alexander Cañeda
 * 
 * 
 */

public class FileResourceModel {
	
	String folderResource;
	List<String> fileListResource;
	
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
	
	

	
	
	
	

}
