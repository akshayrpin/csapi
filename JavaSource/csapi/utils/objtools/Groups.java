package csapi.utils.objtools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.LkupCache;
import csapi.impl.appointment.AppointmentAgent;
import csapi.impl.custom.CustomSQL;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.info.InfoAgent;
import csapi.impl.people.PeopleAgent;
import csapi.impl.resolution.ResolutionAgent;
import csapi.impl.resolution.ResolutionFields;
import csapi.impl.review.ReviewAgent;
import csapi.impl.team.TeamAgent;
import csapi.utils.CsGetCache;
import csapi.utils.CsSaveCache;
import csapi.utils.CsTools;
import csshared.vo.ComboReviewGroup;
import csshared.vo.ComboReviewList;
import csshared.vo.HoldsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ObjVO;
import csshared.vo.ResolutionVO;
import csshared.vo.lkup.RolesVO;

public class Groups {


	// SUMMARIES

	public static ObjGroupVO[] summary(String type, int typeid, String option, Token u) {
		boolean e = true;
		String[] modules = Modules.summary(type, typeid, u);

		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();

		String command = "";
		if (Operator.contains(modules, "custom")) {
			command = CustomSQL.groups(type, typeid);
			Sage db = new Sage();
			if (db.query(command)) {
				e = false;
			}
			db.clear();
		}

		Logger.info("MODULE", type);
		result.add(cachedGroupSummary(type, typeid, type, option, u));

		for (int i=0; i<modules.length; i++) {
			String module = modules[i];
			Logger.info("MODULE", module);
			if (module.equalsIgnoreCase("custom")) {
				if (!e) {
					ArrayList<ObjGroupVO> custom = custom(type, typeid, u);
					if (custom.size() > 0) { result.addAll(custom); }
				}
			}
			else {
				ObjGroupVO g = cachedGroupSummary(type, typeid, module, option, u);
				if (g.getValues().length > 0 || g.getObj().length > 0 || g.getReviews().length > 0) {
					result.add(g);
				}
			}
		}

		return result.toArray(new ObjGroupVO[result.size()]);
	}

	public static ObjGroupVO cachedGroupSummary(String type, int typeid, String module, String option, Token u) {
		ObjGroupVO vo = new ObjGroupVO();
		if (Operator.hasValue(option)) {
			vo = uncachedGroupSummary(type, typeid, module, option, u);
		}
		else {
			try { vo = CsGetCache.getObjCache(type, typeid, module, "summary"); }
			catch (Exception e) {
				vo = uncachedGroupSummary(type, typeid, module, option, u);
				CsSaveCache.saveCache(type, typeid, module, "summary", vo);
			}
		}
		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold(module);
		}
		if (dh) {
			HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
			RolesVO r = LkupCache.getModuleRoles(module);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
		}
		else {
			RolesVO r = LkupCache.getModuleRoles(module);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		}
		if (!vo.isRead()) { return new ObjGroupVO(); }
//		vo.setRead(r.readAccess(u.getRoles(), u.getNonpublicroles()));
//		vo.setCreate(r.createAccess(u.getRoles(), u.getNonpublicroles()));
//		vo.setUpdate(r.updateAccess(u.getRoles(), u.getNonpublicroles()));
//		vo.setDelete(r.deleteAccess(u.getRoles(), u.getNonpublicroles()));
		vo.setToken(u);
		return vo;
	}

	public static ObjGroupVO uncachedGroupSummary(String type, int typeid, String module, String option, Token u) {
		Logger.info("Retrieving non cached version of "+module);
		ObjGroupVO vo = new ObjGroupVO();
		if (module.equalsIgnoreCase("divisions")) { vo = DivisionsAgent.summary(type, typeid); }
		else if (module.equalsIgnoreCase("appointment")) {
			vo = AppointmentAgent.getSummary(type, typeid, -1, option);
		}
		else if (module.equalsIgnoreCase("people")) {
			vo = PeopleAgent.summary(type, typeid, -1);
		}
		else if (module.equalsIgnoreCase("resolution")) {
			ArrayList<ResolutionVO> res = ResolutionAgent.getResolutions(type, typeid);
			ObjGroupVO g = ResolutionFields.summary();
			if (res.size() > 0) {
				g.setResolutions(res.toArray(new ResolutionVO[res.size()]));
			}
			vo = g;
		}
		else { vo = Group.summary(type, typeid, module, option, u); }
		return vo;
	}

	public static ObjGroupVO cachedGroupExt(String type, int typeid, String module, String option, Token u) {
		if (Operator.hasValue(option)) { return uncachedGroupSummary(type, typeid, module, option, u); }
		ObjGroupVO vo = new ObjGroupVO();
		try { vo = CsGetCache.getObjCache(type, typeid, module, "ext"); }
		catch (Exception e) {
			vo = uncachedGroupExt(type, typeid, module, option, u);
			CsSaveCache.saveCache(type, typeid, module, "ext", vo);
		}
		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold(module);
		}
		if (dh) {
			HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
			RolesVO r = LkupCache.getModuleRoles(module);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
		}
		else {
			RolesVO r = LkupCache.getModuleRoles(module);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		}
		if (!vo.isRead()) { return new ObjGroupVO(); }
		return vo;
	}

	public static ObjGroupVO uncachedGroupExt(String type, int typeid, String module, String option, Token u) {
		Logger.info("Retrieving non cached version of "+module);
		ObjGroupVO vo = new ObjGroupVO();
		if (module.equalsIgnoreCase("divisions")) { vo = DivisionsAgent.details(type, typeid); }
		else if (module.equalsIgnoreCase("appointment")) {
			vo = AppointmentAgent.getSummary(type, typeid, -1, option);
		}
		else if (module.equalsIgnoreCase("resolution")) {
			ArrayList<ResolutionVO> res = ResolutionAgent.getResolutions(type, typeid);
			ObjGroupVO g = ResolutionFields.summary();
			if (res.size() > 0) {
				g.setResolutions(res.toArray(new ResolutionVO[res.size()]));
			}
			vo = g;
		}
		else { vo = Group.ext(type, typeid, module, option, u); }
		return vo;
	}

	public static ArrayList<ObjGroupVO> custom(String type, int typeid, Token u) {
		ArrayList<ObjGroupVO> all = new ArrayList<ObjGroupVO>();
		try { all = CsGetCache.getObjArrayCache(type, typeid, "custom", "summary"); }
		catch (Exception e) {
			all = uncachedCustom(type, typeid, u);
			CsSaveCache.saveCache(type, typeid, "custom", "summary", all);
		}
		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();
		HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold("custom");
		}
		for (int i=0; i<all.size(); i++) {
			ObjGroupVO vo = all.get(i);

			if (dh) {
				RolesVO r = LkupCache.getCustomRoles(Operator.toInt(vo.getGroupid()));
				vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
			}
			else {
				RolesVO r = LkupCache.getCustomRoles(Operator.toInt(vo.getGroupid()));
				vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}

			if (vo.isRead() || vo.isCreate() || vo.isUpdate() || vo.isDelete()) {
				result.add(vo);
			}

		}
		return result;
	}

	public static ArrayList<ObjGroupVO> uncachedCustom(String type, int typeid, Token u) {
		Logger.info("Retrieving non cached version of custom");
		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();
		Sage db = new Sage();
		String command = "";

//		HashMap<String, String> create = new HashMap<String, String>();
//		HashMap<String, String> read = new HashMap<String, String>();
//		HashMap<String, String> update = new HashMap<String, String>();
//		HashMap<String, String> delete = new HashMap<String, String>();
//		command = CustomSQL.roles(type, typeid);
//		db.query(command);
//		while (db.next()) {
//			String module = db.getString("FIELD_GROUPS_ID");
//			String role = db.getString("LKUP_ROLES_ID");
//
//			if (db.equalsIgnoreCase("C", "Y")) {
//				String r = create.get("C");
//				StringBuilder rsb = new StringBuilder();
//				if (Operator.hasValue(r)) { rsb.append(r).append(","); }
//				rsb.append(role);
//				create.put(module, rsb.toString());
//			}
//			if (db.equalsIgnoreCase("R", "Y")) {
//				String r = read.get("R");
//				StringBuilder rsb = new StringBuilder();
//				if (Operator.hasValue(r)) { rsb.append(r).append(","); }
//				rsb.append(role);
//				read.put(module, rsb.toString());
//			}
//			if (db.equalsIgnoreCase("U", "Y")) {
//				String r = update.get("U");
//				StringBuilder rsb = new StringBuilder();
//				if (Operator.hasValue(r)) { rsb.append(r).append(","); }
//				rsb.append(role);
//				update.put(module, rsb.toString());
//			}
//			if (db.equalsIgnoreCase("D", "Y")) {
//				String r = delete.get("D");
//				StringBuilder rsb = new StringBuilder();
//				if (Operator.hasValue(r)) { rsb.append(r).append(","); }
//				rsb.append(role);
//				delete.put(module, rsb.toString());
//			}
//		}


		String grpname = "";
		boolean multi = false;
		int setid = -1;
		command = CustomSQL.summary(type, typeid);
		if (db.query(command) && db.size() > 0) {
			ObjGroupVO g = new ObjGroupVO();
			g.setToken(u);
			g.setDisplayempty(true);
			ArrayList<ObjVO> obj = new ArrayList<ObjVO>();
			ArrayList<String> idx = new ArrayList<String>();
			ArrayList<ObjMap> maps = new ArrayList<ObjMap>();
			ObjMap m = new ObjMap();
			while (db.next()) {
				String dbgrpname = db.getString("GROUP_NAME");
				if (!Operator.equalsIgnoreCase(grpname, dbgrpname)) {
					if (Operator.hasValue(grpname)) {
						if (multi) {
							if (Operator.hasValue(idx)) {
								String[] idxa = Operator.toArray(idx);
								g.setFields(idxa);
								g.setIndex(idxa);
								idx = new ArrayList<String>();
							}
							maps.add(m);
							ObjMap[] objs = CsTools.convertMap(maps);
							g.setValues(objs);
						}
						else {
							ObjVO[] objs = CsTools.convert(obj);
							g.setObj(objs);
						}
						result.add(g);
						g = new ObjGroupVO();
						g.setToken(u);
						g.setDisplayempty(true);
						obj = new ArrayList<ObjVO>();
					}
					g.setGroup("custom");
					g.setLabel(dbgrpname);
//					g.setGroup(dbgrpname);
					g.setGroupid(db.getString("GROUP_ID"));

//					g.setCreate(Operator.split(create.get(db.getString("GROUP_ID"))));
//					g.setRead(Operator.split(read.get(db.getString("GROUP_ID"))));
//					g.setUpdate(Operator.split(update.get(db.getString("GROUP_ID"))));
//					g.setDelete(Operator.split(delete.get(db.getString("GROUP_ID"))));

					g.setPub(db.getString("GROUP_PUBLIC"));
					g.setType("custom");
					multi = db.equalsIgnoreCase("MULTI", "Y");
					grpname = dbgrpname;
					setid = -1;
					m = new ObjMap();
					m.setId(db.getInt("SET_ID"));
					maps = new ArrayList<ObjMap>();
				}
				ObjVO vo = new ObjVO();
				String value = db.getString("VALUE");
				String itype = db.getString("FIELD_ITYPE");
				vo.setId(db.getInt("ID"));
				vo.setField(db.getString("NAME"));
				vo.setValue(value);
				vo.setType(db.getString("TYPE"));
				vo.setItype(itype);
				vo.setFieldid(db.getString("ID"));
				vo.setType(db.getString("FIELD_TYPE"));
				vo.setItype(itype);
				if (db.hasValue("ISPUBLIC")) {
					vo.setShowpublic(db.equalsIgnoreCase("ISPUBLIC", "Y"));
				}
				if (itype.equalsIgnoreCase("team") || itype.equalsIgnoreCase("teammember")) {
					String team = TeamAgent.joinTeam(value, ", ");
					vo.setText(team);
				}
				if (multi) {
					if (db.equalsIgnoreCase("IDX", "Y") && !idx.contains(db.getString("NAME"))) {
						idx.add(db.getString("NAME"));
					}
					if (setid > -1 && setid != db.getInt("SET_ID")) {
						maps.add(m);
						m = new ObjMap();
						m.setId(db.getInt("SET_ID"));
					}
					setid = db.getInt("SET_ID");
					m.getValues().put(db.getString("NAME"), vo);
				}
				else if (db.hasValue("NAME")) {
					obj.add(vo);
				}
			}
			if (Operator.hasValue(grpname)) {
				if (multi) {
					if (Operator.hasValue(idx)) {
						String[] idxa =Operator.toArray(idx);
						g.setFields(idxa);
						g.setIndex(idxa);
					}
					maps.add(m);
					ObjMap[] objs = CsTools.convertMap(maps);
					g.setValues(objs);
				}
				else {
					ObjVO[] objs = CsTools.convert(obj);
					g.setObj(objs);
				}
				result.add(g);
			}
		}
		db.clear();
		return result;
	}

	// INFOs

	public static ObjGroupVO[] info(String type, int typeid, String option, Token u) {

		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();
		String[] modules = Modules.info(type, typeid, u);

		for (int i=0; i<modules.length; i++) {
			String module = modules[i];
			Logger.info("MODULE", module);
			if (module.equalsIgnoreCase("review") || module.equalsIgnoreCase("reviews")) {
				ArrayList<ObjGroupVO> rev = reviews(type, typeid, u);
				if (rev.size() > 0) {
					result.addAll(rev);
				}
			}
			else if (module.equalsIgnoreCase("resolution")) {
				ArrayList<ObjGroupVO> res = resolution(type, typeid, u);
				if (res.size() > 0) {
					result.addAll(res);
				}
			}
			else {
				result.add(cachedGroupInfo(type, typeid, module, option, u));
			}
		}
		return result.toArray(new ObjGroupVO[result.size()]);
	}

	public static ObjGroupVO cachedGroupInfo(String type, int typeid, String grouptype, String option, Token u) {
		if (Operator.hasValue(option)) { return uncachedGroupInfo(type, typeid, grouptype, option, u); }
		ObjGroupVO vo = new ObjGroupVO();
		try {
			vo = CsGetCache.getObjCache(type, typeid, grouptype, "info");
		}
		catch (Exception e) {
			vo = uncachedGroupInfo(type, typeid, grouptype, option, u);
			CsSaveCache.saveCache(type, typeid, grouptype, "info", vo);
		}

		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold(grouptype);
		}
		if (dh) {
			HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
			RolesVO r = LkupCache.getModuleRoles(grouptype);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
		}
		else {
			RolesVO r = LkupCache.getModuleRoles(grouptype);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		}
		if (!vo.isRead()) { return new ObjGroupVO(); }

		return vo;
	}

	public static ObjGroupVO uncachedGroupInfo(String type, int typeid, String grouptype, String option, Token u) {
		Logger.info("Retrieving non cached version of "+grouptype);
		ObjGroupVO vo = new ObjGroupVO();
		vo = Group.info(type, typeid, grouptype, option, u);
		return vo;
	}

	public static ArrayList<ObjGroupVO> reviews(String type, int typeid, Token u) {
		ArrayList<ObjGroupVO> all =  new ArrayList<ObjGroupVO>();
		try {
			all = CsGetCache.getObjArrayCache(type, typeid, "review", "info");
		}
		catch (Exception e) {
			all = unCachedReviews(type, typeid);
			CsSaveCache.saveCache(type, typeid, "review", "info", all);
		}
		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();
		HoldsList hl = HoldsAgent.getActivityHolds(type, typeid);
		boolean dh = false;
		if (type.equalsIgnoreCase("activity")) {
			dh = Modules.disableOnHold("review");
		}
		for (int i=0; i<all.size(); i++) {
			ObjGroupVO vo = all.get(i);

			if (dh) {
				RolesVO r = LkupCache.getReviewRoles(Operator.toInt(vo.getGroupid()));
				vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
			}
			else {
				RolesVO r = LkupCache.getReviewRoles(Operator.toInt(vo.getGroupid()));
				vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}
			if (vo.isRead()) { result.add(vo); }
		}
		return result;
	}

	public static ArrayList<ObjGroupVO> unCachedReviews(String type, int typeid) {
		Logger.info("Retrieving non cached version of reviews");
		ArrayList<ObjGroupVO> result =  new ArrayList<ObjGroupVO>();
		ComboReviewGroup review = ReviewAgent.getCombos(type, typeid);
		LinkedHashMap<String, ComboReviewList> reviews = review.getList();
		for (Map.Entry<String, ComboReviewList> entry : reviews.entrySet()) {
			ComboReviewList v = entry.getValue();
			ObjGroupVO g = new ObjGroupVO();
			String title = v.getTitle();
			if (InfoAgent.hasContent(title)) {
				g.setContenttype(title);
			}
			g.setGroup(v.getTitle());
			g.setGroupid(Operator.toString(v.getGroupid()));
			g.setType("review");
			g.setComboreview(v);
			result.add(g);
		}
		return result;
	}

	public static ArrayList<ObjGroupVO> resolution(String type, int typeid, Token u) {
		ArrayList<ObjGroupVO> all =  new ArrayList<ObjGroupVO>();
		try {
			all = CsGetCache.getObjArrayCache(type, typeid, "resolution", "info");
		}
		catch (Exception e) {
			all = uncachedResolution(type, typeid);
			CsSaveCache.saveCache(type, typeid, "resolution", "info", all);
		}
		RolesVO r = LkupCache.getModuleRoles("resolution");
		ArrayList<ObjGroupVO> result = new ArrayList<ObjGroupVO>();
		for (int i=0; i<all.size(); i++) {
			ObjGroupVO vo = all.get(i);
			vo.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			if (vo.isRead() || vo.isCreate() || vo.isUpdate() || vo.isDelete()) {
				result.add(vo);
			}
		}
		
		return result;
	}

	public static ArrayList<ObjGroupVO> uncachedResolution(String type, int typeid) {
		Logger.info("Retrieving non cached version of resolution");
		ArrayList<ObjGroupVO> result =  new ArrayList<ObjGroupVO>();
		ArrayList<ResolutionVO> res = ResolutionAgent.getResolutions(type, typeid);
		if (res.size() > 0) {
			ObjGroupVO g = ResolutionFields.summary();
			g.setResolutions(res.toArray(new ResolutionVO[res.size()]));
			result.add(g);
		}
		return result;
	}

	public static ObjGroupVO[] history(String type, int typeid, String module, String option, Token u) {
		ObjGroupVO[] vo = new ObjGroupVO[0];
		RolesVO r = LkupCache.getModuleRoles(module);
		if (u.isAdmin() || r.createAccess(u.getRoles(), u.getNonpublicroles())) {
			vo = Group.history(type, typeid, module, option);
		}
		return vo;
	}

	public static ObjGroupVO id(String type, int typeid, String module, int id, Token u) {
		ObjGroupVO vo = new ObjGroupVO();
		RolesVO r = LkupCache.getModuleRoles(module);
		if (u.isAdmin() || r.createAccess(u.getRoles(), u.getNonpublicroles())) {
			vo = Group.id(type, typeid, module, id);
		}
		return vo;
	}

	// OTHERS

	public static ObjGroupVO[] search(String type, int typeid, String entity) {
		ObjGroupVO[] r = new ObjGroupVO[0];
		return r;
	}
	
}
