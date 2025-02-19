package csapi.services.notes;

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
import csapi.impl.appointment.AppointmentImpl;
import csapi.impl.general.GeneralImpl;
import csapi.impl.notes.NotesImpl;
import csapi.impl.people.PeopleImpl;
import csapi.impl.users.UsersImpl;

@Path("/notes")
public class NotesProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	


	@POST
	@Path("/saveusernotes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveusernotes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USER NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.saveusernotes(request, response, json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	@POST
	@Path("/deletenotes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNotes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API Delete NOTES");
		Logger.line("API INPUT", json);
		String output = NotesImpl.deleteNotes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

}
