package csapi.services.inspections;

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
import csapi.impl.inspections.InspectionsImpl;

@Path("/inspections")
public class InspectionsProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/active")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response active(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/my")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response my(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.my(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/myactive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response myactive(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.myActive(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/inspectiondetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response inspectiondetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.inspectionDetails(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/full")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response full(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.full(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/types")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response request(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/availability")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response availability(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.availability(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/month")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response month(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.month(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/day")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response day(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.day(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response team(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.team(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.cancel(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/activities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activities(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.inspectableActivities(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


	@POST
	@Path("/statistics")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response statistics(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API INSPECTIONS");
		Logger.line("API INPUT", json);
		String output = InspectionsImpl.statistics(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

}