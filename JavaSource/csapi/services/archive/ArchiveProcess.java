package csapi.services.archive;

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
import csapi.impl.archive.ArchiveImpl;


@Path("/archive")
public class ArchiveProcess  {

	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API CUSTOM");
		Logger.line("API INPUT", json);
		String output = ArchiveImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API CUSTOM");
		Logger.line("API INPUT", json);
		String output = ArchiveImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
}