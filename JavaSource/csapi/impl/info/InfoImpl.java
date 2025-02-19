package csapi.impl.info;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.users.UsersAgent;
import csapi.utils.CsApiConfig;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class InfoImpl {

	public static String version(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Logger.info("VERSION :: "+vo.getToken());
			Token u = UsersAgent.createToken(vo.getUsername(), vo.getToken(), vo.getIp());
			
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO r = new ResponseVO();
			r.addInfo("VERSION", CsApiConfig.getString("cs.version"));
			r.addInfo("USERNAME", u.getUsername());
			r.addInfo("USERID", Operator.toString(u.getId()));
			r.addInfo("STAFFID", Operator.toString(u.getEmplnum()));
			r.addInfo("IP", u.getIp());
			if (u.isAdmin()) {
				r.addInfo("ROLES", "ADMIN");
			}
			else {
				r.addInfo("ROLES", u.getInfo().get("ROLES"));
			}
			r.addInfo("TOKEN", u.token);
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String content(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		String ctype = vo.getReference();
		Logger.highlight(vo.getToken());
		s = InfoAgent.content(ctype, u);
		return s;
	}

}















