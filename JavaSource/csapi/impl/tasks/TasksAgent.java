package csapi.impl.tasks;

import alain.core.db.Sage;
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
}












