package csapi.impl.email;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.log.LogAgent;
import csapi.security.AuthorizeToken;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class EmailRunnable {

	public static void threadEmail(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						EmailImpl.sendEmail(request, token);
					}
					catch (Exception e) {
					}
                }
            }).start();
        }
        catch(Exception e){
        }
    }
	
	public static String send(HttpServletRequest request, HttpServletResponse response, String json) {
		Logger.highlight("Send Email Using Thread");
		String s = "";
		ResponseVO r = LogAgent.createResponse("SEND EMAIL");
		RequestVO vo = ObjMapper.toRequestObj(json);
		vo.setProcessid(r.getProcessid());
		Token u = AuthorizeToken.authenticate(vo);

		threadEmail(vo, u);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}






}





