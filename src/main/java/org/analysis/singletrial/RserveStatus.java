package org.analysis.singletrial;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

@Path("/Rserve")
public class RserveStatus {
	
	private RConnection rConnection;

	@GET
	@Path("status/")
	@Produces("text/plain")
	public String getRserveStatus() {
		String msg="";
		try {
			rConnection	= new RConnection();
			msg="Connected";
			return msg;
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			msg="Not Connected";
			e.printStackTrace();
			return msg;
		}
	}
}
