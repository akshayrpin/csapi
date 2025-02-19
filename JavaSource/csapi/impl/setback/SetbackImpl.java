package csapi.impl.setback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.general.GeneralAgent;
import csapi.impl.sitedata.SitedataAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class SetbackImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			
			
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			v.setId(id);
			v.setTypeid(vo.getTypeid());
			
			ObjGroupVO g = SetbackAgent.setbackfields(vo.getTypeid(), id);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			//gs[1] = SetbackAgent.details(vo.getTypeid());
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
			ObjGroupVO g = SetbackAgent.setbackdetails(id);
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

	
	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			if(Operator.hasValue(u.getMessage())){
				r.setMessagecode(u.getMessage());
			}
			if(Operator.equalsIgnoreCase(u.getMessage(), "cs402")){
				r.setRedirect(true);
			}
			
			//Check for staff 
			//if(!u.isStaff()){
			//	r.setMessagecode("cs400");
			//}
			
			ObjGroupVO v = vo.getData()[0];
			r = saveCommon(vo, u);
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
			}
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	
	public static ResponseVO saveCommon(RequestVO vo, Token u){
		ResponseVO result = new ResponseVO();
		try{
			ObjGroupVO o = vo.getData()[0];
			result = ValidateRequest.processGeneral(vo);
			
			if(result.isValid()){
				boolean action = SetbackAgent.saveSetback(vo,u);
				if(action){
					result.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "setback");
				}else {
					result.setMessagecode("cs500");
				}
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			result.setMessagecode("cs500");
		}
		return result;
	}

}















