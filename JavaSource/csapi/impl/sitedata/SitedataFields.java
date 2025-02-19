package csapi.impl.sitedata;

import alain.core.utils.Logger;
import csapi.common.Choices;
import csapi.common.FieldObjects;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class SitedataFields {

	public static final String TABLE_TYPE = "horizontal";
	//public static final String TABLE_TYPE_LIST = "vertical";

	/*public static ObjGroupVO details() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("sitedata");
		r.setType("sitedata");
		r.setGroupid("sitedata");
		
		r.setObj(getInitial());
		return r;
	}*/

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("sitedata");
		r.setType("sitedata");
		r.setGroupid("sitedata");
		r.setAddable(true);
		r.setFields(new String[] {"ENTITY","DESCRIPTION","DATE"});
		r.setIndex(new String[] {"ENTITY","DESCRIPTION","DATE"});
		
		//r.setAddable(true);
		r.setDeletable(false);
		//r.setEditable(true);*/
		//r.setObj(getInitial());
		ObjVO[] o = new ObjVO[3];
		ObjVO s = new ObjVO();

		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("ENTITY");
		s.setType("String");
		s.setItype("String");
		s.setField("ENTITY");
		s.setLabel("ENTITY");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[0] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DESCRIPTION");
		s.setType("String");
		s.setItype("String");
		s.setField("DESCRIPTION");
		s.setLabel("DESCRIPTION");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[1] = s;
		
		s = new ObjVO();
		s.setId(-1);
		s.setOrder(0);
		s.setFieldid("DATE");
		s.setType("date");
		s.setItype("date");
		s.setField("DATE");
		s.setLabel("DATE");
		s.setRequired("N");
		s.setEditable("N");
		s.setAddable("N");
		o[2] = s;
		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	public static ObjGroupVO list() {
		return summary();
	}
	
	public static ObjVO[] getAsssesorAll(){
		ObjVO[] owner = getInitial();
		ObjVO[] site = getAsssesorSite();
		ObjVO[] tax = getAsssesorTax();
		ObjVO[] data = getAsssesorData();
		
		int d = owner.length+site.length+tax.length+data.length;
		ObjVO[] o = new ObjVO[d];
		
		int c =0;
		for(int i=0;i<owner.length;i++){
			o[c] = owner[i];
			c++;
		}
	
		for(int i=0;i<site.length;i++){
			o[c] = site[i];
			c++;
		}
		
		
		for(int i=0;i<tax.length;i++){
			o[c] = tax[i];
			c++;
		}
		
		
		for(int i=0;i<data.length;i++){
			o[c] = data[i];
			c++;
		}
		
		
		return o;
	}	
	
	public static ObjVO[] getInitial(){
		ObjVO[] o = new ObjVO[2];
		
		o[0] = FieldObjects.toObject(3, 3, "NAME", "String", "String", "NAME", "NAME", "", "", "", "", "", "", "N","N","N");
		o[1] = FieldObjects.toObject(3, 3, "DATE", "Date", "Date", "DATE", "DATE", "", "", "", "", "", "", "N","N","N");
		/*o[2] = FieldObjects.toObject(3, 3, "FRST_OWNR_NAME_OVR", "String", "String", "FRST_OWNR_NAME_OVR", "", "", "", "", "", "", "", "N","N","N");
		o[3] = FieldObjects.toObject(3, 3, "MAIL_ADDR_HOUSE_NO", "String", "String", "MAIL_ADDR_HOUSE_NO", "", "", "", "", "", "", "", "N","N","N");
		o[4] = FieldObjects.toObject(3, 3, "MAIL_ADDR_FRACTION", "String", "String", "MAIL_ADDR_FRACTION", "", "", "", "", "", "", "", "N","N","N");
		o[5] = FieldObjects.toObject(3, 3, "MAIL_ADDR_DIR", "String", "String", "MAIL_ADDR_DIR", "", "", "", "", "", "", "", "N","N","N");
		o[6] = FieldObjects.toObject(3, 3, "MAIL_ADDR_ST_NAME", "String", "String", "MAIL_ADDR_ST_NAME", "", "", "", "", "", "", "", "N","N","N");
		o[7] = FieldObjects.toObject(3, 3, "MAIL_ADDR_UNIT", "String", "String", "MAIL_ADDR_UNIT", "", "", "", "", "", "", "", "N","N","N");
		o[8] = FieldObjects.toObject(3, 3, "MAIL_ADDR_CTY_STA", "String", "String", "MAIL_ADDR_CTY_STA", "", "", "", "", "", "", "", "N","N","N");
		o[9] = FieldObjects.toObject(3, 3, "MAIL_ADDR_ZIP", "String", "String", "MAIL_ADDR_ZIP", "", "", "", "", "", "", "", "N","N","N");
		o[10] = FieldObjects.toObject(3, 3, "SPEC_NAME_LEGEND", "String", "String", "SPEC_NAME_LEGEND", "", "", "", "", "", "", "", "N","N","N");
		o[11] = FieldObjects.toObject(3, 3, "SPEC_NAME_ASSESSEE", "String", "String", "SPEC_NAME_ASSESSEE", "", "", "", "", "", "", "", "N","N","N");
		o[12] = FieldObjects.toObject(3, 3, "SECND_OWNR_NAME", "String", "String", "SECND_OWNR_NAME", "", "", "", "", "", "", "", "N","N","N");
		o[13] = FieldObjects.toObject(3, 3, "RECORDING_DATE", "String", "String", "RECORDING_DATE", "", "", "", "", "", "", "", "N","N","N");*/
		
		return o;
	}	
	
	
	public static ObjVO[] getAsssesorTax(){
		ObjVO[] o = new ObjVO[27];
		
		o[0] = FieldObjects.toObject(3, 3, "TAX_RATE_AREA", "String", "String", "TAX_RATE_AREA", "", "", "", "", "", "", "", "N","N","N");
		o[1] = FieldObjects.toObject(3, 3, "AGENCY_CLASS_NO", "String", "String", "AGENCY_CLASS_NO", "", "", "", "", "", "", "", "N","N","N");
		o[2] = FieldObjects.toObject(3, 3, "LAND_CURR_ROLL_YR", "String", "String", "LAND_CURR_ROLL_YR", "", "", "", "", "", "", "", "N","N","N");
		o[3] = FieldObjects.toObject(3, 3, "LAND_CURR_VAL", "String", "String", "LAND_CURR_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[4] = FieldObjects.toObject(3, 3, "IMP_CURR_ROLL_YR", "String", "String", "IMP_CURR_ROLL_YR", "", "", "", "", "", "", "", "N","N","N");
		o[5] = FieldObjects.toObject(3, 3, "IMP_CURR_VAL", "String", "String", "IMP_CURR_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[6] = FieldObjects.toObject(3, 3, "TAX_STATUS_KEY", "String", "String", "TAX_STATUS_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[7] = FieldObjects.toObject(3, 3, "TAX_STAT_YR_SOLD", "String", "String", "TAX_STAT_YR_SOLD", "", "", "", "", "", "", "", "N","N","N");
		o[8] = FieldObjects.toObject(3, 3, "OWNERSHIP_CODE", "String", "String", "OWNERSHIP_CODE", "", "", "", "", "", "", "", "N","N","N");
		o[9] = FieldObjects.toObject(3, 3, "EXEM_CLAIM_TYPE", "String", "String", "EXEM_CLAIM_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[10] = FieldObjects.toObject(3, 3, "PER_PROP_KEY", "String", "String", "PER_PROP_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[11] = FieldObjects.toObject(3, 3, "PER_PROP_VAL", "String", "String", "PER_PROP_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[12] = FieldObjects.toObject(3, 3, "PERL_PROP_EXEM_VAL", "String", "String", "PERL_PROP_EXEM_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[13] = FieldObjects.toObject(3, 3, "FIXTURE_VALUE", "String", "String", "FIXTURE_VALUE", "", "", "", "", "", "", "", "N","N","N");
		o[14] = FieldObjects.toObject(3, 3, "FIXTURE_EXEM_VAL", "String", "String", "FIXTURE_EXEM_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[15] = FieldObjects.toObject(3, 3, "HOMEOWNER_NO_EXEM", "String", "String", "HOMEOWNER_NO_EXEM", "", "", "", "", "", "", "", "N","N","N");
		o[16] = FieldObjects.toObject(3, 3, "HOMEOWNER_EXEM_VAL", "String", "String", "HOMEOWNER_EXEM_VAL", "", "", "", "", "", "", "", "N","N","N");
		o[17] = FieldObjects.toObject(3, 3, "REAL_ESTATE_EXEMPT", "String", "String", "REAL_ESTATE_EXEMPT", "", "", "", "", "", "", "", "N","N","N");
		o[18] = FieldObjects.toObject(3, 3, "LAST_SL1_VER_KEY", "String", "String", "LAST_SL1_VER_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[19] = FieldObjects.toObject(3, 3, "LAST_SL1_SL_AMNT", "currency", "currency", "LAST_SL1_SL_AMNT", "", "", "", "", "", "", "", "N","N","N");
		o[20] = FieldObjects.toObject(3, 3, "LAST_SL1_SL_DT", "String", "String", "LAST_SL1_SL_DT", "", "", "", "", "", "", "", "N","N","N");
		o[21] = FieldObjects.toObject(3, 3, "LAST_SL2_VER_KEY", "String", "String", "LAST_SL2_VER_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[22] = FieldObjects.toObject(3, 3, "LAST_SL2_SL_AMNT", "currency", "currency", "LAST_SL2_SL_AMNT", "", "", "", "", "", "", "", "N","N","N");
		o[23] = FieldObjects.toObject(3, 3, "LAST_SL2_SL_DT", "String", "String", "LAST_SL2_SL_DT", "", "", "", "", "", "", "", "N","N","N");
		o[24] = FieldObjects.toObject(3, 3, "LAST_SL3_VER_KEY", "String", "String", "LAST_SL3_VER_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[25] = FieldObjects.toObject(3, 3, "LAST_SL3_SL_AMNT", "currency", "currency", "LAST_SL3_SL_AMNT", "", "", "", "", "", "", "", "N","N","N");
		o[26] = FieldObjects.toObject(3, 3, "LAST_SL3_SL_DT", "String", "String", "LAST_SL3_SL_DT", "", "", "", "", "", "", "", "N","N","N");
		
		return o;
	}	
	
	public static ObjVO[] getAsssesorSite(){
		ObjVO[] o = new ObjVO[15];
		
		o[0] = FieldObjects.toObject(3, 3, "SITUS_HOUSE_NO", "String", "String", "SITUS_HOUSE_NO", "", "", "", "", "", "", "", "N","N","N");
		o[1] = FieldObjects.toObject(3, 3, "SITUS_FRACTION", "String", "String", "SITUS_FRACTION", "", "", "", "", "", "", "", "N","N","N");
		o[2] = FieldObjects.toObject(3, 3, "SITUS_DIRECTION", "String", "String", "SITUS_DIRECTION", "", "", "", "", "", "", "", "N","N","N");
		o[3] = FieldObjects.toObject(3, 3, "SITUS_STREET_NAME", "String", "String", "SITUS_STREET_NAME", "", "", "", "", "", "", "", "N","N","N");
		o[4] = FieldObjects.toObject(3, 3, "SITUS_UNIT", "String", "String", "SITUS_UNIT", "", "", "", "", "", "", "", "N","N","N");
		o[5] = FieldObjects.toObject(3, 3, "SITUS_CITY_STATE", "String", "String", "SITUS_CITY_STATE", "", "", "", "", "", "", "", "N","N","N");
		o[6] = FieldObjects.toObject(3, 3, "SITUS_ZIP", "String", "String", "SITUS_ZIP", "", "", "", "", "", "", "", "N","N","N");
		o[7] = FieldObjects.toObject(3, 3, "ZONING_CODE", "String", "String", "ZONING_CODE", "", "", "", "", "", "", "", "N","N","N");
		o[8] = FieldObjects.toObject(3, 3, "USE_CODE", "String", "String", "USE_CODE", "", "", "", "", "", "", "", "N","N","N");
		o[9] = FieldObjects.toObject(3, 3, "LEGAL_DESC1", "String", "String", "LEGAL_DESC1", "", "", "", "", "", "", "", "N","N","N");
		o[10] = FieldObjects.toObject(3, 3, "LEGAL_DESC2", "String", "String", "LEGAL_DESC2", "", "", "", "", "", "", "", "N","N","N");
		o[11] = FieldObjects.toObject(3, 3, "LEGAL_DESC3", "String", "String", "LEGAL_DESC3", "", "", "", "", "", "", "", "N","N","N");
		o[12] = FieldObjects.toObject(3, 3, "LEGAL_DESC4", "String", "String", "LEGAL_DESC4", "", "", "", "", "", "", "", "N","N","N");
		o[13] = FieldObjects.toObject(3, 3, "LEGAL_DESC5", "String", "String", "LEGAL_DESC5", "", "", "", "", "", "", "", "N","N","N");
		o[14] = FieldObjects.toObject(3, 3, "LEGAL_DESC6", "String", "String", "LEGAL_DESC6", "", "", "", "", "", "", "", "N","N","N");
		
		return o;
	}	
	
	public static ObjVO[] getAsssesorData(){
		ObjVO[] o = new ObjVO[44];
		
		o[0] = FieldObjects.toObject(3, 3, "HAZ_ABATE_CTY_KEY", "String", "String", "HAZ_ABATE_CTY_KEY", "", "", "", "", "", "", "", "N","N","N");
		o[1] = FieldObjects.toObject(3, 3, "HAZ_ABATE_INFO", "String", "String", "HAZ_ABATE_INFO", "", "", "", "", "", "", "", "N","N","N");
		o[2] = FieldObjects.toObject(3, 3, "PARTIAL_INTEREST", "String", "String", "PARTIAL_INTEREST", "", "", "", "", "", "", "", "N","N","N");
		o[3] = FieldObjects.toObject(3, 3, "DOC_REASON_CODE", "String", "String", "DOC_REASON_CODE", "", "", "", "", "", "", "", "N","N","N");
		
		o[4] = FieldObjects.toObject(3, 3, "DATA1_SUBPART", "String", "String", "DATA1_SUBPART", "", "", "", "", "", "", "", "N","N","N");
		o[5] = FieldObjects.toObject(3, 3, "DATA1_DESIGN_TYPE", "String", "String", "DATA1_DESIGN_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[6] = FieldObjects.toObject(3, 3, "DATA1_QUALITY", "String", "String", "DATA1_QUALITY", "", "", "", "", "", "", "", "N","N","N");
		o[7] = FieldObjects.toObject(3, 3, "DATA1_YR_BUILT", "String", "String", "DATA1_YR_BUILT", "", "", "", "", "", "", "", "N","N","N");
		o[8] = FieldObjects.toObject(3, 3, "DATA1_NO_UNITS", "String", "String", "DATA1_NO_UNITS", "", "", "", "", "", "", "", "N","N","N");
		o[9] = FieldObjects.toObject(3, 3, "DATA1_NO_BDRMS", "String", "String", "DATA1_NO_BDRMS", "", "", "", "", "", "", "", "N","N","N");
		o[10] = FieldObjects.toObject(3, 3, "DATA1_NO_BATHS", "String", "String", "DATA1_NO_BATHS", "", "", "", "", "", "", "", "N","N","N");
		o[11] = FieldObjects.toObject(3, 3, "DATA1_SQ_FT", "String", "String", "DATA1_SQ_FT", "", "", "", "", "", "", "", "N","N","N");
		
		o[12] = FieldObjects.toObject(3, 3, "DATA2_SUBPART", "String", "String", "DATA2_SUBPART", "", "", "", "", "", "", "", "N","N","N");
		o[13] = FieldObjects.toObject(3, 3, "DATA2_DESIGN_TYPE", "String", "String", "DATA2_DESIGN_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[14] = FieldObjects.toObject(3, 3, "DATA2_QUALITY", "String", "String", "DATA2_QUALITY", "", "", "", "", "", "", "", "N","N","N");
		o[15] = FieldObjects.toObject(3, 3, "DATA2_YR_BUILT", "String", "String", "DATA2_YR_BUILT", "", "", "", "", "", "", "", "N","N","N");
		o[16] = FieldObjects.toObject(3, 3, "DATA2_NO_UNITS", "String", "String", "DATA2_NO_UNITS", "", "", "", "", "", "", "", "N","N","N");
		o[17] = FieldObjects.toObject(3, 3, "DATA2_NO_BDRMS", "String", "String", "DATA2_NO_BDRMS", "", "", "", "", "", "", "", "N","N","N");
		o[18] = FieldObjects.toObject(3, 3, "DATA2_NO_BATHS", "String", "String", "DATA2_NO_BATHS", "", "", "", "", "", "", "", "N","N","N");
		o[19] = FieldObjects.toObject(3, 3, "DATA2_SQ_FT", "String", "String", "DATA2_SQ_FT", "", "", "", "", "", "", "", "N","N","N");
		
		o[20] = FieldObjects.toObject(3, 3, "DATA3_SUBPART", "String", "String", "DATA3_SUBPART", "", "", "", "", "", "", "", "N","N","N");
		o[21] = FieldObjects.toObject(3, 3, "DATA3_DESIGN_TYPE", "String", "String", "DATA3_DESIGN_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[22] = FieldObjects.toObject(3, 3, "DATA3_QUALITY", "String", "String", "DATA3_QUALITY", "", "", "", "", "", "", "", "N","N","N");
		o[23] = FieldObjects.toObject(3, 3, "DATA3_YR_BUILT", "String", "String", "DATA3_YR_BUILT", "", "", "", "", "", "", "", "N","N","N");
		o[24] = FieldObjects.toObject(3, 3, "DATA3_NO_UNITS", "String", "String", "DATA3_NO_UNITS", "", "", "", "", "", "", "", "N","N","N");
		o[25] = FieldObjects.toObject(3, 3, "DATA3_NO_BDRMS", "String", "String", "DATA3_NO_BDRMS", "", "", "", "", "", "", "", "N","N","N");
		o[26] = FieldObjects.toObject(3, 3, "DATA3_NO_BATHS", "String", "String", "DATA3_NO_BATHS", "", "", "", "", "", "", "", "N","N","N");
		o[27] = FieldObjects.toObject(3, 3, "DATA3_SQ_FT", "String", "String", "DATA3_SQ_FT", "", "", "", "", "", "", "", "N","N","N");
		
		o[28] = FieldObjects.toObject(3, 3, "DATA4_SUBPART", "String", "String", "DATA4_SUBPART", "", "", "", "", "", "", "", "N","N","N");
		o[29] = FieldObjects.toObject(3, 3, "DATA4_DESIGN_TYPE", "String", "String", "DATA4_DESIGN_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[30] = FieldObjects.toObject(3, 3, "DATA4_QUALITY", "String", "String", "DATA4_QUALITY", "", "", "", "", "", "", "", "N","N","N");
		o[31] = FieldObjects.toObject(3, 3, "DATA4_YR_BUILT", "String", "String", "DATA4_YR_BUILT", "", "", "", "", "", "", "", "N","N","N");
		o[32] = FieldObjects.toObject(3, 3, "DATA4_NO_UNITS", "String", "String", "DATA4_NO_UNITS", "", "", "", "", "", "", "", "N","N","N");
		o[33] = FieldObjects.toObject(3, 3, "DATA4_NO_BDRMS", "String", "String", "DATA4_NO_BDRMS", "", "", "", "", "", "", "", "N","N","N");
		o[34] = FieldObjects.toObject(3, 3, "DATA4_NO_BATHS", "String", "String", "DATA4_NO_BATHS", "", "", "", "", "", "", "", "N","N","N");
		o[35] = FieldObjects.toObject(3, 3, "DATA4_SQ_FT", "String", "String", "DATA4_SQ_FT", "", "", "", "", "", "", "", "N","N","N");
		

		o[36] = FieldObjects.toObject(3, 3, "DATA5_SUBPART", "String", "String", "DATA5_SUBPART", "", "", "", "", "", "", "", "N","N","N");
		o[37] = FieldObjects.toObject(3, 3, "DATA5_DESIGN_TYPE", "String", "String", "DATA5_DESIGN_TYPE", "", "", "", "", "", "", "", "N","N","N");
		o[38] = FieldObjects.toObject(3, 3, "DATA5_QUALITY", "String", "String", "DATA5_QUALITY", "", "", "", "", "", "", "", "N","N","N");
		o[39] = FieldObjects.toObject(3, 3, "DATA5_YR_BUILT", "String", "String", "DATA5_YR_BUILT", "", "", "", "", "", "", "", "N","N","N");
		o[40] = FieldObjects.toObject(3, 3, "DATA5_NO_UNITS", "String", "String", "DATA5_NO_UNITS", "", "", "", "", "", "", "", "N","N","N");
		o[41] = FieldObjects.toObject(3, 3, "DATA5_NO_BDRMS", "String", "String", "DATA5_NO_BDRMS", "", "", "", "", "", "", "", "N","N","N");
		o[42] = FieldObjects.toObject(3, 3, "DATA5_NO_BATHS", "String", "String", "DATA5_NO_BATHS", "", "", "", "", "", "", "", "N","N","N");
		o[43] = FieldObjects.toObject(3, 3, "DATA5_SQ_FT", "String", "String", "DATA5_SQ_FT", "", "", "", "", "", "", "", "N","N","N");
		
		return o;
	}
	

}




