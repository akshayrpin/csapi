package csapi.impl.copy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.entity.EntityAgent;
import csapi.impl.lkup.LkupAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.lso.LsoSQL;
import csapi.impl.project.ProjectAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Modules;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeInfo;
import csshared.vo.TypeVO;


public class CopyImpl {

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			TypeInfo ti = EntityAgent.getEntity(vo.getType(), vo.getTypeid());
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getDetails(vo);
			ToolsVO t = Modules.copy(ti.getEntity(), ti.getEntityid(), vo.getType(), vo.getTypeid(), u);
			v.setTools(t);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String modules(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			ToolsVO t = Modules.copy(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), u);
			s = mapper.writeValueAsString(t);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String custom(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			ToolsVO t = Modules.custom(vo.getType(), vo.getTypeid(), u);
			s = mapper.writeValueAsString(t);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String reviews(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjectMapper mapper = new ObjectMapper();
			ToolsVO t = Modules.reviews(vo.getType(), vo.getTypeid(), u);
			s = mapper.writeValueAsString(t);
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

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = CopyAgent.copy(vo);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
				
		return s;
	}


}















