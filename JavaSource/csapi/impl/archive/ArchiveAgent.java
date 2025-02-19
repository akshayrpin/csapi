package csapi.impl.archive;

import alain.core.security.Token;
import alain.core.utils.Logger;
import csapi.common.LkupCache;
import csapi.impl.lso.LsoAgent;
import csshared.vo.ObjGroupVO;
import csshared.vo.lkup.RolesVO;


public class ArchiveAgent {

	public static ObjGroupVO summary(String type, int typeid, int id, String option, Token u) {
		return summary(type, typeid, id, option, u,0,0);
	}
	
	public static ObjGroupVO summary(String type, int typeid, int id, String option, Token u, int start, int end) {
		ObjGroupVO result = new ObjGroupVO();
		
		if(!u.isStaff()){
			boolean blocked = LsoAgent.blocked(type, typeid);
			if(blocked){
				return new ObjGroupVO();
			}
		}
		result.setFields(new String[] {"NUMBER","TYPE","ADDRESS","DESCRIPTION","DATE"});
		String grp = "archive";
		String grpid = "archive";
		String grppublic = "Y";
		try{
			int top = 25;
			int skip =0;
			if(start>0){
				top = start;
				skip = end;
			}
			
	
			result = Archives.getArchive(result,type, typeid,top,skip,u.isStaff());
			result.setAddable(false);
			result.setDeletable(false);
			result.setEditable(false);
			result.setType("archive");
			result.setGroup(grp);
			result.setPub(grppublic);
			if(start>0){
				result.setLabel("Archived Documents");
			}else {
				if(result.getCustomsize()>25){
					result.setLabel("Archived Documents (25/"+result.getCustomsize()+" records showing) ");
				}else {
					result.setLabel("Archived Documents");
				}
			}
			result.setGroupid(grpid);
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.info("EXCEPTION"+e.getMessage());	
		}
		RolesVO r = LkupCache.getModuleRoles("archive");
		result.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		if (!result.isRead()) { return new ObjGroupVO(); }
		return result;
	}
	
	
	


}















