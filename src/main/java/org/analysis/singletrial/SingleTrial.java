package org.analysis.singletrial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import org.analysis.rserve.manager.RServeManager;
import org.analysis.rserve.manager.test.CsvToJsonModel;
import org.analysis.rserve.manager.test.ResultAnalysisModel;
import org.analysis.rserve.manager.test.SingleTrialParametersModel;
import org.analysis.util.FileUtilities;

import com.google.gson.Gson;


@Path("/SingleTrial")
public class SingleTrial {

	@Context
	private UriInfo context;
	private static String BSLASH = "\\";
	private static String FSLASH = "/";
	static String separator=java.nio.file.FileSystems.getDefault().getSeparator();
	private  String outputFolderPath;
	public  String dataFolderPath ;
	public FileResourceModel fileList= new FileResourceModel();
	private String csvDataFile;


	@Context 
	ServletContext ctx;
	@Context
	UriInfo uriInfo;
	private SingleTrialParametersModel params;



	@GET
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response postSingleTrialTest(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testSingleEnvironment(outputFolderPath,dataFolderPath);
			fileList=getOutputFolderFileResources(name);
			Gson gson = new Gson();
			toreturn=gson.toJson(fileList);


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
		String dataFileName = null;
		String path=null;
		String filename="summaryStats.csv";
		try{
			String realpath=ctx.getRealPath("/");

			path= realpath+separator+"output"+separator+folder+separator;

			dataFolderPath=path+separator;
			dataFileName = dataFolderPath.replace(BSLASH, FSLASH) + filename;

			BufferedReader reader = new BufferedReader(new FileReader(dataFileName));
			String line = null;
			Scanner scanner = null;
			int index = 0;
			ResultAnalysisModel result=new ResultAnalysisModel();
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
			ResultAnalysisModel finalResult =getOtherResultOutput(folder,result);
			Gson gson = new Gson();
			toreturn=gson.toJson(finalResult);

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}


	//GET STANDARD ERROR OF THE DIFFERENCE (SED)
	public ResultAnalysisModel getOtherResultOutput(String folder,ResultAnalysisModel result) {
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
			assembleParameters(json);
			RServeManager rserve= new RServeManager();
			rserve.doSingleEnvironmentAnalysis(params);
			fileList=getOutputFolderFileResources(params.getAnalysisResultFolder());
			removeDataFile();
			Gson gson = new Gson();
			toreturn=gson.toJson(fileList);
			return toreturn;

		}catch(Exception e){

		}
		return toreturn;


		/*	return Response.status(302).entity(toreturn).build().toString();*/

	}

	private void removeDataFile() {
		File f= new File(csvDataFile);
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


	private FileResourceModel getOutputFolderFileResources(String name){
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


	private void assembleParameters(String json){

		Gson jsonInput= new Gson();
		SingleTrialParametersModel jsonField=jsonInput.fromJson(json, SingleTrialParametersModel.class);
		params= new SingleTrialParametersModel(); 	

		String realpath=ctx.getRealPath("/");
		outputFolderPath=createOutputFolder(ctx,jsonField.getAnalysisResultFolder()); //jsonField.getAnalysisResultFolder()
		dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();

		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String fileName=jsonField.getUserAccount()+timestamp;
		csvDataFile=dataFolderPath.replace(BSLASH, FSLASH)+ File.separator+ fileName+".csv";
		fileUtil.createCSVFile(Arrays.asList(jsonField.getDataHeader()),jsonField.getData(), csvDataFile);

		params.setResultFolderName(outputFolderPath.replace(BSLASH, FSLASH));
		params.setOutFileName(outputFolderPath.replace(BSLASH, FSLASH) + "SEA_output.txt");
		params.setDataFileName(dataFolderPath.replace(BSLASH, FSLASH) + fileName+".csv");

		params.setAnalysisResultFolder(jsonField.getAnalysisResultFolder());
		params.setDesign(jsonField.getDesign());
		params.setEnvironment("NULL");
		params.setEnvironmentLevels(null);
		params.setRespvars(jsonField.getRespvars());
		params.setGenotype(jsonField.getGenotype());
		params.setBlock(jsonField.getBlock());
		params.setRep(jsonField.getRow());
		params.setRow(jsonField.getRow());
		params.setColumn(jsonField.getColumn());
		params.setDescriptiveStat(jsonField.isDescriptiveStat()); 
		params.setVarianceComponents(jsonField.isVarianceComponents());
		params.setBoxplotRawData(Boolean.valueOf(jsonField.isBoxplotRawData()));
		params.setHistogramRawData(jsonField.isHistogramRawData());
		params.setHeatmapResiduals(jsonField.isHeatmapResiduals());
		params.setHeatmapRow(jsonField.getHeatmapRow());
		params.setHeatmapColumn(jsonField.getHeatmapColumn());
		params.setDiagnosticPlot(jsonField.isDiagnosticPlot());
		params.setGenotypeFixed(jsonField.isGenotypeFixed());
		params.setPerformPairwise(jsonField.isPerformPairwise());
		params.setPairwiseAlpha(jsonField.getPairwiseAlpha());
		params.setCompareControl(jsonField.isCompareControl());
		params.setPerformAllPairwise(jsonField.isPerformAllPairwise());
		params.setGenotypeRandom(jsonField.isGenotypeRandom());
		params.setExcludeControls(jsonField.isExcludeControls());
		params.setGenoPhenoCorrelation(jsonField.isGenoPhenoCorrelation());
		params.setControlLevels(jsonField.getControlLevels());
		params.setGenotypeLevels(jsonField.getGenotypeLevels());

		//		System.out.println("Model: " +ssaModel.toString());

		System.out.println("Data Header :" +Arrays.toString(jsonField.getDataHeader()));
		System.out.println("Data ");
		int i=1;
		for(String[] s:jsonField.getData()){
			System.out.println("row" +i +":" +Arrays.toString(s));
			i++;
		}

	}



	@GET
	@Path("download/{folder}/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("folder") final String folder,@PathParam("filename") final String filename)
	{
		// fetch the file to download from file system or database, or wherever...
		File file = getFileToDownload(folder,filename);

		if(file == null || !file.exists())
			throw new WebApplicationException(Status.NOT_FOUND);

		return Response.ok(file).header("Content-Disposition", "attachment; filename=" + file.getName()).build();

	}


	private File getFileToDownload(String folder,String filename) {
		String realpath=ctx.getRealPath("/");
		String path = null;
		path= realpath+separator+"output"+separator+folder+separator+filename;
		File f = new File (path);
		return f;
	}



}


