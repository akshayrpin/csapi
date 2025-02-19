package csapi.services.deposit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import csapi.impl.deposit.DepositImpl;

@Path("/deposit")
public class DepositProcess  {

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.depositadd(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.saveDeposit(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/depositlist")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response depositlist(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.depositlist(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/depositpayees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response depositpayees(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.depositpayees(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/showdepositledger")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response showdepositledger(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.showdepositledger(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/showdepositoptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response showdepositoptions(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = DepositImpl.showdepositoptions(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
}