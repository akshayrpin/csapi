package csapi.impl.project;

import java.util.ArrayList;

import alain.core.security.Token;
import csapi.common.LkupCache;
import csshared.vo.ToolVO;
import csshared.vo.ToolsVO;
import csshared.vo.lkup.RolesVO;

public class ProjectTools {

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
			vo.setTool("activity");
			vo.setImage("clipboard");
			vo.setTitle("Add Activity");
			vo.setAction("add");
			vo.setDisableonhold(true);
			vo.setHolds(alert);
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
		
		rl = LkupCache.getModuleRoles("team");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("team");
			vo.setImage("team");
			vo.setTitle("Add Team Member");
			tl.add(vo);
		}
		
		rl = LkupCache.getModuleRoles("people");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("people");
			vo.setImage("people");
			vo.setTitle("Add People");
			tl.add(vo);
		}
		
		rl = LkupCache.getModuleRoles("deposit");
		if (rl.readAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("deposit");
			vo.setImage("deposit");
			vo.setTitle("Deposit");
			vo.setAction("depositpayment");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("history");
		if (rl.readAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("history");
			vo.setImage("history");
			vo.setTitle("View History");
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

		ToolVO[] t = tl.toArray(new ToolVO[tl.size()]);
		r.setTools(t);

		return r;
	}
	

}
