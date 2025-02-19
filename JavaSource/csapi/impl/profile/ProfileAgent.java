package csapi.impl.profile;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.security.AuthorizeToken;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class ProfileAgent {

	public static ResponseVO save(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		r.setMessagecode("cs200");
		Token u = AuthorizeToken.authenticate(vo);
		DataVO m = DataVO.toDataVO(vo);
		String username = u.getUsername();
		String fname = m.get("FIRST_NAME");
		String mname = m.get("MIDDLE_NAME");
		String lname = m.get("LAST_NAME");
		String email = m.get("EMAIL");
		String address = m.get("ADDRESS");
		String city = m.get("CITY");
		String state = m.get("STATE");
		String zip = m.get("ZIP");
		String wphone = m.get("PHONE_WORK");
		String hphone = m.get("PHONE_HOME");
		String cphone = m.get("PHONE_CELL");
		String fax = m.get("FAX");
		if (Operator.hasValue(username)) {
			Sage db = new Sage();
			String command = "";
			command = ProfileSQL.updateUser(username, fname, mname, lname, email);
			if (db.update(command)) {
				command = ProfileSQL.updatePeople(username, address, city, state, zip, wphone, hphone, cphone, fax);
				db.update(command);
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Could not update user information.");
			}
			db.clear();
		}
		else {
			r.setMessagecode("cs400");
			r.addError("Username not available. Please make sure that you are logged in.");
		}
		return r;
	}


}
































