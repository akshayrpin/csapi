package csapi.services.ui;

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
import csapi.impl.ui.UiImpl;

@Path("/ui")
public class UiProcess  {

	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API UI");
		Logger.line("API INPUT", json);
		String output = UiImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/info")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response info(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API UI");
		Logger.line("API INPUT", json);
		String output = UiImpl.info(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/history")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response history(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API UI");
		Logger.line("API INPUT", json);
		String output = UiImpl.history(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/id")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response id(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API UI");
		Logger.line("API INPUT", json);
		String output = UiImpl.id(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/tabaccess")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tabaccess(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API UI");
		Logger.line("API INPUT", json);
		String output = UiImpl.tabaccess(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}




}















