package csapi.impl.appointment;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.availability.AvailabilityAgent;
import csapi.impl.inspections.InspectionsAgent;
import csapi.impl.notes.NotesSQL;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.AvailabilityVO;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class AppointmentImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { e.printStackTrace(); }
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);

//			ObjGroupVO f = AppointmentFields.details();
//			String command = AppointmentSQL.details(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			ObjGroupVO g = AppointmentAgent.getDetail(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			g.setGroupid(vo.getId());

			g.setAction(vo.getAction());
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		Token u = AuthorizeToken.authenticate(vo);
		String type = vo.getType();
		int typeid = vo.getTypeid();
		String ref = vo.getReference();
		if (typeid < 1 && Operator.equalsIgnoreCase(type, "activity") && Operator.hasValue(ref)) {
			typeid = InspectionsAgent.getActivityId(ref);
		}
		int id = Operator.toInt(vo.getId());

		CsDeleteCache.deleteCache(type, typeid, "appointment");
		CsDeleteCache.deleteCache(type, typeid, "reviews");
		if (id > 0) {
			if (AppointmentAgent.update(type, typeid, id, m.get("COLLABORATORS"), m.get("TEAM"), m.get("NOTES"), u.getId(), u.getIp())) {
			}
			else {
				r.setMessagecode("500");
				r.addError("Unable to save collaborators and/or teams");;
			}
		}
		else {
			r = AppointmentAgent.add(u.getId(), vo.getIp(), type, typeid, m, u);
		}
		//Update Activity Expirations dates + 180 days.
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			res = mapper.writeValueAsString(r);
		} catch(Exception e) { }

		return res;
	}

	public static String multiedit(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		Token u = AuthorizeToken.authenticate(vo);
//		r = AppointmentAgent.multiedit(vo.getId(), u.getId(), vo.getIp(), m);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			res = mapper.writeValueAsString(r);
		} catch(Exception e) { }

		return res;
	}

	public static String cancel(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		int id = Operator.toInt(vo.getId());
		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		if (id > 0) {
			DataVO m = DataVO.toDataVO(vo);
			String note = m.get("NOTES");
			b = AppointmentAgent.cancel(id, note, u.getId(), vo.getIp());
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Appointment schedule not specified.");
		}
		if (b) {
			r.setMessagecode("cs200");
			AppointmentAgent.addHistory(vo.getType(), vo.getTypeid(), "cancel", id, -1, "", "", "");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "reviews");
		}
		else {
			r.setMessagecode("cs500");
			r.addMessage("Server Error");
		}
		try {
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}

	public static String reassign(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		String ids = vo.getId();
		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		if (Operator.hasValue(ids)) {
			DataVO m = DataVO.toDataVO(vo);
			String team = m.get("TEAM");
			if (Operator.hasValue(team)) {
				b = AppointmentAgent.reassign(ids, team, u.getId(), u.getIp());
			}
			else {
				r.setMessagecode("cs412");
				r.addMessage("Appointment team not specified.");
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Appointment id not specified.");
		}
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
		}
		else {
			r.setMessagecode("cs500");
			r.addMessage("Server Error");
		}
		try {
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}

	public static String reschedule(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		String ids = vo.getId();
		ObjectMapper mapper = new ObjectMapper();
		Logger.highlight(ids);
		if (Operator.hasValue(ids)) {
			DataVO m = DataVO.toDataVO(vo);
			String date = m.get("DATE");
			String time = m.get("TIME");
			String notify = m.get("NOTIFY");
			if (Operator.hasValue(date) && Operator.hasValue(time)) {
				r = AppointmentAgent.reschedule(ids, date, time, notify, u.getId(), u.getIp());
			}
			else {
				r.setMessagecode("cs412");
				r.addMessage("Appointment team not specified.");
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Appointment id not specified.");
		}
		if (r.isValid()) {
			GlobalSearch.index(GlobalSearch.REVIEW_DELTA);
			GlobalSearch.indexWait(GlobalSearch.INSPECTIONS_DELTA);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r.setMessagecode("cs200");
		}
		else {
			r.setMessagecode("cs500");
			r.addMessage("Server Error");
		}
		try {
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}

	public static String list(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getList(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


	public static String my(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getMy(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String types(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] v = Choices.getChoices(AppointmentSQL.getAppointmentTypes(vo.getType(), vo.getTypeid()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String schedulestatus(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] v = Choices.getChoices(AppointmentSQL.getAppointmentScheduleStatus(vo.getAppttypeid(), vo.getApptsubtypeid()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String availability(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			Timekeeper st = new Timekeeper();
			if (Operator.hasValue(vo.getStartdate())) {
				st.setDate(vo.getStartdate());
			}
			Timekeeper en = new Timekeeper();
			if (Operator.hasValue(vo.getEnddate())) {
				en.setDate(vo.getEnddate());
			}
			else if (vo.getEnd() > 0) {
				en = st.copy();
				en.addDay(vo.getEnd());
			}
			else if (Operator.hasValue(vo.getStartdate())) {
				en.setDate(vo.getStartdate());
			}
			ObjectMapper mapper = new ObjectMapper();
			AvailabilityVO v = new AvailabilityVO();
			if (Operator.equalsIgnoreCase(vo.getType(), "MULTI")) {
				v = AvailabilityAgent.getAvailability(AvailabilityAgent.getAvailabilityVO(Operator.toInt(vo.getGroupid()), u), new HashMap<String, Integer>(), st, en, true, u);
			}
			else {
				String id = "";
				if (Operator.isNumber(vo.getId())) {
					id = vo.getId();
				}
				v = AvailabilityAgent.getAvailability(vo.getType(), vo.getTypeid(), vo.getAppttypeid(), vo.getApptsubtypeid(), id, st, en, u);
			}
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String multiavailability(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			Timekeeper st = new Timekeeper();
			if (Operator.hasValue(vo.getStartdate())) {
				st.setDate(vo.getStartdate());
			}
			Timekeeper en = new Timekeeper();
			if (Operator.hasValue(vo.getEnddate())) {
				en.setDate(vo.getEnddate());
			}
			else if (vo.getEnd() > 0) {
				en = st.copy();
				en.addDay(vo.getEnd());
			}
			else if (Operator.hasValue(vo.getStartdate())) {
				en.setDate(vo.getStartdate());
			}
			ObjectMapper mapper = new ObjectMapper();
			AvailabilityVO v = new AvailabilityVO();
			int availabilityid = Operator.toInt(vo.getId());
			if (availabilityid > 0) {
				v = AvailabilityAgent.getAvailability(availabilityid, new HashMap<String, Integer>(), st, en, u);
			}
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String notes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			int id = Operator.toInt(vo.getId());
			SubObjVO[] v = Choices.getChoices(NotesSQL.getNotes("appointment", id));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String collaborators(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();

			String type = vo.getType();
			int typeid = vo.getTypeid();
			int id = Operator.toInt(vo.getId());
			SubObjVO[] v = Choices.getChoices(AppointmentSQL.getCollaborators(type, typeid, id));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String reviewCollaborators(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();

			String type = vo.getType();
			int typeid = vo.getTypeid();
			int id = Operator.toInt(vo.getId());
			SubObjVO[] v = Choices.getChoices(AppointmentSQL.getReviewCollaborators(type, typeid, id));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String team(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String type = vo.getType();
			int typeid = vo.getTypeid();
			int id = Operator.toInt(vo.getId());
			int apptid = 0;
			int appttypeid = vo.getAppttypeid();
			int apptstypeid = vo.getApptsubtypeid();
			if (appttypeid < 1 && apptstypeid < 1 && id > 0) {
				String command = AppointmentSQL.getApptAppointment(id);
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					apptid = db.getInt("ID");
					appttypeid = db.getInt("LKUP_APPOINTMENT_TYPE_ID");
					apptstypeid = db.getInt("REVIEW_ID");
				}
				db.clear();
			}
			SubObjVO[] v = Choices.getChoices(AppointmentSQL.getTeam(type, typeid, apptid, appttypeid, apptstypeid));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String reroute(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		String ids = vo.getId();
		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		try {
			b = AppointmentAgent.reroute(vo.getReference(), Operator.toInt(vo.getId()), u.getId(), "", true);
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}



}















