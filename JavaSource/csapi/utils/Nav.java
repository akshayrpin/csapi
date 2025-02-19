package csapi.utils;

import alain.core.utils.Config;

public class Nav {

	
	public static String getFormEditUrl(String type,int id,String formgrp){
		StringBuilder sb = new StringBuilder();
		
		/*nav.setEntity(map.getString("_ent"));
		nav.setToken(map.filetoken());
		nav.setType(map.getString("_type"));
		nav.setId(map.getString("_id"));
		nav.setGroup(map.getString("_grp"));
		nav.setRequest("details");*/

		
		sb.append(Config.rooturl()+"cs/form.jsp");
		sb.append("?");
		sb.append("_ent=").append(type);
		sb.append("&");
		sb.append("token=").append(id);
		sb.append("&");
		sb.append("frmid=").append(formgrp).append(id);
		return sb.toString();
	}
	
	public static String getFormDeleteUrl(String type,int id,String formgrp){
		StringBuilder sb = new StringBuilder();
		sb.append(Config.rooturl()+"cs/deleteentry.jsp");
		sb.append("?");
		sb.append("type=").append(type);
		sb.append("&");
		sb.append("id=").append(id);
		sb.append("&");
		sb.append("frmid=").append(formgrp).append(id);
		return sb.toString();
	}
	
	
	/*public static String getSummaryUrl(String type,int id,String formgrp){
		StringBuilder sb = new StringBuilder();
		sb.append(Config.rooturl()+"cs/details.jsp");
		sb.append("?");
		sb.append("type=").append(type);
		sb.append("&");
		sb.append("id=").append(id);
		sb.append("&");
		sb.append("frmid=").append(formgrp).append(id);
		return sb.toString();
	}*/
}
