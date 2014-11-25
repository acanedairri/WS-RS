package randomization.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.analysis.model.RandomizationParamModel;
import org.analysis.rserve.manager.RServeManager;

import com.google.gson.Gson;

public class RandomizationTest{


	public static void main(String[] args){
		
		testRCBD0();
		testAugmentedRCB1();
		testAugmentedLatinSquare2();
		testAlphaLattice3();
		testRowColumn4();
		testLatinizedAlphaLattice5();
		testLatinizedRowColumn6();
		testPRep7();
		testAugmentedRowColumn8();
		
	}


	private static void testRCBD0(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String resultFolder="RCBD";
			String fieldBookName = "fieldbookRCBD"; 
			int design=0;

			String[] factorName = {"EntryNo"}; // for PBTools Version 1.5 and up and BIMS, only one factor should be entered
			String[] factorID = {"V"};
			Integer[] factorLevel = {24};
			Integer blk = 2;
			Integer trial = 2;
			Integer numFieldRow = 4;
			Integer rowPerBlk = 4;
			String fieldOrder = "Serpentine";


			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setFactorName(factorName);
			param.setFactorID(factorID); 
			param.setFactorLevel(factorLevel);
			param.setBlk(blk); 
			param.setTrial(trial);
			param.setNumFieldRow(numFieldRow);
			param.setRowPerBlk(rowPerBlk);
			param.setFieldOrder(fieldOrder);
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void testAugmentedRCB1(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String resultFolder="AugmentedRCB";
			String fieldBookName = "fieldbookAugmentedRCB"; 
			int design=1;

			Integer repTrmt = 5;
			Integer unrepTrmt = 702;
			Integer Blk = 6;
			Integer fieldRow = 122;
			Integer trial = 1;
			String fieldOrder = "Serpentine";


			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setRepTrmt(repTrmt);
			param.setUnrepTrmt(unrepTrmt);
			param.setBlk(Blk); 
			param.setFieldRow(fieldRow);
			param.setTrial(trial);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testAugmentedLatinSquare2(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String resultFolder="AugmentedLatinSquare";
			String fieldBookName = "fieldbookAugmentedLatinSquare"; 
			int design=2;

			Integer repTrmt = 5;
			Integer unrepTrmt = 230;
			Integer fieldRow = 17;
			Integer trial = 2;
			String fieldOrder = "Serpentine";


			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setRepTrmt(repTrmt);
			param.setUnrepTrmt(unrepTrmt);
			param.setFieldRow(fieldRow);
			param.setTrial(trial);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	private static void testAlphaLattice3(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookDesignAlphaLattice"; 		
			String resultFolder="AlphaLattice";
			//specify parameters
			int design=3;

			Integer numTrmt = 70;
			Integer blkSize = 14;
			Integer rep = 2;
			Integer trial = 3;
			Integer rowPerBlk = 14;
			Integer rowPerRep = 14;
			Integer numFieldRow = 28;
			String fieldOrder = "Serpentine";

			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumTrmt(numTrmt);
			param.setBlkSize(blkSize);
			param.setRep(rep);
			param.setTrial(trial);
			param.setRowPerBlk(rowPerBlk);
			param.setRowPerRep(rowPerRep);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void testRowColumn4(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookRowColumn"; 		
			String resultFolder="RowColumn";
			//specify parameters
			int design=4;

			Integer numTrmt = 20;
			Integer rep = 3;
			Integer trial = 1;
			Integer rowPerRep = 4;
			Integer numFieldRow = 12;
			String fieldOrder = "Serpentine";
			
			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumTrmt(numTrmt);
			param.setRep(rep);
			param.setTrial(trial);
			param.setRowPerRep(rowPerRep);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void testLatinizedAlphaLattice5(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookLatinizedAlphaLattice"; 		
			String resultFolder="LatinizedAlphaLattice";
			//specify parameters
			int design=5;

			Integer numTrmt = 15;
			Integer blkSize = 5;
			Integer rep = 3;
			Integer trial = 1;
			Integer numFieldRow = 3;
			String fieldOrder = "Serpentine";
			
			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumTrmt(numTrmt);
			param.setBlkSize(blkSize);
			param.setRep(rep);
			param.setTrial(trial);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void testLatinizedRowColumn6(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookLatinizedRowColumn"; 		
			String resultFolder="LatinizedRowColumn";
			//specify parameters
			int design=6;

			Integer numTrmt = 9;
			Integer rep = 3;
			Integer rowPerRep = 3;
			Integer numFieldRow = 9;
			Integer trial = 1;
			String fieldOrder = "Serpentine";
			
			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumTrmt(numTrmt);
			param.setRep(rep);
			param.setRowPerRep(rowPerRep);
			param.setNumFieldRow(numFieldRow);
			param.setTrial(trial);
			param.setFieldOrder(fieldOrder);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void testPRep7(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookPRep"; 		
			String resultFolder="PRep";
			//specify parameters
			int design=7;

			String[] trmtGrpName = {"NewRepTrmt", "NewUnrepTrmt", "Parent"};
			Integer[] numTrmtPerGrp = {48, 80, 2};
			Integer[] trmtRepPerGrp = {2, 1, 2};
			String trmtName = "ENTRYNO";
			Integer blk = 6;
			Integer trial = 1;
			Integer rowPerBlk = 6;
			Integer numFieldRow = 18; 
			String fieldOrder = "Serpentine";
			String trmtLabel = null;
			String trmtListPerGrp = null;
			
			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setTrmtGrpName(trmtGrpName); 
			param.setNumTrmtPerGrp(numTrmtPerGrp);
			param.setTrmtRepPerGrp(trmtRepPerGrp);
			param.setTrmtName(trmtName);
			param.setBlk(blk);
			param.setTrial(trial); 
			param.setRowPerBlk(rowPerBlk);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			param.setTrmtLabel(trmtLabel);
			param.setTrmtListPerGrp(trmtListPerGrp);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void testAugmentedRowColumn8(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String fieldBookName = "fieldbookAugmentedRowColumn"; 		
			String resultFolder="AugmentedRowColumn";
			//specify parameters
			int design=8;

			//specify parameters
			Integer numCheck = 12;
			Integer numNew = 120;
			String trmtName = "ENTRYNO";
			Integer rep = 2; 
			Integer trial = 2; 
			Integer rowblkPerRep = 3;
			Integer rowPerRowblk = 3; 
			Integer numFieldRow = 18;
			String fieldOrder = "Serpentine";
			String trmtLabel = null;
			String checkTrmt = null;
			String newTrmt = null;
			
			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumCheck(numCheck);
			param.setNumNew(numNew); 
			param.setTrmtName(trmtName);
			param.setRep(rep);
			param.setTrial(trial);
			param.setRowBlockPerRep(rowblkPerRep); 
			param.setRowPerRowBlock(rowPerRowblk);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			param.setTrmtLabel(trmtLabel);
			param.setChecktrmt(checkTrmt);
			param.setNewTrmt(newTrmt);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void testAugmentedAlphaLattice9(){

		try {
			RandomizationParamModel param= new RandomizationParamModel();


			String resultFolder="AugmentedAlphaLattice";
			String fieldBookName = "fieldbookAugmentedAlphaLattice"; 
			int design=9;

			//specify parameters
			Integer numCheck = 9;
			Integer numNew = 63;
			String trmtName = "EntryNum";
			Integer blkSize = 10;
			Integer rep = 3;
			Integer trial = 2;
			Integer rowPerBlk = 5;
			Integer rowPerRep = 15;
			Integer numFieldRow = 15;
			String fieldOrder = "Serpentine";
			String trmtLabel = "Designation";
			String checkTrmt = null;
			String newTrmt = null;

			param.setFieldBookName(fieldBookName);
			param.setDesign(design);
			param.setResultFolder(resultFolder);

			param.setNumCheck(numCheck);
			param.setNumNew(numNew); 
			param.setTrmtName(trmtName);
			param.setBlkSize(blkSize);
			param.setRep(rep);
			param.setTrial(trial);
			param.setRowPerBlk(rowPerBlk);
			param.setRowPerRep(rowPerRep);
			param.setNumFieldRow(numFieldRow);
			param.setFieldOrder(fieldOrder);
			param.setTrmtLabel(trmtLabel);
			param.setChecktrmt(null);
			param.setNewTrmt(null);
			
			Gson gson = new Gson();
			String json = gson.toJson(param);

			System.out.println(json);

			Client c = ClientBuilder.newClient();
			//		WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/run");
			//		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/run");
			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/Randomization/run");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println(response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
