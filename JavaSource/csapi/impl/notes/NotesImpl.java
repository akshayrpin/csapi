package csapi.impl.notes;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralImpl;
import csapi.impl.users.UsersAgent;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class NotesImpl {

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

	public static String delete(HttpServletRequest request, HttpServletResponse response, String json) {
		return GeneralImpl.delete(request, response, json);
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

	public static String save(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);
			DataVO m = DataVO.toDataVO(vo);
			
			r = NotesAgent.add(vo.getType(), vo.getTypeid(), m.getInt("LKUP_NOTES_TYPE_ID"), m.getString("NOTE"), m.getString("RECIPIENT"), m.getString("SUBJECT"), m.getString("DATA"), u.getId(), u.getIp());
			s = mapper.writeValueAsString(r);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	








	public static String saveusernotes(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		RequestVO vo = ObjMapper.toRequestObj(json);
		ResponseVO r = new ResponseVO();
		ObjectMapper mapper = new ObjectMapper();
		try {
			//Validate user
			Token u = AuthorizeToken.authenticate(vo);
			DataVO m = DataVO.toDataVO(vo);
			int noteId= NotesAgent.addNote(m.getString("NOTE"),m.getInt("LKUP_NOTES_TYPE_ID"), Operator.toInt(vo.getId()),u.getId(), u.getIp());
			if(noteId>0)
			r.setMessagecode("cs200");
			
			s = mapper.writeValueAsString(r);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return s;
	}
	

	public static String deleteNotes(HttpServletRequest request, HttpServletResponse response, String json) {
		String res = "";
		ResponseVO r = new ResponseVO();
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);
		ArrayList<MessageVO> msgs = new ArrayList<MessageVO>();
		boolean b = NotesAgent.deleteNotesRef(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()), u.getId());
	    NotesAgent.deleteNotes(vo.getGrouptype(), Operator.toInt(vo.getId()), u.getId());
		if (b) {
			r.setMessagecode("cs200");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), vo.getGrouptype());
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





}















