package csapi.utils.objtools;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.LkupCache;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.divisions.DivisionsFields;
import csapi.impl.entity.EntityAgent;
import csapi.impl.general.GeneralAgent;
import csapi.impl.holds.HoldsAgent;
import csapi.impl.parking.ParkingFields;
import csapi.impl.people.PeopleSQL;
import csapi.impl.project.ProjectAgent;
import csapi.utils.CsReflect;
import csshared.vo.HoldsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.TypeInfo;
import csshared.vo.TypeVO;
import csshared.vo.lkup.RolesVO;

public class Types {

	/**
	 * @author aromero
	 */
	public static TypeVO getType(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		return getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
	}

	public static TypeVO getType(String type, int typeid, String entity, String option, Token u) {
		TypeInfo ti = EntityAgent.getEntity(type, typeid);
		TypeVO t = new TypeVO();
		t.setId(typeid);
		t.setType(type);
		t.setTypeid(typeid);
		t.setEntity(entity);
		t.setTypeinfo(ti);

		if (u.isAdmin()) {
			t.setAdmin(true);
			t.setCreate(true);
			t.setRead(true);
			t.setUpdate(true);
			t.setDelete(true);
		}
		else if (Operator.equalsIgnoreCase(type, "project")) {
			if (!ti.isIspublic() && !u.isStaff()) {
				t.setRead(false);
			}
			else {
				int ptypeid = ProjectAgent.getProjectTypeId(typeid);
				RolesVO r = LkupCache.getProjectRoles(ptypeid);
				t.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}
		}
		else if (Operator.equalsIgnoreCase(type, "activity")) {
			if (!ti.isIspublic() && !u.isStaff()) {
				t.setRead(false);
			}
			else {
				boolean issued = ActivityAgent.isPublicIssued(typeid);
				int[] patypeid = ActivityAgent.getProjectAndActivityType(typeid);
				int ptypeid = patypeid[0];
				int atypeid = patypeid[1];
				RolesVO r = LkupCache.getProjectRoles(ptypeid);
				if (r.readAccess(u.getRoles(), u.getNonpublicroles())) {
					r = LkupCache.getActivityRoles(atypeid);
					t.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin(), issued);
				}
				else {
					t.setAdmin(false);
					t.setCreate(false);
					t.setRead(false);
					t.setUpdate(false);
					t.setDelete(false);
				}
			}
		}
		else {
			t.setCreate(true);
			t.setRead(true);
			t.setUpdate(true);
			t.setDelete(true);
		}

		if (t.isRead()) {
			String hold = GeneralAgent.getAlert(type, typeid);
			t.setHold(hold.trim());
			String command = CsReflect.getQuery("type", type, typeid, option);
			if (Operator.hasValue(command)) {
				Sage db = new Sage();
				db.query(command);
				if (db.next()) {
					t.setId(db.getInt("ID"));
					t.setTitle(db.getString("TITLE"));
					t.setSubtitle(db.getString("SUBTITLE"));
					t.setStatus(db.getString("STATUS").trim());
				}
				db.clear();
			}
		}
		return t;
	}

	public static TypeVO getMyType(String entity, String type, String username) {
		String command = PeopleSQL.getUser(username);
		TypeVO t = new TypeVO();
		t.setType("USERS");
		t.setEntity(entity);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			if (db.next()) {
				t.setId(db.getInt("ID"));
				t.setTitle(db.getString("TITLE"));
				t.setSubtitle(db.getString("SUBTITLE"));
			}
			db.clear();
		}
		return t;
	}

	public static TypeVO getDetails(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		TypeVO result = getType(vo);
		ObjGroupVO g = Group.details(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()), vo.getOption(), u);
		g.setAction(vo.getAction());
		Logger.info(vo.getGrouptype()+"-----------------------------------------"+g.getGroup());
		boolean dh = Modules.disableOnHold(vo.getGrouptype());
		if (dh) {
			HoldsList hl = HoldsAgent.getActivityHolds(vo.getType(), vo.getTypeid());
			RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
			g.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
		}
		else {
			RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
			g.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
		}

		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getVertical(String command, RequestVO vo) {
		TypeVO result = getType(vo);
		ObjGroupVO fields = CsReflect.getDetailFields(vo.getType(), vo.getGrouptype());
		ObjGroupVO g = Group.vertical(fields, command);
		g.setAction(vo.getAction());
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getSummary(RequestVO vo) {
		TypeVO result = getType(vo);
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		result.setTools(Modules.tools(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), result.getHold(), u));
		ObjGroupVO[] gs = Groups.summary(vo.getType(), vo.getTypeid(), vo.getOption(), u);
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getInfo(RequestVO vo) {
		TypeVO result = getType(vo);
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		ObjGroupVO[] gs = Groups.info(vo.getType(), vo.getTypeid(), vo.getOption(), u);
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getModules(RequestVO vo) {
		TypeVO result = getType(vo);
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		result.setTools(Modules.tools(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), result.getHold(), u));
		result.setModule("summary", Modules.summary(vo.getType(), vo.getTypeid(), u));
		result.setModule("info", Modules.info(vo.getType(), vo.getTypeid(), u));
		return result;
	}

	public static TypeVO getList(RequestVO vo) {
		TypeVO result = getType(vo);
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		result.setTools(Modules.tools(vo.getEntity(), vo.getEntityid(), vo.getType(), vo.getTypeid(), result.getHold(), u));
		ObjGroupVO g = Group.list(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getGroupid()), vo.getOption(), u);
		if (vo.isViewonly()) {
			g.setEditable(false);
			g.setDeletable(false);
		}
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getMy(RequestVO vo) {
		String username = "";
		String token = vo.getToken();
		String ip = vo.getIp();
		if (!Operator.hasValue(token) || !Operator.hasValue(ip)) { return new TypeVO(); }
		Token t = Token.retrieve(token, ip);
		username = t.getUsername();
		/*if(vo.getGrouptype().equalsIgnoreCase("activity")&& vo.getStart()>=0 && vo.getEnd()>=0){
			return getMy(vo, username);
		}*/
		return getMy(vo.getEntity(), vo.getGrouptype(), username);
	}
	
	
	/*public static TypeVO getMy(RequestVO vo, String username) {
		if (!Operator.hasValue(username)) { return new TypeVO(); }
		TypeVO result = getMyType(vo.getEntity(), vo.getGrouptype(), username);
		ObjGroupVO g = Group.my(grouptype, username);
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}*/

	public static TypeVO getMy(String entity, String grouptype, String username) {
		if (!Operator.hasValue(username)) { return new TypeVO(); }
		TypeVO result = getMyType(entity, grouptype, username);
		ObjGroupVO g = Group.my(grouptype, username);
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getMyActive(RequestVO vo) {
		String username = "";
		String token = vo.getToken();
		String ip = vo.getIp();
		Logger.info("FDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"+token);
		if (!Operator.hasValue(token) || !Operator.hasValue(ip)) { return new TypeVO(); }
		Token t = Token.retrieve(token, ip);
		Logger.info("FDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"+t.getUsername());
		username = t.getUsername();
		String option = vo.getOption();
		return getMyActive(vo.getEntity(), vo.getGrouptype(), username, option);
	}

	public static TypeVO getMyActive(String entity, String grouptype, String username, String option) {
		if (!Operator.hasValue(username)) { return new TypeVO(); }
		TypeVO result = getMyType(entity, grouptype, username);
		ObjGroupVO g = Group.myActive(grouptype, username, option);
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getFull(RequestVO vo) {
		TypeVO result = new TypeVO();
		ObjGroupVO g = Group.full(vo.getType(), vo.getId(), vo.getStartdate(), vo.getEnddate());
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	public static TypeVO getFields(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		if (vo.getGrouptype().equalsIgnoreCase("divisions")) {
			TypeVO result = getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
			ObjGroupVO g = new ObjGroupVO();
			g = DivisionsFields.details(vo.getType(), vo.getTypeid());
			g.setAction(vo.getAction());

			boolean dh = Modules.disableOnHold(vo.getGrouptype());
			if (dh) {
				HoldsList hl = HoldsAgent.getActivityHolds(vo.getType(), vo.getTypeid());
				RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
				g.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
			}
			else {
				RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
				g.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}

			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			result.setGroups(gs);
			return result;
		}
		else{
			TypeVO result = getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
			ObjGroupVO g = new ObjGroupVO();
			g = Group.fields(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()));
			g.setAction(vo.getAction());

			boolean dh = Modules.disableOnHold(vo.getGrouptype());
			if (dh) {
				HoldsList hl = HoldsAgent.getActivityHolds(vo.getType(), vo.getTypeid());
				RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
				g.putRoles(r, u.getRoles(), u.getNonpublicroles(), hl.actOnSignificantHold(), u.isAdmin());
			}
			else {
				RolesVO r = LkupCache.getModuleRoles(vo.getGrouptype());
				g.putRoles(r, u.getRoles(), u.getNonpublicroles(), u.isAdmin());
			}

			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			result.setGroups(gs);
			return result;
		}	
	}
	
	
	public static TypeVO getAddlFields(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		TypeVO result = getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
		if(vo.getEntity().equals("parking")){
			result.setGroups(ParkingFields.addlfields());
		}
		return result;
		
	}

	public static TypeVO getSearchFields(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		TypeVO result = getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
		ObjGroupVO g = new ObjGroupVO();
		g = Group.searchfields(vo.getType(), vo.getTypeid(), vo.getGrouptype(), Operator.toInt(vo.getId()));
		g.setAction(vo.getAction());
		ObjGroupVO[] gs = new ObjGroupVO[1];
		gs[0] = g;
		result.setGroups(gs);
		return result;
	}

	
	
	
}












