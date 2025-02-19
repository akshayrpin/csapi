package csapi.impl.tasks;

import alain.core.utils.Operator;

public class TasksSQL {

	public static String summary(String type, int typeid, int id, String option) {
		if (!Operator.hasValue(type)) { return ""; }
		if (typeid < 1 && id < 1) { return ""; }
		
		StringBuilder sb = new StringBuilder();
		if (Operator.equalsIgnoreCase(type, "activity") ) {
			
			sb.append(" select DISTINCT RAT.ID,RAT.TASK,RAT.DESCRIPTION,CASE WHEN TAR.RESULT IS NULL THEN 'N' ELSE TAR.RESULT END AS COMPLETE,  ");
			sb.append(" CONVERT(VARCHAR(10),TAR.CREATED_DATE , 101) + ' @ ' + CONVERT(VARCHAR(10),TAR.CREATED_DATE , 108)  as DATE ");
			sb.append(" , TAR.CREATED_DATE ,RAT.ORDR");
			sb.append(" from activity A ");
			sb.append(" LEFT OUTER JOIN REF_ACT_TYPE_TASKS RAT on A.LKUP_ACT_TYPE_ID= RAT.LKUP_ACT_TYPE_ID AND RAT.ACTIVE='Y' ");
			sb.append(" LEFT OUTER JOIN TASKS_ACT_RESULTS TAR on A.ID = TAR.ACTIVITY_ID  AND TAR.REF_ACT_TYPE_TASKS_ID= RAT.ID AND TAR.ACTIVE='Y' ");
			sb.append(" where A.ID = ").append(typeid);
			sb.append(" order by RAT.ORDR, TAR.CREATED_DATE  ");
			
		}
		
		/*if (Operator.equalsIgnoreCase(type, "project") ) {
			
			sb.append(" select DISTINCT RAT.ID,RAT.TASK,RAT.DESCRIPTION,CASE WHEN TAR.RESULT IS NULL THEN 'N' ELSE TAR.RESULT END AS COMPLETE,  ");
			sb.append(" CONVERT(VARCHAR(10),TAR.CREATED_DATE , 101) + ' @ ' + CONVERT(VARCHAR(10),TAR.CREATED_DATE , 108)  as DATE ");
			sb.append(" from PROJECT A ");
			sb.append(" LEFT OUTER JOIN REF_ACT_TYPE_TASKS RAT on A.LKUP_PROJECT_TYPE_ID= RAT.LKUP_ACT_TYPE_ID ");
			sb.append(" LEFT OUTER JOIN TASKS_ACT_RESULTS TAR on A.ID = TAR.ACTIVITY_ID ");
			sb.append(" where A.ID = ").append(typeid);
			
			
		}
		|| Operator.equalsIgnoreCase(type, "project")*/
		

		/*sb.append(" SELECT ");
		if (Operator.equalsIgnoreCase(option, "50")) {
			sb.append(" TOP 50 ");
		}
		else if (Operator.equalsIgnoreCase(option, "All")) {
		}
		else {
			sb.append(" TOP 10 ");
		}
		
		
		sb.append(" ORDER BY ");
		sb.append(" DATE DESC, TIME DESC ");*/
		return sb.toString();
	}

	


}
























