package csapi.impl.vehicle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.general.GeneralAgent;
import csapi.impl.people.PeopleAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateRequest;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.UserVO;


public class VehicleImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { e.printStackTrace(); }
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getDetails(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String r = "";
		ResponseVO result = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);	
		boolean action = VehicleAgent.save(vo,u);
		if(action) {
			result.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), vo.getGrouptype());
		}
		else {
			result.setMessagecode("cs500");
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			r = mapper.writeValueAsString(result);
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		return r;
	}

	/*public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getFields(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String search(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] v = UsersAgent.search(vo.getSearch(), Operator.toInt(vo.getOption()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String users(String type, int typeid) {
		String s = "";
		try {
			//TODO secure
			ObjectMapper mapper = new ObjectMapper();
			UserVO[] v = UsersAgent.getUsers(type, typeid);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		Token u = AuthorizeToken.authenticate(vo);

		String type = vo.getType();
		int typeid = vo.getTypeid();

		String group = vo.getGroup();
		String groupid = vo.getGroupid();

		if (Operator.hasValue(group)) {
			type = group;
			typeid = Operator.toInt(groupid);
		}

		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		b = UsersAgent.updateUsers(type, typeid, m.get("CURRENT"), m.get("PEOPLE"), u.getId(), u.getIp());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), group);
		}
		else {
			r.setMessagecode("cs500");
		}
		try {
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}
*/






}















