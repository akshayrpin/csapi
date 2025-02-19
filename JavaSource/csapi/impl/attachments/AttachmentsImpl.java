package csapi.impl.attachments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.ImageUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralImpl;
import csapi.impl.parking.ParkingAgent;
import csapi.impl.print.PrintAgent;
import csapi.impl.review.ReviewOptSQL;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.PrintPDF;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.FileVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class AttachmentsImpl {

	public static int thumbwidth = 125;
	public static int thumbheight = 125;
	public static int slidewidth = 300;
	public static int slideheight = 300;
	public static int picwidth = 800;
	public static int picheight = 800;

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { e.printStackTrace(); }
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getDetails(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		return GeneralImpl.delete(request, response, json);
	}

	public static String list(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getList(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			r = saveAttachments(vo, u);
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
			}
			GlobalSearch.index(GlobalSearch.ATTACHMENTS_DELTA);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static ResponseVO saveAttachments(RequestVO vo, Token u){
		ResponseVO result = new ResponseVO();
		if (result.isValid()) {
			boolean action = AttachmentsAgent.saveAttachments(vo, u);
			if (action) {
				result.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "attachments");
			}
			else {
				result.setMessagecode("cs500");
				result.addError("Request could not be saved to the database.");;
			}
		}
		return result;
	}

	public static String types(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] o = Choices.getChoices(AttachmentsSQL.getTypes());
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String fileinfo(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO v = new ResponseVO();
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			
			
			v= AttachmentsAgent.getAttachmentById(vo);
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(v);
			
			
			
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
			v.setMessagecode("cs500");
			
		}
		return s;
	}
	
	public static byte[] view(HttpServletRequest request, HttpServletResponse response, String json) {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			
			FileVO fvo = new FileVO();
			if(Operator.hasValue(vo.getReference())){
				String fileName = vo.getReference();
				String filepath = Config.getString("files.storage_path")+""+fileName;
				Logger.highlight(filepath);
				fvo = AttachmentsAgent.getAttachment(id);
			
				fvo.setFullpath(filepath);
			}else{
				fvo = AttachmentsAgent.getAttachment(id);

			}
			String fullpath = fvo.getFullpath();
			
			
			if (fvo.isPublic() || t.isStaff()) {
				if (Operator.hasValue(fullpath)) {
					File f = new File(fullpath);
					if (f.exists() ) {
						Logger.info(" doc "+fullpath);
						if(fullpath.endsWith(".doc")){
							Logger.info("came here doc "+fullpath);
						}
						RandomAccessFile raf = new RandomAccessFile(f, "r");
						response.setContentLength((int) raf.length());
			            //out = response.getOutputStream();
						byte[] loader= new byte [ (int) raf.length() ];
						while ((raf.read( loader )) > 0) {
							o.write(loader);
						}
						b = o.toByteArray();
						raf.close();
					}
				}
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] thumb(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				b = ImageUtil.getBytes(fullpath, thumbwidth, thumbheight);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] slide(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				b = ImageUtil.getBytes(fullpath, slidewidth, slideheight);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] pic(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				b = ImageUtil.getBytes(fullpath, picwidth, picheight);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] rotatethumb(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				ImageUtil.rotateBytes(fullpath, slidewidth, slideheight);
				ImageUtil.rotateBytes(fullpath, picwidth, picheight);
				b = ImageUtil.rotateBytes(fullpath, thumbwidth, thumbheight);
				ImageUtil.rotateBytes(fullpath, -1, -1);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] rotateslide(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				ImageUtil.rotateBytes(fullpath, thumbwidth, thumbheight);
				ImageUtil.rotateBytes(fullpath, picwidth, picheight);
				b = ImageUtil.rotateBytes(fullpath, slidewidth, slideheight);
				ImageUtil.rotateBytes(fullpath, -1, -1);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static byte[] rotatepic(HttpServletRequest request, HttpServletResponse response, String json) {
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);	
			int id = Operator.toInt(vo.getId());
			FileVO fvo = AttachmentsAgent.getAttachment(id);
			if (fvo.isPublic() || t.isStaff()) {
				String fullpath = fvo.getFullpath();
				ImageUtil.rotateBytes(fullpath, thumbwidth, thumbheight);
				ImageUtil.rotateBytes(fullpath, slidewidth, slideheight);
				b = ImageUtil.rotateBytes(fullpath, picwidth, picheight);
				ImageUtil.rotateBytes(fullpath, -1, -1);
			}
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return b;
	}

}















