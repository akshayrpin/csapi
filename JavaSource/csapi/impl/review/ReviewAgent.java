package csapi.impl.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.common.LkupCache;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.appointment.AppointmentAgent;
import csapi.impl.attachments.AttachmentsSQL;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.project.ProjectSQL;
import csapi.impl.users.UsersSQL;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.Email;
import csapi.utils.objtools.Modules;
import csapi.utils.validate.ValidateUtil;
import csshared.vo.AppointmentScheduleVO;
import csshared.vo.AppointmentVO;
import csshared.vo.ComboReviewGroup;
import csshared.vo.ComboReviewList;
import csshared.vo.ComboReviewVO;
import csshared.vo.DataVO;
import csshared.vo.HoldsList;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.ReviewActionVO;
import csshared.vo.ReviewAttachmentVO;
import csshared.vo.ReviewTeamVO;
import csshared.vo.ReviewVO;
import csshared.vo.SubObjVO;
import csshared.vo.lkup.RolesVO;

public class ReviewAgent {

//	public static boolean save(RequestVO r, Token u){
//		boolean result= false;
//		try{
//			int id = Operator.toInt(r.getId());
//			String command ="";
//			Sage db = new Sage();
//			if(id>0){
//				/*command = GeneralSQL.updateCommon(r,u);
//				Logger.info("********"+command);
//				result = db.update(command);*/
//				
//				/*if(result){
//					ObjVO v = Fields.getField(r.data[0], "LKUP_ACT_STATUS_ID");
//					command = ActivitySQL.insertStatus(v,id,u.getUserId());
//					Logger.info("********"+command);
//					result = db.update(command);
//				}*/
//			}
//			else {
//				command = GeneralSQL.insertCommon("REVIEW_ACTION",r,u);
//				if(Operator.hasValue(command)){
//					result = db.update(command);
//					
//					if(result){
//						
//						command = CsReflect.commonQuery("getRefId",r,u);
//						int newId = 0;
//						if(Operator.hasValue(command)){
//							db.query(command);
//							if(db.next()){
//								newId = db.getInt("ID");
//								
//							}
//							
//							if(newId>0){
//								Logger.info(newId);
//								command = CsReflect.commonQuery("insertRef",r,u,newId);
//								result = db.update(command);
//							}
//						}
//						
//					}
//				}
//			}
//			
//			db.clear();
//			
//		}
//		catch(Exception e){
//			Logger.error(e.getMessage());
//			
//		}
//		
//		return result;
//	}
//	

//	public static ResponseVO saveAction(RequestVO vo, Token u) {
//	DataVO m = CsTools.toHashMap(vo);
//	return saveAction(vo.getReviewrefid(), Operator.toInt(m.get("LKUP_REVIEW_STATUS_ID")), m.get("REVIEW_COMMENTS"), m.get("REF_TEAM_ID"), m.get("ATTACHMENT_TITLE"), m.get("ATTACHMENT"), Operator.toInt(m.get("ATTACHMENT_TYPE_ID")), m.get("ATTACHMENT_DESCRIPTION"), m.get("ATTACHMENT_PUBLIC"), u.getId(), u.getIp());
//	}

	public static String getSystemGeneratedAct_nbr(RequestVO r){
		return Operator.randomString(9);
	}
	
	public static String getSystemGeneratedProject_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}

	// Created after changes in review logic

	public static ResponseVO scheduleInspection(String type, int typeid, int reviewid, String comment, String collaborators, String inspectors, String date, String time, String title, int userid, String ip, Token u) {
		ResponseVO r = new ResponseVO();
		boolean existing = false;
		Sage db = new Sage();
		try {
			String command = "";
			if (!Operator.hasValue(inspectors)) {
				command = ReviewSQL.getReviewTeam(type, typeid, reviewid);
				if (Operator.hasValue(command)) {
					if (db.query(command) && db.next()) {
						inspectors = db.getString("REF_TEAM_ID");
					}
				}
			}
			command = ReviewSQL.getCurrentInspection(type, typeid, reviewid);
			if (db.query(command) && db.next()) {
				int comboid = db.getInt("COMBOREVIEW_ID");
				int reviewrefid = db.getInt("REF_COMBOREVIEW_REVIEW_ID");
				int statusid = -1;
				if (comboid > 0 && reviewrefid > 0) {
					existing = true;
					command = ReviewSQL.getReview(reviewid);
					if (db.query(command) && db.next()) {
						statusid = db.getInt("SCHEDULE_INSPECTION_ID");
					}

					if (statusid > 0) {
						r = saveCombo(true, type, typeid, comboid, reviewrefid, reviewid, statusid, "", comment, collaborators, inspectors, "", date, time, "", "", -1, "", "", "", userid, ip, u);
					}
					else {
						r.setMessagecode("cs412");
						r.addError("No inspection scheduling status has been found for this review.");
					}
				}
			}
			if (!existing) {
				r = createInspectionCombo(type, typeid, reviewid, comment, collaborators, inspectors, date, time, title, userid, ip, true, u);
			}
			
			
		} catch (Exception e) { }
		db.clear();
		return r;
	}

	public static ResponseVO createInspectionCombo(String type, int typeid, int reviewid, String comment, String collaborators, String inspectors, String date, String time, String title, int userid, String ip, boolean doreview, Token u) {
		ResponseVO r = new ResponseVO();
		int reviewgroupid = -1;
		int statusid = -1;
		if (reviewid > 0) {
			String command = ReviewSQL.getReview(reviewid);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				reviewgroupid = db.getInt("REVIEW_GROUP_ID");
				statusid = db.getInt("SCHEDULE_INSPECTION_ID");
			}
			db.clear();
		}
		if (statusid < 1) {
			r.setMessagecode("cs412");
			r.addError("Review status is not found in the request.");
		}
		else if (reviewgroupid < 1) {
			r.setMessagecode("cs412");
			r.addError("Review group is not found in the request.");
		}
		else {
			r = createCombo("", type, typeid, reviewgroupid, reviewid, statusid, "", comment, collaborators, inspectors, "", date, time, title, "", "", "", -1, "", "", userid, ip, doreview, u);
		}
		return r;
	}

	public static ResponseVO saveCombo(RequestVO vo, Token u) {
		DataVO m = DataVO.toDataVO(vo);
		return saveCombo(false, vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), vo.getReviewrefid(), Operator.toInt(m.get("REVIEW_ID")), Operator.toInt(m.get("LKUP_REVIEW_STATUS_ID")), m.get("DUE_DATE"), m.get("REVIEW_COMMENTS"), m.get("USERS_ID"), m.get("INSPECTOR_ID"), m.get("REF_TEAM_ID"), m.get("DATE"), m.get("TIME"), m.get("ATTACHMENT_TITLE"), m.get("ATTACHMENT"), Operator.toInt(m.get("ATTACHMENT_TYPE_ID")), m.get("ATTACHMENT_DESCRIPTION"), m.get("ATTACHMENT_PUBLIC"), m.get("NOTIFY"), u.getId(), u.getIp(), u);
	}

	public static ResponseVO saveCombo(boolean validate, String type, int typeid, int comboid, int reviewrefid, int reviewid, int statusid, String due, String comment, String collaborators, String inspectors, String team, String date, String time, String attachtitle, String attachpath, int attachtypeid, String attachdesc, String attachpublic, String notify, int userid, String ip, Token u) {

		ResponseVO r = new ResponseVO();

		if (comboid > 0) {
			Timekeeper now = new Timekeeper();
			ValidateUtil v = new ValidateUtil();
			r.setMessagecode("cs200");
			r.setId(comboid);
			if (Operator.hasValue(date) && Operator.hasValue(time)) {
				v = AppointmentAgent.validateAvailability("", type, typeid, reviewid, date, time, u);
			}

			if ((!validate || v.isValid()) && reviewrefid > 0) {
				r = saveAction(r, v, type, typeid, comboid, reviewrefid, statusid, date, due, time, comment, attachpath, attachtitle, attachdesc, attachtypeid, attachpublic, collaborators, inspectors, team, notify, userid, ip, now);
			}
			else if (v.isValid()) {
				r = addReview(r, comboid, reviewid, userid, ip, now);
				if (r.isValid()) {
					reviewrefid = Operator.toInt(r.getInfo("REF_COMBOREVIEW_REVIEW_ID"));
					r = saveAction(r, v, type, typeid, comboid, reviewrefid, statusid, date, due, time, comment, attachpath, attachtitle, attachdesc, attachtypeid, attachpublic, collaborators, inspectors, team, notify, userid, ip, now);
				}
			}
			else {
				r = v.getResponse();
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Combo ID is not found in the request.");
		}
		
		return r;
	}

	public static ResponseVO saveAction(ResponseVO r, ValidateUtil v, String type, int typeid, int comboid, int reviewrefid, int statusid, String date, String due, String time, String comment, String attachpath, String attachtitle, String attachdesc, int attachtypeid, String attachpublic, String collaborators, String inspectors, String team, String notify, int userid, String ip, Timekeeper now) {
		if (v.isValid()) {
			int actionrefid = -1;
			r = addAction(r, reviewrefid, statusid, due, comment, userid, ip, now);
			if (r.isValid()) {
				actionrefid = Operator.toInt(r.getInfo("REF_COMBOREVIEW_ACTION_ID"));
				if (Operator.hasValue(date) && Operator.hasValue(time)) {
					r = addAppointment(r, v, comboid, date, time, type, typeid, reviewrefid, actionrefid, comment, collaborators, inspectors, userid, ip, now);
				}
				if (r.isValid()) {
					if (Operator.hasValue(attachpath)) {
						r = addAttachment(r, actionrefid, attachpath, attachtitle, attachdesc, attachtypeid, attachpublic, userid, ip, now);
					}
					if (Operator.hasValue(team)) {
						r = addTeam(r, reviewrefid, actionrefid, team, userid, ip);
					}
					//if (Operator.hasValue(notify)) {
						notify("", actionrefid, notify, userid, ip);
					//}
				}
			}
		}
		return r;
	}


	public static ResponseVO addReview(ResponseVO r, int comboid, int reviewid, int userid, String ip, Timekeeper now) {
		String command = ReviewSQL.addComboReview(comboid, reviewid, userid, ip, now);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			try {
				if (db.update(command)) {
					command = ReviewSQL.getReview(comboid, reviewid, userid, now);
					if (db.query(command) && db.next()) {
						r.addInfo("REF_COMBOREVIEW_REVIEW_ID", db.getString("ID"));
						r.addInfo("COMBOREVIEW_ID", Operator.toString(comboid));
						r.addInfo("REVIEW_ID", Operator.toString(reviewid));
					}
					else {
						r.setMessagecode("cs500");
						r.addError("Could not verify addition of review.");
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Could not add review to the database.");
				}
			} catch (Exception e) { }
			db.clear();
		}
		else {
			r.setMessagecode("cs500");
			r.addError("Could not add review to the database.");
		}
		return r;
	}

	public static ResponseVO addAction(ResponseVO r, int reviewrefid, int statusid, String due, String comment, int userid, String ip, Timekeeper now) {
		return addAction(r, reviewrefid, statusid, due, comment, userid, ip, now, true);
	}

	public static ResponseVO addAction(ResponseVO r, int reviewrefid, int statusid, String due, String comment, int userid, String ip, Timekeeper now, boolean updateduedate) {
		Sage db = new Sage();
		try {
			int previd = -1;

			AppointmentAgent.completeReview(reviewrefid, -1, "", userid, ip);
			boolean previnspection = false;
			boolean currinspection = false;
			int reviewid = -1;

			String command = ReviewSQL.getCurrentAction(reviewrefid);
			if (db.query(command) && db.next()) {
				previd = db.getInt("ID");
				r.addInfo("PREVIOUS_REF_COMBOREVIEW_ACTION_ID", db.getString("ID"));
				previnspection = db.equalsIgnoreCase("SCHEDULE_INSPECTION", "Y");
				reviewid = db.getInt("REVIEW_ID");
			}

			String reviewinherit = "N";
			command = ReviewSQL.getStatus(statusid);
			if (db.query(command) && db.next()) {
				reviewinherit = db.getString("INHERIT");
				currinspection = db.equalsIgnoreCase("SCHEDULE_INSPECTION", "Y");
				int daystilldue = db.getInt("DAYS_TILL_DUE");
				if (daystilldue > 0) {
					Timekeeper dtd = new Timekeeper();
					dtd.addDay(daystilldue);
					due = dtd.getString("DATECODE");
					updateduedate = true;
				}
			}

			if (updateduedate) {
				command = ReviewSQL.updateDue(reviewrefid, due, userid, ip);
				Logger.highlight(command);
				db.update(command);
			}

			if (previnspection && currinspection && reviewid > 0) {
				command = ReviewSQL.getInspectionCancelStatus(reviewid);
				if (db.query(command) && db.next()) {
					int cancelstatus = db.getInt("ID");
					Timekeeper before = now.copy();
					before.addSecond(-1);
					command = ReviewSQL.addComboAction(reviewrefid, cancelstatus, "Automated cancellation for id "+previd+" because of a new appointment request.", previd, userid, ip, before);
					if (db.query(command)) {
						if (db.next()) {
							previd = db.getInt("ID");
							Logger.highlight(previd);
						}
					}
				}
			}

			command = ReviewSQL.addComboAction(reviewrefid, statusid, comment, previd, userid, ip, now);
			if (Operator.hasValue(command)) {
				if (db.query(command)) {
					if (db.next()) {
						int actionid = db.getInt("ID");
						r.addInfo("REF_COMBOREVIEW_REVIEW_ID", db.getString("REF_COMBOREVIEW_REVIEW_ID"));
						r.addInfo("REF_COMBOREVIEW_ACTION_ID", db.getString("ID"));
						command = ReviewSQL.expireComboAction(reviewrefid, actionid, userid, ip);
						db.update(command);
						if (Operator.equalsIgnoreCase(reviewinherit, "Y")) {
							command = ReviewSQL.getRefCombo(reviewrefid);
							if (db.query(command) && db.next()) {
								String type = db.getString("TYPE");
								int comboid = db.getInt("ID");
								if (Operator.equalsIgnoreCase(type, "activity")) {
									command = ReviewSQL.getActivity(comboid);
									if (db.query(command) && db.next()) {
										int projectid = db.getInt("PROJECT_ID");
										int actid = db.getInt("ID");
										String inherit = db.getString("INHERIT");
										String statusinherit = db.getString("STATUS_INHERIT");
										if (Operator.equalsIgnoreCase(inherit, "Y") && Operator.equalsIgnoreCase(statusinherit, "Y")) {
											command = ActivitySQL.inheritExpiration(projectid);
											if (db.update(command)) {
												command = ActivitySQL.getNullExpiration(projectid);
												if (db.query(command) && db.size() < 1) {
													command = ProjectSQL.updateExpiration(projectid);
												}
												else {
													command = ProjectSQL.removeExpiration(projectid);
												}
												db.update(command);
												CsDeleteCache.deleteProjectChildCache("project", projectid, "activity");
												CsDeleteCache.deleteProjectCache("project", projectid, "activities");
												CsDeleteCache.deleteProjectCache("project", projectid, "project");
											}
										}
									}
								}
							}
						}
					}
					else {
						r.setMessagecode("cs500");
						r.addError("Could not verify addition of action");
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Could not add action to the database");
				}
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Could not add action to the database");
			}
		} catch (Exception e) {
			r.setMessagecode("cs500");
			r.addError("Could not add action to the database");
		}
		db.clear();
		return r;
	}

	public static ResponseVO addAppointment(ResponseVO r, ValidateUtil v, int comboid, String date, String time, String type, int typeid, int reviewrefid, int actionrefid, String comment, String collaborators, String team, int userid, String ip, Timekeeper now) {
		Logger.highlight("METHOD", "addAppointment");
		if (Operator.hasValue(date) && Operator.hasValue(time)) {
			ResponseVO ar = AppointmentAgent.add(v, type, typeid, -1, reviewrefid, actionrefid, 0, "", comment, collaborators, team, userid, ip);
			if (ar.isValid()) {
				r.addInfo("APPOINTMENT_ID", ar.getInfo("APPOINTMENT_ID"));
				r.addInfo("APPOINTMENT_SCHEDULE_ID", ar.getInfo("APPOINTMENT_SCHEDULE_ID"));
//				String command = ReviewSQL.updateComboreview(comboid, v.getStart(), v.getEnd(), userid, ip, now);
//				Sage db = new Sage();
//				db.update(command);
//				db.clear();
			}
			else {
				r.setValid(false);
				r.setMessagecode(ar.getMessagecode());
				r.setMessages(ar.getMessages());
				r.setErrors(ar.getErrors());
			}
		}
		return r;
	}

	public static ResponseVO addAttachment(ResponseVO r, int actionrefid, String attachpath, String attachtitle, String attachdesc, int attachtypeid, String attachpublic, int userid, String ip, Timekeeper now) {
		Logger.highlight("METHOD", "addAttachment");
		if (Operator.hasValue(attachpath)) {
			String command = AttachmentsSQL.addAttachment(attachtitle, attachdesc, attachpath, attachtypeid, 0, attachpublic.equalsIgnoreCase("Y"), userid, ip, now);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.update(command)) {
					command = AttachmentsSQL.getAttachment(attachtitle, attachpath, userid, now);
					if (db.query(command) && db.next()) {
						int attachid = db.getInt("ID");
						if (attachid > 0) {
							command = ReviewSQL.refAttachment(actionrefid, attachid, userid, ip);
							db.update(command);
						}
					}
				}
				db.clear();
			}
		}
		else {
			r.setMessagecode("cs500");
			r.addError("Could not add attachment to the database.");
		}
		return r;
	}

	public static ResponseVO addTeam(ResponseVO r, int reviewrefid, int actionrefid, String team, int userid, String ip) {
		if (Operator.hasValue(team)) {
			String command = "";
			Sage db = new Sage();
			boolean assign = true;

			command = ReviewSQL.getActionStatus(actionrefid);
			if (db.query(command) && db.next()) {
				assign = db.equalsIgnoreCase("ASSIGN", "Y");
			}

			if (assign) {
				command = ReviewSQL.deleteTeam(reviewrefid);
				db.update(command);
				String[] ta = Operator.split(team, "|");
				if (ta.length > 0) {
					for (int i=0; i<ta.length; i++) {
						int t = Operator.toInt(ta[i]);
						if (t > 0) {
							command = ReviewSQL.addTeam(reviewrefid, actionrefid, t, userid, ip);
							if (db.update(command)) {
								
							}
						}
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Could not add team to the database.");
				}
			}
			db.clear();
		}
		else {
			r.setMessagecode("cs500");
			r.addError("Could not add team to the database.");
		}
		return r;
	}

	public static boolean notify(String subject, int actionrefid, String userids, int creator, String ip) {
		boolean r = true;
		Sage db = new Sage();
		boolean noresult= false;
		if (Operator.hasValue(userids)) {
			
			String[] uarr = Operator.split(userids, "|");
			for (int i=0; i<uarr.length; i++) {
				int userid = Operator.toInt(uarr[i]);
				if (userid > 0) {
					String command = UsersSQL.getUsers(userid);
					if (db.query(command) && db.next()) {
						boolean notify = Operator.s2b(db.getString("NOTIFY"));
						String email = db.getString("EMAIL");
						if (!Operator.hasValue(email) || !Operator.isEmail(email)) {
							email = db.getString("USERNAME");
						}
						Logger.info(notify+"#####"+Operator.isEmail(email)+"###PRE EMAILS"+email);
						if (Operator.hasValue(email) && Operator.isEmail(email) && notify) {
							String name = db.getString("NAME");
							Logger.info(name+"########PRE EMAILS"+email);
							command = ReviewSQL.getComboAction(actionrefid);
							if (db.query(command) && db.next()) {
								String comment = db.getString("REVIEW_COMMENTS");
								String actnbr = db.getString("ACT_NBR");
								String address = db.getString("ADDRESS");
								String review = db.getString("REVIEW");
								String status = db.getString("STATUS");
								String date = db.getString("START_DATE");
								String time = db.getString("INSPECTION_TIME");
								
								String content = createNotificationContent(name, comment, actnbr, address, review, status,date,time);
								if (!Operator.hasValue(subject)) {
									subject = "City of Beverly Hills: A review has been updated for activity number "+actnbr;
								}
								Logger.info(actionrefid+"########PRE EMAILS"+userid);
								CommunicationsAgent.email("review", actionrefid, email, userid, subject, content, creator, ip);
							}
						}
					}
				}
			}
			
		}
		else {
			
			noresult = true;
			
		}
		
		db.clear();
		if(noresult){
			r = notify(subject, actionrefid, creator, ip, 0);
		}
		return r;
	}
	
	
	public static boolean notify(String subject, int actionrefid, int creator, String ip,int refreviewId) {
		boolean r = true;
		Sage db = new Sage();
		Sage db2 = new Sage();
		
		
		String command = ReviewSQL.getNotifyEmails(actionrefid,refreviewId);
		db2.query(command);
		while(db2.next()){
			String email = db2.getString("EMAIL");
			boolean notify = Operator.s2b(db2.getString("NOTIFY"));
			if (!Operator.hasValue(email) || !Operator.isEmail(email) ) {
				email = db2.getString("USERNAME");
			}
			if (Operator.hasValue(email) && Operator.isEmail(email) && notify) {
				String name = db2.getString("NAME");
				command = ReviewSQL.getComboAction(actionrefid);
				if(!Operator.hasValue(command)){
					command = ReviewSQL.getComboActionByReviewId(refreviewId);
				}
				if (db.query(command) && db.next()) {
					String comment = db.getString("REVIEW_COMMENTS");
					String actnbr = db.getString("ACT_NBR");
					String address = db.getString("ADDRESS");
					String review = db.getString("REVIEW");
					String status = db.getString("STATUS");
					String date = db.getString("START_DATE");
					String time = db.getString("INSPECTION_TIME");
					
					String content = createNotificationContent(name, comment, actnbr, address, review, status,date,time);
					if (!Operator.hasValue(subject)) {
						subject = "City of Beverly Hills: A review has been updated for activity number "+actnbr;
					}
					CommunicationsAgent.email("review", actionrefid, email, db2.getInt("ID"), subject, content, creator, ip);
				}
			}
		}
		db2.clear();
		db.clear();
		return r;
		
	}

	public static String createNotificationSubject(String name) {
		// TODO : Notification Subject
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	public static String createNotificationContent(String name, String comment, String actnbr, String address, String review, String status,String date, String time) {
		// TODO : Notification Content
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put("ACTIVITY NUMBER", actnbr);
		m.put("ADDRESS", address);
		m.put("REVIEW", review);
		m.put("STATUS", status);
		if(Operator.hasValue(date)){
			m.put("DATE", date);
		}
		if(Operator.hasValue(time)){
			m.put("TIME", time);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(Email.genericTemplate(name, comment, m));
		return sb.toString();
	}

	public static ResponseVO updateTeam(ResponseVO r, int reviewrefid, int actionrefid, String team, int userid, String ip) {
		Sage db = new Sage();
		String command = ReviewSQL.deleteTeam(reviewrefid);
		db.update(command);
		String[] ta = Operator.split(team, "|");
		if (ta.length > 0) {
			for (int i=0; i<ta.length; i++) {
				int t = Operator.toInt(ta[i]);
				if (t > 0) {
					command = ReviewSQL.addTeam(reviewrefid, actionrefid, t, userid, ip);
					if (db.update(command)) {
						
					}
				}
			}
		}
		db.clear();
		return r;
	}


//	public static ResponseVO saveAction(int reviewrefid, int statusid, String comment, String teammembers, String attachtitle, String attachpath, int attachtypeid, String attachdesc, String attachpublic, int userid, String ip) {
//		Logger.highlight("METHOD", "saveAction");
//		ResponseVO r = new ResponseVO();
//		r.setMessagecode("cs200");
//
//		String[] team = new String[0];
//		String date = "";
//		boolean valid = true;
//
//		if (reviewrefid > 0) {
//			Timekeeper now = new Timekeeper();
//			String assign = "";
//			String command = ReviewSQL.getRefComboReviews(reviewrefid);
//			Sage db = new Sage();
//			if (db.query(command) && db.next()) {
//				reviewrefid = db.getInt("ID");
//				int comboid = db.getInt("COMBOREVIEW_ID");
//				r.setId(comboid);
//
//
//				command = ReviewSQL.getStatus(statusid);
//				if (db.query(command) && db.next()) {
//					assign = db.getString("ASSIGN");
//					if (db.equalsIgnoreCase("SCHEDULE", "Y")) {
////						date = m.get("DATE");
////						if (!Operator.hasValue(date)) {
////							r.setMessagecode("cs412");
////							r.addError("Date is a required field");
////							valid = false;
////						}
//					}
//					if (db.equalsIgnoreCase("ASSIGN", "Y")) {
//						team = Operator.split(teammembers, "|");
//						if (!Operator.hasValue(team)) {
//							r.setMessagecode("cs412");
//							r.addError("Team members must be selected");
//							valid = false;
//						}
//					}
//				}
//				else {
//					r.setMessagecode("cs412");
//					r.addError("A valid status was not found in the request");
//					valid = false;
//				}
//			}
//			else {
//				r.setMessagecode("cs412");
//				r.addError("A valid combo review was not found in the request");
//				valid = false;
//			}
//			if (valid) {
//				AppointmentAgent.completeReview(reviewrefid, -1, "", userid, ip);
//				int previd = -1;
//				command = ReviewSQL.getCurrentAction(reviewrefid);
//				if (db.query(command) && db.next()) {
//					previd = db.getInt("ID");
//				}
//				command = ReviewSQL.addComboAction(reviewrefid,  statusid, comment, previd, userid, ip, now);
//				if (db.update(command)) {
//					command = ReviewSQL.getComboAction(reviewrefid, statusid, userid, now);
//					if (db.query(command) && db.next()) {
//						int actid = db.getInt("ID");
//						command = ReviewSQL.expireComboAction(reviewrefid, actid);
//						db.update(command);
//						if (Operator.hasValue(attachpath)) {
//							command = AttachmentsSQL.addAttachment(attachtitle, attachdesc, attachpath, attachtypeid, 0, attachpublic.equalsIgnoreCase("Y"), userid, ip, now);
//							if (db.update(command)) {
//								command = AttachmentsSQL.getAttachment(attachtitle, attachpath, userid, now);
//								if (db.query(command) && db.next()) {
//									int attachid = db.getInt("ID");
//									if (attachid > 0) {
//										command = ReviewSQL.refAttachment(actid, attachid, userid, ip);
//										db.update(command);
//									}
//								}
//							}
//						}
//						if (Operator.hasValue(team)) {
//							if (!Operator.equalsIgnoreCase(assign, "Y")) {
//								command = ReviewSQL.deleteTeam(reviewrefid);
//								db.update(command);
//							}
//							for (int i=0; i<team.length; i++) {
//								int t = Operator.toInt(team[i]);
//								if (t > 0) {
//									command = ReviewSQL.addTeam(reviewrefid, actid, t, userid, ip);
//									if (db.update(command)) {
//										
//									}
//								}
//							}
//						}
//					}
//				}
//				else {
//					r.setMessagecode("cs500");
//					r.addError("Could not save action in the database");
//					valid = false;
//				}
//			}
//			db.clear();
//		}
//		else {
//			r.setMessagecode("cs412");
//			r.addError("A valid combo review was not found in the request");
//			valid = false;
//		}
//
//		return r;
//	}

//	public static boolean autoCreate(String type, int typeid) {
//		Timekeeper d = new Timekeeper();
//	}

	public static boolean autoCreateCombo(String processid, String type, int typeid, int userid, String ip, Token u) {
		boolean r = true;
		String command = ReviewSQL.getRequiredGroups(type, typeid);
		if (Operator.hasValue(command)) {
			HashMap<Integer, String> titles = new HashMap<Integer, String>();
			Sage db = new Sage();
			try {
				if (db.query(command)) {
					while (db.next()) {
						int reviewgroupid = db.getInt("REVIEW_GROUP_ID");
						String title = db.getString("GROUP_NAME");
						titles.put(reviewgroupid, title);
					}
				}
			}
			catch (Exception e) { }
			db.clear();
			for (Map.Entry<Integer, String> entry : titles.entrySet()) {
				int reviewgroupid = entry.getKey();
				String title = entry.getValue();
				LogAgent.updateLog(processid, "Adding Review: "+title);
				int comboid = createCombo(type, typeid, reviewgroupid, title, "", "", userid, ip, u);
				if (comboid < 1) { r = false; }
			}
		}
		return r;
	}

	public static ResponseVO createCombo(RequestVO vo, Token u) {
		DataVO m = DataVO.toDataVO(vo);
		return createCombo(vo.getProcessid(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()), Operator.toInt(m.get("REVIEW_ID")), Operator.toInt(m.get("LKUP_REVIEW_STATUS_ID")), m.get("DUE_DATE"), m.get("REVIEW_COMMENTS"), m.get("USERS_ID"), m.get("INSPECTOR_ID"), m.get("REF_TEAM_ID"), m.get("DATE"), m.get("TIME"), m.get("TITLE"), m.get("START_DATE"), m.get("ATTACHMENT_TITLE"), m.get("ATTACHMENT"), Operator.toInt(m.get("ATTACHMENT_TYPE_ID")), m.get("ATTACHMENT_DESCRIPTION"), m.get("ATTACHMENT_PUBLIC"), u.getId(), u.getIp(), false, u);
	}

	public static ResponseVO createCombo(String processid, String type, int typeid, int reviewgroupid, int reviewid, int statusid, String due, String comment, String collaborators, String inspectors, String teammembers, String date, String time, String title, String startdate, String attachtitle, String attachpath, int attachtypeid, String attachdesc, String attachpublic, int userid, String ip, boolean doreview, Token u) {
		ResponseVO r = LogAgent.getLog(processid);
		r.setMessagecode("cs200");
		int comboid = -1;

		boolean valid = true;
		String command = "";
		if (reviewgroupid < 1) {
			r.setMessagecode("cs412");
			r.addError("Review group is not found in the request.");
			valid = false;
			LogAgent.saveLog(r);
		}
		if (!Operator.hasValue(type)) {
			r.setMessagecode("cs412");
			r.addError("Type is not found in the request.");
			valid = false;
			LogAgent.saveLog(r);
		}
		if (typeid < 1) {
			r.setMessagecode("cs412");
			r.addError("Type id is not found in the request.");
			valid = false;
			LogAgent.saveLog(r);
		}
		if (!Operator.hasValue(startdate) && !Operator.hasValue(date)) {
			r.setMessagecode("cs412");
			r.addError("Start date is a required field.");
			valid = false;
			LogAgent.saveLog(r);
		}

		if (valid) {
			if (!Operator.hasValue(startdate)) {
				startdate = date;
			}
			comboid = createCombo(type, typeid, reviewgroupid, title, startdate, collaborators, userid, ip, u);
			if (comboid > 0) {
				r.setId(comboid);
				LogAgent.saveLog(r);
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Could not add reference to type.");
				valid = false;
				LogAgent.saveLog(r);
			}
			if (valid && doreview && comboid > 0) {
				r = saveCombo(false, type, typeid, comboid, -1, reviewid, statusid, due, comment, collaborators, inspectors, teammembers, date, time, attachtitle, attachpath, attachtypeid, attachdesc, attachpublic, "", userid, ip, u);
				LogAgent.saveLog(r);
			}
		}


		return r;
	}

	public static int createCombo(String type, int typeid, int reviewgroupid, String title, String startdate, String collaborators, int userid, String ip, Token u) {
		int r = -1;
		ArrayList<Integer> reviewids = new ArrayList<Integer>();
		int comboid = -1;
		Timekeeper d = new Timekeeper();
		Sage db = new Sage();
		try {
			String command = ReviewSQL.createCombo(reviewgroupid, title, startdate, type, d, userid, ip);
			if (db.query(command) && db.next()) {
				comboid = db.getInt("ID");
				if (comboid > 0) {
					command = ReviewSQL.refCombo(type, typeid, comboid, userid, ip);
					if (db.update(command)) {
						command = ReviewSQL.getRequired(reviewgroupid);
						db.query(command);
						while (db.next()) {
							reviewids.add(db.getInt("ID"));
						}
						r = comboid;
					}
				}
			}
		}
		catch (Exception e) { }
		db.clear();
		for (int i=0; i<reviewids.size(); i++) {
			int reviewid = reviewids.get(i);
			if (reviewid > 0) {
				saveCombo(false, type, typeid, comboid, -1, reviewid, -1, "", "", collaborators, "", "", "", "", "", "", -1, "", "", "", userid, ip, u);
			}
		}
		return r;
	}

	public static ResponseVO updateCombo(RequestVO req, Token u) {
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");

		boolean valid = true;
		int comboid = Operator.toInt(req.getId());
		if (comboid > 0) {
			DataVO m = DataVO.toDataVO(req);
			String title = m.get("TITLE");
			String startdate = m.get("START_DATE");
			if (!Operator.hasValue(startdate)) {
				r.setMessagecode("cs412");
				r.addError("Start date is a required field");
				valid = false;
			}
			else {
				Timekeeper date = new Timekeeper();
				String command = ReviewSQL.comboreviewHistory(comboid, u.getId(), u.getIp(), date);
				Sage db = new Sage();
				db.update(command);
				command = ReviewSQL.updateComboreview(comboid, title, startdate, u.getId(), u.getIp(), date);
				if (db.update(command)) {
					r.setId(comboid);
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Could not update the database");
					valid = false;
				}
				db.clear();
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Review group is not found in the request");
			valid = false;
		}
		return r;
	}

	public static ComboReviewVO getCombo(RequestVO req, Token u) {
		int id = Operator.toInt(req.getId());
		int refid = req.getReviewrefid();
		String type = req.getType();
		int typeid = req.getTypeid();
		ComboReviewVO r = new ComboReviewVO();
		r.setEntity(req.getEntity());
		r.setType(req.getType());
		r.setTypeid(req.getTypeid());
		r.setGroup(req.getGroup());
		r.setGroupid(Operator.toInt(req.getGroupid()));

		String command = "";
		if (refid > 0) {
			command = ReviewSQL.getRefCombo(refid);
		}
		else {
			command = ReviewSQL.getCombo(id);
		}
		Sage db = new Sage();
		if (Operator.hasValue(command)) {
			if (db.query(command) && db.next()) {
				id = db.getInt("ID");
				r.setComboid(id);
				r.setCombotitle(db.getString("TITLE"));
				r.setStart(db.getString("START_DATE"));

				int reviewgroupid = db.getInt("REVIEW_GROUP_ID");
				r.setReviewgroupid(reviewgroupid);

				HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
				boolean dh = Modules.disableOnHold("review");
				if (dh) {
					RolesVO roles = LkupCache.getReviewRoles(reviewgroupid);
					r.putRoles(roles, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
				}
				else {
					RolesVO roles = LkupCache.getReviewRoles(reviewgroupid);
					r.putRoles(roles, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
				}

				if (db.hasValue("TYPE")) {
					type = db.getString("TYPE");
				}
				if (refid > 0) {
					command = ReviewSQL.getRefComboReviews(refid);
				}
				else {
					command = ReviewSQL.getComboReviews(id);
				}
				if (db.query(command)) {
					while (db.next()) {
						ReviewVO rvo = new ReviewVO();
						rvo.setId(db.getInt("ID"));
						rvo.setComboid(db.getInt("COMBOREVIEW_ID"));
						rvo.setReviewid(db.getInt("REVIEW_ID"));
						rvo.setReview(db.getString("REVIEW"));
						rvo.setDaystilldue(db.getInt("DAYS_TILL_DUE"));
						rvo.setAvailabilityid(db.getInt("AVAILABILITY_ID"));
						rvo.setDuedate(db.getString("DUE_DATE"));
						rvo.setStartdate(db.getString("START_DATE"));
						r.addReview(rvo);
					}
					if (refid > 0) {
						command = ReviewSQL.getRefReviewActions(refid);
					}
					else {
						command = ReviewSQL.getReviewActions(id);
					}
					if (db.query(command)) {
						while (db.next()) {
							ReviewActionVO avo = new ReviewActionVO();
							avo.setId(db.getInt("ID"));
							avo.setDate(db.getString("DATE"));
							avo.setReviewrefid(db.getInt("REF_COMBOREVIEW_REVIEW_ID"));
							avo.setComments(db.getString("REVIEW_COMMENTS"));
							avo.setStatus(db.getString("STATUS"));
							avo.setStatusid(db.getInt("LKUP_REVIEW_STATUS_ID"));
							avo.setUnapproved(db.getString("UNAPPROVED"));
							avo.setApproved(db.getString("APPROVED"));
							avo.setFnal(db.getString("FINAL"));
							avo.setScheduled(db.getString("SCHEDULE"));
							avo.setCancel(db.getString("SCHEDULE_CANCEL"));
							avo.setInspection(db.getString("SCHEDULE_INSPECTION"));
							avo.setInspectioncancel(db.getString("SCHEDULE_INSPECTION_CANCEL"));
							avo.setAssign(db.getString("ASSIGN"));
							avo.setScheduled(db.getString("SCHEDULE"));
							avo.setCreatedby(db.getString("USERNAME"));
							int notifications = db.getInt("NOTIFICATIONS");
							if (notifications < 1) { notifications = 0; }
							avo.setNotifications(notifications);

							ReviewAttachmentVO atvo = new ReviewAttachmentVO();
							atvo.setAttachid(db.getInt("ATTACHMENT_ID"));
							atvo.setActionid(db.getInt("ID"));
							atvo.setTitle(db.getString("ATTACHMENT_TITLE"));
							atvo.setPath(db.getString("ATTACHMENT"));
							avo.setAttachment(atvo);

							AppointmentVO apvo = new AppointmentVO();
							apvo.setId(db.getInt("APPOINTMENT_ID"));
							apvo.setCstype(db.getString("CSTYPE"));
							apvo.setSubject(db.getString("APPTSUBJECT"));

							AppointmentScheduleVO apsvo = new AppointmentScheduleVO();
							apsvo.setId(db.getInt("SCHEDULE_ID"));
							apsvo.setApptid(db.getInt("APPOINTMENT_ID"));
							apsvo.setStart(db.getString("START_DATE"));
							apsvo.setEnd(db.getString("END_DATE"));
							apsvo.setComplete(db.getString("APPTCOMPLETE"));
							apsvo.setScheduled(db.getString("APPTSCHEDULED"));
							apsvo.setSource(db.getString("APPTSOURCE"));

							apvo.addSchedule(apsvo);
							avo.setAppointment(apvo);
							r.addAction(avo);
						}
					}

					if (refid > 0) {
						command = ReviewSQL.getRefReviewTeam(refid);
					}
					else {
						command = ReviewSQL.getReviewTeam(id);
					}
					if (db.query(command)) {
						while (db.next()) {
							ReviewTeamVO tvo = new ReviewTeamVO();
							tvo.setId(db.getInt("ID"));
							tvo.setFirstname(db.getString("FIRST_NAME"));
							tvo.setLastname(db.getString("LAST_NAME"));
							tvo.setUsername(db.getString("USERNAME"));
							tvo.setReviewrefid(db.getInt("REF_COMBOREVIEW_REVIEW_ID"));
							tvo.setActionid(db.getInt("REF_COMBOREVIEW_ACTION_ID"));
							tvo.setComboid(db.getInt("COMBOREVIEW_ID"));
							if (db.equalsIgnoreCase("ACTIVE", "Y")) {
								r.addTeam(tvo);
							}
//							r.addAssigned(tvo);
						}
					}
				}
			}
		}
		if (Operator.hasValue(type)) {
			if (typeid < 1) {
				command = ReviewSQL.getTypeId(id, type);
				if (db.query(command) && db.next()) {
					typeid = db.getInt("ID");
				}
			}
			if (typeid > 0) {
				command = CsReflect.getQuery("type", type, typeid, req.getOption());
				if (db.query(command) && db.next()) {
					r.setType(type);
					r.setTypeid(typeid);
					r.setTitle(db.getString("TITLE"));
					r.setSubtitle(db.getString("SUBTITLE"));
				}
			}
		}
		db.clear();
		return r;
	}

	public static ComboReviewGroup getCombos(String type, int typeid) {
		ComboReviewGroup r = new ComboReviewGroup();
		String command = ReviewSQL.getComboreviews(type, typeid);
		Sage db = new Sage();
		db.query(command);
		while (db.next()) {
			int revgroupid = db.getInt("GROUPID");
			int comboid = db.getInt("COMBOID");
			String combotitle = db.getString("COMBOTITLE");
			String expedited = db.getString("EXPEDITED");
			String revgrouptitle = db.getString("REVIEWGROUP");
			String start = db.getString("START_DATE");
			String due = db.getString("DUE_DATE");
			String activity = db.getString("ACT_NBR");
			String project = db.getString("PROJ_NBR");
			String address = db.getString("ADDRESS");
			int activityid = db.getInt("ACT_ID");
			int projectid = db.getInt("PROJ_ID");
			int lsoid = db.getInt("LSO_ID");
			String projecttype = db.getString("PROJ_TYPE");
			String activitytype = db.getString("ACT_TYPE");

			ReviewVO review = new ReviewVO();
			review.setComboid(comboid);
			review.setDuedate(db.getString("DUE_DATE"));
			review.setId(db.getInt("REFREVIEWID"));
			review.setReviewid(db.getInt("REVIEWID"));
			review.setReview(db.getString("REVIEW"));
			review.setAvailabilityid(db.getInt("AVAILABILITY_ID"));
			review.setMaxactiveappt(db.getInt("MAX_ACTIVE_APPOINTMENTS"));
			review.setStartdate(db.getString("START_DATE"));
			review.setDaystilldue(db.getInt("DAYS_TILL_DUE"));

			ReviewActionVO action = new ReviewActionVO();
			action.setId(db.getInt("REFACTID"));
			action.setReviewrefid(db.getInt("REFREVIEWID"));
			action.setStatus(db.getString("STATUS"));
			action.setStatusid(db.getInt("STATUSID"));
			action.setFnal(db.getString("FINAL"));
			action.setApproved(db.getString("APPROVED"));
			action.setUnapproved(db.getString("UNAPPROVED"));

			r.addAction(revgrouptitle, revgroupid, comboid, combotitle, start, due, expedited.equalsIgnoreCase("Y"), type, typeid, project, projectid, projecttype, activity, activityid, activitytype, address, lsoid, review, action);
		}
		db.clear();
		return r;
	}

	public static ComboReviewList appt(RequestVO req, Token u) {
		int userid = Operator.toInt(req.getId());
		String start = req.getStartdate();
		String end = req.getEnddate();
		String rtype = req.getReference();
		return appt(rtype, start, end, userid);
	}

	public static ComboReviewList appt(String reviewtype, String start, String end, int userid) {
		ComboReviewList r = new ComboReviewList();
		String command = ReviewSQL.getComboreviewAppointments(reviewtype, start, end, userid);
		Sage db = new Sage();
		db.query(command);
		while (db.next()) {
			int revgroupid = db.getInt("GROUPID");
			int comboid = db.getInt("COMBOID");
			String combotitle = db.getString("COMBOTITLE");
			String expedited = db.getString("EXPEDITED");
			String revgrouptitle = db.getString("REVIEWGROUP");
			String startdate = db.getString("START_DATE");
			String duedate = db.getString("DUE_DATE");
			String activity = db.getString("ACT_NBR");
			String project = db.getString("PROJECT_NBR");
			String address = db.getString("ADDRESS");
			int activityid = db.getInt("ACT_ID");
			int projectid = db.getInt("PROJ_ID");
			int lsoid = db.getInt("LSO_ID");
			String projecttype = db.getString("PROJ_TYPE");
			String activitytype = db.getString("ACT_TYPE");

			int typeid = -1;
			String type = db.getString("REF");
			if (Operator.equalsIgnoreCase(type, "activity")) {
				typeid = activityid;
			}
			if (Operator.equalsIgnoreCase(type, "project")) {
				typeid = projectid;
			}

			ReviewVO review = new ReviewVO();
			review.setComboid(comboid);
			review.setId(db.getInt("REFREVIEWID"));
			review.setReviewid(db.getInt("REVIEWID"));
			review.setReview(db.getString("REVIEW"));
			review.setAvailabilityid(db.getInt("AVAILABILITY_ID"));
			review.setMaxactiveappt(db.getInt("MAX_ACTIVE_APPOINTMENTS"));

			ReviewActionVO action = new ReviewActionVO();
			action.setId(db.getInt("REFACTID"));
			action.setReviewrefid(db.getInt("REFREVIEWID"));
			action.setStatus(db.getString("STATUS"));
			action.setStatusid(db.getInt("STATUSID"));
			action.setFnal(db.getString("FINAL"));
			action.setApproved(db.getString("APPROVED"));
			action.setUnapproved(db.getString("UNAPPROVED"));
			action.setExpired(db.getString("EXPIRED"));

			AppointmentVO appt = new AppointmentVO();
			appt.setAppttype(db.getString("REVIEWGROUP"));
			appt.setReview(db.getString("REVIEW"));
			appt.setAppttypeid(comboid);
			appt.setCstype(db.getString("CSTYPE"));
			appt.setRefreviewid(db.getInt("REFREVIEWID"));
			appt.setReviewid(db.getInt("REVIEWID"));
			appt.setId(db.getInt("APPTID"));

			AppointmentScheduleVO schedvo = new AppointmentScheduleVO();
			schedvo.setId(db.getInt("SCHEDID"));
			schedvo.setApptid(db.getInt("APPTID"));
			schedvo.setComplete(db.getString("COMPLETE"));
			schedvo.setStart(db.getString("APPT_START_DATE"));
			schedvo.setEnd(db.getString("APPT_END_DATE"));

			appt.addSchedule(schedvo);
			action.setAppointment(appt);

			r.addAction(comboid, combotitle, startdate, duedate, expedited.equalsIgnoreCase("Y"), type, typeid, project, projectid, projecttype, activity, activityid, activitytype, address, lsoid, revgrouptitle, revgroupid, review, action);	

			ReviewTeamVO team = new ReviewTeamVO();
			team.setActionid(db.getInt("REFACTID"));
			team.setLastname(db.getString("LAST_NAME"));
			team.setFirstname(db.getString("FIRST_NAME"));
			team.setMiddlename(db.getString("MI"));
			team.setUserid(db.getInt("USERID"));
			team.setId(db.getInt("TEAMID"));
			team.setUsername(db.getString("USERNAME"));
			r.addTeam(comboid, db.getInt("REFREVIEWID"), team);

		}
		db.clear();
		return r;
	}

	public static void clearCache(int reviewrefid) {
		if (reviewrefid > 0) {
			String command = ReviewSQL.getComboreview(reviewrefid);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				String type = db.getString("TYPE");
				int comboid = db.getInt("ID");
				if (comboid > 0 && Operator.hasValue(type)) {
					command = ReviewSQL.getTypeId(comboid, type);
					if (db.query(command) && db.next()) {
						int typeid = db.getInt("ID");
						if (typeid > 0) {
							CsDeleteCache.deleteCache(type, typeid, "appointment");
							CsDeleteCache.deleteCache(type, typeid, "reviews");
						}
					}
				}
			}
			db.clear();
		}
	}

	public static String getDue(int reviewrefid) {
		String r = "";
		if (reviewrefid > 0) {
			String command = ReviewSQL.getComboreview(reviewrefid);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r = db.getString("DUE_DATE");
			}
			db.clear();
		}
		return r;
	}

	public static boolean updateDue(int reviewrefid, String date, int userid, String ip) {
		boolean r = false;
		String command = ReviewSQL.updateDue(reviewrefid, date, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}

	public static SubObjVO[] getNotifications(int revactid) {
		String command = ReviewSQL.getNotifications(revactid);
		return Choices.getChoices(command);
	}

	public static SubObjVO[] getNotification(int notifyid) {
		String command = ReviewSQL.getNotification(notifyid);
		return Choices.getChoices(command);
	}






	public static List<ComboReviewVO> getCycleDetails(RequestVO req, Token u) {
		String type = req.getType();
		int typeid = req.getTypeid();
		
		List<ComboReviewVO> rlist = new ArrayList<ComboReviewVO>();
		

		String command = "";
			command = ReviewSQL.getCycleDetails(typeid);
		Sage db = new Sage();
		if (Operator.hasValue(command)) {
			if (db.query(command)) {
				while(db.next()) {
				ComboReviewVO r = new ComboReviewVO();
				r.setEntity(req.getEntity());
				r.setType(type);
				r.setTypeid(typeid);
				r.setComboid(db.getInt("COMBOID"));
				r.setCombotitle(db.getString("TITLE"));
				r.setStart(db.getString("START_DATE"));

				int reviewgroupid = db.getInt("REVIEW_GROUP_ID");
				r.setReviewgroupid(reviewgroupid);
				r.setReviewgrouptitle(db.getString("GROUP_NAME"));
				r.setTypeid(db.getInt("ACTIVITY_ID"));
				HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
				boolean dh = Modules.disableOnHold("review");
				if (dh) {
					RolesVO roles = LkupCache.getReviewRoles(reviewgroupid);
					r.putRoles(roles, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
				}
				else {
					RolesVO roles = LkupCache.getReviewRoles(reviewgroupid);
					r.putRoles(roles, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
				}

				if (db.hasValue("TYPE")) {
					type = db.getString("TYPE");
				}
				rlist.add(r);
				}
			}
		}
		
		db.clear();
		return rlist;
	}



	public static ResponseVO addUpdateTeam(ResponseVO r, int reviewrefid, int actionrefid, String team, int userid, String ip) {
		Sage db = new Sage();
		String command = ReviewSQL.deleteTeam(reviewrefid);
		db.update(command);
		try {
			team = team.replace("|", ",");
			if (Operator.hasValue(team)) {
				int[] arr = Arrays.stream(team.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
				Arrays.sort(arr);
				int length = arr.length;
				length = removeDuplicates(arr, length);
				int[] finalarr = new int[length];
				for (int i = 0; i < length; i++) {
					finalarr[i] = arr[i];
				}
				team = Arrays.toString(finalarr);
				team = team.replace(",", "|");
				team = team.replace("[", "");
				team = team.replace("]", "");
			}
		  String[] ta = Operator.split(team, "|");
		  if (ta.length > 0) {
			for (int i=0; i<ta.length; i++) {
				int t = Operator.toInt(ta[i]);
				if (t > 0) {
					command = ReviewSQL.addTeam(reviewrefid, actionrefid, t, userid, ip);
					if (db.update(command)) {
						
					}
				}
			}
		}
		}catch (Exception e) {
			Logger.error(e.getMessage());
			e.printStackTrace();
		}
		db.clear();
		return r;
	}

	public static int removeDuplicates(int arr[], int n){
        if (n==0 || n==1){
            return n;
        }
        int[] temp = new int[n];
        int j = 0;
        for (int i=0; i<n-1; i++){
            if (arr[i] != arr[i+1]){
                temp[j++] = arr[i];
            }
         }
        temp[j++] = arr[n-1];  
        // Changing original array
        for (int i=0; i<j; i++){
            arr[i] = temp[i];
        }
        return j;
    }



}












