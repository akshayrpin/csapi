package csapi.services.email;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import csapi.impl.activity.ActivityRunnable;
import csapi.impl.email.EmailImpl;
import csapi.impl.email.EmailRunnable;
import csapi.impl.print.PrintImpl;
import alain.core.utils.Logger;


@Path("/email")
public class EmailProcess  {

	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
		//String output = PrintImpl.print(request,response,json).toString();
		//Logger.line("API OUTPUT", output);
		return Response.status(200).entity(EmailImpl.details(request,response,json)).build();
		
	}
	
	
	@POST
	@Path("/send")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response send(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API EMAIL");
		Logger.line("API INPUT", json);
		String output = EmailRunnable.send(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/sendemail")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendOnline(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API EMAIL");
		Logger.line("API INPUT", json);
		String output = EmailImpl.sendEmail(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	
}