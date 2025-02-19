package csapi.security;

import java.io.File;

import alain.core.security.Token;
import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import csshared.vo.RequestVO;

public class AuthorizeToken {

	public static Token getToken(String token, String ip){
		return Token.retrieve(token, ip);
	}
	
	
	public static Token authenticate(RequestVO vo){
		return Token.retrieve(vo.getToken(), vo.getIp());
	}
	
	public static Token authenticate(String token, String ip){
		return Token.retrieve(token, ip);
	}

	public static Token toToken(File f) {
		Token evo = new Token();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			evo = mapper.readValue(f, Token.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}
}