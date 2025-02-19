package csapi.services.finance;

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
import csapi.impl.finance.FinanceImpl;
import csapi.impl.general.GeneralImpl;
import csapi.impl.holds.HoldsImpl;
import csapi.impl.lso.LsoImpl;
import csapi.impl.review.ReviewImpl;

@Path("/finance")
public class FinanceProcess  {

	@POST
	@Path("/fees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.fees(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/feespick")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response feespick(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.feespick(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/calculate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.calculate(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.save(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/browse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response browse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.browse(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/cashier/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cashierSearch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.cashierSearch(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/cart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.cart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/paymentdetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response paymentdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.paymentdetails(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/pay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pay(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.pay(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/paymentlist")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response paymentlist(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.paymentlist(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/reverse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reverse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.reverse(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/showstatementpayment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response showstatementpayment(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.showstatementpayment(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/partialreverse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response partialreverse(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.partialreverse(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/showledger")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response showledger(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.showledger(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/savecart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response savecart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.savecart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updatecart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatecart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.updatecart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/getcart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getcart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.getcart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/scantocart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response scantocart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = ""; //FinanceImpl.getcart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/deletecart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletecart(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.deletecart(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/payonline")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response payonline(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.payonline(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/mypayments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response mypayments(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.myPayments(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/deletefee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletefee(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.deletefee(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/onlinepay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onlinepay(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.onlinepay(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updateonlinepay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateonlinepay(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.updateonlinepay(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/getmanualaccounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getmanualaccounts(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.getManualAccounts(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/extractfinancerecords")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response extractfinancerecords(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.extractfinancerecords(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/statementdetail")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response statementdetail(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.statementdetail(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updatestatementdetailfinance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatestatementdetailfinance(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.updatestatementdetailfinance(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/currentvaluation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response currentvaluation(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.currentvaluation(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/updatecurrentvaluation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatecurrentvaluation(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		String output = FinanceImpl.updatecurrentvaluation(request,response,json).toString();
		return Response.status(200).entity(output).build();
		
	}
	
}