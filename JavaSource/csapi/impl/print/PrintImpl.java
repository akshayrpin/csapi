package csapi.impl.print;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.parking.ParkingAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsApiConfig;
import csapi.utils.PrintPDF;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class PrintImpl {

	

	public static byte[] print(HttpServletRequest request, HttpServletResponse response, String json) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			HashMap<String,String> t = PrintAgent.getTemplate(vo);
			JSONObject doList = PrintAgent.listPrints(t);
			JSONArray l = PrintAgent.getPrintDetail(vo, doList);
			
			if(Operator.hasValue(t.get("FILE_DESIGN"))){
				b = new PrintPDF().designtoPdfNew(t,l.getJSONObject(0)).toByteArray();
			}else {
				b = new PrintPDF().htmltoPdf(t,l, o).toByteArray();
			}
			
		}
		catch (Exception e) { Logger.error(e.getMessage());}
		return b;
	}
	
	//duplicate method of above TODO 1 method
	public static byte[] print(RequestVO vo,HashMap<String,String> t) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			
			JSONObject doList = PrintAgent.listPrints(t);
			JSONArray l = PrintAgent.getPrintDetail(vo, doList);
			Logger.info(html);
			
			
			if(Operator.hasValue(t.get("FILE_DESIGN"))){
				b = new PrintPDF().designtoPdfNew(t,l.getJSONObject(0)).toByteArray();
			}else {
				b = new PrintPDF().htmltoPdf(t,l, o).toByteArray();
			}
			
			
			
		}
		catch (Exception e) { }
		return b;
	}
	
		
		
	
		//TODO think if appendextrahtml is required.
		public static String printHtml(RequestVO vo,HashMap<String,String> t) {
			StringBuilder sb = new StringBuilder();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			
			try {
				
				JSONObject doList = PrintAgent.listPrints(t);
				JSONArray l = PrintAgent.getPrintDetail(vo, doList);
				
				String html = t.get("TEMPLATE");	
				/*if(Operator.hasValue(appendextrahtml)){
					html = 
				}*/
				for(int i=0;i<l.length();i++){
					JSONObject  g = l.getJSONObject(i);
					
					String transformed = PrintPDF.parseHtmlSingle(html, g);
					sb.append(transformed);
					sb.append("</br>");
				 }	 
				
				
				
			}
			catch (Exception e) { }
			return sb.toString();
		}
		
		
		
	
	public static byte[] doBatch(RequestVO vo, int batchid, String processid, boolean newbatch){

		FileOutputStream fop = null;
		File file;
		byte b[] = null;
		String ids = "";
		try{
			if (newbatch) {
				vo.setGroupid("");
			}
			String projId = getId(vo);
			vo.setId(projId);
			vo.setProcessid(processid);
			LogAgent.updateLog(vo.getProcessid(), 1);
			if(!Operator.equalsIgnoreCase(vo.getGroup(), "renewal")){
				JSONObject d = GeneralAgent.getConfiguration("DOT");
				// get activity based on old batch id (groupid)
				if (newbatch) {
					ids = ActivityAgent.getActivityId(projId, vo.getType(), -1, ParkingAgent.getParkingDate(d.getString("START_DATE")), ParkingAgent.getParkingDate(d.getString("EXP_DATE")));
				}
				else {
					ids = ActivityAgent.getActivityId(projId, vo.getType(), batchid, ParkingAgent.getParkingDate(d.getString("START_DATE")), ParkingAgent.getParkingDate(d.getString("EXP_DATE")));
				}
			}
			vo.setGroupid(Operator.toString(batchid));
			if(!Operator.equalsIgnoreCase(vo.getGroup(), "renewal")){
				//update based on new batch id (groupid)
				ActivityAgent.updateActivity(ids, "N", Operator.toString(batchid));
			}
			Timekeeper now = new Timekeeper();
			b = printBatchLogic(vo, new Token());
			String filename = vo.getGroup()+"_"+now.getString("FULLDATECODE")+"_"+batchid+".pdf";
			PrintAgent.updateBatch(Operator.toString(batchid), filename);
			file = new File(Config.getString("files.storage_path") +"/"+filename);
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fop.write(b);
			fop.flush();
			fop.close();
			LogAgent.completeLog(vo.getProcessid(), "completed processing "+vo.getGroup());
			
		}
		catch(Exception e){
			Logger.error(e);
		}
		
		return b;
	}
	
	public static byte[] doBatchRenewal(RequestVO vo, int batchid, String processid){

		FileOutputStream fop = null;
		File file;
		byte b[] = null;
		try{
			/*String projId = getId(vo);
			vo.setId(projId);*/
			vo.setProcessid(processid);
			LogAgent.updateLog(vo.getProcessid(), 1);
			Timekeeper now = new Timekeeper();
			Token u = AuthorizeToken.authenticate(vo);	
			b = printBatchRenewalLogic(vo, u);
			String filename = vo.getGroup()+"_"+now.getString("FULLDATECODE")+"_"+batchid+".pdf";
			PrintAgent.updateBatch(Operator.toString(batchid), filename);
			file = new File(Config.getString("files.storage_path") +"/"+filename);
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fop.write(b);
			fop.flush();
			fop.close();
			LogAgent.completeLog(vo.getProcessid(), "completed processing "+vo.getGroup());
			
		}
		catch(Exception e){
			Logger.error(e);
		}
		
		return b;
	}
	
	
	public static byte[] printBatchRenewalLogic(RequestVO vo, Token u) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			HashMap<String,String> t = PrintAgent.getTemplate(vo);
			
			Logger.info("GET NEW TEMPLATE "+t.get("ID")+"---"+t.get("TEMPLATE"));
			
			String type = "B";
			if(vo.getGroupid().equals("251")){
				type = "O";
			}
			else if(vo.getGroupid().equals("252")){
				type = "P";
			}
			//JSONObject doList = PrintAgent.getAccountsForRenewalLetter(vo.getType());
			JSONArray l = PrintAgent.getAccountsForRenewalLetter(type);
			Logger.info(html);
			b = new PrintPDF().htmltoPdf(t,l, o).toByteArray();
		}
		catch (Exception e) { }
		return b;
	}
	
	
	public static byte[] printBatchLogic(RequestVO vo, Token u) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			HashMap<String,String> t = PrintAgent.getTemplate(vo);
			JSONObject doList = PrintAgent.listPrints(t);
			JSONArray l = PrintAgent.getPrintDetail(vo, doList);
			Logger.info(html);
			b = new PrintPDF().htmltoPdf(t,l, o).toByteArray();
		}
		catch (Exception e) { }
		return b;
	}
	

	public static byte[] printTransaction(HttpServletRequest request, HttpServletResponse response, String json) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			String url = CsApiConfig.getString("print.transaction");
			html = FileUtil.getUrlContent(url);
			Logger.info(html);
			html = PrintAgent.renderTransactionSummary(vo, html);
			b = new PrintPDF().htmltoPdf(html, o,false).toByteArray();
			
		}
		catch (Exception e) { }
		return b;
	}
	
	public static byte[] printTransaction(RequestVO vo) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			
			String url = CsApiConfig.getString("print.transaction");
			html = FileUtil.getUrlContent(url);
			Logger.info(html);
			html = PrintAgent.renderTransactionSummary(vo, html);
			b = new PrintPDF().htmltoPdf(html, o,false).toByteArray();
			
		}
		catch (Exception e) { }
		return b;
	}
	
	
	public static String getTemplates(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = new ResponseVO();
		
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			r = PrintAgent.getTemplates(vo);
			
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
			
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String dotbatch(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = new ResponseVO();
		//byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			if(Operator.equalsIgnoreCase(vo.getGroup(),"renewal")) {
				r = BatchRunnable.threadRenewal(vo);
			}
			else {
				r = BatchRunnable.threadPrint(vo);
			}
			//b = doBatch(vo);
			
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
			
		}
		catch (Exception e) { }
		return s;
	}
	
	
	public static String getBatchDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			String command = PrintSQL.getBatchDetails(vo.getGroup(), Operator.toInt(vo.getGroupid()));
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
	
	
	public static String getId(RequestVO vo){
		ResponseVO r = ParkingAgent.onlinePrints(vo);
		ObjGroupVO[] objgrp = r.getType().getGroups();
		StringBuffer id = new StringBuffer();
		int count = 1;
		for(ObjGroupVO obj: objgrp){
			id.append(obj.getExtras().get("ID"));
			if(count < objgrp.length)
				id.append(",");
			count++;
		}
		return id.toString();
	}
	
	
	public static String getBatchStatus(HttpServletRequest request, HttpServletResponse response, String json) {
		ResponseVO r = new ResponseVO();
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
//			if(Operator.equalsIgnoreCase(vo.getGroup(), "renewal")) {
//				r = LogAgent.getLog(vo.getGroupid());
//			}
//			else {
				r.setPercentcomplete(PrintAgent.getBatchStatus(vo));
//			}
			r.setId(Operator.toInt(vo.getGroupid()));
			
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}
	
	/*public static byte[] doBatchRenewal(RequestVO vo, int id){

		FileOutputStream fop = null;
		File file;
		byte b[] = null;
		try{
			String projId = getId(vo);
			vo.setId(projId);
			
			if(id > 0){
				vo.setGroupid(Operator.toString(id));
			}
			
			Timekeeper now = new Timekeeper();
			b = printBatchLogic(vo, new Token());
			String filename = "batch_"+now.getString("MMDDYYY")+"_"+vo.getGroupid()+".pdf";
			PrintAgent.updateBatch(Operator.toString(vo.getGroupid()), filename);
			file = new File(CsApiConfig.getString("cs.fullcontexturl") +"/"+"C:"+Config.getString("files.storage_url") +"/"+filename);
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fop.write(b);
			fop.flush();
			fop.close();
				
		}catch(Exception e){
			
		}
		
		return b;
	}*/
	
	
	
	
}















