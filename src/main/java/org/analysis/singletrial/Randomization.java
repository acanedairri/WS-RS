package org.analysis.singletrial;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	@Path("/alpha")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postDesignAlphaLattice(String json){
		String toreturn="";
		String msg;
		try{
			msg=validateInputSingleTrial(json);
			if(msg.length() > 0){
				return msg;
			}else{
				msg="Data has been received and started randomized";
				(new Thread(new Randomization(json,ctx))).start();
				return msg;
			}

		}catch(Exception e){

		}
		return null;
	}


	private String validateInputSingleTrial(String json) {
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	public void run() {
		try{

			long startTime=System.nanoTime();
			RServeManager rserve= new RServeManager();
			RandomizationParamModel param=assembleParameters(json);
			if(param.getDesign()==1){
				rserve.doDesignAlpha(param.getPath(),param.getFieldBookName(), param.getNumTrmt(), param.getBlkSize(),param.getRep(),param.getTrial(),
						param.getRowPerBlk(), param.getRowPerRep(),param.getNumFieldRow(),param.getFieldOrder());
			}else{
				
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
	
	private RandomizationParamModel assembleParameters(String json) {
		
		RandomizationParamModel param=new RandomizationParamModel(); 	
		Gson jsonInput= new Gson();
		RandomizationParamModel jsonField=jsonInput.fromJson(json, RandomizationParamModel.class);

		String realpath=ctx.getRealPath("/");
		String outputFolderPath=createOutputFolder(ctx,jsonField.getResultFolder()); //jsonField.getAnalysisResultFolder()
		String dataFolderPath=realpath+separator+"temp"+separator;

		FileUtilities fileUtil = new FileUtilities();
		String timestamp =  new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		
		if(jsonField.getDesign()==1){
			param.setFieldBookName(jsonField.getFieldBookName());
			param.setNumTrmt(jsonField.getNumTrmt());
			param.setBlkSize(jsonField.getBlkSize());
			param.setRep(jsonField.getRep());
			param.setTrial(jsonField.getTrial());
			param.setRowPerBlk(jsonField.getRowPerBlk());
			param.setNumFieldRow(jsonField.getNumFieldRow());
			param.setFieldOrder(jsonField.getFieldOrder());
		}
	
		
		return param;
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

	
	@GET
	@Path("alpha/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response testDesignAlpha(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"temp"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testDesignAlpha(outputFolderPath,dataFolderPath);
		/*	SingleTrialFileResourceModel singleTrialFileResourceModel = fetchOutputFolderFileResources(name);*/
/*			Gson gson = new Gson();
			toreturn=gson.toJson(singleTrialFileResourceModel);*/

		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}
}
