package csapi.impl.divisions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.entity.EntityAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Modules;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DivisionsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeInfo;
import csshared.vo.TypeVO;


public class DivisionsImpl {

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
			TypeVO v = Types.getType(vo);
			ObjGroupVO g = DivisionsAgent.details(vo.getType(), vo.getTypeid());
			ObjGroupVO[] gs = { g };
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String divisions(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		return divisions(vo.getType(), vo.getTypeid());
	}
	
	public static String divisions(String type, int typeid) {
		String s = "";
		try {
			//TODO secure
			ObjectMapper mapper = new ObjectMapper();
			DivisionsList l = DivisionsAgent.getDivisions(type, typeid, true);
			s = mapper.writeValueAsString(l);
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
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();

			TypeVO v = Types.getType(vo);
			v.setTools(Modules.tools(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), v.getHold(), u));
			ObjGroupVO g = DivisionsAgent.details(vo.getType(), vo.getTypeid());
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String update(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO res = DivisionsAgent.update(vo);
		ObjectMapper mapper = new ObjectMapper();
		String s = "";
		try {
			s = mapper.writeValueAsString(res);
		}
		catch (Exception e) {
			
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

			if(v.getGroup().equalsIgnoreCase("DIVISIONS")){
				r = saveDivision(vo, u);
			} else {
				r.setMessagecode("cs500");
			}
			
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

	public static ResponseVO saveDivision(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try{
//			ObjGroupVO o = vo.getData()[0];
			if(r.isValid()){
//				TypeInfo entity = EntityAgent.getEntity(vo.getType(), vo.getTypeid());
//				String type = entity.getEntity();
//				int typeid = entity.getLowestEntity();
				ObjGroupVO o = vo.getData()[0];
				boolean result = DivisionsAgent.saveDivision(vo.getType(), vo.getTypeid(), o, u);
				if(result){
					r.setMessagecode("cs200");
					CsDeleteCache.deleteCacheDir(vo.getType(), vo.getTypeid());
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Error occured while saving divisions");
				}
			}
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			r.addError("Exception error occured while saving divisions");
		}
		return r;
	}


}















