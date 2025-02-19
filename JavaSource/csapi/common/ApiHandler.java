package csapi.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;





import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import alain.core.security.RequestToken;
import alain.core.utils.Cartographer;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import csshared.utils.CsConfig;
import csshared.vo.AvailabilityVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserItemsVO;
import csshared.vo.ComboReviewList;
import csshared.vo.ComboReviewVO;
import csshared.vo.LibraryVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.ToolsVO;
import csshared.vo.TypeVO;
import csshared.vo.RequestVO;
import csshared.vo.finance.FeesGroupVO;
import csshared.vo.finance.FinanceVO;

public class ApiHandler {

	public static String jsonRequest(RequestVO vo) {
		String r = "";
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			r = ow.writeValueAsString(vo);
		}
		catch (Exception e) {}
		return r;
	}

	public static String post(RequestVO vo) {
		String r = "";
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			r = ow.writeValueAsString(vo);
			r = post(vo.getUrl(), r);

		}
		catch (Exception e) { }
		return r;
	}

	public static String post(String entity, RequestToken vo) {
		String r = "";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(Operator.removeTrailingSlash(CsConfig.getDomain(entity)));
			sb.append("/");
			sb.append(Operator.removeOpeningAndTrailingSlash(CsConfig.getApiPath()));
			sb.append("/auth/login");
			String url = sb.toString();
			sb = new StringBuilder();
			sb = null;
			r = post(url, vo.toString());
		}
		catch (Exception e) {}
		return r;
	}

	public static String postTools(RequestVO vo) {
		String r = "";
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			r = ow.writeValueAsString(vo);
			r = post(vo.getToolsUrl(), r);
		}
		catch (Exception e) {}
		return r;
	}

	public static String post(String url, String json) {
		Logger.info("POST URL", url);
		StringBuilder sb = new StringBuilder();
		try {
			HttpClient c = new DefaultHttpClient();
			
			HttpPost p = new HttpPost(url);
			
			p.addHeader("token", "123");
			
			StringEntity input = new StringEntity(json);
			input.setContentType("application/json");
			p.setEntity(input);

			HttpResponse r = c.execute(p);
			HttpEntity entity = r.getEntity();

			if (entity != null) {
				InputStream is = entity.getContent();

				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String l = System.getProperty("line.separator");
				String line;
				
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append(l);
				}
				is.close();
			}
		}
		catch (Exception e) { Logger.error(e); }
		return sb.toString();
	}

	public static BrowserItemsVO getPanels(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		BrowserItemsVO evo = new BrowserItemsVO();
		try {
			evo = mapper.readValue(json, BrowserItemsVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}

	public static ToolsVO getTools(RequestVO vo) {
		String json = postTools(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ToolsVO evo = new ToolsVO();
		try {
			evo = mapper.readValue(json, ToolsVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}

	public static TypeVO getType(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TypeVO evo = new TypeVO();
		try {
			evo = mapper.readValue(json, TypeVO.class);
		}
		catch (Exception e) { e.printStackTrace(); Logger.error(e); }
		return evo;
	}
	
	public static ComboReviewVO getComboReview(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ComboReviewVO evo = new ComboReviewVO();
		try {
			evo = mapper.readValue(json, ComboReviewVO.class);
		}
		catch (Exception e) { e.printStackTrace(); Logger.error(e); }
		return evo;
	}
	
	public static ObjGroupVO getGroup(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ObjGroupVO evo = new ObjGroupVO();
		try {
			evo = mapper.readValue(json, ObjGroupVO.class);
		}
		catch (Exception e) { e.printStackTrace(); Logger.error(e); }
		return evo;
	}
	
	public static SubObjVO[] searchPeople(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SubObjVO[] evo = new SubObjVO[0];
		try {
			evo = mapper.readValue(json, SubObjVO[].class);
		}
		catch (Exception e) { e.printStackTrace(); Logger.error(e); }
		return evo;
	}

	/*public static ResponseVO getSaveResponse(Cartographer map) {
		RequestVO vo = RequestMapper.getSaveRequest(map);
		return getResponse(vo);
	}*/

	public static ResponseVO getResponse(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ResponseVO rvo = new ResponseVO();
		try {
			rvo = mapper.readValue(json, ResponseVO.class);
		}
		catch (Exception e) {
			rvo = new ResponseVO();
			rvo.setMessagecode("cs500");
			rvo.addError(e.getMessage());
			Logger.error(e);
		}
		return rvo;
	}
	
	public static SubObjVO[] searchTeam(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SubObjVO[] evo = new SubObjVO[0];
		try {
			evo = mapper.readValue(json, SubObjVO[].class);
		}
		catch (Exception e) { e.printStackTrace(); Logger.error(e); }
		return evo;
	}
	
	public static FinanceVO getFinance(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		FinanceVO evo = new FinanceVO();
		try {
			evo = mapper.readValue(json, FinanceVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}
	
	public static FeesGroupVO getFeesGroupVO(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		FeesGroupVO evo = new FeesGroupVO();
		try {
			evo = mapper.readValue(json, FeesGroupVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}

	public static SubObjVO[] getChoices(String choicetype, String entity, String type, int typeid) {
		RequestVO vo = new RequestVO();
		vo.setEntity(entity);
		vo.setGrouptype(choicetype);
		vo.setType(type);
		vo.setTypeid(typeid);
		return getChoices(vo);
	}

	public static String getLkup(String choicetype, String entity, String type, int typeid, String group, String groupid, String grouptype, int selectedid) {
		RequestVO vo = new RequestVO();
		vo.setEntity(entity);
		vo.setRequest(choicetype);
		vo.setType(type);
		vo.setTypeid(typeid);
		vo.setGroup(group);
		vo.setGroupid(groupid);
		vo.setGrouptype(grouptype);
		vo.setModule("lkup");
		return post(vo);
	}

	public static SubObjVO[] getLkupChoices(String choicetype, String entity, String type, int selectedid) {
		RequestVO vo = new RequestVO();
		vo.setEntity(entity);
		vo.setRequest(choicetype);
		vo.setType(type);
		vo.setGrouptype("lkup");
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SubObjVO[] evo = new SubObjVO[0];
		try {
			evo = mapper.readValue(json, SubObjVO[].class);
		}
		catch (Exception e) {
			try {
				ObjVO ovo = mapper.readValue(json, ObjVO.class);
				evo = ovo.getChoices();
			}
			catch (Exception e1) { Logger.error(e); }
		}
		return evo;
	}

	public static SubObjVO[] getChoices(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SubObjVO[] evo = new SubObjVO[0];
		try {
			evo = mapper.readValue(json, SubObjVO[].class);
		}
		catch (Exception e) {
			try {
				ObjVO ovo = mapper.readValue(json, ObjVO.class);
				evo = ovo.getChoices();
			}
			catch (Exception e1) { Logger.error(e); }
		}
		return evo;
	}

	public static AvailabilityVO getAvailability(RequestVO vo) {
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		AvailabilityVO evo = new AvailabilityVO();
		try {
			evo = mapper.readValue(json, AvailabilityVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}

	public static LibraryVO[] getLibraryGroup(RequestVO vo) {
		vo.setModule("library");
		vo.setRequest("group");
		String json = post(vo);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		LibraryVO[] evo = new LibraryVO[0];
		try {
			evo = mapper.readValue(json, LibraryVO[].class);
		}
		catch (Exception e) { Logger.error(e); }
		return evo;
	}

	public static TypeVO getType(String url) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TypeVO u = new TypeVO();
		try {
			u = mapper.readValue(new URL(url), TypeVO.class);
		}
		catch (Exception e) { Logger.error(e); }
		return u;
	}


	public static String getResponsePost(String url,ArrayList<NameValuePair> params)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(url);
		  
	   	  UrlEncodedFormEntity e = new UrlEncodedFormEntity(params);
		  httppost.setEntity(e);
		  
		 
		  //post json
		 /*  StringEntity params1 =new StringEntity("details={\"name\":\"City Smart\",\"age\":\"20\"} ");
		   httppost.addHeader("content-type", "application/x-www-form-urlencoded");
		   httppost.setEntity(params1);*/
		  
		//  params.add(new BasicName(11,"password"));
		  
		  
		  HttpResponse response = httpclient.execute(httppost);
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
	 
	 public static String getResponseGet(String url,ArrayList<NameValuePair> params)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpGet httpget = new HttpGet();
	   	  
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
	 
	 
	 public static String getResponsePost(String url,String json)  {
		  StringBuilder out = new StringBuilder();
		  try {
		  
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(url);
		  
	   	 
		  //post json
		  StringEntity content =new StringEntity(json);
		  httppost.addHeader("content-type", "application/json");
		  httppost.setEntity(content);
		  Logger.info("+++"+url);
		  Logger.info("+++"+json);
		  
		 /* Cartographer map = new Cartographer(request,response);connRequest
		  */
		 // httppost.setConnectionRequest(map.REQUEST);
		  
		  
		  HttpResponse response = httpclient.execute(httppost);
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
		     
		  }
		  catch(Exception ex){
			  ex.printStackTrace();
	    	  out.append("Error while getting response "+ex.getMessage());  
	      } 
		  return out.toString();
	  }
	
	 public static ByteArrayOutputStream postPdf(RequestVO vo) {
			String r = "";
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			try {
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				r = ow.writeValueAsString(vo);
				o = postPdf(vo.getUrl(), r);
				
			}
			catch (Exception e) {}
			return o;
		}
	 
	 public static ByteArrayOutputStream postPdf(String url, String json) {
			Logger.info("POST URL", url);
			
			byte[] b = new byte[500];
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			try {
				HttpClient c = new DefaultHttpClient();
				HttpPost p = new HttpPost(url);
				StringEntity input = new StringEntity(json);
				input.setContentType("application/json");
				p.setEntity(input);

				HttpResponse r = c.execute(p);
				HttpEntity entity = r.getEntity();

				if (entity != null) {
					InputStream is = entity.getContent();
					for(int lengthread = 0; (lengthread = is.read(b)) != -1;){
						o.write(b, 0, lengthread);
					
					}
				
					is.close();
				}
				 
			}
			
			catch (Exception e) { }
			return o;
		}
	 
	 
	 public static ResponseVO getResponseVO(RequestVO vo) {
			String json = post(vo);
			Logger.info(vo.getUrl()+"###"+json);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ResponseVO evo = new ResponseVO();
			try {
				evo = mapper.readValue(json, ResponseVO.class);
			}
			catch (Exception e) { e.printStackTrace(); Logger.error(e); }
			return evo;
		}

	 

	
	 
	 
	


}









