package csapi.impl.parking;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.general.GeneralAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;

public class ParkingImpl {

	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getSummary(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String info(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
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
			if (ParkingAgent.update(vo, u)) {
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "parking");
			}
			else {
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

	public static String addlFields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getAddlFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	

	public static String tools(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ToolsVO v = Tools.getTools(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String search(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.search(vo); 
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}

	
	
	public static String getRemoteIp(HttpServletRequest request) {
		String result = "";
		try {
			result = request.getHeader("x-forwarded-for");
			if (!Operator.hasValue(result)) { result = request.getHeader("X-FORWARDED-FOR"); }
			if (!Operator.hasValue(result)) { result = request.getHeader("X-Forwarded-For"); }
			if (!Operator.hasValue(result)) { result = request.getRemoteAddr(); }
			if (!Operator.hasValue(result)) { result = ""; }
		}
		catch (Exception e) { result = ""; }
		return result;
	}
	

	public static String listExemptions(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.search(vo,false,true,false); 
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String listParkingPermits(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.search(vo,false,false,true); 
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String listParkingAccounts(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO r = new ResponseVO();
			ObjectMapper mapper = new ObjectMapper();
			Logger.highlight(u.getId());
			if(u.getId() != -1){
				r = ParkingAgent.getParkingAccounts(vo,u.getId());
				s = mapper.writeValueAsString(r);
			}
			else {
				if (Operator.hasValue(u.getUsername())) {
					r.setMessagecode("cs411");
					MessageVO m = new MessageVO();
					m.setMessage("We do not have your profile information.");
					ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
					mv.add(m);
					r.setErrors(mv);
				}
				else {
					r.setMessagecode("cs400");
					MessageVO m = new MessageVO();
					m.setMessage("Your current login session needs to be renewed. Please logout and login again.");
					ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
					mv.add(m);
					r.setErrors(mv);
				}
				s=mapper.writeValueAsString(r);
			}
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	
	/**
	 * Listing all the online Exemptions based on the Zones the user is
	 * requesting for. If the residents are eligible to purchase daytime
	 * permits, and purchase 2 or less, online user should not be able to obtain
	 * more than 5 exemptions in a parking permit year (Oct. 1st – Sept. 30th.)
	 * If the residents that live in a preferential zone purchase all 3 PPP
	 * permits, they can get unlimited daytime exemptions. If the residents are
	 * in a non PPP zone they can get unlimited daytime exemptions without
	 * purchasing any permits.
	 * 
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static String listExemptionTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.listExemptionTypes(vo, u);
			s = mapper.writeValueAsString(r);
			
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String listPermitTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.listPermitTypes(vo, u);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	
	public static String listLastYearTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			
			SubObjVO[] sv = ParkingAgent.listLastYearTypes(vo);
			o.setChoices(sv);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String parkingConfig(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			s = GeneralAgent.getConfiguration("DOT").toString();
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String saveParkingAccount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			ResponseVO r = new ResponseVO();
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			if(u.getId() !=-1){
				r = ParkingAgent.saveAccount(vo, u, r);
				
			}else {
				
				r.setMessagecode("-1");
				MessageVO m = new MessageVO();
				m.setMessage(u.getMessage() + " Login Again");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setRedirect(true);
				s=mapper.writeValueAsString(r);
			}
			
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	
	public static String permitType(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ParkingAgent.permitType((vo.getType()), vo.getTypeid(), u);
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String exemptionType(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ParkingAgent.exemptionType((vo.getType()), vo.getTypeid());
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String onlinePrints(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			Logger.highlight("type");
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
		
			ResponseVO v = ParkingAgent.onlinePrints(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String saveParkingActivity(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = new ResponseVO();
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	

			if(Operator.hasValue(u.getMessage())){
				r.setMessagecode(u.getMessage());
			}

			if(Operator.equalsIgnoreCase(u.getMessage(), "cs402")){
				r.setRedirect(true);
			}
			
			if(u.getId()>=0){
				r = ParkingAgent.saveParkingActivity(vo, u, r);
			}
			
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String listTempExemptionTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.listTempExemptionTypes(vo, u);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	
	public static String saveTempParkingActivity(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.highlight(" Parking Temp Activity");
		String s = "";
		ResponseVO r = new ResponseVO();
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			
			if(Operator.hasValue(u.getMessage())){
				r.setMessagecode(u.getMessage());
			}
			if(Operator.equalsIgnoreCase(u.getMessage(), "cs402")){
				r.setRedirect(true);
			}
			
			if(u.getId()>=0){
				r = ParkingAgent.saveTempParkingActivity(vo, u, r);
			}
			
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String getAttachment(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.highlight(" Parking Attachment");
		String s = "";
		ResponseVO r = new ResponseVO();
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			
			if(Operator.hasValue(u.getMessage())){
				r.setMessagecode(u.getMessage());
			}
			if(Operator.equalsIgnoreCase(u.getMessage(), "cs402")){
				r.setRedirect(true);
			}
			
			if(u.getId()>=0){
				r = ParkingAgent.getAttachment(vo, u, r);
			}
			
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String deleteAttachment(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = new ResponseVO();
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			
			if(Operator.hasValue(u.getMessage())){
				r.setMessagecode(u.getMessage());
			}
			if(Operator.equalsIgnoreCase(u.getMessage(), "cs402")){
				r.setRedirect(true);
			}
			
			if(u.getId()>=0){
				r = ParkingAgent.deleteAttachment(vo, u, r);
			}
			
			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String parkingApproval(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
		
			ResponseVO v = ParkingAgent.parkingApproval(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String parkingApprovalCount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
		
			ResponseVO v = ParkingAgent.parkingApprovalCount(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String approve(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Cartographer map = new Cartographer(request,response);
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO v = ParkingAgent.approve(vo, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String unapprove(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO v = ParkingAgent.unapprove(vo, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String merge(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO v = ParkingAgent.merge(vo, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String listRenewalPermits(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.getRenewalPermits(vo); 
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String listRenewalPermitTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.listPermitTypes(vo, u);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String deleteDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			ResponseVO r = ParkingAgent.deleteDetails(vo, u);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String getParkingDates(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			TypeVO t = ParkingAgent.getParkingDates(vo, u);
			s = mapper.writeValueAsString(t);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String getRenewalTypes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			
			SubObjVO[] grp = ParkingAgent.listRenewalTypes(vo, u);
			s = mapper.writeValueAsString(grp);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}

	public static Object renewalCount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			
			SubObjVO[] sv = ParkingAgent.renewalCount(vo);
			o.setChoices(sv);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}

	public static Object processPermitCount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			
			SubObjVO[] sv = ParkingAgent.processPermitCount(vo);
			o.setChoices(sv);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String myActive(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getMyActive(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	
}
