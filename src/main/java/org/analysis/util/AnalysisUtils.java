package org.analysis.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AnalysisUtils {

	private static final String FILE_SEPARATOR  = System.getProperty("file.separator");



	public static String getOutputFolderName(String fileName) {
		// TODO Auto-generated method stub
		return new File(fileName).getName()+"("+getTimeStamp()+")";
	}
	
	public static String getTimeStamp() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();
		return Long.toString(now.getTimeInMillis());
	}
	
	public static String createOutputFolder(String fileName, String analysisType) {
		// TODO Auto-generated method stub
		String dataFileName = fileName.replaceAll(".csv", "");
		dataFileName = dataFileName.replaceAll(".tmp", "");
		
		String userFolderPath =  System.getProperty("user.dir")+ FILE_SEPARATOR+ "Single-Site";

		String outputStudyPath = userFolderPath+ FILE_SEPARATOR + getOutputFolderName(dataFileName) +FILE_SEPARATOR;
		
		if(createFolder(userFolderPath)){
			createFolder(outputStudyPath);
		}
		return outputStudyPath;
	}
	
	public static boolean createFolder(String folderPath){
		File outputFolder = new File(folderPath);
		
		if(outputFolder.exists()) return true;
		return outputFolder.mkdir();
	}

	public static ArrayList<String> getVarNames(ArrayList<String> varInfo) {
		// TODO Auto-generated method stub
		ArrayList<String> modelList = new ArrayList<String>();
		for(String s : varInfo){
			modelList.add(s.split(":")[0]);
		}
		return modelList;
	}

	public static String getoutputFolderPath(String filenamePath) {
		// TODO Auto-generated method stub
		System.out.println("path:"+filenamePath);
		return  System.getProperty("user.dir")+filenamePath;
	}


}
