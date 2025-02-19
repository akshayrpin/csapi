package csapi.impl.activities;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.log.LogAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;


public class ActivitiesImpl {

	

	

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

	public static String active(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			ArrayList<HashMap<String, String>> l = ActivitiesAgent.activeList(vo.getTypeid(), u);
			r.setList(l);
			if(Operator.hasValue(r.getMessagecode())) {
				s = mapper.writeValueAsString(r);
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}

	public static String save(RequestVO vo, Token u) {
		String s = "";
		ResponseVO r = LogAgent.getLog(vo.getProcessid());
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			r = ActivitiesAgent.save(vo, u);

			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "activities");
			CsDeleteCache.deleteCacheDir(vo.getType(), vo.getTypeid());
			GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

			r.setMessagecode("cs200");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);

			if(Operator.hasValue(r.getMessagecode())) {
				s = mapper.writeValueAsString(r);
			}
		}
		catch (Exception e) {
			r.setMessagecode("cs500");
			r.addError("A internal server error has occured");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);
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
			SubObjVO[] v = ActivitiesAgent.status(vo.getId());
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String psearch(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			int page = Operator.toInt(vo.getExtra("PAGE"));
			int max = Operator.toInt(vo.getExtra("MAX"));
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ObjVO v = ActivitiesAgent.psearch(vo.getSearch(), page, max, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}



}















