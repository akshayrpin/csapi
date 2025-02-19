package csapi.impl.review;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activity.ActivityImpl;
import csapi.impl.log.LogAgent;
import csapi.impl.people.PeopleAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ComboReviewGroup;
import csshared.vo.ComboReviewList;
import csshared.vo.ComboReviewVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class ReviewRunnable {

	public static void threadCreate(RequestVO vo, Token u) {
		final RequestVO request = vo;
		final Token token = u;

		// start process in new thread
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
						ReviewImpl.create(request, token);
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
	
	public static String create(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		ResponseVO r = LogAgent.createResponse("CREATE REVIEWS");
		RequestVO vo = ObjMapper.toRequestObj(json);
		vo.setProcessid(r.getProcessid());
		Token u = AuthorizeToken.authenticate(vo);

		threadCreate(vo, u);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
			Logger.highlight(s);
		}
		catch (Exception e) { Logger.error(e.getMessage()); }
		return s;
	}


}















