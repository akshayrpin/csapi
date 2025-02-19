package csapi.impl.inspections;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.appointment.AppointmentAgent;
import csapi.impl.appointment.AppointmentFields;
import csapi.impl.appointment.AppointmentSQL;
import csapi.impl.availability.AvailabilityAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.review.ReviewAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Modules;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.AvailabilityVO;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.HoldsList;
import csshared.vo.InspectionStatisticsList;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class InspectionsImpl {

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = AppointmentAgent.processRequest(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);

			ObjGroupVO f = AppointmentFields.details();
			String command = AppointmentSQL.details(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			ObjGroupVO g = Group.vertical(f, command);
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

	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = InspectionsAgent.browse(vo.getEntity());
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}

	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getSummary(vo);
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

	public static String myActive(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getMyActive(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String inspectionDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		HashMap<String, String> r = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			r = ActivityAgent.getPermitDetails(vo.getTypeid(), vo.getId());
			ArrayList<HashMap<String, String>> a = InspectionsAgent.getActivityInspections(vo.getTypeid());
			ResponseVO rvo = new ResponseVO();
			rvo.setInfo(r);
			rvo.setList(a);
			s = mapper.writeValueAsString(rvo);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}

	public static String full(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFull(vo);
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
			String ref = vo.getReference();
			String type = vo.getType();
			int id = vo.getTypeid();
			if (Operator.equalsIgnoreCase(type, "permits") && Operator.hasValue(ref)) {
				id = InspectionsAgent.getActivityId(ref);
			}
			ObjectMapper mapper = new ObjectMapper();
			TypeVO result = new TypeVO();
			if (id > 0) {
				vo.setType("activity");
				vo.setTypeid(id);

				boolean dh = Modules.disableOnHold("review");
				HoldsList hl = HoldsAgent.getActivityHolds("activity", id);

				if (dh && hl.actOnSignificantHold()) {
					result.setMessagecode("-1");
					result.setMessage("An Inspection cannot be scheduled for this permit at this time. A hold exists for this activity.");
				}
				else {
					result = Types.getType(vo);
					ObjGroupVO g = new ObjGroupVO();
					g = InspectionsAgent.types(vo.getType(), vo.getTypeid(), "");
					
					
					ObjGroupVO[] gs = new ObjGroupVO[1];
					gs[0] = g;
					Logger.info(g.getValues().length+"***************************************************************"+g.getMessage());
					
					if(g.getValues().length > 0){
						result.setGroups(gs);
						result.setMessage(g.getMessage());
						result.setMessagecode(g.getMessagecode());
					} else {
						if (Operator.hasValue(g.getMessage())) {
							result.setGroups(gs);
							result.setMessage(g.getMessage());
							result.setMessagecode("-1");
						}else if(!InspectionsAgent.inspectionAvailable(vo.getType(), vo.getTypeid())){
							result.setGroups(gs);
							result.setMessage("You cannot request inspections for this permit online. Please refer to the conditions listed on the permit.");
							result.setMessagecode("-1");
						}
						else {
							result.setGroups(gs);
							result.setMessage("An Inspection cannot be scheduled for this permit at this time. This permit has not been issued.");
							result.setMessagecode("-1");
						}
					}
				}

			}
			else{
				result.setMessagecode("-1");
				result.setMessage("Invalid Permit Number.");
			}
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String inspectableActivities(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			int userid = u.getId();

			ObjectMapper mapper = new ObjectMapper();
			TypeVO result = new TypeVO();
			ObjGroupVO g = new ObjGroupVO();
			g = InspectionsAgent.inspectableActivities(userid);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			result.setGroups(gs);
			result.setMessage(g.getMessage());
			result.setMessagecode(g.getMessagecode());
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(result);
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
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);
			Timekeeper st = new Timekeeper();
			if (Operator.hasValue(vo.getStartdate())) {
				st.setDate(vo.getStartdate());
			}
			Timekeeper en = new Timekeeper();
			if (Operator.hasValue(vo.getEnddate())) {
				en.setDate(vo.getEnddate());
			}
			else if (Operator.hasValue(vo.getStartdate())) {
				en.setDate(vo.getStartdate());
			}

			String ref = vo.getReference();
			int actid = vo.getTypeid();
			if (Operator.hasValue(ref)) {
				actid = InspectionsAgent.getActivityId(ref);
			}

			AvailabilityVO result = new AvailabilityVO();
			if (actid < 1) {
				result.setMessagecode("cs412");
				result.setMessage("Reference not specified");
			}
			else {
				result = AvailabilityAgent.getAvailability("activity", actid, -1, vo.getApptsubtypeid(), "", st, en, u);
			}
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String month(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			ObjectMapper mapper = new ObjectMapper();

			Timekeeper st = new Timekeeper();
			if (Operator.hasValue(vo.getStartdate())) {
				st.setDate(vo.getStartdate());
				st.setDay(1);
			}
			Timekeeper en = st.copy();
			en.setDay(en.DAYS_IN_MONTH());

			AvailabilityVO result = AvailabilityAgent.getAvailability(vo.getType(), vo.getTypeid(), "", st, en, u);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String day(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			ObjectMapper mapper = new ObjectMapper();

			Timekeeper st = new Timekeeper();
			if (Operator.hasValue(vo.getStartdate())) {
				st.setDate(vo.getStartdate());
			}
			Timekeeper en = st.copy();

			AvailabilityVO result = AvailabilityAgent.getAvailability(vo.getType(), vo.getTypeid(), "", st, en, u);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			String ref = vo.getReference();
			String type = vo.getType();
			int id = vo.getTypeid();
			if (Operator.equalsIgnoreCase(type, "permits") && Operator.hasValue(ref)) {
				id = InspectionsAgent.getActivityId(ref);
			}
			if (id > 0) {
				vo.setType("activity");
				vo.setTypeid(id);
			}
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { e.printStackTrace(); }
		return s;
	}

	public static String team(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String id = vo.getId();
			SubObjVO[] v = Choices.getChoices(InspectionsSQL.team(id));
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String cancel(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		int id = Operator.toInt(vo.getId());
		ObjectMapper mapper = new ObjectMapper();
		if (id > 0) {
			DataVO m = DataVO.toDataVO(vo);
			String note = m.get("NOTES");
			r = InspectionsAgent.cancelInspection(r, id, note, u.getId(), vo.getIp());
			if (r.isValid()) {
				ReviewAgent.clearCache(id);
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Appointment schedule not specified.");
		}
		try {
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
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

		r = InspectionsAgent.schedule(u.getId(), vo.getIp(), type, typeid, m, u);
		CsDeleteCache.deleteCache(type, typeid, "appointment");
		CsDeleteCache.deleteCache(type, typeid, "reviews");

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			res = mapper.writeValueAsString(r);
		} catch(Exception e) { }
		
		
		GlobalSearch.updateGlobalSearch(GlobalSearch.INSPECTIONS_DELTA);

		return res;
	}



	public static String statistics1(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			TypeVO t = new TypeVO();
			
			ObjGroupVO[] ga = new ObjGroupVO[1];

			ObjGroupVO g = new ObjGroupVO();
			
			
			ArrayList<HashMap<String, String>> extraslist = new ArrayList<HashMap<String, String>>();
			ArrayList<MapSet> all = InspectionsAgent.getStatistics(vo.getStartdate());
			for(MapSet m: all){
				 HashMap<String, String> e = new HashMap<String, String>();
				 e.put("TYPE", m.getString("TYPE"));
				 e.put("ID", m.getString("ID"));
				 e.put("COUNT", m.getString("COUNT"));
				 e.put("DATE", m.getString("DATE"));
				 e.put("TOPIC", m.getString("TOPIC"));
	    		 e.put("CURRENT_COUNT", m.getString("CURRENT_COUNT"));
	    		 e.put("MANUAL", m.getString("MANUAL"));
	    		 e.put("ONLINE", m.getString("ONLINE"));
	    		 e.put("IVR", m.getString("IVR"));
	    		 extraslist.add(e);
	    		 Logger.info(extraslist.size());
			}
			
			g.setExtraslist(extraslist);
			
			ga[0] = g;
			t.setGroups(ga);
			r.setType(t);
			
			
			
			//mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}

	public static String statistics(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String date = vo.getStartdate();
			InspectionStatisticsList l = InspectionsAgent.statistics(date); 
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			s = mapper.writeValueAsString(l);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}





}















