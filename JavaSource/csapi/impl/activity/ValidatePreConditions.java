package csapi.impl.activity;

import java.util.ArrayList;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.impl.admin.AdminAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.log.LogAgent;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ValidatePreConditions {

	
	public static ResponseVO validate(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		r.setProcessid(vo.getProcessid());
		DataVO m = DataVO.toDataVO(vo);
		
		
		/*String type = vo.getType();
		int typeid = vo.getTypeid();*/
		
		MapSet v = new MapSet();
		v = AdminAgent.getType(m.getInt("LKUP_ACT_TYPE_ID"),Table.LKUPACTTYPETABLE);
		//divisions
		r = checkDivisions(vo, u, r, v);
//		LogAgent.saveLog(r);
		if(r.isValid()){
			//holds
		
			r = checkHolds(vo, u, r, v);
//			LogAgent.saveLog(r);
			if(r.isValid()){
				//project status
				
				r = projectStatus(vo, u, r, v);
//				LogAgent.saveLog(r);
				if(r.isValid()){
					//max  			
					
					r = maxDuration(vo, u, r, v);
//					LogAgent.saveLog(r);
					
				}
				
			}
			
			
			
		}
		
		
		
		return r;
	}
	
	
	
	public static ResponseVO checkDivisions(RequestVO vo, Token u, ResponseVO r, MapSet v) {
		boolean checkdivisions = ActivityAgent.checkActivityDivisions(vo.getTypeid(), v.getInt("ID"));
		if (!checkdivisions) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to create this permit has the division doens't match .");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}
	
	public static ResponseVO checkHolds(RequestVO vo, Token u, ResponseVO r, MapSet v) {
		String alert1 = GeneralAgent.getAlert(vo.getType(), vo.getTypeid());
	
		boolean alert = true;
		Logger.info("HOLDS####################"+alert);
		// holds
		if (!alert) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to request as there is a hold on your account.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}
	
	
	
	public static ResponseVO projectStatus(RequestVO vo, Token u, ResponseVO r, MapSet v) {
		boolean alert = ActivityAgent.checkProjectStatus(vo.getTypeid());
		Logger.info("PROJECT STATUS CHECK ------------"+alert);
	
		// alert
		if (!alert) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to create a new activity as the current project status is expired. ");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}
		return r;
	}
	
	
	public static ResponseVO maxDuration(RequestVO vo, Token u, ResponseVO r, MapSet v) {
		/*boolean alert = ActivityAgent.checkMax(vo.getTypeid(), v);

		// max
		if (!alert) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to request as the max allowed is already obtained.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
		}*/
		return r;
	}
}
