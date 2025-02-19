package csapi.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;

import alain.core.file.FileClerk;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class FileUtil {

	
	
	public static String getFileDirectory(String file) {
    	try{
    		File f = new File(file);
    	    String absolutePath = f.getAbsolutePath();
    	    String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
    	    return filePath;
    	}
    	catch(Exception e){ }
    	return "";
    }

    public static boolean fileAgeLessThan(int numhours, long lastmoddate) {
    	try {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String fds = sdf.format(lastmoddate);
    		Timekeeper fd = new Timekeeper();
    		fd.setDate(fds);
    		Timekeeper rn = new Timekeeper();
    		rn.addHour(numhours*-1);
    		if (fd.lessThan(rn)) {
    			return true;
    		}
    		return false;
    	}
    	catch (Exception e) {
    		return true;
    	}
    }

    public static String simplifyFilename(String filename) {
		if (!Operator.hasValue(filename)) { return ""; }
		filename = filename.replaceAll("[^a-zA-Z0-9]+","");
		filename = filename.toLowerCase();
		filename = Operator.subString(filename, 0, 200);
		return filename;
    }

    public static String readFile(String path) {
    	try {
    		FileInputStream fis = new FileInputStream(path);

    		FileChannel channel = fis.getChannel();
    		MappedByteBuffer mmb = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

    		byte[] buffer = new byte[(int)channel.size()];
    	    mmb.get(buffer);
    	    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    	    InputStreamReader isr = new InputStreamReader(bais);
    	    BufferedReader in = new BufferedReader(isr);

    	    StringBuilder sb = new StringBuilder();
    	    for (String line = in.readLine(); line != null; line = in.readLine()) {
    	      sb.append(line);
    	    }

    	    in.close();
    	    isr.close();
    	    bais.close();
    	    buffer = new byte[0];
    	    buffer = null;
    	    mmb.clear();
    	    channel.close();
    	    fis.close();
    		return sb.toString();

    	}
    	catch(Exception ex) {
    		Logger.error("*********************************"+ex.getMessage());
    		Logger.error("*********************************"+ex.getCause());
    		return "";
    	}

    }

    public static String getString(String path) {
    	String contents  = "";
    	try {
	    	File file = new File(path);
	    	FileInputStream fis = new FileInputStream(file);
	    	FileChannel fileChannel = fis.getChannel();
	    	int filesize = (int) fileChannel.size();
	    	byte[] data = new byte[filesize];
	
	    	ByteBuffer mappedFile = ByteBuffer.allocate(filesize);
	    	fileChannel.read(mappedFile);
	    	
	    	fileChannel.close();
	    	try { fis.close(); } catch (Exception e) { }
	
	    	mappedFile.position(0);
	    	mappedFile.get(data);
	    	contents = new String(data);
	    	mappedFile.clear();
		}
    	catch (Exception e) {
    		Logger.error(e);
    	}
    	return contents;
    }

    public static void saveString(String str, String fullpath) {
    	try {
    		String dir = getFileDirectory(fullpath);
    		File d = new File(dir);
    		if (!d.exists()) {
    			d.mkdirs();
    		}
    		FileWriter writer = new FileWriter(fullpath);
    		writer.write(str);
    		try { writer.flush(); } catch (Exception e) { }
    		try { writer.close(); } catch (Exception e) { }
    	}
    	catch (Exception e) { Logger.error(e); }
    }

	public static boolean deleteDir(String fullpath) {
		File d = new File(fullpath);
		return deleteDir(d);
	}

	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	    	String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

    public static String getUrlContent(String url){
    	StringBuilder sb = new StringBuilder();
    	try {
    		if(!(Operator.hasValue(url))){
    			return url;
    		}
    		
			java.net.URL u = new java.net.URL(url);
			URLConnection conn = u.openConnection();
 			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
 			br.close();
 			
		}catch(Exception e){
			Logger.error(e);
			sb.append(url);
		}
		return sb.toString();
    }
    
    
   /* public static String getEnoticeContent(String url){
    	try{
    		
    	}
    }*/

    public static boolean rename(String file,String newFile){
    	boolean result = false;
    	
    	try{
	    	File f = new File(file);
	
	        // File (or directory) with new name
	        File f2 = new File(newFile);
	        if(f2.exists()){
	        	result = false;
	        }
	
	        // Rename file (or directory)
	        result = f.renameTo(f2);
	        
    	}catch(Exception e){
    		Logger.error(e.getMessage());
    	}
    	return result;
    	
    }
    
    public static String contentType(String str) {
    	if (str.equalsIgnoreCase("application/postscript"))         {  return ".aiff" ; }// Adobe Illustrator artwork
    		else if (str.equalsIgnoreCase("audio/x-aiff")) 			  { return ".aiff"; } // Mac audio file
    		else if (str.equalsIgnoreCase("audio/basic")) 			  { return ".au"; } //   Mac/Unix audio file
    		else if (str.equalsIgnoreCase("video/x-msvideo")) 		  { return ".avi"; } //  Video file
    		else if (str.equalsIgnoreCase("image/bmp")) 			  { return ".bmp"; } //  Bitmap
    		else if (str.equalsIgnoreCase("application/msword")) 		  { return ".doc"; } // Microsoft Word document
    		else if (str.equalsIgnoreCase("application/msword")) 		  { return ".docx"; } //Microsoft Word document
    		else if (str.equalsIgnoreCase("application/postscript")) 	  { return ".eps"; } // Encapsulated postscript document
    		else if (str.equalsIgnoreCase("image/gif")) 			  { return ".gif"; } //  gif image
    		else if (str.equalsIgnoreCase("text/calendar")) 		  { return ".ics"; } //  iCalendar File
    		else if (str.equalsIgnoreCase("image/jpeg")) 			  { return ".jpeg"; } // jpg image
    		else if (str.equalsIgnoreCase("image/jpeg")) 			  { return ".jpg"; } //  jpg image
    		else if (str.equalsIgnoreCase("video/quicktime")) 		  { return ".mov"; } //  Quicktime movie
    		else if (str.equalsIgnoreCase("audio/mpeg")) 			  { return ".mp3"; } //  mp3 audio
    		else if (str.equalsIgnoreCase("video/mp4")) 			  { return ".mp4"; } //  mpeg movie
    		else if (str.equalsIgnoreCase("video/mpeg")) 			  { return ".mpeg"; } // mpeg movie
    		else if (str.equalsIgnoreCase("video/mpeg")) 			  { return ".mpg"; } //  mpeg movie
    		else if (str.equalsIgnoreCase("application/vnd.ms-project")) 	  { return ".mpp"; } // Microsoft Project Document
    		else if (str.equalsIgnoreCase("application/pdf")) 		  { return ".pdf"; } //  Adobe Acrobat pdf document
    		else if (str.equalsIgnoreCase("image/png")) 			  { return ".png"; } //  png image
    		else if (str.equalsIgnoreCase("application/vnd.ms-powerpoint"))   { return ".ppt"; } // Microsoft Powerpoint presentation
    		else if (str.equalsIgnoreCase("application/vnd.ms-powerpoint"))   { return ".pptx"; } //Microsoft Powerpoint presentation
    		else if (str.equalsIgnoreCase("application/photoshop")) 	  { return ".psd"; } // Adobe Photoshop file
    		else if (str.equalsIgnoreCase("video/quicktime")) 		  { return ".qt"; } //   Quicktime movie
    		else if (str.equalsIgnoreCase("audio/x-pn-realaudio")) 		  { return ".ra"; } //  Real Audio file
    		else if (str.equalsIgnoreCase("audio/x-pn-realaudio")) 		  { return ".ram"; } // Real Audio file
    		else if (str.equalsIgnoreCase("application/x-shockwave-flash"))   { return ".swf"; } // Macromedia flash movie
    		else if (str.equalsIgnoreCase("image/tiff")) 			  { return ".tif"; } //  Tiff image
    		else if (str.equalsIgnoreCase("image/tiff")) 			  { return ".tiff"; } // Tiff image
    		else if (str.equalsIgnoreCase("application/x-font")) 		  { return ".ttf"; } // True type font
    		else if (str.equalsIgnoreCase("text/plain")) 			  { return ".txt"; } //  Text file
    		else if (str.equalsIgnoreCase("text/calendar")) 		  { return ".vcf"; } //  vCard File
    		else if (str.equalsIgnoreCase("text/calendar")) 		  { return ".vcs"; } //  vCalendar File
    		else if (str.equalsIgnoreCase("application/x-visio")) 		  { return ".vdx"; } // Microsoft Visio drawing
    		else if (str.equalsIgnoreCase("application/x-visio")) 		  { return ".vsd"; } // Microsoft Visio drawing
    		else if (str.equalsIgnoreCase("audio/x-wav")) 			  { return ".wav"; } //  Windows wav audio file
    		else if (str.equalsIgnoreCase("application/vnd.ms-works")) 	  { return ".wks"; } // Microsoft Works document
    		else if (str.equalsIgnoreCase("audio/x-ms-wma")) 		  { return ".wma"; } //  Microsoft Windows Audio file
    		else if (str.equalsIgnoreCase("audio/x-ms-wmv")) 		  { return ".wmv"; } //  Microsoft Windows Movie file
    		else if (str.equalsIgnoreCase("application/vnd.ms-excel")) 	  { return ".xls"; } // Microsoft Excel workbook
    		else if (str.equalsIgnoreCase("application/vnd.ms-excel")) 	  { return ".xlsx"; } //Microsoft Excel workbook
    		else if (str.equalsIgnoreCase("text/xml")) 			  { return ".xml"; } // XML file
    		else if (str.equalsIgnoreCase("text/csv")) 			  { return ".csv"; } // CSV Comma Separated Values file
    		return ".txt";

    	}

}
