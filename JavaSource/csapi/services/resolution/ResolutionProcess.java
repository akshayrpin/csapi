package csapi.services.resolution;

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
import csapi.impl.resolution.ResolutionImpl;

@Path("/resolution")
public class ResolutionProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/multiedit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response multiedit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.multiedit(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/expire")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response expire(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.expire(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/comply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response complete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.comply(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/appcomply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response appcomplete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.appcomply(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/complyall")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response complyall(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.complyall(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/appcomplyall")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response appcomplyall(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.appcomplyall(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/uncomply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response incomplete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.uncomply(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/appuncomply")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response appincomplete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.appuncomply(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rstatus(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.status(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/import")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response importResolution(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API RESOLUTION");
		Logger.line("API INPUT", json);
		String output = ResolutionImpl.importResolution(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}



}













