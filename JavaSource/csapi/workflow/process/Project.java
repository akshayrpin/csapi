package csapi.workflow.process;

import java.util.ArrayList;

import alain.core.utils.Operator;
import csapi.impl.general.GeneralAgent;
import csapi.impl.project.ProjectAgent;
import csshared.vo.MessageVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class Project {

	
	
	public static ResponseVO checkProjectActive(RequestVO vo){
		ResponseVO r = new ResponseVO();
		r.setValid(true);
		
		
		boolean res = checkProjectActive(vo,true);
		if(res){
			MessageVO m = new MessageVO();
			m.setMessage(" Cannot create the requested process as the account is inactive.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			r.setValid(false);
			
		}
		
		return r;
	}
	
	public static boolean checkProjectActive(RequestVO vo,boolean post){
		return ProjectAgent.getProjectStatus(vo.getTypeid());
	}
}
