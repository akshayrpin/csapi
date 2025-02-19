package csapi.utils.objtools;

import alain.core.security.Token;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;

public class Tools {

	/**
	 * @author aromero
	 */
	public static ToolsVO getTools(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		TypeVO t = Types.getType(vo);
		return getTools(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), t.getHold(), u);
	}

	public static ToolsVO getTools(String entity, int entityid, String type, int typeid, String hold, Token u) {
		ToolsVO t = new ToolsVO();
		t = CsReflect.getTools(entity, entityid, type, typeid, hold, u);
		return t;
	}

	
	
}












