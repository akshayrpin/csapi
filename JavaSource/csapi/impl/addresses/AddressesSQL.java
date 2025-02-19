package csapi.impl.addresses;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.utils.CsReflect;

public class AddressesSQL {

	public static String info(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	

	public static String ext(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}

	public static String list(String type, int typeid, int id, Token u) {
		return details(type, typeid, -1, u);
	}
	
	public static String details(String type, int typeid, int id, Token u) {
		return summary(type, typeid, -1, u);
	}
	
	public static String summary(String type,int typeid, int id, Token u){
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		
		if(type.equals("project")){
			sb.append(" WITH Q AS ( ");
			sb.append(" 		select DISTINCT L.ID from  ");
			sb.append(" LSO L  ");
			sb.append(" JOIN REF_LSO_PROJECT RLP on L.PRIMARY_ID = RLP.LSO_ID AND RLP.ACTIVE='Y' ");
			sb.append(" WHERE L.ACTIVE='Y'  ");
			sb.append(" AND  RLP.PROJECT_ID =  ").append(typeid);
			sb.append(" ) ");
		}else if(type.equals("activity")){
			sb.append(" WITH Q AS ( ");
			sb.append(" 		select DISTINCT L.ID from  ACTIVITY A ");
			sb.append(" JOIN REF_LSO_PROJECT RLP on A.PROJECT_ID = RLP.PROJECT_ID AND RLP.ACTIVE='Y' ");
			sb.append(" JOIN LSO L on RLP.LSO_ID= L.PRIMARY_ID ");
			sb.append(" WHERE L.ACTIVE='Y'  ");
			sb.append(" AND  A.ID =  ").append(typeid);
			sb.append(" ) ");
		}else {
			sb.append(" WITH Q AS ( ");
			sb.append(" 		select DISTINCT L.ID from  ");
			sb.append(" LSO L  ");
			sb.append(" WHERE L.ACTIVE='Y'  ");
			sb.append(" AND L.PRIMARY_ID =  ").append(typeid);
			sb.append(" ) ");
		}
		
		
		
		
		
		sb.append(" select DISTINCT ADDRESS,APN,LSO_ID, ");
		sb.append(" CASE WHEN LSO_TYPE = 'S' THEN 'STRUCTURE' WHEN LSO_TYPE='O' THEN 'OCCUPANCY' ELSE 'LAND' END AS TYPE ");
		sb.append("  from V_CENTRAL_ADDRESS VA ");
		sb.append(" JOIN Q on VA.LSO_ID= Q.ID ORDER BY ADDRESS ");
		
		
		//sb.append(" ") ");
		return sb.toString();
	}
	
}
