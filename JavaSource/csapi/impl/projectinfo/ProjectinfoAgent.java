package csapi.impl.projectinfo;

import alain.core.db.Sage;
import alain.core.utils.Operator;
import csapi.search.GlobalSearch;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csshared.vo.ResponseVO;

public class ProjectinfoAgent {

	public static ResponseVO add(String type, int typeid, int id, String ptype, String btype, String buse, String cuse, String pextra, String bextra, String buextra, String cextraex, String cextrapr, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		String command ;
		Sage db = new Sage();
		command = ProjectinfoSQL.details(type, typeid, 0, null);
		if (db.query(command) && db.next()) {
			command = ProjectinfoSQL.update(db.getInt("ID"), ptype, btype, buse, cuse, pextra, bextra, buextra, cextraex, cextrapr, userid, ip);
			db.update(command);
			r.setMessagecode("cs200");
			CsReflect.addHistory(type, typeid, "projectinfo", id, "add");
			CsDeleteCache.deleteCache(type, typeid, "projectinfo");
		} else {
			command = ProjectinfoSQL.add(ptype, btype, buse, cuse, pextra, bextra, buextra, cextraex, cextrapr, userid, ip);
			if (Operator.hasValue(command)) {
				if (db.query(command) && db.next()) {
					id = db.getInt("ID");
					command = ProjectinfoSQL.addRef(type, typeid, id, userid, ip);
					if (db.update(command)) {
						r.setMessagecode("cs200");
						CsReflect.addHistory(type, typeid, "projectinfo", id, "add");
						CsDeleteCache.deleteCache(type, typeid, "projectinfo");
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
