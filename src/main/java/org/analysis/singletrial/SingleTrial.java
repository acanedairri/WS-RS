package org.analysis.singletrial;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
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
import org.analysis.model.OutlierFileResourceModel;
import org.analysis.model.OutlierParametersModel;
import org.analysis.model.SingleTrialResultModel;
import org.analysis.model.SingleTrialParametersModel;
import org.analysis.rserve.manager.RServeManager;
import org.analysis.util.FileUtilities;

import com.google.gson.Gson;


@Path("/SingleTrial")
public class SingleTrial  implements Runnable{

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
	private SingleTrialParametersModel paramsSingleTrial;
	private OutlierParametersModel	paramsOutlier;


	public SingleTrial(String json,ServletContext ctx){
		this.json=json;
		this.ctx=ctx;
	}

	public SingleTrial(){

	}


	@GET
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response testGetSingleTrial(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testSingleEnvironment(outputFolderPath,dataFolderPath);
			FileResourceModel singleTrialFileResourceModel = fetchOutputFolderFileResources(name);
			Gson gson = new Gson();
			toreturn=gson.toJson(singleTrialFileResourceModel);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}

	@GET
	@Path("PRep/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response testGetSingleTrialPRep(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testSingleEnvironmentPRef(outputFolderPath,dataFolderPath);
			FileResourceModel singleTrialFileResourceModel = fetchOutputFolderFileResources(name);
			Gson gson = new Gson();
			toreturn=gson.toJson(singleTrialFileResourceModel);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}

	@GET
	@Path("outlier/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response testGetOutlierDetection(@PathParam("name") String name) {
		String toreturn = null;
		OutlierFileResourceModel toreturnObject=new OutlierFileResourceModel();
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testOutlierDetection(outputFolderPath,dataFolderPath);

			toreturnObject=fetchOutlierFileResources(name);

			String path=null;
			String filenameOutlier="Outlier.csv";


			path= realpath+separator+"output"+separator+name+separator;

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


		}catch(Exception e){

		}


		return Response.status(320).entity(toreturn).build();

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
		SingleTrialResultModel result=new SingleTrialResultModel();
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

			SingleTrialResultModel finalResult =retreivedOtherResultOutput(folder,result);
			Gson gson = new Gson();
			toreturn=gson.toJson(finalResult);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}





	//GET STANDARD ERROR OF THE DIFFERENCE (SED)
	public SingleTrialResultModel retreivedOtherResultOutput(String folder,SingleTrialResultModel result) {
		String toreturn = null;
		String dataFileName = null;
		String path=null;
		String[] sed = null;
		try{
			String realpath=ctx.getRealPath("/");
			path= realpath+separator+"output"+separator+folder+separator;
			dataFolderPath=path+separator;
			dataFileName = dataFolderPath.replace(BSLASH, FSLASH) + "SEA_output.txt";
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
	public String postSingleTrial(String json) {
		String toreturn="";

		try{
			SingleTrialParametersModel paramsSingleTrial= assembleSingleTrialParameters(json);
			RServeManager rserve= new RServeManager();
			rserve.doSingleEnvironmentAnalysis(paramsSingleTrial);
			FileResourceModel fileList = fetchOutputFolderFileResources(paramsSingleTrial.getAnalysisResultFolder());
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
	public String postSingleTrialAnalyze(String json) {
		String toreturn="";
		String errorMsg;
		try{
			errorMsg=validateInputSingleTrial(json);
			if(errorMsg.length() > 0){
				return errorMsg;
			}else{
				errorMsg="Data has been received and started analyzing";
				(new Thread(new SingleTrial(json,ctx))).start();
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
			SingleTrialParametersModel param=assembleSingleTrialParameters(json);
			if(param.getDesign()==7){
				rserve.doSingleEnvironmentAnalysisPRep(param);
			}else{
				rserve.doSingleEnvironmentAnalysis(param);
			}
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

	private String validateInputSingleTrial(String json) {
		SingleTrialParametersModel paramsSingleTrial=new SingleTrialParametersModel(); 	
		Gson jsonInput= new Gson();
		SingleTrialParametersModel param=jsonInput.fromJson(json, SingleTrialParametersModel.class);

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

	private SingleTrialParametersModel assembleSingleTrialParameters(String json){

		SingleTrialParametersModel paramsSingleTrial=new SingleTrialParametersModel(); 	
		Gson jsonInput= new Gson();
		SingleTrialParametersModel jsonField=jsonInput.fromJson(json, SingleTrialParametersModel.class);

		String realpath=ctx.getRealPath("/");
		String outputFolderPath=createOutputFolder(ctx,jsonField.getAnalysisResultFolder()); //jsonField.getAnalysisResultFolder()
		String dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();

		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String fileName=jsonField.getUserAccount()+timestamp;
		String csvDataFileSingleTrial = dataFolderPath.replace(BSLASH, FSLASH)+ File.separator+ fileName+".csv";
		fileUtil.createCSVFile(Arrays.asList(jsonField.getDataHeader()),jsonField.getData(), csvDataFileSingleTrial);

		paramsSingleTrial.setResultFolderName(outputFolderPath.replace(BSLASH, FSLASH));
		paramsSingleTrial.setOutFileName(outputFolderPath.replace(BSLASH, FSLASH) + "SEA_output.txt");
		paramsSingleTrial.setDataFileName(dataFolderPath.replace(BSLASH, FSLASH) + fileName+".csv");

		paramsSingleTrial.setAnalysisResultFolder(jsonField.getAnalysisResultFolder());
		paramsSingleTrial.setDesign(jsonField.getDesign());
		paramsSingleTrial.setEnvironment("NULL");
		paramsSingleTrial.setEnvironmentLevels(jsonField.getEnvironmentLevels());
		paramsSingleTrial.setRespvars(jsonField.getRespvars());
		paramsSingleTrial.setGenotype(jsonField.getGenotype());
		paramsSingleTrial.setBlock(jsonField.getBlock());
		paramsSingleTrial.setRep(jsonField.getRep());
		paramsSingleTrial.setRow(jsonField.getRow());
		paramsSingleTrial.setColumn(jsonField.getColumn());
		paramsSingleTrial.setDescriptiveStat(jsonField.isDescriptiveStat()); 
		paramsSingleTrial.setVarianceComponents(jsonField.isVarianceComponents());
		paramsSingleTrial.setBoxplotRawData(Boolean.valueOf(jsonField.isBoxplotRawData()));
		paramsSingleTrial.setHistogramRawData(jsonField.isHistogramRawData());
		paramsSingleTrial.setHeatmapResiduals(jsonField.isHeatmapResiduals());
		paramsSingleTrial.setHeatmapRow(jsonField.getHeatmapRow());
		paramsSingleTrial.setHeatmapColumn(jsonField.getHeatmapColumn());
		paramsSingleTrial.setDiagnosticPlot(jsonField.isDiagnosticPlot());
		paramsSingleTrial.setGenotypeFixed(true); //jsonField.isGenotypeFixed()
		paramsSingleTrial.setGenotypeRandom(true); //jsonField.isGenotypeRandom()
		paramsSingleTrial.setPerformPairwise(jsonField.isPerformPairwise());
		paramsSingleTrial.setPairwiseAlpha(jsonField.getPairwiseAlpha());
		paramsSingleTrial.setCompareControl(jsonField.isCompareControl());
		paramsSingleTrial.setPerformAllPairwise(jsonField.isPerformAllPairwise());

		paramsSingleTrial.setExcludeControls(jsonField.isExcludeControls());
		paramsSingleTrial.setGenoPhenoCorrelation(jsonField.isGenoPhenoCorrelation());
		paramsSingleTrial.setControlLevels(jsonField.getControlLevels());
		paramsSingleTrial.setGenotypeLevels(jsonField.getGenotypeLevels());

		String[] spatialStruc = {"none", "CompSymm", "Gaus", "Exp", "Spher"};
		if(jsonField.getDesign()==7){
			paramsSingleTrial.setControlLevels(null);
			paramsSingleTrial.setEnvironment(null);
		}
		
		paramsSingleTrial.setMoransTest(false);
		paramsSingleTrial.setSpatialStruc(spatialStruc);


		System.out.println("Data Header :" +Arrays.toString(jsonField.getDataHeader()));
		System.out.println("Data ");
		int i=1;
		for(String[] s:jsonField.getData()){
			System.out.println("row" +i +":" +Arrays.toString(s));
			i++;
		}

		return paramsSingleTrial;

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


