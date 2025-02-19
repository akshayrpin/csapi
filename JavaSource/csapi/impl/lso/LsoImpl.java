package csapi.impl.lso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.security.Token;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.activities.ActivitiesAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralImpl;
import csapi.search.activity.SearchActivity;
import csapi.search.address.SearchAddress;
import csapi.security.AuthorizeToken;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserItemsVO;
import csshared.vo.BrowserVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;

public class LsoImpl {

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

//	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
//		String s = "";
//		try {
//			//TODO secure
//			RequestVO vo = ObjMapper.toRequestObj(json);
//			String path = CsApiConfig.getCachePath(vo.getType(), vo.getTypeid(), "summary");
//			try {
//				s = FileUtil.getCache(path, CsApiConfig.getCacheInterval());
//				Logger.highlight("CACHE", "Retrieved lso summary from cache");
//			}
//			catch (Exception se) {
//				s = refreshsummary(request, response, json);
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return s;
//	}
//
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

	public static String panels(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserItemsVO v = LsoAgent.panels(vo.getEntity(), vo.getTypeid());
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
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
			String path = CsApiConfig.getCachePath(vo.getType(), vo.getTypeid(), "lso", "info");
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
			FileUtil.saveCache(path, s);
			Logger.highlight("REFRESHED CACHE", "Refreshed cache of lso info");
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

	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = LsoAgent.browse(vo.getId(),vo.getOption());
			s = mapper.writeValueAsString(v);
			//REMOVE THE COMMENT if search is not working
			/*s = "{\"panels\":{},\"header\":{\"parents\":[],\"search\":{\"url\":\"http://localhost:8080/csapi/rest/lso/search\",\"typeid\":-1,\"entity\":\"\",\"type\":\"\",\"grouptype\":\"\",\"domain\":\"\",\"option\":\"\",\"placeholder\":\"Search for Address\"},\"dataid\":\"menu1\",\"follow\":\"\",\"options\":[\"Inactive\"],\"label\":\"LSO BROWSER\",\"menu\":\"\",\"entity\":\"\",\"type\":\"\",\"domain\":\"\",\"found\":0,\"querytime\":0,\"status\":0,\"query\":\"\",\"message\":\"\",\"option\":\"active\"},\"items\":[],\"root\":[]}
";
*/		}
		catch (Exception e) { }
		return s;
	}

	public static String search(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			JSONObject p = new JSONObject(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = LsoAgent.search(p.getString("search"), p.getInt("start"), p.getInt("end"),p.getString("option"));
			s = mapper.writeValueAsString(v);
			//REMOVE THE COMMENT if search is not working
			//s ={\"panels\":{},\"header\":{\"parents\":[],\"search\":{\"url\":\"http://localhost:8080/csapi/rest/lso/search\",\"typeid\":-1,\"entity\":\"\",\"type\":\"\",\"grouptype\":\"\",\"domain\":\"\",\"option\":\"\",\"placeholder\":\"156 crescent\"},\"dataid\":\"\",\"follow\":\"\",\"options\":[\"Inactive\"],\"label\":\"LSO BROWSER\",\"menu\":\"\",\"entity\":\"\",\"type\":\"\",\"domain\":\"\",\"found\":1,\"querytime\":1,\"status\":0,\"query\":\"156 crescent\",\"message\":\"\",\"option\":\"active\"},\"items\":[],\"root\":[{\"dataid\":\"2870\",\"id\":\"2870\",\"title\":\"156 S CRESCENT DR\",\"description\":\"TRACT # 6380 EX OF ALLEY\",\"children\":1,\"child\":\"\",\"sub\":\"project\",\"link\":\"summary\",\"entity\":\"lso\",\"type\":\"lso\",\"domain\":\"http://localhost:8080\"}]} ]}";
		}
		catch (Exception e) { }
		return s;
	}

	public static JSONObject search1(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
		
		
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = SearchAddress.getSearchList(p.getString("q"), p.getInt("start"), p.getInt("end"));
			
				o.put("scheme", request.getScheme());
			
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	
	public static JSONObject searchAll(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = SearchActivity.getSearchList(p.getString("q"), p.getInt("start"), p.getInt("end"));
			
				o.put("scheme", request.getScheme());
			
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static JSONObject search(String json){
		JSONObject o = new JSONObject();
		
		try{
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = SearchAddress.getSearchList(p.getString("q"), p.getInt("start"), p.getInt("end"));
			
			
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	
	
	
	public static JSONObject children(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = LsoAgent.getChildren(p.getInt("id"));
						
				o.put("scheme", request.getScheme());
			
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
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
	
//	public static JSONObject summary(HttpServletRequest request,HttpServletResponse response,String json){
//		JSONObject o = new JSONObject();
//		
//		try{
//			Cartographer map = new Cartographer(request,response);
//			System.out.println("rr"+request.getAttribute("token"));
//			JSONObject p = new JSONObject(json);
//			if(p.has("token")){
//				o = getEntity(p.getInt("id"),p.getString("entity"));
//				
//				ObjectMapper mapper = new ObjectMapper();
//				TypeVO v = getEntityLandType("L", p.getInt("id"), p.getString("entity"));
//				System.out.println(mapper.writeValueAsString(v));
//				Logger.info("***************"+v.getTitle());
//			
//			}else {
//				o.put("error", "Invalid Token");
//			}
//			
//		} catch(Exception e) {
//			Logger.error(e.getMessage());
//		}
//		return o;
//	}
	
	public static JSONObject formdetails(HttpServletRequest request,HttpServletResponse response,String json){
		return formdetails(request, response, json, false);
	}
	
	public static JSONObject formdetails(HttpServletRequest request,HttpServletResponse response,String json,boolean fields){
		JSONObject o = new JSONObject();
		
		try{
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = getEntity(p,fields);
				
			}else {
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static JSONObject getEntity(JSONObject p,boolean fields){
		JSONObject o = new JSONObject();
		
		try{
			int id = Operator.toInt(p.getString("id"));
			String type = LsoAgent.getLsoType(id);
			o.put("id", id);
			
			o.put("subtitle", "");
			
			if(id==10045){
				o.put("alert", "hold");
			}else {
				o.put("alert", "");
			}
			int typeid = p.getInt("typeid");
			String t = p.getString("type");
			String entity = p.getString("entity");
			o.put("entity", entity);
			o.put("type", t);
			if(id<=0){
				fields = true;
			}
		
			JSONObject details = LsoAgent.getDetailGroup(type, typeid);
			o.put("title", details.getString("address"));
			//getGroups
			JSONArray groups = new JSONArray();
			
			String formgrp = p.getString("group");
			
			
					
			
					
			if(formgrp.startsWith("divisions")){
			//	groups.put(getDivisionsDetails(type, id));
			}else if(formgrp.startsWith("hold")){
				groups.put(GeneralAgent.getHold(type,id));
			}else if(formgrp.startsWith("cust")){
				groups.put(GeneralAgent.getCustomFields(type,id));
			}else if(formgrp.startsWith("attac")){
				groups.put(GeneralAgent.getAttachments(type,id));
			}else if(formgrp.startsWith("cond")){
				groups.put(GeneralAgent.getConditions(type,id));
			}else if(formgrp.startsWith("COMMENTS")){
				groups.put(GeneralAgent.getComment(type, typeid, entity, id, fields));
			}else if(formgrp.startsWith("LAND DETAI")){
				groups.put(details);
			}else {
				groups.put(GeneralAgent.getCustomFields(type,typeid));
			}
			o.put("groups",groups);
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static JSONObject getEntity(int id,String entity){
		JSONObject o = new JSONObject();
		
		try{
			String type = LsoAgent.getLsoType(id);
			if(type.equalsIgnoreCase("L")){
				o = getEntityLand(type, id,entity);
			}else if(type.equalsIgnoreCase("S")){
				o = getEntityStructure(type, id,entity);
			}else if(type.equalsIgnoreCase("O")){
				o = getEntityOccupancy(type, id,entity);
			}
			
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	

//	public static TypeVO getEntityLandType(String type,int id,String entity){
//		TypeVO o = new TypeVO();
//		
//		try{
//			//entity
//			o.setId(id);
//			o.setEntity(entity);
//			o.setType("lso");
//			o.setSubtitle("");
//			if(id==10045){
//				o.setAlert("hold");
//			}else {
//				o.setAlert("");
//			}
//			
//			ObjGroupVO vo = LsoAgent.getDetailGroup1(type, id);
//			o.setEntity(entity);
//			o.setType("lso");
//			o.setTitle(vo.getExtras().get("address"));
//			
//			o.setGroups(GeneralImpl.getGroups1(vo, type, entity, id));
//			
//			
//			
//		
//			
//			
//		} catch(Exception e) {
//			Logger.error(e.getMessage());
//		}
//		return o;
//	}

	public static JSONObject getEntityLand(String type,int id,String entity){
		JSONObject o = new JSONObject();
		
		try{
			//entity
			
			//ObjectMapper mapper = new ObjectMapper();
			o.put("id", id);
			o.put("entity", entity);
			o.put("type", "lso");
			o.put("subtitle", "");
			
			if(id==10045){
				o.put("alert", "hold");
			}else {
				o.put("alert", "");
			}
			
			JSONObject details = LsoAgent.getDetailGroup(type, id);
			details.put("entity",entity);
			details.put("type","lso");
			o.put("title", details.getString("address"));
			//getGroups
			
			
			JSONArray groups = GeneralImpl.getGroups(details, type, entity, id);
			/*groups.put(details);
			
			groups.put(getDivisionsDetails(type, id));
			groups.put(GeneralAgent.getCustomFields(type,id));
			groups.put(GeneralAgent.getHolds(type,id));
			groups.put(GeneralAgent.getConditions(type,id));
			groups.put(GeneralImpl.getAttachments(type,id));
			groups.put(GeneralImpl.getComments(type, id));
			groups.put(getSetBack(type, id));*/
			
			o.put("groups",groups);
			
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static JSONObject getEntityStructure(String type,int id,String entity){
		JSONObject o = new JSONObject();
		
		try{
			//entity
			
			//ObjectMapper mapper = new ObjectMapper();
			o.put("id", id);
			o.put("entity", entity);
			o.put("type", "lso");
			o.put("subtitle", "");
			
			if(id==10045){
				o.put("alert", "hold");
			}else {
				o.put("alert", "");
			}
			
			JSONObject details = LsoAgent.getDetailGroup(type, id);
			details.put("entity",entity);
			details.put("type","lso");
			o.put("title", details.getString("address"));
			//getGroups
			/*JSONArray groups = new JSONArray();
			groups.put(details);
			
			groups.put(GeneralAgent.getCustomFields(type,id));
			
			groups.put(GeneralAgent.getHolds(type,id));
			groups.put(GeneralAgent.getConditions(type,id));
			groups.put(GeneralImpl.getAttachments(type,id));
			groups.put(GeneralImpl.getComments(type, id));
			//groups.put(getSetBack(type, id));
*/			
			JSONArray groups = GeneralImpl.getGroups(details, type, entity, id);
			o.put("groups",groups);
			
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	
	
	public static JSONObject getEntityOccupancy(String type,int id,String entity){
		JSONObject o = new JSONObject();
		
		try{
			//entity
			
			//ObjectMapper mapper = new ObjectMapper();
			o.put("id", id);
			
			o.put("entity", entity);
			o.put("type", "lso");
			
			if(id==10045){
				o.put("alert", "hold");
			}else {
				o.put("alert", "");
			}
			
			JSONObject details = LsoAgent.getDetailGroup(type, id);
			details.put("entity",entity);
			details.put("type","lso");
			o.put("title", details.getString("address"));
			o.put("subtitle",  "UNIT ".concat(details.getString("unit")));
			//getGroups
			/*JSONArray groups = new JSONArray();
			groups.put(details);
			//groups.put(getDivisionsDetails(type, id));
			groups.put(GeneralAgent.getCustomFields(type,id));
			
			
			//groups.put(getSetBack(type, id));
			groups.put(GeneralAgent.getHolds(type,id));
			groups.put(GeneralAgent.getConditions(type,id));
			groups.put(GeneralImpl.getAttachments(type,id));
			groups.put(GeneralImpl.getComments(type, id));*/
			
			JSONArray groups = GeneralImpl.getGroups(details, type, entity, id);
			o.put("groups",groups);
			
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			ObjGroupVO v = vo.getData()[0];

			if (v.getGroup().equalsIgnoreCase("LSO")){
				r = saveLso(vo, u);
			}
			else {
				r.setMessagecode("cs404");
			}

			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

	public static ResponseVO saveLso(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try{
			ObjGroupVO o = vo.getData()[0];
			r = ValidateRequest.process(o, vo.getType());
			if(r.isValid()){
				boolean result = LsoAgent.saveLso(vo,u);
				if(result){
					r.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "lso");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Unable to save lso");
				}
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			r.addMessage(e.getMessage());
		}
		return r;
	}
	
	public static String psearch(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			int page = Operator.toInt(vo.getExtra("PAGE"));
			int max = Operator.toInt(vo.getExtra("MAX"));
			ObjVO v = LsoAgent.psearch(vo.getSearch(), page, max, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	
	
	
	
}
