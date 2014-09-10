package org.analysis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileUtilities {

	public static void uploadFile(String filepath, InputStream file) {
		try {
			OutputStream out = new java.io.FileOutputStream(filepath);
			InputStream in = file;
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			in.close();
			out.flush();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static public File zipFolder(String srcFolder) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		File destZipFile = File.createTempFile(new File(srcFolder).getName(), ".zip");

		fileWriter = new FileOutputStream(destZipFile.getAbsolutePath());
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
		return destZipFile;
	}

	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}




	
	public  File createCSVFile(List<String> columns, List<String[]> rows, String filePath) {
		// TODO Auto-generated method stub
		List<String[]> grid = rows;

		StringBuffer sb = new StringBuffer();

		System.out.println("creating File...");
		int ctr = 0;
		for (String s : columns) {
			ctr++;
			sb.append(s);
			if (ctr != columns.size())
				sb.append(",");
		}
		sb.append("\n");

		for (String[] row : grid) {
			ctr = 0;
			for (String s : row) {
				ctr++;
				sb.append(s);
				if (ctr != row.length)
					sb.append(",");
			}
			sb.append("\n");
		}

		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath);
			writer.write(sb.toString());
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return new File(filePath);
	}

}
