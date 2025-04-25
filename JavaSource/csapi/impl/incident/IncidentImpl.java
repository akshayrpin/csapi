package csapi.impl.incident;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.security.AuthorizeToken;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;

public class IncidentImpl {

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
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

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		Token u = AuthorizeToken.authenticate(vo);
		DataVO m = DataVO.toDataVO(vo);
		//r = HousingcountAgent.deleteHousing(vo.getType(), Operator.toInt(vo.getId()), u.getId());
		String s = null;
		try {
			s = mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			Logger.error(e.getMessage());
		}
		return s;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Token u = AuthorizeToken.authenticate(vo);
			DataVO m = DataVO.toDataVO(vo);
			
			r = IncidentAgent.add(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), m.getString("LKUP_INCIDENT_TYPE_ID"), m.getString("LKUP_INCIDENT_STATUS_ID"), m.getString("DESCRIPTION"), u.getId(), u.getIp());
			s = mapper.writeValueAsString(r);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
}
