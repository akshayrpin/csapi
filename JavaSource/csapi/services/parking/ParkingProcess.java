package csapi.services.parking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import alain.core.utils.Logger;
import csapi.impl.activity.ActivityImpl;
import csapi.impl.parking.ParkingImpl;

@Path("/parking")
public class ParkingProcess  {

	
	
/*	@POST
	@Path("/getsearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getsearch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LSO SEARCH");
		Logger.line("API INPUT", json);
		String output = ExemptionImpl.getsearch(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}*/
	
	
	
	@POST
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = ParkingImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@GET
	@Path("/refreshsearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshsearch(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.line("API INPUT", json);
		String output = ParkingImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API DETAILS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsFields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API FIELDS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/addlfields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response formdetailsAddlFields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ADDLFIELDS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.addlFields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/tools")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tools(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API TOOLS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.tools(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API TOOLS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("saveparkingactivity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveParkingPermit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.saveParkingActivity(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listexemptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExemptions(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIST EXEMPTIONS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listExemptions(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("listparkingpermits")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getParkingPermits(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIST PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listParkingPermits(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listall")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ALL");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.search(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}



	@POST
	@Path("listparkingaccounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listParkingAccounts(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING ACCOUNTS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listParkingAccounts(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listexemptiontypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listExemptionTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API EXEMPTION TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listExemptionTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listpermittypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listPermitTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PERMIT TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listPermitTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("listlastyeartypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listLastYearTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LAST YEAR TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listLastYearTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("lastyearpermitcount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response processPermitCount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LAST YEAR TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.processPermitCount(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("renewalcount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response renewalCount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LAST YEAR TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.renewalCount(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/parkingconfig")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response parkingConfig(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING CONFIG");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.parkingConfig(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/saveaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveParkingAccount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API SAVE ACCOUNT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.saveParkingAccount(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	
	@POST
	@Path("/permittype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response permittype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PERMIT TYPE");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.permitType(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/exemptiontype")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exemptiontype(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API EXEMPTION TYPE");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.exemptionType(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	
	@POST
	@Path("/onlineprints")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onlineprints(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.onlinePrints(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listtempexemptiontypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listTempExemptionTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API EXEMPTION TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listTempExemptionTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("savetempparkingactivity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveTempParkingPermit(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.saveTempParkingActivity(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("attachment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAttachments(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.getAttachment(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("deleteattachment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAttachments(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.deleteAttachment(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/parkingapproval")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response parkingApproval(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.parkingApproval(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/parkingapprovalcount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response parkingApprovalCount(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.parkingApprovalCount(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/approve")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response approve(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.approve(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/unapprove")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unapprove(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.unapprove(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/merge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response merge(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ONLINE PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.merge(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("listrenewalpermits")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRenewalPermits(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API LIST PERMITS");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listRenewalPermits(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("listrenewalpermittypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRenewalPermitTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PERMIT TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.listRenewalPermitTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("deletedetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDetails(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.deleteDetails(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

	@POST
	@Path("getparkingdates")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getParkingDates(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PARKING PERMIT");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.getParkingDates(request,response,json);
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("getrenewaltypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRenewalTypes(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API PERMIT TYPES");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.getRenewalTypes(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/myactive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response myactive(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ACTIVITY");
		Logger.line("API INPUT", json);
		String output = ParkingImpl.myActive(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	

}

