package csapi.utils.validate;

import java.lang.reflect.Method;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.Group;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;

public class FieldValidate {

	
	public static MessageVO validate(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			String field = orig.getField();
			String label = orig.getLabel();
			boolean required = Operator.s2b(orig.getRequired());
			boolean editable = Operator.s2b(orig.getEditable());
		
			if(required && !Operator.hasValue(value) && editable){
				vo = getError(1,label.concat(" is required "),field);
			}
			else {
				vo = validateTo(orig, value);
			}
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
	}

	
	public static MessageVO validateTo(ObjVO orig, String value) {
		MessageVO result = new MessageVO();
		String classname = "csapi.utils.validate.ValidateItype";
		try {
			Class<?> _class = Class.forName(classname);
			Method _method = _class.getDeclaredMethod(orig.getItype(), ObjVO.class, String.class);
			result = (MessageVO) _method.invoke(null, new Object[]{orig, value});
		}
		catch (Exception e) { }
		return result;
	}
	
	public static MessageVO getError(int code,String message,String field) {
		MessageVO r = new MessageVO();
		try {
			r.setId(code);
			r.setMessage(message);
			r.setField(field);
		}
		catch(Exception e) {
			Logger.error("Error while validating request obj"+e.getMessage());
		}
		return r;
	}

	
	

}
