package csapi.impl.people;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.general.GeneralAgent;
import csapi.impl.library.LibraryAgent;
import csapi.impl.library.LibrarySQL;
import csapi.impl.review.ReviewAgent;
import csapi.impl.users.UsersSQL;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class PeopleImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
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

	public static String email(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO v = PeopleAgent.email(vo.getReference());
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String username(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String username = vo.getExtra("username");
			String utype = vo.getExtra("usertype");
			ResponseVO v = PeopleAgent.username(username, Operator.toInt(utype));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String license(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String username = vo.getExtra("username");
			String utype = vo.getExtra("usertype");
			ResponseVO v = PeopleAgent.license(username, Operator.toInt(utype));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String list(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getList(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String searchfields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getSearchFields(vo);
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
			SubObjVO[] v = PeopleAgent.search(vo.getSearch(), Operator.toInt(vo.getOption()));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String browse(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			BrowserVO v = PeopleAgent.browse(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) { }
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
		int id = Operator.toInt(vo.getId());
		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		b = PeopleAgent.addPeople(type, typeid, m.get("PEOPLE"), m.getInt("SET_PRIMARY_CONTACT"), m.getString("COPYAPPLICANT"), u.getId(), u.getIp());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
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

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ArrayList<MessageVO> msgs = new ArrayList<MessageVO>();

		boolean b = PeopleAgent.delete(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), u.getId(), u.getIp());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
		}
		else {
			r.setMessagecode("cs500");
			MessageVO mvo = new MessageVO();
			mvo.setMessage("Database Error");
			msgs.add(mvo);
		}

		try {
			r.setErrors(msgs);
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
	}

	public static String setPrimary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO v = PeopleAgent.setPrimary(vo);
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
			if (vo.getType().equalsIgnoreCase("project")) {
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "people");
			}
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String unPrimary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ResponseVO v = PeopleAgent.unPrimary(vo);
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
			if (vo.getType().equalsIgnoreCase("project")) {
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "people");
			}
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String select(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ResponseVO v = PeopleAgent.select(vo);
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String update(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ResponseVO v = PeopleAgent.update(vo);
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "people");
			CsDeleteCache.deleteProjectAndActivityCache(vo.getType(), vo.getTypeid(), "peoplesummary");
			if (vo.getType().equalsIgnoreCase("project")) {
				CsDeleteCache.deleteChildCache(vo.getType(), vo.getTypeid(), "people");
			}
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String peopleusers(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = PeopleAgent.peopleUsers(vo.getType(), vo.getTypeid());
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}






}















