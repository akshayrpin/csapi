package csapi.impl.setback;

import java.util.ArrayList;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.utils.CsReflect;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.SubObjVO;


public class SetbackAgent {

	public static ObjGroupVO details(int sitedataid) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("setback");
		result.setGroupid("setback");
		String command = SetbackSQL.details(sitedataid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("FIELD"));
					vo.setFieldid(db.getString("ID"));
					vo.setValue(db.getString("VALUE"));
					/*vo.setType("text");
					vo.setItype("String");*/
					vo.setType("String");
					vo.setItype("String");
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			db.clear();
		}
		result.setType("setback");
		return result;
	}
	
	
	
	public static ObjGroupVO setbackfields(int typeid,int id) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("setback");
		result.setGroupid("setback");
		result.setType("setback");
		result.setAddable(false);
		
		
		String command = "select * from LKUP_LSO_SETBACK_TYPE WHERE ACTIVE ='Y'";
		ObjGroupVO g = SetbackFields.details();
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				int sz = db.size()*6;
				ObjVO[] o = new ObjVO[sz];
				int count =0;
				while (db.next()) {
					String type = db.getString("TYPE");
					ObjVO vo = new ObjVO();
					
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_FT");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_FT");
					vo.setLabel(type+" FT");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_IN");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_IN");
					vo.setLabel(type+" IN");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_Comments");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_Comments");
					vo.setLabel(type+" Comments");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_REQ_FT");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_FT");
					vo.setLabel(type+" REQ_FT");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_REQ_IN");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_IN");
					vo.setLabel(type+" REQ_IN");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("ID")+"_REQ_Comment");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_Comment");
					vo.setLabel(type+" REQ_Comment");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					o[count] = vo;
					count ++;
					
					
				}
				result.setObj(o);
				
			}
			db.clear();
		}
	//	result.setType("setback");
		return result;
	}
	
	
	
	public static ObjGroupVO setbackdetails(int sitedataid) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("setback");
		result.setGroupid("setback");
		result.setType("setback");
		result.setAddable(false);
		
		
		String command = SetbackSQL.details(sitedataid);
		ObjGroupVO g = SetbackFields.details();
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				int dz = db.size();
				if(dz<=0){
					command = 	"SELECT ID AS TYPE_ID, TYPE from LKUP_LSO_SETBACK_TYPE WHERE ACTIVE ='Y'";
					db.query(command);
				}
				
				int sz = db.size()*6;
				ObjVO[] o = new ObjVO[sz];
				int count =0;
				while (db.next()) {
					String type = db.getString("TYPE");
					ObjVO vo = new ObjVO();
					
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_FT");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_FT");
					vo.setLabel(type+" FT");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("FT"));
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_IN");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_IN");
					vo.setLabel(type+" IN");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("INCHES"));
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_Comments");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_Comments");
					vo.setLabel(type+" Comments");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("COMMENTS"));
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_REQ_FT");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_FT");
					vo.setLabel(type+" REQ_FT");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("REQ_FT"));
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_REQ_IN");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_IN");
					vo.setLabel(type+" REQ_IN");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("REQ_INCHES"));
					
					o[count] = vo;
					count ++;
					
					
					vo = new ObjVO();
					vo.setId(-1);
					vo.setOrder(0);
					vo.setFieldid(db.getString("TYPE_ID")+"_REQ_Comment");
					vo.setType("String");
					vo.setItype("String");
					vo.setField(type+"_REQ_Comment");
					vo.setLabel(type+" REQ_Comment");
					vo.setRequired("N");
					vo.setEditable("Y");
					vo.setAddable("Y");
					vo.setValue(db.getString("REQ_COMMENT"));
					o[count] = vo;
					count ++;
					
					
				}
				result.setObj(o);
				
			}
			db.clear();
		}
	//	result.setType("setback");
		return result;
	}

	
	
	
	
	
	public static boolean saveSetback(RequestVO r, Token u){
		boolean result= false;
		try{
			int sitedataid = Operator.toInt(r.getId());
			int userid = u.getId();
			String command ="";
			
			if(sitedataid>0){
				
				ObjGroupVO o = r.getData()[0];
				ObjVO[] obj = o.getObj();
				StringBuilder sb = new StringBuilder();
				ArrayList<String> q = new ArrayList<String>();
				if(obj.length>0 && userid>0){
					Sage db = new Sage();
					command = "select * from LKUP_LSO_SETBACK_TYPE WHERE ACTIVE ='Y' ORDER BY ORDR";
					db.query(command);
					while(db.next()){
						int typeid = db.getInt("ID");
						StringBuilder t = new StringBuilder();
						t.append(" insert into  LSO_SETBACK ( LSO_SITEDATA_ID, LKUP_LSO_SETBACK_TYPE_ID, ");
						StringBuilder v = new StringBuilder();
						v.append(sitedataid);
						v.append(",");
						v.append(typeid);
						v.append(",");
						for(int i=0;i<obj.length;i++){
							ObjVO vo = obj[i];
							String field = vo.getFieldid();
							String value = vo.getValue();
							String ft = typeid+"_FT";
							if(ft.equalsIgnoreCase(field)){
								t.append("FT").append(",");
								v.append(Operator.toInt(value));
								v.append(",");
							}
							
							String in = typeid+"_IN";
							if(in.equalsIgnoreCase(field)){
								t.append("INCHES").append(",");
								v.append(Operator.toInt(value));
								v.append(",");
							}
							
							
							String comments = typeid+"_Comments";
							if(comments.equalsIgnoreCase(field)){
								t.append("COMMENTS").append(",");
								v.append("'").append(Operator.sqlEscape(value)).append("'");
								v.append(",");
							}
							
							
							String reqft = typeid+"_REQ_FT";
							if(reqft.equalsIgnoreCase(field)){
								t.append("REQ_FT").append(",");
								v.append(Operator.toInt(value));
								v.append(",");
							}
							
							String reqin = typeid+"_REQ_IN";
							if(reqin.equalsIgnoreCase(field)){
								t.append("REQ_INCHES").append(",");
								v.append(Operator.toInt(value));
								v.append(",");
							}
							
							
						
							
							String reqcomments = typeid+"_REQ_Comment";
							if(reqcomments.equalsIgnoreCase(field)){
								t.append("REQ_COMMENT").append(",");
								v.append("'").append(Operator.sqlEscape(value)).append("'");
								v.append(",");
							}
							
						}//end for
						Timekeeper k = new Timekeeper();
						t.append(" START_DATE,CREATED_BY,UPDATED_BY ) VALUES(");
						v.append("CURRENT_TIMESTAMP");
						v.append(",");
						v.append(userid);
						v.append(",");
						v.append(userid);
						v.append(")");
						
						t.append(v.toString());
						q.add(t.toString());
					}//end while
				
					if(q.size()>0){
						command = "UPDATE LSO_SETBACK SET ACTIVE='N' WHERE LSO_SITEDATA_ID="+sitedataid;
						db.update(command);
						for(int i=0;i<q.size();i++){
							result = db.update(q.get(i));
						}
						command = "UPDATE LSO_SETBACK SET START_DATE = getDate() WHERE ACTIVE='Y' AND LSO_SITEDATA_ID="+sitedataid;
						db.update(command);
					}
					db.clear();
				
				}
				
			}
			else {
				
				result = false;
				
			}
			
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}
}















