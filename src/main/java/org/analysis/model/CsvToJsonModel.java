package org.analysis.model;

import java.util.List;

/**
 * This class contains model for data in JSON format
 * 
 * @author Alexander Cañeda
 * 
 * 
 */

public class CsvToJsonModel {
	
	private String[] dataHeader;
	private List<String[]> data;

	
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
