package csapi.services.divisions;

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
import csapi.impl.divisions.DivisionsImpl;

@Path("/divisions")
public class DivisionsProcess  {

	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsFields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/divisions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response divisions(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.divisions(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updateval")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateval(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DIVISIONS");
		Logger.line("API INPUT", json);
		String output = DivisionsImpl.update(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	




}





