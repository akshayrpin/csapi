package csapi.impl.apn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.general.GeneralImpl;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.RequestVO;
import csshared.vo.TypeVO;


public class ApnImpl {

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
















}















