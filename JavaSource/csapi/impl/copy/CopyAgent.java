package csapi.impl.copy;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.finance.FinanceAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsReflect;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;



public class CopyAgent {

	public static ResponseVO copy(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		String type = vo.getType();
		if (Operator.equalsIgnoreCase(type, "activity")) {
			int typeid = vo.getTypeid();
			Token u = AuthorizeToken.authenticate(vo);
			DataVO d = DataVO.toDataVO(vo);
			String module = d.getString("LKUP_MODULES_ID");
			String projnbr = d.getString("PROJECT_NBR");
			String applieddate = d.getString("APPLIED_DATE");
			String startdate = d.getString("START_DATE");
			String issueddate = d.getString("ISSUED_DATE");
			String appexpdate = d.getString("APPLICATION_EXP_DATE");
			String expdate = d.getString("EXP_DATE");
			String finaldate = d.getString("FINAL_DATE");
			String feedate = d.getString("PERMIT_FEE_DATE");
			int status = d.getInt("LKUP_ACT_STATUS_ID");
			String[] modules = Operator.split(module, "|");
			r = ActivityAgent.copy(vo.getProcessid(), typeid, projnbr, status, applieddate, startdate, issueddate, appexpdate, expdate, finaldate, feedate, u);
			int newid = r.getId();
			if (newid > 0) {
				boolean finance = false;
				for (int i=0; i<modules.length; i++) {
					String m = modules[i];
					if (Operator.equalsIgnoreCase(m, "finance")) {
						FinanceAgent.copy(type, typeid, newid, feedate,u.getId(), u.getIp());
						finance=true;
					}
					else {
						boolean c = CsReflect.copy(m, type, typeid, newid, u.getId(), u.getIp());
						if (!c) {
							r.addMessage("Unable to copy "+m);
						}
					}
				}
				if(!finance){
					FinanceAgent.autosave("lso", "activity", newid, feedate, u.getId());
				}
				TypeVO t = new TypeVO();
				t.setType("activity");
				t.setTypeid(newid);
				t.setId(newid);
				t.setEntity(vo.getEntity());
				t.setEntityid(vo.getEntityid());
				r.setType(t);
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Unable to save new activity.");
			}
		}
		else {
			r.setMessagecode("cs501");
			r.addError("Copy of "+type+" has not been implemented in this version");
		}
		return r;
	}


}















