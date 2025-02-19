package csapi.impl.log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class LogImpl {

	public static String get(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			String processid = vo.getId();
			ResponseVO r = LogAgent.getLog(processid);
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}






}





