package csapi.services.project;

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
import csapi.impl.project.ProjectImpl;
import csapi.impl.project.ProjectRunnable;
import csapi.utils.objtools.Modules;

@Path("/project")
public class ProjectProcess  {

	
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
	@Path("/addressProjects")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addressProjects(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.subs(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/panels")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response panels(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.panels(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	

	@POST
	@Path("/childrens")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response childrens(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.childrens(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/refreshsummary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshsummary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.refreshsummary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIST");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response info(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.info(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/refreshinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.refreshinfo(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/tools")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tools(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.tools(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectRunnable.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/type")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response type(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response status(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.status(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/autoactivities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autoactivities(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PROJECT");
		Logger.line("API INPUT", json);
		String output = ProjectImpl.autoActivities(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}




}