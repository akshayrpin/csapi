package csapi.impl.print;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.custom.CustomSQL;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.holds.HoldsSQL;
import csapi.impl.log.LogAgent;
import csapi.impl.sitedata.SitedataSQL;
import csshared.vo.DivisionsList;
import csshared.vo.DivisionsVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
public class PrintAgent {

	
	public static HashMap<String,String> getTemplate(RequestVO vo){
		String template =""; 
		HashMap<String,String> t = new HashMap<String,String>();
		try{
			
			
			Sage db = new Sage();
			
			String command = "";
			int ref = Operator.toInt(vo.getReference());
			
			if(ref>0){
				command =PrintSQL.getTemplate(ref);
			}else {
				command =PrintSQL.getTemplate(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), vo.getReference(), vo.getSubrequest());
			}
			
			db.query(command);
			if(db.next()){
				template = db.getString("TEMPLATE");
				
				t.put("NAME", db.getString("NAME"));
				t.put("TEMPLATE", template);
				t.put("LANDSCAPE", db.getString("LANDSCAPE"));
				t.put("PAGE_NUMBERS", db.getString("PAGE_NUMBERS"));
				t.put("PAGE_WIDTH", db.getString("PAGE_WIDTH"));
				t.put("PAGE_HEIGHT", db.getString("PAGE_HEIGHT"));
				
				t.put("MARGIN_LEFT", db.getString("MARGIN_LEFT"));
				t.put("MARGIN_RIGHT", db.getString("MARGIN_RIGHT"));
				t.put("MARGIN_TOP", db.getString("MARGIN_TOP"));
				t.put("MARGIN_BOTTOM", db.getString("MARGIN_BOTTOM"));
				t.put("FILE_DESIGN", db.getString("FILE_DESIGN"));
				t.put("PUBLIC", db.getString("ISPUBLIC"));
			}
			
			
			if(!Operator.hasValue(template)){
				command = "select * from TEMPLATE WHERE LOWER(GENERIC) = '"+Operator.sqlEscape(vo.getType().toLowerCase())+"'";
				db.query(command);
				if(db.next()){
					template = db.getString("TEMPLATE");
					t.put("NAME", db.getString("NAME"));
					t.put("TEMPLATE", template);
					t.put("LANDSCAPE", db.getString("LANDSCAPE"));
					t.put("PAGE_NUMBERS", db.getString("PAGE_NUMBERS"));
					t.put("PAGE_WIDTH", db.getString("PAGE_WIDTH"));
					t.put("PAGE_HEIGHT", db.getString("PAGE_HEIGHT"));
					
					t.put("MARGIN_LEFT", db.getString("MARGIN_LEFT"));
					t.put("MARGIN_RIGHT", db.getString("MARGIN_RIGHT"));
					t.put("MARGIN_TOP", db.getString("MARGIN_TOP"));
					t.put("MARGIN_BOTTOM", db.getString("MARGIN_BOTTOM"));
					t.put("FILE_DESIGN", db.getString("FILE_DESIGN"));
					t.put("PUBLIC", db.getString("ISPUBLIC"));
				}
			}
			
			db.clear();
			
			
			/*if(Operator.hasValue(template)){
				template=	renderTemplate(vo, template);
			}*/
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return t;
	}
	
	
	public static ResponseVO getTemplates(RequestVO vo){
		ResponseVO r = new ResponseVO();
		try{
			
			
			Sage db = new Sage();
			String command =PrintSQL.getTemplate(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), vo.getRef(), vo.getSubrequest(), vo.getReference());
			db.query(command);
			HashMap<String, String> info = new HashMap<String, String>();
			while(db.next()){
				info.put(db.getString("ID"), db.getString("NAME"));
				r.addMap(db.getString("ID"), "TYPE", db.getString("TTYPE"));
				r.addMap(db.getString("ID"), "TYPE_ID", db.getString("TID"));
				r.addMap(db.getString("ID"), "TEMPLATE", db.getString("NAME"));
			}
			r.setInfo(info);
			db.clear();
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		return r;
	}
	

	
	/*public static String renderTemplate(RequestVO r,HashMap<String,String> t){
		try{
			String template = t.get("TEMPLATE")+"";
			if(r.getGrouptype().equalsIgnoreCase("stickers")){
				ObjGroupVO[] g = ProjectAgent.print(r);
			//	template = 	parseHtmlSingle(template, g);
				
			}else if(r.getType().equals("activity")){
				JSONObject doList = listPrints(t);
				JSONArray l = getPrintDetail(r, doList);
				template = 	parseHtmlSingle(template, l);
			} else if(r.getType().equals("project")){
				ObjGroupVO[] g = ProjectAgent.print(r);
				//template = 	parseHtmlSingle(template, g);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return template;
	}*/
	
	
	
	public static String parseHtmlSingle(String r,JSONArray l) {
		try {
			
		
			for(int i=0;i<l.length();i++){
				JSONObject g = l.getJSONObject(i);
				String html = r;
				Logger.info(g.toString());
			if (Operator.hasValue(html)) {
				
					Document doc = Jsoup.parse(html);
					
					//list
					
					Elements les = doc.getElementsByAttribute("rowtype");
					for(Element e : les){
						Logger.info("new waaaaaaaaaaaaaaaaaaa"+e.html());
						String attr = e.attr("rowtype");
						if(g.has(attr)){
							String replacedList = getReplacedList(e.html(),g.getJSONArray(attr));
							e.html(replacedList);
						
						}
					}
					
					
				//	html = Operator.replace(html, "{", "<g>");
				//	html = Operator.replace(html, "}", "</g>");
					
				/*	Elements tables = doc.select("table"); //select the first table.
					Elements rows = tables.select("tr");
					Elements cols = rows.get(1).select("td");
					
					JSONArray l = g.getJSONArray("list_people");
					ArrayList<String> a = new ArrayList<String>();
					for(Element c: cols){
						a.add(c.text());
						Logger.info(c.toString());
					}
					StringBuilder sb = new StringBuilder();
						for(int k=0;k<l.length();k++){
							sb.append("<tr>");
							JSONObject lo = l.getJSONObject(k);
								for(String cname: a){
									String v = lo.getString(cname);
									sb.append("<td>").append(v).append("</td>");
								}
							sb.append("</tr>");
						}
						doc.select("table").append(sb.toString());
							
					
					   */
					    
					html = Operator.replace(html, "{", "<g>");
					html = Operator.replace(html, "}", "</g>");
				
					Elements es = doc.getElementsByTag("g");
					for(Element  e: es){
						
						if(g.has(e.text())){
							e.before(g.getString(e.text()));
							e.remove();
						}
						
						
					}
					
					
					
					html= doc.html();
					r = html;
				}
			}
			//Logger.info(html);
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return r= "";
		}
		return r;

	}
	
	
	public static String parseHtmlSingle1(String html,ObjGroupVO[] g) {
		try {
			if (Operator.hasValue(html)) {
				Document doc = Jsoup.parse(html);
				for(int k=0;k<g.length;k++){
					for(int i=0;i<g[k].getObj().length;i++){
						String label = g[k].getObj()[i].getLabel();
						String value = g[k].getObj()[i].getValue();
						Elements es = doc.getElementsByTag("span");
						for(int j=0;j<es.size();j++){
							Element  e = es.get(j);
							if(Operator.equalsIgnoreCase(label, e.text())){
								if(e.text().indexOf("_list")>0){
									e.html(value);
								}else {
									e.text(value);
								}
							}
						}	
						
						// img
						if(Operator.equalsIgnoreCase(label, "QR_CODE")){
							
							es = doc.getElementsByTag("img");
							Logger.info("Enter img"+value);
							for(int j=0;j<es.size();j++){
								Element  e = es.get(j);
								Logger.info("inside img id "+e.id());
								Logger.info("inside img src "+e.attr("src"));
								if(Operator.equalsIgnoreCase("qrcode", e.id())){
									String url = value;
									url = Operator.urlFriendly(url);
									e.attr("src", "http://beverlyhills.org/alain?qrcode="+url);
									//e.
								}
							}	
						}
						
						
					}
				}
				
				html= doc.html();
				Logger.info(html);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			return html= "";
		}
		return html;

	}
	
	public static ObjGroupVO getStandards(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		ObjVO[] oa = new ObjVO[1];
		
		Timekeeper k = new Timekeeper();
		ObjVO v = new ObjVO();
		v.setLabel("special_currentdate");
		v.setValue(k.getString("MM/DD/YYYY"));
		
		oa[0] = v;
		g.setObj(oa);
		
		return g;
	}
	
	
	public static String renderTransactionSummary(RequestVO r,String template){
		
		try{
			if(Operator.toInt(r.getId())<=0){
				r.setId(r.getTypeid()+"");
			}
			ObjGroupVO[] oa = new ObjGroupVO[2];
			oa[0] = FinanceAgent.printFinanceTransactionSummary(r);
			oa[1] = PrintAgent.getStandards(r);
			
			template =parseHtmlSingle1(template, oa);
			
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return template;
	}
	
	
	public static JSONObject listPrintsOld(String template){
		JSONObject f = new JSONObject();
		Pattern p = Pattern.compile("\\{(.*?)\\}");
		Pattern r = Pattern.compile("\\[(.*?)\\]");
		try{
			Matcher m = p.matcher(template);
			while (m.find()) {
				String type =  m.group();
				Matcher rm = r.matcher(type);
				while (rm.find()) {
					JSONObject o = new JSONObject();
					o.put("type_original", type);
					type = Operator.replace(type, "{", "");
					type = Operator.replace(type, "}", "");
					String ltype = Operator.replace(type, rm.group(), "");
					o.put("type", ltype);
					o.put("columns", rm.group());
				//	l.put(o);
					f.put(ltype, true);
					f.put(ltype+"_addl", o);
					
				}
				
				
			}	
			//eliminate duplicates in case not adding to the list
			//f.put("types", l);
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return f;
	}
	
	
	
	public static String getReplacedList(String listhtml,JSONArray list){
		StringBuilder o = new StringBuilder();
		
		try{
			String keys[] = new String[0];
			for(int i=0;i<list.length();i++){
				JSONObject p = list.getJSONObject(i);
				if(i==0){
					keys = list.getJSONObject(i).getNames(p);
				}
				String s = listhtml;
				o.append("<tr>");	
				for(String key : keys){
					Logger.info("key name : "+key+"::"+p.getString(key));
					s = Operator.replace(s, key, p.getString(key));
					
					
				}
				o.append(s);
				o.append("</tr>");
				
				
			}
			o.append("&nbsp;</br>");
			Logger.info("new list"+o.toString());
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return o.toString();
	}
	
	
	
	
	
	
	
	
	
	
	public static JSONObject listPrints(HashMap<String,String> t){
		JSONObject f = new JSONObject();
		
		try{
			String template = t.get("TEMPLATE");
			Document doc = Jsoup.parse(template);
			Elements es = doc.getElementsByAttribute("rowtype");
			JSONArray l = new JSONArray();
			//add one only same name 
			HashSet<String> s = new HashSet<String>();
			
			
			if(Operator.equalsIgnoreCase(t.get("PUBLIC"),"Y")) {
				Sage db = new Sage();
				for(Element e : es){
					db.query(" Select module from LKUP_MODULE M, REF_MODULE_ROLES R where LKUP_MODULE_ID = M.ID and LKUP_ROLES_ID = 17 and R.active = 'Y' and R = 'Y' ");
					while(db.next()) {
						String attr = e.attr("rowtype");
						if(!s.contains(attr) && Operator.contains(attr, db.getString("module"))){
							s.add(attr);
						JSONObject o = new JSONObject();
						o.put("type", attr);
						l.put(o);
						}
					}
					
				}
				f.put("lists", l);
				
				db.query(" Select module from LKUP_MODULE M, REF_MODULE_ROLES R where LKUP_MODULE_ID = M.ID and LKUP_ROLES_ID = 17 and R.active = 'Y' and R = 'Y' ");
				while(db.next()) {
				
					if(template.indexOf("{custom_")>0 && Operator.contains("custom", db.getString("module"))){
						f.put("docustom", true);
					}
					
					if(template.indexOf("{list_custom_")>0  && Operator.contains("custom", db.getString("module"))){
						f.put("docustomlist", true);
					}
					
					if(template.indexOf("{land_sd_")>0  || template.indexOf("{structure_sd_")>0 || template.indexOf("{occupancy_sd_")>0){
						f.put("dositedata", true);
					}
					
					if(template.indexOf("{division_")>0 || template.indexOf("{lso_")>0  && Operator.contains("division", db.getString("module"))){
						f.put("dodivision", true);
					}
					
					if(template.indexOf("{people_")>0  && Operator.contains("people", db.getString("module"))){
						f.put("dopeople", true);
					}
					
					if(template.indexOf("{dot_stickers")>0){
						f.put("dodotstickers", true);
					}
					
					if(template.indexOf("{dot_reprint_stickers")>0){
						f.put("dodotreprintstickers", true);
					}
					
					if(template.indexOf("{dot_renewals")>0){
						f.put("dodotrenewals", true);
					}
					
					if(template.indexOf("{onlinereceipt")>0  && Operator.contains("finance", db.getString("module"))){
						f.put("doinvoice", true);
					}
				}
				db.clear();
			} else {
				for(Element e : es){
					String attr = e.attr("rowtype");
					/*String attr = e.attr("rowtype");
					f.put(attr, true);
					if(e.hasAttr("filters")){
						f.put(attr + e.attr("filters"), getFilters(e.attr("filters")));
					}else {
						f.put(attr, true);
					}*/
					if(!s.contains(attr)){
						s.add(attr);
					JSONObject o = new JSONObject();
					o.put("type", attr);
					/*if(e.hasAttr("filters")){
						o.put("filters", getFilters(e.attr("filters")));
					}else {
						f.put(attr, true);
					}*/
					l.put(o);
					}
					
				}
				f.put("lists", l);
				
				if(template.indexOf("{custom_")>0){
					f.put("docustom", true);
				}
				
				if(template.indexOf("{list_custom_")>0){
					f.put("docustomlist", true);
				}
				
				if(template.indexOf("{land_sd_")>0  || template.indexOf("{structure_sd_")>0 || template.indexOf("{occupancy_sd_")>0){
					f.put("dositedata", true);
				}
				
				if(template.indexOf("{division_")>0 || template.indexOf("{lso_")>0){
					f.put("dodivision", true);
				}
				
				if(template.indexOf("{people_")>0){
					f.put("dopeople", true);
				}
				
				if(template.indexOf("{dot_stickers")>0){
					f.put("dodotstickers", true);
				}
				
				if(template.indexOf("{dot_reprint_stickers")>0){
					f.put("dodotreprintstickers", true);
				}
				
				if(template.indexOf("{dot_renewals")>0){
					f.put("dodotrenewals", true);
				}
				
				if(template.indexOf("{onlinereceipt")>0){
					f.put("doinvoice", true);
				}
			}
			
			
			Logger.info(f.toString());
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return f;
	}
	
	
	public static JSONArray getPrintDetail(RequestVO r,JSONObject doList){

		JSONArray o = new JSONArray();
		JSONArray p = new JSONArray();
		Sage db = new Sage();
		try{
			String command = "";
			ResponseVO rvo1 = LogAgent.getLog(r.getProcessid());

			
			if(doList.has("dopeople")){
				command = "select ( select ID from LKUP_USERS_TYPE  FOR JSON PATH, INCLUDE_NULL_VALUES  )as PEOPLE_TYPES ";
				db.query(command);
				if(db.next()){
					p = new JSONArray(db.getString("PEOPLE_TYPES"));
				}
			}
			
			
			if(r.getType().equals("project")){
				command = prepareQueryProject(r,doList,p);
			}else if(r.getType().equals("onlinereceipt")){
				String receipt = PrintUi.dotPaymentReceipt(r.getEntityid(), r.getId());
				JSONObject jo = new JSONObject();
				jo.put("onlinereceipt", receipt);

				o = new JSONArray();
				o.put(jo);
			}else {
				command = prepareQuery(r,doList,p);
			}
			
			
			db.query(command);
			if(db.next()){
				o = new JSONArray(db.getString("RESULTS"));
				
			}
			
			
			
			int counter = 0;
			
			for(int i=0;i<o.length();i++){
				Logger.info("o"+o.length()+ doList.has("dodivision"));
				if(doList.has("dodivision")){
					
					if(o.getJSONObject(i).getString("type").equalsIgnoreCase("project")){
						command = MassTemplateSQL.printAddlProject(o.getJSONObject(i).getString("type"),o.getJSONObject(i).getInt("type_id"));
					}else {
						command = MassTemplateSQL.printAddlActivity(o.getJSONObject(i).getString("type"),o.getJSONObject(i).getInt("type_id"));
					}
					
					
					db.query(command);
					while(db.next()){
						if(Operator.hasValue(db.getString("divisions_lso"))){
							JSONArray d = new JSONArray(db.getString("divisions_lso"));
							for(int j=0;j<d.length();j++){
								if(d.getJSONObject(j).has("LABEL")){
									o.getJSONObject(i).put(d.getJSONObject(j).getString("LABEL"),Operator.toString(d.getJSONObject(j).get("FIELDVALUE")));
								}
							}
						}
						
						o.getJSONObject(i).put(db.getString("LABEL"),db.getString("FIELDVALUE"));
					}
					
					DivisionsList dl = DivisionsAgent.getDivisions(o.getJSONObject(i).getString("type"), o.getJSONObject(i).getInt("type_id"));
					while(dl.next()){
						DivisionsVO d = dl.getDivision();
						o.getJSONObject(i).put(d.templateName(),d.getDivision());
						o.getJSONObject(i).put(d.templateName()+"_description",d.getDescription());
						o.getJSONObject(i).put(d.templateName()+"_info",d.getInfo());
						Logger.info(d.templateName()+"::"+d.getDivision()+"::"+d.getDescription());
					}
							
					
				}
				
				
				// holds
				command = HoldsSQL.info(o.getJSONObject(i).getString("type"),o.getJSONObject(i).getInt("type_id"), 0);
				db.query(command);
				if(db.next()){
					if(Operator.hasValue(db.getString("hold_watermark_path")) || Operator.hasValue(db.getString("hold_watermark_text"))){
						o.getJSONObject(i).put("hold_watermark_path",db.getString("hold_watermark_path"));
						o.getJSONObject(i).put("hold_watermark_text",db.getString("hold_watermark_text"));
					}
				}
				
				if(doList.has("docustom")){
					command = CustomSQL.print(o.getJSONObject(i).getString("type"),o.getJSONObject(i).getInt("type_id"));
					db.query(command);
					while(db.next()){
						
						if(Operator.hasValue(db.getString("custom"))){
							JSONArray d = new JSONArray(db.getString("custom"));
							
							for(int j=0;j<d.length();j++){
								if(d.getJSONObject(j).has("LABEL")){
									o.getJSONObject(i).put(d.getJSONObject(j).getString("LABEL"),Operator.toString(d.getJSONObject(j).get("VALUE")));
								}
							}
						}
						o.getJSONObject(i).put(db.getString("LABEL"),db.getString("VALUE"));
					}
				
				}
				
				if(doList.has("dositedata")){
					command = SitedataSQL.print(o.getJSONObject(i).getString("type"),o.getJSONObject(i).getInt("type_id"));
					db.query(command);
					while(db.next()){
						if(Operator.hasValue(db.getString("LABEL"))){
							o.getJSONObject(i).put(db.getString("LABEL"),db.getString("VALUE"));
						}
					}
				
				}
				
				
				if(doList.has("dopeople")){
					
					for(int k=0;k<p.length();k++){
						
						if(o.getJSONObject(i).has("people_"+p.getJSONObject(k).getInt("ID"))){
							JSONArray pl = new JSONArray(o.getJSONObject(i).getString("people_"+p.getJSONObject(k).getInt("ID")));
							Logger.info(pl.toString());
							if(pl.length()>0){
								JSONObject d = pl.getJSONObject(0);
								String []keys = d.getNames(d);
								for(String key : keys){
									o.getJSONObject(i).put(key,d.getString(key));
								}
							}
						}
						
					}
				
				}// end people
				
				if(doList.has("dodotstickers")){
					LogAgent.updateLog(r.getProcessid(), counter*100/o.length());
					String stickers = PrintUi.dotStrickers(o.getJSONObject(i).getInt("type_id"), Operator.toInt(r.getGroupid()), false);
					o.getJSONObject(i).put("dot_stickers",stickers);
					counter++;
				}
				
				if(doList.has("dodotreprintstickers")){
					LogAgent.updateLog(r.getProcessid(), counter*100/o.length());
					String stickers = PrintUi.dotStrickers(o.getJSONObject(i).getInt("type_id"), Operator.toInt(r.getGroupid()), true);
					o.getJSONObject(i).put("dot_reprint_stickers",stickers);
					counter++;
				}
				
				if(doList.has("dodotrenewals")){
					ResponseVO rvo = LogAgent.getLog(r.getProcessid());
					LogAgent.updateLog(r.getProcessid(), counter*100/o.length());
					String renewals = PrintUi.dotRenewals(o.getJSONObject(i).getInt("type_id"), r.getRef(), r.getGroupid(), true);
					o.getJSONObject(i).put("dot_renewals",renewals);
					counter++;
					
				}
				
				
				if(doList.has("lists")){
					for(int c=0;c<doList.getJSONArray("lists").length();c++){
						JSONObject co = doList.getJSONArray("lists").getJSONObject(c);
						String listtype = co.getString("type");
						Logger.info("HAS CUSTOMMMMMMMMMMMMMMMM"+listtype);
						if(listtype.startsWith("list_custom")){
							int groupId =0;				
							Logger.info("CAME CUSTOMMMMMMMMMMMMMMMM"+listtype);
							JSONArray filters = PrintAgent.getFilters("list_custom",listtype);
						
							if(filters.length()>0){
								for(int f=0;f<filters.length();f++){
									JSONObject fl = filters.getJSONObject(f);
									if(fl.has("filter") && fl.has("filterValue")){
										String val = Operator.sqlEscape(fl.getString("filterValue"));
										String[] v = Operator.split(fl.getString("filterValue"),",");
										if(v.length>0){
											if(Operator.toInt(v[0])<=0){
												val = "'"+Operator.replace(val, ",", "','") +"'";
											}
										}
										if(fl.getString("filter").equalsIgnoreCase("FIELD_GROUPS_ID")){
											groupId = Operator.toInt(val);
										}
											
									}
								}
							}
						
						
							command = CustomSQL.getFields(groupId);
							db.query(command);
							String fieldids ="";
							if(db.next()){
								fieldids = db.getString("COMBINED");
							}
							
							command = CustomSQL.printList(o.getJSONObject(i).getString("type"), o.getJSONObject(i).getInt("type_id"), fieldids, listtype);
							db.query(command);
							if(db.next()){
								JSONArray s = new JSONArray(db.getString("list_custom"));
								Logger.info("list_custpm ssssssssssssssssssssss"+s.toString());
								o.getJSONObject(i).put(listtype,s);
							}	
						
						}// end if listtype
						
						
						
						if(listtype.startsWith("list_summary_activities")){
							command = MassTemplateSQL.activitySummary(doList, o.getJSONObject(i).getInt("type_id"));
							db.query(command);
							if(db.next()){
								JSONArray s = new JSONArray(db.getString("summary_activities"));
								o.getJSONObject(i).put(listtype,s);
							}
								
						
						}// end if listtype
						
						
						
						
						
					}// end for
				}// end doList
					
			}
		
			
		
			
			Logger.info(o.toString());
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		
		db.clear();
		
		return o;
	}
	
	
	
	public static String prepareQuery(RequestVO r,JSONObject doList,JSONArray addl){
		
		StringBuilder sb = new StringBuilder();
		String ids = r.getTypeid()+"";
		try{
			sb.append(peopleQuery(r, doList, addl));
			
			if(Operator.hasValue(r.getId())){
				ids = r.getId();
			}
			
		}catch(Exception e){
			
		}
		//if needed to add additonal queries for masss 
		
		
		return MassTemplateSQL.getAllInOneQueryActivity(r.getType(), ids, doList, "", sb.toString(), "");
		
	
	}
	
	
	public static String prepareQueryProject(RequestVO r,JSONObject doList,JSONArray addl){
		
		StringBuilder sb = new StringBuilder();
		String ids = r.getTypeid()+"";
		try{
			
			sb.append(peopleQuery(r, doList, addl));
			
			
			if(Operator.hasValue(r.getId())){
				ids = r.getId();
			}
			
		}catch(Exception e){
			
		}
		//if needed to add additonal queries for masss 
		
		
		return MassTemplateSQL.getAllInOneQueryProject(r.getType(), ids, doList, "", sb.toString(), "");
		
	
	}
	
	public static String peopleQuery(RequestVO r,JSONObject doList,JSONArray addl){
		
		StringBuilder sb = new StringBuilder();
		try{
			for(int i=0;i<addl.length();i++){
				int id = addl.getJSONObject(i).getInt("ID");
				sb.append(" , ");
				sb.append("         (      ");
				sb.append(" SELECT ");
				sb.append(" TOP 1 ");
				sb.append(" UPPER(FIRST_NAME) +' '+ UPPER(LAST_NAME) as 'people_").append(id).append("_name' , ");
				sb.append(" UPPER(LAST_NAME) as 'people_").append(id).append("_last_name' , ");
				sb.append(" UPPER(FIRST_NAME)  as 'people_").append(id).append("_first_name', ");
				sb.append(" ADDRESS AS 'people_").append(id).append("_address', ");
				sb.append("         U.EMAIL AS 'people_").append(id).append("_email', ");
				sb.append("         P.CITY  AS 'people_").append(id).append("_city', ");
				sb.append("         P.STATE AS 'people_").append(id).append("_state', ");
				sb.append("         P.ZIP AS 'people_").append(id).append("_zip', ");
				sb.append("         P.PHONE_HOME AS 'people_").append(id).append("_phone_home', ");
				sb.append("         P.FAX AS 'people_").append(id).append("_fax', ");
				sb.append("         P.COMMENTS AS 'people_").append(id).append("_comments', ");
				sb.append("         P.PHONE_WORK AS 'people_").append(id).append("_phone_work', ");
				sb.append("         P.PHONE_CELL AS 'people_").append(id).append("_phone_cell', ");
				sb.append("         RU.LIC_NO AS 'people_").append(id).append("_lic_num', ");
				sb.append("         CONVERT(VARCHAR(10), RU.LIC_EXP_DT, 101) as 'people_").append(id).append("_lic_expiration_date' "); 
				
				
				if(r.getType().equals("project")){
					sb.append(" FROM PROJECT A ");
					sb.append(" join REF_PROJECT_USERS RAU on A.ID=RAU.PROJECT_ID AND RAU.ACTIVE = 'Y' ");
				}else {
					sb.append(" FROM ACTIVITY A ");
					sb.append(" join REF_ACT_USERS RAU on A.ID=RAU.ACTIVITY_ID AND RAU.ACTIVE = 'Y' ");
				}
				
				
				
				sb.append(" join REF_USERS RU on RAU.REF_USERS_ID=RU.ID ");
				sb.append(" JOIN USERS AS U ON RU.USERS_ID = U.ID ");
				sb.append(" left outer join PEOPLE P on U.ID = P.USERS_ID ");
				sb.append(" WHERE A.ID = MAIN_ID AND LKUP_USERS_TYPE_ID= ").append(id).append(" ORDER BY RAU.UPDATED_DATE DESC ");
				sb.append("                       ");
				sb.append("                         FOR JSON PATH )as 'people_").append(id).append("'                ");
				sb.append("                     ");
			}
			
		}catch(Exception e){
			
		}
		//if needed to add additonal queries for masss 
		
		
		return sb.toString();
		
	
	}
	
	
	
	
	
	public static boolean insertBatchLog(String processId, int percent,String title,String description,String response){
		boolean result = false;
		Sage db = new Sage();
		String command = PrintSQL.insertBatchLog(processId, percent, title, description, response);
		result = db.update(command);
		db.clear();
		return result;
		
	}
	
	public static boolean deleteBatchLog(String processId){
		boolean result = false;
		Sage db = new Sage();
		String command = PrintSQL.deleteBatchLog(processId);
		result = db.update(command);
		db.clear();
		return result;
		
	}
	
	public static JSONArray getFilters(String repl,String s){
		JSONArray a = new JSONArray();
		try{
		String filters = Operator.replace(s, repl, "");
		String filter[] = Operator.split(filters, ";");
		for(String j: filter){
			String sp[] = Operator.split(j,":");
			if(sp.length>1){
				String id = sp[0];
				String value = sp[1];
				JSONObject f = new JSONObject();
				f.put("filter", id.trim());
				f.put("filterValue", value.trim());
				a.put(f);
			}
		}
		
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
	
		return a;
		
		
	}
	
	
	public static void main(String args[]){
		//JSONArray a = new JSONArray();
		try{
			/*JSONObject o = new JSONObject();
			o.put("type","list_notes filter1:y;filter2:u;");
			a.put(o);
			
			o = new JSONObject();
			o.put("type","list_notes filter1:h;filter2:g;");
			a.put(o);
			
			for(int i=0;i<a.length();i++){
				 o = a.getJSONObject(i);
				 if(o.getString("type").startsWith(("list_notes"))){
					 Logger.info(o.getString("type"));
				 }
				
				 if(o.getString("type").startsWith(("list_review"))){
					 Logger.info(o.getString("type"));
				 }
			}*/
			
			int d =  Operator.toInt("30");
			int dsub = d+5;
			System.out.println(dsub);
			
			
		}catch(Exception e){
			
		}
	}
	
	public static int insertBatch(String processId, String filename, int userid, String templateid, String type){
		int  id = 0;
		Sage db = new Sage();
		String command = PrintSQL.insertBatch(processId, filename, userid, templateid, type);
		db.query(command);
		if(db.next())
			id =  db.getInt("ID");
		db.clear();
		return id;
	}
	
	public static void updateBatch(String id, String filename){
		Sage db = new Sage();
		String command = PrintSQL.updateBatch(id, filename);
		db.update(command);
		db.clear();
	}
	
	public static void updateBatchProcess(int id, String process){
		Sage db = new Sage();
		String command = PrintSQL.updateBatchProcess(id, process);
		db.update(command);
		db.clear();
	}
	
	public static int getBatchStatus(RequestVO vo){
		String processid = "";
		Sage db = new Sage();
		String command = PrintSQL.getBatch(vo.getGroupid());
		db.query(command);
		if(db.next()) {
			processid = db.getString("PROCESS_ID");
		}
		db.clear();
		ResponseVO r = LogAgent.getLog(processid);
		return r.getPercentcomplete();
	}
	
	
	
	
	
	public static JSONObject listAccount(int projectid){
		JSONObject g = new JSONObject();
		
		try {

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT TOP 1 ");
			sb.append(" 	LAND_ID, ");
			sb.append(" 	RPP.* ");
			sb.append(" FROM ");
			sb.append(" 	REF_LSO_PROJECT AS RLP "); 
			sb.append(" 	JOIN V_CENTRAL_ADDRESS AS VCD ON RLP.LSO_ID = VCD.LSO_ID "); 
			sb.append(" 	JOIN REF_PROJECT_PARKING AS RPP ON RLP.PROJECT_ID = RPP.PROJECT_ID "); 
			sb.append(" WHERE ");
			sb.append(" 	RLP.PROJECT_ID = ").append(projectid);
			Sage db = new Sage();
			String command = sb.toString();
			db.query(command);
			if(db.next()){
				g.put("LAND_ID", db.getInt("LAND_ID"));
				g.put("ACCOUNT_NUMBER", db.getInt("ID"));
			}
			db.clear();
	
			DivisionsList l = DivisionsAgent.getDivisions("project", projectid);
			DivisionsList dl = l.dot();
			if (dl.next()) {
				DivisionsVO dv = dl.getDivision();
				String div = dv.getDivision();
				g.put("DIVISION", div);
			}
	
	//		command = "select DIVISION  from V_CENTRAL_DIVISION VCD JOIN LKUP_DIVISIONS_TYPE  LDT  ON VCD.TYPE = LDT.TYPE WHERE ISDOT='Y' AND VCD.LSO_ID="+g.getInt("LAND_ID");
	//		db.query(command);
	//		if(db.next()){
	//			g.put("DIVISION", db.getString("DIVISION"));
	//		}
			
		}
		catch(Exception e) {
			Logger.error(e.getMessage());
		}
		return g;
	} 
	
	
	public static JSONArray getAccountsForRenewalLetter(String type){
		//JSONObject o = new JSONObject();
		JSONArray l = new JSONArray();
		Sage db = new Sage();
		
		try{
		
		String command = PrintSQL.getAccountsForRenewalLetter(type);
		db.query(command);
		while(db.next()){
			JSONObject g = new JSONObject();
			g.put("lso_address", db.getString("ADDRESS"));
			g.put("lso_city", db.getString("CITY"));
			g.put("lso_state", db.getString("STATE"));
			g.put("lso_zip", db.getString("ZIP"));
			g.put("parking_account_number", db.getString("PARKING_ACCOUNT_NUMBER"));
			g.put("ACCOUNT_NUMBER", db.getString("PARKING_ACCOUNT_NUMBER"));
			g.put("username", db.getString("USERNAME"));
			g.put("people_11_username", db.getString("USERNAME"));
			g.put("people_11_username", db.getString("USERNAME"));
			g.put("people_11_name", db.getString("FIRST_NAME")+" "+db.getString("LAST_NAME"));
			g.put("name", db.getString("FIRST_NAME")+" "+db.getString("LAST_NAME"));
			g.put("overnight_count", db.getInt("OCOUNT"));
			g.put("dot_renewals", PrintUi.dotRenewalsUi(type,db.getInt("OCOUNT")));
			
			g.put("dot_notify", PrintUi.getPermits2021(Operator.toInt(db.getString("PARKING_ACCOUNT_NUMBER"))));
			
			l.put(g);
			
		}
		
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return l;
	}
	
	
	
}