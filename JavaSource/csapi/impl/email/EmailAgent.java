package csapi.impl.email;

import alain.core.db.Sage;
import alain.core.security.Token;
import csapi.utils.objtools.Types;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeVO;


public class EmailAgent {

	public static TypeVO getDetails(RequestVO r) {
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO t = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);
		
		ObjGroupVO[] groups = new ObjGroupVO[1];
		ObjGroupVO g = new ObjGroupVO();
		if(r.getType().equals("payment")){
			g.setObj(getPayee(r));
		}else {
			g.setObj(getEmails(r));
		}
		groups[0]= g;
		
		t.setGroups(groups);
		
		
		
		
		return t;
		
		
	}
	
	
	
	public static ObjVO[] getEmails(RequestVO r){
		ObjVO[] va = new ObjVO[0];
		
		Sage db = new Sage();
		String command = EmailSQL.getPeople(r.getType(), r.getTypeid());
		db.query(command);
		va = new ObjVO[db.size()];
		int c =0;
		while(db.next()){
			ObjVO v = new ObjVO();
			v.setFieldid(db.getString("EMAIL"));
			v.setLabel(db.getString("TEXT"));
			va[c] = v;
			c++;
		}
		
		
		
		db.clear();
		
		return va;
		
		
	}
	
	public static ObjVO[] getPayee(RequestVO r){
		ObjVO[] va = new ObjVO[0];
		
		Sage db = new Sage();
		String command = EmailSQL.getPayee(r.getTypeid());
		db.query(command);
		va = new ObjVO[db.size()];
		int c =0;
		while(db.next()){
			ObjVO v = new ObjVO();
			v.setFieldid(db.getString("EMAIL"));
			v.setLabel(db.getString("TEXT"));
			va[c] = v;
			c++;
		}
		
		
		
		db.clear();
		
		return va;
		
		
	}
	


}
















