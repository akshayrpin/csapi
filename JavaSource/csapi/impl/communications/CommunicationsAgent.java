package csapi.impl.communications;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.utils.Email;
import csshared.vo.SubObjVO;

public class CommunicationsAgent {


	public static SubObjVO[] getNotifications(String type, int typeid) {
		String command = CommunicationsSQL.getNotifications(type, typeid);
		return Choices.getChoices(command);
	}

	public static SubObjVO[] getNotification(int notifyid) {
		String command = CommunicationsSQL.getNotification(notifyid);
		return Choices.getChoices(command);
	}
	
	public static boolean save(String type, int typeid, String recipient, int usersid, String subject, String content, int creator, String ip) {
		return save(type, typeid, recipient, usersid, subject, content, creator, ip, "");
	}

	public static boolean save(String type, int typeid, String recipient, int usersid, String subject, String content, int creator, String ip,String source) {
		boolean r = false;
		String command = CommunicationsSQL.addNotify(recipient, usersid, subject, content, creator, ip,source);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				int nid = db.getInt("ID");
				command = CommunicationsSQL.addRef(type, typeid, nid, creator, ip);
				r = db.update(command);
			}
			db.clear();
		}
		return r;
	}

	public static boolean email(String type, int typeid, String recipient, int usersid, String subject, String content, int creator, String ip) {
		boolean r = false;
		Logger.highlight("SENDING EMAIL ###############"+recipient);
		if (Email.send(recipient, subject, content)) {
			r = true;
			save(type, typeid, recipient, usersid, subject, content, creator, ip);
		}
		return r;
	}











}












