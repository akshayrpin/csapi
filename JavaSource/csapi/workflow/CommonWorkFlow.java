package csapi.workflow;

import java.util.ArrayList;

import alain.core.utils.Operator;
import csapi.impl.general.GeneralAgent;
import csshared.vo.MessageVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class CommonWorkFlow {

	public static ResponseVO prerunCommon(RequestVO vo){
		ResponseVO r = new ResponseVO();
		r.setValid(true);
		
		//holds
		String hold = GeneralAgent.getAlert(vo.getType(), vo.getTypeid());
		if(Operator.hasValue(hold) && !Operator.equalsIgnoreCase(hold.trim(), "HOLD_W")){
			MessageVO m = new MessageVO();
			m.setMessage(" Cannot create the requested process as there is an hold on the account.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			r.setValid(false);
			
		}
		
		if(r.isValid()){
			
			
			
		}
		
		return r;
	}
	
	public static boolean postrunCommon(RequestVO vo){
		boolean result = true;
		
		
		return result;
	}
	
	
	

}
