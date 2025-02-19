package csapi.impl.admin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URI;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;     
import org.json.JSONObject;
import org.json.XML;

import com.itextpdf.text.log.SysoCounter;

import org.apache.commons.lang.ArrayUtils;

import alain.core.db.Sage;
import alain.core.email.ExchangeMessenger;
import alain.core.security.Token;
import alain.core.utils.Cartographer;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.Table;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserVO;
import csshared.vo.FileVO;

/**
 * @author svijay
 */
public class AdminAgent {

	//TODO determine type and set a relation in database;
	//TODO add token and set a relation in database;
	// Token not coming in the initial request check CS for it
	public static BrowserVO browse(String type, String typeid, Token u) {
		BrowserVO b = new BrowserVO();
		try {

			StringBuilder sb = new StringBuilder();
			sb.append(type.toUpperCase()).append(" ADMINISTRATOR");
			String label = sb.toString();

			BrowserHeaderVO h = new BrowserHeaderVO();
			h.setLabel(label);
			h.setDataid(Operator.toString(typeid));
			b.setHeader(h);

			b.setRoot(browseDefault(type, u));

			BrowserItemVO[] brs = getItems(type, typeid, u);
			b.setItems(brs);

		
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		return b;
	}
	
	
	public static String indexSolrIndex(String url)
	{
		
		Logger.info("indexSolrIndex()");
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
			//JSONObject json = XML.toJSONObject(content); // converts xml to json
		    //String jsonPrettyPrintString = json.toString(); // json pretty print
		    System.out.println(content);
			JSONObject o = new JSONObject(content);            
			
			
			r = o.getString("status");
			Logger.highlight(r);
			
			
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;

		
	}

	public static BrowserItemVO[] getItems(String type, String typeid, Token u) {
		BrowserItemVO[] result = new BrowserItemVO[0];
		try {
			Logger.info(typeid+"*****typeid*******"+type+"*****userid*******"+u.getId());
			StringBuilder sb = new StringBuilder();
			sb.append("browse").append("Children");
			String method = sb.toString();
			Class<?> _class = Class.forName("csapi.impl.admin.AdminAgent");
			Method _method = _class.getDeclaredMethod(method, new Class[]{String.class,String.class,Token.class});
			result = (BrowserItemVO[]) _method.invoke(null, new Object[]{type,typeid,u});
		}
		catch (Exception e) { Logger.error(e); }
		return result;
	}

	public static BrowserItemVO[] browseDefault(String type, Token u) {
		Sage db = new Sage();
		
		String command = AdminSQL.browseDefault(u);
		
		db.query(command);
		BrowserItemVO[] brs = new BrowserItemVO[db.size()];
		int c =0;
		while(db.next()){
			BrowserItemVO vo = new BrowserItemVO();
			vo.setTitle(db.getString("NAME"));
			vo.setId(db.getString("ID"));
			vo.setDataid(db.getString("ID"));
			vo.setChildren(1);
			vo.setChild("admin");
			vo.setEntity(type);
			vo.setType("admin");
			//vo.setDomain(Config.rooturl());
			//vo.setLink(Config.fullcontexturl()+db.getString("LOCATION"));
			
			StringBuilder url = new StringBuilder();
			url.append(Config.fullcontexturl()).append(db.getString("LOCATION")).append("?_token=").append(u.getToken()).append("&_userid=").append(u.getId());
			vo.setLink(url.toString());
			brs[c] = vo;
			c++;
		}
		
		db.clear();
		return brs;
	}

	public static BrowserItemVO[] browseChildren(String type, String typeid, Token u) {
		BrowserItemVO[] brs = new BrowserItemVO[0];
		if(!typeid.startsWith("menu")){
			Sage db = new Sage();
			String command = AdminSQL.browseChildren(Operator.toInt(typeid), u);
			
			db.query(command);
			brs = new BrowserItemVO[db.size()];
			int c =0;
			while(db.next()){
				BrowserItemVO vo = new BrowserItemVO();
				vo.setTitle(db.getString("NAME"));
				vo.setId(db.getString("ID"));
				vo.setDataid(db.getString("ID"));
				vo.setEntity(type);
				vo.setType("admin");
				StringBuilder url = new StringBuilder();
				url.append(Config.fullcontexturl()).append(db.getString("LOCATION")).append("?_token=").append(u.getToken()).append("&_userid=").append(u.getId());
				
				//vo.setLink(Config.fullcontexturl()+db.getString("LOCATION")+"?_token="+);
				vo.setLink(url.toString());
				//vo.setDomain(Config.fullcontexturl());
				brs[c] = vo;
				c++;
			}
			
			db.clear();
		}
		return brs;
	}
	
	public static BrowserItemVO[] browseFees(String type) {
		BrowserItemVO[] brs = new BrowserItemVO[1];

		BrowserItemVO vo = new BrowserItemVO();
		vo.setTitle("Fee Lookup");
		vo.setId("FeeLookup");
		vo.setDataid("FeeLookup");
		vo.setEntity(type);
		vo.setType("admin");
		vo.setLink("FeeLookup");
		vo.setDomain(Config.rooturl());
		brs[0] = vo;

		return brs;
	}
	
	public static ArrayList<MapSet> getIndex(String type,String typeid, String id){
		return getIndex(type, typeid, id,true);
	}
	
	public static ArrayList<MapSet> getIndex(String type,String typeid, String id, boolean parent){
			return getIndex(type, typeid, id, parent, new Token());
	}
	
	public static ArrayList<MapSet> getIndex(String type,String typeid, String id, boolean parent, Cartographer map){
		return getIndex(type, typeid, id, parent, Token.retrieve(map.getString("_token"), map.getRemoteIp()));
}

	public static ArrayList<MapSet> getIndex(String type,String typeid, String id, boolean parent, Token u){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		String command = AdminSQL.getIndex(id, parent, u);
		db.query(command);
		
		String[] cols = db.COLUMNS;
		
		while(db.next()){
			
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			a.add(h);
		}
		/*while(db.next()){
			MapSet h = new MapSet();
			h.add("ID", db.getString("ID"));
			h.add("NAME", db.getString("NAME"));
			h.add("DESCRIPTION", db.getString("DESCRIPTION"));
			h.add("LOCATION", Config.fullcontexturl()+db.getString("LOCATION"));
			h.add("PARENT_ID", db.getString("PARENT_ID"));
			a.add(h);
		}
		*/
		
		
		db.clear();
		
		
		return a;
			
	}		
	
	
	public static ArrayList<MapSet> getSubIndex(String type,String typeid, String id){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		String command = AdminSQL.getSubIndex(type, typeid, id);
		db.query(command);
		
		String[] cols = db.COLUMNS;
		
		while(db.next()){
			
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			a.add(h);
		}
		
		
		db.clear();
		
		
		return a;
			
	}		
	
	public static ArrayList<MapSet> getSubModule(String type,String typeid, String id){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		String command = AdminSQL.getSubModule(type, typeid, id);
		db.query(command);
		
		String[] cols = db.COLUMNS;
		
		while(db.next()){
			
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			a.add(h);
		}
		
		
		db.clear();
		
		
		return a;
			
	}
	
	
	public static ArrayList<MapSet> getList(String table,int start, int end,String sortfield,String sortorder,String query, String searchField){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getList(table,start, end, sortfield, sortorder, query, searchField));
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			
			a.add(h);
		}
		
		
		
		
		db.clear();
		
		
		return a;
			
	}	
	
	public static ArrayList<MapSet> getListWithAdditional(String table,int start, int end,String sortfield,String sortorder,String query, String searchField,String additionalColumns,String additionaljoins,String additionaland){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getListWithAdditional(table, start, end, sortfield, sortorder, query, searchField, additionalColumns, additionaljoins,additionaland));
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			
			a.add(h);
		}
		
		
		
		
		db.clear();
		
		
		return a;
			
	}	
	
	
	public static ArrayList<MapSet> getRefList(Cartographer map,int start, int end,String sortfield,String sortorder,String query, String searchField){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		Sage dbRef = new Sage();
		
		db.query(AdminSQL.getRefTables(map));
		String[] cols = db.COLUMNS;

		
		while(db.next()){
			
			    dbRef.query(AdminSQL.getRefList(db.getString("BASE_TABLE"),db.getString("REF_TABLE"),db.getString("SOURCE_TABLE"),map,start,end,sortfield,sortorder,query,searchField));
			    
			    String moduleName=db.getString("MODULE_NAME");
				while(dbRef.next()){
					
					MapSet h = new MapSet();
					h.add("GROUP_NAME", dbRef.getString("GROUP_NAME"));
					h.add("DESCRIPTION", dbRef.getString("DESCRIPTION"));
					h.add("MODULE_NAME", moduleName);
					
					a.add(h);
				
			}
			
		}
		dbRef.clear();
		db.clear();
		return a;
			
	}	
	
	public static ArrayList<MapSet> getRefList(Cartographer map){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		Sage dbRef = new Sage();
		
		db.query(AdminSQL.getRefTables(map));
		String[] cols = db.COLUMNS;

		
		while(db.next()){
			
			   dbRef.query(AdminSQL.getRefList(db.getString("BASE_TABLE"),db.getString("REF_TABLE"),db.getString("SOURCE_TABLE"),map));
			   
			    String moduleName=db.getString("MODULE_NAME");
				while(dbRef.next()){
					
					MapSet h = new MapSet();
					h.add("GROUP_NAME", dbRef.getString("GROUP_NAME"));
					h.add("DESCRIPTION", dbRef.getString("DESCRIPTION"));
					h.add("MODULE_NAME", moduleName);
					
					a.add(h);
				
			}
			
		}
		dbRef.clear();
		db.clear();
		return a;
			
	}	
	
	
	public static ArrayList<MapSet> getList(String command){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		
		db.query(command);
		String[] cols = db.COLUMNS;
		while(db.next()){
		
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			
			a.add(h);
		}
		
		
		
		
		db.clear();
		
		
		return a;
			
	}		
	
	public static JSONArray getJsonList(String command){
		JSONArray a = new JSONArray();
		
		try{
		Sage db = new Sage();
		
		db.query(command);
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			JSONObject h = new JSONObject();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.put(c, db.getString(c));
			}
			
			a.put(h);
		}
		
		db.clear();
		}catch(Exception e){
			Logger.info(e.getMessage());
		}
		
		return a;
			
	}		
	
	
	public static ArrayList<MapSet> getSubList(MapSet m){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		
		if(Operator.hasValue(m.getString("sublistquery"))){
			
			Sage db = new Sage();
			
			db.query(m.getString("sublistquery"));
			String[] cols = db.COLUMNS;
			
			while(db.next()){
			
				MapSet h = new MapSet();
				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					h.add(c, db.getString(c));
				}
				a.add(h);
			}
			db.clear();
		
		}
		return a;
			
	}		
	
	public static MapSet getType(String query){
		MapSet a = new MapSet();
		
		Sage db = new Sage();
		
		db.query(query);
		
		String[] cols = db.COLUMNS;
		while(db.next()){
			
			
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				//Logger.info(c+"^^^^^^^^^^^^^^^^^^");
				a.add(c, db.getString(c));
			}
			/*h.put("ID", db.getString("id"));
			h.put("TYPE", db.getString("TYPE"));
			h.put("DESCRIPTION", db.getString("DESCRIPTION"));
			h.put("ISPUBLIC", db.getString("ispublic"));
			h.put("ONLINE", db.getString("ONLINE"));
			h.put("PATTERN", db.getString("PATTERN"));
			h.put("ISDOT", db.getString("ISDOT"));
			h.put("C_CREATED_DATE", db.getString("C_CREATED_DATE"));
			h.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
			h.put("CREATED", db.getString("CREATED"));
			h.put("UPDATED", db.getString("UPDATED"));*/
			//a.add(h);
		}
		
		
		/*while(db.next()){
			HashMap<String,String> h = new HashMap<String,String>();
			h.put("ID", db.getString("id"));
			h.put("TYPE", db.getString("TYPE"));
			h.put("DESCRIPTION", db.getString("DESCRIPTION"));
			h.put("ISPUBLIC", db.getString("ispublic"));
			h.put("ONLINE", db.getString("ONLINE"));
			h.put("PATTERN", db.getString("PATTERN"));
			h.put("ISDOT", db.getString("ISDOT"));
			h.put("C_CREATED_DATE", db.getString("C_CREATED_DATE"));
			h.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
			h.put("CREATED", db.getString("CREATED"));
			h.put("UPDATED", db.getString("UPDATED"));
		
		}*/
		
		
		
		db.clear();
		
		
		return a;
		
	}
	
	public static MapSet getType(int id,String table){
		MapSet a = new MapSet();
		System.out.println("Entering here");
		Sage db = new Sage();
		
		db.query(AdminSQL.getType(id,table));
		
		String[] cols = db.COLUMNS;
		while(db.next()){
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				//Logger.info(c+"^^^^^^^^^^^^^^^^^^");
				a.add(c, db.getString(c));
			}
			/*h.put("ID", db.getString("id"));
			h.put("TYPE", db.getString("TYPE"));
			h.put("DESCRIPTION", db.getString("DESCRIPTION"));
			h.put("ISPUBLIC", db.getString("ispublic"));
			h.put("ONLINE", db.getString("ONLINE"));
			h.put("PATTERN", db.getString("PATTERN"));
			h.put("ISDOT", db.getString("ISDOT"));
			h.put("C_CREATED_DATE", db.getString("C_CREATED_DATE"));
			h.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
			h.put("CREATED", db.getString("CREATED"));
			h.put("UPDATED", db.getString("UPDATED"));*/
			//a.add(h);
		}
		
		
		/*while(db.next()){
			HashMap<String,String> h = new HashMap<String,String>();
			h.put("ID", db.getString("id"));
			h.put("TYPE", db.getString("TYPE"));
			h.put("DESCRIPTION", db.getString("DESCRIPTION"));
			h.put("ISPUBLIC", db.getString("ispublic"));
			h.put("ONLINE", db.getString("ONLINE"));
			h.put("PATTERN", db.getString("PATTERN"));
			h.put("ISDOT", db.getString("ISDOT"));
			h.put("C_CREATED_DATE", db.getString("C_CREATED_DATE"));
			h.put("C_UPDATED_DATE", db.getString("C_UPDATED_DATE"));
			h.put("CREATED", db.getString("CREATED"));
			h.put("UPDATED", db.getString("UPDATED"));
		
		}*/
		
		
		
		db.clear();
		
		
		return a;
			
	}	
	
	
	public static int getRType(int id,String table){
		
		Sage db = new Sage();
		
		db.query("SELECT LKUP_VOIP_MENU_ID FROM REF_VOIP_REVIEW WHERE ACTIVE='Y' AND REVIEW_ID="+id);
		
		int voipMenuId=0;
	
		while(db.next()){
			voipMenuId= db.getInt("LKUP_VOIP_MENU_ID");
		}
			
		db.clear();

		return voipMenuId;
			
	}	
	
	public static boolean saveType(Cartographer map,String table){
		boolean result = false;
		int userid = map.getInt("_userid");
		StringBuilder sb = new StringBuilder();
		StringBuilder t = new StringBuilder();
		StringBuilder v = new StringBuilder();
		if(Operator.hasValue(table)){
			Sage db = new Sage();
			String command = "select * from "+table;
			db.query(command);
			String[] cols = db.COLUMNS;
			t.append(" INSERT INTO ").append(table).append("(");
			
			for (int i=0; i<cols.length; i++) {
	            String c = cols[i];
	            
	            
	            boolean increment = db.columnIncrement(c);
	            String type = db.columnType(c);
	            String value = map.getString(c);
	            Logger.info(c+"----"+db.columnType(c)+"----"+db.columnSize(c)+"---"+db.columnIncrement(c)+"---"+db.columnNullable(c)+"---"+value);
	            boolean common =  false;
	            if(Operator.equalsIgnoreCase(c, "ACTIVE") || Operator.equalsIgnoreCase(c, "CREATED_IP") || Operator.equalsIgnoreCase(c, "UPDATED_IP") || Operator.equalsIgnoreCase(c, "CREATED_DATE") || Operator.equalsIgnoreCase(c, "CREATED_BY") || Operator.equalsIgnoreCase(c, "UPDATED_BY") || Operator.equalsIgnoreCase(c, "UPDATED_DATE")){
	            	common =  true;
	            }
	            if(type.equalsIgnoreCase("char")){
	            	if(value.equalsIgnoreCase("on")){
	            		value = "Y";
	            	}
	            	if(!Operator.hasValue(value)){
	            		value = "N";
	            	}
	            }
	            
	            if(!increment && !common){
	            	t.append(c).append(",");
	            	v.append("'").append(Operator.sqlEscape(value)).append("' ").append(",");
	            }
	         
			}
			
			t.append(" CREATED_BY,UPDATED_BY,CREATED_DATE,UPDATED_DATE, ACTIVE) VALUES ( ");
			
			sb.append(t.toString());
			sb.append(v.toString());
			
			sb.append(userid);
			sb.append(",");
			sb.append(userid);
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(",");
			sb.append(" CURRENT_TIMESTAMP ");
			sb.append(" , ");
			sb.append(" 'Y' ");
			sb.append(" ) "); 
			
			
			result = db.update(sb.toString());
			
			
			db.clear();
			CsDeleteCache.deleteCache();
		}
				
		return result;

	}
	
	
	public static boolean saveType(MapSet m,String table){
		boolean result = false;

		Sage db = new Sage();
		
		boolean addOrder = Operator.equalsIgnoreCase(m.getString("addorder"), "Y");
		m.remove("addorder");
		int order =0;
		if(addOrder){
			db.query(m.getString("orderquery"));
			if(db.next()){
				order = db.getInt("ORDR");
			}
			m.remove("orderquery");	
			m.remove("ORDR");
			m.add("ORDR", order);
		}
		
		result = db.insertMapOnly(table, m, "ID", false);
		db.clear();
		CsDeleteCache.deleteCache();
		return result;
	}
	
	public static boolean saveType(String query){
		boolean result = false;
		
		if(Operator.hasValue(query)){
			Sage db = new Sage();
			result = db.update(query);
			db.clear();
			CsDeleteCache.deleteCache();
		}
		return result;
	}
	
	
	public static boolean updateData(String query){
		boolean result = false;
		
		if(Operator.hasValue(query)){
			Sage db = new Sage();
			result = db.update(query);
			db.clear();
			CsDeleteCache.deleteCache();
		}
		return result;
	}
	
	
	public static boolean saveMultiFieldChoices(MapSet m,String table){
		boolean result = false;

		Sage db = new Sage();
		
		boolean addOrder = Operator.equalsIgnoreCase(m.getString("addorder"), "Y");
		m.remove("addorder");
		int order =0;
		if(addOrder){
			db.query(m.getString("orderquery"));
			if(db.next()){
				order = db.getInt("ORDR");
			}
			m.remove("orderquery");	
			m.remove("ORDR");
			m.add("ORDR", order);
		}
		
		String choiceTitle[]=(m.getString("TITLE")).split("[\\|\\s]+");
		String choiceTitleValue[]=(m.getString("TITLE_VALUE")).split("[\\|\\s]+");
		String sql="";
		
	    for(int i=0;i<choiceTitle.length;i++)
		{
			if(!choiceTitle[i].equals("") && !choiceTitleValue[i].equals(""))
			{
				m.add("TITLE", choiceTitle[i]);
				m.add("TITLE_VALUE", choiceTitleValue[i]);
			
				sql="INSERT INTO "+m.getString("table")+"( CREATED_IP, UPDATED_IP, CREATED_BY, UPDATED_BY, CREATED_DATE, UPDATED_DATE, FIELD_ID, TITLE, TITLE_VALUE, ORDR)"
						+ "VALUES('','',"+m.getInt("CREATED_BY")+","+m.getString("UPDATED_BY")+","+m.getString("CREATED_DATE")+","+m.getString("UPDATED_DATE")
						+","+m.getString("FIELD_ID")+",'"+m.getString("TITLE")+"','"+m.getString("TITLE_VALUE")+"',"+(m.getInt("ORDR")+i)+")";
			    //result = db.insertMapOnly(table, m, "ID", false);	
				result=db.update(sql);
			}
		}
		
		db.clear();
		CsDeleteCache.deleteCache();
		return result;
	}


	//saveChoices
	public static boolean saveChoices(Cartographer map,MapSet m,String table){
		boolean result = false;
		for(int i=0;i<map.getInt("choiceslength");i++){
			if(Operator.hasValue(map.getString("TITLE_"+i))){
				m.add("TITLE", map.getString("TITLE_"+i));
				if(Operator.hasValue(map.getString("TITLE_VALUE_"+i))){
					m.add("TITLE_VALUE", map.getString("TITLE_VALUE_"+i));
				}else {
					m.add("TITLE_VALUE", map.getString("TITLE_"+i));
				}
				result = saveType(m, table);
				
				if(result){
					m.remove("TITLE");
					m.remove("TITLE_VALUE");
				}
				
			}
		}
		
		CsDeleteCache.deleteCache();
		
		return result;
	}
	
	
	
	public static boolean updateType(MapSet m,String table){
		boolean result = false;
		
		m.remove("CREATED_DATE");
		m.remove("CREATED_IP");
		m.remove("CREATED_BY");

		Sage db = new Sage();
		result = db.updateMapOnly(table, m,  "ID", false,false);
		db.clear();
		CsDeleteCache.deleteCache();
		return result;
	}
	
	
	public static boolean updateRefVoipReview(MapSet m,String table,int id){
		boolean result = false;
		
    	Sage db = new Sage();
    	
    	String updateMerge ="UPDATE "+table+ " SET LKUP_VOIP_MENU_ID ="+m.getString("LKUP_VOIP_MENU_ID")+ " WHERE REVIEW_ID= "+id;
		
		result=db.update(updateMerge);
		
		db.clear();
		
		return result;
	}
	
	
	public static boolean saveRefVoipReview(MapSet m,MapSet voip){
		boolean result = true;
		MapSet voipdup = voip;
		result = deleteRef(voipdup);
		if(m.getInt("ID")>0){
			
			
				if(voip.getInt("REVIEW_ID")>0){
				//	if(voip.getInt("LKUP_VOIP_MENU_ID")>0){
					Logger.info("^^^^^^^^^^^^^^^^"+voip.getString("CREATED_BY"));
					voip.set("CREATED_BY",-1);
					voipdup.remove("ACTIVE");
					voipdup.set("ACTIVE","Y");
					result = AdminAgent.saveType(voip,voip.getString("table"));
				//}
			}
		}else{
			Logger.info(voip.getInt("LKUP_VOIP_MENU_ID")+"#########ssssssss#######");
			if(voip.getInt("LKUP_VOIP_MENU_ID")>0){
				Sage db = new Sage();
				
				String command = "SELECT ID from REVIEW WHERE REVIEW_GROUP_ID= "+m.getString("REVIEW_GROUP_ID")+" AND NAME='"+Operator.sqlEscape(m.getString("NAME"))+"' ";
				Logger.info(command);
				db.query(command);
				
				int reviewId =0;
				if (db.next()){
					reviewId=db.getInt("ID");
				}
				db.clear();
			
				if(reviewId>0){
					voip.set("REVIEW_ID", reviewId);
					result = AdminAgent.saveType(voip,voip.getString("table"));
				}
			}
			
		}
		
		return result;
	}
	
	
	
	
	
	
	public static boolean saveupdateCustom(MapSet m){
		boolean result = false;
		
		if(Operator.hasValue(m.getString("COMMAND"))){
			String command = m.getString("COMMAND");
		
			Sage db = new Sage();
			result = db.update(command);
			db.clear();
		}
		
		return result;
	}
	
	public static boolean updateMultipleType(MapSet m,String table){
		boolean result = false;
		Logger.info("enter sqllllllllllllllllll");
		m.remove("CREATED_DATE");
		m.remove("CREATED_IP");
		m.remove("CREATED_BY");

		
		Sage db = new Sage();
		result = db.updateMapMultiple(table, m);
		
		Logger.info(result+"sqllllllllllllllllll"+ m.getString("wherecondition"));
		
		db.clear();
		CsDeleteCache.deleteCache();
		return result;
	} 
	
	
	public static boolean mergeStatus(MapSet m,String table,String updateTable){
		boolean result = false;
		Logger.info("enter mergeStatus");
		Sage db = new Sage();
		  
		String insertHistory="INSERT INTO "+updateTable+"_HISTORY SELECT * FROM "+updateTable+" WHERE "+table+"_ID IN ("+m.getString("IDS")+")";
		result=db.update(insertHistory);
		
		String updateMerge ="UPDATE "+updateTable+ " SET "+table+"_ID ="+m.getString("STATUS_ID")+ " WHERE "+table+"_ID IN ("+m.getString("IDS")+")";
		result=db.update(updateMerge);
		

		String deactivateMerge="";
		if(result)
		{
		deactivateMerge="UPDATE "+table+" SET ACTIVE='N',STATUS_ID_MERGE_MAP="+m.getString("STATUS_ID")+" WHERE "+m.getString("wherecondition");
		result=db.update(deactivateMerge);
		//result = db.updateMapMultiple();
		}
		
		db.clear();
		
		return result;
	}
	
	
	public static boolean copyField(MapSet m,String table){
		boolean result = false;
		Logger.info("enter copyField");
		Sage db = new Sage();
		
		String command = "select * from "+table;
		db.query(command);
		String[] cols = db.COLUMNS;
		String colms="";
		for (int i=0; i<cols.length; i++) {
			if(i>0)
			{
				if(i>1)
				{
					colms=colms+",";
				}
				colms = colms+ cols[i];
			}
		}
		
		
		String copySql ="INSERT INTO  "+table+ " ( "+colms+") SELECT NAME, SOURCE, "+m.getInt("GROUP_ID")+", "
				+ "LKUP_FIELD_TYPE_ID, ORDR, REQUIRED, MAX_CHAR, ACTIVE, CREATED_BY,CURRENT_TIMESTAMP, UPDATED_BY, CURRENT_TIMESTAMP, CREATED_IP,"
				+ " UPDATED_IP, LKUP_FIELD_ITYPE_ID, IDX, IDX_ORDR, DESCRIPTION FROM "+table+" WHERE "+m.getString("wherecondition")+"";
		result=db.update(copySql);
		
		String idtySql="SELECT MAX(ID) AS ID FROM "+table+" WHERE FIELD_GROUPS_ID="+m.getInt("GROUP_ID")+" AND CREATED_BY=890";
		
		command = "select * from "+Table.FIELDCHOICESTABLE;
		db.query(command);
	    cols = db.COLUMNS;
		colms="";
		for (int i=0; i<cols.length; i++) {
			if(i>0)
			{
				if(i>1)
				{
					colms=colms+",";
				}
				colms = colms+ cols[i];
			}
		}
		
		copySql ="INSERT INTO  "+Table.FIELDCHOICESTABLE+"  (  "+colms+ ") SELECT ("+idtySql+"), TITLE, TITLE_VALUE, CHOICE_INT, CHOICE_DATE, "
				+ "ORDR, ACTIVE, CREATED_BY, CURRENT_TIMESTAMP, UPDATED_BY, CURRENT_TIMESTAMP, CREATED_IP, UPDATED_IP "				
				+ "  FROM "+Table.FIELDCHOICESTABLE+"  WHERE FIELD_ID="+m.getInt("id")+"";
		result=db.update(copySql);
		
		db.clear();
		
		return result;
	}
	
	

	
	public static boolean saveTypeMultiValues(MapSet m,String table){
		boolean result = false;
		
		if(m.hasString("MULTI_IDS") && m.hasString("MULTI_IDS_COLUMN")){
		
			Sage db = new Sage();
			
			String column = m.getString("MULTI_IDS_COLUMN");
			String s[] = Operator.split(m.getString("MULTI_IDS"),",");
			Logger.info(s.length);
			for(int i =0;i<s.length;i++){
				m.add(column, s[i]);
				result = db.insertMapOnly(table, m, "ID", false);
				m.remove(column);
			}
			
			
			db.clear();
		
		}
		
		
		return result;
	}
	
	
	
	//TODO SECURE
	public static boolean secureAdmin(Cartographer map){
		Token t = Token.retrieve(map.getString("_token"), map.getRemoteIp());
		if (t.isAdmin() || t.isStaff()) {
			int userid = t.getId();
			map.setSession("_userid", Operator.toString(userid));
			map.setSession("_token", map.getString("_token"));
			return true;
		}
		return false;

//		//Token t = AuthorizeToken.getToken(map.getString("_token"), map.getRemoteIp());
//		//String filename = StringEncrypter.encrypt(map.getString("_token"), map.IP);
//		Logger.info("******USERRRR IDDDDDDDDDDDDDDD***********"+t.getId()+"***IP="+map.getRemoteIp());
//		String roles[] = t.getRoles();
//		
//		
//		
//		String _userid = map.getString("_userid",t.getId()+"");
//		_userid = Operator.replace(_userid, "?_ent=permit", "");
//		int userId = Operator.toInt(_userid);
//		
//		
//		if(checkRolesAdmin(roles)){
//		
//			map.setSession("_userid", _userid);
//			map.setSession("_token", map.getString("_token"));
//	
//		Logger.info(userId+"**********WHAT is it finally?*****");
//		
//		}else {
//			return false;
//		}
//		
//		
//		//ENABLE ONCE TOKEN is passed and validated properly and check roles before providing access
//		
//		if(userId<=0){
//			return false;
//		}
//		
//		
//		
//		
//		
//		return true;
	}
	
	public static boolean insertRef(MapSet m){
		
		boolean result = false;
		
		String table = m.getString("table");
		String column = m.getString("column");
		
		/*StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE ").append(table).append(" SET ACTIVE ='N',UPDATED_DATE=CURRENT_TIMESTAMP WHERE  ACTIVE='Y' AND ").append(column).append(" IN (").append(m.getString("connectids")).append(") AND ");
		result = saveType(sb.toString());*/
		if(m.hasString("deleteselection")){
			MapSet d = new MapSet();
			while(m.next()){
				d.add(m.getField(), m.get(m.getField()));
				
			}
			deleteRef(d);
			Logger.info(m.getInt("CREATED_BY")+"***********AFTER DELETE***********"+m.getString("connectids"));
		}
		String c[] = Operator.split(m.getString("connectids"),",");
		if(c.length>0 & Operator.hasValue(table) && Operator.hasValue(column)){
				Sage db = new Sage();
				
				

				
				
				int order =0;
				boolean addOrder = Operator.equalsIgnoreCase(m.getString("addorder"), "Y");
				m.remove("addorder");
				for(int i=0;i<c.length;i++){
					if(addOrder){
						db.query(AdminSQL.getOrder(table, column, c[i]));
						if(db.next()){
							order = db.getInt("ORDR");
						}
						
						m.remove("ORDR");
						m.add("ORDR", order);
					}
					m.remove(column);
					m.add(column, c[i]);
					
					result = db.insertMapOnly(table, m, "ID", false);
				
				}
				
				
				
				db.clear();
			
		
		}
		return result;
	}
	
	public static boolean deleteRef(MapSet m){
		m.remove("CREATED_DATE");
		m.remove("CREATED_IP");
		m.remove("CREATED_BY");
		m.add("ACTIVE", "N");
		boolean result = false;
		//StringBuilder w = new StringBuilder();
		String table = m.getString("table");
		
		//int refId = m.getInt("REF_ID");
		
		if(Operator.hasValue(table)){
			//m.remove("REF_ID");
		//	m.add("ID",refId);
			Sage db = new Sage();
			//result = db.updateMapOnly(table, m, "ID", false, true);
			db.query(m.getString("deleteselection"));
			String s[] = new String[db.size()];
			int c =0;
			while(db.next()){
				s[c] = db.getString("ID");
				c++;
				
			}
			
			for(int i=0;i<s.length;i++){
				m.remove("ID");
				m.add("ID",s[i]);
				result = db.updateMapOnly(table, m, "ID", false, true);
			}
			
			
			
			//NOT WORKING THIS METHOD NEED TO FIX CORE
			//result = db.updateMapMultiple(table, m, "ID", false, true, m.getString("wherecondition"));
			db.clear();
		}
		
		
		return result;
	}
	
	
	public static boolean sortRef(MapSet m){
		boolean result = false;
		StringBuilder w = new StringBuilder();
		String sorts[] = Operator.split(m.getString("sortselection"),"|");
		if(sorts.length>0){
				Sage db = new Sage();
				for(int i=0;i<sorts.length;i++){
					result = db.update(sorts[i]);
				}
			
				db.clear();
		}
		return result;
	}
	
	public static JSONArray getLookup(String lookup){
		return getLookup(lookup, 0);
	}
	
	public static JSONArray getLookup(String lookup, int id){
		JSONArray a = new JSONArray();
		
		try{
			if(Operator.equalsIgnoreCase(lookup, "LKUP_FIELD_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, TYPE AS TEXT FROM LKUP_FIELD_TYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_FIELD_ITYPE")){
				a = getJsonList("select ID, ID AS VALUE, TYPE AS TEXT FROM LKUP_FIELD_ITYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "DEPARTMENT")){
				a = getJsonList("select ID, ID AS VALUE, DEPT AS TEXT FROM DEPARTMENT WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_DIVISIONS_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, TYPE+' - '+ DESCRIPTION AS TEXT FROM LKUP_DIVISIONS_TYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_REVIEW_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, DESCRIPTION AS TEXT FROM LKUP_REVIEW_TYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_HOLIDAY_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, DESCRIPTION AS TEXT FROM LKUP_HOLIDAY_TYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "AVAILABILITY")){
				a = getJsonList("select ID, ID AS VALUE, TITLE AS TEXT FROM AVAILABILITY WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LIBRARY_GROUP")){
				a = getJsonList("select ID, ID AS VALUE, GROUP_NAME  +' - ' + DESCRIPTION  AS TEXT FROM LIBRARY_GROUP WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_PEOPLE_LICENSE_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, TYPE  AS TEXT FROM LKUP_PEOPLE_LICENSE_TYPE WHERE ACTIVE = 'Y' ");
				
			}
			
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_ACT_STATUS")){
				a = getJsonList("select ID, ID AS VALUE, STATUS  AS TEXT FROM LKUP_ACT_STATUS WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_PROJECT_STATUS")){
				a = getJsonList("select ID, ID AS VALUE, STATUS  AS TEXT FROM LKUP_PROJECT_STATUS WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_REVIEW_STATUS")){
				a = getJsonList("select ID, ID AS VALUE, STATUS  AS TEXT FROM LKUP_REVIEW_STATUS WHERE ACTIVE = 'Y' AND LKUP_REVIEW_TYPE_ID= "+id);
				
			}
			if(Operator.equalsIgnoreCase(lookup, "LKUP_VOIP_MENU")){
				a = getJsonList("select ID, ID AS VALUE, 'PRESS ' + CONVERT(VARCHAR(10),PRESS) +' - '+ DESCRIPTION  AS TEXT FROM LKUP_VOIP_MENU WHERE ACTIVE = 'Y' ");
				
			}
			
			
			if(Operator.equalsIgnoreCase(lookup, "FIELD_GROUPS")){
				a = getJsonList("select ID, ID AS VALUE, GROUP_NAME  AS TEXT FROM FIELD_GROUPS WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_LSO_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, DESCRIPTION  AS TEXT FROM LKUP_LSO_TYPE WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LSO_STREET")){
				a = getJsonList("select ID, ID AS VALUE, CONCAT(PRE_DIR,' ',STR_NAME,' ' ,STR_TYPE,' ' ,SUF_DIR) AS TEXT FROM LSO_STREET WHERE ACTIVE = 'Y'  ORDER BY STR_NAME ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "STREET_MOD")){
				a = getJsonList("select DISTINCT STR_MOD as ID,STR_MOD as VALUE, STR_MOD as TEXT  FROM LSO  WHERE  ACTIVE='Y' AND STR_MOD <> ''   ");
				
			}
			
			 
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_PATTERN")){
				a = getJsonList("select ID, ID AS VALUE, PATTERN  AS TEXT FROM LKUP_PATTERN WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_CONFIGURATION")){
				a = getJsonList("select ID, ID AS VALUE, NAME  AS TEXT FROM LKUP_CONFIGURATION WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "CONFIGURATION_GROUP")){
				a = getJsonList("select ID, ID AS VALUE, GROUP_NAME  AS TEXT FROM CONFIGURATION_GROUP WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_DIVISIONS_GROUP")){
				a = getJsonList("select ID, ID AS VALUE, GROUP_NAME  AS TEXT FROM LKUP_DIVISIONS_GROUP WHERE ACTIVE = 'Y' ");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_TASKS")){
				a = getJsonList("select ID, ID AS VALUE, TASK  AS TEXT,PATH,RUN_URL,RUN_TIME FROM LKUP_TASKS WHERE ACTIVE = 'Y'");
				
			}
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_TEAM_TYPE")){
				a = getJsonList("select ID, ID AS VALUE, TYPE  AS TEXT FROM LKUP_TEAM_TYPE WHERE ACTIVE = 'Y'");
				
			}
			
			
			if(Operator.equalsIgnoreCase(lookup, "ACTIVE")){
				JSONObject o = new JSONObject();
				o.put("ID", "Y");
				o.put("TEXT", "Active");
				a.put(o);
				
				o = new JSONObject();
				o.put("ID", "I");
				o.put("TEXT", "In-Active");
				a.put(o);
			}
			
			
			if(Operator.equalsIgnoreCase(lookup, "LKUP_USERS_TYPE")){
				if(id>0){
					a = getJsonList(" WITH Q AS( select * from REF_USERS R where USERS_ID  in ("+id+") and ACTIVE = 'Y')  select A.ID, A.ID AS VALUE,  A.DESCRIPTION  AS TEXT FROM LKUP_USERS_TYPE A  left outer join Q on A.ID= Q.LKUP_USERS_TYPE_ID    where Q.ID is null  ");
				}else {
					a = getJsonList("select ID, ID AS VALUE,  DESCRIPTION  AS TEXT FROM LKUP_USERS_TYPE WHERE ACTIVE = 'Y' ");
				}
				
			}
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return a;
	}
	
	
	public static ArrayList<MapSet> getAssociateLibraryGroups(int id){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT DISTINCT LG.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),LG.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),LG.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append("  FROM LIBRARY_GROUP LG ");
		sb.append(" LEFT OUTER JOIN REF_LIBRARY_GROUP RLG on LG.ID=RLG.LIBRARY_GROUP_ID  ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on LG.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on LG.UPDATED_BY = UP.ID "); 
		sb.append(" WHERE RLG.LIBRARY_ID <> ").append(id).append(" AND RLG.ACTIVE ='Y' ");
		
		a = getList(sb.toString());
		
		return a;
	}
	

	//Copy is associate
	public static ArrayList<MapSet> getCopyFeeGroups(int id){
		String[] dependents = { "customgroup", "feename" };
		ArrayList<MapSet> a = new ArrayList<MapSet>();

		Sage db = new Sage();
		
		boolean dependent = false;
		String command = "select * from REF_FEE_FORMULA R left outer join LKUP_FORMULA F on R.LKUP_FORMULA_ID = F.ID where R.ID ="+id;
		int feeId =0;
		String formula ="";
		db.query(command);
		if(db.next()){
			formula = db.getString("FORMULA");
			feeId = db.getInt("FEE_ID");
			if ( ArrayUtils.contains( dependents, formula.toLowerCase())) {
				dependent = true;
				
			}
		}
		db.clear();
		//TODO change if fee is dependent parse only available 
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_FEE_GROUP R where FEE_ID  in (").append(feeId).append(")");
		sb.append(" ) ");
		sb.append(" select * ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from FEE_GROUP A ");
		sb.append(" left outer join Q on A.ID= Q.FEE_GROUP_ID");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where A.ACTIVE ='Y' AND  Q.ID is null order by GROUP_NAME");
		if(!dependent){
			a = getList(sb.toString());
		}
		
		if(dependent){
			String s = formula;
			String keyword = "customgroup";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
		
			int md =0;
			while (m.find()) {
				String noofmonths =  m.group();
				String original = noofmonths;
			
				String sp = noofmonths;
				sp = sp.replace("[", "");
				sp = sp.replace("]", "");
				sp = sp.replace(keyword, "");
			}
			
		}
		
		
		return a;
	}
	
	
	public static ArrayList<MapSet> getFeeGroupsPresent(int id){
		ArrayList<MapSet> a = new ArrayList<MapSet>();

		Sage db = new Sage();
		
		boolean dependent = false;
		String command = "select * from REF_FEE_FORMULA R left outer join LKUP_FORMULA F on R.LKUP_FORMULA_ID = F.ID where R.ID ="+id;
		int feeId =0;
		String formula ="";
		db.query(command);
		if(db.next()){
			formula = db.getString("FORMULA");
			feeId = db.getInt("FEE_ID");
			
		}
		db.clear();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_FEE_GROUP R where  R.ACTIVE ='Y' AND  FEE_ID  in (").append(feeId).append(")");
		sb.append(" ) ");
		sb.append(" select * ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from FEE_GROUP A ");
		sb.append(" left outer join Q on A.ID= Q.FEE_GROUP_ID");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where A.ACTIVE ='Y'  AND  Q.ID is not null order by GROUP_NAME");
		
		a = getList(sb.toString());
		
		
		
		
		
		return a;
	}
	
	
	
	public static boolean copyFee(Cartographer map){
		boolean result = false;
		MapSet f = AdminAgent.getType(map.getInt("FEE_ID"), "FEE");
		MapSet fc = AdminMap.getCopyFeeName(map,f.getString("NAME"),f.getString("DESCRIPTION"));
		
		Sage db = new Sage();
		
		StringBuilder sb = new StringBuilder();
		int nfeeId =0;
		
		sb.append(" insert into fee (NAME, DESCRIPTION,CREATED_BY,UPDATED_BY)  OUTPUT Inserted.ID values ( ");
		sb.append("'").append(fc.getString("NAME")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("DESCRIPTION")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("CREATED_BY")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("UPDATED_BY")).append("'");
		sb.append(") ");
		
		String command = sb.toString(); 
		
		db.query(command);
		
		if(db.next()){
			nfeeId = db.getInt("ID");
		}
		
		
		sb = new StringBuilder();
		sb.append(" insert into ref_fee_formula (FEE_ID, START_DATE,CREATED_BY,UPDATED_BY,MANUAL_ACCOUNT,FINANCE_MAP_ID,LKUP_FORMULA_ID,");
		sb.append(" INPUT1,INPUT2,INPUT3,INPUT4,INPUT5,INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5,INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5,INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5) ");
		
		sb.append(" select ");
		sb.append(nfeeId);
		sb.append(",");
		sb.append("'").append(fc.getString("START_DATE")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("CREATED_BY")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("UPDATED_BY")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("MANUAL_ACCOUNT")).append("'");
		sb.append(",");
		sb.append("'").append(fc.getString("FINANCE_MAP_ID")).append("'");
		sb.append(" ,LKUP_FORMULA_ID, ");
		sb.append(" INPUT1,INPUT2,INPUT3,INPUT4,INPUT5,INPUT_TYPE1,INPUT_TYPE2,INPUT_TYPE3,INPUT_TYPE4,INPUT_TYPE5,INPUT_LABEL1,INPUT_LABEL2,INPUT_LABEL3,INPUT_LABEL4,INPUT_LABEL5, ");
		sb.append(" INPUT_DESCRIPTION1,INPUT_DESCRIPTION2,INPUT_DESCRIPTION3,INPUT_DESCRIPTION4,INPUT_DESCRIPTION5,INPUT_EDITABLE1,INPUT_EDITABLE2,INPUT_EDITABLE3,INPUT_EDITABLE4,INPUT_EDITABLE5 ");
		sb.append(" FROM   ref_fee_formula WHERE ID =  ").append(fc.getString("REF_FEE_ID"));
		
		Logger.info(sb.toString());
		boolean r = db.update(sb.toString());
		
		
				
		db.clear();
		
		if(r){
			map.remove("FEE_ID");
			map.setString("FEE_ID", nfeeId+"");
		
			MapSet m = AdminMap.insertFeeGroupFeeRef(map);
			String table = Table.REFFEEGROUPTABLE;
			
			result = saveTypeMultiValues(m, table);
		}
		
		
		return result;
	}
	
	
	
	/*public static ArrayList<MapSet> getCopyLibraryGroups(int id){
		String[] dependents = { "customgroup", "feename" };
		ArrayList<MapSet> a = new ArrayList<MapSet>();

		Sage db = new Sage();
		
		boolean dependent = false;
		String command = "select * from LIBRARY R  where R.ID ="+id;
		int libraryId =0;
		int groupId =0;
		db.query(command);
		if(db.next()){
			//formula = db.getString("FORMULA");
			libraryId = db.getInt("ID");
			groupId=db.getInt("LIBRARY_GROUP_ID");
			//if ( ArrayUtils.contains( dependents, formula.toLowerCase())) {
				//dependent = true;
				
			//}
		}
		db.clear();
		//TODO change if fee is dependent parse only available 
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_FEE_GROUP R where FEE_ID  in (").append(feeId).append(")");
		sb.append(" ) ");
		sb.append(" select * ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from FEE_GROUP A ");
		sb.append(" left outer join Q on A.ID= Q.FEE_GROUP_ID");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where Q.ID is null order by GROUP_NAME");
		if(!dependent){
			a = getList(sb.toString());
		}
		
		if(dependent){
			String s = formula;
			String keyword = "customgroup";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
		
			int md =0;
			while (m.find()) {
				String noofmonths =  m.group();
				String original = noofmonths;
			
				String sp = noofmonths;
				sp = sp.replace("[", "");
				sp = sp.replace("]", "");
				sp = sp.replace(keyword, "");
			}
			
		}
		
		
		return a;
	}*/
	
	
	
	public static void main(String args[]){
		/*String s = "customgroup[a_area_d] + customgroup[b_height] ";
		String keyword = "customgroup";
		Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
		Matcher m = p.matcher(s);
		ArrayList a = new ArrayList();
		int md =0;
		while (m.find()) {
			String g =  m.group();
			
		
			
			String sp = g;
			sp = sp.replace("[", "");
			sp = sp.replace("]", "");
			sp = sp.replace(keyword, "");
			sp = sp.substring(0,sp.indexOf("_"));
			a.add(sp);
			Logger.info(sp);
			
		}
		
		if(a.size()>0){
			
		}*/
		
		/*MapSet m = new MapSet();
		m.add("A", "1");
		m.add("B", "2");
		MapSet d = new MapSet();
		while(m.next()){
			d.add(m.getField(), m.get(m.getField()));
		}
		
		
		d.remove("A");
		Logger.info(d.getString("A")+"*************"+d.getString("B"));
		
		Logger.info(m.getString("A")+"*************"+m.getString("B"));*/
		try{
			System.out.println("came here ");
			ExchangeMessenger m = new ExchangeMessenger();
			m.setRecipient("svijay@edgesoftinc.com");
			//m.setBCC(Config.adminemail());
			m.setSubject("TEST");
			m.setContent("TEST");
			m.setAttachments("");
			m.deliver();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	public static ArrayList<MapSet> getAssociateUserGroups(int id, int start, int end, String search){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_USERS_GROUP R where USERS_ID  in (").append(id).append(")");
		sb.append(" ) ");
		
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.GROUP_NAME ASC) AS RowNum  ");
		sb.append(",");
		
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from USERS_GROUP A ");
		sb.append(" left outer join Q on A.ID= Q.USERS_GROUP_ID   ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where Q.ID is null ");
		if(Operator.hasValue(search)){
		sb.append(" AND LOWER(A.GROUP_NAME) like '%").append(Operator.sqlEscape(search)).append("%' ");
		}
		//sb.append(" order by GROUP_NAME");
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		a = getList(sb.toString());
		return a;
	}
	
	public static ArrayList<MapSet> getLsoList(String id){
		MapSet m = new MapSet();
		StringBuilder sb = new StringBuilder();
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		sb.append(" SELECT  ");
		sb.append("   A.*, CONCAT(LS.PRE_DIR,' ',LS.STR_NAME,' ' ,");
		sb.append("LS.STR_TYPE,' ' ,LS.SUF_DIR) AS STREET_NAME,LLT.DESCRIPTION AS LSO_TYPE, ");
		sb.append("CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   C.FIRST_NAME AS CREATED ");
		sb.append(",");
		sb.append("  U.FIRST_NAME as UPDATED "); 
		sb.append(" FROM LSO A JOIN LSO_STREET  LS ON A.LSO_STREET_ID=LS.ID JOIN LKUP_LSO_TYPE LLT ON A.LKUP_LSO_TYPE_ID=LLT.ID");
		sb.append( " JOIN USERS C ON A.CREATED_BY=C.ID JOIN USERS U ON A.UPDATED_BY=U.ID ");
		sb.append(" WHERE A.ACTIVE='Y' AND A.LKUP_LSO_TYPE_ID=2 AND PARENT_ID="+id);
		a = getList(sb.toString());
		
		return a;
	}
	
	
	
	public static ArrayList<MapSet> getAssociateUserTypes(int id, int start, int end, String search){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_TEAM R where USERS_ID  in (").append(id).append(")");
		sb.append(" ) ");
		
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.TYPE ASC) AS RowNum  ");
		sb.append(",");
		
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from LKUP_TEAM_TYPE A ");
		sb.append(" left outer join Q on A.ID= Q.USERS_ID   ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where Q.ID is null ");
		if(Operator.hasValue(search)){
		sb.append(" AND LOWER(A.TYPE) = '").append(Operator.sqlEscape(search)).append("' ");
		}
		//sb.append(" order by GROUP_NAME");
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		a = getList(sb.toString());
		return a;
	}
	
	
	public static ArrayList<MapSet> getAssociateUserRoles(int id, int start, int end, String search){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_USERS_ROLES R where USERS_ID  in (").append(id).append(")");
		sb.append(" ) ");
		
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.ROLE ASC) AS RowNum  ");
		sb.append(",");
		
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from LKUP_ROLES A ");
		sb.append(" left outer join Q on A.ID= Q.USERS_ID   ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where Q.ID is null ");
		if(Operator.hasValue(search)){
		sb.append(" AND LOWER(A.ROLE) = '").append(Operator.sqlEscape(search)).append("' ");
		}
		//sb.append(" order by GROUP_NAME");
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		a = getList(sb.toString());
		return a;
	}
	
	
	
	public static ArrayList<MapSet> getColumns(String query){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		if(Operator.hasValue(query)){
			Sage db = new Sage();
			db.query(query);
			String[] cols = db.COLUMNS;
			while(db.next()){
				
				MapSet h = new MapSet();
				for (int i=0; i<cols.length; i++) {
					String c = cols[i];
					System.out.println(" Columns " + i + " - " +c );
					h.add(c, db.getString(c));
				}
				
				a.add(h);
			}
			
			db.clear();
		
		}
		return a;
			
	}		
	
	
	
	
	public static int getRecordInserted(String query){
		int id =0;
		if(Operator.hasValue(query)){
			Sage db = new Sage();
			db.query(query);
			if(db.next()){
				id = db.getInt("ID");
			}
			
			db.clear();
		
		}
		return id;
			
	}		
	
	public static ArrayList<MapSet> getFeeNames(int id, int start, int end, String search){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		sb.append(" WITH Q AS(");
		sb.append(" select * from REF_USERS_GROUP R where USERS_ID  in (").append(id).append(")");
		sb.append(" ) ");
		
		
		sb.append(" SELECT * FROM (");
		sb.append(" SELECT ROW_NUMBER() OVER (ORDER BY A.GROUP_NAME ASC) AS RowNum  ");
		sb.append(",");
		
		sb.append(" A.* ");
		sb.append(",");	
		sb.append(" CONVERT(VARCHAR(10),A.CREATED_DATE,101) as C_CREATED_DATE ");
		sb.append(",");
		sb.append(" CONVERT(VARCHAR(10),A.UPDATED_DATE,101) as C_UPDATED_DATE ");
		sb.append(",");
		sb.append("   CU.USERNAME AS CREATED ");
		sb.append(",");
		sb.append("   UP.USERNAME as UPDATED "); 
		sb.append(" from USERS_GROUP A ");
		sb.append(" left outer join Q on A.ID= Q.USERS_GROUP_ID   ");
		sb.append("   LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" CU on A.CREATED_BY = CU.ID ");
		sb.append("	  LEFT OUTER JOIN ").append(Table.USERSTABLE).append(" UP on A.UPDATED_BY = UP.ID "); 
		sb.append(" where Q.ID is null ");
		if(Operator.hasValue(search)){
		sb.append(" AND LOWER(A.GROUP_NAME) = '").append(Operator.sqlEscape(search)).append("' ");
		}
		//sb.append(" order by GROUP_NAME");
		sb.append(" ) ");
		sb.append(" AS RowConstrainedResult WHERE RowNum >").append(start).append(" AND RowNum <=").append(end).append(" order by rownum ");
		a = getList(sb.toString());
		return a;
	}
	
	public static String getCustomFieldsSolr(){
		StringBuilder sb = new StringBuilder();
		Sage db = new Sage();
		String command = "select stuff((select  ','+QUOTENAME(convert(varchar(100),id)) + ' as '+NAME+'_s' from FIELD  for xml path('')),1,1,'') as COMBINED ";
		db.query(command);
		sb.append(",");
		while(db.next()){
			sb.append(db.getString("COMBINED"));
			
		}
		
		db.clear();
		return sb.toString();
				
	}
	
	public static int getActiveJobCount(){
		Sage db = new Sage();
		String command = "SELECT COUNT(*) AS COUNT FROM JOB_SCHEDULE WHERE ACTIVE='Y'; ";
		db.query(command);
		int count=0;
		while(db.next()){
			
			count =db.getInt("COUNT");
		}
		
		db.clear();
		return count;
				
	}
	
	
	
	public static int getJobId(int id){
		Sage db = new Sage();
		String sql="SELECT * FROM  JOB_SCHEDULE where ACTIVE='Y' AND LKUP_JOB_SCHEDULE_TYPE_ID="+id;
        db.query(sql);
        int jobId=0;
		    if(db.next()){
		  	  jobId=db.getInt("ID");
		    } 
		
		db.clear();
		return jobId;
				
	}
	
	
	public static void scheduleJob(int id){
		Sage db = new Sage();
		String sql="INSERT INTO JOB_SCHEDULE (LKUP_JOB_SCHEDULE_TYPE_ID, START_DATE, END_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP) VALUES ("+id+",current_timestamp,current_timestamp, 'Y', 890, current_timestamp, 890, current_timestamp, '', '')";
        db.update(sql);
		db.clear();
	}
	  
	public static void updateJobSchedule(int id){
		Sage db = new Sage();
		String sql="UPDATE JOB_SCHEDULE SET ACTIVE='N' ,END_DATE= current_timestamp, UPDATED_DATE =current_timestamp WHERE ACTIVE='Y' and LKUP_JOB_SCHEDULE_TYPE_ID="+id;
        db.update(sql);
        db.clear();
				
	}	
	
	
	
	
	
	
	public static boolean checkRolesAdmin(String roles[]){
		boolean r = false;
	
		try{
			String ids = Arrays.toString(roles);
			ids = Operator.replace(ids, "[", "");
			ids = Operator.replace(ids, "]", "");
			/*Logger.info(roles.length);
			for (int i=0; i<roles.length; i++) {
				String role = roles[i];
				Logger.info(role+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				if(role.equals("ADMIN")){
					r = true;
				}
			}*/
			
			
			
			if(Operator.hasValue(ids)){
				Sage db = new Sage();
				Logger.info(ids+"********ids");
				String command = "select TOP 1 *   from LKUP_ROLES where ACTIVE='Y' AND ADMIN='Y' AND ID  in ("+Operator.sqlEscape(ids)+")";
				db.query(command);
				if(db.next()){
					r = true;
				}
				db.clear();
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
	
		return r;
	}
	
	
	public static String assessorReport(String date) throws Exception {
        Sage db = new Sage();
        StringBuilder sb = new StringBuilder();
        try {
        	Timekeeper d = new Timekeeper();
        	d.setDate(date);
        	String mm = d.mm();
        	String yyyy = d.yyyy();
        	
        	
        	
            StringBuilder sql = new StringBuilder();
            //obc
            /*sql.append("SELECT 'BH' || SUBSTR(VA.ACT_NBR,3) ACTNBR,VA.DESC as DESCRIPTION,VA.ACT_TYPE,INTEGER(VA.VALUATION) VALUE,");
            sql.append("SUBSTR(CHAR(VA.ISSUED_DATE),3,2) || SUBSTR(CHAR(VA.ISSUED_DATE),6,2) || SUBSTR(CHAR(VA.ISSUED_DATE),9) ISSUEDATE,");
            sql.append("ASS.APN,SUBSTR(ASS.FRST_OWNR_NAME,1,32) NAME,");
            sql.append("SUBSTR(RTRIM(LTRIM(ASS.SITUS_HOUSE_NO)) ||");

            //sql.append(" COALESCE(' ' || RTRIM(LTRIM(ASS.SITUS_FRACTION)),'') ||");
            sql.append(" COALESCE(' ' || RTRIM(LTRIM(ASS.SITUS_DIRECTION)),'') ||");
            sql.append(" COALESCE(' ' || RTRIM(LTRIM(ASS.SITUS_STREET_NAME)),'') ||");
            sql.append(" COALESCE(' ' || RTRIM(LTRIM(ASS.SITUS_UNIT)),''),1,26) AS ADDRESS,");
            sql.append("ASS.SITUS_CITY_STATE,ASS.SITUS_ZIP");
            sql.append(" FROM (V_ACTIVITY VA JOIN V_LSO_APN VLA ON VA.LSO_ID = VLA.LSO_ID)");
            sql.append(" JOIN ASSESSOR ASS ON VLA.APN = ASS.APN");
            sql.append(" WHERE VA.ACT_TYPE IN ('BLDG','ABEST','BLDGFS','COMPAV','CONCON','DEMO','ELEC','FENCE','GAME','GRAD','LAND','MAJREM','MECH','PARK','PAVING','PBARR','PLUM','RETAIN','ROOF','SDBL','SIGN')");
            sql.append(" AND MONTH(VA.ISSUED_DATE) = " + mm + " AND YEAR(VA.ISSUED_DATE) = " + yyyy);
            sql.append(" ORDER BY ASS.SITUS_STREET_NAME,ASS.SITUS_DIRECTION,ASS.SITUS_HOUSE_NO,ASS.SITUS_FRACTION");*/
            
            sql.append(" select DISTINCT 'BH'+''+ SUBSTRING(ACT_NBR,3,7) as ACTNBR , ");
            sql.append("  ");
            sql.append(" CAST (VALUATION_CALCULATED AS INT) as VALUE ");
            sql.append(" ,ISSUED_DATE ");
            sql.append(" ,FORMAT(ISSUED_DATE,'yyMMdd')  as ISSUEDATE ");
            //sql.append(" ,A.DESCRIPTION ");
            sql.append(",REPLACE(REPLACE(A.DESCRIPTION , char(13), ''), char(10), ' ') as DESCRIPTION ");
            sql.append(" , RLA.APN ");
            sql.append(" ,SUBSTRING(AO.FRST_OWNR_NAME,1,32) as  NAME ");
            sql.append(" , ");
            sql.append(" SUBSTRING(RTRIM(LTRIM(ASS.SITUS_HOUSE_NO)) +''+ COALESCE(' ' + RTRIM(LTRIM(ASS.SITUS_DIRECTION)),'')  ");
            sql.append(" +''+  ");
            sql.append(" COALESCE(' ' + RTRIM(LTRIM(ASS.SITUS_STREET_NAME)),'')  ");
            sql.append("  ");
            sql.append(" +''+ COALESCE(' ' +''+ RTRIM(LTRIM(ASS.SITUS_UNIT)),''),1,26) AS ADDRESS, ");
            sql.append("  ");
            sql.append(" ASS.SITUS_CITY_STATE,ASS.SITUS_ZIP  ");
            sql.append("  ");
            sql.append(" from ACTIVITY A  ");
            sql.append(" LEFT OUTER JOIN REF_LSO_PROJECT RLP on A.PROJECT_ID=RLP.PROJECT_ID AND RLP.ACTIVE='Y' ");
            sql.append(" LEFT OUTER JOIN V_CENTRAL_ADDRESS VCA on RLP.LSO_ID= VCA.LSO_ID ");
            sql.append(" JOIN REF_LSO_APN RLA on VCA.LAND_ID=RLA.LSO_ID AND RLA.ACTIVE='Y' ");
            sql.append(" JOIN ASSESSOR_OWNER AO on RLA.APN = AO.APN AND AO.ACTIVE='Y' ");
            sql.append("  ");
            sql.append(" JOIN ASSESSOR_SITE ASS on RLA.APN = ASS.APN AND ASS.ACTIVE='Y' ");
            sql.append(" where  LKUP_ACT_TYPE_ID IN (1,2,3,4,8,10,12,13,15,16,33,34,35,36,37,50,51,57,66,160)  ");
            sql.append(" AND MONTH(ISSUED_DATE) = ").append(mm).append(" AND YEAR(ISSUED_DATE) = ").append(yyyy).append("  ");
            sql.append("  ");
            sql.append(" ");
            Logger.info("Assessor SQL : " + sql.toString());
            db.query(sql.toString());

			//String location ="Z:/temp/Beverly_Hills_Monthly_Report_" + mm + yyyy + ".txt";
/*			String location=null;

			   java.util.ResourceBundle obcProperties = java.util.ResourceBundle.getBundle("obc");

					  String PDF_PATH_AIX = obcProperties.getString("PDF_PATH_AIX");
					  String PDF_PATH_WINDOWS = obcProperties.getString("PDF_PATH_WINDOWS");

					  //			checking for ostype
					  String ostype = System.getProperty("os.name");
					  logger.debug("os name is " + ostype);

					  //basedirectory
					  if (ostype.equalsIgnoreCase("aix")) {
						location = PDF_PATH_AIX;
					  }
					  else {
						location = PDF_PATH_WINDOWS;
					  }

			location = location+"/"+ mm + yyyy + ".txt";*/

			String path =  mm + yyyy + ".txt";

 
			
            while (db.next()) {
              

                //OLD COMMENTED EXTRACT BEFORE JAN 2018
                /*// APN
                 *  StringBuffer row = new StringBuffer();
                if ((rs.getString("APN") == null) || rs.getString("APN").equals("")) {
                    row.append("0000000000");
                }
                else {
                    row.append(rs.getString("APN")); // 10
                }

                //if(rs.getString("ACT_NBR").equals("BS0412272")) logger.debug(row);
                // PERMIT NUMBER
                row.append(rs.getString("ACTNBR")); // 10 + 9 = 19

                row.append(rs.getString("ISSUEDATE")); // 19 + 6 = 25

                row.append("000"); // 25 + 3 = 28

                row.append("          "); // 28 + 10 = 38

                row.append(StringUtils.lpad(rs.getString("VALUE"), 9, "0")); // 38 + 9 = 47

                row.append("             "); // 47 + 13 = 60
                row.append(rs.getString("DESCRIPTION")); // 51 + 25 = 85

                row.append(StringUtils.rpad(rs.getString("NAME"), 32, " ")); // 85 + 32 = 117

                row.append(StringUtils.rpad(rs.getString("ADDRESS"), 26, " ")); // 117 + 26 = 143

                row.append("          "); // 143 + 10 = 153

                row.append("       "); //153 + 7 = 160
*/                
                
                Logger.info(db.getString("ACTNBR"));
                String ACTNBR = db.getString("ACTNBR");
        		String DESCRIPTION= db.getString("DESCRIPTION");
        		String ACT_TYPE= "BLDG"; 	
        		String VALUE= db.getString("VALUE");
        		String ISSUEDATE=db.getString("ISSUEDATE");
        		String APN=db.getString("APN");
        		String NAME=db.getString("NAME");
        		String ADDRESS=db.getString("ADDRESS");
        		String SITUS_CITY_STATE="BEVERLY HILLS CA";
        		String SITUS_ZIP="902100000";
        		
        			
        		
        		
        		if(!Operator.hasValue(APN)){
        			APN = String.format("%010d",0);	
        		}
        		
        		sb.append(addSpace(APN, 10, false)); // PAIN 10 1-10
        		sb.append(addSpace(ACTNBR, 9, false)); // PERMITID 9 11-19 
        		sb.append(addSpace(ISSUEDATE, 6, false));// PDATE 6 20-25
        		
        		sb.append(addSpace("000", 3, false)); // PUNITS 3 26-28
        		sb.append(addSpace("0000000000", 10, false));// PDIMENSIONS 10 29-38
        		sb.append(addSpace(VALUE, 9, true)); // PVALUE 9 39-47
        		sb.append(addSpace(DESCRIPTION, 38, false)); //PINFO 38 48-85
        		sb.append(addSpace(NAME, 32, false));// PNAME 32 86-117
        		sb.append(addSpace(ADDRESS, 26, false));// PADDR 26 118-143
        		sb.append(addSpace("", 10, false));// PHONE 10 144-153
        		sb.append(addSpace("", 7, false));// FILLER 7 154-160
        		sb.append("\r\n");
                //Logger.info(sb.toString());
            }
          
            
          
			
        }
         
         catch (Exception e) {
            Logger.error("Error: " + e.getMessage());
            throw e;
        }
        db.clear();
        return sb.toString();
    }
	
	
	public static String addSpace(String col,int totalspace,boolean left){
		String c = "";
		if(Operator.hasValue(col)){
			c = col;
		}
		
		if(c.length()>totalspace){
			c =c.substring(0, totalspace);
		}
		String p = "";
		
		if(left){
			p = String.format("%"+totalspace+"s", c);	
		}else  {
			p = String.format("%-"+totalspace+"s", c);	
		}
		
        return p;
		
		
	}
	
	/*public static JSONObject searchLsoParent(int lsoId){
		JSONObject o = new JSONObject();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from LSO where ACTIVE ='Y' AND  ID =").append(lsoId);
		String command = sb.toString();
		Sage db = new Sage();
		db.query(command);
		while(db.next()){
			
		}
		
	}*/
	
	public static JSONObject searchLso(String strno,String mod,String street,String unit, String lsoId){
		JSONObject o = new JSONObject();
		Sage db = new Sage();
		try{
			StringBuilder sb = new StringBuilder();
			if(Operator.hasValue(lsoId)){
				sb.append("select * from LSO where ACTIVE ='Y' AND  ID =").append(lsoId);
			}else {
				sb.append("select * from LSO where ACTIVE ='Y' AND ");
				sb.append(" STR_NO =").append(Operator.sqlEscape(strno));
				sb.append(" AND ");
				sb.append(" LSO_STREET_ID =").append(Operator.sqlEscape(street));
				if(Operator.hasValue(mod)){
					sb.append(" AND ");
					sb.append(" STR_MOD =").append(Operator.sqlEscape(mod));
				}
				
				if(Operator.hasValue(unit)){
					sb.append(" AND ");
					sb.append(" UNIT =").append(Operator.sqlEscape(unit));
				}
				
				
			}
			db.query(sb.toString());
			
			if(db.next()){
				o.put("ID",db.getInt("ID"));
				o.put("LKUP_LSO_TYPE_ID",db.getInt("LKUP_LSO_TYPE_ID"));
			}
			
			
			if(o.getInt("LKUP_LSO_TYPE_ID")==1){
				JSONObject d = new JSONObject();
				db.query(lsoCommand(o.getInt("ID")));
				if(db.next()){
					d.put("LSO_ID", db.getInt("LSO_ID"));
					d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
					d.put("ADDRESS", db.getString("ADDRESS"));
					d.put("DESCRIPTION", db.getString("DESCRIPTION"));
					d.put("APN", db.getString("APN"));
					d.put("LSO_TYPE", db.getString("LSO_TYPE"));
					
				}
				o.put("L",d);
			}
			else 
			if(o.getInt("LKUP_LSO_TYPE_ID")==2){
				JSONObject d = new JSONObject();
				db.query(lsoCommand(o.getInt("ID")));
				if(db.next()){
					d.put("LSO_ID", db.getInt("LSO_ID"));
					d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
					d.put("ADDRESS", db.getString("ADDRESS"));
					d.put("DESCRIPTION", db.getString("DESCRIPTION"));
					d.put("APN", db.getString("APN"));
					d.put("LSO_TYPE", db.getString("LSO_TYPE"));
					o.put("LAND_ID", db.getInt("LAND_ID"));
				}
				o.put("S",d);
				
				d = new JSONObject();
				db.query(lsoCommand(o.getInt("LAND_ID")));
				if(db.next()){
					d.put("LSO_ID", db.getInt("LSO_ID"));
					d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
					d.put("ADDRESS", db.getString("ADDRESS"));
					d.put("DESCRIPTION", db.getString("DESCRIPTION"));
					d.put("APN", db.getString("APN"));
					d.put("LSO_TYPE", db.getString("LSO_TYPE"));
					//o.put("LAND_ID", db.getInt("LAND_ID"));
				}
				o.put("L",d);
				
			}
			else 
				if(o.getInt("LKUP_LSO_TYPE_ID")==3){
					JSONObject d = new JSONObject();
					db.query(lsoCommand(o.getInt("ID")));
					if(db.next()){
						d.put("LSO_ID", db.getInt("LSO_ID"));
						d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
						d.put("ADDRESS", db.getString("ADDRESS"));
						d.put("DESCRIPTION", db.getString("DESCRIPTION"));
						d.put("APN", db.getString("APN"));
						d.put("LSO_TYPE", db.getString("LSO_TYPE"));
						o.put("LAND_ID", db.getInt("LAND_ID"));
						o.put("PARENT_ID", db.getInt("PARENT_ID"));
					}
					o.put("O",d);
					
					d = new JSONObject();
					db.query(lsoCommand(o.getInt("PARENT_ID")));
					if(db.next()){
						d.put("LSO_ID", db.getInt("LSO_ID"));
						d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
						d.put("ADDRESS", db.getString("ADDRESS"));
						d.put("DESCRIPTION", db.getString("DESCRIPTION"));
						d.put("APN", db.getString("APN"));
						d.put("LSO_TYPE", db.getString("LSO_TYPE"));
						//o.put("LAND_ID", db.getInt("LAND_ID"));
					}
					o.put("S",d);
					
					d = new JSONObject();
					db.query(lsoCommand(o.getInt("LAND_ID")));
					if(db.next()){
						d.put("LSO_ID", db.getInt("LSO_ID"));
						d.put("PRIMARY_ID", db.getInt("PRIMARY_ID"));
						d.put("ADDRESS", db.getString("ADDRESS"));
						d.put("DESCRIPTION", db.getString("DESCRIPTION"));
						d.put("APN", db.getString("APN"));
						d.put("LSO_TYPE", db.getString("LSO_TYPE"));
						//o.put("LAND_ID", db.getInt("LAND_ID"));
					}
					o.put("L",d);
					
				}
			
			Logger.info(o.toString());
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		return o;
	}
	
	
	/*public static JSONObject searchLsoID(String strno,String mod,String street,String unit, String lsoId){
		JSONObject o = new JSONObject();
		Sage db = new Sage();
		try{
			JSONObject r = searchLsoId(strno, mod, street, unit, lsoId);
			StringBuilder sb = new StringBuilder();
			if(r.getInt("LKUP_LSO_TYPE_ID")==1){
				JSONArray a = new JSONArray();
				
			}
			
			if(r.getInt("LKUP_LSO_TYPE_ID")==2){
				JSONArray a = new JSONArray();
				
			}
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		return o;
	}*/
	
	public static String lsoCommand(int lsoId){
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" 	V.LSO_ID, ");
		sb.append(" 	V.PRIMARY_ID, ");
		sb.append(" 	CAST(V.STR_NO AS VARCHAR(10)) + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.STR_MOD IS NOT NULL THEN ' ' + V.STR_MOD ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.PRE_DIR IS NOT NULL THEN ' ' + V.PRE_DIR ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		' ' + V.STR_NAME + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.STR_TYPE IS NOT NULL THEN ' ' + V.STR_TYPE ");
		sb.append(" 			ELSE '' END + ");
		sb.append(" 		CASE ");
		sb.append(" 			WHEN V.UNIT IS NOT NULL AND V.UNIT <> '' THEN ' ' + V.UNIT ");
		sb.append(" 			ELSE '' ");
		sb.append(" 	END AS ADDRESS, ");
		sb.append(" 	L.DESCRIPTION, ");
		sb.append(" 	V.APN, ");
		sb.append(" 	V.LSO_TYPE, ");
		sb.append(" 	V.LAND_ID, ");
		sb.append(" 	V.PARENT_ID ");
		sb.append(" from V_CENTRAL_ADDRESS V ");
		sb.append(" 	JOIN LSO AS L ON V.LSO_ID = L.ID ");
		sb.append(" WHERE LSO_ID=").append(lsoId);
		return sb.toString();
	}

	public static boolean saveAPNOwner(Cartographer map){
		boolean result = false;
		StringBuffer sbRLA = new StringBuffer();
		StringBuffer sbAO = new StringBuffer();
		StringBuffer sbAD = new StringBuffer();
		StringBuffer sbAS = new StringBuffer();
		StringBuffer sbAT = new StringBuffer();
		Sage db = new Sage();
		try{
		String lsoId = map.getString("ID");
		String apn = map.getString("APN").trim();
		String ownerName = map.getString("OWNER_NAME").trim();
		String type =map.getString("LSO_TYPE");
		String startDate = map.getString("RLA_START_DATE");
		if(Operator.hasValue(apn)){
		String updateRefLso = "UPDATE REF_LSO_APN SET ACTIVE='N',UPDATED_BY="+map.getString("_userid")+",UPDATED_DATE=CURRENT_TIMESTAMP WHERE LSO_ID="+lsoId+"";
		db.update(updateRefLso);
		
		sbRLA = sbRLA.append("INSERT INTO REF_LSO_APN (LSO_ID, APN, LSO_TYPE,START_DATE, CREATED_BY, UPDATED_BY, CREATED_IP, UPDATED_IP)");
		sbRLA.append("  VALUES ("+lsoId+", "+Operator.checkString(apn)+", "+Operator.checkString(type)+", '"+startDate+"', "+map.getString("_userid")+","+map.getString("_userid")+" , "+Operator.checkString(map.getRemoteIp())+", "+Operator.checkString(map.getRemoteIp())+") ");
		db.update(sbRLA.toString());
		if(Operator.hasValue(ownerName)){
		String updateAO ="UPDATE ASSESSOR_OWNER SET ACTIVE='N',UPDATED_BY="+map.getString("_userid")+",UPDATED_DATE=CURRENT_TIMESTAMP WHERE ACTIVE='Y' AND APN="+Operator.checkString(apn)+"";
		db.update(updateAO);
		sbAO=sbAO.append("INSERT INTO ASSESSOR_OWNER (APN, FRST_OWNR_NAME, CREATED_BY,  UPDATED_BY,  CREATED_IP, UPDATED_IP) ");
		sbAO.append(" VALUES ("+Operator.checkString(apn)+", "+Operator.checkString(ownerName)+","+map.getString("_userid")+","+map.getString("_userid")+","+Operator.checkString(map.getRemoteIp())+","+Operator.checkString(map.getRemoteIp())+")");
		db.update(sbAO.toString());
		
		String updateAS ="UPDATE ASSESSOR_SITE SET ACTIVE='N',UPDATED_BY="+map.getString("_userid")+",UPDATED_DATE=CURRENT_TIMESTAMP WHERE ACTIVE='Y' AND APN="+Operator.checkString(apn)+"";
		db.update(updateAS);
		sbAS=sbAS.append("INSERT INTO ASSESSOR_SITE (APN, CREATED_BY,  UPDATED_BY,  CREATED_IP, UPDATED_IP) ");
		sbAS.append(" VALUES ("+Operator.checkString(apn)+","+map.getString("_userid")+","+map.getString("_userid")+","+Operator.checkString(map.getRemoteIp())+","+Operator.checkString(map.getRemoteIp())+")");
		db.update(sbAS.toString());
		
		String updateAT ="UPDATE ASSESSOR_TAX SET ACTIVE='N',UPDATED_BY="+map.getString("_userid")+",UPDATED_DATE=CURRENT_TIMESTAMP WHERE ACTIVE='Y' AND APN="+Operator.checkString(apn)+"";
		db.update(updateAT);
		sbAT=sbAT.append("INSERT INTO ASSESSOR_TAX (APN, CREATED_BY,  UPDATED_BY,  CREATED_IP, UPDATED_IP) ");
		sbAT.append(" VALUES ("+Operator.checkString(apn)+","+map.getString("_userid")+","+map.getString("_userid")+","+Operator.checkString(map.getRemoteIp())+","+Operator.checkString(map.getRemoteIp())+")");
		db.update(sbAT.toString());
		
		String updateAD ="UPDATE ASSESSOR_DATA SET ACTIVE='N',UPDATED_BY="+map.getString("_userid")+",UPDATED_DATE=CURRENT_TIMESTAMP WHERE ACTIVE='Y' AND APN="+Operator.checkString(apn)+"";
		db.update(updateAD);
		sbAD=sbAD.append("INSERT INTO ASSESSOR_DATA (APN, CREATED_BY,  UPDATED_BY,  CREATED_IP, UPDATED_IP) ");
		sbAD.append(" VALUES ("+Operator.checkString(apn)+","+map.getString("_userid")+","+map.getString("_userid")+","+Operator.checkString(map.getRemoteIp())+","+Operator.checkString(map.getRemoteIp())+")");
		db.update(sbAD.toString());
		}
		result=true;
		}
		
		db.clear();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void replaceSolrConfigFiles(){
		try{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet();
		URI website = new URI(CsApiConfig.getString("scheduler.solr.config"));
		httpget.setURI(website);
		httpclient.execute(httpget);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<MapSet> getFeeList(String table,int start, int end,String sortfield,String sortorder,String query, String searchField, String startDate,String expDate){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getFeeList(table,start, end, sortfield, sortorder, query, searchField,startDate,expDate));
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			
			a.add(h);
		}
		db.clear();
		return a;
			
	}	
	public static MapSet getFeeGroupDetails(String feeId){
		MapSet a = new MapSet();
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getFeeGroupDetails(feeId));
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				//Logger.info(c+"^^^^^^^^^^^^^^^^^^");
				a.add(c, db.getString(c));
			}
		}
		db.clear();
		return a;
			
	}
	public static int getFeeListCount(String table,int start, int end,String sortfield,String sortorder,String query, String searchField, String startDate,String expDate){
		int count=0;
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getFeeListCount(table,start, end, sortfield, sortorder, query, searchField,startDate,expDate));
		//String[] cols = db.COLUMNS;
		
		while(db.next()){
		count=db.getInt("COUNT");
			/*MapSet h = new MapSet();
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				h.add(c, db.getString(c));
			}
			
			a.add(h);*/
		}
		db.clear();
		return count;
	}	
	public static MapSet getFeeGroupDefaultDetails(){
		MapSet a = new MapSet();
		
		Sage db = new Sage();
		
		db.query(AdminSQL.getFeeGroupDefaultDetails());
		String[] cols = db.COLUMNS;
		
		while(db.next()){
		
			for (int i=0; i<cols.length; i++) {
				String c = cols[i];
				//Logger.info(c+"^^^^^^^^^^^^^^^^^^");
				a.add(c, db.getString(c));
			}
		}
		db.clear();
		return a;
			
	}
	
	
	public static ByteArrayOutputStream view(String fileName) {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		byte[] b = new byte[0];
		try {
		

			
			FileVO fvo = new FileVO();
			
			
			String filepath = Config.getString("files.storage_path")+""+fileName;
			Logger.highlight(filepath);
			
		
			fvo.setFullpath(filepath);
			String fullpath = fvo.getFullpath();
		
		
		
			if (Operator.hasValue(fullpath)) {
				File f = new File(fullpath);
				if (f.exists() ) {
					Logger.info(" doc "+fullpath);
					if(fullpath.endsWith(".doc")){
						Logger.info("came here doc "+fullpath);
					}
					RandomAccessFile raf = new RandomAccessFile(f, "r");
				//	map.RESPONSE.setContentLength((int) raf.length());
		            //out = response.getOutputStream();
					byte[] loader= new byte [ (int) raf.length() ];
					while ((raf.read( loader )) > 0) {
						o.write(loader);
					}
					b = o.toByteArray();
					raf.close();
				}
			}
			
		}
		catch (Exception e) { 
			Logger.error(e.getMessage());
		}
		return o;
	}
}
