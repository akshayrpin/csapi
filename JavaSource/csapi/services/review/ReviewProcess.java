package csapi.services.review;

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
import csapi.impl.review.ReviewImpl;

@Path("/review")
public class ReviewProcess  {

	
	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response info(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.info(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.add(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.create(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.update(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/reviewstatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reviewstatus(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.reviewstatus(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/reviewtype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reviewtype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.reviewtype(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/types")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response types(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/appttype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response appttype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.reviewappttype(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/reviewusers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reviewusers(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.reviewusers(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response team(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.team(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/saveteam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveteam(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.updateTeam(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/savedue")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response savedue(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.saveDue(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/comboteam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response comboteam(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.comboTeam(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/inspectors")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response inspectors(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.inspectors(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/appt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response appt(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.appt(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/due")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response due(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ""; //ReviewImpl.appt(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/notifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifications(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.notifications(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/notification")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notification(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.notification(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/cycledetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cycledetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API CYCLE REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.cycledetails(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	@POST
	@Path("/addteam")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addteam(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = ReviewImpl.addTeam(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
}