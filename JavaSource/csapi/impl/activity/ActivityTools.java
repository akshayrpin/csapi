package csapi.impl.activity;

import java.util.ArrayList;

import alain.core.security.Token;
import csapi.common.LkupCache;
import csapi.impl.general.GeneralAgent;
import csshared.vo.ToolVO;
import csshared.vo.ToolsVO;
import csshared.vo.lkup.RolesVO;

public class ActivityTools {

	public static ToolsVO tools(String entity, int entityid, String type, int typeid, String alert, Token u) {
		ToolsVO r = new ToolsVO();
		r.setType(type);
		r.setTypeid(typeid);
		r.setEntity(entity);
		r.setEntityid(entityid);
		ArrayList<ToolVO> tl = new ArrayList<ToolVO>();

		ToolVO vo = new ToolVO();

		RolesVO rl = LkupCache.getModuleRoles("appointment");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("appointment");
			vo.setImage("appointment");
			vo.setTitle("Add Appointment");
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

		rl = LkupCache.getModuleRoles("people");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			vo = new ToolVO();
			vo.setTool("people");
			vo.setImage("people");
			vo.setTitle("Add People");
			tl.add(vo);
		}

		rl = LkupCache.getModuleRoles("library");
		if (rl.createAccess(u.getRoles(), u.getNonpublicroles()) || u.isAdmin()) {
			String libname = GeneralAgent.getLibraryGroupName(type, typeid);
			vo = new ToolVO();
			vo.setTool("library");
			vo.setImage("library");
			vo.setTitle("Add "+libname);
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
