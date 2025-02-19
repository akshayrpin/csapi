package csapi.impl.team;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import alain.core.db.Sage;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.impl.people.PeopleSQL;
import csapi.impl.users.UsersSQL;
import csshared.vo.SubObjVO;

public class TeamAgent {

	public static String solrurl = "http://10.14.6.19:8983/solr/team_core/";
	public static String searchurl = Config.rooturl()+"/csapi/rest/team/search";

	public static SubObjVO[] search(String q, int utype) {
		return search(q, utype, 0, 10000);
	}

	public static SubObjVO[] search(String q, int ttype, int start, int end) {
		SubObjVO[] r = new SubObjVO[0];
		
		try {

			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			SolrQuery query = new SolrQuery();
			query.setQuery("allteam:"+q);
			query.setStart(start);
			query.setRows(end);
			//query.setParam("sort", "usergroup asc, username asc");
			query.setParam("defType", "edismax");
			query.setParam("mm", "100");
			query.setParam("wt", "json");
			query.setParam("indent", "true");
			
			if(ttype>0){
				query.setParam("fq", "teamtypeid:"+ttype);
			}

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

				int refteamid = -1;
				try { refteamid = (int) doc.getFieldValue("refteamid"); } catch (Exception e) { }

				int teamtypeid = -1;
				try { teamtypeid = (int) doc.getFieldValue("teamtypeid"); } catch (Exception e) { }

				int usersid = -1;
				try { usersid = (int) doc.getFieldValue("usersid"); } catch (Exception e) { }

				String username = "";
				try { username = (String) doc.getFieldValue("username"); } catch (Exception e) { }

				String email = "";
				try { email = (String) doc.getFieldValue("email"); } catch (Exception e) { }
				vo.setData("EMAIL", email);

				String type = "";
				try { type = (String) doc.getFieldValue("type"); } catch (Exception e) { }
				vo.setData("TYPE", type);

				String name = "";
				try { name = (String) doc.getFieldValue("name"); } catch (Exception e) { }
				vo.setData("NAME", name);

				String phonework = "";
				try { phonework = (String) doc.getFieldValue("phonework"); } catch (Exception e) { }
				vo.setData("PHONEWORK", phonework);

				String phonecell = "";
				try { phonecell = (String) doc.getFieldValue("phonecell"); } catch (Exception e) { }
				vo.setData("PHONECELL", phonecell);

				vo.setId(refteamid);
				vo.setValue(Operator.toString(refteamid));
				vo.setText(name);

				sb = new StringBuilder();
				sb.append("<div class=\"people_result\">\n");
				if (Operator.hasValue(type)) {
					sb.append("<span class=\"people_result people_type\">").append(type).append("</span>\n");
				}
				if (Operator.hasValue(name)) {
					sb.append("<span class=\"people_result people_name\">").append(name).append("</span>\n");
				}
				if (Operator.hasValue(email)) {
					sb.append("<span class=\"people_result people_email\">").append(email).append("</span>\n");
				}
				if (Operator.hasValue(phonework)) {
					sb.append("<span class=\"people_result people_phone\">WORK: ").append(phonework).append("</span>\n");
				}
				if (Operator.hasValue(phonecell)) {
					sb.append("<span class=\"people_result people_phone\">CELL: ").append(phonecell).append("</span>\n");
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

	public static boolean addTeam(String type, int typeid, String users, int createdby) {
		if (!Operator.hasValue(users)) { return false; }
		String[] u = Operator.split(users, "|");
		return addTeam(type, typeid, u, createdby);
	}

	public static boolean addTeam(String type, int typeid, String[] users, int createdby) {
		if (!Operator.hasValue(users)) { return false; }
		boolean r = false;
		String command = "";
		Sage db = new Sage();
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			int userid = Operator.toInt(user);
			if (userid > 0) {
				command = TeamSQL.getTeam(type, typeid, userid);
				if (db.query(command) && db.next() && db.getInt("ID") > 0) {
					r = true;
				}
				else {
					command = TeamSQL.addTeam(type, typeid, userid, createdby);
					if (db.update(command)) {
						r = true;
					}
				}
			}
		}
		db.clear();
		return r;
	}

	public static SubObjVO[] getTeam(String refteamids) {
		String[] t = Operator.split(refteamids, "|");
		String command = TeamSQL.getTeam(t);
		SubObjVO[] c = Choices.getChoices(command);
		return c;
	}

	public static String joinTeam(String refteamids, String divider) {
		StringBuilder sb = new StringBuilder();
		String[] t = Operator.split(refteamids, "|");
		String command = TeamSQL.getTeam(t);
		Sage db = new Sage();
		db.query(command);
		boolean empty = true;
		while (db.next()) {
			String name = db.getString("TEXT");
			if (!empty) { sb.append(divider); }
			sb.append(name);
		}
		db.clear();
		return sb.toString();
	}

	public static void updateTeam(String type, int typeid, String users, int createdby) {
		String[] u = Operator.split(users, "|");
		updateTeam(type, typeid, u, createdby);
	}

	public static void updateTeam(String type, int typeid, String[] users, int createdby) {
		String command = TeamSQL.deleteTeam(type, typeid, createdby);
		Sage db = new Sage();
		db.update(command);
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			int userid = Operator.toInt(user);
			if (userid > 0) {
				command = TeamSQL.addTeam(type, typeid, userid, createdby);
				db.update(command);
			}
		}
		db.clear();
	}







	public static int addTeam(int userid, int teamTypeId, int creator, String ip) {
		int r = -1;
		Sage db = new Sage();
		String command = "";
		
		command = TeamSQL.addTeam(userid, teamTypeId, creator, ip);
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}

	public static int getUserTeamId(int userid) {
		int r = -1;
		Sage db = new Sage();
		String command = "";
		
		command = TeamSQL.getUserTeamId(userid);
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}





}
















