package csapi.impl.print;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;




import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;



public class CSDOTExtractSQL {

	public static HashMap<String, String> p  = getProperties();

	public static HashMap<String, String> getProperties(){
		HashMap<String, String> p = new HashMap<String, String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			 
			
			input = new FileInputStream("C:\\cronjobs\\parkingdata\\parkingdata.properties");
			
			if (input == null) {
				System.err.println("Unable to find munisaclara.properties");
				return p;
			}
	 
			prop.load(input);
	 
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				//System.out.println("Key : " + key + ", Value : " + value);
				p.put(key, value);
			}
			
			
	 
	 
		} catch (Exception ex) {
			System.err.println("Problem reading properties file"+ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					System.err.println("Problem closing properties file"+e.getMessage());
				}
			}
		}
		return p;
	}
	
	
	public static void run(){
		  Sage db = new Sage();
		try{
		StringBuilder sb = new StringBuilder();
		sb.append("Beverly Hills Permit Upload.txt");
		
		StringBuilder local = new StringBuilder("c:/cronjobs/cbhneogov/files/");
		local.append(sb.toString());
		
		  File file = new File(local.toString());
	      sb = new StringBuilder();
	      
	      String command = extract();
	      db.query(command);
	      
	      while(db.next()){
	    	  sb.append(db.getString("PERMIT_NO"));
	    	  sb.append("\t");
	    	  
	    	  sb.append(db.getString("PLATE"));
	    	  sb.append("\t");
	    	  
	    	  sb.append(db.getString("EXP_DATE"));
	    	  sb.append("\t");
	    	  
	    	  sb.append(db.getString("DESCRIPTION"));
	    	  sb.append("\t");
	    	  
	    	  sb.append("");
	    	  sb.append("\t");
	    	  
	    	  String name ="";
	    	  try{
	    		  
	    	  JSONArray l = new JSONArray(db.getString("list_people"));
	    	  
	    	  for(int i=0;i<l.length();i++){
	    		  if(!Operator.hasValue(name)){
	    			  name = l.getJSONObject(i).getString("people_name");
	    			  break;
	    		  }
	    	  }
	    	  
	    	  }catch (Exception e){
	    		  Logger.error(e.getMessage());
	    	  }
	    	  sb.append(name);
	    	  sb.append(" ");
	    	  sb.append(db.getString("ADDRESS"));
	    	  sb.append("\r\n");
	      }

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(sb.toString());
			bw.close();
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
	}
	
	

	public static String extract(){
		StringBuilder sb = new StringBuilder();
		sb.append("	WITH Q AS ( ");
				sb.append("			select  ID as MAIN_ID from activity WHERE ACTIVE='Y'  AND EXP_DATE='2021-09-30' AND LKUP_ACT_TYPE_ID in (251,252,278,279,280) ");
				sb.append(") ");
				sb.append(" ");
				sb.append("select   DISTINCT A.ACT_NBR as PERMIT_NO, '' as PLATE, CONVERT(VARCHAR(10),A.EXP_DATE,101) +' 11:59 PM' as EXP_DATE, ");
				sb.append("CASE WHEN RLA.ID>0 THEN LTRIM(RTRIM(ATT.DESCRIPTION))+' Onetime' ELSE LTRIM(RTRIM(ATT.DESCRIPTION)) END as DESCRIPTION ");
				sb.append(", CONVERT(varchar(100), L.STR_NO)+' '+  L.STR_NAME+' '+ L.STR_TYPE+' '+ CASE WHEN PRE_DIR is null THEN '' ELSE PRE_DIR END+''+CASE WHEN L.UNIT is null then '' ELSE L.UNIT END as ADDRESS				 ");
				sb.append(" ");
				sb.append(", (        ");
				sb.append("			SELECT   ");
				sb.append(" ");
				sb.append(" ");
				sb.append("			UPPER(FIRST_NAME) + ' ' + UPPER(LAST_NAME)   as people_name ,   ");
				sb.append("			LUT.TYPE as people_type, ");
				sb.append("			LUT.ID ");
				sb.append(" ");
				sb.append(" ");
				sb.append("			FROM ACTIVITY A   ");
				sb.append("			LEFT OUTER join REF_ACT_USERS RAU on A.ID=RAU.ACTIVITY_ID  AND RAU.ACTIVE='Y' ");
				sb.append("			LEFT OUTER JOIN   REF_PROJECT_USERS RAUP on A.PROJECT_ID=RAUP.PROJECT_ID  AND RAUP.ACTIVE='Y'   ");
				sb.append("			join REF_USERS RU on (RAU.REF_USERS_ID=RU.ID  OR      RAUP.REF_USERS_ID=RU.ID   ) ");
				sb.append("			left outer join LKUP_USERS_TYPE as LUT on RU.LKUP_USERS_TYPE_ID=LUT.ID    ");
				sb.append("			JOIN USERS AS U ON RU.USERS_ID = U.ID   ");
				sb.append("			left outer join PEOPLE P on U.ID = P.USERS_ID   ");
				sb.append("			WHERE A.ID=MAIN_ID  ");
				sb.append(" ");
				sb.append(" ");
				sb.append("			FOR JSON PATH, INCLUDE_NULL_VALUES  )as list_people	  ");
				sb.append(" ");
				sb.append("from  ACTIVITY A   ");
				sb.append("join Q on A.ID=Q.MAIN_ID  ");
				sb.append("JOIN  LKUP_ACT_TYPE ATT ON A.LKUP_ACT_TYPE_ID=ATT.ID  AND ATT.ID in (251,252,278,279,280) ");
				sb.append(" ");
				sb.append("LEFT OUTER JOIN   LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
				sb.append("LEFT OUTER JOIN  PROJECT P on  A.PROJECT_ID=P.ID  ");
				sb.append("LEFT OUTER join REF_LSO_PROJECT R on A.PROJECT_ID=R.PROJECT_ID  AND R.ACTIVE='Y' ");
				sb.append("LEFT OUTER join REF_LSO_ACT RLA on A.ID = RLA.ACTIVITY_ID ");
				sb.append("LEFT OUTER join  V_CENTRAL_ADDRESS L on (R.LSO_ID=L.LSO_ID OR RLA.LSO_ID=L.LSO_ID) ");
				sb.append("left outer join REF_PROJECT_PARKING RPP on A.PROJECT_ID =RPP.PROJECT_ID ");
				sb.append(" ");
				sb.append(" ");
				return sb.toString();
	}
	
	
	
	public static void main(String args[]){
		FTPClient client = new FTPClient();
		FileInputStream fis = null;
		try {
			String filename= p.get("FILE_NAME");
			
		    client.connect(p.get("FTPPUBLICHOST"));
		    client.login(p.get("FTPPUBLICUSER"), p.get("FTPPUBLICPASS"));
		    File file = new File(p.get("LOCAL_PATH")+"/"+filename);
		    fis = new FileInputStream(file);
		    client.storeFile("test.txt", fis);
		    
		
		   
		    
		    
		    
		    client.logout();
		    client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Problem Uploading to FTP"+e.getMessage());
		} finally {
		    try {
		        if (fis != null) {
		            fis.close();
		        }
		        client.disconnect();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	System.err.println("Problem Uploading to FTP FINAL"+e.getMessage());
		    }
		}
		
	}
	
	
}















