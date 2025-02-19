package csapi.utils;

import csshared.utils.CsConfig;
import alain.core.db.Sage;
import alain.core.utils.Logger;

public class ViewGenerate {

	public static boolean createViewCustomFields(int groupId,String table,String valuetable,String col){
		boolean result = false;
		String command = "select F.*,G.GROUP_NAME  from FIELD F join FIELD_GROUPS G on F.FIELD_GROUPS_ID=G.ID where FIELD_GROUPS_ID= "+groupId;
		StringBuilder sb = new StringBuilder();
		StringBuilder v = new StringBuilder();
		StringBuilder cols = new StringBuilder();
		StringBuilder selects = new StringBuilder();
		StringBuilder joins = new StringBuilder();
		Sage db = new Sage();
		int i =0;
		String name = "";
		if(db.query(command)){
			int size = db.size();
			while(db.next()){
				i = i+1;
				int id = db.getInt("ID");
				name = db.getString("GROUP_NAME");
				cols.append(db.getString("NAME"));
				selects.append("E").append(id).append(".VALUE ");
				
				if(i!=size){
					cols.append(",");
					selects.append(",");
				}
				
				joins.append(" LEFT OUTER JOIN    ").append(valuetable).append(" AS E").append(id).append(" ON     A.ID = E").append(id).append(".ACTIVITY_ID AND E").append(id).append(".FIELD_ID = ").append(id).append("  ");
			}
		}
		
		
		//db.clear();
		
		Logger.info(cols.toString());
		Logger.info("--------------------");
		Logger.info(selects.toString());
		Logger.info("--------------------");
		Logger.info(joins.toString());
		
		String gname = "V_CUSTOM_FIELD_GROUP_"+name;
		
		StringBuilder drop = new StringBuilder();
		drop.append("DROP VIEW ").append(gname);
		result = db.update(drop.toString());
		
		v.append(" CREATE VIEW  ").append(gname).append(" ( "); 
		v.append(col);
		v.append(",");
		v.append(cols.toString());
		v.append(" ) AS ");
		
		v.append(" SELECT DISTINCT ");
		v.append("AF.").append(col);
		v.append(",");
		v.append(selects.toString());
		v.append(" FROM  ").append(table);
		v.append(" A ");
		v.append(" LEFT OUTER JOIN     ").append(valuetable).append(" AF ON    A.ID = AF.").append(col).append(" ");
		v.append(" LEFT OUTER JOIN    FIELD F ON    AF.FIELD_ID = F.ID ");
		v.append(joins.toString());
		v.append(" WHERE F.ACTIVE='Y' AND F.FIELD_GROUPS_ID=").append(groupId);
		
		result = db.update(v.toString());
		db.clear();
		Logger.info(v.toString());
		return result;
	}
	
	
}
