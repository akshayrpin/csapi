package csapi.impl.lockbox;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.general.DBBatch;
import csapi.impl.log.LogAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.Excel;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class LockboxImpl {

	public static String exception(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			r = LockboxAgent.lockboxdataExceptions(r);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String search(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			r = LockboxAgent.lockboxdataSearch(r, vo.getRef());
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static String edit(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			r.setInfo(LockboxAgent.getlockboxedit(Operator.toInt(vo.getId())));
			r = LockboxAgent.lockboxdataExceptions(r);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
	
	public static ResponseVO upload(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		try {
			
			String batchref = vo.getRef();
			String f = vo.getReference();
			Logger.info(f +"file");
			LogAgent.updateLog(vo.getProcessid(), "Reading file : "+batchref, "Reading file: "+batchref);
			LogAgent.updateLog(vo.getProcessid(),5);
			if (!Operator.hasValue(f)) {
				r.addError("File not found");
				r.setMessagecode("cs500");
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
				return r;
			}
		/*	if(u.getId()<=1){
				LogAgent.updateLog(vo.getProcessid(),101, "User session timeout kindnly relogin to process the file: ", "User session timeout kindnly relogin to process the file: ");
				
				r.setPercentcomplete(101);
				LogAgent.saveLog(r);
			}*/
			StringBuilder sb = new StringBuilder();
			sb.append(Config.getString("files.storage_path")).append(f);
			String file = sb.toString();
			Logger.info(file +"file full path");
			ArrayList<HashMap<String, String>> al = Excel.read(file);
			Logger.info(al.size()+" TOTAL SIZEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
			Logger.info(vo.getProcessid()+"PROCESSSING ID "+u.getId());
			LogAgent.updateLog(vo.getProcessid(), 20, "Reading file complete: "+batchref, "Reading file complete. if file contains duplicates it won't process further: "+batchref);
			boolean s = false ;
			try{
				s = new DBBatch().insertLockUpload(al,batchref, vo.getProcessid(), u.getId(),f);
			}catch(Exception e){
				
				LogAgent.updateLog(vo.getProcessid(),101, "The file is already processed/contains duplicates already loaded: "+e.getMessage(), "The file is already processed/contains duplicates already loaded "+e.getMessage());
				s = false;
				r.setPercentcomplete(101);
				LogAgent.saveLog(r);

				
			}
			
			
			Logger.info("FILE PROCESSED "+ s);
			if(s){
				LogAgent.updateLog(vo.getProcessid(),25, "Validating Accounts: "+batchref, "Validating Accounts "+batchref);
				
				boolean validateqty  = LockboxAgent.validateQty(al, batchref, vo.getProcessid());
				
				if(validateqty){
					LogAgent.updateLog(vo.getProcessid(),40, "Validate Account Complete: "+batchref, "Validate Account Complete"+batchref);
					r = LockboxAgent.loadlockbox(vo, u);
					
				}else {
					LogAgent.updateLog(vo.getProcessid(),100,"Validate Account Failed:: "+batchref, "Validate Account Failed"+batchref);
					r.setMessagecode("cs500");
					//LogAgent.saveLog(r);
				}
			}else {
				r.setMessagecode("cs500");
				ArrayList<String> messages = new ArrayList<String>();
				messages.add(" IMPROPER FILE ");
				r.setMessages(messages);
				LogAgent.updateLog(vo.getProcessid(),100);
			}
			
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			LogAgent.updateLog(vo.getProcessid(),100);
			r.setPercentcomplete(100);
		}
		//r.setPercentcomplete(100);
		return r;
	}

	public static ResponseVO update(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		try {
			
			boolean s = LockboxAgent.updateLockbox(vo,u);
			
			if(s){
				r = LockboxAgent.loadlockbox(vo, u);
			}else {
				r.setMessagecode("cs500");
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);
		}
		r.setPercentcomplete(100);
		return r;
	}
	
	public static ResponseVO add(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		try {
			
			boolean s = LockboxAgent.addLockbox(vo,u);
			
			if(s){
				r = LockboxAgent.loadlockbox(vo, u);
			}else {
				r.setMessagecode("cs500");
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
			r.setMessagecode("cs500");
			r.setPercentcomplete(100);
			LogAgent.saveLog(r);
		}
		r.setPercentcomplete(100);
		return r;
	}
	
	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			boolean d =LockboxAgent.deleteLockbox(vo,u); 
			if(d){
				r.setMessagecode("cs200");
				
			}else {
				r.setMessagecode("cs500");
			}
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}

	public static String manualprocess(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);	
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			boolean d =LockboxAgent.manualprocess(vo,u); 
			if(d){
				r.setMessagecode("cs200");
				
			}else {
				r.setMessagecode("cs500");
			}
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e);
		}
		return s;
	}
}





