package org.analysis.singletrial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.analysis.model.CsvToJsonModel;
import org.analysis.model.FileResourceModel;
import org.analysis.model.RandomizationParamModel;
import org.analysis.rserve.manager.RServeManager;
import org.analysis.util.FileUtilities;

import com.google.gson.Gson;


@Path("/Randomization")
public class Randomization  implements Runnable {

	@Context
	private UriInfo context;
	private static String BSLASH = "\\";
	private static String FSLASH = "/";
	static String separator=java.nio.file.FileSystems.getDefault().getSeparator();
	private  String outputFolderPath;
	public  String dataFolderPath ;
	private RandomizationParamModel param;

	private String json;

	@Context 
	ServletContext ctx;
	@Context
	UriInfo uriInfo;


	public Randomization(String json,ServletContext ctx){
		this.json=json;
		this.ctx=ctx;
	}

	public Randomization(){

	}


	@POST
	@Path("/run")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postRandomized(String json){
		String toreturn="";
		String msg;
		try{
			msg=validateInputSingleTrial(json);
			if(msg.length() > 0){
				return msg;
			}else{
				msg="Data has been received and started the randomization";
				(new Thread(new Randomization(json,ctx))).start();
				return msg;
			}

		}catch(Exception e){

		}
		return null;
	}


	/* This method validate json input data for single trial */
	private String validateInputSingleTrial(String json) {
		// TODO Auto-generated method stub
		
		RandomizationParamModel paramsRandomization=new RandomizationParamModel(); 	
		Gson jsonInput= new Gson();
		RandomizationParamModel param=jsonInput.fromJson(json, RandomizationParamModel.class);
		StringBuffer errorMsg= new StringBuffer();
		if(param.getDesign()==0){ //RCBD
			if(param.getBlk() < 2){
				errorMsg.append("The minimum value of the number of block is equal to 2 \n");
			}
			

			
		}
		return "";
	}


	@Override
	public void run() {
		try{

			long startTime=System.nanoTime();
			RServeManager rserve= new RServeManager();
			RandomizationParamModel param=assembleParameters(json);
			if(param.getDesign()==0){ // RCB
				rserve.doDesignRCBD(param.getPath(),param.getFieldBookName(), param.getFactorName(),param.getFactorID(), param.getFactorLevel()
						,param.getBlk(), param.getTrial(),param.getNumFieldRow(), param.getRowPerBlk(),param.getFieldOrder());
			}else if(param.getDesign()==1){ // Augmented RCB
				rserve.doDesignAugmentedRCB(param.getPath(),param.getFieldBookName(), param.getNumCheck(),param.getNumNew(),param.getBlk(), param.getFieldRow(), param.getTrial(),param.getFieldOrder(),param.getRowPerBlk(),null,null,null,null);
			}else if(param.getDesign()==2){ // Augmented Latin Square
				rserve.doDesignAugmentedLSD(param.getPath(),param.getFieldBookName(),param.getNumCheck(),param.getNumNew(),param.getTrmtName(),param.getTrial(),param.getNumFieldRow(),param.getFieldOrder(),param.getTrmtLabel(),param.getChecktrmt(),param.getNewTrmt());
			}else if(param.getDesign()==3){ // Alpha Lattice
				rserve.doDesignAlpha(param.getPath(),param.getFieldBookName(), param.getNumTrmt(), param.getBlkSize(),param.getRep(),param.getTrial(),
						param.getRowPerBlk(), param.getRowPerRep(),param.getNumFieldRow(),param.getFieldOrder());
			}else if(param.getDesign()==4){ // Row Column
				rserve.doDesignRowColumn(param.getPath(), param.getFieldBookName(), param.getNumTrmt(),param.getRep(),param.getTrial(),param.getRowPerRep(),param.getNumFieldRow(),param.getFieldOrder());
			}else if(param.getDesign()==5){ // Latinized Alpha Lattice
				rserve.doDesignLatinizedAlpha(param.getPath(), param.getFieldBookName(),param.getNumTrmt(),param.getBlkSize(),param.getRep(), param.getTrial(), param.getNumFieldRow(),param.getFieldOrder());

			}else if(param.getDesign()==6){ // Latinized Row Column
				rserve.doDesignRowColumn(param.getPath(), param.getFieldBookName(), param.getNumTrmt(),param.getRep(),param.getTrial(),param.getRowPerRep(), param.getNumFieldRow(),param.getFieldOrder());

			}else if(param.getDesign()==7){ // Prep
				rserve.doDesignPRep(param.getPath(),  param.getFieldBookName(),param.getTrmtGrpName(), param.getNumTrmtPerGrp(),param.getTrmtRepPerGrp(),param.getTrmtName(),param.getBlk(),param.getTrial(), param.getRowPerBlk()
						, param.getNumFieldRow(),param.getFieldOrder(), param.getTrmtLabel(),param.getTrmtListPerGrp());
			}else if(param.getDesign()==8){ // Augmented Row Column
				rserve.doDesignAugmentedRowColumn(param.getPath(),  param.getFieldBookName(), param.getNumCheck(),param.getNumNew(), param.getTrmtName(),param.getRep(),param.getTrial(),param.getRowBlkPerRep(), param.getRowPerRowBlk()
						, param.getNumFieldRow(),param.getFieldOrder(),param.getTrmtLabel(),param.getChecktrmt(),param.getNewTrmt());
			}else if(param.getDesign()==9){ // Augmentead Alpha Lattice
				rserve.doDesignAugmentedAlpha(param.getPath(),  param.getFieldBookName(), param.getNumCheck(),param.getNumNew(), param.getTrmtName(),param.getBlkSize(),param.getRep(),param.getTrial(),param.getRowPerBlk(), param.getRowPerRep()
						, param.getNumFieldRow(),param.getFieldOrder(),param.getTrmtLabel(),param.getChecktrmt(),param.getNewTrmt());
			}
			long elapsedTime = System.nanoTime() - startTime;
			String elapsedTimeResult=((double) elapsedTime / 1000000000) + " sec";
			System.out.println("#####" + ": Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " sec");
			createElapseTimeFile(elapsedTimeResult,param.getResultFolder());
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

	
	/* This methods assemble the parameter in jsonn format
	 * to Randomization Model Class 
	*/
	
	private RandomizationParamModel assembleParameters(String json) {

		RandomizationParamModel param=new RandomizationParamModel(); 	
		Gson jsonInput= new Gson();
		RandomizationParamModel jsonField=jsonInput.fromJson(json, RandomizationParamModel.class);


		String realpath=ctx.getRealPath("/");
		String outputFolderPath=createOutputFolder(ctx,jsonField.getResultFolder()); //jsonField.getAnalysisResultFolder()
		String resultFolder = outputFolderPath.replace(BSLASH, FSLASH);
		String dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();
		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		//default
		param.setResultFolder(jsonField.getResultFolder());
		param.setPath(resultFolder);
		param.setFieldBookName(jsonField.getFieldBookName());
		param.setDesign(jsonField.getDesign());

		if(jsonField.getDesign()==0){ // RCB

			param.setFactorName(jsonField.getFactorName());
			param.setFactorID(jsonField.getFactorID()); 
			param.setFactorLevel(jsonField.getFactorLevel());
			param.setBlk(jsonField.getBlk()); 
			param.setTrial(jsonField.getTrial());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setRowPerBlk(jsonField.getRowPerBlk());
			param.setFieldOrder(jsonField.getFieldOrder());

		}else if(jsonField.getDesign()==1){ // Augmented RCB
			param.setNumCheck(jsonField.getNumCheck());
			param.setNumNew(jsonField.getNumNew());
			param.setBlk(jsonField.getBlk()); 
			param.setFieldRow(jsonField.getFieldRow());
			param.setTrial(jsonField.getTrial());
			param.setFieldOrder(jsonField.getFieldOrder());
			param.setRowPerBlk(jsonField.getRowPerBlk());

		}else if(jsonField.getDesign()==2){ // Augmented Latin Square
			//String path, String fieldBookName, Integer numCheck, Integer numNew, String trmtName,
			
			//Integer trial, Integer numFieldRow, String fieldOrder, String trmtLabel, String[] checkTrmt, String[] newTrmt
			
			param.setNumCheck(jsonField.getNumCheck());
			param.setNumNew(jsonField.getNumNew());
			param.setTrmtName(jsonField.getTrmtName());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setTrial(jsonField.getTrial());
			param.setFieldOrder(jsonField.getFieldOrder());
			
/*			if(!(jsonField.getTrmtLabel().equals("NULL"))){
				param.setTrmtLabel(jsonField.getTrmtLabel());
				
			}else{
				param.setTrmtLabel(null);
			}

			if(!(jsonField.getChecktrmt().equals("NULL"))){
				param.setChecktrmt(jsonField.getChecktrmt());
				
			}else{
				param.setChecktrmt(null);

			}

			if(!(jsonField.getNewTrmt().equals("NULL"))){
				param.setNewTrmt(jsonField.getNewTrmt());
			}else{
				param.setNewTrmt(null);
			}*/
			
			param.setTrmtLabel(null);
			param.setChecktrmt(null);
			param.setNewTrmt(null);

			
			
		}else if(jsonField.getDesign()==3){ // Alpha Lattice

			param.setNumTrmt(jsonField.getNumTrmt());
			param.setBlkSize(jsonField.getBlkSize());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowPerBlk(jsonField.getRowPerBlk());
			param.setRowPerRep(jsonField.getRowPerRep());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

		}else if(jsonField.getDesign()==4){ // Row Column

			param.setNumTrmt(jsonField.getNumTrmt());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowPerRep(jsonField.getRowPerRep());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

		}else if(param.getDesign()==5){ // Latinized Alpha Lattice

			param.setNumTrmt(jsonField.getNumTrmt());
			param.setBlkSize(jsonField.getBlkSize());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial()); 
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

		}else if(jsonField.getDesign()==6){ // Latinized Row Column
			param.setNumTrmt(jsonField.getNumTrmt());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowPerRep(jsonField.getRowPerRep()); 
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

		}else if(jsonField.getDesign()==7){ // Prep

			param.setTrmtGrpName(jsonField.getTrmtGrpName()); 
			param.setNumTrmtPerGrp(jsonField.getNumTrmtPerGrp());
			param.setTrmtRepPerGrp(jsonField.getTrmtRepPerGrp());
			param.setTrmtName(jsonField.getTrmtName());
			param.setBlk(jsonField.getBlk());
			param.setTrial(jsonField.getTrial()); 
			param.setRowPerBlk(jsonField.getRowPerBlk());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

			if(jsonField.getTrmtLabel().equals("NULL")){
				param.setTrmtLabel(null);
			}else{
				param.setTrmtLabel(jsonField.getTrmtLabel());
			}

			if(jsonField.getTrmtListPerGrp().equals("NULL")){
				param.setTrmtListPerGrp(null);
			}else{
				param.setTrmtListPerGrp(jsonField.getTrmtListPerGrp());
			}


		}else if(jsonField.getDesign()==8){ // Augmented Row Column

			param.setNumCheck(jsonField.getNumCheck());
			param.setNumNew(jsonField.getNumNew()); 
			param.setTrmtName(jsonField.getTrmtName());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowBlkPerRep(jsonField.getRowBlkPerRep()); 
			param.setRowPerRowBlk(jsonField.getRowPerRowBlk());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());

/*			if(!(jsonField.getTrmtLabel().equals("NULL"))){
				param.setTrmtLabel(jsonField.getTrmtLabel());
				
			}else{
				param.setTrmtLabel(null);
			}

			if(!(jsonField.getChecktrmt().equals("NULL"))){
				param.setChecktrmt(jsonField.getChecktrmt());
				
			}else{
				param.setChecktrmt(null);

			}

			if(!(jsonField.getNewTrmt().equals("NULL"))){
				param.setNewTrmt(jsonField.getNewTrmt());
			}else{
				param.setNewTrmt(null);
			}*/
			
			param.setTrmtLabel(null);
			param.setChecktrmt(null);
			param.setNewTrmt(null);

		}else if(jsonField.getDesign()==9){ // Augmented Alpha{
			
	/*		String path, String fieldBookName, Integer numCheck, Integer numNew, 
			String trmtName, Integer blkSize, Integer rep, Integer trial, Integer rowPerBlk, Integer rowPerRep, 
			Integer numFieldRow, String fieldOrder, String trmtLabel, String[] checkTrmt, String[] newTrmt*/
			

			param.setNumCheck(jsonField.getNumCheck());
			param.setNumNew(jsonField.getNumNew()); 
			param.setTrmtName(jsonField.getTrmtName());
			param.setBlkSize(jsonField.getBlkSize());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowPerBlk(jsonField.getRowPerBlk());
			param.setRowPerRep(jsonField.getRowPerRep());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());
		

/*			if(!(jsonField.getTrmtLabel().equals("NULL"))){
				param.setTrmtLabel(jsonField.getTrmtLabel());
				
			}else{
				param.setTrmtLabel(null);
			}

			if(!(jsonField.getChecktrmt().equals("NULL"))){
				param.setChecktrmt(jsonField.getChecktrmt());
				
			}else{
				param.setChecktrmt(null);

			}

			if(!(jsonField.getNewTrmt().equals("NULL"))){
				param.setNewTrmt(jsonField.getNewTrmt());
			}else{
				param.setNewTrmt(null);
			}*/
			
			param.setTrmtLabel(null);
			param.setChecktrmt(null);
			param.setNewTrmt(null);
		}


		return param;
	}
	
	/* This method create folder in the output folder of the WS
	 */

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



	@GET
	@Path("getResultFiles/{foldername}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response getResultFiles(@PathParam("foldername") String name) {
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

}
