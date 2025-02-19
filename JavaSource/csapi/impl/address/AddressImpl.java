package csapi.impl.address;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralImpl;
import csapi.search.activity.SearchActivity;
import csapi.search.address.SearchAddress;
import csapi.utils.Nav;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeVO;

public class AddressImpl {

	
	
	public static JSONObject search(HttpServletRequest request,HttpServletResponse response,String json){
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
			Cartographer map = new Cartographer(request,response);
			System.out.println("rr"+request.getAttribute("token"));
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = AddressAgent.getChildren(p.getInt("id"));
						
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
	
//	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
//		String s = "";
//		try {
//			//TODO secure
//			RequestVO vo = ObjMapper.toRequestObj(json);
//			ObjectMapper mapper = new ObjectMapper();
//			TypeVO v = Types.getSummary(vo);
//			s = mapper.writeValueAsString(v);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return s;
//	}

	public static JSONObject summary(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			Cartographer map = new Cartographer(request,response);
			System.out.println("rr"+request.getAttribute("token"));
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
				o = getEntity(p.getInt("id"),p.getString("entity"));
				
				ObjectMapper mapper = new ObjectMapper();
				TypeVO v = getEntityLandType("L", p.getInt("id"), p.getString("entity"));
				System.out.println(mapper.writeValueAsString(v));
				Logger.info("***************"+v.getTitle());
			
			}else {
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static JSONObject formdetails(HttpServletRequest request,HttpServletResponse response,String json){
		return formdetails(request, response, json, false);
	}
	
	public static JSONObject formdetails(HttpServletRequest request,HttpServletResponse response,String json,boolean fields){
		JSONObject o = new JSONObject();
		
		try{
			Cartographer map = new Cartographer(request,response);
			System.out.println("rr"+request.getAttribute("token"));
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
			String type = AddressAgent.getLsoType(id);
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
		
			JSONObject details = AddressAgent.getDetailGroup(type, typeid);
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
			String type = AddressAgent.getLsoType(id);
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
	
	
	public static TypeVO getEntityLandType(String type,int id,String entity){
		TypeVO o = new TypeVO();
		
		try{
			//entity
			o.setId(id);
			o.setEntity(entity);
			o.setType("lso");
			o.setSubtitle("");
			if(id==10045){
				o.setHold("hold");
			}else {
				o.setHold("");
			}
			
			ObjGroupVO vo = AddressAgent.getDetailGroup1(type, id);
			o.setEntity(entity);
			o.setType("lso");
			o.setTitle(vo.getExtras().get("address"));
			
			o.setGroups(GeneralImpl.getGroups1(vo, type, entity, id));
			
			
			
		
			
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}
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
			
			JSONObject details = AddressAgent.getDetailGroup(type, id);
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
			
			JSONObject details = AddressAgent.getDetailGroup(type, id);
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
			
			JSONObject details = AddressAgent.getDetailGroup(type, id);
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
	
	
	
	
	
	
	
}
