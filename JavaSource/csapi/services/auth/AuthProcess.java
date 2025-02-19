package csapi.services.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import alain.core.utils.Logger;
import csapi.impl.attachments.AttachmentsImpl;
import csapi.impl.auth.AuthImpl;

@Path("/auth")
public class AuthProcess  {

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API AUTH");
		Logger.line("API INPUT", json);
		String output = AuthImpl.login(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/token")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response token(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API AUTH");
		Logger.line("API INPUT", json);
		String output = AuthImpl.token(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/loginoauth")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginOauth(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API AUTH");
		Logger.line("API INPUT", json);
		String output = AuthImpl.loginOauth(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


}















