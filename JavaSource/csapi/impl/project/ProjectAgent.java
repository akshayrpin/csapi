package csapi.impl.project;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Choices;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.custom.CustomSQL;
import csapi.impl.entity.EntityAgent;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.general.GeneralSQL;
import csapi.impl.library.LibraryAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.notes.NotesAgent;
import csapi.impl.parking.ParkingAgent;
import csapi.impl.print.PrintAgent;
import csapi.utils.CsApiConfig;
import csapi.utils.CsReflect;
import csapi.utils.objtools.Fields;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserItemsVO;
import csshared.vo.BrowserVO;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeInfo;

public class ProjectAgent {

	public static final String LOG_CLASS= "ProjectAgent.java  : ";
	public static final String PROJECT_STATUS_EXPIRED = "Y";
	public static final String PROJECT_STATUS_ACTIVE = "N";

	public static BrowserVO browse(String type, String typeid, String option, Token u) {
		BrowserVO b = new BrowserVO();
		Sage db = new Sage();
		try {

			BrowserHeaderVO h = new BrowserHeaderVO();
			h.setLabel("PROJECT BROWSER");
			h.setDataid(Operator.toString(typeid));

			String expired ="";
			String sublevel ="";
			if (u.isStaff()) {
				String op[] = new String[4];
				op[0] ="Active";
				op[1] ="All";
				op[2] ="Sub";
				op[3] ="Sub All";
				h.setOptions(op);
				
				if(Operator.equalsIgnoreCase(option, "active") || !Operator.hasValue(option)){
					h.setOption("Active");
					expired = "N";
					sublevel = "N";
				}
				else if(Operator.equalsIgnoreCase(option, "sub")){
					h.setOption("Sub");
					expired = "N";
					sublevel = "Y";
				}
				else if(Operator.equalsIgnoreCase(option, "sub all")){
					h.setOption("Sub All");
					expired = "Y";
					sublevel = "Y";
				}
				else {
					h.setOption("All");
					expired = "Y";
					sublevel = "N";
				}
			}
			else {
				expired = "Y";
				sublevel = "Y";
			}

			int id = Operator.toInt(typeid);

			if (id > 0) {
				ArrayList<BrowserItemVO> r = new ArrayList<BrowserItemVO>();

				String command = ProjectSQL.browse(type, id, "Y", expired, sublevel, u);
				
				db.query(command);

				while(db.next()) {
					BrowserItemVO vo = new BrowserItemVO();
					if(Operator.hasValue(db.getString("ACCOUNT_NUMBER"))){
						vo.setTitle(db.getString("NAME")+" - "+db.getString("ACCOUNT_NUMBER"));
						vo.setDescription(db.getString("NAME")+" - "+db.getString("ACCOUNT_NUMBER"));
					}else {
						vo.setTitle(db.getString("NAME"));
						vo.setDescription(db.getString("NAME"));
					}
					vo.setId(db.getString("ID"));
					vo.setDataid(db.getString("ID"));
					vo.setChildren(db.getInt("CHILDREN"));
					vo.setChild("activity");
					vo.setEntity(type);
					vo.setType("project");
					vo.setLink("summary");
					if (db.equalsIgnoreCase("ISEXPIRED", "Y") && u.isStaff()) {
						vo.setExpired(true);
					}
					if (db.getInt("HARD_HOLDS") > 0) {
						vo.addAlert("hardhold");
					}
					if (db.getInt("SOFT_HOLDS") > 0) {
						vo.addAlert("softhold");
					}
					vo.setDomain(Config.rooturl());
					r.add(vo);
				}

			

				BrowserItemVO[] rs = r.toArray(new BrowserItemVO[r.size()]);
				b.setRoot(rs);
			}
			db.clear();
			b.setHeader(h);
			
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		return b;
	}

	public static BrowserItemsVO panels(String entity, int projectid, String reference) {
		BrowserItemsVO r = new BrowserItemsVO();
		String command = ProjectSQL.getProject(entity, projectid, reference);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {

			BrowserItemVO e = new BrowserItemVO();
			e.setEntity(entity);
			e.setType(entity);
			e.setId(db.getString("ENTITY_ID"));
			e.setDataid(db.getString("ENTITY_ID"));
			r.addPanel("entity", e);

			BrowserItemVO t = new BrowserItemVO();
			t.setEntity(entity);
			t.setType("project");
			t.setDataid(db.getString("ID"));
			t.setId(db.getString("ENTITY_ID"));
			r.addPanel("type", t);

			BrowserItemVO d = new BrowserItemVO();
			d.setEntity(entity);
			d.setType("project");
			d.setDataid(db.getString("ID"));
			d.setId(db.getString("ID"));
			r.addPanel("detail", t);
		}
		db.clear();
		return r;
	}
	
	public static JSONObject getSubs(int id){
		JSONObject o = new JSONObject();
		try{
			
			JSONObject h = new JSONObject();
			//h.put("search", Config.rooturl()+"/cs/lso/search.jsp");
			h.put("label", "PROJECT BROWSER");
			h.put("dataid",id);
			h.put("menu",id);
			h.put("parent", id);
			JSONArray optHeader =  new JSONArray();
			h.put("options", optHeader);
			
			
			String command = ProjectSQL.getSubs(id,"Y");
			Sage db = new Sage();
			db.query(command);
			JSONArray r = new JSONArray();
			
			JSONArray items = new JSONArray();
			while(db.next()){
				JSONObject root = new JSONObject();
				root.put("title",db.getString("NAME"));
				root.put("description",db.getString("NAME"));
				root.put("dataid",db.getString("id"));
				root.put("id",db.getString("id"));
				if(db.getInt("childrens")>0){
					root.put("children",Config.rooturl()+"/cs/lso/projects.jsp?id="+db.getString("id"));
				}
				//root.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+db.getString("id"));
				root.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_typeid="+db.getInt("id")+"&_type=project&_id="+db.getString("id"));
				r.put(root);
					
				
			}
			
			o.put("header", h);
			o.put("root", r);
			//o.put("items", items);
			db.clear();
		
		} catch (Exception e){
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return o;
	}
	
	
	public static JSONObject getChildrens(int id){
		JSONObject o = new JSONObject();
		try{
			
			JSONObject h = new JSONObject();
			h.put("search", Config.rooturl()+"/cs/lso/search.jsp");
			h.put("label", "LSO BROWSER");
			h.put("dataid",id);
			h.put("menu",id);
			h.put("parent", id);
			JSONArray optHeader =  new JSONArray();
			h.put("options", optHeader);
			
			
			String command = ProjectSQL.getChildrens(id,"Y");
			Sage db = new Sage();
			db.query(command);
			JSONArray r = new JSONArray();
			JSONObject root = new JSONObject();
			JSONArray items = new JSONArray();
			while(db.next()){
				if(db.getInt("ID")==id){
					root.put("title",db.getString("title"));
					root.put("description",db.getString("description"));
					root.put("dataid",db.getString("id"));
					root.put("id",db.getString("id"));
					//root.put("link",Config.rooturl()+"/cs/details.jsp?type=P&id="+db.getString("id"));
					r.put(root);
					
				}else {
					JSONObject i = new JSONObject();
					i.put("title",db.getString("title") +" - "+db.getString("ACT_NBR") );
					i.put("description",db.getString("description"));
					i.put("dataid",db.getString("id"));
					i.put("id",db.getString("id"));
					i.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_typeid="+db.getInt("id")+"&_type=activity&_id=".concat(db.getString("id")));
					items.put(i);
				}
			}
			
			o.put("header", h);
			o.put("root", r);
			o.put("items", items);
			db.clear();
		
		} catch (Exception e){
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return o;
	}
	
	public static int saveProject(RequestVO r, Token u){
		int result = -1;
		DataVO m = DataVO.toDataVO(r);
		if(Operator.equalsIgnoreCase(r.getType(), "project")) {
			if (r.getTypeid() > 0) {
				result = updateProject(r.getTypeid(), m.getString("NAME"), m.getString("DESCRIPTION"), m.getInt("LKUP_PROJECT_STATUS_ID"), m.getString("CIP_ACCTNO"), m.getString("START_DT"), m.getString("COMPLETION_DT"), m.getString("APPLIED_DT"), m.getString("EXPIRED_DT"), m.getString("ACCOUNT_NUMBER"), u.getId(), u.getIp());
			}
		}
		else {
			TypeInfo tinfo = EntityAgent.getEntity(r.getType(), r.getTypeid());
			String entity = tinfo.getEntity();
			int entityid = tinfo.getEntityid();
			result = addProject(r.getProcessid(), entity, entityid, m.getInt("LKUP_PROJECT_TYPE_ID"), m.getString("NAME"), m.getString("DESCRIPTION"), m.getInt("LKUP_PROJECT_STATUS_ID"), m.getString("CIP_ACCTNO"), m.getString("START_DT"), m.getString("COMPLETION_DT"), m.getString("APPLIED_DT"), m.getString("EXPIRED_DT"), m.getString("ACCOUNT_NUMBER"), Operator.toDouble(m.getString("VALUATION_CALCULATED")), Operator.toDouble(m.getString("VALUATION_DECLARED")), m.getString("ACTIVITY_DESCRIPTION"), m.getString("ACTIVITY_ARRAY"), m.getString("PLAN_CHK_REQ_ARRAY"), m.getString("INHERIT_ARRAY"), u.getId(), u.getIp(), u);
		}
		return result;
	}

	public static int addProject(String processid, String entity, int entityid, int projecttypeid, String name, String description, int statusid, String cip, String startdate, String completiondate, String applieddate, String expireddate, String accountnumber, double valuationcalculated, double valuationdeclared, String actdesc, String acttypes, String planchkreq, String inherit, int userid, String ip, Token u) {
		int r = -1;
		boolean valid = false;
		int projectid = -1;
		Timekeeper now = new Timekeeper();
		//String projectnbr = getProjectNumber(projecttypeid);
		LogAgent.updateLog(processid, 20, "Add new project: "+description, "Add new project: "+description);
		String command = ProjectSQL.add(projecttypeid, "", name, description, statusid, cip, startdate, completiondate, applieddate, expireddate, accountnumber, userid, ip, entity, now);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				projectid = db.getInt("ID");
				String projectnbr = getProjectNumber(projecttypeid,projectid);
				LogAgent.updateLog(processid, 20, "Add new project: "+projectnbr, "Add new project: "+projectnbr);
				command = ProjectSQL.updateProjectNumber(projectid, projectnbr);
				if (db.update(command)) {
					command = ProjectSQL.addRef(entity, entityid, projectid, userid, ip, now);
					if (db.update(command)) {
						r = projectid;
						command = ProjectSQL.getLkupProjectType(projecttypeid);
						if (db.query(command) && db.next()) {
							boolean dot = Operator.equalsIgnoreCase(db.getString("ISDOT"), "Y");
							if (dot) {
								command = ProjectSQL.insertRefAccount(projectid, userid);
								db.update(command);
							}
						}
						valid = true;
					}
				}

				addHistory(projectid, "project", projectid, "add");
//				command = ProjectSQL.addHistory(projectid);
//				db.update(command);
			}
			db.clear();
			if (valid && projectid > 0) {
				ActivityAgent.autoAddActivity(processid, entity, projectid, actdesc, valuationcalculated, valuationdeclared, startdate, applieddate, expireddate, "", completiondate, "N", Operator.split(acttypes, "|"), Operator.split(planchkreq, "|"), Operator.split(inherit, "|"), userid, ip, u);
			}
		}
		return r;
	}



	public static int updateProject(int projectid, String name, String description, int statusid, String cip, String startdate, String completiondate, String applieddate, String expireddate, String accountnumber, int userid, String ip) {
		int r = -1;
		Timekeeper now = new Timekeeper();
		String command = ProjectSQL.update(projectid, name, description, statusid, cip, startdate, completiondate, applieddate, expireddate, accountnumber, userid, ip, now);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.update(command)) {
//				command = ProjectSQL.addHistory(projectid);
//				db.update(command);
				r = projectid;
				addHistory(r, "project", r, "update");
			}
			db.clear();
		}
		return r;
	}

	public static boolean addHistory(int projid, String module, int moduleid, String moduleaction) {
		if (projid < 1) { return false; }
		String type = "";
		int lkupprojecttypeid = -1;
		String projectnbr = "";
		String name = "";
		String description = "";
		String status = "";
		int lkupprojectstatusid = -1;
		String cipacctno = "";
		String startdt = "";
		String completiondt = "";
		String applieddt = "";
		String expireddt = "";
		int lsouseid = -1;
		int deptid = -1;
		String microfilm = "";
		String active = "";
		int createdby = -1;
		String createddate = "";
		String updated = "";
		int updatedby = -1;
		String updateddate = "";
		String createdip = "";
		String updatedip = "";
		String accountnumber = "";
		String entity = "";
		String modulechanged = module;
		int modulechangedid = moduleid;
		boolean valid = false;
		String command = ProjectSQL.getProject(projid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			if (db.getInt("ID") > 0) {
				projid = db.getInt("ID");
				type = db.getString("TYPE");
				lkupprojecttypeid = db.getInt("LKUP_PROJECT_TYPE_ID");
				projectnbr = db.getString("PROJECT_NBR");
				name = db.getString("NAME");
				description = db.getString("DESCRIPTION");
				status = db.getString("STATUS");
				lkupprojectstatusid = db.getInt("LKUP_PROJECT_STATUS_ID");
				cipacctno = db.getString("CIP_ACCTNO");
				startdt = db.getString("START_DT");
				completiondt = db.getString("COMPLETION_DT");
				applieddt = db.getString("APPLIED_DT");
				expireddt = db.getString("EXPIRED_DT");
				lsouseid = db.getInt("LSO_USE_ID");
				deptid = db.getInt("DEPT_ID");
				microfilm = db.getString("MICROFILM");
				active = db.getString("ACTIVE");
				createdby = db.getInt("CREATED_BY");
				createddate = db.getString("CREATED_DATE");
				updated = db.getString("UPDATED");
				updatedby = db.getInt("UPDATED_BY");
				updateddate = db.getString("UPDATED_DATE");
				createdip = db.getString("CREATED_IP");
				updatedip = db.getString("UPDATED_IP");
				accountnumber = db.getString("ACCOUNT_NUMBER");
				entity = db.getString("ENTITY");
				valid = true;
			}
		}
		db.clear();
		if (valid) {
			return addHistory(projid, type, lkupprojecttypeid, projectnbr, name, description, status, lkupprojectstatusid, cipacctno, startdt, completiondt, applieddt, expireddt, lsouseid, deptid, microfilm, active, createdby, createddate, updated, updatedby, updateddate, createdip, updatedip, accountnumber, entity, modulechanged, modulechangedid, moduleaction);
		}
		return false;
	}

	public static boolean addHistory(int projid, String type, int lkupprojecttypeid, String projectnbr, String name, String description, String status, int lkupprojectstatusid, String cipacctno, String startdt, String completiondt, String applieddt, String expireddt, int lsouseid, int deptid, String microfilm, String active, int createdby, String createddate, String updated, int updatedby, String updateddate, String createdip, String updatedip, String accountnumber, String entity, String modulechanged, int modulechangedid, String moduleaction) {
		if (projid < 1) { return false; }
		boolean r = false;
		String command = ProjectSQL.addHistory(projid, type, lkupprojecttypeid, projectnbr, name, description, status, lkupprojectstatusid, cipacctno, startdt, completiondt, applieddt, expireddt, lsouseid, deptid, microfilm, active, createdby, createddate, updated, updatedby, updateddate, createdip, updatedip, accountnumber, entity, modulechanged, modulechangedid, moduleaction);
		Sage db = new Sage(CsApiConfig.getHistorySource());
		r = db.update(command);
		db.clear();
		return r;
	}



	public static String getSystemGeneratedProject_nbr(RequestVO r){
		return GeneralAgent.getNumber(r);
	}

	public static String getProjectNumber(int projecttypeid, int outputid) {
		return GeneralAgent.getNumber("project", projecttypeid,outputid);
	}
	
	public static ObjGroupVO getPrintDetail(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		Sage db = new Sage();
		boolean custom = false;
		String command = ProjectSQL.print(r.getType(), r.getTypeid());
		db.query(command);
		ObjVO[] fo  = new ObjVO[0];
		ObjVO[] v  = new ObjVO[db.size()];
		int counter =0;
		while(db.next()){
			ObjVO vo = new ObjVO();
			vo.setLabel(db.getString("LABEL"));
			vo.setValue(db.getString("FIELDVALUE"));
			v[counter] = vo;
			counter++;
		}
		
		
		
		command = CustomSQL.print(r.getType(), r.getTypeid());
		db.query(command);
		int ccouner =0;	
		ObjVO[] cv  = new ObjVO[db.size()];
		while(db.next()){
			ObjVO vo = new ObjVO();
			vo.setLabel(db.getString("LABEL"));
			vo.setValue(db.getString("VALUE"));
			cv[ccouner] = vo;
			ccouner++;
		}
		
		db.clear();
		
		if(cv.length>0){
			custom = true;
		}
		if(custom){
			fo = new ObjVO[v.length+cv.length];
			int c =0;
			for(int i=0;i<v.length;i++){
				fo[i] = v[i];
				c=i;
			}
			c = c+1;
			for(int i=0;i<cv.length;i++){
				fo[c] = cv[i];
				c++;
			}
			
			g.setObj(fo);
		}else {
			g.setObj(v);
		}
		
		return g;
	}
	
	
	public static ObjGroupVO getZone(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		Sage db = new Sage();
		String command = ProjectSQL.zone(r.getType(), r.getTypeid());
		db.query(command);
		ObjVO[] oa = new ObjVO[1];
		ObjVO vo = new ObjVO();
		if(db.next()){
			vo.setLabel(r.getType()+"_zone");
			vo.setValue("ZONE "+db.getString("VALUE"));
		}
		db.clear();
		oa[0] = vo;
		g.setObj(oa);
		
		return g;
	} 
	public static ObjGroupVO listAccount(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		
		String boldfont = "font-size:13pt; font-family:sans-serif;font-weight: bold;";
		String normalfont = "font-size:10pt; font-family:sans-serif;font-weight:100;";
		String biggernormalfont = "font-size:13pt; font-family:sans-serif;font-weight:100;";
		String border = "border: 1px solid black";
		
		StringBuilder sb = new StringBuilder();
		boolean result = true;
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;margin-top:20px;\"><tbody><tr>\n");
		
		sb.append("<td valign=\"top\" style=\"width:50%;").append(border).append("\">");
		sb.append("<table border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td align=\"right\" style=\"").append(boldfont).append("width: 150px;\">Account # : </td><td align=\"left\"><span style=\"").append(biggernormalfont).append("\">195414</span></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td align=\"right\" style=\"").append(boldfont).append("\">Date Issued : </td><td align=\"left\"><span style=\"").append(biggernormalfont).append("\">09/25/2015</span></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td  align=\"right\" style=\"").append(boldfont).append("\">Issued By : </td><td align=\"left\"><span style=\"").append(biggernormalfont).append("\">Online User</span></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td  align=\"right\" style=\"").append(boldfont).append("\">Amount Paid : </td><td align=\"left\"> <span style=\"").append(biggernormalfont).append("\">$215.00</span></td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td  align=\"right\" style=\"").append(boldfont).append("\">Method : </td><td align=\"left\"><span style=\"").append(biggernormalfont).append("\">creditcard</span></td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</td>");
		
		Sage db = new Sage();
		String command = ProjectSQL.printStickers(r.getType(), r.getTypeid());
		db.query(command);

		sb.append("<td valign=\"top\" width=\"50%\" style=\"").append(border).append("\">");
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\">");
		sb.append("<thead><th align=\"left\">Permit #</th><th align=\"left\">Permit Type</th><th align=\"left\">Fees</th></thead>");
		sb.append("<tbody>");
		
		while(db.next()){
			sb.append("<tr>");
			sb.append("<td style=\"").append(normalfont).append("\">").append(db.getString("ACT_NBR")).append("</td>");
			sb.append("<td style=\"").append(normalfont).append("\">").append(db.getString("TITLE")).append("</td>");
			sb.append("<td style=\"").append(normalfont).append("\"></td>");
			sb.append("</tr>");
		}
		
		sb.append("</tbody></table></td>\n");
		db.clear();
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_account");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	} 
	
	public static ObjGroupVO listAccountDetail(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		
		try{
			String boldfont = "font-size:10pt; font-family:sans-serif;font-weight: bold;";
			String normalfont = "font-size:10pt; font-family:sans-serif;font-weight:100;";
			StringBuilder sb = new StringBuilder();
			boolean result = true;
			
			String ids = r.getId();
			if(!Operator.hasValue(ids)){
				ids = Operator.toString(r.getTypeid());
			}
			ObjGroupVO oga[] = ParkingAgent.getParkingInfo(ids);
			ObjGroupVO og = oga[0];
			sb.append("<table align=\"center\" style=\"width:100%;\"><tbody><tr>\n");
			
			sb.append(" <td valign=\"top\" width=\"25%\">");
			sb.append("	<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\"><tbody>");
			sb.append("	<tr><td colspan=\"2\" style=\"").append(boldfont).append("\">Account # : ").append(og.getExtras().get("ACCOUNT_NO")).append("</td></tr>");
			sb.append("	<tr><td colspan=\"2\"><span style=\"").append(normalfont).append("\">");
			sb.append("	").append(og.getExtras().get("NAME")).append(" <br/>");
			sb.append("	").append(og.getExtras().get("ADDRESS")).append(" <br/>");
			sb.append("	BEVERLY HILLS ");
			sb.append("	</span></td></tr>");
			sb.append("</tbody></table>");
			sb.append("</td>");
			
			sb.append("<td valign=\"top\" width=\"30%\">");
			/*sb.append("<table align=\"center\"  border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\"><tbody>");
			sb.append("<tr><td align=\"right\" style=\"").append(boldfont).append("\">Date Issued : </td><td align=\"left\" style=\"").append(normalfont).append("\">09/25/2015</td></tr>");
			sb.append("<tr><td align=\"right\" align=\"right\" style=\"").append(boldfont).append("\">Issued By : </td><td align=\"left\" style=\"").append(normalfont).append("\">Online User</td></tr>");
			sb.append("<tr><td align=\"right\" style=\"").append(boldfont).append("\">Amount Paid : </td><td align=\"left\" style=\"").append(normalfont).append("\">$215.00</td></tr>");
			sb.append("<tr><td align=\"right\"  style=\"").append(boldfont).append("\">Method : </td><td align=\"left\" style=\"").append(normalfont).append("\">creditcard</td></tr>");
			sb.append("<tr><td align=\"right\"  style=\"").append(boldfont).append("width: 110px;\">Account # : </td><td align=\"left\" style=\"").append(normalfont).append("\">195414</td></tr>");
			sb.append("<tr><td align=\"right\"  style=\"").append(boldfont).append("width: 110px;\">Check No/<br/>Authorization : </td><td align=\"left\"  style=\"").append(normalfont).append("\">195414</td></tr>");
			sb.append("	</tbody></table>");*/
			
			sb.append("&nbsp;");
			sb.append("</td>");
			
			sb.append("<td valign=\"top\" width=\"45%\">");
			sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\">");
			sb.append("<thead><th align=\"left\">Permit #</th><th align=\"left\">Permit Type</th><th align=\"left\">Fees</th></thead>");
			sb.append("<tbody>");
			
			
			String startdate = "";
			String enddate = "";
			
		
			
			String type = r.getType();
			
			if(!Operator.hasValue(ids)){
				ids = Operator.toString(r.getTypeid());
			}
			boolean checkprinted = true;
			if(Operator.hasValue(r.getReference())){
				type = "activity";
				checkprinted = false;
				ids = r.getReference();
			}else {
				JSONObject d = GeneralAgent.getConfiguration("DOT");
				startdate = ParkingAgent.getParkingDate(d.getString("START_DATE"));
				enddate = ParkingAgent.getParkingDate(d.getString("EXP_DATE"));
			}
			
			Sage db = new Sage();
			//String command = ProjectSQL.printStickers(r.getType(), r.getTypeid());
			String command =  ProjectSQL.getParkingPermits(ids, type, startdate, enddate, checkprinted);
			db.query(command);
			while(db.next()){
				if(db.getDouble("AMOUNT")>0 && db.getDouble("BALANCE")<=0){
					sb.append("<tr>");
					sb.append("<td style=\"").append(normalfont).append("\">").append(db.getString("ACT_NBR")).append("</td>");
					sb.append("<td style=\"").append(normalfont).append("\">").append(db.getString("TYPE")).append("</td>");
					sb.append("<td style=\"").append(normalfont).append("\"></td>");
					sb.append("</tr>");
				}
			}
			
			sb.append("</tbody></table></td></tr></tbody></table>\n");
			db.clear();
			ObjVO[] oa = new ObjVO[1];	
			ObjVO o = new ObjVO();
			o.setLabel(r.getType()+"_list_account_detail");
			if(result){
				o.setValue(sb.toString());
			}else {
				o.setValue("");
			}	
			oa[0] = o;
			g.setObj(oa);
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	} 
	
	public static ObjGroupVO getPrintStickers(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		
		try{
			StringBuilder sb = new StringBuilder();
			boolean result = true;
			String breakline = "</tr><tr>\n";
			
			sb.append("<table style=\"width:100%;background-color: black;margin-top:80px;\"><tbody><tr>\n");
			
			String startdate = "";
			String enddate = "";
			
			
			String type = r.getType();
			
			String ids = r.getId();
			if(!Operator.hasValue(ids)){
				ids = Operator.toString(r.getTypeid());
			}
			boolean checkprinted = true;
			if(Operator.hasValue(r.getReference())){
				type = "activity";
				checkprinted = false;
				ids = r.getReference();
			}else {
				JSONObject d = GeneralAgent.getConfiguration("DOT");
				startdate = ParkingAgent.getParkingDate(d.getString("START_DATE"));
				enddate = ParkingAgent.getParkingDate(d.getString("EXP_DATE"));
				
			}
			
			Sage db = new Sage();
			String command =  ProjectSQL.getParkingPermits(ids, type, startdate, enddate, checkprinted);
			db.query(command);
			int count =0;
	
			while(db.next()){
				if(db.getDouble("AMOUNT")>0 && db.getDouble("BALANCE")<=0){
					if(count%2==0){
						sb.append(breakline);
					}
					sb.append(getStickerStyle(db.getString("TYPE"),db.getString("EXP_DATE"),db.getString("ACT_NBR")));
					count++;
				}
			}
			while(count<6){
				if(count%2==0){
					sb.append(breakline);
				}
				sb.append(getStickerStyle("VOID",null,null));
				count++;
			}
			sb.append("	</tr></tbody></table>\n");
			
			db.clear();
			Logger.info("STICKERS############"+sb.toString());
			ObjVO[] oa = new ObjVO[1];	
			ObjVO o = new ObjVO();
			o.setLabel(r.getType()+"_list_stickers");
			if(result){
				o.setValue(sb.toString());
			}else {
				o.setValue("");
			}	
			oa[0] = o;
			g.setObj(oa);
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static StringBuilder getStickerStyle(String parkingType,String expiry,String permitnumber){

		String colorPreferential = "#C14296";
		String colorOvernight = "#47C147";
		String headingstyle = "font-weight: bold;color: white;font-size: 20px;";
		String boxstyle = "background-color:white;height:130px;width:40%;font-size: 60px;";
		String voidstyle = "background-color:#fff;height: 180px;font-size: 60px;color:#888;";
		String sticker = Config.fullcontexturl()+"/images/DOTCG.png";

		StringBuilder sb = new StringBuilder();
		if(parkingType.equalsIgnoreCase("PREFERENTIAL PARKING")){
			sb.append("	<td>\n");
			sb.append("	<table style=\"width: 100%;\">\n");
			sb.append(" <tr><td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td></tr> \n");
			sb.append("	<tr> \n");
			sb.append("	<td align=\"center\" style=\"").append(boxstyle).append("\"> Q </td>\n");
			sb.append("	<td align=\"center\" style=\"width:60%;").append("\">\n");
			sb.append("	<img src=\"").append(sticker).append("\" alt=\"image\" style=\"height: 100%;width: 155%;background-color:").append(colorPreferential).append("\"/>\n");
			sb.append(" <p align=\"left\" style=\"margin-top:-40px;background:white;font-weight: bold;font-size:25px\">").append(permitnumber).append("</p>\n");
			sb.append(" <p align=\"center\" style=\"font-weight: bold;color: white;\">EXPIRES ").append(expiry).append("</p>\n");
			sb.append("	</td> \n");
			sb.append("	</tr> \n");
			sb.append("	</table> \n");
			sb.append("	</td> \n");
		}else if(parkingType.equalsIgnoreCase("OVERNIGHT PARKING")){
			sb.append("	<td>\n");
			sb.append("	<table style=\"width: 100%;\">\n");
			sb.append(" <tr><td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td></tr> \n");
			sb.append("	<tr> \n");
			sb.append("	<td align=\"center\" style=\"width:60%;").append("\">\n");
			sb.append("	<img src=\"").append(sticker).append("\" alt=\"image\" style=\"height: 100%;width: 155%;background-color:").append(colorOvernight).append("\"/>\n");
			sb.append(" <p align=\"left\" style=\"margin-top:-40px;background:white;font-weight: bold;font-size:25px\">").append(permitnumber).append("</p>\n");
			sb.append(" <p align=\"center\" style=\"font-weight: bold;color: white;\">EXPIRES ").append(expiry).append("</p>\n");
			sb.append("	</td> \n");
			sb.append("	<td align=\"center\" style=\"").append(boxstyle).append("\"> O/N </td>\n");
			sb.append("	</tr> \n");
			sb.append("	</table> \n");
			sb.append("	</td> \n");
		}else if(parkingType.equalsIgnoreCase("VOID")){
			sb.append("	<td>\n");
			sb.append("	<table align=\"center\" style=\"width:100%;background-color: black;\">\n");
			sb.append("	<tr> \n");
			sb.append("	<td colspan=\"2\" align=\"center\" style=\"").append(voidstyle).append("\">VOID</td> \n");
			sb.append("	</tr> \n");
			sb.append("	</table> \n");
			sb.append("	</td> \n");
		}
	
		return sb;
	}

	public static ObjGroupVO[] print(RequestVO r){
		ObjGroupVO[] oa = new ObjGroupVO[11];
		oa[0] = getPrintDetail(r);
		oa[1] = FinanceAgent.printFeeSummary(r);
		oa[2] = FinanceAgent.printFinanceSummary(r);
		oa[3] = FinanceAgent.printFinanceLedger(r);
		oa[4] = NotesAgent.printNotes(r);
		oa[5] = LibraryAgent.printLibrary(r);
		oa[6] = PrintAgent.getStandards(r);
		oa[7] = getPrintStickers(r);
		oa[8] = getZone(r);
		oa[9] = listAccount(r);
		oa[10] = listAccountDetail(r);
		
		return oa;
	}
	
	
	public static boolean getProjectStatus(int projectId){
		boolean result = false;
		Sage db = new Sage();
		String command = "select LPS.* from PROJECT P JOIN LKUP_PROJECT_STATUS LPS  where ID = "+projectId+"";
		db.query(command);
		if(db.next()){
			result = Operator.equalsIgnoreCase(db.getString("EXPIRED"), "Y");
		}
		db.clear();
		return result;
			
	}

	public static int getProjectId(String type, int typeid) {
		if (type.equalsIgnoreCase("project")) {
			return typeid;
		}
		else if (type.equalsIgnoreCase("activity")) {
			int id = -1;
			String command = ProjectSQL.getProjectId(type, typeid);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				id = db.getInt("PROJECT_ID");
			}
			db.clear();
			return id;
		}
		else {
			return -1;
		}
	}

	public static SubObjVO[] types(String type, int typeid) {
		SubObjVO[] r = new SubObjVO[0];
		String command = ProjectSQL.getProjectTypes(type, typeid);
		r = Choices.getChoices(command);
		if (r.length < 1) {
			if (!Operator.equalsIgnoreCase(type, "project")) {
				command = ProjectSQL.getProjectTypes();
				r = Choices.getChoices(command);
			}
		}
		return r;
	}

	public static SubObjVO[] status() {
		String command = ProjectSQL.getProjectStatuses();
		return Choices.getChoices(command);
	}

	public static SubObjVO[] autoActivities(String entity, int entityid, int projecttypeid) {
		String command = ActivitySQL.getAutoAddByProjectType(entity, entityid, projecttypeid);
		return Choices.getChoices(command);
	}

	public static int getProjectTypeId(int projectid) {
		int r = -1;
		String command = ProjectSQL.getProjectType(projectid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			r = db.getInt("ID");
		}
		db.clear();
		return r;
	}

	public static boolean move(int projectid, int newlsoid, int userid, String ip) {
		if (projectid < 1) { return false; }
		if (newlsoid < 1) { return false; }
		boolean r = false;
		String command = ProjectSQL.deactivateRef(projectid, userid, ip);
		Sage db = new Sage();
		if (db.update(command)) {
			command = ProjectSQL.addRef(projectid, newlsoid, userid, ip);
			r = db.update(command);
		}
		db.clear();
		return r;
	}

	/*public static ObjGroupVO getPrintStickers(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		Sage db = new Sage();
		String command = ProjectSQL.printStickers(r.getType(), r.getTypeid());
		db.query(command);
		ObjVO[] v  = new ObjVO[db.size()];
		int counter =0;
		while(db.next()){
			ObjVO vo = new ObjVO();
			vo.setLabel(db.getString("LABEL"));
			vo.setValue(db.getString("FIELDVALUE"));
			v[counter] = vo;
			counter++;
		}
		db.clear();
		g.setObj(v);
		
		
		return g;
	}
	
	
	public static ObjGroupVO[] print(RequestVO r){
		ObjGroupVO[] oa = new ObjGroupVO[8];
		oa[0] = getPrintDetail(r);
		oa[1] = FinanceAgent.printFeeSummary(r);
		oa[2] = FinanceAgent.printFinanceSummary(r);
		oa[3] = FinanceAgent.printFinanceLedger(r);
		oa[4] = NotesAgent.printNotes(r);
		oa[5] = LibraryAgent.printLibrary(r);
		oa[6] = PrintAgent.getStandards(r);
		oa[7] = printSticker(r);
		
		return oa;
	}
	
	
	
	public static ObjGroupVO printSticker(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		
		String outstyle ="font-size:9pt; font-family:sans-serif;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		
		StringBuilder sb = new StringBuilder();
		boolean result = true;
		
		
		sb.append("<table style=\"width:100%;background-color: black;\">\n");
		sb.append("<tbody>");
		
		sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table style=\"width: 100%; text-align: justify;\"> \n");
					sb.append("<tr><td colspan=\"2\" align=\"center\" style=\"font-weight: bold;color: white;font-size: 20px;\">PREFERENTIAL PARKING</td></tr> \n");
					sb.append("	<tr> \n");
						sb.append("	<td align=\"center\" style=\"border: 1px solid black;background-color:white;height:140px;width:60%;font-size: 60px;\"> Q </td>\n");
						sb.append("	<td style=\"width:40%;\" > \n");
							sb.append("	<img src=\"http://localhost:8080/csapi/images/DOTCG.png\" alt=\"img\" style=\"height: 150px;width: 100%;background-color: #C14296;\"/>\n");
						sb.append("	</td> \n");
					sb.append("	</tr> \n");
					sb.append("<tr><td colspan=\"2\" align=\"right\" style=\"font-weight: bold;color: white;\">EXPIRES 09/30/2016</td></tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
		
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table style=\"width: 100%; text-align: justify;\"> \n");
					sb.append("<tr><td colspan=\"2\" align=\"center\" style=\"font-weight: bold;color: white;font-size: 20px;\">PREFERENTIAL PARKING</td></tr> \n");
					sb.append("	<tr> \n");
						sb.append("	<td align=\"center\" style=\"border: 1px solid black;background-color:white;height:140px;width:60%;font-size: 60px;\"> Q </td>\n");
						sb.append("	<td style=\"width:40%;\" > \n");
							sb.append("	<img src=\"http://localhost:8080/csapi/images/DOTCG.png\" alt=\"img\" style=\"height: 150px;width: 100%;background-color: #C14296;\"/>\n");
						sb.append("	</td> \n");
					sb.append("	</tr> \n");
					sb.append("<tr><td colspan=\"2\" align=\"right\" style=\"font-weight: bold;color: white;\">EXPIRES 09/30/2016</td></tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
		sb.append("	</tr> \n");

		sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table style=\"width: 100%; text-align: justify;\"> \n");
					sb.append("<tr><td colspan=\"2\" align=\"center\" style=\"font-weight: bold;color: white;font-size: 20px;\">PREFERENTIAL PARKING</td></tr> \n");
					sb.append("	<tr> \n");
						sb.append("	<td align=\"center\" style=\"border: 1px solid black;background-color:white;height:140px;width:60%;font-size: 60px;\"> Q </td>\n");
						sb.append("	<td style=\"width:40%;\" > \n");
							sb.append("	<img src=\"http://localhost:8080/csapi/images/DOTCG.png\" alt=\"img\" style=\"height: 150px;width: 100%;background-color: #C14296;\"/>\n");
						sb.append("	</td> \n");
					sb.append("	</tr> \n");
					sb.append("<tr><td colspan=\"2\" align=\"right\" style=\"font-weight: bold;color: white;\">EXPIRES 09/30/2016</td></tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
		
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table style=\"width: 100%; text-align: justify;\"> \n");
					sb.append("<tr><td colspan=\"2\" align=\"center\" style=\"font-weight: bold;color: white;font-size: 20px;\">OVERNIGHT PARKING</td></tr> \n");
					sb.append("	<tr> \n");
					sb.append("	<td style=\"width:40%;\" > \n");
							sb.append("	<img src=\"http://localhost:8080/csapi/images/DOTCG.png\" alt=\"img\" style=\"height: 150px;width: 100%;background-color: #47C147;\"/>\n");
						sb.append("	</td> \n");
						sb.append("	<td align=\"center\" style=\"border: 1px solid black;background-color:white;height:140px;width:60%;font-size: 60px;\"> O/N </td>\n");
					sb.append("	</tr> \n");
					sb.append("<tr><td colspan=\"2\" align=\"left\" style=\"font-weight: bold;color: white;\">EXPIRES 09/30/2016</td></tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
		sb.append("	</tr> \n");
		
		sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;border: 1px solid black;background-color: black;\"> \n");
					sb.append("	<tr> \n");
						sb.append("	<td colspan=\"2\" align=\"center\" style=\"border: 1px solid black;background-color: white;height: 180px;font-size: 60px;\">VOID</td> \n");
					sb.append("	</tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
			sb.append("	<td style=\"").append(outstyle).append("\">");
				sb.append("	<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;border: 1px solid black;background-color: black;\"> \n");
					sb.append("	<tr> \n");
						sb.append("	<td colspan=\"2\" align=\"center\" style=\"border: 1px solid black;background-color: white;height: 180px;font-size: 60px;\">VOID</td> \n");
					sb.append("	</tr> \n");
				sb.append("	</table> \n");
			sb.append("	</td> \n");
		sb.append("	</tr> \n");
		
		sb.append("</tbody></table>");
		
		Logger.info("sb :"+sb);
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel("activity_list_stickers");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	*/
	
}
