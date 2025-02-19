package csapi.impl.move;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.lkup.LkupAgent;
import csapi.impl.lso.LsoSQL;
import csapi.impl.project.ProjectAgent;
import csapi.impl.project.ProjectSQL;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;


public class MoveImpl {

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

	public static String move(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			DataVO d = DataVO.toDataVO(vo);
			if (Operator.equalsIgnoreCase(vo.getType(), "project")) {
				int projectid = vo.getTypeid();
				int newlsoid = d.getInt("LSO_ID");
				if (projectid < 1) {
					r.setMessagecode("cs412");
					r.addError("Project is a required field");
				}
				else if (newlsoid < 1) {
					r.setMessagecode("cs412");
					r.addError("LSO is a required field");
				}
				else if (ProjectAgent.move(projectid, newlsoid, u.getId(), u.getIp())) {
					r.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "project");
					GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
					GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
				}
				else {
					r.setMessagecode("cs500");
					r.addError("The move could not be successfully completed");
				}
			}
			else if (Operator.equalsIgnoreCase(vo.getType(), "activity")) {
				int actid = vo.getTypeid();
				int projid = d.getInt("PROJECT_ID");
				if (projid < 1) {
					r.setMessagecode("cs412");
					r.addError("Project is a required field");
				}
				else if (actid < 1) {
					r.setMessagecode("cs412");
					r.addError("Unknown activity");
				}
				else if (ActivityAgent.move(actid, projid, u)) {
					r.setMessagecode("cs200");
					CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "activity");
					GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
					GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
				}
				else {
					r.setMessagecode("cs500");
					r.addError("The move could not be successfully completed");
				}
			}
			else {
				r.setMessagecode("cs412");
				r.addError("Can not move "+vo.getType());
			}

			if(Operator.hasValue(r.getMessagecode())) {
				s = mapper.writeValueAsString(r);
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}

	public static String search(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			String type = vo.getType();
			int typeid = vo.getTypeid();
			DataVO d = DataVO.toDataVO(vo);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = new SubObjVO[0];
			if (d.getInt("LSO_ID") > 0 || (d.getInt("STR_NO") > 0 && d.getInt("STREET") > 0)) {
				String command = LsoSQL.search(d.getInt("LSO_ID"), d.getInt("STR_NO"), d.getString("STR_MOD"), d.getInt("STREET"), d.getString("UNIT"));
				v = Choices.getChoices(command);
			}
			else if ((Operator.equalsIgnoreCase(type, "activity") && typeid > 0) || Operator.hasValue(d.getString("PROJECT_NBR"))) {
				String command = ProjectSQL.search(typeid, d.getString("PROJECT_NBR"));
				v = Choices.getChoices(command);
			}
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


}















