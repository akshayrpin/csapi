package csapi.impl.peoplesummary;

import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import alain.core.db.Sage;
import alain.core.utils.Operator;


public class PeoplesummaryAgent {

	public static boolean save(String actids, String refusersids, int userid, String ip) {
		boolean r = false;
		String command = "";
		String[] a = Operator.split(actids, "|");
		String[] u = Operator.split(refusersids, "|");
		Sage db = new Sage();
		for (int ax=0; ax<a.length; ax++) {
			int actid = Operator.toInt(a[ax]);
			if (actid > 0) {
				for (int ux=0; ux<u.length; ux++) {
					int refusersid = Operator.toInt(u[ux]);
					command = PeoplesummarySQL.find(actid, refusersid);
					if (db.query(command) && db.next() && db.getInt("ID") > 0) {
					}
					else {
						command = PeoplesummarySQL.save(actid, refusersid, userid, ip);
						if (db.update(command)) {
							r = true;
							CsDeleteCache.deleteCache("activity", actid, "peoplesummary");
						}
					}
				}
			}
		}
		db.clear();
		return r;
	}

	public static boolean delete(int refid, int userid, String ip) {
		boolean r = false;
		String command = PeoplesummarySQL.delete(refid, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}




}
















