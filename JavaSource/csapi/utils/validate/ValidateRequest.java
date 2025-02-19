package csapi.utils.validate;
import java.util.ArrayList;
import java.util.HashMap;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.CsReflect;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.Group;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ValidateRequest {

	
	public static ResponseVO process(ObjGroupVO values, String type) {
		ResponseVO r = new ResponseVO();
		try {
			ObjGroupVO fg = Group.fields(type);
			ObjVO[] f = fg.getObj();
			r = process(f, values.objValues());
		}
		catch(Exception e){
			Logger.error("Error while process request obj "+e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
	}
	
	
	public static ResponseVO processGeneral(RequestVO vo){
		ResponseVO r = new ResponseVO();
		try{
			ObjGroupVO vg = vo.getData()[0];
			ObjGroupVO fields = new ObjGroupVO();
			if(Operator.isNumber(vo.getGroup()) && Operator.toInt(vo.getGroup())>0){
				//g = CsReflect.getDetails(group, grouptype, "", 0,Operator.toInt(group));
			}
			else {
				fields = Group.fields(vo.getType(), vo.getTypeid(), vo.getGrouptype(), 0);
				//g = CsReflect.getFields(vo.getGroup(), vo.getGrouptype(), "", 0);
			}
			ObjVO[] o = fields.getObj();
			r = process(o, vg.objValues());
		}
		catch(Exception e){
			Logger.error("Error while process request obj "+e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
		
	}
	
	
	public static ResponseVO processCustom(int groupid, HashMap<String, String> values){
		ResponseVO r = new ResponseVO();
		try {
			ObjGroupVO g = Fields.custom(groupid);
			ObjVO[] o = g.getObj();
			r = process(o, values);
		}
		catch(Exception e){
			Logger.error("Error while process request obj "+e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
		
	}
	
	public static ResponseVO process(ObjVO[] o, HashMap<String, String> values) {
		ResponseVO r = new ResponseVO();
		try {
			int l = o.length;
			ArrayList<MessageVO> al = new ArrayList<MessageVO>();
			for (int i = 0; i < l; i++) {
				ObjVO vo = o[i];
			
				MessageVO v = FieldValidate.validate(vo, values.get(vo.getField()));
				if(v.getId()>0){
					al.add(v);
				}
				
			}
			if(al.size()>0) {
				r.setMessagecode("cs412");
				r.setErrors(al);
			}
			al = null;
		}
		catch(Exception e) {
			Logger.error("Error while process request obj "+e.getMessage());
			try {
				r.setMessagecode("cs500");
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return r;
		
	}
}
