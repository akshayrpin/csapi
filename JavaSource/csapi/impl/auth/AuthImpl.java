package csapi.impl.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.OauthUtils;
import alain.core.security.RequestToken;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.users.UsersAgent;
import csshared.utils.ObjMapper;


public class AuthImpl {


	public static String login(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken vo = ObjMapper.toRequestToken(json);
		
		Logger.info("suuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+vo.getOauthAccessToken());
		
		Logger.info("suuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+vo.getToken());
		if (Operator.hasValue(vo.getUsername()) && Operator.hasValue(vo.getPassword())) {
			Token t = UsersAgent.login(vo.getUsername(), vo.getPassword(), vo.getRequestor(), vo.getIp());
			return t.toString();
		}
		else {
			Token t = Token.retrieve(vo.getToken(), vo.getIp());
			return t.toString();
		}
	}

	public static String token(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken vo = ObjMapper.toRequestToken(json);
		//Token t = new Token();
		//t = Token.retrieve(vo.getToken(), vo.getIp());
		//if(t.getId()<=0 && (Operator.hasValue(Config.getString("user.rest.url")))){
		
		 //String username = OauthUtils.getUsernameString(Config.getString("security.oauth2.userinfo_endpoint"), vo.getToken());
		Token t = UsersAgent.createToken(vo.getUsername(), vo.getToken(), vo.getIp());
		//}
		
		return t.toString();
	}


	public static String loginOauth(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken vo = ObjMapper.toRequestToken(json);
		if (Operator.hasValue(vo.getOauthAccessToken())) {
			Token t = UsersAgent.loginOauth(vo.getOauthAccessToken(), vo.getIp());
			return t.toString();
		}
		else {
			Token t = Token.retrieve(vo.getToken(), vo.getIp());
			return t.toString();
		}
	}


}















