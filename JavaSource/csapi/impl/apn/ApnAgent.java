package csapi.impl.apn;

import java.text.DecimalFormat;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.finance.FinanceSQL;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
public class ApnAgent {

	
	public static ObjGroupVO printNotes(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		String command = ApnSQL.summary(r.getType(), r.getTypeid(), 0);
		Sage db = new Sage();
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		db.query(command);
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(outstyle).append("\">NOTES </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> DATE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> NOTE </td>");
	
		sb.append("	</tr> \n");
		
		
		while(db.next()){
			result = true;
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("CREATED_DATE")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("NOTE")).append("</td>");
			sb.append("	</tr> \n");
			
		}
		sb.append("</tbody>");
		sb.append("</table> \n");
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_notes");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	
}
















