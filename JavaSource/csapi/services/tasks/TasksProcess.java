package csapi.services.tasks;

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
import csapi.impl.tasks.TasksRunnable;

@Path("/tasks")
public class TasksProcess  {

	@POST
	@Path("/run")
	
	public Response notification() {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", "RUN");
		String output = TasksRunnable.run().toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/immediate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifications(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", json);
		String output = TasksRunnable.runImmediate(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/autopenalty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autopenalty(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", "RUN");
		String output = TasksRunnable.runAutoPenalty().toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/emailexpiredpermits")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response emailexpiredpermits(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", "RUN");
		String output = TasksRunnable.emailExpiredPermits().toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/expirepermits")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response expirePermits(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API COMMUNICATIONS");
		Logger.line("API INPUT", "RUN");
		String output = TasksRunnable.updateActivity().toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}













