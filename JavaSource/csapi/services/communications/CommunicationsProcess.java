package csapi.services.communications;

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
import csapi.impl.communications.CommunicationsImpl;
import csapi.impl.people.PeopleImpl;

@Path("/communications")
public class CommunicationsProcess  {

	@POST
	@Path("/notification")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notification(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", json);
		String output = CommunicationsImpl.notification(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/notifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifications(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", json);
		String output = CommunicationsImpl.notifications(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/recipients")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response peopleusers(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = CommunicationsImpl.recipients(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/peopletypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response peopletypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = CommunicationsImpl.peopleTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}




}













