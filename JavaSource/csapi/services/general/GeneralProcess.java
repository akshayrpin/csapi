package csapi.services.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import alain.core.utils.Logger;
import csapi.impl.general.GeneralImpl;

@Path("/general")
public class GeneralProcess  {

	
	@POST
	@Path("/choices")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.choicesUrl(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/choicescommand")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response choicescommand(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.choicesCommand(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@GET
	@Path("/getstreetlist")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStreetList(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getStreetList(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/getstreetlist")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStreetListPOST(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getStreetList(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@GET
	@Path("/getstreetfraction")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStreetFraction(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getStreetFraction(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/getstreetfraction")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStreetFractionPOST(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getStreetFraction(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@GET
	@Path("/getdepartments")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getDepartments(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getDepartments(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	/*@POST
	@Path("/globalSearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response globalSearch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.globalSearch(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}*/

	@POST
	@Path("/getuserstype")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUsersType(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getUsersType(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/getattachmentstype")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAttachmentsType(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getAttachmentsType(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@GET
	@Path("/getapplicationstatus")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getApplicationStatus(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API GENERAL");
		Logger.line("API INPUT", json);
		String output = GeneralImpl.getApplicationStatus(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}





