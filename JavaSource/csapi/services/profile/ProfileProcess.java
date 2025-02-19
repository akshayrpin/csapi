package csapi.services.profile;

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
import csapi.impl.profile.ProfileImpl;

@Path("/profile")

public class ProfileProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/permit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response permit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.permit(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/parkingaddl")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response parkingaddl(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.parkingAddl(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/addaddl")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addaddl(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.addlFields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/addlfields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addlfields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.addlFields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/saveaddl")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveaddl(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROFILE");
		Logger.line("API INPUT", json);
		String output = ProfileImpl.saveParkingAddl(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	


	
	
	
}

