package csapi.impl.team;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.general.GeneralAgent;
import csapi.impl.review.ReviewOptSQL;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class TeamImpl {

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
			SubObjVO[] v = TeamAgent.search(vo.getSearch(), Operator.toInt(vo.getOption()));
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
		ObjectMapper mapper = new ObjectMapper();
		boolean b = false;
		b = TeamAgent.addTeam(type, typeid, m.get("PEOPLE"), u.getId());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "team");
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
		ObjectMapper mapper = new ObjectMapper();
		boolean b = GeneralAgent.deleteRef(vo.getType(), vo.getTypeid(), "team", Operator.toInt(vo.getId()), u.getId());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "team");
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

	public static String type(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String teamtype = vo.getReference();
			SubObjVO[] v = Choices.getChoices(TeamSQL.getTeamType(teamtype));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String team(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(TeamSQL.summary(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()))));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}




}















