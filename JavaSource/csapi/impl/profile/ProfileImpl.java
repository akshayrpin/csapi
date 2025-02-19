package csapi.impl.profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.parking.ParkingAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class ProfileImpl {

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

	public static String permit(HttpServletRequest request, HttpServletResponse response, String json) {
		String r = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = Token.retrieve(vo.getToken(), vo.getIp());

			TypeVO v = new TypeVO();
			ObjGroupVO fields = ProfileFields.details();
			String command = ProfileSQL.details(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), u);
			ObjGroupVO g = Group.vertical(fields, command);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			r = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String parkingAddl(HttpServletRequest request, HttpServletResponse response, String json) {
		String r = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			TypeVO v = new TypeVO();
			int id = Operator.toInt(vo.getId());
//			if (id < 1) {
//				ObjGroupVO fields = ProfileFields.parkingaddl();
//				fields.setRead(true);
//				fields.setUpdate(true);
//				ObjGroupVO[] gs = new ObjGroupVO[1];
//				gs[0] = fields;
//				v.setGroups(gs);
//				r = mapper.writeValueAsString(v);
//			}
//			else {
				ObjGroupVO fields = ProfileFields.parkingaddl();
				fields.setRead(true);
				fields.setUpdate(true);
				String command = ProfileSQL.parkingAddl(id, u);
				ObjGroupVO[] gs = Group.verticals(fields, command);
				if (!Operator.hasValue(gs)) {
					v.setMessage("No parking account associated with your profile was found.");
				}
				v.setGroups(gs);
				r = mapper.writeValueAsString(v);
//			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String addlFields(HttpServletRequest request, HttpServletResponse response, String json) {
		String r = "";
		try {
			//TODO secure
			ObjectMapper mapper = new ObjectMapper();

			TypeVO v = new TypeVO();
			ObjGroupVO fields = ProfileFields.parkingaddl();
			fields.setRead(true);
			fields.setUpdate(true);
			ObjGroupVO[] f = new ObjGroupVO[1];
			f[0] = fields;

			v.setGroups(f);
			r = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String saveParkingAddl(HttpServletRequest request, HttpServletResponse response, String json) {
		String r = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		String id = vo.getId();

		ResponseVO res = new ResponseVO();
		res.setMessagecode("cs200");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			r = mapper.writeValueAsString(res);
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return r;
	}

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			r = ProfileAgent.save(vo);
			s = mapper.writeValueAsString(r);
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}



}















