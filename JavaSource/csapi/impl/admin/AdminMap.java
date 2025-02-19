package csapi.impl.admin;

import java.util.ArrayList;

import csapi.common.Table;
import csapi.impl.users.UsersSQL;
import alain.core.db.Sage;
import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class AdminMap {

	//TODO TOKENVO
	public static MapSet getCommon(Cartographer map){
		MapSet m = new MapSet();
		m.add("CREATED_IP", map.getRemoteIp());
		m.add("UPDATED_IP", map.getRemoteIp());
		Logger.info(map.getString("_userid")+"********************************** SESSION ");
		
		m.add("CREATED_BY",map.getString("_userid"));
		m.add("UPDATED_BY", map.getString("_userid"));
		m.add("CREATED_DATE", "CURRENT_TIMESTAMP");
		m.add("UPDATED_DATE", "CURRENT_TIMESTAMP");
		return m;
	}
	
	public static MapSet getProjectType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("LKUP_PATTERN_ID", map.getString("LKUP_PATTERN_ID"));
		String d = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ISDOT", d);
		String o = map.getString("ONLINE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ONLINE", o);
		
		o = map.getString("EXPIRES","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("EXPIRES", o);
		
		o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);
		
		
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getString("MAX_ACTIVE_APPOINTMENTS"));
		
		m.add("MAX_CHILD_APPOINTMENTS", map.getString("MAX_CHILD_APPOINTMENTS"));
		m.add("MAX_CHILD_APPOINTMENTS_DAY", map.getString("MAX_CHILD_APPOINTMENTS_DAY"));
		
		return m;
	}
	
	public static MapSet getProjectTypeMulti(Cartographer map){
		MapSet m = getCommon(map);
		
		m.add("PATTERN_ID", map.getString("PATTERN_ID"));
		
		m.add("ISDOT", map.getString("ISDOT"));
		m.add("ONLINE", map.getString("ONLINE"));
		m.add("EXPIRES", map.getString("EXPIRES"));
		m.add("ISPUBLIC", map.getString("ISPUBLIC"));
				
		m.add("LKUP_PATTERN_ID", map.getString("LKUP_PATTERN_ID"));
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getString("MAX_ACTIVE_APPOINTMENTS"));
		
		m.add("MAX_CHILD_APPOINTMENTS", map.getString("MAX_CHILD_APPOINTMENTS"));
		m.add("MAX_CHILD_APPOINTMENTS_DAY", map.getString("MAX_CHILD_APPOINTMENTS_DAY"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());

		return m;
	}
	
	
	public static MapSet getProjectStatus(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("STATUS", map.getString("STATUS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("EXPIRED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("EXPIRED", o);
		
		o = map.getString("LIVE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("LIVE", o);		
		
		o = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISDOT", o);		
		
		o = map.getString("ISSUED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISSUED", o);		
		
		o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);		
		
		m.add("STATUS_ID_MERGE_MAP", map.getString("STATUS_ID_MERGE_MAP"));
		m.add("WATERMARK_PATH", map.getString("WATERMARK_PATH"));
		
		o = map.getString("WATERMARK_STATUS","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("WATERMARK_STATUS", o);	
				
		return m;
	}
	
	public static MapSet getProjectStatusMulti(Cartographer map){
		MapSet m = getCommon(map);
		
		m.add("EXPIRED", map.getString("EXPIRED"));
		m.add("LIVE", map.getString("LIVE"));
		m.add("ISDOT", map.getString("ISDOT"));
		m.add("ISSUED", map.getString("ISSUED"));
		m.add("DEFLT", map.getString("DEFLT"));
		m.add("WATERMARK_STATUS", map.getString("WATERMARK_STATUS"));
		m.add("STATUS_ID_MERGE_MAP", map.getString("STATUS_ID_MERGE_MAP"));
		m.add("WATERMARK_PATH", map.getString("WATERMARK_PATH"));

		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	
	public static MapSet getActivityType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("LKUP_PATTERN_ID", map.getString("LKUP_PATTERN_ID"));
		String d = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ISDOT", d);
		
		String o = map.getString("ONLINE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ONLINE", o);
		
		o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);

		
		o = map.getString("FINANCE_LOCK","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("FINANCE_LOCK", o);
		
		o = map.getString("ISTEMP","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISTEMP", o);
		
		o = map.getString("RENEWAL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("RENEWAL", o);

		o = map.getString("ENABLE_TIME","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ENABLE_TIME", o);
		
		o = map.getString("INHERIT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INHERIT", o);

		o = map.getString("DOT_EXEMPTION","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DOT_EXEMPTION", o);
		
		o = map.getString("DOT_STICKERS","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DOT_STICKERS", o);
		
		o = map.getString("BATCH","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
        m.add("BATCH", o);
        
        o = map.getString("AUTO_ISSUE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
        m.add("AUTO_ISSUE", o);
        
		
		m.add("MAX_ALLOWED", map.getString("MAX_ALLOWED"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getString("MAX_ACTIVE_APPOINTMENTS"));
		m.add("MONTH_START", map.getString("MONTH_START"));
		m.add("DAY_START", map.getString("DAY_START"));
		m.add("YEARS_TILL_EXPIRED", map.getString("YEARS_TILL_EXPIRED"));
		m.add("DAYS_TILL_EXPIRED", map.getString("DAYS_TILL_EXPIRED"));
		m.add("DAYS_TILL_APPLICATION_EXPIRED", map.getString("DAYS_TILL_APPLICATION_EXPIRED"));
		
		//TO DISCUSS AND REMOVE
		m.add("DEPARTMENT_ID", map.getString("DEPARTMENT_ID","8"));
		m.add("TEMPLATE", map.getString("TEMPLATE","0"));
		Logger.info("AVAILABILITY_ID"+map.getString("AVAILABILITY_ID"));
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("CONFIGURATION_GROUP_ID", map.getString("CONFIGURATION_GROUP_ID"));
		
		
		m.add("DURATION_MAX", map.getString("DURATION_MAX"));
		m.add("DURATION_MAX_MONTHS", map.getString("DURATION_MAX_MONTHS"));
		m.add("DURATION_MAX_YEARS", map.getString("DURATION_MAX_YEARS"));
		m.add("DURATION_MAX_DAYS", map.getString("DURATION_MAX_DAYS"));
		
		
		
		
		return m;
	}
	
	
	public static MapSet getActivityTypeMulti(Cartographer map){
		MapSet m = getCommon(map);
		//m.add("IDS", map.getInt("IDS"));
		
		m.add("LKUP_PATTERN_ID", map.getString("LKUP_PATTERN_ID"));
		m.add("ISDOT",  map.getString("ISDOT"));
		m.add("ONLINE", map.getString("ONLINE"));
		m.add("ISPUBLIC", map.getString("ISPUBLIC"));
		
		m.add("FINANCE_LOCK", map.getString("FINANCE_LOCK"));
		m.add("ISTEMP", map.getString("ISTEMP"));
		m.add("RENEWAL", map.getString("RENEWAL"));
		
		m.add("ENABLE_TIME", map.getString("ENABLE_TIME"));
		m.add("INHERIT", map.getString("INHERIT"));
		m.add("DOT_STICKERS", map.getString("DOT_STICKERS"));
		
		m.add("DOT_EXEMPTION", map.getString("DOT_EXEMPTION"));
		
		m.add("MAX_ALLOWED", map.getString("MAX_ALLOWED"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getString("MAX_ACTIVE_APPOINTMENTS"));
		m.add("MONTH_START", map.getString("MONTH_START"));
		m.add("DAY_START", map.getString("DAY_START"));
		m.add("YEARS_TILL_EXPIRED", map.getString("YEARS_TILL_EXPIRED"));
		m.add("DAYS_TILL_EXPIRED", map.getString("DAYS_TILL_EXPIRED"));
		m.add("DAYS_TILL_APPLICATION_EXPIRED", map.getString("DAYS_TILL_APPLICATION_EXPIRED"));
		
		
	    m.add("AUTO_ISSUE", map.getString("AUTO_ISSUE"));
		
		//m.add("DURATION_MAX", map.getString("DURATION_MAX"));
		m.add("DURATION_MONTHS", map.getString("DURATION_MONTHS"));
		m.add("DURATION_YEARS", map.getString("DURATION_YEARS"));
		m.add("DURATION_DAYS", map.getString("DURATION_DAYS"));
		
		//TO DISCUSS AND REMOVE
		m.add("DEPARTMENT_ID", map.getString("DEPARTMENT_ID"));
		m.add("TEMPLATE", map.getString("TEMPLATE","0"));
		Logger.info("AVAILABILITY_ID"+map.getString("MONTH_START"));
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		
		m.add("CONFIGURATION_GROUP_ID", map.getString("CONFIGURATION_GROUP_ID"));
		m.add("MAX_ALLOWED", map.getString("MAX_ALLOWED"));
		
		m.add("DURATION_MAX", map.getString("DURATION_MAX"));
		m.add("DURATION_MAX_MONTHS", map.getString("DURATION_MAX_MONTHS"));
		m.add("DURATION_MAX_YEARS", map.getString("DURATION_MAX_YEARS"));
		m.add("DURATION_MAX_DAYS", map.getString("DURATION_MAX_DAYS"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		return m;
	}
	
	public static MapSet getActivityStatusMulti(Cartographer map){
		MapSet m = getCommon(map);
			
		m.add("EXPIRED", map.getString("EXPIRED"));
		m.add("LIVE", map.getString("LIVE"));
		m.add("ISSUED",  map.getString("ISSUED"));
		m.add("INHERIT",  map.getString("INHERIT"));
		m.add("DEFLT",  map.getString("DEFLT"));
		
		m.add("ONLINE_PRINT",  map.getString("ONLINE_PRINT"));
		m.add("ISDOT",  map.getString("ISDOT"));
		
		m.add("ISPUBLIC",  map.getString("ISPUBLIC"));
		
		m.add("STATUS_ID_MERGE_MAP",  map.getString("STATUS_ID_MERGE_MAP"));
		
		m.add("FINAL",  map.getString("FINAL"));
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	public static MapSet getActivityStatus(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("STATUS", map.getString("STATUS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("EXPIRED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("EXPIRED", o);
		
		o = map.getString("INHERIT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INHERIT", o);
		
		o = map.getString("ISSUED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISSUED", o);
		
		o = map.getString("LIVE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("LIVE", o);
		
		o = map.getString("FINAL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("FINAL", o);
		
		o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);
		
		o = map.getString("ONLINE_PRINT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ONLINE_PRINT", o);
		
		
		o = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISDOT", o);
		
		o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);
		
		m.add("STATUS_ID_MERGE_MAP", map.getString("STATUS_ID_MERGE_MAP"));
		
		m.add("WATERMARK_PATH", map.getString("WATERMARK_PATH"));
		
		o = map.getString("WATERMARK_STATUS","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("WATERMARK_STATUS", o);
		
		return m;
	}
	
	public static MapSet getCIP(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("CIP_ACCTNO", map.getString("CIP_ACCTNO"));
		m.add("START_DATE", map.getString("START_DATE"));
		m.add("END_DATE", map.getString("END_DATE"));
		return m;
	}  
	
	
	public static MapSet getDepartment(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("DEPT", map.getString("DEPT"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("CODE", map.getString("CODE"));
	
		return m;
	}  
	
	public static MapSet getAttachments(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("SENSITIVE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("SENSITIVE", o);
		
		o = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("REQUIRED", o);
		
		o = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISDOT", o);
		
		o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);
	
		return m;
	}  
	
	public static MapSet getHolidayType(Cartographer map){   
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));   
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("CLOSED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("CLOSED", o);
		o = map.getString("GLOBAL_HOLIDAY","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("GLOBAL_HOLIDAY", o);
	
		

		return m;
	}
	
	
	public static MapSet getVoipMenu(Cartographer map){   
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("PRESS", map.getInt("PRESS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("LOGIC", map.getString("LOGIC"));   
		m.add("HINT", map.getString("HINT"));   
		m.add("SAY_DESCRIPTION", map.getString("SAY_DESCRIPTION"));   
		return m;
	}
	
	public static MapSet getRefVoipTransfer(Cartographer map){   
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("LKUP_VOIP_MENU_ID", map.getInt("LKUP_VOIP_MENU_ID"));		
		m.add("PRESS", map.getInt("PRESS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("PHONE", map.getString("PHONE"));   
		m.add("HINT", map.getString("HINT"));   
	
		return m;
	}
	
	public static MapSet getVoipLogic(Cartographer map){   
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		String o = map.getString("ENABLE_VOICE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ENABLE_VOICE", o);   
		return m;
	}
	
	
	public static MapSet getCustomType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("GROUP_NAME", map.getString("GROUP_NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		String d = map.getString("MULTI","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("MULTI", d);
		String o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);
		
		
		
		return m;
	}
	
	
	public static MapSet getCustomTypeMulti(Cartographer map){
		MapSet m = getCommon(map);
		
		
		m.add("MULTI", map.getString("MULTI"));
	
		m.add("ISPUBLIC", map.getString("ISPUBLIC"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());		
		
		return m;
	}
	
	
	
	
	public static String getCustomFields(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.FIELDTABLE;
		sb.append("select  F.FIELD_GROUPS_ID,F.NAME,F.ID,F.REQUIRED,F.MAX_CHAR,F.IDX,T.TYPE, IT.TYPE as ITYPE, COUNT(C.ID) as CHOICE_COUNT  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" F ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE T on F.LKUP_FIELD_TYPE_ID = T.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE IT on F.LKUP_FIELD_ITYPE_ID = IT.ID  "); 
		sb.append(" LEFT OUTER JOIN FIELD_CHOICES C on F.ID = C.FIELD_ID AND C.ACTIVE='Y' "); 
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE FIELD_GROUPS_ID=").append(groupid).append(" group by F.FIELD_GROUPS_ID,F.NAME,F.ID,T.TYPE,IT.TYPE,F.REQUIRED,F.MAX_CHAR,F.IDX,F.CREATED_DATE,F.UPDATED_DATE,CU.USERNAME,UP.USERNAME,F.ordr order by F.ordr ");
		return sb.toString();
	}
	
	public static String getAliasFields(int lsoId){
		StringBuilder sb = new StringBuilder();
		String table = "V_CENTRAL_ADDRESS";
		sb.append("select  *  ");
		/*sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); */
		sb.append(" from " ).append(table).append(" F ");
	
		//sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		//sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE PRIMARY_ID=").append(lsoId).append(" AND ACTIVE='Y' order by ADDRESS ");
		return sb.toString();
	}
	
	public static String getLsoChildren(int lsoId){
		StringBuilder sb = new StringBuilder();
		String table = Table.FIELDTABLE;
		sb.append("select  *  ");
		
		sb.append(" from V_CENTRAL_ADDRESS F ");
	
	
		sb.append("	WHERE F.PARENT_ID=").append(lsoId).append(" AND ACTIVE='Y'  ");
		return sb.toString();
	}
	
	
	public static String getActivityTypes(String searchFields , String searchString){
		StringBuilder sb = new StringBuilder();
		String table = Table.LKUPACTTYPETABLE;
		sb.append("select  A.*,LP.PATTERN  ");
		sb.append(" from " ).append(table).append(" A ");
		sb.append(" LEFT OUTER JOIN LKUP_PATTERN LP on A.LKUP_PATTERN_ID = LP.ID   ");
		if(Operator.hasValue(searchString)){
			sb.append(" WHERE  LOWER(A.DESCRIPTION) like '%").append(Operator.sqlEscape(searchString)).append("%'  OR LOWER(A.TYPE) like '%").append(Operator.sqlEscape(searchString)).append("%'  ");
		}
		return sb.toString();
	}
	
	
	public static String getCustomFieldChoices(int id){
		StringBuilder sb = new StringBuilder();
		String table = Table.FIELDCHOICESTABLE;
		sb.append("select FC.*  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),FC.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),FC.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" FC ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on FC.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on FC.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE FC.ACTIVE='Y' AND FC.FIELD_ID=").append(id).append(" order by ordr ");
		return sb.toString();
	}
	
	
	public static MapSet getModuleType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MODULE", map.getString("MODULE"));
		
		
		String d = map.getString("DISABLE_ON_HOLD","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("DISABLE_ON_HOLD", d);
		
		String o = map.getString("DISABLE_TOOL_ON_HOLD","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DISABLE_TOOL_ON_HOLD", o);
		
		
		
		return m;
	}
	
	public static MapSet geSolrJob(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("PACKAGE_NAME", map.getString("PACKAGE_NAME"));
		m.add("PACKAGE_IMPORT_URL", map.getString("PACKAGE_IMPORT_URL"));
		m.add("PACKAGE_STATUS_URL", map.getString("PACKAGE_STATUS_URL"));
		
		
		
		return m;
	}
	 
	public static MapSet getLSOMap(Cartographer map){
		MapSet m = getCommon(map);       
		String o ="";
		m.add("ID", map.getInt("ID"));
		m.add("table", Table.LSOTABLE);
		m.add("LKUP_LSO_TYPE_ID", map.getString("LKUP_LSO_TYPE_ID"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("STR_NO", map.getString("STR_NO"));
		m.add("STR_MOD", map.getString("STR_MOD"));
		m.add("LSO_STREET_ID", map.getString("LSO_STREET_ID"));
		m.add("UNIT", map.getString("UNIT"));
		m.add("CITY", map.getString("CITY"));
		m.add("STATE", map.getString("STATE"));
		m.add("ZIP", map.getString("ZIP"));
		m.add("SOURCE", map.getString("SOURCE"));
		m.add("ZIP4", map.getString("ZIP4"));
		m.add("START_DATE", map.getString("START_DATE"));
		m.add("END_DATE", map.getString("END_DATE"));
		m.add("PARENT_ID", map.getString("PARENT_ID"));
		m.add("PRIMARY_ID", map.getString("PRIMARY_ID"));
		
		String d = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ISPUBLIC", d);
		
		/*if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE ID =").append(m.getInt("ID"));
			
			
			m.add("deleteselection",w.toString());
		}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(" ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}*/
		
 
		return m;
	}
	
	public static MapSet getLSOStreetMap(Cartographer map){
		MapSet m = getCommon(map);       
		String o ="";
		m.add("table", Table.LSOSTREETTABLE);
		m.add("ID", map.getInt("ID"));
		m.add("PRE_DIR", map.getString("PRE_DIR"));
		m.add("STR_NAME", map.getString("STR_NAME"));
		m.add("STR_TYPE", map.getString("STR_TYPE"));
		m.add("SUF_DIR", map.getString("SUF_DIR"));
		o = map.getString("ACTIVE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ACTIVE", o);
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE ID =").append(m.getInt("ID"));
			
			
			m.add("deleteselection",w.toString());
		}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(" ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}
		
		return m;
	}
	
	
	
	
	public static String getDivisionsFields(int groupid){
		StringBuilder sb = new StringBuilder();
		sb.append("select F.*,T.TYPE AS DIVISION_TYPE, T.DESCRIPTION as DIVISION_DESCRIPTION  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from LKUP_DIVISIONS F ");
		sb.append(" LEFT OUTER JOIN LKUP_DIVISIONS_TYPE T on F.LKUP_DIVISIONS_TYPE_ID = T.ID   ");
	
		sb.append("   LEFT OUTER JOIN USERS AS CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN USERS AS UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE F.LKUP_DIVISIONS_TYPE_ID=").append(groupid).append(" AND F.ACTIVE='Y'  order by ordr ");
		return sb.toString();
	}
	
	
	public static MapSet getCustomRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));   
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table",Table.REFPROJECTFIELDGROUPSTABLE);
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table",Table.REFACTFIELDGROUPSTABLE);
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("user")){
				m.add("table","REF_USERS_FIELD_GROUPS");
				m.add("column","LKUP_USERS_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("lso")){
				m.add("table","REF_LSO_FIELD_GROUPS");
				m.add("column","LKUP_LSO_TYPE_ID");
			
			}
			
			Logger.info(connecttype+"&&&&&&&&&&&&");
			if(connecttype.equalsIgnoreCase("LIBRARY")){
				m.add("table","REF_LIBRARY_GROUP");
				m.add("column","LIBRARY_GROUP_ID");
			
			}
			
			m.add("FIELD_GROUPS_ID", map.getInt("FIELD_GROUPS_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.GROUP_NAME FROM ").append(m.getString("table")).append(" R JOIN FIELD_GROUPS G on R.FIELD_GROUPS_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE FIELD_GROUPS_ID =").append(m.getString("FIELD_GROUPS_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND FIELD_GROUPS_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	public static MapSet getModuleRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table",Table.REFPROJECTMODULETABLE);
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table",Table.REFACTMODULETABLE);
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("user")){
				m.add("table","REF_USERS_FIELD_GROUPS");
				m.add("column","LKUP_USERS_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("lso")){
				m.add("table",Table.REFLSOMODULETABLE);
				m.add("column","LKUP_LSO_TYPE_ID");
			
			}
			
			m.add("LKUP_MODULE_ID", map.getInt("LKUP_MODULE_ID"));
			Logger.info("LOCATION#####################"+map.getString("LOCATION"));
			m.add("LOCATION", map.getString("LOCATION"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.LKUP_MODULE_ID as ID,R.LKUP_MODULE_ID,G.MODULE,R.LOCATION  FROM ").append(m.getString("table")).append(" R JOIN LKUP_MODULE G on R.LKUP_MODULE_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY R.ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_MODULE_ID =").append(m.getString("LKUP_MODULE_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_MODULE_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	
	public static MapSet getHoldsTypeRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			
			m.add("table",Table.REFACTTYPEHOLDSTABLE);
			m.add("column","LKUP_ACT_TYPE_ID");
			m.add("LKUP_HOLDS_TYPE_ID", map.getInt("LKUP_HOLDS_TYPE_ID"));
			Logger.info("LOCATION#####################"+map.getString("LOCATION"));
			m.add("LOCATION", "summary");
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.LKUP_HOLDS_TYPE_ID as ID,R.LKUP_HOLDS_TYPE_ID,G.DESCRIPTION,'summary'  FROM ").append(m.getString("table")).append(" R JOIN LKUP_HOLDS_TYPE G on R.LKUP_HOLDS_TYPE_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]);
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_HOLDS_TYPE_ID =").append(m.getString("LKUP_HOLDS_TYPE_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_HOLDS_TYPE_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	
	
	public static MapSet getFieldType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("SOURCE", map.getString("SOURCE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		m.add("FIELD_GROUPS_ID", map.getString("FIELD_GROUPS_ID"));
		m.add("LKUP_FIELD_TYPE_ID", map.getString("LKUP_FIELD_TYPE_ID"));
		m.add("LKUP_FIELD_ITYPE_ID", map.getString("LKUP_FIELD_ITYPE_ID"));
		m.add("MAX_CHAR", map.getString("MAX_CHAR"));
		
		
		String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQUIRED", d);
		String o = map.getString("IDX","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("IDX", o);
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.FIELDTABLE).append(" WHERE FIELD_GROUPS_ID = ").append(m.getString("FIELD_GROUPS_ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.FIELDTABLE).append(" SET ORDR =  ").append(i).append(" WHERE FIELD_GROUPS_ID = ").append(m.getString("FIELD_GROUPS_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
	}
	
	
	public static MapSet getFieldChoice(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("FIELD_ID", map.getInt("FIELD_ID"));
		
		
		
	/*	m.add("TITLE", map.getString("TITLE"));
		m.add("TITLE_VALUE", map.getString("TITLE_VALUE"));*/
		m.add("column","ID");
		/*int fieldId = map.getInt("FIELD_ID");
		
		if(fieldId <=0)		{
			
			fieldId=map.getInt("_fieldId");
		}
		m.add("table",Table.FIELDCHOICESTABLE);*/
		
		/*if(Operator.equalsIgnoreCase(map.getString("_action"), "deletefieldchoices")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE FIELD_ID =").append(fieldId);
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(m.getInt("ID")).append(")");
			
			m.add("deleteselection",w.toString());
			
		}	*/
		
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.FIELDCHOICESTABLE).append(" WHERE FIELD_ID = ").append(m.getString("FIELD_ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.FIELDCHOICESTABLE).append(" SET ORDR =  ").append(i).append(" WHERE FIELD_ID = ").append(m.getString("FIELD_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		return m;
	}
	
	
	
	public static MapSet getTemplateType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("TEMPLATE", map.getString("TEMPLATE"));
		
		String d = map.getString("LANDSCAPE","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("LANDSCAPE", d);
		
		d = map.getString("PAGE_NUMBERS","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("PAGE_NUMBERS", d);
		
		m.add("PAGE_WIDTH", map.getString("PAGE_WIDTH"));
		m.add("PAGE_HEIGHT", map.getString("PAGE_HEIGHT"));
		
		if(Operator.hasValue(map.getString("FILE_DESIGN"))){
			m.add("FILE_DESIGN", map.getString("FILE_DESIGN"));
		}
		
		
		
		if(Operator.hasValue(map.getString("MARGIN_LEFT")) || Operator.hasValue(map.getString("MARGIN_RIGHT"))){
			m.add("MARGIN_LEFT", map.getDouble("MARGIN_LEFT"));
			m.add("MARGIN_RIGHT", map.getDouble("MARGIN_RIGHT"));
			m.add("MARGIN_TOP", map.getDouble("MARGIN_TOP"));
			m.add("MARGIN_BOTTOM", map.getDouble("MARGIN_BOTTOM"));
		}
	
		return m;
	}
	
	
	
	public static MapSet getTemplateRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_TEMPLATE");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table","REF_ACT_TEMPLATE");
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("type")){
				m.add("table","REF_LKUP_TEMPLATE_TYPE_TEMPLATE");
				m.add("column","LKUP_TEMPLATE_TYPE_ID");
			
			}
			
			m.add("TEMPLATE_ID", map.getInt("TEMPLATE_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.NAME FROM ").append(m.getString("table")).append(" R JOIN TEMPLATE G on R.TEMPLATE_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE TEMPLATE_ID =").append(m.getString("TEMPLATE_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND TEMPLATE_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	public static String getTemplateHistory(int id){
		StringBuilder sb = new StringBuilder();
		sb.append("select F.*  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from TEMPLATE_HISTORY F ");
		
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE F.TEMPLATE_ID=").append(id).append("  ");
		return sb.toString();
	}
	
	
	
	
	
	
	public static MapSet getDivisionsType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("LKUP_DIVISIONS_GROUP_ID", map.getInt("LKUP_DIVISIONS_GROUP_ID"));
		String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQUIRED", d);
		
		d = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ISPUBLIC", d);
		
		return m;
	}
	
	
	public static MapSet getDivisionsFieldType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("DIVISION", map.getString("DIVISION"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("INFO", map.getString("INFO"));
		
		m.add("LKUP_DIVISIONS_TYPE_ID", map.getString("LKUP_DIVISIONS_TYPE_ID"));
	
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from LKUP_DIVISIONS WHERE LKUP_DIVISIONS_TYPE_ID = ").append(m.getString("LKUP_DIVISIONS_TYPE_ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE LKUP_DIVISIONS SET ORDR =  ").append(i).append(" WHERE LKUP_DIVISIONS_TYPE_ID = ").append(m.getString("LKUP_DIVISIONS_TYPE_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		return m;
	}
	
	
	public static MapSet getDivisionsRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_DIVISIONS");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table","REF_ACT_DIVISIONS");
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			if(connecttype.equalsIgnoreCase("feeinn")){
				m.add("table","REF_FEE_IN_DIVISIONS");
				m.add("FEE_GROUP_ID",map.getString("FEE_GROUP_ID"));
				m.add("column","FEE_ID");
			
			}
			if(connecttype.equalsIgnoreCase("libraryinn")){
				m.add("table","REF_LIBRARY_IN_DIVISIONS");
				m.add("LIBRARY_GROUP_ID",map.getString("LIBRARY_GROUP_ID"));
				m.add("column","LIBRARY_ID");
			}
			//NOT IN condition
			if(connecttype.equalsIgnoreCase("feeout")){
				m.add("table","REF_FEE_OUT_DIVISIONS");
				m.add("column","FEE_ID");
				m.add("FEE_GROUP_ID",map.getString("FEE_GROUP_ID"));
			}
			if(connecttype.equalsIgnoreCase("libraryout")){
				m.add("table","REF_LIBRARY_OUT_DIVISIONS");
				m.add("LIBRARY_GROUP_ID",map.getString("LIBRARY_GROUP_ID"));
				m.add("column","LIBRARY_ID");
			}
			m.add("LKUP_DIVISIONS_ID", map.getInt("LKUP_DIVISIONS_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.ID as REF_ID,G.*,T.TYPE FROM ").append(m.getString("table")).append(" R JOIN LKUP_DIVISIONS G on R.LKUP_DIVISIONS_ID=G.ID JOIN LKUP_DIVISIONS_TYPE T on G.LKUP_DIVISIONS_TYPE_ID=T.ID  WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ");
			if(connecttype.equalsIgnoreCase("feeout") || connecttype.equalsIgnoreCase("feeinn")){
				s.append(" AND R.FEE_GROUP_ID = ").append(m.getString("FEE_GROUP_ID"));
			}
			s.append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_DIVISIONS_ID =").append(m.getString("LKUP_DIVISIONS_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			if(connecttype.equalsIgnoreCase("feeinn") || (connecttype.equalsIgnoreCase("feeout"))){
				w.append(" AND  FEE_GROUP_ID in (").append(m.getString("FEE_GROUP_ID")).append(") ");	
			}
			
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_DIVISIONS_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	public static MapSet getProjectActivityRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_ACT_TYPE");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			
			m.add("LKUP_ACT_TYPE_ID", map.getInt("LKUP_ACT_TYPE_ID"));
			m.add("AUTO_ADD", map.getString("AUTO_ADD"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.ID as REF_ID,R.AUTO_ADD,G.* FROM ").append(m.getString("table")).append(" R JOIN LKUP_ACT_TYPE G on R.LKUP_ACT_TYPE_ID=G.ID   WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_ACT_TYPE_ID =").append(m.getString("LKUP_ACT_TYPE_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_ACT_TYPE_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	
	public static MapSet getFeeGroupType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("GROUP_NAME", map.getString("GROUP_NAME"));
	
		m.add("START_DATE", map.getString("START_DATE"));
		m.add("EXPIRATION_DATE", map.getString("EXPIRATION_DATE"));
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.REFFEEGROUPTABLE).append(" WHERE FEE_GROUP_ID = ").append(m.getString("ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			//m.add("column","FEE_ID");
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.REFFEEGROUPTABLE).append(" SET ORDR =  ").append(i).append(" WHERE ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		return m;
	}
	
	
	public static MapSet getFeeGroupRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_FEE_GROUP");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table","REF_ACT_FEE_GROUP");
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			m.add("FEE_GROUP_ID", map.getInt("FEE_GROUP_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.ID as REF_ID,G.* FROM ").append(m.getString("table")).append(" R JOIN FEE_GROUP G on R.FEE_GROUP_ID=G.ID  WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE FEE_GROUP_ID =").append(m.getString("FEE_GROUP_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND FEE_GROUP_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	public static MapSet getRefFeeGroup(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("FEE_GROUP_ID", map.getString("FEE_GROUP_ID"));
		m.add("FEE_ID", map.getString("FEE_ID"));
	/*	String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQ", d);*/
		
		/*d = map.getString("MANUAL_ACCOUNT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("MANUAL_ACCOUNT", d);*/
		
		/*d = map.getString("AUTO_ADD","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("AUTO_ADD", d);*/
		
		if(m.getInt("ID")<0){
			m.add("addorder", "Y");
			StringBuilder order = new StringBuilder();
			order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.REFFEEGROUPTABLE).append(" WHERE FEE_GROUP_ID = ").append(m.getString("FEE_GROUP_ID"));
			m.add("orderquery", order.toString());
		}
		return m;
		
	}
	
	public static MapSet getRefFeeFormula(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("FEE_ID", map.getString("FEE_ID"));
		m.add("START_DATE", map.getString("START_DATE"));
		
		m.add("FINANCE_MAP_ID", map.getString("FINANCE_MAP_ID"));
		m.add("LKUP_FORMULA_ID", map.getString("LKUP_FORMULA_ID"));
		
		
		m.add("INPUT_LABEL1", map.getString("INPUT_LABEL1"));
		m.add("INPUT_LABEL2", map.getString("INPUT_LABEL2"));
		m.add("INPUT_LABEL3", map.getString("INPUT_LABEL3"));
		m.add("INPUT_LABEL4", map.getString("INPUT_LABEL4"));
		m.add("INPUT_LABEL5", map.getString("INPUT_LABEL5"));
		
		m.add("INPUT_TYPE1", map.getString("INPUT_TYPE1"));
		m.add("INPUT_TYPE2", map.getString("INPUT_TYPE2"));
		m.add("INPUT_TYPE3", map.getString("INPUT_TYPE3"));
		m.add("INPUT_TYPE4", map.getString("INPUT_TYPE4"));
		m.add("INPUT_TYPE5", map.getString("INPUT_TYPE5"));
		
		m.add("INPUT1", map.getString("INPUT1","0"));
		m.add("INPUT2", map.getString("INPUT2","0"));
		m.add("INPUT3", map.getString("INPUT3","0"));
		m.add("INPUT4", map.getString("INPUT4","0"));
		m.add("INPUT5", map.getString("INPUT5","0"));
		
		
		m.add("INPUT_DESCRIPTION1", map.getString("INPUT_DESCRIPTION1"));
		m.add("INPUT_DESCRIPTION2", map.getString("INPUT_DESCRIPTION2"));
		m.add("INPUT_DESCRIPTION3", map.getString("INPUT_DESCRIPTION3"));
		m.add("INPUT_DESCRIPTION4", map.getString("INPUT_DESCRIPTION4"));
		m.add("INPUT_DESCRIPTION5", map.getString("INPUT_DESCRIPTION5"));
		
		
		String d = map.getString("INPUT_EDITABLE1","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("INPUT_EDITABLE1", d);
		
		d = map.getString("INPUT_EDITABLE2","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("INPUT_EDITABLE2", d);
		
		d = map.getString("INPUT_EDITABLE3","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("INPUT_EDITABLE3", d);
		
		d = map.getString("INPUT_EDITABLE4","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("INPUT_EDITABLE4", d);
		
		d = map.getString("INPUT_EDITABLE5","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("INPUT_EDITABLE5", d);
		
		
		d = map.getString("UPDATABLE","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("UPDATABLE", d);
		
		
		m.add("FEE_GROUP_ID", map.getString("FEE_GROUP_ID"));
		
		boolean exp = false; 
		if(map.getInt("OLD_LKUP_FORMULA_ID")>0){
			if(map.getInt("OLD_LKUP_FORMULA_ID")!= map.getInt("LKUP_FORMULA_ID")){
				m.add("NEW_START_DATE", map.getString("NEW_START_DATE"));
				exp = true; 
			}
		}
		if(Operator.hasValue(map.getString("NEW_START_DATE"))){
			m.remove("START_DATE");
			m.add("START_DATE", map.getString("NEW_START_DATE"));
			m.add("NEW_START_DATE", map.getString("NEW_START_DATE"));
			String ed =  map.getString("NEW_START_DATE");
			Timekeeper k = new Timekeeper();
			k.setDay(Operator.subString(ed, 8, 10));
			k.setMonth(Operator.subString(ed, 5, 7));
			k.setYear(Operator.subString(ed, 0, 4));
			
			k.addDay(-1);
			
			Logger.info(k.getString("YYYY/MM/DD")+" EXP DATE EEEEEEEEEE");
			Logger.info(Operator.subString(ed, 0, 4)+" EXP DATE EEEEEEEEEE");
			Logger.info(Operator.subString(ed, 5, 7)+" EXP DATE EEEEEEEEEE");
			Logger.info(Operator.subString(ed, 8, 10)+" EXP DATE EEEEEEEEEE");
			
			m.add("EXPIRATION_DATE", k.getString("YYYY/MM/DD"));
		}
		
		d = map.getString("MANUAL_ACCOUNT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("MANUAL_ACCOUNT", d);
		
		/*String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQ", d);
		
		d = map.getString("MANUAL_ACCOUNT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("MANUAL_ACCOUNT", d);
		
		d = map.getString("AUTO_ADD_FEE","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("AUTO_ADD_FEE", d);
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.REFFEEGROUPTABLE).append(" WHERE FEE_GROUP_ID = ").append(m.getString("FEE_GROUP_ID"));
		m.add("orderquery", order.toString());*/
		
		/*if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.FIELDTABLE).append(" SET ORDR =  ").append(i).append(" WHERE FIELD_GROUPS_ID = ").append(m.getString("FIELD_GROUPS_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	*/
		return m;
	}
	
	
	public static String getRefFeeId(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" RF.* ");
		sb.append(",");	
		sb.append(" LF.ID as FORMULA_ID, LF.NAME AS FORMULA_NAME ");
		sb.append(",");	
		sb.append(" F.NAME AS FEE_NAME ");
		sb.append(",");	
		sb.append(" MP.ID as MAP_ID, MP.ACCOUNT_NUMBER, MP.BUDGET_UNIT,MP.KEY_CODE ");
		sb.append(",");	
		
		sb.append(" CONVERT(VARCHAR(10),RF.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),RF.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from REF_FEE_FORMULA RF  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RF.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RF.UPDATED_BY = UP.ID "); 
		
		sb.append("   LEFT OUTER JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID ");
		sb.append("   LEFT OUTER JOIN FINANCE_MAP MP on RF.FINANCE_MAP_ID = MP.ID ");
		sb.append("  JOIN FEE F on RF.FEE_ID = F.ID ");
		sb.append(" WHERE RF.ACTIVE='Y' ");
		sb.append(" AND RF.ID =  ").append(id);
		
		return sb.toString();
	}
	
	public static String getFeeNames(String search){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),RF.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from FEE RF  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on RF.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on RF.UPDATED_BY = UP.ID "); 
		
		sb.append("   LEFT OUTER JOIN REF_FEE_FORMULA RFF on RF.ID = RFF.FEE_ID ");
		sb.append(" WHERE RF.ACTIVE='Y'  AND RFF.ID IS NULL ");
		
		if(Operator.hasValue(search)){
			sb.append(" AND LOWER(RF.NAME) like '%").append(Operator.sqlEscape(search.toLowerCase())).append("%'  ");
		}

		sb.append(" ORDER BY  RF.UPDATED_DATE DESC,RF.NAME ");
		
		return sb.toString();
	}
	
	
	public static String getFeeGroupFields(int groupid){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select DISTINCT   R.ID as REF_FEE_GROUP_ID,RF.ID ,R.ORDR,F.NAME,RF.FEE_ID,RF.START_DATE,RF.EXPIRATION_DATE, R.REQ,R.AUTO_ADD,RF.MANUAL_ACCOUNT,R.PLAN_CHK_REQ,R.REVIEW_FEE,LF.NAME as FORMULA_NAME,MP.KEY_CODE, MP.ACCOUNT_NUMBER,COUNT(FIN.ID) as FINN_ID,COUNT(FOUT.ID) as FOUT_ID");
		sb.append(",");	
		sb.append(" count(GG.FEE_ID) as MULTI ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.START_DATE,101) as C_START_DATE ");
		
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.EXPIRATION_DATE,101) as C_EXPIRATION_DATE ");
		
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),RF.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(",");
		sb.append("   R.SEQUENCE "); 
		sb.append(" from REF_FEE_GROUP R ");
		sb.append(" JOIN REF_FEE_FORMULA RF on R.FEE_ID=RF.FEE_ID  ");
		sb.append(" JOIN FEE F on RF.FEE_ID=F.ID");
		sb.append("   LEFT OUTER JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID ");
		sb.append("   LEFT OUTER JOIN FINANCE_MAP MP on RF.FINANCE_MAP_ID = MP.ID ");
		sb.append("   LEFT OUTER JOIN USERS CU on RF.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN USERS UP on RF.UPDATED_BY = UP.ID "); 
		sb.append("	  LEFT OUTER JOIN REF_FEE_IN_DIVISIONS FIN on R.FEE_ID = FIN.FEE_ID and FIN.ACTIVE='Y' AND   FIN.FEE_GROUP_ID=").append(groupid).append(" "); 
		sb.append("	  LEFT OUTER JOIN REF_FEE_OUT_DIVISIONS FOUT on R.FEE_ID = FOUT.FEE_ID and FOUT.ACTIVE='Y' AND   FOUT.FEE_GROUP_ID=").append(groupid).append(" "); 
		
		sb.append("	  LEFT OUTER JOIN REF_FEE_GROUP GG on R.FEE_ID = GG.FEE_ID and GG.ACTIVE='Y' "); 
		sb.append(" WHERE  (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null) and R.FEE_GROUP_ID=").append(groupid).append("  AND R.ACTIVE='Y'   ");
		
		sb.append(" group by R.ID ,RF.ID,R.ORDR,F.NAME,RF.START_DATE,RF.EXPIRATION_DATE,RF.UPDATED_DATE,RF.CREATED_DATE,CU.USERNAME,RF.FEE_ID,UP.USERNAME, R.SEQUENCE, R.REQ,R.AUTO_ADD,RF.MANUAL_ACCOUNT,R.PLAN_CHK_REQ,R.REVIEW_FEE,LF.NAME,MP.KEY_CODE, MP.ACCOUNT_NUMBER,FIN.ID ,FOUT.ID order by R.ordr  ");

		return sb.toString();
	}
	
	
	public static String getFormulaFields(int id){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select DISTINCT   R.ID as REF_FEE_GROUP_ID,RF.ID ,R.ORDR,F.NAME,RF.FEE_ID,RF.START_DATE,RF.EXPIRATION_DATE, R.REQ,R.AUTO_ADD,R.MANUAL_ACCOUNT,R.PLAN_CHK_REQ,R.REVIEW_FEE,LF.NAME as FORMULA_NAME, MP.ACCOUNT_NUMBER");
		sb.append(",");	
		sb.append("  G.GROUP_NAME ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.START_DATE,101) as C_START_DATE ");
		
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.EXPIRATION_DATE,101) as C_EXPIRATION_DATE ");
		
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),RF.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),RF.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from REF_FEE_GROUP R ");
		sb.append(" JOIN REF_FEE_FORMULA RF on R.FEE_ID=RF.FEE_ID  ");
		sb.append(" JOIN FEE F on RF.FEE_ID=F.ID ");
		sb.append("  JOIN FEE_GROUP G on R.FEE_GROUP_ID=G.ID    ");
		
	    

		sb.append("   LEFT OUTER JOIN LKUP_FORMULA LF on RF.LKUP_FORMULA_ID = LF.ID ");
		sb.append("   LEFT OUTER JOIN FINANCE_MAP MP on RF.FINANCE_MAP_ID = MP.ID ");
		sb.append("   LEFT OUTER JOIN USERS CU on RF.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN USERS UP on RF.UPDATED_BY = UP.ID "); 
	
		
		sb.append(" WHERE  LF.ID=").append(id).append("    ");
		

		return sb.toString();
	}
	
	
	public static MapSet insertFeeGroupFeeRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN","FEE_GROUP_ID");
		m.add("FEE_ID", map.getString("FEE_ID"));
		m.add("ORDR", map.getString("ORDR"));
		m.add("REQ", map.getString("REQ"));
		return m;
	}
	
	
	public static MapSet getFeeFormula(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DEFINITION", map.getString("DEFINITION"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		
		if(Operator.hasValue(map.getString("NEW_START_DATE"))){
			m.add("NEW_START_DATE", map.getString("NEW_START_DATE"));
			String ed =  map.getString("NEW_START_DATE");
			Timekeeper k = new Timekeeper();
			k.setDay(Operator.subString(ed, 8, 10));
			k.setMonth(Operator.subString(ed, 5, 7));
			k.setYear(Operator.subString(ed, 0, 4));
			k.addDay(-1);
			m.add("EXPIRATION_DATE", k.getString("YYYY/MM/DD"));
		}
		return m;
	}
	
	public static MapSet getFeeName(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		return m;
	}
	
	public static MapSet getFeeNameValue(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("VALUE", map.getString("VALUE"));
		
		if(Operator.hasValue(map.getString("NEW_START_DATE"))){
			m.add("NEW_START_DATE", map.getString("NEW_START_DATE"));
			String ed =  map.getString("NEW_START_DATE");
			Timekeeper k = new Timekeeper();
			k.setDay(Operator.subString(ed, 8, 10));
			k.setMonth(Operator.subString(ed, 5, 7));
			k.setYear(Operator.subString(ed, 0, 4));
			k.addDay(-1);
			m.add("EXPIRATION_DATE", k.getString("YYYY/MM/DD"));
		}
		m.add("START_DATE", map.getString("START_DATE"));
//		m.add("EXPIRATION_DATE", map.getString("EXPIRATION_DATE"));
		
		
		String d = map.getString("SUM_NAMES","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("SUM_NAMES", d);
		
		
		/*String table = Table.FEENAMEVALUETABLE;
		StringBuilder sb = new StringBuilder();
		if(map.getInt("ID")>0){
			sb.append(" UPDATE ").append(table).append(" SET ");
			sb.append(" NAME = '").append(Operator.sqlEscape(m.getString("NAME"))).append("'");
			sb.append(" , ");
			sb.append(" DESCRIPTION = '").append(Operator.sqlEscape(m.getString("DESCRIPTION"))).append("'");
			
			if(Operator.hasValue(m.getString("EXPIRATION_DATE"))){
			sb.append(" , ");
			sb.append(" EXPIRATION_DATE = '").append(m.getString("EXPIRATION_DATE")).append("'");
			}
			sb.append(" , ");
			sb.append(" START_DATE = '").append(m.getString("START_DATE")).append("'");
			
			sb.append(" , ");
			sb.append(" SUM_NAMES = '").append(m.getString("SUM_NAMES")).append("'");
			
			sb.append(" , ");
			sb.append(" VALUE = ").append(m.getString("VALUE"));
			sb.append(" , ");
			sb.append(" UPDATED_BY = ").append(890);
			sb.append(" , ");
			sb.append(" UPDATED_DATE = CURRENT_TIMESTAMP");
			sb.append(" , ");
			sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" WHERE ID =  ").append(m.getString("ID"));
			 
		}else {
			
			sb.append(" INSERT INTO  ").append(table).append(" ( CREATED_IP, UPDATED_IP, CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE, NAME, DESCRIPTION, START_DATE, EXPIRATION_DATE, VALUE,SUM_NAMES ) VALUES ( ");
			sb.append("'").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" , ");
			sb.append("'").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" , ");
			sb.append("").append(890);
			sb.append(" , ");
			sb.append("").append(890);
			sb.append(" , ");
			sb.append(" CURRENT_TIMESTAMP");
			sb.append(" , ");
			sb.append(" CURRENT_TIMESTAMP");
			
			sb.append(" , ");
			sb.append("'").append(Operator.sqlEscape(m.getString("NAME"))).append("'");
			sb.append(" , ");
			sb.append("'").append(Operator.sqlEscape(m.getString("DESCRIPTION"))).append("'");
			sb.append(" , ");
			sb.append("'").append(m.getString("START_DATE")).append("'");
			if(Operator.hasValue(m.getString("EXPIRATION_DATE"))){
			sb.append(" , ");
			sb.append("'").append(m.getString("EXPIRATION_DATE")).append("'");
			}else {
				sb.append(" , ");
				sb.append("null");
			}
			sb.append(" , ");
			sb.append("").append(m.getString("VALUE"));
			sb.append(" , ");
			sb.append("'").append(m.getString("SUM_NAMES")).append("'");
			sb.append(" )");
		}
		
		// custom insert/update date in sage needs to be corrected
		m.add("COMMAND", sb.toString());*/
		
		
		return m;
	}
	
	public static MapSet getFeeMap(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("KEY_CODE", map.getString("KEY_CODE"));
		m.add("BUDGET_UNIT", map.getString("BUDGET_UNIT"));
		m.add("ACCOUNT_NUMBER", map.getString("ACCOUNT_NUMBER"));
		m.add("FUND", map.getString("FUND"));
		return m;
	}
	
	//Review
	
	public static String getReviewFields(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.REVIEWTABLE;
		sb.append("select DISTINCT R.*,T.DESCRIPTION as TYPE   ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),R.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),R.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(",");
		sb.append("   'PRESS ' + CONVERT(VARCHAR(5),VM.PRESS) +' - '+ VM.DESCRIPTION  AS TEXT "); 
		sb.append(" from " ).append(table).append(" R ");
		sb.append(" LEFT OUTER JOIN LKUP_REVIEW_TYPE T on R.LKUP_REVIEW_TYPE_ID = T.ID   ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on R.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on R.UPDATED_BY = UP.ID "); 
		sb.append("	  LEFT OUTER JOIN ").append(Table.REFVOIPREVIEW).append(" VR on R.ID = VR.REVIEW_ID  AND VR.ACTIVE='Y' AND VR.LKUP_VOIP_MENU_ID>0	 "); 
		sb.append("	  LEFT OUTER JOIN ").append(Table.LKUPVOIPMENUTABLE).append(" VM on VR.LKUP_VOIP_MENU_ID = VM.ID "); 
		sb.append("	WHERE REVIEW_GROUP_ID=").append(groupid).append(" AND R.ACTIVE='Y' order by ordr ");
		return sb.toString();
	}
	
	
	//Act Review Group map
	
	public static String getActReviewGroupMap(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.REVIEWTABLE;
		sb.append("SELECT LAT.DESCRIPTION , RG.NAME , RG.DESCRIPTION ,RG.REQUIRED  ");   
		sb.append(" from " );
		sb.append(" REF_ACT_REVIEW_GROUP RARG  JOIN REVIEW_GROUP RG ON RARG.REVIEW_GROUP_ID=RG.ID   ");
		sb.append(" JOIN LKUP_ACT_TYPE LAT ON RARG.LKUP_ACT_TYPE_ID=LAT.ID ");
		sb.append("	WHERE REVIEW_GROUP_ID=").append(groupid).append(" order by ordr ");
		return sb.toString();
	}
	
	
	public static String getReviewStatus(int typeId,String table,String sortfield , String sortorder){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from ").append(table).append(" A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE A.ACTIVE='Y' AND A.LKUP_REVIEW_TYPE_ID= "+typeId);
	
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult " );
		//WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		
		return sb.toString();
	
	}
	
	
	public static String getRefVoipTransfer(int typeId,String table,String sortfield , String sortorder){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.").append(sortfield).append(" ").append(sortorder).append(") AS RowNum  ");
		sb.append(",");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(" from ").append(table).append(" A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE A.ACTIVE='Y' AND A.LKUP_VOIP_MENU_ID= "+typeId);
	
		
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult " );
		//WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		
		return sb.toString();
	
	}
	
	public static MapSet getReviewGroupMulti(Cartographer map){
		MapSet m = getCommon(map);

		String d = map.getString("AUTO_ADD");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("AUTO_ADD", d);
		String o = map.getString("INSPECTION","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INSPECTION", o);
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	public static MapSet getReviewMulti(Cartographer map){
		MapSet m = getCommon(map);
		
		m.add("LKUP_REVIEW_TYPE_ID", map.getString("LKUP_REVIEW_TYPE_ID"));
		m.add("REQUIRED",map.getInt("REQUIRED"));
		m.add("DISPLAY_TYPE",map.getInt("DISPLAY_TYPE"));
		
		
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getInt("MAX_ACTIVE_APPOINTMENTS"));
		
		m.add("LKUP_VOIP_MENU_ID", map.getInt("LKUP_VOIP_MENU_ID"));
		
		m.add("DAYS_TILL_DUE",map.getInt("DAYS_TILL_DUE"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	public static MapSet getReviewGroup(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("GROUP_NAME", map.getString("GROUP_NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		String d = map.getString("AUTO_ADD","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("AUTO_ADD", d);
		
		d = map.getString("ACTIVE");
		
		m.add("ACTIVE", d);
		/*String o = map.getString("INSPECTION","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INSPECTION", o);*/
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.REVIEWGROUPTABLE).append(" WHERE REVIEW_GROUP_ID = ").append(m.getString("REVIEW_GROUP_ID"));
		m.add("orderquery", order.toString());
		
		return m;
	}
	
	public static MapSet getRefVoipReview(Cartographer map){
		MapSet m = getCommon(map);
		m.add("REVIEW_ID", map.getInt("ID"));
		m.add("LKUP_VOIP_MENU_ID", map.getString("LKUP_VOIP_MENU_ID","0"));
		
		/*String o = map.getString("ACTIVE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ACTIVE", o);*/
		
		return m;
	}
	
	
	public static MapSet getReviews(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("REVIEW_GROUP_ID", map.getString("REVIEW_GROUP_ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		
		m.add("LKUP_REVIEW_TYPE_ID", map.getString("LKUP_REVIEW_TYPE_ID"));
		m.add("LKUP_FIELD_ITYPE_ID", map.getString("LKUP_FIELD_ITYPE_ID"));
		m.add("MAX_CHAR", map.getString("MAX_CHAR"));
		
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("MAX_ACTIVE_APPOINTMENTS", map.getInt("MAX_ACTIVE_APPOINTMENTS"));
		
		m.add("DAYS_TILL_DUE",map.getInt("DAYS_TILL_DUE"));
		
		String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQUIRED", d);
		String o = map.getString("DISPLAY_TYPE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "AUTO";}
		m.add("DISPLAY_TYPE", o);
		
		d = map.getString("ONLINE","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ONLINE", d);
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.REVIEWTABLE).append(" WHERE REVIEW_GROUP_ID = ").append(m.getString("REVIEW_GROUP_ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.REVIEWTABLE).append(" SET ORDR =  ").append(i).append(" WHERE REVIEW_GROUP_ID = ").append(m.getString("REVIEW_GROUP_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		return m;
	}
	
	public static MapSet getReviewType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("CODE", map.getString("CODE"));
		m.add("AVAILABILITY_MINUTES", map.getString("AVAILABILITY_MINUTES"));
		String o = map.getString("INSPECTABLE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INSPECTABLE", o);
	
		return m;
	}
	
	public static MapSet getReviewStatus(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("STATUS", map.getString("STATUS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("LKUP_REVIEW_TYPE_ID", map.getString("LKUP_REVIEW_TYPE_ID"));
		m.add("LIBRARY_GROUP_ID", map.getString("LIBRARY_GROUP_ID"));
		m.add("DAYS_TILL_DUE", map.getInt("DAYS_TILL_DUE",0));
		
		String o = map.getString("APPROVED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("APPROVED", o);
		o = map.getString("UNAPPROVED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("UNAPPROVED", o);
		
		o = map.getString("ASSIGN","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ASSIGN", o);
		
		o = map.getString("INHERIT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INHERIT", o);
		
		o = map.getString("FINAL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("FINAL", o);
		
		o = map.getString("SCHEDULE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("SCHEDULE", o);
		
		o = map.getString("SCHEDULE_CANCEL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("SCHEDULE_CANCEL", o);
		
		o = map.getString("ATTACHMENT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ATTACHMENT", o);
		
		o = map.getString("SCHEDULE_INSPECTION","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("SCHEDULE_INSPECTION", o);
		
		o = map.getString("SCHEDULE_INSPECTION_CANCEL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("SCHEDULE_INSPECTION_CANCEL", o);
		
		o = map.getString("REQUIRE_ISSUED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("REQUIRE_ISSUED", o);
		
		o = map.getString("REQUIRE_PAID","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("REQUIRE_PAID", o);
		
		o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);
		
		o = map.getString("PDOX","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PDOX", o);
		return m;
	}
	
	
	public static MapSet getReviewStatusMulti(Cartographer map){
		MapSet m = getCommon(map);
		
	
		m.add("LIBRARY_GROUP_ID", map.getString("LIBRARY_GROUP_ID"));
		
	
		m.add("APPROVED", map.getString("APPROVED"));
		
		m.add("UNAPPROVED", map.getString("UNAPPROVED"));
		
		
		m.add("ASSIGN", map.getString("ASSIGN"));
		
		m.add("FINAL", map.getString("FINAL"));
		
		m.add("SCHEDULE", map.getString("SCHEDULE"));
		
		m.add("SCHEDULE_CANCEL", map.getString("SCHEDULE_CANCEL"));
		
		m.add("ATTACHMENT", map.getString("ATTACHMENT"));
		
		m.add("DAYS_TILL_DUE", map.getString("DAYS_TILL_DUE"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());

		
		return m;
	}
	
	
	
	
	public static MapSet getReviewGroupRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table",Table.REFPROJECTREVIEWGROUPTABLE);
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table",Table.REFACTREVIEWGROUPTABLE);
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
		/*	if(connecttype.equalsIgnoreCase("user")){
				m.add("table","REF_USERS_FIELD_GROUPS");
				m.add("column","LKUP_USERS_TYPE_ID");
			
			}*/
			
			if(connecttype.equalsIgnoreCase("lso")){
				m.add("table",Table.REFLSOREVIEWGROUPTABLE);
				m.add("column","LKUP_LSO_TYPE_ID");
			
			}
			
			m.add("REQUIRED", "Y");
			m.add("REVIEW_GROUP_ID", map.getInt("REVIEW_GROUP_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		
		if(c.length==1){
			s.append(" SELECT R.*,G.GROUP_NAME, G.DESCRIPTION AS GROUP_DESCRIPTION FROM ").append(m.getString("table")).append(" R JOIN REVIEW_GROUP G on R.REVIEW_GROUP_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE REVIEW_GROUP_ID =").append(m.getString("REVIEW_GROUP_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND REVIEW_GROUP_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	

	
	public static MapSet getAvailability(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TITLE", map.getString("TITLE"));
		m.add("PRESCHEDULE_DAYS", map.getString("PRESCHEDULE_DAYS"));
		m.add("LKUP_HOLIDAY_TYPE_ID", map.getString("LKUP_HOLIDAY_TYPE_ID"));
		
		return m;
	}
	
	
	public static MapSet mergeStatus(Cartographer map){
		MapSet m = getCommon(map);
	
		m.add("STATUS_ID", map.getString("STATUS_ID"));
		m.add("ids",map.getString("IDS"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("IDS")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	public static MapSet copyField(Cartographer map){
		MapSet m = getCommon(map);
	
		m.add("GROUP_ID", map.getString("GROUP_ID"));
		m.add("id",map.getString("ID"));
		
		StringBuilder sb = new StringBuilder();
		Logger.info(map.getString("IDS")+" set map");
		sb.append(" ID in (").append(map.getString("ID")).append(")");
		m.add("wherecondition",sb.toString());
		
		return m;
	}
	
	public static String getAvailabilityDefault(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.AVAILABILITYDEFAULTTABLE;
		sb.append("select F.*  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" F ");
	/*	sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE T on F.LKUP_FIELD_TYPE_ID = T.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE IT on F.LKUP_FIELD_ITYPE_ID = IT.ID  "); */
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE AVAILABILITY_ID=").append(groupid).append(" AND F.ACTIVE='Y' order by DAY_OF_WEEK,TIME_START ");
		return sb.toString();
	}
	
	
	public static MapSet getAvailabilityDefault(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
		m.add("DAY_OF_WEEK", map.getString("DAY_OF_WEEK"));
		
		m.add("TIME_START", map.getString("TIME_START"));
		m.add("TIME_END", map.getString("TIME_END"));
		m.add("SEATS", map.getString("SEATS"));
		m.add("BUFFER_SEATS", map.getString("BUFFER_SEATS"));
		
		m.add("BUFFER_HOURS", map.getString("BUFFER_HOURS"));
		
		m.add("TIME_BEGIN", map.getString("TIME_BEGIN"));
		m.add("TIME_STOP", map.getString("TIME_STOP"));
		
		return m;
	}
	
	public static String getAvailabilityCustom(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.AVAILABILITYCUSTOMTABLE;
		sb.append("select F.*  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from " ).append(table).append(" F ");
	/*	sb.append(" LEFT OUTER JOIN LKUP_FIELD_TYPE T on F.LKUP_FIELD_TYPE_ID = T.ID   ");
		sb.append(" LEFT OUTER JOIN LKUP_FIELD_ITYPE IT on F.LKUP_FIELD_ITYPE_ID = IT.ID  "); */
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE AVAILABILITY_ID=").append(groupid).append(" AND F.ACTIVE='Y' order by CUSTOM_DATE DESC ,TIME_START ");
		return sb.toString();
	}
	
	public static String getAvailabilitySeats(int groupid,String date){
		StringBuilder sb = new StringBuilder();
		Timekeeper d = new Timekeeper();
		d.setDate(date);
		
		sb.append(" WITH Q AS (select F.*  , 'C' as OD, ");
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ,  ");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ,   CU.USERNAME AS CREATED ,   UP.USERNAME as UPDATED   ");
		sb.append(" from AVAILABILITY_CUSTOM F    ");
		sb.append(" LEFT OUTER JOIN USERS CU on F.CREATED_BY = CU.ID 	   ");
		sb.append(" LEFT OUTER JOIN USERS UP on F.UPDATED_BY = UP.ID 	 ");
		sb.append(" WHERE AVAILABILITY_ID=").append(groupid).append(" AND F.ACTIVE='Y'  ");
		sb.append(" AND CUSTOM_DATE = '").append(d.getString("YYYY-MM-DD")).append("'  ");
		sb.append(" union  ");
		sb.append(" select 0 as ID , ");
		sb.append(" AVAILABILITY_ID, ");
		sb.append(" '").append(d.getString("YYYY-MM-DD")).append("' as CUSTOM_DATE, ");
		sb.append(" TIME_START, ");
		sb.append(" TIME_END, ");
		sb.append(" SEATS, ");
		sb.append(" F.ACTIVE, ");
		sb.append(" F.CREATED_BY, ");
		sb.append(" F.CREATED_DATE, ");
		sb.append(" F.UPDATED_BY, ");
		sb.append(" F.UPDATED_DATE, ");
		sb.append(" F.CREATED_IP, ");
		sb.append(" F.UPDATED_IP, ");
		sb.append(" BUFFER_SEATS, ");
		sb.append(" BUFFER_HOURS, ");
		sb.append(" TIME_BEGIN, ");
		sb.append(" TIME_STOP  , 'D' as OD, ");
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ,  ");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ,    ");
		sb.append(" CU.USERNAME AS CREATED ,   UP.USERNAME as UPDATED   ");
		sb.append(" from AVAILABILITY_DEFAULT F    ");
		sb.append("  LEFT OUTER JOIN USERS CU on F.CREATED_BY = CU.ID 	   ");
		sb.append(" LEFT OUTER JOIN USERS UP on F.UPDATED_BY = UP.ID 	 ");
		sb.append(" WHERE AVAILABILITY_ID=").append(groupid).append(" AND F.ACTIVE='Y' AND DAY_OF_WEEK=").append(d.DAY_OF_WEEK()).append(" ");
		sb.append(" ) select ROW_NUMBER() OVER (Order by ID) AS ROW_NUM , * from Q  ORDER BY OD  ");
		return sb.toString();
	}
	
	public static MapSet getAvailabilityCustom(Cartographer map){
		return getAvailabilityCustom(map, 0, new MapSet());
	}
	
	public static MapSet getAvailabilityCustom(Cartographer map,int rownum,MapSet seats){
		MapSet m = getCommon(map);
		
		if(rownum>0){
			m.add("ID", seats.getInt("ID"));
			m.add("AVAILABILITY_ID", seats.getString("AVAILABILITY_ID"));
			m.add("CUSTOM_DATE", seats.getString("CUSTOM_DATE"));
			Logger.info(m.getString("CUSTOM_DATE")+"$$$$$$$$$$$$$$$$$$");
			m.add("TIME_START", seats.getString("TIME_START"));
			m.add("TIME_END", seats.getString("TIME_END"));
			m.add("SEATS", seats.getString("SEATS"));
			m.add("BUFFER_SEATS", seats.getString("BUFFER_SEATS"));
			m.add("BUFFER_HOURS", seats.getString("BUFFER_HOURS"));
			m.add("TIME_BEGIN", seats.getString("TIME_BEGIN"));
			m.add("TIME_STOP", seats.getString("TIME_STOP"));
		}else {
			m.add("ID", map.getInt("ID"));
			m.add("AVAILABILITY_ID", map.getString("AVAILABILITY_ID"));
			m.add("CUSTOM_DATE", map.getString("CUSTOM_DATE"));
			Logger.info(m.getString("CUSTOM_DATE")+"$$$$$$$$$$$$$$$$$$");
			m.add("TIME_START", map.getString("TIME_START"));
			m.add("TIME_END", map.getString("TIME_END"));
			
			
			
			m.add("SEATS", map.getString("SEATS"));
			m.add("BUFFER_SEATS", map.getString("BUFFER_SEATS"));
			m.add("BUFFER_HOURS", map.getString("BUFFER_HOURS"));
			
			m.add("TIME_BEGIN", map.getString("TIME_BEGIN"));
			m.add("TIME_STOP", map.getString("TIME_STOP"));
		}
		//TODO USER RETRIEVAL ID
		String table = Table.AVAILABILITYCUSTOMTABLE;
		StringBuilder sb = new StringBuilder();
		if(m.getInt("ID")>0){
			sb.append(" UPDATE ").append(table).append(" SET ");
			sb.append(" AVAILABILITY_ID = ").append(m.getString("AVAILABILITY_ID"));
			sb.append(" , ");
			sb.append(" CUSTOM_DATE = '").append(m.getString("CUSTOM_DATE")).append("'");
			sb.append(" , ");
			sb.append(" TIME_START = '").append(m.getString("TIME_START")).append("'");
			sb.append(" , ");
			sb.append(" TIME_END = '").append(m.getString("TIME_END")).append("'");
			sb.append(" , ");
			sb.append(" SEATS = ").append(m.getString("SEATS"));
			sb.append(" , ");
			sb.append(" BUFFER_SEATS = ").append(m.getString("BUFFER_SEATS"));
			sb.append(" , ");
			sb.append(" BUFFER_HOURS = ").append(m.getString("BUFFER_HOURS"));
			sb.append(" , ");
			sb.append(" UPDATED_BY = ").append(m.getInt("UPDATED_BY"));
			sb.append(" , ");
			sb.append(" UPDATED_DATE = CURRENT_TIMESTAMP");
			
			
			sb.append(" , ");
			sb.append(" UPDATED_IP = '").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" , ");
			sb.append(" TIME_BEGIN = '").append(m.getString("TIME_BEGIN")).append("'");
			sb.append(" , ");
			sb.append(" TIME_STOP = '").append(m.getString("TIME_STOP")).append("'");
			
			
			sb.append(" WHERE ID =  ").append(m.getString("ID"));
			 
		}else {
			
			sb.append(" INSERT INTO  ").append(table).append(" ( CREATED_IP, UPDATED_IP, CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE, AVAILABILITY_ID, CUSTOM_DATE, TIME_START, TIME_END, SEATS, BUFFER_SEATS, BUFFER_HOURS,TIME_BEGIN,TIME_STOP ) VALUES ( ");
			sb.append("'").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" , ");
			sb.append("'").append(Operator.sqlEscape(m.getString("UPDATED_IP"))).append("'");
			sb.append(" , ");
			sb.append("").append(m.getInt("UPDATED_BY"));
			sb.append(" , ");
			sb.append("").append(m.getInt("UPDATED_BY"));
			sb.append(" , ");
			sb.append(" CURRENT_TIMESTAMP");
			sb.append(" , ");
			sb.append(" CURRENT_TIMESTAMP");
			sb.append(" , ");
			sb.append("").append(m.getString("AVAILABILITY_ID"));
			sb.append(" , ");
			sb.append("'").append(m.getString("CUSTOM_DATE")).append("'");
			sb.append(" , ");
			sb.append("'").append(m.getString("TIME_START")).append("'");
			sb.append(" , ");
			sb.append("'").append(m.getString("TIME_END")).append("'");
			sb.append(" , ");
			sb.append("").append(m.getString("SEATS"));
			sb.append(" , ");
			sb.append("").append(m.getString("BUFFER_SEATS"));
			sb.append(" , ");
			sb.append(" ").append(m.getString("BUFFER_HOURS"));
			sb.append(" , ");
			sb.append("'").append(m.getString("TIME_BEGIN")).append("'");
			sb.append(" , ");
			sb.append("'").append(m.getString("TIME_STOP")).append("'");
			sb.append(" )");
		}
		
		// custom insert/update date in sage needs to be corrected
		m.add("COMMAND", sb.toString());
		
		return m;
	}
	
	public static MapSet getRefLibraryGroup(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		//m.add("GROUP_NAME", map.getString("GROUP_NAME"));
		//m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		//m.add("table",Table.REFLIBRARYGROUPTABLE);
		
		Sage db = new Sage();
		
		String command = "SELECT TOP 1 ID from LIBRARY WHERE LIBRARY_GROUP_ID= "+map.getInt("LIBRARY_GROUP_ID")+" ORDER BY ID DESC ";
		Logger.info(command);
		db.query(command);
		
		int libraryId =0;
		if (db.next()){
			libraryId=db.getInt("ID");
		}
		db.clear();

		m.add("LIBRARY_ID",libraryId);
		m.add("LIBRARY_GROUP_ID", map.getInt("LIBRARY_GROUP_ID"));
		
		return m;
	}
	
	
	public static MapSet getLibraryRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table",Table.REFPROJECTLIBRARYGROUPTABLE);
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table",Table.REFACTLIBRARYGROUPTABLE);
				m.add("column","LKUP_ACT_TYPE_ID");
				
			}
			
		
			
			if(connecttype.equalsIgnoreCase("lso")){
				m.add("table",Table.REFLSOLIBRARYGROUPTABLE);
				m.add("column","LKUP_LSO_TYPE_ID");
			
			}
			
			Logger.info(connecttype+"&&&&SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS&&&&&&");   
			if(connecttype.equalsIgnoreCase("library")){
				m.add("table",Table.REFLIBRARYGROUPTABLE);
				m.add("column","LIBRARY_ID");
			
			}  
			
			m.add("LIBRARY_GROUP_ID", map.getInt("LIBRARY_GROUP_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
			
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.GROUP_NAME,G.DESCRIPTION FROM ").append(m.getString("table")).append(" R JOIN LIBRARY_GROUP G on R.LIBRARY_GROUP_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LIBRARY_GROUP_ID =").append(m.getString("LIBRARY_GROUP_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LIBRARY_GROUP_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	public static MapSet insertLibraryGroupRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN","LIBRARY_GROUP_ID");
		m.add("LIBRARY_ID", map.getString("LIBRARY_ID"));
		m.add("ORDR", map.getString("ORDR"));
		//m.add("REQ", map.getString("REQ"));
		return m;
	}
	
	
	public static String getLibraryFields(int groupid){
		StringBuilder sb = new StringBuilder();
		String table = Table.LIBRARYTABLE;
		//sb.append("select  RLG.ID as REF_LIBRARY_GROUP_ID ,F.*  ");
		sb.append("select F.ID, F.NAME, F.LEVEL_TYPE, F.LIBRARY_GROUP_ID, F.LIBRARY_TYPE_ID, F.TITLE, F.TXT, F.LIBRARY_CODE, F.LIBRARY_DESC, F.INSPECTABLE, F.WARNING, F.COMPLETE, F.REQUIRED, F.ORDR, F.ACTIVE, F.CREATED_BY, F.CREATED_DATE, F.UPDATED_BY, F.UPDATED_DATE, F.CREATED_IP, F.UPDATED_IP  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(",");
		sb.append(" COUNT(LIN.ID) as LINN_ID,COUNT(LOUT.ID) as LOUT_ID");
		
		sb.append(" from REF_LIBRARY_GROUP RLG  ");
		sb.append(" join " ).append(table).append(" F on RLG.LIBRARY_ID= F.ID   ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		
		sb.append("	  LEFT OUTER JOIN REF_LIBRARY_IN_DIVISIONS LIN on RLG.LIBRARY_ID = LIN.LIBRARY_ID and LIN.ACTIVE='Y' ");// AND   LIN.LIBRARY_GROUP_ID=").append(groupid).append(" "); 
		sb.append("	  LEFT OUTER JOIN REF_LIBRARY_OUT_DIVISIONS LOUT on RLG.LIBRARY_ID = LOUT.LIBRARY_ID and LOUT.ACTIVE='Y' ");// AND   LOUT.LIBRARY_GROUP_ID=").append(groupid).append(" "); 
		
		
		sb.append("	WHERE F.ACTIVE ='Y' AND RLG.ACTIVE='Y' AND RLG.LIBRARY_GROUP_ID=").append(groupid);
		sb.append(" group by F.ID, F.NAME, F.LEVEL_TYPE, F.LIBRARY_GROUP_ID, F.LIBRARY_TYPE_ID, F.TITLE, F.TXT, F.LIBRARY_CODE, F.LIBRARY_DESC, F.INSPECTABLE, F.WARNING, F.COMPLETE, F.REQUIRED, F.ORDR, F.ACTIVE, F.CREATED_BY, F.CREATED_DATE, F.UPDATED_BY, F.UPDATED_DATE, F.CREATED_IP, F.UPDATED_IP,F.CREATED_DATE,F.UPDATED_DATE,CU.USERNAME, UP.USERNAME,LIN.LIBRARY_ID,LOUT.LIBRARY_ID ");
		sb.append(" order by ordr ");
		return sb.toString();
	}
	
	
	
	public static MapSet getUserRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			/*if(connecttype.equalsIgnoreCase("project")){
				m.add("table",Table.REFPROJECTLIBRARYGROUPTABLE);
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table",Table.REFACTLIBRARYGROUPTABLE);
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}*/
			
		
		
			if(connecttype.equalsIgnoreCase("user")){
				m.add("table",Table.REFUSERROLESTABLE);
				m.add("column","USERS_ID");
				
			}
			
			m.add("LKUP_ROLES_ID", map.getInt("LKUP_ROLES_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		//m.add("addorder", "Y");
		
		
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.ROLE,U.USERNAME FROM ").append(m.getString("table")).append(" R JOIN LKUP_ROLES G on R.LKUP_ROLES_ID=G.ID JOIN USERS U on R.USERS_ID=U.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ROLE");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" SELECT *  from  ").append(m.getString("table")).append(" WHERE LKUP_ROLES_ID =").append(m.getString("LKUP_ROLES_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		/*
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LIBRARY_GROUP_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	*/
		
		
		
		return m;
		
	}
	
	
	public static String getFormulaHistory(int id){
		StringBuilder sb = new StringBuilder();
		String table = Table.LKUPFORMULATABLE;
		sb.append("select F.*  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from ").append(table).append(" F  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE F.ORIGINAL_ID =").append(id).append(" order by UPDATED_DATE ");
		return sb.toString();
	}
	
	
	public static MapSet getLibraryGroupType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("GROUP_NAME", map.getString("GROUP_NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		return m;
	}
	
	public static MapSet getLibraryType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TITLE", map.getString("TITLE"));
		m.add("TXT", map.getString("TXT"));
		
		m.add("LIBRARY_CODE", map.getString("LIBRARY_CODE"));
		m.add("LIBRARY_DESC", map.getString("LIBRARY_DESC","O"));
		m.add("LIBRARY_GROUP_ID", map.getString("LIBRARY_GROUP_ID"));
		
		
		String d = map.getString("REQUIRED","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("REQUIRED", d);
		String o = map.getString("INSPECTABLE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("INSPECTABLE", o);
		
		o = map.getString("WARNING","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("WARNING", o);
		
		o = map.getString("COMPLETE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("COMPLETE", o);
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(Table.LIBRARYTABLE).append(" WHERE LIBRARY_GROUP_ID = ").append(m.getString("LIBRARY_GROUP_ID"));
		m.add("orderquery", order.toString());
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			StringBuilder w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(Table.LIBRARYTABLE).append(" SET ORDR =  ").append(i).append(" WHERE LIBRARY_GROUP_ID = ").append(m.getString("LIBRARY_GROUP_ID")).append(" AND ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		return m;
	}
	
	
	public static MapSet getResolutionStatus(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("STATUS", map.getString("STATUS"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		
		String o = map.getString("APPROVED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("APPROVED", o);
		
		
		o = map.getString("FINAL","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("FINAL", o);
		
		
		
		
		return m;
	}
	
	
	public static MapSet getUser(int id){
		MapSet m = UsersSQL.getUser(id);
		return m;
	}
	
	public static MapSet getLsoLst(Cartographer map,String table,int start, int end,String sortfield,String sortorder,String query, String searchField){
		MapSet m = AdminSQL.getLsoLst(map,table,start,end,sortfield,sortorder,query,searchField);
		return m;
	}
	
	public static MapSet getUserType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("REQUIRED_LICENSE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("REQUIRED_LICENSE", o);
		
		o = map.getString("APPLICANT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("APPLICANT", o);
		
		String d = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("ISPUBLIC", d);
				
		m.add("VALIDATE_LICENSE_URL", map.getString("VALIDATE_LICENSE_URL"));
		return m;
	}
	
	public static MapSet getDivisionsGroup(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("GROUP_NAME", map.getString("GROUP_NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("ISDOT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISDOT", o);
		
		o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);
		
		
		return m;
	}
	
	
	public static MapSet getHoldsType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		String o = map.getString("ISPUBLIC","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ISPUBLIC", o);
		
		String d = map.getString("SIGNIFICANT","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("SIGNIFICANT", d);
		
		return m;
	}
	
	public static MapSet getPattern(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("PATTERN", map.getString("PATTERN"));
		
		String o = map.getString("PATTERN_YEAR","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PATTERN_YEAR", o);
		
		
		o = map.getString("PATTERN_MONTH","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PATTERN_MONTH", o);
		
		o = map.getString("PATTERN_DAY","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PATTERN_DAY", o);
		
		return m;
	}
	
	
	
	public static MapSet getLicenseType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("LKUP_PEOPLE_LICENSE_TYPE_ID", map.getString("LKUP_PEOPLE_LICENSE_TYPE_ID"));
		m.add("LIC_NUM", map.getString("LIC_NUM"));
		m.add("LIC_EXPIRATION_DATE", map.getString("LIC_EXPIRATION_DATE"));
		m.add("PEOPLE_ID", map.getString("PEOPLE_ID"));
		
		return m;
	}
	
	public static MapSet insertUserGroupUserRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN","USERS_GROUP_ID");
		m.add("USERS_ID", map.getString("USERS_ID"));
		
		return m;
	}
	
	public static MapSet insertUserTeamRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN","LKUP_TEAM_TYPE_ID");
		m.add("USERS_ID", map.getString("USERS_ID"));
		
		return m;
	}
	
	public static MapSet insertUserRolesRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN","LKUP_ROLES_ID");
		m.add("USERS_ID", map.getString("USERS_ID"));
		
		return m;
	}
	
	public static MapSet getRefUsers(Cartographer map){
		MapSet m = getCommon(map);
		m.add("_table",Table.REFUSERSTABLE);
		m.add("ID", map.getInt("ID"));
		m.add("LKUP_USERS_TYPE_ID", map.getString("LKUP_USERS_TYPE_ID"));
		m.add("USERS_ID", map.getString("USERS_ID"));
		
		StringBuilder sb = new StringBuilder();
		sb.append("select * ");
		
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),U.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),U.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  from ").append(Table.REFUSERSTABLE).append(" U JOIN ").append(Table.LKUPUSERTYPETABLE).append("  L on U.LKUP_USERS_TYPE_ID = L.ID  "); 
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on U.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on U.UPDATED_BY = UP.ID "); 
		
		sb.append(" where U.ACTIVE='Y' AND U.LKUP_USERS_TYPE_ID=").append(map.getString("LKUP_USERS_TYPE_ID")).append(" AND U.USERS_ID=").append(map.getString("USERS_ID"));
		m.add("initialquery",sb.toString());
		
		return m;
	}
	
	
	public static MapSet getTeamType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		return m;   
	}   
	
	public static MapSet getRole(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("ROLE", map.getString("ROLE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		String o = map.getString("ADMIN","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("ADMIN", o);
		
		o = map.getString("EVERYONE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("EVERYONE", o);
		
		o = map.getString("STAFF","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("STAFF", o);   
		
		o = map.getString("PEOPLE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PEOPLE", o);   

		
		return m;
	}
	
	
	public static MapSet getProjectStatusRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_TYPE_STATUS");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table","REF_ACT_TYPE_STATUS");
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			m.add("LKUP_PROJECT_STATUS_ID", map.getInt("LKUP_PROJECT_STATUS_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		String o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);
		
		
	/*	o = map.getString("DEFLT_ISSUED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT_ISSUED", o);*/
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.STATUS as NAME FROM ").append(m.getString("table")).append(" R JOIN LKUP_PROJECT_STATUS G on R.LKUP_PROJECT_STATUS_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_PROJECT_STATUS_ID =").append(m.getString("LKUP_PROJECT_STATUS_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_PROJECT_STATUS_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	public static MapSet getActivityStatusRef(Cartographer map){
		MapSet m = getCommon(map);
		String connecttype= map.getString("connecttype");
		String connectIds = map.getString("connectids");
		/*if(map.getInt("REF_ID")>0){
			m.add("REF_ID", map.getInt("REF_ID"));
		}*/
		if(Operator.hasValue(connectIds)){
			if(connecttype.equalsIgnoreCase("project")){
				m.add("table","REF_PROJECT_TYPE_STATUS");
				m.add("column","LKUP_PROJECT_TYPE_ID");
			}
			if(connecttype.equalsIgnoreCase("activity")){
				m.add("table","REF_ACT_TYPE_STATUS");
				m.add("column","LKUP_ACT_TYPE_ID");
			
			}
			
			m.add("LKUP_ACT_STATUS_ID", map.getInt("LKUP_ACT_STATUS_ID"));
			m.add("connectids", map.getString("connectids"));
			
		}
		
		m.add("addorder", "Y");
		
		String o = map.getString("DEFLT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT", o);
		
		
		o = map.getString("DEFLT_ISSUED","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("DEFLT_ISSUED", o);
		
		
		StringBuilder s = new StringBuilder();
		String c[] = Operator.split(connectIds,",");
		Logger.info(c.length+"MAPPPPPPPPPPPPPPPPPPPPPP");
		if(c.length==1){
			s.append(" SELECT R.*,G.STATUS as NAME FROM ").append(m.getString("table")).append(" R JOIN LKUP_ACT_STATUS G on R.LKUP_ACT_STATUS_ID=G.ID WHERE R.ACTIVE='Y' AND R.").append(m.getString("column")).append("=").append(c[0]).append(" ORDER BY ORDR");
			m.add("sublistquery", s.toString());
		}
		
		Logger.info(map.getString("action"));
		
		//if(Operator.equalsIgnoreCase(map.getString("_action"), "deleteref")){
			StringBuilder w = new StringBuilder();
			w.append(" select * from  ").append(m.getString("table")).append(" WHERE LKUP_ACT_STATUS_ID =").append(m.getString("LKUP_ACT_STATUS_ID"));
			w.append(" AND ");
			w.append(m.getString("column")).append(" IN (").append(connectIds).append(")");
			
			m.add("deleteselection",w.toString());
		//}	
		
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortref")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(m.getString("table")).append(" SET ORDR =  ").append(i).append(" WHERE ").append(m.getString("column")).append(" = ").append(m.getString("connectids")).append(" AND LKUP_ACT_STATUS_ID =").append(sorts[i]).append("");
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		
		return m;
		
	}
	
	
	
	/*public static String forumlaExpire(String startDate,int forumlaId, int newFormulaId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" insert into REF_FEE_FORMULA (FEE_ID,START_DATE,LKUP_FORMULA_ID,EXPIRATION_DATE,INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, ");
		sb.append(" INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5,INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" ACTIVE,FINANCE_MAP_ID,INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5, ");
		sb.append(" INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5,MANUAL_ACCOUNT ");
		sb.append(" ,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CREATED_IP,UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" select FEE_ID,START_DATE,").append(newFormulaId).append(",'").append(Operator.sqlEscape(startDate)).append("',INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, ");
		sb.append(" INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5, ");
		sb.append(" INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" ACTIVE,FINANCE_MAP_ID,INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5, ");
		sb.append(" INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5,MANUAL_ACCOUNT  ");
		sb.append(" ,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CREATED_IP,UPDATED_IP ");
		sb.append(" from REF_FEE_FORMULA where LKUP_FORMULA_ID =").append(forumlaId).append(" and EXPIRATION_DATE is null and START_DATE < '").append(Operator.sqlEscape(startDate)).append("' ");
		return sb.toString();
	}*/
	
	
	public static String forumlaCreateSet(int newFormulaId,MapSet u){
		StringBuilder sb = new StringBuilder();
		
		String startDate = u.getString("NEW_START_DATE");
		String expdate = u.getString("EXPIRATION_DATE");
		int formulaId = u.getInt("ID");
		
		
		sb.append(" insert into REF_FEE_FORMULA (FEE_ID,LKUP_FORMULA_ID,START_DATE,INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, ");
		sb.append(" INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5,INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" ACTIVE,FINANCE_MAP_ID,INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5, ");
		sb.append(" INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5,MANUAL_ACCOUNT ");
		sb.append(" ,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CREATED_IP,UPDATED_IP ");
		sb.append(" ) ");
		sb.append(" select FEE_ID,").append(newFormulaId).append(",'").append(Operator.sqlEscape(startDate)).append("',INPUT1,INPUT2,INPUT3,INPUT4,INPUT5, ");
		sb.append(" INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5, ");
		sb.append(" INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" ACTIVE,FINANCE_MAP_ID,INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5, ");
		sb.append(" INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5,MANUAL_ACCOUNT  ");
		sb.append(" ,").append(u.getString("CREATED_BY")).append(",CURRENT_TIMESTAMP,").append(u.getString("CREATED_BY")).append(",CURRENT_TIMESTAMP,'").append(Operator.sqlEscape(u.getString("CREATED_IP"))).append("','").append(Operator.sqlEscape(u.getString("UPDATED_IP"))).append("' ");
		sb.append(" from REF_FEE_FORMULA where LKUP_FORMULA_ID =").append(formulaId).append(" and EXPIRATION_DATE ='").append(Operator.sqlEscape(expdate)).append("' ");
		return sb.toString();
	}
	
	public static String forumlaExpire(String expDate,int forumlaId, int updatedBy){
		StringBuilder sb = new StringBuilder();
		sb.append(" update REF_FEE_FORMULA set EXPIRATION_DATE = '").append(Operator.sqlEscape(expDate)).append("' ,UPDATED_BY = ").append(updatedBy).append(", UPDATED_DATE = CURRENT_TIMESTAMP where ID in ( ");
		sb.append(" select ID from  REF_FEE_FORMULA where LKUP_FORMULA_ID =").append(forumlaId).append(" and EXPIRATION_DATE is null and START_DATE < getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}
	
	public static String formulaUpdateForStart(int forumlaId, int newFormulaId, int updatedBy){
		StringBuilder sb = new StringBuilder();
		sb.append(" update REF_FEE_FORMULA set LKUP_FORMULA_ID=").append(newFormulaId).append(", UPDATED_BY = ").append(updatedBy).append(", UPDATED_DATE = CURRENT_TIMESTAMP where ID in ( ");
		sb.append(" select ID from  REF_FEE_FORMULA where LKUP_FORMULA_ID =").append(forumlaId).append(" and EXPIRATION_DATE is null and START_DATE > getDate() ");
		sb.append(" ) ");
		return sb.toString();
	}
	
	
	public static String getHolidays(int typeId){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.HOLIDAY_DATE,101) as H_HOLIDAY_DATE ");
		
		sb.append(" from HOLIDAY A  ");
		
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE A.ACTIVE='Y' AND A.LKUP_HOLIDAY_TYPE_ID= ").append(typeId);
		sb.append("  ORDER BY HOLIDAY_DATE  ");
		
	
		//WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		
	
		return sb.toString();
	
	}

	public static MapSet getHoliday(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("HOLIDAY", map.getString("HOLIDAY"));
		m.add("HOLIDAY_DATE", map.getString("HOLIDAY_DATE"));
		m.add("MESSAGE", map.getString("MESSAGE"));
		m.add("LKUP_HOLIDAY_TYPE_ID", map.getString("LKUP_HOLIDAY_TYPE_ID"));
		
	
		return m;
	}
	
	
	public static MapSet getCopyFeeName(Cartographer map,String name,String description){
		MapSet m = getCommon(map);
		m.add("NAME", name);
		m.add("DESCRIPTION", description);
		
		m.add("START_DATE", map.getString("START_DATE"));
		m.add("FINANCE_MAP_ID", map.getString("FINANCE_MAP_ID"));
		String o = map.getString("MANUAL_ACCOUNT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("MANUAL_ACCOUNT", o);
		m.add("REF_FEE_ID", map.getString("REF_FEE_ID"));
		return m;
	}
	
	
	public static MapSet insertRolesRef(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("MULTI_IDS", map.getString("MULTI_IDS"));
		m.add("MULTI_IDS_COLUMN", map.getString("_typeid"));
		m.add("LKUP_ROLES_ID", map.getString("id"));
		m.add("C", map.getString("c"));
		m.add("R", map.getString("r"));
		m.add("U", map.getString("u"));
		m.add("D", map.getString("d"));
		//m.add("ACTIVE", 'Y');
		m.add("reftable", map.getString("reftable"));
		return m;
	}

	public static MapSet getPaymentMethod(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("METHOD_TYPE", map.getString("METHOD_TYPE"));
		
		String o = map.getString("RECORD_FINANCIALS","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("RECORD_FINANCIALS", o);
		
		o = map.getString("PAYMENT_MODE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("PAYMENT_MODE", o);
		
		o = map.getString("REVERSE_MODE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("REVERSE_MODE", o);
		
		
		o = map.getString("APPLY_DEPOSIT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("APPLY_DEPOSIT", o);
		
		o = map.getString("CASH_FLAG","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("CASH_FLAG", o);
		
		
		
		return m;
	}
	
	public static MapSet getPaymentTransaction(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		String o = map.getString("RECORD_FINANCIALS","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("RECORD_FINANCIALS", o);
		
		
		
		
		return m;
	}
	
	
	public static MapSet getAppointmentType(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("AVAILABILITY_ID", map.getInt("AVAILABILITY_ID"));
		
	
		return m;
	}
	
	public static MapSet getModuleContent(Cartographer map){
		MapSet m = getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("NAME", map.getString("NAME"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		m.add("TYPE", map.getString("TYPE"));
		m.add("CONTENT", map.getString("CONTENT"));
		
		String d = map.getString("STAFF","N");
		if(Operator.equalsIgnoreCase(d, "on")){	d = "Y";}
		m.add("STAFF", d);
		
		
	
		return m;
	}
	
	
	public static MapSet getTaskOptions(Cartographer map,String table){
		MapSet m = AdminMap.getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TASK_ID", map.getString("TASK_ID"));
		m.add("TASK", map.getString("TASK"));
		m.add("LKUP_ACT_TYPE_ID", map.getString("LKUP_ACT_TYPE_ID"));
		m.add("DESCRIPTION", map.getString("DESCRIPTION"));
		
		
		m.add("addorder", "Y");
		StringBuilder order = new StringBuilder();
		order.append(" select  IsNull(MAX(ORDR),0) +1 as ORDR from  ").append(table).append(" WHERE LKUP_ACT_TYPE_ID = ").append(m.getString("LKUP_ACT_TYPE_ID"));
		m.add("orderquery", order.toString());
		
		/*StringBuilder sb = new StringBuilder();
		sb.append(AdminSQL.getList(table, 0, 1000, "", "ORDR", "", "","","","",""));
		m.add("sublistquery", sb.toString());*/
		
		StringBuilder w = new StringBuilder();
		if(Operator.equalsIgnoreCase(map.getString("_action"), "sortfields")){
			 w = new StringBuilder();
			String sorts[] = Operator.split(map.getString("sortOrder"),",");
			for(int i=0;i<sorts.length;i++){
				w.append(" UPDATE ").append(table).append(" SET ORDR =  ").append(i).append(" WHERE  LKUP_ACT_TYPE_ID =  "+ map.getInt("ID") +" AND ID = ").append(sorts[i]);
				w.append("|");
			}
			m.add("sortselection",w.toString());
		}	
		
		
		return m;
	}  
	
	public static String getTaskFields(String table,int id){
		StringBuilder sb = new StringBuilder();
		//String table = Table.FIELDTABLE;
		sb.append("select  F.ID,F.TASK,F.TASK_ID,F.DESCRIPTION,F.REPEAT  ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),F.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),F.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		//sb.append(",");	
		//sb.append("  RUN_ONCE,IMMEDIATE,RUN_CONTINUOUS ");
		sb.append(" from " ).append(table).append(" F ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on F.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on F.UPDATED_BY = UP.ID "); 
		sb.append("	WHERE LKUP_ACT_TYPE_ID=").append(id).append(" AND F.ACTIVE='Y' order by F.ordr ");
		return sb.toString();
	}
	
	
	/*public static String getType(ArrayList<MapSet> l,MapSet cols,boolean check, boolean edit, boolean delete){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" <table class=\"csui tablesorter\" type=\"horizontal\"> ");
		sb.append(" <thead> " );
		sb.append(" <tr> ");
		if(check){
			sb.append(" 	<td class=\"csui_header\"><input type=\"checkbox\" name=\"selectorall\" id=\"selectorall\" ></td> ");
		}
		while(cols.next()){
			sb.append(" 	<td class=\"csui_header\">").append(cols.FIELD).append("</td> ");
		}
		
		
		if(edit){
			sb.append(" 	<td class=\"csui_header\" widtd=\"1%\">&nbsp;</td> ");
		}
		if(delete){
			sb.append(" 	<td class=\"csui_header\" widtd=\"1%\">&nbsp;</td> ");
		}
		sb.append("  ");
		sb.append(" </tr> ");
		sb.append(" </thead>");
		
		
		sb.append(" <tbody> ");
		cols.remove("ID");
		 for(int i=0;i<l.size();i++){ 
		 MapSet r = l.get(i); 
		
		 
		 while(cols.next()){
			 
			 
			 sb.append(" 	<tr id=\"tr_").append(r.getString("ID")).append("\"> ");
			 if(check){
				 sb.append(" 		<td class=\"csui\"><input type=\"checkbox\" name=\"selector\" id=\"selector\" class=\"selector\" value=\"").append(r.getString("ID")).append("\"> </td> ");
			 }
			 
			 sb.append(" 		<td class=\"csui\">").append(r.getString(cols.FIELD)).append("</td> ");
			
			 if(edit){
				 sb.append(" 		<td class=\"csui\" width=\"1%\"> ");
				 sb.append(" 		<a class=\"lightbox-iframe\" href=\"add.jsp?_ent=<%=ent%>&_id=<%=id%>&_type=<%=type%>&_typeid=<%=typeid%>&id=<%=r.getString(\"ID\") %>&process=edit\" ><img src=\"/cs/images/icons/controls/black/edit.png\" border=\"0\"></a> ");
				 sb.append(" 		</td> ");
			 }
			 if(delete){
				 sb.append(" 		<td class=\"csui\" width=\"1%\"> ");
				 sb.append(" 		<a href=\"javascript:void(0);\" target=\"\" onclick=\"deletetype(<%=r.getString(\"ID\") %>);\" ><img src=\"/cs/images/icons/controls/black/delete.png\" border=\"0\"></a> ");
				 sb.append(" 		</td> ");
			 }
			 sb.append(" 	</tr> ");
		 }
		 } 
		 sb.append(" </tbody>");
		
		return sb.toString();
	}*/

}
