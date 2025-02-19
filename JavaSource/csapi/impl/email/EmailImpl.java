package csapi.impl.email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.email.ExchangeMessenger;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.print.PrintAgent;
import csapi.impl.print.PrintImpl;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.PrintPDF;
import csapi.utils.objtools.Types;
import csshared.utils.CsConfig;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class EmailImpl {

	

	/*public static byte[] print(HttpServletRequest request, HttpServletResponse response, String json) {
		String html = "";
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			HashMap<String,String> t = PrintAgent.getTemplate(vo);
			JSONObject doList = PrintAgent.listPrints(t);
			JSONArray l = PrintAgent.getPrintDetail(vo, doList);
			Logger.info(html);
			
			
			b = new PrintPDF().htmltoPdf(t,l, o).toByteArray();
			
		}
		catch (Exception e) { }
		return b;
	}*/
	
	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			
			
			
			TypeVO v = EmailAgent.getDetails(vo);
			r.setType(v);
			s = mapper.writeValueAsString(r);
			
			
			Logger.info("sunil "+s); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String send(RequestVO vo, Token u,byte[] b,HashMap<String,String> t) {
		String s = "";
		try {
			//TODO secure
			
			ObjectMapper mapper = new ObjectMapper();
			
			
			LogAgent.updateLog(vo.getProcessid(), 80, "Send Email");
			ExchangeMessenger m = new ExchangeMessenger();
			//m.setBCC(vo.getExtra("email_bcc"));
			m.setRecipient(Operator.replace(vo.getExtra("email_bcc"), "|", ","));
			m.setBCC(Operator.replace(vo.getExtra("email_bcc"), "|", ","));
			m.setSubject(vo.getExtra("email_subject"));
			m.setContent(vo.getExtra("email_body"));
			
			
			Timekeeper k = new Timekeeper();
			
			
			String filename = Config.getString("files.templates_path")+"email/pdf/"+t.get("NAME")+"_"+k.getString("DT")+".pdf";
			
			
			FileOutputStream fos = new FileOutputStream (new File(filename)); 
			
			fos.write(b);
			
			fos.close();
			
			
			m.setAttachments(filename);
			boolean result = m.deliver();
			
			Logger.info(vo.getExtra("email_bcc")+"EMAIL SEND %%%%%%%%%%%%%%%%"+vo.getExtra("email_subject"));
			Logger.info("EMAIL SEND %%%%%%%%%%%%%%%%"+result);
			//s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static ResponseVO sendEmail(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		try {
			LogAgent.updateLog(vo.getProcessid(), 20, "Preparing template");
			
			HashMap<String,String> template = PrintAgent.getTemplate(vo);
			byte[] b = new byte[0];
			if(vo.getType().equalsIgnoreCase("payment")){
				template.put("NAME", "TRANSACTION");
				b = PrintImpl.printTransaction(vo);
			} else {
				b = PrintImpl.print(vo,template);
			}
			
			
			LogAgent.updateLog(vo.getProcessid(), 70, "Processed Template");
			
			
		
			
			send(vo, u, b,template);
			
			LogAgent.updateLog(vo.getProcessid(), 90, "Completed ");
			int result = b.length;
			r = LogAgent.getLog(vo.getProcessid());
			if (result > 0) {
				r.setMessagecode("cs200");
				TypeVO t = new TypeVO();
				t.setType(vo.getType());
				t.setTypeid(result);
				t.setId(result);
				t.setEntity(vo.getEntity());
				t.setEntityid(vo.getEntityid());
				r.setType(t);
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			else if (!r.isValid()) {
				r.setPercentcomplete(100);
				LogAgent.saveLog(r);
			}
			else {
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
	
	public static String send(RequestVO vo) {
		String s = "";
		try {
			
			LogAgent.updateLog(vo.getProcessid(), 80, "Send Email");
			ExchangeMessenger m = new ExchangeMessenger();
			m.setRecipient(Operator.replace(vo.getExtra("email_bcc"), "|", ","));
			m.setBCC(Operator.replace(vo.getExtra("email_bcc"), "|", ","));
			m.setSubject(vo.getExtra("email_subject"));
			m.setContent(vo.getExtra("email_body"));
			boolean result = m.deliver();
			
			Logger.info(vo.getExtra("email_bcc")+"EMAIL SEND %%%%%%%%%%%%%%%%"+vo.getExtra("email_subject"));
			Logger.info("EMAIL SEND %%%%%%%%%%%%%%%%"+result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String sendEmail(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		ObjectMapper mapper = new ObjectMapper();
		return send(vo);
	}
	

}















