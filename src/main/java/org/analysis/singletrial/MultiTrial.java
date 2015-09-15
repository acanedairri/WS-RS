package org.analysis.singletrial;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.analysis.model.CsvToJsonModel;
import org.analysis.model.FileResourceModel;
import org.analysis.model.MultiTrialParametersModel;
import org.analysis.model.MultiTrialResultModel;
import org.analysis.model.OutlierFileResourceModel;
import org.analysis.model.OutlierParametersModel;
import org.analysis.rserve.manager.RServeManager;
import org.analysis.util.FileUtilities;

import com.google.gson.Gson;


@Path("/MultiTrial")
public class MultiTrial  implements Runnable{

	@Context
	private UriInfo context;
	private static String BSLASH = "\\";
	private static String FSLASH = "/";
	static String separator=java.nio.file.FileSystems.getDefault().getSeparator();
	private  String outputFolderPath;
	public  String dataFolderPath ;

	//	private String csvDataFileSingleTrial;
	private String csvDataFileOutlier;
	private String json;

	@Context 
	ServletContext ctx;
	@Context
	UriInfo uriInfo;
	private MultiTrialParametersModel paramsMultiTrial;
	private OutlierParametersModel	paramsOutlier;


	public MultiTrial(String json,ServletContext ctx){
		this.json=json;
		this.ctx=ctx;
	}

	public MultiTrial(){

	}


	@GET
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response testGetMultiTrial(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testMultiEnvironment(outputFolderPath,dataFolderPath);
			FileResourceModel singleTrialFileResourceModel = fetchOutputFolderFileResources(name);
			Gson gson = new Gson();
			toreturn=gson.toJson(singleTrialFileResourceModel);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}

	//	@GET
	//	@Path("PRep/{name}")
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces("application/json")
	//	public Response testGetMultiTrialPRep(@PathParam("name") String name) {
	//		String toreturn = null;
	//		try{
	//			String realpath=ctx.getRealPath("/");
	//			outputFolderPath=createOutputFolder(ctx,name);
	//			dataFolderPath=realpath+separator+"temp"+separator;
	//			RServeManager rserve= new RServeManager();
	//			rserve.testMultiEnvironmentPRef(outputFolderPath,dataFolderPath);
	//			FileResourceModel singleTrialFileResourceModel = fetchOutputFolderFileResources(name);
	//			Gson gson = new Gson();
	//			toreturn=gson.toJson(singleTrialFileResourceModel);
	//
	//		}catch(Exception e){
	//
	//		}
	//		return Response.status(320).entity(toreturn).build();
	//
	//	}
	//
	//	@GET
	//	@Path("outlier/{name}")
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces("application/json")
	//	public Response testGetOutlierDetection(@PathParam("name") String name) {
	//		String toreturn = null;
	//		OutlierFileResourceModel toreturnObject=new OutlierFileResourceModel();
	//		try{
	//			String realpath=ctx.getRealPath("/");
	//			outputFolderPath=createOutputFolder(ctx,name);
	//			dataFolderPath=realpath+separator+"temp"+separator;
	//			RServeManager rserve= new RServeManager();
	//			rserve.testOutlierDetection(outputFolderPath,dataFolderPath);
	//
	//			toreturnObject=fetchOutlierFileResources(name);
	//
	//			String path=null;
	//			String filenameOutlier="Outlier.csv";
	//
	//
	//			path= realpath+separator+"output"+separator+name+separator;
	//
	//			dataFolderPath=path+separator;
	//			String dataFileNameOutlier = dataFolderPath.replace(BSLASH, FSLASH) + filenameOutlier;
	//			BufferedReader readerOutlier = new BufferedReader(new FileReader(dataFileNameOutlier));
	//
	//			String line = null;
	//			Scanner scanner = null;
	//			int index = 0;
	//			int hindex=0;
	//			ArrayList<String> headerArray = new ArrayList<String>();
	//			List<String[]> dataRow=new ArrayList<String[]>();
	//
	//
	//			// read summary stats
	//			while ((line = readerOutlier.readLine()) != null) {
	//
	//				scanner = new Scanner(line);
	//				scanner.useDelimiter(",");
	//				ArrayList<String> rowDataArray = new ArrayList<String>();
	//
	//
	//				List<String> s= new ArrayList<String>(); 
	//				while (scanner.hasNext()) {
	//					String data = scanner.next();
	//					if(hindex==0){
	//						headerArray.add(data.replace("\"", ""));
	//						System.out.println(data.replace("\"", ""));
	//					}else{
	//						rowDataArray.add(data.replace("\"", ""));
	//					}
	//
	//					index++;
	//				}
	//				if(hindex==0){
	//					String[] header= new String[headerArray.size()];
	//					header=headerArray.toArray(header);
	//					toreturnObject.setDataHeader(header);
	//				}else{
	//					String[] rowData= new String[rowDataArray.size()];
	//					rowData=rowDataArray.toArray(rowData);
	//					dataRow.add(rowData);
	//				}
	//				hindex=1;
	//				index = 0;
	//			}
	//			toreturnObject.setData(dataRow);
	//			Gson gson = new Gson();
	//			toreturn=gson.toJson(toreturnObject);
	//
	//
	//		}catch(Exception e){
	//
	//		}
	//
	//
	//		return Response.status(320).entity(toreturn).build();
	//
	//	}
	//

	@GET
	@Path("exportAsZip/{folder}")
	@Consumes("text/plain")
	@Produces("application/zip")
	public Response exportAsZip(@PathParam("folder") String folder) {
		
//		HttpServletResponse response = (HttpServletResponse) Executions.getCurrent().getNativeResponse();
//		String toreturn = "";
		byte[] zip;
		String path = ctx.getRealPath("/");
		try {
			File directory = new File(path);
			String[] files = directory.list();

			//
			// Checks to see if the directory contains some files.
			//
			if (files != null && files.length > 0) {

				//
				// Call the zipFiles method for creating a zip stream.
				//
				
				zip = zipFiles(directory, files);

				//
				// Sends the response back to the user / browser. The
				// content for zip file type is "application/zip". We
				// also set the content disposition as attachment for
				// the browser to show a dialog that will let user 
				// choose what action will he do to the sent content.
				
//				ServletOutputStream sos = response.getOutputStream();
//                response.setContentType("application/zip");
//                response.setHeader("Content-Disposition", "attachment; filename=\"AnalysisResult.ZIP\"");
// 
//                sos.write(zip);
//                sos.flush();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(320).entity(zip).build();
	}

	 /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFiles(File directory, String[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];
 
        for (String fileName : files) {
            FileInputStream fis = new FileInputStream(directory.getPath() + 
            		System.getProperty("file.separator") + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
 
            zos.putNextEntry(new ZipEntry(fileName));
 
            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
 
        return baos.toByteArray();
    }

	@GET
	@Path("getResult/{folder}")
	@Consumes("text/plain")
	@Produces("text/plain")
	public Response getResult(@PathParam("folder") String folder) {
		String toreturn = null;
		String dataFileNameSummaryStats = null;
		String dataFileNamePredictedMeans = null;
		String dataFileNameElapsedTime=null;
		String path=null;
		String filenameSummaryStats="summaryStats.csv";
		String filenamePredictedMeans="predictedMeans.csv";
		String fileElapsedTime="elapsed-time.txt";
		MultiTrialResultModel result=new MultiTrialResultModel();
		try{
			String realpath=ctx.getRealPath("/");

			path= realpath+separator+"output"+separator+folder+separator;

			dataFolderPath=path+separator;
			dataFileNameSummaryStats = dataFolderPath.replace(BSLASH, FSLASH) + filenameSummaryStats;
			dataFileNamePredictedMeans= dataFolderPath.replace(BSLASH, FSLASH) + filenamePredictedMeans;
			dataFileNameElapsedTime=dataFolderPath.replace(BSLASH, FSLASH) + fileElapsedTime;
			BufferedReader readerSummaryStats = new BufferedReader(new FileReader(dataFileNameSummaryStats));
			BufferedReader readerPredictedMeans = new BufferedReader(new FileReader(dataFileNamePredictedMeans));
			BufferedReader readerElapsedTime=new BufferedReader(new FileReader(dataFileNameElapsedTime));

			Scanner scanner = null;
			String lineElapsed = null;
			String elapseTime = null;

			while ((lineElapsed = readerElapsedTime.readLine()) != null) {
				elapseTime=lineElapsed;
			}

			System.out.println("Time" +elapseTime);

			result.setElapsedtime(elapseTime);

			String line = null;

			int index = 0;
			int hindex=0;
			ArrayList<String> headerArray = new ArrayList<String>();
			List<String[]> dataRow=new ArrayList<String[]>();


			// read summary stats
			while ((line = readerSummaryStats.readLine()) != null) {

				scanner = new Scanner(line);
				scanner.useDelimiter(",");
				ArrayList<String> rowDataArray = new ArrayList<String>();


				List<String> s= new ArrayList<String>(); 
				while (scanner.hasNext()) {
					String data = scanner.next();
					if(hindex==0){
						headerArray.add(data.replace("\"", ""));
						System.out.println(data.replace("\"", ""));
					}else{
						rowDataArray.add(data.replace("\"", ""));
					}

					index++;
				}
				if(hindex==0){
					String[] header= new String[headerArray.size()];
					header=headerArray.toArray(header);
					result.setSummaryStatsHeader(header);
				}else{
					String[] rowData= new String[rowDataArray.size()];
					rowData=rowDataArray.toArray(rowData);
					dataRow.add(rowData);
				}
				hindex=1;
				index = 0;
			}
			result.setSummaryStatsData(dataRow);


			String lineP = null;
			Scanner scannerP = null;
			int indexP = 0;
			int hindexP=0;
			ArrayList<String> headerArrayP = new ArrayList<String>();
			List<String[]> dataRowP=new ArrayList<String[]>();

			// Read Predicted Means
			while ((lineP = readerPredictedMeans.readLine()) != null) {

				scannerP = new Scanner(lineP);
				scannerP.useDelimiter(",");
				ArrayList<String> rowDataArray = new ArrayList<String>();


				List<String> s= new ArrayList<String>(); 
				while (scannerP.hasNext()) {
					String data = scannerP.next();
					if(hindexP==0){
						headerArrayP.add(data.replace("\"", ""));
						System.out.println(data.replace("\"", ""));
					}else{
						rowDataArray.add(data.replace("\"", ""));
					}

					indexP++;
				}
				if(hindexP==0){
					String[] header2= new String[headerArrayP.size()];
					header2=headerArrayP.toArray(header2);
					result.setPredictedMeansHeader(header2);
				}else{
					String[] rowData2= new String[rowDataArray.size()];
					rowData2=rowDataArray.toArray(rowData2);
					dataRowP.add(rowData2);
				}
				hindexP=1;
				indexP = 0;
			}
			result.setPredicatedMeansData(dataRowP);

			MultiTrialResultModel finalResult =retreivedOtherResultOutput(folder,result);
			Gson gson = new Gson();
			toreturn=gson.toJson(finalResult);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}





	//GET STANDARD ERROR OF THE DIFFERENCE (SED)
	public MultiTrialResultModel retreivedOtherResultOutput(String folder,MultiTrialResultModel result) {
		String toreturn = null;
		String dataFileName = null;
		String path=null;
		String[] sed = null;
		try{
			String realpath=ctx.getRealPath("/");
			path= realpath+separator+"output"+separator+folder+separator;
			dataFolderPath=path+separator;
			dataFileName = dataFolderPath.replace(BSLASH, FSLASH) + "MEA_output.txt";
			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			String line = null;
			List<String> sedArray = new ArrayList<String>();
			int lineIndexHeri=0;

			boolean sedLine=false;
			boolean heritabilityLine=false;
			while ((line = reader.readLine()) != null) {

				System.out.println(line);
				if(line.trim().toLowerCase().contains("heritability")){
					heritabilityLine=true;
					lineIndexHeri=0;
				}

				if(heritabilityLine){
					lineIndexHeri++;
					if(lineIndexHeri==3){
						result.setHeritability(Double.valueOf(line.trim()));// get heritability
					}
				}

				if(line.trim().toLowerCase().contains("standard error of the difference")){
					sedLine=true;
				}


				if(sedLine){
					//					System.out.println(line);
					String l=line.replaceAll("^\\s+|\\s+$", "");
					l=l.replace(" ", ":");
					l=line.replace(":::", ":");
					if(line.trim().toLowerCase().contains("minimum")){
						sedArray.add(l);
					}else if(line.trim().toLowerCase().contains("average")){
						sedArray.add(l);
					}else if(line.trim().toLowerCase().contains("maximum")){
						sedArray.add(l);
					}

				}
			}
			sed= new String[sedArray.size()];
			sed=sedArray.toArray(sed);
			result.setSed(sed);
		}catch(Exception e){

		}
		return result;

	}


	@GET
	@Path("getResultCsvToJson/{folder}/{filename}")
	@Consumes("text/plain")
	@Produces("text/plain")
	public Response getResultCsvToJson(@PathParam("folder") String folder,@PathParam("filename") String filename) {
		String toreturn = null;
		String dataFileName = null;
		String path=null;
		try{
			String realpath=ctx.getRealPath("/");

			path= realpath+separator+"output"+separator+folder+separator;

			dataFolderPath=path+separator;
			dataFileName = dataFolderPath.replace(BSLASH, FSLASH) + filename;


			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			String line = null;
			Scanner scanner = null;
			int index = 0;
			CsvToJsonModel csvModel=new CsvToJsonModel();
			int hindex=0;

			ArrayList<String> headerArray = new ArrayList<String>();
			List<String[]> dataRow=new ArrayList<String[]>();

			while ((line = reader.readLine()) != null) {

				scanner = new Scanner(line);
				scanner.useDelimiter(",");
				ArrayList<String> rowDataArray = new ArrayList<String>();


				List<String> s= new ArrayList<String>(); 
				while (scanner.hasNext()) {
					String data = scanner.next();
					if(hindex==0){
						headerArray.add(data.replace("\"", ""));
						System.out.println(data.replace("\"", ""));
					}else{
						rowDataArray.add(data.replace("\"", ""));
					}

					index++;
				}
				if(hindex==0){
					String[] header= new String[headerArray.size()];
					header=headerArray.toArray(header);
					csvModel.setDataHeader(header);
				}else{
					String[] rowData= new String[rowDataArray.size()];
					rowData=rowDataArray.toArray(rowData);
					dataRow.add(rowData);
				}
				hindex=1;
				index = 0;
			}
			csvModel.setData(dataRow);
			Gson gson = new Gson();
			toreturn=gson.toJson(csvModel);


		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}


	/*@GET
	@Path("getResultStandardError/{folder}")
	@Consumes("text/plain")
	@Produces("text/plain")
	public Response getResultStandardError(@PathParam("folder") String name) {
		String toreturn = null;
		String dataFileName = null;
		String path=null;
		try{
			String realpath=ctx.getRealPath("/");

			path= realpath+separator+"output"+separator+name+separator;

			dataFolderPath=path+separator;
			dataFileName = dataFolderPath.replace(BSLASH, FSLASH) + "SEA_output.txt";


			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			String line = null;
			Scanner scanner = null;
			int index = 0;
			CsvToJsonModel csvModel=new CsvToJsonModel();
			int hindex=0;

			ArrayList<String> headerArray = new ArrayList<String>();
			List<String> dataRow=new ArrayList<String>();
			boolean sedLine=false;
			while ((line = reader.readLine()) != null) {


				if(line.trim().toLowerCase().contains("standard error of the difference")){
					sedLine=true;

				}

				if(sedLine){
					//					System.out.println(line);
					if(line.trim().toLowerCase().contains("minimum")){
						dataRow.add(line);
					}else if(line.trim().toLowerCase().contains("average")){
						dataRow.add(line);
					}else if(line.trim().toLowerCase().contains("maximum")){
						dataRow.add(line);
					}

				}
			}

			Gson gson = new Gson();
			toreturn=gson.toJson(csvModel);
			for(String d:dataRow){
				d=d.replaceAll("^\\s+|\\s+$", "");
				d=d.replace(" ", ":");
				d=d.replace(":::", ":");
				System.out.println(d);
			}

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}
	 */



	@POST
	@Path("/run")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postMultiTrial(String json) {
		String toreturn="";

		try{
			MultiTrialParametersModel paramsMultiTrial= assembleMultiTrialParameters(json);
			RServeManager rserve= new RServeManager();
			rserve.doMultiEnvironmentAnalysis(paramsMultiTrial);
			FileResourceModel fileList = fetchOutputFolderFileResources(paramsMultiTrial.getAnalysisResultFolder());
			//			removeDataFile();
			Gson gson = new Gson();
			toreturn=gson.toJson(fileList);
			return toreturn;

		}catch(Exception e){

		}
		return toreturn;


		/*	return Response.status(302).entity(toreturn).build().toString();*/

	}


	@GET
	@Path("getResultFiles/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response getResultFiles(@PathParam("name") String name) {
		String toreturn = null;
		try{
			FileUtilities fUtil= new FileUtilities();
			FileResourceModel fileList = fUtil.fetchOutputFolderFileResources(ctx,name);
			Gson gson = new Gson();
			boolean finished=false;

			for(String f:fileList.getFileListResource()){
				if(f.contains("elapsed")){
					finished=true;
				}
			}

			if(fileList.getFileListResource().size() > 0 && finished){
				toreturn=gson.toJson(fileList);
			}else{
				toreturn="Analysis is not yet done";
			}
		}catch(Exception e){
		}
		return Response.status(320).entity(toreturn).build();

	}


	@POST
	@Path("/analyze")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postMultiTrialAnalyze(String json) {
		String toreturn="";
		String errorMsg;
		try{
			errorMsg=validateInputMultiTrial(json);
			if(errorMsg.length() > 0){
				return errorMsg;
			}else{
				errorMsg="Data has been received and started analyzing";
				(new Thread(new MultiTrial(json,ctx))).start();
				return errorMsg;
			}

		}catch(Exception e){

		}
		return null;
	}

	@Override
	public void run() {

		try{

			long startTime=System.nanoTime();
			RServeManager rserve= new RServeManager();
			MultiTrialParametersModel param=assembleMultiTrialParameters(json);
			//			if(param.getDesign()==7){
			//				rserve.doMultiEnvironmentAnalysisPRep(param);
			//			}else{
			rserve.doMultiEnvironmentAnalysis(param);
			//			}
			long elapsedTime = System.nanoTime() - startTime;
			String elapsedTimeResult=((double) elapsedTime / 1000000000) + " sec";
			System.out.println("#####" + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " sec");
			createElapseTimeFile(elapsedTimeResult,param.getAnalysisResultFolder());
		}catch(Exception e){

		}

	}

	private void createElapseTimeFile(String elapsedTimeResult,
			String analysisResultFolder) {
		String realpath=ctx.getRealPath("/");
		String dataFolderPath=realpath+separator+"output/"+analysisResultFolder+separator;
		String filePath=dataFolderPath.replace(BSLASH, FSLASH)+ File.separator+ "elapsed-time.txt";
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath);
			writer.write(elapsedTimeResult);
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private String validateInputMultiTrial(String json) {
		MultiTrialParametersModel paramsMultiTrial=new MultiTrialParametersModel(); 	
		Gson jsonInput= new Gson();
		MultiTrialParametersModel param=jsonInput.fromJson(json, MultiTrialParametersModel.class);

		StringBuffer errorMsg= new StringBuffer();

		if(String.valueOf(param.getDesign()).trim().length() <=0){
			errorMsg.append("No Design Specified\n");
		}else{

			if(param.getDesign()==0 || param.getDesign()==1){   // RCB and Augmented RCB

				if((param.getBlock().length()==0 || param.getBlock()==null || param.getBlock().toLowerCase().equals("null"))  ){
					errorMsg.append("No Rep Variable Specified\n");
				}

				/*				if(!param.getDataHeader().toString().contains(param.getBlock())){
					errorMsg.append("Wrong Data Header defined for Block\n");
				}*/

			}else if(param.getDesign()==3){  // Alpha-Lattice

				if(param.getRep().length()==0 || param.getRep()==null || param.getRep().toLowerCase().equals("null")){
					errorMsg.append("No Rep Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getRep())){
					errorMsg.append("Wrong Data Header defined for Rep\n");
				}

				if(param.getBlock().length()==0 || param.getBlock()==null || param.getBlock().toLowerCase().equals("null")){
					errorMsg.append("No Block Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getBlock())){
					errorMsg.append("Wrong Data Header defined for Block\n");
				}


			}else if(param.getDesign()==4){ //Row Column 
				if(param.getRep().length()==0 || param.getRep()==null || param.getRep().toLowerCase().equals("null")){
					errorMsg.append("No Rep Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getRep())){
					errorMsg.append("Wrong Data Header defined for Rep\n");
				}


				if(param.getRow().length()==0 || param.getRow()==null || param.getRow().toLowerCase().equals("null")){
					errorMsg.append("No Row Block Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getRow())){
					errorMsg.append("Wrong Data Header defined for Row\n");
				}


				if(param.getColumn().length()==0 || param.getColumn()==null || param.getColumn().toLowerCase().equals("null")){
					errorMsg.append("No Column Block Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getColumn())){
					errorMsg.append("Wrong Data Header defined for Column\n");
				}

			}

			else if(param.getDesign()==7){ //Row Column 

				if(param.getColumn().length()==0 || param.getColumn()==null || param.getColumn().toLowerCase().equals("null")){
					errorMsg.append("No Column Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getColumn())){
					errorMsg.append("Wrong Data Header defined for Column\n");
				}

				if(param.getRow().length()==0 || param.getRow()==null || param.getRow().toLowerCase().equals("null")){
					errorMsg.append("No Row Variable Specified\n");
				}

				if(!Arrays.toString(param.getDataHeader()).contains(param.getRow())){
					errorMsg.append("Wrong Data Header defined for Row\n");
				}


			}
		}

		if(param.getDataHeader()==null || param.getDataHeader().toString().length()==0){
			errorMsg.append("No Data Header Specified\n");
		}

		if(param.getData()==null || param.getData().toString().length()==0){
			errorMsg.append("No Data  Specified\n");
		}

		if(param.getRespvars()[0]==null || param.getRespvars()[0].length()==0){
			errorMsg.append("No Response Variable  specified\n");
		}

		System.out.println(Arrays.toString(param.getDataHeader()));
		System.out.println(param.getRespvars()[0]);
		if(!Arrays.toString(param.getDataHeader()).contains(param.getRespvars()[0])){
			errorMsg.append("Wrong Data Header defined for Response Variable\n");
		}


		return errorMsg.toString();
	}


	@POST
	@Path("/outlier")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postOutlierDetection(String json) {
		String toreturn="";
		OutlierFileResourceModel toreturnObject=new OutlierFileResourceModel();
		try{
			assembleOutlierParameters(json);
			RServeManager rserve= new RServeManager();
			rserve.doOutlierDetection(paramsOutlier);

			System.out.println(paramsOutlier.getAnalysisResultFolder());
			toreturnObject=fetchOutlierFileResources(paramsOutlier.getAnalysisResultFolder());

			String path=null;
			String filenameOutlier="Outlier.csv";

			String realpath=ctx.getRealPath("/");

			path= realpath+separator+"output"+separator+paramsOutlier.getAnalysisResultFolder()+separator;

			dataFolderPath=path+separator;
			String dataFileNameOutlier = dataFolderPath.replace(BSLASH, FSLASH) + filenameOutlier;
			BufferedReader readerOutlier = new BufferedReader(new FileReader(dataFileNameOutlier));

			String line = null;
			Scanner scanner = null;
			int index = 0;
			int hindex=0;
			ArrayList<String> headerArray = new ArrayList<String>();
			List<String[]> dataRow=new ArrayList<String[]>();


			// read summary stats
			while ((line = readerOutlier.readLine()) != null) {

				scanner = new Scanner(line);
				scanner.useDelimiter(",");
				ArrayList<String> rowDataArray = new ArrayList<String>();


				List<String> s= new ArrayList<String>(); 
				while (scanner.hasNext()) {
					String data = scanner.next();
					if(hindex==0){
						headerArray.add(data.replace("\"", ""));
						System.out.println(data.replace("\"", ""));
					}else{
						rowDataArray.add(data.replace("\"", ""));
					}

					index++;
				}
				if(hindex==0){
					String[] header= new String[headerArray.size()];
					header=headerArray.toArray(header);
					toreturnObject.setDataHeader(header);
				}else{
					String[] rowData= new String[rowDataArray.size()];
					rowData=rowDataArray.toArray(rowData);
					dataRow.add(rowData);
				}
				hindex=1;
				index = 0;
			}
			toreturnObject.setData(dataRow);
			Gson gson = new Gson();
			toreturn=gson.toJson(toreturnObject);
			return toreturn;

		}catch(Exception e){

		}
		return toreturn;

	}

	private void removeDataFile(String filename) {
		File f= new File(filename);
		try {
			Files.delete(f.toPath());
		} catch (NoSuchFileException x) {
			System.err.format("%s: no such" + " file or directory%n", f.toPath());
		} catch (DirectoryNotEmptyException x) {
			System.err.format("%s not empty%n", f.toPath());
		} catch (IOException x) {
			// File permission problems are caught here.
			System.err.println(x);
		}

	}


	private String createOutputFolder(@Context ServletContext ctx,String name){
		String path = null;
		try{
			String realthapth=ctx.getRealPath("/");
			path= realthapth+separator+"output"+separator+name+separator;
			File f = new File (path);
			f.setWritable(true);
			f.setReadable(true);
			f.mkdir();	

			return path;
		}catch(Exception e){

		}
		return path;
	}


	public FileResourceModel fetchOutputFolderFileResources(String name){
		String path = null;
		FileResourceModel fileResourceModel= new FileResourceModel();
		try{

			String realpath=ctx.getRealPath("/");
			path= realpath+separator+"output"+separator+name+separator;
			File f = new File (path);
			File directory = new File(path);
			File[] fList = directory.listFiles();
			String url=ctx.getContextPath()+"/output"+separator+name+separator;
			fileResourceModel.setFolderResource(url.replace(BSLASH, FSLASH));
			List<String> fileList= new ArrayList<String>();
			for (File file : fList){
				fileList.add(file.getName());
				System.out.println();
			}
			fileResourceModel.setFileListResource(fileList);
			return fileResourceModel;
		}catch(Exception e){

		}
		return fileResourceModel;
	}


	private OutlierFileResourceModel fetchOutlierFileResources(String name){
		String path = null;
		OutlierFileResourceModel fileResourceModel= new OutlierFileResourceModel();
		try{

			String realpath=ctx.getRealPath("/");
			path= realpath+separator+"output"+separator+name+separator;
			File f = new File (path);
			File directory = new File(path);
			File[] fList = directory.listFiles();
			String url=ctx.getContextPath()+"/output"+separator+name+separator;
			fileResourceModel.setFolderResource(url.replace(BSLASH, FSLASH));
			List<String> fileList= new ArrayList<String>();
			for (File file : fList){
				fileList.add(file.getName());
				System.out.println();
			}
			fileResourceModel.setFileListResource(fileList);
			return fileResourceModel;
		}catch(Exception e){

		}
		return fileResourceModel;
	}

	private void assembleOutlierParameters(String json){
		Gson jsonInput= new Gson();
		OutlierParametersModel jsonField=jsonInput.fromJson(json, OutlierParametersModel.class);
		paramsOutlier = new OutlierParametersModel();

		String realpath=ctx.getRealPath("/");
		outputFolderPath=createOutputFolder(ctx,jsonField.getAnalysisResultFolder()); //jsonField.getAnalysisResultFolder()
		dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();

		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String fileName=jsonField.getUserAccount()+timestamp;
		csvDataFileOutlier=dataFolderPath.replace(BSLASH, FSLASH)+ File.separator+ fileName+".csv";
		fileUtil.createCSVFile(Arrays.asList(jsonField.getDataHeader()),jsonField.getData(), csvDataFileOutlier);
		String dataFileName = dataFolderPath.replace(BSLASH, FSLASH) +fileName +".csv";

		paramsOutlier.setAnalysisResultFolder(jsonField.getAnalysisResultFolder());
		paramsOutlier.setResultFolderName(outputFolderPath.replace(BSLASH, FSLASH));
		paramsOutlier.setDataFileName(dataFileName);
		paramsOutlier.setRespvar(jsonField.getRespvar());
		paramsOutlier.setGenotype(jsonField.getGenotype());
		paramsOutlier.setRep(jsonField.getRep());


	}

	private MultiTrialParametersModel assembleMultiTrialParameters(String json){

		MultiTrialParametersModel paramsMultiTrial=new MultiTrialParametersModel(); 	
		Gson jsonInput= new Gson();
		MultiTrialParametersModel jsonField=jsonInput.fromJson(json, MultiTrialParametersModel.class);

		String realpath=ctx.getRealPath("/");
		String outputFolderPath=createOutputFolder(ctx,jsonField.getAnalysisResultFolder()); //jsonField.getAnalysisResultFolder()
		String dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();

		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String fileName=jsonField.getUserAccount()+timestamp;
		String csvDataFileMultiTrial = dataFolderPath.replace(BSLASH, FSLASH)+ File.separator+ fileName+".csv";
		fileUtil.createCSVFile(Arrays.asList(jsonField.getDataHeader()),jsonField.getData(), csvDataFileMultiTrial);

		paramsMultiTrial.setResultFolderPath(outputFolderPath.replace(BSLASH, FSLASH));
		paramsMultiTrial.setOutFileName(outputFolderPath.replace(BSLASH, FSLASH) + "MEA_output.txt");
		paramsMultiTrial.setDataFileName(dataFolderPath.replace(BSLASH, FSLASH) + fileName+".csv");

		paramsMultiTrial.setAnalysisResultFolder(jsonField.getAnalysisResultFolder());
		paramsMultiTrial.setDesign(jsonField.getDesign());
		paramsMultiTrial.setEnvironment(jsonField.getEnvironment());
		paramsMultiTrial.setEnvironmentLevels(jsonField.getEnvironmentLevels());
		paramsMultiTrial.setRespvars(jsonField.getRespvars());
		paramsMultiTrial.setGenotype(jsonField.getGenotype());
		paramsMultiTrial.setBlock(jsonField.getBlock());
		paramsMultiTrial.setRep(jsonField.getRep());
		paramsMultiTrial.setRow(jsonField.getRow());
		paramsMultiTrial.setColumn(jsonField.getColumn());
		paramsMultiTrial.setDescriptiveStat(jsonField.isDescriptiveStat()); 
		paramsMultiTrial.setVarianceComponents(jsonField.isVarianceComponents());
		paramsMultiTrial.setBoxplotRawData(Boolean.valueOf(jsonField.isBoxplotRawData()));
		paramsMultiTrial.setHistogramRawData(jsonField.isHistogramRawData());
		paramsMultiTrial.setDiagnosticPlot(jsonField.isDiagnosticPlot());
		paramsMultiTrial.setGenotypeFixed(jsonField.isGenotypeFixed()); //jsonField.isGenotypeFixed()
		paramsMultiTrial.setGenotypeRandom(jsonField.isGenotypeRandom()); //jsonField.isGenotypeRandom()
		paramsMultiTrial.setPerformPairwise(jsonField.isPerformPairwise());
		paramsMultiTrial.setPairwiseAlpha(jsonField.getPairwiseAlpha());
		paramsMultiTrial.setCompareControl(jsonField.isCompareControl());
		paramsMultiTrial.setPerformAllPairwise(jsonField.isPerformAllPairwise());

		paramsMultiTrial.setAmmi(jsonField.isAmmi());
		paramsMultiTrial.setStabilityFinlay(jsonField.isStabilityFinlay());
		paramsMultiTrial.setStabilityShukla(jsonField.isStabilityShukla());
		paramsMultiTrial.setGge(jsonField.isGge());

		paramsMultiTrial.setControlLevels(jsonField.getControlLevels());
		paramsMultiTrial.setGenotypeLevels(jsonField.getGenotypeLevels());

		String[] spatialStruc = {"none", "CompSymm", "Gaus", "Exp", "Spher"};
		if(jsonField.getDesign()==7){
			paramsMultiTrial.setControlLevels(null);
			paramsMultiTrial.setEnvironment(null);
		}

		//		
		//		paramsMultiTrial.setMoransTest(false);
		//		paramsMultiTrial.setSpatialStruc(spatialStruc);


		System.out.println("Data Header :" +Arrays.toString(jsonField.getDataHeader()));
		System.out.println("Data ");
		int i=1;
		for(String[] s:jsonField.getData()){
			System.out.println("row" +i +":" +Arrays.toString(s));
			i++;
		}

		return paramsMultiTrial;

	}



	@GET
	@Path("download/{folder}/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("folder") final String folder,@PathParam("filename") final String filename)
	{
		// fetch the file to download from file system or database, or wherever...
		File file = fetchFileToDownload(folder,filename);

		if(file == null || !file.exists())
			throw new WebApplicationException(Status.NOT_FOUND);

		return Response.ok(file).header("Content-Disposition", "attachment; filename=" + file.getName()).build();

	}


	private File fetchFileToDownload(String folder,String filename) {
		String realpath=ctx.getRealPath("/");
		String path = null;
		path= realpath+separator+"output"+separator+folder+separator+filename;
		File f = new File (path);
		System.out.println(f.getName());
		return f;
	}



	@GET
	@Path("getImageByte/{folder}/{filename}")
	@Consumes("text/plain")
	@Produces("image/png")
	//	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getImage(@PathParam("folder") final String folder,@PathParam("filename") final String filename) throws IOException
	{
		// fetch the file to download from file system or database, or wherever...
		File file = fetchFileToDownload(folder,filename);

		BufferedImage originalImage = ImageIO.read(file);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, "png", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();


		if(file == null || !file.exists())
			throw new WebApplicationException(Status.NOT_FOUND);

		return Response.ok(new ByteArrayInputStream(imageInByte)).build();
		//		return Response.ok(imageInByte).build();

	}
}


