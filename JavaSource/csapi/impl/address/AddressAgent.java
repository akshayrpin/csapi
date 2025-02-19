package csapi.impl.address;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
















import csapi.common.Choices;
import csapi.common.FieldObjects;
import csapi.utils.Nav;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import alain.core.db.Sage;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class AddressAgent {

	
	public static JSONObject getChildren(int id){
		JSONObject o = new JSONObject();
		try{
			
			JSONObject h = new JSONObject();
			h.put("search", Config.rooturl()+"/cs/lso/search.jsp");
			h.put("label", "LSO BROWSER");
			h.put("dataid",id);
			h.put("menu",id);
			h.put("parent", id);
			JSONArray optHeader =  new JSONArray();
			h.put("options", optHeader);
			
			
			String command = AddressSQL.getChildren(id,"Y");
			Sage db = new Sage();
			db.query(command);
			JSONArray r = new JSONArray();
			JSONObject root = new JSONObject();
			JSONArray items = new JSONArray();
			while(db.next()){
				if(db.getInt("ID")==id){
					root.put("title",db.getString("title"));
					root.put("description",db.getString("description"));
					root.put("dataid",db.getString("id"));
					root.put("id",db.getString("id"));
					if(Operator.hasValue(db.getString("childrens"))){
						root.put("children",Config.rooturl()+"/cs/lso/childrens.jsp?id="+db.getString("id"));
					}
					root.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+db.getString("id"));
					
					root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lso&_id="+db.getString("id"));
					/*if(db.getInt("LKUP_LSO_TYPE_ID")==1){
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}else {
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}
					*/
					
					r.put(root);
					
				}else {
					JSONObject i = new JSONObject();
					i.put("title",db.getString("title"));
					i.put("description",db.getString("description"));
					i.put("dataid",db.getString("id"));
					i.put("id",db.getString("id"));
					if(Operator.hasValue(db.getString("childrens"))){
						i.put("children",Config.rooturl()+"/cs/lso/childrens.jsp?id="+db.getString("id"));
					}
					i.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+db.getString("id"));
					
					//if(db.getInt("LKUP_LSO_TYPE_ID")==1){
						i.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_type=lso&_id="+db.getString("id"));
					//}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
					//	i.put("link",Config.rooturl()+"/cs/details.jsp?type=S&id="+db.getString("id"));
					//}else {
						//i.put("link",Config.rooturl()+"/cs/details.jsp?type=O&id="+db.getString("id"));
					//}
					items.put(i);
				}
			}
			
			o.put("header", h);
			o.put("root", r);
			o.put("items", items);
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	
	public static JSONObject getDetailGroup(String type,int id){
		JSONObject g = new JSONObject();
		try{
			//getGroups
			g.put("id", id);
			g.put("pub", "Y");
			if(type.equalsIgnoreCase("L")){
				g.put("group","LAND DETAILS");
			}else if(type.equalsIgnoreCase("S")){
				g.put("group","STRUCTURE DETAILS");
			}else {
				g.put("group","OCCUPANCY DETAILS");
			}
			
			
			JSONArray obj = new JSONArray();
			
			String command = AddressSQL.getDetails(id,"Y");
			Sage db = new Sage();
			db.query(command);
			
			
			
			
			
			if(db.next()){
				JSONObject ob = new JSONObject();
				
				ob = new JSONObject();
				ob.put("id", 1);
				ob.put("order", 1);
				ob.put("fieldid", "DESCRIPTION");
				ob.put("type", "String");
				ob.put("itype", "text");
				ob.put("field", "Description");
				ob.put("value", db.getString("DESCRIPTION"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob); 
				
				
				
				ob = new JSONObject();
				ob.put("id", 2);
				ob.put("order", 2);
				ob.put("fieldid", "CREATED_DATE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "datetime");
				ob.put("itype", "uneditable");
				ob.put("field", "Created Date");
				ob.put("value", db.getString("CREATED_DATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				ob = new JSONObject();
				ob.put("id", 3);
				ob.put("order", 3);
				ob.put("fieldid", "ADDRESS");
				ob.put("type", "String");
				ob.put("itype", "text");
				ob.put("field", "Address");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("ADDRESS"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				ob = new JSONObject();
				ob.put("id", 4);
				ob.put("order", 4);
				ob.put("fieldid", "UPDATED_DATE");
				ob.put("type", "datetime");
				ob.put("itype", "uneditable");
				ob.put("field", "Updated Date");
				ob.put("value", db.getString("UPDATED_DATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				
				ob = new JSONObject();
				ob.put("id", 5);
				ob.put("order", 5);
				ob.put("fieldid", "CITY");
				ob.put("type", "String");
				ob.put("itype", "uneditable");
				ob.put("field", "City");
				ob.put("value", db.getString("CITY"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				
				
				
				ob = new JSONObject();
				ob.put("id", 6);
				ob.put("order", 6);
				ob.put("fieldid", "PRIMAR");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "boolean");
				ob.put("itype", "boolean");
				ob.put("field", "Primary");
				ob.put("value", db.getString("PRIMAR"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				ob.put("choices", Choices.getChoicesyn());
				obj.put(ob);
				
				
				//cc
				
				
				ob = new JSONObject();
				ob.put("id", 7);
				ob.put("order", 7);
				ob.put("fieldid", "STATE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("field", "State");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("STATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				//up
				
				
				ob = new JSONObject();
				ob.put("id", 8);
				ob.put("order", 8);
				ob.put("fieldid", "ACTIVE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "boolean");
				ob.put("itype", "boolean");
				ob.put("field", "Active");
				ob.put("value", db.getString("ACTIVE"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				ob.put("choices", Choices.getChoicesyn());
				
				
				
				obj.put(ob);
				
				
				ob = new JSONObject();
				ob.put("id", 9);
				ob.put("order", 9);
				ob.put("fieldid", "ZIP");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("field", "Zip");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("ZIP"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				ob = new JSONObject();
				ob.put("id", 9);
				ob.put("order", 9);
				ob.put("fieldid", "ID");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("itype", "uneditable");
				ob.put("field", "ID");
				ob.put("value", db.getString("ID"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				if(db.getInt("LKUP_LSO_TYPE_ID")==3){
					ob = new JSONObject();
					ob.put("id", 10);
					ob.put("fieldid", "UNIT");
					ob.put("order", 10);
					ob.put("type", "String");
					ob.put("field", "UNIT");
					ob.put("value", db.getString("UNIT"));
					ob.put("link", "");
					ob.put("alert", "");
					
					ob.put("required", "Y");
					obj.put(ob);
					
					g.put("unit",db.getString("UNIT"));
				}
				g.put("unit",db.getString("UNIT"));
				g.put("address",db.getString("ADDRESS"));
				g.put("lsoId",db.getInt("ID"));
				g.put("ctypeid", db.getInt("LKUP_LSO_TYPE_ID"));
			}
			g.put("obj",obj);
			
			/*if(db.getInt("LKUP_LSO_TYPE_ID")==1){
				g.put("editurl", Nav.getFormEditUrl("L",id,"landdetail_"));
			}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
				g.put("editurl", Nav.getFormEditUrl("S",id,"structdetail_"));
			}else {
				g.put("editurl", Nav.getFormEditUrl("O",id,"occdetail_"));
			}
			*/
			
			db.clear();
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	/*public static JSONArray getLandDetails(int id){
		JSONArray obj = new JSONArray();
		try{
			
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}*/
	
	public static JSONArray getDivisionsDetails(int id){
		JSONArray obj = new JSONArray();
		try{
			String command = AddressSQL.getDivisionsDetails(id);
			Sage db = new Sage();
			db.query(command);
			
			
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("order", 1);
				ob.put("type", "String");
				ob.put("field", db.getString("FIELD"));
				ob.put("value", db.getString("FIELD_VALUE"));
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
	
	
	public static ObjVO[] getDivisionsDetails2(int id){
		ObjVO[] obj= new ObjVO[0];
		try{
			String command = AddressSQL.getDivisionsDetails(id);
			Sage db = new Sage();
			db.query(command);
			int sz = db.size();
			if(sz>0){
				obj  = new ObjVO[sz];
			}
			int count =0 ;
			
			while(db.next()){
				obj[count] = (FieldObjects.getFieldObjectVO(db.getInt("ID"), 1, db.getString("ID"), "String", "String",  db.getString("FIELD"), db.getString("FIELD_VALUE"), "", "", "Y"));
				count++;
			}
		
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	
	public static JSONObject getSetBack(int id,int typeId){
		JSONObject g = new JSONObject();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("group","SETBACK DETAILS");
		
		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("FT","FT");
		f.put("INCH","INCH");
		f.put("COMMENTS","COMMENTS");
		
		fields.put(f.get("FT"));
		fields.put(f.get("INCH"));
		fields.put(f.get("COMMENTS"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("FT"));
		index.put(f.get("INCH"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("obj",getSetBackArray(id, typeId));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getSetBackArray(int id,int typeId){
		JSONArray obj = new JSONArray();
		try{
			
			obj.put(getSetBackDetails(id, typeId).get("FRONT"));
			obj.put(getSetBackDetails(id, typeId).get("REAR"));
			obj.put(getSetBackDetails(id, typeId).get("SIDE1"));
			obj.put(getSetBackDetails(id, typeId).get("SIDE2"));
			
			
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getSetBackDetails(int id,int typeId){
		JSONObject o = new JSONObject();
		try{
			String command = AddressSQL.getSetBack(id,typeId);
			Sage db = new Sage();
			db.query(command);
			
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("field", db.getString("TYPE"));
				ob.put("order", 1);
				ob.put("type", "Map");
				ob.put("required", "N");
				JSONObject f = new JSONObject();
				
				JSONObject v = new JSONObject();	
			
				/*StringBuilder sb = new StringBuilder();
				sb.append(db.getString("FT")).append("'").append(db.getString("INCHES")).append(" ").append(db.getString("COMMENTS"));*/
				
				v.put("value", db.getString("FT"));
				v.put("link", "");
				v.put("type", "String");
				f.put("FT", v);
				
				v = new JSONObject();	
				v.put("value", db.getString("INCHES"));
				v.put("link", "");
				v.put("type", "String");
				f.put("INCH", v);
				
				v = new JSONObject();	
				v.put("value", db.getString("COMMENTS"));
				v.put("link", "");
				v.put("type", "String");
				f.put("COMMENTS", v);
				
				ob.put("values",f);
				
				o.put(db.getString("TYPE"),ob);
			}
			
		
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static String getLsoType(int id){
		String type="";
		Sage db = new Sage();
		String command = AddressSQL.getLsoType(id);
		db.query(command);
		if(db.next()){
			type = db.getString("TYPE");
		}
		db.clear();
		return type;
	}
	
	public static ObjGroupVO getDetailGroup1(String type,int id){
		ObjGroupVO g = new ObjGroupVO();
		try{
			//getGroups
			//g.setGroupid(id+"");
			g.setPub("Y");
			
			if(type.equalsIgnoreCase("L")){
				g.setGroup("LAND DETAILS");
			}else if(type.equalsIgnoreCase("S")){
				g.setGroup("STRUCTURE DETAILS");
			}else {
				g.setGroup("OCCUPANCY DETAILS");
			}
			
			HashMap<String, String> extras = new HashMap<String, String>();
			ObjVO[] obj = new ObjVO[0];
			ArrayList<ObjVO> l = new ArrayList();
			
			String command = AddressSQL.getDetails(id,"Y");
			Sage db = new Sage();
			db.query(command);
			db.next();
			
			
			l.add(FieldObjects.getFieldObjectVO(1, 1, "DESCRIPTION", "String", "String", "Description", db.getString("DESCRIPTION"), "", "", "Y"));
			
			l.add(FieldObjects.getFieldObjectVO(1, 1, "CREATED_DATE", "datetime", "uneditable", "CREATED DATE", db.getString("CREATED_DATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ADDRESS", "String", "uneditable", "ADDRESS", db.getString("ADDRESS"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "UPDATED_DATE", "datetime", "uneditable", "UPDATED DATE", db.getString("UPDATED_DATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "CITY", "String", "uneditable", "CITY", db.getString("CITY"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "PRIMAR", "boolean", "boolean", "PRIMARY", db.getString("PRIMAR"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "STATE", "String", "uneditable", "STATE", db.getString("STATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ZIP", "String", "uneditable", "ZIP", db.getString("ZIP"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ID", "String", "uneditable", "ID", db.getString("ID"), "", "", "Y")); 
			if(db.getInt("LKUP_LSO_TYPE_ID")==3){
				l.add(FieldObjects.getFieldObjectVO(1, 1, "UNIT", "String", "", "UNIT", db.getString("UNIT"), "", "", "Y")); 
				extras.put("unit",db.getString("UNIT"));
			}
			extras.put("unit",db.getString("UNIT"));
			extras.put("address",db.getString("ADDRESS"));
			extras.put("lsoId",db.getString("ID"));
			extras.put("ctypeid", db.getString("LKUP_LSO_TYPE_ID"));
			
			g.setObj(l.toArray(obj));
			g.setExtras(extras);
			
			db.clear();
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	
	
	
}
