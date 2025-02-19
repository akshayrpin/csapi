package csapi.utils;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Numeral;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFileSpecification;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import csshared.vo.FileVO;
import csshared.vo.RequestVO;
//sunil
public class PrintPDF {

	
	 public static ByteArrayOutputStream print(String html) {
			String r = "";
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			try {
				o = new PrintPDF().htmltoPdf(html, o);
				o.close();
			}
			catch (Exception e) {}
			return o;
		}
	
	 public ByteArrayOutputStream htmltoPdf(String html, ByteArrayOutputStream pdfstream) {
		 return htmltoPdf(html, pdfstream,false);
	 }
	 
	  //TODO separate 
	 public ByteArrayOutputStream htmltoPdf(String html, ByteArrayOutputStream pdfstream, boolean watermark) {
		 try {
		    
			//byte[] b = new byte[16384];
		    Document document = new Document();
		    PdfWriter writer = PdfWriter.getInstance(document, pdfstream);
		    document.addAuthor("City Smart");
		    document.setPageSize(PageSize.LETTER);
		    document.open();
		    InputStream is = new ByteArrayInputStream(html.getBytes());
		    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
		    
		 /*   String qrUrl = "http://dev.beverlyhills.org/cs/?entity=lso&type=activity&reference=CS0833881";
		   // html = Operator.replace(html, "", with)
		    
		    BarcodeQRCode qrcode = new BarcodeQRCode(qrUrl.trim(), 1, 1, null);
			Image qrcodeImage = qrcode.getImage();
			qrcodeImage.setAbsolutePosition(180f,200f);
			qrcodeImage.scalePercent(500);
			document.add(qrcodeImage);*/
			
			document.close();
		   
		   
			if(watermark){
		  
			    is = new ByteArrayInputStream(pdfstream.toByteArray());
			    PdfReader pdfReader = new PdfReader(is);
				   
				Logger.info(pdfReader.getNumberOfPages()+"############");
			    PdfStamper pdfStamper =new PdfStamper(pdfReader,pdfstream);
				 //  pdfStamper.addSignature("sunilllllll", 2, 100f, 500f, 400f, 50f);
			    
			    String url = Config.rooturl()+Config.contexturl();
			    Logger.info(url+"/images/shield_300x300.png");
			     Image bg = Image.getInstance(url+"/images/shield_300x300.png");
				 Image image = Image.getInstance("http://localhost:8080/cs/images/watermark/hold.png");
				 for(int i=1; i<= pdfReader.getNumberOfPages(); i++){
					 PdfContentByte content = pdfStamper.getUnderContent(i);
					 image.setAbsolutePosition(100f, 400f);
		             content.addImage(image);
		             bg.setAbsolutePosition(150f, 250f);
		             content.addImage(bg);
		          }
	
				
				 
				 
				 pdfStamper.close();
			}
			
			
			
		    pdfstream.close();
		    return pdfstream;
		   
		} 
		 catch (Exception e) {
		    e.printStackTrace();
			 Logger.error(e.getMessage());
		    return pdfstream;
		}
	 

	}
	 
	 
	
	 
	 
	 //translate
	 public ByteArrayOutputStream htmltoPdf(HashMap<String,String> t, JSONArray a, ByteArrayOutputStream pdfstreamnew) {
		 ArrayList<ByteArrayOutputStream> list = new ArrayList<ByteArrayOutputStream>();
		 
		 try{
			 String html = t.get("TEMPLATE");
			 try {
				 for(int i=0;i<a.length();i++){
					 try {
						 JSONObject  g = a.getJSONObject(i);
						 ByteArrayOutputStream pdfstream  = htmltoPdfNew(t,g);
						 list.add(pdfstream);
					 }
					 catch (Exception e) { }
					 
				 }	 
			 }
			 catch (Exception ae) {
				 
			 }
			 ArrayList<InputStream> listip = new ArrayList<InputStream>();

			 
			 for(ByteArrayOutputStream o: list){
				 InputStream is = new ByteArrayInputStream(o.toByteArray()); 
				 listip.add(is);
			 }
			 
			// Rectangle r = new Rectangle(288,144);
			 Document document = new Document();
		
			 document.addAuthor("City Smart");
			 boolean landscape = Operator.equalsIgnoreCase(t.get("LANDSCAPE"), "Y");
			
			 Rectangle r = new Rectangle(0,0);
			 boolean margins = false;
			 float left = 0;
		     float right = 0;
		     float top = 0;
		     float bottom = 0;
			 if(Operator.hasValue(t.get("MARGIN_LEFT")) && Operator.hasValue(t.get("MARGIN_RIGHT")) && Operator.hasValue(t.get("MARGIN_TOP")) && Operator.hasValue(t.get("MARGIN_BOTTOM"))){
				 left = Float.parseFloat(t.get("MARGIN_LEFT"));
			     right = Float.parseFloat(t.get("MARGIN_RIGHT"));
			     top = Float.parseFloat(t.get("MARGIN_TOP"));
			     bottom = Float.parseFloat(t.get("MARGIN_BOTTOM"));
			     margins = true;
			 }
			 
			boolean pageset = false;
			 if(Operator.toInt(t.get("PAGE_WIDTH"))>0 && Operator.toInt(t.get("PAGE_HEIGHT"))>0 ){
				 float w = Float.parseFloat(t.get("PAGE_WIDTH"));
				 float h = Float.parseFloat(t.get("PAGE_HEIGHT"));
				 
				 //1 in = 2.54 cm = 72 
				 //8.27 × 11.69 inches
				 
				 double dw = Numeral.multiplyDouble(w, 72);
				 double dh = Numeral.multiplyDouble(h, 72);
				 
				 float fw = (float)dw;
				 float fh = (float)dh;
				 
				 Logger.info(fw+"fw");
				 Logger.info(fh+"fh");
				 
				 r = new Rectangle(fw,fh);
				 pageset = true;
				 
				
				
			 }
			 if(margins){
				 document = new Document(r,left,right,top,bottom);	 
				
			 }else if(pageset){
				 document.setPageSize(r);	 
			 }else  if(landscape){
				 document.setPageSize(PageSize.LETTER.rotate());
			 }else {
				 document.setPageSize(PageSize.LETTER);
			 }		 
			 
		     
			 PdfWriter writer = PdfWriter.getInstance(document, pdfstreamnew);
			 
		     document.open();
		    
		     PdfContentByte cb = writer.getDirectContent();
		        
		        for (InputStream in : listip) {
		            PdfReader reader = new PdfReader(in);
		            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
		                document.newPage();
		                Logger.info("SETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
		               
		                PdfImportedPage page = writer.getImportedPage(reader, i);
		                cb.addTemplate(page, 0, 0);
		            }
		        }
		        
		      pdfstreamnew.flush();
		      document.close();
		       // outputStream.close();
			 
			 
			 
			// System.out.println("size: " + pdfstreamnew.size());
			 pdfstreamnew.close();
		 
		 }catch (Exception e) {
			    e.printStackTrace();
				Logger.error(e.getMessage());
			  
			}
		 
		 return pdfstreamnew;
	 }
	 
	 
	 public ByteArrayOutputStream htmltoPdfNew(HashMap<String,String> t, JSONObject g) {
		 ByteArrayOutputStream pdfstream = new ByteArrayOutputStream();
		 try {
			
			 
			 boolean margins = false;
			 float left = 0;
		     float right = 0;
		     float top = 0;
		     float bottom = 0;
			 if(Operator.hasValue(t.get("MARGIN_LEFT")) && Operator.hasValue(t.get("MARGIN_RIGHT")) && Operator.hasValue(t.get("MARGIN_TOP")) && Operator.hasValue(t.get("MARGIN_BOTTOM"))){
				 left = Float.parseFloat(t.get("MARGIN_LEFT"));
			     right = Float.parseFloat(t.get("MARGIN_RIGHT"));
			     top = Float.parseFloat(t.get("MARGIN_TOP"));
			     bottom = Float.parseFloat(t.get("MARGIN_BOTTOM"));
			     margins = true;
			 }
			 
			 Rectangle r = new Rectangle(0,0);
			 float fw = 0;
			float fh = 0;
			boolean pageset = false;
				 if(Operator.toInt(t.get("PAGE_WIDTH"))>0 && Operator.toInt(t.get("PAGE_HEIGHT"))>0 ){
					 float w = Float.parseFloat(t.get("PAGE_WIDTH"));
					 float h = Float.parseFloat(t.get("PAGE_HEIGHT"));
					 
					 double dw = Numeral.multiplyDouble(w, 72);
					 double dh = Numeral.multiplyDouble(h, 72);
					 
					 fw = (float)dw;
					 fh = (float)dh;
					
					pageset = true;
					 //document = new Document(r,15,40,15,0);
					 
					 
					/*float left = 15;
			        float right = 40;
			        float top = 15;
			        float bottom = 0;*/
					// document.setMargins(left, right, top, bottom);
					 //Rectangle r = new Rectangle(left, right,top,bottom);
					 //document.setPageSize(r);*/
				 }
		
		 
		    Document document = new Document();
			   if(margins){
				 	r = new Rectangle(288,144);
					document = new Document(r,left,right,top,bottom);
			  }else  if(pageset){
				  r = new Rectangle(fw,fh);
				  document.setPageSize(r);	  
			  }else {
				  document.setPageSize(PageSize.LETTER);
			  }
		
			PdfWriter writer = PdfWriter.getInstance(document, pdfstream);
			String html = t.get("TEMPLATE");	
			String transformed = parseHtmlSingle(html, g);
			document.addAuthor("City Smart");
			
			
			
			
			document.open();
			InputStream is = new ByteArrayInputStream(transformed.getBytes());
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			document.close();
				
				if(true){
				    is = new ByteArrayInputStream(pdfstream.toByteArray());
				    PdfReader pdfReader = new PdfReader(is);
				    PdfStamper pdfStamper =new PdfStamper(pdfReader,pdfstream);
				    String fileurl = CsApiConfig.getString("cs.fullcontexturl") +"/"+Config.getString("files.storage_url");
				    //String url = Config.rooturl()+Config.contexturl();
				    String url = CsApiConfig.getString("public.fullcontexturl");
				    Logger.info(url+"------------------------------");
				    url = Operator.replace(url, "https", "http");
				    fileurl = Operator.replace(fileurl, "https", "http");
				    Image bg = Image.getInstance(url+"/images/shield_300x300.png");
				    
				    String watermarkpath =url+"/images/shield_300x300.png";
				    boolean watermark = false;
				    if(g.has("watermark_path")){
		            	 if(Operator.hasValue(g.getString("watermark_path"))){
		            		 watermarkpath = fileurl +g.getString("watermark_path");
		            		 watermark= true;
		            	 }
		             }
				    
				    if(g.has("hold_watermark_path")){
		            	 if(Operator.hasValue(g.getString("hold_watermark_path"))){
		            		 watermarkpath = fileurl + g.getString("hold_watermark_path");
		            		 watermark= true;
		            	 }
		             }
				    Image image = Image.getInstance(watermarkpath);
				    
				    int totalpg = pdfReader.getNumberOfPages();
  			      	BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
  			        
					for(int j=1; j<= pdfReader.getNumberOfPages(); j++){
						 PdfContentByte content = pdfStamper.getUnderContent(j);
						 if(watermark){
							 image.setAbsolutePosition(150f, 250f);
							 content.addImage(image);
						 }	 
			             bg.setAbsolutePosition(150f, 250f);
			             content.addImage(bg);
			             
			             
			             
			            //page number 
			             String pnumber = t.get("PAGE_NUMBERS");
			             if(Operator.equalsIgnoreCase(pnumber, "Y")){
				             String pageXofY = String.format("Page %d of %d", j, totalpg); 
				             content.beginText();
				             content.setFontAndSize(bf, 14);
				             content.setTextMatrix(500, 30);
				             content.showText(pageXofY);
				             content.endText();
			             }
			             
			            
			             if(g.has("watermark_text")){
			            	
			            	 if(Operator.hasValue(g.getString("watermark_text"))){
			            		 /*FontSelector selector = new FontSelector();
			            		 
			            		
			            		 selector.addFont(f1);*/
			            		 Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 45, Font.BOLD);
			            		 f1.setColor(BaseColor.RED);
			            		 
			            		 BaseFont wf = f1.getBaseFont();
			      			     content.beginText();
			      			     content.setCMYKColorFill(14, 50, 94, 61);
			            		 content.setFontAndSize(wf, 45);
			            		 content.showTextAligned(com.itextpdf.text.Element.ALIGN_LEFT, g.getString("watermark_text"), 230, 430, 45);
			            		 content.endText();
			            	 }
			             }
			             
			             if(g.has("hold_watermark_text")){
				            	
			            	 if(Operator.hasValue(g.getString("hold_watermark_text"))){
			            		 /*FontSelector selector = new FontSelector();
			            		 
			            		
			            		 selector.addFont(f1);*/
			            		 Font f1 = FontFactory.getFont(FontFactory.HELVETICA, 45, Font.BOLD);
			            		 f1.setColor(BaseColor.RED);
			            		 
			            		 BaseFont wf = f1.getBaseFont();
			      			     content.beginText();
			      			     content.setCMYKColorFill(4, 97, 77, 2);
			            		 content.setFontAndSize(wf, 45);
			            		 content.showTextAligned(com.itextpdf.text.Element.ALIGN_LEFT, g.getString("hold_watermark_text"), 230, 430, 45);
			            		 content.endText();
			            	 }
			             }
			            
			             
			             
			             
			         }
					
					/*//to do attach
					Logger.info("add attachment");
					 PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(
							 pdfStamper.getWriter(), "C:\\test122.pdf", "test122.pdf", null,0);
					 pdfStamper.addFileAttachment("some test file", fs);
					 Logger.info("end attachment");
					 //stop
*/					
					 pdfStamper.close();
				
				}
			 document.close();
		     pdfstream.close();
			 
			
		   
		} 
		 catch (Exception e) {
			 e.printStackTrace();
		    Logger.error(e.getMessage());
		   
		}
	 
		return pdfstream;
	}
	 
	 public ByteArrayOutputStream htmltoPdflistway(String html, JSONArray a, ByteArrayOutputStream pdfstream) {
		 try {
		    Logger.info("SUUUUUUUUUUUUUUUUU"+a.length());
		    
		    ArrayList<String> l = parseHtmlMultiple(html, a);
		    
		    Document document = new Document();
		    PdfWriter writer = PdfWriter.getInstance(document, pdfstream);
			for(String s: l){
				
			    
			    document.addAuthor("City Smart");
			    document.setPageSize(PageSize.LETTER);
			    document.open();
			    InputStream is = new ByteArrayInputStream(s.getBytes());
			    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			
				
				
				/*if(true){
				    is = new ByteArrayInputStream(pdfstream.toByteArray());
				    PdfReader pdfReader = new PdfReader(is);
				    PdfStamper pdfStamper =new PdfStamper(pdfReader,pdfstream);
				    String url = Config.rooturl()+Config.contexturl();
				    Image bg = Image.getInstance(url+"/images/shield_300x300.png");
					// Image image = Image.getInstance("http://localhost:8080/cs/images/watermark/hold.png");
					for(int j=1; j<= pdfReader.getNumberOfPages(); j++){
						 PdfContentByte content = pdfStamper.getUnderContent(j);
						 image.setAbsolutePosition(100f, 400f);
			             content.addImage(image);
			             bg.setAbsolutePosition(150f, 250f);
			             content.addImage(bg);
			         }
					 pdfStamper.close();
				
				}*/
				
				
				
			  
		    
			}
			
			document.close();
			pdfstream.close();
		   
		} 
		 catch (Exception e) {
		    Logger.error(e.getMessage());
		   
		}
	 
		return pdfstream;
	}
	 
	 public ByteArrayOutputStream htmltoPdfMulti(String html, JSONArray a, ByteArrayOutputStream pdfstream) {
		 try {
		    Logger.info("SUUUUUUUUUUUUUUUUU"+a.length());
		    Document document = new Document();
			for(int i=0;i<a.length();i++){
				JSONObject  g = a.getJSONObject(i);
				
				String transformed = parseHtmlSingle(html, g);
			  
			    PdfWriter writer = PdfWriter.getInstance(document, pdfstream);
			    document.addAuthor("City Smart");
			    document.setPageSize(PageSize.LETTER);
			    document.open();
			    InputStream is = new ByteArrayInputStream(transformed.getBytes());
			    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			
				
				
				/*if(true){
				    is = new ByteArrayInputStream(pdfstream.toByteArray());
				    PdfReader pdfReader = new PdfReader(is);
				    PdfStamper pdfStamper =new PdfStamper(pdfReader,pdfstream);
				    String url = Config.rooturl()+Config.contexturl();
				    Image bg = Image.getInstance(url+"/images/shield_300x300.png");
					// Image image = Image.getInstance("http://localhost:8080/cs/images/watermark/hold.png");
					for(int j=1; j<= pdfReader.getNumberOfPages(); j++){
						 PdfContentByte content = pdfStamper.getUnderContent(j);
						 image.setAbsolutePosition(100f, 400f);
			             content.addImage(image);
			             bg.setAbsolutePosition(150f, 250f);
			             content.addImage(bg);
			         }
					 pdfStamper.close();
				
				}*/
				
				
				
			  
		    
			}
			
			document.close();
			pdfstream.close();
		   
		} 
		 catch (Exception e) {
		    Logger.error(e.getMessage());
		   
		}
	 
		return pdfstream;
	}
	
	 
	 public static String parseHtmlSingle(String html,JSONObject g) {
		 try {
			
				Timekeeper k = new Timekeeper();
				try { g.put("special_current_date", k.getString("MM/DD/YYYY")); } catch (Exception ex) { }
				html = Operator.replace(html, "{", "<g>");
				html = Operator.replace(html, "}", "</g>");
				
				
				org.jsoup.nodes.Document doc = Jsoup.parse(html);
				
				
				org.jsoup.select.Elements es = doc.getElementsByTag("g");
				for(Element  e: es){
					try {
						if(g.has(e.text())){
							e.before(g.getString(e.text()));
							e.remove();
						}
					}
					catch (Exception ex) {
						
					}
				}
				
				
					es = doc.getElementsByTag("img");
				for(Element  e: es){
					if(Operator.equalsIgnoreCase("qrcode", e.id())){
						try {
							String url = g.getString("qr_code");
							StringBuilder sb = new StringBuilder();
							sb.append(url);
							if (url.indexOf("?") > -1) {
								sb.append("&");
							}
							else {
								sb.append("?");
							}
							sb.append("end=y");
							url = Operator.urlFriendly(sb.toString());
							String generate = CsApiConfig.getString("qrcode.generate");
							Logger.info(generate+"**************************generate$$$$$$$$$$$$$$$$$$$$");
							if(!Operator.hasValue(generate)){
								generate = "http://beverlyhills.org/alain?qrcode=";
							}
							e.attr("src", generate+url);
						}
						catch (Exception ex) { }
					}
				}
				
			//	JSONArray sp = new JSONArray();
				org.jsoup.select.Elements les = doc.getElementsByAttribute("rowtype");
				//int i = 0; 
				for(Element e : les){
					//Logger.info(e.html());
					String attr = e.attr("rowtype");
					/*boolean regular = true;
				
					
					String childattr = e.attr("child");
					if(Operator.hasValue(childattr)){
						regular = false;
						
						
					}
					String parentattr = e.attr("parent");
					Logger.info(childattr+"childattr"+paentattr);*/
					
					boolean has = false;
					try { has = g.has(attr); } catch (Exception ex) { }
					if(has){
						try {
							Logger.info("attr::::"+attr);
							String replacedList = getReplacedList(e.html(),g.getJSONArray(attr));
							
							
							Element p = e.parent();
							e.before(replacedList);
							e.remove();
							
							if(p.parent()!=null){
								p.parent().after("<p>&nbsp</p>");
							}
						} catch (Exception ex) { }
					}else {
						Logger.info("NO LIST"+attr);
						String nl = replaceEmpty(e.html());
						Element p = e.parent();
						e.before(nl);
						e.remove();
						
						if(p.parent()!=null){
							p.parent().after("<p>&nbsp</p>");
						}
					}
					//i++;
				}
				
			
				html = doc.html();
				
				Logger.info(html);
				
						
						
					
				
		 } catch (Exception e) {
			Logger.error(e.getMessage());
			return html= "";
		 }
		return html;

	} 
	 
	 
	 
	 public static ArrayList<String> parseHtmlMultiple(String html,JSONArray l) {
		 ArrayList<String> s = new ArrayList<String>();
		 try {
			 for(int i=0;i<l.length();i++){
				 s.add(parseHtmlSingle(html, l.getJSONObject(i)));
			 }
				
		 } catch (Exception e) {
			Logger.error(e.getMessage());
		 }
		return s;

	} 
	 
	
	 

	 public static String getReplacedList(String listhtml,JSONArray list){
		 	StringBuilder o = new StringBuilder();
			try{
				Logger.info(list.length()+"list.length()::"+listhtml);
				DecimalFormat fa = new DecimalFormat("#,##0.00"); 
				if(list.length()>0){
					for(int i=0;i<list.length();i++){
						o.append("<tr>");		
						JSONObject p = list.getJSONObject(i);
						String s = listhtml;
						 //Logger.info(s);
						@SuppressWarnings("unchecked")
						Iterator<String> key = p.keys();
				        while (key.hasNext()) {
					        String k = key.next().toString();
					        String rk = "["+k+"]";
					        String v = Operator.toString(p.get(k)); 
					      // System.out.println("Key : " + k + ", value : " + p.getString(k) +" repl:"+rk);
					        
					        /*if(k.indexOf("activity_fee")>0 || k.indexOf("review_fee")>0 || k.indexOf("paid")>0 || k.indexOf("balance")>0){
					        	double d = Operator.toDouble(v);
					        	v = "$"+fa.format(d)+"";
					        }*/
					        s = Operator.replace(s, rk, v);
				        }
				       // Logger.info(s);
				        o.append(replaceEmpty(s));
				     	o.append("</tr>");
					}
				}
				
				//extra spacing to see if required or not
			/*	if(o.toString().equals(listhtml)){
					o.append("<p>&nbsp;</p>");
				}*/
				
				
				
			}catch (Exception e){
				Logger.error(e.getMessage());
			}
			return o.toString();
		}
	 
	 
	 
		public static String replaceEmpty(String html){
			String s = html;
			try{
				//Logger.info("Before empty replace::"+s);
				Pattern p = Pattern.compile("\\[(.*?)\\]");
				Matcher m = p.matcher(s);
				while (m.find()) {
					String g =  m.group();
					html = Operator.replace(html, g, "");
					
				}
				//Logger.info("After empty replace::"+html);
			}catch (Exception e){
				Logger.error(e.getMessage());
			}
			return html;
		}	
		
		
	

		
		 //file translate
		
		 public ByteArrayOutputStream designtoPdfNew(HashMap<String,String> t, JSONObject g) {
			 ByteArrayOutputStream o = new ByteArrayOutputStream();
			 try {
				   Timekeeper k = new Timekeeper();
				   Logger.info(t.get("FILE_DESIGN")+"template file"); 
				   String s = Config.getString("files.storage_path")+t.get("FILE_DESIGN");
				   
				   String s1 = Config.getString("files.temp_path")+Operator.randomString()+".pdf";
				   
				   PdfReader reader = new PdfReader(s);
				    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(s1));
				    AcroFields form = stamper.getAcroFields();
		    		String patternRegex = "(?i)<br */?>";				    
				    Set<String> fields = form.getFields().keySet();
				    for (String key : fields) {
				    	
				    	String val = "";
				    	if(Operator.hasValue(g.getString(key))){
				    		val = Operator.replace(g.getString(key), "null", "");
				    		val = val.replaceAll(patternRegex, "");


				    	}
				    	Logger.info(key+"33"+val);
				    	form.setField(key, val.toUpperCase());
				    }	
				    
				    stamper.setFormFlattening(true);
				    stamper.close();
				    reader.close();
				
				
				    byte[] b = new byte[0];
				    FileVO fvo = new FileVO();
					
					
					String filepath = s1;
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
				 e.printStackTrace();
			    Logger.error(e.getMessage());
			   
			}
		 
			return o;
		}
		
		 public ByteArrayOutputStream filetoPdf(HashMap<String,String> t, JSONArray a, ByteArrayOutputStream pdfstreamnew) {
			 ArrayList<ByteArrayOutputStream> list = new ArrayList<ByteArrayOutputStream>();
			 
			 try{
				 String html = t.get("TEMPLATE");
				 try {
					 for(int i=0;i<a.length();i++){
						 try {
							 JSONObject  g = a.getJSONObject(i);
							 ByteArrayOutputStream pdfstream  = htmltoPdfNew(t,g);
							 list.add(pdfstream);
						 }
						 catch (Exception e) { }
						 
					 }	 
				 }
				 catch (Exception ae) {
					 
				 }
				 ArrayList<InputStream> listip = new ArrayList<InputStream>();

				 
				 for(ByteArrayOutputStream o: list){
					 InputStream is = new ByteArrayInputStream(o.toByteArray()); 
					 listip.add(is);
				 }
				 
				// Rectangle r = new Rectangle(288,144);
				 Document document = new Document();
			
				 document.addAuthor("City Smart");
				 boolean landscape = Operator.equalsIgnoreCase(t.get("LANDSCAPE"), "Y");
				
				 Rectangle r = new Rectangle(0,0);
				 boolean margins = false;
				 float left = 0;
			     float right = 0;
			     float top = 0;
			     float bottom = 0;
				 if(Operator.hasValue(t.get("MARGIN_LEFT")) && Operator.hasValue(t.get("MARGIN_RIGHT")) && Operator.hasValue(t.get("MARGIN_TOP")) && Operator.hasValue(t.get("MARGIN_BOTTOM"))){
					 left = Float.parseFloat(t.get("MARGIN_LEFT"));
				     right = Float.parseFloat(t.get("MARGIN_RIGHT"));
				     top = Float.parseFloat(t.get("MARGIN_TOP"));
				     bottom = Float.parseFloat(t.get("MARGIN_BOTTOM"));
				     margins = true;
				 }
				 
				boolean pageset = false;
				 if(Operator.toInt(t.get("PAGE_WIDTH"))>0 && Operator.toInt(t.get("PAGE_HEIGHT"))>0 ){
					 float w = Float.parseFloat(t.get("PAGE_WIDTH"));
					 float h = Float.parseFloat(t.get("PAGE_HEIGHT"));
					 
					 //1 in = 2.54 cm = 72 
					 //8.27 × 11.69 inches
					 
					 double dw = Numeral.multiplyDouble(w, 72);
					 double dh = Numeral.multiplyDouble(h, 72);
					 
					 float fw = (float)dw;
					 float fh = (float)dh;
					 
					 Logger.info(fw+"fw");
					 Logger.info(fh+"fh");
					 
					 r = new Rectangle(fw,fh);
					 pageset = true;
					 
					
					
				 }
				 if(margins){
					 document = new Document(r,left,right,top,bottom);	 
					
				 }else if(pageset){
					 document.setPageSize(r);	 
				 }else  if(landscape){
					 document.setPageSize(PageSize.LETTER.rotate());
				 }else {
					 document.setPageSize(PageSize.LETTER);
				 }		 
				 
			     
				 PdfWriter writer = PdfWriter.getInstance(document, pdfstreamnew);
				 
			     document.open();
			    
			     PdfContentByte cb = writer.getDirectContent();
			        
			        for (InputStream in : listip) {
			            PdfReader reader = new PdfReader(in);
			            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			                document.newPage();
			                Logger.info("SETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
			               
			                PdfImportedPage page = writer.getImportedPage(reader, i);
			                cb.addTemplate(page, 0, 0);
			            }
			        }
			        
			      pdfstreamnew.flush();
			      document.close();
			       // outputStream.close();
				 
				 
				 
				// System.out.println("size: " + pdfstreamnew.size());
				 pdfstreamnew.close();
			 
			 }catch (Exception e) {
				    e.printStackTrace();
					Logger.error(e.getMessage());
				  
				}
			 
			 return pdfstreamnew;
		 }
		
		
		
}
