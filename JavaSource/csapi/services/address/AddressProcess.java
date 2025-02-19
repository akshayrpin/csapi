package csapi.services.address;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.ws.spi.http.HttpContext;

import org.apache.http.HttpRequest;
import org.json.JSONException;
import org.json.JSONObject;

import csapi.impl.address.AddressImpl;
import alain.core.utils.Cartographer;
import alain.core.utils.Logger;

@Path("/address")
public class AddressProcess  {

	
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/children")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response children(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.children(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/summary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response summary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.summary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	 
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.formdetails(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/details/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsFields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.formdetails(request,response,json,true).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	/*@POST
	@Path("/details/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsEdit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = ProjectImpl.formdetailsActivity(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}*/
	
	@POST
	@Path("/searchAll")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchAll(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = AddressImpl.searchAll(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
}