package csapi.impl.address;

import alain.core.utils.Operator;
import csapi.common.Table;

public class AddressSQL {

	
	public static String getLsoType(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" select  S.TYPE ");
		sb.append(" from ").append(Table.LSOTABLE).append(" L ");
		sb.append(" join ").append(Table.LKUPLSOTYPETABLE).append(" S on L.LKUP_LSO_TYPE_ID = S.ID ");
		sb.append(" where  L.ID = ").append(id);
		return sb.toString();
	}
	
	public static String getChildren(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append("select  CAST(L.STR_NO as VARCHAR(25)) +CASE WHEN S.PRE_DIR IS NOT NULL THEN ' '+S.PRE_DIR ELSE '' END+' '+S.STR_NAME+CASE WHEN S.STR_TYPE IS NOT NULL THEN ' '+S.STR_TYPE ELSE '' END+' '+CASE WHEN L.UNIT IS NOT NULL THEN ' '+L.UNIT ELSE '' END as TITLE,L.* ");
		//sb.append(" where ID = ").append(id).append(" OR (PARENT_ID=").append(id).append(" and LKUP_LSO_TYPE_ID=").append(type).append(")");
		
		sb.append(" , stuff((select DISTINCT  ','+convert(varchar(100),SP.STR_NO)+' '+LS.STR_NAME "); 
		sb.append("from ").append(Table.LSOTABLE).append(" SP join ").append(Table.LSOSTREETTABLE).append(" LS on SP.LSO_STREET_ID = LS.ID  ");
		sb.append(" where SP.STR_NO=L.STR_NO and L.LSO_STREET_ID=SP.LSO_STREET_ID  and L.ID= SP.PARENT_ID and LS.ACTIVE='Y' for xml path('')),1,1,'') as CHILDRENS ");
		
		sb.append("  from ").append(Table.LSOTABLE).append(" L ");
		sb.append(" join ").append(Table.LSOSTREETTABLE).append(" S on L.LSO_STREET_ID = S.ID");
		sb.append(" where ( L.ID = ").append(id).append(" OR PARENT_ID=").append(id).append(" )");
		sb.append(" and L.ACTIVE = '").append(Operator.sqlEscape(active)).append("'");
		
		sb.append("  order by L.UNIT,L.STR_NO ");

		return sb.toString();
	}
	
	
	public static String getDetails(int id,String active){
		StringBuilder sb = new StringBuilder();
		sb.append(" select L.ID,L.LKUP_LSO_TYPE_ID,L.UNIT,L.DESCRIPTION, ");
		sb.append(" CAST(L.STR_NO as VARCHAR(25)) +CASE WHEN S.PRE_DIR IS NOT NULL THEN ' '+S.PRE_DIR ELSE '' END+' '+S.STR_NAME+CASE WHEN S.STR_TYPE IS NOT NULL THEN ' '+S.STR_TYPE ELSE '' END as ADDRESS ");
		sb.append(" ,L.CITY,L.STATE,L.ZIP,L.CREATED_DATE,L.UPDATED_DATE,CASE WHEN L.PRIMARY_ID >0 AND PARENT_ID>0 THEN 'N' ELSE 'Y' END as PRIMAR,L.ACTIVE ");
		sb.append(" from ").append(Table.LSOTABLE).append(" L  ");
		sb.append(" join ").append(Table.LKUPLSOTYPETABLE).append(" LT on L.LKUP_LSO_TYPE_ID=LT.ID ");
		sb.append(" join  ").append(Table.LSOSTREETTABLE).append(" S on L.LSO_STREET_ID = S.ID ");
		sb.append(" where L.ID=").append(id).append(" and L.ACTIVE='").append(Operator.sqlEscape(active)).append("' ");

		return sb.toString();
	}
	
	
	public static String getDivisionsDetails(int id){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT LTT.ID,LTT.DESCRIPTION as FIELD,LT.DIVISIONS as FIELD_VALUE ");
		sb.append(" FROM REF_LSO_DIVISIONS R  ");
		sb.append(" JOIN  LKUP_DIVISIONS  LT on R.LKUP_DIVISION_ID = LT.ID  ");
		sb.append(" JOIN  LKUP_DIVISIONS_TYPE  LTT on LT.LKUP_DIVISIONS_TYPE_ID = LTT.ID  ");
		sb.append(" where LSO_ID=").append(id).append(" and LTT.ACTIVE='Y' and  R.ACTIVE='Y' and LT.ACTIVE='Y' ");
	
		return sb.toString();
	}
	
	
	
	/*public static String getSetBack(int id,int typeId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" select TOP 2 ");
		sb.append(" LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD  ");
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID  ");
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append(" LSB on LSD.ID=LSB.LSO_SITEDATA_ID  ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" ");
		sb.append(" group by LSB.START_DATE ");
		sb.append(" order by LSB.START_DATE DESC ");
		
		return sb.toString();
	}*/
	
	
	public static String getSetBack(int id,int typeId){
		StringBuilder sb = new StringBuilder();
		
		sb.append(" WITH Q AS ( ");
		sb.append(" select TOP 1 ");
		sb.append(" LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD  ");
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID  ");
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append(" LSB on LSD.ID=LSB.LSO_SITEDATA_ID  ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" ");
		sb.append(" group by LSB.START_DATE ");
		sb.append(" order by LSB.START_DATE DESC ");
		sb.append("  ");
		sb.append(" 	) ");
		sb.append(" 	select  ");
		sb.append(" LSB.ID,LLST.ID,LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD "); 
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID "); 
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append("  LSB on LSD.ID=LSB.LSO_SITEDATA_ID "); 
		sb.append(" join ").append(Table.LKUPLSOSETBACKTYPETABLE).append("  LLST on LSB.LKUP_LSO_SETBACK_ID=LLST.ID ");
		sb.append(" join Q on Q.START_DATE=LSB.START_DATE ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" and LLST.ACTIVE='Y' ");
		sb.append("  ");
		sb.append(" group by LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE,LSB.ID,LLST.ID ");
		sb.append(" order by LSB.START_DATE DESC,LLST.ID ");

		return sb.toString();

	}
	
	
	/*public static String getSetBack(int id,int typeId,String date){
		StringBuilder sb = new StringBuilder();
		sb.append(" 	select  ");
		sb.append(" LSB.ID,LLST.ID,LLST.TYPE, LSB.FT,LSB.INCHES,LSB.COMMENTS,LSB.REQ_FT,LSB.REQ_INCHES,LSB.REQ_COMMENT,LSB.START_DATE ");
		sb.append(" from  ");
		sb.append(" ").append(Table.LSOSITEDATATABLE).append("  LSD "); 
		sb.append(" join ").append(Table.LSOTABLE).append("  L on LSD.LSO_ID=L.ID "); 
		sb.append(" join ").append(Table.LSOSETBACKTABLE).append("  LSB on LSD.ID=LSB.LSO_SITEDATA_ID "); 
		sb.append(" join ").append(Table.LKUPLSOSETBACKTYPETABLE).append("  LLST on LSB.LKUP_LSO_SETBACK_ID=LLST.ID ");
		sb.append(" where LSO_ID=").append(id).append(" AND LSD.ACTIVE='Y' and LSB.ACTIVE='Y' and L.LKUP_LSO_TYPE_ID=").append(typeId).append(" and LLST.ACTIVE='Y' ");
		sb.append(" AND LSB.START_DATE='").append(date).append("' ");
	
		return sb.toString();

	}*/
	
}
