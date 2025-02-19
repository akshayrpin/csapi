package csapi.impl.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.activity.ActivityAgent;
import csapi.impl.entity.EntityAgent;
import csapi.impl.log.LogAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
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
import csshared.vo.TypeInfo;
import csshared.vo.TypeVO;

public class ProjectImpl {

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
	
//	public static String cachedSummary(HttpServletRequest request, HttpServletResponse response, String json) {
//		String s = "";
//		try {
//			//TODO secure
//			RequestVO vo = ObjMapper.toRequestObj(json);
//			String path = CsApiConfig.getCachePath(vo.getType(), vo.getTypeid(), "summary");
//			try {
//				s = FileUtil.getCache(path, CsApiConfig.getCacheInterval());
//				Logger.highlight("CACHE", "Retrieved project summary from cache");
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
			BrowserItemsVO v = ProjectAgent.panels(vo.getEntity(), vo.getTypeid(), vo.getReference());
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
			String path = CsApiConfig.getCachePath(vo.getType(), vo.getTypeid(), "project", "info");
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
			FileUtil.saveCache(path, s);
			Logger.highlight("REFRESHED CACHE", "Refreshed cache of project info");
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
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = ProjectAgent.browse(vo.getEntity(), vo.getId(),vo.getOption(), u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}


	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static JSONObject subs(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			Cartographer map = new Cartographer(request,response);
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = ProjectAgent.getSubs(p.getInt("id"));
			}else {
				o.put("error", "Invalid Token");
			}
		} catch(Exception e) {
		}
		return o;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static JSONObject childrens(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			Cartographer map = new Cartographer(request,response);
			
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = ProjectAgent.getChildrens(p.getInt("id"));
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
		}
		return o;
	}
	
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static String projectSummary(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		
		
		String s = "";
		try{
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getSummary(vo);
			s = mapper.writeValueAsString(v);
		}catch (Exception e){
			e.printStackTrace();
			Logger.error("e.ger"+e.getMessage());
		}
		return s;
		
	}
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	/*public static JSONObject activitySummary(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			Cartographer map = new Cartographer(request,response);
			
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = getEntityActivity(p.getInt("id"),p.getString("entity"));
			}else {
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return o;
	}*/
	
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static String activityInfo(HttpServletRequest request,HttpServletResponse response,String json){
	//	JSONObject o = new JSONObject();
		
		
		
		String s = "";
		try{
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
		}catch (Exception e){
			e.printStackTrace();
			Logger.error("e.ger"+e.getMessage());
		}
		return s;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @return
	 */
	public static String formdetailsProject(HttpServletRequest request,HttpServletResponse response,String json){
		return formdetailsProject(request, response, json,false);
	}
	
	/**
	 * @param request
	 * @param response
	 * @param json
	 * @param fields
	 * @return
	 */
	public static String formdetailsProject(HttpServletRequest request,HttpServletResponse response,String json,boolean fields){
		JSONObject o = new JSONObject();
		
		
		
		String s = "";
		try{
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			TypeVO v = new TypeVO();
			if(fields){
				v = Types.getFields(vo);
			}else{
				//v = Types.getDetails(vo);
			}
			s = mapper.writeValueAsString(v);
		}catch (Exception e){
			Logger.error("e.ger");
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

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
			Logger.info(s+"***************************");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();

		Token u = AuthorizeToken.authenticate(vo);	

//		DataVO m = DataVO.toDataVO(vo);
		ObjGroupVO v = vo.getData()[0];
		
		if(v.getGroup().equalsIgnoreCase("PROJECT")){
			r = saveProject(vo, u);
			//GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
		}
		else {
			r.setMessagecode("cs404");
		}
		
		try {
			if (Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}

	public static ResponseVO saveProject(RequestVO vo, Token u){
		ResponseVO r = LogAgent.getLog(vo.getProcessid());
		try {
			boolean v = ProjectValidate.pre(vo, u);
			if(v) {
				int result = ProjectAgent.saveProject(vo,u);

				if(result > 0) {
					r.setMessagecode("cs200");
					TypeInfo tinfo = EntityAgent.getEntity(vo.getType(), vo.getTypeid());
					String entity = tinfo.getEntity();
					int entityid = tinfo.getEntityid();
					TypeVO t = new TypeVO();
					t.setType("project");
					t.setTypeid(result);
					t.setEntity(entity);
					t.setEntityid(entityid);
					r.setType(t);
					r.setPercentcomplete(100);
					LogAgent.saveLog(r);
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "project");
					
					GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
					GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

				}
				else {
					r.setMessagecode("cs500");
					r.setPercentcomplete(100);
					LogAgent.saveLog(r);
				}
			}
			else {
				r = LogAgent.getLog(vo.getProcessid());
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			e.printStackTrace();
			r.setMessagecode("cs500");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);
		}
		r.setPercentcomplete(100);
		return r;
	}

	public static String types(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ProjectAgent.types(vo.getType(), vo.getTypeid());
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
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
			SubObjVO[] v = ProjectAgent.status();
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String autoActivities(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ProjectAgent.autoActivities(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}





}







