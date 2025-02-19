package csapi.impl.lockbox;

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


public class LockboxRunnable {

	public static void threadUpload(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						LockboxImpl.upload(request, token);
					}
					catch (Exception e) {
						Logger.error(e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
			Logger.error(e.getMessage());
        }
    }
	
	
	public static void threadUpdate(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						LockboxImpl.update(request, token);
					}
					catch (Exception e) {
						Logger.error(e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
			Logger.error(e.getMessage());
        }
    }
	
	public static void threadAdd(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						LockboxImpl.add(request, token);
					}
					catch (Exception e) {
						Logger.error(e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
			Logger.error(e.getMessage());
        }
    }
	
	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		LogAgent.completeLog("xyzlockbox"+u.getId(), "200");
		ResponseVO r = LogAgent.createResponse("UPLOAD LOCKBOX","xyzlockbox"+u.getId());
		vo.setProcessid(r.getProcessid());
		threadUpload(vo, u);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}


	public static String update(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ResponseVO r = LogAgent.createResponse("UPDATE LOCKBOX","xyzlockbox"+u.getId());
		vo.setProcessid(r.getProcessid());
		threadUpdate(vo, u);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}
	
	public static String add(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ResponseVO r = LogAgent.createResponse("ADD LOCKBOX","xyzlockbox"+u.getId());
		vo.setProcessid(r.getProcessid());
		threadAdd(vo, u);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}




}





