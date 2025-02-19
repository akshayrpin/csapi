package csapi.impl.attachments;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

















import java.util.ArrayList;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.FileUtils;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import csapi.impl.activity.ActivitySQL;
import csapi.impl.general.GeneralImpl;
import csapi.impl.general.GeneralSQL;
import csapi.utils.CsReflect;
import csapi.utils.CsTools;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.DataVO;
import csshared.vo.FileVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeVO;


public class AttachmentsAgent {

	/*public static void main1(String args[]){
		
		String s = "C:/citysmart/csapi/WebContent/print/base64.txt";
		String targetFile = "C:/citysmart/csapi/WebContent/print/base641";
		
		 
        try {
        	byte[] decodedBytes = Base64.decodeBase64(loadFileAsBytesArray(s));
			writeByteArraysToFile(targetFile, decodedBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static byte[] loadFileAsBytesArray(String fileName) throws Exception {
		 
        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
 
    }
 
    *//**
     * This method writes byte array content into a file.
     *
     * @param fileName
     * @param content
     * @throws IOException
     *//*
    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {
 
        File file = new File(fileName);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
       
        writer.write(content);
        writer.flush();
        writer.close();
 
    }*/
    
    
    public static void main1(String args[]){
        try {
  		
         /*  // Encode using basic encoder
           String base64encodedString = FileUtil.getString("C:/citysmart/csapi/WebContent/print/base64.txt");// Base64.getEncoder().encodeToString("TutorialsPoint?java8".getBytes("utf-8"));
  		
           // Decode
           byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
  		
           base64encodedString = Base64.getUrlEncoder().encodeToString("TutorialsPoint?java8".getBytes("utf-8"));
           System.out.println("Base64 Encoded String (URL) :" + base64encodedString);
  		
           StringBuilder stringBuilder = new StringBuilder();
           stringBuilder.append(new String(base64decodedBytes));
           for (int i = 0; i < 10; ++i) {
              stringBuilder.append(UUID.randomUUID().toString());
           }
  		
           byte[] mimeBytes = stringBuilder.toString().getBytes("utf-8");
           byte[] mimeEncodedString = Base64.getMimeDecoder().decode(base64decodedBytes);*/
        	System.out.println("heel");
        	int d = Operator.toInt("4");
        	int d2 = Operator.toInt("4");
        	int min = Math.min(d, d2);
        	int max = Math.max(d, d2);
        	
        	
        	String start = "";
			String end = "";
			if(min<0){ start = min+""; }else { start = "+"+min; }
			if(max<0){ end = max+""; } else { end = "+"+max; }
			System.out.println(start+"rrrr"+end);
        }catch(Exception e){
        	e.printStackTrace();
        }
     }
    
    
    
    public static boolean saveAttachments(RequestVO r, Token u){
		boolean result= false;
		try {
			int id = Operator.toInt(r.getId());
			
			int mapid=r.typeid;
			DataVO d = DataVO.toDataVO(r);
			int size = 0;
			String title = d.get("TITLE");
			String path = d.get("PATH");
			int attachtype = Operator.toInt(d.get("LKUP_ATTACHMENTS_TYPE_ID"));
			String description = d.get("DESCRIPTION");
			String ispublic = d.get("ISPUBLIC");
			String sensitive = d.get("SENSITIVE");
			String command ="";
			
			if (id > 0) {
				command = AttachmentsSQL.updateAttachment(id,mapid,r.getType(),"ATTACHMENTS", title, path, attachtype, description, size, Operator.equalsIgnoreCase(ispublic, "Y"), Operator.equalsIgnoreCase(sensitive, "Y"), u.getId(), u.getIp());
				Sage db = new Sage();
				result = db.update(command);
				db.clear();
				CsReflect.addHistory(r.getType(), r.getTypeid(), "attachments", id, "update");
			}
			else {
				Timekeeper now = new Timekeeper();
				ArrayList<String> a = processAttachment(path);
				Sage db = new Sage();
					for(String f: a){
						command = AttachmentsSQL.insertAttachments(title,mapid, r.getType() ,"ATTACHMENTS",f, attachtype, description, size, Operator.equalsIgnoreCase(ispublic, "Y"), Operator.equalsIgnoreCase(sensitive, "Y"), u.getId(), u.getIp(), now);
						//result = db.update(command);
							//if (result) {
							//	command = AttachmentsSQL.getAttachment(title, path, u.getId(), now);
								if (db.query(command) && db.next()) {
									int attachid = db.getInt("ID");
									command = GeneralSQL.insertRef(r.getType(), "ATTACHMENTS", r.getTypeid(), attachid, u.getId());
									result = db.update(command);
									CsReflect.addHistory(r.getType(), r.getTypeid(), "attachments", attachid, "add");
								}
							//}
					}
				db.clear();
			}
			
			
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		return result;
	}



    
    public static ArrayList<String> processAttachment(String path){
    	ArrayList<String> a = new ArrayList<String>();

		if(Operator.hasValue(path)){
			try {
				
				File f = new File(Config.getString("files.storage_path")+path);

				File folder = f.getParentFile();
				File[] listOfFiles = folder.listFiles();
				String s = Operator.replace(f.getParent()+"/", "\\", "/");
				s = Operator.replace(s, Config.getString("files.storage_path"), "");
				
				    for (int i = 0; i < listOfFiles.length; i++) {
				      if (listOfFiles[i].isFile()) {
				        
				        if (Operator.hasValue(listOfFiles[i].getName())) {
				        	a.add(s+listOfFiles[i].getName()); 
						}
				      } else if (listOfFiles[i].isDirectory()) {
				      }
				    }
		
			}catch(Exception e){
				Logger.error(e.getMessage());
				
			}
		}
		return a;
		
	}
    
    
    public static ResponseVO getAttachmentById(RequestVO vo){
    	ResponseVO  v = new ResponseVO();
    	Sage db = new Sage();
    	
    	try {
    		String command = "";
    		if(!Operator.hasValue(vo.getSubrequest())) {
    			command = AttachmentsSQL.getAttachmentById(Operator.toInt(vo.getId()));
    		}
    		else if(Operator.equalsIgnoreCase(vo.getSubrequest(), "batch")) {
    			command = AttachmentsSQL.getAttachmentforBatch(Operator.toInt(vo.getId()));
    		}
    		else if(Operator.equalsIgnoreCase(vo.getSubrequest(), "online")) {
    			command = AttachmentsSQL.getAttachmentforOnline(Operator.toInt(vo.getId()));
    		}
    		if (Operator.hasValue(command)) {
    			if (db.query(command) && db.next()) {
    				String path = db.getString("PATH");
    				StringBuilder sb = new StringBuilder();
    				sb.append(Config.getString("files.storage_path")).append("/").append(db.getString("PATH"));
    				String fullpath = sb.toString();
    	    		File f = new File(fullpath);
    				if (f.exists() ) {
    				    String ext =  Operator.getExt(f);
    				    String ct = Operator.contentType(ext);
    				    FileVO fvo = new FileVO();
    				    fvo.setId(db.getInt("ID"));
    				    fvo.setExtension(ext);
    				    fvo.setFilename(db.getString("TITLE"));
    				    fvo.setPath(db.getString("PATH"));
    					fvo.setFullpath(fullpath);
    					fvo.setIspublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
    					Logger.info("ISPUBLIC **********************"+fvo.isIspublic());
    				    fvo.setContenttype(ct);
    					if(ct.startsWith("application/pdf") || ct.startsWith("image") ){
    						fvo.setShowbrowser(true);
    					}
    					if(path.endsWith(".msg")){
    						fvo.setContenttype("application/vnd.ms-outlook");
    					}
    					v.setFile(fvo);
    					v.setMessagecode("cs200");
    				}
    			}
    		}
    	}
    	catch (Exception e){
    		Logger.error(e);
    		v.setMessagecode("cs500");
    		v.setValid(false);
    	}
    		
    	db.clear();
    	return v;
    }

	public static FileVO getAttachment(RequestVO vo) {
		return getAttachment(Operator.toInt(vo.getId()));
	}
	
	public static FileVO getAttachment(int id) {
		FileVO fvo = new FileVO();
		Sage db = new Sage();
		try {
			db.query(AttachmentsSQL.getAttachmentById(id));
			if(db.next()) {
				String path = db.getString("PATH");
				StringBuilder sb = new StringBuilder();
				sb.append(Config.getString("files.storage_path")).append("/").append(db.getString("PATH"));
				String fullpath = sb.toString();
				File f = new File(fullpath);
				if (f.exists()) {
					String ext =  Operator.getExt(f);
					String ct = Operator.contentType(ext);
					fvo.setExtension(ext);
					fvo.setFilename(db.getString("TITLE"));
					fvo.setPath(db.getString("PATH"));
					fvo.setFullpath(fullpath);
					fvo.setIspublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
					fvo.setContenttype(ct);
					if(ct.startsWith("application/pdf") || ct.startsWith("image") ) {
						fvo.setShowbrowser(true);
					}
					if(path.endsWith(".msg")){
						fvo.setContenttype("application/vnd.ms-outlook");
					}
				}
			}
		}
		catch (Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return fvo;
    }
	
	
	public static  ByteArrayOutputStream mergeDocuments(String ids) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		try {
		    //Prepare input pdf file list as list of input stream.
		    List<InputStream> inputPdfList = new ArrayList<InputStream>();
			    if(Operator.hasValue(ids)){
				    Sage db = new Sage();
				    
				    db.query("select * from attachments where ID in ("+Operator.sqlEscape(ids)+")");
				    while(db.next()){
				    	inputPdfList.add(new FileInputStream(Config.getString("files.storage_path")+db.getString("PATH")));
				    }
				    db.clear();
				    //Prepare output stream for merged pdf file.
				     o =  mergePdfFiles(inputPdfList, o);  
			     
			    }
		   } catch (Exception e) {
			e.printStackTrace();
		  }
		return o;
	}
	
	
	static ByteArrayOutputStream mergePdfFiles(List<InputStream> inputPdfList, ByteArrayOutputStream outputStream) throws Exception{
 
        //Create document and pdfReader objects.
	Document document = new Document();
        List<PdfReader> readers = 
        		new ArrayList<PdfReader>();
        int totalPages = 0;
 
        //Create pdf Iterator object using inputPdfList.
        Iterator<InputStream> pdfIterator = 
        		inputPdfList.iterator();
 
        // Create reader list for the input pdf files.
        while (pdfIterator.hasNext()) {
                InputStream pdf = pdfIterator.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages = totalPages + pdfReader.getNumberOfPages();
        }
 
        // Create writer for the outputStream
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
 
        //Open document.
        document.open();
 
        //Contain the pdf data.
        PdfContentByte pageContentByte = writer.getDirectContent();
 
        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        Iterator<PdfReader> iteratorPDFReader = readers.iterator();
 
        // Iterate and process the reader list.
        while (iteratorPDFReader.hasNext()) {
          PdfReader pdfReader = iteratorPDFReader.next();
          //Create page and add content.
          while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                      document.newPage();
                      pdfImportedPage = writer.getImportedPage(
                    		  pdfReader,currentPdfReaderPage);
                      pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                      currentPdfReaderPage++;
          }
          currentPdfReaderPage = 1;
        }
 
        //Close document and outputStream.
        outputStream.flush();
        document.close();
        outputStream.close();
        System.out.println("Pdf files merged successfully.");
        return outputStream;
        
	}

	
	
	//zip file
	
	public static void main(String args[]) { 

		try { 
		// let's create a ZIP file to write data 
			FileOutputStream fos = new FileOutputStream("C:/DL/csfiles/tokens/sample.zip"); 
			ZipOutputStream zipOS = new ZipOutputStream(fos); 
			String file1 = "C:/DL/csfiles/tokens/472e01f12c3e6f055cbd05eefc97e4e4.json"; 
			String file2 = "C:/DL/csfiles/tokens/5fb6034455a897bb21564d9ec0e56859.json"; 
		
	
			writeToZipFile(file1, zipOS); 
			writeToZipFile(file2, zipOS); 
			
			zipOS.close(); 
			fos.close(); 
		}  catch (IOException e) { 
			Logger.error(e.getMessage());
		} 
	} 


		public static void writeToZipFile(String path, ZipOutputStream zipStream) throws FileNotFoundException, IOException {
			System.out.println("Writing file : '" + path + "' to zip file");
			try{
				File aFile = new File(path); 
				FileInputStream fis = new FileInputStream(aFile); 
				ZipEntry zipEntry = new ZipEntry(path); 
				zipStream.putNextEntry(zipEntry); 
				
				byte[] bytes = new byte[1024]; 
				int length; 
				while ((length = fis.read(bytes)) >= 0) { 
					zipStream.write(bytes, 0, length);
				} 
				zipStream.closeEntry(); 
				fis.close(); 
			}
			catch(Exception e){
				Logger.error(e.getMessage());
			}
		} 
		


}















