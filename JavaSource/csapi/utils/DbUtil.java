package csapi.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csshared.vo.ObjGroupVO;

import java.util.Iterator;

public class DbUtil {

	@SafeVarargs
	public static HashMap<String, HashMap<String, String>> getListAsMap(String table, List<String> ... args){
		return getListAsMap(table, null, args);
	}


	@SafeVarargs
	public static ObjGroupVO[] getListAsObjGroup(String table, List<String> ... args){
		return getListAsObjGroup(table, null, args);
	}
	
	@SafeVarargs
	public static HashMap<String, String> getObjectAsMap(String table, List<String> ... args) throws SQLException{
		return getObjectAsMap(table, null, args);
	}
	
	@SafeVarargs
	public static String buildQuery(String table, Map<String, String> where, List<String> ... args){
		
		List<String> select = null;
		List<String> join = null;
		
		if (args != null)
			select = args[0];
		if(args.length == 2){
			select = args[0];
			join = args[1];
		}
		
		StringBuffer sb = new StringBuffer("select distinct ");
		if (select != null) {			
			Iterator<String> itr = select.iterator();
			while(itr.hasNext()){
				sb.append(itr.next());
				if(itr.hasNext()){
					sb.append(", ");
				}
			}
		} 

		sb.append(" from ").append(table).append(" ");

		if (join != null) {
			join.stream().forEach(item -> sb.append(item).append(" "));
		}

		if (where != null) {
			sb.append(" where ");			
			Iterator<Entry<String, String>> itrMap = where.entrySet().iterator();
			while(itrMap.hasNext()){
				Entry<String, String> item = itrMap.next();
				if(item.getValue().equalsIgnoreCase("CURRENT_TIMESTAMP")){
					sb.append(item.getKey()).append(" = ").append(item.getValue());
				}else{
					sb.append(item.getKey()).append(" = '").append(item.getValue()).append("'");
				}
				if(itrMap.hasNext())
					sb.append(" and ");
			}
		}
		
		return sb.toString();
	}
	
	@SafeVarargs
	public static HashMap<String, HashMap<String, String>> getListAsMap(String table, Map<String, String> where, List<String> ... args){
		
		List<String> select = null;
		if (args != null)
			select = args[0];
		
		String sb = buildQuery(table, where, args);

		Sage db = new Sage();			
		
		db.query(sb.toString());
		
		HashMap<String, HashMap<String, String>> outter = new HashMap<String, HashMap<String, String>>();
		String key = chekAlias(select.get(0));
		while(db.next()){	
			HashMap<String, String> extras = new HashMap<String, String>();
			String value = null;
			for(String item : select){				
				value = chekAlias(item);
				extras.put(value, db.getString(value));
			}
			outter.put(db.getString(key), extras);
		}
		
		db.clear();
		return outter;
	}
	

	@SafeVarargs
	public static ObjGroupVO[] getListAsObjGroup(String table, Map<String, String> where, List<String> ... args){
		
		List<String> select = null;
		if (args != null)
			select = args[0];
		String sb = buildQuery(table, where, args);

		Sage db = new Sage();	
		
		db.query(sb.toString());
		ObjGroupVO[] vos =new ObjGroupVO[db.size()];
		
		int count = 0;
		while(db.next()){
			ObjGroupVO vo = new ObjGroupVO();			
			HashMap<String, String> extras = new HashMap<String, String>();	
			
			select.stream().forEach(item -> extras.put(chekAlias(item), db.getString(chekAlias(item))));			
			vo.setExtras(extras);
			vos[count++] = vo;
		}
		
		db.clear();
		return vos;
	}
	
	private static String chekAlias(String str){
		
		String[] dot;
		String[] alias = Operator.split(str, ' ');
		if(alias.length > 1)
			if(Operator.equalsIgnoreCase("as", alias[1]))
				return alias[2];
			else 
				return alias[1];
		
		dot = Operator.split(str, '.');
		if(dot.length>1)
			str = dot[1];
		
		return str;
	}
	
	@SafeVarargs
	public static HashMap<String, String> getObjectAsMap(String table, Map<String, String> where, List<String>... args)
			throws SQLException {

		String sb = buildQuery(table, where, args);

		Sage db = new Sage();
		HashMap<String, String> hm = new HashMap<String, String>();
		try{
			CachedRowSet rs = db.getRowset(sb.toString());
			
			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
					hm.put(rs.getMetaData().getColumnName(i), rs.getString(i) != null ? Operator.escape(rs.getString(i).trim()) : Operator.escape(rs.getString(i)));
			}
	
			if(rs != null) rs.close();
			//if(db != null) db.clear();
			}
		catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return hm;
	}
	
	@SafeVarargs
	public static String getAsString(String table, Map<String, String> where, List<String> ... args){
		
		List<String> select = null;
		if (args != null)
			select = args[0];
		
		String sb = buildQuery(table, where, args);

		Sage db = new Sage();			
		
		db.query(sb.toString());
		
		String key = chekAlias(select.get(0));
		String value = null;
		if(db.next()){	
			value = db.getString(key);
		}
		
		db.clear();
		return value;
	}	
	
}
