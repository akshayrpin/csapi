package csapi.common;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringEscapeUtils;

import alain.core.db.Sage;
import alain.core.utils.Timekeeper;

public class Test1 {

	public static String doDb() {
		StringBuilder sb = new StringBuilder();
		try{
		Sage db = new Sage();
		String command = "select * from LSO_STREET";
		System.out.println("***"+command);
		if(db.query(command)){
			System.out.println(db.size());
			while(db.next()){
				sb.append(db.getString("STR_NO") +" " +db.getString("PRE_DIR") +" " + db.getString("STR_NAME") + " "+ db.getString("STR_TYPE")  );
			}
		}
		db.clear();
		
		} catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	 public void run() {
	      try {
	    	 
	    	 Timekeeper k = new Timekeeper(); 
	         String text = k.fullDateMilitaryTime()+"|Hell'o Wor@ld78$|";
	         k.addMinute(30);
	         text = text + k.fullDateMilitaryTime();
	         String key = "8knujv90b$%@97tr"; // 128 bit key

	         // Create key and cipher
	         Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
	         Cipher cipher = Cipher.getInstance("AES");

	         // encrypt the text
	         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
	         byte[] encrypted = cipher.doFinal(text.getBytes());
	         String s = StringEscapeUtils.escapeJava(new String(encrypted));
	         System.out.println("encrypted::"+s);

	         // decrypt the text
	         String g = StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeJava(s));
	         cipher.init(Cipher.DECRYPT_MODE, aesKey);
	         String decrypted = new String(cipher.doFinal(g.getBytes()));
	         System.out.println("decrypted::"+decrypted);
	         
	         String decrypted1 = new String(cipher.doFinal(encrypted));
	         System.out.println("decrypted::"+decrypted);
	         
	      }catch(Exception e) {
	         e.printStackTrace();
	      }
	    }
	 
	    public static void main(String[] args) {
	    	Test1 app = new Test1();
	       app.run();
	    }

}
