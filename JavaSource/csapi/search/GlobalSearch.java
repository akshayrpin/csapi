package csapi.search;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.utils.CsApiConfig;

public class GlobalSearch {

	
	public final static String INSPECTIONS_DELTA = CsApiConfig.getString("search.inspection").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String TEAM_DELTA = CsApiConfig.getString("search.team").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String PEOPLE_DELTA = CsApiConfig.getString("search.people").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String USERS_DELTA = CsApiConfig.getString("search.user").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String LOAD_INITIAL_DELTA = CsApiConfig.getString("search.global").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String FINANCE_DELTA = CsApiConfig.getString("search.finance").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String REVIEW_DELTA = CsApiConfig.getString("search.review").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String ATTACHMENTS_DELTA = CsApiConfig.getString("search.attachments").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String LEDGER_DELTA = CsApiConfig.getString("search.ledger").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	
	public final static String ADDRESS_LSO_DELTA = CsApiConfig.getString("search.address_lso").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	public final static String ADDRESS_DELTA = CsApiConfig.getString("search.address").replace("query","dataimport?command=delta-import&wt=json&indent=on");
	

	public static String spell(Cartographer map){
			
			String resp ="";
			try{
				String url = map.getString("_url");
				url = Operator.replace(url, "/query", "/spell");
				URIBuilder  o = new URIBuilder(url);
				ArrayList<NameValuePair> oparams = new ArrayList<NameValuePair>();
				oparams.add(new BasicNameValuePair("spellcheck.q",URLEncoder.encode(map.getString("q"), "UTF-8")));
				oparams.add(new BasicNameValuePair("indent","on"));
				oparams.add(new BasicNameValuePair("wt",map.getString("wt")));
				oparams.add(new BasicNameValuePair("spellcheck.maxCollations","1"));
				oparams.add(new BasicNameValuePair("spellcheck.collateParam.q.op","AND"));
				String u = o.toString();
				resp = searchSolr(u, oparams,map.getString("method"));
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
			return resp;
		}
		
	
	
	
	 public static String searchSolr(String url,ArrayList<NameValuePair> params,String format)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpGet httpget = new HttpGet();
		
		  String encoding =  CsApiConfig.getString("search.credentials.login_username")+":"+ CsApiConfig.getString("search.credentials.login_pass"); 
		  byte[] encodedBytes = Base64.encodeBase64(encoding.getBytes());
		  httpget.setHeader("Authorization", "Basic " + new String(encodedBytes));
		  
		  httpget.addHeader("content-type", "application/json");
		  
		 // httpget.setParams(arg0);
		  StringBuilder sb = new StringBuilder();
		  sb.append(url);
	      if(params.size()>0){
	    	  sb.append("?");
	      }
	      String pr = "";
	      String dt = "";
	      String fq = "";
	      String filters="";
	      for(int i=0;i<params.size();i++){
	    	 Logger.info(params.get(i)+"");
	    	 if(params.get(i).getName().equals("_fq")){
	    		 fq = params.get(i).getValue();
	    	 }else  if(params.get(i).getName().equals("_filters")){
	    		 filters = params.get(i).getValue();
	    	 }else  if(params.get(i).getName().equals("_dt")){
	    		 dt = params.get(i).getValue();
	    	 }else  if(params.get(i).getName().equals("_price")){
	    		 pr = params.get(i).getValue();
	    	 }else  if(params.get(i).getName().equals("q")){
	    		 String q = params.get(i).getValue();
		    		
	    		 if(Operator.hasValue(q) && q.indexOf("@")>0){
	    			 q = Operator.replace(q,"@","&#64;");
	    		 }else {
	    			 q = URLEncoder.encode(q, "UTF-8");
	    			
	    		 }
	    		 sb.append(params.get(i).getName()).append("=").append(q);
	    		 sb.append("&");
	    		
	    	 }
	    	 else
	    		 if(params.get(i).getName().equals("json.facet")){
	    		// sb.append(params.get(i).getName()).append("=").append(URLEncoder.encode(params.get(i).getValue(), "UTF-8"));
	    		 
	    		 //211{"type" :{ "type":"terms","field":"type","domain": {"excludeTags": "type" } },"status" :{ "type":"terms","field":"status","domain": {"excludeTags": "status" } }}
	    		// %7B%22type%22+%3A%7B+%22type%22%3A%22terms%22%2C%22field%22%3A%22type%22%2C%22domain%22%3A+%7B%22excludeTags%22%3A+%22type%22+%7D+%7D%2C%22status%22+%3A%7B+%22type%22%3A%22terms%22%2C%22field%22%3A%22status%22%2C%22domain%22%3A+%7B%22excludeTags%22%3A+%22status%22+%7D+%7D%7D
	    		 String j = params.get(i).getValue();
	    		 j = Operator.replace(j, "{", "%7b");
	    		 j = Operator.replace(j, "}", "%7d");
	    		 j = Operator.replace(j,"\"","%22");
	    		
	    		// j = Operator.replace(j,":","%20:");
	    		
	    		 sb.append(params.get(i).getName()).append("=").append(j);
	    		 sb.append("&");
	    		
	    		
	    	 }  	 
	    	 else {
	    	  sb.append(params.get(i));
	    	  sb.append("&");
	    	 }
	      }
	      
	   
	      
	  	ArrayList<String> a = solrescapeFilter(fq, filters);
	    for(String qfp : a){
	    	
	    	String k = qfp;
	    	
	    	if(k.indexOf("display_type")<0){
	    		sb.append("&fq").append("=").append(k).append("&");
	    	}
	    }
	    
	    String []dts = Operator.split(dt,"&");
	    for(String qfp : dts){
	    	sb.append("&fq").append("=").append(qfp).append("&");
	    }
	    String []prs = Operator.split(pr,"&");
	    for(String qfp : prs){
	    	sb.append("&fq").append("=").append(qfp).append("&");
	    }
	    
	      
	   
	      String s = sb.toString();
	    
	      Logger.info(s);
	      URI website = new URI(s);
	   
	      httpget.setURI(website);
		  HttpResponse response = httpclient.execute(httpget);
		  HttpEntity entity = response.getEntity();
	
			  if (entity != null) {
				  	InputStream instream = entity.getContent();
			          	BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
			    	    String newLine = System.getProperty("line.separator");
			    	    String line;
			    	    while ((line = reader.readLine()) != null) {
			    	        out.append(line);
			    	        out.append(newLine);
			    	    }
			    	instream.close();    
			      }
			      
			  }
			  catch(Exception ex){
		    	  out.append("Error while getting response "+ex.getMessage());  
		      } 
			  return out.toString();
		  }
	 
	 
	 
	 
	 
	 public static ArrayList<String> solrescapeFilter(String fq, String filter){
			ArrayList<String> a = new ArrayList<String>();
			try{
				String t = filter;
				String type[] = Operator.split(t,",");
				StringBuilder sb = new StringBuilder();
				Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(fq);
				int i =0; 
				
				
				while(m.find()) {
				    sb = new StringBuilder();
				    String g = m.group(1);
				  
				    String s = Operator.replace(g," ","%5C%20");
				    s = Operator.replace(s, "(", "%5C%28");
					
					s = Operator.replace(s, ")", "%5C%29");
					s = Operator.replace(s, "," ,"%20");
					s = Operator.replace(s,"&","%26");
				    
				    sb.append("%7B!tag=").append(type[i]).append("%7D").append(type[i]).append(":(").append(s).append(")");
				    a.add(sb.toString());
				    i = i+1;
				}
				
				
				
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
			return a;
		}
	 
	 
	


	 public static boolean index(String url) {
		 updateGlobalSearch(url);
		 return true;
//		 boolean res = true;
//		 boolean r = true;
//		 int i =0;
//		 while (r) {
//			String s = getIndexStatus(url);
//			if (!Operator.hasValue(s)) {
//				r = false;
//			}
//			else if (!Operator.equalsIgnoreCase(s, "busy")&& i>1) {
//				r = false;
//			}
//			i++;
//			
//			if(i==900){
//				break;
//			}
//			
//		 }
//		 return res;
	 }
	 
	 public static boolean indexWait(String url) {
		
		 boolean res = true;
		 boolean r = true;
		 int i =0;
		 while (r) {
			String s = getIndexStatus(url);
			if (!Operator.hasValue(s)) {
				r = false;
			}
			else if (!Operator.equalsIgnoreCase(s, "busy")&& i>1) {
				r = false;
			}
			i++;
			
			if(i==900){
				break;
			}
			
		 }
		 return res;
	 }

	 public static String getIndexStatus(String url) {
			String r = "";
			try {
				/*String content = FileUtil.getUrlContent(url);
				JSONObject o = new JSONObject(content);
				r = o.getString("status");
				Logger.highlight(r);*/
				Logger.info(url);
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet();
				
				String encoding =  CsApiConfig.getString("search.credentials.login_username")+":"+ CsApiConfig.getString("search.credentials.login_pass"); 
				byte[] encodedBytes = Base64.encodeBase64(encoding.getBytes());
				httpget.setHeader("Authorization", "Basic " + new String(encodedBytes));
				  
				httpget.addHeader("content-type", "application/json");
				URI website = new URI(url);
				httpget.setURI(website);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				StringBuilder out = new StringBuilder();
				  if (entity != null) {
					  	InputStream instream = entity.getContent();
				        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				    	String newLine = System.getProperty("line.separator");
			    	    String line;
			    	    while ((line = reader.readLine()) != null) {
			    	        out.append(line);
			    	        out.append(newLine);
			    	    }
			    	instream.close();    
			      }
				      
				 
				String content = out.toString();
				JSONObject o = new JSONObject(content);
				r = o.getString("status");
				Logger.highlight(r);
				
				
			}
			catch (Exception e) {
				Logger.error(e.getMessage());
			}
			return r;
		}

	 public static void updateGlobalSearch(String url) {
			Logger.info("URRR"+url);
		 	final String u = url;
			// start process in new thread
			try{
				new Thread(new Runnable() {
					public void run() {
						try {
							//GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
							indexThread(u);
						}
						catch (Exception e) {
							Logger.error(e.getMessage());
						}
	                }
	            }).start();
	        }
	        catch(Exception e){
				Logger.error(e.getMessage());
	        }
	 }
	 
	 
	 public static boolean indexThread(String url) {
		 boolean res = true;
		 boolean r = true;
		 Logger.info("URRR"+url);
		 int i =0;
		 while (r) {
			String s = getIndexStatus(url);
			if (!Operator.hasValue(s)) {
				r = false;
			}
			else if (!Operator.equalsIgnoreCase(s, "busy")&& i>1) {
				r = false;
			}
			i++;
			
			if(i==900){
				break;
			}
			Logger.info("URRR"+i);
		 }
		 return res;
	 }

}
