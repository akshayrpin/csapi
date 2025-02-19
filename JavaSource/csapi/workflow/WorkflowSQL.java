package csapi.workflow;

import alain.core.security.Token;
import csapi.common.Table;
import csapi.utils.CsReflect;
import csshared.vo.RequestVO;

public class WorkflowSQL {

	
	
	public static String getWorkFlows(RequestVO r, boolean pre) {
		String tableref = CsReflect.getTableRef(r.getGrouptype());
		String idref = CsReflect.getFieldIdRef(r.getGroup());
		StringBuilder sb = new StringBuilder();
		sb.append(" select R.*,LW.NAME as WORKFLOW_NAME,LW.CLASS,LW.METHOD ");
		sb.append(" FROM REF_").append(tableref).append("_WORKFLOW  R");
		sb.append(" join LKUP_WORKFLOW LW on R.LKUP_WORKFLOW_ID = LW.ID ");
		//sb.append(" left outer join ").append(tableref).append("_WORKFLOW_RESULTS TW on R.LKUP_WORKFLOW_ID = LW.ID ");
		sb.append(" WHERE R.ACTIVE='Y'  ");
		
		if(pre){
			sb.append(" AND R.PRE='Y' ");
		}else {
			sb.append(" AND R.POST='Y' ");
		}
		
		sb.append(" order by R.ORDR  ");
		return sb.toString();
	}
	
	
	public static String getWorkFlowConfig(String type,int id, int wfId) {
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		StringBuilder sb = new StringBuilder();
		sb.append(" select * ");
		sb.append(" FROM REF_").append(tableref).append("_WORKFLOW R join  REF_").append(tableref).append("_WORKFLOW_CONFIG  RC on R.ID = RC.REF_").append(tableref).append("_WORKFLOW_ID  ");
		sb.append(" join LKUP_WORKFLOW_CONFIG LW on R.LKUP_WORKFLOW_ID = LW.LKUP_WORKFLOW_ID ");
		//sb.append(" left outer join ").append(tableref).append("_WORKFLOW_RESULTS TW on R.LKUP_WORKFLOW_ID = LW.ID ");
		sb.append(" WHERE LW.ACTIVE='Y'  ");
		//sb.append(" order by R.ORDR DESC ");
		return sb.toString();
	}
}
