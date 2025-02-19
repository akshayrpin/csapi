package csapi.impl.gis;

import alain.core.security.Token;
import alain.core.utils.Logger;
import csapi.common.LkupCache;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.lkup.RolesVO;

public class GisAgent {

	
	
	
	
	public static ObjGroupVO summary(String type, int typeid, int id, String option, Token u) {
		ObjGroupVO result = new ObjGroupVO();
		
		
		try{
			result = GisFields.details();
			ObjMap[] m = new ObjMap[1];
			
			ObjMap ma = new ObjMap();
			ma.setField("GIS");
			ma.setId(122);
			
			m[0] = ma;
			
			result.setValues(m);
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.info("EXCEPTION"+e.getMessage());	
		}
		RolesVO r = LkupCache.getModuleRoles("gis");
		result.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		if (!result.isRead()) { return new ObjGroupVO(); }
		return result;
	}
}
