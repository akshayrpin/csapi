package csapi.impl.peoplesummary;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.people.PeopleAgent;
import csapi.impl.review.ReviewAgent;
import csapi.impl.review.ReviewOptSQL;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class PeoplesummaryImpl {

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

	public static String activities(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			SubObjVO[] o = Choices.getChoices(PeoplesummarySQL.activities(vo.getType(), vo.getTypeid()));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO d = DataVO.toDataVO(vo);
		String r = "";

		try {
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO resp = new ResponseVO();
			String actids = d.getString("ACTIVITIES");
			String people = d.getString("PEOPLE");
			if (!Operator.hasValue(actids)) {
				resp.setMessagecode("cs404");
				resp.addError("Activities is a required field");
			}
			else if (!Operator.hasValue(people)) {
				resp.setMessagecode("cs404");
				resp.addError("People is a required field");
			}
			else {
				if (PeoplesummaryAgent.save(actids, people, u.getId(), u.getIp())) {
					resp.setMessagecode("cs200");
				}
				else {
					resp.setMessagecode("cs500");
				}
				CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "peoplesummary");
			}
			r = mapper.writeValueAsString(resp);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ArrayList<MessageVO> msgs = new ArrayList<MessageVO>();

		if (!Operator.hasValue(vo.getId())) {
			if (!Operator.hasValue(vo.getId())) {
				r.setMessagecode("cs404");
				r.addError("Id is a required field");
			}
		}
		else {
			boolean b = PeoplesummaryAgent.delete(Operator.toInt(vo.getId()), u.getId(), u.getIp());
			if (b) {
				r.setMessagecode("cs200");
				CsDeleteCache.deleteProjectCache(vo.getType(), vo.getTypeid(), "peoplesummary");
			}
			else {
				r.setMessagecode("cs500");
				MessageVO mvo = new MessageVO();
				mvo.setMessage("Database Error");
				msgs.add(mvo);
			}
		}

		try {
			r.setErrors(msgs);
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) { }
		return res;
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



}















