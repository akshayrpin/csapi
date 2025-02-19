package csapi.impl.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.LkupCache;
import csapi.security.AuthorizeToken;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.lkup.RolesVO;

public class AdminImpl {

	public static final String LOG_CLASS = "AdminImpl.java  : ";

	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			//NOT FETCHING TOKEN OR USER ID CHECK WITH ALAIN
			Logger.info(u.getToken());
			Logger.info(u.getId());
			
			BrowserVO v = AdminAgent.browse(vo.getEntity(), vo.getId(), u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
		return s;
	}

	public static String adminaccess(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			String entity = vo.getEntity();
			ResponseVO r = new ResponseVO();
			r.setMessagecode("cs403");
			r.setValid(false);
			if (u.isAdmin()) {
				r.setMessagecode("cs200");
				r.setValid(true);
			}
			else {
				RolesVO rl = LkupCache.getAdminRoles(entity);
				if (rl.readAccess(u.getRoles(), u.getNonpublicroles())) {
					r.setMessagecode("cs200");
					r.setValid(true);
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
}
