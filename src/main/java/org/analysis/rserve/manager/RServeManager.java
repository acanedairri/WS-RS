package org.analysis.rserve.manager;

import java.util.ArrayList;
import java.util.List;

import org.analysis.model.OutlierParametersModel;
import org.analysis.model.SingleTrialParametersModel;
import org.analysis.util.InputTransform;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * This class contains connection to RServer and methods to  calling RPackages 
 * 
 * @author Alexander Cañeda
 */ 

public class RServeManager {
	private RConnection rConnection;
	private StringBuilder rscriptCommand;
	private InputTransform inputTransform;
	static String separator=java.nio.file.FileSystems.getDefault().getSeparator();
	private static String BSLASH = "\\";
	private static String FSLASH = "/";
	//private static String OUTPUTFOLDER_PATH = System.getProperty("user.dir")+ separator + "output" + separator; // Sessions.getCurrent().getWebApp().getRealPath("resultanalysis")+ System.getProperty("file.separator");
	//public static String DATA_PATH = System.getProperty("user.dir")+ separator + "input" +separator;

	//	private static String OUTPUTFOLDER_PATH = System.getProperty("user.dir")+ System.getProperty("file.separator") + "sample_datasets" + System.getProperty("file.separator"); // Sessions.getCurrent().getWebApp().getRealPath("resultanalysis")+ System.getProperty("file.separator");
	//	public static String DATA_PATH = System.getProperty("user.dir")+ System.getProperty("file.separator") + "sample_datasets" + System.getProperty("file.separator");


	//Constructor to iniliazed RServe Connection
	public RServeManager() {
		try {
			rConnection	= new RConnection();
			System.out.println("started rserve connection");
			inputTransform = new InputTransform();
			rConnection.eval("library(PBTools)");
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*
	 * Read data 
	 * @param dataFileName 
	 */
	private void readData(String dataFileName){
		String readData = "dataRead <- read.csv(\"" + dataFileName + "\", header = TRUE, na.strings = c(\"NA\",\".\",\"\"), blank.lines.skip=TRUE, sep = \",\")";
		System.out.println(readData);
		try {
			rConnection.eval(readData);
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		rConnection.close();
	}


	/*
	 * Get variable info by calling R
	 * @param filename 
	 * @param fileFormat- file format of the file either in csv,txt
	 * @param separator 
	 * return List of variable info: format- [variable:type]
	 */
	public ArrayList<String> getVariableInfo(String fileName, int fileFormat,String separator) {
		String funcGetVarInfo;
		ArrayList<String> toreturn=new ArrayList<String>();
		if (fileFormat == 2)  
			funcGetVarInfo = "varsAndTypes <- getVarInfo(fileName = \"" + fileName + "\", fileFormat = 2, separator = \"" + separator + "\")";
		else funcGetVarInfo = "varsAndTypes <- getVarInfo(fileName = \"" + fileName + "\", fileFormat = " + fileFormat + ", separator = NULL)"; 
		//		String writeTempData = "tryCatch(write.table(varsAndTypes,file =\"" + tempFileName + "\",sep=\":\",row.names=FALSE), error=function(err) \"notRun\")";

		System.out.println(funcGetVarInfo);
		//		System.out.println(writeTempData);

		String[] vars;

		try {
			rConnection.eval(funcGetVarInfo);
			//			rConnection.eval(writeTempData);
			vars = rConnection.eval("as.vector(varsAndTypes$Variable)").asStrings();
			String[] types = rConnection.eval("as.vector(varsAndTypes$Type)").asStrings();
			for (int i = 0; i < vars.length; i++){
				toreturn.add(vars[i]+":"+types[i]);
			}
			for(String s:toreturn){
				System.out.println(s);
			}
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rConnection.close();
		return toreturn;
	}




	/*This methods execute R to Single Trial Analysis */
	public void doSingleEnvironmentAnalysis(SingleTrialParametersModel ssaModel) {

		String resultFolderPath = ssaModel.getResultFolderName().replace(BSLASH, FSLASH);
		String outFileName = ssaModel.getOutFileName().replace(BSLASH, FSLASH);
		String dataFileName = ssaModel.getDataFileName().replace(BSLASH, FSLASH);
		int designIndex = ssaModel.getDesign();
		String[] respvars = ssaModel.getRespvars();
		String environment = ssaModel.getEnvironment();
		String[] environmentLevels = ssaModel.getEnvironmentLevels();
		String genotype = ssaModel.getGenotype();
		String block = ssaModel.getBlock();
		String rep = ssaModel.getRep();
		String row = ssaModel.getRow();
		String column = ssaModel.getColumn();
		boolean descriptiveStat = ssaModel.isDescriptiveStat();
		boolean varianceComponents = ssaModel.isVarianceComponents();
		boolean boxplotRawData = ssaModel.isBoxplotRawData();
		boolean histogramRawData = ssaModel.isHistogramRawData();
		boolean heatmapResiduals = ssaModel.isHeatmapResiduals();
		String heatmapRow = ssaModel.getHeatmapRow();
		String heatmapColumn = ssaModel.getHeatmapColumn();
		boolean diagnosticPlot = ssaModel.isDiagnosticPlot();
		boolean genotypeFixed = ssaModel.isGenotypeFixed();
		boolean performPairwise = ssaModel.isPerformPairwise();
		String pairwiseAlpha = ssaModel.getPairwiseAlpha();
		String[] genotypeLevels = ssaModel.getGenotypeLevels();
		String[] controlLevels = ssaModel.getControlLevels();
		boolean compareControl = ssaModel.isCompareControl();
		boolean performAllPairwise = ssaModel.isPerformAllPairwise();
		boolean genotypeRandom = ssaModel.isGenotypeRandom();
		boolean excludeControls = ssaModel.isExcludeControls();
		boolean genoPhenoCorrelation = ssaModel.isGenoPhenoCorrelation();

		System.out.println(ssaModel.toString());


		String respvarVector= inputTransform.createRVector(respvars);
		//		String genotypeLevelsVector= inputTransform.createRVector(genotypeLevels);
		String controlLevelsVector= inputTransform.createRVector(controlLevels);
		boolean runningFixedSuccess =true;
		boolean runningRandomSuccess =true;
		boolean printAllOutputFixed =true;
		boolean printAllOutputRandom =true;

		try {				
			String designUsed = new String();
			String design = new String();
			switch (designIndex) {
			case 0: {
				designUsed = "Randomized Complete Block (RCB)"; 
				design = "RCB"; 
				break;
			}
			case 1: {
				designUsed = "Augmented RCB"; 
				design = "AugRCB";
				break;
			}
			case 2: {
				designUsed = "Augmented Latin Square"; 
				design = "AugLS";
				break;
			}
			case 3: {
				designUsed = "Alpha-Lattice"; 
				design = "Alpha";
				break;
			}
			case 4: {
				designUsed = "Row-Column"; 
				design = "RowCol";
				break;
			}
			case 5: {
				designUsed = "Latinized Alpha-Lattice"; 
				design = "LatinAlpha";
				break;
			}
			case 6: {
				designUsed = "Latinized Row-Column"; 
				design = "LatinRowCol";
				break;
			}
			default: {
				designUsed = "Randomized Complete Block (RCB)"; 
				design = "RCB";
				break;
			}
			}

			String readData = "data <- read.csv(\"" + dataFileName + "\", header = TRUE, na.strings = c(\"NA\",\".\",\" \",\"\"), blank.lines.skip=TRUE, sep = \",\")";
			System.out.println(readData);
			rConnection.eval(readData);
			String runSuccessData = rConnection.eval("data").toString();

			if (runSuccessData != null && runSuccessData.equals("notRun")) {	
				System.out.println("error");
				rConnection.eval("capture.output(cat(\"\n***Error reading data.***\n\"),file=\"" + outFileName + "\",append = FALSE)"); 
			}
			else {
				String setWd = "setwd(\"" + resultFolderPath + "\")";
				System.out.println(setWd);
				rConnection.eval(setWd);
			}

			String usedData = "capture.output(cat(\"\nDATA FILE: " + dataFileName + "\n\",file=\"" + outFileName + "\"))";
			String outFile = "capture.output(cat(\"\nSINGLE-ENVIRONMENT ANALYSIS\n\"),file=\"" + outFileName + "\",append = TRUE)";
			String usedDesign = "capture.output(cat(\"\nDESIGN: " + designUsed + "\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
			String sep = "capture.output(cat(\"------------------------------\"),file=\"" + outFileName + "\",append = TRUE)";
			String sep2 = "capture.output(cat(\"==============================\n\"),file=\"" + outFileName + "\",append = TRUE)";
			String outspace = "capture.output(cat(\"\n\"),file=\"" + outFileName + "\",append = TRUE)"; 

			rConnection.eval(usedData);
			rConnection.eval(outFile);
			rConnection.eval(usedDesign);

			// OUTPUT
			// Genotype Fixed
			if (genotypeFixed) {
				String funcSsaFixed = null;
				String groupVars = null;
				if (environment == "NULL") {
					if (designIndex == 0 || designIndex == 1){
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + block+ "\",column=NULL, rep=NULL," + environment+ ", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
					} else if (designIndex == 2) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + row+ "\",\"" + column + "\", rep=NULL," + environment+ ", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
					} else if (designIndex == 3 || designIndex == 5) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + block+ "\",column=NULL,\"" + rep + "\"," + environment+ ", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
					} else if (designIndex == 4 || designIndex == 6) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + row+ "\",\"" + column + "\",\"" + rep + "\","+ environment + ", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
					}
				} else {
					if (designIndex == 0 || designIndex == 1){
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + block+ "\",column=NULL, rep=NULL,\"" + environment+ "\", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
					} else if (designIndex == 2) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + row+ "\",\"" + column + "\", rep=NULL,\"" + environment+ "\", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
					} else if (designIndex == 3 || designIndex == 5) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + block+ "\",column=NULL,\"" + rep + "\",\"" + environment+ "\", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
					} else if (designIndex == 4 || designIndex == 6) {
						funcSsaFixed = "ssa1 <- try(ssa.test(\"" + design + "\",data,"+ respvarVector + ",\"" + genotype + "\",\"" + row+ "\",\"" + column + "\",\"" + rep + "\",\""+ environment + "\", is.random = FALSE), silent = TRUE)";
						groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
					}
				}
				String fixedHead = "capture.output(cat(\"GENOTYPE AS: Fixed\n\"),file=\""+ outFileName + "\",append = TRUE)";
				System.out.println("funcSsaFixed:"+funcSsaFixed);
				rConnection.eval(funcSsaFixed);
				rConnection.eval(sep2);
				rConnection.eval(fixedHead);
				rConnection.eval(sep2);
				rConnection.eval(outspace);

				System.out.println(funcSsaFixed);

				String runSuccess = rConnection.eval("class(ssa1)").asString();
				System.out.println("rs: "+runSuccess);
				if (runSuccess != null && runSuccess.equals("try-error")) {	
					System.out.println("ssa.test: error");
					String checkError = "msg <- trimStrings(strsplit(ssa1, \":\")[[1]])";
					String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
					String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
					String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.test function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
					rConnection.eval(checkError);
					rConnection.eval(checkError2);
					rConnection.eval(checkError3);
					rConnection.eval(checkError4);

					runningFixedSuccess=false;

				}
				else {
					for (int k = 0; k < respvars.length; k++) {
						int i = k + 1; // 1-relative index;
						String respVarHead = "capture.output(cat(\"\nRESPONSE VARIABLE: " + respvars[k] + "\n\"),file=\"" + outFileName + "\",append = TRUE)";
						rConnection.eval(sep);
						rConnection.eval(respVarHead);
						rConnection.eval(sep);
						rConnection.eval(outspace);

						// optional output: descriptive statistics
						if (descriptiveStat) {
							String funcDesc = null;
							if (environment=="NULL") {
								funcDesc = "outDesc <- try(DescriptiveStatistics(data, \"" + respvars[k] + "\", " + environment + "), silent=TRUE)";
							} else {
								funcDesc = "outDesc <- try(DescriptiveStatistics(data, \"" + respvars[k] + "\", \"" + environment + "\"), silent=TRUE)";
							}
							System.out.println(funcDesc);
							rConnection.eval(funcDesc);

							String outDescStat = "capture.output(cat(\"DESCRIPTIVE STATISTICS:\n\n\"),file=\"" + outFileName + "\",append = TRUE)"; 
							String outDescStat2 = "capture.output(outDesc,file=\"" + outFileName + "\",append = TRUE)";

							String runSuccessDescStat = rConnection.eval("class(outDesc)").toString();	
							if (runSuccessDescStat != null && runSuccessDescStat.equals("try-error")) {	
								System.out.println("desc stat: error");
								String checkError = "msg <- trimStrings(strsplit(outDesc, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in DescriptiveStatistics function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							} 
							else {
								rConnection.eval(outspace);
								rConnection.eval(outDescStat);
								rConnection.eval(outDescStat2);
								rConnection.eval(outspace);
							}
						}
						int envLevelsLength=0;
						if (environment == "NULL") {
							envLevelsLength = 1;
						} else {
							envLevelsLength = environmentLevels.length;
						}

						for (int m = 0; m < envLevelsLength; m++) { // no of envts or sites
							printAllOutputFixed=true;
							int j = m + 1; // 1-relative index;
							if (environment != "NULL") {
								String envtHead = "capture.output(cat(\"\nANALYSIS FOR: "+ environment + "\", \" = \" ,ssa1$output[[" + i	+ "]]$site[[" + j + "]]$env,\"\n\"),file=\""+ outFileName + "\",append = TRUE)";
								rConnection.eval(sep);
								System.out.println(envtHead);
								//								rConnection.eval(envtHead);
								rConnection.eval(sep);
								rConnection.eval(outspace);
							}

							//check if the data has too many missing observation
							double nrowData=rConnection.eval("ssa1$output[[" + i + "]]$site[[" + j + "]]$responseRate").asDouble();
							if (nrowData < 0.80) {
								String allNAWarning = rConnection.eval("ssa1$output[[" + i + "]]$site[[" + j + "]]$manyNAWarning").toString();
								String printError1 = "capture.output(cat(\"***\\n\"), file=\"" + outFileName + "\",append = TRUE)";
								String printError2 = "capture.output(cat(\"ERROR:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
								String printError3 = "capture.output(cat(\"" + allNAWarning + "\\n\"), file=\"" + outFileName + "\",append = TRUE)";

								rConnection.eval(outspace);
								rConnection.eval(printError1);
								rConnection.eval(printError2);
								rConnection.eval(printError3);
								rConnection.eval(printError1);
								rConnection.eval(outspace);
								rConnection.eval(outspace);
								printAllOutputFixed=false;

							} else {
								String lmerRun=rConnection.eval("ssa1$output[[" + i + "]]$site[[" + j + "]]$lmerRun").toString();
								if (lmerRun.equals("ERROR")) {
									String lmerError = rConnection.eval("ssa1$output[[" + i + "]]$site[[" + j + "]]$lmerError").toString();
									String printError1 = "capture.output(cat(\"***\\n\"), file=\"" + outFileName + "\",append = TRUE)";
									String printError2 = "capture.output(cat(\"ERROR:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
									String printError3 = "capture.output(cat(\"" + lmerError + "\\n\"), file=\"" + outFileName + "\",append = TRUE)";

									rConnection.eval(outspace);
									rConnection.eval(printError1);
									rConnection.eval(printError2);
									rConnection.eval(printError3);
									rConnection.eval(printError1);
									rConnection.eval(outspace);
									rConnection.eval(outspace);
									printAllOutputFixed=false;
								}
							}

							if (printAllOutputFixed) {
								// default output: trial summary
								String funcTrialSum = "funcTrialSum <- try(class.information(" + groupVars + ",ssa1$output[[" + i + "]]$site[[" + j + "]]$data), silent=TRUE)";
								String trialSumHead = "capture.output(cat(\"\nDATA SUMMARY:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String trialObsRead = "capture.output(cat(\"Number of observations read: \", ssa1$output[["	+ i	+ "]]$site[[" + j + "]]$obsread[[1]],\"\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String trialObsUsed = "capture.output(cat(\"Number of observations used: \", ssa1$output[["	+ i	+ "]]$site[[" + j + "]]$obsused[[1]],\"\n\n\"),file=\""	+ outFileName + "\",append = TRUE)";
								String trialSum = "capture.output(funcTrialSum,file=\"" + outFileName + "\",append = TRUE)";

								rConnection.eval(funcTrialSum);
								//								System.out.println(funcTrialSum);

								String runSuccessTS = rConnection.eval("class(funcTrialSum)").toString();
								if (runSuccessTS != null && runSuccessTS.equals("try-error")) {	
									System.out.println("class info: error");
									String checkError = "msg <- trimStrings(strsplit(funcTrialSum, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in class.information function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								}
								else {
									rConnection.eval(trialSumHead);
									rConnection.eval(trialObsRead);
									rConnection.eval(trialObsUsed);
									rConnection.eval(trialSum);
									rConnection.eval(outspace);
								}	

								// optional output: variance components
								if (varianceComponents) {
									String outVarComp = "capture.output(cat(\"\nVARIANCE COMPONENTS TABLE:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
									String outVarComp2 = "capture.output(ssa1$output[[" + i + "]]$site[[" + j + "]]$varcomp.table,file=\"" + outFileName + "\",append = TRUE)";

									rConnection.eval(outVarComp);
									rConnection.eval(outVarComp2);
									rConnection.eval(outspace);
								}

								//default output: test for genotypic effect
								String outAnovaTable1 = "capture.output(cat(\"\nTESTING FOR THE SIGNIFICANCE OF GENOTYPIC EFFECT:\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outAnovaTable2 = "library(lmerTest)";
								String outAnovaTable3 = "model1b <- lmer(formula(ssa1$output[[" + i + "]]$site[[" + j + "]]$formula1), data = ssa1$output[[" + i + "]]$site[[" + j + "]]$data, REML = T)";
								String outAnovaTable4 = "a.table <- anova(model1b)";
								String outAnovaTable5 = "pvalue <- formatC(as.numeric(format(a.table[1,6], scientific=FALSE)), format=\"f\")";
								String outAnovaTable6 = "a.table<-cbind(round(a.table[,c(\"NumDF\",\"Sum Sq\", \"Mean Sq\", \"F.value\", \"DenDF\")], digits=4),pvalue)";//"a.table<-cbind(round(a.table[,1:5], digits=4),pvalue)"; 
								String outAnovaTable7 = "colnames(a.table)<-c(\"Df\", \"Sum Sq\", \"Mean Sq\", \"F value\", \"Denom\", \"Pr(>F)\")";
								String outAnovaTable8 = "capture.output(cat(\"Analysis of Variance Table with Satterthwaite Denominator Df\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outAnovaTable9 = "capture.output(a.table,file=\"" + outFileName + "\",append = TRUE)";
								String outAnovaTable10 = "detach(\"package:lmerTest\")";

								rConnection.eval(outAnovaTable1);
								rConnection.eval(outAnovaTable2);
								rConnection.eval(outAnovaTable3);
								rConnection.eval(outAnovaTable4);
								rConnection.eval(outAnovaTable5);
								rConnection.eval(outAnovaTable6);
								rConnection.eval(outAnovaTable7);
								rConnection.eval(outspace);
								rConnection.eval(outAnovaTable8);
								rConnection.eval(outAnovaTable9);
								rConnection.eval(outspace);
								rConnection.eval(outAnovaTable10);

								// default output: test for genotypic effect
								//								String outAnovaTable1b = "capture.output(cat(\"\nTESTING FOR THE SIGNIFICANCE OF GENOTYPIC EFFECT:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								//								String outAnovaTable2b = "library(pbkrtest)";
								//								String outAnovaTable3b = "model1b <- lmer(formula(ssa1$output[[" + i + "]]$site[[" + j + "]]$formula1), data = ssa1$output[[" + i + "]]$site[[" + j + "]]$data, REML = T)";
								//								String outAnovaTable4b = "model2b <- lmer(formula(ssa1$output[[" + i + "]]$site[[" + j + "]]$formula2), data = ssa1$output[[" + i + "]]$site[[" + j + "]]$data, REML = T)";
								//								String outAnovaTable5b = "anova.table1 <- KRmodcomp(model1b, model2b)[[1]][1,]";
								//								String outAnovaTable6b = "anova.table1 <- anova.table1[-c(4)]";
								//								String outAnovaTable7b = "rownames(anova.table1) <- " + genotype;
								//								String outAnovaTable8b = "colnames(anova.table1) <- c(\"F value\", \"Num df\", \"Denom df\", \"Pr(>F)\")";
								//								String outAnovaTable9b = "anova.table1[1, \"F value\"] <- format(round(anova.table1[1, \"F value\"],2), digits=2, nsmall=2, scientific=FALSE)";
								//								String outAnovaTable10b = "anova.table1[1, \"Pr(>F)\"] <- formatC(as.numeric(format(anova.table1[1, \"Pr(>F)\"], scientific=FALSE)), format=\"f\")";
								//								String outAnovaTable11b = "capture.output(anova.table1,file=\"" + outFileName + "\",append = TRUE)";
								//								String outAnovaTable12b = "detach(\"package:pbkrtest\")";
								//								
								//								rConnection.eval(outAnovaTable1b);
								//								rConnection.eval(outAnovaTable2b);
								//								rConnection.eval(outAnovaTable3b);
								//								rConnection.eval(outAnovaTable4b);
								//								rConnection.eval(outAnovaTable5b);
								//								rConnection.eval(outAnovaTable6b);
								//								rConnection.eval(outAnovaTable7b);
								//								rConnection.eval(outAnovaTable8b);
								//								rConnection.eval(outAnovaTable9b);
								//								rConnection.eval(outAnovaTable10b);
								//								rConnection.eval(outAnovaTable11b);
								//								rConnection.eval(outAnovaTable12b);
								//								rConnection.eval(outspace);

								//								String outAnovaTable1b = "capture.output(cat(\"\nTESTING FOR THE SIGNIFICANCE OF GENOTYPIC EFFECT:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								//								String outAnovaTable2b = "capture.output(ssa1$output[[" + i + "]]$site[[" + j + "]]$model.comparison,file=\"" + outFileName + "\",append = TRUE)";
								//								
								//								rConnection.eval(outAnovaTable1b);
								//								rConnection.eval(outAnovaTable2b);
								//								rConnection.eval(outspace);

								//default output: LSMeans
								String outDescStat = "capture.output(cat(\"\nGENOTYPE LSMEANS AND STANDARD ERRORS:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outDescStat2 = "capture.output(ssa1$output[[" + i + "]]$site[[" + j + "]]$summary.statistic,file=\"" + outFileName + "\",append = TRUE)"; 

								rConnection.eval(outDescStat);
								rConnection.eval(outDescStat2);
								rConnection.eval(outspace);

								//default output: standard error of the differences
								String outsedTable = "capture.output(cat(\"\nSTANDARD ERROR OF THE DIFFERENCE (SED):\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outsedTable2 = "capture.output(ssa1$output[[" + i + "]]$site[[" + j + "]]$sedTable,file=\"" + outFileName + "\",append = TRUE)";

								rConnection.eval(outsedTable);
								rConnection.eval(outsedTable2);
								rConnection.eval(outspace);


								if (performPairwise) {

									double pairwiseSig = Double.valueOf(pairwiseAlpha);

									//									rConnection.rniAssign("trmt.levels",	rConnection.rniPutStringArray(genotypeLevels),0); // a string array from OptionsPage

									if (compareControl) {
										//											rConnection.rniAssign("controlLevels",rConnection.rniPutStringArray(controlLevels),0); // a string array from OptionsPage

										String funcPwC = "pwControl <- try(ssa.pairwise(ssa1$output[[" + i + "]]$site[["	+ j	+ "]]$model, type = \"Dunnett\", alpha = "	+ pairwiseSig + ", control.level = " + controlLevelsVector + "), silent=TRUE)";
										String outCompareControl = "capture.output(cat(\"\nSIGNIFICANT PAIRWISE COMPARISONS (IF ANY): \nCompared with control(s)\n\n\"),file=\"" + outFileName	+ "\",append = TRUE)";
										String outCompareControl2n = "capture.output(pwControl$result,file=\""	+ outFileName	+ "\",append = TRUE)";
										String outCompareControl3n = "capture.output(cat(\"\n\n\"),file=\"" + outFileName	+ "\",append = TRUE)";
										System.out.println(funcPwC);
										rConnection.eval(funcPwC);
										rConnection.eval(outCompareControl);


										String runSuccessPwC = rConnection.eval("class(pwControl)").toString();	
										if (runSuccessPwC != null && runSuccessPwC.equals("try-error")) {	
											System.out.println("compare with control: error");
											String checkError = "msg <- trimStrings(strsplit(pwControl, \":\")[[1]])";
											String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
											String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
											String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.pairwise function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
											rConnection.eval(checkError);
											rConnection.eval(checkError2);
											rConnection.eval(checkError3);
											rConnection.eval(checkError4);
										}
										else {

											rConnection.eval(outCompareControl2n);

											// display warning generated by checkTest in ssa.test
											String warningControlTest = rConnection.eval("pwControl$controlTestWarning").toString();

											if (!warningControlTest.equals("NONE")) {
												String warningCheckTest2 = "capture.output(cat(\"----- \nNOTE:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
												String warningCheckTest3 = "capture.output(cat(\"" + warningControlTest + "\\n\"), file=\"" + outFileName + "\",append = TRUE)";

												rConnection.eval(warningCheckTest2);
												rConnection.eval(warningCheckTest3);
											}

											rConnection.eval(outCompareControl3n);

											System.out.println("pairwise control test:" + warningControlTest); 

										}
									} else if (performAllPairwise) {
										String outPerformAllPairwise = "capture.output(cat(\"\nSIGNIFICANT PAIRWISE COMPARISONS (IF ANY): \n\n\"),file=\""	+ outFileName	+ "\",append = TRUE)";
										rConnection.eval(outPerformAllPairwise);
										if (genotypeLevels.length > 0 && genotypeLevels.length < 16) {
											String funcPwAll = "pwAll <- try(ssa.pairwise(ssa1$output[[" + i + "]]$site[[" + j + "]]$model, type = \"Tukey\", alpha = "+ pairwiseSig + ", control.level = NULL), silent=TRUE)";
											String outPerformAllPairwise2 = "capture.output(pwAll$result,file=\"" + outFileName + "\",append = TRUE)";
											String outPerformAllPairwise3 = "capture.output(cat(\"\n\"),file=\""	+ outFileName	+ "\",append = TRUE)";
											rConnection.eval(funcPwAll);
											//												System.out.println(funcPwAll);

											String runSuccessPwAll = rConnection.eval("class(pwAll)").toString();
											if (runSuccessPwAll != null && runSuccessPwAll.equals("try-error")) {	
												System.out.println("all pairwise: error");
												String checkError = "msg <- trimStrings(strsplit(pwAll, \":\")[[1]])";
												String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
												String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
												String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.pairwise function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
												rConnection.eval(checkError);
												rConnection.eval(checkError2);
												rConnection.eval(checkError3);
												rConnection.eval(checkError4);
											}
											else {
												rConnection.eval(outPerformAllPairwise2);
												rConnection.eval(outPerformAllPairwise3);
											}
										} else {
											String nLevelsLarge = "capture.output(cat(\"***\nExceeded maximum number of genotypes that can be compared. \n***\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
											rConnection.eval(nLevelsLarge);
										}
									}
								} else {
									rConnection.eval(outspace);
								}
							}

						} // end of for loop for diff envts
					}

					//default output: save the means, standard error of the mean, variance and no. of reps in a file
					String checkMeanSSE = rConnection.eval("ssa1$meansseWarning").toString();
					String checkVarRep = rConnection.eval("ssa1$varrepWarning").toString();
					System.out.println("checkMeanSSE: " + checkMeanSSE);
					System.out.println("checkVarRep: " + checkVarRep);

					if (checkMeanSSE.equals("empty") | checkVarRep.equals("empty")) {
						System.out.println("Saving means not done.");
					} else {
						String meansFileName = "meansFileName <- paste(\"" + resultFolderPath + "\",\"summaryStats.csv\", sep=\"\")";
						String funcSaveSesVarRep=null;
						if (environment=="NULL") {
							funcSaveSesVarRep = "meansVar <- merge(ssa1$meansse,ssa1$varrep, by = \"EnvLevel\")";
						} else {
							funcSaveSesVarRep = "meansVar <- merge(ssa1$meansse,ssa1$varrep, by = \"" + environment + "\")";
						}
						String funcSaveSesVarRepCsv = "saveMeans <- try(write.table(meansVar,file = meansFileName ,sep=\",\",row.names=FALSE), silent=TRUE)";

						rConnection.eval(meansFileName);
						rConnection.eval(funcSaveSesVarRep);
						rConnection.eval(funcSaveSesVarRepCsv);

						String runSuccessSaveMeansSes = rConnection.eval("class(saveMeans)").toString();
						if (runSuccessSaveMeansSes != null && runSuccessSaveMeansSes.equals("try-error")) {	
							System.out.println("saving means file: error");
							String checkError = "msg <- trimStrings(strsplit(saveMeans, \":\")[[1]])";
							String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
							String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
							String checkError4 = "capture.output(cat(\"*** \nERROR in saving means file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(checkError);
							rConnection.eval(checkError2);
							rConnection.eval(checkError3);
							rConnection.eval(checkError4);
						} 
					}

					//diagnostic plots for genotype fixed
					if (diagnosticPlot) {
						String diagPlotsFunc=null;
						if (environment=="NULL") {
							diagPlotsFunc = "diagPlots <- try(graph.sea.diagplots(data, " + respvarVector + ", env = " + environment + ", is.random = FALSE, ssa1), silent=TRUE)";
						} else {
							diagPlotsFunc = "diagPlots <- try(graph.sea.diagplots(data, " + respvarVector + ", env = \"" + environment + "\", is.random = FALSE, ssa1), silent=TRUE)";
						}
						System.out.println(diagPlotsFunc);
						rConnection.eval(diagPlotsFunc);

						String runSuccessDiagPlots = rConnection.eval("class(diagPlots)").toString();
						if (runSuccessDiagPlots != null && runSuccessDiagPlots.equals("try-error")) {	
							System.out.println("diagnostic plots(genotype fixed): error");
							String checkError = "msg <- trimStrings(strsplit(diagPlots, \":\")[[1]])";
							String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
							String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
							String checkError4 = "capture.output(cat(\"*** \nERROR in graph.sea.diagplots function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(checkError);
							rConnection.eval(checkError2);
							rConnection.eval(checkError3);
							rConnection.eval(checkError4);
						}
					}
				}  
			} // end of if fixed


			// Genotype Random
			if (genotypeRandom == true) {
				String funcSsaRandom = null;
				String groupVars = null;

				if (excludeControls) {
					if (environment == "NULL") {
						if (designIndex == 0){
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + ", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 1){
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + ", is.random = TRUE, excludeCheck=TRUE, checkList= NULL), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 2) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL," + environment + ", is.random = TRUE, excludeCheck=TRUE, checkList= NULL), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
						} else if (designIndex == 3 || designIndex == 5) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\"," + environment + ", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
						} else if (designIndex == 4 || designIndex == 6) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\"," + environment + ", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
						}
					} else {
						if (designIndex == 0){
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 1) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\", is.random = TRUE, excludeCheck=TRUE, checkList= NULL), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 2) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL,\"" + environment + "\", is.random = TRUE, excludeCheck=TRUE, checkList= NULL), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
						} else if (designIndex == 3 || designIndex == 5) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\",\"" + environment + "\", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
						} else if (designIndex == 4 || designIndex == 6) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\",\"" + environment + "\", is.random = TRUE, excludeCheck=TRUE, checkList= " + controlLevelsVector + "), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
						}
					}
				} else {
					if (environment == "NULL") {
						if (designIndex == 0 || designIndex == 1) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + ", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 2) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL," + environment + ", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
						} else if (designIndex == 3 || designIndex == 5) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\"," + environment + ", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
						} else if (designIndex == 4 || designIndex == 6) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\"," + environment + ", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
						}
					} else {
						if (designIndex == 0 || designIndex == 1) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\")";
						} else if (designIndex == 2) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL,\"" + environment + "\", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + row + "\", \"" + column + "\")";
						} else if (designIndex == 3 || designIndex == 5) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\",\"" + environment + "\", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + block + "\", \"" + rep + "\")";
						} else if (designIndex == 4 || designIndex == 6) {
							funcSsaRandom = "ssa2 <- try(ssa.test(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\",\"" + environment + "\", is.random = TRUE), silent=TRUE)";
							groupVars = "c(\"" + genotype + "\", \"" + rep + "\", \"" + row + "\", \"" + column + "\")";
						}
					}
				}
				String randomHead = "capture.output(cat(\"GENOTYPE AS: Random\n\"),file=\"" + outFileName + "\",append = TRUE)";
				rConnection.eval(funcSsaRandom);
				rConnection.eval(sep2);
				rConnection.eval(randomHead);
				rConnection.eval(sep2);
				rConnection.eval(outspace);
				System.out.println(funcSsaRandom);

				String runSuccess2 = rConnection.eval("class(ssa2)").toString();	
				if (runSuccess2 != null && runSuccess2.equals("try-error")) {	
					System.out.println("ssa2: error");
					String checkError = "msg <- trimStrings(strsplit(ssa2, \":\")[[1]])";
					String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
					String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
					String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.test function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
					rConnection.eval(checkError);
					rConnection.eval(checkError2);
					rConnection.eval(checkError3);
					rConnection.eval(checkError4);

					runningRandomSuccess=false;
				}
				else {

					for (int k = 0; k < respvars.length; k++) {
						int i = k + 1; // 1-relative index;
						String respVarHead = "capture.output(cat(\"\nRESPONSE VARIABLE: " + respvars[k] + "\n\"),file=\"" + outFileName + "\",append = TRUE)";
						rConnection.eval(sep);
						rConnection.eval(respVarHead);
						rConnection.eval(sep);
						rConnection.eval(outspace);

						// optional output: descriptive statistics
						if (descriptiveStat) {
							String funcDesc = null;
							if (environment == "NULL") {
								funcDesc = "outDesc <- try(DescriptiveStatistics(data, \"" + respvars[k] + "\", " + environment + "), silent=TRUE)";
							} else {
								funcDesc = "outDesc <- try(DescriptiveStatistics(data, \"" + respvars[k] + "\", \"" + environment + "\"), silent=TRUE)";
							}
							rConnection.eval(funcDesc);
							System.out.println(funcDesc);
							String outDescStat = "capture.output(cat(\"DESCRIPTIVE STATISTICS:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
							String outDescStat2 = "capture.output(outDesc,file=\"" + outFileName + "\",append = TRUE)"; 

							String runSuccessDescStat = rConnection.eval("class(outDesc)").toString();
							if (runSuccessDescStat != null && runSuccessDescStat.equals("try-error")) {	
								System.out.println("desc stat: error");
								String checkError = "msg <- trimStrings(strsplit(outDesc, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in DescriptiveStatistics function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							} 
							else {
								rConnection.eval(outspace);
								rConnection.eval(outDescStat);
								rConnection.eval(outDescStat2);
								rConnection.eval(outspace);
							}	
						}
						int envLevelsLength2 = 0;
						if (environment == "NULL") {
							envLevelsLength2 = 1;
						} else {
							envLevelsLength2 = environmentLevels.length;
						}
						for (int m = 0; m < envLevelsLength2; m++) { // no of envts or sites
							printAllOutputRandom=true;
							int j = m + 1; // 1-relative index;

							if (environment != "NULL") {
								String envtHead = "capture.output(cat(\"\nANALYSIS FOR: "+ environment + "\", \" = \" ,ssa2$output[[" + i	+ "]]$site[[" + j + "]]$env,\"\n\"),file=\""+ outFileName + "\",append = TRUE)";
								rConnection.eval(sep);
								rConnection.eval(envtHead);
								rConnection.eval(sep);
								rConnection.eval(outspace);
							}

							//check if the data has too many missing observations
							double responseRate=rConnection.eval("ssa2$output[[" + i + "]]$site[[" + j + "]]$responseRate").asDouble();
							if (responseRate < 0.8) {
								String allNAWarning2 = rConnection.eval("ssa2$output[[" + i + "]]$site[[" + j + "]]$manyNAWarning").toString();
								String printError1 = "capture.output(cat(\"***\\n\"), file=\"" + outFileName + "\",append = TRUE)";
								String printError2 = "capture.output(cat(\"ERROR:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
								String printError3 = "capture.output(cat(\"" + allNAWarning2 + "\\n\"), file=\"" + outFileName + "\",append = TRUE)";

								rConnection.eval(outspace);
								rConnection.eval(printError1);
								rConnection.eval(printError2);
								rConnection.eval(printError3);
								rConnection.eval(printError1);
								rConnection.eval(outspace);
								rConnection.eval(outspace);
								printAllOutputRandom=false;
							} else {
								String lmerRun=rConnection.eval("ssa2$output[[" + i + "]]$site[[" + j + "]]$lmerRun").toString();
								if (lmerRun.equals("ERROR")) {
									String lmerError = rConnection.eval("ssa2$output[[" + i + "]]$site[[" + j + "]]$lmerError").toString();
									String printError1 = "capture.output(cat(\"***\\n\"), file=\"" + outFileName + "\",append = TRUE)";
									String printError2 = "capture.output(cat(\"ERROR:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
									String printError3 = "capture.output(cat(\"" + lmerError + "\\n\"), file=\"" + outFileName + "\",append = TRUE)";

									rConnection.eval(outspace);
									rConnection.eval(printError1);
									rConnection.eval(printError2);
									rConnection.eval(printError3);
									rConnection.eval(printError1);
									rConnection.eval(outspace);
									rConnection.eval(outspace);
									printAllOutputRandom=false;
								}
							}

							if (printAllOutputRandom) {
								// display warning generated by checkTest in ssa.test
								String warningCheckTest = rConnection.eval("ssa2$output[[" + i	+ "]]$site[[" + j + "]]$checkTestWarning").toString();

								if (!warningCheckTest.equals("NONE")) {
									String warningCheckTest2 = "capture.output(cat(\"\n*** \nWARNING:\\n\"), file=\"" + outFileName + "\",append = TRUE)";
									String warningCheckTest3 = "capture.output(cat(\"" + warningCheckTest + "\"), file=\"" + outFileName + "\",append = TRUE)";
									String warningCheckTest4 = "capture.output(cat(\"\n*** \\n\"), file=\"" + outFileName + "\",append = TRUE)";

									rConnection.eval(warningCheckTest2);
									rConnection.eval(warningCheckTest3);
									rConnection.eval(warningCheckTest4);
								} 
								System.out.println("check test:" + warningCheckTest);

								// default output: trial summary
								String funcTrialSum = "funcTrialSum <- try(class.information(" + groupVars + ",ssa2$output[[" + i + "]]$site[[" + j + "]]$data), silent=TRUE)";
								String trialSumHead = "capture.output(cat(\"\nDATA SUMMARY:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String trialObsRead = "capture.output(cat(\"Number of observations read: \", ssa2$output[["	+ i	+ "]]$site[[" + j + "]]$obsread[[1]],\"\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String trialObsUsed = "capture.output(cat(\"Number of observations used: \", ssa2$output[["	+ i	+ "]]$site[[" + j + "]]$obsused[[1]],\"\n\n\"),file=\""	+ outFileName + "\",append = TRUE)";
								String trialSum = "capture.output(funcTrialSum,file=\"" + outFileName + "\",append = TRUE)";

								rConnection.eval(funcTrialSum);

								String runSuccessTS = rConnection.eval("class(funcTrialSum)").toString();
								if (runSuccessTS != null && runSuccessTS.equals("try-error")) {	
									System.out.println("class info: error");
									String checkError = "msg <- trimStrings(strsplit(funcTrialSum, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in class.information function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								}
								else {
									rConnection.eval(trialSumHead);
									rConnection.eval(trialObsRead);
									rConnection.eval(trialObsUsed);
									rConnection.eval(trialSum);
									rConnection.eval(outspace);
								}

								// optional output: variance components
								if (varianceComponents) {
									String outVarComp = "capture.output(cat(\"\nVARIANCE COMPONENTS TABLE:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
									String outVarComp2 = "capture.output(ssa2$output[[" + i + "]]$site[[" + j + "]]$varcomp.table,file=\"" + outFileName + "\",append = TRUE)";

									rConnection.eval(outVarComp);
									rConnection.eval(outVarComp2);
									rConnection.eval(outspace);
								}

								//default output: test genotypic effect
								String outTestGen1 = "capture.output(cat(\"\nTESTING FOR THE SIGNIFICANCE OF GENOTYPIC EFFECT USING -2 LOGLIKELIHOOD RATIO TEST:\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outTestGen2 = "capture.output(cat(\"\nFormula for Model1: \", ssa2$output[["	+ i	+ "]]$site[[" + j + "]]$formula1,\"\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outTestGen3 = "capture.output(cat(\"Formula for Model2: \", ssa2$output[["	+ i	+ "]]$site[[" + j + "]]$formula2,\"\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outTestGen4 = "capture.output(ssa2$output[[" + i + "]]$site[[" + j + "]]$models.table,file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(outTestGen1);
								rConnection.eval(outTestGen2);
								rConnection.eval(outTestGen3);
								rConnection.eval(outTestGen4);
								rConnection.eval(outspace);

								//default output: test for check effect
								String newExcludeCheck = rConnection.eval("ssa2$output[[" + i + "]]$site[[" + j + "]]$newExcludeCheck").toString();
								System.out.println("newExcludeCheck: " + newExcludeCheck);

								if (newExcludeCheck.equals("TRUE")) {
									String outAnovaTable1 = "capture.output(cat(\"\nTESTING FOR THE SIGNIFICANCE OF CHECK EFFECT:\n\"),file=\"" + outFileName + "\",append = TRUE)";
									String outAnovaTable2 = "library(lmerTest)";
									String outAnovaTable3 = "model2b <- lmer(formula(ssa2$output[[" + i + "]]$site[[" + j + "]]$formula1), data = ssa2$output[[" + i + "]]$site[[" + j + "]]$data, REML = T)";
									String outAnovaTable4 = "a.table <- anova(model2b)";
									String outAnovaTable5 = "pvalue <- formatC(as.numeric(format(a.table[1,6], scientific=FALSE)), format=\"f\")";
									String outAnovaTable6 ="a.table<-cbind(round(a.table[,c(\"NumDF\",\"Sum Sq\", \"Mean Sq\", \"F.value\", \"DenDF\")], digits=4),pvalue)";// "a.table<-cbind(round(a.table[,1:5], digits=4),pvalue)";
									String outAnovaTable7 = "colnames(a.table)<-c(\"Df\", \"Sum Sq\", \"Mean Sq\", \"F value\", \"Denom\", \"Pr(>F)\")";
									String outAnovaTable8 = "capture.output(cat(\"Analysis of Variance Table with Satterthwaite Denominator Df\n\"),file=\"" + outFileName + "\",append = TRUE)";
									String outAnovaTable9 = "capture.output(a.table,file=\"" + outFileName + "\",append = TRUE)";
									String outAnovaTable10 = "detach(\"package:lmerTest\")";

									//								rConnection.eval(outspace);
									rConnection.eval(outAnovaTable1);
									rConnection.eval(outAnovaTable2);
									rConnection.eval(outAnovaTable3);
									rConnection.eval(outAnovaTable4);
									rConnection.eval(outAnovaTable5);
									rConnection.eval(outAnovaTable6);
									rConnection.eval(outAnovaTable7);
									rConnection.eval(outspace);
									rConnection.eval(outAnovaTable8);
									rConnection.eval(outAnovaTable9);
									rConnection.eval(outspace);
									rConnection.eval(outAnovaTable10);
								}

								//default output: predicted means
								String outPredMeans = "capture.output(cat(\"\nPREDICTED MEANS:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outPredMeans2 = "capture.output(ssa2$output[[" + i + "]]$site[[" + j + "]]$summary.statistic,file=\"" + outFileName + "\",append = TRUE)"; 
								rConnection.eval(outPredMeans);
								rConnection.eval(outPredMeans2);
								rConnection.eval(outspace);

								//default output: lsmeans of checks
								if (excludeControls) {
									int newCheckListLength = rConnection.eval("ssa2$output[[" + i	+ "]]$site[[" + j + "]]$newCheckListLength").asInteger();

									if (newCheckListLength > 0) {
										String outLSMeansCheck = "capture.output(cat(\"\nCHECK/CONTROL LSMEANS:\n\n\"),file=\"" + outFileName + "\",append = TRUE)";
										String outLSMeansCheck2 = "capture.output(ssa2$output[[" + i + "]]$site[[" + j + "]]$lsmeans.checks,file=\"" + outFileName + "\",append = TRUE)"; 
										rConnection.eval(outLSMeansCheck);
										rConnection.eval(outLSMeansCheck2);
										rConnection.eval(outspace);
									}
								}

								//default output: estimate heritability
								String outEstHerit = "capture.output(cat(\"\nHERITABILITY:\n\"),file=\"" + outFileName + "\",append = TRUE)";
								String outEstHerit2 = "capture.output(ssa2$output[[" + i + "]]$site[[" + j + "]]$heritability,file=\""	+ outFileName + "\",append = TRUE)";
								String outEstHerit3 = "capture.output(cat(\"\n\"),file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(outEstHerit);
								rConnection.eval(outEstHerit2);
								rConnection.eval(outEstHerit3);
								rConnection.eval(outspace);
							}

						}
					}

					//optional output: estimate genotypic and phenotypic correlations
					if (genoPhenoCorrelation) {
						rConnection.eval(sep2);
						String funcEstCorr = null;
						if (excludeControls) {
							if (environment == "NULL") {
								if (designIndex == 0)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + ", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
								else if (designIndex == 1)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + ", excludeLevels=TRUE, excludeList = NULL), silent=TRUE)";
								else if (designIndex == 2)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL," + environment + ", excludeLevels=TRUE, excludeList = NULL), silent=TRUE)";
								else if (designIndex == 3)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\"," + environment + ", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
								else if (designIndex == 4)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\"," + environment + ", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
							} else {
								if (designIndex == 0)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
								else if (designIndex == 1)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\", excludeLevels=TRUE, excludeList = NULL), silent=TRUE)";
								else if (designIndex == 2)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL,\"" + environment + "\", excludeLevels=TRUE, excludeList = NULL), silent=TRUE)";
								else if (designIndex == 3)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\",\"" + environment + "\", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
								else if (designIndex == 4)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\",\"" + environment + "\", excludeLevels=TRUE, excludeList = " + controlLevelsVector + "), silent=TRUE)";
							}
						} else {
							if (environment == "NULL") {
								if (designIndex == 0 || designIndex == 1)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL," + environment + "), silent=TRUE)";
								else if (designIndex == 2)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL," + environment + "), silent=TRUE)";
								else if (designIndex == 3)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\"," + environment + "), silent=TRUE)";
								else if (designIndex == 4)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\"," + environment + "), silent=TRUE)";
							} else {
								if (designIndex == 0 || designIndex == 1)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL, rep=NULL,\"" + environment + "\"), silent=TRUE)";
								else if (designIndex == 2)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\", rep=NULL,\"" + environment + "\"), silent=TRUE)";
								else if (designIndex == 3)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + block + "\",column=NULL,\"" + rep + "\",\"" + environment + "\"), silent=TRUE)";
								else if (designIndex == 4)
									funcEstCorr = "gpcorr <- try(genoNpheno.corr(\"" + design + "\",data," + respvarVector + ",\"" + genotype + "\",\"" + row + "\",\"" + column + "\",\"" + rep + "\",\"" + environment + "\"), silent=TRUE)";
							}
						}

						System.out.println(funcEstCorr);
						rConnection.eval(funcEstCorr);	

						String runSuccessGPCorr = rConnection.eval("class(gpcorr)").toString();
						if (runSuccessGPCorr != null && runSuccessGPCorr.equals("try-error")) {	
							System.out.println("geno pheno corr: error");
							String checkError = "msg <- trimStrings(strsplit(gpcorr, \":\")[[1]])";
							String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
							String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
							String checkError4 = "capture.output(cat(\"*** \nERROR in genoNpheno.corr function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(checkError);
							rConnection.eval(checkError2);
							rConnection.eval(checkError3);
							rConnection.eval(checkError4);
						}
						else {
							String outEstGenoCorr = "capture.output(cat(\"\nGENOTYPIC CORRELATIONS:\n\"),file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(outEstGenoCorr);

							int envLevelsLength = 0;
							if (environment == "NULL") {
								envLevelsLength = 1;
							} else {
								envLevelsLength = environmentLevels.length;
							}

							for (int m = 0; m < envLevelsLength; m++) { // no of envts or sites
								int j = m + 1; // 1-relative index;
								if (environment != "NULL") {
									String outEstGenoCorr2 = "capture.output(cat(\"" + environment + " = \", gpcorr$EnvLevels[[" + j + "]]),file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(outspace);
									rConnection.eval(outEstGenoCorr2);
									rConnection.eval(outspace);
								}
								String outEstGenoCorr2b = "capture.output(gpcorr$GenoCorr[[" + j + "]],file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(outspace);
								rConnection.eval(outEstGenoCorr2b);
								rConnection.eval(outspace);
							}

							String outEstPhenoCorr = "capture.output(cat(\"\nPHENOTYPIC CORRELATIONS:\n\"),file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(outEstPhenoCorr);

							for (int m = 0; m < envLevelsLength; m++) { // no of envts or sites
								int j = m + 1; // 1-relative index;
								if (environment != "NULL") {
									String outEstPhenoCorr2 = "capture.output(cat(\"" + environment + " = \", gpcorr$EnvLevels[[" + j + "]]),file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(outspace);
									rConnection.eval(outEstPhenoCorr2);
									rConnection.eval(outspace);
								}
								String outEstPhenoCorr2b = "capture.output(gpcorr$PhenoCorr[[" + j + "]],file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(outspace);
								rConnection.eval(outEstPhenoCorr2b);
								rConnection.eval(outspace);
							}
						} //end of else for if runSuccessGPCorr	
					}

					//default option: save predicted means to a file
					String checkPredMean = rConnection.eval("ssa2$meansWarning").toString();
					System.out.println("checkPredMean: " + checkPredMean);

					if (checkPredMean.equals("empty")) {
						System.out.println("Saving predicted means not done.");
					} else {
						String meansFileName2 = "meansFileName2 <- paste(\"" + resultFolderPath + "\",\"predictedMeans.csv\", sep=\"\")";
						String funcSavePredMeansCsv = "saveDataB1 <- try(write.table(ssa2$means,file = meansFileName2 ,sep=\",\",row.names=FALSE), silent=TRUE)";
						rConnection.eval(meansFileName2);
						rConnection.eval(funcSavePredMeansCsv);

						String runSuccessSavePredMeans = rConnection.eval("class(saveDataB1)").toString();
						if (runSuccessSavePredMeans != null && runSuccessSavePredMeans.equals("try-error")) {	
							System.out.println("save pred means: error");
							String checkError = "msg <- trimStrings(strsplit(saveDataB1, \":\")[[1]])";
							String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
							String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
							String checkError4 = "capture.output(cat(\"*** \nERROR in saving predicted means to a file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(checkError);
							rConnection.eval(checkError2);
							rConnection.eval(checkError3);
							rConnection.eval(checkError4);
						}
					}

					//optional output: diagnostic plots for genotype random
					if (diagnosticPlot) {
						String diagPlotsFunc = null;
						if (environment == "NULL") {
							diagPlotsFunc = "diagPlots <- try(graph.sea.diagplots(data, " + respvarVector + ", env = " + environment + ", is.random = TRUE, ssa2), silent=TRUE)";
						} else {
							diagPlotsFunc = "diagPlots <- try(graph.sea.diagplots(data, " + respvarVector + ", env = \"" + environment + "\", is.random = TRUE, ssa2), silent=TRUE)";
						}
						System.out.println(diagPlotsFunc);
						rConnection.eval(diagPlotsFunc);

						String runSuccessDiagPlots = rConnection.eval("class(diagPlots)").toString();
						if (runSuccessDiagPlots != null && runSuccessDiagPlots.equals("try-error")) {	
							System.out.println("diagnostic plots (genotype random): error");
							String checkError = "msg <- trimStrings(strsplit(diagPlots, \":\")[[1]])";
							String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
							String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
							String checkError4 = "capture.output(cat(\"*** \nERROR in graph.sea.diagplots function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
							rConnection.eval(checkError);
							rConnection.eval(checkError2);
							rConnection.eval(checkError3);
							rConnection.eval(checkError4);
						}
					}

				} // end of else for if (runSuccess == "notRun") 
			} // end of if random

			//default output: save residuals to a file
			if (runningFixedSuccess & runningRandomSuccess) {
				String residFileNameFixed = "residFileNameFixed <- paste(\"" + resultFolderPath + "\",\"residuals_fixed.csv\", sep=\"\")";
				String residFileNameRandom = "residFileNameRandom <- paste(\"" + resultFolderPath + "\",\"residuals_random.csv\", sep=\"\")";
				if ((genotypeFixed) & (genotypeRandom == false)) {
					String runSsaResid1 = null;
					if (environment == "NULL") {
						runSsaResid1 = "resid_f <- try(ssa.resid(data, ssa1, " + respvarVector + ", env = " + environment + ", is.genoRandom = FALSE), silent=TRUE)";
					} else {
						runSsaResid1 = "resid_f <- try(ssa.resid(data, ssa1, " + respvarVector + ", env = \"" + environment + "\", is.genoRandom = FALSE), silent=TRUE)";
					}
					System.out.println(runSsaResid1);
					rConnection.eval(runSsaResid1);

					String runSuccessDiagPlots = rConnection.eval("class(resid_f)").toString();
					if (runSuccessDiagPlots != null && runSuccessDiagPlots.equals("try-error")) {	
						System.out.println("ssa.resid (genotype fixed): error");
						String checkError = "msg <- trimStrings(strsplit(resid_f, \":\")[[1]])";
						String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
						String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
						String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.resid function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
						rConnection.eval(checkError);
						rConnection.eval(checkError2);
						rConnection.eval(checkError3);
						rConnection.eval(checkError4);
					} else {
						String checkResid1 = rConnection.eval("resid_f$residWarning").toString();
						System.out.println("checkResid1: " + checkResid1);
						if (checkResid1.equals("empty")) {
							System.out.println("Saving resid (fixed) not done.");
						} else {
							String func1SaveResidualsCsv = "saveResid <- try(write.table(resid_f$residuals, file = residFileNameFixed ,sep=\",\",row.names=FALSE), silent=TRUE)";
							rConnection.eval(residFileNameFixed);
							rConnection.eval(func1SaveResidualsCsv);

							String runSuccessSaveResid = rConnection.eval("class(saveResid)").toString();
							if (runSuccessSaveResid != null && runSuccessSaveResid.equals("try-error")) {	
								System.out.println("save residuals: error");
								String checkError = "msg <- trimStrings(strsplit(saveResid, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in saving residuals to a file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							}

							//generate heatmap
							if (heatmapResiduals) {
								String funcHeat=null;
								if (environment == "NULL") {
									funcHeat = "heat1 <- try(Heatmap(resid_f$residuals, genAs=\"fixed\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", " + environment + "), silent=TRUE)";
								} else {
									funcHeat = "heat1 <- try(Heatmap(resid_f$residuals, genAs=\"fixed\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", \"" + environment + "\"), silent=TRUE)";
								}
								System.out.println(funcHeat);
								rConnection.eval(funcHeat);

								String runSuccessHeat = rConnection.eval("class(heat1)").toString();
								if (runSuccessHeat != null && runSuccessHeat.equals("try-error")) {	
									System.out.println("heatmap (fixed): error");
									String checkError = "msg <- trimStrings(strsplit(heat1, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in Heatmap function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								} else {
									for (int k = 0; k < respvars.length; k++) {
										int i = k + 1; // 1-relative index;

										String envLevelsCommand = "length(heat1[[" + i + "]]$site)";
										int envLevels = rConnection.eval(envLevelsCommand).asInteger();
										for (int m = 0; m < envLevels; m++) { 
											int j = m + 1; // 1-relative index;

											String warningListCommand = "heat1[[" + i + "]]$site[["+ j + "]]";
											String warningList = rConnection.eval(warningListCommand).toString();

											if (warningList.equals("empty")) {

											} else if (warningList.equals("unique")) {

											} else {
												String trialObsUsed = "capture.output(cat(\"\nERROR:\", heat1[[" + i + "]]$site[["+ j + "]],\"\n\"),file=\""	+ outFileName + "\",append = TRUE)";
												rConnection.eval(trialObsUsed);
											}
										}
									}
								} //end (heat1 is not error)
							}
						}
					}
				}
				else if ((genotypeFixed == false) & (genotypeRandom)) {
					String runSsaResid2 = null;
					if (environment == "NULL") {
						runSsaResid2 = "resid_r <- try(ssa.resid(data, ssa2, " + respvarVector + ", env = " + environment + ", is.genoRandom = TRUE), silent=TRUE)";
					} else {
						runSsaResid2 = "resid_r <- try(ssa.resid(data, ssa2, " + respvarVector + ", env = \"" + environment + "\", is.genoRandom = TRUE), silent=TRUE)";
					}
					System.out.println(runSsaResid2);
					rConnection.eval(runSsaResid2);

					String runSuccessDiagPlots = rConnection.eval("class(resid_r)").toString();
					if (runSuccessDiagPlots != null && runSuccessDiagPlots.equals("try-error")) {	
						System.out.println("ssa.resid (genotype random): error");
						String checkError = "msg <- trimStrings(strsplit(resid_r, \":\")[[1]])";
						String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
						String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
						String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.resid function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
						rConnection.eval(checkError);
						rConnection.eval(checkError2);
						rConnection.eval(checkError3);
						rConnection.eval(checkError4);
					} else {
						String checkResid1 = rConnection.eval("resid_r$residWarning").toString();
						System.out.println("checkResid2: " + checkResid1);
						if (checkResid1.equals("empty")) {
							System.out.println("Saving resid (random) not done.");
						} else {
							String func1SaveResidualsCsv = "saveResid <- try(write.table(resid_r$residuals, file = residFileNameRandom ,sep=\",\",row.names=FALSE), silent=TRUE)";
							rConnection.eval(residFileNameRandom);
							rConnection.eval(func1SaveResidualsCsv);

							String runSuccessSaveResid = rConnection.eval("class(saveResid)").toString();
							if (runSuccessSaveResid != null && runSuccessSaveResid.equals("try-error")) {	
								System.out.println("save residuals: error");
								String checkError = "msg <- trimStrings(strsplit(saveResid, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in saving residuals to a file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							}

							//generate heatmap
							if (heatmapResiduals) {
								String funcHeat=null;
								if (environment == "NULL") {
									funcHeat = "heat2 <- try(Heatmap(resid_r$residuals, genAs=\"random\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", " + environment + "), silent=TRUE)";
								} else {
									funcHeat = "heat2 <- try(Heatmap(resid_r$residuals, genAs=\"random\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", \"" + environment + "\"), silent=TRUE)";
								}
								System.out.println(funcHeat);
								rConnection.eval(funcHeat);

								String runSuccessHeat = rConnection.eval("class(heat2)").toString();
								if (runSuccessHeat != null && runSuccessHeat.equals("try-error")) {	
									System.out.println("heatmap (random): error");
									String checkError = "msg <- trimStrings(strsplit(heat2, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in Heatmap function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								} else {
									for (int k = 0; k < respvars.length; k++) {
										int i = k + 1; // 1-relative index;

										String envLevelsCommand = "length(heat2[[" + i + "]]$site)";
										int envLevels = rConnection.eval(envLevelsCommand).asInteger();
										for (int m = 0; m < envLevels; m++) { 
											int j = m + 1; // 1-relative index;

											String warningListCommand = "heat2[[" + i + "]]$site[["+ j + "]]";
											String warningList = rConnection.eval(warningListCommand).toString();

											if (warningList.equals("empty")) {

											} else if (warningList.equals("unique")) {

											} else {
												String trialObsUsed = "capture.output(cat(\"\nERROR:\", heat2[[" + i + "]]$site[["+ j + "]],\"\n\"),file=\""	+ outFileName + "\",append = TRUE)";
												rConnection.eval(trialObsUsed);
											}
										}
									}
								}
							}
						}
					}
				}
				else if ((genotypeFixed) & (genotypeRandom)) {
					String runSsaResid1 = null;
					String runSsaResid2 = null;
					if (environment == "NULL") {
						runSsaResid1 = "resid_f <- try(ssa.resid(data, ssa1, " + respvarVector + ", env = " + environment + ", is.genoRandom = FALSE), silent=TRUE)";
						runSsaResid2 = "resid_r <- try(ssa.resid(data, ssa2, " + respvarVector + ", env = " + environment + ", is.genoRandom = TRUE), silent=TRUE)";
					} else {
						runSsaResid1 = "resid_f <- try(ssa.resid(data, ssa1, " + respvarVector + ", env = \"" + environment + "\", is.genoRandom = FALSE), silent=TRUE)";
						runSsaResid2 = "resid_r <- try(ssa.resid(data, ssa2, " + respvarVector + ", env = \"" + environment + "\", is.genoRandom = TRUE), silent=TRUE)";
					}
					System.out.println(runSsaResid1);
					System.out.println(runSsaResid2);
					rConnection.eval(runSsaResid1);
					rConnection.eval(runSsaResid2);

					String runSuccessResidFixed = rConnection.eval("class(resid_f)").toString();
					if (runSuccessResidFixed != null && runSuccessResidFixed.equals("try-error")) {	
						System.out.println("ssa.resid (genotype fixed): error");
						String checkError = "msg <- trimStrings(strsplit(resid_f, \":\")[[1]])";
						String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
						String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
						String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.resid function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
						rConnection.eval(checkError);
						rConnection.eval(checkError2);
						rConnection.eval(checkError3);
						rConnection.eval(checkError4);
					} else {
						String checkResid1 = rConnection.eval("resid_f$residWarning").toString();
						System.out.println("checkResid1: " + checkResid1);
						if (checkResid1.equals("empty")) {
							System.out.println("Saving resid (fixed) not done.");
						} else {
							String func1SaveResidualsCsv = "saveResid <- try(write.table(resid_f$residuals, file = residFileNameFixed ,sep=\",\",row.names=FALSE), silent=TRUE)";
							rConnection.eval(residFileNameFixed);
							rConnection.eval(func1SaveResidualsCsv);

							String runSuccessSaveResid = rConnection.eval("class(saveResid)").toString();
							if (runSuccessSaveResid != null && runSuccessSaveResid.equals("try-error")) {	
								System.out.println("save residuals: error");
								String checkError = "msg <- trimStrings(strsplit(saveResid, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in saving residuals to a file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							}

							//generate heatmap
							if (heatmapResiduals) {
								String funcHeat=null;
								if (environment == "NULL") {
									funcHeat = "heat1 <- try(Heatmap(resid_f$residuals, genAs=\"fixed\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", " + environment + "), silent=TRUE)";
								} else {
									funcHeat = "heat1 <- try(Heatmap(resid_f$residuals, genAs=\"fixed\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", \"" + environment + "\"), silent=TRUE)";
								}
								System.out.println(funcHeat);
								rConnection.eval(funcHeat);

								String runSuccessHeat = rConnection.eval("class(heat1)").toString();
								if (runSuccessHeat != null && runSuccessHeat.equals("try-error")) {	
									System.out.println("heatmap (fixed): error");
									String checkError = "msg <- trimStrings(strsplit(heat1, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in Heatmap function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								} else {
									for (int k = 0; k < respvars.length; k++) {
										int i = k + 1; // 1-relative index;

										String envLevelsCommand = "length(heat1[[" + i + "]]$site)";
										int envLevels = rConnection.eval(envLevelsCommand).asInteger();
										for (int m = 0; m < envLevels; m++) { 
											int j = m + 1; // 1-relative index;

											String warningListCommand = "heat1[[" + i + "]]$site[["+ j + "]]";
											String warningList = rConnection.eval(warningListCommand).toString();

											if (warningList.equals("empty")) {

											} else if (warningList.equals("unique")) {

											} else {
												String trialObsUsed = "capture.output(cat(\"\nERROR:\", heat1[[" + i + "]]$site[["+ j + "]],\"\n\"),file=\""	+ outFileName + "\",append = TRUE)";
												rConnection.eval(trialObsUsed);
											}
										}
									}
								}
							}
						}
					}

					String runSuccessResidRandom = rConnection.eval("class(resid_r)").toString();
					if (runSuccessResidRandom != null && runSuccessResidRandom.equals("try-error")) {	
						System.out.println("ssa.resid (genotype random): error");
						String checkError = "msg <- trimStrings(strsplit(resid_r, \":\")[[1]])";
						String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
						String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
						String checkError4 = "capture.output(cat(\"*** \nERROR in ssa.resid function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";

						rConnection.eval(checkError);
						rConnection.eval(checkError2);
						rConnection.eval(checkError3);
						rConnection.eval(checkError4);
					} else {
						String checkResid1 = rConnection.eval("resid_r$residWarning").toString();
						System.out.println("checkResid2: " + checkResid1);
						if (checkResid1.equals("empty")) {
							System.out.println("Saving resid (random) not done.");
						} else {
							String func1SaveResidualsCsv = "saveResid2 <- try(write.table(resid_r$residuals, file = residFileNameRandom ,sep=\",\",row.names=FALSE), silent=TRUE)";
							rConnection.eval(residFileNameRandom);
							rConnection.eval(func1SaveResidualsCsv);

							String runSuccessSaveResid = rConnection.eval("class(saveResid2)").toString();
							if (runSuccessSaveResid != null && runSuccessSaveResid.equals("try-error")) {	
								System.out.println("save residuals: error");
								String checkError = "msg <- trimStrings(strsplit(saveResid2, \":\")[[1]])";
								String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
								String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
								String checkError4 = "capture.output(cat(\"*** \nERROR in saving residuals to a file:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
								rConnection.eval(checkError);
								rConnection.eval(checkError2);
								rConnection.eval(checkError3);
								rConnection.eval(checkError4);
							}

							if (heatmapResiduals) {
								String funcHeat=null;
								if (environment == "NULL") {
									funcHeat = "heat2 <- try(Heatmap(resid_r$residuals, genAs=\"random\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", " + environment + "), silent=TRUE)";
								} else {
									funcHeat = "heat2 <- try(Heatmap(resid_r$residuals, genAs=\"random\", \"" + heatmapRow + "\", \"" + heatmapColumn + "\", " + respvarVector + ", \"" + designUsed + "\", \"" + environment + "\"), silent=TRUE)";
								}
								System.out.println(funcHeat);
								rConnection.eval(funcHeat);

								String runSuccessHeat = rConnection.eval("class(heat2)").toString();
								if (runSuccessHeat != null && runSuccessHeat.equals("try-error")) {	
									System.out.println("heatmap (random): error");
									String checkError = "msg <- trimStrings(strsplit(heat2, \":\")[[1]])";
									String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
									String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
									String checkError4 = "capture.output(cat(\"*** \nERROR in Heatmap function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
									rConnection.eval(checkError);
									rConnection.eval(checkError2);
									rConnection.eval(checkError3);
									rConnection.eval(checkError4);
								} else {
									for (int k = 0; k < respvars.length; k++) {
										int i = k + 1; // 1-relative index;

										String envLevelsCommand = "length(heat2[[" + i + "]]$site)";
										int envLevels = rConnection.eval(envLevelsCommand).asInteger();
										for (int m = 0; m < envLevels; m++) { 
											int j = m + 1; // 1-relative index;

											String warningListCommand = "heat2[[" + i + "]]$site[["+ j + "]]";
											String warningList = rConnection.eval(warningListCommand).toString();

											if (warningList.equals("empty")) {

											} else if (warningList.equals("unique")) {

											} else {
												String trialObsUsed = "capture.output(cat(\"\nERROR:\", heat2[[" + i + "]]$site[["+ j + "]],\"\n\"),file=\""	+ outFileName + "\",append = TRUE)";
												rConnection.eval(trialObsUsed);
											}
										}
									}
								}
							}
						}
					}
				}
			}

			//optional output: boxplot and histogram
			String withBox = "FALSE";
			if (boxplotRawData) withBox = "TRUE";
			String withHist = "FALSE";
			if (histogramRawData) withHist = "TRUE";
			String ssaOut = "ssa1";
			if (genotypeFixed) ssaOut = "ssa1";
			else if (genotypeRandom) ssaOut = "ssa2";

			String boxHistFunc = null;
			if (environment =="NULL") {
				boxHistFunc = "boxHist <- try(graph.sea.boxhist(data, " + respvarVector + ", env = " + environment + ", " + ssaOut + ", box = \"" + withBox + "\", hist = \"" + withHist + "\"), silent=TRUE)";
			} else {
				boxHistFunc = "boxHist <- try(graph.sea.boxhist(data, " + respvarVector + ", env = \"" + environment + "\", " + ssaOut + ", box = \"" + withBox + "\", hist = \"" + withHist + "\"), silent=TRUE)";
			}
			System.out.println(boxHistFunc);
			rConnection.eval(boxHistFunc);

			String runSuccessBoxHist = rConnection.eval("class(boxHist)").toString();
			if (runSuccessBoxHist != null && runSuccessBoxHist.equals("try-error")) {	
				System.out.println("boxplot/histogram: error");
				String checkError = "msg <- trimStrings(strsplit(boxHist, \":\")[[1]])";
				String checkError2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String checkError3 ="msg <- gsub(\"\\\"\", \"\", msg)";
				String checkError4 = "capture.output(cat(\"*** \nERROR in graph.sea.boxhist function:\\n  \",msg, \"\n***\n\n\", sep = \"\"), file=\"" + outFileName + "\",append = TRUE)";
				rConnection.eval(checkError);
				rConnection.eval(checkError2);
				rConnection.eval(checkError3);
				rConnection.eval(checkError4);
			}
			rConnection.eval(outspace);
			rConnection.eval(sep2);
			//			

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
	}

	/*This methods execute R to run SingleTrial Analysis in PRep Design*/
	public void doSingleEnvironmentAnalysisPRep(SingleTrialParametersModel ssaModel){
		String resultFolderPath = ssaModel.getResultFolderName().replace(BSLASH, FSLASH);
		String outFileName = ssaModel.getOutFileName().replace(BSLASH, FSLASH);
		String dataFileName = ssaModel.getDataFileName().replace(BSLASH, FSLASH);
		String[] respvar = ssaModel.getRespvars();
		String genotype = ssaModel.getGenotype();
		String row = ssaModel.getRow();
		String column = ssaModel.getColumn();
		String environment = ssaModel.getEnvironment();
		boolean genotypeFixed = ssaModel.isGenotypeFixed();
		boolean genotypeRandom = ssaModel.isGenotypeRandom();
		boolean excludeControls = ssaModel.isExcludeControls();
		String[] controlLevel = ssaModel.getControlLevels(); // c("CIHERANG","CIHERANGSUB1","IRRI105","IRRI119", "IRRI154","IRRI168") 
		boolean moransTest = ssaModel.isMoransTest();// for BIMS always false
		String[] spatialStruc = ssaModel.getSpatialStruc(); // {"none", "CompSymm", "Gaus", "Exp", "Spher"}, for BIMS include the five choices, for standalone determine by the user
		boolean descriptiveStat =ssaModel.isDescriptiveStat();
		boolean varianceComponents = ssaModel.isVarianceComponents();
		boolean boxplotRawData = ssaModel.isBoxplotRawData();
		boolean histogramRawData = ssaModel.isHistogramRawData();
		boolean heatmapResiduals = ssaModel.isHeatmapResiduals();
		boolean diagnosticPlot = ssaModel.isDiagnosticPlot();

		try{
			//Single-Site Analysis for p-rep design
			String readData = "dataRead <- read.csv(\"" + dataFileName + "\", header = TRUE, na.strings = c(\"NA\",\".\", \"\", \" \"), blank.lines.skip=TRUE, sep = \",\")";
			String sinkIn = "sink(\"" + resultFolderPath + "SEA_output.txt\")";
			String usedData = "cat(\"\\nDATA FILE: " + dataFileName + "\\n\")";
			String analysisDone = "cat(\"\\nSINGLE-ENVIRONMENT ANALYSIS\\n\")";
			String usedDesign = "cat(\"\\nDESIGN: p-rep Design\\n\\n\")";

			String command1 = "ssaTestPrep(data = dataRead, respvar = "+ inputTransform.createRVector(respvar) + ", geno = \""+ genotype + "\"";
			command1 = command1 + ", row = \"" + row + "\", column = \"" + column + "\"";
			if (environment != null) {
				command1 = command1 + ", env = \"" + environment +"\"";
			} else { 
				command1 = command1 + ", env = " + String.valueOf(environment).toUpperCase();
			}

			String command2;
			if (controlLevel != null) {
				command2 = ", checkList = " + inputTransform.createRVector(controlLevel);	
			} else { 
				command2 = ", checkList = " + String.valueOf(controlLevel).toUpperCase();
			}
			command2 = command2 + ", moransTest = " + String.valueOf(moransTest).toUpperCase();
			command2 = command2 + ", spatialStruc = "+ inputTransform.createRVector(spatialStruc);
			command2 = command2 + ", descriptive = "+ String.valueOf(descriptiveStat).toUpperCase();
			command2 = command2 + ", varCorr = "+ String.valueOf(varianceComponents).toUpperCase();
			command2 = command2 + ", heatmap = "+ String.valueOf(heatmapResiduals).toUpperCase();
			command2 = command2 + ", diagplot = "+ String.valueOf(diagnosticPlot).toUpperCase();
			command2 = command2 + ", histogram = "+ String.valueOf(histogramRawData).toUpperCase();
			command2 = command2 + ", boxplot = "+ String.valueOf(boxplotRawData).toUpperCase();
			command2 = command2 + ", outputPath = \"" + resultFolderPath + "\")";

			String funcSSAPRepFixed = "resultFixed <- try(" + command1 ;
			if (genotypeFixed) {
				funcSSAPRepFixed = funcSSAPRepFixed + ", is.random = FALSE";
				funcSSAPRepFixed = funcSSAPRepFixed + command2 + ", silent = TRUE)";
			}

			String funcSSAPRepRandom = "resultRandom <- try(" + command1;
			if (genotypeRandom) {
				funcSSAPRepRandom = funcSSAPRepRandom + ", is.random = " + String.valueOf(genotypeRandom).toUpperCase();
				funcSSAPRepRandom = funcSSAPRepRandom + ", excludeCheck = " + String.valueOf(excludeControls).toUpperCase();
				funcSSAPRepRandom = funcSSAPRepRandom + command2 + ", silent = TRUE)";
			} 

			System.out.println(readData);
			System.out.println(sinkIn);
			System.out.println(usedData);
			System.out.println(analysisDone);
			System.out.println(usedDesign);

			rConnection.eval(readData);
			rConnection.eval(sinkIn);
			rConnection.eval(usedData);
			rConnection.eval(analysisDone);
			rConnection.eval(usedDesign);

			if (genotypeFixed) {
				System.out.println(funcSSAPRepFixed);
				rConnection.eval(funcSSAPRepFixed);

				String runSuccessCommand = rConnection.eval("class(resultFixed)").asString();
				if (runSuccessCommand.equals("try-error")) {
					String errorMsg1 = "msg <- trimStrings(strsplit(resultFixed, \":\")[[1]])";
					String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
					String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
					String errorMsg4 = "cat(\"Error in SSATestPrep:\\n\",msg, sep = \"\")";

					System.out.println(errorMsg1);
					System.out.println(errorMsg2);
					System.out.println(errorMsg3);
					System.out.println(errorMsg4);

					rConnection.eval(errorMsg1);
					rConnection.eval(errorMsg2);
					rConnection.eval(errorMsg3);
					rConnection.eval(errorMsg4);
				} 
				else{
					String funcResidFixed = "residFixed <- ssaTestPrepResid(resultFixed)";
					String funcResidFixedWrite = "if (nrow(residFixed) > 0) { \n";
					funcResidFixedWrite = funcResidFixedWrite + "  write.csv(residFixed, file = \"" + resultFolderPath + "residuals_fixed.csv\", row.names = FALSE) \n";
					funcResidFixedWrite = funcResidFixedWrite + "} \n";

					String funcSummaryFixed = "summaryFixed <- ssaTestPrepSummary(resultFixed)";
					String funcSummaryFixedWrite = "if (nrow(summaryFixed) > 0) { \n";
					funcSummaryFixedWrite = funcSummaryFixedWrite + "  write.csv(summaryFixed, file = \"" + resultFolderPath + "summaryStats.csv\", row.names = FALSE) \n";
					funcSummaryFixedWrite = funcSummaryFixedWrite + "} \n";

					System.out.println(funcResidFixed);
					rConnection.eval(funcResidFixed);

					System.out.println(funcResidFixedWrite);
					rConnection.eval(funcResidFixedWrite);

					System.out.println(funcSummaryFixed);
					rConnection.eval(funcSummaryFixed);

					System.out.println(funcSummaryFixedWrite);
					rConnection.eval(funcSummaryFixedWrite);
				}
			}

			if (genotypeRandom) {
				System.out.println(funcSSAPRepRandom);
				rConnection.eval(funcSSAPRepRandom);

				String runSuccessCommand = rConnection.eval("class(resultFixed)").asString();
				if (runSuccessCommand.equals("try-error")) {
					String errorMsg1 = "msg <- trimStrings(strsplit(resultFixed, \":\")[[1]])";
					String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
					String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
					String errorMsg4 = "cat(\"Error in SSATestPrep:\\n\",msg, sep = \"\")";

					System.out.println(errorMsg1);
					System.out.println(errorMsg2);
					System.out.println(errorMsg3);
					System.out.println(errorMsg4);

					rConnection.eval(errorMsg1);
					rConnection.eval(errorMsg2);
					rConnection.eval(errorMsg3);
					rConnection.eval(errorMsg4);
				} 
				else{
					String funcResidRandom = "residRandom <- ssaTestPrepResid(resultRandom)";
					String funcResidRandomWrite = "if (nrow(residRandom) > 0) { \n";
					funcResidRandomWrite = funcResidRandomWrite + "  write.csv(residRandom, file = \"" + resultFolderPath + "residuals_random.csv\", row.names = FALSE) \n";
					funcResidRandomWrite = funcResidRandomWrite + "} \n";

					String funcSummaryRandom = "summaryRandom <- ssaTestPrepSummary(resultRandom)";
					String funcSummaryRandomWrite = "if (nrow(summaryRandom) > 0) { \n";
					funcSummaryRandomWrite = funcSummaryRandomWrite + "  write.csv(summaryRandom, file = \"" + resultFolderPath + "predictedMeans.csv\", row.names = FALSE) \n";
					funcSummaryRandomWrite = funcSummaryRandomWrite + "} \n";

					System.out.println(funcResidRandom);
					rConnection.eval(funcResidRandom);

					System.out.println(funcResidRandomWrite);
					rConnection.eval(funcResidRandomWrite);

					System.out.println(funcSummaryRandom);
					rConnection.eval(funcSummaryRandom);

					System.out.println(funcSummaryRandomWrite);
					rConnection.eval(funcSummaryRandomWrite);
				}
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);			


		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
	}





	/* This method get the level in the dataset
	 * @param columnList - List of column in the dataset
	 * @param dataList - list of data
	 * 
	 */
	public String[] getLevels(List<String> columnList, List<String[]> dataList,
			String environment) {
		// TODO Auto-generated method stub
		int envtColumn = 0;
		for (int i = 0; i < columnList.size(); i++) {
			if (columnList.get(i).equals(environment)) {
				envtColumn = i;
			}
		}

		ArrayList<String> envts = new ArrayList<String>();
		for (int j = 0; j <dataList.size(); j++) {
			String level = dataList.get(j)[envtColumn];
			if (!envts.contains(level)&& !level.isEmpty()) {
				envts.add(level);
			}
		}

		String[] envtLevels = new String[envts.size()];
		for (int k = 0; k < envts.size(); k++) {
			envtLevels[k] = (String) envts.get(k);
		}

		return envtLevels;
	}


	/* This method call R to run outlierdection */
	public void doOutlierDetection(OutlierParametersModel param ) {
		String dataFileName=param.getDataFileName();
		String outputPath=param.getResultFolderName();
		String respvar=param.getRespvar();
		String trmt=param.getGenotype();
		String replicate=param.getRep();



		try {
			String readData = "dataRead <- read.csv(\"" + dataFileName.replace(BSLASH, FSLASH) + "\", header = TRUE, na.strings = c(\"NA\",\".\", \"\", \" \"), blank.lines.skip=TRUE, sep = \",\")";
			String funcStmt = "result <- try(";
			//			String command = "OutlierDetection(data = \"dataRead\", var = "+ inputTransform.createRVector(respvar);
			String command = "OutlierDetection(data = \"dataRead\", var = \""+ respvar + "\"";
			if (trmt != null) {
				command = command + ", grp = \""+ trmt + "\"";
			}
			if (replicate != null) {
				command = command + ", rep = \""+ replicate + "\"";
			}

			command = command + ", path = \""+ outputPath.replace(BSLASH, FSLASH) + "\", method = \"method2\")";
			funcStmt = funcStmt + command + ", silent = TRUE)";
			String saveData = "write.csv(result[[1]]$outlier, file = \""+ outputPath.replace(BSLASH, FSLASH) +"Outlier.csv\", row.names = FALSE)";

			System.out.println(readData);
			System.out.println(funcStmt);
			System.out.println(saveData);

			rConnection.eval(readData);
			rConnection.eval(funcStmt);
			rConnection.eval(saveData);


		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
	}


	/*This methods execute R to run Alpha Lattice*/
	public void doDesignAlpha(String path, String fieldBookName, Integer numTrmt, Integer blkSize, 
			Integer rep, Integer trial, Integer rowPerBlk, Integer rowPerRep, Integer numFieldRow, String fieldOrder){
		try{
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			String LayoutOutput = path + fieldBookName + ".txt";

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designAlphaLattice(list(EntryNo = c(1:"+ numTrmt +"))";
			command = command + ", blksize = "+ blkSize +", r = "+ rep +", trial = "+ trial;
			command = command + ", rowPerBlk = " + rowPerBlk +", rowPerRep = "+ rowPerRep +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE, file = \""+ CSVOutput +"\")";
			} else {
				command = command + ", serpentine = TRUE, file = \""+ CSVOutput +"\")";
			}
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			//save sorted to csv file
			//String sortFile = "write.csv(result$fieldbook, file = \""+ CSVOutput +"\", row.names = FALSE)";
			//System.out.println(sortFile);
			//rEngine.eval(sortFile);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designAlphaLattice:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {
				//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
				//			checkOutput = checkOutput + "    cat(\"\\nLayout for Alpha Lattice Design:\",\"\\n\\n\")\n";
				//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
				//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
				//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
				//			checkOutput = checkOutput + "    }\n";
				//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
				//			checkOutput = checkOutput + "}";

				Integer colPerBlk = blkSize/rowPerBlk;
				Integer colPerRep = numTrmt/rowPerRep;

				String checkOutput = "for (i in (1:length(result$layout))) {\n";
				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerBlk+", "+ colPerBlk +"), bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 7, new = FALSE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerRep+", "+ colPerRep +"), bwd = 4)\n";
				checkOutput = checkOutput + "     dev.off() \n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}


	/*This methods execute R to run AugmentedAlpha*/
	public void doDesignAugmentedAlpha(String path, String fieldBookName, Integer numCheck, Integer numNew, 
			String trmtName, Integer blkSize, Integer rep, Integer trial, Integer rowPerBlk, Integer rowPerRep, 
			Integer numFieldRow, String fieldOrder, String trmtLabel, String[] checkTrmt, String[] newTrmt){
		try{
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOuptut = path + fieldBookName + ".txt";

			String sinkIn = "sink(\"" + TxtOuptut + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designAugmentedAlpha(numCheck = "+ numCheck + ", numNew = "+ numNew;
			if (trmtName == null) {
				command = command + ", trmtName = NULL";
			} else {
				command = command + ", trmtName = \""+ trmtName +"\"";
			}
			command = command + ", blksize = "+ blkSize +", r = "+ rep +", trial = "+ trial;
			command = command + ", rowPerBlk = " + rowPerBlk +", rowPerRep = "+ rowPerRep +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			if (trmtLabel == null) {
				command = command + ", trmtLabel = NULL";
			} else {
				command = command + ", trmtLabel = \""+ trmtLabel +"\"";
			}
			if (checkTrmt == null) {
				command = command + ", checkTrmt = NULL";
			} else {
				command = command + ", checkTrmt = "+ inputTransform.createRVector(checkTrmt);
			}
			if (newTrmt == null) {
				command = command + ", newTrmt = NULL";
			} else {
				command = command + ", newtrmt = "+ inputTransform.createRVector(newTrmt);
			}
			command = command + ", file = \""+ CSVOutput +"\")";
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			//save sorted to csv file
			//String sortFile = "write.csv(result$fieldbook, file = \""+ CSVOutput +"\", row.names = FALSE)";
			//System.out.println(sortFile);
			//rEngine.eval(sortFile);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designAlphaLattice:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			//		else {
			//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
			//			checkOutput = checkOutput + "    cat(\"\\nLayout for Alpha Lattice Design:\",\"\\n\\n\")\n";
			//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
			//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
			//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
			//			checkOutput = checkOutput + "    }\n";
			//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
			//			checkOutput = checkOutput + "}";
			//	
			//			System.out.println(checkOutput);
			//			rEngine.eval(checkOutput);
			//		}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}


	public void doDesignAugmentedRowColumn(String path, String fieldBookName, Integer numCheck, Integer numNew,
			String trmtName, Integer rep, Integer trial, Integer rowblkPerRep, Integer rowPerRowblk, 
			Integer numFieldRow, String fieldOrder, String trmtLabel, String[] checkTrmt, String[] newTrmt){

		try{
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOuptut = path + fieldBookName + ".txt";

			String sinkIn = "sink(\"" + TxtOuptut + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designAugmentedRowColumn(numCheck = "+ numCheck +", numNew = "+ numNew;
			command = command + ", trmtName = \""+ trmtName +"\", r = "+ rep +", trial = "+ trial;
			command = command + ", rowblkPerRep = "+ rowblkPerRep +", rowPerRowblk = "+ rowPerRowblk +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			if (trmtLabel == null) {
				command = command + ", trmtLabel = NULL";
			} else {
				command = command + ", trmtLabel = \""+ trmtLabel +"\"";
			}
			if (checkTrmt == null) {
				command = command + ", checkTrmt = NULL";
			} else {
				command = command + ", checkTrmt = "+ inputTransform.createRVector(checkTrmt);
			}
			if (newTrmt == null) {
				command = command + ", newTrmt = NULL";
			} else {
				command = command + ", newtrmt = "+ inputTransform.createRVector(newTrmt);
			}
			command = command + ", file = \""+ CSVOutput +"\")";
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designRowColumn:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			//		else {
			//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
			//			checkOutput = checkOutput + "    cat(\"\\nLayout for Row-Column Design:\",\"\\n\\n\")\n";
			//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
			//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
			//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
			//			checkOutput = checkOutput + "    }\n";
			//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
			//			checkOutput = checkOutput + "}";
			//	
			//			System.out.println(checkOutput);
			//			rEngine.eval(checkOutput);
			//		}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}

	public void doDesignLatinizedAlpha(String path, String fieldBookName, Integer numTrmt, Integer blkSize, 
			Integer rep, Integer trial, Integer numFieldRow, String fieldOrder){

		try{
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			String LayoutOutput = path + fieldBookName;

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designLatinizedAlpha(generate = list(EntryNo = c(1:"+ numTrmt +"))";
			command = command + ", blksize = "+ blkSize +", r = "+ rep +", trial = "+ trial +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE, file = \""+ CSVOutput +"\")";
			} else {
				command = command + ", serpentine = TRUE, file = \""+ CSVOutput +"\")";
			}
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);


			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designAlphaLattice:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {
				//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
				//			checkOutput = checkOutput + "    cat(\"\\nLayout for Latinized Alpha Lattice Design:\",\"\\n\\n\")\n";
				//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
				//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
				//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
				//			checkOutput = checkOutput + "    }\n";
				//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
				//			checkOutput = checkOutput + "}";

				Integer numBlk = numTrmt/blkSize;
				Integer rowPerBlk;
				Integer colPerBlk;
				Integer rowPerRep;
				Integer colPerRep;
				if (numFieldRow == numBlk) {
					rowPerBlk = 1;
					colPerBlk = blkSize;
					rowPerRep = numBlk;
					colPerRep = blkSize;
				} else {
					rowPerBlk = blkSize;
					colPerBlk = 1;
					rowPerRep = blkSize;
					colPerRep = numBlk;
				}

				String checkOutput = "for (i in (1:length(result$layout))) {\n";
				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerBlk+", "+ colPerBlk +"), bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 7, new = FALSE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerRep+", "+ colPerRep +"), bwd = 4)\n";
				checkOutput = checkOutput + "     dev.off() \n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");
			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}

	public void doDesignLatinizedRowColumn(String path, String fieldBookName, Integer numTrmt, Integer rep, Integer trial, 
			Integer rowPerRep, Integer numFieldRow, String fieldOrder){

		try{
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			String LayoutOutput = path + fieldBookName;

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designLatinizedRowCol(list(EntryNo = c(1:"+ numTrmt +"))";
			command = command + ", r = "+ rep +", trial = "+ trial;
			command = command + ", rowPerRep = "+ rowPerRep +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE, file = \""+ CSVOutput +"\")";
			} else {
				command = command + ", serpentine = TRUE, file = \""+ CSVOutput +"\")";
			}
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designLatinizedRowCol:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {
				//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
				//			checkOutput = checkOutput + "    cat(\"\\nLayout for Latinized Row-Column Design:\",\"\\n\\n\")\n";
				//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
				//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
				//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
				//			checkOutput = checkOutput + "    }\n";
				//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
				//			checkOutput = checkOutput + "}";

				Integer colPerRep = numTrmt/rowPerRep;

				String checkOutput = "for (i in (1:length(result$layout))) {\n";
				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerRep+", "+ colPerRep +"), bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
				checkOutput = checkOutput + "     dev.off() \n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}


	public void doDesignPRep(String path, String fieldBookName, String[] trmtGrpName, Integer[] numTrmtPerGrp, 
			Integer[] trmtRepPerGrp, String trmtName, Integer blk, Integer trial, Integer rowPerBlk, Integer numFieldRow, 
			String fieldOrder, String trmtLabel, String[] trmtListPerGrp){

		try{
			//defining the R statements for randomization for Alpha Lattice
			//defining the R statements for randomization for Alpha Lattice
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOuptut = path + fieldBookName + ".txt";

			String sinkIn = "sink(\"" + TxtOuptut + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designPRep(trmtPerGrp = "+ inputTransform.createRList(trmtGrpName, numTrmtPerGrp);
			command = command + ", trmtRepPerGrp = "+ inputTransform.createRNumVector(trmtRepPerGrp);
			command = command + ", trmtName = \""+ trmtName  +"\", trial = "+ trial;
			command = command + ", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			if (trmtLabel == null) {
				command = command + ", trmtLabel = NULL";
			} else {
				command = command + ", trmtLabel = \""+ trmtLabel + "\"";
			}
			if (trmtListPerGrp == null) {
				command = command + ", trmtListPerGrp = NULL";
			} else {
				command = command + ", trmtListPerGrp = "+ inputTransform.createRVector(trmtListPerGrp);
			}

			command = command + ", file = \""+ CSVOutput +"\")";
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designPRep:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			//		else {
			//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
			//			checkOutput = checkOutput + "    cat(\"\\nLayout for Alpha Lattice Design:\",\"\\n\\n\")\n";
			//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
			//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
			//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
			//			checkOutput = checkOutput + "    }\n";
			//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
			//			checkOutput = checkOutput + "}";
			//	
			//			System.out.println(checkOutput);
			//			rEngine.eval(checkOutput);
			//		}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}

	public void doDesignRCBD(String path, String fieldBookName, String[] factorName, String[] factorID, Integer[] factorLevel,
			Integer blk, Integer trial, Integer numFieldRow, Integer rowPerBlk, String fieldOrder){
		try{
			Integer[] startVal = {1};
			String inputList = inputTransform.createRList(factorName, startVal, factorLevel);

			// defining the R statements for randomization of randomized complete block design
			// input specification for PBTools only
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			String LayoutOutput = path + fieldBookName;

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designRCBD(generate = "+ inputList +", r = "+ blk +", trial = "+ trial +", numFieldRow = "+ numFieldRow + ", rowPerBlk = "+ rowPerBlk;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			command = command + ", display = TRUE, file = \""+ CSVOutput +"\")";
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);

			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designRCBD:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {
				//			String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
				//			checkOutput = checkOutput + "    cat(\"\\nLayout for Randomized Complete Block Design:\",\"\\n\\n\")\n";
				//			checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
				//			checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum, RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
				//			checkOutput = checkOutput + "          cat(\"\\n\")\n";
				//			checkOutput = checkOutput + "    }\n";
				//			checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
				//			checkOutput = checkOutput + "}";

				Integer numTrmt = factorLevel[0];
				Integer colPerBlk = numTrmt/rowPerBlk;

				String checkOutput = "for (i in (1:length(result$layout))) {\n";
				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerBlk+", "+ colPerBlk +"), bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
				checkOutput = checkOutput + "     dev.off() \n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");
			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
	}

	public void doDesignRowColumn(String path, String fieldBookName, Integer numTrmt, Integer rep, Integer trial, 
			Integer rowPerRep, Integer numFieldRow, String fieldOrder){

		try{

			//defining the R statements for randomization for Row-Column Design
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			String LayoutOutput = path + fieldBookName;

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designRowColumn(list(EntryNo = c(1: "+ numTrmt +"))";
			command = command + ", r = "+ rep +", trial = "+ trial;
			//			command = command + ", rowblkPerRep = "+ rowblkPerRep +", rowPerRowblk = "+ rowPerRowblk;
			//			command = command + ", colblkPerRep = "+ colblkPerRep +", numFieldRow = "+ numFieldRow;
			command = command + ", rowPerRep = "+ rowPerRep +", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE, file = \""+ CSVOutput +"\")";
			} else {
				command = command + ", serpentine = TRUE, file = \""+ CSVOutput +"\")";
			}
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			//R statements passed on to the R engine
			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designRowColumn:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {

				String checkOutput = "for (i in (1:length(result$layout))) {\n";
				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
				//				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerRowblk +", "+ colPerColblk +"), bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     chtdiv = 3, bwd = 4, bcol = 4, ";
				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
				//				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = FALSE, label = TRUE, ";
				//				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowblkPerRep*rowPerRowblk +", "+ colblkPerRep*colPerColblk +"), bwd = 4)\n";
				checkOutput = checkOutput + "     dev.off() \n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}

	/*This methods execute R to run Latin Square Design*/
	public void doDesignAugmentedLSD(String path, String fieldBookName, Integer numCheck, Integer numNew, String trmtName,
			Integer trial, Integer numFieldRow, String fieldOrder, String trmtLabel, String[] checkTrmt, String[] newTrmt){
		try{

			//defining the R statements for randomization for augmented design in Latin Square Design
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";
			//			String LayoutOutput = path + fieldBookName;

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designAugmentedLSD(numCheck = "+ numCheck +", numNew = "+ numNew;
			if (trmtName == null) {
				command = command + ", trmtName = NULL"; 
			} else {
				command = command + ", trmtName = \""+ trmtName +"\"";
			}
			command = command + ", trial = "+ trial + ", numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			if (trmtLabel == null) {
				command = command + ", trmtLabel = NULL";
			} else {
				command = command + ", trmtLabel = \""+ trmtLabel +"\"";
			}
			if (checkTrmt == null) {
				command = command + ", checkTrmt = NULL";
			} else {
				command = command + ", checkTrmt = "+ inputTransform.createRVector(checkTrmt);
			}
			if (newTrmt == null) {
				command = command + ", newTrmt = NULL";
			} else {
				command = command + ", newTrmt = "+ inputTransform.createRVector(newTrmt);
			}
			command = command + ", file = \""+ CSVOutput +"\")";

			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);
			rConnection.eval(funcRandomize);

			//save sorted to csv file
			//			String sortFile = "write.csv(result$fieldbook[order(result$fieldbook$Trial, result$fieldbook$PlotNum),], file = \""+ CSVOutput +"\", row.names = FALSE)";
			//			System.out.println(sortFile);
			//			rEngine.eval(sortFile);


			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designAugmentedLSD:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			//			else {
			//				String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
			//				checkOutput = checkOutput + "    cat(\"\\nLayout for Augmented Latin Square Design:\",\"\\n\\n\")\n";
			//				checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
			//				checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum[[i]], RowLabel = rownames(result$layout[[i]]), ColLabel = colnames(result$layout[[i]]), title = paste(\"Trial = \", i, sep = \"\"))\n";
			//				checkOutput = checkOutput + "          cat(\"\\n\")\n";
			//				checkOutput = checkOutput + "    }\n";
			//				checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
			//				checkOutput = checkOutput + "}";
			//				
			////				String checkOutput = "for (i in (1:length(result$layout))) {\n";
			////				checkOutput = checkOutput + "     png(filename = paste(\"" + LayoutOutput + "_Trial\",i,\".png\", sep = \"\")) \n";
			////				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 8, new = TRUE, label = TRUE, ";
			////				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerBlk+", "+ colPerBlk +"), bwd = 4, bcol = 4, ";
			////				checkOutput = checkOutput + "     cstr = paste(\"Layout for Trial \",i,\": \\n\\nFieldCol\", sep = \"\"), rstr = \"FieldRow\")\n";
			////				checkOutput = checkOutput + "     des.plot(result$layout[[i]], col = 7, new = FALSE, label = TRUE, ";
			////				checkOutput = checkOutput + "     chtdiv = 3, bdef = cbind("+ rowPerRep+", "+ colPerRep +"), bwd = 4)\n";
			////				checkOutput = checkOutput + "     dev.off() \n";
			////				checkOutput = checkOutput + "}";
			//				
			//				System.out.println(checkOutput);
			//				rEngine.eval(checkOutput);
			//				
			//				
			//			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");


		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
		//return msg;
	}

	/*This methods execute R to run RCBD*/
	public void doDesignAugmentedRCB(String path, String fieldBookName,  Integer numCheck, Integer numNew, Integer Blk, Integer numFieldRow, 
			Integer trial, String fieldOrder,Integer rowPerBlk,String trmtName, String trmtLabel,String[] checkTrmt,String[] newTrmt){
		try{
			//defining the R statements for randomization for augmented design in Latin Square Design
			rscriptCommand = new StringBuilder();
			String CSVOutput = path + fieldBookName + ".csv";
			String TxtOutput = path + fieldBookName + ".txt";

			String sinkIn = "sink(\"" + TxtOutput + "\")";
			String pkgIntro = "cat(\"Result of Randomization\\n\",date(),\"\\n\\n\\n\", sep = \"\")";
			String funcRandomize = "result <- try(";
			String command = "designAugmentedRCB(numCheck = "+ numCheck +", numNew = "+ numNew;
			if (trmtName == null) {
				command = command + ", trmtName = NULL"; 
			} else {
				command = command + ", trmtName = \""+ trmtName +"\"";
			}
			command = command + ", r = "+ Blk +", trial = "+ trial + ", rowPerBlk = "+ rowPerBlk +" , numFieldRow = "+ numFieldRow;
			if (fieldOrder == "Plot Order") {
				command = command + ", serpentine = FALSE";
			} else {
				command = command + ", serpentine = TRUE";
			}
			if (trmtLabel == null) {
				command = command + ", trmtLabel = NULL";
			} else {
				command = command + ", trmtLabel = \""+ trmtLabel +"\"";
			}
			if (checkTrmt == null) {
				command = command + ", checkTrmt = NULL";
			} else {
				command = command + ", checkTrmt = "+ inputTransform.createRVector(checkTrmt);
			}
			if (newTrmt == null) {
				command = command + ", newTrmt = NULL";
			} else {
				command = command + ", newTrmt = "+ inputTransform.createRVector(newTrmt);
			}
			command = command + ", file = \""+ CSVOutput +"\")";
			funcRandomize = funcRandomize + command + ", silent = TRUE)";

			System.out.println(sinkIn);
			System.out.println(pkgIntro);
			System.out.println(funcRandomize);


			rConnection.eval(sinkIn);
			rConnection.eval(pkgIntro);

			rConnection.eval(funcRandomize);

			//save sorted to csv file
			//		String sortFile = "write.csv(result$fieldbook[order(result$fieldbook$Trial, result$fieldbook$PlotNum),], file = \""+ CSVOutput +"\", row.names = FALSE)";
			//		System.out.println(sortFile);
			//		rEngine.eval(sortFile);

			String runSuccessCommand = rConnection.eval("class(result)").asString();
			if (runSuccessCommand.equals("try-error")) {
				String errorMsg1 = "msg <- trimStrings(strsplit(result, \":\")[[1]])";
				String errorMsg2 = "msg <- trimStrings(paste(strsplit(msg, \"\\n\")[[length(msg)]], collapse = \" \"))";
				String errorMsg3 = "msg <- gsub(\"\\\"\", \"\", msg)";
				String errorMsg4 = "cat(\"Error in designAugmentedRCB:\\n\",msg, sep = \"\")";

				System.out.println(errorMsg1);
				System.out.println(errorMsg2);
				System.out.println(errorMsg3);
				System.out.println(errorMsg4);

				rConnection.eval(errorMsg1);
				rConnection.eval(errorMsg2);
				rConnection.eval(errorMsg3);
				rConnection.eval(errorMsg4);
			} 
			else {
				String checkOutput = "if (nrow(result$fieldbook) != 0) {\n";
				checkOutput = checkOutput + "    cat(\"\\nLayout for Augmented Randomized Complete Block Design:\",\"\\n\\n\")\n";
				checkOutput = checkOutput + "    for (i in (1:length(result$layout))) { \n";
				checkOutput = checkOutput + "          printLayout(result$layout[[i]], result$plotNum[[i]], RowLabel = rownames(result$layout[[i]]), ColLabel = NULL, title = paste(\"Trial = \", i, sep = \"\"))\n";
				checkOutput = checkOutput + "          cat(\"\\n\")\n";
				checkOutput = checkOutput + "    }\n";
				checkOutput = checkOutput + "    cat(\"\\n\",\"**Note: Cells contain plot numbers on top, treatments/entries below\")\n";
				checkOutput = checkOutput + "}";

				System.out.println(checkOutput);
				rConnection.eval(checkOutput);
			}

			String sinkOut = "sink()";
			System.out.println(sinkOut);
			rConnection.eval(sinkOut);

			rscriptCommand.append(command+"\n");

			System.out.println("reached end.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			rConnection.close();
		}
	}


}
