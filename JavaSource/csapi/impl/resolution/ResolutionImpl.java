package csapi.impl.resolution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.common.LkupCache;
import csapi.impl.holds.HoldsAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Modules;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.HoldsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResolutionVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.lkup.RolesVO;


public class ResolutionImpl {

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
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			ObjGroupVO g = ResolutionFields.details();

			ResolutionVO r = ResolutionAgent.getResolution(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()), Operator.toInt(vo.getId()));
			ResolutionVO[] rs = new ResolutionVO[1];
			rs[0] = r;
			g.setResolutions(rs);

			boolean dh = Modules.disableOnHold("resolution");
			if (dh) {
				HoldsList hl = HoldsAgent.getActivityHolds(vo.getType(), vo.getTypeid());
				RolesVO role = LkupCache.getModuleRoles("resolution");
				g.putRoles(role, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
			}
			else {
				RolesVO role = LkupCache.getModuleRoles("resolution");
				g.putRoles(role, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}
			if (!g.isRead()) { g = new ObjGroupVO(); }

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

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.delete(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteParentCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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
		r.setMessagecode("cs200");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			r = ResolutionAgent.save(vo, u);
			if (r.isValid()) {
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteParentCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				//r.setMessagecode("cs500");
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

	public static String multiedit(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			if (ResolutionAgent.updateMulti(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteParentCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Database Error");
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

	public static String expire(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			if (ResolutionAgent.expireMulti(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteParentCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Database Error");
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

	public static String comply(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.comply(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String appcomply(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.appcomply(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String complyall(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.complyall(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String appcomplyall(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.appcomplyall(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String uncomply(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.uncomply(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String appuncomply(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			if (ResolutionAgent.appuncomply(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				r.addError("Database Error");
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

	public static String status(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] o = Choices.getChoices(ResolutionSQL.status());
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String importResolution(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			r = ResolutionAgent.importResolution(vo, u);
			if (r.isValid()) {
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "resolution");
				CsDeleteCache.deleteParentCache(vo.getType(), vo.getTypeid(), "resolution");
			}
			else {
				//r.setMessagecode("cs500");
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














}















