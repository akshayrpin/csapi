package csapi.impl.activity;

import java.util.ArrayList;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.impl.admin.AdminAgent;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.log.LogAgent;
import csshared.vo.DataVO;
import csshared.vo.DivisionsList;
import csshared.vo.HoldsList;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ActivityValidate {

	
	/*public static boolean pre(RequestVO vo, String processid, int projectid, int actid, Token u){
		boolean r = true;
		DataVO m = DataVO.toDataVO(vo);

		ObjGroupVO g = ActivityFields.details();
		ObjVO[] va = g.getObj();
		for (int i=0; i<va.length; i++) {
			ObjVO o = va[i];
			if (o.isRequired()) {
				String f = o.getFieldid();
				String l = o.getLabel();
				String val = m.getString(f);
				if (!Operator.hasValue(val)) {
					LogAgent.logError(processid, "412", "Error", l+" is a required field");
					r = false;
				}
			}
		}

		return r;
	}*/
	
	public static boolean pre(RequestVO vo, Token u){
		boolean resp = true;
		ResponseVO r = new ResponseVO();
		r.setProcessid(vo.getProcessid());
		DataVO m = DataVO.toDataVO(vo);
		resp = validateRequired(vo.getProcessid(), m, u);

		r = checkDivisions(vo.getProcessid(), vo.getTypeid(), m.getInt("LKUP_ACT_TYPE_ID"), r);
		if(r.isValid()){
			r = checkHolds(vo.getProcessid(), vo.getTypeid(), r,vo,0);
			if(r.isValid()){
				r = projectStatus(vo.getProcessid(), vo.getTypeid(), r);
				if(r.isValid()){
// TODO: FIX
// DISABLED BY ALAIN - MAX DURATION NOT WORKING CORRECTLY
//					r = max(vo, u, r, v);
				}
			}
		}
		if(!r.isValid()){
			resp = false;
		}
		
		return resp;
	}
	
	public static boolean validateRequired(String processid, DataVO m, Token u){
		boolean resp = true;
		try {
			ObjGroupVO g = ActivityFields.details();
			ObjVO[] va = g.getObj();
			for (int i=0; i<va.length; i++) {
				ObjVO o = va[i];
				if (o.isRequired()) {
					String f = o.getFieldid();
					String l = o.getLabel();
					String val = m.getString(f);
					if (!Operator.hasValue(val)) {
						LogAgent.logError(processid, "412", "Error", l+" is a required field");
						Logger.highlight(l+" is a required field");
						resp = false;
					}
				}
			}
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return resp;
	}
	
	public static boolean checks(String processid, int typeid, int lkupacttypeid, Token u,RequestVO vo){
		return checks(processid, typeid, lkupacttypeid, u, vo,false);
	}	
	
	public static boolean checks(String processid, int typeid, int lkupacttypeid, Token u,RequestVO vo,boolean lockboxignore){
		boolean resp = true;
		ResponseVO r = LogAgent.getLog(processid);
		
		if(!lockboxignore){
			r = checkDivisions(processid, typeid, lkupacttypeid, r); 
		}
		if(r.isValid()){
			r = checkHolds(processid, typeid, r,vo,lkupacttypeid);
			Logger.info("HOLDS CHECK -->"+r.isValid());
			if(r.isValid()){
				r = projectStatus(processid, typeid, r);
				Logger.info("projectStatus CHECK -->"+r.isValid());
				

				if(r.isValid()){
// TODO: FIX
// DISABLED BY ALAIN - MAX DURATION NOT WORKING CORRECTLY 
					//if(!lockboxignore){ 
						r = max(vo, u, r,lkupacttypeid);
						
						Logger.info("max CHECK -->"+r.isValid());
					//}

				}
			}
		}
		if(!r.isValid()){
			resp = false;
		}
		
		return resp;
	}
	
	public static ResponseVO checkDivisions(String processid, int typeid, int lkupacttypeid, ResponseVO r) {
		r.setValid(true);
		if (typeid < 0) { return r; }
		
		boolean checkdivisions = ActivityAgent.checkActivityDivisions(typeid, lkupacttypeid);
		Logger.info("checkdivisions------->"+ checkdivisions);
		if (!checkdivisions) {
			MessageVO m = new MessageVO();
			m.setMessage("This activity type can not be added to this division.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			LogAgent.logError(processid, "cs412", "Error", "This activity is not allowed in this division");
			Logger.highlight("This activity is not allowed in this division");
		}
		return r;
	}
	
	public static ResponseVO checkHolds(String processid, int typeid, ResponseVO r,RequestVO vo, int lkupacttypeid) {
		// holds
		HoldsList h = HoldsAgent.getHolds(vo.getType(), typeid, lkupacttypeid);
		//Logger.info(vo.getType()+"-" +typeid+"="+ lkupacttypeid+ "SUNIL HOLDSSSSSSSS CHECK ******"+ h.actOnSignificantHold(lkupacttypeid));
		// holds
		if (h.actOnSignificantHold(lkupacttypeid)) {
			MessageVO m = new MessageVO();
			m.setMessage("There is a hold on this type");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			LogAgent.logError(processid, "cs412", "Error", "There is a hold on this type");
		}
		return r;
	}
	
	
	
	public static ResponseVO projectStatus(String processid, int typeid, ResponseVO r) {
		boolean alert = ActivityAgent.checkProjectStatus(typeid);
	
		// alert
		if (!alert) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to create a new activity as the current project status is expired. ");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			LogAgent.logError(processid, "cs412", "Error", "Project status is not valid");
			Logger.highlight("Project status is not valid");
		}
		return r;
	}
	
	
	public static ResponseVO max(RequestVO vo, Token u, ResponseVO r, int lkupacttypeid) {
		
		Logger.info(vo.getStartdate());
		Logger.info(vo.getEnddate());
		MapSet v = AdminAgent.getType(lkupacttypeid, "LKUP_ACT_TYPE");
		boolean alert = true;
		int count = ActivityAgent.checkMax(vo,vo.getTypeid(), v);
		
		Logger.info(count+"DURATION_MAX::::::::::::::::::::::::::"+v.getInt("DURATION_MAX"));
		if(v.getInt("DURATION_MAX")>0){
			if(count>=v.getInt("DURATION_MAX")){
				  alert = false;
			}
		}
		Logger.info("ALL checks before  ::::::::::::::::::::::::::"+alert);
		//Daytime exemption harcoded to be replaced by DOT config
		if(lkupacttypeid==253){
			DivisionsList l = DivisionsAgent.getDivisions("project", vo.getTypeid());
			String divids = l.divisionIds();
			Logger.info("ALL div ids::::::::::::::::::::::::::"+divids);
			if(!divids.contains("323")){
				v = AdminAgent.getType(252, "LKUP_ACT_TYPE");
				int pcount =  ActivityAgent.checkMax(vo,vo.getTypeid(), v);
				Logger.info("ALL pcount::::::::::::::::::::::::::"+pcount);
				v = AdminAgent.getType(280, "LKUP_ACT_TYPE");
				pcount +=  ActivityAgent.checkMax(vo,vo.getTypeid(), v);
				Logger.info("ALL transfer pcount::::::::::::::::::::::::::"+pcount);
				
				
				if(pcount<3){
					if(ActivityAgent.checkMaxDaytimePeryear(vo.getTypeid())>=5){
						alert = false;
					}
				}
				
			}
		}
		
		Logger.info("ALL checks done  ::::::::::::::::::::::::::"+alert);
		// max
		if (!alert) {
			MessageVO m = new MessageVO();
			m.setMessage("You will not be able to request as the max allowed is already obtained.");
			m.setField("LKUP_ACT_TYPE_ID");
			ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
			mv.add(m);
			r.setErrors(mv);
			r.setMessagecode("cs412");
			LogAgent.logError(vo.getProcessid(), "cs412", "Error", "Maximum Exceeded");
			Logger.highlight("Maximum Exceeded");
		}
		return r;
	}
	

}
