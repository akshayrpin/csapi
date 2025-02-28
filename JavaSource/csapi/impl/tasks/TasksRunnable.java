package csapi.impl.tasks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.finance.AutoPenalty;
import csapi.impl.log.LogAgent;
import csapi.security.AuthorizeToken;
import csapi.tasks.process.Task;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class TasksRunnable {

	public static void threadRun() {
		//final boolean runimmediate = immediate;
		//final RequestVO request = vo;
		//final Token token = u;
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						Task.runTask();
					}
					catch (Exception e) {
						Logger.error(e.getMessage());
					}
		        }
		    }).start();
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
	}
	
	
	
	public static String run() {
		String s = "Y";
		/*ResponseVO r = LogAgent.createResponse("RUN TASKS");
		RequestVO vo = ObjMapper.toRequestObj(json);
		vo.setProcessid(r.getProcessid());
		Token u = AuthorizeToken.authenticate(vo);*/
		Logger.info("came here");
		threadRun();
		
		try {
			//ObjectMapper mapper = new ObjectMapper();
			//s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}
	
	public static String runAutoPenalty(){
		String s = "Y";
		try {
			new AutoPenalty().automatePenalty();
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}

	
	
	public static void threadRunImmediate(RequestVO vo, Token u) {
		
		final RequestVO request = vo;
		final Token token = u;
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						Task.runTaskImmediate(vo,u);
					}
					catch (Exception e) {
						Logger.error(e.getMessage());
					}
		        }
		    }).start();
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
	}
	
	public static String runImmediate(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = LogAgent.createResponse("RUN IMMEDIATE");
		RequestVO vo = ObjMapper.toRequestObj(json);
		vo.setProcessid(r.getProcessid());
		Token u = AuthorizeToken.authenticate(vo);
	
		
		
		
		
		if(vo.getReference().equalsIgnoreCase("payment") && vo.getRefid()>0){
			u.setLoggedin(true);
			u.setId(vo.getRefid());
		}
		
		Logger.info(u.isLoggedin()+"uuuuuuuuuuuuuuuuuuuuuu"+u.getId());
		Logger.info(vo.getTypeid()+"uuuuuuuuuuuuuuuuuuuuuu"+u.getIp());
		
		if(u.isLoggedin()){
			threadRunImmediate(vo, u);
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}

	public static String emailExpiredPermits(String json) {
		int days;
		String template;
		try {
			JSONObject jsonObject = new JSONObject(json);
			days = jsonObject.getInt("days") > 0 ? jsonObject.getInt("days") : 0;
			template = jsonObject.getString("template");
		} catch (Exception e) {
			days = 0;
			template = "";
		}
		return Operator.b2s(TasksAgent.emailExpiredPermits(days, template));
	}

	public static String updateActivity() {
		return Operator.b2s(TasksAgent.updateActivity());
	}
}





