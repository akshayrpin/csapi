package csapi.impl.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class CustomImpl {

	public static final String LOG_CLASS= "CustomImpl.java  : ";

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			/*TypeVO v = Types.getFields(vo);*/
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			
			ObjGroupVO g = CustomAgent.details(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()),id);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			
			ObjGroupVO g = CustomAgent.details(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()),  id);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			ResponseVO r = new ResponseVO();
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = AuthorizeToken.authenticate(vo);
			ObjectMapper mapper = new ObjectMapper();
			int id = Operator.toInt(vo.getId());
			boolean b = CustomAgent.delete(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()),  id, u.getId(), u.getIp());

			if (b) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "custom");
			}
			else {
				r.setMessagecode("cs500");
				r.addMessage("Server Error");
			}
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


}















