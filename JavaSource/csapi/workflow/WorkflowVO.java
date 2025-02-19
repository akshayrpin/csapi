 package csapi.workflow;
import alain.core.utils.Operator;


public class WorkflowVO {

	public int id = -1;
	public int typeid = -1;
	public int workflowid = -1;
	public String workflowname = "";
	public String pre = "";
	public String post = "";
	public int passid =  -1;
	public int failid =  -1;
	public String skip = "N";
	public int order = -1;
	public String createddate = "";
	public String updateddate = "";
	public String createdby = "";
	public String updatedby = "";
	public WorkflowConfigVO[] workflowConfigVO = new WorkflowConfigVO[0];
	public String workflowclass = "";
	public String workflowmethod = "";
	
	public WorkflowVO() {}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getTypeid() {
		return typeid;
	}



	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}



	public int getWorkflowid() {
		return workflowid;
	}



	public void setWorkflowid(int workflowid) {
		this.workflowid = workflowid;
	}



	public String getWorkflowname() {
		return workflowname;
	}



	public void setWorkflowname(String workflowname) {
		this.workflowname = workflowname;
	}



	public String getPre() {
		return pre;
	}



	public void setPre(String pre) {
		this.pre = pre;
	}
	
	public boolean isPre(){
		return Operator.equalsIgnoreCase(getPre(), "Y");
	}



	public String getPost() {
		return post;
	}



	public void setPost(String post) {
		this.post = post;
	}

	
	public boolean isPost(){
		return Operator.equalsIgnoreCase(getPost(), "Y");
	}



	public int getPassid() {
		return passid;
	}



	public void setPassid(int passid) {
		this.passid = passid;
	}



	public int getFailid() {
		return failid;
	}



	public void setFailid(int failid) {
		this.failid = failid;
	}



	public String getSkip() {
		return skip;
	}



	public void setSkip(String skip) {
		this.skip = skip;
	}

	public boolean isSkip(){
		return Operator.equalsIgnoreCase(getSkip(), "Y");
	}

	public int getOrder() {
		return order;
	}



	public void setOrder(int order) {
		this.order = order;
	}



	public String getCreateddate() {
		return createddate;
	}



	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}



	public String getUpdateddate() {
		return updateddate;
	}



	public void setUpdateddate(String updateddate) {
		this.updateddate = updateddate;
	}



	public String getCreatedby() {
		return createdby;
	}



	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}



	public String getUpdatedby() {
		return updatedby;
	}



	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}



	public WorkflowConfigVO[] getWorkflowConfigVO() {
		return workflowConfigVO;
	}



	public void setWorkflowConfigVO(WorkflowConfigVO[] workflowConfigVO) {
		this.workflowConfigVO = workflowConfigVO;
	}



	public String getWorkflowclass() {
		return workflowclass;
	}



	public void setWorkflowclass(String workflowclass) {
		this.workflowclass = workflowclass;
	}



	public String getWorkflowmethod() {
		return workflowmethod;
	}



	public void setWorkflowmethod(String workflowmethod) {
		this.workflowmethod = workflowmethod;
	}

	
	
	
	


}
