package csapi.impl.divisions;

import java.util.HashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.impl.entity.EntityAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.info.InfoSQL;
import csapi.impl.review.ReviewOptSQL;
import csapi.security.AuthorizeToken;
import csshared.vo.DivisionsList;
import csshared.vo.DivisionsVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeInfo;


public class DivisionsAgent {

	public static ObjGroupVO details(String type, int typeid) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("divisions");
		result.setGroupid("divisions");
		result.setType("divisions");
		String command = DivisionsSQL.details(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("TYPE_ID"));
					vo.setFieldid(db.getString("TYPE_ID"));
					vo.setValue(db.getString("ID"));
					vo.setLabel(db.getString("FIELD"));
					vo.setText(db.getString("VALUE"));
					vo.setRequired(db.getString("REQUIRED"));
					/*vo.setType("text");
					vo.setItype("String");*/
					vo.setType("String");
					vo.setItype("select");
					SubObjVO[] o = Choices.getChoices(DivisionsSQL.choices(db.getInt("TYPE_ID"), db.getInt("ID")));
					vo.setChoices(o);
//					vo.setJson(Choices.choiceUrl("LKUP_DIVISIONS", "DIVISION","ID","ASC","LKUP_DIVISIONS_TYPE_ID",db.getString("TYPE_ID"),db.getString("VALUE")));
					os[count] = vo;
					count++;
				} 
				result.setObj(os);
			}
			db.clear();
		}
		return result;
	}

	public static ObjGroupVO summary1(String type, int typeid) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("divisions");
		result.setGroupid("divisions");
		result.setType("divisions");
		String command = DivisionsSQL.summary(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("TYPE_ID"));
					vo.setFieldid(db.getString("TYPE_ID"));
					vo.setValue(db.getString("ID"));
					vo.setLabel(db.getString("FIELD"));
					vo.setText(db.getString("VALUE"));
					vo.setRequired(db.getString("REQUIRED"));
					if (db.getInt("LSO_ID") == typeid) {
						vo.setDatatype("level");
					}
					else {
						vo.setDatatype("external");
					}
					/*vo.setType("text");
					vo.setItype("String");*/
					vo.setType("String");
					vo.setItype("select");
//					SubObjVO[] o = Choices.getChoices(DivisionsSQL.choices(db.getInt("TYPE_ID"), db.getInt("ID")));
//					vo.setChoices(o);
//					vo.setJson(Choices.choiceUrl("LKUP_DIVISIONS", "DIVISION","ID","ASC","LKUP_DIVISIONS_TYPE_ID",db.getString("TYPE_ID"),db.getString("VALUE")));
					os[count] = vo;
					count++;
				} 
				result.setObj(os);
			}
			db.clear();
		}
		return result;
	}




	/**
	 * @deprecated Use update(RequestVO req)
	 * @return
	 */
	public static boolean saveDivision(RequestVO r, Token u) {
		String rtype = r.getType();
		int rtypeid = r.getTypeid();

		TypeInfo entity = EntityAgent.getEntity(rtype, rtypeid);
		String type = entity.getEntity();
		int typeid = entity.getLowestEntity();
		ObjGroupVO o = r.getData()[0];
		return saveDivision(type, typeid, o, u);
	}
	
	/**
	 * @deprecated Use update(RequestVO req)
	 * @return
	 */
	public static boolean saveDivision(String type, int typeid, ObjGroupVO o, Token u) {
		boolean result= false;
		try {
			ObjVO[] obj = o.getObj();

			String command ="";
			Sage db = new Sage();
			command = DivisionsSQL.inActiveCustom(type, typeid);
			result = db.update(command);
			if(obj.length>0) {
				result = new DBBatch().insertDivisions(type, typeid, obj, u.getId());
			}
			command = DivisionsSQL.backUpCustom(type, typeid);
			result = db.update(command);
			
			command = DivisionsSQL.deleteCustom(type, typeid);
			result = db.update(command);
			db.clear();
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}

	public static DivisionsList getDivisions(String type, int typeid) {
		return getDivisions(type, typeid, false);
	}

	public static DivisionsList getDivisions(String type, int typeid, boolean getchoices) {
		DivisionsList r = new DivisionsList();
		if (Operator.hasValue(type) && typeid > 0) {
			TypeInfo t = EntityAgent.getEntity(type, typeid);
			int lsoid = t.getEntityid();
			int parentid = t.getParentid();
			int grandparentid = t.getGrandparentid();
			r.setLsoid(lsoid);
			r.setParentid(parentid);
			r.setGrandparentid(grandparentid);
			String command = DivisionsSQL.getDivision(lsoid, parentid, grandparentid);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command)) {
					while (db.next()) {
						String divtype = db.getString("TYPE");
						String source = db.getString("SOURCE");
						DivisionsVO vo = new DivisionsVO();
						vo.setId(db.getInt("ID"));
						vo.setLsoid(db.getInt("LSO_ID"));
						vo.setGroupid(db.getInt("LKUP_DIVISIONS_GROUP_ID"));
						vo.setDivisionid(db.getInt("LKUP_DIVISIONS_ID"));
						vo.setDivisiontypeid(db.getInt("LKUP_DIVISIONS_TYPE_ID"));
						vo.setGroup(db.getString("GROUP_NAME"));
						vo.setDivisiontype(divtype);
						vo.setDivision(db.getString("DIVISION"));
						vo.setDescription(db.getString("DESCRIPTION"));
						vo.setInfo(db.getString("INFO"));
						vo.setUrl(db.getString("URL"));
						vo.setSource(source);
						vo.setSourceid(db.getInt("LSO_ID"));
						vo.setLsotype(source);
						vo.setRequired(db.getString("REQUIRED"));
						vo.setDot(db.getString("ISDOT"));
						vo.setDefault(db.getString("DEFLT"));
						if (db.hasValue("ISPUBLIC")) {
							vo.setShowpublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
						}

						if (getchoices) {
							SubObjVO[] o = Choices.getChoices(DivisionsSQL.choices(db.getInt("LKUP_DIVISIONS_TYPE_ID"), db.getInt("LKUP_DIVISIONS_ID")));
							vo.setChoices(o);
						}
						r.addDivision(vo);
					}
				}
				db.clear();
			}
		}
		return r;
	}

	public static ResponseVO update(RequestVO req) {
		ResponseVO res = new ResponseVO();
		res.setMessagecode("cs400");
		int id = Operator.toInt(req.getId());
		int lsoid = req.getTypeid();
		int clsoid = req.getEntityid();
		int divtypeid = Operator.toInt(req.getGroupid());
		if (lsoid < 1) { return res; }
		if (divtypeid < 1) { return res; }
		Token u = AuthorizeToken.authenticate(req);	
		Logger.highlight("CURRENT", clsoid);
		if (clsoid < 1) { clsoid = lsoid; }
		Logger.highlight("ENTITY", clsoid);
		TypeInfo t = EntityAgent.getEntity("lso", clsoid);
		String r = "";
		String command = DivisionsSQL.deactivate(lsoid, divtypeid, u.getId(), u.getIp());
		Sage db = new Sage();
		if (db.update(command)) {
			if (id > 0) {
				command = DivisionsSQL.add(lsoid, id, u.getId(), u.getIp());
				db.update(command);
			}
		}
		command = DivisionsSQL.getDivision(t.getEntityid(), t.getParentid(), t.getGrandparentid(), divtypeid);
		if (db.query(command) && db.next()) {
			r = db.getString("DIVISION");
		}
		db.clear();
		res.setMessagecode("cs200");
		res.addInfo("division", r);
		return res;
	}
	
	/*public static boolean checkDivision(RequestVO r){
		boolean result = true;
		String command = "select * from REF_ACT_DIVISION where ID ="+r.getId();
		Sage db = new Sage();
		db.query(command);
		if(db.next()){
			
		}
		
		
		db.clear();
		
		
		
	}*/

	public static ObjGroupVO summary(String type, int typeid) {
		ObjGroupVO result = new ObjGroupVO();
		result.setGroup("divisions");
		result.setGroupid("divisions");
		result.setType("divisions");
		Sage db = new Sage();
		String command = InfoSQL.getContent("divisions", false);
		if (db.query(command) && db.next()) {
			result.setContenttype("divisions");
		}
		db.clear();
		DivisionsList l = getDivisions(type, typeid);
		ObjVO[] os = new ObjVO[l.size()];
		int count = 0;
		while (l.next()) {
			DivisionsVO d = l.getDivision();
			ObjVO vo = new ObjVO();
			vo.setId(d.getId());
			vo.setField(Operator.toString(d.getDivisiontypeid()));
			vo.setFieldid(Operator.toString(d.getDivisiontypeid()));
			vo.setValue(d.getDivision());
			vo.setLabel(d.getDivisiontype());
			vo.setText(d.getDivision());
			vo.setRequired(d.getRequired());
			vo.setShowpublic(d.isShowpublic());
			vo.setType("String");
			vo.setItype("text");
			os[count] = vo;
			count++;

		}
		result.setObj(os);
		return result;
	}














}















