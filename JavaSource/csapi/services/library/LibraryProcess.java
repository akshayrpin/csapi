package csapi.services.library;

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
import csapi.impl.attachments.AttachmentsImpl;
import csapi.impl.general.GeneralImpl;
import csapi.impl.library.LibraryImpl;

@Path("/library")
public class LibraryProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/complete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response complete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.complete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/incomplete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response incomplete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.incomplete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/group")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response library(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.group(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/savemultiple")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response savemultiple(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIBRARY");
		Logger.line("API INPUT", json);
		String output = LibraryImpl.saveMulti(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}




}













