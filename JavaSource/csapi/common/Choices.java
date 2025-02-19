package csapi.common;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.general.GeneralSQL;
import csapi.impl.holds.HoldsAgent;
import csshared.vo.HoldsList;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;
import csshared.vo.lkup.RolesVO;

public class Choices {


	public static SubObjVO[] getChoices(String command) {
		return getChoices(command, false);
	}

	public static SubObjVO[] getChoices(String command, Token u) {
		return getChoices(command, false, u);
	}

	public static SubObjVO[] getChoices(String command, boolean selectfirst) {
		SubObjVO[] a = new SubObjVO[0];
		Sage db = new Sage();
		boolean hs = false;
		db.query(command);
		if (db.size() > 0) {
			int count = 0;
			a = new SubObjVO[db.size()];
			String[] cols = db.COLUMNS;
			while (db.next()) {
				SubObjVO vo = new SubObjVO();
				vo.setId(db.getInt("ID"));
				vo.setValue(db.getString("VALUE"));
				vo.setText(db.getString("TEXT"));
				vo.setDescription(db.getString("DESCRIPTION"));
				vo.setSelected(db.getString("SELECTED"));
				vo.setItype(db.getString("ITYPE"));
				if (db.equalsIgnoreCase("SELECTED", "Y")) {
					hs = true;
				}
				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					if (!c.equalsIgnoreCase("ID") && !c.equalsIgnoreCase("VALUE") && !c.equalsIgnoreCase("TEXT") && !c.equalsIgnoreCase("DESCRIPTION")  && !c.equalsIgnoreCase("SELECTED")) {
						vo.setData(c, db.getString(c));
					}
				}
				a[count] = vo;
				count++;
			}
		}
		db.clear();
		if (!hs && selectfirst && a.length > 0) {
			a[0].setSelected("Y");
		}
		return a;
	}

	public static SubObjVO[] getChoices(String command, HoldsList hl) {
		return getChoices(command, false, hl);
	}

	public static SubObjVO[] getChoices(String command, boolean selectfirst, HoldsList hl) {
		ArrayList<SubObjVO> ch = new ArrayList<SubObjVO>();
		Sage db = new Sage();
		boolean hs = false;
		db.query(command);
		if (db.size() > 0) {
			ArrayList<String> al = new ArrayList<String>();
			while(db.next()) {
				al.add(db.getString("ID"));
				if (db.equalsIgnoreCase("SELECTED", "Y")) {
					hs = true;
				}
			}
			String[] arr = al.toArray(new String[al.size()]);
			hl = HoldsAgent.setActivityTypes(hl, arr);
			db.beforeFirst();
			String[] cols = db.COLUMNS;
			while (db.next()) {
				if (!hl.actOnSignificantHold(db.getInt("ID"))) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("VALUE"));
					vo.setText(db.getString("TEXT"));
					vo.setDescription(db.getString("DESCRIPTION"));
					vo.setItype(db.getString("ITYPE"));
					if (hs) {
						vo.setSelected(db.getString("SELECTED"));
						hs = false;
					}
					else {
						vo.setSelected(db.getString("SELECTED"));
						hs = false;
					}
					for (int i=0; i<cols.length; i++) {
						String c = cols[i];
						if (!c.equalsIgnoreCase("ID") && !c.equalsIgnoreCase("VALUE") && !c.equalsIgnoreCase("TEXT") && !c.equalsIgnoreCase("DESCRIPTION")  && !c.equalsIgnoreCase("SELECTED")) {
							vo.setData(c, db.getString(c));
						}
					}
					ch.add(vo);
				}
			}
		}
		db.clear();
		return ch.toArray(new SubObjVO[ch.size()]);
	}

	public static SubObjVO[] getChoices(String command, boolean selectfirst, Token u) {
		ArrayList<SubObjVO> a = new ArrayList<SubObjVO>();
		Sage db = new Sage();
		boolean hs = false;
		db.query(command);
		if (db.size() > 0) {
			String[] cols = db.COLUMNS;
			while (db.next()) {
				boolean iscreate = true;
				SubObjVO vo = new SubObjVO();
				vo.setId(db.getInt("ID"));
				vo.setValue(db.getString("VALUE"));
				vo.setText(db.getString("TEXT"));
				vo.setDescription(db.getString("DESCRIPTION"));
				vo.setSelected(db.getString("SELECTED"));
				String roletype = db.getString("ROLE_TYPE");
				int typeid = db.getInt("ROLE_TYPE_ID");
				if (Operator.hasValue(roletype) && typeid > 0) {
					if (Operator.equalsIgnoreCase(roletype, "ACTIVITY")) {
						RolesVO r = LkupCache.getActivityRoles(typeid);
						vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
						iscreate = vo.isCreate();
					}
					else if (Operator.equalsIgnoreCase(roletype, "PROJECT")) {
						RolesVO r = LkupCache.getProjectRoles(typeid);
						vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
						iscreate = vo.isCreate();
					}
				}
				
				if (db.equalsIgnoreCase("SELECTED", "Y")) {
					hs = true;
				}
				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					if (!c.equalsIgnoreCase("ID") && !c.equalsIgnoreCase("VALUE") && !c.equalsIgnoreCase("TEXT") && !c.equalsIgnoreCase("DESCRIPTION")  && !c.equalsIgnoreCase("SELECTED")) {
						vo.setData(c, db.getString(c));
					}
				}
				if (iscreate) {
					a.add(vo);
				}
			}
		}
		db.clear();
		SubObjVO[] r = a.toArray(new SubObjVO[a.size()]);
		if (!hs && selectfirst && r.length > 0) {
			r[0].setSelected("Y");
		}
		return r;
	}

	public static ObjVO getObj(String command) {
		return getObj(command, false);
	}

	public static ObjVO getObj(String command, boolean selectfirst) {
		ObjVO o = new ObjVO();
		SubObjVO[] a = new SubObjVO[0];
		Sage db = new Sage();
		boolean hs = false;
		db.query(command);
		if (db.size() > 0) {
			int count = 0;
			a = new SubObjVO[db.size()];
			String[] cols = db.COLUMNS;
			while (db.next()) {
				SubObjVO vo = new SubObjVO();
				if (db.getInt("RESULTS") > 0) {
					o.setNumresults(db.getInt("RESULTS"));
				}
				vo.setId(db.getInt("ID"));
				vo.setValue(db.getString("VALUE"));
				vo.setText(db.getString("TEXT"));
				vo.setDescription(db.getString("DESCRIPTION"));
				vo.setSelected(db.getString("SELECTED"));
				vo.setItype(db.getString("ITYPE"));
				if (db.equalsIgnoreCase("SELECTED", "Y")) {
					hs = true;
				}
				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					if (!c.equalsIgnoreCase("ID") && !c.equalsIgnoreCase("VALUE") && !c.equalsIgnoreCase("TEXT") && !c.equalsIgnoreCase("DESCRIPTION")  && !c.equalsIgnoreCase("SELECTED")) {
						vo.setData(c, db.getString(c));
					}
				}
				a[count] = vo;
				count++;
			}
		}
		db.clear();
		if (!hs && selectfirst && a.length > 0) {
			a[0].setSelected("Y");
		}
		o.setChoices(a);
		return o;
	}

	public static JSONArray getChoicesyn(){
		JSONArray choices = new JSONArray();
		try{
			JSONObject c = new JSONObject();
			c.put("value","Y");
			choices.put(c);
			c = new JSONObject();
			c.put("value","N");
			choices.put(c);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return choices;
		
	}
	
	/*public static SubObjVO getChoicesYN(){
		SubObjVO choices = new SubObjVO();
		try{
			JSONObject c = new JSONObject();
			c.put("value","Y");
			choices.put(c);
			c = new JSONObject();
			c.put("value","N");
			choices.put(c);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return choices;
		
	}*/
	
	public static String choiceUrl(String table, String column, String orderField){
		return choiceUrl(table, column, orderField,"ASC", "", "","");
	}
	
	public static String choiceUrl(String table, String column, String orderField, String orderType, String filterColumn, String filterValue,String selected){
		StringBuilder sb = new StringBuilder();
		sb.append("table=").append(table);
		sb.append("&");
		sb.append("column=").append(column);
		if(Operator.hasValue(filterColumn) && Operator.hasValue(filterValue)){
			sb.append("&");
			sb.append("filterColumn=").append(filterColumn);
			sb.append("&");
			sb.append("filterValue=").append(filterValue);
		}
		
		if(Operator.hasValue(orderField)){
			sb.append("&");
			sb.append("orderfield=").append(orderField);
			sb.append("&");
			sb.append("orderType=").append(orderType);
		}
		if(Operator.hasValue(selected)){
			sb.append("&");
			sb.append("selected=").append(selected);
		}
		return sb.toString();
	}
	
	public static String choiceUrlQuery(String table, String column, String orderField, String orderType, String filterColumn,String filterValue,String filterValues){
		StringBuilder sb = new StringBuilder();
		sb.append("select ID,ID as VALUE, ").append(column).append(" AS TEXT, DESCRIPTION ");
		sb.append(" FROM ").append(table);
		sb.append(" WHERE ACTIVE='Y' ");
		if(Operator.hasValue(filterColumn) && Operator.hasValue(filterValue)){
			sb.append(" AND ");
			sb.append("").append(filterColumn).append(" = '").append(Operator.sqlEscape(filterValue)).append("'");
		}
		if(Operator.hasValue(filterColumn) && Operator.hasValue(filterValues)){
			sb.append(" AND ");
			sb.append("").append(filterColumn).append(" IN (").append(Operator.sqlEscape(filterValues)).append(")");
		}
		
		if(Operator.hasValue(orderField) && Operator.hasValue(orderType)){
			sb.append("orderby ").append(orderField).append(" ").append(orderType);
		}
		return sb.toString();
	}
	
	public static String choiceUrlQuery(String json){
		StringBuilder sb = new StringBuilder();
		
		try{
			JSONObject o = new JSONObject(json);
			String table = o.getString("table");
			String column = o.getString("column");
			String command = "";
			if(o.has("command")){
				command = o.getString("command");
			}
			
			
			String orderField = "";
			String orderType = "";
			String filterColumn = "";
			String filterValue = "";
			String filterValues = "";
			String selected = "";
		
			if(Operator.hasValue(o.getString("orderField")) && Operator.hasValue(o.getString("orderType"))){
				orderField = o.getString("orderField");
				orderType = o.getString("orderType");
			}
			
			if(Operator.hasValue(o.getString("filterColumn")) && ( Operator.hasValue(o.getString("filterValue")) || Operator.hasValue(o.getString("filterValues"))) ){
				filterColumn = o.getString("filterColumn");
				filterValue = o.getString("filterValue");
				filterValues = o.getString("filterValues");
			}
			if(Operator.hasValue(o.getString("selected"))){
				selected = o.getString("selected");
				
			}
			
			if(Operator.hasValue(command)){
				command = getCommand(command);
				sb.append(getChoicesArray(command,selected));
			}else{
				sb.append(getChoicesArray(choiceUrlQuery(table, column, orderField, orderType, filterColumn, filterValue, filterValues),selected));
			}	
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
		
	}
	
	public static String getFileNameChoices(String json){
		StringBuilder sb = new StringBuilder();
		
		try{
			JSONObject o = new JSONObject(json);
			
			if(o.has("command")){
				sb.append(o.getString("command"));
				sb.append("_");
			}
			sb.append(o.getString("table"));
			sb.append("_");
			sb.append(o.getString("column"));
			if(Operator.hasValue(o.getString("orderField")) && Operator.hasValue(o.getString("orderType"))){
				sb.append("_");
				sb.append(o.getString("orderField"));
				sb.append("_");
				sb.append(o.getString("orderType"));
			}
			
			if(Operator.hasValue(o.getString("filterColumn")) && Operator.hasValue(o.getString("filterValue"))){
				sb.append("_");
				sb.append(o.getString("filterColumn"));
				sb.append("_");
				sb.append(o.getString("filterValue"));
			}
			sb.append(".txt");
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
		
	}
	
	public static  JSONObject getChoicesArray(String command,String selected){
		JSONObject o = new JSONObject();
		JSONArray choices = new JSONArray();
		try{
			
			Sage db = new Sage();
			db.query(command);
			
			while(db.next()){
				JSONObject c = new JSONObject();
				c.put("id",db.getString("ID"));
				c.put("value",db.getString("VALUE"));
				c.put("text",db.getString("TEXT"));
				if(Operator.equalsIgnoreCase(db.getString("VALUE"), selected)){
					c.put("selected","Y");
				}
				if(Operator.equalsIgnoreCase(db.getString("ID"), selected)){
					c.put("selected","Y");
				}
				choices.put(c);
			}
			
		 db.clear();
		 o.put("choices", choices);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return o;
		
	}
	
	public static String getCommand(String type){
		String s ="";
		if(type.equalsIgnoreCase("getstreetlist")){
			s = GeneralSQL.getStreetList();
		}
		if(type.equalsIgnoreCase("getstreetfraction")){
			s = GeneralSQL.getStreetFraction();
		}
		return s;
	}
	
	public static JSONArray getChoices(String table,String ordr){
		//JSONObject o = new JSONObject();
		JSONArray choices = new JSONArray();
		try{
			
			Sage db = new Sage();
			db.query(queryCommand(table, ordr));
			
			while(db.next()){
				
					JSONObject c = new JSONObject();
					c.put("id",db.getInt("ID"));
					c.put("value",db.getString("DESCRIPTION"));
					c.put("text",db.getString("DESCRIPTION"));
					choices.put(c);
				
				
			
			}
			
		 db.clear();
		 //o.put("choices", choices);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return choices;
		
	}
	
	
	
	public static SubObjVO[] getStatus(String table, String ordr, String selected,String text){
		//JSONObject o = new JSONObject();
		SubObjVO[] choices = new SubObjVO[0];
		try{
			
			Sage db = new Sage();
			db.query(queryCommand(table, ordr));
			int sz = db.size();
			
			if(sz>0){
				int count =0;
				choices = new SubObjVO[sz];
				while(db.next()){
					SubObjVO v = new SubObjVO();
					
					v.setId(db.getInt("ID"));
					v.setValue(db.getString("ID"));
					v.setText(db.getString(text));
					/*if(Operator.equalsIgnoreCase(selected, db.getString("DESCRIPTION"))){
						v.setSelected("Y");
					}*/
					choices[count] = v;
					count++;
				}
			}
			
		 db.clear();
		 //o.put("choices", choices);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return choices;
		
	}
	
	//SubObjVO[] choices = new SubObjVO[0];
	public static String queryCommand(String table,String ordr){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(table).append(" where ACTIVE='Y'" );
		if(Operator.hasValue(ordr)){
			sb.append(" order by ").append(ordr).append(" ASC ");
		}
		return sb.toString();
	}
	
	public static SubObjVO[] getNoteTypes(String table, String ordr, String selected){
		//JSONObject o = new JSONObject();
		SubObjVO[] choices = new SubObjVO[0];
		try{
			
			Sage db = new Sage();
			db.query(queryCommand(table, ordr));
			int sz = db.size();
			
			if(sz>0){
				int count =0;
				choices = new SubObjVO[sz];
				while(db.next()){
					SubObjVO v = new SubObjVO();
					
					v.setId(db.getInt("ID"));
					v.setValue(db.getString("ID"));
					v.setText(db.getString("DESCRIPTION"));
					/*if(Operator.equalsIgnoreCase(selected, db.getString("DESCRIPTION"))){
						v.setSelected("Y");
					}*/
					choices[count] = v;
					count++;
				}
			}
			
		 db.clear();
		 //o.put("choices", choices);
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return choices;
		
	}
	
	public static void dochoice(){
		String s = "{\"orderType\":\"ASC\",\"filterValue\":\"6\",\"column\":\"DIVISIONS\",\"filterColumn\":\"LKUP_DIVISIONS_TYPE_ID\",\"orderField\":\"\",\"table\":\"LKUP_DIVISIONS\"}";
	}
}
