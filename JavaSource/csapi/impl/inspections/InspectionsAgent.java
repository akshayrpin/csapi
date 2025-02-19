package csapi.impl.inspections;

import java.util.ArrayList;
import java.util.HashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.appointment.AppointmentAgent;
import csapi.impl.appointment.AppointmentSQL;
import csapi.impl.review.ReviewAgent;
import csapi.impl.review.ReviewSQL;
import csapi.utils.objtools.Group;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.InspectionStatisticsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.ResponseVO;

public class InspectionsAgent {

	public static BrowserVO browse(String entity) {
		BrowserVO b = new BrowserVO();
		try {

			BrowserHeaderVO h = new BrowserHeaderVO();

			h.setLabel("INSPECTIONS BROWSER");

			ArrayList<BrowserItemVO> i = new ArrayList<BrowserItemVO>();

			String command = InspectionsSQL.browse();
			Sage db = new Sage();
			db.query(command);

			BrowserItemVO avo = new BrowserItemVO();
			avo.setTitle("All Inspectors");
			avo.setChildren(0);
			avo.setEntity(entity);
			avo.setType("inspections");
			avo.setLink("inspections");
			avo.setDomain(Config.rooturl());
			i.add(avo);

			while(db.next()) {
				BrowserItemVO vo = new BrowserItemVO();
				vo.setTitle(db.getString("TITLE"));
				vo.setDescription(db.getString("DESCRIPTION"));
				vo.setId(db.getString("ID"));
				vo.setDataid(db.getString("ID"));
				vo.setChildren(0);
				vo.setEntity(entity);
				vo.setType("inspections");
				vo.setLink("inspections");
				vo.setDomain(Config.rooturl());
				i.add(vo);
			}

			db.clear();

			BrowserItemVO[] is = i.toArray(new BrowserItemVO[i.size()]);
			b.setRoot(is);

			b.setHeader(h);
		
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		return b;
	}
	
	public static ObjGroupVO types(String type, int typeid, String scheduleid) {
		String command = "";

		ObjGroupVO error = new ObjGroupVO();
		command = AppointmentSQL.getMax(type, typeid, scheduleid);
		boolean valid = true;
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			int maxactive = db.getInt("MAX_ACTIVE_APPOINTMENTS");
			int active = db.getInt("ACTIVE_APPOINTMENTS");
			Logger.highlight(maxactive+":"+active);
			if (maxactive > 0) {
				if (active >= maxactive) {
					valid = false;
					error.setMessagecode("cs412");
					error.setMessage("The maximum number of active inspections has been reached for this permit");
				}
			}
		}
		db.clear();
		if (!valid) { return error; }

		ObjGroupVO fields = InspectionsFields.type();
		command = InspectionsSQL.getInspectionTypes(type, typeid);
		ObjGroupVO r = Group.horizontal(fields, command);
		return r;
	}

	public static ObjGroupVO permit(String actnbr) {
		ObjGroupVO fields = InspectionsFields.type();
		String command = InspectionsSQL.getInspectionTypes(actnbr);
		Logger.info("Inspections Group", fields.getGroup());
		Logger.info("Horizontal Group", fields.getGroup());
		return Group.horizontal(fields, command);
	}

	public static ObjGroupVO inspectableActivities(int userid) {
		ObjGroupVO fields = InspectionsFields.type();
		String command = InspectionsSQL.getActivities(userid);
		return Group.horizontal(fields, command);
	}

	public static ObjGroupVO inspectableActivities(String username) {
		ObjGroupVO fields = InspectionsFields.type();
		String command = InspectionsSQL.getActivities(username);
		return Group.horizontal(fields, command);
	}

	public static ObjGroupVO inspectableActivitiesByEmail(String email) {
		ObjGroupVO fields = InspectionsFields.type();
		String command = InspectionsSQL.getActivitiesByEmail(email);
		return Group.horizontal(fields, command);
	}

	public static ObjGroupVO inspectableActivitiesByPhone(String phone) {
		ObjGroupVO fields = InspectionsFields.type();
		String command = InspectionsSQL.getActivitiesByPhone(phone);
		return Group.horizontal(fields, command);
	}

	public static int getActivityId(String actnbr) {
		int r = -1;
		if (Operator.hasValue(actnbr)) {
			String command = ActivitySQL.getActivity(actnbr);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
			}
			db.clear();
		}
		return r;
	}

	public static boolean isInspectable(int actid) {
		boolean r = false;
		Sage db = new Sage();
		String command = InspectionsSQL.getActivity(actid);
		if (db.query(command)) {
			r = db.size() > 0;
		}
		db.clear();
		return r;
	}

	public static boolean isInspectable(String actnbr) {
		boolean r = false;
		Sage db = new Sage();
		String command = InspectionsSQL.getActivity(actnbr);
		if (db.query(command)) {
			r = db.size() > 0;
		}
		db.clear();
		return r;
	}

	public static ResponseVO schedule(int userid, String ip, String type, int typeid, DataVO fv, Token u) {
		int reviewid = Operator.toInt(fv.get("REVIEW_ID"));
		String date = fv.get("DATE");
		String time = fv.get("TIME");
		if (reviewid > 0 && Operator.hasValue(date) && Operator.hasValue(time)) {
			String notes =  fv.get("NOTES");
			if(!Operator.hasValue(notes)){
				notes =  fv.get("SUBJECT");
			}
			return ReviewAgent.scheduleInspection(type, typeid, reviewid, notes, Operator.toString(userid), "", fv.get("DATE"), fv.get("TIME"), "", userid, ip, u);
		}
		else {
			ResponseVO r = new ResponseVO();
			r.setMessagecode("cs412");
			r.addError("Command not found");
			return r;
		}
	}

	public static boolean cancelInspection(int reviewrefid, String comment, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		r = cancelInspection(r, reviewrefid, comment, userid, ip);
		return r.isValid();
	}

	public static ResponseVO cancelInspection(ResponseVO r, int reviewrefid, String comment, int userid, String ip) {
		Sage db = new Sage();
		int previd = -1;
		boolean currinspection = false;
		boolean cancelinspection_24 = false;
		boolean sendEmail = false;
		int reviewid = -1;
		Timekeeper now = new Timekeeper();
		String command = "";
		//check 24 hours 
		command = ReviewSQL.getCurrentAction(reviewrefid);
		if (db.query(command) && db.next()) {
			int actionId = db.getInt("ID");
			
			if(actionId>0){
				command = "select CASE WHEN START_DATE < DATEADD(hour, 24, getDate())  THEN 'Y' ELSE 'N' END AS CHECK_HR from APPOINTMENT_SCHEDULE WHERE REF_COMBOREVIEW_ACTION_ID ="+actionId;
				if (db.query(command) && db.next()) {
					cancelinspection_24 = Operator.s2b(db.getString("CHECK_HR"));
				}
			}
			
		}
		if(cancelinspection_24){
			r.setMessagecode("cs500");
			r.addError("Could not cancel prior 24 hours");
		
		}else {
			sendEmail = true;
			AppointmentAgent.cancelReview(reviewrefid, -1, "", userid, ip);
			command = ReviewSQL.getCurrentAction(reviewrefid);
			if (db.query(command) && db.next()) {
				previd = db.getInt("ID");
				currinspection = db.equalsIgnoreCase("SCHEDULE_INSPECTION", "Y");
				reviewid = db.getInt("REVIEW_ID");
			}
			if (currinspection && reviewid > 0) {
				command = ReviewSQL.getInspectionCancelStatus(reviewid);
				if (db.query(command) && db.next()) {
					int cancelstatus = db.getInt("ID");
					Timekeeper before = now.copy();
					before.addSecond(-1);
	
					command = ReviewSQL.addComboAction(reviewrefid, cancelstatus, "Cancel inspection requested", previd, userid, ip, before);
					if (db.query(command)) {
	
						AppointmentAgent.cancelReview(reviewrefid, -1, "", userid, ip);
					}
				}
			}

			
		}
		db.clear();
		if(sendEmail){
			ReviewAgent.notify("", 0, userid, ip, reviewrefid);
		}
		return r;
	}

	public static InspectionStatisticsList statistics(String date) {
		Timekeeper s = new Timekeeper();
		if (Operator.hasValue(date)) {
			s.setDate(date);
		}
		Timekeeper e = s.copy();
		e.addDay(14);
		InspectionStatisticsList l = new InspectionStatisticsList();
		String command = InspectionsSQL.statistics(s, e);
		Sage db = new Sage();
		try {
			if (db.query(command)) {
				while (db.next()) {
					String dt = db.getString("START_DATE");
					String avail = db.getString("AVAILABILITY");
					int avid = db.getInt("AVAILABILITY_ID");
					int seats = db.getInt("SEATS", 0);
					int buffer = db.getInt("BUFFER", 0);
					String source = db.getString("SOURCE");
					String type = db.getString("TYPE");
					if (Operator.equalsIgnoreCase(source, "IVR")) { source = "IVR"; }
					else if (Operator.equalsIgnoreCase(source, "ONLINE")) { source = "ONLINE"; }
					else  { source = "MANUAL"; }
					int req = db.getInt("REQUESTS");
					l.add(dt, avid, avail, type, seats, buffer, source, req);
				}
			}

		} catch (Exception ex ) { }
		db.clear();
		return l;
	}




	 
	 public static ArrayList<MapSet>  getStatistics(String date) throws Exception {
		 ArrayList<MapSet> all = new ArrayList<MapSet>();	
		 Timekeeper st = new Timekeeper();
		 st.setDate(date);
		
		 
		 Timekeeper ed = new Timekeeper();
		 ed.setDate(st);
		 ed.addDay(10);
			
		 ArrayList<MapSet> custom = new ArrayList<MapSet>();
		 
		  String command = "select SUM(SEATS + BUFFER_SEATS) AS COUNT,CUSTOM_DATE,A.TITLE,A.ID from AVAILABILITY_CUSTOM AD JOIN AVAILABILITY A on AD.AVAILABILITY_ID=A.ID where CUSTOM_DATE BETWEEN '"+st.getString("YYYY-MM-DD")+"' AND '"+ed.getString("YYYY-MM-DD")+"' group by CUSTOM_DATE,A.TITLE,A.ID  ORDER BY CUSTOM_DATE ";
          Logger.info("Current count :"+command);
          
          Sage db = new Sage();
          
          db.query(command);
          while (db.next()) {
        	  MapSet m = new MapSet();
        	  m.add("DATE", db.getString("CUSTOM_DATE"));
        	  m.add("TOPIC", db.getString("TITLE"));
        	  m.add("COUNT", db.getString("COUNT"));
        	  m.add("TYPE", "CUSTOM");
        	  m.add("ID", db.getInt("ID"));
        	  custom.add(m);
          }
      	  
          ArrayList<MapSet> def = new ArrayList<MapSet>();
          command = "select SUM(SEATS + BUFFER_SEATS) AS COUNT,DAY_OF_WEEK,A.TITLE,A.ID from AVAILABILITY_DEFAULT AD JOIN AVAILABILITY A on AD.AVAILABILITY_ID=A.ID group by DAY_OF_WEEK,A.TITLE,A.ID ";
          Logger.info("Current count :"+command);
          db.query(command);
          while (db.next()) {
        	  MapSet m = new MapSet();
        	  m.add("DAY_OF_WEEK", db.getString("DAY_OF_WEEK"));
        	  m.add("TOPIC", db.getString("TITLE"));
        	  m.add("COUNT", db.getString("COUNT"));
        	  m.add("TYPE", "DEFAULT");
        	  m.add("ID", db.getInt("ID"));
        	  def.add(m);
            }
          
          ArrayList<MapSet> app = new ArrayList<MapSet>();
          command = "select DISTINCT A.ID,A.TITLE from AVAILABILITY A JOIN REVIEW R on A.ID = R.AVAILABILITY_ID ";
          Logger.info("Current count :"+command);
          db.query(command);
          while (db.next()) {
        	  MapSet m = new MapSet();
        	  m.add("TOPIC", db.getString("TITLE"));
        	  m.add("ID", db.getInt("ID"));
        	  app.add(m);
            }
          
          
          ArrayList<MapSet> curr = new ArrayList<MapSet>();
         
          StringBuilder ad = new StringBuilder();
          ad.append(" select count(*) as COUNT,DATE_START,AVAILABILITY_ID from Q   ");
  		  ad.append(" WHERE DATE_START BETWEEN '").append(st.getString("YYYY-MM-DD")).append("' AND '").append(ed.getString("YYYY-MM-DD")).append("'  ");
  		  ad.append(" GROUP BY DATE_START,Q.AVAILABILITY_ID ORDER BY DATE_START ");
          
          command = InspectionsSQL.statisticsDay(ad.toString());
          
          Logger.info("Current count :"+command);
          db.query(command);
          while (db.next()) {
        	  MapSet m = new MapSet();
        	  m.add("CURRENT_COUNT", db.getString("COUNT"));
        	  m.add("ID", db.getInt("AVAILABILITY_ID"));
        	  m.add("DATE", db.getString("DATE_START"));
        	  curr.add(m);
            }
          
          
          ArrayList<MapSet> source = new ArrayList<MapSet>();
          
          ad = new StringBuilder();
          ad.append(" select count(*) as COUNT, DATE_START,AVAILABILITY_ID,APS.SOURCE from Q    ");
          ad.append(" LEFT OUTER JOIN APPOINTMENT_SCHEDULE APS on APS.REF_COMBOREVIEW_ACTION_ID = Q.REF_COMBOREVIEW_ACTION_ID  ");
  		  ad.append(" WHERE DATE_START BETWEEN '").append(st.getString("YYYY-MM-DD")).append("' AND '").append(ed.getString("YYYY-MM-DD")).append("'  ");
  		  ad.append(" GROUP BY DATE_START,Q.AVAILABILITY_ID,APS.SOURCE  ORDER BY DATE_START ");
          
          command = InspectionsSQL.statisticsDay(ad.toString());
          
          Logger.info("Current count :"+command);
          db.query(command);
          while (db.next()) {
        	  MapSet m = new MapSet();
        	  m.add("CURRENT_COUNT", db.getString("COUNT"));
        	  m.add("ID", db.getInt("AVAILABILITY_ID"));
        	  m.add("DATE", db.getString("DATE_START"));
        	  m.add("SOURCE", db.getString("SOURCE"));
        	  source.add(m);
            }
           
          db.clear(); 
         
         
         while(st.lessThan(ed)){
        	 int d = st.DAY_OF_WEEK();
        	 String dt = st.getString("YYYY-MM-DD");
        	 
        	 //Logger.info(d+"GETTING ::"+dt);
        	 
        	 for(MapSet a: app){
        		 int id = a.getInt("ID");
        		// Logger.info(d+"GETTING ::"+dt+" AVAILA::"+id+"-"+a.getString("TOPIC"));
	        	 boolean cy = false;
	        	 for(MapSet cm: custom){
	            	 if(dt.equalsIgnoreCase(cm.getString("DATE")) && cm.getInt("ID")==id){
	            		 cy=true;
	            		 MapSet m = new MapSet();
	            		 m.add("TYPE", "CUSTOM");
	            		 m.add("ID", cm.getInt("ID"));
	            		 m.add("COUNT", cm.getInt("COUNT"));
	            		 m.add("DATE", dt);
	            		 m.add("TOPIC", a.getString("TOPIC"));
	            		 m.add("CURRENT_COUNT", currentcount(dt, id, curr));
	            		 m.add("MANUAL", currentcount(dt, id, source,"MANUAL"));
	            		 m.add("ONLINE", currentcount(dt, id, source,"ONLINE"));
	            		 m.add("IVR", currentcount(dt, id, source,"IVR"));
	            		 all.add(m);
	            		 break;
	            	 }
	             }
	        	// Logger.info(cy+"NO CUSTOM ::"+st.getString("YYYY-MM-DD"));
	        	 if(!cy){
		        	 for(MapSet dm: def){
		            	if(d==dm.getInt("DAY_OF_WEEK") && dm.getInt("ID")==id){
		            		
		            		MapSet m = new MapSet();
		            		 m.add("ID", dm.getInt("ID"));
		            		 m.add("COUNT", dm.getInt("COUNT"));
		            		 m.add("DATE", dt);
		            		 m.add("TYPE", "DEFAULT");
		            		 m.add("TOPIC", a.getString("TOPIC"));
		            		 m.add("CURRENT_COUNT", currentcount(dt, id, curr));
		            		 m.add("MANUAL", currentcount(dt, id, source,"MANUAL"));
		            		 m.add("ONLINE", currentcount(dt, id, source,"ONLINE"));
		            		 m.add("IVR", currentcount(dt, id, source,"IVR"));
		            		 all.add(m);
		            		cy=true;
		            		break;
		            	}
		             }
	        	 }
        	 }
        	 st.addDay(1);
         }
        
       
          
	     return all;
	    }
	 
	 
	 public static int currentcount(String date,int id,ArrayList<MapSet> curr){
		 int count =0;
		 for(MapSet c: curr){
    		 if(date.equalsIgnoreCase(c.getString("DATE")) && id==c.getInt("ID")){
            		count = c.getInt("CURRENT_COUNT");
            		break;
            	}
           }
		 
		 return count;
		 
	 }
	 
	 public static int currentcount(String date,int id,ArrayList<MapSet> source,String option){
		 int count =0;
		 for(MapSet c: source){
    		 if(date.equalsIgnoreCase(c.getString("DATE")) && id==c.getInt("ID") && option.equalsIgnoreCase(c.getString("SOURCE"))){
            		count = c.getInt("CURRENT_COUNT");
            		break;
            	}
           }
		 
		 return count;
		 
	 }

	 public static ArrayList<HashMap<String, String>> getActivityInspections(int actid) {
		 String command = InspectionsSQL.getActivityInspections(actid);
		 ArrayList<HashMap<String, String>> ins = new ArrayList<HashMap<String, String>>();
		 Sage db = new Sage();
		 db.query(command);
		 while (db.next()) {
			 HashMap<String, String> m = new HashMap<String, String>();
			 int id = db.getInt("ACTIVITY_ID");
			 String actnbr = db.getString("ACT_NBR");
			 String review = db.getString("REVIEW");
			 String date = db.getString("START_DATE");
			 String status = db.getString("STATUS");
			 String comment = db.getString("REVIEW_COMMENTS");
			 String inspcomments = db.getString("INSPECTION_COMMENTS");
			 String schedcomments = db.getString("SCHEDULE_COMMENTS");
			 if (Operator.hasValue(date)) {
				 Timekeeper d = new Timekeeper();
				 d.setDate(date);
				 m.put("DATE", d.getString("MM/DD/YYYY"));
				 m.put("TIME", d.getString("TIME"));
			 }
			 else {
				 m.put("DATE", "");
				 m.put("TIME", "");
			 }
			 m.put("REVIEW", review);
			 m.put("STATUS", status);
			 m.put("REVIEW_COMMENTS", comment);
			 m.put("INSPECTION_COMMENTS", inspcomments);
			 m.put("SCHEDULE_COMMENTS", schedcomments);
			 m.put("START_DATE", date);
			 ins.add(m);
		 }
		 db.clear();
		 return ins;
	 }
	 
	 public static boolean inspectionAvailable(String type,int typeid){
		 boolean result = false;
		 Sage db = new Sage();
		 String command = InspectionsSQL.inspectionAvailable(type, typeid);
		 db.query(command);
		 if(db.next()){
			 int availabilityId = db.getInt("AVAILABILITY_ID");
			 if(availabilityId>0){
				 result = true;
			 }
		 }
		 
		 db.clear();
		 
		 
		 return result;
	 }
	 
}




























