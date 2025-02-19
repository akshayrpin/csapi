package csapi.impl.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.custom.CustomAgent;
import csapi.impl.custom.CustomSQL;
import csapi.impl.holds.HoldsAgent;
import csapi.utils.CsReflect;
import csapi.utils.Nav;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.ObjSQL;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;

public class GeneralAgent {

	public static JSONArray getComments1(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getComments(type,id);
			Sage db = new Sage();
			db.query(command);
			
			int i =0;
			while(db.next()){
				i = i+1;
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("order", 1);
				ob.put("type", "String");
				ob.put("field", db.getString("DATE"));
				ob.put("value", db.getString("NOTE"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				obj.put(ob);
				
				
			}
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	
	public static JSONArray getAttachments1(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getAttachments(type,id);
			Sage db = new Sage();
			db.query(command);
			
			int i =0;
			while(db.next()){
				i = i+1;
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("order", 1);
				ob.put("type", "String");
				ob.put("field", db.getString("CREATED_DATE"));
				ob.put("value", db.getString("TITLE"));
				ob.put("link", "http://file2/DL/"+db.getString("TITLE"));
				ob.put("target", "_blank");
				ob.put("alert", "");
				ob.put("required", "Y");
				obj.put(ob);
				
				
			}
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getCustomFields(String type,int id){
		return getCustomFields(type, id,"lso",10045);
	}
	
	
	public static JSONArray getCustomFieldsArray(String type,int typeid,String entity, int id){
		JSONArray c = new JSONArray();
		try{
			
			Sage db = new Sage();
			String command = GeneralSQL.getCustomFields(type,typeid);
			db.query(command);
			while(db.next()){
				c.put(getCustomFields(type, id, entity,db.getInt("FIELD_GROUPS_ID")));
			
			}
			
			db.clear();
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return c;
	}
	
	public static JSONObject getCustomFields(String type,int id,String entity, int formId){
		JSONArray obj = new JSONArray();
		JSONObject g = new JSONObject();
		
			//getGroups
		
		
			
			
		try{
		
			String command = GeneralSQL.getCustomFields(type,id,formId);
			Sage db = new Sage();
			db.query(command);
			
			if(type.equals("L") || type.equals("S") || type.equals("O")){
				if(db.SIZE<=0){
					db.clear();
					command = GeneralSQL.getCustomFields("SITE",id,formId);
					db.query(command);
				}
			}
			
			boolean t = false;
			while(db.next()){
				
				if(t==false){
					g.put("id", db.getString("GROUP_ID"));
					g.put("groupid",db.getInt("GROUP_ID"));
					g.put("pub", "Y");
					g.put("group",db.getString("GROUP_NAME"));
					g.put("cols",2);
					g.put("editurl", Nav.getFormEditUrl(type,id,"custom_"));
					g.put("type",entity);
					t=true;
				}
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("order", 1);
				ob.put("fieldid",  db.getInt("ID"));
				ob.put("type", "String");
				ob.put("field", db.getString("NAME"));
				ob.put("value", db.getString("VALUE"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				obj.put(ob);
				
				
			}
			g.put("obj",obj);
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONObject getConditions1(String type,int id){
		JSONObject g = new JSONObject();
		JSONArray obj = new JSONArray();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		
		g.put("group","CONDITON DETAILS");
		
		String command = GeneralSQL.getConditions(type,id);
		Sage db = new Sage();
		db.query(command);
		boolean t = false;
		

		
		
		while(db.next()){
			
			
			JSONObject ob = new JSONObject();
			ob.put("id", db.getInt("ID"));
			ob.put("order", 1);
			ob.put("type", "String");
			ob.put("field", db.getString("CREATED_DATE"));
			ob.put("value", db.getString("SHORT_TEXT"));
			ob.put("link", "");
			ob.put("alert", "");
			ob.put("required", "Y");
			obj.put(ob);
			
			
		
			
			
		}
		g.put("obj",obj);
		
		db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	public static JSONObject getHolds1(String type,int id){
		JSONObject g = new JSONObject();
		JSONArray obj = new JSONArray();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("cols", "3");
		g.put("group","HOLD DETAILS");
		
		String command = GeneralSQL.getHolds(type,id);
		Sage db = new Sage();
		db.query(command);
		boolean t = false;
		
		JSONObject ob = new JSONObject();
		ob = new JSONObject();
		ob.put("id", db.getInt("ID"));
		ob.put("order", 1);
		ob.put("type", "String");
		ob.put("field", "TYPE");
		ob.put("value", "DESCRITPION");
		ob.put("link", "");
		ob.put("alert", "");
		ob.put("required", "Y");
		obj.put(ob);
		
		ob = new JSONObject();
		ob.put("id", db.getInt("ID"));
		ob.put("order", 1);
		ob.put("type", "String");
		ob.put("field", "CREATED BY");
		ob.put("value", "CREATED DATE");
		ob.put("link", "");
		ob.put("alert", "");
		ob.put("required", "Y");
		obj.put(ob);
		
		
		ob = new JSONObject();
		ob.put("id", db.getInt("ID"));
		ob.put("order", 1);
		ob.put("type", "String");
		ob.put("field", "UPDATED BY");
		ob.put("value", "UPDATED DATE");
		ob.put("link", "");
		ob.put("alert", "");
		ob.put("required", "Y");
		obj.put(ob);
		
		while(db.next()){
			
			
			ob = new JSONObject();
			ob.put("id", db.getInt("ID"));
			ob.put("order", 1);
			ob.put("type", "String");
			ob.put("field", db.getString("HOLD"));
			ob.put("value", db.getString("DESCRIPTION"));
			ob.put("link", "");
			ob.put("alert", "");
			ob.put("required", "Y");
			obj.put(ob);
			
			ob = new JSONObject();
			ob.put("id", db.getInt("ID"));
			ob.put("order", 1);
			ob.put("type", "String");
			ob.put("field", db.getString("CREATED"));
			ob.put("value", db.getString("CREATED_DATE"));
			
			ob.put("link", "");
			ob.put("alert", "");
			ob.put("required", "Y");
			obj.put(ob);
			
			ob = new JSONObject();
			ob.put("id", db.getInt("ID"));
			ob.put("order", 1);
			ob.put("type", "String");
			
			ob.put("field", db.getString("UPDATED"));
			ob.put("value", db.getString("UPDATED_DATE"));
			ob.put("link", "");
			ob.put("alert", "");
			ob.put("required", "Y");
			obj.put(ob);
			
			/*ob = new JSONObject();
			ob.put("id", db.getInt("ID"));
			ob.put("order", 1);
			ob.put("type", "String");
			ob.put("field", "Updated");
			ob.put("value", db.getString("UPDATED_DATE"));
			ob.put("link", "");
			ob.put("alert", "");
			ob.put("required", "Y");
			obj.put(ob);*/
			
		/*	ob = new JSONObject();
			obj.put(ob);
			ob = new JSONObject();
			obj.put(ob);*/
			
			
			
		}
		g.put("obj",obj);
		
		db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONObject getComments(String type,int id){
		JSONObject g = new JSONObject();
		try{
		
			
			
			
			
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("group","COMMENTS");
		g.put("alert","");
		g.put("addurl", Nav.getFormEditUrl(type,0,"comments_"));
		
		
		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("DATE","DATE");
		f.put("NOTE","NOTE");
		//f.put("UPDATED_BY","UPDATED_BY");
		
		fields.put(f.get("DATE"));
		fields.put(f.get("NOTE"));
		//fields.put(f.get("UPDATED_BY"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("DATE"));
		index.put(f.get("NOTE"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("values",getCommentsArray(type,id));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getCommentsArray(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getComments(type, id);
			Sage db = new Sage();
			db.query(command);
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("field", "");
				ob.put("id", db.getInt("ID"));
				/*ob.put("editurl", Nav.getFormEditUrl(type,db.getInt("ID"),"comments_"));
				ob.put("deleteurl", Nav.getFormDeleteUrl(type,db.getInt("ID"),"comments_"));
				*/ob.put("alert", "");
				
				JSONObject f = new JSONObject();
				

				JSONObject v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "DATE");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("DATE"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", db.getString("DATE"));
				
				f.put("DATE", v);
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "NOTE");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("NOTE"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				
				f.put("NOTE", v);
				
				ob.put("values", f);
				
				obj.put(ob);
				
				
			}
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	
	public static JSONObject getAttachments(String type,int id){
		JSONObject g = new JSONObject();
		try{
			
		g.put("id", 1);
		g.put("pub", "Y");
		
		if(type.equalsIgnoreCase("L") || type.equalsIgnoreCase("S") || type.equalsIgnoreCase("O")){
			g.put("group",Table.REFLSOATTACHMENTSTABLE);	
		}
		
        if(type.equalsIgnoreCase("P") ){
        	g.put("group",Table.REFPROJECTATTACHMENTSTABLE);	
		}
        
        if(type.equalsIgnoreCase("A") ){
        	g.put("group",Table.REFACTATTACHMENTSTABLE);	
		}
        
        if(type.equalsIgnoreCase("D") ){
        	g.put("group",Table.REFPEOPLEATTACHMENTSTABLE);
		}

		
		g.put("groupid","-1");
		g.put("alert","");
		g.put("addurl", Nav.getFormEditUrl(type,0,"attach_"));

		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("CREATED","CREATED");
		f.put("TITLE","TITLE");
		//f.put("UPDATED_BY","UPDATED_BY");
		
		fields.put(f.get("CREATED"));
		fields.put(f.get("TITLE"));
		//fields.put(f.get("UPDATED_BY"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("CREATED"));
		index.put(f.get("TITLE"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("values",getAttachmentsArray(type,id));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getAttachmentsArray(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getAttachments(type, id);
			Sage db = new Sage();
			db.query(command);
			
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("field", "");
				ob.put("id", db.getInt("ID"));
			/*	ob.put("editurl", Nav.getFormEditUrl(type,db.getInt("ID"),"attach_"));
				ob.put("deleteurl", Nav.getFormDeleteUrl(type,db.getInt("ID"),"attach_"));*/
				ob.put("alert", "");
				
				JSONObject f = new JSONObject();
				

				JSONObject v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "CREATED");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("CREATED_DATE"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", db.getString("CREATED_DATE"));
				
				f.put("CREATED", v);
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "TITLE");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("TITLE"));
				v.put("alert", "");
				v.put("link", "http://file2/DL/".concat(db.getString("TITLE")));
				v.put("integer", -1);
				v.put("date", "");
				v.put("target", "_blank");
				f.put("TITLE", v);
				
				ob.put("values", f);
				
				obj.put(ob);
				
				
			}
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getConditions(String type,int id){
		JSONObject g = new JSONObject();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("group","CONDITIONS");
		g.put("groupid","-1");
		g.put("alert","");
		g.put("addurl", Nav.getFormEditUrl(type,0,"cond_"));
		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("CREATED","CREATED");
		f.put("DESCRIPTION","DESCRIPTION");
		//f.put("UPDATED_BY","UPDATED_BY");
		
		fields.put(f.get("CREATED"));
		fields.put(f.get("DESCRIPTION"));
		//fields.put(f.get("UPDATED_BY"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("CREATED"));
		index.put(f.get("DESCRIPTION"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("values",getConditionsArray(type,id));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getConditionsArray(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getConditions(type, id);
			Sage db = new Sage();
			db.query(command);
			
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("field", "");
				/*ob.put("editurl", Nav.getFormEditUrl(type,db.getInt("ID"),"cond_"));
				ob.put("deleteurl", Nav.getFormDeleteUrl(type,db.getInt("ID"),"cond_"));*/
				ob.put("id", db.getInt("ID"));
				ob.put("alert", "");
				
				JSONObject f = new JSONObject();
				

				JSONObject v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "CREATED");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("CREATED_DATE"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", db.getString("CREATED_DATE"));
				
				f.put("CREATED", v);
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "DESCRIPTION");
				v.put("type", "String");
				//v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("SHORT_TEXT"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				
				f.put("DESCRIPTION", v);
				
				ob.put("values", f);
				
				obj.put(ob);
				
				
			}
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getHolds(String type,int id){
		JSONObject g = new JSONObject();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("group","HOLDS");
		g.put("groupid","-1");
		g.put("alert","");
		g.put("addurl", Nav.getFormEditUrl(type,0,"hold_"));
		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("HOLD","HOLD");
		f.put("DESCRIPTION","DESCRIPTION");
		//f.put("UPDATED_BY","UPDATED_BY");
		
		fields.put(f.get("HOLD"));
		fields.put(f.get("DESCRIPTION"));
		//fields.put(f.get("UPDATED_BY"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("HOLD"));
		index.put(f.get("DESCRIPTION"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("values",getHoldBackArray(type,id));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getHoldBackArray(String type,int id){
		JSONArray obj = new JSONArray();
		try{
			String command = GeneralSQL.getHolds(type, id);
			Sage db = new Sage();
			db.query(command);
			
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("field", "");
				
				ob.put("id", db.getInt("ID"));
				
				
				ob.put("alert", "");
				
				JSONObject f = new JSONObject();
				

				JSONObject v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "HOLD");
				v.put("type", "String");
				v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("HOLD"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				
				f.put("HOLD", v);
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "DESCRIPTION");
				v.put("type", "String");
				v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("DESCRIPTION"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				
				f.put("DESCRIPTION", v);
				
				ob.put("values", f);
				
				obj.put(ob);
				
				
			}
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getHold(String type,int id){
		JSONArray obj = new JSONArray();
		JSONObject g = new JSONObject();
		
			//getGroups
			
			
			
		try{
			
			g.put("id", 1);
			g.put("pub", "Y");
			g.put("group","HOLDS");
			g.put("alert","");
			//g.put("addurl", Nav.getFormEditUrl(type,0,"hold_"));
			
			
			String command = GeneralSQL.getHolds(type,id);
			Sage db = new Sage();
			db.query(command);
			while(db.next()){
				
				JSONObject v = new JSONObject();
				v.put("id", 1);
				v.put("field", "HOLD");
				v.put("type", "String");
				v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("HOLD"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				obj.put(v);
				
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "DESCRIPTION");
				v.put("type", "String");
				v.put("itype", "String");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", db.getString("DESCRIPTION"));
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				obj.put(v);
				
			}
			g.put("obj",obj);
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	
	public static JSONObject getComment(String type,int id,String entity, int formId, boolean fields){
		JSONArray obj = new JSONArray();
		JSONObject g = new JSONObject();
		
			//getGroups
			
			
			
		try{
			
			g.put("id", 1);
			g.put("pub", "Y");
			g.put("group","COMMENTS");
			g.put("alert","");
			//g.put("addurl", Nav.getFormEditUrl(type,0,"hold_"));
			
			if(!fields){
				String command = GeneralSQL.getComments(type,id,formId);
				Sage db = new Sage();
				db.query(command);
				while(db.next()){
					
					JSONObject v = new JSONObject();
					v.put("id", 1);
					v.put("field", "DATE");
					v.put("fieldid", "DATE");
					v.put("type", "DATE");
					v.put("itype", "DATE");
					v.put("order", 1);
					v.put("required", "N");
					v.put("value", db.getString("DATE"));
					v.put("alert", "");
					v.put("link", "");
					v.put("integer", -1);
					v.put("date", "");
					obj.put(v);
					
					
					v = new JSONObject();	
					v.put("id", 1);
					v.put("field", "PUBLIC");
					v.put("fieldid", "PUBLIC");
					v.put("type", "boolean");
					v.put("itype", "boolean");
					v.put("order", 1);
					v.put("required", "N");
					v.put("value", db.getString("NOTE"));
					v.put("alert", "");
					v.put("link", "");
					v.put("integer", -1);
					v.put("date", "");
					obj.put(v);
					
					v = new JSONObject();	
					v.put("id", 1);
					v.put("field", "NOTE");
					v.put("fieldid", "NOTE");
					v.put("type", "String");
					v.put("itype", "textarea");
					v.put("order", 1);
					v.put("required", "N");
					v.put("value", db.getString("NOTE"));
					v.put("alert", "");
					v.put("link", "");
					v.put("integer", -1);
					v.put("date", "");
					obj.put(v);
					
					
					
					
				}
				g.put("obj",obj);
				
				db.clear();
			
			}else {
				JSONObject v = new JSONObject();
				v.put("id", 1);
				v.put("field", "DATE");
				v.put("fieldid", "DATE");
				v.put("type", "DATE");
				v.put("itype", "DATE");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", "");
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				obj.put(v);
				
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "PUBLIC");
				v.put("fieldid", "PUBLIC");
				v.put("type", "boolean");
				v.put("itype", "boolean");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", "");
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				obj.put(v);
				
				v = new JSONObject();	
				v.put("id", 1);
				v.put("field", "NOTE");
				v.put("fieldid", "NOTE");
				v.put("type", "String");
				v.put("itype", "textarea");
				v.put("order", 1);
				v.put("required", "N");
				v.put("value", "");
				v.put("alert", "");
				v.put("link", "");
				v.put("integer", -1);
				v.put("date", "");
				obj.put(v);
				
				g.put("obj",obj);
			}
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	
	//NEW METHODS to handle generic methods
	
	
	
	public static boolean saveNote(String type, int typeid, ObjGroupVO vo, Token u){
		boolean result= false;
		try{
			int groupid = Operator.toInt(vo.getGroupid());
			ObjVO[] obj = vo.getObj();
			String command ="";
			Sage db = new Sage();
			if(groupid>0){
				command = ObjSQL.updateNote(groupid, obj, u.getId());
				result = db.update(command);
			}
			else {
				command = ObjSQL.insertNote(obj, u.getId());
				result = db.update(command);
				
				if(result){
					command = ObjSQL.getNoteId(obj, u.getId());
					int noteId = 0;
					db.query(command);
					if(db.next()){
						noteId = db.getInt("ID");
					}
					Logger.info(type);
					Logger.info(typeid);
					Logger.info(noteId);
					command = ObjSQL.insertNoteRef(type, typeid, noteId, u.getId());
					result = db.update(command);
					
				}
			}
			db.clear();
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		return result;
	}
	
	
	
	
	/** sunil
	 * @param id
	 * @param type
	 * @param typeid
	 * @param vo
	 * @param u
	 * @return
	 */
	public static boolean saveCustom(int id, String type, int typeid, ObjGroupVO vo, Token u){
		boolean result= false;
		try{
			int groupid = Operator.toInt(vo.getGroupid());
			String multi = CustomAgent.getGroupMulti(groupid);
			boolean updatealltime = true;
			int setId =0;
			Logger.info("********"+multi);
			Logger.info("********"+id);
			if(multi.equalsIgnoreCase("N")){
				id = 0;
			}else {
				if(id>0){
					setId = id;	
				}else {
					setId = CustomAgent.getSetId(type, typeid, groupid);	
					updatealltime = false;
					result =true;
				}
				
			}
			Logger.info("********"+setId);
			ObjVO[] obj = vo.getObj();
			
			String fields = "";
			if(groupid>0){
				Sage db1 = new Sage();
				db1.query("select stuff((select  ','+convert(varchar(100),id) from FIELD where FIELD_GROUPS_ID="+groupid+"  for xml path('')),1,1,'') as COMBINED");
				if(db1.next()){
					fields = db1.getString("COMBINED");
				}
				db1.clear();
			}
			
			String command ="";
			// deactivate custom
			if(obj.length>0 && typeid>0 && updatealltime){
				Sage db = new Sage();
				command = CustomSQL.inActiveCustom(type, typeid,id,fields);
				Logger.info("********"+command);
				result = db.update(command);
				db.clear();
			}
			
			//insert custom
			if(result){
				result = new DBBatch().insertCustom(type, typeid, obj, u.getId(),setId);
			}
			
			//BACKUP TO HISTORY & DELETE TABLE EXISTS
			if(result && updatealltime){
				Sage db = new Sage();
				command = CustomSQL.backUpCustom(type, typeid,id,fields);
				Logger.info("********"+command);
				result = db.update(command);
				
				
				command = CustomSQL.deleteCustom(type, typeid,id,fields);
				Logger.info("********"+command);
				result = db.update(command);
				
				
				db.clear();
				
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}
	
	
	public static boolean saveAttachment(String type, int typeid, ObjGroupVO vo, Token u){
		boolean result= false;
		try{
			int groupid = Operator.toInt(vo.getGroupid());
			ObjVO[] obj = vo.getObj();
			//Logger.info("********"+id);
			String command ="";
			Sage db = new Sage();
			if(groupid>0){
				command = ObjSQL.updateAttachment(groupid, obj, u.getId());
				Logger.info("********"+command);
				result = db.update(command);
				
			}else {
				
				command = ObjSQL.insertAttachment(obj, u.getId());
				Logger.info("********"+command);
				result = db.update(command);
				
				if(result){
					command = ObjSQL.getAttachmentId(obj, u.getId());
					Logger.info("********"+command);
					int attachId = 0;
					db.query(command);
					if(db.next()){
						attachId = db.getInt("ID");
						
						
					}
					Logger.info(type);
					Logger.info(typeid);
					Logger.info(attachId);
					command = ObjSQL.insertAttachRef(type, typeid, attachId, u.getId());
					Logger.info("********"+command);
					result = db.update(command);
					
				}
			}
			
			db.clear();
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}
	
	public static boolean saveCommon(RequestVO r, Token u){
		boolean result= false;
		try{
			int id = Operator.toInt(r.getId());
			String command ="";
			Sage db = new Sage();
			if(id>0){
				command = CsReflect.commonQuery("update",r, u);
				result = db.update(command);
				if (result) {
					CsReflect.addHistory(r.getType(), r.getTypeid(), r.getGrouptype(), id, "update");
				}
				
			} else {
				command = CsReflect.commonQuery("insert",r, u);
				if(Operator.hasValue(command)){
					result = db.update(command);
					
					if(result){
						command = CsReflect.commonQuery("getRefId",r,u);
						int newId = 0;
						if(Operator.hasValue(command)){
							db.query(command);
							if(db.next()){
								newId = db.getInt("ID");
							}
							
							if(newId>0){
								command = CsReflect.commonQuery("insertRef",r,u,newId);
								result = db.update(command);
								CsReflect.addHistory(r.getType(), r.getTypeid(), r.getGrouptype(), newId, "add");
							}
						}
						
						
					}
				}
				
				
			}
			
			db.clear();
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}

	public static boolean deleteRef(String type, int typeid, String grouptype, int id, int userid) {
		boolean b = false;
		String command = CsReflect.deleteRefQuery(type, typeid, grouptype, id, userid);
		if (!Operator.hasValue(command)) {
			command = GeneralSQL.deleteRef(type, typeid, grouptype, id, userid);
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			b = db.update(command);
			db.clear();
		}
		return b;
	}

	public static boolean delete(String type, int id, int userid) {
		boolean b = false;
		String command = CsReflect.deleteQuery(type, id, userid);
		if (!Operator.hasValue(command)) {
			command = GeneralSQL.delete(type, id, userid);
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			b = db.update(command);
			db.clear();
		}
		return b;
	}

	/**
	 * @deprecated Use HoldsAgent.getAlert(type, typeId)
	 */
	public static String getAlert(String type, int typeId){
		String alert = HoldsAgent.getAlert(type, typeId);
		/*if(!Operator.hasValue(alert)){
			
		}*/
		return alert;
	}
	
	
	public static String getNumber(RequestVO r){
	/*	DataVO m = DataVO.toDataVO(r);
		String type = r.getGrouptype();
		String tableref = CsReflect.getTableRef(type);
		int type_typeid = m.getInt("LKUP_"+tableref+"_TYPE_ID");*/
		return "";
	}

	public static String getNumber(String type, int type_typeid,int outputid) {
		Sage db = new Sage();
		
		String command = GeneralSQL.getPatternId(type,type_typeid);
		db.query(command);
		String pattern ="X";
		int patterId = 0;
		int gcount = 1;
		boolean year = false;
		boolean month = false;
		boolean day = false;
		if(db.next()){
			pattern = db.getString("PATTERN");
			patterId = db.getInt("ID");
			year = Operator.s2b(db.getString("PATTERN_YEAR"));
			month = Operator.s2b(db.getString("PATTERN_MONTH"));
			day = Operator.s2b(db.getString("PATTERN_DAY"));
		}
		
		
		
		
		if(patterId>0){
			
			//TODO change randomacttype today
			if(pattern.startsWith("RANDOMACTTODAYTYPE_2")){
				String random = "";
				boolean x = true;
				gcount=1;
				while(x){
					random = Operator.randomString(2).toUpperCase();
					if(random.indexOf("X")>=0){
						x = true;
					}else {
						x = false;
					}
				}
				pattern = random+"-XXX";
				Logger.info(pattern+"RANDOM_______________"+random);
				
				
				//command = "select DATEPART(HOUR, START_DATE) AS HR, CASE WHEN DATEPART(HOUR, START_DATE) < 5 THEN CONVERT(VARCHAR(50), START_DATE-1, 101) else  CONVERT(VARCHAR(50), START_DATE, 101) END as START_DATE , CASE WHEN DATEPART(HOUR, START_DATE) < 5 THEN CONVERT(VARCHAR(50), START_DATE , 101) ELSE '' end  as START_DATE_PLUS  from ACTIVITY WHERE ID = "+outputid;
				command = "select DATEPART(HOUR, START_DATE) AS HR,CONVERT(VARCHAR(50), START_DATE, 101) as START_DATE from ACTIVITY WHERE ID = "+outputid;

				db.query(command);
				String start_date = "";
				
				
				int hr = 0;
				if(db.next()){
					start_date = db.getString("START_DATE");
					hr = db.getInt("HR");
				}
				
				if(type_typeid==255){
					String start_date_mod = "";
					
					if(hr>=5){
						command = "select CONVERT(VARCHAR(50), START_DATE, 111) as START_DATE , CONVERT(VARCHAR(50), START_DATE+1, 111) as START_DATE_MOD  from ACTIVITY WHERE ID = "+outputid;
					}	else {
						command = "select CONVERT(VARCHAR(50), START_DATE-1, 111)  as START_DATE , CONVERT(VARCHAR(50), START_DATE, 111) as START_DATE_MOD  from ACTIVITY WHERE ID = "+outputid;
					}
					
					db.query(command);
					if(db.next()){
						start_date = db.getString("START_DATE")+" 04:59:59";
						start_date_mod = db.getString("START_DATE_MOD")+" 05:00:00";
					}
					StringBuilder sb2 = new StringBuilder();
					sb2.append(" WITH Q AS ( ");
					sb2.append(" 	SELECT ");
					sb2.append(" 		TOP 1 ");
					sb2.append(" 		0 AS COUNT, ");
					sb2.append(" 		SUBSTRING(ACT_NBR,1,2) AS RANDOM, ");
					sb2.append(" 		CREATED_DATE ");
					sb2.append(" 	FROM ");
					sb2.append(" 		ACTIVITY ");
					sb2.append(" 	WHERE ");
					sb2.append(" 		START_DATE BETWEEN '").append(start_date).append("' AND '").append(start_date_mod).append("' ");
					sb2.append(" 		AND ");
					sb2.append(" 		LKUP_ACT_TYPE_ID = ").append(type_typeid).append(" ");
					sb2.append(" 		AND ");
					sb2.append(" 		ACT_NBR IS NOT NULL AND ACT_NBR <> '' ");
					sb2.append(" 	ORDER BY ");
					sb2.append(" 		CREATED_DATE DESC ");
					sb2.append(" ) ");
					sb2.append("  ");
					sb2.append("  ");
					sb2.append(" SELECT ");
					sb2.append(" 	COUNT(*) +1 as COUNT, ");
					sb2.append(" 	'' as RANDOM ");
					sb2.append(" FROM ");
					sb2.append(" 	ACTIVITY ");
					sb2.append(" WHERE ");
					sb2.append(" 	START_DATE BETWEEN '").append(start_date).append("' AND '").append(start_date_mod).append("' ");
					sb2.append(" 	and ");
					sb2.append(" 	LKUP_ACT_TYPE_ID = ").append(type_typeid).append(" ");
					sb2.append(" UNION ");
					sb2.append(" SELECT ");
					sb2.append(" 	COUNT, ");
					sb2.append(" 	RANDOM ");
					sb2.append(" FROM ");
					sb2.append(" 	Q ");


//					sb2.append(" SELECT    COUNT(*) +1  as COUNT, '' as RANDOM  FROM  ACTIVITY WHERE START_DATE BETWEEN  '").append(start_date).append("' AND  '").append(start_date_mod).append("'  and LKUP_ACT_TYPE_ID= ").append(type_typeid).append(" ");
//					sb2.append(" union  ");
//					sb2.append(" SELECT   TOP 1 0 as count, SUBSTRING(ACT_NBR,1,2) as RANDOM   FROM  ACTIVITY WHERE START_DATE BETWEEN  '").append(start_date).append("' AND  '").append(start_date_mod).append("' and LKUP_ACT_TYPE_ID= ").append(type_typeid).append(" ");
					command = sb2.toString();
				
				}
				
				else if(type_typeid==253){
				
					String start_date_mod = "";
					
					if(hr>=3){
						command = "select CONVERT(VARCHAR(50), START_DATE, 111) as START_DATE , CONVERT(VARCHAR(50), START_DATE+1, 111) as START_DATE_MOD  from ACTIVITY WHERE ID = "+outputid;
					}	else {
						command = "select CONVERT(VARCHAR(50), START_DATE-1, 111)  as START_DATE , CONVERT(VARCHAR(50), START_DATE, 111) as START_DATE_MOD  from ACTIVITY WHERE ID = "+outputid;
					}
					
					db.query(command);
					if(db.next()){
						start_date = db.getString("START_DATE")+" 02:59:59";
						start_date_mod = db.getString("START_DATE_MOD")+" 03:00:00";
					}
					StringBuilder sb2 = new StringBuilder();
					sb2.append(" WITH Q AS ( ");
					sb2.append(" 	SELECT ");
					sb2.append(" 		TOP 1 ");
					sb2.append(" 		0 AS COUNT, ");
					sb2.append(" 		SUBSTRING(ACT_NBR,1,2) AS RANDOM, ");
					sb2.append(" 		CREATED_DATE ");
					sb2.append(" 	FROM ");
					sb2.append(" 		ACTIVITY ");
					sb2.append(" 	WHERE ");
					sb2.append(" 		START_DATE BETWEEN '").append(start_date).append("' AND '").append(start_date_mod).append("' ");
					sb2.append(" 		AND ");
					sb2.append(" 		LKUP_ACT_TYPE_ID = ").append(type_typeid).append(" ");
					sb2.append(" 		AND ");
					sb2.append(" 		ACT_NBR IS NOT NULL AND ACT_NBR <> '' ");
					sb2.append(" 	ORDER BY ");
					sb2.append(" 		CREATED_DATE DESC ");
					sb2.append(" ) ");
					sb2.append(" SELECT ");
					sb2.append(" 	COUNT(*) +1 as COUNT, ");
					sb2.append(" 	'' as RANDOM ");
					sb2.append(" FROM ");
					sb2.append(" 	ACTIVITY ");
					sb2.append(" WHERE ");
					sb2.append(" 	START_DATE BETWEEN '").append(start_date).append("' AND '").append(start_date_mod).append("' ");
					sb2.append(" 	and ");
					sb2.append(" 	LKUP_ACT_TYPE_ID = ").append(type_typeid).append(" ");
					sb2.append(" UNION ");
					sb2.append(" SELECT ");
					sb2.append(" 	Q.COUNT, ");
					sb2.append(" 	Q.RANDOM ");
					sb2.append(" FROM ");
					sb2.append(" 	Q ");

//					sb2.append(" SELECT    COUNT(*) +1  as COUNT, '' as RANDOM  FROM  ACTIVITY WHERE START_DATE BETWEEN  '").append(start_date).append("' AND  '").append(start_date_mod).append("'  and LKUP_ACT_TYPE_ID= ").append(type_typeid).append(" ");
//					sb2.append(" union  ");
//					sb2.append(" SELECT   TOP 1 0 as count, SUBSTRING(ACT_NBR,1,2) as RANDOM   FROM  ACTIVITY WHERE START_DATE BETWEEN  '").append(start_date).append("' AND  '").append(start_date_mod).append("' and LKUP_ACT_TYPE_ID= ").append(type_typeid).append(" ");
					command = sb2.toString();
				
				}
				
				else {
					command = ActivitySQL.getActNbrByType(type_typeid,start_date);
					
				
				}
				db.query(command);
				while(db.next()){
					if(db.getInt("COUNT")>1 ){
						gcount = db.getInt("COUNT");
					}
					Logger.highlight(db.getString("RANDOM")+"############");
					if(Operator.hasValue(db.getString("RANDOM"))){
						String dbrand = db.getString("RANDOM");
						x = true;
						while(x){
							
							if(dbrand.indexOf("X")>=0){
								x = true;
								dbrand = Operator.randomString(2).toUpperCase();
							}else {
								x = false;
							}
						}
						
						pattern = dbrand+"-XXX";
					}
					
				}
				
				Logger.info(pattern+"RANDOM____DB _____"+random);
				
			}else {
			
			
				command = GeneralSQL.getPatternId(type,patterId,year,month,day);
				db.query(command);
				if(db.next()){
					gcount = db.getInt("G_PATTERN_ID");
					
				}
			}
		}
		
		db.clear();
		
		
		if(patterId<=0){
			return outputid+"";
		}
		
		Logger.info("pattern"+pattern);
		
		pattern = getNumber(gcount,pattern);
		
		Logger.info("pattern after "+pattern);
		
		
		/*if(pattern.startsWith("RANDOMACTTODAYTYPE_2")){
			String random = Operator.randomString(2).toUpperCase();
			pattern = Operator.replace(pattern, "RANDOMACTTODAYTYPE_2", random);	
		}*/
	
		
		
		
		
		return pattern;
	}
	
	
	public static String getNumber(int id,String pattern){
		//String pattern = "RANDOMTODAYTYPE_2_XXX";// FOR DOE
		try{
			if(Operator.hasValue(pattern)&& id>0){
				Timekeeper k = new Timekeeper();
				Logger.info(pattern+"##############"+id);
				Pattern p = Pattern.compile("\\[(.*?)\\]");
				Matcher m = p.matcher(pattern);
				while (m.find()) {
					String rep =  m.group();
					if(rep.equalsIgnoreCase("[YY]")){
						pattern = Operator.replace(pattern, "[YY]", k.yy());
					}
					
					if(rep.equalsIgnoreCase("[YYYY]")){
						pattern = Operator.replace(pattern, "[YYYY]", k.yyyy());
					}
					if(rep.equalsIgnoreCase("[MM]")){
						pattern = Operator.replace(pattern, "[MM]", k.mm());
					}
					if(rep.equalsIgnoreCase("[DD]")){
						pattern = Operator.replace(pattern, "[DD]", k.dd());
					}
					if(rep.indexOf("R")>0){
						int rcount = Operator.subString(rep, 1, rep.length()-1).length();	
						String random = Operator.randomString(rcount).toUpperCase();
						pattern = Operator.replace(pattern, rep, random);
					}
					
				}
				
				int count = StringUtils.countMatches(pattern, "X");
				Logger.info(pattern+"##############"+id+"#######"+count);
				StringBuilder x = new StringBuilder();
				for(int i=0;i<count;i++){
					x.append("X");
				}
				
				
				String format = String.format("%%0%dd", count);
				String result = String.format(format, id);
				pattern = Operator.replace(pattern, x.toString(), result);
				pattern = Operator.replace(pattern, "_", "");
			}else {
				pattern = id+"";
			}
			
		} catch (Exception e){
			pattern = pattern +id+"";
			Logger.info(e.getMessage());
		}
		return pattern;
	}
	
	
	public static void main(String args[]){
		/*String pattern = "[RRR]XXX";
		int id = 999;
		System.out.println(getNumber(id, pattern));*/
		
		boolean x = true;
		
		String random = "XT";
		//System.out.println(random.indexOf("X")){
		while(x){
			random = Operator.randomString(2).toUpperCase();	
			if(random.indexOf("X")>=0){
				x = true;
				
			}else {
				x = false;
			}
		}
		
		System.out.println(random);
	}
	
	
	
	
	
	public static  JSONObject getConfiguration(String name){
		JSONObject o = new JSONObject();
		try{
			
			String command = "select LC.NAME,C.C_VALUE as VALUE from CONFIGURATION_GROUP CG JOIN CONFIGURATION C on CG.ID=C.CONFIGURATION_GROUP_ID JOIN LKUP_CONFIGURATION  LC on C.LKUP_CONFIGURATION_ID=LC.ID WHERE CG.GROUP_NAME='"+name+"' AND CG.ACTIVE='Y' AND C.ACTIVE='Y' ";
			Sage db = new Sage();
			db.query(command);
			//HashMap<String, String> a = new HashMap<String, String>();
			while(db.next()){
				o.put(db.getString("NAME"),db.getString("VALUE"));
			
			}
			
		 db.clear();
		// o = new JSONObject(a);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return o;
		
	}
	
	public static ObjGroupVO setValues(String type, String command) {
		ObjGroupVO vo = CsReflect.getDetailFields(type);
		return setValues(vo, command);
	}

	public static ObjGroupVO setValues(ObjGroupVO vo, String command) {
		ObjGroupVO g = new ObjGroupVO();
		if (Operator.hasValue(command)) {
			ObjVO[] oa = vo.getObj();
			if (oa.length > 0) {
				boolean empty = true;
				ArrayList<ObjVO> arr = new ArrayList<ObjVO>();
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					for (int i=0; i<oa.length; i++) {
						ObjVO o = oa[i];
						String name = o.getFieldid();
						String value = db.getString(name);
						if (Operator.hasValue(value)) {
							o.setValue(value);
							arr.add(o);
							empty = false;
						}
					}
				}
				db.clear();
				if (!empty) {
					ObjVO[] oarr = arr.toArray(new ObjVO[arr.size()]);
					g.setObj(oarr);
				}
			}
		}
		return g;
	}

	public static String getLibraryGroupName(String type, int typeid) {
		String r = "Library";
		String command = GeneralSQL.getLibraryGroup(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			if (db.size() > 1) { r = "Library"; }
			else if (db.next()) {
				r = Operator.toTitleCase(db.getString("GROUP_NAME"));
			}
			db.clear();
		}
		return r;
	}

}


















