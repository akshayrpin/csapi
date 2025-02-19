package csapi.impl.deposit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;
import csshared.vo.finance.PaymentVO;


public class DepositImpl {

	public static final String LOG_CLASS= "DepositImpl.java  : ";

	
	
	public static String depositadd(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			//UserVO u = AuthorizeToken.authroize(vo);	
			TypeVO v = DepositAgent.getPaymentDetails(vo);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String saveDeposit(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			
			r = DepositAgent.saveDeposit(r,vo,u);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "deposit");
			
		/*	if(Operator.hasValue(u.getErrormessage())){
				r.setMessagecode(u.getErrormessage());
			}
			if(Operator.equalsIgnoreCase(u.getErrormessage(), "cs402")){
				r.setRedirect(true);
			}
			
			//Check for staff 
			if(!u.isStaff()){
				r.setMessagecode("cs400");
			}*/
			
			
			//if(u.getUserId()>=0){
			
				//r = savePayment(vo, u);
				
			//}
			
			//if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info("final return"+s);
				
				
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

	public static ResponseVO savePayment(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try{
			Logger.info("came savePayment ");
			//ObjGroupVO o = vo.getData()[0];
			//r = ValidateRequest.process(o, vo.getType());
			//if(r.getErrors().length<1){
				r = DepositAgent.saveDeposit(r,vo,u);
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "deposit");
				/*Logger.info("result#######################"+result);
				if(result){
					r.setMessagecode("cs200");
				}else {
					r.setMessagecode("cs500");
				}*/
			//}
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
	}
	
	
	
	public static String depositpayees(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			//UserVO u = AuthorizeToken.authroize(vo);	
			TypeVO v = DepositAgent.getDepositPayees(vo);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String depositlist(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("depositlist");
		String s = "";
		
		
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = DepositAgent.depositlist(vo);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static String showdepositledger(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
		
			TypeVO v = new TypeVO();
			v.setDepositcredits(DepositAgent.showdepositledger(vo));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String getdepositledger(HttpServletRequest request, HttpServletResponse response, String id) {
		String s = "";
		try {
			//TODO secure
			/*RequestVO vo = new RequestVO();
			PaymentVO p = new PaymentVO();
			p.setPaymentid(id);
			vo.setPayment(p);
			ObjectMapper mapper = new ObjectMapper();
			
		
			TypeVO v = new TypeVO();
			v.setDepositcredits(DepositAgent.showdepositledger(vo));
			s = mapper.writeValueAsString(v);*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String showdepositoptions(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			//UserVO u = AuthorizeToken.authroize(vo);	
			Token u = AuthorizeToken.authenticate(vo);	
			TypeVO v = DepositAgent.getDepositOptions(vo,u);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
}















