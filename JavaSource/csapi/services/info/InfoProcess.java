package csapi.services.info;

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
import csapi.impl.info.InfoImpl;

@Path("/info")
public class InfoProcess  {

	@POST
	@Path("/version")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response version(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INFO");
		Logger.line("API INPUT", json);
		String output = InfoImpl.version(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/content")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response content(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INFO");
		Logger.line("API INPUT", json);
		String output = InfoImpl.content(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}




}















