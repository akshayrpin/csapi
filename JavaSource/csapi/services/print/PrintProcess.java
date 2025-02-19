package csapi.services.print;

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
import csapi.impl.print.PrintImpl;


@Path("/print")
public class PrintProcess  {

	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response print(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
		//String output = PrintImpl.print(request,response,json).toString();
		//Logger.line("API OUTPUT", output);
		return Response.status(200).entity(PrintImpl.print(request,response,json)).build();
		
	}
	
	@POST
	@Path("/transaction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response printTransaction(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
//		String output = PrintImpl.printTransaction(request, response, json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(PrintImpl.printTransaction(request, response, json)).build();
		
	}
	

	@POST
	@Path("/gettemplates")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemplates(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
//		String output = PrintImpl.getTemplates(request, response, json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(PrintImpl.getTemplates(request, response, json)).build();
		
	}
	

	@POST
	@Path("/gettemplates/batch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatchTemplates(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
		String output = PrintImpl.getTemplates(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/details/batch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response batch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
		String output = PrintImpl.dotbatch(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/getbatch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT getbatch");
		Logger.line("API INPUT", json);
		String output = PrintImpl.getBatchDetails(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/batchstatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBatchStatus(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PRINT");
		Logger.line("API INPUT", json);
		String output = PrintImpl.getBatchStatus(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}