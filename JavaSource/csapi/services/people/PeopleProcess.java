package csapi.services.people;

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
import csapi.impl.people.PeopleImpl;
import csapi.impl.review.ReviewImpl;

@Path("/people")
public class PeopleProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/fields/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchfields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.searchfields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.browse(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/primary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response primary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.setPrimary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/unprimary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unprimary(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.unPrimary(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/select")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response select(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.select(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.update(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/email")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response email(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.email(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/username")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response username(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.username(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/license")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response license(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PEOPLE");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.license(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/peopleusers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response peopleusers(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API REVIEW");
		Logger.line("API INPUT", json);
		String output = PeopleImpl.peopleusers(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
}



