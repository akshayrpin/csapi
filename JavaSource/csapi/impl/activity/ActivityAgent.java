package csapi.impl.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.common.LkupCache;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.custom.CustomSQL;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.entity.EntityAgent;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralSQL;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.library.LibraryAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.project.ProjectAgent;
import csapi.impl.project.ProjectSQL;
import csapi.impl.review.ReviewAgent;
import csapi.impl.tasks.TasksImpl;
import csapi.impl.users.UsersSQL;
import csapi.impl.vehicle.VehicleAgent;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csapi.utils.CsTools;
import csapi.utils.DbUtil;
import csapi.utils.Email;
import csapi.utils.objtools.Fields;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserItemsVO;
import csshared.vo.BrowserVO;
import csshared.vo.ColorList;
import csshared.vo.DataVO;
import csshared.vo.DivisionsList;
import csshared.vo.HoldsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeInfo;
import csshared.vo.lkup.RolesVO;

public class ActivityAgent {

	public static BrowserVO browse(String entity, String projid, int typeid, String option, Token u) {

		if (typeid > 0) {
			Sage db = new Sage();
			String command = ActivitySQL.getActivity(entity, typeid, "");
			db.query(command);
			String eid = "";
			String pid = "";
			if (db.next()) {
				eid = db.getString("ENTITY_ID");
				pid = db.getString("PROJECT_ID");
			}
			db.clear();
			BrowserVO v = ProjectAgent.browse(entity, eid, option, u);
			v.getHeader().addParent(pid, "project");
			return v;
		}
		else {
			BrowserVO b = new BrowserVO();
			try {
				BrowserHeaderVO h = new BrowserHeaderVO();
				h.setLabel("PROJECT BROWSER");
				h.setDataid(Operator.toString(projid));

				int id = Operator.toInt(projid);

				if (id > 0) {
					ArrayList<BrowserItemVO> i = new ArrayList<BrowserItemVO>();
					String expired = "N";
					if (!Operator.equalsIgnoreCase(option, "Active")) {
						expired = "Y";
					}

					String command = ActivitySQL.browse(id, "Y", expired, u);
					Sage db = new Sage();
					db.query(command);

					while(db.next()) {
						BrowserItemVO vo = new BrowserItemVO();
						StringBuilder sb = new StringBuilder();
						sb.append(db.getString("TITLE")).append(" - ").append(db.getString("ACT_NBR"));
						String title = sb.toString();
						if (db.hasValue("DESCRIPTION")) {
							sb.append(" - ").append(db.getString("DESCRIPTION"));
						}
						String description = sb.toString();
						vo.setTitle(title);
						vo.setDescription(description);
						vo.setId(db.getString("ID"));
						vo.setDataid(db.getString("ID"));
						vo.setChildren(0);
						vo.setEntity(entity);
						vo.setType("activity");
						vo.setLink("summary");
						vo.setDomain(Config.rooturl());
						if (db.equalsIgnoreCase("ISEXPIRED", "Y") && u.isStaff()) {
							vo.setExpired(true);
						}
						if (db.getInt("HARD_HOLDS") > 0) {
							vo.addAlert("hardhold");
						}
						if (db.getInt("SOFT_HOLDS") > 0) {
							vo.addAlert("softhold");
						}
						i.add(vo);
					}

					db.clear();

					BrowserItemVO[] is = i.toArray(new BrowserItemVO[i.size()]);
					b.setItems(is);
				}

				b.setHeader(h);
			
			}
			catch (Exception e){
				Logger.error(e.getMessage());
			}
			return b;
		}
	}
	
	public static String saveActivity(RequestVO r, Token u) {
		DataVO m = DataVO.toDataVO(r);
		String type = r.getType();
		int typeid = r.getTypeid();
		
		
		
		if (type.equalsIgnoreCase("activity") && typeid > 0) {
			int ur = updateActivity(typeid, m.getString("DESCRIPTION"), m.getInt("LKUP_ACT_STATUS_ID"), Operator.toDouble(m.getString("VALUATION_CALCULATED")), Operator.toDouble(m.getString("VALUATION_DECLARED")), Operator.equalsIgnoreCase(m.getString("UPDATE_FEES"), "Y"), m.getString("PLAN_CHK_REQ"), m.getString("START_DATE"), m.getString("APPLIED_DATE"), m.getString("ISSUED_DATE"), m.getString("EXP_DATE"), m.getString("APPLICATION_EXP_DATE"), m.getString("FINAL_DATE"), m.getString("ONLINE"), m.getString("SENSITIVE"), m.getString("INHERIT"), m.getString("SEND_EMAIL"), m.getString("NOTIFY"), m.getString("COMMENT"), u.getId(), u.getIp(), m.getString("CODE_ENFORCEMENT"));
			TasksImpl.runImmediate(ur, r, u);
			return Operator.toString(ur);
		}
		else {
			TypeInfo tinfo = EntityAgent.getEntity(r.getType(), r.getTypeid());
//			int lkupacttypeid = m.getInt("LKUP_ACT_TYPE_ID");
			String acttypes = m.getString("LKUP_ACT_TYPE_ID");
			String[] types = CsTools.paramToString(acttypes);

			int projectid = tinfo.getProjectid();
			int actid = tinfo.getActivityid();
			String processid = r.getProcessid(); 
			String entity = r.getEntity();
			int lkupstatusid = m.getInt("LKUP_ACT_STATUS_ID");
			String description = m.getString("DESCRIPTION");
			double calculated = Operator.toDouble(m.getString("VALUATION_CALCULATED"));
			double declared = Operator.toDouble(m.getString("VALUATION_DECLARED"));
			String planchkreq = m.getString("PLAN_CHK_REQ");
			String startdate = m.getString("START_DATE");
			String applieddate = m.getString("APPLIED_DATE");
			String issueddate = m.getString("ISSUED_DATE");
			String expdate = m.getString("EXP_DATE");
			String appexpdate = m.getString("APPLICATION_EXP_DATE");
			String finaldate = m.getString("FINAL_DATE");
			String online = m.getString("ONLINE");
			String sensitive = m.getString("SENSITIVE");
			String inherit = m.getString("INHERIT");
			String cc = m.getString("CODE_ENFORCEMENT");
				
			r.setStartdate(startdate);
			r.setEnddate(expdate);
		
			int userid = u.getId();
			String ip = u.getIp();

			String result = "0";
			boolean empty = true;
			StringBuilder sb = new StringBuilder();
			boolean required = ActivityValidate.validateRequired(processid, m, u);
			if (required) {
				boolean cvalidate = true;
				for (int i=0; i<types.length; i++) {
					String t = types[i];
					int lkupacttypeid = Operator.toInt(t);
					if (!ActivityValidate.checks(processid, typeid, lkupacttypeid, u,r)) {
						cvalidate = false;
					}
				}
				if (cvalidate) {
					for (int i=0; i<types.length; i++) {
						String t = types[i];
						if (Operator.hasValue(t)) {
							int lkupacttypeid = Operator.toInt(t);
							if (lkupacttypeid > 0) {
								String qtys = m.getString(t);
								String sdt = startdate;
								String adt = applieddate;
								String idt = issueddate;
								String edt = expdate;
								String aedt = appexpdate;
								String fdt = finaldate;

								if (Operator.hasValue(m.getString("START_DATE_"+lkupacttypeid))) {
									sdt = m.getString("START_DATE_"+lkupacttypeid);
								}

								if (Operator.hasValue(m.getString("APPLIED_DATE_"+lkupacttypeid))) {
									adt = m.getString("APPLIED_DATE_"+lkupacttypeid);
								}

								if (Operator.hasValue(m.getString("ISSUED_DATE_"+lkupacttypeid))) {
									idt = m.getString("ISSUED_DATE_"+lkupacttypeid);
								}

								if (Operator.hasValue(m.getString("EXP_DATE_"+lkupacttypeid))) {
									edt = m.getString("EXP_DATE_"+lkupacttypeid);
								}

								if (Operator.hasValue(m.getString("APPLICATION_EXP_DATE_"+lkupacttypeid))) {
									aedt = m.getString("APPLICATION_EXP_DATE_"+lkupacttypeid);
								}

								if (Operator.hasValue(m.getString("FINAL_DATE_"+lkupacttypeid))) {
									fdt = m.getString("FINAL_DATE_"+lkupacttypeid);
								}

								if(cc.equalsIgnoreCase("Y")) {
									Timekeeper now = new Timekeeper();
									now.addDay(90);
									edt = now.getString("YYYY-MM-DD");
								}
									
								int qty = 1;
								if (Operator.hasValue(qtys)) {
									qty = Operator.toInt(qtys, 1);
								}
								if (qty > 0) {
									for (int x=0; x<qty; x++) {
										boolean rvalidate =  ActivityValidate.checks(processid, typeid, lkupacttypeid, u,r);
										
										
										
										if(rvalidate) {
											int ia = addActivity(processid, entity, projectid, lkupacttypeid, description, lkupstatusid, calculated, declared, planchkreq, sdt, adt, idt, edt, aedt, fdt, online, sensitive, inherit, userid, ip, u, cc);
											if (ia > 0) {
												if (!empty) {
													sb.append(",");
												}
												sb.append(ia);
												empty = false;
											}
											result = Operator.toString(ia);
											String sg = r.getSubgroup();
											if (Operator.equalsIgnoreCase(sg, "vehicle")) {
												DataVO[] darr = DataVO.getSubData(r);
												VehicleAgent.add(-1, ia, darr, u.getId(), u.getIp());
											}
											
											if (r.getRef().equals("lso")&& r.getAppttypeid()>0) {
												Logger.info("REF_ACT_LSO inserttttttttttttttttttttttttttttttttttttt");
												insertRefLso(ia, r.getAppttypeid(), u.getId(),u.getIp());
											}
											RequestVO f = r.duplicate();
											TasksImpl.runImmediate(ia, f, u);
										}
										
										
									}
								}
							}
						}
					}
				}
				if (!empty) {
					result = sb.toString();
				}
			}
			return result;
		}
	}

	public static int updateActivity(int actid, String description, int lkupactstatusid, double valuationcalculated, double valuationdeclared, boolean dovaluationfees, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String online, String sensitive, String inherit, String sendemail, String notify, String comment, int userid, String ip, String cc) {
		if (actid < 1) { return -1; }
		int r = -1;
		Timekeeper now = new Timekeeper();
		
		if(cc.equalsIgnoreCase("Y")) {
			Timekeeper cct = new Timekeeper();
			now.addDay(90);
			expdate = now.getString("YYYY-MM-DD");
		}
		String command = ActivitySQL.update(actid, description,lkupactstatusid,  valuationcalculated, valuationdeclared, planchkreq, startdate, applieddate, issueddate, expdate, applexpdate, finaldate, online, sensitive, inherit, userid, ip, now, cc);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				int projectid = -1;
				if (db.next()) {
					projectid = db.getInt("PROJECT_ID");
				}
				command = ActivitySQL.insertStatus(actid, lkupactstatusid, userid);
				if (db.update(command)) {
					r = actid;
					addHistory(r, "activity", r, "update");

					boolean ue = true;
					command = ProjectSQL.getProjectType(projectid);
					if (db.query(command) && db.next()) {
						ue = db.equalsIgnoreCase("EXPIRES", "Y");
					}

					if (ue) {
						command = ProjectSQL.updateExpiration(projectid);
					}
					else {
						command = ProjectSQL.removeExpiration(projectid);
					}
					db.update(command);
				}
				if (Operator.equalsIgnoreCase(sendemail, "Y") && Operator.hasValue(notify)) {
					command = ActivitySQL.getAddress(actid);
					String address = "";
					if (db.query(command) && db.next()) {
						address = db.getString("ADDRESS");
					}
					command = ActivitySQL.getActivityCalc(actid);
					if (db.query(command) && db.next()) {
						Timekeeper d = new Timekeeper();
						String acttype = db.getString("TYPE");
						String actnbr = db.getString("ACT_NBR");
						LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
						m.put("DATA CURRENT AS OF", d.getString("MM/DD/YYYY @ HH:MM:SS"));
						m.put("ACTIVITY NUMBER", actnbr);
						m.put("ACTIVITY TYPE", acttype);
						m.put("ADDRESS", address);
						m.put("STATUS", db.getString("STATUS"));
						m.put("DESCRIPTION", db.getString("DESCRIPTION"));
						if (db.hasValue("ISSUED_DATE")) {
							m.put("ISSUED DATE", db.getString("ISSUED_DATE"));
						}
						if (db.hasValue("EXP_DATE")) {
							m.put("EXPIRATION DATE", db.getString("EXP_DATE"));
						}
						if (db.getDouble("FEE_AMOUNT") > 0 && db.equalsIgnoreCase("FINANCE_LOCK", "N")) {
							m.put("FEE AMOUNT", "$"+db.getString("FEE_AMOUNT"));
							m.put("FEE PAID", "$"+db.getString("FEE_PAID"));
							m.put("BALANCE DUE", "$"+db.getString("BALANCE_DUE"));
						}
						String[] recipients = Operator.split(notify, "|");
						for (int i=0; i<recipients.length; i++) {
							int recipient = Operator.toInt(recipients[i]);
							if (recipient > 0) {
								command = UsersSQL.getUsers(recipient);
								if (db.query(command) && db.next()) {
									String email = db.getString("EMAIL");
									boolean checknotify = Operator.s2b(db.getString("NOTIFY"));

									if (!Operator.hasValue(email) || !Operator.isEmail(email)) {
										email = db.getString("USERNAME");
									}
									if (Operator.hasValue(email) && Operator.isEmail(email) && checknotify) {
										String name = db.getString("NAME");
										String content = Email.genericTemplate(name, comment, m);
										String subject = "City of Beverly Hills: An update has been made to activity number "+actnbr;
										CommunicationsAgent.email("activity", actid, email, recipient, subject, content, userid, ip);
									}
								}
							}
						}
					}
				}
			}
			db.clear();
		}
		return r;
	}

	public static boolean addHistory(int actid, String module, int moduleid, String moduleaction) {
		if (actid < 1) { return false; }
		String command = ActivitySQL.getActivity(actid);
		boolean valid = false;
		String actnbr = "";
		int projectid = -1;
		String type = "";
		int lkupacttypeid = -1; 
		String description = "";
		String status = "";
		int lkupactstatusid = -1;
		int lkupactsubtypeid = -1;
		double valuationcalculated = 0;
		double valuationdeclared = 0;
		String planchkreq = "";
		String startdate = "";
		String applieddate = "";
		String issueddate = "";
		String expdate = "";
		String applexpdate = "";
		String finaldate = "";
		String planchkfeedate = "";
		String permitfeedate = "";
		String online = "";
		String sensitive = "";
		int qty = -1;
		String printed = "";
		String inherit = "";
		int createdby = -1;
		String createddate = "";
		String updated = "";
		int updatedby = -1;
		String updateddate = "";
		int workflowgoupid = -1;
		String active = "";
		String createdip = "";
		String updatedip = "";
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			if (db.getInt("ID") > 0) {
				actnbr = db.getString("ACT_NBR");
				projectid = db.getInt("PROJECT_ID");
				type = db.getString("TYPE");
				lkupacttypeid = db.getInt("LKUP_ACT_TYPE_ID");
				description = db.getString("DESCRIPTION");
				status = db.getString("STATUS");
				lkupactstatusid = db.getInt("LKUP_ACT_STATUS_ID");
				lkupactsubtypeid = db.getInt("LKUP_ACT_SUBTYPE_ID");
				valuationcalculated = db.getDouble("VALUATION_CALCULATED");
				valuationdeclared = db.getDouble("VALUATION_DECLARED");
				planchkreq = db.getString("PLAN_CHK_REQ");
				startdate = db.getString("START_DATE");
				applieddate = db.getString("APPLIED_DATE");
				issueddate = db.getString("ISSUED_DATE");
				expdate = db.getString("EXP_DATE");
				applexpdate = db.getString("APPLICATION_EXP_DATE");
				finaldate = db.getString("FINAL_DATE");
				planchkfeedate = db.getString("PLAN_CHK_FEE_DATE");
				permitfeedate = db.getString("PERMIT_FEE_DATE");
				online = db.getString("ONLINE");
				sensitive = db.getString("SENSITIVE");
				qty = db.getInt("QTY");
				printed = db.getString("PRINTED");
				inherit = db.getString("INHERIT");
				createdby = db.getInt("CREATED_BY");
				createddate = db.getString("CREATED_DATE");
				updated = db.getString("UPDATED");
				updatedby = db.getInt("UPDATED_BY");
				updateddate = db.getString("UPDATED_DATE");
				workflowgoupid = db.getInt("WORKFLOW_GROUP_ID");
				active = db.getString("ACTIVE");
				createdip = db.getString("CREATED_IP");
				updatedip = db.getString("UPDATED_IP");
				valid = true;
			}
		}
		db.clear();
		if (valid) {
			return addHistory(actid, actnbr, projectid, type, lkupacttypeid, lkupactsubtypeid, description, valuationcalculated, valuationdeclared, planchkreq, startdate, applieddate, issueddate, expdate, applexpdate, finaldate, planchkfeedate, permitfeedate, online, sensitive, status, lkupactstatusid, qty, printed, inherit, createdby, createddate, updated, updatedby, updateddate, workflowgoupid, active, createdip, updatedip, module, moduleid, moduleaction);
		}
		return false;
	}

	public static boolean addHistory(int actid, String actnbr, int projectid, String type, int lkupacttypeid, int lkupactsubtypeid, String description, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String planchkfeedate, String permitfeedate, String online, String sensitive, String status, int lkupactstatusid, int qty, String printed, String inherit, int createdby, String createddate, String updated, int updatedby, String updateddate, int workflowgoupid, String active, String createdip, String updatedip, String module, int moduleid, String modulaction) {
		if (actid < 1) { return false; }
		boolean r = false;
		String command = ActivitySQL.addHistory(actid, actnbr, projectid, type, lkupacttypeid, lkupactsubtypeid, description, valuationcalculated, valuationdeclared, planchkreq, startdate, applieddate, issueddate, expdate, applexpdate, finaldate, planchkfeedate, permitfeedate, online, sensitive, status, lkupactstatusid, qty, printed, inherit, createdby, createddate, updated, updatedby, updateddate, workflowgoupid, active, createdip, updatedip, module, moduleid, modulaction);
		Sage db = new Sage(CsApiConfig.getHistorySource());
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean autoAddActivity(String processid, String entity, int projectid, String description, double valuationcalculated, double valuationdeclared, String start, String applied, String exp, String applexp, String finaldate, String online, String[] acttypeids, String[] planchkreq, String[] inherit, int userid, String ip, Token u) {
		String command = ActivitySQL.getAutoAdd(acttypeids);
		return autoAddActivity(processid, command, entity, projectid, description, valuationcalculated, valuationdeclared, start, applied, exp, applexp, finaldate, online, planchkreq, inherit, userid, ip, u);
	}

	public static boolean autoAddActivity(String processid, String entity, int projectid, String description, double valuationcalculated, double valuationdeclared, String start, String applied, String exp, String applexp, String finaldate, String online, String[] planchkreq, String[] inherit, int userid, String ip, Token u) {
		String command = ActivitySQL.getAutoAdd(projectid);
		return autoAddActivity(processid, command, entity, projectid, description, valuationcalculated, valuationdeclared, start, applied, exp, applexp, finaldate, online, planchkreq, inherit, userid, ip, u);
	}

	private static boolean autoAddActivity(String processid, String command, String entity, int projectid, String description, double valuationcalculated, double valuationdeclared, String start, String applied, String exp, String applexp, String finaldate, String online, String[] planchkreq, String[] inherit, int userid, String ip, Token u) {
		boolean r = true;
		if (projectid > 0) {
			if (Operator.hasValue(command)) {
				ArrayList<Integer> acttypes = new ArrayList<Integer>();
				HashMap<Integer, Integer> actstatus = new HashMap<Integer, Integer>();
				HashMap<Integer, String> actdescription = new HashMap<Integer, String>();
//				HashMap<Integer, String> expiration = new HashMap<Integer, String>();
				Sage db = new Sage();
				if (db.query(command)) {
					while (db.next()) {
						int acttypeid = db.getInt("LKUP_ACT_TYPE_ID");
						int statusid = db.getInt("LKUP_ACT_STATUS_ID");
						String d = description;
						if (!Operator.hasValue(description)) {
							d = db.getString("DESCRIPTION");
						}
//						int daystillexpired = db.getInt("DAYS_TILL_EXPIRED");
//						int yearstillexpired = db.getInt("YEARS_TILL_EXPIRED");

						if (acttypeid > 0) {
							if (!Operator.hasValue(applied)) {
								Timekeeper a = new Timekeeper();
								applied = a.getString("DATECODE");
							}
//							String expdate = exp;
//							if (!Operator.hasValue(exp)) {
//								Timekeeper s = new Timekeeper();
//								if (Operator.hasValue(start)) {
//									s.setDate(start);
//								}
//								if (daystillexpired > 0 || yearstillexpired > 0) {
//									if (yearstillexpired > 0) {
//										s.addYear(yearstillexpired);
//									}
//									if (daystillexpired > 0) {
//										s.addDay(daystillexpired);
//									}
//									expdate = s.getString("DATECODE");
//								}
//							}

							acttypes.add(acttypeid);
							actstatus.put(acttypeid, statusid);
							actdescription.put(acttypeid, d);
	//						expiration.put(acttypeid, expdate);

						}
					}
				}
				db.clear();

				int asize = acttypes.size();
				int dsize = 70/asize;
				for (int i=0; i<asize; i++) {
					LogAgent.updateLog(processid, dsize);
					int actid = -1;
					int acttypeid = acttypes.get(i);
					int statusid = actstatus.get(acttypeid);
					String d = actdescription.get(acttypeid);
//					String expdate = expiration.get(acttypeid);

					String pcreq = "N";
					if (acttypeid > 0 && statusid > 0) {
						if (Operator.contains(planchkreq, Operator.toString(acttypeid))) { pcreq = "Y"; }
						String inh = "N";
						if (Operator.contains(inherit, Operator.toString(acttypeid))) { inh = "Y"; }
						actid = addActivity(processid, entity, projectid, acttypeid, d, statusid, valuationcalculated, valuationdeclared, pcreq, start, applied, "", exp, applexp, finaldate, online, "N", inh, userid, ip, u);
					}
					if (actid < 1) { r = false; }
				}

			}
		}
		return r;
	}

	public static int addActivity(String processid, String entity, int projectid, int lkupacttypeid, String description, int lkupactstatusid, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String online, String sensitive, String inherit, int userid, String ip, Token u) {
		return addActivity(processid, entity, projectid, lkupacttypeid, description, lkupactstatusid, valuationcalculated, valuationdeclared, planchkreq, startdate, applieddate, issueddate, expdate, applexpdate, finaldate, online, sensitive, inherit, userid, ip, u, "");
	}
	public static int addActivity(String processid, String entity, int projectid, int lkupacttypeid, String description, int lkupactstatusid, double valuationcalculated, double valuationdeclared, String planchkreq, String startdate, String applieddate, String issueddate, String expdate, String applexpdate, String finaldate, String online, String sensitive, String inherit, int userid, String ip, Token u, String cc) {
		if (lkupacttypeid < 1) { return -1; }
		int actid = -1;
		Timekeeper now = new Timekeeper();
		Sage db = new Sage();
		String command = "";
		if (lkupactstatusid < 1) {
			command = ActivitySQL.getDefaultStatus(lkupacttypeid);
			if (db.query(command) && db.next()) {
				lkupactstatusid = db.getInt("ID");
			}
		}
		LogAgent.updateLog(processid, "Add new activity: "+description, "Adding activity: "+projectid);
		command = ActivitySQL.add("", projectid, lkupacttypeid, description, lkupactstatusid, valuationcalculated, valuationdeclared, planchkreq, startdate, applieddate, issueddate, expdate, applexpdate, finaldate, online, sensitive, inherit, userid, ip, now, cc);
		if (Operator.hasValue(command)) {
			try {
				if (db.query(command) && db.next()) {
					actid = db.getInt("ID");
					if (actid > 0) {
						String actnbr = createActNbr(lkupacttypeid, db.getInt("ID"));
						LogAgent.updateLog(processid, "Update Activity Addl : "+actnbr, "Update Activity Addl: "+actnbr);
						command = ActivitySQL.updateActivityNumber(actid, actnbr);
						if(db.update(command)){
							command = ActivitySQL.insertStatus(actid, lkupactstatusid, userid);
							if (db.update(command)) {
								boolean ue = true;
								command = ProjectSQL.getProjectType(projectid);
								if (db.query(command) && db.next()) {
									ue = db.equalsIgnoreCase("EXPIRES", "Y");
								}

								if (ue) {
									command = ProjectSQL.updateExpiration(projectid);
								}
								else {
									command = ProjectSQL.removeExpiration(projectid);
								}
								db.update(command);
							}

							if(planchkreq.equalsIgnoreCase("Y")) {
								db.update(ActivitySQL.updateActDates(actid, null, null, userid, ip));
							}
							addHistory(actid, "activity", actid, "add");
						}
						else { actid = -1; }
					}
				}
			}
			catch (Exception e) { }
			
			if (actid > 0) {
				FinanceAgent.autosave(entity, "activity", actid, startdate, userid, Operator.s2b(planchkreq));
				ReviewAgent.autoCreateCombo(processid, "activity", actid, userid, ip, u);
				LibraryAgent.autoAdd("activity", actid, userid, ip);
			}
		}
		db.clear();
		return actid;
	}

	public static int saveActivityById(RequestVO r, Token u) {
		return saveActivityById(r, u, false);
	}

	public static int saveActivityById(RequestVO r, Token u, boolean autofee) {
		boolean result= false;
		int actid=0;
		try{
			int id = Operator.toInt(r.getId());
			String command ="";
			int newId = 0;
			Sage db = new Sage();
			if(id>0) {
				command = GeneralSQL.updateCommon(r,u);
				result = db.update(command);
				actid = id;
				if(result) {
					ObjVO v = Fields.getField(r.data[0], "LKUP_ACT_STATUS_ID");
					command = ActivitySQL.insertStatus(v,id,u.getId());
					result = db.update(command);
				}
			}
			else {
				command = GeneralSQL.insertCommon(r,u);
				if(Operator.hasValue(command)){
					result = db.update(command);
					
					if(result){
						
						command = CsReflect.commonQuery("getRefId", r, u);
						if(Operator.hasValue(command)){
							db.query(command);
							if(db.next()){
								newId = db.getInt("ID");
								
							}
							
							if(newId>0){
								Logger.info(newId);
								actid = newId;
								ObjVO v = Fields.getField(r.data[0], "LKUP_ACT_STATUS_ID");
								command = ActivitySQL.insertStatus(v,newId,u.getId());
								result = db.update(command);
							}
						}
						
					}
				}
			}
			
			db.clear();
			
			if(autofee){
				Timekeeper k = new Timekeeper();
				String feedatepick =k.getString("YYYY/MM/DD");
				ObjVO v = Fields.getField(r.data[0], "PLAN_CHK_REQ");
				FinanceAgent.autosave(r, feedatepick, u.getId(), newId,Operator.s2b(v.getValue()));
			
			}
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return actid;
	}

	public static BrowserItemsVO panels(String entity, int actid, String reference) {
		BrowserItemsVO r = new BrowserItemsVO();
		String command = ActivitySQL.getActivity(entity, actid, reference);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {

			BrowserItemVO e = new BrowserItemVO();
			e.setEntity(entity);
			e.setType(entity);
			e.setId(db.getString("ENTITY_ID"));
			e.setDataid(db.getString("ENTITY_ID"));
			r.addPanel("entity", e);

			BrowserItemVO t = new BrowserItemVO();
			t.setEntity(entity);
			t.setType("activity");
			t.setDataid(db.getString("ID"));
			t.setId(db.getString("ENTITY_ID"));
			r.addPanel("type", t);

			BrowserItemVO d = new BrowserItemVO();
			d.setEntity(entity);
			d.setType("activity");
			d.setDataid(db.getString("ID"));
			d.setId(db.getString("ID"));
			r.addPanel("detail", t);
		}
		db.clear();
		return r;
	}
	
	public static String createActNbr(int type_typeid, int outputid) {
		return GeneralAgent.getNumber("activity", type_typeid, outputid);
	}
	
	public static String getSystemGeneratedAct_nbr(RequestVO r){
		return GeneralAgent.getNumber(r);
	}
	
	public static String getSystemGeneratedProject_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}
	
	/*public static String getSystemGeneratedColumn(RequestVO r,String field){
		if(field.equals("ACT_NBR")){
			return getSystemGeneratedAct_nbr(r);
		}
		
		return Operator.toString(r.getTypeid());
	}
	
	public static String getSystemGeneratedValue(RequestVO r,String field){
		return Operator.toString(r.getTypeid());
	}*/
	
	
	
	public static JSONObject getPrintDetail(RequestVO r,JSONObject doList){
		JSONObject o = new JSONObject();
		try{
			Sage db = new Sage();
			String command = ActivitySQL.print(r.getType(), r.getTypeid());
			db.query(command);
			
			while(db.next()){
				o.put(db.getString("LABEL"),db.getString("FIELDVALUE"));
			}
			
			command = CustomSQL.print(r.getType(), r.getTypeid());
			db.query(command);
			while(db.next()){
				o.put(db.getString("LABEL"),db.getString("FIELDVALUE"));
			}
			
			
			
			
			
			db.clear();
			
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		return o;
	}
	
	
	public static ObjGroupVO[] print1(RequestVO r){
		ObjGroupVO[] oa = new ObjGroupVO[0];
	/*	oa[0] = getPrintDetail(r);
		oa[1] = FinanceAgent.printFeeSummary(r);
		oa[2] = FinanceAgent.printFinanceSummary(r);
		oa[3] = FinanceAgent.printFinanceLedger(r);
		oa[4] = NotesAgent.printNotes(r);
		oa[5] = LibraryAgent.printLibrary(r);
		oa[6] = PrintAgent.getStandards(r);*/
		
		return oa;
	}
	
	public static JSONObject print(RequestVO r,JSONObject doList){
		return getPrintDetail(r,doList);
	}

	public static HashMap<String, String> getPermitDetails(int actId, String actnbr) throws SQLException{
		
		
		String table = "Activity a ";
		List<String> select = new ArrayList<String>();
		select.add("a.ID");
		select.add("ACT_NBR");
		select.add("a.DESCRIPTION");
		select.add("CONVERT(VARCHAR(10),APPLIED_DATE,101) as APPLIED_DATE");
		select.add("CONVERT(VARCHAR(10),ISSUED_DATE,101) as ISSUED_DATE");
		select.add("CONVERT(VARCHAR(10),EXP_DATE,101) as EXP_DATE");
		select.add("CONVERT(VARCHAR(10),FINAL_DATE,101) as FINAL_DATE");
		select.add("VALUATION_DECLARED");
		select.add("lat.TYPE as TYPE");
		select.add("lst.SUBTYPE");
		select.add("las.DESCRIPTION STATUS");
		select.add("vad.ADDRESS+' '+vad.CITYSTATEZIP as ADDR");

		List<String> join = new ArrayList<String>();
		join.add("JOIN LKUP_ACT_STATUS las on a.LKUP_ACT_STATUS_ID = las.ID ");
		join.add("LEFT OUTER JOIN LKUP_ACT_SUBTYPE lst on a.LKUP_ACT_SUBTYPE_ID = lst.ID ");
		join.add("LEFT OUTER JOIN LKUP_ACT_TYPE lat on a.LKUP_ACT_TYPE_ID = lat.ID ");
		join.add("LEFT OUTER JOIN REF_LSO_PROJECT rlp on a.PROJECT_ID = rlp.PROJECT_ID AND rlp.ACTIVE='Y' ");
		join.add("JOIN V_ADDRESS_LIST vad on rlp.LSO_ID = vad.LSO_ID ");

		Map<String, String> where = new HashMap<String, String>();
		if(actId > 0)
			where.put("a.ID", Operator.toString(actId));
		if(Operator.hasValue(actnbr))
			where.put("a.ACT_NBR", Operator.toString(actnbr));
		
		return DbUtil.getObjectAsMap(table, where, select, join);
	}

	public static SubObjVO[] status(String acttypeids, int actid) {
		if (Operator.isNumber(acttypeids)) {
			return status(Operator.toInt(acttypeids), actid);
		}
		SubObjVO[] r = new SubObjVO[0];
		if (Operator.hasValue(acttypeids)) {
			String command = ActivitySQL.status(CsTools.paramToString(acttypeids));
			r = Choices.getChoices(command);
		}
		return r;
	}

	public static SubObjVO[] status(int acttypeid, int actid) {
		String command = ActivitySQL.status(acttypeid, actid);
		return Choices.getChoices(command);
	}

	public static SubObjVO[] actType(String type, int typeid, Token u) {
		return actType(type, typeid, u, false);
	}

	public static SubObjVO[] actType(String type, int typeid, Token u, boolean exemption) {
		String command = ActivitySQL.actType(type, typeid, exemption);
		ArrayList<SubObjVO> a = new ArrayList<SubObjVO>();
		Sage db = new Sage();
		db.query(command);
		if (db.size() > 0) {
			ArrayList<String> al = new ArrayList<String>();
			while(db.next()) {
				al.add(db.getString("ID"));
			}
			String[] arr = a.toArray(new String[a.size()]);
			HoldsList hl = HoldsAgent.getHolds(type, typeid, arr);
			db.beforeFirst();
			String[] cols = db.COLUMNS;
			while (db.next()) {
				boolean iscreate = true;
				int id = db.getInt("ID");
				boolean onhold = hl.actOnSignificantHold(id);
				if (!onhold) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("VALUE"));
					vo.setText(db.getString("TEXT"));
					vo.setDescription(db.getString("DESCRIPTION"));
					vo.setSelected(db.getString("SELECTED"));

					String expdate = "";
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
						expdate = exp.getString("YYYY/MM/DD");
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
		}
		db.clear();
		SubObjVO[] r = a.toArray(new SubObjVO[a.size()]);
		return r;
	}

  public static boolean checkActivityDivisions(int projectId,int acttypeid){
	  boolean result = false;
	  DivisionsList l = DivisionsAgent.getDivisions("project", projectId);
	  String divids = l.divisionIds();
	  Sage db = new Sage();
	  String command = ActivitySQL.getDivisions(projectId, acttypeid,divids);
	  db.query(command);
	  if(db.size()<=0){
		  result = true;
	  }
	  
	  while(db.next()){
		  if(db.getInt("CHECKED")==1){
			  result = true;
			  break;
		  }
	  }
	  
	  
	  
	  db.clear();
	  
	  return result;
	  
  }
  
  
  
  
  public static boolean checkProjectStatus(int projectId){
	  boolean result = true;
	  
	 
	  
	  Sage db = new Sage();
	  String command = ActivitySQL.checkProjectStatus(projectId);
	  db.query(command);
	  while(db.next()){
		  if(Operator.equalsIgnoreCase("Y", db.getString("EXPIRED"))){
			  result = false;
		  }
	  }
  
  
  
	  db.clear();
	  
	  
	  return result;
	  
  }
  
  
  public static int checkMax(RequestVO vo,int projectId, MapSet v){
	  int count = 0;
	  Sage db = new Sage();
	  String command = ActivitySQL.checkMax(vo,projectId, v);
	  db.query(command);
	 
	  while(db.next()){
		 /* if(db.getInt("COUNT")>=v.getInt("DURATION_MAX")){
			  result = false;
		  }*/
		  count = db.getInt("COUNT");
		  
		  /*if(db.getInt("COUNT")==v.getInt("MAX_ALLOWED")){
			  result = false;
		  }*/
	  }
	  db.clear();
	  return count;
	  
  }
  
  public static int checkMaxDaytimePeryear(int projectId){
	  int count = 0;
	  Sage db = new Sage();
	  String command = ActivitySQL.checkMaxDayTimePerYear(projectId);
	  db.query(command);
	 
	  while(db.next()){
		 /* if(db.getInt("COUNT")>=v.getInt("DURATION_MAX")){
			  result = false;
		  }*/
		  count = db.getInt("COUNT");
		  
		  /*if(db.getInt("COUNT")==v.getInt("MAX_ALLOWED")){
			  result = false;
		  }*/
	  }
	  db.clear();
	  return count;
	  
  }

  public static int[] getProjectAndActivityType(int actid) {
	  int[] i = new int[2];
	  i[0] = -1;
	  i[1] = -1;
	  Sage db = new Sage();
	  String command = ActivitySQL.getActivityAndProjectType(actid);
	  if (db.query(command) && db.next()) {
		  i[0] = db.getInt("LKUP_PROJECT_TYPE_ID");
		  i[1] = db.getInt("LKUP_ACT_TYPE_ID");
	  }
	  db.clear();
	  return i;
  }

  
  public static boolean updateActivity(String actid, String printed, String batchid){
	  Sage db = new Sage();
	  String command = ActivitySQL.updateActivity(actid, printed, batchid);
	  db.update(command);
	  db.clear();
	  return true;
  }
 
  

  public static String getActivityId(String projectid, String group, int batchid, String startdate, String enddate) {
	  Sage db = new Sage();
	  //String command = ActivitySQL.getActivityId(projectid, group, groupid, startdate, enddate);
	  String command = ProjectSQL.getParkingPermits(projectid, group, startdate, enddate, false, batchid);
	  db.query(command);
	  StringBuffer sb = new StringBuffer();
	  int i = 1;
	  while(db.next()){
		  sb.append(db.getString("ID"));
		  if(i < db.size() ){
			  sb.append(",");
		  }
		  i++;
	  }
	  db.clear();
	  return sb.toString();
  }
  
  public static String getActivityIdRenewal(String projectid, String type, String typeid, String startdate, String enddate) {
	  Sage db = new Sage();
	  String command = ActivitySQL.getActivityId(projectid, type, typeid, startdate, enddate);
	  //String command = ProjectSQL.getParkingPermits(projectid, group, startdate, enddate, true, groupid);
	  db.query(command);
	  StringBuffer sb = new StringBuffer();
	  int i = 1;
	  while(db.next()){
		  sb.append(db.getString("ID"));
		  if(i < db.size() ){
			  sb.append(",");
		  }
		  i++;
	  }
	  db.clear();
	  return sb.toString();
  }

	public static ResponseVO statusDefaultIssued(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		Sage db = new Sage();
		
		try{
			String[] ids = Operator.split(vo.getId(), ",");
			for(String id : ids){
				String command = ActivitySQL.statusDefaultIssued(vo.getType(), id, u.getId());
				db.update(command);
			}
			
			r.setMessagecode("cs200");
		}catch(Exception e){
			r.setMessagecode("cs500");
		}
		db.clear();
		return r;
	}

	public static ColorList getColor() {
		ColorList l = new ColorList();
		String command = ActivitySQL.getColors(-1, -1);
		Sage db = new Sage();
		db.query(command);
		while (db.next()) {
			int year = db.getInt("EXP_YEAR");
			int typeid = db.getInt("LKUP_ACT_TYPE_ID");
			String type = db.getString("DESCRIPTION");
			String color = db.getString("HEX_COLOR");
			String style = db.getString("STYLE");
			String label = db.getString("LABEL");
			l.addColor(year, typeid, type, color, style, label);
		}
		command = ActivitySQL.getDefaultColors();
		db.query(command);
		while (db.next()) {
			int year = db.getInt("EXP_YEAR");
			int typeid = db.getInt("LKUP_ACT_TYPE_ID");
			String type = db.getString("DESCRIPTION");
			String color = db.getString("HEX_COLOR");
			String style = db.getString("STYLE");
			String label = db.getString("LABEL");
			l.addDefault(year, typeid, type, color, style, label);
		}
		db.clear();
		return l;
	}

	public static int getDefaultStatus(int acttypeid) {
		int r = -1;
		String command = ActivitySQL.getDefaultStatus(acttypeid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}



	

	public static LinkedHashMap<String, String> getContentData(int actid) {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		Sage db = new Sage();
		String address = "";
		String command = ActivitySQL.getAddress(actid);
		if (db.query(command) && db.next()) {
			address = db.getString("ADDRESS");
		}

		command = ActivitySQL.getActivityCalc(actid);
		if (Operator.hasValue(command)) {
			if (db.query(command) && db.next()) {
				Timekeeper d = new Timekeeper();
				String acttype = db.getString("TYPE");
				String actnbr = db.getString("ACT_NBR");
				m.put("DATA CURRENT AS OF", d.getString("MM/DD/YYYY @ HH:MM:SS"));
				m.put("ACTIVITY NUMBER", actnbr);
				m.put("ADDRESS", address);
				m.put("ACTIVITY TYPE", acttype);
				m.put("STATUS", db.getString("STATUS"));
				m.put("DESCRIPTION", db.getString("DESCRIPTION"));
				if (db.hasValue("ISSUED_DATE")) {
					m.put("ISSUED DATE", db.getString("ISSUED_DATE"));
				}
				if (db.hasValue("EXP_DATE")) {
					m.put("EXPIRATION DATE", db.getString("EXP_DATE"));
				}
				if (db.getDouble("FEE_AMOUNT") > 0 && db.equalsIgnoreCase("FINANCE_LOCK", "N")) {
					m.put("FEE AMOUNT", "$"+db.getString("FEE_AMOUNT"));
					m.put("FEE PAID", "$"+db.getString("FEE_PAID"));
					m.put("BALANCE DUE", "$"+db.getString("BALANCE_DUE"));
				}
			}
		}
		db.clear();
		return m;
	}

	public static ResponseVO copy(String processid, int actid, String projnbr, int status, String applieddate, String startdate, String issueddate, String appexpdate, String expdate, String finaldate, String permitfeedate, Token u) {
//		TypeInfo e = EntityAgent.getEntity("activity", actid);
		ResponseVO r = new ResponseVO();
		String command = "";
		int proj = -1;
		Sage db = new Sage();
		if (Operator.hasValue(projnbr)) {
			command = ProjectSQL.getProject(projnbr);
			if (db.query(command) && db.next()) {
				proj = db.getInt("ID");
			}
		}
		if (proj > 0) {
			command = ActivitySQL.getActivity(actid);
			if (db.query(command) && db.next()) {
				int acttypeid = db.getInt("LKUP_ACT_TYPE_ID");
				String oldactnbr = db.getString("ACT_NBR");
				command = ActivitySQL.copy(actid, proj, "", acttypeid, db.getInt("LKUP_ACT_SUBTYPE_ID"), status, db.getString("DESCRIPTION"), db.getDouble("VALUATION_CALCULATED"), db.getDouble("VALUATION_DECLARED"), db.getString("PLAN_CHK_REQ"), startdate, applieddate, issueddate, expdate, appexpdate, finaldate, db.getString("PLAN_CHK_FEE_DATE"), permitfeedate, db.getString("ONLINE"), db.getString("SENSITIVE"), db.getString("INHERIT"), db.getInt("QTY"), db.getInt("WORKFLOW_GROUP_ID"), u.getId(), u.getIp());
				if (db.query(command) && db.next()) {
					int newid = db.getInt("ID");
					String actnbr = createActNbr(acttypeid, newid);
					if (Operator.hasValue(actnbr)) {
						command = ActivitySQL.updateActivityNumber(newid, actnbr);
						if (db.update(command)) {
							addHistory(newid, "activity", newid, "copied "+oldactnbr);
							r.setMessagecode("cs200");
							r.setId(newid);
							r.setReference(actnbr);
							r.addMessage("Activity has been successfully copied. The new activity number is "+actnbr);
						}
						else {
							r.setMessagecode("cs500");
							r.addError("Unable to save new activity number.");
						}
					}
					else {
						r.setMessagecode("cs500");
						r.addError("Unable to create activity number.");
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Unable to save new activity.");
				}
			}
		}
		else {
			r.setMessagecode("cs500");
			r.addError("Unable to retrieve project ID.");
		}
		db.clear();
		return r;
	}
	
	
	public static boolean isIssued(int actid) {
		if (actid < 1) { return false; }
		boolean r = false;
		String command = ActivitySQL.getStatus(actid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			r = db.equalsIgnoreCase("ISSUED", "Y");
		}
		db.clear();
		return r;
	}
	
	public static boolean isPublicIssued(int actid) {
		if (actid < 1) { return false; }
		boolean r = false;
		String command = ActivitySQL.getStatus(actid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			r = db.equalsIgnoreCase("ISPUBLIC", "Y");
		}
		db.clear();
		return r;
	}

	public static boolean move(int actid, int newprojid, Token u) {
		if (actid < 1) { return false; }
		if (newprojid < 1) { return false; }
		boolean r = false;
		String command = ActivitySQL.move(actid, newprojid, u.getId(), u.getIp());
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}

	public static boolean insertRefLso(int actid,int lsoId,int userId,String ip) {
		if (actid < 1) { return false; }
		boolean r = false;
		String command = ActivitySQL.insertRefLso(actid, lsoId, userId, ip);
		Sage db = new Sage();
		r = db.update(command);
		db.clear();
		return r;
	}
	
	public static boolean significantHold(String type,int typeid){
		boolean r = false;
		HoldsList h = HoldsAgent.getActivityHolds(type, typeid);
		if(h.actOnSignificantHold()){
			r = true;
		}
		return r;
		
	}
	
	public static String ids(String username, String search, String option,int start,int noofrecords){
		StringBuilder sb = new StringBuilder();
		Sage db = new Sage();
		String command = ActivitySQL.myActivities(username, search, option, start, noofrecords);
		
		db.query(command);
		while(db.next()){
			sb.append(db.getInt("ID"));
			sb.append(",");
			
		}
		
		
		sb.append("0");
		
		Logger.info(sb.toString());
		db.clear();
		
		return sb.toString();
	}

	
	
	public static HashMap<String, String> getPermitInfoDetails(String permitNumber){
		HashMap<String, String> hm = new HashMap<String, String>();
		Sage db = new Sage();
		
		try{
		String command = ActivitySQL.permitInfo(permitNumber);
		db.query(command);
		
		if(db.next()){
			hm.put("PARCEL_ID", db.getString("PARCEL_ID"));
			hm.put("ID", db.getString("ID"));
			hm.put("ACT_NBR", db.getString("ACT_NBR"));
			hm.put("VALUATION_CALCULATED", db.getString("VALUATION_CALCULATED"));
			hm.put("PROJECT_NUMBER", db.getString("PROJECT_NUMBER"));
			hm.put("ISSUED_DATE", db.getString("ISSUED_DATE"));
			hm.put("EXP_DATE", db.getString("EXP_DATE"));
			hm.put("APPLIED_DATE", db.getString("APPLIED_DATE"));
			hm.put("ADDR", db.getString("ADDR"));
			hm.put("HOLDS", "NO");
			hm.put("ACTIVITY_TYPE", db.getString("ACTIVITY_TYPE"));
			hm.put("LINK", db.getString("LINK"));
			hm.put("ACTIVITY_DESCRIPTION", db.getString("ACTIVITY_DESCRIPTION"));
			
			JSONArray peoplearr = new JSONArray(db.getString("LIST_PEOPLE_PRIMARY"));
			Logger.info("peoplearr-------"+peoplearr.toString());
			for(int i=0;i<peoplearr.length();i++){
				JSONObject people = peoplearr.getJSONObject(i);
				
				Logger.info("people-------"+people.toString());
				
				//hm.put("user_id", people.getInt("user_id")+"");
				hm.put("PRIMARY_CONTACT_NAME", people.getString("USER_NAME"));
				hm.put("USER_USERNAME", people.getString("USER_USERNAME"));
				hm.put("PRIMARY_CONTACT_EMAIL", people.getString("USER_EMAIL"));
				hm.put("USER_FNAME", people.getString("USER_FNAME"));
				hm.put("USER_LNAME", people.getString("USER_LNAME"));
				hm.put("PRIMARY_CONTACT_PHONE", people.getString("PHONE"));
			}
			
			//hm.put("LAYERS", db.getString("LIST_DIVISIONS").toUpperCase());
			JSONArray divisionarr = new JSONArray(db.getString("LIST_DIVISIONS"));
			for(int i=0;i<divisionarr.length();i++){
				JSONObject division = divisionarr.getJSONObject(i);
				hm.put(division.getString("TYPE").toUpperCase(), division.getString("DIVISION").toUpperCase());
				
				if(division.getInt("LKUP_DIVISIONS_TYPE_ID")==14){
					hm.put("SEISMIC_RETROFIT", division.getString("DIVISION").toUpperCase());
				}
				if(division.getInt("LKUP_DIVISIONS_TYPE_ID")==3){
					hm.put("ZONING", division.getString("DIVISION").toUpperCase());
				}
				if(division.getInt("LKUP_DIVISIONS_TYPE_ID")==11){
					hm.put("ZONING_AREA_DESIGNATION", division.getString("DIVISION").toUpperCase());
				}
				
			}
			
		}
		}catch(Exception e){
			Logger.info(e.getMessage());
		}
		
		db.clear();
		
		return hm;
	}
	
	
	public static HashMap<String, String> getPermitInfoDetails1(String permitNumber) throws SQLException{
		
		
		String table = "Activity a ";
		List<String> select = new ArrayList<String>();
		select.add("a.ID");
		select.add("ACT_NBR");
		select.add("a.DESCRIPTION as ACTIVITY_DESCRIPTION");
		select.add("CONVERT(VARCHAR(10),APPLIED_DATE,101) as APPLIED_DATE");
		select.add("CONVERT(VARCHAR(10),ISSUED_DATE,101) as ISSUED_DATE");
		select.add("CONVERT(VARCHAR(10),EXP_DATE,101) as EXP_DATE");
		select.add("VALUATION_CALCULATED");
		select.add("P.PROJECT_NBR AS PROJECT_NUMBER");
		select.add("P.NAME AS PROJECT_NAME");
		select.add("'https://cs.beverlyhills.org/cs/?entity=lso&type=activity&_typeid='+CONVERT(VARCHAR(15),A.ID) AS LINK");
		select.add("lat.TYPE as ACTIVITY_TYPE");
		select.add("vad.ADDRESS+' '+vad.CITYSTATEZIP as ADDR");
		select.add("'4345011001' AS PARCEL_ID");
		select.add("'NO' AS HOLDS");
		select.add("VCD.DIVISION AS ZONING");
		
		select.add("VCD1.DIVISION AS SEISMIC_RETROFIT");
		select.add("VCD2.DIVISION AS ZONING_AREA_DESIGNATION");
		select.add("'Alexzandria Lexus Kocsy, (424) 337-8853, alexzandria.kocsy@andersencorp.com' AS PRIMARY_CONTACT");
		

		List<String> join = new ArrayList<String>();
		join.add("JOIN LKUP_ACT_STATUS las on a.LKUP_ACT_STATUS_ID = las.ID ");
		
		join.add("LEFT OUTER JOIN LKUP_ACT_TYPE lat on a.LKUP_ACT_TYPE_ID = lat.ID ");
		join.add("LEFT OUTER JOIN PROJECT p on a.PROJECT_ID = p.ID ");
		join.add("LEFT OUTER JOIN REF_LSO_PROJECT rlp on a.PROJECT_ID = rlp.PROJECT_ID ");
		join.add("JOIN V_ADDRESS_LIST vad on rlp.LSO_ID = vad.LSO_ID ");
		join.add("LEFT OUTER JOIN V_CENTRAL_DIVISION VCD ON VAD.LSO_ID=VCD.LSO_ID AND VCD.TYPE='ZONE' ");
		join.add("LEFT OUTER JOIN V_CENTRAL_DIVISION VCD1 ON VAD.LSO_ID=VCD1.LSO_ID AND VCD1.TYPE='Seismic Soft-Story' ");
		join.add("LEFT OUTER JOIN V_CENTRAL_DIVISION VCD2 ON VAD.LSO_ID=VCD2.LSO_ID AND VCD2.TYPE='Zoning Area Designation' ");
		join.add("LEFT OUTER JOIN REF_LSO_APN RLA ON VAD.LSO_ID=RLA.LSO_ID ");
		Map<String, String> where = new HashMap<String, String>();
		if(Operator.hasValue(permitNumber))
			where.put("a.ACT_NBR", Operator.toString(permitNumber));
		
		
		return DbUtil.getObjectAsMap(table, where, select, join);
	}

	public static SubObjVO[] getActTypedates(String type, int typeid, Token u) {
		String command = ActivitySQL.getActTypedates(type, typeid);
		ArrayList<SubObjVO> a = new ArrayList<SubObjVO>();
		Sage db = new Sage();
		db.query(command);
		if (db.size() > 0) {
			ArrayList<String> al = new ArrayList<String>();
			while(db.next()) {
				al.add(db.getString("ID"));
			}
			String[] arr = a.toArray(new String[a.size()]);
			HoldsList hl = HoldsAgent.getHolds(type, typeid, arr);
			db.beforeFirst();
			String[] cols = db.COLUMNS;
			while (db.next()) {
				boolean iscreate = true;
				int id = db.getInt("ID");
				boolean onhold = hl.actOnSignificantHold(id);
				if (!onhold) {
					SubObjVO vo = new SubObjVO();
					vo.setId(db.getInt("ID"));
					vo.setValue(db.getString("VALUE"));
					vo.setText(db.getString("TEXT"));
					vo.setDescription(db.getString("DESCRIPTION"));
					vo.setSelected(db.getString("SELECTED"));

					String expdate = "";
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
						expdate = exp.getString("YYYY/MM/DD");
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
		}
		db.clear();
		SubObjVO[] r = a.toArray(new SubObjVO[a.size()]);
		return r;
	}

	public static void updatePlanActivity(int activityid, Token u) {
		SubObjVO[] v = getActTypedates("activity", activityid, u);
		String planreq = v[0].getAddldata().get("PLAN_CHK_REQ");
		if(planreq.equalsIgnoreCase("Y")) {
			String command  = ActivitySQL.updateActDates(activityid, v[0].getAddldata().get("PERMIT_EXPIRE"), v[0].getAddldata().get("APPLICATION_EXP_DATE"), u.getId(), u.getIp());
			Sage db = new Sage();
			db.update(command);
		}
	}

}




