package csapi.services.admin;

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
import csapi.impl.admin.AdminImpl;

@Path("/admin")
public class AdminProcess  {

	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ADMIN");
		Logger.line("API INPUT", json);
		String output = AdminImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/adminaccess")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response adminaccess(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ADMIN");
		Logger.line("API INPUT", json);
		String output = AdminImpl.adminaccess(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


}