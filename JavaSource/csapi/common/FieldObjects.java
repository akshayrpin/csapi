package csapi.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;
import alain.core.utils.Logger;

/**
 * @author svijay
 *
 */
public class FieldObjects {

	public static final String LOG_CLASS= "FieldObjects.java";
	
	/**
	 * @param id
	 * @param order
	 * @param fieldid
	 * @param type
	 * @param itype
	 * @param field
	 * @param label
	 * @param value
	 * @param text
	 * @param textfield
	 * @param link
	 * @param linkfield
	 * @param alert
	 * @param required
	 * @param editable
	 * @param addable
	 * @return
	 */
	public static ObjVO toObject(int id, int order, String fieldid, String type, String itype, String field, String label, String value, String text, String textfield, String link, String linkfield, String alert,  String required, String editable, String addable){
		SubObjVO[] choices = new SubObjVO[0];
		return toObject(id, order, fieldid, type, itype, field, label, value, text, textfield, link, linkfield, alert, required, choices, editable, addable);
	} 
	
	public static ObjVO toObject(int id, int order, String fieldid, String type, String itype, String field, String label, String value, String text, String textfield, String link, String linkfield, String alert,  String required, SubObjVO[] choices,String editable, String addable){
		return toObject(id, order, fieldid, type, itype, field, label, value, text, textfield, link, linkfield, alert, required, choices, editable, addable,"Y","Y","");
	} 
	
	public static ObjVO toObject(int id, int order, String fieldid, String type, String itype, String field, String label, String value, String text, String textfield, String link, String linkfield, String alert,  String required, String editable, String addable, String json){
		SubObjVO[] choices = new SubObjVO[0];
		return toObject(id, order, fieldid, type, itype, field, label, value, text, textfield, link, linkfield, alert, required, choices, editable, addable,"Y","Y",json);
	} 



	public static ObjVO toObject(int id, int order, String fieldid, String type, String itype, String field, String label, String value, String text, String textfield, String link, String linkfield, String alert,  String required, SubObjVO[] choices, String editable, String addable,String updateIfValuePresent,String updateSameTable,String json){
		ObjVO vo = new ObjVO();
		vo.setId(id);
		vo.setOrder(order);
		vo.setFieldid(fieldid);
		vo.setType(type);
		vo.setItype(itype);
		vo.setField(field);
		vo.setLabel(label);
		vo.setValue(value);
		vo.setText(text);
		vo.setTextfield(textfield);
		vo.setLink(link);
		vo.setLinkfield(linkfield);
		vo.setAlert(alert);
		vo.setRequired(required);
		vo.setChoices(choices);
		vo.setEditable(editable);
		vo.setAddable(addable);
		vo.setUpdateIfValuePresent(updateIfValuePresent);
		vo.setUpdateSameTable(updateSameTable);
		vo.setJson(json);
		return vo;
	} 
	
	
	/**
	 * @param id
	 * @param order
	 * @param fieldId
	 * @param type
	 * @param iType
	 * @param fieldLabel
	 * @param value
	 * @param link
	 * @param alert
	 * @param required
	 * @return
	 */
	public static JSONObject getFieldObject(int id, int order, String fieldId, String type, String iType, String fieldLabel, String value, String link, String alert,  String required){
	
		JSONObject ob = new JSONObject();
		try {
			ob.put("id",  id);
			ob.put("order",  order);
			ob.put("fieldid",  fieldId);
			ob.put("type",  type);
			ob.put("itype",  iType);
			ob.put("field",  fieldLabel);
			ob.put("value",  value);
			ob.put("link", link);
			ob.put("alert",  alert);
			ob.put("required",  required);
			
		} catch (JSONException e) {
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return ob;
	} 
	
	
	
	
	/**
	 * @param id
	 * @param order
	 * @param fieldId
	 * @param type
	 * @param iType
	 * @param fieldLabel
	 * @param value
	 * @param link
	 * @param alert
	 * @param required
	 * @param choices
	 * @return
	 */
	public static JSONObject getFieldObject(int id, int order, String fieldId, String type, String iType, String fieldLabel, String value, String link, String alert,  String required, JSONArray choices){
	
		JSONObject ob = new JSONObject();
		try {
			ob.put("id",  id);
			ob.put("order",  order);
			ob.put("fieldid",  fieldId);
			ob.put("type",  type);
			ob.put("itype",  iType);
			ob.put("field",  fieldLabel);
			ob.put("value",  value);
			ob.put("link", link);
			ob.put("alert",  alert);
			ob.put("required",  required);
			ob.put("choices",  choices);
		} catch (JSONException e) {
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return ob;
	} 
	
	
	public static ObjVO getFieldObjectVO(int id, int order, String fieldId, String type, String iType, String fieldLabel, String value, String link, String alert,  String required){
		ObjVO ob = new ObjVO();
		try {
			ob.setId(id);
			ob.setOrder(order);
			ob.setFieldid(fieldId);
			ob.setType(type);
			ob.setItype(iType);
			ob.setField(fieldLabel);
			ob.setValue(value);
			ob.setLink(link);
			ob.setAlert(alert);
			ob.setRequired(required);
		} catch (Exception e) {
			Logger.error(LOG_CLASS.concat(e.getMessage()));
		}
		return ob;
	}
	
}
