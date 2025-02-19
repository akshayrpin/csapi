package csapi.impl.library;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.security.AuthorizeToken;
import csshared.vo.DataVO;
import csshared.vo.LibraryVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class LibraryAgent {

	public static final String LOG_CLASS= "LibraryAgent.java  : ";

	public static String getSystemGeneratedActivity_id(RequestVO r){
		return Operator.toString(r.getTypeid());
	}
	
	public static ObjGroupVO printLibrary(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		String command = LibrarySQL.summary(r.getType(), r.getTypeid(), 0);
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
		sb.append("	<td style=\"").append(outstyle).append("\">LIBRARY </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> DATE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> LIBRARY </td>");
	
		sb.append("	</tr> \n");
		
		
		while(db.next()){
			result = true;
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("CREATED_DATE")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("SHORT_TEXT")).append("</td>");
			sb.append("	</tr> \n");
			
		}
		sb.append("</tbody>");
		sb.append("</table> \n");
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_library");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}

	public static LibraryVO[] group(int groupid, String type, int typeid) {
		Logger.highlight(type);
		Logger.highlight(typeid);
		LibraryVO[] l = new LibraryVO[0];
		String command = LibrarySQL.getGroup(groupid, type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				int count = 0;
				l = new LibraryVO[db.size()];
				while (db.next()) {
					LibraryVO vo = new LibraryVO();
					vo.setId(db.getInt("ID"));
					vo.setGroupid(db.getInt("LIBRARY_GROUP_ID"));
					vo.setTitle(db.getString("TITLE"));
					vo.setText(db.getString("TXT"));
					vo.setCode(db.getString("LIBRARY_CODE"));
					vo.setOrder(db.getInt("ORDR"));
					vo.setInspectable(db.getString("INSPECTABLE"));
					vo.setWarning(db.getString("WARNING"));
					vo.setComplete(db.getString("COMPLETE"));
					vo.setRequired(db.getString("REQUIRED"));
					l[count] = vo;
					count++;
				}
			}
			db.clear();
		}
		return l;
	}

	public static ResponseVO delete(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		String type = vo.getType();
		if (id > 0 && Operator.hasValue(type)) {
			String command = LibrarySQL.delete(id, type, u.getId(), u.getIp());
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.update(command)) {
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("Error occured while deleting the record from the database");
				}
				db.clear();
			}
		}
		return r;
	}

	public static ResponseVO complete(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		String type = vo.getType();
		if (id > 0 && Operator.hasValue(type)) {
			String command = LibrarySQL.complete(id, type, u.getId(), u.getIp());
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.update(command)) {
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("A database error occured");
				}
				db.clear();
			}
		}
		return r;
	}

	public static ResponseVO incomplete(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		String type = vo.getType();
		if (id > 0 && Operator.hasValue(type)) {
			String command = LibrarySQL.incomplete(id, type, u.getId(), u.getIp());
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.update(command)) {
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("A database error occured");
				}
				db.clear();
			}
		}
		return r;
	}

	public static ResponseVO save(RequestVO vo) {
		int id = Operator.toInt(vo.getId());
		if (id < 1) { return add(vo); }
		else { return edit(vo); }
	}

	public static ResponseVO edit(RequestVO vo) {
		Token u = AuthorizeToken.authenticate(vo);	
		ResponseVO r = new ResponseVO();
		DataVO m = DataVO.toDataVO(vo);
		String command = LibrarySQL.edit(Operator.toInt(vo.getId()), vo.getType(), m.getInt("LIBRARY_ID"), m.getInt("GROUP"), m.getString("TITLE"), m.getString("TXT"), m.getString("LIBRARY_CODE"), m.getString("INSPECTABLE"), m.getString("WARNING"), m.getString("COMPLETE"), m.getString("REQUIRED"), u.getId(), u.getIp());
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.update(command)) {
				r.setMessagecode("cs200");
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Error occured while editing in the database");
			}
			db.clear();
		}
		return r;
	}

	public static ResponseVO add(RequestVO vo) {
		Token u = AuthorizeToken.authenticate(vo);	
		DataVO m = DataVO.toDataVO(vo);
		return add(vo.getType(), vo.getTypeid(), m.getInt("LIBRARY_ID"), m.getInt("GROUP"), m.getString("TITLE"), m.getString("TXT"), m.getString("LIBRARY_CODE"), m.getString("INSPECTABLE"), m.getString("WARNING"), m.getString("COMPLETE"), m.getString("REQUIRED"), u.getId(), u.getIp());
	}

	public static ResponseVO add(String type, int typeid, int libid, int libgroupid, String title, String text, String libcode, String insp, String warning, String comp, String req, int userid, String ip) {
		ResponseVO r = new ResponseVO();
		String command = LibrarySQL.add(type, typeid, libid, libgroupid, title, text, libcode, insp, warning, comp, req, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.update(command)) {
				r.setMessagecode("cs200");
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Error occured while adding to the database");
			}
			db.clear();
		}
		return r;
	}

	public static boolean autoAdd(String type, int typeid, int userid, String ip) {
		boolean r = false;
		String command = LibrarySQL.add(type, typeid, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}
	
	
	
	public static ResponseVO multiAdd(String type, int typeid, String libraryIds,int userid, String ip) {
		ResponseVO r = new ResponseVO();
		
		String command = LibrarySQL.addMulti(type, typeid,libraryIds, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.update(command)) {
				r.setMessagecode("cs200");
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Error occured while adding to the database");
			}
			db.clear();
		}
		return r;
	}









}



























