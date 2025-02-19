package csapi.search.address;

import java.time.LocalTime;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class SearchAddress {

	public static String solrurl = "http://10.14.6.19:8983/solr/address_core/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println(getSearchList("Rodeo dr",0,10).toString());
			
			LocalTime now = LocalTime.now();
			System.out.println(now);
			LocalTime later = now.plusHours(-2);
			System.out.println(later);
			/*DateFormatter f = new 
			System.out.println(later.format(formatter))*/
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*public static JSONObject getSearchList(String searchField,int start,int end) throws Exception {
		JSONObject r = new JSONObject();
		
		try{
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			//server.setParser(new JSONParser());
			SolrQuery query = new SolrQuery();
			query.setQuery(searchField);
			query.setStart(start);
			query.setRows(end);
			
			query.setParam("wt", "json");
			query.setParam("indent", "true");
			query.setParam("defType", "dismax");
			query.setParam("mm", "100%");
			
		        
			QueryResponse response = server.query(query);
			Logger.info("***********"+query);
			SolrDocumentList documents = response.getResults();
			JSONObject h = new JSONObject();
			JSONObject resp = new JSONObject(response.getResponse().getVal(1));
			//System.out.println("*numFound*"+resp.getString("numFound"));
			h.put("numFound",resp.getInt("numFound"));
			h.put("QTime",response.getQTime());
			h.put("status",response.getStatus());
			
			int dsize = documents.size();
			JSONArray a = new JSONArray();
			for (int i=0; i<dsize; i++) {
				SolrDocument doc = documents.get(i);
				JSONObject o = new JSONObject();
				o.put("title",doc.getFieldValue("title"));
				o.put("description",doc.getFieldValue("description"));
				o.put("json-id",doc.getFieldValue("id"));
				o.put("id",doc.getFieldValue("id"));
				String alladdress = doc.getFieldValue("alladdress")+"";
				if(!Operator.equalsIgnoreCase(alladdress, "null")){
					o.put("children",1);	
				}else {
					o.put("children",0);
				}
			
				a.put(o);
				
			}
			r.put("header",h);
			r.put("items",a);
			
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("content error = " + e.getMessage());
	
			}
		
		
		
		return r;
	}*/
	
	
	public static JSONObject getSearchList(String searchField,int start,int end) throws Exception {
		JSONObject r = new JSONObject();
		
		try{
			CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrurl);
			//server.setParser(new JSONParser());
			SolrQuery query = new SolrQuery();
			query.setQuery(searchField);
			query.setStart(start);
			query.setRows(end);
			
			//query.setParam("sort", "title asc");
			query.setParam("sort", "strno asc");
			
			
			query.setParam("defType", "dismax");
			query.setParam("mm", "100");
			query.setParam("wt", "json");
			query.setParam("indent", "true");
			
			if(Operator.hasValue(searchField)){
				String predir = "";
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
				
			
				if(Operator.hasValue(predir)){
					query.setParam("fq", "predir:"+predir);
				}
			}
			
			//query.setParam("sort", "strno+asc");
			
		        
			QueryResponse response = server.query(query);
			Logger.info("***********"+query);
			SolrDocumentList documents = response.getResults();
			JSONObject h = new JSONObject();
			JSONObject resp = new JSONObject(response.getResponse().getVal(1));
			//System.out.println("*numFound*"+resp.getString("numFound"));
			h.put("numFound",resp.getInt("numFound"));
			h.put("QTime",response.getQTime());
			h.put("status",response.getStatus());
			
			h.put("search", Config.rooturl()+"/cs/lso/search.jsp");
			h.put("label", "LSO BROWSER");
			h.put("q", searchField);
			
			if(resp.getInt("numFound")<1){
				
				h.put("message", "No Results found");
			}
			
			h.put("parent", "0");
			
			int dsize = documents.size();
			JSONArray a = new JSONArray();
			for (int i=0; i<dsize; i++) {
				SolrDocument doc = documents.get(i);
				JSONObject o = new JSONObject();
				o.put("title",doc.getFieldValue("title"));
				o.put("description",doc.getFieldValue("description"));
				o.put("dataid",doc.getFieldValue("id"));
				o.put("id",doc.getFieldValue("id"));
				o.put("entity","lso");
				o.put("type","land");
				
				String alladdress = doc.getFieldValue("alladdress")+"";
				if(!Operator.equalsIgnoreCase(alladdress, "null")){
					//o.put("children",1);
					o.put("children",Config.rooturl()+"/cs/lso/childrens.jsp?id="+doc.getFieldValue("id"));
				}else {
					o.put("children",0);
				}
				o.put("sub",Config.rooturl()+"/cs/lso/sub.jsp?id="+doc.getFieldValue("id"));
				o.put("link",Config.rooturl()+"/cs/summary.jsp?_ent=lso&_type=lso&_typeid="+doc.getFieldValue("id")+"&_id="+doc.getFieldValue("id"));
			//	o.put("link",Config.rooturl()+"/cs/sub.jsp?id="+doc.getFieldValue("id")"));
				a.put(o);
				
			}
			r.put("header",h);
			r.put("root",a);
			
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("content error = " + e.getMessage());
	
			}
		
		
		
		return r;
	}

}
