package csapi.impl.tasks;

import alain.core.security.RequestToken;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import csapi.common.ApiHandler;
import csapi.impl.users.UsersAgent;
import csapi.utils.CsApiConfig;
import csshared.vo.RequestVO;



public class TasksImpl {

	

	
	public static void runImmediate(int id,RequestVO vo, Token u) {
		Logger.info(id+"run immediate" + vo.getType());
		vo.setToken(u.getToken());
		vo.setTypeid(id);
		ObjectMapper mapper = new ObjectMapper();
		String s = "";
		
		try {
			s = mapper.writeValueAsString(vo);
			post(vo);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		
		
	}	
	
	public static void runPayment(String ids, int userId) {
		Logger.info(ids+"run immediate" + userId);
		

		
		//Token t = UsersAgent.login(Config.getString("security.administrator.username"), Config.getString("security.administrator.password"), "ft", "10.14.6.19");
		RequestVO vo = new RequestVO();
		vo.setRef(ids);
		vo.setRefid(userId);
		vo.setReference("payment");
		
		ObjectMapper mapper = new ObjectMapper();
		String s = "";
		
		try {
			s = mapper.writeValueAsString(vo);
			post(vo);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
		
		
	}	
	
	
	public static String post(RequestVO vo) {
		String r = "";
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			r = ow.writeValueAsString(vo);
			String url = CsApiConfig.getString("tasks.immediate");
			Logger.info("POSTING TO---->"+url);
			r = ApiHandler.post(url, r);

		}
		catch (Exception e) { }
		return r;
	}
	
	
	
	
}





