package csapi.impl.parking;

import alain.core.security.Token;
import alain.core.utils.Logger;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public abstract class ParkingActivity {
	ParkingActivity(RequestVO vo, Token u, ResponseVO r){
		r = ParkingAgent.checkAlert(vo, u, r);
		if(r.isValid()) {
			r = ParkingAgent.checkAccountandZone(vo, u, r);
		}
	}
	
	public abstract ResponseVO saveActivity(RequestVO vo, Token u, ResponseVO r, DataVO m);

}
