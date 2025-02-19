package csapi.impl.notes;

import java.util.LinkedHashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.communications.CommunicationsAgent;
import csapi.impl.general.GeneralSQL;
import csapi.impl.users.UsersAgent;
import csapi.impl.users.UsersSQL;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.Email;
import csshared.utils.CsConfig;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
public class NotesAgent {

	
	public static ObjGroupVO printNotes(RequestVO r){
		Token u = AuthorizeToken.authenticate(r);
		ObjGroupVO g = new ObjGroupVO();
		String command = NotesSQL.summary(r.getType(), r.getTypeid(), 0, u);
		Sage db = new Sage();
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		db.query(command);
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(outstyle).append("\">NOTES </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> DATE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> NOTE </td>");
	
		sb.append("	</tr> \n");
		
		
		while(db.next()){
			result = true;
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("CREATED_DATE")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("NOTE")).append("</td>");
			sb.append("	</tr> \n");
			
		}
		sb.append("</tbody>");
		sb.append("</table> \n");
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_notes");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}

	public static ResponseVO add(String type, int typeid, int notetype, String note, String recipient, String subject, String data, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		String command = NotesSQL.add(notetype, note, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				int noteid = db.getInt("ID");
				command = NotesSQL.addRef(type, typeid, noteid, userid, ip);
				if (db.update(command)) {
					if (Operator.hasValue(recipient)) {
						command = NotesSQL.type(notetype);
						if (db.query(command) && db.next()) {
							String ntfy = db.getString("NOTIFY");
							if (Operator.equalsIgnoreCase(ntfy, "Y")) {

								LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
								if (Operator.equalsIgnoreCase(data, "Y") && Operator.equalsIgnoreCase(type, "activity")) {
									m = ActivityAgent.getContentData(typeid);
								}

								String[] recipients = Operator.split(recipient, "|");
								for (int i=0; i<recipients.length; i++) {
									int rec = Operator.toInt(recipients[i]);
									if (rec > 0) {
										command = UsersSQL.getUsers(rec);
										if (db.query(command) && db.next()) {
											String email = db.getString("EMAIL");
											if (!Operator.hasValue(email) || !Operator.isEmail(email)) {
												email = db.getString("USERNAME");
											}
											if (Operator.hasValue(email) && Operator.isEmail(email)) {
												String name = db.getString("NAME");
												String content = Email.genericTemplate(name, note, m);
												CommunicationsAgent.email("notes", noteid, email, rec, subject, content, userid, ip);
											}
										}
									}
								}
							}
						}
					}
					r.setMessagecode("cs200");
					CsReflect.addHistory(type, typeid, "notes", noteid, "add");
					CsDeleteCache.deleteCache(type, typeid, "notes");
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
		return r;
	}

	public static int addNote(String note,int notetypeid, int usersid, int userid, String ip) {
		int r = -1;
		if (Operator.hasValue(note) && !Operator.equalsIgnoreCase(note, CsConfig.SKIPVALUE)) {
			Timekeeper d = new Timekeeper();
			String command = NotesSQL.addUserNote(note, notetypeid, userid, ip, d);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
				if (r > 0) {
					usersid= UsersAgent.getRefUser(usersid);
					command = NotesSQL.addNoteRef("USERS", usersid, r, userid, ip, d);
					db.update(command);
				}
			}
			db.clear();
		}
		return r;
	}
	public static boolean deleteNotesRef(String type, int typeid, String grouptype, int id, int userid) {
		boolean b = false;
		String command =NotesSQL.DeleteRefNote(id, userid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			b = db.update(command);
			db.clear();
		}
		return b;
	}

	public static boolean deleteNotes(String type, int id, int userid) {
		boolean b = false;
		String command = NotesSQL.DeleteNote(id, userid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			b = db.update(command);
			db.clear();
		}
		return b;
	}
}
















