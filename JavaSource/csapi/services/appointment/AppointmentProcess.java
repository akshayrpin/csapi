package csapi.services.appointment;

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

@Path("/appointment")
public class AppointmentProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.cancel(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/my")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response my(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.my(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/types")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response types(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/schedulestatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response schedulestatus(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.schedulestatus(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/availability")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response availability(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.availability(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/multiavailability")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response multiavailability(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.multiavailability(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/collaborators")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response collaborators(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.collaborators(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/reviewcollaborators")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reviewcollaborators(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.reviewCollaborators(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response team(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.team(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/notes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.notes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/multiedit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response multiedit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.multiedit(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("/reassign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reassign(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.reassign(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/reschedule")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reschedule(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.reschedule(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/reroute")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reroute(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API APPOINTMENT");
		Logger.line("API INPUT", json);
		String output = AppointmentImpl.reroute(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
}



