import java.awt.PageAttributes.MediaType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;


public class TestDownload {

	public static void main(String[] args){
		
	
		Client c = ClientBuilder.newClient();
		WebTarget target= c.target("http://localhost:8080/WS-RS/rest/SingleTrial/getImageByte/study5/plot.png");
//		ByteArrayInputStream r = target.request().get(ByteArrayInputStream.class);
		Response response = target.request().get();
		 
		ByteArrayInputStream entity=response.readEntity(ByteArrayInputStream.class);
		
		
		
		
		
		try{

			/*			BufferedImage originalImage = 
					ImageIO.read(new File("e:\\plot.png"));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			System.out.println(imageInByte);
			baos.close();
			 */

			// write image		
/*			InputStream in = r; //new ByteArrayInputStream(imagebyte);
			BufferedImage bImageFromConvert = ImageIO.read(in);

			ImageIO.write(bImageFromConvert, "png", new File(
					"e:/plot6.jpg"));*/

		}catch(Exception e){
			System.out.println(e.getMessage());
		}		
	}	


}
