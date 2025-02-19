package csapi.impl.finance;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.deposit.DepositAgent;
import csapi.impl.users.UsersAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;
import csshared.vo.finance.FeesGroupVO;


public class FinanceImpl {



	public static String fees(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			
			Logger.info("ACTION ________"+vo.getAction());
			if(vo.getAction().equalsIgnoreCase("unlockfee")){
			 boolean r = FinanceAgent.updateInvoiceFinanceLock(vo.getType(), vo.getTypeid(),0,false);
			}
			if(vo.getAction().equalsIgnoreCase("lockfee")){
				 boolean r = FinanceAgent.updateInvoiceFinanceLock(vo.getType(), vo.getTypeid(),0,true);
			}
			//FinanceVO v = FinanceAgent.getFees(vo);
			TypeVO v = FinanceAgent.getFees(vo);
			
			v = FinanceAgent.getInvoiceFinanceLock(v,vo.getType(), vo.getTypeid());
			
			v.setDepositcredits(DepositAgent.getDepositCredits("activity", v.getTypeid()));
			v.setTools(FinanceTools.fees(v.getEntity(), v.getEntityid(), v.getType(), v.getTypeid(), u));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	
	
	public static String feespick(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			
			FeesGroupVO v = FinanceAgent.getFeesList(vo);
			
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String calculate(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			//JSONObject o = new JSONObject(json);
			//FinanceVO o = ObjMapper.toRequestFinanceObj(json);
			//FinanceVO r = FinanceAgent.calculate(o);
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			TypeVO r = FinanceAgent.calculate(vo,u.getId());
			Logger.info(vo.getType()+"---o---"+vo.getTypeid());
			Logger.info(r.getType()+"---r---"+r.getTypeid());
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	

	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving");
		String s = "";
		
		//ResponseVO r = new ResponseVO();
		//ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			// TO DO SECURE properly 
			//JSONObject o = new JSONObject(json);

			//now new
			//FinanceVO o = ObjMapper.toRequestFinanceObj(json);
			RequestVO vo = ObjMapper.toRequestObj(json);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");
			CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "activities");
			
			//Token u = AuthorizeToken.authenticate(o.getToken(),o.getIp());	
			/*if(Operator.hasValue(u.getErrormessage())){
				r.setMessagecode(u.getErrormessage());
			}
			if(Operator.equalsIgnoreCase(u.getErrormessage(), "cs402")){
				r.setRedirect(true);
			}
			
			//Check for staff 
			if(!u.isStaff()){
				r.setMessagecode("cs400");
			}
			
			
			if(u.getId()>=0){
			//	ObjGroupVO v = vo.getData()[0];
				
				if(v.getType().equalsIgnoreCase("holds")){
					r = saveReview(vo, u);
				} else {
					r.setMessagecode("cs404");
				}
				
				
				
				JSONObject r = FinanceAgent.calculate(o);
				
				s = r.toString();
				
			}*/
			
			/*if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info(s);
			}*/
			
			/*JSONObject r = FinanceAgent.calculate(o,true);
			boolean result = FinanceAgent.saveFees(r,u);*/
			
			Token u = AuthorizeToken.authenticate(vo);
			TypeVO r = FinanceAgent.calculate(vo,u.getId(),true);
			
			/*if(r.getGroups().length>0){
				boolean result = saveFees(r,true);
			}*/
			
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
			
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}

	/*public static ResponseVO saveReview(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		try{
			if(Operator.toInt(vo.getId())>0){
			
			}else {
				r = ValidateRequest.processGeneral(vo);
			}
			if(r.getErrors().length<1){
				boolean result = HoldsAgent.save(vo,u);
				Logger.info("result#######################"+result);
				if(result){
					r.setMessagecode("cs200");
				}else {
					r.setMessagecode("cs500");
				}
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
		}
		return r;
	}*/

	
	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = FinanceAgent.browse(vo.getId(),vo.getType());
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}
	
	public static String cashierSearch(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = FinanceAgent.search(vo.getSearch().trim(), vo.getStart(), vo.getEnd());
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}

	
	public static String cart(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			TypeVO v = FinanceAgent.cart(vo, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String paymentdetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			//Token u = AuthorizeToken.authenticate(vo);	
			TypeVO v = FinanceAgent.getPaymentDetails(vo);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String pay(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");

		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			
			r = FinanceAgent.savePayment(r,vo,u);
			
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
			
			
			//if(u.getId()>=0){
			
				//r = savePayment(vo, u);
				
			//}
			
			//if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info("final return"+s);
				GlobalSearch.index(GlobalSearch.FINANCE_DELTA);
				GlobalSearch.index(GlobalSearch.LEDGER_DELTA);
				
				
				
			
				
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
				r = FinanceAgent.savePayment(r,vo,u);
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");
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
	
	
	public static String paymentlist(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("paymentlist");
		String s = "";
		
		
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = FinanceAgent.paymentlist(vo);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	
	
	
	
	
	
	
	public static String reverse(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			
			if(vo.getRef().equals("user")){
				r = FinanceAgent.refundUserDeposit(r,vo,u);
			}else {
				r = FinanceAgent.reversePayment(r,vo,u);
			}
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");

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
			
			
			//if(u.getId()>=0){
			
				//r = savePayment(vo, u);
				
			//}
			
			//if(Operator.hasValue(r.getMessagecode())){
				s = mapper.writeValueAsString(r);
				Logger.info("final return"+s);
				GlobalSearch.index(GlobalSearch.FINANCE_DELTA);
				GlobalSearch.index(GlobalSearch.LEDGER_DELTA);
				
			//}
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static String showstatementpayment(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			
			//FinanceVO v = FinanceAgent.getFees(vo);
			TypeVO v = FinanceAgent.showstatementpayment(vo);
			//v.setTools(FinanceTools.fees(v.getEntity(), v.getEntityid(), v.getType(), v.getTypeid()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String partialreverse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
			
			//FinanceVO v = FinanceAgent.getFees(vo);
			Token u = AuthorizeToken.authenticate(vo);
		
			boolean v = DepositAgent.reversePartialPayment(vo,u.getId());
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");

			//v.setTools(FinanceTools.fees(v.getEntity(), v.getEntityid(), v.getType(), v.getTypeid()));
			s = Operator.toString(v);
			GlobalSearch.index(GlobalSearch.FINANCE_DELTA);
			GlobalSearch.index(GlobalSearch.LEDGER_DELTA);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String showledger(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			
		
			FeesGroupVO v = new FeesGroupVO();
			v.setFees(FinanceAgent.showledgerpayment(vo));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static String payonline(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving online");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);	
			
			ResponseVO cart = FinanceAgent.getCart(vo, u);
			vo.setStatements(cart.getType().getStatements());
			r = FinanceAgent.savePayment(r,vo,u);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");

			
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
			
			
			//if(u.getId()>=0){
			
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
	
	public static String onlinepay(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving online");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = FinanceAgent.updateCartAfterPay(vo, u, "N", "Y");
			r = FinanceAgent.savePayment(r,vo,u);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "finance");
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	
	public static String savecart(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			
			ObjectMapper mapper = new ObjectMapper();
			Logger.info("user id ---->"+u.getId());
			if(u.getId()>0){
				ResponseVO v = FinanceAgent.saveCart(vo, u);
				s = mapper.writeValueAsString(v);
				Logger.info("ssss"+s);
			}else {
				ResponseVO v = new ResponseVO();
				v.setMessagecode("412");
				s = mapper.writeValueAsString(v);
			}
		}
		catch (Exception e) { }
		return s;
	}
	
	public static String updatecart(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			if(u.getId()>0){
				ResponseVO v = FinanceAgent.updateCart(vo, u);
				s = mapper.writeValueAsString(v);
				Logger.info("ssss"+s);
			}else {
				ResponseVO v = new ResponseVO();
				v.setMessagecode("412");
				s = mapper.writeValueAsString(v);
			}
		}
		catch (Exception e) { }
		return s;
	}
	
	public static String getcart(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			//Token t = UsersAgent.createToken("", vo.getToken(), vo.getIp());
			
			Token u = AuthorizeToken.authenticate(vo);	
			
			if(u.getId()<=0 && Operator.hasValue(u.getUsername())){
				Token t = UsersAgent.createToken("", vo.getToken(), vo.getIp());
				u = AuthorizeToken.authenticate(vo);	
			}
			
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			Logger.info(u.getId()+"userID is ----"+u.getUsername());
			
			if(u.getId()>0 || Operator.toInt(vo.getReference())>0){
				ResponseVO v = FinanceAgent.getCart(vo, u);
				s = mapper.writeValueAsString(v);
			}else {
				ResponseVO v = new ResponseVO();
				v.setMessagecode("412");
				s = mapper.writeValueAsString(v);
			}
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
	
	public static String deletecart(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			if(u.getId()>0){
				ResponseVO v = FinanceAgent.deleteCart(vo, u);
				s = mapper.writeValueAsString(v);
				Logger.info("ssss"+s);
			
			} else {
				ResponseVO v = new ResponseVO();
				v.setMessagecode("412");
				s = mapper.writeValueAsString(v);
			}
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String myPayments(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("paymentlist");
		String s = "";
		
		
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			//TODO SECURE
			ResponseVO v =  new ResponseVO();
			TypeVO t = new TypeVO();
			t.setPayment(FinanceAgent.getPayments(u.getId()));
			v.setType(t);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	
	public static String deletefee(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			ResponseVO v = FinanceAgent.deletefee(vo, u);
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}
	
	public static String updateonlinepay(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving updateonlinepay");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = FinanceAgent.updateCartAfterPay(vo, u, "Y", "N");
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	
	public static String getManualAccounts(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving updateonlinepay");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = FinanceAgent.getManualAccounts(vo, u);
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	
	public static String extractfinancerecords(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("saving extractfinancerecords");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			r = FinanceAgent.extractFinanceRecords(vo, u);
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	
	public static String statementdetail(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("get statementdetail");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			

			r = FinanceAgent.getStatementDetail(Operator.toInt(vo.getId()));
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	public static String updatestatementdetailfinance(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("get statementdetail");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			
			
			
			
			r = FinanceAgent.updateStatementDetailFinance(vo,u);
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	
	public static String currentvaluation(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("get currentvaluation");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			
			
			r = FinanceAgent.currentvaluation(vo.getTypeid());
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	public static String updatecurrentvaluation(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.info("updatecurrentvaluation ");
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);	
			
			
			if(u.getId()>0){
			
				r = FinanceAgent.updatecurrentvaluation(vo,u);
			}
			s = mapper.writeValueAsString(r);
			Logger.info("final return"+s);
			
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
	
		return s;
	}
	
	/*public static String unlockfee(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO v = FinanceAgent.updateInvoiceFinanceLock(vo.getType(),vo.getTypeid(), u.getId());
			s = mapper.writeValueAsString(v);
			Logger.info("ssss"+s);
		}
		catch (Exception e) { }
		return s;
	}*/
}