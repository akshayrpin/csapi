package csapi.impl.entity;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csshared.vo.TypeInfo;




public class EntityAgent {

	public static TypeInfo getProjectEntity(String type, int typeid) {
		TypeInfo r = new TypeInfo();
		r.setType(type);
		r.setTypeid(typeid);
		if (!type.equalsIgnoreCase("activity") && !type.equalsIgnoreCase("project")) {
			r.setEntity(type);
			r.setEntityid(typeid);
			return r;
		}
		String command = EntitySQL.getEntity(type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r.setEntity(db.getString("ENTITY"));
				r.setProjectid(db.getInt("PROJECT_ID"));
			}
			db.clear();
		}
		return r;
	}

	public static TypeInfo getEntity(String type, int typeid) {
		TypeInfo r = new TypeInfo();
		r.setType(type);
		r.setTypeid(typeid);

		int entityid = -1;
		String entity = "";
		Sage db = new Sage();
		String command = "";
		if (!type.equalsIgnoreCase("activity") && !type.equalsIgnoreCase("project")) {
			entity = type;
			entityid = typeid;
			r.setEntity(entity);
			r.setEntityid(entityid);
		}
		else {
			command = EntitySQL.getEntity(type, typeid);
			if (Operator.hasValue(command)) {
				if (db.query(command) && db.next()) {
					entity = db.getString("ENTITY");
					int projectid = db.getInt("PROJECT_ID");
					String projectnbr = db.getString("PROJECT_NBR");
					int activityid = -1;
					String activitynbr = "";
					if (type.equalsIgnoreCase("activity")) {
						activityid = typeid;
						activitynbr = db.getString("ACT_NBR");
					}
					r.setEntity(entity);
					r.setProjectid(projectid);
					r.setProjectnumber(projectnbr);
					r.setActivityid(activityid);
					r.setActivitynumber(activitynbr);
					command = EntitySQL.getEntityId(entity, type, typeid);
					if (db.query(command) && db.next()) {
						entityid = db.getInt("ID");
						r.setEntityid(entityid);
					}
				}
			}
		}
		if (Operator.hasValue(entity) && entityid > 0) {
			command = EntitySQL.getEntityParents(entity, entityid);
			db.query(command);
			while (db.next()) {
				r.setEntitydescription(db.getString("DESCRIPTION"));
				if (db.equalsIgnoreCase("ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (db.equalsIgnoreCase("PARENT_ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (db.equalsIgnoreCase("GRANDPARENT_ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (r.getParentid() < 1 && db.getInt("PARENT_ID") > 0) {
					r.setParentid(db.getInt("PARENT_ID"));
				}
				if (r.getGrandparentid() < 1 && db.getInt("GRANDPARENT_ID") > 0) {
					r.setGrandparentid(db.getInt("GRANDPARENT_ID"));
				}
				if (db.getInt("CHILD_ID") > 0) {
					r.addChildid(db.getInt("CHILD_ID"));
				}
				if (db.getInt("GRANDCHILD_ID") > 0) {
					r.addGrandchildid(db.getInt("GRANDCHILD_ID"));
				}
			}
		}
		db.clear();
		return r;
	}

	public static TypeInfo getEntity(String type, String ref) {
		TypeInfo r = new TypeInfo();
		if (!Operator.hasValue(ref)) { return r; }
		if (!type.equalsIgnoreCase("activity") && !type.equalsIgnoreCase("project")) {
			return r;
		}
		r.setType(type);

		int entityid = -1;
		String entity = "";
		Sage db = new Sage();
		String command = "";
		command = EntitySQL.getEntity(type, ref);
		if (Operator.hasValue(command)) {
			if (db.query(command) && db.next()) {
				int typeid = db.getInt("TYPEID");
				entity = db.getString("ENTITY");
				int projectid = db.getInt("PROJECT_ID");
				String projectnbr = db.getString("PROJECT_NBR");
				int activityid = -1;
				String activitynbr = "";
				if (type.equalsIgnoreCase("activity")) {
					activityid = typeid;
					activitynbr = db.getString("ACT_NBR");
				}
				r.setEntity(entity);
				r.setProjectid(projectid);
				r.setProjectnumber(projectnbr);
				r.setActivityid(activityid);
				r.setActivitynumber(activitynbr);
				command = EntitySQL.getEntityId(entity, type, typeid);
				if (db.query(command) && db.next()) {
					entityid = db.getInt("ID");
					r.setEntityid(entityid);
				}
			}
		}
		if (Operator.hasValue(entity) && entityid > 0) {
			command = EntitySQL.getEntityParents(entity, entityid);
			db.query(command);
			while (db.next()) {
				r.setEntitydescription(db.getString("DESCRIPTION"));
				if (db.equalsIgnoreCase("ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (db.equalsIgnoreCase("PARENT_ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (db.equalsIgnoreCase("GRANDPARENT_ISPUBLIC", "N")) {
					r.setIspublic(false);
				}
				if (r.getParentid() < 1 && db.getInt("PARENT_ID") > 0) {
					r.setParentid(db.getInt("PARENT_ID"));
				}
				if (r.getGrandparentid() < 1 && db.getInt("GRANDPARENT_ID") > 0) {
					r.setGrandparentid(db.getInt("GRANDPARENT_ID"));
				}
				if (db.getInt("CHILD_ID") > 0) {
					r.addChildid(db.getInt("CHILD_ID"));
				}
				if (db.getInt("GRANDCHILD_ID") > 0) {
					r.addGrandchildid(db.getInt("GRANDCHILD_ID"));
				}
			}
		}
		db.clear();
		return r;
	}

	public static int getProjectEntityId(String entity, String type, int typeid) {
		if (Operator.equalsIgnoreCase(entity, type)) { return typeid; }
		int r = -1;
		String command = EntitySQL.getEntityId(entity, type, typeid);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
			}
			db.clear();
		}
		return r;
	}














}















