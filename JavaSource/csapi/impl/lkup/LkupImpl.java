package csapi.impl.lkup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csshared.utils.ObjMapper;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.SubObjVO;


public class LkupImpl {

	public static String type(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.types(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String typedescriptions(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.typeDescriptions(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String status(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.status(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String statusdescriptions(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.statusDescriptions(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String group(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.groups(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String usertype(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.usertypes(vo.getGrouptype(), vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String strmod(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.strmod(Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String streets(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = LkupAgent.streets(Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


}















