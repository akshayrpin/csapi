package csapi.impl.appointment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivityFields;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.availability.AvailabilitySQL;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.general.GeneralSQL;
import csapi.impl.info.InfoSQL;
import csapi.impl.notes.NotesSQL;
import csapi.impl.review.ReviewAgent;
import csapi.impl.review.ReviewSQL;
import csapi.impl.team.TeamAgent;
import csapi.impl.users.UsersAgent;
import csapi.impl.users.UsersSQL;
import csapi.search.GlobalSearch;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csapi.utils.Email;
import csapi.utils.validate.ValidateUtil;
import csshared.utils.CsConfig;
import csshared.utils.ObjMapper;
import csshared.vo.AppointmentScheduleVO;
import csshared.vo.AppointmentVO;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.UserVO;

public class AppointmentAgent {

	public static RequestVO processRequest(String json) {
		RequestVO rvo = ObjMapper.toRequestObj(json);
		return processRequest(rvo);
	}

	public static RequestVO processRequest(RequestVO vo) {
		String command = AppointmentSQL.getAppointment(Operator.toInt(vo.getId()));
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			vo.setType(db.getString("REF"));
			int aid = db.getInt("ID");
			if (Operator.hasValue(vo.getType())) {
				command = AppointmentSQL.getTypeId(vo.getType(), aid);
				if (Operator.hasValue(command)) {
					if (db.query(command) && db.next()) {
						vo.setTypeid(db.getInt("ID"));
					}
				}
			}
		}
		db.clear();
		return vo;
	}

	public static ValidateUtil validateAvailability(String apptids, String type, int typeid, int reviewid, String date, String time, Token u) {

		ValidateUtil r = new ValidateUtil();
		String[] apptarr = Operator.split(apptids, "|");
		int size = apptarr.length;
		String attype = type;
		int attypeid = typeid;

		ResponseVO resp = new ResponseVO();
		Sage db = new Sage();
		String command = "";

		Timekeeper s = new Timekeeper();
		s.setDate(date);
		s.setSecond(0);
		s.setMilliSecond(0);

		Timekeeper e = new Timekeeper();
		e = s.copy();
		e.setSecond(0);
		e.setMilliSecond(0);

		boolean valid = true;

		if (valid) {
			if (Operator.equalsIgnoreCase(attype, "project") || Operator.equalsIgnoreCase(attype, "activity")) {
				int projid = -1;
				if (Operator.equalsIgnoreCase(attype, "project")) {
					projid = attypeid;
				}
				else if (Operator.equalsIgnoreCase(attype, "activity")) {
					command = ActivitySQL.getActivity(attypeid);
					if (db.query(command) && db.next()) {
						projid = db.getInt("PROJECT_ID");
					}
				}
				if (projid < 1) {
					resp.setMessagecode("cs422");
					resp.addError("Could not find specified project");
					Logger.highlight("AVAILABILITY","Could not find specified project");
					r.setValid(false);
					valid = false;
				}
				else {
					command = AppointmentSQL.getMaxChildDay(projid, s, s, apptids);
					if (db.query(command) && db.next()) {
						int maxday = db.getInt("MAX_CHILD_APPOINTMENTS_DAY");
						int maxchild = db.getInt("MAX_CHILD_APPOINTMENTS");
						if (maxday > 0) {
							int appts = db.getInt("APPOINTMENTS");
							if (appts >= maxday) {
								resp.setMessagecode("cs422");
								resp.addError("Maximum number of appointments per day has been reached");
								Logger.highlight("AVAILABILITY","Maximum number of appointments per day has been reached");
								r.setValid(false);
								valid = false;
							}
						}
						if (maxchild > 0) {
							int totalactive = db.getInt("TOTAL_ACTIVE");
							if (totalactive >= maxchild) {
								resp.setMessagecode("cs422");
								resp.addError("Maximum number of appointments has been reached for this project");
								Logger.highlight("AVAILABILITY","Maximum number of appointments has been reached for this project");
								r.setValid(false);
								valid = false;
							}
						}
					}
				}
			}
		}
		if (valid) {
			if (reviewid > 0) {
				command = AppointmentSQL.getReviewMax(reviewid, apptids);
				if (db.query(command) && db.next()) {
					int max = db.getInt("MAX_ACTIVE_APPOINTMENTS");
					int atv = db.getInt("ACTIVE_APPOINTMENTS");
					if (max > 0 && atv >= max) {
						resp.setMessagecode("cs422");
						resp.addError("Maximum number of active review appointments has been reached");
						Logger.highlight("AVAILABILITY","Maximum number of active review appointments has been reached");
						r.setValid(false);
						valid = false;
					}
				}
			}
			else {
				command = AppointmentSQL.getMax(type, typeid, apptids);
				if (db.query(command) && db.next()) {
					int max = db.getInt("MAX_ACTIVE_APPOINTMENTS");
					int atv = db.getInt("ACTIVE_APPOINTMENTS");
					if (max > 0 && atv >= max) {
						resp.setMessagecode("cs422");
						resp.addError("Maximum number of active appointments has been reached");
						Logger.highlight("AVAILABILITY","Maximum number of active appointments has been reached");
						r.setValid(false);
						valid = false;
					}
				}
			}
		}

		if (u.isStaff() || valid) {

			String av = time;
			int defaultid = -1;
			int customid = -1;

			if (Operator.hasValue(av)) {
				s.setHour(0);
				s.setMinute(0);
				s.setSecond(0);

				String[] arr = Operator.split(av, "|");
				if (arr.length > 0) {
					defaultid = Operator.toInt(arr[0], 0);
				}
				if (arr.length > 1) {
					customid = Operator.toInt(arr[1], 0);
				}

				if (defaultid > 0) {
					Logger.info("AVAILABILITY", "Validate Default Time: "+defaultid);
					command = AvailabilitySQL.getCustomAvailability(s, defaultid);
					if (db.query(command) && db.size() > 0) {
						resp.setMessagecode("cs412");
						resp.addError("Availability of the requested time is not valid.");
						Logger.highlight("AVAILABILITY","Availability of the requested time is not valid.");
						r.setValid(false);
					}
					command = AvailabilitySQL.getDefaultAvailability(defaultid);
					if (db.query(command) && db.next()) {
						int dow = db.getInt("DAY_OF_WEEK");
						int sdow = s.DAY_OF_WEEK();
						int seats = db.getInt("SEATS");
						String cutoff = db.getString("TIME_STOP");
						int bufferseats = db.getInt("BUFFER_SEATS");
						int bufferhours = db.getInt("BUFFER_HOURS");
						if (dow != sdow) {
							resp.setMessagecode("cs412");
							resp.addError("Availability of the requested date is not valid.");
							Logger.highlight("AVAILABILITY","Day ("+sdow+") of the requested date ("+s.getString("MM/DD/YYYY")+") falls outside the predefined day ("+dow+") of the selected time.");
							r.setValid(false);
						}
						else {
							s.setTime(db.getString("TIME_START"));
							e.setTime(db.getString("TIME_END"));
							int taken = 0;
							command = AppointmentSQL.getDuplicateAppointment(apptids, type, typeid, reviewid, s, e);
							if (db.query(command)&& db.size() > 0) {
								resp.setMessagecode("cs422");
								resp.addError("Duplicate Request");
								Logger.highlight("AVAILABILITY","Duplicate Request");
								r.setValid(false);
							}
							else {
								command = AvailabilitySQL.getDefaultTaken(s, defaultid, apptids);
								if (db.query(command) && db.next()) {
									taken = db.getInt("TAKEN");
								}
								int cseats = seats(date, time, cutoff, bufferhours, seats, bufferseats);
								if (cseats < (taken+size)) {
									// 422 is validated in multiedit
									resp.setMessagecode("cs422");
									resp.addError("Requested date is full.");
									Logger.highlight("AVAILABILITY","Requested date is full.");
									r.setValid(false);
								}
							}
						}
					}
					else {
						resp.setMessagecode("cs412");
						resp.addError("Requested availability not found.");
						Logger.highlight("AVAILABILITY","Requested availability not found.");
						r.setValid(false);
					}
				}
				else if (customid > 0) {
					Logger.info("AVAILABILITY", "Validate Custom Time: "+customid);
					command = AvailabilitySQL.getCustomAvailability(customid);
					if (db.query(command) && db.next()) {
						Timekeeper cd = new Timekeeper();
						cd.setDate(db.getString("CUSTOM_DATE"));
						if (s.DATECODE() != cd.DATECODE()) {
							resp.setMessagecode("cs412");
							resp.addError("Availability of the requested date is not valid.");
							Logger.highlight("AVAILABILITY","Availability of the requested date is not valid.");
							r.setValid(false);
						}
						else {
							s.setTime(db.getString("TIME_START"));
							e.setTime(db.getString("TIME_END"));
							int seats = db.getInt("SEATS");
							String cutoff = db.getString("TIME_STOP");
							int bufferseats = db.getInt("BUFFER_SEATS");
							int bufferhours = db.getInt("BUFFER_HOURS");
							int taken = 0;

							command = AppointmentSQL.getDuplicateAppointment(apptids, type, typeid, reviewid, s, e);
							if (db.query(command) && db.size() > 0) {
								resp.setMessagecode("cs422");
								resp.addError("Duplicate Request");
								Logger.highlight("AVAILABILITY","Duplicate Request");
								r.setValid(false);
							}
							else {
								// QUESTION: Why isn't the scheduleid used here like in the defaultaken section
								command = AvailabilitySQL.getCustomTaken(s, customid);
								if (db.query(command) && db.next()) {
									taken = db.getInt("TAKEN");
								}
								int cseats = seats(date, time, cutoff, bufferhours, seats, bufferseats);
								Logger.highlight(cseats);
								Logger.highlight(date);
								Logger.highlight(time);
								Logger.highlight(cutoff);
								Logger.highlight(bufferhours);
								Logger.highlight(seats);
								Logger.highlight(bufferseats);
								Logger.highlight(taken);
								Logger.highlight(size);
								if (cseats < (taken+size)) {
									// 422 is validated in multiedit
									resp.setMessagecode("cs422");
									resp.addError("Requested date is full.");
									Logger.highlight("AVAILABILITY","Requested date is full.");
									r.setValid(false);
								}
							}


						}
					}
					else {
						resp.setMessagecode("cs412");
						resp.addError("Requested availability not found.");
						Logger.highlight("AVAILABILITY","Requested availability not found.");
						r.setValid(false);
					}
				}
				else {
					resp.setMessagecode("cs412");
					resp.addError("Requested availability not found.");
					Logger.highlight("AVAILABILITY","Requested availability was not specified.");
					r.setValid(false);
				}

				if (s.DATECODE() > e.DATECODE()) {
					resp.setMessagecode("cs412");
					resp.addError("The specified date range is not valid. The requested start date (" + s.getString("MM/DD/YYYY") + ") is greater than the requested ending date (" + e.getString("MM/DD/YYYY") + ")");
					Logger.highlight("AVAILABILITY","The specified date range is not valid. The requested start date (" + s.getString("MM/DD/YYYY") + ") is greater than the requested ending date (" + e.getString("MM/DD/YYYY") + ")");
					r.setValid(false);
				}
				
			}
			else {
				resp.setMessagecode("cs412");
				resp.addError("Requested availability not found.");
				Logger.highlight("AVAILABILITY","Requested availability not found.");
				r.setValid(false);
			}
		}

		if (u.isStaff()) {
			resp.setErrors(new ArrayList<MessageVO>());
			resp.setMessagecode("cs200");
			resp.setValid(true);
			r.setValid(true);
		}
		db.clear();
		r.setStart(s);
		r.setEnd(e);
		r.setResponse(resp);
		return r;
	}

	public static int seats(String startdate, String starttime, String cutoff, int bufferhours, int seats, int bufferseats) {
		int r = seats;
		if (bufferhours > 0) {
			Timekeeper s = new Timekeeper();
			s.setDate(startdate);
			s.setTime(starttime);

			Timekeeper c = new Timekeeper();
			c.addDay(1);
			c.setHour(0);
			c.setMinute(0);
			c.setSecond(0);
			if (Operator.hasValue(cutoff)) {
				c.setTime(cutoff);
			}
			c.addHour(bufferhours);
			if (c.DATECODE() == s.DATECODE()) {
				if (c.HHMM() >= s.HHMM()) {
					r = r + bufferseats;
				}
			}
			else if (c.DATECODE() == s.DATECODE()) {
				r = r + bufferseats;
			}

		}
		return r;
	}

	public static ValidateUtil validateMultiAvailability(String apptids, String date, String time) {

		ValidateUtil r = new ValidateUtil();

		ResponseVO resp = new ResponseVO();
		Sage db = new Sage();
		String command = "";

		Timekeeper s = new Timekeeper();
		s.setDate(date);

		Timekeeper e = new Timekeeper();
		e = s.copy();

		boolean valid = true;
		if (valid) {

			int defaultid = -1;
			int customid = -1;

			if (Operator.hasValue(time)) {
				s.setHour(0);
				s.setMinute(0);
				s.setSecond(0);

				String[] arr = Operator.split(time, "|");
				if (arr.length > 0) {
					defaultid = Operator.toInt(arr[0], 0);
				}
				if (arr.length > 1) {
					customid = Operator.toInt(arr[1], 0);
				}

				if (defaultid > 0) {
					Logger.info("AVAILABILITY", "Validate Default Time: "+defaultid);
					command = AvailabilitySQL.getCustomAvailability(s);
					if (db.query(command) && db.size() > 0) {
						resp.setMessagecode("cs412");
						resp.addError("Availability of the requested time is not valid.");
						Logger.highlight("AVAILABILITY","Availability of the requested time is not valid.");
						r.setValid(false);
					}
					command = AvailabilitySQL.getDefaultAvailability(defaultid);
					if (db.query(command) && db.next()) {
						int dow = db.getInt("DAY_OF_WEEK");
						int sdow = s.DAY_OF_WEEK();
						if (dow != sdow) {
							resp.setMessagecode("cs412");
							resp.addError("Availability of the requested date is not valid.");
							Logger.highlight("AVAILABILITY","Day ("+sdow+") of the requested date ("+s.getString("MM/DD/YYYY")+") falls outside the predefined day ("+dow+") of the selected time.");
							r.setValid(false);
						}
						else {
							s.setTime(db.getString("TIME_START"));
							e.setTime(db.getString("TIME_END"));
						}
					}
					else {
						resp.setMessagecode("cs412");
						resp.addError("Requested availability not found.");
						Logger.highlight("AVAILABILITY","Requested availability not found.");
						r.setValid(false);
					}
				}
				else if (customid > 0) {
					Logger.info("AVAILABILITY", "Validate Custom Time: "+customid);
					command = AvailabilitySQL.getCustomAvailability(customid);
					if (db.query(command) && db.next()) {
						Timekeeper cd = new Timekeeper();
						cd.setDate(db.getString("CUSTOM_DATE"));
						if (s.DATECODE() != cd.DATECODE()) {
							resp.setMessagecode("cs412");
							resp.addError("Availability of the requested date is not valid.");
							Logger.highlight("AVAILABILITY","Availability of the requested date is not valid.");
							r.setValid(false);
						}
						else {
							s.setTime(db.getString("TIME_START"));
							e.setTime(db.getString("TIME_END"));
						}
					}
					else {
						resp.setMessagecode("cs412");
						resp.addError("Requested availability not found.");
						Logger.highlight("AVAILABILITY","Requested availability not found.");
						r.setValid(false);
					}
				}
				else {
					resp.setMessagecode("cs412");
					resp.addError("Requested availability not found.");
					Logger.highlight("AVAILABILITY","Requested availability was not specified.");
					r.setValid(false);
				}

				if (s.DATECODE() > e.DATECODE()) {
					resp.setMessagecode("cs412");
					resp.addError("The specified date range is not valid. The requested start date (" + s.getString("MM/DD/YYYY") + ") is greater than the requested ending date (" + e.getString("MM/DD/YYYY") + ")");
					Logger.highlight("AVAILABILITY","The specified date range is not valid. The requested start date (" + s.getString("MM/DD/YYYY") + ") is greater than the requested ending date (" + e.getString("MM/DD/YYYY") + ")");
					r.setValid(false);
				}
				
			}
			else {
				resp.setMessagecode("cs412");
				resp.addError("Requested availability not found.");
				Logger.highlight("AVAILABILITY","Requested availability not found.");
				r.setValid(false);
			}
		}

		db.clear();
		r.setStart(s);
		r.setEnd(e);
		r.setResponse(resp);
		return r;
	}

	public static boolean update(String type, int typeid, int apptid, String collaborators, String team, String notes, int userid, String ip) {
		boolean r = true;
		int noteid = -1;
		UsersAgent.updateUsers("appointment", apptid, collaborators, userid, ip);
		TeamAgent.updateTeam("appointment", apptid, team, userid);
		if (Operator.hasValue(notes)) {
			noteid = addNote(notes, apptid, userid, ip);
		}
		addHistory(type, typeid, "update", apptid, noteid, notes, collaborators, team);
		return r;
	}

	public static ResponseVO add(int userid, String ip, String type, int typeid, DataVO fv, Token u) {
		int appttypeid = Operator.toInt(fv.get("LKUP_APPOINTMENT_TYPE_ID"));
		int reviewid = Operator.toInt(fv.get("REVIEW_ID"));
		String date = fv.get("DATE");
		String time = fv.get("TIME");
		if (appttypeid > 0) {
			return add(type, typeid, appttypeid, Operator.toInt(fv.get("STATUS_ID")), fv.get("SUBJECT"), fv.get("DATE"), fv.get("TIME"), fv.get("NOTES"), fv.get("COLLABORATORS"), fv.get("TEAM"), userid, ip, u);
		}
		else if (appttypeid < 0 && reviewid > 0) {
			int rgroupid = appttypeid * -1;
			int statusid = Operator.toInt(fv.get("STATUS_ID"));
			if (statusid < 0) { statusid = statusid * -1; }
			return ReviewAgent.createCombo("", type, typeid, rgroupid, reviewid, statusid, "", fv.get("NOTES"), "", fv.get("TEAM"), "", fv.get("DATE"), fv.get("TIME"), "", "", fv.get("ATTACHMENT_TITLE"), fv.get("ATTACHMENT"), Operator.toInt(fv.get("ATTACHMENT_TYPE_ID")), fv.get("ATTACHMENT_DESCRIPTION"), fv.get("ATTACHMENT_PUBLIC"), userid, ip, true, u);
		}
		else if (reviewid > 0 && Operator.hasValue(date) && Operator.hasValue(time)) {
			return ReviewAgent.scheduleInspection(type, typeid, reviewid, fv.get("NOTES"), "", "", fv.get("DATE"), fv.get("TIME"), "", userid, ip, u);
		}
		else {
			ResponseVO r = new ResponseVO();
			r.setMessagecode("cs412");
			r.addError("Command not found");
			return r;
		}
	}

	public static ResponseVO add(String type, int typeid, int appttypeid, int statusid, String subject, String date, String time, String notes, String collaborators, String team, int userid, String ip, Token u) {
		ValidateUtil v = validateAvailability("", type, typeid, -1, date, time, u);
		return add(v, type, typeid, appttypeid, -1, -1, statusid, subject, notes, collaborators, team, userid, ip);
	}

	public static ResponseVO add(ValidateUtil v, String type, int typeid, int appttypeid, int refcomboreviewid, int refcomboactionid, int statusid, String subject, String notes, String collaborators, String team, int userid, String ip) {
		ResponseVO r = v.getResponse();
		boolean valid = v.isValid();

		if (valid) {

			Sage db = new Sage();
			String command = "";

			Timekeeper s = v.getStart();
			Timekeeper e = v.getEnd();

			Timekeeper d = new Timekeeper();
			command = AppointmentSQL.addAppointment(type, appttypeid, refcomboreviewid, subject, userid, ip, d);
			Logger.info("APPOINTMENT", "Add Appointment");
			if (db.update(command)) {
				command = AppointmentSQL.getAppointment(subject, userid, d);

				Logger.info("APPOINTMENT", "Get Added Appointment");
				if (db.query(command) && db.next()) {
					int apptid = db.getInt("ID");
					if (apptid > 0) {

						command = AppointmentSQL.addSchedule(apptid, s, e, statusid, refcomboactionid, userid, ip, d);
						Logger.info("APPOINTMENT", "Add Schedule");
						if (db.query(command) && db.next()) {
							int schid = db.getInt("ID");

							command = GeneralSQL.insertRef(type, "APPOINTMENT", typeid, apptid, userid);
							Logger.info("APPOINTMENT", "Add References");
							db.update(command);

							command = AppointmentSQL.getSchedule(apptid, s, e, refcomboactionid, userid, d);
							if (db.query(command) && db.next()) {
								int schedid = db.getInt("ID");
								if (schedid > 0) {
									r.addInfo("APPOINTMENT_SCHEDULE_ID", Operator.toString(schedid));
								}
							}

							int noteid = -1;
							if (Operator.hasValue(notes)) {
								Logger.info("APPOINTMENT", "Add Notes");
								noteid = addNote(notes, apptid, userid, ip);
							}

							if (userid > 0) {
								UsersAgent.addUser("appointment", apptid, Operator.toString(userid), userid, ip);
							}
							if (Operator.hasValue(collaborators)) {
								Logger.info("APPOINTMENT", "Add Collaborators");
								UsersAgent.updateUsers("appointment", apptid, collaborators, userid, ip);
							}

							if (Operator.hasValue(team)) {
								Logger.info("APPOINTMENT", "Add Team");
								TeamAgent.updateTeam("appointment", apptid, team, userid);
							}

							r.addInfo("APPOINTMENT_ID", Operator.toString(apptid));
							r.setMessagecode("cs200");

							addHistory(type, typeid, "add", apptid, noteid, notes, collaborators, team);
						}
						else {
							r.setMessagecode("cs500");
							r.addError("Server Error");
							Logger.highlight("AVAILABILITY","Could not add schedule to the database.");
						}
					}
					else {
						r.setMessagecode("cs500");
						r.addError("Server Error");
						Logger.highlight("AVAILABILITY","Could not find added id appointment in the database.");
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Server Error");
					Logger.highlight("AVAILABILITY","Could not find added appointment in the database.");
				}
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Server Error");
				Logger.highlight("AVAILABILITY","Could not add appointment to the database.");
			}
			db.clear();
		}

		return r;
	}

	public static int addHistory(String type, int typeid, String action, int apptid, int noteid, String note, String collabids, String teamids) {
		if (apptid < 1) { return -1; }
		int r = -1;
		int appointmentid = -1;
		int appointmentscheduleid = -1;
		int lkupappointmenttypeid = -1;
		int refcomboreviewreviewid = -1;
		int refcomboreviewactionid = -1;
		String subject = "";
		String ref = "";
		String appointmentdate = "";
		String startdate = "";
		String enddate = "";
		int lkupappointmentstatusid = -1;
		String status = "";
		int parentid = -1;
		String collaborators = "";
		String team = "";
		String source = "";
		String active = "";
		int createdby = -1;
		String createdusername = "";
		String createddate = "";
		int updatedby = -1;
		String updatedusername = "";
		String updateddate = "";
		String createdip = "";
		String updatedip = "";
		boolean valid = false;
		String command = AppointmentSQL.getFullAppointment(apptid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			appointmentid = db.getInt("APPOINTMENT_ID");
			appointmentscheduleid = db.getInt("APPOINTMENT_SCHEDULE_ID");
			lkupappointmenttypeid = db.getInt("LKUP_APPOINTMENT_TYPE_ID");
			refcomboreviewreviewid = db.getInt("REF_COMBOREVIEW_REVIEW_ID");
			refcomboreviewactionid = db.getInt("REF_COMBOREVIEW_ACTION_ID");
			subject = db.getString("SUBJECT");
			ref = db.getString("TYPE");
			appointmentdate = db.getString("APPOINTMENT_DATE");
			startdate = db.getString("START_DATE");
			enddate = db.getString("END_DATE");
			lkupappointmentstatusid = db.getInt("LKUP_APPOINTMENT_STATUS_ID");
			status = db.getString("STATUS");
			parentid = db.getInt("PARENT_ID");
			source = db.getString("SOURCE");
			active = db.getString("ACTIVE");
			createdby = db.getInt("CREATED_BY");
			createdusername = db.getString("CREATED_USERNAME");
			createddate = db.getString("CREATED_DATE");
			updatedby = db.getInt("UPDATED_BY");
			updatedusername = db.getString("UPDATED_USERNAME");
			updateddate = db.getString("UPDATED_DATE");
			createdip = db.getString("CREATED_IP");
			updatedip = db.getString("UPDATED_IP");
			valid = true;
			if (Operator.hasValue(collabids)) {
				String[] c = Operator.split(collabids, "|");
				command = UsersSQL.getUsers(c);
				if (Operator.hasValue(command)) {
					if (db.query(command)) {
						StringBuilder sb = new StringBuilder();
						boolean empty = true;
						while (db.next()) {
							if (db.hasValue("USERNAME")) {
								if (empty) { sb.append(","); }
								sb.append(db.getString("USERNAME"));
								empty = false;
							}
						}
						collaborators = sb.toString();
					}
				}
			}
			if (Operator.hasValue(teamids)) {
				String[] t = Operator.split(teamids, "|");
				command = UsersSQL.getUsers(t);
				if (Operator.hasValue(command)) {
					if (db.query(command)) {
						StringBuilder sb = new StringBuilder();
						boolean empty = true;
						while (db.next()) {
							if (db.hasValue("USERNAME")) {
								if (empty) { sb.append(","); }
								sb.append(db.getString("USERNAME"));
								empty = false;
							}
						}
						team = sb.toString();
					}
				}
			}
		}
		db.clear();
		if (valid) {
			command = AppointmentSQL.addHistory(appointmentid, appointmentscheduleid, lkupappointmenttypeid, refcomboreviewreviewid, refcomboreviewactionid, subject, ref, noteid, note, appointmentdate, startdate, enddate, lkupappointmentstatusid, status, parentid, collaborators, team, source, active, createdby, createdusername, createddate, updatedby, updatedusername, updateddate, createdip, updatedip);
			if (Operator.hasValue(command)) {
				db = new Sage(CsApiConfig.getHistorySource());
				if (db.query(command) && db.next()) {
					r = db.getInt("ID");
				}
				db.clear();
			}
		}
		CsReflect.addHistory(type, typeid, "appointment", r, action);
		return r;
	}

//	public static ResponseVO completeReview(String type, int typeid, int scheduleid, String subject, int reviewstatusid, String date, String notes, String collaborators, String team, int userid, String ip) {
//		ResponseVO r = new ResponseVO();
//		if (scheduleid < 1) {
//			r.setMessagecode("cs412");
//			r.addError("Schedule not found in the request.");
//			Logger.highlight("AVAILABILITY","Schedule not found in the request.");
//			return r;
//		}
//		else {
//			if (reviewstatusid < 0) { reviewstatusid = reviewstatusid * -1; }
//			r.setMessagecode("cs200");
//			Timekeeper d = new Timekeeper();
//			Timekeeper sdate = new Timekeeper();
//			sdate.setDate(date);
//			String command = "";
//			Sage db = new Sage();
//			command = AppointmentSQL.updateSchedule(scheduleid, "MODIFIED", userid, ip);
//			if (Operator.hasValue(command)) {
//				if (db.update(command)) {
//					command = AppointmentSQL.copySchedule(scheduleid, "COMPLETE", reviewstatusid, userid, ip, d);
//					if (Operator.hasValue(command)) {
//						if (db.update(command)) {
//							command = AppointmentSQL.getChildSchedule(scheduleid, userid, d);
//							if (Operator.hasValue(command)) {
//								if (db.query(command) && db.next()) {
//									int apptid = db.getInt("APPOINTMENT_ID");
//									scheduleid = db.getInt("ID");
//									int reviewid = db.getInt("REVIEW_ID");
//									command = AppointmentSQL.updateAppointment(apptid, subject, userid, ip);
//									db.update(command);
//									command = ReviewSQL.addAction(reviewid, reviewstatusid, sdate, notes, userid, scheduleid, userid, ip, d);
//									if (Operator.hasValue(command)) {
//										if (db.update(command)) {
//											command = ReviewSQL.getAction(reviewid, reviewstatusid, userid, d);
//											if (db.query(command) && db.next()) {
//												int actid = db.getInt("ID");
//												command = ReviewSQL.addActionRef(type, typeid, actid, userid, ip);
//												if (db.update(command)) {
//
//													String notemsg = addNote(notes, apptid, userid, ip);
//													if (Operator.hasValue(notemsg)) { r.addError(notemsg); }
//
//													UsersAgent.updateUsers("appointment", apptid, collaborators, userid);
//
//													TeamAgent.updateTeam("appointment", apptid, team, userid);
//												}
//												else {
//													r.setMessagecode("cs500");
//													r.addError("Database error occured while adding review reference");
//													Logger.highlight("AVAILABILITY","Could not add review reference");
//												}
//											}
//											else {
//												r.setMessagecode("cs500");
//												r.addError("An error occured while querying for new review action");
//												Logger.highlight("AVAILABILITY","Could not retrieve review action");
//											}
//
//										}
//										else {
//											r.setMessagecode("cs500");
//											r.addError("Database error occured while adding review action");
//											Logger.highlight("AVAILABILITY","Could not add review action");
//										}
//									}
//									else {
//										r.setMessagecode("cs500");
//										r.addError("An error occured while adding review action");
//										Logger.highlight("AVAILABILITY","Could not add review action");
//									}
//								}
//								else {
//									r.setMessagecode("cs500");
//									r.addError("An error occured while querying for new schedule");
//									Logger.highlight("AVAILABILITY","Could not query for new schedule");
//								}
//							}
//							else {
//								r.setMessagecode("cs500");
//								r.addError("An error occured while retrieving new schedule");
//								Logger.highlight("AVAILABILITY","Could not retrieve new schedule");
//							}
//						}
//						else {
//							r.setMessagecode("cs500");
//							r.addError("Database error occured while creating new schedule");
//							Logger.highlight("AVAILABILITY","Could not create new schedule");
//						}
//					}
//					else {
//						r.setMessagecode("cs500");
//						r.addError("An error occured while creating new schedule");
//						Logger.highlight("AVAILABILITY","Could not create new schedule");
//					}
//				}
//				else {
//					r.setMessagecode("cs500");
//					r.addError("Database error occured while updating original schedule");
//					Logger.highlight("AVAILABILITY","Could not update original schedule");
//				}
//			}
//			else {
//				r.setMessagecode("cs500");
//				r.addError("An error occured while updating original schedule");
//				Logger.highlight("AVAILABILITY","Could not update original schedule");
//			}
//			db.clear();
//		}
//		
//		return r;
//	}
//
//	public static ResponseVO update(int scheduleid, int userid, String ip, String type, int typeid, DataVO fv) {
//
//		ResponseVO r = new ResponseVO();
//		if (scheduleid < 1) {
//			r.setMessagecode("cs412");
//			r.addError("Schedule not found in the request.");
//			Logger.highlight("AVAILABILITY","Schedule not found in the request.");
//			return r;
//		}
//		int mstatus = Operator.toInt(fv.get("STATUS_ID"));
//		if (mstatus < 0) {
//			return completeReview(type, typeid, scheduleid, fv.get("SUBJECT"), mstatus, fv.get("DATE"), fv.get("NOTES"), fv.get("COLLABORATORS"), fv.get("TEAM"), userid, ip);
//		}
//		else {
//			boolean complete = false;
//			Sage db = new Sage();
//			int apptid = -1;
//			int reviewid = -1;
//			String command = "";
//			if (scheduleid > 0) {
//				command = AppointmentSQL.getSchedule(scheduleid);
//				if (db.query(command) && db.next()) {
//					apptid = db.getInt("APPOINTMENT_ID");
//					reviewid = db.getInt("REVIEW_ID");
//					complete = db.equalsIgnoreCase("COMPLETE", "Y");
//					if (db.getInt("LKUP_REVIEW_STATUS_ID") > 0) {
//						complete = true;
//					}
//				}
//			}
//
//			if (complete) {
//				String note = fv.get("NOTES");
//				String notemsg = addNote(note, apptid, userid, ip);
//				if (Operator.hasValue(notemsg)) { r.addError(notemsg); }
//				String coll = fv.get("COLLABORATORS");
//				PeopleAgent.updatePeople("appointment", apptid, coll, userid);
//				r.setMessagecode("cs200");
//			}
//			else {
//				ValidateUtil v = validateAvailability(Operator.toString(scheduleid), type, typeid, Operator.toInt(fv.get("REVIEW_ID")), fv.get("DATE"), fv.get("TIME"));
//				r = v.getResponse();
//				boolean valid = v.isValid();
//
//				if (valid) {
//					Timekeeper s = v.getStart();
//					Timekeeper e = v.getEnd();
//
//					Timekeeper d = new Timekeeper();
//					command = AppointmentSQL.updateAppointment(apptid, fv.get("SUBJECT"), userid, ip);
//					db.update(command);
//					boolean reschedule = true;
//					boolean updatestatus = true;
//					command = AppointmentSQL.getSchedule(scheduleid);
//					if (db.query(command) && db.next()) {
//						String dbss = db.getString("START_DATE");
//						Timekeeper dbs = new Timekeeper();
//						dbs.setDate(dbss);
//						String dbes = db.getString("END_DATE");
//						Timekeeper dbe = new Timekeeper();
//						dbe.setDate(dbes);
//						if (s.FULLDATECODE() == dbs.FULLDATECODE() && e.FULLDATECODE() == dbe.FULLDATECODE()) {
//							reschedule = false;
//						}
//
//						int statusid = db.getInt("LKUP_APPOINTMENT_STATUS_ID");
//						int reviewstid = db.getInt("LKUP_REVIEW_STATUS_ID");
//						if (mstatus < 0 && reviewstid == (mstatus * -1)) {
//							updatestatus = false;
//						}
//						else if (mstatus > 0 && statusid == mstatus) {
//							updatestatus = false;
//						}
//					}
//					String n = fv.get("NOTES");
//					String restatus = "RESCHEDULED";
//					if (!reschedule) { restatus = "MODIFIED"; }
//					if (reschedule || updatestatus) {
//						command = AppointmentSQL.updateSchedule(scheduleid, restatus, userid, ip);
//						if (db.update(command)) {
//							command = AppointmentSQL.addSchedule(apptid, s, e, mstatus, userid, ip, d, scheduleid);
//							db.update(command);
//						}
//						else {
//							r.setMessagecode("cs500");
//							r.addError("Server Error");
//							Logger.highlight("AVAILABILITY","Could not update schedule in the database.");
//						}
//					}
//
//					String notemsg = addNote(n, apptid, userid, ip);
//					if (Operator.hasValue(notemsg)) { r.addError(notemsg); }
//
//					String coll = fv.get("COLLABORATORS");
//					UsersAgent.updateUsers("appointment", apptid, coll, userid);
//					String team = fv.get("TEAM");
//					TeamAgent.updateTeam("appointment", apptid, team, userid);
//					r.setMessagecode("cs200");
//
//				}
//			}
//
//			db.clear();
//		}
//		return r;
//	}
//
//	public static ResponseVO multiedit(String scheduleids, int userid, String ip, DataVO fv) {
//		ResponseVO r = new ResponseVO();
//		boolean valid = true;
//		Logger.highlight("Editing "+scheduleids);
//		if (!Operator.hasValue(scheduleids)) {
//			r.setMessagecode("cs412");
//			r.addError("Schedule not found in the request.");
//			Logger.highlight("AVAILABILITY","Schedule not found in the request.");
//			return r;
//		}
//		String command = "";
//		if (Operator.hasValue(scheduleids)) {
//			ArrayList<String> aids = new ArrayList<String>();
//			Sage db = new Sage();
//			command = AppointmentSQL.getSchedules(scheduleids);
//			if (db.query(command)) {
//				while (db.next() && valid) {
//					boolean complete = db.equalsIgnoreCase("COMPLETE", "Y");
//					if (db.getInt("LKUP_REVIEW_STATUS_ID") > 0) {
//						complete = true;
//					}
//					if (complete) {
//						r.setMessagecode("cs412");
//						r.addError("At least one item can not be edited because it has been completed.");
//						Logger.highlight("AVAILABILITY","At least one item can not be edited because it has been previously completed.");
//						valid = false;
//					}
//					else {
//						StringBuilder sb = new StringBuilder();
//						sb.append(db.getString("APPOINTMENT_ID")).append("|").append(db.getString("ID"));
//						aids.add(sb.toString());
//					}
//				}
//				if (valid) {
//					Timekeeper d = new Timekeeper();
//					String time = fv.get("TIME");
//					if (Operator.hasValue(time) && !Operator.equalsIgnoreCase(time, CsConfig.SKIPVALUE)) {
//						ValidateUtil v = validateMultiAvailability(scheduleids, fv);
//						ResponseVO vr = v.getResponse();
//						String mc = vr.getMessagecode();
//						if (Operator.equalsIgnoreCase(mc, "cs422")) {
//							r.setMessagecode("cs422");
//							r.addError("There are not enough seats available to complete the appointment request.");
//							valid = false;
//						}
//						else {
//							for (int i=0; i<aids.size(); i++) {
//								String aid = aids.get(i);
//								if (Operator.hasValue(aid)) {
//									String[] asids = Operator.split(aid, "|");
//									if (asids.length > 1) {
//										String apptidstr = asids[0];
//										String schedidstr = asids[1];
//										int apptid = Operator.toInt(apptidstr);
//										int schedid = Operator.toInt(schedidstr);
//										if (apptid > 0 && schedid > 0) {
//											command = AppointmentSQL.addSchedule(apptid, v.getStart(), v.getEnd(), userid, ip, d, schedid);
//											if (db.update(command)) {
//												command = AppointmentSQL.updateSchedule(schedid, "RESCHEDULED", userid, ip);
//												db.update(command);
//											}
//											else {
//												r.setMessagecode("cs500");
//												r.addError("Server error occured while rescheduling appointment "+apptid+". Any other request will continue to be processed.");
//												Logger.highlight("AVAILABILITY","Server error occured while rescheduling appointment "+apptid);
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//				if (valid) {
//					String notes = fv.get("NOTES");
//					String team = fv.get("TEAM");
//					if (Operator.hasValue(notes) || !Operator.equalsIgnoreCase(team, CsConfig.SKIPVALUE)) {
//						for (int i=0; i<aids.size(); i++) {
//							String aid = aids.get(i);
//							if (Operator.hasValue(aid)) {
//								String[] asids = Operator.split(aid, "|");
//								if (asids.length > 1) {
//									String apptidstr = asids[0];
//									int apptid = Operator.toInt(apptidstr);
//									if (apptid > 0) {
//										if (Operator.hasValue(notes) && !Operator.equalsIgnoreCase(notes, CsConfig.SKIPVALUE)) {
//											String notemsg = addNote(notes, apptid, userid, ip);
//											if (Operator.hasValue(notemsg)) { r.addError(notemsg); }
//										}
//										if (!Operator.equalsIgnoreCase(team, CsConfig.SKIPVALUE)) {
//											TeamAgent.updateTeam("appointment", apptid, team, userid);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//			db.clear();
//		}
//		if (valid) {
//			r.setMessagecode("cs200");
//		}
//		return r;
//	}
//
	public static boolean cancel(int apptid, String note, int userid, String ip) {
		boolean r = false;
		int id = -1;
		Sage db = new Sage();
		String command = AppointmentSQL.updateAppointment(apptid, userid, ip);
		db.update(command);
		command = AppointmentSQL.updateSchedule(apptid, "CANCELLED", userid, ip);
		r = db.update(command);
		if (r) {
			addNote(note, id, userid, ip);
		}
		db.clear();
		return r;
	}

	public static boolean reassign(String scheduleids, String teamids, int userid, String ip) {
		boolean r = false;
		Sage db = new Sage();
		try {
			String[] t = Operator.split(teamids, "|");

			String command = "";
			command = AppointmentSQL.getSchedules(scheduleids);
			if (db.query(command)) {
				String[] a = new String[db.size()];
				int count = 0;
				while (db.next()) {
					a[count] = db.getString("APPOINTMENT_ID");
					count ++;
				}
				command = AppointmentSQL.deactivateTeam(a, userid, ip);
				db.update(command);

				for (int ai=0; ai<a.length; ai++) {
					int aid = Operator.toInt(a[ai]);
					if (aid > 0) {
						for (int ti=0; ti<t.length; ti++) {
							int tid = Operator.toInt(t[ti]);
							if (tid > 0) {
								command = AppointmentSQL.addTeam(aid, tid, userid, ip);
								if (db.update(command)) {
									r = true;
								}
							}
						}
					}
				}

			}
		}
		catch (Exception e) {}
		db.clear();
		if (r) {
			GlobalSearch.indexWait(GlobalSearch.INSPECTIONS_DELTA);
		}
		return r;
	}

	public static ResponseVO reschedule(String scheduleids, String date, String time, String notify, int userid, String ip) {
		boolean k = false;
		ResponseVO r = new ResponseVO();
		ValidateUtil v = validateMultiAvailability(scheduleids, date, time);
		if (v.isValid()) {
			Timekeeper d = new Timekeeper();
			String command = "";
			String[] sids = Operator.split(scheduleids, "|");
			Sage db = new Sage();
			for (int i=0; i<sids.length; i++) {
				int scheduleid = Operator.toInt(sids[i]);
				if (scheduleid > 0) {
					command = AppointmentSQL.createReschedule(scheduleid, v.getStart(), v.getEnd(), userid, ip, d);
					if (db.update(command)) {
						command = AppointmentSQL.updateStatus(scheduleid, "RESCHEDULED", userid, ip);
						db.update(command);
						Logger.info("**********************************NOTIFY*******************"+notify);
						if (Operator.equalsIgnoreCase(notify, "Y")) {
							String email = "";
							String name = "";
							String actnbr = "";
							String review = "";
							String status = "";
							int actionrefid = -1;
							int usersid = -1;
							command = AppointmentSQL.getScheduleCollaborators(scheduleid);
							db.query(command);
							while (db.next()) {
								email = db.getString("EMAIL");
								boolean checknotify = Operator.s2b(db.getString("NOTIFY"));
								Logger.info("**********************************email*******************"+email);
								actnbr = db.getString("ACT_NBR");
								review = db.getString("REVIEW");
								status = db.getString("STATUS");
								usersid = db.getInt("USERS_ID");
								actionrefid = db.getInt("REF_COMBOREVIEW_ACTION_ID");
								if (!Operator.hasValue(email) || !Operator.isEmail(email)) {
									email = db.getString("USERNAME");
								}
								if (Operator.hasValue(email) && Operator.isEmail(email) && checknotify) {
									name = db.getString("NAME");
									LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
									m.put("ACTIVITY NUMBER", actnbr);
									m.put("REVIEW", review);
									m.put("APPOINTMENT FROM", v.getStart().getString("MM-DD-YYYY @ HH:MM"));
									m.put("APPOINTMENT TO", v.getEnd().getString("MM-DD-YYYY @ HH:MM"));
									m.put("CURRENT STATUS", status);
									String subject = "City of Beverly Hills: An update to a scheduled appointment regarding activity number "+actnbr+" has been made";
									String message = "Your review has been rescheduled.";
									String content = Email.genericTemplate(name, message, m);
									CommunicationsAgent.email("notes", actionrefid, email, usersid, subject, content, userid, ip);
								}
							}
						}
					}
				}
			}
			db.clear();
		}
		
		if (k) {
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
		}
		return r;
	}

	public static boolean reschedule(int scheduleid, Timekeeper start, Timekeeper end, int userid, String ip) {
		boolean r = false;
		Timekeeper d = new Timekeeper();
		Sage db = new Sage();
		String command = AppointmentSQL.createReschedule(scheduleid, start, end, userid, ip, d);
		if (db.update(command)) {
			command = AppointmentSQL.updateStatus(scheduleid, "RESCHEDULED", userid, ip);
			db.update(command);
		}
		db.clear();
		return r;
	}

	public static boolean completeReview(int revrefid, int actid, String note, int userid, String ip) {
		boolean r = false;
		Sage db = new Sage();
		String command = AppointmentSQL.updateReviewApointment(revrefid, userid, ip);
		db.update(command);

		command = AppointmentSQL.completeSchedule(revrefid, actid, userid, ip);
		r = db.update(command);

		db.clear();
		return r;
	}

	public static boolean cancelReview(int revrefid, int actid, String note, int userid, String ip) {
		boolean r = false;
		Sage db = new Sage();
		String command = AppointmentSQL.updateReviewApointment(revrefid, userid, ip);
		db.update(command);

		command = AppointmentSQL.cancelSchedule(revrefid, actid, userid, ip);
		r = db.update(command);

		db.clear();
		return r;
	}

	public static int getAppointmentId(int scheduleid) {
		int id = -1;
		if (scheduleid > 0) {
			String command = AppointmentSQL.getAppointment(scheduleid);
			Sage db = new Sage();
			db.query(command);
			if (db.next()) {
				id = db.getInt("ID");
			}
			db.clear();
		}
		return id;
	}

	public static int addNote(String note, int apptid, int userid, String ip) {
		int r = -1;
		if (Operator.hasValue(note) && !Operator.equalsIgnoreCase(note, CsConfig.SKIPVALUE)) {
			Timekeeper d = new Timekeeper();
			String command = NotesSQL.addNote(note, "Note", userid, ip, d);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
				if (r > 0) {
					command = NotesSQL.addNoteRef("APPOINTMENT", apptid, r, userid, ip, d);
					db.update(command);
				}
			}
			db.clear();
		}
		return r;
	}

	public static HashMap<String, Integer> getMaxInfo(String type, int typeid, int reviewid, String scheduleid, Timekeeper start, Timekeeper end) {
		HashMap<String, Integer> r = new HashMap<String, Integer>();
		if (!Operator.equalsIgnoreCase(type, "multi")) {
			String t = type;
			int tid = typeid;
			Sage db = new Sage();
			String command = "";
			if (type.equalsIgnoreCase("review")) {
				command = ReviewSQL.getCombo(typeid);
				if (db.query(command) && db.next()) {
					if (db.equalsIgnoreCase("TYPE", "activity")) {
						command = ReviewSQL.getTypeId(typeid, "activity");
						if (db.query(command) && db.next()) {
							tid = db.getInt(ActivityFields.FIELD_ID_REF);
						}
					}
				}
				command = AppointmentSQL.getReviewMax(reviewid, scheduleid);
			}
			else { command = AppointmentSQL.getMax(type, typeid, scheduleid); }
			if (Operator.hasValue(command)) {
				if (db.query(command) && db.next()) {
					int max = db.getInt("MAX_ACTIVE_APPOINTMENTS");
					int active = db.getInt("ACTIVE_APPOINTMENTS");
					r.put("MAX_ACTIVE_APPOINTMENTS", max);
					r.put("ACTIVE_APPOINTMENTS", active);
				}
			}
			if (Operator.equalsIgnoreCase(t, "activity")) {
				command = ActivitySQL.getActivity(tid);
				if (db.query(command) && db.next()) {
					int projid = db.getInt("PROJECT_ID");
					if (projid > 0) {
						command = AppointmentSQL.getMaxChildDay(projid, start, end, scheduleid);
						db.query(command);
						while (db.next()) {
							int maxday = db.getInt("MAX_CHILD_APPOINTMENTS_DAY");
							int maxchild = db.getInt("MAX_CHILD_APPOINTMENTS");
							r.put("CHILD_APPOINTMENTS", db.getInt("TOTAL_ACTIVE"));
							r.put("MAX_DAY_APPOINTMENTS", maxday);
							r.put("MAX_CHILD_APPOINTMENTS", maxchild);
							String sdate = db.getString("DATE");
							Timekeeper date = new Timekeeper();
							date.setDate(sdate);
							int dayappt = db.getInt("APPOINTMENTS");
							r.put(date.getString("YYYY/MM/DD"), dayappt);
						}
					}
				}
			}
			db.clear();
		}
		return r;
	}

	public static ObjGroupVO getSummary(String type, int typeid, int id, String option) {
		ObjGroupVO g = new ObjGroupVO();
		ArrayList<AppointmentVO> r = new ArrayList<AppointmentVO>();

		boolean docheck = !Operator.hasValue(option);
		boolean doquery = true;
		String command = "";
		Sage db = new Sage();

		if (docheck) {
			command = AppointmentSQL.summary(type, typeid, id, "COUNT");
			if (db.query(command) && db.next() ) {
				if (db.getInt("NUMRESULTS") < 1) {
					doquery = false;
				}
			}
		}

		if (doquery) {
			g = AppointmentFields.summary();
			command = AppointmentSQL.summary(type, typeid, id, option);
			db.query(command);
			if (db.size() > 0) {
				while (db.next()) {
					if (db.hasValue("CONTENT_TYPE")) {
						g.setContenttype(db.getString("CONTENT_TYPE"));
					}
					AppointmentVO vo = new AppointmentVO();
					int appointmentid = db.getInt("APPOINTMENT_ID");
					vo.setId(db.getInt("ID"));
					vo.setAppttypeid(db.getInt("TYPE"));
					vo.setAppttype(db.getString("TYPE_TEXT"));
					vo.setSubject(db.getString("SUBJECT"));
					vo.setCstype(type);
					vo.setComboreviewid(db.getInt("COMBOREVIEW_ID"));
					vo.setReview(db.getString("REVIEW"));
					vo.setReviewid(db.getInt("REVIEW_ID"));
					vo.setRefreviewid(db.getInt("REF_COMBOREVIEW_REVIEW_ID"));
					vo.setCollaboratorsize(db.getInt("COLLABORATORS"));

					AppointmentScheduleVO avo = new AppointmentScheduleVO();
					avo.setId(db.getInt("SCHEDULE_ID"));
					avo.setStatus(db.getString("STATUS"));
					avo.setStart(db.getString("START_DATE"));
					avo.setEnd(db.getString("END_DATE"));
					avo.setScheduled(db.getString("SCHEDULED"));

					vo.addSchedule(avo);

//					vo.setCollaborators(getCollaborators(appointmentid));
					r.add(vo);

				}
				g.setAppointments(r.toArray(new AppointmentVO[r.size()]));
			}
			else {
				command = InfoSQL.getContent("appointment", false);
				if (db.query(command) && db.next()) {
					g.setContenttype("appointment");
				}
			}
		}
		else {
			command = InfoSQL.getContent("appointment", false);
			if (db.query(command) && db.next()) {
				g.setContenttype("appointment");
			}
		}

		db.clear();
		return g;
	}

//	public static ObjGroupVO getId(String type, int typeid, int id) {
//		ObjGroupVO g = new ObjGroupVO();
//		ArrayList<AppointmentVO> r = new ArrayList<AppointmentVO>();
//		g = AppointmentFields.id();
//		Sage db = new Sage();
//		String command = AppointmentSQL.id(type, typeid, id);
//		db.query(command);
//		while (db.next()) {
//			AppointmentVO vo = new AppointmentVO();
//			int appointmentid = db.getInt("APPOINTMENT_ID");
//			vo.setId(db.getInt("ID"));
//			vo.setAppttype(db.getString("TYPE"));
//			vo.setSubject(db.getString("SUBJECT"));
//			vo.setCstype(type);
//			vo.setComboreviewid(db.getInt("COMBOREVIEW_ID"));
//			vo.setReview(db.getString("REVIEW"));
//			vo.setReviewid(db.getInt("REVIEW_ID"));
//			vo.setRefreviewid(db.getInt("REF_COMBOREVIEW_REVIEW_ID"));
//			vo.setCollaboratorsize(db.getInt("COLLABORATORS"));
//
//			AppointmentScheduleVO avo = new AppointmentScheduleVO();
//			avo.setId(db.getInt("SCHEDULE_ID"));
//			avo.setStatus(db.getString("STATUS"));
//			avo.setStart(db.getString("START_DATE"));
//			avo.setEnd(db.getString("END_DATE"));
//			avo.setScheduled(db.getString("SCHEDULED"));
//
//			vo.addSchedule(avo);
//
////			vo.setCollaborators(getCollaborators(appointmentid));
//			r.add(vo);
//
//		}
//		g.setAppointments(r.toArray(new AppointmentVO[r.size()]));
//		db.clear();
//		return g;
//	}
//
	public static ObjGroupVO getDetail(String type, int typeid, int id) {
		ObjGroupVO g = new ObjGroupVO();
		ArrayList<AppointmentVO> r = new ArrayList<AppointmentVO>();
		g = AppointmentFields.details();
		Sage db = new Sage();
		String command = AppointmentSQL.details(type, typeid, id);
		db.query(command);
		while (db.next()) {
			AppointmentVO vo = new AppointmentVO();
			vo.setId(db.getInt("ID"));
			vo.setAppttypeid(db.getInt("TYPE"));
			vo.setAppttype(db.getString("TYPE_TEXT"));
			vo.setSubject(db.getString("SUBJECT"));
			vo.setCstype(type);
			vo.setComboreviewid(db.getInt("COMBOREVIEW_ID"));
			vo.setReview(db.getString("REVIEW"));
			vo.setReviewid(db.getInt("REVIEW_ID"));
			vo.setRefreviewid(db.getInt("REF_COMBOREVIEW_REVIEW_ID"));
			vo.setCollaboratorsize(db.getInt("COLLABORATORS"));

			AppointmentScheduleVO avo = new AppointmentScheduleVO();
			avo.setId(db.getInt("SCHEDULE_ID"));
			avo.setStatus(db.getString("STATUS"));
			avo.setStart(db.getString("START_DATE"));
			avo.setEnd(db.getString("END_DATE"));
			avo.setScheduled(db.getString("SCHEDULED"));

			vo.addSchedule(avo);

//				vo.setCollaborators(getCollaborators(appointmentid));
			r.add(vo);
			g.setAppointments(r.toArray(new AppointmentVO[r.size()]));
		}

		db.clear();
		return g;
	}

	public static ArrayList<UserVO> getCollaborators(int appointmentid) {
		ArrayList<UserVO> a = new ArrayList<UserVO>();
		String command = AppointmentSQL.getCollaborators(appointmentid);
		Sage db = new Sage();
		db.query(command);
		while (db.next()) {
			UserVO vo = new UserVO();
			vo.setUserid(db.getInt("ID"));
			vo.setUsername(db.getString("USERNAME"));
			vo.setFirstname(db.getString("FIRST_NAME"));
			vo.setLastname(db.getString("LAST_NAME"));
			vo.setMiddlename(db.getString("MI"));
			vo.setEmail(db.getString("EMAIL"));
			a.add(vo);
		}
		db.clear();
		return a;
	}


	
	public static boolean reroute(String order, int teamid, int userid, String ip,boolean search) {
		boolean r = false;
		
		
		try{
			r = new DBBatch().insertReRoute(order, teamid, userid);
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		if (search) {
			GlobalSearch.indexWait(GlobalSearch.INSPECTIONS_DELTA);
		}
		return r;
	}


}















