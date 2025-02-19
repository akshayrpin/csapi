package csapi.impl.sitedata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.entity.EntityAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeInfo;
import csshared.vo.TypeVO;


public class SitedataImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			
			ObjGroupVO g = SitedataAgent.fields(vo.getType(), vo.getTypeid());
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
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
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			
			ObjGroupVO g = SitedataAgent.details(vo.getType(), vo.getTypeid(),  id);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			//gs[1] = SetbackAgent.details(vo.getTypeid());
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	
	public static String combineddetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			
			ObjGroupVO g = SitedataAgent.details(vo.getType(), vo.getTypeid(),  id);
			ObjGroupVO[] gs = new ObjGroupVO[2];
			gs[0] = g;
			gs[1] = SitedataAgent.setbackdetails(id);
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
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
			ObjGroupVO v = vo.getData()[0];
			r = saveCustom(vo, u);
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
			}
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}


	public static ResponseVO saveCustom(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try {
			TypeInfo entity = EntityAgent.getEntity(vo.getType(), vo.getTypeid());
			ObjGroupVO o = vo.getData()[0];
			//TODO
			r = ValidateRequest.processCustom(Operator.toInt(o.getGroupid()), o.objValues());
			boolean result = SitedataAgent.saveSitedata(Operator.toInt(vo.getId()), entity.getEntityid(), o, u);
			if(result) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "sitedata");
			}
			else {
				r.setMessagecode("cs500");
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
	}












}















