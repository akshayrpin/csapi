package csapi.impl.ui;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.LkupCache;
import csapi.impl.activities.ActivitiesAgent;
import csapi.impl.archive.ArchiveAgent;
import csapi.impl.gis.GisAgent;
import csapi.utils.objtools.Groups;
import csshared.utils.ObjMapper;
import csshared.vo.ObjGroupVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.lkup.RolesVO;



public class UiImpl {

	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjGroupVO[] ga = new ObjGroupVO[0];
			ObjectMapper mapper = new ObjectMapper();
			String type = vo.getType();
			int typeid = vo.getTypeid();
			String group = vo.getGroup();
			if (Operator.equalsIgnoreCase(type, "parking")) {
				type = "project";
			}

			if (group.equalsIgnoreCase("custom")) {
				ArrayList<ObjGroupVO> custom = Groups.custom(type, typeid, u);
				if (custom.size() > 0) {
					ga = custom.toArray(new ObjGroupVO[custom.size()]);
				}
			}
			else if (group.equalsIgnoreCase("activities")) {
				//Thread.sleep(10000);
				ga = new ObjGroupVO[1];
				ObjGroupVO g = ActivitiesAgent.summary(type, typeid, -1, vo.getOption(), u);
				ga[0] = g;
			}
			else if (group.equalsIgnoreCase("archive")) {
				//Thread.sleep(5000);
				ga = new ObjGroupVO[1];
				ObjGroupVO g = ArchiveAgent.summary(type, typeid, -1, vo.getOption(), u);
				ga[0] = g;
			}
			else {
				ga = new ObjGroupVO[1];
				ObjGroupVO g = Groups.cachedGroupSummary(type, typeid, group, vo.getOption(), u);
				ga[0] = g;
			}
			s = mapper.writeValueAsString(ga);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String info(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		String gr = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjGroupVO[] ga = new ObjGroupVO[0];
			ObjectMapper mapper = new ObjectMapper();
			String type = vo.getType();
			int typeid = vo.getTypeid();
			String group = vo.getGroup();
			if (group.equalsIgnoreCase("team")) {
				gr = "team";
			}
			if (group.equalsIgnoreCase("reviews")) {
				group = "review";
			}

			if (group.equalsIgnoreCase("review")) {
				ArrayList<ObjGroupVO> rev = Groups.reviews(type, typeid, u);
				if (rev.size() > 0) {
					ga = rev.toArray(new ObjGroupVO[rev.size()]);
				}
			}
			else if (group.equalsIgnoreCase("resolution")) {
				ArrayList<ObjGroupVO> res = Groups.resolution(type, typeid, u);
				if (res.size() > 0) {
					ga = res.toArray(new ObjGroupVO[res.size()]);
				}
			}
			
			
			
			else {
				ga = new ObjGroupVO[1];
				ObjGroupVO g = Groups.cachedGroupInfo(type, typeid, group, vo.getOption(), u);
				ga[0] = g;
			}

			s = mapper.writeValueAsString(ga);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String history(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjGroupVO[] ga = new ObjGroupVO[0];
			ObjectMapper mapper = new ObjectMapper();
			String type = vo.getType();
			int typeid = vo.getTypeid();
			String group = vo.getGroup();

			if (group.equalsIgnoreCase("custom")) {
				// TO DO
			}
			else {
				ga = Groups.history(type, typeid, group, vo.getOption(), u);
			}
			s = mapper.writeValueAsString(ga);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String id(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ObjGroupVO[] ga = new ObjGroupVO[0];
			ObjectMapper mapper = new ObjectMapper();
			String type = vo.getType();
			int typeid = vo.getTypeid();
			String group = vo.getGroup();
			int id = Operator.toInt(vo.getId());

			ga = new ObjGroupVO[1];
			ObjGroupVO g = new ObjGroupVO();
//			if (group.equalsIgnoreCase("appointment")) {
//				g = AppointmentAgent.getId(type, typeid, id);
//			}
//			else {
				g = Groups.id(type, typeid, group, id, u);
//			}
			ga[0] = g;
			s = mapper.writeValueAsString(ga);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String tabaccess(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			Logger.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+vo.getToken());
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			String entity = vo.getEntity();
			ResponseVO r = new ResponseVO();
			r.setMessagecode("cs403");
			r.setValid(false);
			if (u.isAdmin()) {
				r.setMessagecode("cs200");
				r.setValid(true);
			}
			else {
				RolesVO rl = LkupCache.getTabRoles(entity);
				if (rl.readAccess(u.getRoles(), u.getNonpublicroles())) {
					r.setMessagecode("cs200");
					r.setValid(true);
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			s = mapper.writeValueAsString(r);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	




}





