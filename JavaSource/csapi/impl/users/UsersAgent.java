package csapi.impl.users;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import alain.core.db.Sage;
import alain.core.security.AuthenticateAgent;
import alain.core.security.DatasourceInfo;
import alain.core.security.OauthUtils;
import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.admin.AdminMap;
import csapi.impl.people.PeopleAgent;
import csapi.impl.people.PeopleSQL;
import csapi.impl.team.TeamAgent;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csshared.vo.DataVO;
import csshared.vo.PeopleVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.StaffVO;
import csshared.vo.SubObjVO;
import csshared.vo.UserList;
import csshared.vo.UserVO;

public class UsersAgent {

	public static String solrurl = "http://10.14.6.19:8983/solr/user_core/";
	public static String searchurl = Config.rooturl()+"/csapi/rest/user/search";

	public static SubObjVO[] search(String q, int utype) {
		return search(q, utype, 0, 10000);
	}

	public static SubObjVO[] search(String q, int utype, int start, int end) {
		SubObjVO[] r = new SubObjVO[0];
		/*
		try {
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			SolrQuery query = new SolrQuery();
			query.setQuery("alluser:"+q);
			query.setStart(start);
			query.setRows(end);
			//query.setParam("sort", "usergroup asc, username asc");
			query.setParam("defType", "edismax");
			query.setParam("mm", "100");
			query.setParam("wt", "json");
			query.setParam("indent", "true");
			
			if(utype>0){
				query.setParam("fq", "usertypeid:"+utype);
			}
			//query.setParam("fq", "userstypeid:"+utype);
			QueryResponse response = server.query(query);
			SolrDocumentList documents = response.getResults();
			StringBuilder sb = new StringBuilder();

			int dsize = documents.size();
			r = new SubObjVO[dsize];
			for (int i=0; i < dsize; i++) {
				SolrDocument doc = documents.get(i);
				SubObjVO vo = new SubObjVO();

				String id = "";
				try { id = (String) doc.getFieldValue("id"); } catch (Exception e) { }

				int usersgroupid = -1;
				try { usersgroupid = (int) doc.getFieldValue("usersgroupid"); } catch (Exception e) { }

				int usersid = -1;
				try { usersid = (int) doc.getFieldValue("usersid"); } catch (Exception e) { }

				int userstypeid = -1;
				try { userstypeid = (int) doc.getFieldValue("userstypeid"); } catch (Exception e) { }

				int peopleid = -1;
				try { peopleid = (int) doc.getFieldValue("peopleid"); } catch (Exception e) { }

				int lkuppeoplelicensetypeid = -1;
				try { lkuppeoplelicensetypeid = (int) doc.getFieldValue("lkuppeoplelicensetypeid"); } catch (Exception e) { }

				String username = "";
				try { username = (String) doc.getFieldValue("username"); } catch (Exception e) { }
				vo.setData("USERNAME", username);

				String email = "";
				try { email = (String) doc.getFieldValue("email"); } catch (Exception e) { }
				vo.setData("EMAIL", email);

				String type = "";
				try { type = (String) doc.getFieldValue("type"); } catch (Exception e) { }
				vo.setData("TYPE", type);
				vo.setDescription(type);

				String usergroup = "";
				try { usergroup = (String) doc.getFieldValue("usergroup"); } catch (Exception e) { }

				String name = "";
				try { name = (String) doc.getFieldValue("name"); } catch (Exception e) { }
				vo.setData("NAME", name);

				String address = "";
				try { address = (String) doc.getFieldValue("address"); } catch (Exception e) { }
				vo.setData("ADDRESS", address);

				String city = "";
				try { city = (String) doc.getFieldValue("city"); } catch (Exception e) { }

				String state = "";
				try { state = (String) doc.getFieldValue("state"); } catch (Exception e) { }

				String zip = "";
				try { zip = (String) doc.getFieldValue("zip"); } catch (Exception e) { }

				String phonework = "";
				try { phonework = (String) doc.getFieldValue("phonework"); } catch (Exception e) { }
				vo.setData("PHONEWORK", phonework);

				String phonecell = "";
				try { phonecell = (String) doc.getFieldValue("phonecell"); } catch (Exception e) { }
				vo.setData("PHONECELL", phonecell);

				String fax = "";
				try { fax = (String) doc.getFieldValue("fax"); } catch (Exception e) { }

				String licensetype = "";
				try { licensetype = (String) doc.getFieldValue("licensetype"); } catch (Exception e) { }
				vo.setData("LICENSETYPE", licensetype);

				String licnum = "";
				try { licnum = (String) doc.getFieldValue("licnum"); } catch (Exception e) { }
				vo.setData("LICNUM", licnum);

				String licexpirationdate = "";
				try { licexpirationdate = (String) doc.getFieldValue("licexpirationdate"); } catch (Exception e) { }
				vo.setData("LICEXP", licexpirationdate);


				vo.setId(usersid);
				vo.setValue(Operator.toString(usersid));
				if (Operator.hasValue(name)) {
					StringBuilder txt = new StringBuilder();
					txt.append(name);
					if (Operator.hasValue(username)) {
						txt.append(" (").append(username).append(")");
					}
					else if (Operator.hasValue(email)) {
						txt.append(" (").append(email).append(")");
					}
					vo.setText(txt.toString());
				}
				else if (Operator.hasValue(usergroup)) {
					vo.setText(usergroup);
				}

				sb = new StringBuilder();
				sb.append("<div class=\"people_result\">\n");

				if (Operator.hasValue(type)) {
					sb.append("<span class=\"people_result people_type\">").append(type).append("</span>\n");
				}
				if (Operator.hasValue(usergroup)) {
					sb.append("<span class=\"people_result people_group\">").append(usergroup).append("</span>\n");
				}
				if (Operator.hasValue(name)) {
					sb.append("<span class=\"people_result people_name\">").append(name).append("</span>\n");
				}
				if (Operator.hasValue(username)) {
					sb.append("<span class=\"people_result people_username\">").append(username).append("</span>\n");
				}
				if (Operator.hasValue(email) && !Operator.equalsIgnoreCase(username, email)) {
					sb.append("<span class=\"people_result people_email\">").append(email).append("</span>\n");
				}
				if (Operator.hasValue(address)) {
					boolean ae = false;
					sb.append("<span class=\"people_result people_address\">").append(address);
					if (Operator.hasValue(city)) {
						sb.append("<br/>").append(city);
						ae = true;
					}
					if (Operator.hasValue(state)) {
						if (ae) { sb.append(","); }
						sb.append(" ").append(state);
						ae = true;
					}
					if (Operator.hasValue(zip)) {
						if (ae) { sb.append(","); }
						sb.append(" ").append(zip);
						ae = true;
					}
					sb.append("</span>\n");
				}
				if (Operator.hasValue(licnum)) {
					sb.append("<span class=\"people_result people_lic\">LIC#: ").append(licnum).append(" (").append(licensetype).append(")</span>\n");
				}
				if (Operator.hasValue(licnum)) {
					sb.append("<span class=\"people_result people_lic\">EXP: ").append(licnum).append("</span>\n");
				}
				if (Operator.hasValue(phonework)) {
					sb.append("<span class=\"people_result people_phone\">WORK: ").append(phonework).append("</span>\n");
				}
				if (Operator.hasValue(phonecell)) {
					sb.append("<span class=\"people_result people_phone\">CELL: ").append(phonecell).append("</span>\n");
				}
				if (Operator.hasValue(fax)) {
					sb.append("<span class=\"people_result people_phone\">FAX: ").append(fax).append("</span>\n");
				}
				sb.append("</div>\n");

				String h = sb.toString();
				vo.setHtml(h);

				r[i] = vo;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		*/
		return r;
	}

	public static boolean deleteUser(String type, int typeid, int userid, String ip) {
		boolean r = false;
		String command = UsersSQL.deleteUser(type, typeid, userid, ip);
		Sage db = new Sage();
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean updateUsers(String type, int typeid, String keepcurrent, String addnew, int createdby, String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(keepcurrent).append("|").append(addnew);
		String[] u = Operator.split(sb.toString(), "|");
		return updateUsers(type, typeid, u, createdby, ip);
	}

	public static void updateUsers(String type, int typeid, String users, int createdby, String ip) {
		String[] u = Operator.split(users, "|");
		updateUsers(type, typeid, u, createdby, ip);
	}

	public static boolean updateUsers(String type, int typeid, String[] users, int createdby, String ip) {
		boolean r = false;
		String command = UsersSQL.deleteUser(type, typeid, createdby, ip);
		Sage db = new Sage();
		db.update(command);
		if (users.length > 0) {
			for (int i=0; i<users.length; i++) {
				String user = users[i];
				int userid = Operator.toInt(user);
				if (userid > 0) {
					command = UsersSQL.addUser(type, typeid, userid, createdby, ip);
					if (db.update(command)) {
						r = true;
					}
				}
			}
		}
		else {
			r = true;
		}
		db.clear();
		return r;
	}

	public static boolean addUser(String type, int typeid, String users, int createdby, String ip) {
		String[] u = Operator.split(users, "|");
		return addUser(type, typeid, u, createdby, ip);
	}

	public static boolean addUser(String type, int typeid, String[] users, int createdby, String ip) {
		boolean r = false;
		String command = "";
		Sage db = new Sage();
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			int userid = Operator.toInt(user);
			if (userid > 0) {
				command = UsersSQL.addUser(type, typeid, userid, createdby, ip);
				if (db.update(command)) {
					r = true;
				}
			}
		}
		db.clear();
		return r;
	}

	public static boolean saveProfile(String username, String fname, String mname, String lname, String email, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip,String notify) {
		boolean r = false;
		if (Operator.hasValue(username)) {
			String command = UsersSQL.getUsername(username);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int userid = db.getInt("ID");
					if (userid > 0) {
						int activeusersid = merge(userid, updater, ip);
						dbUpdateUser(activeusersid, fname, mname, lname, email, updater, ip,notify);
						command = PeopleSQL.getPeople(activeusersid);
						if (db.query(command) && db.next()) {
							int peopleid = db.getInt("ID");
							r = PeopleAgent.dbUpdatePeople(activeusersid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip);
						}
						else {
							int ri = PeopleAgent.dbAddPeople(activeusersid, address, city, state, zip, wphone, cphone, hphone, fax, updater, ip);
							r = ri > 0;
						}
					}
				}
				else {
					command = UsersSQL.addUsers(username, fname, mname, lname, email, updater, ip);
					if (db.query(command) && db.next()) {
						int activeusersid = db.getInt("ID");
						if (activeusersid > 0) {
							int ri = PeopleAgent.dbAddPeople(activeusersid, address, city, state, zip, wphone, cphone, hphone, fax, updater, ip);
							r = ri > 0;
						}
					}
					
				}
				db.clear();
			}
		}
		return r;
	}
	
	
	public static boolean saveUser(Cartographer map){
		boolean result = false;
		
		boolean check = true;
		//user
		String uTable = Table.USERSTABLE;
		MapSet u = AdminMap.getCommon(map);
		u.add("ID", map.getInt("ID"));
		u.add("USERNAME", map.getString("USERNAME"));
		u.add("EMAIL", map.getString("EMAIL"));
		u.add("FIRST_NAME", map.getString("FIRST_NAME"));
		u.add("MI", map.getString("MI"));
		u.add("LAST_NAME", map.getString("LAST_NAME"));
		
		String oa = map.getString("NOTIFY","N");
		if(Operator.equalsIgnoreCase(oa, "on")){	oa = "Y";}
		u.add("NOTIFY", oa);
		//people
		String pTable = Table.PEOPLETABLE;
		MapSet p = AdminMap.getCommon(map);
		
		String o = map.getString("ENABLE_PEOPLE","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		p.add("ENABLE_PEOPLE", o);
		
		if(p.getString("ENABLE_PEOPLE").equals("N") && map.getInt("PEOPLE_ID")>0){
			p.add("ACTIVE", "N");
			p.add("ID", map.getInt("PEOPLE_ID"));
		}else {
			p.add("ID", map.getInt("PEOPLE_ID"));
			p.add("USERS_ID", map.getInt("ID"));
			p.add("ADDRESS", map.getString("ADDRESS"));
			p.add("CITY", map.getString("CITY"));
			p.add("STATE", map.getString("STATE"));
			p.add("ZIP", map.getString("ZIP"));
			p.add("ZIP4", map.getString("ZIP4"));
			p.add("PHONE_WORK", map.getString("P_PHONE_WORK"));
			p.add("PHONE_CELL", map.getString("P_PHONE_CELL"));
			p.add("FAX", map.getString("FAX"));
			p.add("COMMENTS", map.getString("COMMENTS"));
		}
		
		//staff
		String sTable = Table.STAFFTABLE;
		MapSet s = AdminMap.getCommon(map);
		o = map.getString("ENABLE_STAFF","N");
		if(Operator.equalsIgnoreCase(o, "on")){	o = "Y";}
		s.add("ENABLE_STAFF", o);
		
		if(s.getString("ENABLE_STAFF").equals("N") && map.getInt("STAFF_ID")>0){
			s.add("ACTIVE", "N");
			s.add("ID", map.getInt("STAFF_ID"));
		}else {
		
			s.add("ID", map.getInt("STAFF_ID"));
			s.add("USERS_ID", map.getInt("ID"));
			s.add("EMPL_NUM", map.getString("EMPL_NUM"));
			s.add("DEPARTMENT_ID", map.getString("DEPARTMENT_ID"));
			s.add("TITLE", map.getString("TITLE"));
			s.add("DIVISION", map.getString("DIVISION"));
			s.add("PHONE_WORK", map.getString("PHONE_WORK"));
			s.add("PHONE_CELL", map.getString("PHONE_CELL"));
		}
		
		
		Sage db = new Sage();
		
		int id =0;
		String command = UsersSQL.checkUser(u);
		
		db.query(command);
		if(db.next()){
			id = db.getInt("ID");
			
		}
		
		if(id>0 && u.getInt("ID")!= id){
			check =false;
		}
		
		if(id>0 && u.getInt("ID")==0){
			check = false;
		}
		
		if(check){
			if(u.getInt("ID")>0){
				//result = AdminAgent.updateType(u, uTable);
				result = db.updateMapOnly(uTable, u,  "ID", false,false);
			}else {
				//result = AdminAgent.saveType(u, uTable);
				result = db.insertMapOnly(uTable, u, "ID", false);
				
			}
			
			
			if(map.getInt("PEOPLE_ID")>0){
				//result = AdminAgent.updateType(p, pTable);
				result = db.updateMapOnly(pTable, p,  "ID", false,false);
				
			}else {
				if(!p.getString("ENABLE_PEOPLE").equals("N")){
					//result = AdminAgent.saveType(p, pTable);
					result = db.insertMapOnly(pTable, p, "ID", false);
				}
			}
			
			
			if(map.getInt("STAFF_ID")>0){
				//result = AdminAgent.updateType(s, sTable);
				result = db.updateMapOnly(sTable, s,  "ID", false,false);
			}else {
				if(!s.getString("ENABLE_STAFF").equals("N")){
					//result = AdminAgent.saveType(s, sTable);
					result = db.insertMapOnly(sTable, s, "ID", false);
				}
			}
		}
		db.clear();
		
		return result;
	}
	
	public static int getUser(Cartographer map){
		int id =0;
		MapSet u = AdminMap.getCommon(map);
		//u.add("ID", map.getInt("ID"));
		u.add("USERNAME", map.getString("USERNAME"));
		u.add("EMAIL", map.getString("EMAIL"));
		u.add("FIRST_NAME", map.getString("FIRST_NAME"));
		u.add("MI", map.getString("MI"));
		u.add("LAST_NAME", map.getString("LAST_NAME"));
		String command = UsersSQL.getUser(u);
		Sage db = new Sage();
		db.query(command);
		if(db.next()){
			id = db.getInt("ID");
		}
		db.clear();
		
		return id;
		
	}

	public static UserVO[] getUsers(String type, int typeid) {
		UserList r = new UserList();
		String command = UsersSQL.getUsers(type, typeid);
		Sage db = new Sage();
		if (db.query(command)) {
			while (db.next()) {
				int id = db.getInt("ID");
				UserVO vo = r.getUser(id);
				vo.setId(id);
				vo.setEmail(db.getString("EMAIL"));
				vo.setFirstname(db.getString("FIRST_NAME"));
				vo.setMiddlename(db.getString("MI"));
				vo.setLastname(db.getString("LAST_NAME"));

				int peopleid = db.getInt("PEOPLE_ID");
				if (peopleid > 0) {
					PeopleVO p = vo.getPeople(peopleid);
					p.setId(peopleid);
					p.setUserid(id);
					p.setAddress(db.getString("ADDRESS"));
					p.setCity(db.getString("CITY"));
					p.setState(db.getString("STATE"));
					p.setZip(db.getString("ZIP"));
					p.setZip4(db.getString("ZIP4"));
					p.setWorkphone(db.getString("PHONE_WORK"));
					p.setCell(db.getString("PHONE_CELL"));
					p.setComments(db.getString("COMMENTS"));
					p.setLicense(db.getString("LIC_NUM"));
					p.setLicensetype(db.getString("LICENSE_TYPE"));
					p.setLicenseexpiration(db.getString("LIC_EXPIRATION_DATE"));
					vo.addPeople(p);
				}

				int staffid = db.getInt("STAFF_ID");
				if (staffid > 0) {
					StaffVO s = vo.getStaff(staffid);
					s.setId(staffid);
					s.setUserid(id);
					s.setEmplnum(db.getInt("EMPL_NUM"));
					s.setDepartment(db.getString("DEPARTMENT"));
					s.setDivision(db.getString("DIVISION"));
					s.setTitle(db.getString("TITLE"));
					s.setWorkphone(db.getString("STAFF_PHONE"));
					s.setCell(db.getString("STAFF_CELL"));
					s.setDepartmentid(db.getInt("DEPARTMENT_ID"));
					vo.addStaff(s);
				}
			}
		}
		db.clear();
		return r.getUsersMap().values().toArray(new UserVO[r.getUsersMap().size()]);
	}

	public static int saveRefUser(int userid, int usertypeid, String licno, String licexp, String genliability, String autoliability, String workcomp, int updater, String ip) {
		int result = -1;
		if (userid > 0 && usertypeid > 0) {
			Sage db = new Sage();
			String command = UsersSQL.getRefUser(userid, usertypeid);
			if (db.query(command) && db.next()) {
				result = db.getInt("REF_USERS_ID");
				String dblicno = db.getString("LIC_NO");

				String dblicexp = getDateValue(db.getString("LIC_EXP_DT"));
				String dbgenliability = getDateValue(db.getString("GEN_LIABILITY_DT"));
				String dbautoliability = getDateValue(db.getString("AUTO_LIABILITY_DT"));
				String dbworkcomp = getDateValue(db.getString("WORK_COMP_EXP_DT"));

				boolean update = false;
				if (!Operator.equalsIgnoreCase(licno, dblicno)) {
					update = true;
				}
				else if (!Operator.equalsIgnoreCase(licexp, dblicexp)) {
					update = true;
				}
				else if (!Operator.equalsIgnoreCase(genliability, dbgenliability)) {
					update = true;
				}
				else if (!Operator.equalsIgnoreCase(autoliability, dbautoliability)) {
					update = true;
				}
				else if (!Operator.equalsIgnoreCase(workcomp, dbworkcomp)) {
					update = true;
				}

				if (update) {
					command = UsersSQL.archiveRefUser(result);
					db.update(command);
					command = UsersSQL.updateRefUser(result, licno, licexp, genliability, autoliability, workcomp, updater, ip);
					db.update(command);
				}
			}
			if (result < 1) {
				command = UsersSQL.addRefUser(userid, usertypeid, licno, licexp, genliability, autoliability, workcomp, updater, ip);
				if (db.query(command) && db.next()) {
					result = db.getInt("ID");
				}
			}
			db.clear();
		}
		return result;
	}

	public static String getDateValue(String d) {
		String r = "";
		if (Operator.hasValue(d)) {
			Timekeeper tk = new Timekeeper();
			tk.setDate(d);
			r = tk.getString("YYYY/MM/DD");
		}
		return r;
	}

	public static int addUser(String username, String fname, String mname, String lname, String email, int creator, String ip) {
		int r = -1;
		String command = "";
		Sage db = new Sage();
		if (!Operator.hasValue(email) && Operator.isEmail(username)) { email = username; }
		if (Operator.hasValue(username)) {
			command = UsersSQL.getUsername(username);
			if (db.query(command) && db.next()) {
				boolean update = false;
				r = db.getInt("ID");
				if (!db.equalsIgnoreCase("FIRST_NAME", fname)) { update = true; }
				if (!db.equalsIgnoreCase("MI", mname)) { update = true; }
				if (!db.equalsIgnoreCase("LAST_NAME", lname)) { update = true; }
				if (!db.equalsIgnoreCase("EMAIL", email)) { update = true; }
				if (!db.equalsIgnoreCase("USERNAME", username)) { update = true; }
				if (update) {
					dbUpdateUser(r, username, fname, mname, lname, email, creator, ip);
				}
			}
			else {
				command = UsersSQL.addUsers(username, fname, mname, lname, email, creator, ip);
				if (db.query(command) && db.next()) {
					r = db.getInt("ID");
				}
			}
		}
		else if (Operator.hasValue(email)) {
			command = UsersSQL.getUserEmail(email);
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
				dbUpdateUser(r, email, fname, mname, lname, email, creator, ip);
			}
			else {
				command = UsersSQL.addUsers(email, fname, mname, lname, email, creator, ip);
				if (db.query(command) && db.next()) {
					r = db.getInt("ID");
				}
			}
		}
		else {
			command = UsersSQL.addUsers(username, fname, mname, lname, email, creator, ip);
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
				StringBuilder sb = new StringBuilder();
				sb.append(r).append("@citysmart");
				dbUpdateUsernameEmail(r, sb.toString(), creator, ip);
			}
		}
		db.clear();
		return r;
	}
	
	public static Token loginOauth(String accessToken, String ip) {
		String username = OauthUtils.getUsernameString("/userinfo",accessToken);
		if (!Operator.hasValue(username)) { return new Token(); }

		int usersid = -1;
		String command = UsersSQL.getUsername(username);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			usersid = db.getInt("ID");
			merge(usersid, usersid, ip);
			//cart(usersid);
			db.update(ActivitySQL.deactiveHours());
			
		}

		//Token t = AuthenticateAgent.loginOauth(accessToken,ip);
		Token t = new Token();
		t.setUsername(username);
		t.setId(usersid);
		if (Operator.hasValue(t.getUsername())) {
			boolean empty = true;
			StringBuilder sb = new StringBuilder();
			ArrayList<String> r = new ArrayList<String>();
			ArrayList<String> p = new ArrayList<String>();
			if (t.getEmplnum() > 0) {
				t.setStaff(true);
			}
			else {
				command = UsersSQL.getStaff(username);
				if (db.query(command) && db.next()) {
					t.setStaff(true);
				}
			}
			command = UsersSQL.getUserRoles(username, t.getEmplnum());
			db.query(command);
			while (db.next()) {
				String role = db.getString("ROLE");
				String text = db.getString("TEXT");
				String admin = db.getString("ADMIN");
				String everyone = db.getString("EVERYONE");
				r.add(role);
				if (!empty) { sb.append(","); }
				sb.append(text);
				empty = false;
				if (Operator.equalsIgnoreCase(admin, "Y")) {
					t.setAdmin(true);
					if (!empty) { sb.append(","); }
					sb.append("ADMIN");
				}
				if (Operator.equalsIgnoreCase(everyone, "N")) {
					p.add(role);
				}
			}
			String[] ra = r.toArray(new String[r.size()]);
			String[] pa = p.toArray(new String[p.size()]);
			t.setRoles(ra);
			t.setNonpublicroles(pa);
			HashMap<String, String> rl = new HashMap<String, String>();
			rl.put("ROLES", sb.toString());
			t.setInfo(rl);
		}
		db.clear();
		t.save();
		return t;
	}

	public static Token login(String username, String password, String requestor, String ip) {
		if (!Operator.hasValue(username)) { return new Token(); }

		int usersid = -1;
		String command = UsersSQL.getUsername(username);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			usersid = db.getInt("ID");
			merge(usersid, usersid, ip);
			//cart(usersid);
			db.update(ActivitySQL.deactiveHours());
			
		}

		Token t = AuthenticateAgent.login(username, password, requestor, ip);
		if (Operator.hasValue(t.getUsername()) && t.getId() > 0) {
			boolean empty = true;
			StringBuilder sb = new StringBuilder();
			ArrayList<String> r = new ArrayList<String>();
			ArrayList<String> p = new ArrayList<String>();
			if (t.getEmplnum() > 0) {
				t.setStaff(true);
			}
			else {
				command = UsersSQL.getStaff(username);
				if (db.query(command) && db.next()) {
					t.setStaff(true);
					t.setEmplnum(t.getId());
				}
			}
			Logger.info(t.getEmplnum()+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+t.getId());
			command = UsersSQL.getUserRoles(username, t.getEmplnum());
			db.query(command);
			while (db.next()) {
				String role = db.getString("ROLE");
				String text = db.getString("TEXT");
				String admin = db.getString("ADMIN");
				String everyone = db.getString("EVERYONE");
				r.add(role);
				if (!empty) { sb.append(","); }
				sb.append(text);
				empty = false;
				if (Operator.equalsIgnoreCase(admin, "Y")) {
					t.setAdmin(true);
					if (!empty) { sb.append(","); }
					sb.append("ADMIN");
				}
				if (Operator.equalsIgnoreCase(everyone, "N")) {
					p.add(role);
				}
			}
			String[] ra = r.toArray(new String[r.size()]);
			String[] pa = p.toArray(new String[p.size()]);
			t.setRoles(ra);
			t.setNonpublicroles(pa);
			HashMap<String, String> rl = new HashMap<String, String>();
			rl.put("ROLES", sb.toString());
			t.setInfo(rl);
		}
		db.clear();
		t.save();
		return t;
	}

	public static boolean logout(String token, String ip) {
		return Token.delete(token, ip);
	}
	
	public static Token createToken(String username, String ip) {
		String token = Operator.randomString(25);
		return createToken(username, token, ip);
	}

	public static Token createToken(String username, String token, String ip) {
		Token t = new Token();
		t = Token.retrieve(token, ip);
		
		if(Operator.equalsIgnoreCase(Config.getString("security.oauth2.perform"), "N")){
			return t;
		}
		if (t.getId() < 1) {
			if (!Operator.hasValue(username)) {
				 username = OauthUtils.getUsernameString(Config.getString("security.oauth2.userinfo_endpoint"), token);
			}
			t = AuthenticateAgent.getInfo(username, ip);
			Logger.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSS"+username);
			if (Operator.hasValue(t.getUsername())) {
				t.setToken(token);
				t.setUsername(username);
				t.setLoggedin(true);
				t.setIp(ip);
				t.setAuthtype("REST");
    			t.setStrictlogin(true);
				
				boolean empty = true;
				StringBuilder sb = new StringBuilder();
				ArrayList<String> r = new ArrayList<String>();
				ArrayList<String> p = new ArrayList<String>();
				
				String command = "";
				Sage db = new Sage();
				
				command = UsersSQL.getUsername(username);
				
				if (db.query(command) && db.next()) {
					t.setId(db.getInt("ID"));
					
				}
				
			
				
				if (t.getId()>0) {
					int usersid = t.getId();
					merge(usersid, usersid, ip);
					//cart(usersid);
					db.update(ActivitySQL.deactiveHours());
					
				}
				
				
				command = UsersSQL.getStaff(username);
				if (db.query(command) && db.next()) {
					t.setEmplnum(db.getInt("ID"));
					t.setStaff(true);
				}
				
				
				command = UsersSQL.getUserRoles(username, t.getEmplnum());
				db.query(command);
				while (db.next()) {
					String role = db.getString("ROLE");
					String text = db.getString("TEXT");
					String admin = db.getString("ADMIN");
					/*if (!Operator.hasValue(text)) { text = role; }
					r.add(role);
					if (!empty) { sb.append(", "); }
					sb.append(text);
					empty = false;
					if (Operator.equalsIgnoreCase(admin, "Y")) {
						t.setAdmin(true);
						if (!empty) { sb.append(", "); }
						sb.append("ADMIN");
					}*/
					String everyone = db.getString("EVERYONE");
					r.add(role);
					if (!empty) { sb.append(","); }
					sb.append(text);
					empty = false;
					if (Operator.equalsIgnoreCase(admin, "Y")) {
						t.setAdmin(true);
						if (!empty) { sb.append(","); }
						sb.append("ADMIN");
					}
					if (Operator.equalsIgnoreCase(everyone, "N")) {
						p.add(role);
					}
				}
				
				
				
				
				
				
				db.clear();
				String[] ra = r.toArray(new String[r.size()]);
				String[] pa = p.toArray(new String[p.size()]);
				t.setRoles(ra);
				t.setNonpublicroles(pa);

				HashMap<String, String> rl = new HashMap<String, String>();
				rl.put("ROLES", sb.toString());
				t.setInfo(rl);
				
				Timekeeper d = new Timekeeper();
				t.setCreated(Operator.toString(d.FULLDATECODE()));
				t.setLastaccessed(Operator.toString(d.FULLDATECODE()));
				
				
				if (Operator.equalsIgnoreCase(username, Config.adminuser())) {
					t.setAuthtype("ADMIN");
	    			t.setAdmin(true);
	    			t.setStrictlogin(true);
	    		
				}
				
			}
			t.save();
		}
		
		
		if(!Operator.hasValue(t.getToken())){
			String json2 = FileUtil.getString("C:/DL/csfiles/tokens/695cbfa19895c7015a4240d0d50255eb.json");
			
			//t = fromJson(json2);
			
		}

		return t;
	}

	public static boolean isRepresentative(String rep, String pass) {
		boolean r = false;
		String command = UsersSQL.getRepresentative(rep, pass);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			r = db.size() > 0;
			db.clear();
		}
		return r;
	}

	public static Token getToken(String token, String ip) {
		Token u = Token.retrieve(token, ip);
		return u;
	}

	public static HashMap<String, String> onlineUser(String username, int id) {
			String command = UsersSQL.getOnlineUsers(username, id, 0);
			Sage db = new Sage();
			db.query(command);
			HashMap<String, String> hm = new HashMap<String, String>(); 
			if (db.next()) {
				hm.put("ID", Operator.toString(id));
				hm.put("EMAIL",db.getString("EMAIL"));
				hm.put("FIRST_NAME",db.getString("FIRST_NAME"));
				hm.put("MIDDLE_NAME",db.getString("MI"));
				hm.put("LAST_NAME",db.getString("LAST_NAME"));
				hm.put("ADDRESS", db.getString("ADDRESS"));
				hm.put("CITY",db.getString("CITY"));
				hm.put("STATE",db.getString("STATE"));
				hm.put("ZIP",db.getString("ZIP"));
				hm.put("ZIP4",db.getString("ZIP4"));
				hm.put("PHONE_WORK",db.getString("PHONE_WORK"));
				hm.put("PHONE_CELL",db.getString("PHONE_CELL"));
				hm.put("PHONE_HOME",db.getString("PHONE_HOME"));
				hm.put("LKUP_USERS_TYPE_ID",db.getString("LKUP_USERS_TYPE_ID"));
				hm.put("NOTIFY",db.getString("NOTIFY"));
			}
			db.clear();
			return hm;
	}

	public static ResponseVO username(String username) {
		ResponseVO r = new ResponseVO();
		if (Operator.hasValue(username)) {
			String command = UsersSQL.user(username);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int id = db.getInt("ID");
					String fname = db.getString("FIRST_NAME");
					String mname = db.getString("MI");
					String lname = db.getString("LAST_NAME");
					String email = db.getString("EMAIL");
					String address = db.getString("ADDRESS");
					String city = db.getString("CITY");
					String state = db.getString("STATE");
					String zip = db.getString("ZIP");
					String wphone = db.getString("PHONE_WORK");
					String hphone = db.getString("PHONE_HOME");
					String cphone = db.getString("PHONE_CELL");
					String fax = db.getString("FAX");
					String licno = db.getString("LIC_NO");
					String licexp = db.getString("LIC_EXP_DT");
					String buslic = db.getString("BUS_LIC_NO");
					String buslicexp = db.getString("BUS_LIC_EXP_DT");
					String genliability = db.getString("GEN_LIABILITY_DT");
					String autoliability = db.getString("AUTO_LIABILITY_DT");
					String workcompexp = db.getString("WORK_COMP_EXP_DT");
					StringBuilder sb = new StringBuilder();
					sb.append(fname);
					if (Operator.hasValue(mname)) { sb.append(" ").append(mname); }
					if (Operator.hasValue(lname)) { sb.append(" ").append(lname); }
					String name = sb.toString();
					r.setId(id);
					r.addInfo("NAME", name);
					r.addInfo("FIRST_NAME", fname);
					r.addInfo("LAST_NAME", lname);
					r.addInfo("MIDDLE_NAME", mname);
					r.addInfo("EMAIL", email);
					r.addInfo("USERNAME", username);
					r.addInfo("ADDRESS", address);
					r.addInfo("CITY", city);
					r.addInfo("STATE", state);
					r.addInfo("ZIP", zip);
					r.addInfo("PHONE_WORK", wphone);
					r.addInfo("PHONE_CELL", cphone);
					r.addInfo("PHONE_HOME", hphone);
					r.addInfo("FAX", fax);
					r.addInfo("NOTIFY",db.getString("NOTIFY"));
				}
				db.clear();
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Database query not found");
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Username is a required field");
		}
		return r;
	}

	public static ResponseVO email(String emailaddr) {
		ResponseVO r = new ResponseVO();
		if (Operator.hasValue(emailaddr)) {
			String command = UsersSQL.email(emailaddr);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int id = db.getInt("ID");
					String username = db.getString("USERNAME");
					String fname = db.getString("FIRST_NAME");
					String mname = db.getString("MI");
					String lname = db.getString("LAST_NAME");
					String email = db.getString("EMAIL");
					String address = db.getString("ADDRESS");
					String city = db.getString("CITY");
					String state = db.getString("STATE");
					String zip = db.getString("ZIP");
					String wphone = db.getString("PHONE_WORK");
					String hphone = db.getString("PHONE_HOME");
					String cphone = db.getString("PHONE_CELL");
					String fax = db.getString("FAX");
					StringBuilder sb = new StringBuilder();
					sb.append(fname);
					if (Operator.hasValue(mname)) { sb.append(" ").append(mname); }
					if (Operator.hasValue(lname)) { sb.append(" ").append(lname); }
					String name = sb.toString();
					r.setId(id);
					r.addInfo("USERNAME", username);
					r.addInfo("NAME", name);
					r.addInfo("FIRST_NAME", fname);
					r.addInfo("LAST_NAME", lname);
					r.addInfo("MIDDLE_NAME", mname);
					r.addInfo("EMAIL", email);
					r.addInfo("USERNAME", username);
					r.addInfo("ADDRESS", address);
					r.addInfo("CITY", city);
					r.addInfo("STATE", state);
					r.addInfo("ZIP", zip);
					r.addInfo("PHONE_WORK", wphone);
					r.addInfo("PHONE_CELL", cphone);
					r.addInfo("PHONE_HOME", hphone);
					r.addInfo("FAX", fax);
				}
				db.clear();
			}
			else {
				r.setMessagecode("cs500");
				r.addError("Database query not found");
			}
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Username is a required field");
		}
		return r;
	}

	public static ResponseVO select(RequestVO vo) {
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		DataVO m = DataVO.toDataVO(vo);
		return select(id, m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"), m.getString("USERNAME"), m.getString("ADDRESS"), m.getString("CITY"), m.getString("STATE"), m.getString("ZIP"), m.getString("PHONE_WORK"), m.getString("PHONE_CELL"), m.getString("PHONE_HOME"), m.getString("FAX"), u.getId(), u.getIp());
	}

	public static ResponseVO select(int userid, String fname, String mname, String lname, String email, String username, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, int creator, String ip) {
		ResponseVO r = new ResponseVO();
		if (userid < 1) {
			userid = UsersAgent.addUser(username, fname, mname, lname, email, creator, ip);
			PeopleAgent.updatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, "", creator, ip);
		}
		else {
			UsersAgent.dbUpdateUser(userid, username, fname, mname, lname, email, creator, ip);
			PeopleAgent.updatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, "", creator, ip);
		}
		GlobalSearch.index(GlobalSearch.PEOPLE_DELTA);
		GlobalSearch.index(GlobalSearch.USERS_DELTA);
		if (userid > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(fname);
			if (Operator.hasValue(mname)) { sb.append(" ").append(mname); }
			if (Operator.hasValue(lname)) { sb.append(" ").append(lname); }
			String name = sb.toString();
			r.setId(userid);
			r.addInfo("USERS_ID", Operator.toString(userid));
			r.addInfo("NAME", name);
			r.addInfo("FIRST_NAME", fname);
			r.addInfo("LAST_NAME", lname);
			r.addInfo("MIDDLE_NAME", mname);
			r.addInfo("EMAIL", email);
			r.addInfo("USERNAME", username);
			r.addInfo("ADDRESS", address);
			r.addInfo("CITY", city);
			r.addInfo("STATE", state);
			r.addInfo("ZIP", zip);
			r.setMessagecode("cs200");
		}
		else {
			r.setMessagecode("cs500");
			r.addError("A database error occured");
		}
		return r;
	}

	public static int merge(int usersid) {
		return merge(usersid, -1, "");
	}

	public static int merge(int usersid, int updater, String ip) {
		if (usersid < 1) { return -1; }
		int r = -1;
		String username = "";
		String command = UsersSQL.getUsers(usersid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			username = db.getString("USERNAME");
			if (!Operator.hasValue(username)) {
				String email = db.getString("EMAIL");
				if (Operator.hasValue(email)) {
					if (dbUpdateUsername(usersid, email, updater, ip)) {
						username = email;
					}
				}
			}
			if (Operator.hasValue(username)) {
				db.query(command);
				if (db.next()) {
					int activeusersid = db.getInt("ID");
					if (activeusersid > 0) {
						command = UsersSQL.mergeRefUser(activeusersid, username);
						db.update(command);
						command = UsersSQL.mergePayee(activeusersid, username);
						db.update(command);
						command = UsersSQL.mergeRefUserDeposit(activeusersid, username);
						db.update(command);
						command = UsersSQL.mergeUserGroup(activeusersid, username);
						db.update(command);
						command = UsersSQL.mergeAppointmentUsers(activeusersid, username);
						db.update(command);

						int peopleid = -1;
						command = PeopleSQL.getLastPeople(username);
						if (db.query(command) && db.next()) {
							peopleid = db.getInt("ID");
							if (peopleid > 0) {
								PeopleAgent.dbMergeUserPeople(activeusersid, username, updater, ip);
								PeopleAgent.dbDisableDuplicatePeople(peopleid, activeusersid, updater, ip);
							}
						}

						dbDisableDuplicateUsers(activeusersid, updater, ip);
						command = UsersSQL.getRefUserCount(activeusersid);
						db.query(command);
						Sage db2 = new Sage();
						while (db.next()) {
							int count = db.getInt("RECS");
							if (count > 1) {
								int max = db.getInt("MAX_LICENSE_ID");
								if (max < 1) {
									max = db.getInt("MAX_ID");
								}
								if (max > 0) {
									command = UsersSQL.disableDuplicateRefUsers(max);
									db2.update(command);
								}
							}
						}
						db2.clear();
						r = activeusersid;
					}
				}
			}
		}
		db.clear();
		return r;
	}

	public static Token resetUsersPassword(int usersid) {
		Token r = new Token();
		if (usersid < 1) { return r; }
		String command = UsersSQL.getUsers(usersid);
		Sage db = new Sage();
		db.query(command);
		if (db.next()) {
			String username = db.getString("USERNAME");
			if (Operator.hasValue(username)) {
				r = AuthenticateAgent.resetPassword(username, "", "");
			}
		}
		db.clear();
		return r;
	}

	public static Token createAccount(int usersid) {
		Token r = new Token();
		if (usersid < 1) { return r; }
		String command = UsersSQL.getUsers(usersid);
		Sage db = new Sage();
		db.query(command);
		if (db.next()) {
			String username = db.getString("USERNAME");
			if (Operator.hasValue(username)) {
				String email = db.getString("EMAIL");
				String name = db.getString("NAME");
				String fname = db.getString("FIRST_NAME");
				String lname = db.getString("LAST_NAME");
				Logger.highlight(username);
				r = AuthenticateAgent.createAccount(username, email, name, fname, lname, "", "", "", "", "", "", "", "");
			}
		}
		db.clear();
		return r;
	}

	public static boolean hasOnlineAccount(int usersid) {
		boolean r = false;
		String command = UsersSQL.getUsers(usersid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			String username = db.getString("USERNAME");
			if (Operator.hasValue(username)) {
				r = DatasourceInfo.hasUser(username);
			}
		}
		db.clear();
		return r;
	}

	public static Token assistOnlineAccount(int usersid, String reqemail, int updater, String ip) {
		Token u = new Token();
		int activeusersid = merge(usersid, updater, ip);
		boolean h = hasOnlineAccount(activeusersid);
		if (h) {
			u = resetUsersPassword(activeusersid);
			if (u.isLoggedin()) {
				String email = u.getEmail();
				String password = u.getMessage();
				if (Operator.equalsIgnoreCase(reqemail, email)) {
					String reseturl = resetUrl(u.getUsername(), password, "csportal");
					u.setMessage("An account already exists for this user and a password reset link has been emailed to "+reqemail+"\n\nUser must access the link and change their password before the link expires.\n\nReset Password URL: "+reseturl);
					AuthenticateAgent.sendPasswordReset(reqemail, u.getFirstname() + " " + u.getLastname(), u.getUsername(), password, "csportal");
				}
				else {
					String reseturl = resetUrl(u.getUsername(), password, "csportal");
					u.setMessage("An account already exists for this user, however an email has not been sent to the user because the email address on the account is different than the one on file.\n\nReset Password URL: "+reseturl);
				}
			}
			else {
				u.setMessage("Unable to create a temporary password.");
			}
		}
		else {
			u = createAccount(activeusersid);
			if (u.isLoggedin()) {
				String password = u.getMessage();
				String email = u.getEmail();
				if (Operator.equalsIgnoreCase(reqemail, email)) {
					String reseturl = resetUrl(u.getUsername(), password, "csportal");
					u.setMessage("A new account has been created for this user and a password has been emailed to "+reqemail+" that includes a link to reset the password.\n\nUsername: "+u.getUsername()+"\n\nPassword: "+password+"\n\n\nThey may use the login credentials above or go to the provided link to reset their password. Please note the the reset link will expire after 2 hours. User must access the link to change their password before the link expires.\n\nReset Password URL: "+reseturl);
					AuthenticateAgent.sendAccountCreated(reqemail, u.getFirstname() + " " + u.getLastname(), u.getUsername(), password, "csportal");
				}
				else {
					String reseturl = resetUrl(u.getUsername(), password, "csportal");
					u.setMessage("An account already exists for this user, however an email has not been sent to the user because the email address on the account is different than the one on file.\n\nReset Password URL: "+reseturl);
				}
			}
			else {
				u.setMessage("Unable to create a new account.");
			}
		}
		return u;
	}

	public static String resetUrl(String username, String password, String app) {
		StringBuilder sb = new StringBuilder();
		sb.append(Config.reseturl());
		sb.append("?token=").append(password);
		sb.append("&uid=").append(username);
		if (Operator.hasValue(app)) {
			sb.append("&app=").append(app);
		}
		return sb.toString();
	}

	public static Token assistOnlineAccount(String username, String reqemail, int updater, String ip) {
		Token u = new Token();
		int usersid = -1;
		String command = UsersSQL.getUsername(username);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			usersid = db.getInt("ID");
		}
		db.clear();
		if (usersid > 0) { u = assistOnlineAccount(usersid, reqemail, updater, ip); }
		return u;
	}

	public static Token assistRefUsersOnlineAccount(int refusersid, String reqemail, int updater, String ip) {
		Token u = new Token();
		int usersid = -1;
		String username = "";
		String email = "";
		String command = UsersSQL.getRefUser(refusersid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			usersid = db.getInt("USERS_ID");
			username = db.getString("USERNAME");
			email = db.getString("EMAIL");
		}
		db.clear();
		if (!Operator.equalsIgnoreCase(reqemail, email)) {
			u.setMessage("Email address could not be verified.");
		}
		else if (!Operator.hasValue(username) && !Operator.hasValue(email)) {
			u.setMessage("Username and email has not been entered for this user.");
		}
		else if (usersid > 0) { u = assistOnlineAccount(usersid, reqemail, updater, ip); }
		else {
			u.setMessage("Could not find user id.");
		}
		return u;
	}

	public static boolean dbUpdateUser(int userid, String username, String fname, String mname, String lname, String email, int updater, String ip) {
		return dbUpdateUser(userid, username, fname, mname, lname, email, updater, ip, true);
	}

	public static boolean dbUpdateUser(int userid, String username, String fname, String mname, String lname, String email, int updater, String ip, boolean history) {
		boolean r = false;
		String command = "";
		Sage db = new Sage();
		if (history) {
			command = UsersSQL.updateHistory(userid, updater, ip);
			db.update(command);
		}
		command = UsersSQL.updateUsers(userid, username, fname, mname, lname, email, updater, ip);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateUser(int userid, String fname, String mname, String lname, String email, int updater, String ip,String notify) {
		return dbUpdateUser(userid, fname, mname, lname, email, updater, ip, true,notify);
	}

	public static boolean dbUpdateUser(int userid, String fname, String mname, String lname, String email, int updater, String ip, boolean history,String notify) {
		boolean r = false;
		String command = "";
		Sage db = new Sage();
		if (history) {
			command = UsersSQL.updateHistory(userid, updater, ip);
			db.update(command);
		}
		command = UsersSQL.updateUsers(userid, fname, mname, lname, email, updater, ip,notify);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateUsername(int userid, String username, int updater, String ip) {
		return dbUpdateUsername(userid, username, updater, ip, true);
	}

	public static boolean dbUpdateUsername(int userid, String username, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateHistory(userid, updater, ip);
			db.update(command);
		}
		command = UsersSQL.updateUsername(userid, username);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateUsernameEmail(int userid, String username, int updater, String ip) {
		return dbUpdateUsernameEmail(userid, username, updater, ip, true);
	}

	public static boolean dbUpdateUsernameEmail(int userid, String username, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateHistory(userid, updater, ip);
			db.update(command);
		}
		command = UsersSQL.updateUsernameEmail(userid, username);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbDisableDuplicateUsers(int activeuserid, int updater, String ip) {
		return dbDisableDuplicateUsers(activeuserid, updater, ip, true);
	}

	public static boolean dbDisableDuplicateUsers(int userid, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateAllHistory(userid, updater, ip);
			db.update(command);
		}
		command = UsersSQL.disableDuplicateUsers(userid);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateHistory(int userid, int updater, String ip) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		command = UsersSQL.updateHistory(userid, updater, ip);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateAllHistory(int userid, int updater, String ip) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		command = UsersSQL.updateAllHistory(userid, updater, ip);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbUpdateHistory(String username, int updater, String ip) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		command = UsersSQL.updateHistory(username, updater, ip);
		r = db.update(command);
		db.clear();
		return r;
	}


	public static Token fromJson(String json) {
		Token t = new Token();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			t = mapper.readValue(json, Token.class);
		}
		catch (Exception e) { }
		return t;
	}

	
	public static int saveTeamProfile(String username, String fname, String mname, String lname, String email,  int updater, String ip,int teamTypeId,String wphone) {
		int id =0;
		if (Operator.hasValue(username)) {
			String command = UsersSQL.getUsername(username);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int userid = db.getInt("ID");
					if (userid > 0) {
						id=TeamAgent.getUserTeamId(userid);
					}
				}
				else {
					command = UsersSQL.addUsers(username, fname, mname, lname, email, updater, ip);
					if (db.query(command) && db.next()) {
						int activeusersid = db.getInt("ID");
						if (activeusersid > 0) {
							int teamId = TeamAgent.addTeam(activeusersid, teamTypeId, updater, ip);
							id=teamId;
							if(Operator.hasValue(wphone)) {
							int peopleId = PeopleAgent.dbAddPeople(activeusersid, "", "", "", "", wphone, wphone, "", "", updater, ip);
							}
						}
					}					
				}
				db.clear();
			}
		}
		return id;
	}
	
	
	
	public static int getRefUser(int id) {
		int usersid=0;
			String command = UsersSQL.getRefUser(id);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				usersid = db.getInt("USERS_ID");
			}
			db.clear();
			return usersid;
	}
			
	public static boolean saveUserNotes(Cartographer map) {

		boolean result = true;
		Logger.info("userid : "+map.getString("_userid"));
		Logger.info("id : "+map.getString("_id"));
		Logger.info("type : "+map.getString("type"));
		return result;
	}
	
}
































