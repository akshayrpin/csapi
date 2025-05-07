package csapi.impl.tasks;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.communications.CommunicationsAgent;

public class TasksAgent {
	
	public static boolean emailExpiredPermits(int days, String template) {
		Sage db = new Sage();
		Sage db1 = new Sage();
		boolean result = false;
		try {
			String content = null;
			String subject = null;
			String email;
			db.query("select * from TEMPLATE where NAME = '"+ template +"' ");
			if(db.next()) {
				subject = db.getString("NAME");
				content = db.getString("TEMPLATE");
				
				db1.query(" SELECT A.ID, STUFF(( select ';' + EMAIL from REF_ACT_USERS AU JOIN REF_USERS RU ON AU.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' JOIN USERS U ON RU.USERS_ID = U.ID AND U.ACTIVE = 'Y' where A.ID = AU.ACTIVITY_ID AND AU.ACTIVE = 'Y' AND NOTIFY = 'Y' for xml path('')),1,1,'') EMAIL FROM ACTIVITY A WHERE exp_date = CONVERT(date, getDate() + "+ days +", 101) ");
				while(db1.next()) {
					int actid = db1.getInt("ID");
					email = db1.getString("EMAIL");
					
					content = formatData(content, actid);
					
					if(Operator.hasValue(email)) {
						CommunicationsAgent.email("activity", 0, email, 890, subject, content, 0, "");
						result = true;
					}
				}
			}
		} finally {
			db.clear();
			db1.clear();
		}
		return result;
	}

	public static boolean updateActivity(String template) {
		Sage db = new Sage();
		Sage db1 = new Sage();
		Sage db2 = new Sage();
		String command;
		boolean result = false;
		int status = 0;
		try {
			db.query("select TOP 1 ID from LKUP_ACT_STATUS WHERE STATUS = 'Expired' AND EXPIRED = 'Y' AND ACTIVE = 'Y'");
			if(db.next()) {
				status = db.getInt("ID");
			}
			db.clear();
			db = new Sage();
			db.query("select a.id, exp_date from activity a join lkup_act_status s on a.lkup_act_status_id = s.id where (issued !='Y' and expired !='Y') and exp_date <= getdate() and exp_date >= '2025-01-31' ");
			while(db.next()) {
				int actid = db.getInt("ID");
				command = "UPDATE ACTIVITY SET UPDATED_DATE = CURRENT_TIMESTAMP, LKUP_ACT_STATUS_ID = "+ status +" WHERE ID = "+actid;
				result = db1.update(command);
				
				command = ActivitySQL.insertStatus(actid, status, 890);
				if (db1.update(command)) {
					result = true;
				}
				db1.clear();
				
				if(result){
					ActivityAgent.addHistory(actid, "activity", actid, "update");
					
					String content = null;
					String subject = null;
					String email;
					db2.query("select * from TEMPLATE where NAME = '"+ template +"' ");
					if(db2.next()) {
						subject = db2.getString("NAME");
						content = db2.getString("TEMPLATE");
						
						content = formatData(content, actid);
					
						db1 = new Sage();
						db1.query(" SELECT A.ID, STUFF(( select ';' + EMAIL from REF_ACT_USERS AU JOIN REF_USERS RU ON AU.REF_USERS_ID = RU.ID AND RU.ACTIVE = 'Y' JOIN USERS U ON RU.USERS_ID = U.ID AND U.ACTIVE = 'Y' where A.ID = AU.ACTIVITY_ID AND AU.ACTIVE = 'Y' AND NOTIFY = 'Y' for xml path('')),1,1,'') EMAIL FROM ACTIVITY A WHERE A.ID = "+ actid);
						while(db1.next()) {
							email = db1.getString("EMAIL");
							if(Operator.hasValue(email)) {
								CommunicationsAgent.email("activity", 0, email, 890, subject, content, 0, "");
								result = true;
							}
						}
					}
				}
				
			}
		} finally {
			db.clear();
			db1.clear();
			db2.clear();
		}
		return result;
	}

	private static String formatData(String content, int actid) {
		
		Sage db = new Sage();
		db.query("select a.id, exp_date, ACT_NBR , a.DESCRIPTION , getdate() currentdate, ISSUED_DATE, EXP_DATE, act.description type, s.description status, cast(L.STR_NO as varchar) +' '+ LS.STR_NAME+' '+ LS.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as lso_address from activity a join lkup_act_type act on a.LKUP_ACT_TYPE_ID = act.ID join lkup_act_status s on a.lkup_act_status_id = s.id join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID join LSO L on R.LSO_ID=L.ID join LSO_STREET LS on L.LSO_STREET_ID=LS.ID where a.id = "+ actid);
		if(db.next()) {
			String number = db.getString("ACT_NBR");
			String status = db.getString("status");
			String date = db.getString("currentdate");
			String type = db.getString("type");
			String address = db.getString("lso_address");
			String issueddate = db.getString("issued_date");
			String expdate = db.getString("exp_date");
			String desc = db.getString("description");
			content = content.replaceAll("activity_act_nbr", number);
			content = content.replaceAll("activity_status", status);
			content = content.replaceAll("special_current_date", date);
			content = content.replaceAll("activity_type", type);
			content = content.replaceAll("lso_address", address);
			content = content.replaceAll("activity_issued_date", issueddate);
			content = content.replaceAll("activity_exp_date", expdate);
			content = content.replaceAll("activity_description", desc);
			content = content.replaceAll("\\{", "");
			content = content.replaceAll("}", "");
		}
		db.clear();
		return content;
	}
}












