import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;



public class TestOutlier {


	public static void main(String[] args){

		try {
			Gson gson = new Gson();
			String json="{ 'analysisResultFolder': 'test1', 'username': 'j.lagare', 'dataHeader': [ 'Site', 'Designation', 'HT_CONT', 'Blk' ], 'data': [ [ 'Env1', 'IR12A256', '102', '1' ], [ 'Env1', 'IR12A200', '122', '1' ], [ 'Env1', 'IR07A107', '92', '1' ], [ 'Env1', 'IRRI 154', '98', '2' ], [ 'Env1', 'IRRI 154', '98', '1' ], [ 'Env1', 'IRRI 146', '83', '2' ], [ 'Env1', '(SITU BAGENDIT*3/NIL-C443 5)-204', '102', '1' ], [ 'Env1', '(SITU BAGENDIT*3/KASALATH 5)-72', '155', '1' ], [ 'Env1', 'SITU BAGENDIT', '92', '1' ], [ 'Env1', 'IR 64-Pup1-M', '94', '1' ], [ 'Env1', 'IR 74-Pup1-D', '84', '1' ], [ 'Env1', 'IR 74-Pup1-A', '86', '2' ], [ 'Env1', 'IR 64', '86', '2' ], [ 'Env1', 'IR08N180', '101', '2' ], [ 'Env1', 'IR 84636-13-12-2-2-2-2-4-2-2-2-B', '93', '2' ], [ 'Env1', 'IR 84636-13-12-2-2-2-2-4-2-2-2-B', '97', '1' ], [ 'Env1', 'IR 64', '126', '1' ], [ 'Env1', 'IR07A253', '111', '2' ], [ 'Env1', 'IR07A253', '106', '1' ], [ 'Env1', 'IR06A144', '89', '1' ], [ 'Env1', 'IR09N536', '92', '1' ], [ 'Env1', 'IR12A108', '99', '1' ], [ 'Env1', 'IR11N121', '102', '1' ], [ 'Env1', 'IRRI 168', '99', '1' ], [ 'Env1', 'IRRI 123', '83', '2' ], [ 'Env1', 'IRRI 104', '72', '1' ], [ 'Env1', '(SITU BAGENDIT*3/NIL-C443 5)-204', '103', '2' ], [ 'Env1', '(SITU BAGENDIT*3/KASALATH 5)-72', '155', '2' ], [ 'Env1', '(BATUR*3/NIL-C443 5)-376', '137', '2' ], [ 'Env1', 'IR 64-PUP1-H', '102', '1' ], [ 'Env1', 'IR 64-Pup1-M', '94', '2' ], [ 'Env1', 'IR 74-Pup1-C', '76', '1' ], [ 'Env1', 'IR 74-Pup1-B', '83', '2' ], [ 'Env1', 'IR64-NIL5', '92', '2' ], [ 'Env1', 'IR 84636-13-12-2-6-3-3-2-2-B', '91', '1' ], [ 'Env1', 'IR 82355-5-1-3', '103', '1' ], [ 'Env1', 'IR10N285', '89', '2' ], [ 'Env1', '(BATUR*3/KASALATH)-1830', '136', '2' ], [ 'Env1', 'IR12N125', '87', '1' ], [ 'Env1', 'IR09A128', '105', '2' ], [ 'Env1', 'IR 64', '88', '1' ], [ 'Env1', 'BATUR', '121', '2' ], [ 'Env1', 'IR12A256', '97', '2' ], [ 'Env1', 'IR12A200', '118', '2' ], [ 'Env1', 'IR09A128', '105', '1' ], [ 'Env1', 'IR09A130', '103', '2' ], [ 'Env1', 'IR06A144', '100', '2' ], [ 'Env1', 'IR09N536', '89', '2' ], [ 'Env1', 'IR07A107', '87', '2' ], [ 'Env1', 'IRRI 156', '97', '2' ], [ 'Env1', 'IRRI 123', '93', '1' ], [ 'Env1', 'IRRI 104', '73', '2' ], [ 'Env1', '(BATUR*3/NIL-C443 5)-376', '136', '1' ], [ 'Env1', '(BATUR*3/KASALATH)-1830', '137', '1' ], [ 'Env1', 'IR 64-PUP1-H', '95', '2' ], [ 'Env1', 'IR74', '84', '2' ], [ 'Env1', 'IR64-NIL5', '92', '1' ], [ 'Env1', 'IR 82355-5-1-3', '100', '2' ], [ 'Env1', 'IR08N180', '99', '1' ], [ 'Env1', 'IR12A108', '101', '2' ], [ 'Env1', 'IR12N125', '91', '2' ], [ 'Env1', 'IR09A130', '104', '1' ], [ 'Env1', 'IRRI 146', '88', '1' ], [ 'Env1', 'IRRI 156', '102', '1' ], [ 'Env1', 'SITU BAGENDIT', '95', '2' ], [ 'Env1', 'BATUR', '133', '1' ], [ 'Env1', 'IR 64', '132', '2' ], [ 'Env1', 'IR 74-Pup1-D', '80', '2' ], [ 'Env1', 'IR 74-Pup1-C', '82', '2' ], [ 'Env1', 'IR 74-Pup1-B', '75', '1' ], [ 'Env1', 'IR 74-Pup1-A', '81', '1' ], [ 'Env1', 'IR74', '86', '1' ], [ 'Env1', 'IR 84636-13-12-2-6-3-3-2-2-B', '88', '2' ], [ 'Env1', 'IR10N285', '91', '1' ], [ 'Env1', 'IRRI 168', '95', '2' ], [ 'Env1', 'IR11N121', '99', '2' ] ], 'respvar' : 'HT_CONT', 'genotype' :'Designation', 'rep' : 'Blk' }";
			System.out.println(json);

			Client c = ClientBuilder.newClient();
			WebTarget target= c.target("http://172.29.4.166:8080/WS-RS/rest/SingleTrial/outlier");
			//WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/outlier");
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
