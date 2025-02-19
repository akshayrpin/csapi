package csapi.impl.project;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.log.LogAgent;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ProjectValidate {

	
	public static boolean pre(RequestVO vo, Token u){
		boolean resp = true;
		ResponseVO r = LogAgent.getLog(vo.getProcessid());
		DataVO m = DataVO.toDataVO(vo);
		
		ObjGroupVO g = ProjectFields.details();
		ObjVO[] va = g.getObj();
		for (int i=0; i<va.length; i++) {
			ObjVO o = va[i];
			boolean required = o.isRequired();
			if (vo.getTypeid() > 0 && !o.isEditable()) {
				required = false;
			}

			if (required) {
				String f = o.getFieldid();
				String l = o.getLabel();
				String val = m.getString(f);
				if (!Operator.hasValue(val)) {
					r.setMessagecode("cs412");
					r.addError(l+" is a required field");
					LogAgent.logError(vo.getProcessid(), "cs412", "Error", l+" is a required field");
					resp = false;
				}
			}
		}
		
		if(!r.isValid()){
			resp = false;
		}
		
		
		return resp;
	}
	
	

}
