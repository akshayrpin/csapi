package csapi.impl.holds;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralSQL;
import csapi.impl.users.UsersAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class HoldsImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getDetails(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String list(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getList(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			Token u = AuthorizeToken.authenticate(vo);	
			if (HoldsAgent.save(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "holds");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "holds");
			}
			else {
				r.addError("Database Error");
			}

			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

//	public static ResponseVO saveReview(RequestVO vo, Token u){
//		ResponseVO r = new ResponseVO();
//		try{
//			if(Operator.toInt(vo.getId())>0){
//			
//			}else {
//				r = ValidateRequest.processGeneral(vo);
//			}
//			if(r.isValid()){
//				boolean result = HoldsAgent.save(vo,u);
//				if(result){
//					r.setMessagecode("cs200");
//					CsTools.deleteCache(vo.getType(), vo.getTypeid());
//				}else {
//					r.setMessagecode("cs500");
//				}
//			}
//			
//		}catch(Exception e){
//			Logger.error(e.getMessage());
//			r.setMessagecode("cs500");
//		}
//		return r;
//	}
	
	
	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ArrayList<MessageVO> msgs = new ArrayList<MessageVO>();

		if (!Operator.hasValue(vo.getType()) || vo.getTypeid() < 1 || !Operator.hasValue(vo.getId())) {
			if (!Operator.hasValue(vo.getType())) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Type is a required field.");
				msgs.add(mvo);
			}
			if (vo.getTypeid() < 1) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Type id is a required field.");
				msgs.add(mvo);
			}
			if (!Operator.hasValue(vo.getId())) {
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Id is a required field.");
				msgs.add(mvo);
			}
		}
		else {
			
			boolean b = GeneralAgent.deleteRef(vo.getType(), vo.getTypeid(), "holds", Operator.toInt(vo.getId()), u.getId());
			if (b) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "holds");
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "holds");
			}
			else {
				r.setMessagecode("cs500");
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Database Error");
				msgs.add(mvo);
			}
		}

		try {
			r.setErrors(msgs);
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}
	public static String types(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] v = Choices.getChoices(HoldsSQL.holdstypes(vo.getId(),vo.getType()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String saveuserholds(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String type=vo.getType();
			int typeId= vo.getTypeid();
			vo.setType(vo.getRef());
			vo.setTypeid(Operator.toInt(vo.getGroupid()));
			Token u = AuthorizeToken.authenticate(vo);	
			if (HoldsAgent.save(vo, u)) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(type, typeId, "holds");
				CsDeleteCache.deleteChildCache(type, typeId, "holds");
				CsDeleteCache.deleteProjectAndActivityCache(type, typeId, "people");
				CsDeleteCache.deleteProjectAndActivityCache(type, typeId, "peoplesummary");
			}
			else {
				r.addError("Database Error");
			}

			if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
}















