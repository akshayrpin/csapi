package csapi.impl.parking;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONException;
import org.json.JSONObject;

import sun.security.action.GetLongAction;
import alain.core.db.Sage;
import alain.core.security.DatasourceInfo;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.common.LkupCache;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.attachments.AttachmentsSQL;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.email.EmailImpl;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralSQL;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.people.PeopleAgent;
import csapi.impl.people.PeopleSQL;
import csapi.impl.print.BatchRunnable;
import csapi.impl.project.ProjectAgent;
import csapi.impl.project.ProjectSQL;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.DbUtil;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Types;
import csshared.vo.DataVO;
import csshared.vo.DivisionsList;
import csshared.vo.DivisionsVO;
import csshared.vo.HoldsList;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjGroupVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.lkup.RolesVO;

public class ParkingAgent {

	public static ResponseVO search(RequestVO r) {
		return search(r, true, true, true);
	}

	public static ResponseVO search(RequestVO r, boolean info, boolean exemption, boolean permit) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		ResponseVO vo = new ResponseVO();
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO t = new TypeVO();
		try {

			t.setEntity(r.getEntity());
			t.setType(r.getType());

//			String search = r.getSearch();

			HashMap<String, String> s = r.getExtras();
//			String strno = s.get("strno");
//			String strnameid = s.get("strname");

			Sage db = new Sage();
			String command = ParkingSQL.searchLSO(s);
			db.query(command);
			if(db.next()){
				vo.setMessagecode("-1");
				vo.setId(db.getInt("ID"));
//				t.setHold(GeneralAgent.getAlert("lso", vo.getId()));
			}
			
			command = ParkingSQL.search(s, r.getReference());
			db.query(command);

			HashMap<Integer, String> hit = new HashMap<Integer, String>();
			int count = 0;
			StringBuilder sb = new StringBuilder();
			while (db.next()) {
				int projectId = db.getInt("PROJECT_ID");
				String hitstr = db.getString("HIT");
				hit.put(projectId, hitstr);
				if (count > 0) { sb.append(" , "); }
				sb.append(projectId);
				count++;

			}
			String ids = sb.toString();

			if (count == 0) {
				vo.setMessagecode("No Results Found");
				t.setTitle("N");
				vo.setMessagecode("-1");
			}
			else if (count > 1) {
				db.query(ParkingSQL.getParkingProjects(ids, null));

				ga = new ObjGroupVO[db.size()];
				int c = 0;

				while (db.next()) {
					ObjGroupVO g = new ObjGroupVO();
					g.setGroup("accounts");
					int projectid = db.getInt("ID");

					HashMap<String, String> extras = new HashMap<String, String>();
					extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
					extras.put("STATUS", db.getString("LKUP_PROJECT_STATUS_TEXT"));
					extras.put("EXPIRED", db.getString("EXPIRED"));
					extras.put("TYPE", db.getString("LKUP_PROJECT_TYPE_TEXT"));
					extras.put("PROJECT_NBR", db.getString("PROJECT_NBR"));
					extras.put("ID", db.getString("ID"));
					extras.put("NO_SPACES", db.getString("NO_SPACES"));
					extras.put("NO_CARS", db.getString("NO_CARS"));
					extras.put("APPROVED_SPACE", db.getString("APPROVED_SPACE"));
					extras.put("ADDRESS", db.getString("ADDRESS"));
					extras.put("CREATED_DATE", db.getString("P_CREATED_DATE"));
					extras.put("UPDATED_DATE", db.getString("P_UPDATED_DATE"));
					extras.put("CREATED", db.getString("CREATED"));
					extras.put("UPDATED", db.getString("UPDATED"));
					String hitstr = hit.get(projectid);
					if (!Operator.hasValue(hitstr)) { hitstr = ""; }
					extras.put("HIT", hitstr);
					g.setExtras(extras);
					ga[c] = g;

					c++;

				}
				vo.setMessagecode("2");
				t.setTitle("accounts");
			}
			else if (count == 1) {
				t = Types.getType("project", Operator.toInt(ids), "parking", "", u);
				if (info && exemption && permit) {
					ga = getParking(ids);
				}

				if (exemption && !info && !permit) {
					ga = getExemptions(ids);
				}

				if (!exemption && !info && permit) {
					ga = getParkingPermits(ids, false);
				}

				if (!exemption && info && !permit) {
					ga = getParkingInfo(ids);
				}

				String alert = HoldsAgent.getAlert("project", Operator.toInt(ids));
				t.setHold(alert);
				t.setTitle("info");
				vo.setMessagecode("1");
			}

			db.clear();

			t.setGroups(ga);
			vo.setType(t);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	public static ObjGroupVO[] getParking(String ids) throws ParseException {
		ObjGroupVO[] ga = getExemptions(ids);
		int ce = ga.length;

		ObjGroupVO[] gap = getParkingPermits(ids, false);
		int cp = gap.length;

		ObjGroupVO[] gai = getParkingInfo(ids);
		int ci = gai.length;

		ObjGroupVO[] gapall = getParkingPermits(ids, true);
		int cpall = gapall.length;

		ObjGroupVO[] sv = new ObjGroupVO[ce + cp + ci + cpall];// (ArrayUtils.addAll(cartcurrent.getStatements(),
																// cartsession.getStatements()));

		int c = 0;

		for (int i = 0; i < gai.length; i++) {
			sv[c] = gai[i];
			c = i;
		}
		c = c + 1;
		for (int i = 0; i < ga.length; i++) {
			sv[c] = ga[i];
			c++;
		}
		// c = c+1;
		for (int i = 0; i < gap.length; i++) {
			sv[c] = gap[i];
			c++;
		}
		for (int i = 0; i < gapall.length; i++) {
			sv[c] = gapall[i];
			c++;
		}

		return sv;

	}

	public static ObjGroupVO[] getExemptions(String ids) throws ParseException {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		Sage db = new Sage();

		String command = ParkingSQL.getParkingExemptions(ids);// "select  A.*,LAT.DESCRIPTION as TYPE from activity A join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  where project_id in  ("+ids+")  and LAT.ISDOT='Y' and LAT.DOT_EXEMPTION='Y' order by CREATED_DATE DESC ";
		db.query(command);

		ga = new ObjGroupVO[db.size()];
		int c = 0;
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date dt = new Date();

		while (db.next()) {
			ObjGroupVO g = new ObjGroupVO();
			g.setGroup("exemptions");
			HashMap<String, String> extras = new HashMap<String, String>();
			extras.put("ACT_NBR", db.getString("ACT_NBR"));
			extras.put("TYPE", db.getString("TYPE"));
			extras.put("START_DATE", db.getString("START_DATE"));
			extras.put("EXP_DATE", db.getString("EXP_DATE"));
			extras.put("ID", db.getString("ID"));
			extras.put("PROJECT_ID", db.getString("PROJECT_ID"));

			extras.put("CREATED_DATE", db.getString("A_CREATED_DATE"));
			extras.put("UPDATED_DATE", db.getString("A_UPDATED_DATE"));
			extras.put("CREATED", db.getString("CREATED"));
			extras.put("UPDATED", db.getString("UPDATED"));
			dt = dateFormat.parse(dateFormat.format(dt));
			try {
				Date st = dateFormat.parse(db.getString("START_DATE"));
				Date et = dateFormat.parse(db.getString("EXP_DATE"));
				if(dt.equals(st) && dt.equals(et)){
					extras.put("QTY", "1");
				}
			}
			catch (Exception e) { extras.put("QTY", "1"); }
			
			g.setExtras(extras);
			ga[c] = g;
			c++;
		}

		db.clear();

		return ga;

	}

	public static ObjGroupVO[] getParkingPermits(String ids, boolean all) {

		String startdate = "";
		String enddate = "";
		try {
			if (all) {
				JSONObject d = GeneralAgent.getConfiguration("DOT");
				startdate = getParkingDate(d.getString("START_DATE"));
				enddate = getParkingDate(d.getString("EXP_DATE"));
			}
		} catch (JSONException e) {
			Logger.error(e.getMessage());
		}

		return getParkingPermits(ids, startdate, enddate);
	}

	public static ObjGroupVO[] getParkingPermits(String ids, String startdate,
			String enddate) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		Sage db = new Sage();

		String command = ParkingSQL.getParkingPermits(ids, startdate, enddate);// "select  A.*,LAT.DESCRIPTION as TYPE from activity A join LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID=LAT.ID  where project_id in  ("+ids+") and LAT.ISDOT='Y' and LAT.DOT_EXEMPTION='N'  order by CREATED_DATE DESC ";
		db.query(command);

		ga = new ObjGroupVO[db.size()];
		int c = 0;

		while (db.next()) {
			ObjGroupVO g = new ObjGroupVO();
			if (Operator.hasValue(startdate) && Operator.hasValue(enddate)) {
				g.setGroup("permits");
			} else {
				g.setGroup("permitsall");
			}
			HashMap<String, String> extras = new HashMap<String, String>();
			extras.put("ACT_NBR", db.getString("ACT_NBR"));
			extras.put("TYPE", db.getString("TYPE"));
			extras.put("START_DATE", db.getString("START_DATE"));
			extras.put("EXP_DATE", db.getString("EXP_DATE"));
			extras.put("ID", db.getString("ID"));
			extras.put("PROJECT_ID", db.getString("PROJECT_ID"));
			extras.put("AMOUNT", db.getString("AMOUNT"));
			extras.put("PAID", db.getString("PAID"));
			extras.put("BALANCE", db.getString("BALANCE"));
			extras.put("EXPIRED", db.getString("EXPIRED"));
			extras.put("PRINTED", db.getString("PRINTED"));
			extras.put("DOT_STICKERS", db.getString("DOT_STICKERS"));
			extras.put("STATUS", db.getString("STATUS"));
			extras.put("CREATED_DATE", db.getString("CREATED_DATE"));
			extras.put("UPDATED_DATE", db.getString("UPDATED_DATE"));
			extras.put("CREATED", db.getString("CREATED"));
			extras.put("UPDATED", db.getString("UPDATED"));

			if (db.getDouble("BALANCE") > 0) {
				extras.put("PRINT", "N");
			} else {
				extras.put("PRINT", "Y");
			}

			g.setExtras(extras);
			ga[c] = g;
			c++;
		}

		db.clear();

		return ga;

	}

	public static ObjGroupVO[] getParkingInfo(String ids) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		Sage db = new Sage();

		try {
			String command = ParkingSQL.getParkingInfo(ids);
			db.query(command);

			ga = new ObjGroupVO[db.size()];
			int c = 0;
			if (db.size() > 0) {
				ObjGroupVO g = new ObjGroupVO();
				HashMap<String, String> extras = new HashMap<String, String>();
				if (db.next()) {
					g.setGroup("info");
					extras.put("NAME", db.getString("NAME"));
					extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
					extras.put("PROJECT_NBR", db.getString("PROJECT_NBR"));
					extras.put("ADDRESS", db.getString("ADDRESS"));
					extras.put("EMAIL", db.getString("EMAIL"));
					extras.put("PHONE_WORK", db.getString("PHONE_WORK"));
					extras.put("PHONE_CELL", db.getString("PHONE_CELL"));
					extras.put("PHONE_HOME", db.getString("PHONE_HOME"));
					extras.put("TYPE", db.getString("TYPE"));
					extras.put("NO_SPACES", db.getString("NO_SPACES"));
					extras.put("NO_CARS", db.getString("NO_CARS"));
					extras.put("APPROVED_SPACE", db.getString("APPROVED_SPACE"));
					extras.put("SECRET", db.getString("SECRET"));
					extras.put("PROJECT_ID", db.getString("PROJECT_ID"));
					extras.put("STATUS", db.getString("STATUS"));
					extras.put("EXPIRED", db.getString("EXPIRED"));
					extras.put("LSO_ID", db.getString("LSO_ID"));
				}

				HoldsList holds = HoldsAgent.getHolds("project", Operator.toInt(ids));
				String[] ah = holds.getSignificantHolds();
				String ahs = Operator.join(ah, ", ");
				extras.put("HOLDS", ahs);

				StringBuilder sb = new StringBuilder();
				DivisionsList dl = DivisionsAgent.getDivisions("project", Operator.toInt(ids));
				DivisionsList dotl = dl.dot();
				boolean empty = true;
				while (dotl.next()) {
					DivisionsVO dvo = dotl.getDivision();
					String field = dvo.getDivisiontype();
					String value = dvo.getDivision();
					String info = dvo.getInfo();
					if (!empty) { sb.append(","); }
					sb.append(field);
					extras.put(field, value);
					extras.put("INFO_"+field, info);
					empty = false;
				}
				extras.put("DIVISIONS", sb.toString());
				g.setExtras(extras);

//				command = ParkingSQL.getParkingZones(ids);
//				db.query(command);
//				boolean empty = true;
//				while (db.next()) {
//					String field = db.getString("FIELD");
//					String value = db.getString("VALUE").trim();
//					String info = db.getString("INFO");
//					if (!empty) { sb.append(","); }
//					sb.append(field);
//					extras.put(field, value);
//					extras.put("INFO_"+field, info);
//					empty = false;
//				}
//				extras.put("DIVISIONS", sb.toString());
//				g.setExtras(extras);

				command = ParkingSQL.getParkingPeople(ids);
				db.query(command);

				if (db.size() > 0) {
					ArrayList<String> usersl = new ArrayList<String>();
					while (db.next()) {
						usersl.add(db.getString("USERNAME"));
					}
					String[] users = usersl.toArray(new String[usersl.size()]);
					HashMap<String, String> accounts = DatasourceInfo.hasAccount(users);
		
					if (db.beforeFirst()) {
						ArrayList<HashMap<String, String>> ppl = new ArrayList<HashMap<String, String>>();
						while (db.next()) {
							String username = db.getString("USERNAME");
							HashMap<String, String> pplh = new HashMap<String, String>();
							if (Operator.hasValue(accounts.get(username.toLowerCase()))) {
								pplh.put("HASACCOUNT", "Y");
							}
							else {
								pplh.put("HASACCOUNT", "N");
							}
							pplh.put("REF_USERS_ID", db.getString("REF_USERS_ID"));
							pplh.put("NAME", db.getString("NAME"));
							pplh.put("EMAIL", db.getString("EMAIL"));
							pplh.put("PHONE_WORK", db.getString("PHONE_WORK"));
							pplh.put("PHONE_CELL", db.getString("PHONE_CELL"));
							pplh.put("PHONE_HOME", db.getString("PHONE_HOME"));
							pplh.put("TYPE", db.getString("TYPE"));
							ppl.add(pplh);
						}
						g.setExtraslist(ppl);
					}
				}

				ga[c] = g;

			}
		}
		catch (Exception e) { Logger.error(e); }
		db.clear();

		return ga;

	}

	public static String getSystemGeneratedAct_nbr(RequestVO r) {
		return GeneralAgent.getNumber(r);
	}

	public static String getSystemGeneratedProject_id(RequestVO r) {
		return Operator.toString(r.getTypeid());
	}

	public static ResponseVO getParkingAccounts(RequestVO r, int userId) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		ResponseVO vo = new ResponseVO();
		TypeVO t = new TypeVO();
		try {

			t.setEntity(r.getEntity());
			t.setType(r.getType());

			Sage db = new Sage();
			String command = ParkingSQL.search(userId);
			db.query(command);

			int count = 0;
			StringBuilder sb = new StringBuilder();
			while (db.next()) {
				int projectId = db.getInt("PROJECT_ID");
				if(projectId> 0){
					sb.append(projectId).append(",");
					count++;
				}
				t.setTypeid(db.getInt("ID")); // REF_USER_ID
			}

			if (count > 0) {
				sb.append(0);
				db.query(ParkingSQL.getParkingProjects(sb.toString(), Operator.toString(userId)));
			}
			if (count == 0) {
				t.setTitle("N");
				vo.setMessagecode("cs401");
				MessageVO m = new MessageVO();
				m.setMessage("We're sorry. We don't have information about your parking account.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				vo.setErrors(mv);
				db.clear();
				return vo;
			} //else {

			int c = 0;
			if(Operator.hasValue(r.getGroup())){
				String table = "REF_ACCOUNT_APPLICATION RUO";
				List<String> select = new ArrayList<String>();
				select.add("RUO.ID");
				select.add("RUO.ACCOUNT_NO");
				select.add("RUO.APPROVAL");
				select.add("RUO.LKUP_USERS_TYPE_ID");
				select.add("RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID");
				select.add("LAA.STATUS");
				select.add("LUT.TYPE");
				select.add("RUO.RESIDE");
				select.add("RUO.SPACE_AVAIL");
				select.add("RUO.NO_CARS");
				select.add("RUO.COMMENT");
				select.add("RUO.EXISTING_ACT");
				select.add("VA.LSO_STREET_ID");
				select.add("VA.STR_NO");
				select.add("VA.PRE_DIR");
				select.add("VA.STR_NAME");
				select.add("VA.STR_TYPE");
				select.add("VA.SUF_DIR");
				select.add("VA.UNIT");
				select.add("VA.LSO_ID");
				select.add("VA.LSO_STREET_ID");
				select.add("VA.STR_MOD");

				List<String> join = new ArrayList<String>();
				join.add("JOIN V_CENTRAL_ADDRESS VA ON RUO.LSO_ID = VA.LSO_ID");
				join.add("JOIN LKUP_USERS_TYPE LUT ON RUO.LKUP_USERS_TYPE_ID = LUT.ID");
				join.add("JOIN LKUP_ACCOUNT_APPLICATION_STATUS LAA ON RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID = LAA.ID");

				Map<String, String> where = new HashMap<String, String>();
				where.put("USERS_ID", Operator.toString(userId));
				where.put("APPROVAL", "Y");
				
				command = DbUtil.buildQuery(table, where, select, join);

				Sage db1 = new Sage();
				db1.query(command);
				ga = new ObjGroupVO[db.size()+db1.size()];
				
				while (db1.next()) {
					ObjGroupVO g = new ObjGroupVO();
					g.setGroup("accounts");

					HashMap<String, String> extras = new HashMap<String, String>();
					String comment = db1.getString("COMMENT");
					extras.put("ID", db1.getString("ID"));
					extras.put("ACCOUNT_NO", db1.getString("ACCOUNT_NO"));
					extras.put("ASSOCIATION", db1.getString("LKUP_USERS_TYPE_ID"));
					extras.put("LKUP_USERS_TYPE_ID", db1.getString("LKUP_USERS_TYPE_ID"));
					extras.put("RESIDE", db1.getString("RESIDE"));
					extras.put("SPACE_AVAIL", db1.getString("SPACE_AVAIL"));
					extras.put("NO_CARS", db1.getString("NO_CARS"));
					extras.put("STR_NO", db1.getString("STR_NO"));
					extras.put("UNIT", db1.getString("UNIT"));
					extras.put("LSO_STREET_ID", db1.getString("LSO_STREET_ID"));
					extras.put("STR_MOD", db1.getString("STR_MOD"));
					extras.put("ADDRESS", db1.getString("STR_NO")+" "+db1.getString("PRE_DIR")+ " " + db1.getString("STR_MOD") +" "+db1.getString("STR_NAME")+" "+db1.getString("STR_TYPE")+" "+db1.getString("SUF_DIR")+" "+db1.getString("UNIT"));
					extras.put("LSO_ID", db1.getString("LSO_ID"));
					extras.put("PRIMARY_CONTACT", db1.getString("TYPE").equalsIgnoreCase("Primary Resident")?"Y":"N");
					extras.put("APPROVED", Operator.hasValue(comment)?comment:"N");
					extras.put("COMMENT", comment);
					extras.put("LKUP_ACCOUNT_APPLICATION_STATUS_ID", db1.getString("LKUP_ACCOUNT_APPLICATION_STATUS_ID"));
					extras.put("STATUS", db1.getString("STATUS"));
					extras.put("EXISTING_ACT", db1.getString("EXISTING_ACT"));
					g.setExtras(extras);
					ga[c] = g;

					c++;

				}
				db1.clear();
			} else{
				ga = new ObjGroupVO[db.size()];
			}
			if (count > 0)
				while (db.next()) {
					ObjGroupVO g = new ObjGroupVO();
					g.setGroup("accounts");
	
					HashMap<String, String> extras = new HashMap<String, String>();
					extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
					extras.put("STATUS", db.getString("LKUP_PROJECT_STATUS_TEXT"));
					extras.put("TYPE", db.getString("LKUP_PROJECT_TYPE_TEXT"));
					extras.put("PROJECT_NBR", db.getString("PROJECT_NBR"));
					extras.put("ID", db.getString("ID"));
					extras.put("ADDRESS", db.getString("ADDRESS"));
					extras.put("PRIMARY_CONTACT", db.getString("PRIMARY_CONTACT"));
					extras.put("COMMENT", "");
					extras.put("EXPIRED", db.getString("EXPIRED"));
					extras.put("APPROVED", "Y");
					g.setExtras(extras);
					ga[c] = g;
	
					c++;
	
				}
			vo.setMessagecode("cs200");
			t.setTitle("accounts");
		//}

			db.clear();

			t.setGroups(ga);

			vo.setType(t);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	public static SubObjVO[] listLastYearTypes(RequestVO r) {
		return listLastYearTypes(r, false);
	}

	public static SubObjVO[] listLastYearTypes(RequestVO r, boolean current) {
		SubObjVO[] a = new SubObjVO[0];
		try {
			JSONObject d = GeneralAgent.getConfiguration("DOT");
			String startdate = getParkingDate(d.getString("START_DATE"));
			String enddate = getParkingDate(d.getString("EXP_DATE"));
			Sage db = new Sage();
			db.query(ParkingSQL.getCount(Operator.toString(r.getTypeid()), startdate, enddate, current));
			if (db.size() > 0) {
				int count = 0;
				a = new SubObjVO[db.size()];
				while (db.next()) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("ID"));
					vo.setText(db.getString("TYPE"));
					HashMap<String, String> addldata = new HashMap<String, String>();
					addldata.put("count", db.getString("COUNT"));
					addldata.put("maxallowed", db.getString("DURATION_MAX"));
					addldata.put("isdot", db.getString("ISDOT"));
					addldata.put("dot_exemption", db.getString("DOT_EXEMPTION"));
					addldata.put("configuration_group_id", db.getString("CONFIGURATION_GROUP_ID"));
					addldata.put("c_value", db.getString("C_VALUE"));
					addldata.put("lkup_act_type_id", db.getString("LKUP_ACT_TYPE_ID"));
					addldata.put("conf_max_allowed", db.getString("CONF_DURATION_MAX"));
					vo.setAddldata(addldata);

					a[count] = vo;
					count++;
				}

			}

			db.clear();

		} catch (Exception e) {
			Logger.error(e);
		}
		return a;
	}

	/*public static SubObjVO[] listCurrentMonthTypes(RequestVO r, boolean current) {
		SubObjVO[] a = new SubObjVO[0];
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			SimpleDateFormat formatter1 = new SimpleDateFormat("MM");
			String currentYear = formatter.format(cal.getTime());
			String currentMonth = formatter1.format(cal.getTime());

			String startdate = getParkingDate(currentMonth + "/01/"
					+ currentYear);
			String enddate = getParkingDate(new Timekeeper().toString());
			Sage db = new Sage();
			db.query(ParkingSQL.getParkingPermitsCount(
					String.valueOf(r.getTypeid()), startdate, enddate, current));
			if (db.size() > 0) {
				int count = 0;
				a = new SubObjVO[db.size()];
				while (db.next()) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("ID"));
					vo.setText(db.getString("TYPE"));
					HashMap<String, String> addldata = new HashMap<String, String>();
					addldata.put("count", db.getString("COUNT"));
					addldata.put("maxallowed", db.getString("MAX_ALLOWED"));
					addldata.put("isdot", db.getString("ISDOT"));
					addldata.put("dot_exemption", db.getString("DOT_EXEMPTION"));
					vo.setAddldata(addldata);

					a[count] = vo;
					count++;
				}

			}

			db.clear();

		} catch (Exception e) {
			Logger.error(e);
		}
		return a;
	}*/

	public static String getParkingDate(String date) {
		return getParkingDate(date,false);
	}
	
	public static String getParkingDate(String date,boolean renewal) {
		Timekeeper dt = new Timekeeper();
		String m = Operator.subString(date, 0, 2);
		String d = Operator.subString(date, 3, 5);
		String y = Operator.subString(date, 6, date.length());
		dt.setMonth(m);
		dt.setDay(d);
		dt.setYear(y);
		
		if(renewal){
			if (y.equalsIgnoreCase("[autostyear]")) {
				Timekeeper s = new Timekeeper();
				// s.addMonth(7);
				if (s.MONTH() < dt.MONTH()+1) {
					dt.addYear(-1);
				}
			}
			if (y.equalsIgnoreCase("[autoedyear]")) {
				Timekeeper s = new Timekeeper();
				// s.addMonth(-5);
				if (s.MONTH() > dt.MONTH()+1) {
					dt.addYear(1);
				}
			}
		} else {
			if (y.equalsIgnoreCase("[autostyear]")) {
				Timekeeper s = new Timekeeper();
				
				//if (s.MONTH() < dt.MONTH()) {
					dt.addYear(-1);
				//}
					
				if(s.YEAR()>2021){
					dt.addYear(-1);
				}
			}
			if (y.equalsIgnoreCase("[autoedyear]")) {
				Timekeeper s = new Timekeeper();
				// s.addMonth(-5);
				 if (s.MONTH() > dt.MONTH()) {
					dt.addYear(1);
				 }	
			}
		}
		
		Logger.info("SUNIL_________>"+dt.getString("YYYY/MM/DD"));

		return dt.getString("YYYY/MM/DD");
	}

	public static ResponseVO checkAlert(RequestVO vo, Token u, ResponseVO r) {
		DataVO d = DataVO.toDataVO(vo);
		String type = d.get("LKUP_ACT_TYPE_ID");
		HoldsList h = HoldsAgent.getHolds(vo.getType(), vo.getTypeid(), Operator.toInt(type));

		// holds
		if (h.actOnSignificantHold(type)) {
			MessageVO m = new MessageVO();
			m.setMessage("Unable to complete request. A hold has been placed on the account.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}

	public static ResponseVO checkValidTime(RequestVO vo, Token u, ResponseVO r) {
		Calendar calendar = Calendar.getInstance();
		int hour24 = calendar.get(Calendar.HOUR_OF_DAY); // 0..23
		Timekeeper k = new Timekeeper();
		Timekeeper h2 = new Timekeeper();
		h2.setHour(2);
		h2.setMinute(25);
		
		Timekeeper h5 = new Timekeeper();
		h5.setHour(5);
		h5.setMinute(0);
		Logger.info(hour24+"SSSSSSSSSSSSSSSSSSSSSSSSSSUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
		
		if(k.greaterThanOrEqualTo(h2) && k.lessThanOrEqualTo(h5)){
			Logger.info(true+"SSSSSSSSSSSSSSSSSSSSSSSSSSUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
		//}
		
		//if (hour24 > 2 && hour24 < 5) { // disabled as it doesn't work on 6/4/21

			MessageVO m = new MessageVO();
			m.setMessage("Parking Exemptions are only Issued beteween 5 a.m. - 2 a.m., Daily");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		return r;
	}

	public static ResponseVO checkAccountandZone(RequestVO vo, Token u, ResponseVO r) {
		String pzone = "";
		String rzone = "";

		ObjGroupVO[] za = getParkingInfo(Operator.toString(vo.getTypeid()));

		if (za.length > 0) {
			ObjGroupVO z = za[0];
			pzone = z.getExtras().get("PARKING_ZONE");
			rzone = z.getExtras().get("R_ZONE");
			if (z.getExtras().get("EXPIRED").equals("Y")) {
				MessageVO m = new MessageVO();
				m.setMessage(" Cannot create the requested process as the account is deactivated.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
				return r;
			}

			// Only for Daytime Exemptions
			/*String val = vo.getData()[0].getObj()[0].getText();
			ObjGroupVO[] dte = ListAllOnlinePermits(Operator.toString(vo.getTypeid()), "Y", "Daytime Exemption");
			
			if (dte.length >0  && Operator.hasValue(val) && Operator.equalsIgnoreCase(val, dte[0].getExtras().get("id"))) {

				ObjGroupVO[] zones = getZoneList("R_ZONE");
				for (ObjGroupVO zone : zones) {
					if (Operator.hasValue(rzone) && Operator.equalsIgnoreCase(rzone, zone.getExtras().get("value"))) {

						if (!(Operator.equalsIgnoreCase(pzone, "na") || Operator.equalsIgnoreCase(pzone, "none"))) {

							r = checkPrefentialCount(vo, u, r);
						}
					}
				}
			}*/
		}
		return r;
	}

	/*public static ResponseVO checkPrefentialCount(RequestVO vo, Token u, ResponseVO r) {

		// Checking PrefentialCount
		SubObjVO[] sv = listLastYearTypes(vo);

		for (int i = 0; i < sv.length; i++) {
			if (sv[i].getText().equalsIgnoreCase("Preferential Parking")) { 

				int count = Operator.toInt(sv[i].getAddldata().get("count"));
				Logger.info(count + "*******");
				
				 * check for number of PPP purchased. if count of PPP is less
				 * than 3 then
				 

				if (count < 3) {
					r = checkCurrentMonthCount(vo, u, r);
				}
			}
		}
		return r;
	}

	private static ResponseVO checkCurrentMonthCount(RequestVO vo, Token u,
			ResponseVO r) {

		SubObjVO[] sObj = listCurrentMonthTypes(vo, true);
		int countDaytime = 0;

		ObjVO vObj = Fields.getField(vo.data[0], "LKUP_ACT_TYPE_ID");
		for (int j = 0; j < sObj.length; j++) {
			int id = sObj[j].getId();
			boolean exemption = false;// Operator.s2b(sv[i].getAddldata().get("dot_exemption"));
			String type = sObj[j].getText();
			Logger.info("exemption" + exemption + "###type" + type);

			if (Operator.toInt(vObj.getValue()) == id) {
				countDaytime = Operator.toInt(sObj[j].getAddldata()
						.get("count"));
			}
		}
		
		 * check the number of Daytime applied, Maximum of 5 Daytime Exemptions
		 * can be applied.
		 
		if (countDaytime > 4) {
			MessageVO m = new MessageVO();
			m.setMessage(" You will not be able to request any more, as it has exceed the allowed limit per month.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}*/

	public static ResponseVO checkMaxAllowed(RequestVO vo, Token u, ResponseVO r) {
		String c = vo.getExtras().get("NO_OF_VEHICLES");
		int size = Operator.toInt(c);

		SubObjVO[] sv = listLastYearTypes(vo);

		ObjVO v = Fields.getField(vo.data[0], "LKUP_ACT_TYPE_ID");
		for (int i = 0; i < sv.length; i++) {
			int id = sv[i].getId();

			if (Operator.toInt(v.getValue()) == id) {
				int count = Operator.toInt(sv[i].getAddldata().get("count"));

				count = count + size;
				int maxallowed = Operator.toInt(sv[i].getAddldata().get("maxallowed"));

				if (count > maxallowed) {
					MessageVO m = new MessageVO();
					m.setMessage("You will not be able to request, as you have exceded the limit.");
					ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
					mv.add(m);
					r.setErrors(mv);
					r.setMessagecode("cs412");
					break;
				}
			}
		}
		return r;
	}

	public static TypeVO getResponseType(RequestVO vo, Token u, int size, String ids) {
		TypeVO result = Types.getMyType(vo.getEntity(), vo.getGrouptype(), u.getUsername());

		ObjVO v = Fields.getField(vo.data[0], "LKUP_ACT_TYPE_ID");
		String command = ActivitySQL.getActivities(ids);
		ObjGroupVO fields = CsReflect.getUiFields("activity", "activity", "my");

//		Sage db = new Sage();
//		db.query(command);
//		sb = new StringBuilder();
//		while (db.next()) {
//			sb.append(db.getInt("ID"));
//			sb.append(",");
//
//		}
//
//		sb.append("0");
//		db.clear();

		command = ActivitySQL.getDetails(ids);
		ObjGroupVO g = Group.horizontal(fields, command);
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);

		return result;

	}

	public static ResponseVO saveVehicle(RequestVO vo, Token u, ResponseVO r,
			int actid) {
		boolean result = false;
		try {
			int id = Operator.toInt(vo.getId());

			if (vo.getData()[0].getCustom().length > 0) {

				SubObjGroupVO sa[] = vo.getData()[0].getCustom();
				int customsize = vo.getData()[0].getCustomsize();
				for (int j = 0; j < sa.length; j++) {
					SubObjGroupVO s = sa[j];
					ObjVO va[] = s.getObj();
					StringBuilder t = new StringBuilder(
							"INSERT INTO VEHICLE (REF_PROJECT_PARKING_ID, ACTIVITY_ID, ");
					StringBuilder v = new StringBuilder("VALUES ( ");

					v.append(vo.getReference());
					v.append(",");
					v.append(actid);
					v.append(",");
					boolean q = true;
					for (int i = 0; i < va.length; i++) {
						ObjVO f = va[i];
						String fieldid = f.getFieldid();
						String value = f.getValue();
						boolean req = f.isRequired();

						if (Operator.equalsIgnoreCase(fieldid, "LICENSE_PLATE")
								&& !Operator.hasValue(value)) {
							q = false;
						} else if (Operator.equalsIgnoreCase(fieldid,
								"LICENSE_PLATE") && Operator.hasValue(value)) {

							if (checkBlockedVehicle(value)) {
								q = false;
								MessageVO m = new MessageVO();
								m.setMessage("You will not be able to request, as you have exceded the limit.");
								ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
								mv.add(m);
								r.setErrors(mv);
								r.setMessagecode("cs412");
							}

						}
						if (!Operator.hasValue(value)) {
							value = "";
						}
						t.append(fieldid).append(", ");
						v.append("'").append(Operator.sqlEscape(value))
								.append("'");
						v.append(",");

					}
					t.append("CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) ");
					v.append(u.getId());
					v.append(", CURRENT_TIMESTAMP, ");
					v.append(u.getId());
					v.append(", CURRENT_TIMESTAMP )");
					t.append(v);

					if (q) {
						Sage db = new Sage();
						result = db.update(t.toString());
						db.clear();
					}
				}
			}

		} catch (Exception e) {
			Logger.error(e.getMessage());
			MessageVO m = new MessageVO();
			m.setMessage(" Error while saving Vehicle information. : "
					+ e.getMessage());
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		/*if (!result) {
			MessageVO m = new MessageVO();
			m.setMessage(" Error while saving Vehicle information.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}*/

		return r;
	}

	public static boolean checkBlockedVehicle(String licensePlate) {
		boolean checkVehicle = false;

		try {
			Sage db = new Sage();
			String sql = "select upper(license_plate),blocked,updated_date,updated_by from vehicle where LICENSE_PLATE= upper('"
					+ licensePlate + "')";
			db.query(sql);
			if (db.next()) {
				if (db.getString("blocked").equalsIgnoreCase("Y")) {
					checkVehicle = true;
				} else
					checkVehicle = false;
			}

			db.clear();
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return checkVehicle;
	}

	public static String checkOffsitePeople(RequestVO vo) {		
		String flag = "N";
		
		Sage db = new Sage();
		String command = PeopleSQL.getOffsitePeople(vo.getUsername(), vo.getReference());
		db.query(command);
		
		if (db.next()) {
			flag = db.getString("applicant");
			if(flag == null || Operator.equalsIgnoreCase(flag, "N")){
				flag = db.getString("absentee");
			}
		}
		db.clear();
		return flag;
	}

	public static ResponseVO listExemptionTypes(RequestVO vo, Token u) {
		
		ObjVO obj = new ObjVO();
		ObjVO[] objarr = new ObjVO[1];
		ObjGroupVO[] data = null;
		ResponseVO r = new ResponseVO();
		
		//r = checkAlert(vo, u, r);

		r = checkValidTime(vo, u, r);
		
		// get Zone for User and check before listing.
		ObjGroupVO[] pk = getParkingInfo(Operator.toString(vo.getTypeid()));
		String pzone = "";
		if (pk.length > 0) {
			ObjGroupVO z = pk[0];
			pzone = z.getExtras().get("LAND_PARKING_ZONE");

			if (z.getExtras().get("EXPIRED").equals("Y")) {
				MessageVO m = new MessageVO();
				m.setMessage(" Cannot create the requested process as the account is deactivated.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
			}
		}
		
		// getting Permit counts for Current year
		//SubObjVO[] sv = listLastYearTypes(vo);
		/*
		 	checking for configuration add modifying the number of permits provided[maxallowed]
			eg: #PPP count is less to its duration_max, then duration_max for daytime is 5 configured in configuration table 
			else maxallowed for daytime as per Admin configuration 
		*/
		
		/*for (int i = 0; i < sv.length; i++) {
			if(sv[i].getAddldata().get("configuration_group_id")!= null && Operator.toInt(sv[i].getAddldata().get("configuration_group_id")) > 0){
				for (int j = 0; j < sv.length; j++) {
					if(Operator.toInt(sv[i].getAddldata().get("lkup_act_type_id")) == sv[j].getId()){
						if (!Operator.equalsIgnoreCase(pzone, "none") && !sv[i].getText().equalsIgnoreCase("Daytime Exemption")) {
							if(Operator.toInt(sv[j].getAddldata().get("count")) != Operator.toInt(sv[j].getAddldata().get("maxallowed"))){
								String size = sv[i].getAddldata().get("c_value");
								HashMap<String, String> addldata = sv[i].getAddldata();
								addldata.put("maxallowed", size);
								sv[i].setAddldata(addldata);
							}
						}
					}
				}
				
			}
		}*/

// COMMENTED BY ALAIN BECAUSE OF NEW HOLD LOGIC - 6/19/2019
//		String alert1 = GeneralAgent.getAlert("project", vo.getTypeid());
//
//		// holds
//		Logger.info("#####################SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+alert1);
//		if (Operator.hasValue(alert1) && (Operator.equalsIgnoreCase(alert1.trim(), "HOLD_H"))) {
//			MessageVO m = new MessageVO();
//			m.setMessage("You will not be able to request as there is a hold on your account.");
//			m.setField("LKUP_ACT_TYPE_ID");
//			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
//			mv.add(m);
//			r.setErrors(mv);
//			r.setMessagecode("cs412");
//			Logger.highlight("There is a hold on this type");
//			return r;
//		}
//		boolean softhold = false;
//		if (Operator.hasValue(alert1) && (Operator.equalsIgnoreCase(alert1.trim(), "HOLD_S"))) {
//			softhold = true;
//			/*MessageVO m = new MessageVO();
//			m.setMessage("You will not be able to request as there is a hold on your account.");
//			m.setField("LKUP_ACT_TYPE_ID");
//			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
//			mv.add(m);
//			r.setErrors(mv);
//			r.setMessagecode("cs412");
//			Logger.highlight("There is a hold on this type");
//			return r;*/
//		}
		
		
		SubObjVO[] sv = processPermitCount(vo, pzone);	
		if (r.isValid()) {
			data = ListAllOnlinePermits(vo.getTypeid(), true, false);
		}
		
		obj.setChoices(sv);
		objarr[0] = obj;
		if(data == null || data.length <= 0){
			r.getMessages().add(" Permit cannot be applied to your zone, Please contact City of Beverly Hills.");
			/*MessageVO m = new MessageVO();
			m.setMessage(" There are no Exemptions mapped, Please contact City of Beverly Hills.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);*/
			r.setMessagecode("cs412");
			return r;
		}
		data[0].setObj(objarr);
		TypeVO t = new TypeVO();
		t.setStatus(getOnlineStatus("LKUP_ACT_STATUS", "Y"));
		t.setGroups(data);
		r.setType(t);
		return r;
	}
	
	public static SubObjVO[] processPermitCount(RequestVO vo){
		ObjGroupVO[] pk = getParkingInfo(Operator.toString(vo.getTypeid()));
		String pzone = pk[0].getExtras().get("LAND_PARKING_ZONE");
		return processPermitCount(vo, pzone);
	}
	
	public static SubObjVO[] processPermitCount(RequestVO vo, String pzone){
		
		SubObjVO[] sv = listLastYearTypes(vo);
			
		for (int i = 0; i < sv.length; i++) {
			if(sv[i].getAddldata().get("configuration_group_id")!= null && Operator.toInt(sv[i].getAddldata().get("configuration_group_id")) > 0){
				for (int j = 0; j < sv.length; j++) {
					if(Operator.toInt(sv[i].getAddldata().get("lkup_act_type_id")) == sv[j].getId()){
						if (!Operator.equalsIgnoreCase(pzone, "none") && sv[i].getText().equalsIgnoreCase("Daytime Exemption")) {
							if(Operator.toInt(sv[j].getAddldata().get("count")) != Operator.toInt(sv[j].getAddldata().get("maxallowed"))){
								String size = sv[i].getAddldata().get("c_value");
								HashMap<String, String> addldata = sv[i].getAddldata();
								addldata.put("maxallowed", size);
								sv[i].setAddldata(addldata);
							}
						}
					}
				}
				
			}
			if (sv[i].getText().equalsIgnoreCase("Overnight Parking")) {
				int space = Operator.toInt(getApprovedSpace(Operator.toString(vo.getTypeid())));
				sv[i].getAddldata().put("maxallowed", Operator.toString(space)); 
			}
		}
		
		return sv;
	}

	public static ResponseVO listPermitTypes(RequestVO vo, Token u) {

		ObjVO obj = new ObjVO();
		ObjVO[] objarr = new ObjVO[1];
		ObjGroupVO[] data = null;
		ResponseVO r = new ResponseVO();
		String pzone = "";
		r = checkAlert(vo, u, r);

		ObjGroupVO[] pk = getParkingInfo(Operator.toString(vo.getTypeid()));

		if (pk.length > 0) {
			ObjGroupVO z = pk[0];
			pzone = pk[0].getExtras().get("LAND_PARKING_ZONE");
			if (z.getExtras().get("EXPIRED").equals("Y")) {
				MessageVO m = new MessageVO();
				m.setMessage(" Cannot create the requested process as the account is deactivated.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
			}

		}

		String offsitePeople = checkOffsitePeople(vo);
		if(offsitePeople == null || Operator.equalsIgnoreCase("", offsitePeople)) {
			offsitePeople = "N";
		}
		SubObjVO[] sv = null;
		if(Operator.hasValue(vo.getGroup()) && Operator.equalsIgnoreCase(vo.getGroup(), "renewal")){
			sv = renewalCount(vo);
//			sv = listRenewalTypes(vo, u);
			if(sv.length < 1){
				MessageVO m = new MessageVO();
				m.setMessage(" There are no Permits to be Renewed.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
				return r;
			}
		} else {
			/*sv = listLastYearTypes(vo);
			for (int i = 0; i < sv.length; i++) {
				if (sv[i].getText().equalsIgnoreCase("Overnight Parking")) {
					int space = Operator.toInt(getApprovedSpace(vo.getReference()));
					sv[i].getAddldata().put("maxallowed", Operator.toString(space)); 
				}
			}*/
			sv = processPermitCount(vo, pzone);
		}
		data = ListAllOnlinePermits(vo.getTypeid(), false, false);

		obj.setChoices(sv);
		objarr[0] = obj;
		if(data.length <= 0){
			MessageVO m = new MessageVO();
			m.setMessage("Permit cannot be applied to your zone.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			return r;
		}
		data[0].setObj(objarr);
		TypeVO t = new TypeVO();
		t.setGroups(data);
//		t.setStatus(getOnlineStatus("LKUP_ACT_STATUS", "Y"));
		r.setType(t);
		return r;
	}

	public static ObjGroupVO[] ListAllOnlinePermits(int projectid, boolean exemption, boolean istemp){
		DivisionsList l = DivisionsAgent.getDivisions("project", projectid);
		String divids = l.divisionIds();
		ArrayList<ObjGroupVO> vos = new ArrayList<ObjGroupVO>();

		Sage db = new Sage();
		String command = ParkingSQL.getOnlinePermits(projectid, divids, exemption, istemp);
		db.query(command);
		int c = 0;
		String[] ids = new String[db.size()];
		while (db.next()) {
			String acttypeid = db.getString("ID");
			ids[c] = acttypeid;
			c++;
		}
		HoldsList hl = HoldsAgent.getHolds("project", projectid, ids);
		db.beforeFirst();
		while(db.next()){
			ObjGroupVO vo = new ObjGroupVO();
			int id = db.getInt("ID");
// COMMENTED THE if STATEMENT TO INCLUDE onhold VARIABLE IN THE RESPONSE. HOLD WILL BE MANAGED BY FRONT END. REMOVE COMMENT IF HOLD SHOULD BE REMOVED FROM THE RESPONSE PREVENTING UI FROM ACKNOWLEDGING IT.
//			if (!hl.actOnHold(id)) {
				HashMap<String, String> extras = new HashMap<String, String>();	
				extras.put("id", db.getString("ID"));
				extras.put("value", db.getString("VALUE"));
				extras.put("text", db.getString("TEXT"));
				if (hl.actOnSignificantHold(id)) {
					extras.put("onhold", "Y");
				}
				else {
					extras.put("onhold", "N");
				}
				vo.setExtras(extras);
				vos.add(vo);
//			}
		}
		
		db.clear();
		ObjGroupVO[] arr = vos.toArray(new ObjGroupVO[vos.size()]);
		return arr;
	}
	
	public static ObjGroupVO[] getZoneList(String zone) {

		String table = "LKUP_DIVISIONS_TYPE TY ";
		List<String> select = new ArrayList<String>();
		select.add("T.id");
		select.add("TY.description type");
		select.add("T.description value");

		List<String> join = new ArrayList<String>();
		join.add("join LKUP_DIVISIONS T on TY.ID = T.LKUP_DIVISIONS_TYPE_ID");

		Map<String, String> where = new HashMap<String, String>();
		where.put("TY.description", zone);
		
		return DbUtil.getListAsObjGroup(table, where, select, join);
	}
	
	public static ObjGroupVO[] ListAllOnlinePermits(String id, String dotExemption, String exemption, String istemp){
		return ListAllOnlinePermits(id, dotExemption, exemption, istemp, false);
	}
	
	public static ObjGroupVO[] ListAllOnlinePermits(String id, String dotExemption, String exemption, String istemp,boolean softhold){
		DivisionsList l = DivisionsAgent.getDivisions("project", Operator.toInt(id));
		String divids = l.divisionIds();
		
		String table = "PROJECT P ";
		List<String> select = new ArrayList<String>();
		select.add("LAT.id");
		select.add("LAT.id value");
		select.add("LAT.DESCRIPTION as text");
		
		List<String> join = new ArrayList<String>();
		/*join.add("join REF_PROJECT_ACT_TYPE RPAT on P.LKUP_PROJECT_TYPE_ID = RPAT.LKUP_PROJECT_TYPE_ID and RPAT.ACTIVE = 'Y'");
		join.add("join REF_LSO_PROJECT RLP on P.ID = RLP.PROJECT_ID and P.ACTIVE = 'Y' and RLP.ACTIVE ='Y' ");
		//join.add(" JOIN V_CENTRAL_ADDRESS AS V ON RLP.LSO_ID = V.LSO_ID ");
		//join.add("join REF_LSO_DIVISIONS RLT on V.LAND_ID = RLT.LSO_ID and RLT.ACTIVE = 'Y' ");
		//join.add("join REF_ACT_DIVISIONS RAT on RLT.LKUP_DIVISIONS_ID = RAT.LKUP_DIVISIONS_ID and RAT.ACTIVE = 'Y' ");
		join.add("join REF_LSO_DIVISIONS RLT on RLP.LSO_ID = RLT.LSO_ID and RLT.ACTIVE = 'Y' ");
		join.add("join REF_ACT_DIVISIONS RAT on RLT.LKUP_DIVISIONS_ID = RAT.LKUP_DIVISIONS_ID and RAT.ACTIVE = 'Y' AND RAT.LKUP_DIVISIONS_ID IN ("+divids+") ");
		join.add("join LKUP_ACT_TYPE LAT on RAT.LKUP_ACT_TYPE_ID = LAT.ID and LAT.ACTIVE = 'Y'  and RPAT.LKUP_ACT_TYPE_ID = LAT.ID");*/
		
		join.add(" JOIN REF_PROJECT_ACT_TYPE AS PAT ON P.LKUP_PROJECT_TYPE_ID = PAT.LKUP_PROJECT_TYPE_ID ");
		join.add(" JOIN LKUP_ACT_TYPE AS LAT ON PAT.LKUP_ACT_TYPE_ID = LAT.ID ");
		join.add(" JOIN REF_ACT_DIVISIONS AS RAD ON LAT.ID = RAD.LKUP_ACT_TYPE_ID AND RAD.ACTIVE = 'Y' AND RAD.LKUP_DIVISIONS_ID IN ("+divids+")  ");
		
		

		Map<String, String> where = new HashMap<String, String>();
		where.put("P.ID", id);
		where.put("online", "Y");
		where.put("isdot", "Y");
		where.put("istemp", istemp);
		where.put("dot_exemption", dotExemption);
		if(softhold){
			where.put("LAT.ID", "255");
		}
		
		if(exemption != null)
			where.put("LAT.description", exemption);
		
		return DbUtil.getListAsObjGroup(table, where, select, join);
	}
	
	public static String getApprovedSpace(String account){
		
		String table = "REF_PROJECT_PARKING ";
		List<String> select = new ArrayList<String>();
		select.add("APPROVED_SPACE");

		Map<String, String> where = new HashMap<String, String>();
//		where.put("ID", account);
		where.put("PROJECT_ID", account);
		
		return DbUtil.getAsString(table, where, select);
	}
	
	
	public static ResponseVO saveAccount(RequestVO vo, Token u, ResponseVO r) {
		DataVO dvo = DataVO.toDataVO(vo);
		Sage db = new Sage();
		try {
			String table = "";
			List<String> select = new ArrayList<String>();
			List<String> join = new ArrayList<String>();
			Map<String, String> where = new HashMap<String, String>();
			int projectid = 0;
			String lsoid= "";
			String accountno = dvo.getString("ACCOUNT_NUMBER");
			String command = "";
			String usertype = dvo.getString("LKUP_USERS_TYPE_ID");
			String applicationStatus= "";
			
			command = GeneralSQL.getApplicationStatus("Y");
			db.query(command);
			if(db.next())
				applicationStatus = db.getString("ID");
			
			if(vo.getTypeid() > 0){
				command = ParkingSQL.updateOnlineUser(vo.getTypeid(), u.getId(), usertype, dvo.getString("RESIDE"), dvo.getString("SPACE_AVAIL"), dvo.getString("NO_CARS"), applicationStatus, dvo.getString("EXISTING_ACT")); // Update Or Insert
				db.update(command);
				int count = Operator.toInt(dvo.getString("filecounts"));
				for(int i=0;i<count;i++){
					saveFile(vo.getTypeid(), dvo.getString("parkfile_"+i), dvo.getString("parkselect_"+i));
				}
				
				r = parkingApproval(vo);
				projectid = Operator.toInt(r.getType().getGroups()[0].getExtras().get("PROJECT_ID"));
				
				if(projectid > 0){
					command = ParkingSQL.updateProject(Operator.toString(projectid), getOnlineStatus("LKUP_PROJECT_STATUS", "Y"));
					db.update(command);
				}
				
				CsDeleteCache.deleteProjectCache("project", projectid, "parking");
				
				db.clear();
				r.setMessagecode("cs200");
				r.setRedirect(true);
				return r;
			}
			
			if(Operator.hasValue(accountno) && Operator.toInt(accountno) > 0){
				table = "REF_PROJECT_PARKING RAP ";
				select.add("RAP.PROJECT_ID");
				select.add("LSO_ID");
				join.add("JOIN REF_LSO_PROJECT RLP on RAP.PROJECT_ID = RLP.PROJECT_ID");
				where.put("RAP.ID", accountno);
				ObjGroupVO[] objgrp = DbUtil.getListAsObjGroup(table, where, select, join);
				
				if(objgrp.length > 0){
					projectid = Operator.toInt(objgrp[0].getExtras().get("PROJECT_ID"));
					lsoid = objgrp[0].getExtras().get("LSO_ID");
				}
				if(projectid < 1){
					throw new Exception("Invalid Parking Account Number");
				}
			}else {
				String number = dvo.getString("STREET_NUMBER");
				String name = dvo.getString("STREET_ID");
				String unit = dvo.getString("UNIT");
				String fraction = dvo.getString("NEWSTREETFRACTION");
				
				
				

				String level = "O";
				if (usertype.equals(getOffsiteType())) {
					level = "L";
				}

				command = ParkingSQL.getLSO(Operator.toInt(number), Operator.toInt(name), fraction, unit, level);
				if (db.query(command) && db.next()) {
					lsoid = db.getString("LSO_ID");
				}

				if(!Operator.hasValue(lsoid)){
					command = ParkingSQL.getLSO(Operator.toInt(number), Operator.toInt(name), fraction, unit, "L");
					if (db.query(command) && db.next()) {
						lsoid = db.getString("LSO_ID");
					}
					
					if(!Operator.hasValue(lsoid)) {
						throw new Exception("Given Address is Invalid");
					}
				}
				
				projectid = getParkingProject(Operator.toInt(lsoid), null, null, usertype); 
			}
			
			table = "REF_ACCOUNT_APPLICATION ";
			select = new ArrayList<String>();
			select.add("id");
			where = new HashMap<String, String>();
			where.put("USERS_ID", Operator.toString(u.getId()));
			where.put("APPROVAL", "Y");
			
			if(Operator.hasValue(DbUtil.getAsString(table, where, select))){
				throw new Exception("There is an Requesting Pending for Approval, Kindly contact City of Beverly Hills.");
			} 
			
			command = ParkingSQL.insertOnlineUser(u.getId(), accountno, lsoid, usertype, dvo.getString("RESIDE"), dvo.getString("SPACE_AVAIL"), dvo.getString("NO_CARS"), projectid, applicationStatus, dvo.getString("EXISTING_ACT"));
			db.query(command);
			if (db.next()) {
				
				int count = Operator.toInt(dvo.getString("filecounts"));
				for(int i=0;i<count;i++){
					saveFile(db.getInt("ID"), dvo.getString("parkfile_"+i), dvo.getString("parkselect_"+i));
				}
				
				command = ParkingSQL.updateOnlineUserAdditionalAddress(db.getInt("ID"), dvo.getString("STREET_NUMBER"),dvo.getString("STREET_ID"),dvo.getString("UNIT"),dvo.getString("NEWSTREETFRACTION")); // Update Or Insert
				db.update(command);
				
				r.setMessagecode("cs200"); 
				r.setRedirect(true);
				
				//Save Project
				if(projectid < 1) {
					vo.setType("lso");
					vo.setTypeid(Operator.toInt(lsoid));
					String prjtype = getOnlineProjectTypes(usertype);
					String prjstatus = getOnlineStatus("LKUP_PROJECT_STATUS", "Y");
					
					for(int i =0; i< vo.getData()[0].getObj().length; i++){
						if(vo.getData()[0].getObj()[i].getFieldid().equals("LKUP_PROJECT_TYPE_ID")){
							vo.getData()[0].getObj()[i].setText(prjtype);
							vo.getData()[0].getObj()[i].setValue(prjtype);
						}
						if(vo.getData()[0].getObj()[i].getFieldid().equals("LKUP_PROJECT_STATUS_ID")){
							vo.getData()[0].getObj()[i].setText(prjstatus);
							vo.getData()[0].getObj()[i].setValue(prjstatus);
						}
					}
					projectid = saveParkingProject(vo, u);
				}
			}				
			else
				throw new Exception("Internal Server Error, Kindly contact City of Beverly Hills.");

			db.clear();	
		} catch (Exception e) {
			db.clear();
			r.setMessagecode("-1");
			r.setRedirect(true);
			MessageVO m = new MessageVO();
			m.setMessage(e.getMessage());
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
		}
		return r;
	}
	
	public static void saveFile(int id, String file, String type) throws FileUploadException{
		Sage db = new Sage();
		String command = ParkingSQL.insertAttachment(id, file, type);
		db.update(command);
		db.clear();
	}
	
	public static SubObjVO[] permitType(String type, int typeid, Token u) {
		return ActivityAgent.actType("project", typeid, u, false);
	}
	
	public static SubObjVO[] exemptionType(String type, int typeid) {
		if (typeid < 1) {
			String command = ParkingSQL.acttype(type, typeid,false,true);
			return Choices.getChoices(command);
		}
		else {
			HoldsList l = HoldsAgent.getActivityHolds(type, typeid);
			String command = ParkingSQL.acttype(type, typeid,false,true);
			return Choices.getChoices(command, l);
		}
	}

	public static ResponseVO onlinePrints(RequestVO r) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		ResponseVO vo = new ResponseVO();
		TypeVO t = new TypeVO();
		try {

			t.setEntity(r.getEntity());
			t.setType(r.getType());

			Sage db = new Sage();
			
			JSONObject d = GeneralAgent.getConfiguration("DOT");

			db.query(ParkingSQL.onlinePrints(r.getGroup(), r.getGroupid(), getParkingDate(d.getString("START_DATE")), getParkingDate(d.getString("EXP_DATE")), r.getRef()));
			BatchRunnable.size = db.size();
			
			ga = new ObjGroupVO[db.size()];
			int c = 0;
			while (db.next()) {
				ObjGroupVO g = new ObjGroupVO();
				g.setGroup("accounts");

				HashMap<String, String> extras = new HashMap<String, String>();
				extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
				extras.put("STATUS",
						db.getString("LKUP_PROJECT_STATUS_TEXT"));
				extras.put("TYPE", db.getString("LKUP_PROJECT_TYPE_TEXT"));
				extras.put("PROJECT_NBR", db.getString("PROJECT_NBR"));
				extras.put("ID", db.getString("ID"));
				extras.put("LSO_ID", db.getString("LSO_ID"));
				extras.put("PROJECT_ID", db.getString("PROJECT_ID"));
				extras.put("ADDRESS", db.getString("ADDRESS"));
				extras.put("CREATED_DATE", db.getString("P_CREATED_DATE"));
				extras.put("UPDATED_DATE", db.getString("P_UPDATED_DATE"));
				extras.put("CREATED", db.getString("CREATED"));
				extras.put("UPDATED", db.getString("UPDATED"));
				extras.put("BATCH_ID", db.getString("BATCH_PRINT_ID"));
				g.setExtras(extras);
				ga[c] = g;

				c++;

				}
			db.clear();

			vo.setMessagecode("cs200");
			t.setTitle("accounts");
			
			t.setGroups(ga);

			vo.setType(t);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}
	
	public static ResponseVO saveParkingActivity(RequestVO vo, Token u, ResponseVO r) {
		DataVO m = DataVO.toDataVO(vo);
		String type = m.get("ACT_TYPE");
		ParkingActivity pa = null;
		
		String[] size = m.get("QTY").split(",");
		int qty = 0;
		ObjGroupVO[] obj = vo.getData();
		ObjVO[] oldobj = obj[0].getObj();
		for(int i=0; i<size.length; i++){
			if(Operator.toInt(size[i]) > 0) {
				qty += Operator.toInt(size[i]);
				try {
					vo = processParkingRequestVO(obj, oldobj, vo, i);
					m = DataVO.toDataVO(vo);

					if(type.contains("Exemption")){
						pa = new ParkingExemption(vo, u, r);
//						if (r.isValid()) {
//							r = ParkingAgent.checkMaxAllowed(vo, u, r);
//						}
//						if (r.isValid()) {
//							r = ParkingAgent.checkValidTime(vo, u, r);
//						}
					}
					else {
//						r = ParkingAgent.checkAlert(vo, u, r);
//						if(r.isValid()) {
//							r = ParkingAgent.checkAccountandZone(vo, u, r);
//						}
						pa = new ParkingPermit(vo, u, r);
					}

					if(r.isValid()) {
						r = saveActivity(vo, u, r, m);
					}

				}
				catch (Exception e) {
					MessageVO msg = new MessageVO();
					msg.setMessage(" Error while processing your request");
					ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
					mv.add(msg);
					r.setErrors(mv);
					r.setMessagecode("cs412");
					return r;
				}
			}
		}
		if(r.isValid()) {
			r.setType(ParkingAgent.getResponseType(vo, u, qty, r.getInfo("IDS")));
		}
		
		return r;
	}

	public static ResponseVO saveActivity(RequestVO vo, Token u, ResponseVO r, DataVO m) {
		int size = Operator.toInt(m.get("QTY"));
		int id = 0;
		String processId = Operator.randomString(12);
		vo.setProcessid(processId);
		for (int i = size ; i > 0; i--) {
			String str = ActivityAgent.saveActivity(vo, u); 
			//id = 
			
			id = Operator.toInt(str);
			if (id > 0) {
				String ids = r.getInfo("IDS");
				StringBuilder sb = new StringBuilder();
				if (Operator.hasValue(ids)) {
					sb.append(ids);
					sb.append(",");
				}
				sb.append(id);
				r.addInfo("IDS", sb.toString());
			}
		}
		if (id > 0) {
			r.setMessagecode("cs200");
			r.setType(ParkingAgent.getResponseType(vo, u, size, Operator.toString(id)));
			r.setId(id);
		}
		else {
			r = LogAgent.getLog(processId);
			
			if(r.getMessages().size()<=0){
				r.setMessagecode("  ");
				r.addError("Unable to save permit.2");
			}
			//r.setMessagecode("cs412");
			//r.addError("Unable to save permit.2");
		}
		return r;
	}

	public static ResponseVO listTempExemptionTypes(RequestVO vo, Token u) {
		
		ObjVO obj = new ObjVO();
		ObjVO[] objarr = new ObjVO[1];
		ObjGroupVO[] data = null;
		ResponseVO r = new ResponseVO();
		
		// getting Permit counts for Current year
		r = parkingApproval(vo);
		if(r.getType().getGroups().length > 0){
			String usertype = r.getType().getGroups()[0].getExtras().get("LKUP_USERS_TYPE_ID");
			String lsoid = r.getType().getGroups()[0].getExtras().get("LSO_ID");
			String prjtype = getOnlineProjectTypes(usertype);
			String prjstatus = getOnlineStatus("LKUP_PROJECT_STATUS", "Y");
			int projectid = getParkingProject(Operator.toInt(lsoid), prjtype, null, usertype);
			data = ListAllOnlinePermits(Operator.toString(projectid), "Y", null, "Y");
			vo.setTypeid(projectid);
			if(projectid <= 0){
				MessageVO m = new MessageVO();
				m.setMessage(" Your application has not been approved. You cannot apply for an exemption at this time.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
				return r;
			}
			
			
			
			SubObjVO[] sv = listLastYearTypes(vo);
			
			HashMap<String, String> hm = new HashMap<String, String>();
			for(int i=0;i<data.length;i++){
				data[i].getExtras().put("LKUP_ACT_STATUS_ID", getOnlineStatus("LKUP_ACT_STATUS", "Y"));
			}
			obj.setChoices(sv);
			objarr[0] = obj;
			if(data == null || data.length <= 0){
				MessageVO m = new MessageVO();
				m.setMessage("The permit cannot be applied to your zone.");
				ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
				mv.add(m);
				r.setErrors(mv);
				r.setMessagecode("cs412");
				return r;
			}
			data[0].setObj(objarr);
			TypeVO t = new TypeVO();
			t.setTypeid(projectid);
			t.setType(prjtype);
			t.setStatus(prjstatus);
			t.setGroups(data);
			r.setType(t);
			return r;
		}else{
			MessageVO m = new MessageVO();
			m.setMessage(" Your application has not been approved. You cannot apply for an exemption at this time.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			return r;
		}
	}
	
	/*public static ObjGroupVO[] ListTempOnlinePermits(String lsoid, String dotExemption, String exemption){

		String table = "V_CENTRAL_ADDRESS VCA ";
		List<String> select = new ArrayList<String>();
		select.add("LAT.id");
		select.add("LAT.id value");
		select.add("LAT.DESCRIPTION as text");
		select.add("RATS.LKUP_ACT_STATUS_ID");
		
		List<String> join = new ArrayList<String>();
		join.add("join REF_LSO_DIVISIONS RLT on VCA.LAND_ID = RLT.LSO_ID");
		join.add("join REF_ACT_DIVISIONS RAT on RLT.LKUP_DIVISIONS_ID = RAT.LKUP_DIVISIONS_ID and RAT.ACTIVE = 'Y' ");
		join.add("join LKUP_ACT_TYPE LAT on RAT.LKUP_ACT_TYPE_ID = LAT.ID and LAT.ACTIVE = 'Y' ");
		join.add("LEFT OUTER join REF_ACT_TYPE_STATUS RATS on LAT.ID = RATS.LKUP_ACT_TYPE_ID and RATS.ACTIVE = 'Y' ");

		Map<String, String> where = new HashMap<String, String>();
		where.put("VCA.lso_id", lsoid);
		where.put("online", "Y");
		where.put("isdot", "Y");
		where.put("istemp", "Y");
		where.put("dot_exemption", dotExemption);
		
		if(exemption != null)
			where.put("LAT.description", exemption);

		return DbUtil.getListAsObjGroup(table, where, select, join);
	}*/
	
	public static String getOnlineProjectTypes(String usertype){
		String table = "LKUP_PROJECT_TYPE LPT";
		List<String> select = new ArrayList<String>();
		select.add("LPT.ID");
		select.add("LPT.TYPE");
		
		List<String> join = new ArrayList<String>();
		join.add(" JOIN REF_USERS_TYPE_PROJECT_TYPE RUPT on RUPT.LKUP_PROJECT_TYPE_ID = LPT.ID and RUPT.active='Y' ");
		
		Map<String, String> where = new HashMap<String, String>();
		where.put("isdot", "Y");
		where.put("online", "Y");
		where.put("LPT.active", "Y");
		where.put("RUPT.LKUP_USERS_TYPE_ID", usertype);
		
		ObjGroupVO[] types = DbUtil.getListAsObjGroup(table, where, select, join);
		String prjtype = types.length > 0 ? types[0].getExtras().get("ID") : "";
		return prjtype;
	}
	
	public static String getOnlineStatus(String table, String dflt){
		//String table = "LKUP_PROJECT_STATUS";
		List<String> select = new ArrayList<String>();
		select.add("ID");
		select.add("STATUS");
		
		Map<String, String> where = new HashMap<String, String>();
		where.put("isdot", "Y");
		where.put("active", "Y");
		where.put("Expired ", "N");
		where.put("live", "Y");
		if(dflt.equals("Y"))
			where.put("DEFLT", dflt);
			
		
		
		ObjGroupVO[] status = DbUtil.getListAsObjGroup(table, where, select);
		StringBuffer prjstatus = new StringBuffer();
		int i = 1;
		for(ObjGroupVO obj :status){
			prjstatus.append(obj.getExtras().get("ID"));
			if(i<status.length){
				prjstatus.append(",");
				i++;
			}
		}
		
		return prjstatus.toString();
	}

	public static ResponseVO saveTempParkingActivity(RequestVO vo, Token u, ResponseVO r) {

		//Check if any Project of type Transportation Exist with status Pending. if yes add the activities to it else create a new One.
		int projectId = 0;
		try {
			projectId = saveParkingProject(vo, u);
		} catch (Exception e1) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while saving project :"+ e1.getMessage());
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		vo.setTypeid(projectId);
		vo.setType("project");
		try {
			if(r.isValid())
				r = saveParkingActivity(vo, u, r);
		} catch (Exception e) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while saving activity :"+ e.getMessage());
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}

	public static RequestVO processParkingRequestVO(ObjGroupVO[] obj, ObjVO[] oldobj, RequestVO vo, int i) throws Exception{
		//Splitting value for LKUP_ACT_TYPE_ID, QTY and ACT_TYPE while obtaining Permits as these value come as String Array. 
		ObjVO[] newobj = new ObjVO[oldobj.length];
		ObjGroupVO objgrpvo = new ObjGroupVO();
		for (int j = 0; j < oldobj.length; j++) {
			ObjVO o = oldobj[j];
			ObjVO n = oldobj[j].duplicate();
			String f = o.getFieldid();
			String v = o.getValue().split(",").length > 1 ? o.getValue().split(",")[i] : o.getValue();
			String t = o.getText().split(",").length > 1 ? o.getText().split(",")[i] : o.getText();			
			n.setFieldid(f);
			n.setValue(v);
			n.setText(t);
			newobj[j] = n;
		}
		objgrpvo.setObj(newobj);
		objgrpvo.setCustom(obj[0].getCustom());

		obj[0] = objgrpvo;
		vo.setData(obj);
		return vo;
	}
	
	public static int saveParkingProject(RequestVO vo, Token u) throws Exception {
		DataVO m = DataVO.toDataVO(vo);
		int projectid = Operator.toInt(m.getString("PROJECT_ID"));
		if(projectid <= 0){
			ResponseVO r = LogAgent.createResponse("SAVE PROJECT");
			vo.setProcessid(r.getProcessid());
			projectid = ProjectAgent.saveProject(vo, u);
		}
		return projectid;
	}
	
	public static int getParkingProject(int lsoid, String prjtype, String prjstatus, String usertype){
		
		prjtype = Operator.hasValue(prjtype)?prjtype:getOnlineProjectTypes(usertype);
		prjstatus = Operator.hasValue(prjstatus)?prjstatus:getOnlineStatus("LKUP_PROJECT_STATUS", "N");
		
		String command = ParkingSQL.getProjectByLSO(lsoid, prjtype, prjstatus, usertype);
		Sage db = new Sage();
		int projectid = 0;
		db.query(command);
		if(db.next())
			projectid = db.getInt("ID");
		db.clear();
		
		return projectid;
	}
	
	public static ResponseVO getAttachment(RequestVO vo, Token u, ResponseVO r) {
		String table = "REF_ACCOUNT_APPLICATION_ATTACHMENTS RUOA";
		List<String> select = new ArrayList<String>();
		select.add("RUOA.ID");
		select.add("RUOA.REF_ACCOUNT_APPLICATION_ID");
		select.add("RUOA.ATTACHMENT");
		select.add("RUOA.LKUP_ATTACHMENTS_TYPE_ID");
		select.add("LAT.TYPE");
		
		List<String> join = new ArrayList<String>();
		join.add("JOIN LKUP_ATTACHMENTS_TYPE LAT ON RUOA.LKUP_ATTACHMENTS_TYPE_ID = LAT.ID");
		
		Map<String, String> where = new HashMap<String, String>();
		where.put("REF_ACCOUNT_APPLICATION_ID", Operator.toString(vo.getTypeid()));
		
		String command = DbUtil.buildQuery(table, where, select, join);
		Sage db = new Sage();
		db.query(command);
		ObjGroupVO[] ga = new ObjGroupVO[db.size()];
		int c = 0;
		while (db.next()) {
			ObjGroupVO g = new ObjGroupVO();
			g.setGroup("accounts");

			HashMap<String, String> extras = new HashMap<String, String>();
			extras.put("ID", db.getString("ID"));
			extras.put("REF_ACCOUNT_APPLICATION_ID", db.getString("REF_ACCOUNT_APPLICATION_ID"));
			extras.put("ATTACHMENT", db.getString("ATTACHMENT"));
			extras.put("LKUP_ATTACHMENTS_TYPE_ID", db.getString("LKUP_ATTACHMENTS_TYPE_ID"));
			extras.put("TYPE", db.getString("TYPE"));
			g.setExtras(extras);
			ga[c] = g;
			c++;
		}
		db.clear();
		TypeVO t = new TypeVO();
		t.setGroups(ga);
		r.setType(t);
		return r;
	}
	
	public static ResponseVO deleteAttachment(RequestVO vo, Token u, ResponseVO r) {
		String command = ParkingSQL.deleteAttachment(vo.getTypeid());
		Sage db = new Sage();
		db.update(command);
		db.clear();
		r.setMessagecode("cs200");
		return r;
	}

	public static ResponseVO parkingApprovalCount(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		int count = 0;
		String command = ParkingSQL.getParkingApprovalCount();
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				count = db.size();
			}
			db.clear();
		}
		r.addInfo("numaccounts", Operator.toString(count));
		return r;
	}

	public static ResponseVO parkingApproval(RequestVO vo) {
		ObjGroupVO[] ga;
		ResponseVO r = new ResponseVO();
		TypeVO t = new TypeVO();
		try {

			t.setEntity(vo.getEntity());
			t.setType(vo.getType());

			String table = "REF_ACCOUNT_APPLICATION RUO";
			List<String> select = new ArrayList<String>();
			select.add("RUO.ID");
			select.add("RUO.USERS_ID");
			select.add("U.FIRST_NAME");
			select.add("U.MI");
			select.add("U.LAST_NAME");
			select.add("U.USERNAME");
			select.add("U.EMAIL");
			select.add("RAP.ID ACCOUNT_NO");
			select.add("RUO.ACCOUNT_NO");
			select.add("RUO.APPROVAL");
			select.add("RUO.PROJECT_ID");
			select.add("RUO.LKUP_USERS_TYPE_ID");
			select.add("RUO.RESIDE");
			select.add("RUO.SPACE_AVAIL");
			select.add("RUO.NO_CARS");
			select.add("RUO.COMMENT");
			select.add("RUO.EXISTING_ACT");
			select.add("PS.EXPIRED");
			select.add("VA.LSO_STREET_ID");
			select.add("VA.STR_NO");
			select.add("VA.PRE_DIR");
			select.add("VA.STR_NAME");
			select.add("VA.STR_TYPE");
			select.add("VA.SUF_DIR");
			select.add("VA.UNIT");
			select.add("VA.LSO_ID");
			select.add("VA.LSO_STREET_ID");
			select.add("LUT.TYPE");
			select.add("RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID");
			select.add("LAA.STATUS");
			select.add("VA.STR_MOD");
			select.add("RUO.UNIT AS UNIT_ENTERED");

			List<String> join = new ArrayList<String>();
			join.add("JOIN USERS U ON RUO.USERS_ID = U.ID");
			join.add("JOIN V_CENTRAL_ADDRESS VA ON RUO.LSO_ID = VA.LSO_ID");
			join.add("JOIN LKUP_USERS_TYPE LUT ON RUO.LKUP_USERS_TYPE_ID = LUT.ID");
			join.add("LEFT OUTER JOIN REF_PROJECT_PARKING RAP ON RUO.PROJECT_ID = RAP.PROJECT_ID");
			join.add("LEFT OUTER JOIN PROJECT PR ON RAP.PROJECT_ID = PR.ID");
			join.add("LEFT OUTER JOIN LKUP_PROJECT_STATUS PS ON PR.LKUP_PROJECT_STATUS_ID = PS.ID");
			join.add("JOIN LKUP_ACCOUNT_APPLICATION_STATUS LAA ON RUO.LKUP_ACCOUNT_APPLICATION_STATUS_ID = LAA.ID");

			Map<String, String> where = new HashMap<String, String>();
			where.put("APPROVAL", "Y");
			where.put("LAA.DEFLT", "Y");
			if(vo.getTypeid() > 0){
				select.add("P.CITY");
				select.add("P.ADDRESS");
				select.add("P.STATE");
				select.add("P.ZIP");
				select.add("P.PHONE_WORK");
				select.add("P.PHONE_CELL");
				select.add("P.PHONE_HOME");
				select.add("P.FAX");
				select.add("RU.ID REF_USERS_ID");
				join.add("LEFT OUTER JOIN PEOPLE P on U.ID = P.USERS_ID");
				join.add(" LEFT OUTER JOIN REF_USERS RU ON U.ID = RU.USERS_ID AND RUO.LKUP_USERS_TYPE_ID = RU.LKUP_USERS_TYPE_ID");
				where.put("RUO.ID", Operator.toString(vo.getTypeid()));
			}

			String command = DbUtil.buildQuery(table, where, select, join);
			Sage db = new Sage();
			db.query(command);
			ga = new ObjGroupVO[db.size()];
			int i = 0;
			while(db.next()){
				ObjGroupVO g = new ObjGroupVO();
				g.setGroup("accounts");

				HashMap<String, String> extras = new HashMap<String, String>();
				extras.put("ID", db.getString("ID"));
				extras.put("USERS_ID", db.getString("USERS_ID"));
				extras.put("FIRST_NAME", db.getString("FIRST_NAME"));
				extras.put("MI", db.getString("MI"));
				extras.put("LAST_NAME", db.getString("LAST_NAME"));
				extras.put("EMAIL", db.getString("EMAIL"));
				extras.put("USERNAME", db.getString("USERNAME"));
				extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
				extras.put("ACCOUNT_NO", db.getString("ACCOUNT_NO"));
				extras.put("APPROVAL", db.getString("APPROVAL"));
				extras.put("PROJECT_ID", db.getString("PROJECT_ID"));
				extras.put("LKUP_USERS_TYPE_ID", db.getString("LKUP_USERS_TYPE_ID"));
				extras.put("ASSOCIATION", db.getString("TYPE"));
				extras.put("RESIDE", db.getString("RESIDE"));
				extras.put("EXPIRED", db.getString("EXPIRED"));
				extras.put("SPACE_AVAIL", db.getString("SPACE_AVAIL"));
				extras.put("NO_CARS", db.getString("NO_CARS"));
				extras.put("COMMENT", db.getString("COMMENT"));
				extras.put("NEWADDRESS", db.getString("STR_NO")+" "+db.getString("PRE_DIR")+" "+db.getString("STR_MOD")+" "+db.getString("STR_NAME")+" "+db.getString("STR_TYPE")+" "+db.getString("SUF_DIR")+" "+db.getString("UNIT"));
				extras.put("LSO_ID", db.getString("LSO_ID"));
				extras.put("LSO_STREET_ID", db.getString("LSO_STREET_ID"));	
				extras.put("STR_MOD", db.getString("STR_MOD"));
				extras.put("ADDRESS", db.getString("ADDRESS"));
				extras.put("CITY", db.getString("CITY"));
				extras.put("STATE", db.getString("STATE"));
				extras.put("ZIP", db.getString("ZIP"));
				extras.put("PHONE_WORK", db.getString("PHONE_WORK"));
				extras.put("PHONE_CELL", db.getString("PHONE_CELL"));
				extras.put("PHONE_HOME", db.getString("PHONE_HOME"));
				extras.put("FAX", db.getString("FAX"));
				extras.put("LKUP_ACCOUNT_APPLICATION_STATUS_ID", db.getString("LKUP_ACCOUNT_APPLICATION_STATUS_ID"));
				extras.put("STATUS", db.getString("STATUS"));
				extras.put("EXISTING_ACT", db.getString("EXISTING_ACT"));
				extras.put("UNIT_ENTERED", db.getString("UNIT_ENTERED"));
				if(vo.getTypeid() > 0){
					extras.put("REF_USERS_ID", db.getString("REF_USERS_ID"));	
				}
				g.setExtraslist(getAttachment(db.getString("ID")));
				g.setExtras(extras);
				ga[i++] = g;
			}
			db.clear();
			r.setMessagecode("cs200");
			
			t.setGroups(ga);

			r.setType(t);
		} catch (Exception e) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while getting the Online Approvals");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		return r;
	}
	
	public static ArrayList<HashMap<String, String>> getAttachment(String id){
		String table = "REF_ACCOUNT_APPLICATION_ATTACHMENTS RUOA";
		List<String> select = new ArrayList<String>();
		select.add("RUOA.ID");
		select.add("RUOA.ATTACHMENT");
		select.add("LAT.TYPE");
		
		List<String> join = new ArrayList<String>();
		join.add("JOIN LKUP_ATTACHMENTS_TYPE LAT ON RUOA.LKUP_ATTACHMENTS_TYPE_ID = LAT.ID");
		
		HashMap<String, String> where = new HashMap<String, String>();
		where.put("REF_ACCOUNT_APPLICATION_ID", id);
		String command = DbUtil.buildQuery(table, where, select, join);
		Sage db = new Sage();
		db.query(command);
		ArrayList<HashMap<String, String>> extraslist = new ArrayList<HashMap<String, String>>();
		while (db.next()){
			HashMap<String, String> attachment = new HashMap<>();
			attachment.put("ID", db.getString("ID"));
			attachment.put("TYPE", db.getString("TYPE"));
			attachment.put("ATTACHMENT", db.getString("ATTACHMENT"));
			extraslist.add(attachment);
		}
		db.clear();
		return extraslist;
	}

	public static ResponseVO approve(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		try {
			r = parkingApproval(vo);
			if (r.getType().getGroups().length > 0) {
				String refuserid = r.getType().getGroups()[0].getExtras().get("REF_USERS_ID");
				String usertypeid = r.getType().getGroups()[0].getExtras().get("LKUP_USERS_TYPE_ID").trim();
				String fname = r.getType().getGroups()[0].getExtras().get("FIRST_NAME").trim();
				String mname = r.getType().getGroups()[0].getExtras().get("MI").trim();
				String lname = r.getType().getGroups()[0].getExtras().get("LAST_NAME").trim();
				String email = r.getType().getGroups()[0].getExtras().get("EMAIL").trim();
				String username = r.getType().getGroups()[0].getExtras().get("USERNAME").trim();
				String address = r.getType().getGroups()[0].getExtras().get("ADDRESS").trim();
				String city = r.getType().getGroups()[0].getExtras().get("CITY");
				String state = r.getType().getGroups()[0].getExtras().get("STATE");
				String zip = r.getType().getGroups()[0].getExtras().get("ZIP");
				String wphone = r.getType().getGroups()[0].getExtras().get("PHONE_WORK");
				String cphone = r.getType().getGroups()[0].getExtras().get("PHONE_CELL");
				String hphone = r.getType().getGroups()[0].getExtras().get("PHONE_HOME");
				String fax = r.getType().getGroups()[0].getExtras().get("FAX");
				String typeid = r.getType().getGroups()[0].getExtras().get("PROJECT_ID");
				String space = r.getType().getGroups()[0].getExtras().get("SPACE_AVAIL");
				String cars = r.getType().getGroups()[0].getExtras().get("NO_CARS");
				int lsoid = Operator.toInt(r.getType().getGroups()[0].getExtras().get("LSO_ID"));
				String accountno = r.getType().getGroups()[0].getExtras().get("ACCOUNT_NO");
				
				//if((Operator.hasValue(accountno) && Operator.toInt(accountno) > 0) || Operator.toInt(typeid)<1){
					String status = getOnlineStatus("LKUP_PROJECT_STATUS", "Y");
					String type = getOnlineProjectTypes(usertypeid);
					typeid = Operator.toString(getParkingProject(lsoid, type, status, usertypeid));

					if(Operator.toInt(typeid)<1 ){
						//Creating new project
						RequestVO pvo = new RequestVO();
						pvo.setType("lso");
						pvo.setTypeid(lsoid);
						ObjGroupVO[] data = new ObjGroupVO[1];
						ObjGroupVO g = new ObjGroupVO();
						ObjVO[]	objvodata = new ObjVO[5];
						objvodata[0]=getObjectVo(-1,"LKUP_PROJECT_STATUS_ID","LKUP_PROJECT_STATUS_ID","LKUP_PROJECT_STATUS_ID","String","text",getApprovedStatus(),"LKUP_PROJECT_STATUS_ID","Y","Y","Y","",1,"N");
						objvodata[1]=getObjectVo(-1,"LKUP_PROJECT_TYPE_ID","LKUP_PROJECT_TYPE_ID","LKUP_PROJECT_TYPE_ID","String","text",type,"LKUP_PROJECT_TYPE_ID","Y","Y","Y","",2,"N");
						objvodata[2]=getObjectVo(-1,"NAME","NAME","NAME","String","text","PARKING","NAME","N","N","N","",3,"N");
						objvodata[3]=getObjectVo(-1,"DESCRIPTION","DESCRIPTION","DESCRIPTION","String","text","ONLINE PARKING","DESCRIPTION","N","N","N","",4,"N");
						objvodata[4]=getObjectVo(-1,"START_DT","START_DT","START_DT","date","date","","START_DT","N","N","N","",5,"N");
						g.setObj(objvodata);
						data[0] = g;
						pvo.setData(data);
						
						typeid = Operator.toString(saveParkingProject(pvo, u));
					}
				//}

				r = PeopleAgent.select(Operator.toInt(refuserid), Operator.toInt(usertypeid), fname, mname, lname, email, username, address, city, state, zip, wphone, cphone, hphone, fax, "", "", "", "", "", u.getId(), ""); 
				deActivateOtherParkingProject(lsoid, Operator.toInt(typeid), usertypeid);
				addPeople(Operator.toInt(typeid), Operator.toString(r.getId()), u);
				
				Sage db = new Sage();
				String command = ParkingSQL.update("project", Operator.toInt(typeid), Operator.toInt(vo.getExtras().get("REQUIRED_SPACE"), 0), Operator.toInt(vo.getExtras().get("CARS"), 0), Operator.toInt(vo.getExtras().get("APPROVED_SPACE"), 0), "", u.getId(), u.getIp());
				db.update(command);
				db.clear();

				CsDeleteCache.deleteProjectCache("project", Operator.toInt(typeid), "project");
				
				updateOnlineUser(vo.getTypeid(), u.getId(), "N", vo.getExtras().get("comment"), vo.getExtras().get("status"));
				
				r = getAttachment(vo, u, r);
				moveAttachment(r, u, Operator.toInt(typeid));
				

				r.setMessagecode("cs200");
				
				try { sendStatusEmail( r, email, "Your City of Beverly Hills parking application has been approved", getEmailContent("APPROVED", vo.getExtras().get("comment"))); } catch (Exception e1) { }
			}
		} catch (Exception e) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while approving online user." + e.getMessage());
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		return r;
	}
	
	public static ResponseVO unapprove(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		try {
			r = parkingApproval(vo);
			if (r.getType().getGroups().length > 0) {
				String refid = r.getType().getGroups()[0].getExtras().get("REF_USERS_ID");
				String usertypeid = r.getType().getGroups()[0].getExtras().get("LKUP_USERS_TYPE_ID").trim();
				String email = r.getType().getGroups()[0].getExtras().get("EMAIL").trim();
				int projectid = 0;
				String prjid = r.getType().getGroups()[0].getExtras().get("PROJECT_ID");
				if(Operator.toInt(prjid)<1){
					projectid = getParkingProject(Operator.toInt(r.getType().getGroups()[0].getExtras().get("LSO_ID")), null, null, usertypeid);
					Sage db = new Sage();
					String command = ProjectSQL.updateExpiration(projectid);
					db.update(command);
					command = ParkingSQL.updateProject(Operator.toString(projectid), getUnApprovedStatus());
					db.update(command);
					db.clear();
					ProjectAgent.addHistory(projectid, "project", projectid, "update");
				}else{
					projectid = Operator.toInt(prjid);
					deletePeople(projectid , Operator.toInt(refid), u);
				}
				
				updateOnlineUser(vo.getTypeid(), u.getId(), "Y", vo.getExtras().get("comment"), vo.getExtras().get("status"));
				r.setMessagecode("cs200");
				
				CsDeleteCache.deleteProjectCache("project", projectid, "project");
				
				try { sendStatusEmail( r, email, "Your City of Beverly Hills parking account application is ineligible", getEmailContent("UNAPPROVED", vo.getExtras().get("comment"))); } catch (Exception e1) { }
			}
			
		} catch (Exception e) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while un approving online user.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		return r;
	}
	
	public static ResponseVO merge(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		try {
			r = parkingApproval(vo);
			if (r.getType().getGroups().length > 0) {
				String refuserid = r.getType().getGroups()[0].getExtras().get("REF_USERS_ID");
				String usertypeid = r.getType().getGroups()[0].getExtras().get("LKUP_USERS_TYPE_ID").trim();
				String fname = r.getType().getGroups()[0].getExtras().get("FIRST_NAME").trim();
				String mname = r.getType().getGroups()[0].getExtras().get("MI").trim();
				String lname = r.getType().getGroups()[0].getExtras().get("LAST_NAME").trim();
				String email = r.getType().getGroups()[0].getExtras().get("EMAIL").trim();
				String username = r.getType().getGroups()[0].getExtras().get("USERNAME").trim();
				String address = r.getType().getGroups()[0].getExtras().get("ADDRESS").trim();
				String city = r.getType().getGroups()[0].getExtras().get("CITY");
				String state = r.getType().getGroups()[0].getExtras().get("STATE");
				String zip = r.getType().getGroups()[0].getExtras().get("ZIP");
				String wphone = r.getType().getGroups()[0].getExtras().get("PHONE_WORK");
				String cphone = r.getType().getGroups()[0].getExtras().get("PHONE_CELL");
				String hphone = r.getType().getGroups()[0].getExtras().get("PHONE_HOME");
				String fax = r.getType().getGroups()[0].getExtras().get("FAX");
				int projectid = 0;
				String prjid = r.getType().getGroups()[0].getExtras().get("PROJECT_ID");
				if(Operator.toInt(prjid)<1){
					projectid = getParkingProject(Operator.toInt(r.getType().getGroups()[0].getExtras().get("LSO_ID")), null, null, usertypeid);
					String command = ParkingSQL.updateProject(Operator.toString(projectid), getUnApprovedStatus());
					Sage db = new Sage();
					db.update(command);
					db.clear();
				}else 
					projectid = Operator.toInt(prjid);
				
				r = PeopleAgent.select(Operator.toInt(refuserid), Operator.toInt(usertypeid), fname, mname, lname, email, username, address, city, state, zip, wphone, cphone, hphone, fax, "", "", "", "", "", u.getId(), ""); 
				deletePeople(projectid , r.getId(), u);
							
				// add to new Account
				String table = "REF_PROJECT_PARKING RAP ";
				List<String> select = new ArrayList<String>();
				Map<String, String> where = new HashMap<String, String>();
				select.add("RAP.PROJECT_ID");
				where.put("RAP.ID", vo.getExtras().get("account"));
				projectid = Operator.toInt(DbUtil.getAsString(table, where, select));
				addPeople(projectid , Operator.toString(r.getId()), u);
				
				updateOnlineUser(vo.getTypeid(), u.getId(), "N", vo.getExtras().get("comment"), vo.getExtras().get("status"));
				
				r = getAttachment(vo, u, r);
				moveAttachment(r, u, projectid);
				
				CsDeleteCache.deleteProjectCache("project", projectid, "project");
				r.setMessagecode("cs200");
				
				try { sendStatusEmail( r, email, "Your City of Beverly Hills parking account has been approved", getEmailContent("APPROVED", vo.getExtras().get("comment"))); } catch (Exception e1) { }
			}
			
		} catch (Exception e) {
			MessageVO m = new MessageVO();
			m.setMessage("Error while un approving online user.");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}

		return r;
	}

	public static boolean update(RequestVO vo, Token u) {
		boolean r = false;
		DataVO m = DataVO.toDataVO(vo);
		String type = vo.getType();
		int typeid = vo.getTypeid();
		Sage db = new Sage();
		String command = ParkingSQL.update(type, typeid, Operator.toInt(m.get("NO_SPACES"), 0), Operator.toInt(m.get("NO_CARS"), 0), Operator.toInt(m.get("APPROVED_SPACE"), 0), m.get("SECRET"), u.getId(), u.getIp());
		r = db.update(command);
		db.clear();
		if (r) {
			int id = addHistory(type, typeid);
			CsReflect.addHistory(type, typeid, "parking", id, "update");
		}
		return r;
	}

	public static int addHistory(String type, int typeid) {
		int r = -1;
		Sage db = new Sage();
		String histcommand = "";
		String command = ParkingSQL.getParking(type, typeid);
		if (db.query(command) && db.next()) {
			histcommand = ParkingSQL.addHistory(db.getInt("ID"), db.getInt("PROJECT_ID"), db.getInt("NO_SPACES"), db.getInt("NO_CARS"), db.getString("ACTIVE"), db.getInt("CREATED_BY"), db.getString("CREATED_DATE"), db.getInt("UPDATED_BY"), db.getString("UPDATED_DATE"), db.getString("CREATED_IP"), db.getString("UPDATED_IP"));
		}
		db.clear();
		if (Operator.hasValue(histcommand)) {
			db = new Sage(CsApiConfig.getHistorySource());
			if (db.query(histcommand) && db.next()) {
				r = db.getInt("ID");
			}
			db.clear();
		}
		return r;
	}

	public static void addPeople(int id, String user, Token u) throws Exception {

		boolean b = false;
		b = PeopleAgent.addPeople("project", id , user, 0, "", u.getId(), u.getIp());
		if (b) {
			CsDeleteCache.deleteProjectAndActivityCache("project", id, "people");
			CsDeleteCache.deleteProjectAndActivityCache("project", id, "peoplesummary");
		}
	}
	
	public static void deletePeople(int id, int user, Token u) throws Exception {

		boolean b = PeopleAgent.delete("project", id , user, u.getId(), u.getIp());
		if (b) {
			CsDeleteCache.deleteProjectAndActivityCache("project", id, "people");
			CsDeleteCache.deleteProjectAndActivityCache("project", id, "peoplesummary");
		}
	}
	
	public static void updateOnlineUser(int id, int user, String active, String comment, String status) throws Exception {
		String command = ParkingSQL.updateOnlineUser(id, user, active, comment, status);
		Sage db = new Sage();
		db.update(command);
		db.clear();
	}

	public static void moveAttachment(ResponseVO r, Token u, int typeid) throws Exception {

		Sage db = new Sage();
		ObjGroupVO[] attachment = r.getType().getGroups();
		for(ObjGroupVO a : attachment){
			Timekeeper now = new Timekeeper();
			String path = a.getExtras().get("ATTACHMENT");
			String command = AttachmentsSQL.insertAttachments(a.getExtras().get("TYPE") ,0, "PROJECT" ,"ATTACHMENTS",path, Operator.toInt(a.getExtras().get("LKUP_ATTACHMENTS_TYPE_ID")), a.getExtras().get("TYPE"), 0, false, true, u.getId(), u.getIp(), now);
				if (db.query(command) && db.next()) {
					int attachid = db.getInt("ID");
					command = GeneralSQL.insertRef("PROJECT", "ATTACHMENTS", typeid, attachid, u.getId());
					db.update(command);
					CsReflect.addHistory("PROJECT", typeid, "attachments", attachid, "add");
				}
		}
		db.clear();
	}
	
	
	public static ResponseVO getRenewalPermits(RequestVO vo) {
		ObjGroupVO[] ga = new ObjGroupVO[0];
		ResponseVO r = new ResponseVO();
		TypeVO t = new TypeVO();
		try {

			t.setEntity(vo.getEntity());
			t.setType(vo.getType());
			
			JSONObject d = GeneralAgent.getConfiguration("DOT");
			String startdate = getRenewalDate(d.getString("START_DATE"));
			String enddate = getRenewalDate(d.getString("EXP_DATE"));
			
			Sage db = new Sage();
			String command = ParkingSQL.search(vo.getExtras(), vo.getReference());
			db.query(command);
			while (db.next()) {
				ga = getParkingPermits(db.getString("PROJECT_ID"), startdate, enddate);
			}
			db.clear();
			t.setGroups(ga);
			r.setType(t);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}
	
	public static String getRenewalDate(String date) {
		Timekeeper dt = new Timekeeper();
		String m = Operator.subString(date, 0, 2);
		String d = Operator.subString(date, 3, 5);
		String y = Operator.subString(date, 6, date.length());
		dt.setMonth(m);
		dt.setDay(d);
		dt.setYear(y);
		/*if (y.equalsIgnoreCase("[autostyear]")) {
			Timekeeper s = new Timekeeper();
			Logger.info(s.MONTH() + "::" + dt.MONTH());
			if (s.MONTH() < dt.MONTH()) {
				dt.addYear(-1);
			}
		}*/
		if (y.equalsIgnoreCase("[autoedyear]")) {
			Timekeeper s = new Timekeeper();
			Logger.info(s.MONTH() + "::" + dt.MONTH());
			//if (s.MONTH() > dt.MONTH()) {
				dt.addYear(1);
			//}
		}

		return dt.getString("YYYY/MM/DD");
	}

	public static SubObjVO[] listRenewalTypes(RequestVO r) {
		return listRenewalTypes(r.getTypeid(), new Token());
	}

	public static SubObjVO[] listRenewalTypes(RequestVO r, Token u) {
		return listRenewalTypes(r.getTypeid(), u);
	}

	public static SubObjVO[] listRenewalTypes(int projectid, Token u) {
		String command = ActivitySQL.renewalActType(projectid);

		ArrayList<SubObjVO> a = new ArrayList<SubObjVO>();
		Sage db = new Sage();
		db.query(command);
		if (db.size() > 0) {
			String[] cols = db.COLUMNS;
			while (db.next()) {
				boolean iscreate = true;
				SubObjVO vo = new SubObjVO();
				vo.setId(db.getInt("ID"));
				vo.setValue(db.getString("VALUE"));
				vo.setText(db.getString("TEXT"));
				vo.setDescription(db.getString("DESCRIPTION"));
				vo.setSelected(db.getString("SELECTED"));

				String expdate = "";
				String stdate = "";
				Timekeeper exp = new Timekeeper();
				if (db.getInt("MONTH_START") > 0) {
					exp.setMonth(db.getInt("MONTH_START"));
					if (db.getInt("DAY_START") > 0) {
						exp.setDay(db.getInt("DAY_START"));
					}
					else {
						exp.setDay(1);
					}
					exp.addDay(-1);
					if (exp.past()) {
						exp.addYear(1);
					}
					exp.addYear(1);
					Timekeeper st = exp.copy();
					st.addYear(-1);
					st.setMonth(db.getInt("MONTH_START"));
					st.setDay(db.getInt("DAY_START"));
					expdate = exp.getString("YYYY/MM/DD");
					stdate = st.getString("YYYY/MM/DD");
				}
				else if (db.getInt("YEARS_TILL_EXPIRED") > -1) {
					exp.addYear(db.getInt("YEARS_TILL_EXPIRED"));
					if (db.getInt("DAYS_TILL_EXPIRED") > -1) {
						exp.addDay(db.getInt("DAYS_TILL_EXPIRED"));
					}
					expdate = exp.getString("YYYY/MM/DD");
				}
				else if (db.getInt("DAYS_TILL_EXPIRED") > -1) {
					exp.addDay(db.getInt("DAYS_TILL_EXPIRED"));
					expdate = exp.getString("YYYY/MM/DD");
				}

				vo.setData("PERMIT_EXPIRE", expdate);
				vo.setData("START_DATE", stdate);

				String appexpdate = "";
				if (db.getInt("DAYS_TILL_APPLICATION_EXPIRED") > -1) {
					Timekeeper appexp = new Timekeeper();
					appexp.addDay(db.getInt("DAYS_TILL_APPLICATION_EXPIRED"));
					appexpdate = appexp.getString("YYYY/MM/DD");
				}

				vo.setData("APPLICATION_EXPIRE", appexpdate);

				String roletype = db.getString("ROLE_TYPE");
				int rtypeid = db.getInt("ROLE_TYPE_ID");
				if (Operator.hasValue(roletype) && rtypeid > 0) {
					RolesVO r = LkupCache.getActivityRoles(rtypeid);
					vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
					iscreate = vo.isCreate();
				}

				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					if (!c.equalsIgnoreCase("ID") && !c.equalsIgnoreCase("VALUE") && !c.equalsIgnoreCase("TEXT") && !c.equalsIgnoreCase("DESCRIPTION")  && !c.equalsIgnoreCase("SELECTED")) {
						vo.setData(c, db.getString(c));
					}
				}
				if (iscreate) {
					a.add(vo);
				}
			}
		}
		db.clear();
		SubObjVO[] r = a.toArray(new SubObjVO[a.size()]);
		return r;




//		SubObjVO[] a = new SubObjVO[0];
//		try {
//			JSONObject d = GeneralAgent.getConfiguration("DOT");
//			
//			Sage db = new Sage();
////			String command = ParkingSQL.getRenewalPermitsCount(String.valueOf(r.getTypeid()), getParkingDate(d.getString("START_DATE")), getParkingDate(d.getString("EXP_DATE")), getRenewalDate(d.getString("START_DATE")), getRenewalDate(d.getString("EXP_DATE")));
//			Timekeeper ed = new Timekeeper();
//			ed.setDate(getParkingDate(d.getString("EXP_DATE")));
//			String command = ParkingSQL.getRenewalPermitsCount(Operator.toString(r.getTypeid()), ed.YEAR()+1);
//			db.query(command);
//
//			if (db.size() > 0) {
//				int count = 0;
//				a = new SubObjVO[db.size()];
//				while (db.next()) {
//					SubObjVO vo = new SubObjVO();
//					vo.setId(db.getInt("ID"));
//					vo.setValue(db.getString("ID"));
//					vo.setText(db.getString("TYPE"));
//					HashMap<String, String> addldata = new HashMap<String, String>();
//					addldata.put("count", db.getString("COUNT"));
//					addldata.put("maxallowed", db.getString("MAX_ALLOWED"));
//					addldata.put("isdot", db.getString("ISDOT"));
//					addldata.put("dot_exemption", db.getString("DOT_EXEMPTION"));
//					addldata.put("configuration_group_id", db.getString("CONFIGURATION_GROUP_ID"));
//					addldata.put("c_value", db.getString("C_VALUE"));
//					addldata.put("lkup_act_type_id", db.getString("LKUP_ACT_TYPE_ID"));
//					addldata.put("conf_max_allowed", db.getString("CONF_MAX_ALLOWED"));
//					vo.setAddldata(addldata);
//
//					a[count] = vo;
//					count++;
//				}
//
//			}
//
//			db.clear();
//
//		} catch (Exception e) {
//			Logger.error(e);
//		}
//		return a;
	}
	
	public static SubObjVO[] renewalCount(RequestVO r) {
		SubObjVO[] a = new SubObjVO[0];
		try {
			JSONObject d = GeneralAgent.getConfiguration("DOT");
			
			Sage db = new Sage();
//			String command = ParkingSQL.getRenewalPermitsCount(String.valueOf(r.getTypeid()), getParkingDate(d.getString("START_DATE")), getParkingDate(d.getString("EXP_DATE")), getRenewalDate(d.getString("START_DATE")), getRenewalDate(d.getString("EXP_DATE")));
			Timekeeper ed = new Timekeeper();
			ed.setDate(getParkingDate(d.getString("EXP_DATE"),true));
			String command = ParkingSQL.getRenewalPermitsCount(Operator.toString(r.getTypeid()), ed.YEAR()+1);
			db.query(command);

			if (db.size() > 0) {
				int count = 0;
				a = new SubObjVO[db.size()];
				while (db.next()) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("ID"));
					vo.setText(db.getString("TYPE"));
					HashMap<String, String> addldata = new HashMap<String, String>();
					addldata.put("count", db.getString("COUNT"));
					addldata.put("maxallowed", db.getString("MAX_ALLOWED"));
					addldata.put("isdot", db.getString("ISDOT"));
					addldata.put("dot_exemption", db.getString("DOT_EXEMPTION"));
					addldata.put("configuration_group_id", db.getString("CONFIGURATION_GROUP_ID"));
					addldata.put("c_value", db.getString("C_VALUE"));
					addldata.put("lkup_act_type_id", db.getString("LKUP_ACT_TYPE_ID"));
					addldata.put("conf_max_allowed", db.getString("CONF_MAX_ALLOWED"));
					vo.setAddldata(addldata);

					a[count] = vo;
					count++;
				}

			}

			db.clear();

		} catch (Exception e) {
			Logger.error(e);
		}
		return a;
	}
	
	public static ResponseVO deleteDetails(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		String command = ParkingSQL.deleteAllAttachment(vo.getTypeid());
		Sage db = new Sage();
		db.update(command);
		command = ParkingSQL.deleteDetails(vo.getTypeid());
		db.update(command);
		db.clear();
		r.setMessagecode("cs200");
		return r;
	}

	public static TypeVO getParkingDates(RequestVO vo, Token u) throws JSONException {
		JSONObject d = GeneralAgent.getConfiguration("DOT");
		String startdate = getParkingDate(d.getString("START_DATE"));
		String enddate = getParkingDate(d.getString("EXP_DATE"));

		String startRenewdate = getRenewalDate(d.getString("START_DATE"));
		String endRenewdate = getRenewalDate(d.getString("EXP_DATE"));
		TypeVO t = new TypeVO();
		ObjGroupVO[] ga = new ObjGroupVO[1];
		ObjGroupVO g = new ObjGroupVO();
		HashMap<String, String> extras = new HashMap<String, String>();
		extras.put("START_DATE", startdate);
		extras.put("EXP_DATE", enddate);
		extras.put("START_RENEWAL_DATE", startRenewdate);
		extras.put("EXP_RENEWAL_DATE", endRenewdate);
		g.setExtras(extras);
		ga[0] = g;
		t.setGroups(ga);
		return t;
	}
	
	public static String getOffsiteType(){
		String table = "LKUP_USERS_TYPE ";
		List<String> select = new ArrayList<String>();
		Map<String, String> where = new HashMap<String, String>();
		select.add("ID");
		where.put("OFFSITE", "Y");
		return DbUtil.getAsString(table, where, select);
	}
	
	public static void deActivateOtherParkingProject(int lsoid, int prjid, String usertype){
		Sage db = new Sage();
		String command = "";
		String prjtype = getOnlineProjectTypes(usertype);
		String approvedStatus = getApprovedStatus();
		String prjstatus = approvedStatus;//+","+getOnlineStatus("LKUP_PROJECT_STATUS", "Y");
		
		command = ParkingSQL.getProjectByLSO(lsoid, prjtype, prjstatus, usertype);
		
		db.query(command);
		while(db.next()){
			int projectid = db.getInt("ID");
			command = ParkingSQL.updateProject(Operator.toString(projectid), getStatus("Closed"));
			db.update(command);
			command = ProjectSQL.updateExpiration(projectid);
			db.update(command);
			ProjectAgent.addHistory(projectid, "project", projectid, "update");
			CsDeleteCache.deleteProjectAndActivityCache("project", projectid, "people");
			CsDeleteCache.deleteProjectAndActivityCache("project", projectid, "peoplesummary");
		}
		
		command = ParkingSQL.updateProject(Operator.toString(prjid), approvedStatus);
		db.update(command);
		ProjectAgent.addHistory(prjid, "project", prjid, "update");
		CsDeleteCache.deleteProjectAndActivityCache("project", prjid, "people");
		CsDeleteCache.deleteProjectAndActivityCache("project", prjid, "peoplesummary");
		
		db.clear();
	}
	
	private static String getApprovedStatus() {
		String table = "LKUP_PROJECT_STATUS ";
		List<String> select = new ArrayList<String>();
		Map<String, String> where = new HashMap<String, String>();
		select.add("ID");
		where.put("active", "Y");
		where.put("ISSUED", "Y");
		return DbUtil.getAsString(table, where, select);
	}

	private static String getUnApprovedStatus() {
		String table = "LKUP_PROJECT_STATUS ";
		List<String> select = new ArrayList<String>();
		Map<String, String> where = new HashMap<String, String>();
		select.add("ID");
		where.put("active", "Y");
		where.put("UNAPPROVED", "Y");
		return DbUtil.getAsString(table, where, select);
	}
	
	public static String getStatus(String status){
		Sage db = new Sage();
		String command = ParkingSQL.getStatus(status);
		db.query(command);
		StringBuffer prjstatus = new StringBuffer();
		int i = 1;
		while(db.next()){
			prjstatus.append(db.getString("ID"));
			if(i < db.size()){
				prjstatus.append(",");
				i++;
			}
		}
		db.clear();
		return prjstatus.toString();
	}
	
	public static ResponseVO sendEmail(String type, int typeid, ResponseVO r, Token u, String email){
		//Send Email
		RequestVO vobj = new RequestVO();
		vobj.setType(type);
		vobj.setTypeid(typeid);
		r = LogAgent.createResponse("SEND EMAIL");
		vobj.setReference("online");
		vobj.setProcessid(r.getProcessid());
		HashMap<String, String> extras = new HashMap<String, String>();
		extras.put("email_subject", "Parking Permit");
		extras.put("email_bcc", email);
		vobj.setExtras(extras);
		EmailImpl.sendEmail(vobj, u);
		return r;
	}
	
	public static ResponseVO sendStatusEmail(ResponseVO r, String email, String subject, String content){
		//Send Email
		RequestVO vobj = new RequestVO();
		r = LogAgent.createResponse("SEND EMAIL");
		vobj.setReference("online");
		vobj.setProcessid(r.getProcessid());
		HashMap<String, String> extras = new HashMap<String, String>();
		extras.put("email_subject", subject);
		extras.put("email_body", content);
		extras.put("email_bcc", email);
		vobj.setExtras(extras);
		EmailImpl.send(vobj);
		return r;
	}
	
	private static String getEmailContent(String status, String comment) {
		StringBuffer sb = new StringBuffer();
		sb.append(" <html> <body>");
		sb.append(" <table border=\"0\" cellpadding=\"2\" cellspacing=\"2\" style=\"width:100%\">");
		sb.append(" <tbody>");
		sb.append(" <tr>");
		sb.append(" <td align=\"left\" width=\"125\" nowrap>");
		sb.append(" <a href=\"http://www.beverlyhills.org\"><img src=\"https://www.beverlyhills.org/images/shield/color/highlight/125x125.png\" height=\"125\" width=\"125\"/></a>");
		sb.append(" </td>");
		sb.append(" <td align=\"left\" style=\"font-family: Arial, Helvetica; font-size: 14px;\">");
		sb.append(" <strong>CITY OF BEVERLY HILLS</strong>");
		sb.append(" <br/>");
		sb.append(" 455 N Rexford Dr</br>Beverly Hills, CA 90210");
		sb.append(" </td>");
		sb.append(" </tr>");
		sb.append(" <tr>");
		sb.append(" <td align=\"left\" colspan=\"2\">");
		sb.append(" <span style=\"font-family: Arial, Helvetica; font-size:12px;\">");
		sb.append(" Your parking application has been: ").append(status);
		if(Operator.hasValue(comment)){
			sb.append("<br/>");
			sb.append(comment);
		}
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("<hr/>");
		sb.append("<br/>");
		sb.append("<a href=\"https://www.beverlyhills.org/csportal\" style=\"font-family: Arial, Helvetica; font-size:12px; color: #000000\">Click here to access your online account</a>");
		sb.append("</span>");
		sb.append(" </td>");
		sb.append(" </tr>");
		sb.append(" </tbody>");
		sb.append("  </table></body> </html>");
		return sb.toString();
	}
	
	public static ObjVO getObjectVo(int id,String fieldid,String field,String label,String type,String itype,String value,String textfield,String required,String editable,String addable,String json,int order,String sysgenerated) {

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String currentDate = sdf.format(new Date());
		
		ObjVO objectvo = new ObjVO();
		
		objectvo.setId(id);
		objectvo.setFieldid(fieldid);
		objectvo.setField(field);
		objectvo.setLabel(label);
		objectvo.setType(type);
		objectvo.setItype(itype);
		objectvo.setValue(value);
		objectvo.setText(value);
		
		
		if(fieldid.equalsIgnoreCase("START_DT")){
			objectvo.setValue(currentDate);
			objectvo.setText(currentDate);
		}
		
		objectvo.setTextfield(textfield);
		objectvo.setRequired(required);
		objectvo.setEditable(editable);
		objectvo.setAddable(addable);
		objectvo.setOrder(order);
		objectvo.setSystemGenerated(sysgenerated);
		objectvo.setJson(json);
		
		return objectvo;
	}
}
