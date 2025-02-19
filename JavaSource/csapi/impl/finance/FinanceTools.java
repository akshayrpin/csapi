package csapi.impl.finance;

import alain.core.security.Token;
import csshared.vo.ToolVO;
import csshared.vo.ToolsVO;

public class FinanceTools {

	public static ToolsVO tools(String entity, int entityid, String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity(entity);
		r.setEntityid(entityid);

		ToolVO[] t = new ToolVO[1];
		
		ToolVO vo = new ToolVO();
		vo.setTool("print");
		vo.setImage("print");
		vo.setTitle("Print");
		
		t[0] = vo;


		r.setTools(t);

		return r;
	}
	
	
	public static ToolsVO fees(String entity, int entityid, String type, int typeid, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity("deposit");
		r.setEntityid(entityid);
		

		ToolVO[] t = new ToolVO[1];
		
		ToolVO vo = new ToolVO();
		vo.setTool("deposit");
		vo.setImage("deposit");
		vo.setTitle("Deposit");
		vo.setAction("depositpayment");
		
		t[0] = vo;


		r.setTools(t);

		return r;
	}

}
