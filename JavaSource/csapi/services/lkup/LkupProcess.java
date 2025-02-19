package csapi.services.lkup;

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
import csapi.impl.lkup.LkupImpl;

@Path("/lkup")
public class LkupProcess  {

	
	@POST
	@Path("/type")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response type(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.type(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/typedescriptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response typedescriptions(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.typedescriptions(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


	@POST
	@Path("/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response status(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.status(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/statusdescriptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response statusdescriptions(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.statusdescriptions(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/group")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response group(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.group(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/usertype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response usertype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.usertype(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


	@POST
	@Path("/strmod")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response strmod(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.strmod(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}


	@POST
	@Path("/streets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response streets(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LKUP");
		Logger.line("API INPUT", json);
		String output = LkupImpl.streets(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}





















}

