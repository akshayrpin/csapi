package csapi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import csshared.vo.ObjGroupVO;
import csshared.vo.ObjMap;
import csshared.vo.ObjVO;
import csshared.vo.ReviewVO;
import csshared.vo.SubObjVO;
import csshared.vo.lkup.RolesVO;

public class CsTools {

	public static ObjVO[] convert(ArrayList<ObjVO> l) {
		int s = l.size();
		ObjVO[] o = new ObjVO[s];
		for (int i = 0; i < s; i++) {
			o[i] = l.get(i);
		}
		return o;
	}

	public static ObjMap[] convertMap(ArrayList<ObjMap> l) {
		int s = l.size();
		ObjMap[] o = new ObjMap[s];
		for (int i = 0; i < s; i++) {
			o[i] = l.get(i);
		}
		return o;
	}

	public static ReviewVO[] convertReview(ArrayList<ReviewVO> l) {
		int s = l.size();
		ReviewVO[] o = new ReviewVO[s];
		for (int i = 0; i < s; i++) {
			o[i] = l.get(i);
		}
		return o;
	}

	public static SubObjVO[] yesNoNone(String nonetext) {
		SubObjVO[] r = new SubObjVO[3];

		SubObjVO none = new SubObjVO();
		none.setValue("");
		none.setText(nonetext);
		r[0] = none;

		SubObjVO yes = new SubObjVO();
		yes.setValue("Y");
		yes.setText("yes");
		r[1] = yes;

		SubObjVO no = new SubObjVO();
		no.setValue("N");
		no.setText("no");
		r[2] = no;

		return r;
	}

	public static RolesVO toRoles(String json) {
		RolesVO r = new RolesVO();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			r = mapper.readValue(json, RolesVO.class);
		}
		catch (Exception e) { }
		return r;

	}

	public static ObjGroupVO toGroup(String json) {
		ObjGroupVO r = new ObjGroupVO();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			r = mapper.readValue(json, ObjGroupVO.class);
		}
		catch (Exception e) { }
		return r;

	}

	public static ArrayList<ObjGroupVO> toGroupArray(String json) {
		ObjGroupVO[] r = new ObjGroupVO[0];
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			r = mapper.readValue(json, ObjGroupVO[].class);
		}
		catch (Exception e) { }
		return new ArrayList<>(Arrays.asList(r));
	}

	public static String toJson(ObjGroupVO vo) {
		String r = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			r = mapper.writeValueAsString(vo);
		}
		catch (Exception e) { }
		return r;
	}

	public static String toJson(ArrayList<ObjGroupVO> vo) {
		String r = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			r = mapper.writeValueAsString(vo);
		}
		catch (Exception e) { }
		return r;
	}

	public static String toJson(RolesVO vo) {
		String r = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			r = mapper.writeValueAsString(vo);
		}
		catch (Exception e) { }
		return r;
	}

	public static String dateColumnValue(String date) {
		StringBuilder sb = new StringBuilder();
		if (Operator.hasValue(date)) {
			Timekeeper d = new Timekeeper();
			d.setDate(date);
			sb.append(d.sqlDatetime());
		}
		else {
			sb.append(" null ");
		}
		return sb.toString();
	}

	public static String booleanColumnValue(boolean value) {
		if (value) { return " 'Y' "; }
		else { return " 'N' "; }
	}

	public static String booleanColumnValue(String value) {
		if (Operator.equalsIgnoreCase(value, "Y")) { return " 'Y' "; }
		else { return " 'N' "; }
	}

	public static String[] paramToString(String param) {
		String[] r = new String[0];
		if (Operator.hasValue(param)) {
			if (param.indexOf(",") > -1) {
				r = Operator.split(param, ",");
			}
			else {
				r = Operator.split(param, "|");
			}
		}
		return r;
	}

	public static String abbreviateAddress(String addresspart) {
		if (!Operator.hasValue(addresspart)) { return ""; }
		String r = addresspart;
		if (Operator.equalsIgnoreCase(r, "North"))  { r = "N"; }
		else if (Operator.equalsIgnoreCase(r, "South"))  { r = "S"; }
		else if (Operator.equalsIgnoreCase(r, "East"))  { r = "E"; }
		else if (Operator.equalsIgnoreCase(r, "West"))  { r = "W"; }
		else if (Operator.equalsIgnoreCase(r, "Boulevard"))  { r = "BLVD"; }
		else if (Operator.equalsIgnoreCase(r, "Avenue"))  { r = "AVE"; }
		else if (Operator.equalsIgnoreCase(r, "Drive"))  { r = "DR"; }
		else if (Operator.equalsIgnoreCase(r, "Lane"))  { r = "LN"; }
		else if (Operator.equalsIgnoreCase(r, "Road"))  { r = "RD"; }
		else if (Operator.equalsIgnoreCase(r, "Street"))  { r = "ST"; }
		else if (Operator.equalsIgnoreCase(r, "Terrace"))  { r = "TER"; }
		else if (Operator.equalsIgnoreCase(r, "WY"))  { r = "WAY"; }
		return r;
	}

}













