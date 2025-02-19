package csapi.tasks.pdox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import csapi.utils.CsApiConfig;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class PDoxAPI {

	public static final String _url = CsApiConfig.getString("pdox.url");
	public static final String _username = CsApiConfig.getString("pdox.username");
	public static final String _password = CsApiConfig.getString("pdox.password");
	
	
	private static final Map<String, String> _urls = prepareMap();

    private static Map<String, String> prepareMap() {
        Map<String, String> hashMap = new HashMap<String, String>();
        
       // hashMap.put("axadhocqueryresults", _url+"api/AXDataSources/"+_dsn+"/axadhocqueryresults/");
       // hashMap.put("axapps", _url+"api/AXDataSources/"+_dsn+"/axapps");
       // hashMap.put("axappfields", _url+"api/AXDataSources/"+_dsn+"/axappfields/");
    
        return hashMap;
    }
	
	
    public static boolean createProject(String nbr,String desc,String type,String primary){
    	boolean result = false;
    	JSONObject o = new JSONObject();
    	try{
    		
    		
    		
    		String people = "[]";
			
			
			if(Operator.hasValue(primary)){
				people =primary;
			
				JSONArray l = new JSONArray(people);
				for(int i=0;i<l.length();i++){
					JSONObject user = l.getJSONObject(i);
					o.put("PermitNumber", nbr);
		    		o.put("PermitDescription", desc);
		    		o.put("ProjectType", type);
					o.put("SubmitterEmail", user.getString("user_email"));
		    		o.put("SubmitterFName", user.getString("user_fname"));
		    		o.put("SubmitterLName", user.getString("user_lname"));
		    		
	    		}
				
				String u = _url+"/ProjectDoxWebAPI/Project/CreateProjectWithService";
				
				
				ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("email", _username));
				headers.add(new BasicNameValuePair("password", _password));
				
				//params.add(new BasicName(11,"password")
	    		String s = getResponsePost(u, headers,params,o.toString());	
	    		
	    		if(Operator.hasValue(s)){
	    			if(Operator.equalsIgnoreCase(s.trim(), "true")){
	    				result = true;
	    			}
	    			
	    		}
			}
    	}catch(Exception e){
    		Logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    	
    			
    	return result;
    	
    }
    
    
    
    
    
    public static boolean releasePlans(String nbr){
    	boolean result = false;
    	JSONObject o = new JSONObject();
    	String SessionID = "";
    	int WFlowTaskID = 0;
    	try{
    		
    			o.put("email", _username);
			
				String u = _url+"/ProjectDoxWebAPI/User/LoginByProxy";
				
				
				ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("email", _username));
				headers.add(new BasicNameValuePair("password", _password));
				
				//params.add(new BasicName(11,"password")
	    		String s = getResponsePost(u, headers,params,o.toString());	
	    		
	    		JSONObject user = new JSONObject(s);
	    		Logger.info(user.toString());
	    		if(user.has("SessionID")){
	    			SessionID = user.getString("SessionID");
	    			u = _url+"/ProjectDoxWebAPI/WorkflowTask/GetWorkflowTaskListForCurrentUser?projectName="+nbr;
	    			headers = new ArrayList<NameValuePair>();
	    			headers.add(new BasicNameValuePair("SessionID", SessionID));
	    			
	    			s = getResponseGet(u, headers,params);	
	    			
	    			//s = FileUtil.getString("C:\\Users\\svijay\\Documents\\BHills\\pdox\\pdoxjson.txt");
	    			Logger.info(s);
	    			JSONObject tasks = new JSONObject(s);
	    		
	    			JSONArray t = new JSONArray(tasks.get("Tasks").toString());
	    			for(int i=0;i<t.length();i++){
	    				JSONObject taskdetails = t.getJSONObject(i);
	    				
	    				if(Operator.equalsIgnoreCase(taskdetails.getString("TaskStatus"),"Pending") && Operator.equalsIgnoreCase(taskdetails.getString("TaskName"),"Final Payment")){
	    					WFlowTaskID = taskdetails.getInt("WFlowTaskID");
	    				}
	    				
	    			}
	    			
	    			if(WFlowTaskID>0){
	    				Logger.info(WFlowTaskID);
	    				
	    				
	    				u = _url+"/ProjectDoxWebAPI/WorkflowTask/UpdateWorkflowTaskStatus?wflowTaskID="+WFlowTaskID+"&wflowTaskStatusTypeID=1";
		    			headers = new ArrayList<NameValuePair>();
		    			headers.add(new BasicNameValuePair("SessionID", SessionID));
		    			
		    			s = getResponsePost(u, headers,params,o.toString());	
		    			if(Operator.hasValue(s)){
			    			if(Operator.equalsIgnoreCase(s.trim(), "204")){
			    				result = true;
			    			}
			    			
			    		}
		    			
	    			}
	    			
	    			
	    		}
	    		Logger.info(SessionID);
			
    	}catch(Exception e){
    		Logger.error(e.getMessage());
    		e.printStackTrace();
    	}
    	
    			
    	return result;
    	
    }
	
	public static void main(String args[]){
		
		/*String u = _url+"/ProjectDoxWebAPI/Project/CreateProjectWithService";
		ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		headers.add(new BasicNameValuePair("email", _username));
		headers.add(new BasicNameValuePair("password", _password));
		String o ="{\"PermitDescription\":\"TESTING 123 33\",\"SubmitterEmail\":\"svijay@beverlyhills.org\",\"PermitNumber\":\"BS3100013\",\"ProjectType\":\"Building (BS)\",\"SubmitterLName\":\"\",\"SubmitterFName\":\"Sunil vijay\"}";
		
		String s = getResponsePost(u, headers,params,o);	
		
		if(Operator.hasValue(s)){
			if(Operator.equalsIgnoreCase(s.trim(), "yes") || Operator.equalsIgnoreCase(s.trim(), "true")){
				Logger.info("TRUE RESULT");
			}
			
		}*/
		
		Logger.info(releasePlans("BS2100015"));
		
	}
	
	
	 public static String getResponsePost(String url,ArrayList<NameValuePair> headers,ArrayList<NameValuePair> params,String json)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  
		  HttpClient httpclient = new SSLClient();
		  HttpPost httppost = new HttpPost(url);
		   //post json
		  StringEntity content =new StringEntity(json);
		  httppost.addHeader("Content-Type", "application/json");
		  for(NameValuePair b : headers){
			  httppost.addHeader(b.getName(), b.getValue());
			  Logger.info(b.getName()+"+++"+b.getValue());
		  }
		  
		  
		 
		  
		  httppost.setEntity(content);
		  Logger.info("+++"+url);
		  Logger.info("+++"+json);
		  
	
		  
		  
		  HttpResponse response = httpclient.execute(httppost);
		  Logger.info("+++"+response.getStatusLine().getStatusCode());
		  HttpEntity entity = response.getEntity();

		  
		  if (entity != null) {
		      InputStream instream = entity.getContent();
		    
		          	BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		    	    String newLine = System.getProperty("line.separator");
		    	    String line;
		    	   // Logger.info("--"+reader.readLine()+"");
		    	    out.append(reader.readLine());
		    	    Logger.info("--"+out.toString());
		    	    /*while ((line = reader.readLine()) != null) {
		    	        out.append(line);
		    	        out.append(newLine);
		    	    }*/
		    	instream.close();    
		      }
		  
		  	if(!Operator.hasValue(out.toString())){
		  		out.append(response.getStatusLine().getStatusCode());
		  	}
		     
		  }
		  
		  
		  
		  catch(Exception ex){
			  ex.printStackTrace();
	    	  out.append("Error while getting response "+ex.getMessage());  
	      } 
		  return out.toString();
	  }
	
	 
	 
	
	 
	 public static String getResponseGet(String url,ArrayList<NameValuePair> headers,ArrayList<NameValuePair> params)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  HttpClient httpclient = new SSLClient();
		  HttpGet httpget = new HttpGet();
	   	  
		  
		  httpget.addHeader("Content-Type", "application/json");
		  for(NameValuePair b : headers){
			  httpget.addHeader(b.getName(), b.getValue());
			  Logger.info(b.getName()+"+++"+b.getValue());
		  }
		  
		  StringBuilder sb = new StringBuilder();
		  sb.append(url);
	      if(params.size()>0){
	    	  sb.append("?");
	      }
	      for(int i=0;i<params.size();i++){
	    	  sb.append(params.get(i));
	    	  sb.append("&");
	      }
	      
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
	
}
