package csapi.services.lockbox;

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
import csapi.impl.lockbox.LockboxImpl;
import csapi.impl.lockbox.LockboxRunnable;

@Path("/lockbox")
public class LockboxProcess  {

	
	
	@POST
	@Path("/process")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response lockbox(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxRunnable.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response lockboxadd(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxRunnable.add(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response lockboxupdate(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxRunnable.update(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("exception")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response lockboxexception(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxImpl.exception(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response lockboxedit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxImpl.edit(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("manualprocess")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response manualprocess(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LOCKBOX ");
		Logger.line("API INPUT", json);
		String output = LockboxImpl.manualprocess(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}





