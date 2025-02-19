package csapi.impl.resolution;

import java.util.ArrayList;
import java.util.HashMap;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.FileUtil;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.entity.EntityAgent;
import csapi.impl.project.ProjectAgent;
import csapi.impl.project.ProjectSQL;
import csapi.utils.CsApiConfig;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsReflect;
import csapi.utils.Excel;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResolutionDetailVO;
import csshared.vo.ResolutionVO;
import csshared.vo.ResolutionsVO;
import csshared.vo.ResponseVO;
import csshared.vo.TypeInfo;



public class ResolutionAgent {

	public static ResponseVO save(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		DataVO d = DataVO.toDataVO(vo);
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		int groupid = Operator.toInt(vo.getGroupid());
		int userid = u.getId();
		String username = u.getUsername();
		String ip = u.getIp();

		if (groupid > 0) {
			if (id < 1) {
				String restype = d.getString("TYPE");
				if (Operator.hasValue(restype)) {
					r = addDetail(r, type, typeid, groupid, d.getString("PART"), d.getString("PART_TITLE"), d.getString("DESCRIPTION"), d.getString("DATE"), d.getString("EXP_DATE"), d.getInt("STATUS_ID"), d.getString("TYPE"), username, userid, ip);
				}
				else {
					String number = d.getString("RESOLUTION_NUMBER");
					String title = d.getString("TITLE");
					if (Operator.hasValue(title)) {
						r = editResolution(r, groupid, number, title, userid, ip);
					}
				}
			}
			else {
				r = editDetail(r, groupid, id, d.getString("PART"), d.getString("PART_TITLE"), d.getString("DESCRIPTION"), d.getString("DATE"), d.getString("EXP_DATE"), d.getInt("STATUS_ID"), d.getString("TYPE"), username, userid, ip);
			}
		}
		else {
			r = addResolution(r, type, typeid, d.getString("RESOLUTION_NUMBER"), d.getString("TITLE"), d.getString("PART"), d.getString("PART_TITLE"), d.getString("DESCRIPTION"), d.getString("DATE"), d.getString("EXP_DATE"), d.getInt("STATUS_ID"), d.getString("TYPE"), username, userid, ip);
		}
		return r;
	}

	public static ResponseVO addResolution(ResponseVO vo, String type, int typeid, String resnum, String title, String part, String name, String description, String resdate, String expdate, int statusid, String restype, String username, int userid, String ip) {
		if (Operator.hasValue(resnum) || Operator.hasValue(title)) {
			Timekeeper d = new Timekeeper();
			d.setDate(resdate);

			TypeInfo m = EntityAgent.getEntity(type, typeid);
			String ref = type;
			int refid = typeid;
			String refnum = Operator.toString(refid);
			int detailid = -1;

			if (Operator.equalsIgnoreCase(restype, "permanent")) {
				ref = m.getEntity();
				refid = m.getEntityid();
				refnum = Operator.toString(refid);
			}
			else {
// commented out to disable resolution attachment on activity. Uncomment if resolutions is allowed to be added on the activity level.
//				if (type.equalsIgnoreCase("activity")) {
//					ref = "activity";
//					refid = m.getActivityid();
//					refnum = m.getActivitynumber();
//				}
//				else {
					ref = "project";
					refid = m.getProjectid();
					refnum = m.getProjectnumber();
//				}
			}

			String command = ResolutionSQL.addResolution(resnum, title, userid, ip);
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				int resid = db.getInt("ID");
				if (resid > 0) {
					command = ResolutionSQL.addDetail(resid, part, name, description, d, expdate, statusid, ref, refid, refnum, userid, ip);
					if (db.query(command) && db.next()) {
						detailid = db.getInt("ID");
						command = ResolutionSQL.updateAdopted(resid);
						db.update(command);

						updateRef(vo, ref, refid, m, "", restype, detailid, userid, ip);
						vo.setMessagecode("cs200");
					}
					vo.setId(resid);
				}
				else {
					//error
				}
			}
			db.clear();
			if (detailid > 0) {
				int histid = addHistory(detailid, username, userid, ip);
				CsReflect.addHistory(ref, refid, "resolution", histid, "add");
			}
		}
		return vo;
	}

	public static ResponseVO editResolution(ResponseVO vo, int resid, String resnum, String title, int userid, String ip) {
		if (resid > 0) {
			String command = ResolutionSQL.editResolution(resid, resnum, title, userid, ip);
			Sage db = new Sage();
			if (db.update(command)) {
				command = ResolutionSQL.updateAdopted(resid);
				db.update(command);
			}
			else {
				//error
			}
			db.clear();
		}
		return vo;
	}

	public static ResponseVO editDetail(ResponseVO vo, int resid, int detailid, String part, String name, String description, String resdate, String expdate, int statusid, String restype, String username, int userid, String ip) {
		String origref = "";
		String type = "";
		int typeid = -1;
		String command = ResolutionSQL.getDetail(detailid);
		Sage db = new Sage();
		db.query(command);
		if (db.next()) {
			origref = db.getString("REF");
			type = db.getString("REF");
			typeid = db.getInt("REF_ID");
		}

		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String ref = type;
		int refid = typeid;
		String refnum = Operator.toString(refid);

		if (Operator.equalsIgnoreCase(restype, "permanent")) {
			ref = m.getEntity();
			refid = m.getEntityid();
			refnum = Operator.toString(refid);
		}
		else {
			// commented out to disable resolution attachment on activity. Uncomment if resolutions is allowed to be added on the activity level.
			// if (type.equalsIgnoreCase("activity")) {
			//	ref = "activity";
			//	refid = m.getActivityid();
			//	refnum = m.getActivitynumber();
			// }
			// else {
				ref = "project";
				refid = m.getProjectid();
				refnum = m.getProjectnumber();
			// }
		}

		updateRef(vo, type, typeid, m, origref, restype, detailid, userid, ip);
		Timekeeper d = new Timekeeper();
		d.setDate(resdate);
		command = ResolutionSQL.editDetail(detailid, part, name, description, d, expdate, statusid, ref, refid, refnum, userid, ip);
		if (db.update(command)) {
			command = ResolutionSQL.updateAdopted(resid);
			db.update(command);
			int histid = addHistory(detailid, username, userid, ip);
			CsReflect.addHistory(ref, refid, "resolution", histid, "update");
		}
		else {
			//error
		}
		db.clear();
		return vo;
	}

	public static ResponseVO addDetail(ResponseVO vo, String type, int typeid, int resid, String part, String name, String description, String resdate, String expdate, int statusid, String restype, String username, int userid, String ip) {
		String origref = "";
		TypeInfo m = EntityAgent.getEntity(type, typeid);
		String ref = type;
		int refid = typeid;
		String refnum = Operator.toString(refid);

		if (Operator.equalsIgnoreCase(restype, "permanent")) {
			ref = m.getEntity();
			refid = m.getEntityid();
			refnum = Operator.toString(refid);
		}
		else {
			// commented out to disable resolutions on activity. Uncomment if resolutions is allowed to be added on the activity level.
			// if (type.equalsIgnoreCase("activity")) {
			//	ref = "activity";
			//	refid = m.getActivityid();
			//	refnum = m.getActivitynumber();
			// }
			// else {
				ref = "project";
				refid = m.getProjectid();
				refnum = m.getProjectnumber();
			// }
		}

		Timekeeper d = new Timekeeper();
		d.setDate(resdate);

		String command = ResolutionSQL.addDetail(resid, part, name, description, d, expdate, statusid, ref, refid, refnum, userid, ip);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			int detailid = db.getInt("ID");
			vo = updateRef(vo, type, typeid, m, origref, restype, detailid, userid, ip);
			command = ResolutionSQL.updateAdopted(resid);
			db.update(command);
			int histid = addHistory(detailid, username, userid, ip);
			CsReflect.addHistory(ref, refid, "resolution", histid, "add");
		}
		else {
			vo.addError("A database error was encountered while saving resolution part.");
		}
		db.clear();
		return vo;
	}

	public static ResponseVO updateRef(ResponseVO vo, String type, int typeid, TypeInfo entity, String origref, String restype, int detailid, int userid, String ip) {
		if (Operator.equalsIgnoreCase(restype, "temporary") && Operator.hasValue(origref) && (Operator.equalsIgnoreCase(origref, "project") || Operator.equalsIgnoreCase(origref, "activity"))) {
		}
		else if (Operator.equalsIgnoreCase(restype, "permanent") && Operator.hasValue(origref) && !Operator.equalsIgnoreCase(origref, "project") && !Operator.equalsIgnoreCase(origref, "activity")) {
		}
		else if (Operator.equalsIgnoreCase(restype, origref)) {
		}
		else {
			Sage db = new Sage();
			String reference = "";
			String command = "";
			int refid = -1;
			if (Operator.hasValue(origref)) {
				command = ResolutionSQL.deleteRef(origref, detailid, userid, ip);
				db.update(command);
			}

			if (Operator.equalsIgnoreCase(restype, "temporary")) {
				// commented because resolution are disabled from the activity level. Uncomment to enable resolutions on activity level.
				// if (Operator.equalsIgnoreCase(type, "activity")) {
				//	reference = "activity";
				//	refid = entity.getActivityid();
				// }
				// else {
					reference = "project";
					refid = entity.getProjectid();
				// }
			}
			else if (Operator.equalsIgnoreCase(restype, "permanent")) {
				reference = entity.getEntity();
				refid = entity.getEntityid();
			}

			if (Operator.hasValue(reference) && refid > 0) {
				command = ResolutionSQL.addRef(reference, refid, detailid, userid, ip);
				if (db.update(command)) {
					
				}
				else {
					//error
				}
			}
			db.clear();
		}

		return vo;

	}

	public static boolean delete(RequestVO vo, Token u) {
		boolean r = false;
		int detailid = Operator.toInt(vo.getId());
		String command = ResolutionSQL.getDetail(detailid);
		Sage db = new Sage();
		if (db.query(command) && db.next()) {
			String ref = db.getString("REF");
			int refid = db.getInt("REF_ID");
			command = ResolutionSQL.deleteRef(ref, detailid, u.getId(), u.getIp());
			db.update(command);
			command = ResolutionSQL.deleteDetail(detailid, u.getId(), u.getIp());
			r = db.update(command);
			int histid = addHistory(detailid, u.getUsername(), u.getId(), u.getIp());
			CsReflect.addHistory(ref, refid, "resolution", histid, "delete");
		}
		db.clear();
		return r;
	}

	public static boolean comply(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = comply(type, typeid, id, u.getUsername(), u.getId(), u.getIp());
		return r;
	}

	public static boolean comply(String type, int typeid, int resdetailid, String username, int userid, String ip) {
		boolean r = false;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			String command = ResolutionSQL.deleteCompliance(resdetailid, projectid);
			Sage db = new Sage();
			db.update(command);
			command = ResolutionSQL.addCompliance(resdetailid, projectid, userid, ip);
			r = db.update(command);
			db.clear();
			int histid = addHistory(resdetailid, username, userid, ip);
			CsReflect.addHistory(type, typeid, "resolution", histid, "permit comply");
		}
		return r;
	}

	public static boolean appcomply(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = appcomply(type, typeid, id, u.getUsername(), u.getId(), u.getIp());
		return r;
	}

	public static boolean appcomply(String type, int typeid, int resdetailid, String username, int userid, String ip) {
		boolean r = false;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			String command = ResolutionSQL.deleteApplicationCompliance(resdetailid, projectid);
			Sage db = new Sage();
			db.update(command);
			command = ResolutionSQL.addApplicationCompliance(resdetailid, projectid, userid, ip);
			r = db.update(command);
			db.clear();
			int histid = addHistory(resdetailid, username, userid, ip);
			CsReflect.addHistory(type, typeid, "resolution", histid, "application comply");
		}
		return r;
	}

	public static boolean complyall(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = complyall(type, typeid, id, u.getId(), u.getIp());
		return r;
	}

	public static boolean complyall(String type, int typeid, int resid, int userid, String ip) {
		boolean r = true;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			Sage db = new Sage();
			String command = ResolutionSQL.getResolutions(type, typeid, resid);
			if (db.query(command)) {
				Sage db2 = new Sage();
				while (db.next()) {
					int resdetid = db.getInt("ID");
					command = ResolutionSQL.deleteCompliance(resdetid, projectid);
					db2.update(command);
					command = ResolutionSQL.addCompliance(resdetid, projectid, userid, ip);
					db2.update(command);
				}
				db2.clear();
			}
			db.clear();
		}
		return r;
	}

	public static boolean appcomplyall(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = appcomplyall(type, typeid, id, u.getId(), u.getIp());
		return r;
	}

	public static boolean appcomplyall(String type, int typeid, int resid, int userid, String ip) {
		boolean r = true;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			Sage db = new Sage();
			String command = ResolutionSQL.getResolutions(type, typeid, resid);
			if (db.query(command)) {
				Sage db2 = new Sage();
				while (db.next()) {
					int resdetid = db.getInt("ID");
					command = ResolutionSQL.deleteApplicationCompliance(resdetid, projectid);
					db2.update(command);
					command = ResolutionSQL.addApplicationCompliance(resdetid, projectid, userid, ip);
					db2.update(command);
				}
				db2.clear();
			}
			db.clear();
		}
		return r;
	}

	public static boolean uncomply(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = uncomply(type, typeid, id, u.getUsername(), u.getId(), u.getIp());
		return r;
	}

	public static boolean uncomply(String type, int typeid, int resdetailid, String username, int userid, String ip) {
		boolean r = false;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			String command = ResolutionSQL.deleteCompliance(resdetailid, projectid);
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
			int histid = addHistory(resdetailid, username, userid, ip);
			CsReflect.addHistory(type, typeid, "resolution", histid, "permit uncomply");
		}
		return r;
	}


	public static boolean appuncomply(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int id = Operator.toInt(vo.getId());
		r = appuncomply(type, typeid, id, u.getUsername(), u.getId(), u.getIp());
		return r;
	}

	public static boolean appuncomply(String type, int typeid, int resdetailid, String username, int userid, String ip) {
		boolean r = false;
		int projectid = ProjectAgent.getProjectId(type, typeid);
		if (projectid > 0) {
			String command = ResolutionSQL.deleteApplicationCompliance(resdetailid, projectid);
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
			int histid = addHistory(resdetailid, username, userid, ip);
			CsReflect.addHistory(type, typeid, "resolution", histid, "application uncomply");
		}
		return r;
	}


	public static ArrayList<ResolutionVO> getResolutions(String type, int typeid) {
		ResolutionsVO r = new ResolutionsVO();
		if (Operator.hasValue(type) && typeid > 0) {
			String command = ResolutionSQL.getResolutions(type, typeid, -1);
			Sage db = new Sage();
			db.query(command);
			while (db.next()) {
				int resid = db.getInt("RESOLUTION_ID");
				String number = db.getString("RESOLUTION_NUMBER");
				String title = db.getString("TITLE");
				String adopted = db.getString("ADOPTED_DATE");
				String creator = db.getString("RESOLUTION_CREATOR");
				String updater = db.getString("RESOLUTION_UPDATER");
				int createdby = db.getInt("RESOLUTION_CREATED_BY");
				int updatedby = db.getInt("RESOLUTION_UPDATED_BY");
				String createddate = db.getString("RESOLUTION_CREATED_DATE");
				String updateddate = db.getString("RESOLUTION_UPDATED_DATE");
				ResolutionDetailVO d = new ResolutionDetailVO();
				d.setId(db.getInt("ID"));
				d.setPart(db.getString("PART"));
				d.setName(db.getString("NAME"));
				d.setRef(db.getString("REF"));
				d.setRefid(db.getInt("REF_ID"));
				d.setRefnum(db.getString("REF_NUMBER"));
				d.setDescription(db.getString("DESCRIPTION"));
				d.setDate(db.getString("RESOLUTION_DATE"));
				d.setExpdate(db.getString("EXP_DATE"));
				d.setStatusid(db.getInt("LKUP_RESOLUTION_STATUS_ID"));
				d.setStatus(db.getString("STATUS"));
				d.setComplieddate(db.getString("COMPLIANCE_DATE"));
				d.setAppcomplieddate(db.getString("APPLICATION_COMPLIANCE_DATE"));
				d.setApproved(db.getString("APPROVED"));
				d.setUnapproved(db.getString("UNAPPROVED"));
				d.setFinaled(db.getString("FINAL"));
				d.setCreator(db.getString("CREATOR"));
				d.setUpdater(db.getString("UPDATER"));
				d.setCreatedby(db.getInt("CREATED_BY"));
				d.setUpdatedby(db.getInt("UPDATED_BY"));
				d.setCreateddate(db.getString("CREATED_DATE"));
				d.setUpdateddate(db.getString("UPDATED_DATE"));
				r.addResolutionDetail(resid, number, title, adopted, createdby, creator, updatedby, updater, createddate, updateddate, d);
			}
			db.clear();
		}
		return r.array();
	}

	public static ResolutionVO getResolution(String type, int typeid, int resid, int resdetailid) {
		ResolutionVO vo = new ResolutionVO();
		if (resid > 0) {
			String command = ResolutionSQL.getResolution(type, typeid, resid);
			Sage db = new Sage();
			db.query(command);
			while (db.next()) {
				vo.setId(db.getInt("RESOLUTION_ID"));
				vo.setNumber(db.getString("RESOLUTION_NUMBER"));
				vo.setTitle(db.getString("TITLE"));
				vo.setAdopted(db.getString("ADOPTED_DATE"));
				vo.setCreator(db.getString("RESOLUTION_CREATOR"));
				vo.setUpdater(db.getString("RESOLUTION_UPDATER"));
				vo.setCreatedby(db.getInt("RESOLUTION_CREATED_BY"));
				vo.setUpdatedby(db.getInt("RESOLUTION_UPDATED_BY"));
				vo.setCreateddate(db.getString("RESOLUTION_CREATED_DATE"));
				vo.setUpdateddate(db.getString("RESOLUTION_UPDATED_DATE"));
				int detailid = db.getInt("ID");
				if (detailid > 0) {
					ResolutionDetailVO d = new ResolutionDetailVO();
					d.setId(db.getInt("ID"));
					d.setResolutionid(db.getInt("RESOLUTION_ID"));
					d.setDetailid(db.getInt("RESOLUTION_DETAIL_ID"));
					d.setRef(db.getString("REF"));
					d.setRefid(db.getInt("REF_ID"));
					d.setRefnum(db.getString("REF_NUMBER"));
					d.setPart(db.getString("PART"));
					d.setName(db.getString("NAME"));
					d.setDescription(db.getString("DESCRIPTION"));
					d.setDate(db.getString("RESOLUTION_DATE"));
					d.setExpdate(db.getString("EXP_DATE"));
					d.setStatusid(db.getInt("LKUP_RESOLUTION_STATUS_ID"));
					d.setStatus(db.getString("STATUS"));
					d.setComplieddate(db.getString("COMPLIANCE_DATE"));
					d.setAppcomplieddate(db.getString("APPLICATION_COMPLIANCE_DATE"));
					d.setApproved(db.getString("APPROVED"));
					d.setFinaled(db.getString("FINAL"));
					d.setUnapproved(db.getString("UNAPPROVED"));
					d.setCreator(db.getString("CREATOR"));
					d.setUpdater(db.getString("UPDATER"));
					d.setCreatedby(db.getInt("CREATED_BY"));
					d.setUpdatedby(db.getInt("UPDATED_BY"));
					d.setCreateddate(db.getString("CREATED_DATE"));
					d.setUpdateddate(db.getString("UPDATED_DATE"));
					vo.addDetail(d);
					if (resdetailid > 0 && db.getInt("ID") == resdetailid) {
						d.addHistory(d.duplicate());
						String hist = ResolutionSQL.getHistory(resdetailid);
						if (Operator.hasValue(hist)) {
							Sage db2 = new Sage();
							db2.query(hist);
							while (db2.next()) {
								int histid = db2.getInt("ID");
								if (histid > 0) {
									ResolutionDetailVO hd = new ResolutionDetailVO();
									hd.setId(db2.getInt("ID"));
									hd.setResolutionid(db2.getInt("RESOLUTION_ID"));
									hd.setDetailid(db2.getInt("RESOLUTION_DETAIL_ID"));
									hd.setRef(db2.getString("REF"));
									hd.setRefid(db2.getInt("REF_ID"));
									hd.setRefnum(db2.getString("REF_NUMBER"));
									hd.setPart(db2.getString("PART"));
									hd.setName(db2.getString("NAME"));
									hd.setDescription(db2.getString("DESCRIPTION"));
									hd.setDate(db2.getString("RESOLUTION_DATE"));
									hd.setExpdate(db2.getString("EXP_DATE"));
									hd.setStatusid(db2.getInt("LKUP_RESOLUTION_STATUS_ID"));
									hd.setStatus(db2.getString("STATUS"));
									hd.setComplieddate(db2.getString("COMPLIANCE_DATE"));
									hd.setApproved(db2.getString("APPROVED"));
									hd.setUnapproved(db2.getString("UNAPPROVED"));
									hd.setFinaled(db2.getString("FINAL"));
									hd.setCreator(db2.getString("CREATOR"));
									hd.setUpdater(db2.getString("UPDATER"));
									hd.setCreatedby(db2.getInt("CREATED_BY"));
									hd.setUpdatedby(db2.getInt("UPDATED_BY"));
									hd.setCreateddate(db2.getString("CREATED_DATE"));
									hd.setUpdateddate(db2.getString("UPDATED_DATE"));
									d.addHistory(hd);
								}
							}
							db2.clear();
						}
						vo.setDetail(d);
					}
				}
			}
			db.clear();
		}
		return vo;
	}

	public static boolean updateMulti(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int resid = Operator.toInt(vo.getGroupid());
		DataVO d = DataVO.toDataVO(vo);
		Timekeeper dt = new Timekeeper();
		dt.setDate(d.getString("DATE"));
		String apply = d.getString("APPLY_TO_ALL");
		String command = "";
		if (Operator.equalsIgnoreCase(apply, "N")) {
			command = ResolutionSQL.updateMultiLevel(type, typeid, resid, d.getInt("STATUS_ID"), dt, u.getId(), u.getIp());
		}
		else {
			command = ResolutionSQL.updateMultiAll(resid, d.getInt("STATUS_ID"), dt, u.getId(), u.getIp());
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				r = true;
				int count = 0;
				int[] detailids = new int[db.size()];
				while (db.next()) {
					int resdetailid = db.getInt("ID");
					detailids[count] = resdetailid;
					count++;
				}
				command = ResolutionSQL.updateAdopted(resid);
				db.update(command);
				addHistory(detailids, "update", u.getUsername(), u.getId(), u.getIp());
			}
			db.clear();
		}
		return r;
	}

	public static boolean expireMulti(RequestVO vo, Token u) {
		boolean r = false;
		String type = vo.getType();
		int typeid = vo.getTypeid();
		int resid = Operator.toInt(vo.getGroupid());
		DataVO d = DataVO.toDataVO(vo);
		String apply = d.getString("APPLY_TO_ALL");
		String command = "";
		if (Operator.equalsIgnoreCase(apply, "N")) {
			command = ResolutionSQL.expireMultiLevel(type, typeid, resid, d.getString("EXP_DATE"), u.getId(), u.getIp());
		}
		else {
			command = ResolutionSQL.expireMultiAll(resid, d.getString("EXP_DATE"), u.getId(), u.getIp());
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command)) {
				r = true;
				int count = 0;
				int[] detailids = new int[db.size()];
				while (db.next()) {
					int resdetailid = db.getInt("ID");
					detailids[count] = resdetailid;
					count++;
				}
				addHistory(detailids, "update", u.getUsername(), u.getId(), u.getIp());
			}
			db.clear();
		}
		return r;
	}

	public static ResponseVO importResolution(RequestVO vo, Token u) {
		ResponseVO r = new ResponseVO();
		DataVO d = DataVO.toDataVO(vo);
		String f = d.get("FILE");
		if (!Operator.hasValue(f)) {
			r.addError("File not found");
			return r;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(Config.getString("files.storage_path")).append(f);
		String file = sb.toString();
		ArrayList<HashMap<String, String>> al = Excel.read(file);
		FileUtil.deleteFile(file);
		Sage db = new Sage();

		String type = vo.getType();
		int typeid = vo.getTypeid();

		HashMap<String, String> statuses = new HashMap<String, String>();
		String command = ResolutionSQL.getStatus();
		db.query(command);
		while (db.next()) {
			statuses.put(db.getString("STATUS").toLowerCase(), db.getString("ID"));
		}

		String numberval = "";
		int resid = -1;
		int size = al.size();
		for (int i=0; i<size; i++) {
			HashMap<String, String> map = al.get(i);

			String ref = "";
			int refid = -1;
			String refnum = "";

			int projectid = -1;
			String projectnum = "";

			String status = map.get("STATUS");

			int statusid = Operator.toInt(statuses.get(status.toLowerCase()));
			if (statusid > 0) {

				TypeInfo entity = new TypeInfo();
				String activity = map.get("ACTIVITY_NUMBER");
				String project = map.get("PROJECT_NUMBER");
				String lsoid = map.get("LSO_ID");
				String restype = map.get("TYPE");

				if (restype.equalsIgnoreCase("permanent")) { restype = "permanent"; }
				else { restype = "temporary"; }

				if (Operator.hasValue(activity)) {
					command = ActivitySQL.getProject(activity);
					if (db.query(command) && db.next()) {
						projectid = db.getInt("ID");
						projectnum = db.getString("PROJECT_NBR");
					}
				}
				else if (Operator.hasValue(project)) {
					command = ProjectSQL.getProject(project);
					if (db.query(command) && db.next()) {
						projectid = db.getInt("ID");
						projectnum = db.getString("PROJECT_NBR");
					}
				}
				else if (type.equalsIgnoreCase("activity")) {
					command = ActivitySQL.getProject(typeid);
					if (db.query(command) && db.next()) {
						projectid = db.getInt("ID");
						projectnum = db.getString("PROJECT_NBR");
					}
				}
				else if (type.equalsIgnoreCase("project")) {
					command = ProjectSQL.getProject(typeid);
					if (db.query(command) && db.next()) {
						projectid = db.getInt("ID");
						projectnum = db.getString("PROJECT_NBR");
					}
				}

				if (projectid > 0) {
					entity = EntityAgent.getEntity("project", projectid);
					if (restype.equalsIgnoreCase("temporary")) {
						ref = "project";
						refid = projectid;
						refnum = projectnum;
					}
					else {
						entity = EntityAgent.getEntity("project", projectid);
						ref = entity.getEntity();
						refid = entity.getEntityid();
						refnum = Operator.toString(refid);
					}
				}
				else {
					entity = EntityAgent.getEntity(type, typeid);
					ref = entity.getEntity();
					refid = entity.getEntityid();
					refnum = Operator.toString(refid);
				}
				
				if (Operator.hasValue(ref) && refid > 0) {

					String number = map.get("RESOLUTION_NUMBER");
					String title = map.get("TITLE");
					String part = map.get("PART");
					String name = map.get("PART_TITLE");
					String description = map.get("DESCRIPTION");
					String comment = map.get("COMMENT");
					String rdate = map.get("DATE");
					String expdate = map.get("EXP_DATE");
	
					if (!Operator.equalsIgnoreCase(numberval, number)) {
						command = ResolutionSQL.getResolution(ref, refid, number, entity);
						Logger.highlight(command);
						if (db.query(command) && db.next()) {
							resid = db.getInt("ID");
						}
						else {
							command = ResolutionSQL.addResolution(number, title, u.getId(), u.getIp());
							if (db.query(command) && db.next()) {
								resid = db.getInt("ID");
							}
						}
						numberval = number;
					}
					Timekeeper resdate = new Timekeeper();
					if (Operator.hasValue(rdate)) {
						resdate.setDate(rdate);
					}
	
					command = ResolutionSQL.addDetail(resid, part, name, description, resdate, expdate, statusid, ref, refid, refnum, u.getId(), u.getIp());
					if (db.query(command) && db.next()) {
						int detailid = db.getInt("ID");
						command = ResolutionSQL.updateAdopted(resid);
						db.update(command);
						updateRef(r, ref, refid, entity, "", restype, detailid, u.getId(), u.getIp());
						int histid = addHistory(detailid, u.getUsername(), u.getId(), u.getIp());
						CsReflect.addHistory(ref, refid, "resolution", histid, "import");
						CsDeleteCache.deleteCache(ref, refid, "resolution");
						CsDeleteCache.deleteChildCache(ref, refid, "resolution");
						CsDeleteCache.deleteParentCache(ref, refid, "resolution");
					}
				}
			}
		}
		db.clear();
		return r;
	}

	public static int addHistory(int detailid, String username, int userid, String ip) {
		if (detailid < 1) { return -1; }
		int r = -1;
		String command = ResolutionSQL.getDetail(detailid);
		if (Operator.hasValue(command)) {
			int resolutionid = -1;
			int resolutiondetailid = -1;
			String resolutionnumber = "";
			String title = "";
			String adopteddate = "";
			String part = "";
			String name = "";
			String description = "";
			String resolutiondate = "";
			String status = "";
			int lkupresolutionstatusid = -1;
			String ref = "";
			int refid = -1;
			String refnumber = "";
			String expdate = "";
			String active = "";
			int createdby = -1;
			String createddate = "";
			String updated = "";
			int updatedby = -1;
			String updateddate = "";
			String createdip = "";
			String updatedip = "";
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				resolutionid = db.getInt("RESOLUTION_ID");
				resolutiondetailid = db.getInt("RESOLUTION_DETAIL_ID");
				resolutionnumber = db.getString("RESOLUTION_NUMBER");
				title = db.getString("TITLE");
				adopteddate = db.getString("ADOPTED_DATE");
				part = db.getString("PART");
				name = db.getString("NAME");
				description = db.getString("DESCRIPTION");
				resolutiondate = db.getString("RESOLUTION_DATE");
				status = db.getString("STATUS");
				lkupresolutionstatusid = db.getInt("LKUP_RESOLUTION_STATUS_ID");
				ref = db.getString("REF");
				refid = db.getInt("REF_ID");
				refnumber = db.getString("REF_NUMBER");
				expdate = db.getString("EXP_DATE");
				active = db.getString("ACTIVE");
				createdby = db.getInt("CREATED_BY");
				createddate = db.getString("CREATED_DATE");
				if (Operator.hasValue(username)) {
					updated = username;
					updatedby = userid;
					updatedip = ip;
					updateddate = new Timekeeper().getString("FULLDATECODE");
				}
				else {
					updated = db.getString("UPDATED");
					updatedby = db.getInt("UPDATED_BY");
					updatedip = db.getString("UPDATED_IP");
					updateddate = db.getString("UPDATED_DATE");
				}
				createdip = db.getString("CREATED_IP");
			}
			db.clear();
			if (resolutionid > 0) {
				r = addHistory(resolutionid, resolutiondetailid, resolutionnumber, title, adopteddate, part, name, description, resolutiondate, status, lkupresolutionstatusid, ref, refid, refnumber, expdate, active, createdby, createddate, updated, updatedby, updateddate, createdip, updatedip);
			}
		}
		return r;
	}

	public static boolean addHistory(int[] detailids, String action, String username, int userid, String ip) {
		if (!Operator.hasValue(detailids)) { return false; }
		boolean r = false;
		String command = ResolutionSQL.getDetails(detailids);
		if (Operator.hasValue(command)) {
			int resolutionid = -1;
			int resolutiondetailid = -1;
			String resolutionnumber = "";
			String title = "";
			String adopteddate = "";
			String part = "";
			String name = "";
			String description = "";
			String resolutiondate = "";
			String status = "";
			int lkupresolutionstatusid = -1;
			String ref = "";
			int refid = -1;
			String refnumber = "";
			String expdate = "";
			String active = "";
			int createdby = -1;
			String createddate = "";
			String updated = "";
			int updatedby = -1;
			String updateddate = "";
			String createdip = "";
			String updatedip = "";
			Sage db = new Sage();
			Sage db2 = new Sage(CsApiConfig.getHistorySource());
			db.query(command);
			while (db.next()) {
				r = true;
				resolutionid = db.getInt("RESOLUTION_ID");
				resolutiondetailid = db.getInt("RESOLUTION_DETAIL_ID");
				resolutionnumber = db.getString("RESOLUTION_NUMBER");
				title = db.getString("TITLE");
				adopteddate = db.getString("ADOPTED_DATE");
				part = db.getString("PART");
				name = db.getString("NAME");
				description = db.getString("DESCRIPTION");
				resolutiondate = db.getString("RESOLUTION_DATE");
				status = db.getString("STATUS");
				lkupresolutionstatusid = db.getInt("LKUP_RESOLUTION_STATUS_ID");
				ref = db.getString("REF");
				refid = db.getInt("REF_ID");
				refnumber = db.getString("REF_NUMBER");
				expdate = db.getString("EXP_DATE");
				active = db.getString("ACTIVE");
				createdby = db.getInt("CREATED_BY");
				createddate = db.getString("CREATED_DATE");
				if (Operator.hasValue(username)) {
					updated = username;
					updatedby = userid;
					updatedip = ip;
					updateddate = new Timekeeper().getString("FULLDATECODE");
				}
				else {
					updated = db.getString("UPDATED");
					updatedby = db.getInt("UPDATED_BY");
					updatedip = db.getString("UPDATED_IP");
					updateddate = db.getString("UPDATED_DATE");
				}
				createdip = db.getString("CREATED_IP");

				int histid = -1;
				command = ResolutionSQL.addHistory(resolutionid, resolutiondetailid, resolutionnumber, title, adopteddate, part, name, description, resolutiondate, status, lkupresolutionstatusid, ref, refid, refnumber, expdate, active, createdby, createddate, updated, updatedby, updateddate, createdip, updatedip);
				if (db2.query(command) && db2.next()) {
					histid = db2.getInt("ID");
					CsReflect.addHistory(ref, refid, "resolution", histid, action);
				}
			}
			db2.clear();
			db.clear();
		}
		return r;
	}

	public static int addHistory(int resolutionid, int resolutiondetailid, String resolutionnumber, String title, String adopteddate, String part, String name, String description, String resolutiondate, String status, int lkupresolutionstatusid, String ref, int refid, String refnumber, String expdate, String active, int createdby, String createddate, String updated, int updatedby, String updateddate, String createdip, String updatedip) {
		int r = -1;
		String command = ResolutionSQL.addHistory(resolutionid, resolutiondetailid, resolutionnumber, title, adopteddate, part, name, description, resolutiondate, status, lkupresolutionstatusid, ref, refid, refnumber, expdate, active, createdby, createddate, updated, updatedby, updateddate, createdip, updatedip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage(CsApiConfig.getHistorySource());
			if (db.query(command) && db.next()) {
				r = db.getInt("ID");
			}
			db.clear();
		}
		return r;
	}







}















