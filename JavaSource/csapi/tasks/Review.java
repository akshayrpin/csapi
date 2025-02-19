package csapi.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.admin.AdminMap;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.print.PrintAgent;
import csapi.impl.print.PrintImpl;
import csapi.tasks.pdox.PDoxAPI;
import csshared.vo.RequestVO;
import csshared.vo.TaskVO;
import alain.core.db.Sage;
import alain.core.email.ExchangeMessenger;
import alain.core.utils.Cartographer;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class Review {

	
	
	public TaskVO TASK = new TaskVO();
	
	public static String immediate ="Y";
	public static String reset ="N";
	
	public void task(TaskVO options) {
		TASK = options;
	}
	
	public TaskVO getTask() {
		return TASK;
	}
	
	
	public static String path =Config.fullcontexturl()+"/jsp/tasks/review.jsp";

	
	public static MapSet getOptions(Cartographer map){
		MapSet m = AdminMap.getCommon(map);
		m.add("ID", map.getInt("ID"));
		m.add("REVIEW_GROUP_ID", map.getString("REVIEW_GROUP_ID"));
		
		m.add("LKUP_REVIEW_STATUS_ID", map.getString("LKUP_REVIEW_STATUS_ID"));
		
		m.add("STATUS_CHANGE_ID", map.getInt("STATUS_CHANGE_ID"));
		
		
	
		m.add("TEMPLATE_ID", map.getString("TEMPLATE_ID"));
		
		m.add("EMAIL_ADDRESS", map.getString("EMAIL_ADDRESS"));
		m.add("EMAIL_SUBJECT", map.getString("EMAIL_SUBJECT"));
		m.add("EMAIL_BODY", map.getString("EMAIL_BODY"));
		
		
		
		
		String o = map.getString("EMAIL_PEOPLE_MANAGER","N");
		if(Operator.equalsIgnoreCase(o, "on") || Operator.hasValue(map.getString("EMAIL_PEOPLE_TYPE"))){	o = "Y";}
		
		m.add("EMAIL_PEOPLE_MANAGER", o);
		
		o = map.getString("EMAIL_TEAM_MANAGER","N");
		if(Operator.equalsIgnoreCase(o, "on") || Operator.hasValue(map.getString("EMAIL_TEAM_TYPE"))){	o = "Y";}
		m.add("EMAIL_TEAM_MANAGER", o);
		
		o = map.getString("TEMPLATE_ATTACHMENT","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		m.add("TEMPLATE_ATTACHMENT", o);
		
		m.add("EMAIL_PEOPLE_TYPE", map.getString("EMAIL_PEOPLE_TYPE"));
		m.add("EMAIL_TEAM_TYPE", map.getString("EMAIL_TEAM_TYPE"));
		
		String notinStatus = map.getString("NOT_IN_STATUS_ID");
		if(Operator.hasValue(notinStatus)){
			if(notinStatus.startsWith("|")){
				notinStatus = Operator.subString(notinStatus, 1, notinStatus.length());
			}
		}
		m.add("NOT_IN_STATUS_ID", notinStatus);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TOP 1 ID FROM TASKS_REVIEW  WHERE ACTIVE='Y' AND CREATED_BY = ").append(m.getString("CREATED_BY")).append(" AND  ( TEMPLATE_ID= ").append(m.getString("TEMPLATE_ID")).append(" OR REVIEW_GROUP_ID = '").append(m.getString("REVIEW_GROUP_ID")).append("' ) ORDER BY ID DESC ");
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
		String command = "select * from TASKS_REVIEW where ID ="+TASK.getTaskid();
		db.query(command);
		HashMap<String,String> taskdetails = new HashMap<String,String>();
		if(db.next()){
			taskdetails.put("ID", db.getString("ID"));	
			
			taskdetails.put("REVIEW_GROUP_ID", db.getString("REVIEW_GROUP_ID"));
			taskdetails.put("LKUP_REVIEW_STATUS_ID", db.getString("LKUP_REVIEW_STATUS_ID"));
			taskdetails.put("STATUS_CHANGE_ID", db.getString("STATUS_CHANGE_ID"));	
			
			taskdetails.put("TEMPLATE_ID", db.getString("TEMPLATE_ID"));	
			taskdetails.put("EMAIL_ADDRESS", db.getString("EMAIL_ADDRESS"));	
			taskdetails.put("EMAIL_PEOPLE_MANAGER", db.getString("EMAIL_PEOPLE_MANAGER"));	
			taskdetails.put("EMAIL_TEAM_MANAGER", db.getString("EMAIL_TEAM_MANAGER"));	
			taskdetails.put("EMAIL_PEOPLE_TYPE", db.getString("EMAIL_PEOPLE_TYPE"));	
			taskdetails.put("EMAIL_TEAM_TYPE", db.getString("EMAIL_TEAM_TYPE"));	
			
			taskdetails.put("EMAIL_SUBJECT", db.getString("EMAIL_SUBJECT"));	
			taskdetails.put("EMAIL_BODY", db.getString("EMAIL_BODY"));	
			taskdetails.put("TEMPLATE_ATTACHMENT", db.getString("TEMPLATE_ATTACHMENT"));	
			taskdetails.put("NOT_IN_STATUS_ID", db.getString("NOT_IN_STATUS_ID"));	
			
			
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
				r = doNotify(t, processids);
				
			}else {
				r = true;
			}
			
			if(r){
				//t.setRepeat(true);
				new DBBatch().updateTaskResults(t, processids);
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		return r;
	}
	
	
	public static ArrayList<HashMap<String,String>> getListtoProcess(TaskVO t){
		String command = "";
		ArrayList<HashMap<String,String>> ids = new ArrayList<HashMap<String,String>>();
		
		String lkupreviewstatusids = t.taskdetails.get("LKUP_REVIEW_STATUS_ID");
		
		String reviewgroupId = t.taskdetails.get("REVIEW_GROUP_ID");
		
		lkupreviewstatusids = Operator.replace(lkupreviewstatusids, "|", ",");
		Sage db = new Sage();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS ( ");
		sb.append(" select ACTIVITY_ID, MAX(RR.COMBOREVIEW_ID) as COMBOREVIEW_ID from REF_ACT_COMBOREVIEW RAC  ");
		sb.append(" JOIN REF_COMBOREVIEW_REVIEW RR ON RAC.COMBOREVIEW_ID= RR.COMBOREVIEW_ID ");
		sb.append(" WHERE ACTIVITY_ID="+t.getTypeid()+" group by ACTIVITY_ID ");
		sb.append(" ) ");
		sb.append(" ,M AS ( ");
		sb.append(" select Q.ACTIVITY_ID,RR.COMBOREVIEW_ID,REVIEW_ID,DUE_DATE,RA.ID AS ACTION_ID, ");
		sb.append(" CASE WHEN LKUP_REVIEW_STATUS_ID IN ("+lkupreviewstatusids+") THEN 'Y' ELSE 'N' END AS CYCLE_COMPLETE from REF_COMBOREVIEW_REVIEW RR  ");
		sb.append(" JOIN Q on RR.COMBOREVIEW_ID= Q.COMBOREVIEW_ID ");
		sb.append(" JOIN  REF_COMBOREVIEW_ACTION RA on RR.ID = RA.REF_COMBOREVIEW_REVIEW_ID AND EXPIRED='N'  ");
		sb.append("  JOIN  REVIEW R ON RR.REVIEW_ID= R.ID AND REVIEW_GROUP_ID = "+reviewgroupId+" ");
		sb.append(" ) ");
		sb.append(" select * from M ");
		sb.append(" ");
		
		command = sb.toString();
		db.query(command);
		boolean cyclecomplete = false;
		boolean records = false;
		while(db.next()){
			records = true;
			if(Operator.equalsIgnoreCase(db.getString("CYCLE_COMPLETE"), "N")){
				cyclecomplete = false;
				break;
			}
			if(Operator.equalsIgnoreCase(db.getString("CYCLE_COMPLETE"), "Y")){
				cyclecomplete = true;
			}
		}
		
		
		Logger.info(records+"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"+cyclecomplete);
		if(records && cyclecomplete){
		
			command = buildQuery(t);
		
		
			if(Operator.hasValue(command)){
			
				db.query(command);
				if(db.next()){
					//if(db.getDouble("BALANCE_DUE")==0.00){
						//if(Operator.hasValue(db.getString("LIST_PEOPLE_PRIMARY"))){
							boolean r = true;
							
							if(r){
								HashMap<String,String> id = new HashMap<String,String>();
								id.put("ID", db.getString("ID"));
								
								id.put("activity_act_nbr", db.getString("ACT_NBR"));
								id.put("activity_type", db.getString("ACTIVITY_TYPE"));
								
								if(Operator.hasValue(db.getString("LIST_PEOPLE"))){
									id.put("LIST_PEOPLE", db.getString("LIST_PEOPLE"));
								}
								
								if(Operator.hasValue(db.getString("LIST_TEAM"))){
									id.put("LIST_TEAM", db.getString("LIST_TEAM"));
								}
								
								ids.add(id);
							
							}
						//}
					}
				//}
			}
		}
		db.clear();
		Logger.info(records+"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"+cyclecomplete+"99999"+ids.size());
		
		return ids;
	}
	
	public static String buildQuery(TaskVO t){
		StringBuilder sb = new StringBuilder();
		HashMap<String,String> taskdetails = t.getTaskdetails();
		sb.append(" select  A.ID,A.ACT_NBR,LAT.TYPE as ACTIVITY_TYPE  ");
		
		//people
		String peoplemanager = taskdetails.get("EMAIL_PEOPLE_MANAGER");
		String peopletype = taskdetails.get("EMAIL_PEOPLE_TYPE");
		String teammanager = taskdetails.get("EMAIL_TEAM_MANAGER");
		String teamtype = taskdetails.get("EMAIL_TEAM_TYPE");
		
		Logger.info(peoplemanager+":::::0000000000000000000000");
		Logger.info(teammanager+":::::0000000000000000000000");
		
		sb.append(" , ");
		sb.append("         (      ");
		sb.append("         SELECT DISTINCT ");
		sb.append("         U.ID as user_id, ");
		sb.append("         FIRST_NAME + ' ' + LAST_NAME   as user_name , ");
		sb.append("         U.USERNAME   as user_username , ");
		sb.append("         U.EMAIL as user_email, ");
		sb.append("         U.FIRST_NAME as user_fname, ");
		sb.append("         U.LAST_NAME as user_lname ");
		
		sb.append("      FROM ACTIVITY APP ");
		sb.append("      LEFT OUTER join REF_ACT_USERS RAU on APP.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' AND RAU.PRIMARY_CONTACT='Y' ");
		sb.append("      LEFT OUTER JOIN   REF_PROJECT_USERS RAUP on APP.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'   AND RAUP.PRIMARY_CONTACT='Y'   ");
		sb.append("      join REF_USERS RU on (RAU.REF_USERS_ID=RU.ID  OR      RAUP.REF_USERS_ID=RU.ID   )  ");
		sb.append("      left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID  ");
		sb.append("      JOIN USERS AS U ON RU.USERS_ID = U.ID   AND U.NOTIFY='Y'      AND U.ACTIVE= 'Y' ");
		sb.append("      WHERE APP.ID = A.ID ");
		
		
		sb.append("                       ");
		sb.append("                         FOR JSON PATH, INCLUDE_NULL_VALUES  )as 'LIST_PEOPLE_PRIMARY' ");
		
		
		
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
		
		sb.append(" JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID =LAT.ID  ");
		sb.append(" JOIN REF_ACT_STATEMENT RAS on A.ID = RAS.ACTIVITY_ID ");
		sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID= SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
		sb.append(" LEFT OUTER JOIN TASKS_ACT_RESULTS TAR on A.ID = TAR.ACTIVITY_ID  AND TAR.REF_ACT_TYPE_TASKS_ID=").append(t.getId()).append("  ");
		sb.append(" WHERE LKUP_ACT_TYPE_ID=").append(t.getLkupid());
		
		
		
		
		
		
		
		
	
		sb.append(" AND A.UPDATED_DATE  BETWEEN CAST(getDate() -").append(365).append(" as date)  AND CAST(getDate() +1").append(1).append(" as date)  ");
	
			
			
		//if(Operator.equalsIgnoreCase(t.getType(), "activity") && t.getTypeid()>0){
			sb.append(" AND A.ID = ").append(t.getTypeid());
		//}
	
		String notinstatusId = taskdetails.get("NOT_IN_STATUS_ID");
		if(Operator.hasValue(notinstatusId)){
			sb.append(" AND ");
			sb.append(" LKUP_ACT_STATUS_ID not in ( ").append(Operator.replace(notinstatusId, "|", ",")).append(" ) ");
			
			//single =true;
		}
		
		
		sb.append(" AND (TAR.ID is null or TAR.RESULT = 'R')   ");
		
		sb.append(" group by A.ID , A.LKUP_ACT_TYPE_ID,A.ACT_NBR,LAT.TYPE");
		return sb.toString();
	}
	
	
	public static boolean doNotify(TaskVO t, ArrayList<HashMap<String,String>> processids){
		boolean done = false;
		
		try{
			boolean attachment = Operator.equalsIgnoreCase(t.getTaskdetails().get("TEMPLATE_ATTACHMENT"), "Y");
			
			String templateId = t.getTaskdetails().get("TEMPLATE_ID");
				
			for(HashMap<String,String> p :processids){
				String type = "activity";
				int typeid = Operator.toInt(p.get("ID"));
				
				
				if(Operator.hasValue(t.getTaskdetails().get("STATUS_CHANGE_ID"))){
					updateStatus(typeid, t);
				}
		
				String fileattachment = "";
				String emailbody = t.getTaskdetails().get("EMAIL_BODY");
				String emailSubject = t.getTaskdetails().get("EMAIL_SUBJECT");
			
				if(attachment){
					fileattachment = getFileAttachment(templateId, type, typeid);
				}else {
					emailbody = processEmailBody(templateId, type, typeid);
				}
				
				
				if(Operator.hasValue(emailSubject)){
					emailSubject = Operator.replace(emailSubject, "{activity_act_nbr}", p.get("activity_act_nbr"));
					emailSubject = Operator.replace(emailSubject, "{activity_type}", p.get("activity_type"));
				}
				
				String iurl = "https://cs/cs/?entity=lso&type=activity&_typeid="+typeid;
				String eurl = "https://cs.beverlyhills.org/cs/?entity=lso&type=activity&_typeid="+typeid;
				
				
				
				
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
							
							
							emailbody = processEmailBody(emailbody,email,name);
							
							
							
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
							emailbody = processEmailBody(emailbody,email,name);
							
							
							
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
								if(Operator.hasValue(emailbody)){
									emailbody = Operator.replace(emailbody, "{internal_cs_link}", iurl);
									emailbody = Operator.replace(emailbody, "{external_cs_link}", eurl);
								}
							}
							emailbody = processEmailBody(emailbody,ea,ea);
							
							
							
							done= doEmail(-1, "activity", typeid, ea, emailSubject, emailbody, fileattachment);
						}
					}
				}
					
				done = true;

				if(done && Operator.hasValue(t.getTaskdetails().get("STATUS_CHANGE_ID"))){
					//updateStatus(typeid, t);
				}
				
				
			}
			
			
			
			
			}catch (Exception e){
				e.printStackTrace();
				Logger.error(e.getMessage());
				
			}
		return done;
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
				m.setAttachments(fileattachment);
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
			
			filename = Config.getString("files.templates_path")+"email/pdf/"+template.get("NAME")+"_"+k.getString("DT")+".pdf";
			
			
			FileOutputStream fos = new FileOutputStream (new File(filename)); 
			
			fos.write(b);
			
			fos.close();
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return filename;
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
				emailbody = Operator.replace(emailbody, "user_name", name);
			}
			if(Operator.hasValue(email)){
				emailbody = Operator.replace(emailbody, "{user_email}", email);
				emailbody = Operator.replace(emailbody, "user_email", email);
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return emailbody;
	}
	
	public static void main(String args[]){
		//String f = FileUtil.getUrlContent("http://localhost:8080/csapi/dd.html");
		//System.out.println(f);
		//boolean result = doEmail(822, "r@g.com", 24234, "svijay@beverlyhills.org", "ell", f, "");
		//System.out.println(result);
		
		double input1 =101;
		double input2 =2;
		double input3 =659;
		double input4 =783;
		double input5 =909;
		
		double o =0;
		System.out.println((Math.ceil(input2/2)));
		if(input1 >0 && input1<=100){
			o = 517  ; 
		}else if(input1 >100 && input1<=200){
			o = input3  ; 
		}else if(input1 >200 && input1<=300){
			o = input4; 
		}else if(input1 >300){
			o = input5 ; 
		}
		if(input2>2){
			o = o * Math.ceil(input2);
		}
		
		System.out.println(o);
	}
	
	
	public static boolean updateStatus1(int actid,TaskVO t){
		boolean result = false;
		
		Sage db = new Sage();
		String command ="";
		
		command = "UPDATE ACTIVITY SET UPDATED_DATE = CURRENT_TIMESTAMP,LKUP_ACT_STATUS_ID="+t.getTaskdetails().get("STATUS_CHANGE_ID")+" WHERE    ID = "+actid;
		result = db.update(command);
		
		command = ActivitySQL.insertStatus(actid, Operator.toInt(t.getTaskdetails().get("STATUS_CHANGE_ID")), 890);
		if (db.update(command)) {
			result = true;
		}
		
		db.clear();
		
		if(result){
			ActivityAgent.addHistory(actid, "activity", actid, "update");
		}
		
		return result;
	}
	
	
	public static boolean updateStatus(int actid,TaskVO t){
		boolean result = false;
		Timekeeper k = new Timekeeper();
		int statusId = Operator.toInt(t.getTaskdetails().get("STATUS_CHANGE_ID"));
		
		if(statusId>0){
		
			Sage db = new Sage();
			String command ="";
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(" UPDATE ACTIVITY SET UPDATED_DATE = CURRENT_TIMESTAMP,LKUP_ACT_STATUS_ID=").append(statusId);
			
			if(statusId==6 || statusId==535){
				
				sb.append(" ,ISSUED_DATE = '").append(k.getString("YYYY-MM-DD")).append("' ");
			}
			
			if(statusId==4){
				
				sb.append(" ,FINAL_DATE = '").append(k.getString("YYYY-MM-DD")).append("' ");
			}
			
			
			
			sb.append(" WHERE    ID = ").append(actid);
			
			command = sb.toString();
			
			
			result = db.update(command);
			
			command = ActivitySQL.insertStatus(actid, Operator.toInt(t.getTaskdetails().get("STATUS_CHANGE_ID")), 890);
			if (db.update(command)) {
				result = true;
			}
			
			db.clear();
		
			if(result){
				ActivityAgent.addHistory(actid, "activity", actid, "update");
			}
		}
		
		
		return result;
	}
	
	
}
