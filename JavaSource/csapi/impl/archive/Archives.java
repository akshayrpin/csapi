package csapi.impl.archive;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import csapi.impl.activity.ActivitySQL;
import csapi.impl.attachments.AttachmentsSQL;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import alain.core.db.Sage;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class Archives {

	
	
	public static final String _url="http://edocs.beverlyhills.org/appxtenderrest/";
	//public static final String _url="http://lyon/appxtenderrest/";
	//public static final String _dsn="COBH_DEV";
	public static final String _dsn="AX-COBH_City";
	public static final String _username="axdeveloper";
	public static final String _password="D3v3loper";
	
	//public static final String _username="AX_Public";
	//public static final String _password="Iseed0cument$";
	
	public static final String _usernameUrl="ax_publicpal";
	public static final String _passwordUrl="D0cumentum!";
	
	//public static final String _usernameUrl="axdeveloper";
	//public static final String _passwordUrl="D3v3loper";
	
	private static final Map<String, String> _urls = prepareMap();

    private static Map<String, String> prepareMap() {
        Map<String, String> hashMap = new HashMap<String, String>();
    
        hashMap.put("axadhocqueryresults", _url+"api/AXDataSources/"+_dsn+"/axadhocqueryresults/");
        hashMap.put("axapps", _url+"api/AXDataSources/"+_dsn+"/axapps");
        hashMap.put("axappfields", _url+"api/AXDataSources/"+_dsn+"/axappfields/");
        hashMap.put("axdocs", "/axdocs/");
        hashMap.put("docurl", "http://edocs.beverlyhills.org/AppXtender/datasources/AX-COBH_City/IDocument/?AppId=30&DocId=");
        hashMap.put("linkurl", "http://www.beverlyhills.org/d/doc.jsp?AppId=30&DocId=");
        hashMap.put("linkurladdress", "http://www.beverlyhills.org/d/doc.jsp?AppId=20&DocId=");
       // hashMap.put("axfulltextquery", "/axdocs/");
        hashMap.put("axsavedqueryresults", _url+"api/AXDataSources/"+_dsn+"/axsavedqueryresults/77");// axdatasources/{datasource}/axsavedqueryresults/{queryid}

        return hashMap;
    }
    
    
    public static void main(String args[]){
    	//System.out.println(getDocumentsAddress(9100, "", "Wilshire Blvd", 0, 0).toString());
    	
    	System.out.println("TEST :: "+Operator.isEmail("enelitefire@aol.com "));
    	//int i = 145;
    	//System.out.println(i%5);
    	//System.out.println(ActivitySQL.checkMaxDayTimePerYear(12334));
    }
	
    
    
    public static ObjGroupVO getArchive(ObjGroupVO result,String type,int typeId,int noofrecords,int skip,boolean staff){
    	try{
	    	Sage db = new Sage();
	    	String command = AttachmentsSQL.documentum(type, typeId);
	    	
	    	db.query(command);
	    	int anumber =0;
	    	String adirection="";
	    	String astreet="";
	    	String actnbr="";
	    	String aunit="";
	    	if(db.next()){
	    		anumber = db.getInt("STR_NO");
	    		adirection=db.getString("PRE_DIR");
	    		astreet = db.getString("STR_NAME");
	    		//aunit = db.getString("UNIT");
	    		if(Operator.hasValue(db.getString("STR_TYPE"))){
	    			astreet = astreet+" "+ db.getString("STR_TYPE");
	    		}
	    		actnbr = db.getString("ACT_NBR");
	    	}
	    	db.clear();
	    	result = getArchive(result,anumber, adirection, astreet,aunit, actnbr, noofrecords, skip,staff);
	    	
    	}catch(Exception e){
    		Logger.error(e.getMessage());
    	}
    	
    	return result;
    	
    }
    
    
    public static ObjGroupVO getArchive(ObjGroupVO result,int anumber, String adirection, String astreet, String aunit, String actnbr, int noofrecords,int skip,boolean staff){
		
		ObjMap[] l = new ObjMap[0];
		try{
			if(noofrecords<=0){ noofrecords = 10; 	}
			if(skip<=0){ skip=0; 	}
			
			

			JSONObject a= new JSONObject();
			JSONArray ga = new JSONArray();
			
			JSONObject g= new JSONObject();
			boolean dr = false;
			if(anumber>0){
				g.put("Name", "ADDRESS-NUMBER");
				g.put("Value", anumber);
				ga.put(g);
			}
			if(Operator.hasValue(adirection)){
				g= new JSONObject();
				g.put("Name", "ADDRESS - DIRECTION");
				g.put("Value", adirection);
				ga.put(g);
			}
			if(Operator.hasValue(astreet)){
				g= new JSONObject();
				g.put("Name", "ADDRESS-STREET");
				g.put("Value", astreet);
				ga.put(g);
				dr = true;
			}
			if(Operator.hasValue(aunit)){
				g= new JSONObject();
				g.put("Name", "ADDRESS - UNIT/SUITE");
				g.put("Value", aunit);
				ga.put(g);
				dr = true;
			}
			
			if(Operator.hasValue(actnbr)){
				g= new JSONObject();
				g.put("Name", "PERMIT NUMBER");
				g.put("Value", actnbr);
				ga.put(g);
				dr = true;
				
				
			}
			
			
			if(dr){
			
				if(!staff){
					g= new JSONObject();
					g.put("Name", "INTERNAL");
					g.put("Value", "No");
					ga.put(g);
				}
				
				a.put("Indexes", ga);
				
				StringBuilder sb = new StringBuilder(_urls.get("axadhocqueryresults"));
				sb.append(20);
				
				
				
				
				JSONObject f= new JSONObject();
				f.put("QueryOperator", 0);
				f.put("SearchType", 0);
				f.put("Thesaurus", true);
				f.put("Value", "");
				
				a.put("fullText", f);
				
				a.put("IsIncludingPreviousRevisions", false);
				
				String json = a.toString();
				Logger.info("Input"+json);
				
				StringBuilder params = new StringBuilder();
				params.append("?");
				params.append("$top=").append(noofrecords);
				params.append("&");
				params.append("$skip=").append(skip);
				params.append("&");
				params.append("$inlinecount=AllPages");
				params.append("&");
				params.append("$orderby=").append("ID").append("%20desc");
				sb.append(params.toString());
				String s = doPost(sb.toString(), json);
				a = new JSONObject(s);
				
				Logger.info("Output:"+a.toString());
				String [] fields =result.getFields();
				if(a.has("entries")){
						
						l = new ObjMap[a.getJSONArray("entries").length()];
						int count =0;
						for(int i=0;i<a.getJSONArray("entries").length();i++){
							JSONObject o = a.getJSONArray("entries").getJSONObject(i);
							JSONArray  op = o.getJSONArray("indexvalues");
							JSONObject r = new JSONObject();
							r.put("ID",o.getInt("ID"));
							
							r.put("NUMBER",op.optString(0));
							r.put("ADDRESS",op.optString(1)+" "+op.optString(2)+" "+op.optString(3)+" "+op.optString(4));
							r.put("TYPE",op.optString(5));
							r.put("DESCRIPTION",op.optString(6));
							//r.put("FILE CLASSIFICATION",op.optString(7));
							r.put("DATE",op.optString(8));
							r.put("url",_urls.get("linkurladdress")+o.getInt("ID"));
	
							
							
							
							
							l[count] = getObjMap(o.getInt("ID"), r, fields);;
							count++;
							
							
							
							
						}
						result.setValues(l);
						result.setCustomsize(a.getInt("count"));
				}
				Logger.info(l.length+"OBJ MAP DONEEEEEEEEEEEEE");
			}
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		return result;
		
	}
    
    public static JSONObject getDocuments(String type,int typeId){
    	JSONObject docs = new JSONObject();
    	try{
	    	Sage db = new Sage();
	    	String command = AttachmentsSQL.documentum(type, typeId);
	    	db.query(command);
	    	int anumber =0;
	    	String adirection="";
	    	String astreet="";
	    	if(db.next()){
	    		anumber = db.getInt("STR_NO");
	    		adirection=db.getString("PRE_DIR");
	    		astreet = db.getString("STR_NAME");
	    		if(Operator.hasValue(db.getString("STR_TYPE"))){
	    			astreet = astreet+" "+ db.getString("STR_TYPE");
	    		}
	    	}
	    	db.clear();
	    	docs = getDocumentsAddress(anumber, adirection, astreet, 0, 0);
	    	
    	}catch(Exception e){
    		Logger.error(e.getMessage());
    	}
    	
    	return docs;
    	
    }
    
    
    public static JSONObject getDocumentsAddress(int anumber, String adirection, String astreet, int noofrecords,int skip){
		//ADDRESS-NUMBER	
	    //ADDRESS - DIRECTION	
		//ADDRESS-STREET
		/*int anumber = map.getInt("ADDRESSNUMBER",0);
	 	String adirection = map.getString("ADDRESSDIRECTION","");
		String astreet = map.getString("ADDRESSSTREET","");*/
		JSONObject l = new JSONObject();
		try{
		

			if(noofrecords<=0){
				noofrecords = 5000;
			}
			if(skip<=0){
				skip=0;
			}
			
			String credentials = "";

			JSONObject a= new JSONObject();
			JSONArray ga = new JSONArray();
			
			JSONObject g= new JSONObject();
			
			if(anumber>0){
				g.put("Name", "ADDRESS-NUMBER");
				g.put("Value", anumber);
				ga.put(g);
			}
			if(Operator.hasValue(adirection)){
				g= new JSONObject();
				g.put("Name", "ADDRESS - DIRECTION");
				g.put("Value", adirection);
				ga.put(g);
			}
			if(Operator.hasValue(astreet)){
				g= new JSONObject();
				g.put("Name", "ADDRESS-STREET");
				g.put("Value", astreet);
				ga.put(g);
			}
			
			/*g= new JSONObject();
			g.put("Name", "SCAN DATE");
			g.put("Value", astreet);
			ga.put(g);*/
			
			a.put("Indexes", ga);
			
			StringBuilder sb = new StringBuilder(_urls.get("axadhocqueryresults"));
			sb.append(20);
			/*sb.append("?");
			sb.append("$inlinecount=allpages");*/
			
			JSONObject oa= new JSONObject();
			oa.put("sortfieldid", 12);
			String oas = "%7b12%7d";
			System.out.println(oas);
			
			JSONObject f= new JSONObject();
			f.put("QueryOperator", 0);
			f.put("SearchType", 0);
			f.put("Thesaurus", true);
			f.put("Value", "");
			
			a.put("fullText", f);
			
			a.put("IsIncludingPreviousRevisions", false);
			
			String json = a.toString();
			Logger.info("Input"+json);
			//TODO paginations not working as intended
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append("$top=").append(25);
			params.append("&");
			params.append("$skip=").append(0);
			//params.append("&");
			//params.append("$inlinecount=AllPages");
			params.append("&");
			params.append("$orderby=").append("ID").append("%20desc");
			//$orderby={sortfieldid} desc
			sb.append(params.toString());
			Logger.info(sb.toString()+"--"+json);
			String s = doPost(sb.toString(), json);
			//String s = doGet(sb.toString(), json);
			a = new JSONObject(s);
			
			
			
			Logger.info("Output:"+a.toString());
			
			if(a.has("entries")){
					for(int i=0;i<a.getJSONArray("entries").length();i++){
						JSONObject o = a.getJSONArray("entries").getJSONObject(i);
						JSONArray  op = o.getJSONArray("indexvalues");
						JSONObject r = new JSONObject();
						r.put("ID",o.getInt("ID"));
						
						r.put("PERMIT NUMBER",op.optString(0));
						r.put("ADDRESS-NUMBER",op.optString(1));
						r.put("ADDRESS - DIRECTION",op.optString(2));
						r.put("ADDRESS-STREET",op.optString(3));
						r.put("ADDRESS - UNIT/SUITE",op.optString(4));
						r.put("DOC TYPE",op.optString(5));
						r.put("DESCRIPTION",op.optString(6));
						r.put("FILE CLASSIFICATION",op.optString(7));
						r.put("DATE",op.optString(8));
						r.put("url",_urls.get("linkurladdress")+o.getInt("ID"));
						l.append("results", r);
						
					}
				
			}
		
		
			
		}catch(Exception e){
			Logger.error(e.getMessage());
			
		}
		return l;
		
	}
    
    
    public static String doPost(String url,String json)  {
		  StringBuilder out = new StringBuilder();
		  try {
		 // String encoding = "YXhkZXZlbG9wZXI6RDN2M2xvcGVy"; // axdeveloper:D3v3loper
		  String encoding =  _username+":"+_password; 
		  byte[] encodedBytes = Base64.encodeBase64(encoding.getBytes());
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(url);
		  
		  httppost.addHeader("content-type", "application/vnd.emc.ax+json");

		  httppost.setHeader("Authorization", "Basic " + new String(encodedBytes));
		  //post json
		  StringEntity content =new StringEntity(json);

		  httppost.setEntity(content);
		  HttpResponse response = httpclient.execute(httppost);
		  HttpEntity entity = response.getEntity();
		  Logger.info(url);
		  if (entity != null) {
		      InputStream instream = entity.getContent();
		    
		          	BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		    	    String newLine = System.getProperty("line.separator");
		    	    String line;

		    	    //out.append(reader.readLine());
		    	    //Logger.info("--"+out.toString());
		    	    while ((line = reader.readLine()) != null) {
		    	        out.append(line);
		    	        out.append(newLine);
		    	    }
		    	instream.close();    
		      }
		     
		  }
		  catch(Exception ex){
			  ex.printStackTrace();
	    	  out.append("Error while getting response "+ex.getMessage());  
	      } 
		  return out.toString();
	  }
    
    
    public static String doGet(String url,String json)  {
		  StringBuilder out = new StringBuilder();
		  try {
		 // String encoding = "YXhkZXZlbG9wZXI6RDN2M2xvcGVy"; // axdeveloper:D3v3loper
		  String encoding =  _username+":"+_password; 
		  byte[] encodedBytes = Base64.encodeBase64(encoding.getBytes());
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpGet httpget = new HttpGet(url);
		  
		  httpget.addHeader("content-type", "application/vnd.emc.ax+json");

		  httpget.setHeader("Authorization", "Basic " + new String(encodedBytes));
		  //post json
		  StringEntity content =new StringEntity(json);

		 // httpget.setEntity(content);
		  HttpResponse response = httpclient.execute(httpget);
		  HttpEntity entity = response.getEntity();
		  Logger.info(url);
		  if (entity != null) {
		      InputStream instream = entity.getContent();
		    
		          	BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		    	    String newLine = System.getProperty("line.separator");
		    	    String line;

		    	    //out.append(reader.readLine());
		    	    //Logger.info("--"+out.toString());
		    	    while ((line = reader.readLine()) != null) {
		    	        out.append(line);
		    	        out.append(newLine);
		    	    }
		    	instream.close();    
		      }
		     
		  }
		  catch(Exception ex){
			  ex.printStackTrace();
	    	  out.append("Error while getting response "+ex.getMessage());  
	      } 
		  return out.toString();
	  }
	
	 
	 public static String getCredentials() {
  	 String r = "";
  	 try {
  		 r = getApiCredentials();
  	 }
  	 catch (Exception e) { }
  	 return r;
	 }
	 
	 public static String getApiCredentials() {
  	 String r = "";
  	 try {
           String url="http://docweb4:50543/API/gencred/"+_usernameUrl+"?p="+_passwordUrl;
           String c = FileUtil.getUrlContent(url);
           if(Operator.hasValue(c)){
          	 c = Operator.replace(c, "\"", "");
          	 r = c;
          	 Logger.info("API Credentials "+r);
           }else {
          	r =  getHackCredentials();
          	Logger.error("Hack Credentials "+r);
           }
           
  	 }
  	 catch (Exception e) { }
  	 return r;
	 }
	 
	 
	 public static String getHackCredentials() {
    	 String r = "";
    	 try {
             String url="http://edocs.beverlyhills.org/AppXtender/login.aspx";
             URL u = new URL(url);
             r = splitQuery(getFinalURL(u)).get("Credentials");
    	 }
    	 catch (Exception e) { }
    	 return r;
   }
   
   public static URL getFinalURL(URL url) {
       try {
           HttpURLConnection con = (HttpURLConnection) url.openConnection();
           con.setInstanceFollowRedirects(false);
           con.addRequestProperty("Accept-Language", "en-US,en;");
           con.connect();
           //con.getInputStream();
           int resCode = con.getResponseCode();
           if (resCode == HttpURLConnection.HTTP_SEE_OTHER
                   || resCode == HttpURLConnection.HTTP_MOVED_PERM
                   || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
               String Location = con.getHeaderField("Location");
               if (Location.startsWith("/")) {
                   Location = url.getProtocol() + "://" + url.getHost() + Location;
               }
               return getFinalURL(new URL(Location));
           }
       } catch (Exception e) {
           System.out.println(e.getMessage());
       }
       return url;
   }
   
   public static Map<String, String> splitQuery(URL url) {
       Map<String, String> query_pairs = new LinkedHashMap<String, String>();
	   try {
	       String query = url.getQuery();
	       String[] pairs = query.split("&");
	       for (String pair : pairs) {
	           int idx = pair.indexOf("=");
	           query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	       }
	   } catch (Exception e) { }
       return query_pairs;
   }
	
   
   public static String getResponseGet(String url,ArrayList<NameValuePair> params)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpGet httpget = new HttpGet();
	   	  
		  StringBuilder sb = new StringBuilder();
		  sb.append(url);
	     /* if(params.size()>0){
	    	  sb.append("?");
	      }
	      for(int i=0;i<params.size();i++){
	    	  sb.append(params.get(i));
	    	  sb.append("&");
	      }*/
	      
	      Logger.info(sb.toString());
	      URI website = new URI(sb.toString());
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
   
   
   public static ObjMap getObjMap(int id,JSONObject v,String[] fields){
	   ObjMap om = new ObjMap();
	   try{
		   HashMap<String, ObjVO> values = new HashMap<String, ObjVO>();
		   om.setId(id);
		   
		   	for(String field:fields){
				
				String column =field;
				ObjVO vo = new ObjVO();
			
				String value = v.getString(field);
			
				vo.setField(column);
				vo.setFieldid(column);
				vo.setValue(value);
				vo.setType("String");
				vo.setItype("String");
				vo.setLink(v.getString("url"));
				vo.setLinktype("archive");
				vo.setLinkid(2);
				vo.setTarget("_blank");
				values.put(column, vo);
			
		   	}
			om.setValues(values);
	   }catch(Exception e){
		   Logger.error(e.getMessage());
	   }
	   
	   return om;
	   
   }

}
