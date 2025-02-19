package csapi.services.attachments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import csapi.impl.attachments.AttachmentsAgent;
import csapi.impl.attachments.AttachmentsImpl;
import csapi.security.AuthorizeToken;
import csshared.utils.ObjMapper;
import csshared.vo.FileVO;
import csshared.vo.RequestVO;

@Path("/attachments")
public class AttachmentsProcess  {

	@POST
	@Path("/fields")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fields(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.fields(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response details(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.details(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.save(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.delete(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}

	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.list(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	@POST
	@Path("/types")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response types(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.types(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}
	
	/*@GET
	@Path("/get")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		String output = AttachmentsImpl.get(request,response,json).toString();
		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(output).build();
		
	}*/
	
	
	@POST
	@Path("/fileinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fileinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.fileinfo(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.fileinfo(request,response,json)).build();
		
	}

	@POST
	@Path("/view")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response view(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.view(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.view(request,response,json)).build();
	}

	@POST
	@Path("/thumb")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response thumb(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.view(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.thumb(request,response,json)).build();
	}

	@POST
	@Path("/slide")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response slide(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.view(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.slide(request,response,json)).build();
	}

	@POST
	@Path("/pic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response pic(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.view(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.pic(request,response,json)).build();
	}

	@POST
	@Path("/rotate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rotate(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.view(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.rotatepic(request,response,json)).build();
	}

	@POST
	@Path("/viewer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response viewer(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		String file ="";
		String ct = "";
		try{
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			FileVO fvo = AttachmentsAgent.getAttachment(vo);
			
			String fileName = fvo.getPath();
			
			file = fvo.getFilename()+"."+fvo.getExtension();
			ct = fvo.getContenttype();
			
			String filepath = Config.getString("files.storage_path")+""+fileName;
			
			File f = new File(filepath);
			 Logger.info(filepath+"f.exists()::"+f.exists());
			if ( f.exists() ) {
			    RandomAccessFile raf = new RandomAccessFile( f, "r" );
	            response.setContentLength( (int) raf.length() );
	            //out = response.getOutputStream();
	            byte[] loader= new byte [ (int) raf.length() ];
	            while ( (raf.read( loader )) > 0 ) {
	                  o.write( loader );
	            }
	            b = o.toByteArray();
	            
	           
			}
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			return Response.status(500).entity(b).header("Content-Disposition", "attachment; filename=" + "nofile.txt").build();
		}
		return Response.status(200).entity(b).header("Content-Type", ct).header("Content-Disposition", "attachment; filename=" + file).build();
		
	}
	
	
	@POST
	@Path("/fileinfo/batch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response batchfileinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.fileinfo(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.fileinfo(request,response,json)).build();
		
	}
	
	
	@POST
	@Path("/fileinfo/online")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onlinefileinfo(@Context HttpServletRequest request,@Context HttpServletResponse response, String json) {
		Logger.logmethod("API ATTACHMENTS");
		Logger.line("API INPUT", json);
//		String output = AttachmentsImpl.fileinfo(request,response,json).toString();
//		Logger.line("API OUTPUT", output);
		return Response.status(200).entity(AttachmentsImpl.fileinfo(request,response,json)).build();
		
	}
}















