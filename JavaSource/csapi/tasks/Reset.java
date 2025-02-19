package csapi.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import csapi.impl.admin.AdminMap;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.print.PrintAgent;
import csapi.impl.print.PrintImpl;
import csshared.vo.RequestVO;
import csshared.vo.TaskVO;
import alain.core.db.Sage;
import alain.core.email.ExchangeMessenger;
import alain.core.utils.Cartographer;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class Reset {

	
	
	public TaskVO TASK = new TaskVO();
	
	public static String immediate ="Y";
	public static String reset ="Y";
	
	public void task(TaskVO options) {
		TASK = options;
	}
	
	public TaskVO getTask() {
		return TASK;
	}
	public static String path =Config.fullcontexturl()+"/jsp/tasks/reset.jsp";

	
	public static MapSet getOptions(Cartographer map){
		MapSet m = AdminMap.getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("RESET_TASK_ID", map.getString("RESET_TASK_ID"));
		m.add("DATE_SELECT", map.getString("DATE_SELECT"));
	

		m.add("STATUS_ID", map.getString("STATUS_ID"));
		
	
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ID FROM TASKS_RESET  WHERE ACTIVE='Y' AND CREATED_BY = ").append(m.getString("CREATED_BY")).append(" AND  STATUS_ID = '").append(Operator.sqlEscape(m.getString("STATUS_ID"))).append("' AND DATE_SELECT = '").append(m.getString("DATE_SELECT")).append("' ORDER BY ID DESC ");
		m.add("lastinsert", sb.toString());
	
		return m;
	}  
	
	
	
	public TaskVO run() {
		Logger.info(TASK.getId()+"#################################################+Notify run");
		getSettings();
		boolean r = doTask(TASK);
		TASK.setResult(Operator.b2s(r));
		TASK.setTaskdetails(new HashMap<String, String>());
		Logger.info(TASK.getResult()+"#################################################+Finish run");
		return TASK;
	}
	
	
	public TaskVO getSettings(){
		Sage db = new Sage();
		String command = "select * from TASKS_RESET where ID ="+TASK.getTaskid();
		db.query(command);
		HashMap<String,String> taskdetails = new HashMap<String,String>();
		if(db.next()){
			taskdetails.put("ID", db.getString("ID"));	
			taskdetails.put("RESET_TASK_ID", db.getString("RESET_TASK_ID"));	
			taskdetails.put("DATE_SELECT", db.getString("DATE_SELECT"));	
			taskdetails.put("STATUS_ID", db.getString("STATUS_ID"));	
			TASK.setTaskdetails(taskdetails);
		}
		
		db.clear();
		
		return TASK;
	}
	
	
	public static boolean doTask(TaskVO t){
		boolean r = false;
		try{
			 int resetTask= Operator.toInt(t.getTaskdetails().get("RESET_TASK_ID"));
			Sage db = new Sage();
			db.query("select DISTINCT * from TASKS_ACT_RESULTS WHERE ACTIVITY_ID= "+t.getTypeid()+" AND REF_ACT_TYPE_TASKS_ID ="+resetTask+" AND RESULT='Y' ");
			if(db.next()){
				r = true;
			}
			
			
			
			if(r){
			
			 String statusId = t.getTaskdetails().get("STATUS_ID");
			 String date = t.getTaskdetails().get("DATE_SELECT");
			 
			 StringBuilder sb = new StringBuilder();
			 
			 if(Operator.hasValue(statusId)){
				 sb.append(" select DISTINCT * from activity where LKUP_ACT_STATUS_ID in (").append(statusId).append(" ) AND ID = ").append(t.getTypeid());
				 
				 db.query(sb.toString());
				 
				 if(db.next()){
					 String command = "UPDATE TASKS_ACT_RESULTS SET RESULT='R' WHERE ACTIVITY_ID= "+t.getTypeid()+"  AND REF_ACT_TYPE_TASKS_ID ="+resetTask+"  ";
					 db.update(command);
				 }
			 }
			 
			 if(Operator.hasValue(date)){
				 sb.append(" SELECT  TOP 2 *   from ACTIVITY_HISTORY AH WHERE ID = ").append(t.getTypeid()).append(" ORDER BY LOG_DATE DESC ");
				 
				 db.query(sb.toString());
				 
				 String dt1 = "";
				 String dt2 = "";
				 int i = 0;
				 while(db.next()){
					 if(i==0){
						 dt1 = db.getString(date);
						 i = i+1;
					 }else {
						 dt2 = db.getString(date);
					 }
				 }
				 boolean datechange = Operator.equalsIgnoreCase(dt1, dt2);
				 Logger.info(dt1+"----->"+dt2);
				 if(!datechange){
					 String command = "UPDATE TASKS_ACT_RESULTS SET RESULT='R' WHERE ACTIVITY_ID= "+t.getTypeid()+"  AND REF_ACT_TYPE_TASKS_ID ="+resetTask+"  ";
					 db.update(command);
				 }
			 }
			 
			 
			 
			}
			db.clear();
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		return r;
	}
	
	
	
	
	
	
	
	
	
}
