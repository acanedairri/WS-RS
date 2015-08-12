package org.analysis.model;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains setter/getters parameters used in randomization
 * 
 * @author Alexander Cañeda
 * 
 *		username			System generated based on user login to BIMS
		resultFolderName	Folder name of analysis results files
		dataHeader			Column header of the dataset
		data				Dataset
		env					Environment variable label
		Respvars			Response variable
		design					0 - Randomized Complete Block Design (RCB)
								1 - Augmented RCB
								2 - Augmented Latin Square
								3 - Alpha-Lattice
								4 - Row-Column
								5 - Latinized Alpha-Lattice and
								6 - Latinized Row-Column
		genotype			Genotype variable label in BIMS, required for all designs
		genotypeFixed		If true, genotype is considered as a fixed factor
		genotypeRandom		If true, genotype is considered as a random factor
		block				NULL or Block  variable label in BIMS, required for RCB and Augmented RCB
		rep					NULL or Rep variable label in BIMS,  required for alpha lattice, Latinized alpha lattice, row-column design and Latinized row column
		row					NULL or Row variable label in BIMS, required for augmented Latin square, row-column design and Latinized row column
		column				NULL or Column variable label in BIMS, required for augmented Latin square, row-column design and Latinized row column
		performPairwise		If true, pairwise mean comparisons will be performed; for genotype considered as a fixed factor
		pairwiseAlpha		NULL or if performPairwise is true, value of the level of significance for performing pairwise mean comparisons; for genotype considered as a fixed factor
		compareControl		If true, pairwise mean comparison will be performed only on the control level(s) versus the rest of the genotype levels. The user should specify the control level(s) in the controlLevels field ; for genotype considered as a fixed factor
		controlLevels		Control levels (among the genotypes)
		performAllPairwise	If true, pairwise mean comparison will be performed on all genotypes. This option is not recommended when the number of genotype levels is very large. For now, PBTools only executes this option if the number of genotype levels is at most fifteen; for genotype considered as a fixed factor
		excludeControls		If true and the design is RCB, Alpha-Lattice or Row-Column, the user should specify the control level(s) to exclude in the controlLevels field. If true and the design is Augmented RCB or Augmented Latin Square, PBTools will automatically identify the replicated genotypes and set these as controls; for genotype considered as a random factor
		controlLevels		Control levels (among the genotypes)
		genoPhenoCorrelation	If true, genotypic and phenotypic correlations are computed for all pairs of response variables; only possible if there are two or more response variables specified
		descriptiveStat		If true, a summary table with number of missing observations, mean and standard deviation will be displayed.
		varianceComponents	If true, a table with the variance estimates of some model components will be displayed.
		heatmapResiduals	If true, heatmap is generated. User should specify the row and column variables in the heatmapRow and heatmapColumn fields.
		heatmapRow			NULL or name of the variable to be placed in the rows of the heatmap, if heatmapResiduals is true
		heatmapColumn		NULL or name of the variable to be placed in the columns of the heatmap, if heatmapResiduals is true
		boxplotRawData		If true, a boxplot of the raw data is created
		histogramRawData	If true, a histogram of the raw data is created
		diagnosticPlot		If true, diagnostic plots are generated


 */ 

public class SingleTrialParametersModel {
	
	
	private String path;
	private String resultFolderName;
	private String outFileName;
	private String dataFileName;
	private String analysisResultFolder;
	private String userAccount;
	
	private String[] dataHeader;
	private List<String[]> data;

	private int design;
	private String[] respvars= {};
	private String environment;
	private String[] environmentLevels = {};
	private String genotype;
	private String block;
	private String rep;
	private String row;
	private String column;
	private boolean descriptiveStat; 
	private boolean varianceComponents;
	private boolean boxplotRawData;
	private boolean histogramRawData;
	private boolean heatmapResiduals;
	private String heatmapRow;

	private String heatmapColumn;
	private boolean diagnosticPlot;
	private boolean genotypeFixed;
	private boolean performPairwise;
	private String pairwiseAlpha;
	private String[] genotypeLevels = {};
	private String[] controlLevels = {};
	private boolean compareControl;
	private boolean performAllPairwise;
	private boolean genotypeRandom;
	private boolean excludeControls;
	private boolean genoPhenoCorrelation;
	
	private boolean specifiedContrast;
	private String contrastFileName;
	private boolean moransTest; // for BIMS always false
	private String[] spatialStruc;
	
	
	

	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public boolean isSpecifiedContrast() {
		return specifiedContrast;
	}



	public void setSpecifiedContrast(boolean specifiedContrast) {
		this.specifiedContrast = specifiedContrast;
	}



	public String getContrastFileName() {
		return contrastFileName;
	}



	public void setContrastFileName(String contrastFileName) {
		this.contrastFileName = contrastFileName;
	}



	public boolean isMoransTest() {
		return moransTest;
	}



	public void setMoransTest(boolean moransTest) {
		this.moransTest = moransTest;
	}



	public String[] getSpatialStruc() {
		return spatialStruc;
	}



	public void setSpatialStruc(String[] spatialStruc) {
		this.spatialStruc = spatialStruc;
	}



	public SingleTrialParametersModel() {
		
	}
		
	
	
	public String getUserAccount() {
		return userAccount;
	}



	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}



	public String getAnalysisResultFolder() {
		return analysisResultFolder;
	}

	
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

	@Override
	public String toString() {
		return "SingleSiteAnalysisModel [resultFolderName=" + resultFolderName
				+ ", outFileName=" + outFileName + ", dataFileName="
				+ dataFileName + ", analysisResultFolder="
				+ analysisResultFolder + ", dataHeader="
				+ Arrays.toString(dataHeader) + ", data=" + data + ", design="
				+ design + ", respvars=" + Arrays.toString(respvars)
				+ ", environment=" + environment + ", environmentLevels="
				+ Arrays.toString(environmentLevels) + ", genotype=" + genotype
				+ ", block=" + block + ", rep=" + rep + ", row=" + row
				+ ", column=" + column + ", descriptiveStat=" + descriptiveStat
				+ ", varianceComponents=" + varianceComponents
				+ ", boxplotRawData=" + boxplotRawData + ", histogramRawData="
				+ histogramRawData + ", heatmapResiduals=" + heatmapResiduals
				+ ", heatmapRow=" + heatmapRow + ", heatmapColumn="
				+ heatmapColumn + ", diagnosticPlot=" + diagnosticPlot
				+ ", genotypeFixed=" + genotypeFixed + ", performPairwise="
				+ performPairwise + ", pairwiseAlpha=" + pairwiseAlpha
				+ ", genotypeLevels=" + Arrays.toString(genotypeLevels)
				+ ", controlLevels=" + Arrays.toString(controlLevels)
				+ ", compareControl=" + compareControl
				+ ", performAllPairwise=" + performAllPairwise
				+ ", genotypeRandom=" + genotypeRandom + ", excludeControls="
				+ excludeControls + ", genoPhenoCorrelation="
				+ genoPhenoCorrelation + "]";
	}

	public void setAnalysisResultFolder(String analysisResultFolder) {
		this.analysisResultFolder = analysisResultFolder;
	}



	public String getResultFolderName() {
		return resultFolderName;
	}

	public void setResultFolderName(String resultFolderName) {
		this.resultFolderName = resultFolderName;
	}

	public String getOutFileName() {
		return outFileName;
	}

	public void setOutFileName(String outFileName) {
		this.outFileName = outFileName;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public int getDesign() {
		return design;
	}

	public void setDesign(int design) {
		this.design = design;
	}

	public String[] getRespvars() {
		return respvars;
	}

	public void setRespvars(String[] respvars) {
		this.respvars = respvars;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String[] getEnvironmentLevels() {
		return environmentLevels;
	}

	public void setEnvironmentLevels(String[] environmentLevels) {
		this.environmentLevels = environmentLevels;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getRep() {
		return rep;
	}

	public void setRep(String rep) {
		this.rep = rep;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isDescriptiveStat() {
		return descriptiveStat;
	}

	public void setDescriptiveStat(boolean descriptiveStat) {
		this.descriptiveStat = descriptiveStat;
	}

	public boolean isVarianceComponents() {
		return varianceComponents;
	}

	public void setVarianceComponents(boolean varianceComponents) {
		this.varianceComponents = varianceComponents;
	}

	public boolean isBoxplotRawData() {
		return boxplotRawData;
	}

	public void setBoxplotRawData(boolean boxplotRawData) {
		this.boxplotRawData = boxplotRawData;
	}

	public boolean isHistogramRawData() {
		return histogramRawData;
	}

	public void setHistogramRawData(boolean histogramRawData) {
		this.histogramRawData = histogramRawData;
	}

	public boolean isHeatmapResiduals() {
		return heatmapResiduals;
	}

	public void setHeatmapResiduals(boolean heatmapResiduals) {
		this.heatmapResiduals = heatmapResiduals;
	}

	public String getHeatmapRow() {
		return heatmapRow;
	}

	public void setHeatmapRow(String heatmapRow) {
		this.heatmapRow = heatmapRow;
	}

	public String getHeatmapColumn() {
		return heatmapColumn;
	}

	public void setHeatmapColumn(String heatmapColumn) {
		this.heatmapColumn = heatmapColumn;
	}

	public boolean isDiagnosticPlot() {
		return diagnosticPlot;
	}

	public void setDiagnosticPlot(boolean diagnosticPlot) {
		this.diagnosticPlot = diagnosticPlot;
	}

	public boolean isGenotypeFixed() {
		return genotypeFixed;
	}

	public void setGenotypeFixed(boolean genotypeFixed) {
		this.genotypeFixed = genotypeFixed;
	}

	public boolean isPerformPairwise() {
		return performPairwise;
	}

	public void setPerformPairwise(boolean performPairwise) {
		this.performPairwise = performPairwise;
	}

	public String getPairwiseAlpha() {
		return pairwiseAlpha;
	}

	public void setPairwiseAlpha(String pairwiseAlpha) {
		this.pairwiseAlpha = pairwiseAlpha;
	}

	public String[] getGenotypeLevels() {
		return genotypeLevels;
	}

	public void setGenotypeLevels(String[] genotypeLevels) {
		this.genotypeLevels = genotypeLevels;
	}

	public String[] getControlLevels() {
		return controlLevels;
	}

	public void setControlLevels(String[] controlLevels) {
		this.controlLevels = controlLevels;
	}

	public boolean isCompareControl() {
		return compareControl;
	}

	public void setCompareControl(boolean compareControl) {
		this.compareControl = compareControl;
	}

	public boolean isPerformAllPairwise() {
		return performAllPairwise;
	}

	public void setPerformAllPairwise(boolean performAllPairwise) {
		this.performAllPairwise = performAllPairwise;
	}

	public boolean isGenotypeRandom() {
		return genotypeRandom;
	}

	public void setGenotypeRandom(boolean genotypeRandom) {
		this.genotypeRandom = genotypeRandom;
	}

	public boolean isExcludeControls() {
		return excludeControls;
	}

	public void setExcludeControls(boolean excludeControls) {
		this.excludeControls = excludeControls;
	}

	public boolean isGenoPhenoCorrelation() {
		return genoPhenoCorrelation;
	}

	public void setGenoPhenoCorrelation(boolean genoPhenoCorrelation) {
		this.genoPhenoCorrelation = genoPhenoCorrelation;
	}

}
