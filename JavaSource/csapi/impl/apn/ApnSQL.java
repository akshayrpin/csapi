package csapi.impl.apn;

import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;

public class ApnSQL {

	public static String info(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String summary(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}

	public static String list(String type, int typeid, int id) {
		return details(type, typeid, -1);
	}
	

	public static String details(String type, int typeid, int id) {
		if (!Operator.hasValue(type)) { return ""; }
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" LA.APN as ID,");
		sb.append(" LA.APN ");
		
		
		sb.append(",");
		sb.append(" FRST_OWNR_NAME,FRST_OWNR_NAME_OVR,MAIL_ADDR_HOUSE_NO,MAIL_ADDR_FRACTION,MAIL_ADDR_DIR,MAIL_ADDR_ST_NAME,MAIL_ADDR_UNIT,MAIL_ADDR_CTY_STA,MAIL_ADDR_ZIP,SPEC_NAME_LEGEND,SPEC_NAME_ASSESSEE,SECND_OWNR_NAME,AO.RECORDING_DATE ");
		sb.append(",");
		sb.append(" AD.HAZ_ABATE_CTY_KEY,AD.HAZ_ABATE_INFO,AD.PARTIAL_INTEREST,AD.DOC_REASON_CODE,AD.DATA1_SUBPART,AD.DATA1_DESIGN_TYPE,AD.DATA1_QUALITY,AD.DATA1_YR_BUILT,AD.DATA1_NO_UNITS,AD.DATA1_NO_BDRMS,AD.DATA1_NO_BATHS,AD.DATA1_SQ_FT,AD.DATA2_SUBPART,AD.DATA2_DESIGN_TYPE,AD.DATA2_QUALITY,AD.DATA2_YR_BUILT,AD.DATA2_NO_UNITS,AD.DATA2_NO_BDRMS,AD.DATA2_NO_BATHS,AD.DATA2_SQ_FT,AD.DATA3_SUBPART,AD.DATA3_DESIGN_TYPE,AD.DATA3_QUALITY,AD.DATA3_YR_BUILT,AD.DATA3_NO_UNITS,AD.DATA3_NO_BDRMS,AD.DATA3_NO_BATHS,AD.DATA3_SQ_FT,AD.DATA4_SUBPART,AD.DATA4_DESIGN_TYPE,AD.DATA4_QUALITY,AD.DATA4_YR_BUILT,AD.DATA4_NO_UNITS,AD.DATA4_NO_BDRMS,AD.DATA4_NO_BATHS,AD.DATA4_SQ_FT,AD.DATA5_SUBPART,AD.DATA5_DESIGN_TYPE,AD.DATA5_QUALITY,AD.DATA5_YR_BUILT,AD.DATA5_NO_UNITS,AD.DATA5_NO_BDRMS,AD.DATA5_NO_BATHS,AD.DATA5_SQ_FT ");
		sb.append(",");
		sb.append(" AST.SITUS_HOUSE_NO,AST.SITUS_FRACTION,AST.SITUS_DIRECTION,AST.SITUS_STREET_NAME,AST.SITUS_UNIT,AST.SITUS_CITY_STATE,AST.SITUS_ZIP,AST.ZONING_CODE,AST.USE_CODE,AST.LEGAL_DESC1,AST.LEGAL_DESC2,AST.LEGAL_DESC3,AST.LEGAL_DESC4,AST.LEGAL_DESC5,AST.LEGAL_DESC6");
		sb.append(",");
		sb.append(" AT.TAX_RATE_AREA,AT.AGENCY_CLASS_NO,AT.LAND_CURR_ROLL_YR,AT.LAND_CURR_VAL,AT.IMP_CURR_ROLL_YR,AT.IMP_CURR_VAL,AT.TAX_STATUS_KEY,AT.TAX_STAT_YR_SOLD,AT.OWNERSHIP_CODE,AT.EXEM_CLAIM_TYPE,AT.PER_PROP_KEY,AT.PER_PROP_VAL,AT.PERL_PROP_EXEM_VAL,AT.FIXTURE_VALUE,AT.FIXTURE_EXEM_VAL,AT.HOMEOWNER_NO_EXEM,AT.HOMEOWNER_EXEM_VAL,AT.REAL_ESTATE_EXEMPT,AT.LAST_SL1_VER_KEY,AT.LAST_SL1_SL_AMNT,AT.LAST_SL1_SL_DT,AT.LAST_SL2_VER_KEY,AT.LAST_SL2_SL_AMNT,AT.LAST_SL2_SL_DT,AT.LAST_SL3_VER_KEY,AT.LAST_SL3_SL_AMNT,AT.LAST_SL3_SL_DT ");
		
		sb.append(",");
		sb.append(" 'https://portal.assessor.lacounty.gov/parceldetail/'+CAST(LA.APN AS varchar) AS APN_LINK ");
		
		sb.append(" from REF_LSO_APN LA ");
		sb.append(" join ASSESSOR_OWNER AO on LA.APN= AO.APN ");
		sb.append(" join ASSESSOR_DATA AD on LA.APN= AD.APN ");
		sb.append(" join ASSESSOR_SITE AST on LA.APN= AST.APN ");
		sb.append(" join ASSESSOR_TAX AT on LA.APN= AT.APN ");
		
		sb.append(" where  LA.ACTIVE='Y' and AO.ACTIVE='Y'  and AD.ACTIVE='Y' and AST.ACTIVE='Y'  and AT.ACTIVE='Y' ");
		
		sb.append(" AND ");
		sb.append(" LSO_ID = ").append(typeid);
		if(id>0){
			sb.append(" AND ");
			sb.append(" LA.APN = ").append(id);
		}
		//sb.append(" ORDER BY N.UPDATED_DATE DESC ");
		return sb.toString();
	}
	
	

















}















