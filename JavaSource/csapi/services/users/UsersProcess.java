package csapi.services.users;

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
import csapi.impl.appointment.AppointmentImpl;
import csapi.impl.users.UsersImpl;

@Path("/users")

public class UsersProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.save(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/saveprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveprofile(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.saveProfile(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	public Response login(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.login(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/loginoauth")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	public Response loginoauth(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.loginOauth(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.logout(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/gettoken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response gettoken(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.getToken(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/createtoken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createtoken(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.createToken(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	//Using for oauth
	@POST
	@Path("/generatetoken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generatetoken(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.generateToken(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/onlineuser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onlineUser(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.onlineUser(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/usernameonlineaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response usernameonlineaccount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.usernameonlineaccount(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/refuseronlineaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refuseronlineaccount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.refuseronlineaccount(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/useronlineaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response useronlineaccount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.useronlineaccount(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/username")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response username(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.username(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	
	@POST
	@Path("/email")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response email(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.email(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/select")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response select(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.select(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}

	@POST
	@Path("/peopletype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response peopletype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.peopletype(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
	}
	


	@POST
	@Path("/teamprofile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response teamprofile(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.saveTeamProfile(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/notes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.notes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	@POST
	@Path("/holds")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response holds(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.holds(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	@POST
	@Path("/refuser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refuser(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API USERS");
		Logger.line("API INPUT", json);
		String output = UsersImpl.refUserDetails(request, response, json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
}
