package csapi.impl.log;

import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.CsApiConfig;
import csshared.utils.ObjMapper;
import csshared.vo.ResponseVO;



public class LogAgent {

	public static ResponseVO getLog(String processid) {
		ResponseVO vo = new ResponseVO();
		if (!Operator.hasValue(processid)) { return vo; }
		try {
			String json = FileUtil.getCache(getCachePath(processid), 0);
			vo = ObjMapper.toResponseObj(json);
			if (vo == null) {
				vo = new ResponseVO();
			}
		}
		catch (Exception e) { vo = new ResponseVO(); }
		return vo;
	}

	public static ResponseVO saveLog(ResponseVO response) {
		String processid = response.getProcessid();
		if (Operator.hasValue(processid)) {
			String json = ObjMapper.toJson(response);
			FileUtil.saveCache(getCachePath(processid), json);
		}
		return response;
	}

	public static ResponseVO updateLog(String processid, String message) {
		return updateLog(processid, 0, "", message);
	}

	public static ResponseVO updateLog(String processid, int addpercent) {
		Logger.highlight("PROCESS:"+processid);
		Logger.highlight("ADD:"+addpercent);
		return updateLog(processid, addpercent, "", "");
	}

	public static ResponseVO updateLog(String processid, String title, String message) {
		return updateLog(processid, 0, title, message);
	}

	public static ResponseVO updateLog(String processid, int addpercent, String message) {
		return updateLog(processid, addpercent, "", message);
	}

	public static ResponseVO updateLog(String processid, int addpercent, String title, String message) {
		if (!Operator.hasValue(processid)) { return new ResponseVO(); }
		ResponseVO vo = getLog(processid);
		if (addpercent > 0) {
			
			int p = 0;
			if(!processid.startsWith("xyzlockbox")){
				p = vo.getPercentcomplete();
			}
			Logger.highlight("PERCENT COMPELTE:"+p);
			p = p + addpercent;
			if(!processid.startsWith("xyzlockbox")){
				if (p >= 100) { p = 99; }
			}
			
			Logger.highlight("TOTAL:"+p);
			vo.setPercentcomplete(p);
		}
		vo.setProcessid(processid);
		if (Operator.hasValue(title)) {
			vo.setProcesstitle(title);
		}
		if (Operator.hasValue(message)) {
			vo.setProcessmessage(message);
		}
		saveLog(vo);
		return vo;
	}

	public static ResponseVO updateLog(String processid, int addpercent, int maxpercent, String message) {
		return updateLog(processid, addpercent, maxpercent, "", message);
	}

	public static ResponseVO updateLog(String processid, int addpercent, int maxpercent, String title, String message) {
		if (!Operator.hasValue(processid)) { return new ResponseVO(); }
		ResponseVO vo = getLog(processid);
		int p = vo.getPercentcomplete();
		p = p + addpercent;
		if (p > maxpercent) { p = vo.getPercentcomplete(); }
		vo.setProcessid(processid);
		vo.setPercentcomplete(p);
		vo.setProcessmessage(message);
		if (Operator.hasValue(title)) {
			vo.setProcesstitle(title);
		}
		saveLog(vo);
		return vo;
	}

	public static ResponseVO logError(String processid, String code, String title, String error) {
		if (!Operator.hasValue(processid)) { return new ResponseVO(); }
		ResponseVO vo = getLog(processid);
		if (Operator.hasValue(code)) {
			vo.setMessagecode(code);
		}
		vo.setProcessid(processid);
		if (Operator.hasValue(title)) {
			vo.setProcesstitle(title);
		}
		if (Operator.hasValue(error)) {
			vo.addError(error);
		}
		saveLog(vo);
		return vo;
	}

	public static ResponseVO completeLog(String processid, String code) {
		if (!Operator.hasValue(processid)) { return new ResponseVO(); }
		ResponseVO vo = getLog(processid);
		vo.setMessagecode(code);
		vo.setProcessid(processid);
		vo.setPercentcomplete(100);
		saveLog(vo);
		return vo;
	}

	public static ResponseVO completeLog(String processid, String code, String message) {
		if (!Operator.hasValue(processid)) { return new ResponseVO(); }
		ResponseVO vo = getLog(processid);
		vo.setMessagecode(code);
		vo.setProcessid(processid);
		vo.setProcessmessage(message);
		vo.setPercentcomplete(100);
		saveLog(vo);
		return vo;
	}

	public static ResponseVO createResponse(String title) {
		String processid = createProcessId();
		ResponseVO vo = new ResponseVO();
		vo.setProcessid(processid);
		vo.setProcesstitle(title);
		vo.setPercentcomplete(0);
		vo.setProcessmessage("Run");
		saveLog(vo);
		return vo;
	}

	public static String getCachePath(String processid) {
		if (!Operator.hasValue(processid)) { return ""; }
		StringBuilder sb = new StringBuilder();
		sb.append(CsApiConfig.getCachePath());
		sb.append("/process");
		sb.append("/").append(FileUtil.simplifyFilename(processid)).append(".json");
		return sb.toString();
	}

	public static String createProcessId() {
		return Operator.randomString(15);
	}
	
	
	public static ResponseVO createResponse(String title,String processid) {
		ResponseVO vo = new ResponseVO();
		vo.setProcessid(processid);
		vo.setProcesstitle(title);
		vo.setPercentcomplete(0);
		vo.setProcessmessage("Run");
		saveLog(vo);
		return vo;
	}





}















