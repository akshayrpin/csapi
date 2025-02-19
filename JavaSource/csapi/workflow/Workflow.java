package csapi.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.security.AuthorizeToken;
import csshared.vo.MessageVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


/**
 * @author svijay
 */
public class Workflow {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	
	
	public static ResponseVO prerun(RequestVO vo){
		ResponseVO o = new ResponseVO();
		o = CommonWorkFlow.prerunCommon(vo);
		Token u = AuthorizeToken.authenticate(vo);
		
		if(o.isValid()){ 
		
			LinkedHashMap<String, WorkflowVO> lm = WorkflowAgent.getWorkflows(vo);
			for (Map.Entry<String,WorkflowVO> entry : lm.entrySet()) {
			    // entry.getValue() is of type User now
				Logger.info("RUN WORKFLOW -------------->");
				Logger.info(entry.getKey());
				Logger.info(entry.getValue().getWorkflowclass());
				Logger.info(entry.getValue().getWorkflowmethod());
			}
		
			
		/*	MessageVO m = new MessageVO();
			m.setMessage(" Workflow Cannot create the requested process as there is an active on the account.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			o.setErrors(mv);
			o.setMessagecode("cs412");
			o.setValid(false);*/
		}
		
		return o;
	}
	
	public static boolean run(RequestVO qvo,ResponseVO rvo){
		boolean result = true;
		
		
		return result;
	}

	
	
	public static boolean runWorkflow(WorkflowVO vo) {
		boolean result = false;
		StringBuilder sb =  new StringBuilder();
		sb.append("csapi.workflow.process.").append(Operator.toTitleCase(vo.getWorkflowclass())).append(".").append(vo.getWorkflowmethod()).append("Fields");
		String classname = sb.toString();
		try {
			Logger.info("REFLECT METHOD", classname + ".FIELD_ID_REF");
			Class<?> _class = Class.forName(classname);
			result = (Boolean) _class.getField("FIELD_ID_REF").get(null);
		}
		catch (Exception e) { 
			sb = new StringBuilder();
			//result = sb.toString();
		}
		return result;
	}
}
