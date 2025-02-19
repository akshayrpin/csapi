package csapi.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.email.ExchangeMessenger;
import alain.core.utils.Cartographer;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Numeral;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.admin.AdminMap;
import csapi.impl.attachments.AttachmentsSQL;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.general.GeneralSQL;
import csapi.impl.print.PrintAgent;
import csapi.impl.print.PrintImpl;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;
import csshared.vo.TaskVO;

public class SelfAttach {

	
	
	public TaskVO TASK = new TaskVO();
	
	public static String immediate ="Y";
	public static String reset ="N";
	
	public void task(TaskVO options) {
		TASK = options;
	}
	
	public TaskVO getTask() {
		return TASK;
	}
	public static String path =Config.fullcontexturl()+"/jsp/tasks/selfattach.jsp";

	
	public static MapSet getOptions(Cartographer map){
		MapSet m = AdminMap.getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("TEMPLATE_ID", map.getString("TEMPLATE_ID"));
		m.add("STATUS_ID", map.getString("STATUS_ID"));
		
		m.add("LKUP_ATTACHMENT_TYPE_ID", map.getString("LKUP_ATTACHMENT_TYPE_ID"));
		
		
		m.add("EMAIL_ADDRESS", map.getString("EMAIL_ADDRESS"));
		m.add("EMAIL_SUBJECT", map.getString("EMAIL_SUBJECT"));
		m.add("EMAIL_BODY", map.getString("EMAIL_BODY"));
		
		String o = map.getString("EMAIL_PEOPLE_MANAGER","N");
		if(Operator.equalsIgnoreCase(o, "on") || Operator.hasValue(map.getString("EMAIL_PEOPLE_TYPE"))){	o = "Y";}
		
		m.add("EMAIL_PEOPLE_MANAGER", o);
		
		o = map.getString("EMAIL_TEAM_MANAGER","N");
		if(Operator.equalsIgnoreCase(o, "on") || Operator.hasValue(map.getString("EMAIL_TEAM_TYPE"))){	o = "Y";}
		m.add("EMAIL_TEAM_MANAGER", o);
		
		m.add("EMAIL_PEOPLE_TYPE", map.getString("EMAIL_PEOPLE_TYPE"));
		m.add("EMAIL_TEAM_TYPE", map.getString("EMAIL_TEAM_TYPE"));
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ID FROM TASKS_SELFATTACH  WHERE ACTIVE='Y' AND CREATED_BY = ").append(m.getString("CREATED_BY")).append(" AND LKUP_ATTACHMENT_TYPE_ID=  ").append(m.getString("LKUP_ATTACHMENT_TYPE_ID")).append(" AND  TEMPLATE_ID= ").append(m.getString("TEMPLATE_ID")).append(" AND STATUS_ID = '").append(m.getString("STATUS_ID")).append("' ORDER BY ID DESC ");
		m.add("lastinsert", sb.toString());
	
		return m;
	}  
	
	
	
	public TaskVO run() {
		Logger.info(TASK.getId()+"#################################################+SelfAttach run");
		getSettings();
		boolean r = doTask(TASK);
		TASK.setResult(Operator.b2s(r));
		TASK.setTaskdetails(new HashMap<String, String>());
		Logger.info(TASK.getResult()+"#################################################+Finish run");
		return TASK;
	}
	
	
	public TaskVO getSettings(){
		Sage db = new Sage();
		String command = "select * from TASKS_SELFATTACH where ID ="+TASK.getTaskid();
		db.query(command);
		HashMap<String,String> taskdetails = new HashMap<String,String>();
		if(db.next()){
			taskdetails.put("ID", db.getString("ID"));	
			taskdetails.put("TEMPLATE_ID", db.getString("TEMPLATE_ID"));	
			taskdetails.put("STATUS_ID", db.getString("STATUS_ID"));	
			taskdetails.put("LKUP_ATTACHMENT_TYPE_ID", db.getString("LKUP_ATTACHMENT_TYPE_ID"));	
			
			
			
			taskdetails.put("EMAIL_ADDRESS", db.getString("EMAIL_ADDRESS"));	
			taskdetails.put("EMAIL_PEOPLE_MANAGER", db.getString("EMAIL_PEOPLE_MANAGER"));	
			taskdetails.put("EMAIL_TEAM_MANAGER", db.getString("EMAIL_TEAM_MANAGER"));	
			taskdetails.put("EMAIL_PEOPLE_TYPE", db.getString("EMAIL_PEOPLE_TYPE"));	
			taskdetails.put("EMAIL_TEAM_TYPE", db.getString("EMAIL_TEAM_TYPE"));	
			
			taskdetails.put("EMAIL_SUBJECT", db.getString("EMAIL_SUBJECT"));	
			taskdetails.put("EMAIL_BODY", db.getString("EMAIL_BODY"));	
			
			TASK.setTaskdetails(taskdetails);
		}
		
		db.clear();
		
		return TASK;
	}
	
	
	public static boolean doTask(TaskVO t){
		boolean r = false;
		try{
			ArrayList<HashMap<String,String>> processids = getListtoProcess(t);
			if(processids.size()>0){
				r = doSelfAttach(t, processids);
				
			}else {
				r = true;
			}
			
			if(r){
				new DBBatch().updateTaskResults(t, processids);
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		return r;
	}
	
	
	public static ArrayList<HashMap<String,String>> getListtoProcess(TaskVO t){
		String command = buildQuery(t);
		
		ArrayList<HashMap<String,String>> ids = new ArrayList<HashMap<String,String>>();
		if(Operator.hasValue(command)){
			Sage db = new Sage();
			db.query(command);
			while(db.next()){
				HashMap<String,String> id = new HashMap<String,String>();
				id.put("ID", db.getString("ID"));
				
				id.put("activity_act_nbr", db.getString("ACT_NBR"));
				//id.put("activity_type", db.getString("ACTIVITY_TYPE"));
				
				if(Operator.hasValue(db.getString("LIST_PEOPLE"))){
					id.put("LIST_PEOPLE", db.getString("LIST_PEOPLE"));
				}
				
				if(Operator.hasValue(db.getString("LIST_TEAM"))){
					id.put("LIST_TEAM", db.getString("LIST_TEAM"));
				}
				
				ids.add(id);
			}
			
			
			
			db.clear();
		
		}
		return ids;
	}
	
	public static String buildQuery(TaskVO t){
		StringBuilder sb = new StringBuilder();
		HashMap<String,String> taskdetails = t.getTaskdetails();
		sb.append(" select DISTINCT A.ID, A.ACT_NBR   ");
		
		
		
		
		
		//people
				String peoplemanager = taskdetails.get("EMAIL_PEOPLE_MANAGER");
				String peopletype = taskdetails.get("EMAIL_PEOPLE_TYPE");
				String teammanager = taskdetails.get("EMAIL_TEAM_MANAGER");
				String teamtype = taskdetails.get("EMAIL_TEAM_TYPE");
				
				if(Operator.equalsIgnoreCase(peoplemanager, "Y")){
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					sb.append("         U.ID as user_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as user_name , ");
					sb.append("         U.USERNAME   as user_username , ");
					sb.append("         U.EMAIL as user_email ");
					
					
					sb.append("      FROM ACTIVITY APP ");
					sb.append("      LEFT OUTER join REF_ACT_USERS RAU on APP.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' ");
					sb.append("      LEFT OUTER JOIN   REF_PROJECT_USERS RAUP on APP.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
					sb.append("      join REF_USERS RU on (RAU.REF_USERS_ID=RU.ID  OR      RAUP.REF_USERS_ID=RU.ID   )  ");
					sb.append("      left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID  ");
					sb.append("      JOIN USERS AS U ON RU.USERS_ID = U.ID   AND U.NOTIFY='Y'      AND U.ACTIVE= 'Y' ");
					sb.append("      WHERE APP.ID = A.ID ");
					
					if(Operator.hasValue(peopletype)){
						peopletype = Operator.replace(peopletype, "|", ",");
						sb.append("                         AND LUT.ID in ( ").append(peopletype).append(" ) ");
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as 'LIST_PEOPLE' ");
				
				}
				else if(Operator.equalsIgnoreCase(teammanager, "Y")){
					sb.append(" , ");
					sb.append("         (      ");
					sb.append("         SELECT DISTINCT ");
					//sb.append("         A.ID as ACT_ID, ");
					sb.append("         U.ID as user_id, ");
					sb.append("         FIRST_NAME + ' ' + LAST_NAME   as team_name , ");
					sb.append("         U.USERNAME   as team_username , ");
					sb.append("         U.EMAIL as team_email, ");
					sb.append("        type as team_type  , ");
					sb.append("         dept as team_department       ");
					sb.append("                         FROM ACTIVITY APP ");
					
					sb.append("                         left outer join REF_ACT_TEAM RAU on APP.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y'     ");
					sb.append("                         LEFT OUTER JOIN   REF_PROJECT_TEAM RAUP on APP.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'    ");
					sb.append("                         join REF_TEAM RU on (RAU.REF_TEAM_ID=RU.ID  OR      RAUP.REF_TEAM_ID=RU.ID                     ) ");
					
					
					sb.append("                         left outer join LKUP_TEAM_TYPE as LUT on RU.LKUP_TEAM_TYPE_ID=LUT.ID     ");
					sb.append("                         JOIN USERS AS U ON RU.USERS_ID = U.ID    AND U.NOTIFY='Y'      AND U.ACTIVE= 'Y'  ");
					sb.append("                        LEFT OUTER JOIN (      STAFF AS S      LEFT OUTER JOIN DEPARTMENT AS D ON S.DEPARTMENT_ID = D.ID    ) ON U.ID = S.USERS_ID AND S.ACTIVE = 'Y' ");
					sb.append("                         WHERE APP.ID = A.ID ");
					
					if(Operator.hasValue(teamtype)){
						teamtype = Operator.replace(teamtype, "|", ",");
						sb.append("                         AND LUT.ID in ( ").append(teamtype).append(" ) ");
					}
					sb.append("                       ");
					sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as 'LIST_TEAM' ");
				}
				
				
				
				else {
					sb.append(", A.LKUP_ACT_TYPE_ID ");
				}
		
		
		
		
		sb.append(" from ACTIVITY A ");
		sb.append(" LEFT OUTER JOIN TASKS_ACT_RESULTS TAR on A.ID = TAR.ACTIVITY_ID  AND TAR.REF_ACT_TYPE_TASKS_ID=").append(t.getId()).append("  ");
		sb.append(" WHERE LKUP_ACT_TYPE_ID=").append(t.getLkupid());
		
		
		
		
		
		
		
		
		int d = Operator.toInt("-5");
		int dsub=0;
		boolean negative = false;
		if(d>=0){
			dsub = d+5;
		}else {
			negative = true;
			dsub = d+d;
		}
		String statusId = taskdetails.get("STATUS_ID");
		if(Operator.hasValue(statusId)){
		String dateselect = "A.UPDATED_DATE";
		
		if(Operator.hasValue(dateselect)){
			sb.append(" AND ");
			sb.append(dateselect).append(" ");
			if(negative){
				sb.append(" BETWEEN getDate()-5 AND getDate() ");
			}
		}
		
		//int statusId = Operator.toInt(taskdetails.get("STATUS_ID"));
	//	if(statusId>0){
			sb.append(" AND ");
			sb.append(" LKUP_ACT_STATUS_ID in ( ").append(Operator.replace(statusId, "|", ",")).append(" ) ");
			
			
			
			sb.append(" AND (TAR.ID is null or TAR.RESULT = 'R') ");
			
			
			if(Operator.equalsIgnoreCase(t.getType(), "activity") && t.getTypeid()>0){
				sb.append(" AND A.ID = ").append(t.getTypeid());
			}
		}else {
			return "";
		}
		
		
		
		
		return sb.toString();
	}
	
	
	public static boolean doSelfAttach(TaskVO t, ArrayList<HashMap<String,String>> processids){
		boolean done = false;
		Sage db = new Sage();
		try{
			boolean attachment = true;
			
			String templateId = t.getTaskdetails().get("TEMPLATE_ID");
			int lkupid = Operator.toInt(t.getTaskdetails().get("LKUP_ATTACHMENT_TYPE_ID"));
			boolean sensitive = true;
			boolean ispublic = false;
			db.query("select * from LKUP_ATTACHMENTS_TYPE WHERE ID = "+lkupid);
			if(db.next()){
				sensitive = Operator.s2b(db.getString("SENSITIVE"));
				ispublic = Operator.s2b(db.getString("ISPUBLIC"));
			}
			String fileattachment = "";
			for(HashMap<String,String> p :processids){
				String type = "activity";
				int typeid = Operator.toInt(p.get("ID"));
		
				//String fileattachment = "";
				fileattachment = getFileAttachment(templateId, type, typeid);
			
				Timekeeper now = new Timekeeper();
				
				//public static String insertAttachments(String title,int mapid,String type,String subtype, String path, int attachtypeid, String description, int size, boolean ispublic, boolean sensitive, int userid, String ip, Timekeeper now) {

				String command = AttachmentsSQL.insertAttachments("Self Attachment ",0, type ,"ATTACHMENTS",fileattachment, lkupid, t.getDescription(), 0, ispublic, sensitive, 890, "10.14.6.19", now);
				

				if (db.query(command) && db.next()) {
						int attachid = db.getInt("ID");
						command = GeneralSQL.insertRef(type, "ATTACHMENTS", typeid, attachid, 890);
						done = db.update(command);
						CsReflect.addHistory(type, typeid, "attachments", attachid, "add");
				 }
						
			
			
			
			
			//new notify
			
			String emailbody = t.getTaskdetails().get("EMAIL_BODY");
			String emailSubject = t.getTaskdetails().get("EMAIL_SUBJECT");
		
						
			if(Operator.hasValue(emailSubject)){
				emailSubject = Operator.replace(emailSubject, "{activity_act_nbr}", p.get("activity_act_nbr"));
				emailSubject = Operator.replace(emailSubject, "{activity_type}", p.get("activity_type"));
			}
			
			String people = "[]";
			
			if(p.containsKey("LIST_PEOPLE")){
				if(Operator.hasValue(p.get("LIST_PEOPLE"))){
					people = p.get("LIST_PEOPLE");
				}
			}
			String team = "[]";
			if(p.containsKey("LIST_TEAM")){
				if(Operator.hasValue(p.get("LIST_TEAM"))){
					team = p.get("LIST_TEAM");
				}
			}
		
			JSONArray l = new JSONArray(people);
			for(int i=0;i<l.length();i++){
				
				JSONObject user = l.getJSONObject(i);
				if(user.has("user_email")){
					String email = user.getString("user_email");
					int userid = user.getInt("user_id");
					/*if(!Operator.hasValue(email)){
						email = user.getString("user_username");
					}*/
					String name = "";
					if(user.has("user_name")){
						name = user.getString("user_name");
					}
					
					if(Operator.hasValue(email)){
						
						if(!attachment && Operator.hasValue(t.getTaskdetails().get("EMAIL_BODY"))){
							String extraemailcontent = processEmailBody(t.getTaskdetails().get("EMAIL_BODY"), email,name);
							if(Operator.hasValue(extraemailcontent)){
								extraemailcontent = extraemailcontent + "</br></br></br></br></br>";
							}
							emailbody = extraemailcontent + emailbody;
						}
						done= doEmail(userid, "activity", typeid, email, emailSubject, emailbody, fileattachment);
						
						
					}
				}
			}
			
			l = new JSONArray(team);
			for(int i=0;i<l.length();i++){
				JSONObject user = l.getJSONObject(i);
				if(user.has("team_email")){
				String email = user.getString("team_email");
				int userid = user.getInt("user_id");
					/*if(!Operator.hasValue(email)){
						email = user.getString("team_username");
					}*/
					String name = "";
					
					if(user.has("team_name")){
						name = user.getString("team_name");
					}
					
					if(Operator.hasValue(email)){
						
						if(!attachment && Operator.hasValue(t.getTaskdetails().get("EMAIL_BODY"))){
							String extraemailcontent = processEmailBody(t.getTaskdetails().get("EMAIL_BODY"), email,name);
							if(Operator.hasValue(extraemailcontent)){
								extraemailcontent = extraemailcontent + "</br></br></br></br></br>";
							}
							emailbody = extraemailcontent + emailbody;
						}
						done= doEmail(userid, "activity", typeid, email, emailSubject, emailbody, fileattachment);
						
						
					}
				}
			}
				
			if(Operator.hasValue(t.getTaskdetails().get("EMAIL_ADDRESS"))){
				String eal[] = Operator.split(t.getTaskdetails().get("EMAIL_ADDRESS"),",");
				for(String ea: eal){
					if(Operator.hasValue(ea)){
						if(!attachment && Operator.hasValue(t.getTaskdetails().get("EMAIL_BODY"))){
							String extraemailcontent = processEmailBody(t.getTaskdetails().get("EMAIL_BODY"), ea,ea);
							if(Operator.hasValue(extraemailcontent)){
								extraemailcontent = extraemailcontent + "</br></br></br></br></br>";
							}
							emailbody = extraemailcontent + emailbody;
						}
						done= doEmail(-1, "activity", typeid, ea, emailSubject, emailbody, fileattachment);
					}
				}
			}
				
			
			
			//end notify
			}
					
			done = true;

			
			
			
			}catch (Exception e){
				Logger.error(e.getMessage());
				
			}
		db.clear();
		return done;
	}
	
	
	
	public static String getFileAttachment(String templateId,String type,int typeid){
		String filename = "";
		try{
		
			
			RequestVO vo = new RequestVO();
			vo.setReference(templateId);
			vo.setType(type);
			vo.setTypeid(typeid);
			
			HashMap<String,String> template = PrintAgent.getTemplate(vo);
			Timekeeper k = new Timekeeper();
			
			byte[] b = new byte[0];
			b = PrintImpl.print(vo,template);
			StringBuffer sb = new StringBuffer();
			sb.append(Numeral.random()).append(Numeral.random());
			String filepath = Config.getString("files.storage_path");
			filename = "files/"+sb.toString()+"/"+template.get("NAME")+"_"+k.getString("DT")+".pdf";
			Logger.info(filename);
			new File(filepath+"files/"+sb.toString()).mkdir();

			FileOutputStream fos = new FileOutputStream (new File(filepath+filename)); 
			
			fos.write(b);
			
			fos.close();
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return filename;
	}
	
	
	
	
	public static boolean doEmail(int userId,String type,int typeid, String email,String emailSubject,String emailBody,String fileattachment){
		boolean result = false;
		try{
			
			ExchangeMessenger m = new ExchangeMessenger();
			m.setRecipient(email);
			//m.setBCC(email);
			m.setSubject(emailSubject);
			m.setContent(emailBody);
			
			if(Operator.hasValue(fileattachment)){
				m.setAttachments(Config.getString("files.storage_path")+fileattachment);
			}
			
			
			 
			
			 
			 result = m.deliver();
			 Logger.error("EMAIL SeND"+result);
			 CommunicationsAgent.save(type, typeid, email, userId, emailSubject, emailBody, 890, "","Workflow Task");
			
		}catch (Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
			
		}
		return result;
	}
	
	
	public static String processEmailBody(String templateId,String type,int typeid){
		String emailbody = "";
		try{
		
			
			RequestVO vo = new RequestVO();
			vo.setReference(templateId);
			vo.setType(type);
			vo.setTypeid(typeid);
			
			
			
			HashMap<String,String> template = PrintAgent.getTemplate(vo);
			Timekeeper k = new Timekeeper();
			
			
			emailbody = PrintImpl.printHtml(vo,template);
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return emailbody;
	}
	
	
	public static String processEmailBody(String emailbody,String email,String name){
		
		try{
		
			if(Operator.hasValue(name)){
				emailbody = Operator.replace(emailbody, "{user_name}", name);
			}
			if(Operator.hasValue(email)){
				emailbody = Operator.replace(emailbody, "{user_email}", email);
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return emailbody;
	}
	
	
	
}
