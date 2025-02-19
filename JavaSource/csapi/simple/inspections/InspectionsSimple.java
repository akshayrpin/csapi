package csapi.simple.inspections;

import alain.core.security.Token;
import alain.core.utils.Timekeeper;
import csapi.impl.availability.AvailabilityAgent;
import csapi.impl.inspections.InspectionsAgent;
import csapi.impl.review.ReviewAgent;
import csapi.utils.objtools.Types;
import csshared.vo.AvailabilityVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;

public class InspectionsSimple {

	/**
	 * @param reviewrefid - ID from the REF_COMBOREVIEW_REVIEW_ID table
	 * @param comment - reason why inspection is being canceled
	 * @param userid - userid of cancellation requester
	 * @param ip - ip address of cancellation requester
	 * @return true if inspection was cancelled successfully
	 */
	public static boolean cancelInspection(int reviewrefid, String comment, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		r = InspectionsAgent.cancelInspection(r, reviewrefid, comment, userid, ip);
		return r.isValid();
	}

	/**
	 * @param type - module type ("entity", "project", "activity"...)
	 * @param typeid - id of the module type
	 * @param inspectiontype - id of the review that allow the use of a review status marked "SCHEDULE_INSPECTION"
	 * @param comment - description or comment about the request
	 * @param date - date of the inspection
	 * @param time - a pipe delimited id value of the inspection time. You must use the time id that is provided from the getAvailability() methods. This pipe delimited value contains an array of the ids found in AVAILABILITY_DEFAULT and AVAILABILITY_CUSTOM
	 * @param title - used as the title of a comboreview if one does not already exist
	 * @param userid - userid of the requester
	 * @param ip - ip address of the requester
	 * @return true if inspection was successfully scheduled
	 */
	public static boolean scheduleInspection(String type, int typeid, int inspectiontype, String comment, String collaborators, String inspectors, String date, String time, String title, int userid, String ip, Token u) {
		ResponseVO r = new ResponseVO();
		r = ReviewAgent.scheduleInspection(type, typeid, inspectiontype, comment, collaborators, inspectors, date, time, title, userid, ip, u);
		return r.isValid();
	}

	/**
	 * @param userid - id of user who is involved in the activities meant to be retrieved
	 * @return ObjMap array containing all the activities related to the userid
	 */
	public static ObjMap[] getInspectableActivities(int userid) {
		ObjGroupVO g = InspectionsAgent.inspectableActivities(userid);
		return g.getValues();
	}

	/**
	 * @param username - username of user who is involved in the activities meant to be retrieved
	 * @return ObjMap array containing all the activities related to the username
	 */
	public static ObjMap[] getInspectableActivities(String username) {
		ObjGroupVO g = InspectionsAgent.inspectableActivities(username);
		return g.getValues();
	}

	/**
	 * @param email - email address of user who is involved in the activities meant to be retrieved
	 * @return ObjMap array containing all the activities related to the user whose email is specified
	 */
	public static ObjMap[] getInspectableActivitiesByEmail(String email) {
		ObjGroupVO g = InspectionsAgent.inspectableActivitiesByEmail(email);
		return g.getValues();
	}

	/**
	 * @param phone - phone number of user who is involved in the activities meant to be retrieved
	 * @return ObjMap array containing all the activities related to the user whose phone number is specified
	 */
	public static ObjMap[] getInspectableActivitiesByPhone(String phone) {
		ObjGroupVO g = InspectionsAgent.inspectableActivitiesByPhone(phone);
		return g.getValues();
	}

	/**
	 * @param type - module type ("entity", "project", "activity"...)
	 * @param typeid - id of the module type
	 * @return ObjMap array containing records from the REVIEW table which allow the use of a review status marked "SCHEDULE_INSPECTION"
	 */
	public static ObjMap[] getInspectionTypes(String type, int typeid) {
		ObjGroupVO g = InspectionsAgent.types(type, typeid, "");
		return g.getValues();
	}

	/**
	 * @param actid - activity id
	 * @return ObjMap array containing records from the REVIEW table which allow the use of a review status marked "SCHEDULE_INSPECTION"
	 */
	public static ObjMap[] getInspectionTypes(int actid) {
		ObjGroupVO g = InspectionsAgent.types("activity", actid, "");
		return g.getValues();
	}

	/**
	 * @param actnbr - activity/permit number
	 * @return ObjMap array containing records from the REVIEW table which allow the use of a review status marked "SCHEDULE_INSPECTION"
	 */
	public static ObjMap[] getInspectionTypes(String actnbr) {
		int actid = InspectionsAgent.getActivityId(actnbr);
		ObjGroupVO g = InspectionsAgent.types("activity", actid, "");
		return g.getValues();
	}

	
	/**
	 * @param type - module type ("entity", "project", "activity"...)
	 * @param typeid - id of the module type
	 * @param inspectiontype - id of the review that allow the use of a review status marked "SCHEDULE_INSPECTION"
	 * @param start - Timekeeper object containing the requested start date of availability options
	 * @param end - Timekeeper object containing the requested end date of availability options
	 * @return AvailabilityVO object containing dates and times of available slots that can be scheduled
	 */
	public static AvailabilityVO getAvailability(String type, int typeid, int inspectiontype, Timekeeper start, Timekeeper end) {
		return getAvailability(type, typeid, inspectiontype, start, end, new Token());
	}
	public static AvailabilityVO getAvailability(String type, int typeid, int inspectiontype, Timekeeper start, Timekeeper end, Token u) {
		return AvailabilityAgent.getAvailability(type, typeid, -1, inspectiontype, "", start, end, u);
	}

	/**
	 * @param actid - activity id
	 * @param inspectiontype - id of the review that allow the use of a review status marked "SCHEDULE_INSPECTION"
	 * @param start - Timekeeper object containing the requested start date of availability options
	 * @param end - Timekeeper object containing the requested end date of availability options
	 * @return AvailabilityVO object containing dates and times of available slots that can be scheduled
	 */
	public static AvailabilityVO getAvailability(int actid, int inspectiontype, Timekeeper start, Timekeeper end) {
		return getAvailability(actid, inspectiontype, start, end, new Token());
	}

	public static AvailabilityVO getAvailability(int actid, int inspectiontype, Timekeeper start, Timekeeper end, Token u) {
		return AvailabilityAgent.getAvailability("activity", actid, -1, inspectiontype, "", start, end, u);
	}

	/**
	 * @param actnbr - activity/permit number
	 * @param inspectiontype - id of the review that allow the use of a review status marked "SCHEDULE_INSPECTION"
	 * @param start - Timekeeper object containing the requested start date of availability options
	 * @param end - Timekeeper object containing the requested end date of availability options
	 * @return AvailabilityVO object containing dates and times of available slots that can be scheduled
	 */
	public static AvailabilityVO getAvailability(String actnbr, int inspectiontype, Timekeeper start, Timekeeper end) {
		return getAvailability(actnbr, inspectiontype, start, end, new Token());
	}

	public static AvailabilityVO getAvailability(String actnbr, int inspectiontype, Timekeeper start, Timekeeper end, Token u) {
		int actid = InspectionsAgent.getActivityId(actnbr);
		return AvailabilityAgent.getAvailability("activity", actid, -1, inspectiontype, "", start, end, u);
	}

	public static TypeVO getInspections(String username) {
		TypeVO t = Types.getMy("", "inspections", username);
		return t;
	}




}




























