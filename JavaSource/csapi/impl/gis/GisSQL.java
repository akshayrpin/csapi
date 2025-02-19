package csapi.impl.gis;



public class GisSQL {

	public static String details(String type, int typeid, int id) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select 'GIS' as TYPE ");
		
		
		return sb.toString();
	}
	
	public static String info(String type, int typeid, int id) {
	
		return details(type, typeid, -1);
	}
}
