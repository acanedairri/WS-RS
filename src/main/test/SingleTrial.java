

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.analysis.model.SingleTrialParametersModel;
import org.analysis.rserve.manager.RServeManager;

import com.google.gson.Gson;


@Path("/SingleTrialTest")
public class SingleTrial {

	@Context
	private UriInfo context;
	private static String BSLASH = "\\";
	private static String FSLASH = "/";
	static String separator=java.nio.file.FileSystems.getDefault().getSeparator();
	private  String outputFolderPath;
	public  String dataFolderPath ;
	public FileResourceModel fileList= new FileResourceModel();
	

	@Context 
	ServletContext ctx;
	@Context
	UriInfo uriInfo;
	private SingleTrialParametersModel ssModel;

	// This method is called if TEXT_PLAIN is request

/*	@GET
	@Path("/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public Response postSingleTrial(@PathParam("name") String name) {
		String toreturn = null;
		try{
			String realpath=ctx.getRealPath("/");
			outputFolderPath=createOutputFolder(ctx,name);
			dataFolderPath=realpath+separator+"input"+separator;
			RServeManager rserve= new RServeManager();
			rserve.testSingleEnvironment(outputFolderPath,dataFolderPath);
			fileList=getOutputFolderFileResources(ctx,name);
			Gson gson = new Gson();
			toreturn=gson.toJson(fileList);


		}catch(Exception e){

		}
		return Response.status(320).entity(toreturn).build();

	}*/

	@POST
	@Path("/run")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	public String postSingleTrial(String json) {
		String toreturn="";
		try{
			assembleParameters(json);
			RServeManagerTest rserve= new RServeManagerTest();
			rserve.testSingleEnvironment(outputFolderPath,dataFolderPath);
			fileList=getOutputFolderFileResources(ctx,ssModel.getAnalysisResultFolder());
			Gson gson = new Gson();
			toreturn=gson.toJson(fileList);
			return toreturn;

		}catch(Exception e){

		}
		return toreturn;


		/*	return Response.status(302).entity(toreturn).build().toString();*/

	}

	private String createOutputFolder(@Context ServletContext ctx,String name){
		String path = null;
		try{
			String realthapth=ctx.getRealPath("/");
			path= realthapth+separator+"output"+separator+name+separator;
			File f = new File (path);
			
			f.mkdir();
			return path;
		}catch(Exception e){

		}
		return path;
	}


	private FileResourceModel getOutputFolderFileResources(@Context ServletContext ctx,String name){
		String path = null;
		FileResourceModel fileResourceModel= new FileResourceModel();
		try{
			System.out.println("testing file list");
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


	private void assembleParameters(String param){
		
		Gson jsonInput= new Gson();
		SingleTrialParametersModel jsonField=jsonInput.fromJson(param, SingleTrialParametersModel.class);
		ssModel= new SingleTrialParametersModel(); 	
		
		String realpath=ctx.getRealPath("/");
		outputFolderPath=createOutputFolder(ctx,ssModel.getAnalysisResultFolder());
		dataFolderPath=realpath+separator+"input"+separator;
		
		
		ssModel.setResultFolderName(outputFolderPath.replace(BSLASH, FSLASH));
		ssModel.setOutFileName(outputFolderPath.replace(BSLASH, FSLASH) + "SEA_output.txt");
		ssModel.setDataFileName(dataFolderPath.replace(BSLASH, FSLASH) + "RCB_Multi.csv");
		
		ssModel.setAnalysisResultFolder(jsonField.getAnalysisResultFolder());
		ssModel.setDesign(jsonField.getDesign());
		ssModel.setEnvironment(jsonField.getEnvironment());
		ssModel.setGenotype(jsonField.getGenotype());
		ssModel.setBlock(jsonField.getBlock());
		ssModel.setRep(jsonField.getRep());
		ssModel.setRow(jsonField.getRow());
		ssModel.setColumn(jsonField.getColumn());
		ssModel.setDescriptiveStat(jsonField.isDescriptiveStat()); 
		ssModel.setVarianceComponents(jsonField.isVarianceComponents());
		ssModel.setBoxplotRawData(jsonField.isBoxplotRawData());
		ssModel.setHistogramRawData(jsonField.isHistogramRawData());
		ssModel.setHeatmapResiduals(jsonField.isHeatmapResiduals());
		ssModel.setHeatmapRow(jsonField.getHeatmapRow());
		ssModel.setHeatmapColumn(jsonField.getHeatmapColumn());
		ssModel.setDiagnosticPlot(jsonField.isDiagnosticPlot());
		ssModel.setGenotypeFixed(jsonField.isGenotypeFixed());
		ssModel.setPerformPairwise(jsonField.isPerformPairwise());
		ssModel.setPairwiseAlpha(jsonField.getPairwiseAlpha());
		ssModel.setCompareControl(jsonField.isCompareControl());
		ssModel.setPerformAllPairwise(jsonField.isPerformAllPairwise());
		ssModel.setGenotypeRandom(jsonField.isGenotypeRandom());
		ssModel.setExcludeControls(jsonField.isExcludeControls());
		ssModel.setGenoPhenoCorrelation(jsonField.isGenoPhenoCorrelation());
		
		System.out.println(ssModel.toString());


	}


}
