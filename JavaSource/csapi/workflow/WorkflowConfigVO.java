 package csapi.workflow;
import alain.core.utils.Operator;


public class WorkflowConfigVO {

	public int id = -1;
	public int workflowid = -1;
	public int fieldtypeid = -1;
	public int fieldItypeid = -1;
	public String name = "";
	public String value = "";
	
	public WorkflowConfigVO() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWorkflowid() {
		return workflowid;
	}

	public void setWorkflowid(int workflowid) {
		this.workflowid = workflowid;
	}

	public int getFieldtypeid() {
		return fieldtypeid;
	}

	public void setFieldtypeid(int fieldtypeid) {
		this.fieldtypeid = fieldtypeid;
	}
	
	

	public int getFieldItypeid() {
		return fieldItypeid;
	}

	public void setFieldItypeid(int fieldItypeid) {
		this.fieldItypeid = fieldItypeid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	


}
