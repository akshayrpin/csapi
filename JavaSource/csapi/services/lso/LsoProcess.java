package csapi.services.lso;

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
import csapi.impl.lso.LsoImpl;
import csapi.utils.objtools.Modules;

@Path("/lso")
public class LsoProcess  {


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
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO SEARCH");
		Logger.line("API INPUT", json);
		String output = LsoImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/panels")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response panels(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO SEARCH");
		Logger.line("API INPUT", json);
		String output = LsoImpl.panels(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/children")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response children(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO BROWSE");
		Logger.line("API INPUT", json);
		String output = LsoImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	 
	@POST
	@Path("/refreshsummary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshsummary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.refreshsummary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	 
	@POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response info(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.info(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	 
	@POST
	@Path("/refreshinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.refreshinfo(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	 
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsFields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/tools")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tools(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.tools(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsEdit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/searchAll")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAll(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.searchAll(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/psearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response psearch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO");
		Logger.line("API INPUT", json);
		String output = LsoImpl.psearch(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	





















}

