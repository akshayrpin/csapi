package csapi.tasks.process;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import csapi.impl.activity.ActivityImpl;
import csapi.impl.admin.AdminAgent;
import csapi.impl.admin.AdminMap;
import csapi.impl.finance.AutoPenalty;
import csapi.tasks.Notify;
import csapi.utils.CsApiConfig;
import csshared.vo.RequestVO;
import csshared.vo.TaskVO;
import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;

public class Task {

	
	
	public static ArrayList<MapSet> getTasks() {
		ArrayList<MapSet> list = new ArrayList<MapSet>();
		try {
			String dir = CsApiConfig.getString("tasks.path");
			File folder = new File(dir);
			File[] contenuti = folder.listFiles();
			for(File actual: contenuti){
				String wf = actual.getName();
				MapSet o = new MapSet();
				if (Operator.hasValue(wf) && wf.indexOf(".")>0) {
					wf = wf.substring(0, wf.lastIndexOf('.'));
					o.add("_TASK", wf);
				}
				wf = actual.getName();
				if (Operator.hasValue(wf) && wf.indexOf(".")>0) {
				System.out.println("ssss"+actual.getName());
				wf = wf.substring(0, wf.lastIndexOf('.'));
				Class _class = Class.forName(CsApiConfig.getString("tasks.package")+wf);
				Field f = _class.getField("path");
				o.add("_PATH",(String) f.get(null));
				list.add(o);	
				}
			}
			
		}
		catch (Exception e) { e.printStackTrace(); System.err.println(e.getMessage());}
		
		
		return list;
	}
	
	public static ArrayList<Integer> getUniqueTasks() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		String command = "select LKUP_ACT_TYPE_ID from REF_ACT_TYPE_TASKS WHERE ACTIVE='Y' GROUP BY LKUP_ACT_TYPE_ID ";
		Sage db = new Sage();
		db.query(command);
		while(db.next()){
			int id = db.getInt("LKUP_ACT_TYPE_ID");
			a.add(id);
		}
		db.clear();
		return a;
	}
	
	public static ArrayList<TaskVO> getAllTasks() {
		ArrayList<TaskVO> a = new ArrayList<TaskVO>();
		String command = "select ID,LKUP_ACT_TYPE_ID,TASK_ID,TASK,DESCRIPTION from REF_ACT_TYPE_TASKS WHERE ACTIVE='Y'    order by LKUP_ACT_TYPE_ID,ORDR ";
		Sage db = new Sage();
		db.query(command);
		while(db.next()){
			TaskVO t = new TaskVO();
			t.setDescription(db.getString("DESCRIPTION"));
			t.setId(db.getInt("ID"));
			t.setTaskid(db.getString("TASK_ID"));
			t.setTask(db.getString("TASK"));
		//	t.setReferenceid(db.getInt("LKUP_ACT_TYPE_ID"));
			t.setLkupid(db.getInt("LKUP_ACT_TYPE_ID"));
			t.setRepeat(Operator.equalsIgnoreCase(db.getString("REPEAT"), "Y"));
			a.add(t);
		}
		db.clear();
		return a;
	}
	
	
	public static ArrayList<TaskVO> getSpecificTasks(String type,String typeId) {
		ArrayList<TaskVO> a = new ArrayList<TaskVO>();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select A.ID AS ACTIVITY_ID,RAT.ID,RAT.LKUP_ACT_TYPE_ID,RAT.TASK_ID,RAT.TASK,RAT.DESCRIPTION,RAT.REPEAT ");
		sb.append(" ,CASE WHEN N.ID >0 THEN 'Y' ");
		sb.append(" WHEN TP.ID>0 THEN 'Y'  "); 
		sb.append(" WHEN TPDOXCREATOR.ID>0 THEN 'Y' ");
		sb.append(" WHEN TPDOXREL.ID>0 THEN 'Y' ");
		sb.append(" WHEN TREVIEW.ID>0 THEN 'Y' ");
		sb.append(" ELSE 'N' END AS AUTO_IMMEDIATE ");
		sb.append(" from ACTIVITY A ");
		sb.append(" JOIN  REF_ACT_TYPE_TASKS RAT on A.LKUP_ACT_TYPE_ID = RAT.LKUP_ACT_TYPE_ID ");
		sb.append(" LEFT OUTER JOIN TASKS_NOTIFY N on RAT.TASK_ID=N.ID AND  N.NO_OF_DAYS=0 AND N.NO_OF_DAYS_TO =0 AND N.DATE_SELECT = '' AND RAT.TASK='Notify' ");
		sb.append(" LEFT OUTER JOIN TASKS_PAYMENT TP on RAT.TASK_ID=TP.ID  ");
		sb.append(" LEFT OUTER JOIN TASKS_PDOX_CREATOR TPDOXCREATOR on RAT.TASK_ID=TPDOXCREATOR.ID  ");
		sb.append(" LEFT OUTER JOIN TASKS_PDOX_RELEASE_PLAN TPDOXREL on RAT.TASK_ID=TPDOXREL.ID  ");
		
		sb.append(" LEFT OUTER JOIN TASKS_REVIEW TREVIEW on RAT.TASK_ID=TREVIEW.ID  ");
		
		sb.append(" WHERE RAT.ACTIVE='Y'  ");
		sb.append(" AND ");
		sb.append(" A.ID IN ( "+typeId+") ");
		sb.append(" order by RAT.LKUP_ACT_TYPE_ID,RAT.ORDR  ");
		//String command = "select A.ID AS ACTIVITY_ID,RAT.ID,RAT.LKUP_ACT_TYPE_ID,RAT.TASK_ID,RAT.TASK,RAT.DESCRIPTION,RAT.REPEAT,CASE WHEN N.ID >0 THEN 'Y'  WHEN TP.ID>0 THEN 'Y'  ELSE 'N' END AS AUTO_IMMEDIATE from ACTIVITY A JOIN  REF_ACT_TYPE_TASKS RAT on A.LKUP_ACT_TYPE_ID = RAT.LKUP_ACT_TYPE_ID LEFT OUTER JOIN TASKS_NOTIFY N on RAT.TASK_ID=N.ID AND  N.NO_OF_DAYS=0 AND N.NO_OF_DAYS_TO =0 AND N.DATE_SELECT = '' AND RAT.TASK='Notify' LEFT OUTER JOIN TASKS_PAYMENT TP on RAT.TASK_ID=TP.ID   WHERE RAT.ACTIVE='Y' AND A.ID IN ( "+typeId+") order by RAT.LKUP_ACT_TYPE_ID,RAT.ORDR ";
		
		String command = sb.toString();
		
		Sage db = new Sage();
		db.query(command);
		while(db.next()){
			TaskVO t = new TaskVO();
			t.setDescription(db.getString("DESCRIPTION"));
			t.setId(db.getInt("ID"));
			t.setTaskid(db.getString("TASK_ID"));
			t.setTask(db.getString("TASK"));
			t.setLkupid(db.getInt("LKUP_ACT_TYPE_ID"));
			t.setTypeid(db.getInt("ACTIVITY_ID"));
			t.setType(type);
			t.setRepeat(Operator.equalsIgnoreCase(db.getString("REPEAT"), "Y"));
			t.setImmediate(Operator.equalsIgnoreCase(db.getString("AUTO_IMMEDIATE"), "Y"));
			a.add(t);
		}
		db.clear();
		return a;
	}
	
	
	
	
	public static void runTask(){
		try{
			
			ArrayList<TaskVO> l = getAllTasks();
			for(TaskVO t: l){
				String classname = t.getTask();
				t = new Task().run(t);
				Logger.info(t.getResult());
			}
			
			
			
		}catch(Exception e) { e.printStackTrace(); Logger.error(e.getMessage());}
	}
	
	
	public static void runTaskImmediate(RequestVO vo, Token u){
		try{
			
			String typeid = vo.getTypeid()+"";
			if(vo.getTypeid()<=0){
				typeid = vo.getRef();
			}
			ArrayList<TaskVO> l = getSpecificTasks(vo.getType(),typeid);
			for(TaskVO t: l){
				String classname = t.getTask();
				t = new Task().run(t,true);
				Logger.info(t.getResult());
				if(t.getTask().equals("Notify") && t.isImmediate()){
					t = new Task().run(t,false);
				}
			}
			
			
		}catch(Exception e) { e.printStackTrace(); Logger.error(e.getMessage());}
	}
	
	public TaskVO run(TaskVO vo) {
		return run(vo, false);
	}
	
	public TaskVO run(TaskVO vo,boolean immediate) {
		
		try {
			String cl = CsApiConfig.getString("tasks.package")+vo.getTask();
			Class _class = Class.forName(cl);
			Object _instance = _class.newInstance();
			String result  = "";
			try {
	            Field f = _class.getDeclaredField("immediate");
	            f.setAccessible(true);
	            if(f.isAccessible()){
	                result = (String) f.get(null);
	            }
	             
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
			Logger.info("result of immediate  "+result);
			
			if(immediate && Operator.equalsIgnoreCase(result, "Y")){
				try {
					Method _method = _class.getDeclaredMethod("task", TaskVO.class);
					_method.invoke(_instance, vo);
				}
				catch (Exception e) { e.printStackTrace();}
			
				@SuppressWarnings("unchecked")
				Method _func_to_call = _class.getMethod("run");
				Object o = _func_to_call.invoke(_instance);
				try {
					vo = (TaskVO) o;
				}
				catch (Exception e) { }
				
			}
			
			
		
			if(!immediate && Operator.equalsIgnoreCase(result, "N")){
				
			
				try {
					Method _method = _class.getDeclaredMethod("task", TaskVO.class);
					_method.invoke(_instance, vo);
				}
				catch (Exception e) { e.printStackTrace();}
	
				
				@SuppressWarnings("unchecked")
				Method _func_to_call = _class.getMethod("run");
				Object o = _func_to_call.invoke(_instance);
				try {
					vo = (TaskVO) o;
				}
				catch (Exception e) { }
				
			}
			
		}
		catch (Exception e) { e.printStackTrace();}
		if (vo != null) {
			Logger.info(vo.getResult()+"#################################################+TASK Complete");
			//updateResult(getEntries().getEntryId(), vo);
		}
		return vo;
	}
	
	
	

}
