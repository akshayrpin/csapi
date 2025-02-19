package csapi.impl.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.log.LogAgent;
import csapi.security.AuthorizeToken;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class ProjectRunnable {

	public static void threadSave(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						ProjectImpl.saveProject(request, token);
					}
					catch (Exception e) {
						System.out.println("Error while saving in thread "+e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
        	System.out.println("Error Thread Process "+e.getMessage());
        }
    }
	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = LogAgent.createResponse("SAVE PROJECT");
		RequestVO vo = ObjMapper.toRequestObj(json);
		vo.setProcessid(r.getProcessid());
		Token u = AuthorizeToken.authenticate(vo);

		threadSave(vo, u);

		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}






}





