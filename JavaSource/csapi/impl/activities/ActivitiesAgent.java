package csapi.impl.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.common.LkupCache;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.lso.LsoAgent;
import csapi.impl.people.PeopleSQL;
import csapi.utils.CsTools;
import csapi.utils.Email;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Modules;
import csshared.vo.DataVO;
import csshared.vo.HoldsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.lkup.RolesVO;


public class ActivitiesAgent {

	public static ObjGroupVO summary(String type, int typeid, int id, String option, Token u) {
		ObjGroupVO fields = new ObjGroupVO();
		if(!u.isStaff()){
			boolean blocked = LsoAgent.blocked(type, typeid);
			if(blocked){
				return new ObjGroupVO();
			}
			option = "All";
		}
		String command = ActivitiesSQL.summary(type, typeid, id, option, u);
		if (!u.isStaff()) {
			fields = ActivitiesFields.publicSummary();
		}
		else if (!Operator.equalsIgnoreCase(type, "project")) {
			fields = ActivitiesFields.entitySummary();
		}
		else {
			fields = ActivitiesFields.summary();
		}
		ObjGroupVO g = Group.horizontal(fields, command);

		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold("activities");
		}
		if (dh) {
			HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
			RolesVO r = LkupCache.getModuleRoles("activities");
			g.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
		}
		else {
			RolesVO r = LkupCache.getModuleRoles("activities");
			g.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		}
		if (!g.isRead()) { return new ObjGroupVO(); }
		return g;
	}

	public static ArrayList<HashMap<String, String>> activeList(int projectid, Token u) {
		ArrayList<HashMap<String, String>> r = new ArrayList<HashMap<String, String>>();
		if (projectid < 1) { return r; }
		String command = ActivitiesSQL.getProjectActivities(projectid, u);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			while (db.next()) {
				if (db.equals("LIVE", "Y")) {
					HashMap<String, String> m = new HashMap<String, String>();
					String actid = db.getString("ID");
					String actnbr = db.getString("ACT_NBR");
					String desc = db.getString("DESCRIPTION");
					String status = db.getString("STATUS");
					String amount = db.getString("FEE_AMOUNT");
					String paid = db.getString("FEE_PAID");
					String balance = db.getString("BALANCE_DUE");
					String type = db.getString("TYPE");
					String updated = db.getString("UPDATED");
					String vdeclared = db.getString("VALUATION_DECLARED");
					String vcalc = db.getString("VALUATION_CALCULATED");
					String applied = db.getString("APPLIED_DATE");
					String start = db.getString("START_DATE");
					String issued = db.getString("ISSUED_DATE");
					String finaled = db.getString("FINAL_DATE");
					String exp = db.getString("EXP_DATE");
					String appexp = db.getString("APPLICATION_EXP_DATE");
					String plchk = db.getString("PLAN_CHK_REQ");
					String sensitive = db.getString("SENSITIVE");
					String inherit = db.getString("INHERIT");
					String active = db.getString("ACTIVE");
					m.put("ID", actid);
					m.put("ACT_NBR", actnbr);
					m.put("DESCRIPTION", desc);
					m.put("STATUS", status);
					m.put("FEE_AMOUNT", amount);
					m.put("FEE_PAID", paid);
					m.put("BALANCE_DUE", balance);
					m.put("TYPE", type);
					m.put("UPDATED", updated);
					m.put("VALUATION_DECLARED", vdeclared);
					m.put("VALUATION_CALCULATED", vcalc);
					m.put("APPLIED_DATE", applied);
					m.put("START_DATE", start);
					m.put("ISSUED_DATE", issued);
					m.put("FINAL_DATE", finaled);
					m.put("EXP_DATE", exp);
					m.put("APPLICATION_EXP_DATE", appexp);
					m.put("PLAN_CHK_REQ", plchk);
					m.put("SENSITIVE", sensitive);
					m.put("INHERIT", inherit);
					if (db.equals("ACTIVE", "Y")) {
						m.put("ACTIVE", "Y");
					}
					else {
						m.put("ACTIVE", "N");
					}
					r.add(m);
				}
			}
			db.clear();
		}
		return r;
	}

	public static ResponseVO save(RequestVO vo, Token u) {
		ResponseVO r = LogAgent.getLog(vo.getProcessid());
		DataVO m = DataVO.toDataVO(vo);
		String actid = m.getString("ACTIVITY_ID");
		String applied = m.getString("APPLIED_DATE");
		String start = m.getString("START_DATE");
		String issued = m.getString("ISSUED_DATE");
		String appexp = m.getString("APPLICATION_EXP_DATE");
		String exp = m.getString("EXP_DATE");
		String finaldate = m.getString("FINAL_DATE");
		String valuationcalculated = m.getString("VALUATION_CALCULATED");
		String valuationdeclared = m.getString("VALUATION_DECLARED");
		String plancheck = m.getString("PLAN_CHK_REQ");
		String inherit = m.getString("INHERIT");
		String sensitive = m.getString("SENSITIVE");
		String status = m.getString("LKUP_ACT_STATUS_ID");
		String send = m.getString("SEND_EMAIL");
		String notifytypes = m.getString("NOTIFY_TYPES");
		String comment = m.getString("COMMENT");

		String[] actids = Operator.split(actid, "|");
		
		int processes = actid.length() / 100;
		String command = ActivitiesSQL.updateActivities(actids, status, applied, start, issued, appexp, exp, finaldate, valuationcalculated, valuationdeclared, plancheck, inherit, sensitive, u.getId(), u.getIp());
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			boolean b = db.update(command);
			if (b) {
				if (Operator.hasValue(valuationcalculated) && Operator.hasValue(actid)) {
					// VALUATION CALCULATED
					FinanceAgent.updateFinance(vo,u, actid,0,null);
				}
				for (int i=0; i<actids.length; i++) {
					int aid = Operator.toInt(actids[i]);
					if (aid > 0){
						ActivityAgent.addHistory(aid, "activity", aid, "multiedit");
						LinkedHashMap<String, String> cm = ActivityAgent.getContentData(aid);
						String actnbr = cm.get("ACTIVITY NUMBER");
						LogAgent.updateLog(vo.getProcessid(), processes, "Updating Activity: "+actnbr);
						if (Operator.equalsIgnoreCase(send, "Y") && Operator.hasValue(notifytypes)) {
							command = PeopleSQL.getActivityRecipients(aid, notifytypes);
							db.query(command);
							while (db.next()) {
								String email = db.getString("EMAIL");
								String name = db.getString("NAME");
								int uid = db.getInt("ID");
								if (Operator.isEmail(email)) {
									String content = Email.genericTemplate(name, comment, cm);
									String subject = "City of Beverly Hills: An update has been made to activity number "+actnbr;
									CommunicationsAgent.email("activity", aid, email, uid, subject, content, u.getId(), u.getIp());
									LogAgent.updateLog(vo.getProcessid(), "Send email: "+email);
								}
							}
						}
					}
				}
				r.setMessagecode("cs200");
				LogAgent.saveLog(r);
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Could not update the database");
				LogAgent.saveLog(r);
			}
			db.clear();
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Please make sure that an activity has been selected and at least one field is edited");
			r.setPercentcomplete(99);
			LogAgent.saveLog(r);
		}
		return r;
	}

	public static SubObjVO[] status(RequestVO vo) {
		return status(vo.getId());
	}

	public static SubObjVO[] status(String actids) {
		SubObjVO[] r = new SubObjVO[0];
		if (Operator.hasValue(actids)) {
			String command = ActivitiesSQL.status(CsTools.paramToString(actids));
			r = Choices.getChoices(command);
		}
		return r;
	}

	public static ObjVO psearch(String query, int page, int max, Token u) {
		ObjVO r = new ObjVO();
		if (Operator.hasValue(query)) {
			String command = ActivitiesSQL.psearch(query, page, max, u);
			r = Choices.getObj(command);
		}
		return r;
	}




}




