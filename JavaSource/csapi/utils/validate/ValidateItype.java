package csapi.utils.validate;

import java.util.ArrayList;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csshared.vo.MessageVO;
import csshared.vo.ObjVO;

public class ValidateItype {

	
	public static MessageVO integer(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			
			if(!Operator.hasValue(value)){
				value ="0";
			}
			if(!Operator.isNumber(value)) {
				vo = getError(2,orig.getLabel().concat(" is not a number"),orig.getField());
			}
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
	}
	
	public static MessageVO Float(ObjVO orig, String value) {
		return currency(orig, value);
	}
	
	
	public static MessageVO currency(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			if(!Operator.hasValue(value)){
				value ="0";
			}
			Double.parseDouble(value);
		}
		catch(Exception e){
			vo = getError(2,orig.getLabel().concat(" is not a double "),orig.getField());
		}
		return vo;
	}

	
	public static MessageVO String(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			
			
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
	}
	
	public static MessageVO DATE(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			
			
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
	}
	
	public static MessageVO text(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			
			
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
	}
	
	public static MessageVO hidden(ObjVO orig, String value) {
		MessageVO vo = new MessageVO();
		try {
			
			
		}
		catch(Exception e){
			vo = getError(10,e.getMessage(),"");
		}
		return vo;
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

	
	public static ArrayList<MessageVO> getErrors(int code,String message,String field) {
		ArrayList<MessageVO> r = new ArrayList<MessageVO>();
		try {
			MessageVO v = getError(code, message, field);
			if (v != null) {
				r.add(v);
			}
		}
		catch(Exception e) {
			Logger.error("Error while validating request obj"+e.getMessage());
			
		}
		return r;
	}
	
	

}
