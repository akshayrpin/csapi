package csapi.impl.lso;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.common.FieldObjects;
import csapi.impl.general.GeneralSQL;
import csapi.search.GlobalSearch;
import csapi.utils.CsApiConfig;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserItemsVO;
import csshared.vo.BrowserSearchVO;
import csshared.vo.BrowserVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.SubObjVO;

public class LsoAgent {

	public static String searchurl = Config.rooturl()+"/csapi/rest/lso/search";
	
	public static BrowserVO search(String searchField, int start, int end) {
		return search(searchField, start, end,"active");
	}
	
	public static String search(String url,String search, int start,int end, String sort, String fq){
			
			String resp ="";
			try{
				
				URIBuilder  o = new URIBuilder(url);
				ArrayList<NameValuePair> oparams = new ArrayList<NameValuePair>();
				oparams.add(new BasicNameValuePair("q",search));
				
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
					oparams.add(new BasicNameValuePair("fq",fq));
				}
				
				
				String u = o.toString();
				resp = GlobalSearch.searchSolr(u, oparams,"");
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
			return resp;
		}
	
	public static BrowserVO search(String searchField, int start, int end,String option) {
		BrowserVO b = new BrowserVO();
		
		try {
			
			String predir = "";
		
			if(Operator.hasValue(searchField)){
				
				searchField = searchField.toLowerCase();
				if(searchField.startsWith("n ")){ predir="N";	}
				if(searchField.indexOf(" n ")>0){ predir="N";	}
				if(searchField.endsWith(" n")){ predir="N";	}
				
				if(searchField.startsWith("north ")){ predir="N";	}
				if(searchField.indexOf(" north ")>0){ predir="N";	}
				if(searchField.endsWith(" north")){ predir="N";	}
				
				if(searchField.startsWith("s ")){ predir="S";	}
				if(searchField.indexOf(" s ")>0){ predir="S";	}
				if(searchField.endsWith(" s")){ predir="S";	}
				
				if(searchField.startsWith("south ")){ predir="S";	}
				if(searchField.indexOf(" south ")>0){ predir="S";	}
				if(searchField.endsWith(" south")){ predir="S";	}
				
				
				
				
			}
			
			BrowserHeaderVO h = new BrowserHeaderVO();
			ArrayList<BrowserItemVO> ro = new ArrayList<BrowserItemVO>();
			String active = "active:Y";
			String[] op = new String[0];
			op = new String[3];
			op[0] ="Active";
			op[1] ="Inactive";
			op[2] ="Description";
			h.setOptions(op);
			if(Operator.equalsIgnoreCase(option, "active") || !Operator.hasValue(option)){
				h.setOption("Active");
			}
			else if(Operator.equalsIgnoreCase(option, "description")){
				h.setOption("Description");
			}
			else {
				h.setOption("Inactive");
				active ="active:N";
			}

			//active ="active:\"Y\"";
			//query.setParam("fq", active);

//			String fq ="active";
			if(Operator.hasValue(predir)){
				//query.setParam("&fq", "predir:"+predir);
			}
			
			String searched = search(CsApiConfig.getString("search.address_lso"), searchField, start, end, "title%20asc", active);
			
		
	
			
			JSONObject results = new JSONObject(searched);
	
			
			
				
			int found = 0;
			if(results.getJSONObject("response").has("numFound")){
				found = results.getJSONObject("response").getInt("numFound");
			}
		/*	Logger.info("_______________>"+found);
			
			String numberOnly= searchField.replaceAll("[^0-9]", "");
			Logger.info("_______________>"+numberOnly);
			
			if(found==0 && Operator.hasValue(numberOnly)){
				String s = "title:";
				searchField = s+ Operator.replace(searchField, numberOnly, "*"+numberOnly+"*");
				Logger.info("_______________>"+searchField);
				searched = search(CsApiConfig.getString("search.address_lso"), searchField, start, end, "strno%20asc", "active","");
				results = new JSONObject(searched);
		
				found = 0;
				if(results.getJSONObject("response").has("numFound")){
					found = results.getJSONObject("response").getInt("numFound");
				}
				
			}*/
			

			BrowserSearchVO s = new BrowserSearchVO();
			s.setUrl(searchurl);

			String ph = "Search for Address";
			if (Operator.hasValue(searchField)) { ph = searchField; }
			s.setPlaceholder(ph);

			h.setSearch(s);
			h.setLabel("ADDRESS BROWSER");
			h.setQuery(searchField);
			h.setFound(found);
			/*h.setQuerytime(response.getQTime());
			h.setStatus(response.getStatus());*/
			
			
			
			if(found < 1) {
				h.setMessage("No Results found");
			}
			
			
			for (int i=0; i < results.getJSONObject("response").getJSONArray("docs").length(); i++) {
				JSONObject  doc = results.getJSONObject("response").getJSONArray("docs").getJSONObject(i);
				BrowserItemVO o = new BrowserItemVO();

				String title = "";
				try { title = (String) doc.getString("title"); } catch (Exception e) { }
				o.setTitle(title);

				String description = "";
				try { description = (String) doc.getString("description"); } catch (Exception e) { }
				if (Operator.equalsIgnoreCase(option, "description")) {
					o.setDescription(description);
				}
				else {
					o.setDescription(title);
				}

				String id = "";
				try { id = (String) doc.getString("id"); } catch (Exception e) { }
				o.setDataid(id);
				o.setId(id);

				o.setEntity("lso");
				o.setType("lso");
				o.setDomain(Config.rooturl());
				o.setSub("project");
				o.setLink("summary");

				try {
					String hh = (String) doc.getString("hardholds");
					if (Operator.toInt(hh) > 0) {
						o.addAlert("hardhold");
					}
				}
				catch (Exception e) {}

				try {
					String sh = (String) doc.getString("softholds");
					if (Operator.toInt(sh) > 0) {
						o.addAlert("softhold");
					}
				}
				catch (Exception e) {}

				String alladdress = "";
				try { alladdress = (String) doc.getString("alladdress"); } catch (Exception e) { }
				if(!Operator.equalsIgnoreCase(alladdress, "null")) {
					o.setChildren(1);
				}

				ro.add(o);
				
			}
			b.setHeader(h);
			BrowserItemVO[] rs = ro.toArray(new BrowserItemVO[ro.size()]);
			b.setRoot(rs);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return b;
	}
	

	/*public static BrowserVO search(String searchField, int start, int end,String option) {
		BrowserVO b = new BrowserVO();
		
		try {
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			SolrQuery query = new SolrQuery();
			String predir = "";
			query.setQuery(searchField);
			query.setStart(start);
			query.setRows(end);
			query.setParam("sort", "strno asc");
			query.setParam("defType", "dismax");
			query.setParam("mm", "100");
			query.setParam("wt", "json");
			query.setParam("indent", "true");
			if(Operator.hasValue(searchField)){
				
				searchField = searchField.toLowerCase();
				if(searchField.startsWith("n ")){ predir="N";	}
				if(searchField.indexOf(" n ")>0){ predir="N";	}
				if(searchField.endsWith(" n")){ predir="N";	}
				
				if(searchField.startsWith("north ")){ predir="N";	}
				if(searchField.indexOf(" north ")>0){ predir="N";	}
				if(searchField.endsWith(" north")){ predir="N";	}
				
				if(searchField.startsWith("s ")){ predir="S";	}
				if(searchField.indexOf(" s ")>0){ predir="S";	}
				if(searchField.endsWith(" s")){ predir="S";	}
				
				if(searchField.startsWith("south ")){ predir="S";	}
				if(searchField.indexOf(" south ")>0){ predir="S";	}
				if(searchField.endsWith(" south")){ predir="S";	}
				
				
				
				
			}
			
			BrowserHeaderVO h = new BrowserHeaderVO();
			ArrayList<BrowserItemVO> ro = new ArrayList<BrowserItemVO>();
			String active ="";
			String op[] = new String[2];
			op[0] ="Active";
			op[1] ="Inactive";
			h.setOptions(op);
			if(Operator.equalsIgnoreCase(option, "active") || !Operator.hasValue(option)){
				h.setOption("Active");
			
			}else {
				h.setOption("Inactive");
				//active ="active:\"N\"";
			}

			active ="active:\"Y\"";
			query.setParam("fq", active);

			if(Operator.hasValue(predir)){
				query.setParam("&fq", "predir:"+predir);
			}

			QueryResponse response = server.query(query);
			
			SolrDocumentList documents = response.getResults();

			

			JSONObject resp = new JSONObject(response.getResponse().getVal(1));
			int found = resp.getInt("numFound");

			BrowserSearchVO s = new BrowserSearchVO();
			s.setUrl(searchurl);

			String ph = "Search for Address";
			if (Operator.hasValue(searchField)) { ph = searchField; }
			s.setPlaceholder(ph);

			h.setSearch(s);
			h.setLabel("LSO BROWSER");
			h.setQuery(searchField);
			h.setFound(resp.getInt("numFound"));
			h.setQuerytime(response.getQTime());
			h.setStatus(response.getStatus());
			
			
			
			String[] op = new String[1];
			op[0] = "inactive";
			h.setOptions(op);
			
			if(found < 1) {
				h.setMessage("No Results found");
			}
			
			int dsize = documents.size();
			for (int i=0; i < dsize; i++) {
				SolrDocument doc = documents.get(i);
				BrowserItemVO o = new BrowserItemVO();

				String title = "";
				try { title = (String) doc.getFieldValue("title"); } catch (Exception e) { }
				o.setTitle(title);

				String description = "";
				try { description = (String) doc.getFieldValue("description"); } catch (Exception e) { }
				o.setDescription(description);

				String id = "";
				try { id = (String) doc.getFieldValue("id"); } catch (Exception e) { }
				o.setDataid(id);
				o.setId(id);

				o.setEntity("lso");
				o.setType("lso");
				o.setDomain(Config.rooturl());
				o.setSub("project");
				o.setLink("summary");

				try {
					String hh = (String) doc.getFieldValue("hardholds");
					if (Operator.toInt(hh) > 0) {
						o.addAlert("hardhold");
					}
				}
				catch (Exception e) {}

				try {
					String sh = (String) doc.getFieldValue("softholds");
					if (Operator.toInt(sh) > 0) {
						o.addAlert("softhold");
					}
				}
				catch (Exception e) {}

				String alladdress = "";
				try { alladdress = (String) doc.getFieldValue("alladdress"); } catch (Exception e) { }
				if(!Operator.equalsIgnoreCase(alladdress, "null")) {
					o.setChildren(1);
				}
//				o.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+doc.getFieldValue("id"));
//				o.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_type=lso&_typeid="+doc.getFieldValue("id")+"&_id="+doc.getFieldValue("id"));
			//	o.put("link",Config.rooturl()+"/cs/sub.jsp?id="+doc.getFieldValue("id")"));
				ro.add(o);
				
			}
			b.setHeader(h);
			BrowserItemVO[] rs = ro.toArray(new BrowserItemVO[ro.size()]);
			b.setRoot(rs);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return b;
	}*/
	
	/*public static BrowserVO browse(String id){
		return browse(id,"active");
	}*/

	public static BrowserVO browse(String id,String option){
		BrowserVO b = new BrowserVO();
		try {
			
			BrowserHeaderVO h = new BrowserHeaderVO();

			BrowserSearchVO srch = new BrowserSearchVO();
			srch.setUrl(searchurl);
			srch.setPlaceholder("Search for Address");
			h.setSearch(srch);
			h.setLabel("ADDRESS BROWSER");
			h.setDataid(Operator.toString(id));
			h.setType("LSO");
			
			String active ="";
		
			
//			srch.setOption(o[0]);

			if (Operator.hasValue(id) && !id.startsWith("menu")) {
				String o[] = new String[3];
				o[0] ="Active";
				o[1] ="Inactive";
				o[2] ="Description";
				h.setOptions(o);
				if(Operator.equalsIgnoreCase(option, "active")|| !Operator.hasValue(option)){
					active ="Y";
					h.setOption("Active");
				}
				else if(Operator.equalsIgnoreCase(option, "description")){
					active ="Y";
					h.setOption("Description");
				}
				else {
					active ="N";
					h.setOption("Inactive");
				}

				ArrayList<BrowserItemVO> r = new ArrayList<BrowserItemVO>();
				ArrayList<BrowserItemVO> i = new ArrayList<BrowserItemVO>();

				String command = LsoSQL.browse(id, active);
				Sage db = new Sage();
				db.query(command);

				while(db.next()) {
					BrowserItemVO vo = new BrowserItemVO();
					if (Operator.equalsIgnoreCase(option, "description")) {
						if (db.hasValue("DESCRIPTION")) {
							vo.setTitle(db.getString("DESCRIPTION"));
							vo.setDescription(db.getString("ADDRESS") + ": " + db.getString("DESCRIPTION"));
						}
						else {
							vo.setTitle(db.getString("ADDRESS"));
							vo.setDescription(db.getString("ADDRESS"));
						}
					}
					else {
						vo.setTitle(db.getString("ADDRESS"));
						if (db.hasValue("DESCRIPTION")) {
							vo.setDescription(db.getString("ADDRESS") + ": " + db.getString("DESCRIPTION"));
						}
						else {
							vo.setDescription(db.getString("ADDRESS"));
						}
					}
					vo.setId(db.getString("ID"));
					vo.setDataid(db.getString("ID"));
					vo.setChildren(db.getInt("CHILDREN"));
					if (db.getInt("HARD_HOLDS") > 0) {
						vo.addAlert("hardhold");
					}
					if (db.getInt("SOFT_HOLDS") > 0) {
						vo.addAlert("softhold");
					}
					vo.setEntity("lso");
					vo.setType("lso");
					vo.setSub("project");
					vo.setLink("summary");
					vo.setDomain(Config.rooturl());
					if (db.equals("CURR", "Y")) {
						if (db.getInt("PARENT_ID") > 0 && db.getInt("GRANDPARENT_ID") > 0) {
							h.addParent(db.getString("PARENT_ID"), "lso");
							h.addParent(db.getString("GRANDPARENT_ID"), "lso");
						}
						else if (db.getInt("PARENT_ID") > 0) {
							String[] s = new String[1];
							s[0] = db.getString("PARENT_ID");
							h.addParent(db.getString("PARENT_ID"), "lso");
						}
					}
					String node = db.getString("NODE");
					if (node.equalsIgnoreCase("ROOT")) {
						r.add(vo);
					}
					else {
						if(!node.equalsIgnoreCase("CURRENT")){
							i.add(vo);
						}
					}
				}

				db.clear();

				BrowserItemVO[] rs = r.toArray(new BrowserItemVO[r.size()]);
				b.setRoot(rs);
				BrowserItemVO[] is = i.toArray(new BrowserItemVO[i.size()]);
				b.setItems(is);
			}

			b.setHeader(h);
		
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		return b;
	}
	
	public static JSONObject getChildren(int id){
		return getChildren(id,"active");
	}

	public static JSONObject getChildren(int id,String option){
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
			
			String active ="";
			if(Operator.equalsIgnoreCase(option, "active")){
				active ="Y";
			}else {
				active ="N";
			}
			
			String command = LsoSQL.getChildren(id,active);
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
					if(Operator.hasValue(db.getString("childrens"))){
						root.put("children",Config.rooturl()+"/cs/lso/childrens.jsp?id="+db.getString("id"));
					}
					root.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+db.getString("id"));
					
					root.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_type=lso&_typeid="+db.getString("id")+"&_id="+db.getString("id"));
					/*if(db.getInt("LKUP_LSO_TYPE_ID")==1){
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}else {
						root.put("link",Config.rooturl()+"/cs/details.jsp?_ent=lso&_type=lsoSummary&_id="+db.getString("id"));
					}
					*/
					
					r.put(root);
					
				}else {
					JSONObject i = new JSONObject();
					i.put("title",db.getString("title"));
					i.put("description",db.getString("description"));
					i.put("dataid",db.getString("id"));
					i.put("id",db.getString("id"));
					if(Operator.hasValue(db.getString("childrens"))){
						i.put("children",Config.rooturl()+"/cs/lso/childrens.jsp?id="+db.getString("id"));
					}
					i.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+db.getString("id"));
					
					//if(db.getInt("LKUP_LSO_TYPE_ID")==1){
						i.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_type=lso&_typeid="+db.getString("id")+"&_id="+db.getString("id"));
					//}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
					//	i.put("link",Config.rooturl()+"/cs/details.jsp?type=S&id="+db.getString("id"));
					//}else {
						//i.put("link",Config.rooturl()+"/cs/details.jsp?type=O&id="+db.getString("id"));
					//}
					items.put(i);
				}
			}
			
			o.put("header", h);
			o.put("root", r);
			o.put("items", items);
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return o;
	}

	public static BrowserItemsVO panels(String entity, int lsoid) {
		BrowserItemsVO r = new BrowserItemsVO();
		String command = LsoSQL.getLso(lsoid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {

			BrowserItemVO e = new BrowserItemVO();
			e.setEntity(entity);
			e.setType(entity);
			e.setId(db.getString("ID"));
			e.setDataid(db.getString("ID"));
			r.addPanel("entity", e);

			BrowserItemVO t = new BrowserItemVO();
			t.setEntity(entity);
			t.setType("project");
			t.setId(db.getString("ID"));
			r.addPanel("type", t);

			BrowserItemVO d = new BrowserItemVO();
			d.setEntity(entity);
			d.setType("lso");
			d.setDataid(db.getString("ID"));
			d.setId(db.getString("ID"));
			r.addPanel("detail", d);
		}
		db.clear();
		return r;
	}
	
	public static JSONObject getDetailGroup(String type,int id){
		JSONObject g = new JSONObject();
		try{
			//getGroups
			g.put("id", id);
			g.put("pub", "Y");
			if(type.equalsIgnoreCase("L")){
				g.put("group","LAND DETAILS");
			}else if(type.equalsIgnoreCase("S")){
				g.put("group","STRUCTURE DETAILS");
			}else {
				g.put("group","OCCUPANCY DETAILS");
			}
			
			
			JSONArray obj = new JSONArray();
			
			String command = LsoSQL.getDetails(id,"Y");
			Sage db = new Sage();
			db.query(command);
			
			
			
			
			
			if(db.next()){
				JSONObject ob = new JSONObject();
				
				ob = new JSONObject();
				ob.put("id", 1);
				ob.put("order", 1);
				ob.put("fieldid", "DESCRIPTION");
				ob.put("type", "String");
				ob.put("itype", "text");
				ob.put("field", "Description");
				ob.put("value", db.getString("DESCRIPTION"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob); 
				
				
				
				ob = new JSONObject();
				ob.put("id", 2);
				ob.put("order", 2);
				ob.put("fieldid", "CREATED_DATE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "datetime");
				ob.put("itype", "uneditable");
				ob.put("field", "Created Date");
				ob.put("value", db.getString("CREATED_DATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				ob = new JSONObject();
				ob.put("id", 3);
				ob.put("order", 3);
				ob.put("fieldid", "ADDRESS");
				ob.put("type", "String");
				ob.put("itype", "text");
				ob.put("field", "Address");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("ADDRESS"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				ob = new JSONObject();
				ob.put("id", 4);
				ob.put("order", 4);
				ob.put("fieldid", "UPDATED_DATE");
				ob.put("type", "datetime");
				ob.put("itype", "uneditable");
				ob.put("field", "Updated Date");
				ob.put("value", db.getString("UPDATED_DATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				
				ob = new JSONObject();
				ob.put("id", 5);
				ob.put("order", 5);
				ob.put("fieldid", "CITY");
				ob.put("type", "String");
				ob.put("itype", "uneditable");
				ob.put("field", "City");
				ob.put("value", db.getString("CITY"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				
				
				
				
				ob = new JSONObject();
				ob.put("id", 6);
				ob.put("order", 6);
				ob.put("fieldid", "PRIMAR");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "boolean");
				ob.put("itype", "boolean");
				ob.put("field", "Primary");
				ob.put("value", db.getString("PRIMAR"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				ob.put("choices", Choices.getChoicesyn());
				obj.put(ob);
				
				
				//cc
				
				
				ob = new JSONObject();
				ob.put("id", 7);
				ob.put("order", 7);
				ob.put("fieldid", "STATE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("field", "State");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("STATE"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				//up
				
				
				ob = new JSONObject();
				ob.put("id", 8);
				ob.put("order", 8);
				ob.put("fieldid", "ACTIVE");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "boolean");
				ob.put("itype", "boolean");
				ob.put("field", "Active");
				ob.put("value", db.getString("ACTIVE"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				ob.put("choices", Choices.getChoicesyn());
				
				
				
				obj.put(ob);
				
				
				ob = new JSONObject();
				ob.put("id", 9);
				ob.put("order", 9);
				ob.put("fieldid", "ZIP");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("field", "Zip");
				ob.put("itype", "uneditable");
				ob.put("value", db.getString("ZIP"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				ob = new JSONObject();
				ob.put("id", 9);
				ob.put("order", 9);
				ob.put("fieldid", "ID");
				ob.put("group", "LAND DETAILS");
				ob.put("type", "String");
				ob.put("itype", "uneditable");
				ob.put("field", "ID");
				ob.put("value", db.getString("ID"));
				ob.put("link", "");
				ob.put("alert", "");
				
				ob.put("required", "Y");
				obj.put(ob);
				
				if(db.getInt("LKUP_LSO_TYPE_ID")==3){
					ob = new JSONObject();
					ob.put("id", 10);
					ob.put("fieldid", "UNIT");
					ob.put("order", 10);
					ob.put("type", "String");
					ob.put("field", "UNIT");
					ob.put("value", db.getString("UNIT"));
					ob.put("link", "");
					ob.put("alert", "");
					
					ob.put("required", "Y");
					obj.put(ob);
					
					g.put("unit",db.getString("UNIT"));
				}
				g.put("unit",db.getString("UNIT"));
				g.put("address",db.getString("ADDRESS"));
				g.put("lsoId",db.getInt("ID"));
				g.put("ctypeid", db.getInt("LKUP_LSO_TYPE_ID"));
			}
			g.put("obj",obj);
			
			/*if(db.getInt("LKUP_LSO_TYPE_ID")==1){
				g.put("editurl", Nav.getFormEditUrl("L",id,"landdetail_"));
			}else if(db.getInt("LKUP_LSO_TYPE_ID")==2){
				g.put("editurl", Nav.getFormEditUrl("S",id,"structdetail_"));
			}else {
				g.put("editurl", Nav.getFormEditUrl("O",id,"occdetail_"));
			}
			*/
			
			db.clear();
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	/*public static JSONArray getLandDetails(int id){
		JSONArray obj = new JSONArray();
		try{
			
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}*/
	
	public static JSONArray getDivisionsDetails(int id){
		JSONArray obj = new JSONArray();
		try{
			String command = LsoSQL.getDivisionsDetails(id);
			Sage db = new Sage();
			db.query(command);
			
			
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("order", 1);
				ob.put("type", "String");
				ob.put("field", db.getString("FIELD"));
				ob.put("value", db.getString("FIELD_VALUE"));
				ob.put("link", "");
				ob.put("alert", "");
				ob.put("required", "Y");
				obj.put(ob);
				
				
				
			}
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	
	public static ObjVO[] getDivisionsDetails2(int id){
		ObjVO[] obj= new ObjVO[0];
		try{
			String command = LsoSQL.getDivisionsDetails(id);
			Sage db = new Sage();
			db.query(command);
			int sz = db.size();
			if(sz>0){
				obj  = new ObjVO[sz];
			}
			int count =0 ;
			
			while(db.next()){
				obj[count] = (FieldObjects.getFieldObjectVO(db.getInt("ID"), 1, db.getString("ID"), "String", "String",  db.getString("FIELD"), db.getString("FIELD_VALUE"), "", "", "Y"));
				count++;
			}
		
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	
	public static JSONObject getSetBack(int id,int typeId){
		JSONObject g = new JSONObject();
		try{
		
		g.put("id", 1);
		g.put("pub", "Y");
		g.put("group","SETBACK DETAILS");
		
		JSONObject f = new JSONObject();
		JSONArray fields = new JSONArray();
		
		f.put("FT","FT");
		f.put("INCH","INCH");
		f.put("COMMENTS","COMMENTS");
		
		fields.put(f.get("FT"));
		fields.put(f.get("INCH"));
		fields.put(f.get("COMMENTS"));
	
	
		
		JSONArray index = new JSONArray();
		
		index.put(f.get("FT"));
		index.put(f.get("INCH"));
		
		g.put("index",index);
		g.put("fields",fields);
		
		
		g.put("obj",getSetBackArray(id, typeId));
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static JSONArray getSetBackArray(int id,int typeId){
		JSONArray obj = new JSONArray();
		try{
			
			obj.put(getSetBackDetails(id, typeId).get("FRONT"));
			obj.put(getSetBackDetails(id, typeId).get("REAR"));
			obj.put(getSetBackDetails(id, typeId).get("SIDE1"));
			obj.put(getSetBackDetails(id, typeId).get("SIDE2"));
			
			
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return obj;
	}
	
	public static JSONObject getSetBackDetails(int id,int typeId){
		JSONObject o = new JSONObject();
		try{
			String command = LsoSQL.getSetBack(id,typeId);
			Sage db = new Sage();
			db.query(command);
			
			
			
		
			//HashSet
			while(db.next()){
				JSONObject ob = new JSONObject();
				ob.put("id", db.getInt("ID"));
				ob.put("field", db.getString("TYPE"));
				ob.put("order", 1);
				ob.put("type", "Map");
				ob.put("required", "N");
				JSONObject f = new JSONObject();
				
				JSONObject v = new JSONObject();	
			
				/*StringBuilder sb = new StringBuilder();
				sb.append(db.getString("FT")).append("'").append(db.getString("INCHES")).append(" ").append(db.getString("COMMENTS"));*/
				
				v.put("value", db.getString("FT"));
				v.put("link", "");
				v.put("type", "String");
				f.put("FT", v);
				
				v = new JSONObject();	
				v.put("value", db.getString("INCHES"));
				v.put("link", "");
				v.put("type", "String");
				f.put("INCH", v);
				
				v = new JSONObject();	
				v.put("value", db.getString("COMMENTS"));
				v.put("link", "");
				v.put("type", "String");
				f.put("COMMENTS", v);
				
				ob.put("values",f);
				
				o.put(db.getString("TYPE"),ob);
			}
			
		
			
			
			db.clear();
		
		} catch (Exception e){
			Logger.error(e.getMessage());
		}
		return o;
	}
	
	public static String getLsoType(int id){
		String type="";
		Sage db = new Sage();
		String command = LsoSQL.type(id);
		db.query(command);
		if(db.next()){
			type = db.getString("TYPE");
		}
		db.clear();
		return type;
	}
	
	public static ObjGroupVO getDetailGroup1(String type,int id){
		ObjGroupVO g = new ObjGroupVO();
		try{
			//getGroups
			//g.setGroupid(id+"");
			g.setPub("Y");
			
			if(type.equalsIgnoreCase("L")){
				g.setGroup("LAND DETAILS");
			}else if(type.equalsIgnoreCase("S")){
				g.setGroup("STRUCTURE DETAILS");
			}else {
				g.setGroup("OCCUPANCY DETAILS");
			}
			
			HashMap<String, String> extras = new HashMap<String, String>();
			ObjVO[] obj = new ObjVO[0];
			ArrayList<ObjVO> l = new ArrayList<ObjVO>();
			
			String command = LsoSQL.getDetails(id,"Y");
			Sage db = new Sage();
			db.query(command);
			db.next();
			
			
			l.add(FieldObjects.getFieldObjectVO(1, 1, "DESCRIPTION", "String", "String", "Description", db.getString("DESCRIPTION"), "", "", "Y"));
			
			l.add(FieldObjects.getFieldObjectVO(1, 1, "CREATED_DATE", "datetime", "uneditable", "CREATED DATE", db.getString("CREATED_DATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ADDRESS", "String", "uneditable", "ADDRESS", db.getString("ADDRESS"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "UPDATED_DATE", "datetime", "uneditable", "UPDATED DATE", db.getString("UPDATED_DATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "CITY", "String", "uneditable", "CITY", db.getString("CITY"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "PRIMAR", "boolean", "boolean", "PRIMARY", db.getString("PRIMAR"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "STATE", "String", "uneditable", "STATE", db.getString("STATE"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ZIP", "String", "uneditable", "ZIP", db.getString("ZIP"), "", "", "Y")); 
			l.add(FieldObjects.getFieldObjectVO(1, 1, "ID", "String", "uneditable", "ID", db.getString("ID"), "", "", "Y")); 
			if(db.getInt("LKUP_LSO_TYPE_ID")==3){
				l.add(FieldObjects.getFieldObjectVO(1, 1, "UNIT", "String", "", "UNIT", db.getString("UNIT"), "", "", "Y")); 
				extras.put("unit",db.getString("UNIT"));
			}
			extras.put("unit",db.getString("UNIT"));
			extras.put("address",db.getString("ADDRESS"));
			extras.put("lsoId",db.getString("ID"));
			extras.put("ctypeid", db.getString("LKUP_LSO_TYPE_ID"));
			
			g.setObj(l.toArray(obj));
			g.setExtras(extras);
			
			db.clear();
			
		} catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	
	public static boolean saveLso(RequestVO r, Token u){
		boolean result= false;
		try {
			if (Operator.equalsIgnoreCase(r.getType(), "lso")) {
				int id = r.getTypeid();
				r.setId(Operator.toString(id));
				String command ="";
				Sage db = new Sage();
				if(id>0) {
					command = GeneralSQL.updateCommon(r,u);
					result = db.update(command);
				}
				else {
					//TODO
					/*command = ActivitySQL.insert(o
					 * bj,id,generateActivityNumber("BLDG"));
					Logger.info("********"+command);*/
				}
				
				db.clear();
			}
			
		} catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		
		return result;
	}
	
	public static ObjVO psearch(String query, int page, int max, Token u) {
		ObjVO r = new ObjVO();
		if (Operator.hasValue(query)) {
			String command = LsoSQL.psearch(query, page, max);
			r = Choices.getObj(command);
		}
		return r;
	}
	
	public static boolean blocked(String type,int typeId) {
		boolean blocked = false;
		Sage db = new Sage();
		String command = LsoSQL.blocked(type, typeId);
		if(db.query(command)&& db.next()){
			if(db.getString("ISPUBLIC").equalsIgnoreCase("N")){
				blocked = true;
			}
		}
		db.clear();
		return blocked;
	}
	
}
















