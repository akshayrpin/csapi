package csapi.impl.parking;

import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ParkingPermit extends ParkingActivity{

	ParkingPermit(RequestVO vo, Token u, ResponseVO r) {
		super(vo, u, r);
	}

	@Override
	public ResponseVO saveActivity(RequestVO vo, Token u, ResponseVO r, DataVO m) {
		int size = Operator.toInt(m.get("QTY"));
		int id = 0;
		for (int i = size ; i > 0; i--) {
			String str = ActivityAgent.saveActivity(vo, u);
			id = Operator.toInt(str);
			if (id > 0) {
				String ids = r.getInfo("IDS");
				StringBuilder sb = new StringBuilder();
				if (Operator.hasValue(ids)) {
					sb.append(ids);
					sb.append(",");
				}
				sb.append(id);
				r.addInfo("IDS", sb.toString());
			}
		}
		if (id > 0) {
			r.setMessagecode("cs200");
			r.setType(ParkingAgent.getResponseType(vo, u, size, Operator.toString(id)));
			r.setId(id);
		}
		else {
			r.setMessagecode("cs412");
			r.addError("Unable to save permit. 1");
		}
		return r;
	}

}
