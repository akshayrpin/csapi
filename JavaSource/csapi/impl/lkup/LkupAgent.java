package csapi.impl.lkup;

import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.impl.lso.LsoSQL;
import csapi.utils.CsReflect;
import csshared.vo.SubObjVO;


public class LkupAgent {

	public static SubObjVO[] types(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("types", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			command = LkupSQL.types(grouptype, -1);
		}
		return Choices.getChoices(command);
	}
	
	public static SubObjVO[] usertypes(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("usertypes", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			return new SubObjVO[0];
		}
		return Choices.getChoices(command);
	}
	
	public static SubObjVO[] typeDescriptions(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("typedescriptions", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			command = LkupSQL.typeDescriptions(grouptype, -1);
		}
		return Choices.getChoices(command);
	}
	

	public static SubObjVO[] status(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("status", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			command = LkupSQL.status(grouptype, -1);
		}
		return Choices.getChoices(command);
	}
	
	public static SubObjVO[] statusDescriptions(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("statusdescriptions", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			command = LkupSQL.statusDescriptions(grouptype, -1);
		}
		return Choices.getChoices(command);
	}
	

	public static SubObjVO[] groups(String grouptype, String type, int typeid, int selected) {
		String command = CsReflect.getLkup("groups", grouptype, type, typeid, selected);
		if (!Operator.hasValue(command)) {
			command = LkupSQL.groups(grouptype, type, typeid, -1);
		}
		return Choices.getChoices(command);
	}
	
	public static SubObjVO[] strmod(int selected) {
		String command = LsoSQL.getMod(selected);
		return Choices.getChoices(command);
	}
	
	public static SubObjVO[] streets(int selected) {
		String command = LsoSQL.getStreets(selected);
		return Choices.getChoices(command);
	}
	












}















