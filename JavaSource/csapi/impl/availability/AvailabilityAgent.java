package csapi.impl.availability;

import java.util.HashMap;
import java.util.LinkedHashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.appointment.AppointmentAgent;
import csshared.vo.AvailabilityDateVO;
import csshared.vo.AvailabilityTimeVO;
import csshared.vo.AvailabilityVO;



public class AvailabilityAgent {

	public static int staffpresched = 250;

	public static AvailabilityVO getAvailabilityVO(int avid, Token u) {
		AvailabilityVO vo = new AvailabilityVO();
		String command = AvailabilitySQL.getAvailability(avid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				vo.setId(db.getInt("ID"));
				vo.setTitle(db.getString("TITLE"));
				if (u.isStaff()) {
					vo.setPreschedule(staffpresched);
				}
				else {
					vo.setPreschedule(db.getInt("PRESCHEDULE_DAYS"));
				}
			}
			db.clear();
		}
		return vo;
	}

	public static AvailabilityVO getAvailability(String type, int typeid, int appttype, int apptsubtype, String scheduleid, Timekeeper start, Timekeeper end, Token u) {
		int presched = -1;
		HashMap<String, Integer> maxinfo = AppointmentAgent.getMaxInfo(type, typeid, apptsubtype, scheduleid, start, end);
		AvailabilityVO vo = new AvailabilityVO();
		String command = AvailabilitySQL.getAvailability(type, typeid, appttype, apptsubtype);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				if (db.next()) {
					vo.setId(db.getInt("ID"));
					if (u.isStaff()) {
						presched = staffpresched;
					}
					else {
						presched = db.getInt("PRESCHEDULE_DAYS", -1);
					}
					maxinfo.put("PRESCHEDULE_DAYS", presched);
				}
			}
			db.clear();
		}
		vo.setPreschedule(presched);
		return getAvailability(vo, maxinfo, start, end, u);
	}

	public static AvailabilityVO getAvailability(int avid, int month, HashMap<String, Integer> maxinfo, Token u) {
		Timekeeper s = new Timekeeper();
		s.setMonth(month);
		s.setDay(1);
		s.setTime("00:00:00");
		Timekeeper e = new Timekeeper();
		e.setMonth(month);
		e.setDay(e.DAYS_IN_MONTH());
		e.setTime("23:59:59");
		return getAvailability(getAvailabilityVO(avid, u), maxinfo, s, e, u);
	}

	public static AvailabilityVO getAvailability(AvailabilityVO vo, HashMap<String, Integer> maxinfo, Timekeeper startdate, Timekeeper enddate, Token u) {
		return getAvailability(vo, maxinfo, startdate, enddate, false, u);
	}

	public static AvailabilityVO getAvailability(AvailabilityVO vo, HashMap<String, Integer> maxinfo, Timekeeper startdate, Timekeeper enddate, boolean multi, Token u) {

		Timekeeper start = startdate.copy();
		start.setHour(0);
		start.setMinute(0);
		start.setSecond(0);

		Timekeeper end = enddate.copy();
		end.setHour(23);
		end.setMinute(59);
		end.setSecond(59);

		if (multi) {
			vo.setPreschedule(-1);
		}
		else {
			if (vo.prescheduleEnd().DATECODE() < end.DATECODE()) {
				end = vo.prescheduleEnd();
			}
		}
		String command = "";

		int avid = vo.getId();
		if (avid < 1) {
			vo.setMindate(start.getString("YYYY/MM/DD"));
			vo.setMaxdate(end.getString("YYYY/MM/DD"));
			vo.createEmpty(start, end);
			Sage db = new Sage();
			command = AvailabilitySQL.getHoliday(start.copy(), end.copy(), avid);
			if (db.query(command)) {
				while (db.next()) {
					Timekeeper td = new Timekeeper(db.getString("HOLIDAY_DATE"));
					String holiday = db.getString("HOLIDAY");
					String message = db.getString("MESSAGE");
					boolean closed = db.equalsIgnoreCase("CLOSED", "Y");
					vo.setHoliday(td.getString("YYYY/MM/DD"), holiday, message, closed);
				}
			}
			db.clear();
			return vo;
		}
		command = AvailabilitySQL.getDefault(avid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				AvailabilityDateVO[] arr = { new AvailabilityDateVO(), new AvailabilityDateVO(), new AvailabilityDateVO(), new AvailabilityDateVO(), new AvailabilityDateVO(), new AvailabilityDateVO(), new AvailabilityDateVO() };
				while (db.next()) {
					Timekeeper s = new Timekeeper();
					s.setTime(db.getString("TIME_START"));
					Timekeeper e = new Timekeeper();
					e.setTime(db.getString("TIME_END"));

					vo.setId(db.getInt("AVAILABILITY_ID"));
					vo.setTitle(db.getString("TITLE"));
					AvailabilityTimeVO t = new AvailabilityTimeVO();
					t.setStaff(u.isStaff());
					t.setId(db.getInt("AVAILABILITY_DEFAULT_ID") + "|0");
					t.setDefaultid(db.getInt("AVAILABILITY_DEFAULT_ID"));
					t.setStart(s.getString("MILITARYTIME"));
					t.setEnd(e.getString("MILITARYTIME"));
					if (u.isStaff()) { t.setPreseats(-1); }
					else { t.setPreseats(db.getInt("SEATS", -1)); }
					t.setBufferhours(db.getInt("BUFFER_HOURS"));
					t.setBufferseats(db.getInt("BUFFER_SEATS"));
					t.setBegin(db.getString("TIME_BEGIN"));
					t.setStop(db.getString("TIME_STOP"));
					t.setPresched(vo.getPreschedule());
					int wkday = db.getInt("DAY_OF_WEEK", 1);
					if (wkday <= 7 && wkday >= 1) {
						arr[wkday-1].setStaff(u.isStaff());
						arr[wkday-1].addTime(t);
					}
				}
				vo.create(start.copy(), end.copy(), maxinfo, arr);
				command = AvailabilitySQL.getCustom(start.copy(), end.copy(), avid);
				if (db.query(command)) {
					while (db.next()) {

						Timekeeper s = new Timekeeper(db.getString("CUSTOM_DATE"));
						s.setTime(db.getString("TIME_START"));

						Timekeeper e = new Timekeeper();
						e.setTime(db.getString("TIME_END"));

						AvailabilityTimeVO t = new AvailabilityTimeVO();
						t.setDate(db.getString("CUSTOM_DATE"));
						t.setStaff(u.isStaff());
						t.setId("0|"+db.getInt("AVAILABILITY_CUSTOM_ID"));
						t.setCustomid(db.getInt("AVAILABILITY_CUSTOM_ID"));
						t.setStart(s.getString("MILITARYTIME"));
						t.setEnd(e.getString("MILITARYTIME"));
						if (u.isStaff()) { 
							t.setPreseats(-1); 
							}
						else { 
							t.setPreseats(db.getInt("SEATS", -1)); 
							}
						t.setBufferhours(db.getInt("BUFFER_HOURS"));
						t.setBufferseats(db.getInt("BUFFER_SEATS"));
						t.setBegin(db.getString("TIME_BEGIN"));
						t.setStop(db.getString("TIME_STOP"));
						t.setPresched(vo.getPreschedule());
						AvailabilityDateVO d = vo.getDate(s.getString("YYYY/MM/DD"));
						d.setStaff(u.isStaff());
						if (!d.isCustom()) {
							d.setCustom(true);
							d.resetTimes();
						}
						d.addTime(t);
						vo.addDate(d);
//						vo.setCustom(s.getString("YYYY/MM/DD"), t);
						
						/*//test sunil 
						int sts = t.getPreseats();
						Logger.highlight(t.getDate()+"--"+sts);
						if (Operator.hasValue(t.getDate()) && t.getBufferhours() > 0 && t.getBufferseats() > 0) {
							Timekeeper avdate = t.dateObject();
							Timekeeper cutoff = t.bufferStart();
							Logger.info(cutoff.DATECODE()+"---"+avdate.DATECODE());
							if (cutoff.DATECODE() == avdate.DATECODE()) {
								Timekeeper k = new Timekeeper();
								Logger.info(k.HHMM()+"---"+avdate.HHMM()+"---"+cutoff.HHMM());
								if (k.HHMM()>=avdate.HHMM() && k.HHMM() <= cutoff.HHMM()) {
									sts = sts + t.getBufferseats();
								}
							}
							
						}
						Logger.highlight(t.getDate()+"<----FINAL--->"+sts+"77777"+t.getSeats());*/
						
					}
				}

				command = AvailabilitySQL.getTaken(start.copy(), end.copy(), avid);
				if (db.query(command)) {
					while (db.next()) {
						Timekeeper td = new Timekeeper(db.getString("DATE_START"));
						td.setTime(db.getString("TIME_START"));
						int taken = db.getInt("TAKEN");
						vo.takeSeat(td, taken);
					}
				}

				command = AvailabilitySQL.getHoliday(start.copy(), end.copy(), avid);
				if (db.query(command)) {
					while (db.next()) {
						Timekeeper td = new Timekeeper(db.getString("HOLIDAY_DATE"));
						String holiday = db.getString("HOLIDAY");
						String message = db.getString("MESSAGE");
						boolean closed = db.equalsIgnoreCase("CLOSED", "Y");
						vo.setHoliday(td.getString("YYYY/MM/DD"), holiday, message, closed);
					}
				}
			}
			db.clear();
		}
		return vo;
	}


	public static void debug(AvailabilityVO vo) {
		LinkedHashMap<String, AvailabilityDateVO> dates = vo.getDates();
		for (String key : dates.keySet()) {
		   Logger.highlight("Date: " + key);   
		    AvailabilityDateVO date = dates.get(key);
	       Logger.highlight(date.getWeekday());
			LinkedHashMap<String, AvailabilityTimeVO> times = date.getTimes();
			for (String tkey : times.keySet())
			{
			    AvailabilityTimeVO tvo = times.get(tkey);
			    Logger.highlight(tvo.getStart());
			}
		}

	}

	public static AvailabilityVO getAvailability(int avid, HashMap<String, Integer> maxinfo, Timekeeper start, Timekeeper end, Token u) {
		AvailabilityVO vo = getAvailabilityVO(avid, u);
		return getAvailability(vo, maxinfo, start, end, u);
	}


	public static AvailabilityVO getAvailability(String type, int typeid, String scheduleid, Timekeeper start, Timekeeper end, Token u) {
		HashMap<String, Integer> maxinfo = AppointmentAgent.getMaxInfo(type, typeid, -1, scheduleid, start, end);

		AvailabilityVO vo = new AvailabilityVO();
		String command = AvailabilitySQL.getAvailability(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				if (db.next()) {
					vo.setId(db.getInt("ID"));
					int presched = db.getInt("PRESCHEDULE_DAYS");
					vo.setPreschedule(presched);
					maxinfo.put("PRESCHEDULE_DAYS", presched);
				}
			}
			db.clear();
		}
		return getAvailability(vo, maxinfo, start, end, u);
	}


	/**
	 * There is a logic used that can produce false results when date range spans multiple availability records
	 * @param start - start date and time
	 * @param end - end date and time
	 * @param avid - availability id
	 * @return number of seats reserved
	 */
	public static int getTaken(Timekeeper start, Timekeeper end, int avid) {
		int r = 0;
		String command = AvailabilitySQL.getTaken(start, end, avid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				while (db.next()) {
					int t = db.getInt("TAKEN");
					if (t > r) {
						r = t;
					}
				}
			}
			db.clear();
		}
		return r;
	}

	
	/**
	 * There is a logic used that can produce false results when date range spans multiple availability records
	 * @param start - start date and time
	 * @param end - end date and time
	 * @param avid - availability id
	 * @return remaining seats available
	 */
	public static int getRemaining1(Timekeeper start, Timekeeper end, int avid) {
		int r = 0;
		int t = 0;
		int s = 0;
		String command = AvailabilitySQL.getTaken(start, end, avid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				while (db.next()) {
					int dbt = db.getInt("TAKEN");
					if (dbt > t) {
						t = dbt;
					}
					int dbs = db.getInt("SEATS");
					if (dbs < s && s > 0) {
						s = dbs;
					}
				}
			}
			db.clear();
		}
		r = s - t;
		return r;
	}






}















