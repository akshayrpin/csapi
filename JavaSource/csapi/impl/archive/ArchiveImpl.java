package csapi.impl.archive;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeVO;


public class ArchiveImpl {

	public static final String LOG_CLASS= "ArchiveImpl.java  : ";

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			int id =Operator.toInt(vo.getId());
			
			ObjGroupVO g = ArchiveAgent.summary(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()), "",new Token(),vo.getStart(),vo.getEnd());
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


}















