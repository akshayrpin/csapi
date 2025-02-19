package csapi.impl.people;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.DatasourceInfo;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.impl.users.UsersAgent;
import csapi.impl.users.UsersSQL;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserSearchVO;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;

public class PeopleAgent {

	public static String searchurl = Config.rooturl()+"/csapi/rest/people/search";

	public static BrowserVO browse(RequestVO vo) {

		BrowserVO b = new BrowserVO();

		try {

			BrowserHeaderVO h = new BrowserHeaderVO();

			BrowserSearchVO s = new BrowserSearchVO();
			s.setEntity(vo.getEntity());
			s.setType(vo.getType());
			s.setTypeid(vo.getTypeid());
			s.setGrouptype("people");
			s.setPlaceholder("Search People");

			h.setSearch(s);
			h.setLabel("PEOPLE BROWSER");
			h.setDataid(Operator.toString(vo.getId()));


			b.setHeader(h);
		
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static SubObjVO[] search(String q, int utype) {
		return search(q, utype, 0, 100000);
	}
	
	public static String search(String url,String q, int start,int end, String sort, String fq){
		
		String resp ="";
		try{
			
			URIBuilder  o = new URIBuilder(url);
			ArrayList<NameValuePair> oparams = new ArrayList<NameValuePair>();
			
			
			oparams.add(new BasicNameValuePair("q",q));
			oparams.add(new BasicNameValuePair("start",start+""));
			oparams.add(new BasicNameValuePair("rows",end+""));
		
		
			oparams.add(new BasicNameValuePair("defType","edismax"));
			oparams.add(new BasicNameValuePair("mm","100"));
			oparams.add(new BasicNameValuePair("indent","on"));
			oparams.add(new BasicNameValuePair("wt","json"));
			if(Operator.hasValue(sort)){
				oparams.add(new BasicNameValuePair("sort",sort));
			}
			if(Operator.hasValue(fq)){
				//oparams.add(new BasicNameValuePair("fq",fq));
			}
			
			
			String u = o.toString();
			resp = GlobalSearch.searchSolr(u, oparams,"");
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return resp;
	}
	
	
	public static SubObjVO[] search(String q, int utype, int start, int end) {
		SubObjVO[] r = new SubObjVO[0];
		
		try {

			String searched = search(CsApiConfig.getString("search.people"), q, start, end, "name%20asc", "");
			StringBuilder sb = new StringBuilder();

			JSONObject results = new JSONObject(searched);
			int found = 0;
			if(results.getJSONObject("response").has("numFound")){
				found = results.getJSONObject("response").getInt("numFound");
			}
			
			
			r = new SubObjVO[found];
			for (int i=0; i < results.getJSONObject("response").getJSONArray("docs").length(); i++) {
				JSONObject  doc = results.getJSONObject("response").getJSONArray("docs").getJSONObject(i);
			
				SubObjVO vo = new SubObjVO();

				String id = "";
				try { id = (String) doc.getString("id"); } catch (Exception e) { }

				int refusersid = -1;
				try { refusersid = doc.getInt("refusersid"); } catch (Exception e) { }

				int usersgroupid = -1;
				try { usersgroupid =  doc.getInt("usersgroupid"); } catch (Exception e) { }

				int usersid = -1;
				try { usersid =  doc.getInt("usersid"); vo.setSelected(usersid+"");} catch (Exception e) { }
				
				
				int userstypeid = -1;
				try { userstypeid =  doc.getInt("userstypeid"); } catch (Exception e) { }

				int peopleid = -1;
				try { peopleid =  doc.getInt("peopleid"); } catch (Exception e) { }

				int lkuppeoplelicensetypeid = -1;
				try { lkuppeoplelicensetypeid = doc.getInt("lkuppeoplelicensetypeid"); } catch (Exception e) { }

				String username = "";
				try { username = (String) doc.getString("username"); } catch (Exception e) { }

				String email = "";
				try { email = (String) doc.getString("email"); } catch (Exception e) { }
				vo.setData("EMAIL", email);

				String type = "";
				try { type = (String) doc.getString("type"); } catch (Exception e) { }
				vo.setData("TYPE", type);
				vo.setDescription(type);

				String usergroup = "";
				try { usergroup = (String) doc.getString("usergroup"); } catch (Exception e) { }

				String name = "";
				try { name = (String) doc.getString("name"); } catch (Exception e) { }
				vo.setData("NAME", name);

				String address = "";
				try { address = (String) doc.getString("address"); } catch (Exception e) { }
				vo.setData("ADDRESS", address);

				String city = "";
				try { city = (String) doc.getString("city"); } catch (Exception e) { }

				String state = "";
				try { state = (String) doc.getString("state"); } catch (Exception e) { }

				String zip = "";
				try { zip = (String) doc.getString("zip"); } catch (Exception e) { }

				String phonework = "";
				try { phonework = (String) doc.getString("phonework"); } catch (Exception e) { }
				vo.setData("PHONEWORK", phonework);

				String phonecell = "";
				try { phonecell = (String) doc.getString("phonecell"); } catch (Exception e) { }
				vo.setData("PHONECELL", phonecell);

				String fax = "";
				try { fax = (String) doc.getString("fax"); } catch (Exception e) { }

				String licensetype = "";
				try { licensetype = (String) doc.getString("licensetype"); } catch (Exception e) { }
				vo.setData("LICENSETYPE", licensetype);

				String licnum = "";
				try { licnum = (String) doc.getString("licnum"); } catch (Exception e) { }
				vo.setData("LICNUM", licnum);

				String licexpirationdate = "";
				try { licexpirationdate = (String) doc.getString("licexpirationdate"); } catch (Exception e) { }
				vo.setData("LICEXP", licexpirationdate);


				vo.setId(refusersid);
				vo.setValue(Operator.toString(refusersid));
				if (Operator.hasValue(name)) {
					vo.setText(name);
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
				if (Operator.hasValue(email)) {
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
		
		return r;
	}

	/*public static SubObjVO[] search(String q, int utype, int start, int end) {
		SubObjVO[] r = new SubObjVO[0];
		
		try {

			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			SolrQuery query = new SolrQuery();
			query.setQuery("allpeople:"+q);
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

				int refusersid = -1;
				try { refusersid = (int) doc.getFieldValue("refusersid"); } catch (Exception e) { }

				int usersgroupid = -1;
				try { usersgroupid = (int) doc.getFieldValue("usersgroupid"); } catch (Exception e) { }

				int usersid = -1;
				try { usersid = (int) doc.getFieldValue("usersid"); vo.setSelected(usersid+"");} catch (Exception e) { }
				
				
				int userstypeid = -1;
				try { userstypeid = (int) doc.getFieldValue("userstypeid"); } catch (Exception e) { }

				int peopleid = -1;
				try { peopleid = (int) doc.getFieldValue("peopleid"); } catch (Exception e) { }

				int lkuppeoplelicensetypeid = -1;
				try { lkuppeoplelicensetypeid = (int) doc.getFieldValue("lkuppeoplelicensetypeid"); } catch (Exception e) { }

				String username = "";
				try { username = (String) doc.getFieldValue("username"); } catch (Exception e) { }

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


				vo.setId(refusersid);
				vo.setValue(Operator.toString(refusersid));
				if (Operator.hasValue(name)) {
					vo.setText(name);
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
				if (Operator.hasValue(email)) {
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
		
		return r;
	}*/

	public static boolean deletePeople(String type, int typeid, int userid, String ip) {
		boolean r = false;
		String command = PeopleSQL.deletePeople(type, typeid, userid, ip);
		Sage db = new Sage();
		r = db.update(command);
		db.clear();
		return r;
	}

	public static int updatePeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
		if (userid < 1) { return -1; }
		int r = -1;
		Sage db = new Sage();
		try {
			int peopleid = -1;
			String command = PeopleSQL.getPeople(userid);
			if (db.query(command) && db.next()) {
				peopleid = db.getInt("ID");
			}
			if (peopleid > 0) {
				r = dbUpdate(peopleid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip);
			}
			else {
				r = dbAddPeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, updater, ip);
			}
		}
		catch (Exception e) { }
		db.clear();
		return r;
	}

	public static int addPeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
		return updatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip);
	}

	public static void updatePeople(String type, int typeid, String users, int primary, int createdby, String ip) {
		String[] u = Operator.split(users, "|");
		updatePeople(type, typeid, u, primary, createdby, ip);
	}

	public static void updatePeople(String type, int typeid, String[] users, int primary, int createdby, String ip) {
		String command = PeopleSQL.deletePeople(type, typeid, createdby, ip);
		Sage db = new Sage();
		db.update(command);
		if (primary > 0) {
			command = PeopleSQL.removePrimary(type, typeid, createdby, ip);
			db.update(command);
		}
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			int userid = Operator.toInt(user);
			if (userid > 0) {
				command = PeopleSQL.addPeople(type, typeid, userid, primary, createdby, ip);
				db.update(command);
			}
		}
		db.clear();
	}

	public static boolean addApplicants(String type, int typeid, String refusersids, int updater, String ip) {
		if (!Operator.hasValue(type)) { return false; }
		if (!Operator.hasValue(refusersids)) { return false; }
		if (typeid < 1) { return false; }
		boolean r = true;
		int usersid = -1;
		String[] ruids = Operator.split(refusersids, "|");
		Sage db = new Sage();
		String command = "";
		for (int i=0; i< ruids.length; i++) {
			int refuserid = Operator.toInt(ruids[i]);
			if (refuserid > 0) {
				command = PeopleSQL.getActiveUsername(refuserid);
				if (db.query(command) && db.next()) {
					usersid = db.getInt("ID");
					int appid = -1;
					command = PeopleSQL.findApplicant(usersid);
					if (db.query(command) && db.next()) {
						appid = db.getInt("REF_USERS_ID");
					}
					else {
						command = PeopleSQL.addApplicant(usersid, updater, ip);
						if (db.query(command) && db.next()) {
							appid = db.getInt("ID");
						}
					}
					if (appid > 0) {
						command = PeopleSQL.delete(type, typeid, appid, updater, ip);
						db.update(command);
						command = PeopleSQL.addPeople(type, typeid, appid, false, updater, ip);
						db.update(command);
					}
				}
			}
		}
		db.clear();
		return r;
	}

	public static boolean addPeople(String type, int typeid, String users, int primary, int createdby, String ip) {
		return addPeople(type, typeid, users, primary, "", createdby, ip);
	}

	public static boolean addPeople(String type, int typeid, String users, int primary, String applicants, int createdby, String ip) {
		String[] u = Operator.split(users, "|");
		return addPeople(type, typeid, u, primary, applicants, createdby, ip);
	}

	public static boolean addPeople(String type, int typeid, String[] users, int primary, int createdby, String ip) {
		return addPeople(type, typeid, users, primary, "", createdby, ip);
	}

	public static boolean addPeople(String type, int typeid, String[] users, int primary, String applicants, int createdby, String ip) {
		boolean r = false;
		String command = "";
		Sage db = new Sage();
		if (primary > 0) {
			command = PeopleSQL.removePrimary(type, typeid, createdby, ip);
			db.update(command);
		}
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			int userid = Operator.toInt(user);
			if (userid > 0) {
				int ruserid = -1;
				command = PeopleSQL.findPerson(type, typeid, userid);
				if (db.query(command) && db.next()) {
					ruserid = db.getInt("ID");
					r = true;
				}
				if (ruserid < 1) {
					command = PeopleSQL.addPeople(type, typeid, userid, primary, createdby, ip);
					if (db.update(command)) {
						r = true;
						CsReflect.addHistory(type, typeid, "people", userid, "add");
					}
				}
			}
		}
		db.clear();
		if (Operator.hasValue(applicants)) {
			addApplicants(type, typeid, applicants, createdby, ip);
		}
		return r;
	}

	public static ResponseVO setPrimary(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		String ref = vo.getRef();
		Logger.highlight(ref);
		String type = vo.getType();
		int typeid = vo.getTypeid();
		if (id > 0 && Operator.hasValue(type)) {
			Sage db = new Sage();
			String command = "";
			command = PeopleSQL.removePrimary(type, typeid, u.getId(), u.getIp());
			db.update(command);
			command = PeopleSQL.setPrimary(id, type, typeid, u.getId(), u.getIp());
			if (Operator.hasValue(command)) {
				if (db.update(command)) {
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("A database error occured");
				}
			}
			db.clear();
		}
		return r;
	}

	public static ResponseVO unPrimary(RequestVO vo) {
		ResponseVO r = new ResponseVO();
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		String ref = vo.getRef();
		Logger.highlight(ref);
		String type = vo.getType();
		int typeid = vo.getTypeid();
		if (id > 0 && Operator.hasValue(type)) {
			Sage db = new Sage();
			String command = "";
			command = PeopleSQL.removePrimary(id, type, typeid, u.getId(), u.getIp());
			if (Operator.hasValue(command)) {
				if (db.update(command)) {
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("A database error occured");
				}
			}
			db.clear();
		}
		return r;
	}

	public static boolean delete(String type, int typeid, int refid, int userid, String ip) {
		boolean r  = false;
		String command = PeopleSQL.delete(type, typeid, refid, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			CsReflect.addHistory(type, typeid, "people", refid, "delete");
			db.clear();
		}
		return r;
	}

	public static boolean hasPrimaryContact(String type, int typeid) {
		boolean r = false;
		String command = PeopleSQL.getPrimary(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			r = db.size() > 0;
			db.clear();
		}
		return r;
	}

	public static ResponseVO update(RequestVO vo) {
		Token u = AuthorizeToken.authenticate(vo);	
		int origrefusersid = Operator.toInt(vo.getId());
		DataVO m = DataVO.toDataVO(vo);
		ResponseVO v = select(origrefusersid, m.getInt("LKUP_USERS_TYPE_ID"), m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"), m.getString("USERNAME"), m.getString("ADDRESS"), m.getString("CITY"), m.getString("STATE"), m.getString("ZIP"), m.getString("PHONE_WORK"), m.getString("PHONE_CELL"), m.getString("PHONE_HOME"), m.getString("FAX"), m.getString("LIC_NO"), m.getString("LIC_EXP_DT"), m.getString("GEN_LIABILITY_DT"), m.getString("AUTO_LIABILITY_DT"), m.getString("WORK_COMP_EXP_DT"), u.getId(), u.getIp());
		int newrefusersid = v.getId();
		boolean ca = Operator.equalsIgnoreCase(m.getString("COPYAPPLICANT"), "Y");
		int updater = u.getId();
		String ip = u.getIp();
		String type = vo.getType();
		int typeid = vo.getTypeid();
		String command = "";
		Sage db = new Sage();
		if (origrefusersid != newrefusersid) {
			String primary = "";
			command = PeopleSQL.getPeople(type, typeid, origrefusersid);
			if (db.query(command) && db.next()) {
				primary = db.getString("PRIMARY_CONTACT");
			}
			command = PeopleSQL.delete(type, typeid, origrefusersid, updater, ip);
			db.update(command);
			command = PeopleSQL.addPeople(type, typeid, newrefusersid, Operator.equalsIgnoreCase(primary, "Y"), updater, ip);
			db.update(command);
		}
		if (ca) {
			command = PeopleSQL.getActiveUsername(newrefusersid);
			int usersid = -1;
			if (db.query(command) && db.next()) {
				usersid = db.getInt("ID");
				int appid = -1;
				command = PeopleSQL.findApplicant(usersid);
				if (db.query(command) && db.next()) {
					appid = db.getInt("REF_USERS_ID");
				}
				else {
					command = PeopleSQL.addApplicant(usersid, updater, ip);
					if (db.query(command) && db.next()) {
						appid = db.getInt("ID");
					}
				}
				if (appid > 0) {
					command = PeopleSQL.delete(type, typeid, appid, updater, ip);
					db.update(command);
					command = PeopleSQL.addPeople(type, typeid, appid, false, updater, ip);
					db.update(command);
				}
			}
		}
		db.clear();
		return v;
	}


	public static ResponseVO select(RequestVO vo) {
		Token u = AuthorizeToken.authenticate(vo);	
		int id = Operator.toInt(vo.getId());
		DataVO m = DataVO.toDataVO(vo);
		return select(id, m.getInt("LKUP_USERS_TYPE_ID"), m.getString("FIRST_NAME"), m.getString("MIDDLE_NAME"), m.getString("LAST_NAME"), m.getString("EMAIL"), m.getString("USERNAME"), m.getString("ADDRESS"), m.getString("CITY"), m.getString("STATE"), m.getString("ZIP"), m.getString("PHONE_WORK"), m.getString("PHONE_CELL"), m.getString("PHONE_HOME"), m.getString("FAX"), m.getString("LIC_NO"), m.getString("LIC_EXP_DT"), m.getString("GEN_LIABILITY_DT"), m.getString("AUTO_LIABILITY_DT"), m.getString("WORK_COMP_EXP_DT"), u.getId(), u.getIp());
	}

	public static ResponseVO select(int refuserid, int usertypeid, String fname, String mname, String lname, String email, String username, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String licno, String licexp, String genliability, String autoliability, String workcomp, int creator, String ip) {
		ResponseVO r = new ResponseVO();
		Sage db = new Sage();
		try{
			String command = UsersSQL.getUserType(usertypeid);
			if (db.query(command) && db.next()) {
				String type = db.getString("TYPE");
				int userid = -1;
				int peopleid = -1;
				boolean doupdate = false;
				if (refuserid < 1) {
					userid = UsersAgent.addUser(username, fname, mname, lname, email, creator, ip);
					refuserid = UsersAgent.saveRefUser(userid, usertypeid, licno, licexp, genliability, autoliability, workcomp, creator, ip);
					command = UsersSQL.getRefUser(refuserid);
					db.query(command);
					if(db.next()){
						userid = db.getInt("USERS_ID");
						peopleid = db.getInt("PEOPLE_ID");
					}
				}
				else {
					command = UsersSQL.getRefUser(refuserid);
					db.query(command); 
					if (db.next()) {
						userid = db.getInt("USERS_ID");
						peopleid = db.getInt("PEOPLE_ID");
						int currtypeid = db.getInt("LKUP_USERS_TYPE_ID");
						refuserid = UsersAgent.saveRefUser(userid, usertypeid, licno, licexp, genliability, autoliability, workcomp, creator, ip);
						doupdate = true;
					}
				}
				if (refuserid > 0) {
					boolean updatepeople = false;
					if (!db.equalsIgnoreCase("ADDRESS", address)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("CITY", city)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("STATE", state)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("ZIP", zip)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("PHONE_WORK", wphone)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("PHONE_CELL", cphone)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("PHONE_HOME", hphone)) { updatepeople = true; }
					if (!db.equalsIgnoreCase("FAX", fax)) { updatepeople = true; }
	
					boolean updateuser = false;
					if (!db.equalsIgnoreCase("FIRST_NAME", fname)) { updateuser = true; }
					if (!db.equalsIgnoreCase("MI", mname)) { updateuser = true; }
					if (!db.equalsIgnoreCase("LAST_NAME", lname)) { updateuser = true; }
					if (!db.equalsIgnoreCase("EMAIL", email)) { updateuser = true; }
	
					if (peopleid < 1) {
						dbAddPeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, creator, ip);
					}
					else if (updatepeople) {
						dbUpdatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, "", creator, ip);
					}
	
					if (doupdate && updateuser) {
						UsersAgent.dbUpdateUser(userid, username, fname, mname, lname, email, creator, ip);
					}
				}

				if (refuserid > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(fname);
					if (Operator.hasValue(mname)) { sb.append(" ").append(mname); }
					if (Operator.hasValue(lname)) { sb.append(" ").append(lname); }
					String name = sb.toString();
					r.setId(refuserid);
					r.addInfo("REF_USERS_ID", Operator.toString(refuserid));
					r.addInfo("TYPE", type);
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
					r.addInfo("LIC_NO", licno);
					if (Operator.hasValue(genliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(genliability);
						r.addInfo("GEN_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(autoliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(autoliability);
						r.addInfo("AUTO_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(workcomp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(workcomp);
						r.addInfo("WORK_COMP_EXP_DT", d.getString("YYYY/MM/DD"));
					}
					r.setMessagecode("cs200");
				}
				else {
					r.setMessagecode("cs500");
					r.addError("A database error occured");
				}
			}
			else {
				r.setMessagecode("cs412");
				r.addError("Unknown User Type");
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		GlobalSearch.index(GlobalSearch.PEOPLE_DELTA);
		GlobalSearch.index(GlobalSearch.USERS_DELTA);
		
		
		return r;
	}

	public static ResponseVO email(String email) {
		ResponseVO r = new ResponseVO();
		if (Operator.hasValue(email)) {
			String command = UsersSQL.getEmail(email);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int id = db.getInt("ID");
					String fname = db.getString("FIRST_NAME");
					String mname = db.getString("MI");
					String lname = db.getString("LAST_NAME");
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
					r.addInfo("NAME", name);
					r.addInfo("FIRST_NAME", fname);
					r.addInfo("LAST_NAME", lname);
					r.addInfo("MIDDLE_NAME", mname);
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
			r.addError("Email is a required field");
		}
		return r;
	}

	public static ResponseVO username(String username, int usertype) {
		ResponseVO r = new ResponseVO();
		if (Operator.hasValue(username)) {
			String command = UsersSQL.getUser(username, usertype);
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
					r.addInfo("LIC_NO", licno);
					r.addInfo("BUS_LIC_NO", buslic);
					if (Operator.hasValue(licexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(licexp);
						r.addInfo("LIC_EXP_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(buslicexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(buslicexp);
						r.addInfo("BUS_LIC_EXP_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(genliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(genliability);
						r.addInfo("GEN_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(autoliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(autoliability);
						r.addInfo("AUTO_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(workcompexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(workcompexp);
						r.addInfo("WORK_COMP_EXP_DT", d.getString("YYYY/MM/DD"));
					}
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

	public static ResponseVO license(String username, int usertype) {
		ResponseVO r = new ResponseVO();
		if (Operator.hasValue(username)) {
			String command = UsersSQL.getLicense(username, usertype);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				if (db.query(command) && db.next()) {
					int id = db.getInt("ID");
					String fname = db.getString("FIRST_NAME");
					String mname = db.getString("MI");
					String lname = db.getString("LAST_NAME");
					String email = db.getString("EMAIL");
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
					r.addInfo("LIC_NO", licno);
					r.addInfo("BUS_LIC_NO", buslic);
					if (Operator.hasValue(licexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(licexp);
						r.addInfo("LIC_EXP_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(buslicexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(buslicexp);
						r.addInfo("BUS_LIC_EXP_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(genliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(genliability);
						r.addInfo("GEN_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(autoliability)) {
						Timekeeper d = new Timekeeper();
						d.setDate(autoliability);
						r.addInfo("AUTO_LIABILITY_DT", d.getString("YYYY/MM/DD"));
					}
					if (Operator.hasValue(workcompexp)) {
						Timekeeper d = new Timekeeper();
						d.setDate(workcompexp);
						r.addInfo("WORK_COMP_EXP_DT", d.getString("YYYY/MM/DD"));
					}
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

	public static ObjGroupVO summary(String type, int typeid, int id) {
		ObjGroupVO g = PeopleFields.summary();
		String command = PeopleSQL.summary(type, typeid, id);
		Sage db = new Sage();
		try {
			if (db.query(command)) {
				ArrayList<String> usersl = new ArrayList<String>();
				while (db.next()) {
					usersl.add(db.getString("USERNAME"));
					if (db.hasValue("CONTENT_TYPE")) {
						g.setContenttype(db.getString("CONTENT_TYPE"));
					}
				}
				String[] users = usersl.toArray(new String[usersl.size()]);
				HashMap<String, String> accounts = DatasourceInfo.hasAccount(users);
				g.setExtras(accounts);
				if (db.beforeFirst()) {
					ObjVO[] fields = g.getObj();
					int l = fields.length;
					int count = 0;
					ObjMap[] o = new ObjMap[db.size()];
					while (db.next()) {
						
						ObjMap v = new ObjMap();
						v.setId(db.getInt("ID"));
						if (db.equalsIgnoreCase("FINAL", "Y")) {
							v.setFinaled(true);
						}
						if (db.equalsIgnoreCase("ADDABLE", "N")) {
							g.setAddable(false);
						}
						if (db.equalsIgnoreCase("EDITABLE", "N")) {
							g.setEditable(false);
						}
						v.setRef(db.getString("REF"));
						v.setRefid(db.getInt("REF_ID"));
						v.setExpires(db.getString("EXPIRES"));
						if (db.hasValue("ISPUBLIC")) {
							v.setShowpublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
						}

						for (int i=0; i<l; i++) {
							ObjVO field = fields[i];
							ObjVO s = field.duplicate();
							String value = db.getString(field.getField());
							String link = db.getString(field.getLinkfield());
							String summarytype = db.getString("SUMMARYTYPE");
							int summaryid = db.getInt("SUMMARYID");
							String linktype = db.getString("LINKTYPE");
							int linkid = db.getInt("LINKID");
							s.setValue(value);
							String text = value;
							String textfield = field.getTextfield();
							if (Operator.hasValue(textfield)) {
								text = db.getString(textfield);
							}
							s.setText(text);
							if (Operator.hasValue(link)) {
								s.setLink(link);
//								s.setTarget("_blank");
							}

							if (Operator.hasValue(linktype)) {
								s.setLinktype(linktype);
							}
							if (linkid > 0) {
								s.setLinkid(linkid);
							}

							if (Operator.hasValue(summarytype)) {
								s.setSummarytype(summarytype);
							}
							if (summaryid > 0) {
								s.setSummaryid(summaryid);
							}

							s.setRef(db.getString("REF"));
							s.setRefid(db.getInt("REF_ID"));
							s.setAdminlink(db.getString("ADMINLINK"));

							String relfield = field.getRelfield();
							if (Operator.hasValue(relfield)) {
								s.setRel(db.getString(relfield));
							}
							String rel2field = field.getRel2field();
							if (Operator.hasValue(rel2field)) {
								s.setRel2(db.getString(rel2field));
							}
							v.getValues().put(field.getLabel(), s);
						}

						o[count] = v;
						count++;
						
					}
					g.setValues(o);
				}
			}
		}
		catch (Exception e) { e.printStackTrace();}
		db.clear();
		g.setObj(new ObjVO[0]);
		return g;
	}

	public static SubObjVO[] peopleUsers(String type, int typeid) {
		String command = PeopleSQL.getPeopleUsers(type, typeid);
		return Choices.getChoices(command);
	}

	public static SubObjVO[] peopleTypes() {
		String command = PeopleSQL.types("", -1, -1);
		return Choices.getChoices(command);
	}

//	public static boolean copy(String type, int typeid, int newtypeid, int userid, String ip) {
//		boolean r = false;
//		if (Operator.hasValue(type) && typeid > 0 && newtypeid > 0) {
//			String command = PeopleSQL.copy(type, typeid, newtypeid, userid, ip);
//			Sage db = new Sage();
//			r = db.update(command);
//			db.clear();
//		}
//		return r;
//	}


	public static boolean dbMergeUserPeople(int activeuserid, String username, int updater, String ip) {
		return dbMergeUserPeople(activeuserid, username, updater, ip, true);
	}

	public static boolean dbMergeUserPeople(int activeuserid, String username, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		command = UsersSQL.updateHistory(username, updater, ip);
		db.update(command);
		command = PeopleSQL.mergeUserPeople(activeuserid, username);
		r = db.update(command);
		db.clear();
		return r;
	}

	public static boolean dbDisableDuplicatePeople(int activepeopleid, int activeuserid, int updater, String ip) {
		return dbDisableDuplicatePeople(activepeopleid, activeuserid, updater, ip, true);
	}

	public static boolean dbDisableDuplicatePeople(int activepeopleid, int activeuserid, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateHistory(activeuserid, updater, ip);
			db.update(command);
		}
		command = PeopleSQL.disableDuplicatePeople(activepeopleid, activeuserid);
		r = db.update(command);
		db.clear();
		return r;
	}

	// Update based on User ID
	public static boolean dbUpdatePeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
		return dbUpdatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip, true);
	}

	public static boolean dbUpdatePeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip, boolean history) {
		boolean r = false;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateHistory(userid, updater, ip);
			db.update(command);
		}
		command = PeopleSQL.updatePeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip);
		r = db.update(command);
		db.clear();
		return r;
	}

	// Update based on People ID
	public static int dbUpdate(int peopleid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip) {
		return dbUpdate(peopleid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip, true);
	}

	public static int dbUpdate(int peopleid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, String comments, int updater, String ip, boolean history) {
		int r = -1;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updatePeopleHistory(peopleid, updater, ip);
			db.update(command);
		}
		command = PeopleSQL.update(peopleid, address, city, state, zip, wphone, cphone, hphone, fax, comments, updater, ip);
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}

	public static int dbAddPeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, int creator, String ip) {
		return dbAddPeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, creator, ip, true);
	}

	public static int dbAddPeople(int userid, String address, String city, String state, String zip, String wphone, String cphone, String hphone, String fax, int creator, String ip, boolean history) {
		int r = -1;
		Sage db = new Sage();
		String command = "";
		if (history) {
			command = UsersSQL.updateHistory(userid, creator, ip);
			db.update(command);
		}
		command = PeopleSQL.addPeople(userid, address, city, state, zip, wphone, cphone, hphone, fax, creator, ip);
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}



}
















