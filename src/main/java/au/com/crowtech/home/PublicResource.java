package au.com.crowtech.home;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Path("/api/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicResource {

	private static final Logger log = Logger.getLogger(PublicResource.class);
	
	 public static final int PORT = 7;   

	@OPTIONS
	public Response opt() {
		return Response.ok().build();
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String echo(String requestBody) throws Exception {
		return requestBody;
	}

	

	@GET
	@Path("/test")
	public Response get() {

		log.info("System TEST received!!");
		String mask = "192.168.86.255";
		String mac = "44:5C:E9:2F:0A:C7";
		
		wakeonlan(mask,mac);
		return Response.status(Status.OK).build();
	}

	

	@Transactional
	void onStart(@Observes StartupEvent ev) {
		log.info("Home Endpoint starting");

	}

	@Transactional
	void onShutdown(@Observes ShutdownEvent ev) {
		log.info("Home Endpoint Shutting down");
	}
	
 
	    
	    public static void wakeonlan(String ipStr, String macStr) {
	        
	        
	        try {
	            byte[] macBytes = getMacBytes(macStr);
	            byte[] bytes = new byte[6 + 16 * macBytes.length];
	            for (int i = 0; i < 6; i++) {
	                bytes[i] = (byte) 0xff;
	            }
	            for (int i = 6; i < bytes.length; i += macBytes.length) {
	                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
	            }
	            
	            InetAddress address = InetAddress.getByName(ipStr);
	            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
	            DatagramSocket socket = new DatagramSocket();
	            socket.send(packet);
	            socket.close();
	            
	            System.out.println("Wake-on-LAN packet sent.");
	        }
	        catch (Exception e) {
	            System.out.println("Failed to send Wake-on-LAN packet: + e");
	            System.exit(1);
	        }
	        
	    }
	    
	    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
	        byte[] bytes = new byte[6];
	        String[] hex = macStr.split("(\\:|\\-)");
	        if (hex.length != 6) {
	            throw new IllegalArgumentException("Invalid MAC address.");
	        }
	        try {
	            for (int i = 0; i < 6; i++) {
	                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
	            }
	        }
	        catch (NumberFormatException e) {
	            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
	        }
	        return bytes;
	    }
}
