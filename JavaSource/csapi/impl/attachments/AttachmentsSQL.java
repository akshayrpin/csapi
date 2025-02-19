package csapi.impl.attachments;

import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;

public class AttachmentsSQL {

	public static String info(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String summary(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String ext(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String list(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String details(String type, int typeid, int id) {
		return details(type, typeid, id, new Token());
	}

	public static String details(String type, int typeid, int id, Token u) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( SELECT COUNT(DISTINCT ID) AS CONTENT_COUNT FROM CONTENT WHERE ACTIVE = 'Y' AND LOWER(TYPE) = 'attachments') ");
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		//sb.append("'").append(Config.getString("files.storage_url")).append("'+ A.PATH as FILEURL ");
		sb.append("'").append("/cs").append("'+'/viewfile.jsp?_id='+ CAST(A.ID as varchar(10)) as FILEURL ");

		sb.append(" , ");
		sb.append(" A.KEYWORD1 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD2 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD3 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD4 ");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.SENSITIVE ");
		sb.append(" , ");
		sb.append(" R.ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" T.TYPE AS LKUP_ATTACHMENTS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" R.UPDATED_DATE ");
		sb.append(" , ");
		sb.append("   CASE ");
		sb.append("     WHEN Q.CONTENT_COUNT IS NULL THEN '' ");
		sb.append("     WHEN Q.CONTENT_COUNT > 0 THEN 'attachments' ");
		sb.append("   ELSE '' END AS CONTENT_TYPE ");
		sb.append(" FROM ");
		sb.append(" REF_").append(tableref).append("_ATTACHMENTS R ");
		sb.append(" JOIN ATTACHMENTS AS A ON R.ATTACHMENT_ID = A.ID AND A.ACTIVE = 'Y' AND R.ACTIVE = 'Y' ");
		
		sb.append(" LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID ");
		sb.append(" LEFT OUTER JOIN Q ON 1 = 1 ");
		sb.append(" WHERE R.ACTIVE = 'Y'  ");
		sb.append(" AND  ");
		sb.append(" A.ACTIVE = 'Y' ");
		sb.append(" AND  ");
		sb.append(" R.").append(idref).append(" = ").append(typeid).append(" ");
		if (id > 0) {
			sb.append(" AND ");
			sb.append(" R.ATTACHMENT_ID = ").append(id);
		}
		if (!u.isStaff()) {
			sb.append(" AND A.ISPUBLIC = 'Y' AND T.ISPUBLIC = 'Y' AND A.SENSITIVE = 'N'");
		}
		sb.append(" ORDER BY A.CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String id(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		//sb.append("'").append(CsApiConfig.getString("cs.fullcontexturl")).append("/viewfile.jsp?'+ A.ID as FILEURL ");
		sb.append("'").append("/cs").append("'+ A.PATH as FILEURL ");
		sb.append(" , ");
		sb.append(" A.KEYWORD1 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD2 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD3 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD4 ");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.ID AS ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" T.TYPE AS LKUP_ATTACHMENTS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" ATTACHMENTS AS A ");
		sb.append(" LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(id);
		return sb.toString();
	}
	
	
	
	public static String updateAttachments(RequestVO r, Token u) {
		return GeneralSQL.updateCommon(r,u);
	}
	
	
	public static String insertAttachments(RequestVO r, Token u) {
		return GeneralSQL.insertCommon(r, u);
	}
	
	
	public static String getRefIdAttachments(RequestVO r, Token u) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select TOP 1 ID FROM ").append(Table.ATTACHMENTSTABLE).append(" ");
		sb.append(" WHERE ID >0  ");
		sb.append(" AND ");
		/*sb.append(" PATH = '").append(Operator.sqlEscape(obj[2].getValue())).append("' ");
		sb.append(" AND ");*/
		sb.append(" CREATED_BY = ").append(u.getId());
		sb.append(" order by CREATED_DATE DESC ");
		return sb.toString();
	}
	
	public static String insertRefAttachments(RequestVO r, Token u, int id) {
		return GeneralSQL.insertRefCommon(r,u,id);
	}
	
	
	
	/**
	 * Alain - modified by sunil 2/3/18
	 */
	public static String insertAttachments(String title,int mapid,String type,String subtype, String path, int attachtypeid, String description, int size, boolean ispublic, boolean sensitive, int userid, String ip, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		
		
		//String fieldId = CsReflect.getFieldIdRef(type);
		
		sb.append(" INSERT INTO ATTACHMENTS ( ");
		sb.append(" TITLE, LKUP_ATTACHMENTS_TYPE_ID, PATH, DESCRIPTION, SIZE, ISPUBLIC, SENSITIVE, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP) OUTPUT Inserted.* VALUES ( ");
	/*	sb.append(mapid);
		sb.append(" , ");		*/
		sb.append(" '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(attachtypeid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(path)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(size);
		sb.append(" , ");
		if (ispublic) {
			sb.append(" 'Y' ");
		}
		else {
			sb.append(" 'N' ");
		}
		sb.append(" , ");
		if (sensitive) {
			sb.append(" 'Y' ");
		}
		else {
			sb.append(" 'N' ");
		}
		sb.append(" , ");
		sb.append(userid);
		sb.append(",");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		/*sb.append(" , ");
		sb.append(now.sqlDatetime());
		sb.append(" , ");
		sb.append(now.sqlDatetime());*/
		sb.append(" ) "); 

		return sb.toString();
	}

	public static String updateAttachment(int id,int mapid,String type,String subtype, String title, String path, int attachtypeid, String description, int size, boolean ispublic, boolean sensitive, int userid, String ip) {
		
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		
		String typeref = CsReflect.getTableRef(type);
		String typeidref = CsReflect.getFieldIdRef(type);
		String styperef = CsReflect.getTableRef(subtype);
		String stypeidref = CsReflect.getFieldIdRef(subtype);
		
		if (id < 1) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ATTACHMENTS SET ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" LKUP_ATTACHMENTS_TYPE_ID = ").append(attachtypeid);
		sb.append(" , ");
		sb.append(" DESCRIPTION = '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" SIZE = ").append(size);
		sb.append(" , ");
		if (ispublic) {
			sb.append(" ISPUBLIC = 'Y' ");
		}
		else {
			sb.append(" ISPUBLIC = 'N' ");
		}
		sb.append(" , ");
		if (sensitive) {
			sb.append(" SENSITIVE = 'Y' ");
		}
		else {
			sb.append(" SENSITIVE = 'N' ");
		}
		if (Operator.hasValue(path)) {
			sb.append(" PATH = '").append(Operator.sqlEscape(path)).append("' ");
		}
		sb.append(" , ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(ip).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		
		sb.append(" ; ");
		
		sb.append(" UPDATE REF_").append(tableref).append("_ATTACHMENTS SET ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" , ");
		sb.append(" UPDATED_IP = '").append(ip).append("' ");
		sb.append(" , ");
		sb.append(" UPDATED_DATE = getDate() ");
		sb.append(" WHERE ");
		sb.append(" ATTACHMENT_ID = ").append(id);
		
		return sb.toString();
	}

	public static String getAttachments(String title, String path, int userid, Timekeeper now) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM ATTACHMENTS WHERE ");
		sb.append(" LOWER(TITLE)  = LOWER('").append(Operator.sqlEscape(title)).append("') ");
		sb.append(" AND ");
		sb.append(" LOWER(PATH)  = LOWER('").append(Operator.sqlEscape(path)).append("') ");
		sb.append(" AND ");
		sb.append(" UPDATED_BY ").append(userid);
		sb.append(" AND ");    
		sb.append(" UPDATED_DATE = ").append(now.sqlDatetime());
		return sb.toString();
	}

	public static String getTypes() {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.ID ");
		sb.append(" , ");
		sb.append(" T.ID AS VALUE ");
		sb.append(" , ");
		sb.append(" T.TYPE AS TEXT ");
		sb.append(" , ");
		sb.append(" T.DESCRIPTION ");
		sb.append(" FROM ");
		sb.append(" LKUP_ATTACHMENTS_TYPE AS T  ");
		sb.append(" WHERE ");
		sb.append(" T.ACTIVE = 'Y' ");
		return sb.toString();
	}

	public static String addAttachment(String title, String description, String path, int attachtypeid, int size, boolean publc, int userid, String ip, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO ATTACHMENTS ( ");
		sb.append(" TITLE, DESCRIPTION, PATH, LKUP_ATTACHMENTS_TYPE_ID, SIZE, ISPUBLIC, CREATED_BY, CREATED_IP, CREATED_DATE, UPDATED_BY, UPDATED_IP, UPDATED_DATE ");
		sb.append(" ) VALUES ( ");
		sb.append(" '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(path)).append("' ");
		sb.append(" , ");
		sb.append(attachtypeid);
		sb.append(" , ");
		sb.append(size);
		sb.append(" , ");
		if (publc) {
			sb.append(" 'Y' ");
		}
		else {
			sb.append(" 'N' ");
		}
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" , ");
		sb.append(userid);
		sb.append(" , ");
		sb.append(" '").append(Operator.sqlEscape(ip)).append("' ");
		sb.append(" , ");
		sb.append(createddate.sqlDatetime());
		sb.append(" ) ");
		return sb.toString();
	}

	public static String getAttachment(String title, String path, int userid, Timekeeper createddate) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(" ATTACHMENTS ");
		sb.append(" WHERE ");
		sb.append(" TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" AND ");
		sb.append(" PATH = '").append(Operator.sqlEscape(path)).append("' ");
		sb.append(" AND ");
		sb.append(" UPDATED_BY = ").append(userid);
		sb.append(" AND ");
		sb.append(" UPDATED_DATE = ").append(createddate.sqlDatetime());
		return sb.toString();
	}

	public static String getAttachmentById(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" A.ID ");
		sb.append(" , ");
		sb.append(" A.TITLE ");
		sb.append(" , ");
		sb.append(" A.CREATED_DATE ");
		sb.append(" , ");
		sb.append(" A.DESCRIPTION ");
		sb.append(" , ");
		sb.append(" A.LOCATION ");
		sb.append(" , ");
		sb.append(" A.PATH ");
		sb.append(" , ");
		//sb.append("'").append(CsApiConfig.getString("cs.fullcontexturl")).append("/viewfile.jsp?'+ A.ID as FILEURL ");
		sb.append("'").append("/cs").append("'+'/viewfile.jsp?_id='+ CAST(A.ID as varchar(10)) as FILEURL ");
		sb.append(" , ");
		sb.append(" A.KEYWORD1 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD2 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD3 ");
		sb.append(" , ");
		sb.append(" A.KEYWORD4 ");
		sb.append(" , ");
		sb.append(" A.ISPUBLIC ");
		sb.append(" , ");
		sb.append(" A.ID AS ATTACHMENT_ID ");
		sb.append(" , ");
		sb.append(" T.TYPE ");
		sb.append(" , ");
		sb.append(" A.LKUP_ATTACHMENTS_TYPE_ID");
		sb.append(" , ");
		sb.append(" T.TYPE AS LKUP_ATTACHMENTS_TYPE_TEXT");
		sb.append(" , ");
		sb.append(" CU.USERNAME AS CREATED ");
		sb.append(" , ");
		sb.append(" UP.USERNAME AS UPDATED ");
		sb.append(" , ");
		sb.append(" A.UPDATED_DATE ");
		sb.append(" FROM ");
		sb.append(" ATTACHMENTS AS A ");
		sb.append(" LEFT OUTER JOIN LKUP_ATTACHMENTS_TYPE AS T ON A.LKUP_ATTACHMENTS_TYPE_ID = T.ID ");
		sb.append(" LEFT OUTER JOIN USERS CU ON A.CREATED_BY = CU.ID ");
		sb.append(" LEFT OUTER JOIN USERS UP ON A.UPDATED_BY = UP.ID ");
		sb.append(" WHERE ");
		sb.append(" A.ID = ").append(id);
		return sb.toString();
	}

	public static String getAttachmentforBatch(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" FILENAME as TITLE ");
		sb.append(" , ");
		sb.append(" FILENAME as PATH ");
		sb.append(" FROM BATCH_PRINT ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String getAttachmentforOnline(int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" ID ");
		sb.append(" , ");
		sb.append(" ATTACHMENT as TITLE ");
		sb.append(" , ");
		sb.append(" ATTACHMENT as PATH ");
		sb.append(" FROM REF_ACCOUNT_APPLICATION_ATTACHMENTS ");
		sb.append(" WHERE ");
		sb.append(" ID = ").append(id);
		return sb.toString();
	}

	public static String types(String type, int typeid, int selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" 	T.ID ");
		sb.append(" 	, ");
		sb.append(" 	T.ID AS VALUE ");
		sb.append(" 	, ");
		sb.append(" 	T.TYPE AS TEXT ");
		sb.append(" 	, ");
		sb.append(" 	CASE WHEN LOWER(T.TYPE) = 'other' THEN 0 ELSE 20 END AS ORDR ");
		sb.append(" 	, ");
		sb.append(" 	T.DESCRIPTION ");
		if (selected > 0) {
			sb.append(" , ");
			sb.append(" 	CASE ");
			sb.append(" 		WHEN T.ID = ").append(selected).append(" THEN 'Y' ");
			sb.append(" 	ELSE 'N' END AS SELECTED ");
		}
		sb.append(" FROM ");
		sb.append(" 	LKUP_ATTACHMENTS_TYPE AS T ");
		sb.append(" WHERE ");
		sb.append(" 	T.ACTIVE = 'Y' ");
		sb.append(" ORDER BY ");
		sb.append(" ORDR, TEXT ");
		return sb.toString();
	}
	
	
	public static String documentum(String type, int typeid){
		StringBuilder sb = new StringBuilder();
		if(type.equals("activity")){
			sb.append(" SELECT * FROM ACTIVITY WHERE ID=").append(typeid);
		}else {
			sb.append(" SELECT * FROM V_CENTRAL_ADDRESS WHERE LSO_ID=").append(typeid);
		}
		return sb.toString();
	}

}















