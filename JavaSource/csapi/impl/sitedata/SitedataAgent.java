package csapi.impl.sitedata;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.general.DBBatch;
import csapi.impl.setback.SetbackFields;
import csapi.impl.setback.SetbackSQL;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;



public class SitedataAgent {
	
	public static ObjGroupVO details(String type, int typeid, int id) {
		ObjGroupVO result = new ObjGroupVO();
//		result.setDodescription(true);
		String grp = "";
		String grpid = "";
		String grppublic = "";
		
		
		String command ="";
		command = SitedataSQL.details(type, typeid, id);
		
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					if (db.hasValue("GROUP_NAME")) { grp = db.getString("GROUP_NAME"); }
					if (db.hasValue("GROUP_ID")) { grpid = db.getString("GROUP_ID"); }
					if (db.hasValue("GROUP_PUBLIC")) { grppublic = db.getString("GROUP_PUBLIC"); }
					result.setDescriptionvalue(db.getString("DESCRIPTION"));
					result.setIdvalue(db.getInt("LSO_ID"));
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setValue(db.getString("VALUE"));
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			
			
			db.clear();
		}
		result.setType("sitedata");
		result.setAddable(true);
		if (Operator.hasValue(grp)) { result.setGroup(grp); }
		if (Operator.hasValue(grppublic)) { result.setPub(grppublic); }
		//if (grpid > 0) { result.setId(grpid); }
		if (Operator.hasValue(grpid)) { result.setGroupid(grpid); }
		//result.set
		return result;
	}
	
	public static ObjGroupVO fields(String type, int typeid) {
		ObjGroupVO result = new ObjGroupVO();
//		result.setDodescription(true);
		String grp = "";
		String grpid = "";
		String grppublic = "";
		
		
		String command ="";
		command = SitedataSQL.fields(type, typeid);
		
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					if (db.hasValue("GROUP_NAME")) { grp = db.getString("GROUP_NAME"); }
					if (db.hasValue("GROUP_ID")) { grpid = db.getString("GROUP_ID"); }
					if (db.hasValue("GROUP_PUBLIC")) { grppublic = db.getString("GROUP_PUBLIC"); }
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			
			
			db.clear();
		}
		result.setType("sitedata");
		result.setAddable(true);
		if (Operator.hasValue(grp)) { result.setGroup(grp); }
		if (Operator.hasValue(grppublic)) { result.setPub(grppublic); }
		//if (grpid > 0) { result.setId(grpid); }
		if (Operator.hasValue(grpid)) { result.setGroupid(grpid); }
		//result.set
		return result;
	}
	
	public static ObjGroupVO setbackdetails(int id) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("setback");
		result.setGroupid("setback");
		result.setGroupid("setback");
		result.setCrosstab("TYPE");
		result.setFields(new String[] {"FT","IN","COMMENTS","REQ_FT","REQ_IN","REQ_COMMENT"});
		result.setIndex(new String[] {"FT","IN"});
		result.setAddable(false);
		
		String command = SetbackSQL.details(id);
		ObjGroupVO g = SetbackFields.summary();
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				
				int sz = db.size();
				if(sz<=0){
					command = "SELECT * FROM LKUP_LSO_SETBACK_TYPE WHERE ACTIVE ='Y'";
					db.query(command);
				}
				
				ObjVO[] o = new ObjVO[db.size()];
				int count = 0;
				ObjVO[] fields = g.getObj();
				int l = fields.length;
				while (db.next()) {
					String ct = db.getString("TYPE");
					ObjVO vo = new ObjVO();
					if (db.equalsIgnoreCase("FINAL", "Y")) {
						vo.setFinaled(true);
					}
					vo.setField(ct);
					vo.setId(id);
					for (int i=0; i<l; i++) {
						ObjVO f = fields[i];
						String field = f.getField();
						String type = f.getType();
						String itype = f.getItype();
						String value = db.getString(field);
						String text = value;
						String textfield = f.getTextfield();
						if (Operator.hasValue(textfield)) {
							text = db.getString(textfield);
						}
						SubObjVO s = new SubObjVO();
						s.setValue(value);
						s.setType(type);
						s.setItype(itype);
						s.setText(text);
						vo.getValues().put(field, s);
					}
					o[count] = vo;
					count++;
				}
				result.setObj(o);
				
			}
			db.clear();
		}
		result.setType("setback");
		return result;
	}
	
	
	
	
	
	public static boolean saveSitedata(int sitedataid, int lsoid, ObjGroupVO vo, Token u){
		boolean result= false;
		try {
			ObjVO[] obj = vo.getObj();
			
		
			String command ="";
			// deactivate custom
			if(obj.length > 0){
				Sage db = new Sage();
				
				StringBuilder sb = new StringBuilder();

				if(sitedataid > 0) {
					sb.append(" UPDATE LSO_SITEDATA SET UPDATED_DATE = CURRENT_TIMESTAMP, UPDATED_BY = ").append(u.getId()).append(" WHERE ID = ").append(sitedataid);
					command = sb.toString();
					result = db.update(command);
					command = SitedataSQL.inActiveCustom(sitedataid, 1);
					result = db.update(command);
					db.clear();
					if(result) {
						result = new DBBatch().insertCustom("lso", sitedataid, obj, u.getId(), 1, "LSO_SITEDATA_FIELD_VALUE", "LSO_SITEDATA_ID");
					}
					if(result){
						command = SitedataSQL.backUpCustom(sitedataid, 1);
						db.update(command);
						command = SitedataSQL.deleteCustom(sitedataid, 1);
						db.update(command);
						
					}
				}
				else if (lsoid > 0) {
					sb = new StringBuilder();
					sb.append(" INSERT INTO LSO_SITEDATA (LSO_ID,CREATED_BY,UPDATED_BY) VALUES(").append(lsoid).append(",").append(u.getId()).append(",").append(u.getId()).append(")");
					command = sb.toString();
					db.update(command);
					
					sb = new StringBuilder();
					sb.append(" SELECT TOP 1 * FROM LSO_SITEDATA WHERE LSO_ID = ").append(lsoid).append(" AND CREATED_BY = ").append(u.getId()).append(" ORDER BY CREATED_DATE DESC");
					command = sb.toString();
					db.query(command);
					if(db.next()){
						sitedataid = db.getInt("ID");
					}
					if (sitedataid > 0) {
						result = new DBBatch().insertCustom("lso", sitedataid, obj, u.getId(), 1, "LSO_SITEDATA_FIELD_VALUE", "LSO_SITEDATA_ID");
					}
				}

				db.clear();
			}
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}
	
}
















