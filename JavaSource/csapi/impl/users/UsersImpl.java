package csapi.impl.users;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import alain.core.security.RequestToken;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.holds.HoldsSQL;
import csapi.impl.notes.NotesAgent;
import csapi.impl.notes.NotesSQL;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.UserVO;


public class UsersImpl {

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

	public static String saveProfile(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		Token u = AuthorizeToken.authenticate(vo);
		boolean b = UsersAgent.saveProfile(u.getUsername(), m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"), m.getString("ADDRESS"), m.getString("CITY"), m.getString("STATE"), m.getString("ZIP"), m.getString("PHONE_WORK"), m.getString("PHONE_CELL"), m.getString("PHONE_HOME"), m.getString("FAX"), m.getString("COMMENTS"), u.getId(), u.getIp(),m.getString("NOTIFY"));
		if (b) {
			r.setMessagecode("cs200");
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Could not save profile.");
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			
		}
		return res;
	}

	public static String login(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		String username = r.getUsername();
		String password = r.getPassword();
		String requestor = r.getRequestor();
		String ip = r.getIp();
		Token t = UsersAgent.login(username, password, requestor, ip);
		t.save();
		return t.toString();
	}
	
	
	
	public static String loginOauth(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		String accesstoken = r.getOauthAccessToken();
	
		String ip = r.getIp();
		Token t = UsersAgent.loginOauth(accesstoken, ip);
		t.save();
		return t.toString();
	}

	public static String logout(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		String token = r.getUsername();
		String ip = r.getIp();
		UsersAgent.logout(token, ip);
		Token t = new Token();
		return t.toString();
	}

	public static String onlineUser(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		
		Token t = UsersAgent.createToken(vo.getUsername(), vo.getToken(), vo.getIp());
		//Token t = AuthorizeToken.authenticate(vo);
		
		//Logger.info("rrsuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+t.getOauthAccessToken());
		Logger.info("rrsuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+vo.getUsername());
		Logger.info("rrsuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+vo.getToken());
		
		ObjectMapper mapper = new ObjectMapper();
		String r = "";
		HashMap<String, String> u = UsersAgent.onlineUser(t.getUsername(), t.getId());
		try {
			r = mapper.writeValueAsString(u);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	public static String getToken(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		String token = r.getToken();
		String ip = r.getIp();
		Token t = UsersAgent.getToken(token, ip);
		t.save();
		return t.toString();
	}

	public static String createToken(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		String rep = r.getRep();
		String reppass = r.getReppassword();
		Logger.highlight(rep);
		Token t = new Token();
		if (UsersAgent.isRepresentative(rep, reppass)) {
			String username = r.getUsername();
			String ip = r.getIp();
			t = UsersAgent.createToken(username, ip);
			t.save();
			return t.toString();
		}
		return t.toString();
	}
	
	public static String generateToken(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestToken r = RequestToken.toRequestToken(json);
		Token t = new Token();
		String username = r.getUsername();
		String ip = r.getIp();
		t = UsersAgent.createToken(username, ip,r.getToken());
		t.save();
		return t.toString();
	}

	public static String username(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			String username = vo.getExtra("username");
			ResponseVO v = new ResponseVO();
			v = UsersAgent.username(username);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String usernameonlineaccount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);
			RequestToken r = RequestToken.toRequestToken(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = UsersAgent.assistOnlineAccount(r.getUsername(), r.getUsername(), t.getId(), t.getIp());
			s = mapper.writeValueAsString(u);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String refuseronlineaccount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);
			ObjectMapper mapper = new ObjectMapper();
			Token u = UsersAgent.assistRefUsersOnlineAccount(Operator.toInt(vo.getId()), vo.getReference(), t.getId(), t.getIp());
			s = mapper.writeValueAsString(u);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String useronlineaccount(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token t = AuthorizeToken.authenticate(vo);
			ObjectMapper mapper = new ObjectMapper();
			Token u = UsersAgent.assistOnlineAccount(Operator.toInt(vo.getId()), vo.getReference(), t.getId(), t.getIp());
			s = mapper.writeValueAsString(u);
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
			String email = vo.getExtra("email");
			ResponseVO v = new ResponseVO();
			v = UsersAgent.email(email);
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
			ResponseVO v = UsersAgent.select(vo);
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static Object peopletype(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			String command = UsersSQL.getPeopleType();
			Logger.info(command);
			JSONObject o = Choices.getChoicesArray(command, "");
			s = o.toString();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return s;
	}

	
	public static String autoLogin(HttpServletRequest request, HttpServletResponse response, String json) {
		
		RequestToken r = RequestToken.toRequestToken(json);
		
		String rep = r.getRep();
		String reppass = r.getReppassword();
		Logger.highlight(rep);
		Token t = new Token();
		if (UsersAgent.isRepresentative(rep, reppass)) {
			String username = r.getUsername();
			String ip = r.getIp();
			t = UsersAgent.createToken(username, ip);
			t.save();
			return t.toString();
		}
		return t.toString();
	}
	
	
	public static String saveTeamProfile(HttpServletRequest request, HttpServletResponse response, String json) {
		boolean ri = false;
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		DataVO m = DataVO.toDataVO(vo);
		Token u = AuthorizeToken.authenticate(vo);
		String email = m.getString("EMAIL");
		int id =0;
		//sunill@beverlyhills.org
		String[] parts = email.split("@");
		System.out.println("parts : "+parts.length);
		if(parts!=null && parts.length>1) {
		if(parts[1]!=null & parts[1].equalsIgnoreCase("beverlyhills.org")) {
		 id = UsersAgent.saveTeamProfile(parts[0], m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"),  u.getId(), u.getIp(),vo.getTypeid(), m.getString("PHONE_WORK"));	
		}else {
		 id = UsersAgent.saveTeamProfile(m.getString("EMAIL"), m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"),  u.getId(), u.getIp(),vo.getTypeid(), m.getString("PHONE_WORK"));
		}
		}
		ri = id > 0;
		if (ri) {
			r.setId(id);
			r.setMessagecode("cs200");
		}
		else {
			r.setMessagecode("cs412");
			r.addMessage("Could not save team profile.");
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			
		}
		return res;
	}

	public static String notes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			int id = Operator.toInt(vo.getId());
			int userId = UsersAgent.getRefUser(id);
			SubObjVO[] v = Choices.getChoices(NotesSQL.getNotes("users", userId));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	public static String holds(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			int id = Operator.toInt(vo.getId());
			int userId = UsersAgent.getRefUser(id);
			SubObjVO[] v = Choices.getChoices(HoldsSQL.userlist(vo.getType(), userId,0));
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	public static String refUserDetails(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token t = AuthorizeToken.authenticate(vo);
		ObjectMapper mapper = new ObjectMapper();
		String r = "";
		SubObjVO[] v = Choices.getChoices(UsersSQL.getRefUser(Operator.toInt(vo.getId())));
		//HashMap<String, String> u = UsersAgent.refUserDetails(Operator.toInt(vo.getId()));
		try {
			r = mapper.writeValueAsString(v);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

}















