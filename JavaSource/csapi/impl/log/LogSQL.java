package csapi.impl.log;

import alain.core.utils.Operator;


public class LogSQL {

	public static String getThreadProgress(String processid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT ");
		sb.append(" T.* ");
		sb.append(" FROM ");
		sb.append(" LOG_THREAD_PROGRESS AS T ");
		sb.append(" WHERE ");
		sb.append(" T.PROCESS_ID = '").append(Operator.sqlEscape(processid)).append("' ");
		return sb.toString();
	}

	public static String setThreadProgress(String processid, int percent, String title, String description, String response) {
		StringBuilder sb= new StringBuilder();
		sb.append(" IF EXISTS ");
		sb.append(" (SELECT * FROM LOG_THREAD_PROGRESS WHERE LOWER(PROCESS_ID) = LOWER('").append(Operator.sqlEscape(processid)).append("') ) ");
		sb.append("     UPDATE ");
		sb.append("     	LOG_THREAD_PROGRESS ");
		sb.append("     SET ");
		sb.append(" 	    PERCENT_COMPLETE = ").append(percent).append(" ");
		sb.append(" 		, ");
		sb.append(" 		TITLE = '").append(Operator.sqlEscape(title)).append("' ");
		sb.append(" 		, ");
		sb.append(" 		DESCRIPTION = '").append(Operator.sqlEscape(description)).append("' ");
		sb.append(" 		, ");
		sb.append(" 		RESPONSE = '").append(Operator.sqlEscape(response)).append("' ");
		sb.append(" 		, ");
		sb.append(" 		UPDATED_DATE = getDate() ");
		sb.append("     OUTPUT Inserted.* ");
		sb.append("     WHERE LOWER(PROCESS_ID) = LOWER('").append(Operator.sqlEscape(processid)).append("') ");
		sb.append(" ELSE ");
		sb.append("     INSERT INTO ");
		sb.append(" 	    LOG_THREAD_PROGRESS (PROCESS_ID, PERCENT_COMPLETE, TITLE, DESCRIPTION, RESPONSE ) ");
		sb.append("     OUTPUT Inserted.* ");
		sb.append("     VALUES ");
		sb.append(" 	(").append(processid).append(", ").append(percent).append(", '").append(Operator.sqlEscape(title)).append("', '").append(Operator.sqlEscape(description)).append("', '").append(Operator.sqlEscape(response)).append("') ");
		return sb.toString();
	}









}




















