 package csapi.utils.validate;

import alain.core.utils.Timekeeper;
import csshared.vo.ResponseVO;


public class ValidateUtil {

	public ResponseVO response = new ResponseVO();
	public Timekeeper start = new Timekeeper();
	public Timekeeper end = new Timekeeper();
	public boolean valid = true;
	
	public ValidateUtil() { }

	public ResponseVO getResponse() {
		return response;
	}

	public void setResponse(ResponseVO response) {
		this.response = response;
	}

	public Timekeeper getStart() {
		return start;
	}

	public void setStart(Timekeeper start) {
		this.start = start;
	}

	public Timekeeper getEnd() {
		return end;
	}

	public void setEnd(Timekeeper end) {
		this.end = end;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}


	



}












