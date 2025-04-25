package csapi.impl.incident;

import alain.core.db.Sage;
import alain.core.utils.Operator;
import csapi.search.GlobalSearch;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csshared.vo.ResponseVO;

public class IncidentAgent {

	public static ResponseVO add(String type, int typeid, int id, String inctype, String status, String description, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		String command ;
		Sage db = new Sage();
		if (id > 0) {
			command = IncidentSQL.update(id, inctype, status, description, userid, ip);
			db.update(command);
			r.setMessagecode("cs200");
			CsReflect.addHistory(type, typeid, "incident", id, "add");
			CsDeleteCache.deleteCache(type, typeid, "incident");
		} else {
			command = IncidentSQL.add(inctype, status, description, userid, ip);
			if (Operator.hasValue(command)) {
				if (db.query(command) && db.next()) {
					id = db.getInt("ID");
					command = IncidentSQL.addRef(type, typeid, id, userid, ip);
					if (db.update(command)) {
						r.setMessagecode("cs200");
						CsReflect.addHistory(type, typeid, "incident", id, "add");
						CsDeleteCache.deleteCache(type, typeid, "incident");
					}
					else {
						r.setMessagecode("cs500");
						r.addError("Error occured that prevented the reference to be added to this note.");
					}
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Error occured that prevented the note from being saved.");
				}
				db.clear();
			}
			else {
				r.setMessagecode("cs412");
				r.addError("Please make sure that values have been entered for all required fields");
			}
		}
		db.clear();
		GlobalSearch.index(GlobalSearch.LOAD_INITIAL_DELTA);
		return r;
	}
}
