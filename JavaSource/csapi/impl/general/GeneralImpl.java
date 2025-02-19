package csapi.impl.general;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.address.AddressAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.Nav;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;

public class GeneralImpl {

	
	public static String LOG_CLASS="GeneralImpl.java   : ";
	
	
	public static JSONObject getComments(String type,int id){
		return GeneralAgent.getComments(type, id);
	}
	
	public static JSONObject getAttachments(String type,int id){
		return GeneralAgent.getAttachments(type, id);
	}
	
	public static JSONArray getGroups(JSONObject details,String type,String entity,int id){
		return getGroups(details, type, entity, id,"");
	}
	
	public static JSONArray getGroups(JSONObject details,String type,String entity,int id,String fields){
		JSONArray groups = new JSONArray();
		try{
			groups.put(details);
			
			if(type.equalsIgnoreCase("L") || type.equalsIgnoreCase("S") ||  type.equalsIgnoreCase("O")){
				groups.put(getDivisionsDetails(type, id));
				groups.put(getSetBack(type, id));
			}
			
			JSONArray c = GeneralAgent.getCustomFieldsArray(type,details.getInt("ctypeid"),entity,id);
			for (int i = 0; i < c.length(); ++i) {
			    JSONObject rec = c.getJSONObject(i);
			    groups.put(rec);
			}
			
			groups.put(GeneralAgent.getHolds(type,id));
			groups.put(GeneralAgent.getConditions(type,id));
			groups.put(GeneralImpl.getAttachments(type,id));
			groups.put(GeneralImpl.getComments(type,id));
	
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return groups;
	}	
	
	
	public static JSONObject getSetBack(String type,int id){
		JSONObject g = new JSONObject();
		try{
			//getGroups
			if(type.equalsIgnoreCase("L")){
				g = AddressAgent.getSetBack(id, 1);
			}else if(type.equalsIgnoreCase("S")){
				g = AddressAgent.getSetBack(id, 2);
			}else {
				g = AddressAgent.getSetBack(id, 3);
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	
	public static JSONObject getDivisionsDetails(String type,int id){
		JSONObject g = new JSONObject();
		try{
			//getGroups
		//	g.setGroupid("2");
			g.put("pub", "Y");
			g.put("group","DIVISIONS DETAILS");
			g.put("obj",AddressAgent.getDivisionsDetails(id));
			//g.put("editurl", Config.rooturl()+"cs/form.jsp?type="+type+"&id="+id+"&formtype=division&frmid=divisiondetail_"+id);
			g.put("editurl", Nav.getFormEditUrl(type,id,"divisionsdetail_"));
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	public static ObjGroupVO getDivisionsDetails2(String type,int id){
		ObjGroupVO g = new ObjGroupVO();
		try{
			//getGroups
			g.setGroupid("2");
			g.setPub("Y");
			g.setGroup("DIVISIONS DETAILS");
			g.setObj(AddressAgent.getDivisionsDetails2(id));
			//g.put("editurl", Config.rooturl()+"cs/form.jsp?type="+type+"&id="+id+"&formtype=division&frmid=divisiondetail_"+id);
			//g.put("editurl", Nav.getFormEditUrl(type,id,"divisiondetail_"));
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static ObjGroupVO[] getGroups1(ObjGroupVO details,String type,String entity,int id){
		return getGroups1(details, type, entity, id,"");
	}
	
	public static ObjGroupVO[] getGroups1(ObjGroupVO details,String type,String entity,int id,String fields){
		ObjGroupVO[] groups = new ObjGroupVO[0];
		try{
			ArrayList l = new ArrayList<ObjGroupVO>();
			l.add(details);
		
			l.add(getDivisionsDetails2(type, id));
			
			
			//String arr [] = new String[l.size()];
			groups = new ObjGroupVO[l.size()];
			l.toArray(groups);
			
			/*if(type.equalsIgnoreCase("L") || type.equalsIgnoreCase("S") ||  type.equalsIgnoreCase("O")){
				groups.put(getDivisionsDetails(type, id));
				groups.put(getSetBack(type, id));
			}
			
			JSONArray c = GeneralAgent.getCustomFieldsArray(type,details.getInt("ctypeid"),entity,id);
			for (int i = 0; i < c.length(); ++i) {
			    JSONObject rec = c.getJSONObject(i);
			    groups.put(rec);
			}
			
			groups.put(GeneralAgent.getHolds(type,id));
			groups.put(GeneralAgent.getConditions(type,id));
			groups.put(GeneralImpl.getAttachments(type,id));
			groups.put(GeneralImpl.getComments(type,id));*/
	
		} catch(Exception e) {
			Logger.error(e.getMessage());
			Logger.stack();
		}
		return groups;
	}	
	
	public static ResponseVO saveCustom(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try{
			ObjGroupVO o = vo.getData()[0];
			//TODO
			r = ValidateRequest.processCustom(Operator.toInt(o.getGroupid()), o.objValues());
			if(r.isValid()){
				boolean result =GeneralAgent.saveCustom(Operator.toInt(vo.getId()),vo.getType(),vo.getTypeid(),o,u);
				if(result){
					r.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "custom");
				}else {
					r.setMessagecode("cs500");
				}
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
	}
	
	public static String choicesUrl(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			String filename = Choices.getFileNameChoices(json);
			/*if(Operator.hasValue(filename)){
				s = FileCache.getCache(2, filename);
			}
			else if(!Operator.hasValue(filename) || !Operator.hasValue(s)) {
				//ObjVO vo = new ObjVO();
				//vo = FieldObjects.toObject(-1, 0, "REVIEW", "String", "review", "REVIEW", "", "", "", "", "", "", "", "Y", Choices.choiceUrlQuery(json));	
				s = Choices.choiceUrlQuery(json);
				Logger.info(s);
				//FileCache.setCache(s, filename);
			}*/
			Logger.info("came");
			s = Choices.choiceUrlQuery(json);
			//s ="{\"choices\":[{\"id\":-6,\"text\":\"NOT APPLICABLE\",\"value\":\"-6\"},{\"id\":288,\"text\":\"Comm Low Density (General and Municipal)\",\"value\":\"288\"},{\"id\":289,\"text\":\"Comm Low Density (General or Medium Density Retail)\",\"value\":\"289\"},{\"id\":290,\"text\":\"Comm Low Density (General)\",\"value\":\"290\"},{\"id\":291,\"text\":\"Comm Specific Plan (Hotel)\",\"value\":\"291\"},{\"id\":292,\"text\":\"Comm Specific Plan (Mixed-Use)\",\"value\":\"292\"},{\"id\":293,\"text\":\"MFR High Density\",\"value\":\"293\"},{\"id\":294,\"text\":\"MFR Low Density\",\"value\":\"294\"},{\"id\":295,\"text\":\"MFR Low-Medium Density\",\"value\":\"295\"},{\"id\":296,\"text\":\"MFR Medium Density\",\"value\":\"296\"},{\"id\":297,\"text\":\"MFR Very Low Density\",\"value\":\"297\"},{\"id\":298,\"text\":\"SFR High Density\",\"value\":\"298\"},{\"id\":299,\"text\":\"SFR Low Density\",\"value\":\"299\"},{\"id\":300,\"text\":\"SFR Medium Density\",\"value\":\"300\"},{\"id\":301,\"text\":\"Transitional MFR - Commercial Parking\",\"value\":\"301\"},{\"id\":302,\"text\":\"Public (Park)\",\"value\":\"302\"},{\"id\":303,\"text\":\"Public (Public School)\",\"value\":\"303\"},{\"id\":304,\"text\":\"Public (Public Building)\",\"value\":\"304\"},{\"id\":305,\"text\":\"Public (Scenic Highway)\",\"value\":\"305\"},{\"id\":306,\"text\":\"Public (Railroad)\",\"value\":\"306\"},{\"id\":307,\"text\":\"Public (Reservoir)\",\"value\":\"307\"}]}";
		} catch (Exception e) {
			Logger.error(e.getMessage());
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
			
			if(v.getType().equalsIgnoreCase("custom")){
				r = saveCustom(vo, u);
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "custom");
			}else {
				//r.setMessagecode("cs404");
				r = saveCommon(vo, u);
			}
			
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
				boolean action = GeneralAgent.saveCommon(vo,u);
				if(action){
					result.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), vo.getGrouptype());
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

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ArrayList<MessageVO> msgs = new ArrayList<MessageVO>();

		if (!Operator.hasValue(vo.getType()) || vo.getTypeid() < 1 || !Operator.hasValue(vo.getId())) {
			if (!Operator.hasValue(vo.getType())) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Type is a required field.");
				msgs.add(mvo);
			}
			if (vo.getTypeid() < 1) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Type id is a required field.");
				msgs.add(mvo);
			}
			if (!Operator.hasValue(vo.getId())) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Id is a required field.");
				msgs.add(mvo);
			}
		}
		else {
			boolean b = GeneralAgent.deleteRef(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()), u.getId());
			GeneralAgent.delete(vo.getGrouptype(), Operator.toInt(vo.getId()), u.getId());
			if (b) {
				CsReflect.addHistory(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()), "delete");
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), vo.getGrouptype());
			}
			else {
				r.setMessagecode("cs500");
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Database Error");
				msgs.add(mvo);
			}
		}

		try {
			r.setErrors(msgs);
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}
	
	
	public static String choicesCommand(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			String command = json;
			SubObjVO[] sv = Choices.getChoices(command);
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(sv);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

	
	public static String getStreetList(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			

			String command = GeneralSQL.getStreetList();
			
			String selected = request.getParameter("selected")!=null?request.getParameter("selected"):"";
			Logger.info(command+"###"+selected);
			JSONObject o = Choices.getChoicesArray(command, selected);
			//SubObjVO[] sv = Choices.getChoices(command);
			//v.setChoices(sv);
			//ObjectMapper mapper = new ObjectMapper();
			//s = mapper.writeValueAsString(sv);
			//o.put("choices", s);
			
			s = o.toString();
			//Logger.info(sv.length);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static String getStreetFraction(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			String selected = request.getParameter("selected")!=null?request.getParameter("selected"):"";

			String command = GeneralSQL.getStreetFraction();
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, selected);
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static String getDepartments(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			

			String command = "select ID, ID AS VALUE, DEPT AS TEXT FROM DEPARTMENT WHERE ACTIVE = 'Y'";
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	
	public static JSONObject getConfiguration(String name) {
		JSONObject o = new JSONObject();
		try {
			o = GeneralAgent.getConfiguration(name);
			
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return o;
	}
	
	/*public static JSONObject globalSearch(HttpServletRequest request,HttpServletResponse response,String json){
		JSONObject o = new JSONObject();
		
		try{
			
			JSONObject p = new JSONObject(json);
			if(p.has("token")){
//				o = GlobalSearch.globalSearch(p.getString("q"), p.getInt("start"), p.getInt("end"));
			
				//o.put("scheme", request.getScheme());
			
			}else {
				
				o.put("error", "Invalid Token");
			}
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return o;
	}*/
	
	public static String getUsersType(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			String command = GeneralSQL.getUsersType(vo.getGrouptype());
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
	
	public static String getAttachmentsType(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			String command = GeneralSQL.getAttachmentsType(vo.getGrouptype());
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
	
	public static String getApplicationStatus(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			String command = GeneralSQL.getApplicationStatus(null);
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
}
