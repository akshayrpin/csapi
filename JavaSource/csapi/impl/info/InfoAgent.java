package csapi.impl.info;

import alain.core.db.Sage;
import alain.core.security.Token;


public class InfoAgent {

	public static String content(String ctype, Token u) {
		String s = "";
		Sage db = new Sage();
		String command = InfoSQL.getContent(ctype, u.isStaff());
		if (db.query(command) && db.next()) {
			s = db.getString("CONTENT");
		}
		db.clear();
		return s;
	}

	public static boolean hasContent(String ctype) {
		boolean s = false;
		Sage db = new Sage();
		String command = InfoSQL.getContent(ctype, false);
		if (db.query(command) && db.next()) {
			s = true;
		}
		db.clear();
		return s;
	}


}















