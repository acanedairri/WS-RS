package org.analysis.model;

/**
 * This class contains setter/getters parameters used in randomization
 * 
 * @author Alexander Cañeda
 * 
		resultFolder:	Folder name of the randomization output files
		design:    		Index number of the study design (0 to 9)
		blk:	 		Number of Block
		blkSize: 		Block size
		checkTrmt		Levels of Check Treatment, default value=”NULL”
		factorLevel		Number of Treatments
		factorName		Name of Treatment
		fieldBookName	Field Book Filename
		fieldOrder		Field Book Order -     Options : Serpentine  or Plot Order
		fieldRow		Total Number of Fieldrow in Experimental Design
		numCheck		Number of Checks
		numFieldRow		Number of Field Rows
		numNew			Number of New Treatment
		numTrmt			Number of Treatment
		numTrmtPerGrp	Number of Treatment per Group
		rep				Number of Replicates
		rowBlkPerRep	Number or Blocks per Replicate
		rowPerBlk		Number of Row per Block
		rowPerRep		Number of Row per Replicate
		rowPerRowBlk	Number of Row per Row Block
		trial			Number of Trials
		trmtGrpName		Name of Group Treatment
		trmtLabel		Name of Treatment
		trmtListPerGrp	Level of Treatment per Group
		trmtName		Name of Treatment
		trmtRepPerGrp	Number of Replicate per Group

 *
 */

public class RandomizationParamModel {
	
	String resultFolder;
	String path;
	int design;
	int blkSize;
	int rep;
	int trial;
	int blk;
	int rowPerBlk;
	int rowPerRep;
	int numFieldRow;
	int fieldRow;
	int numCheck;
	int numNew;
	int numTrmt;
	int rowBlkPerRep;
	int rowPerRowBlk;
	int repTrmt;
	int unrepTrmt;
	String trmtName;
	String trmtLabel;
	String[] checktrmt;
	String[] newTrmt;
	String fieldOrder;
	String fieldBookName;
	String[] trmtListPerGrp;
	String[] trmtGrpName;
	String[] factorName;
	String[] factorID;
	Integer[] factorLevel;
	Integer[]numTrmtPerGrp;
	Integer[]trmtRepPerGrp;
	

	public String getResultFolder() {
		return resultFolder;
	}
	public void setResultFolder(String resultFolder) {
		this.resultFolder = resultFolder;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getDesign() {
		return design;
	}
	public void setDesign(int design) {
		this.design = design;
	}
	public int getBlkSize() {
		return blkSize;
	}
	public void setBlkSize(int blkSize) {
		this.blkSize = blkSize;
	}
	public int getRep() {
		return rep;
	}
	public void setRep(int rep) {
		this.rep = rep;
	}
	public int getTrial() {
		return trial;
	}
	public void setTrial(int trial) {
		this.trial = trial;
	}
	public int getBlk() {
		return blk;
	}
	public void setBlk(int blk) {
		this.blk = blk;
	}
	public int getRowPerBlk() {
		return rowPerBlk;
	}
	public void setRowPerBlk(int rowPerBlk) {
		this.rowPerBlk = rowPerBlk;
	}
	public int getRowPerRep() {
		return rowPerRep;
	}
	public void setRowPerRep(int rowPerRep) {
		this.rowPerRep = rowPerRep;
	}
	public int getNumFieldRow() {
		return numFieldRow;
	}
	public void setNumFieldRow(int numFieldRow) {
		this.numFieldRow = numFieldRow;
	}
	public int getNumCheck() {
		return numCheck;
	}
	public void setNumCheck(int numCheck) {
		this.numCheck = numCheck;
	}
	public int getNumNew() {
		return numNew;
	}
	public void setNumNew(int numNew) {
		this.numNew = numNew;
	}
	public int getNumTrmt() {
		return numTrmt;
	}
	public void setNumTrmt(int numTrmt) {
		this.numTrmt = numTrmt;
	}

	
	public int getRowBlkPerRep() {
		return rowBlkPerRep;
	}
	public void setRowBlkPerRep(int rowBlkPerRep) {
		this.rowBlkPerRep = rowBlkPerRep;
	}
	public int getRowPerRowBlk() {
		return rowPerRowBlk;
	}
	public void setRowPerRowBlk(int rowPerRowBlk) {
		this.rowPerRowBlk = rowPerRowBlk;
	}
	public String getTrmtName() {
		return trmtName;
	}
	public void setTrmtName(String trmtName) {
		this.trmtName = trmtName;
	}
	public String getTrmtLabel() {
		return trmtLabel;
	}
	public void setTrmtLabel(String trmtLabel) {
		this.trmtLabel = trmtLabel;
	}

	
	public String[] getChecktrmt() {
		return checktrmt;
	}
	public void setChecktrmt(String[] checktrmt) {
		this.checktrmt = checktrmt;
	}
	public String[] getNewTrmt() {
		return newTrmt;
	}
	public void setNewTrmt(String[] newTrmt) {
		this.newTrmt = newTrmt;
	}
	public String getFieldOrder() {
		return fieldOrder;
	}
	public void setFieldOrder(String fieldOrder) {
		this.fieldOrder = fieldOrder;
	}
	public String getFieldBookName() {
		return fieldBookName;
	}
	public void setFieldBookName(String fieldBookName) {
		this.fieldBookName = fieldBookName;
	}
	
	public String[] getTrmtListPerGrp() {
		return trmtListPerGrp;
	}
	public void setTrmtListPerGrp(String[] trmtListPerGrp) {
		this.trmtListPerGrp = trmtListPerGrp;
	}
	public String[] getTrmtGrpName() {
		return trmtGrpName;
	}
	public void setTrmtGrpName(String[] trmtGrpName) {
		this.trmtGrpName = trmtGrpName;
	}
	public String[] getFactorName() {
		return factorName;
	}
	public void setFactorName(String[] factorName) {
		this.factorName = factorName;
	}
	public String[] getFactorID() {
		return factorID;
	}
	public void setFactorID(String[] factorID) {
		this.factorID = factorID;
	}
	public Integer[] getFactorLevel() {
		return factorLevel;
	}
	public void setFactorLevel(Integer[] factorLevel) {
		this.factorLevel = factorLevel;
	}
	public Integer[] getNumTrmtPerGrp() {
		return numTrmtPerGrp;
	}
	public void setNumTrmtPerGrp(Integer[] numTrmtPerGrp) {
		this.numTrmtPerGrp = numTrmtPerGrp;
	}
	public Integer[] getTrmtRepPerGrp() {
		return trmtRepPerGrp;
	}
	public void setTrmtRepPerGrp(Integer[] trmtRepPerGrp) {
		this.trmtRepPerGrp = trmtRepPerGrp;
	}
	public int getRepTrmt() {
		return repTrmt;
	}
	public void setRepTrmt(int repTrmt) {
		this.repTrmt = repTrmt;
	}
	public int getUnrepTrmt() {
		return unrepTrmt;
	}
	public void setUnrepTrmt(int unrepTrmt) {
		this.unrepTrmt = unrepTrmt;
	}
	public int getFieldRow() {
		return fieldRow;
	}
	public void setFieldRow(int fieldRow) {
		this.fieldRow = fieldRow;
	}
	
	
	
	
	
	
	
	
	
	
	

}
