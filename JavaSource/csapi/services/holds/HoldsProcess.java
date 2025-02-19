package csapi.services.holds;

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
import csapi.impl.general.GeneralImpl;
import csapi.impl.holds.HoldsImpl;
import csapi.impl.notes.NotesImpl;
import csapi.impl.people.PeopleImpl;
import csapi.impl.review.ReviewImpl;
import csapi.impl.users.UsersImpl;

@Path("/holds")
public class HoldsProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API HOLDS");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API HOLDS");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API HOLDS");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API HOLDS");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	@POST
	@Path("/types")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response types(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API Holds");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/saveuserholds")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveuserholds(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API HOLDS");
		Logger.line("API INPUT", json);
		String output = HoldsImpl.saveuserholds(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
}



