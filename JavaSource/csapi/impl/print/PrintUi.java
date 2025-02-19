package csapi.impl.print;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.admin.AdminAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.parking.ParkingAgent;
import csapi.impl.project.ProjectSQL;
import csapi.utils.CsApiConfig;
import csshared.vo.ColorList;
import csshared.vo.ColorVO;

public class PrintUi {

	
	
		
		public static String dotStrickers(int id, int batch_id, boolean reprint){
			String g = "";
			String boldfont = "font-size:13pt; font-family:sans-serif;font-weight: bold;";
			String normalfont = "font-size:10pt; font-family:sans-serif;font-weight:100;";
			String biggernormalfont = "font-size:13pt; font-family:sans-serif;font-weight:100;";
			String border = "border: 1px solid black";
			try {
				ColorList colors = ActivityAgent.getColor();
				StringBuilder sb = new StringBuilder();
				boolean result = true;
				String breakline = "</tr><tr>\n";
				
				sb.append("<table style=\"width:100%;\"><tr><td style=\"padding-left: 18px; padding-right: 18px\">\n");
				sb.append("<table style=\"width:100%; background-color: black\"><tr>\n");
				
				JSONObject d = GeneralAgent.getConfiguration("DOT");
				String startdate = ParkingAgent.getParkingDate(d.getString("START_DATE"));
				String enddate = ParkingAgent.getParkingDate(d.getString("EXP_DATE"));
				
				
				String type = "project";
				
				String ids = id+"";
				
				JSONObject detail = PrintAgent.listAccount(id);
				
				Sage db = new Sage();
				String command =  ProjectSQL.getParkingPermits(ids, type, startdate, enddate, reprint, batch_id);
				db.query(command);
				int count =0;
				int i =1;
				StringBuffer actid = new StringBuffer();

				while(db.next()){
					actid.append(db.getString("ID"));
					if(i < db.size())
						actid.append(",");
					i++;
					
					if(db.getDouble("BALANCE")<=0){
						if(count%2==0){
							sb.append(breakline);
						}
						sb.append(getStickerStyle(db.getString("TYPE"), db.getString("EXP_DATE"), db.getString("ACT_NBR"), detail, colors));
						count++;
					}
					
				}

				while(count<6){
					if(count%2==0){
						sb.append(breakline);
					}
					sb.append(getStickerStyle("VOID", null, null, detail, colors));
					count++;
				}
				sb.append("	</tr></table>\n");
				sb.append("	</td></tr></table>\n");
				
				db.clear();
				
				
				sb.append("<table border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%;\">");
				sb.append("<tr>");
				sb.append("<td colspan=\"2\">&nbsp;</td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td align=\"right\" style=\"").append(boldfont).append("width: 150px;\">Account # : </td><td align=\"left\"><span style=\"").append(biggernormalfont).append("\">").append(detail.getString("ACCOUNT_NUMBER")).append("</span></td>");
				sb.append("</tr>");
				sb.append("</table>");
				
				
				
				g = sb.toString();
				
			if(Operator.hasValue(actid) && !reprint){
					ActivityAgent.updateActivity(actid.toString(), "Y", Operator.toString(batch_id));
				}
				
			}catch (Exception e){
				Logger.error(e.getMessage());
			}
			return g;
		}
		
		
	
	
	public static StringBuilder getStickerStyle(String parkingType, String expiry, String permitnumber, JSONObject detail, ColorList colors){

		String colorPreferential = "#C14296";
		String colorOvernight = "#47C147";
		String colorCaregiver = "#0099cc";
		String headingstyle = "font-weight: bold;color: white; font-size: 12px;";
		String boxstyle = "background-color:white; font-size: 50px;";
		String voidstyle = "background-color:#fff;font-size: 50px; color:#888;";
		String sticker = Config.fullcontexturl()+"/images/DOTCG.png";
		int expyear = -1;
		if (Operator.hasValue(expiry)) {
			String[] exp = Operator.split(expiry, "/");
			if (exp.length > 2) {
				expyear = Operator.toInt(exp[2]);
			}
		}
		String zone = "";
		try {
			zone = detail.getString("DIVISION");
		}
		catch (Exception e) { }
		ColorVO color = new ColorVO();
		String cstyle = "";
		String clabel = "";
		String ccolor = "";
		if (expyear > 2000) {
			color = colors.getColor(parkingType, expyear);
			clabel = color.getLabel(zone);
			ccolor = color.getColor();
			cstyle = color.getStyle();
		}

		StringBuilder sb = new StringBuilder();
		try{
			if (Operator.equalsIgnoreCase(cstyle, "right")) {
				sb.append(" 		<td style=\"padding: 11px;\">\n");
				sb.append(" 			<table style=\"width: 100%;\">\n");
				sb.append("  				<tr>\n");
				sb.append("  					<td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"width: 35%; ").append(boxstyle).append("\" rowspan=\"2\">").append(clabel).append("</td>\n");
				sb.append(" 					<td align=\"center\" style=\"background-color: #").append(ccolor).append("\" nowrap>\n");
				sb.append(" 						<img src=\"").append(sticker).append("\" height=\"95\"/>\n");
				sb.append("  						<p align=\"left\" style=\"margin-top:-28px;background:white; font-weight: bold; font-size:19px\">").append(permitnumber).append("</p> ");
				sb.append(" 					</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"font-weight: bold; color: white; font-size: 12px; background-color: black;\" nowrap>EXPIRES ").append(expiry).append("</td>\n");
				sb.append(" 				</tr>\n");
				sb.append(" 			</table>\n");
				sb.append(" 		</td>\n");
			}
			else if (Operator.equalsIgnoreCase(cstyle, "left")) {
				sb.append(" 		<td style=\"padding: 11px;\">\n");
				sb.append(" 			<table style=\"width: 100%;\">\n");
				sb.append("  				<tr>\n");
				sb.append("  					<td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"background-color: #").append(ccolor).append("\" nowrap>\n");
				sb.append(" 						<img src=\"").append(sticker).append("\" height=\"95\"/>\n");
				sb.append("  						<p align=\"left\" style=\"margin-top:-28px;background:white; font-weight: bold; font-size:19px\">").append(permitnumber).append("</p> ");
				sb.append(" 					</td>\n");
				sb.append(" 					<td align=\"center\" style=\"width: 35%; ").append(boxstyle).append("\" rowspan=\"2\">").append(clabel).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"font-weight: bold; color: white; font-size: 12px; background-color: black;\" nowrap>EXPIRES ").append(expiry).append("</td>\n");
				sb.append(" 				</tr>\n");
				sb.append(" 			</table>\n");
				sb.append(" 		</td>\n");
			}
			else if(parkingType.equalsIgnoreCase("PREFERENTIAL PARKING")|| parkingType.equalsIgnoreCase("TRANSFER PREFERENTIAL PARKING")){
				sb.append(" 		<td style=\"padding: 11px;\">\n");
				sb.append(" 			<table style=\"width: 100%;\">\n");
				sb.append("  				<tr>\n");
				sb.append("  					<td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"width: 35%; ").append(boxstyle).append("\" rowspan=\"2\">").append(detail.getString("DIVISION")).append("</td>\n");
				sb.append(" 					<td align=\"center\" style=\"background-color:").append(colorPreferential).append("\" nowrap>\n");
				sb.append(" 						<img src=\"").append(sticker).append("\" height=\"95\"/>\n");
				sb.append("  						<p align=\"left\" style=\"margin-top:-28px;background:white; font-weight: bold; font-size:19px\">").append(permitnumber).append("</p> ");
				sb.append(" 					</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"font-weight: bold; color: white; font-size: 12px; background-color: black;\" nowrap>EXPIRES ").append(expiry).append("</td>\n");
				sb.append(" 				</tr>\n");
				sb.append(" 			</table>\n");
				sb.append(" 		</td>\n");
			}
			else if(parkingType.equalsIgnoreCase("OVERNIGHT PARKING") || parkingType.equalsIgnoreCase("REPLACEMENT OVERNIGHT") || parkingType.equalsIgnoreCase("TRANSFER OVERNIGHT")){
				sb.append(" 		<td style=\"padding: 11px;\">\n");
				sb.append(" 			<table style=\"width: 100%;\">\n");
				sb.append("  				<tr>\n");
				sb.append("  					<td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"background-color:").append(colorOvernight).append("\" nowrap>\n");
				sb.append(" 						<img src=\"").append(sticker).append("\" height=\"95\"/>\n");
				sb.append("  						<p align=\"left\" style=\"margin-top:-28px;background:white; font-weight: bold; font-size:19px\">").append(permitnumber).append("</p> ");
				sb.append(" 					</td>\n");
				sb.append(" 					<td align=\"center\" style=\"width: 35%; ").append(boxstyle).append("\" rowspan=\"2\">O/N</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"font-weight: bold; color: white; font-size: 12px; background-color: black;\" nowrap>EXPIRES ").append(expiry).append("</td>\n");
				sb.append(" 				</tr>\n");
				sb.append(" 			</table>\n");
				sb.append(" 		</td>\n");
			}
			
			else if(parkingType.equalsIgnoreCase("CAREGIVER PARKING")){
				sb.append(" 		<td style=\"padding: 11px;\">\n");
				sb.append(" 			<table style=\"width: 100%;\">\n");
				sb.append("  				<tr>\n");
				sb.append("  					<td colspan=\"2\" align=\"center\" style=\"").append(headingstyle).append("\">").append(parkingType.toUpperCase()).append("</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"width: 35%; ").append(boxstyle).append("\" rowspan=\"2\">RN</td>\n");
				sb.append(" 					<td align=\"center\" style=\"background-color:").append(colorCaregiver).append("\" nowrap>\n");
				sb.append(" 						<img src=\"").append(sticker).append("\" height=\"95\"/>\n");
				sb.append("  						<p align=\"left\" style=\"margin-top:-28px;background:white; font-weight: bold; font-size:19px\">").append(permitnumber).append("</p> ");
				sb.append(" 					</td>\n");
				sb.append("  				</tr>\n");
				sb.append(" 				<tr>\n");
				sb.append(" 					<td align=\"center\" style=\"font-weight: bold; color: white; font-size: 12px; background-color: black;\" nowrap>EXPIRES ").append(expiry).append("</td>\n");
				sb.append(" 				</tr>\n");
				sb.append(" 			</table>\n");
				sb.append(" 		</td>\n");
			}
			else if(parkingType.equalsIgnoreCase("VOID")){
				sb.append("	<td style=\"padding-bottom: 10px; background-color: #ffffff\">\n");
				sb.append("	<table align=\"center\" style=\"width:100%;\">\n");
				sb.append("	<tr> \n");
				sb.append("	<td align=\"center\" style=\"height: 140px; ").append(voidstyle).append("\">VOID</td> \n");
				sb.append("	</tr> \n");
				sb.append("	</table> \n");
				sb.append("	</td> \n");
			}
		}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
	
		return sb;
	}
	
	
	
	
	public static JSONObject getFieldsPlugin(String tab){
		JSONObject o = new JSONObject();
		try{
			
			
			Logger.info(tab+"ssssssssssssstabss");
			if(Operator.equalsIgnoreCase("lso", tab)){
				//lso
				JSONArray sarr = new JSONArray();
				JSONObject l = new JSONObject();
				l.put("field", "lso_address");
				sarr.put(l);
				l = new JSONObject();
				l.put("field", "lso_apn");
				sarr.put(l);
				o.put("lso_select", sarr);
				Logger.info(o.toString()+"sssssssssssssssssssssss");
				
				
				ArrayList<MapSet> pfields = new ArrayList<MapSet>();
				pfields = AdminAgent.getList("SELECT *  FROM LKUP_DIVISIONS_TYPE WHERE ACTIVE='Y' ");
				JSONArray divisions = new JSONArray();
				
				JSONObject p = new JSONObject();
				for(MapSet m : pfields){
					p.put("field","divisions_"+m.getString("TYPE"));
				}
				
				divisions.put(p);
				o.put("divisions_select", divisions);
				
				
				
			}
			
			if(Operator.equalsIgnoreCase("tab", "project")){
				//project
				JSONArray project = new JSONArray();
				JSONObject p = new JSONObject();
				p.put("project_ACCOUNT_NUMBER","project_ACCOUNT_NUMBER");
				p.put("project_APPLIED_DT","project_ACCOUNT_NUMBER");
				p.put("project_COMPLETION_DT","project_COMPLETION_DT");
				p.put("project_CREATED_BY","project_CREATED_BY");
				p.put("project_CREATED_DATE","project_CREATED_DATE");
				p.put("project_DESCRIPTION","project_DESCRIPTION");
				p.put("project_ID","project_ID");
				p.put("project_NAME","project_NAME");
				p.put("project_PROJECT_NBR","project_PROJECT_NBR");
				p.put("project_STATUS","project_STATUS");
				p.put("project_TYPE","project_TYPE");
				p.put("project_UPDATED_BY","project_UPDATED_BY");
				p.put("project_UPDATED_DATE","project_UPDATED_DATE");
				project.put(p);
				o.put("project", project);
			}
			
			if(Operator.equalsIgnoreCase("tab", "activity")){
				//activity
				JSONArray activity = new JSONArray();
				JSONObject p = new JSONObject();
				p.put("activity_ACT_NBR","activity_ACT_NBR");
				p.put("activity_APPLIED_DATE","activity_APPLIED_DATE");
				p.put("activity_CREATED_BY","activity_CREATED_BY");
				p.put("activity_DESCRIPTION","activity_DESCRIPTION");
				p.put("activity_EXP_DATE","activity_EXP_DATE");
				//ALAIN
				p.put("activity_APPLICATION_EXP_DATE","activity_APPLICATION_EXP_DATE");

				p.put("activity_ID","activity_ID");
				p.put("activity_ISSUED_DATE","activity_ISSUED_DATE");
				p.put("activity_START_DATE","activity_START_DATE");
				p.put("activity_TYPE","activity_TYPE");
				p.put("activity_UPDATED_BY","activity_UPDATED_BY");
				p.put("activity_VALUATION_CALCULATED","activity_VALUATION_CALCULATED");
				p.put("activity_VALUATION_DECLARED","activity_VALUATION_DECLARED");
				activity.put(p);
				o.put("activity", activity);
			}
			
			if(Operator.equalsIgnoreCase("tab", "divisions")){
				ArrayList<MapSet> pfields = new ArrayList<MapSet>();
				pfields = AdminAgent.getList("SELECT *  FROM LKUP_DIVISIONS_TYPE WHERE ACTIVE='Y' ");
				JSONArray divisions = new JSONArray();
				
				JSONObject p = new JSONObject();
				for(MapSet m : pfields){
					p.put("divisions_"+m.getString("TYPE"),"divisions_"+m.getString("TYPE"));
				}
				
				divisions.put(p);
				o.put("divisions", divisions);
			}
			
			
			
			
		}catch(Exception e){
			
		}
		
		return o;
	}
	public static String dotRenewals(int id, String selected, String batch_id,boolean reprint){
		String g = "";
		
		try{
			
			StringBuilder sb = new StringBuilder();
			sb.append("<table>\n");
			
			JSONObject d = GeneralAgent.getConfiguration("DOT");
			String startdate = ParkingAgent.getParkingDate(d.getString("START_DATE"));
			String enddate = ParkingAgent.getParkingDate(d.getString("EXP_DATE"));
			
			
			String type = "project";
			
			String ids = id+"";
			
			Sage db = new Sage();
			String command =  ProjectSQL.getRenewalPermits(ids, type, startdate, enddate,selected);
			db.query(command);
			while(db.next()){
				
				sb.append("<tr> <td align=\"center\">");
				sb.append(" <img src=\""+Config.rooturl() +"/csapi/images/incomplete.jpg\" style=\"height:16px\" />");
				sb.append(db.getString("COUNT"));
				sb.append(" :  ");
				sb.append(db.getString("DESCRIPTION"));
				sb.append(" Permit(s) @ $  ");
				
				if(Operator.equalsIgnoreCase(db.getString("DESCRIPTION"), "Overnight Parking")){
					sb.append(" 114.00 each </td> </tr>");
					sb.append("<tr> <td align=\"center\">");
					sb.append("<span style=\"font-size:10\">Overnight Parking Permits(California License plate required) </span>");
				}else if(Operator.equalsIgnoreCase(db.getString("DESCRIPTION"), "Preferential Parking")){
					sb.append(" 35 each </td> </tr>");
					sb.append("<tr> <td align=\"center\">");
					sb.append("<span style=\"font-size:10\">(Maximum of 3 Preferential permits allowed per household) </span>");
					
				}
				sb.append(" </td></tr>");
				sb.append("<tr><td>&nbsp;</td></tr>");
			}
			sb.append("<tr><td>&nbsp;</td></tr>");

			sb.append(" </table>");
			g = sb.toString();
			db.clear();
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}




	public static String dotPaymentReceipt(int id, String ref) {
String g = "";
		
		try{
			
			StringBuilder sb = new StringBuilder();
			sb.append("<table>\n");
			
			Sage db = new Sage();
			String command =  ProjectSQL.getPaymentDetails(id, ref);
			db.query(command);
			while(db.next()){
				sb.append("<tr> <td align=\"center\">");
				sb.append("<span style=\"font-size:12\"> Invoice Number : " +db.getString("ID"));
				sb.append("</span></td></tr>");
				sb.append("<tr> <td align=\"center\">");
				sb.append("<span style=\"font-size:12\">Transaction Number : " +db.getString("TRANSACTION_ID"));
				sb.append("</span></td></tr>");
				sb.append("<tr> <td align=\"center\">");
				sb.append("<span style=\"font-size:12\">Total Amount : $ " +db.getString("PAYMENT_AMOUNT"));
				sb.append("</span></td></tr>");
			}
			sb.append(" </table>");
			g = sb.toString();
			db.clear();
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	public static String dotRenewalsUi(String type,int overnightcount){
		StringBuilder sb = new StringBuilder();
		
		try{
			String cimage = "<img src=\""+Config.rooturl()+"/csapi/images/checkbox.png\"  />";
		
			
			 sb.append(" <table cellpadding=\"2\" cellspacing=\"2\" style=\"width:100%; border:1px solid;  \" > ");
			 sb.append("  ");
			 sb.append("  ");
			
			 if(type.equals("P")|| type.equals("B")){
				 sb.append("  <tr> ");
				 sb.append("  <td style=\"border-bottom: 2px solid;\"> ");
				 sb.append("  <table border=\"0\" cellpadding=\"2\" cellspacing=\"2\" style=\"width:100%\"> ");
				 sb.append("  ");
				 sb.append("      <tr> ");
				 sb.append("         <td align=\"center\" style=\"font-size:14px;font-weight:bold;font-family:Arial;\">Preferential Parking Permits (Daytime Permits) </td> ");
				 sb.append("      </tr> ");
				 sb.append("      <tr> ");
				 sb.append("         <td align=\"center\" style=\"font-size:12px;font-family:Arial;\">Maximum of Three(3) preferential permits per household</td> ");
				 sb.append("      </tr> ");
				 sb.append("      <tr> ");
				 sb.append("         <td align=\"center\" style=\"font-size:12px;font-family:Arial;\">Please Indicate the number of permits you wish to purchase</td> ");
				 sb.append("      </tr> "); 
				 sb.append("      <tr> "); 
				 sb.append("       <td align=\"center\" style=\"font-size:12px;font-family:Arial;\">");
				 sb.append("         <table border=\"0\" cellpadding=\"2\" cellspacing=\"2\" style=\"width:100%\" align=\"center\"> ");
				 sb.append("             <tr> ");
				 sb.append("                 <td align=\"center\" style=\"font-size:14px;font-family:Arial;\">").append(cimage).append(" &nbsp; 1 Permit : $35.00&nbsp;&nbsp;</td> ");
				 sb.append("             </tr> ");
				 sb.append("             <tr> ");
				 sb.append("                 <td align=\"center\" style=\"font-size:14px;font-family:Arial;\">").append(cimage).append(" &nbsp; 2 Permits : $70.00&nbsp;</td> ");
				 sb.append("             </tr> ");
				 sb.append("             <tr> ");
				 sb.append("                 <td align=\"center\" style=\"font-size:14px;font-family:Arial;\">").append(cimage).append(" &nbsp; 3 Permits : $105.00</td> ");
				 sb.append("             </tr> ");
				 sb.append("         </table> ");
				 sb.append("       </td> ");
				 sb.append("      </tr> ");
				 sb.append("   </table> ");
				 sb.append("   </td> ");
				 sb.append("   </tr> ");
				 sb.append("  ");
				 if(type.equals("O")|| type.equals("B")){
					 sb.append("   <tr> ");
					 sb.append("    <td><hr/></td> ");
					 sb.append("   </tr> ");
				 }
			 }
			 if(type.equals("O")|| type.equals("B")){
			 
				 sb.append("   <tr> ");
				 sb.append("    <td> ");
				 sb.append("    <table border=\"0\" cellpadding=\"5\" cellspacing=\"2\" style=\"width:100%\"> ");
				 sb.append("        <tr> ");
				 sb.append("           <td align=\"center\" style=\"font-size:14px;font-weight:bold;font-family:Arial;\">Overnight Parking Permits</td> ");
				 sb.append("        </tr> "); 
				 sb.append("        <tr> ");
				 sb.append("           <td align=\"center\" style=\"font-size:14px;font-family:Arial;\">");
				 sb.append("        		").append(cimage).append(" &nbsp; Please renew my existing ").append(overnightcount).append(" Overnight Parking Permit(s) @ $114.00 each");
				 sb.append("         	</td> ");
				 sb.append("         </tr> ");
				 sb.append("     </table> ");
				 sb.append("     </td> ");
				 sb.append("   </tr> ");
				 sb.append("  ");
			 }
			
			 sb.append(" </table> ");
			
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		return sb.toString();
	}
	
	
	public static String getPermits2021(int accountno){
		StringBuilder sb = new StringBuilder();
		
		String command = "select CONVERT(VARCHAR(10),A.EXP_DATE,101) as EXPIRATION ,* from ACTIVITY A JOIN LKUP_ACT_TYPE LAT on A.LKUP_ACT_TYPE_ID = LAT.ID JOIN REF_PROJECT_PARKING AS DTA ON A.PROJECT_ID = DTA.PROJECT_ID  where A.EXP_DATE = '2022-01-31' AND DTA.ID= "+accountno;
		Sage db = new Sage();
		db.query(command);
		sb.append("<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px;align: center;\">");
		sb.append("				<thead>");
		sb.append("					<tr>");
		sb.append("						<th style=\"text-align: center;\" scope=\"col\">Permit&nbsp;</th>");
		//sb.append("						<th style=\"text-align: center;\" scope=\"col\">Type</th>");
		sb.append("						<th style=\"text-align: center;\" scope=\"col\">Expiration Date</th>");
		sb.append("					</tr>");
		sb.append("				</thead>");
		
		
		sb.append("				<tbody>");
		while(db.next()){
		sb.append("					<tr rowtype=\"list_summary_activities\">");
		sb.append("						<td style=\"text-align: center;\">").append(db.getString("ACT_NBR")).append("</td>");
	//	sb.append("						<td style=\"text-align: center;\">").append(db.getString("TYPE")).append("</td>");
		sb.append("						<td style=\"text-align: center;\">").append(db.getString("EXPIRATION")).append(" </td>");
		sb.append("					</tr>");
		}
		sb.append("				</tbody>");
		sb.append("			</table>");
		
		
		db.clear();
		return sb.toString();
	}
	
	
	
	
	
}
