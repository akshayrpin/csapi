package csapi.services.activity;

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
import csapi.impl.activity.ActivityImpl;
import csapi.impl.activity.ActivityRunnable;
import csapi.utils.objtools.Modules;

@Path("/activity")
public class ActivityProcess  {

	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/panels")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response panels(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.panels(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/modules")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modules(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = Modules.modules(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/refreshmodules")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshmodules(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = Modules.refreshmodules(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/refreshsummary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshsummary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.refreshsummary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response info(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.info(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/refreshinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.refreshinfo(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/tools")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tools(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.tools(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityRunnable.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/my")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response my(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.my(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/myactive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response myactive(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.myActive(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updateval")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateVal(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.updateVal(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/permitdetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response permitDetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.permitDetails(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response status(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.status(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/type")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response type(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.actType(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/statusdefaultissued")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response statusDefaultIssued(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = ActivityImpl.statusDefaultIssued(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/addpermit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPermit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = ActivityImpl.addPermit(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/significantHold")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHold(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = ActivityImpl.significantHold(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/permitinfodetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response permitInfoDetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ActivityImpl.permitInfoDetails(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}





