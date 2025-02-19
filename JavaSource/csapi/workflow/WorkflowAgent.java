package csapi.workflow;

import java.util.LinkedHashMap;

import csshared.vo.RequestVO;
import alain.core.db.Sage;


/**
 * @author svijay
 */

public class WorkflowAgent {

	/**
	 * @param RequestVO r
	 * @return LinkedHashMap
	 */
	public static LinkedHashMap<String,WorkflowVO> getWorkflows(RequestVO r){
		LinkedHashMap<String,WorkflowVO> lm = new LinkedHashMap<String,WorkflowVO>();
		
		Sage db = new Sage();
		Sage db2 = new Sage();
		String command = WorkflowSQL.getWorkFlows(r, true);
		db.query(command);
		
		while(db.next()){
			WorkflowVO w = new WorkflowVO();
			w.setId(db.getInt("ID"));
			w.setTypeid(db.getInt("TYPE_ID"));
			w.setWorkflowid(db.getInt("LKUP_WORKFLOW_ID"));
			w.setWorkflowname(db.getString("WORKFLOW_NAME"));
			w.setPre(db.getString("PRE"));
			w.setPost(db.getString("POST"));
			w.setSkip(db.getString("SKIP"));
			w.setPassid(db.getInt("PASS_ID"));
			w.setFailid(db.getInt("FAIL_ID"));
			w.setWorkflowname(db.getString("WORKFLOW_NAME"));
			w.setWorkflowclass(db.getString("CLASS"));
			w.setWorkflowmethod(db.getString("METHOD"));
			
			command = WorkflowSQL.getWorkFlowConfig(r.getGrouptype(),w.getId(), w.getWorkflowid());
			db2.query(command);
			int sz = db2.size();
			WorkflowConfigVO wcl[] = new WorkflowConfigVO[sz];
			int c =0;
			while(db2.next()){
				WorkflowConfigVO wc = new WorkflowConfigVO();
				wc.setId(db2.getInt("ID"));
				wc.setName(db2.getString("NAME"));
				wc.setValue(db2.getString("VALUE"));
				wc.setFieldtypeid(db2.getInt("LKUP_FIELD_TYPE_ID"));
				wc.setFieldItypeid(db2.getInt("LKUP_FIELD_ITYPE_ID"));
				
				wcl[c] = wc;
				c++;
			}
			
			w.setWorkflowConfigVO(wcl);
			
			lm.put(w.getWorkflowname(), w);
		}
		
		db2.clear();
		db.clear();
		
		return lm;
	}
}
