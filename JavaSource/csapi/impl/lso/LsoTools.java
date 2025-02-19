package csapi.impl.lso;

import java.util.ArrayList;

import alain.core.security.Token;
import csapi.common.LkupCache;
import csshared.vo.ToolVO;
import csshared.vo.ToolsVO;
import csshared.vo.lkup.RolesVO;

public class LsoTools {

	public static ToolsVO tools(String entity, int entityid, String type, int typeid, String alert, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity(entity);
		r.setEntityid(entityid);

		ArrayList<ToolVO> tl = new ArrayList<ToolVO>();
		ToolVO vo = new ToolVO();

		RolesVO rl = LkupCache.getModuleRoles("activity");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("project");
			vo.setImage("briefcase");
			vo.setTitle("Add Project");
			vo.setAction("add");
			vo.setHolds(alert);
			vo.setDisableonhold(true);
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("resolution");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("resolution");
			vo.setImage("resolution");
			vo.setTitle("Add Resolution");
			vo.setAction("add");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("appointment");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("appointment");
			vo.setImage("appointment");
			vo.setTitle("Add Appointment");
			vo.setAction("add");
			tl.add(vo);
		}
		
		rl = LkupCache.getModuleRoles("archive");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("archive");
			vo.setImage("archive");
			vo.setTitle("View Archive");
			vo.setAction("more");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("notes");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("notes");
			vo.setImage("notes");
			vo.setTitle("Add Notes");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("attachments");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("attachments");
			vo.setImage("attachments");
			vo.setTitle("Attach File");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("print");
		if (rl.readAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("print");
			vo.setImage("print");
			vo.setTitle("Print");
			vo.setAction("print");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("map");
		if (rl.readAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("map");
			vo.setImage("map");
			vo.setTitle("View GIS");
			vo.setAction("map");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("search");
		if (rl.readAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("search");
			vo.setImage("search");
			vo.setTitle("Search");
			vo.setAction("search");
			tl.add(vo);
		}

		ToolVO[] t = tl.toArray(new ToolVO[tl.size()]);
		r.setTools(t);

		return r;
	}
	

}
