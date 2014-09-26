import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.analysis.rserve.manager.test.OutlierParametersModel;
import org.analysis.rserve.manager.test.SingleTrialParametersModel;

import com.google.gson.Gson;



public class TestOutlier {


	public static void main(String[] args){

		try {
			
/*
			OutlierParametersModel params = new OutlierParametersModel();
			String respvar = "GRNYLD.Y.ha.";
			String genotype = "ENTRY.";
			String rep = "REP.";
			
			String[] dataHeader={"GID","ENTRY!","DESIGNATION","PARENTS","ENTRYNO","PLOT","SOURCE","MATGP!","REP!","ROW","COLUMN","FLW","MATURITY","PLTHGT","TILLER","14MC","PLOTYIELD","MISSHILL","CPY","GRNYLD(Y/ha)","BLAST","BB1","BPH","GLH","HARVESTDATE"};
			
			
			
			List<String[]> data= new ArrayList<String[]>();
			data.add(new String[]{"3426751","IR12N183","IR 91070-73-1-3-2","IR05N128/IR06M139","B3393","1","B5344","1","1","1","1","86","116","119","14","12.5","6080","1","6112","10000","0","1","3","3","2013-04-11"});
			data.add(new String[]{"3426267","IR12A232","IR 91104-9-2-2-1","IR06F434/IR07F289","A3293","2","A5212","1","1","1","2","86","116","117","15","11.6","6680","","","100000","3","1","3","5","2013-04-11"});
			data.add(new String[]{"3426611","IR12A166","IR 91028-123-3-3-3","IR01A111/MTU 1010","A3264","3","A5146","1","1","1","3","82","112","108","21","9.1","6047","","","8454","3","1","9","5","2013-04-08"});
			data.add(new String[]{"2433014","IR09N531","IR 83704-18-3-2-2","IR04N114/IR 73459-120-2-2-3//KHAO KHAE","B3343","4","B2783","1","1","1","4","84","114","107","18","9.1","6042","","","20000","2","1","7","5","2013-04-08"});*/
//			data.add(new String[]{"2524953","IR09A131","IR 83850-4-3-3-2","IR 77080-B-34-3/IRRI 135","A3230","5","A2717","1","1","1","5","81","111","101","16","11.8","4411","3","4482","6080","3","1","5","3","2013-04-04"});
//			data.add(new String[]{"3426287","IR12A236","IR 91106-59-3-1-3","MTU 1010/IR06M139","A3294","6","A5216","1","1","1","6","83","113","128","12","11.7","6256","","","15000","3","1","3","3","2013-04-04"});
//			data.add(new String[]{"2432999","IR09N533","IR 83704-78-2-3-2","IR04N114/IR 73459-120-2-2-3//KHAO KHAE","B3345","7","B2830","1","1","1","7","82","112","98","18","9.9","6468","","","8963","2","1","5","3","2013-04-08"});
//			data.add(new String[]{"1254166","IR04A381","IR 78545-49-2-2-2","IR 71137-184-3-2-3-3/IR 72875-94-3-3-2","A3208","8","A2706","1","1","1","8","81","111","91","17","10.9","4929","3","5009","6864","4","1","3","5","2013-04-04"});
//			
//			//data.add(new String[]{"3425477","IR12A140","IR 90243-14-1-3-1-3","[IRRI 123","IR 71146-97-1-2-1-3","IR 71676-90-2-2","IR 72870-19-2-2-3","IRRI 143","IR04A285","IR03A550","IR 72967-12-2-3","IR 72903-121-2-1-2","IRRI 119","A3258","9","A5120","1","1","1","9","82","112","101","15","11.1","4764","6","4920","6728","0","1","9","3","2013-04-04"});
//			data.add(new String[]{"3425852","IR12A156","IR 91027-64-2-1-3","IR01A111/IR06M139","A3261","11","A5136","1","1","1","11","90","120","109","19","14","6359","3","6462","8547","1","1","5","5","2013-04-13"});
//			data.add(new String[]{"3022800","IR11A208","IR 87477-83-1-2-1","IR00A107/IR 72875-94-3-3-2","A3248","12","A2166","1","1","1","12","83","113","98","20","9","5199","","","7277","1","7","9","5","2013-04-08"});
//			data.add(new String[]{"3426043","IR12N186","IR 91075-37-3-2-2","IR05N160/IR06M139","B3394","13","B5347","1","1","1","13","88","118","102","13","13.3","6403","1","20000","8584","3","1","3","5","2013-04-11"});
//			data.add(new String[]{"3426349","IR12A255","IR 91123-3-3-3-2","IRRI 123/IR06M139","A3300","14","A5235","1","1","1","14","80","110","98","18","11","5828","","","7978","0","1","3","3","2013-04-04"});
//			data.add(new String[]{"50533","IR64","IR 64","IR 5657-33-2-1/IR 2061-465-1-5-5","A3443","15","A2745","1","1","1","15","95","125","101","15","13.8","5678","","","7528","1","1","7","5","2013-04-15"});
//			data.add(new String[]{"3426083","IR12N191","IR 91076-32-1-2-3","IR05N160/MTU 1010","B3397","16","B5352","1","1","1","16","88","118","114","22","11.2","6538","1","6573","50000","2","1","5","5","2013-04-15"});



/*			params.setUserAccount("studyOutlier");
			params.setAnalysisResultFolder("myOutlier20");
			params.setDataHeader(dataHeader);
			params.setData(data);
			
			params.setRespvar(respvar);
			params.setGenotype(genotype);
			params.setRep(rep);*/
			
		

			Gson gson = new Gson();
	//		String json = "{ "analysisResultFolder": "IRSEA-IRRIHQ-AGR-2014-DS-5_HT_CONT_4", "username": "j.lagare", "dataHeader": [ "Site", "Designation", "HT_CONT", "Blk" ], "data": [ [ "Env1", "IR12A256", "102", "1" ], [ "Env1", "IR12A200", "122", "1" ], [ "Env1", "IR07A107", "92", "1" ], [ "Env1", "IRRI 154", "98", "2" ], [ "Env1", "IRRI 154", "98", "1" ], [ "Env1", "IRRI 146", "83", "2" ], [ "Env1", "(SITU BAGENDIT*3/NIL-C443 5)-204", "102", "1" ], [ "Env1", "(SITU BAGENDIT*3/KASALATH 5)-72", "155", "1" ], [ "Env1", "SITU BAGENDIT", "92", "1" ], [ "Env1", "IR 64-Pup1-M", "94", "1" ], [ "Env1", "IR 74-Pup1-D", "84", "1" ], [ "Env1", "IR 74-Pup1-A", "86", "2" ], [ "Env1", "IR 64", "86", "2" ], [ "Env1", "IR08N180", "101", "2" ], [ "Env1", "IR 84636-13-12-2-2-2-2-4-2-2-2-B", "93", "2" ], [ "Env1", "IR 84636-13-12-2-2-2-2-4-2-2-2-B", "97", "1" ], [ "Env1", "IR 64", "126", "1" ], [ "Env1", "IR07A253", "111", "2" ], [ "Env1", "IR07A253", "106", "1" ], [ "Env1", "IR06A144", "89", "1" ], [ "Env1", "IR09N536", "92", "1" ], [ "Env1", "IR12A108", "99", "1" ], [ "Env1", "IR11N121", "102", "1" ], [ "Env1", "IRRI 168", "99", "1" ], [ "Env1", "IRRI 123", "83", "2" ], [ "Env1", "IRRI 104", "72", "1" ], [ "Env1", "(SITU BAGENDIT*3/NIL-C443 5)-204", "103", "2" ], [ "Env1", "(SITU BAGENDIT*3/KASALATH 5)-72", "155", "2" ], [ "Env1", "(BATUR*3/NIL-C443 5)-376", "137", "2" ], [ "Env1", "IR 64-PUP1-H", "102", "1" ], [ "Env1", "IR 64-Pup1-M", "94", "2" ], [ "Env1", "IR 74-Pup1-C", "76", "1" ], [ "Env1", "IR 74-Pup1-B", "83", "2" ], [ "Env1", "IR64-NIL5", "92", "2" ], [ "Env1", "IR 84636-13-12-2-6-3-3-2-2-B", "91", "1" ], [ "Env1", "IR 82355-5-1-3", "103", "1" ], [ "Env1", "IR10N285", "89", "2" ], [ "Env1", "(BATUR*3/KASALATH)-1830", "136", "2" ], [ "Env1", "IR12N125", "87", "1" ], [ "Env1", "IR09A128", "105", "2" ], [ "Env1", "IR 64", "88", "1" ], [ "Env1", "BATUR", "121", "2" ], [ "Env1", "IR12A256", "97", "2" ], [ "Env1", "IR12A200", "118", "2" ], [ "Env1", "IR09A128", "105", "1" ], [ "Env1", "IR09A130", "103", "2" ], [ "Env1", "IR06A144", "100", "2" ], [ "Env1", "IR09N536", "89", "2" ], [ "Env1", "IR07A107", "87", "2" ], [ "Env1", "IRRI 156", "97", "2" ], [ "Env1", "IRRI 123", "93", "1" ], [ "Env1", "IRRI 104", "73", "2" ], [ "Env1", "(BATUR*3/NIL-C443 5)-376", "136", "1" ], [ "Env1", "(BATUR*3/KASALATH)-1830", "137", "1" ], [ "Env1", "IR 64-PUP1-H", "95", "2" ], [ "Env1", "IR74", "84", "2" ], [ "Env1", "IR64-NIL5", "92", "1" ], [ "Env1", "IR 82355-5-1-3", "100", "2" ], [ "Env1", "IR08N180", "99", "1" ], [ "Env1", "IR12A108", "101", "2" ], [ "Env1", "IR12N125", "91", "2" ], [ "Env1", "IR09A130", "104", "1" ], [ "Env1", "IRRI 146", "88", "1" ], [ "Env1", "IRRI 156", "102", "1" ], [ "Env1", "SITU BAGENDIT", "95", "2" ], [ "Env1", "BATUR", "133", "1" ], [ "Env1", "IR 64", "132", "2" ], [ "Env1", "IR 74-Pup1-D", "80", "2" ], [ "Env1", "IR 74-Pup1-C", "82", "2" ], [ "Env1", "IR 74-Pup1-B", "75", "1" ], [ "Env1", "IR 74-Pup1-A", "81", "1" ], [ "Env1", "IR74", "86", "1" ], [ "Env1", "IR 84636-13-12-2-6-3-3-2-2-B", "88", "2" ], [ "Env1", "IR10N285", "91", "1" ], [ "Env1", "IRRI 168", "95", "2" ], [ "Env1", "IR11N121", "99", "2" ] ], "respvar" : "HT_CONT", "genotype" :"Designation", "rep" : "Blk", }";//'// gson.toJson(params);
			String json="{ 'analysisResultFolder': 'IRSEA-IRRIHQ-AGR-2014-DS-5_HT_CONT_4', 'username': 'j.lagare', 'dataHeader': [ 'Site', 'Designation', 'HT_CONT', 'Blk' ], 'data': [ [ 'Env1', 'IR12A256', '102', '1' ], [ 'Env1', 'IR12A200', '122', '1' ], [ 'Env1', 'IR07A107', '92', '1' ], [ 'Env1', 'IRRI 154', '98', '2' ], [ 'Env1', 'IRRI 154', '98', '1' ], [ 'Env1', 'IRRI 146', '83', '2' ], [ 'Env1', '(SITU BAGENDIT*3/NIL-C443 5)-204', '102', '1' ], [ 'Env1', '(SITU BAGENDIT*3/KASALATH 5)-72', '155', '1' ], [ 'Env1', 'SITU BAGENDIT', '92', '1' ], [ 'Env1', 'IR 64-Pup1-M', '94', '1' ], [ 'Env1', 'IR 74-Pup1-D', '84', '1' ], [ 'Env1', 'IR 74-Pup1-A', '86', '2' ], [ 'Env1', 'IR 64', '86', '2' ], [ 'Env1', 'IR08N180', '101', '2' ], [ 'Env1', 'IR 84636-13-12-2-2-2-2-4-2-2-2-B', '93', '2' ], [ 'Env1', 'IR 84636-13-12-2-2-2-2-4-2-2-2-B', '97', '1' ], [ 'Env1', 'IR 64', '126', '1' ], [ 'Env1', 'IR07A253', '111', '2' ], [ 'Env1', 'IR07A253', '106', '1' ], [ 'Env1', 'IR06A144', '89', '1' ], [ 'Env1', 'IR09N536', '92', '1' ], [ 'Env1', 'IR12A108', '99', '1' ], [ 'Env1', 'IR11N121', '102', '1' ], [ 'Env1', 'IRRI 168', '99', '1' ], [ 'Env1', 'IRRI 123', '83', '2' ], [ 'Env1', 'IRRI 104', '72', '1' ], [ 'Env1', '(SITU BAGENDIT*3/NIL-C443 5)-204', '103', '2' ], [ 'Env1', '(SITU BAGENDIT*3/KASALATH 5)-72', '155', '2' ], [ 'Env1', '(BATUR*3/NIL-C443 5)-376', '137', '2' ], [ 'Env1', 'IR 64-PUP1-H', '102', '1' ], [ 'Env1', 'IR 64-Pup1-M', '94', '2' ], [ 'Env1', 'IR 74-Pup1-C', '76', '1' ], [ 'Env1', 'IR 74-Pup1-B', '83', '2' ], [ 'Env1', 'IR64-NIL5', '92', '2' ], [ 'Env1', 'IR 84636-13-12-2-6-3-3-2-2-B', '91', '1' ], [ 'Env1', 'IR 82355-5-1-3', '103', '1' ], [ 'Env1', 'IR10N285', '89', '2' ], [ 'Env1', '(BATUR*3/KASALATH)-1830', '136', '2' ], [ 'Env1', 'IR12N125', '87', '1' ], [ 'Env1', 'IR09A128', '105', '2' ], [ 'Env1', 'IR 64', '88', '1' ], [ 'Env1', 'BATUR', '121', '2' ], [ 'Env1', 'IR12A256', '97', '2' ], [ 'Env1', 'IR12A200', '118', '2' ], [ 'Env1', 'IR09A128', '105', '1' ], [ 'Env1', 'IR09A130', '103', '2' ], [ 'Env1', 'IR06A144', '100', '2' ], [ 'Env1', 'IR09N536', '89', '2' ], [ 'Env1', 'IR07A107', '87', '2' ], [ 'Env1', 'IRRI 156', '97', '2' ], [ 'Env1', 'IRRI 123', '93', '1' ], [ 'Env1', 'IRRI 104', '73', '2' ], [ 'Env1', '(BATUR*3/NIL-C443 5)-376', '136', '1' ], [ 'Env1', '(BATUR*3/KASALATH)-1830', '137', '1' ], [ 'Env1', 'IR 64-PUP1-H', '95', '2' ], [ 'Env1', 'IR74', '84', '2' ], [ 'Env1', 'IR64-NIL5', '92', '1' ], [ 'Env1', 'IR 82355-5-1-3', '100', '2' ], [ 'Env1', 'IR08N180', '99', '1' ], [ 'Env1', 'IR12A108', '101', '2' ], [ 'Env1', 'IR12N125', '91', '2' ], [ 'Env1', 'IR09A130', '104', '1' ], [ 'Env1', 'IRRI 146', '88', '1' ], [ 'Env1', 'IRRI 156', '102', '1' ], [ 'Env1', 'SITU BAGENDIT', '95', '2' ], [ 'Env1', 'BATUR', '133', '1' ], [ 'Env1', 'IR 64', '132', '2' ], [ 'Env1', 'IR 74-Pup1-D', '80', '2' ], [ 'Env1', 'IR 74-Pup1-C', '82', '2' ], [ 'Env1', 'IR 74-Pup1-B', '75', '1' ], [ 'Env1', 'IR 74-Pup1-A', '81', '1' ], [ 'Env1', 'IR74', '86', '1' ], [ 'Env1', 'IR 84636-13-12-2-6-3-3-2-2-B', '88', '2' ], [ 'Env1', 'IR10N285', '91', '1' ], [ 'Env1', 'IRRI 168', '95', '2' ], [ 'Env1', 'IR11N121', '99', '2' ] ], 'respvar' : 'HT_CONT', 'genotype' :'Designation', 'rep' : 'Blk' }";
			System.out.println(json);

			Client c = ClientBuilder.newClient();
			WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/outlier");
//			WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/outlier");
			Response response = target.request().post(Entity.json(json));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}


			String output = response.getEntity().toString();
			System.out.println("Server response .... \n");
			System.out.println("Response "+response.readEntity(String.class));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

}
