package csapi.impl.activity;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.finance.FinanceAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.people.PeopleAgent;
import csapi.impl.users.UsersAgent;
import csapi.impl.users.UsersSQL;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserItemsVO;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;


public class ActivityImpl {

	public static String modules(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getModules(vo);
			s = mapper.writeValueAsString(v);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String refreshmodules(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			CsDeleteCache.deleteTypeCache(vo.getType(), vo.getTypeid());
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getModules(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = ActivityAgent.browse(vo.getEntity(), vo.getId(), vo.getTypeid(), vo.getOption(), u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}

	public static String panels(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserItemsVO v = ActivityAgent.panels(vo.getEntity(), vo.getTypeid(), vo.getReference());
			s = mapper.writeValueAsString(v);
			
			
		}
		catch (Exception e) { }
		return s;
	}

	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
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

	public static String refreshsummary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			CsDeleteCache.deleteTypeCache(vo.getType(), vo.getTypeid());
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

	public static String refreshinfo(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			String path = CsApiConfig.getCachePath(vo.getType(), vo.getTypeid(), "activity", "info");
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
			FileUtil.saveCache(path, s);
			Logger.highlight("REFRESHED CACHE", "Refreshed cache of activity info");
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

//	public static String group(HttpServletRequest request, HttpServletResponse response, String json) {
//		String s = "";
//		try {
//			//TODO secure
//			RequestVO vo = ObjMapper.toRequestObj(json);
//			ObjectMapper mapper = new ObjectMapper();
//			TypeVO v = Types.getGroup(vo);
//			s = mapper.writeValueAsString(v);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return s;
//	}

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

	public static String my(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getMy(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
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
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = saveActivity(vo, u);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
			if(Operator.hasValue(r.getMessagecode())) {
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}

	public static ResponseVO saveActivity(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		r.setProcessid(vo.getProcessid());
		try {
			DataVO m = DataVO.toDataVO(vo);
			int qty = Operator.toInt(m.get("QTY"));
			String result = "";
			if(qty>0){
				for(int i=0;i<qty;i++){
					LogAgent.updateLog(vo.getProcessid(), 50, "Saving");
					result = ActivityAgent.saveActivity(vo, u);
					r = LogAgent.getLog(vo.getProcessid());
					CsDeleteCache.deleteProjectChildCache(vo.getType(), vo.getTypeid(), "activity");
					CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "activities");
					CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "project");
				}
				//GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
				//GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
			}
			else {
				LogAgent.updateLog(vo.getProcessid(), 50, "Saving");
				result = ActivityAgent.saveActivity(vo, u);
				if (vo.getType().equalsIgnoreCase("activity") && vo.getTypeid() > 0 && Operator.s2b(m.getString("UPDATE_FEES"))==true) {
					FinanceAgent.updateFinance(vo,u, Operator.toString(vo.getTypeid()),0,null);
				}
				r = LogAgent.getLog(vo.getProcessid());
				CsDeleteCache.deleteProjectChildCache(vo.getType(), vo.getTypeid(), "activity");
				CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "activities");
				CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "project");
				//GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
				//GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
			}
			if (Operator.hasValue(result) && Operator.isNumber(result) && Operator.toInt(result) > 0) {
				r.setMessagecode("cs200");
				TypeVO t = new TypeVO();
				t.setType("activity");
				t.setTypeid(Operator.toInt(result));
				t.setId(Operator.toInt(result));
				t.setEntity(vo.getEntity());
				t.setEntityid(vo.getEntityid());
				t.addData("activities", result);
				r.setType(t);
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
				//if (r) {
				//}
			}
			else if (Operator.hasValue(result)) {
				Logger.highlight("alain###################################################"+vo.getTypeid());
				r.setMessagecode("cs200");
				TypeVO t = new TypeVO();
				t.setType("project");
				t.setTypeid(vo.getTypeid());
				t.setId(vo.getTypeid());
				t.setEntity(vo.getEntity());
				t.setEntityid(vo.getEntityid());
				t.addData("activities", result);
				r.setType(t);
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			else if (!r.isValid()) {
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			else {
				r.setMessagecode("cs500");
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);
		}
		r.setPercentcomplete(100);
		return r;
	}

	public static String updateVal(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ObjGroupVO r = new ObjGroupVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = GeneralAgent.setValues("activity", ActivitySQL.updateVal(vo.extras));
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
	
	public static String permitDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		HashMap<String, String> r = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = ActivityAgent.getPermitDetails(vo.getTypeid(), vo.getId());
			s = mapper.writeValueAsString(r);
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
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ActivityAgent.status(vo.getId(), vo.getTypeid());
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String actType(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ActivityAgent.actType((vo.getType()), vo.getTypeid(), u);
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String statusDefaultIssued(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("updating statusDefaultIssued");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = ActivityAgent.statusDefaultIssued(vo, u);
			s = mapper.writeValueAsString(r);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}

	public static String addPermit(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		ObjGroupVO r = new ObjGroupVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			Sage db = new Sage();
			String command = UsersSQL.getOnlineUsers(u.getUsername(), u.getId(), m.getInt("LKUP_USERS_TYPE_ID"));
			db.query(command);
			String refuserid = "";
			int userid = 0;
			if(db.next()){
				refuserid = db.getString("REF_USER_ID");
				userid = db.getInt("ID");
			}
			db.clear();
			
			if(Operator.toInt(refuserid) <= 0)
				refuserid = Operator.toString(UsersAgent.saveRefUser(userid, m.getInt("LKUP_USERS_TYPE_ID"), m.getString("LIC_NO"), m.getString("LIC_EXP_DT"), "", "", "", u.getId(), u.getIp()));
			
			if(vo.getTypeid() > 0){
				boolean b = PeopleAgent.addPeople(vo.getType(), vo.getTypeid(), refuserid, 0, "", u.getId(), u.getIp());
				if (b) {
					CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
					CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
				}
				r.setMessagecode("cs200");
			}else{
				r.setMessagecode("cs412");
			}
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}

	public static String significantHold(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			boolean check = ActivityAgent.significantHold(vo.getType(),vo.getTypeid());
			r.setMessagecode(Operator.b2s(check));
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String permitInfoDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		HashMap<String, String> r = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = ActivityAgent.getPermitInfoDetails(vo.getReference());
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}

	public static String getupdatedates(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("logging getupdatedates");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjVO o = new ObjVO();
			SubObjVO[] v = ActivityAgent.getActTypedates((vo.getType()), vo.getTypeid(), u);
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}

}





