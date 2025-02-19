package csapi.impl.vehicle;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csshared.vo.DataVO;
import csshared.vo.RequestVO;


public class VehicleAgent {

	public static boolean save(RequestVO vo, Token u) {
		boolean r = false;
		if (Operator.hasValue(vo.getId())) {
			r = update(vo, u);
		}
		else {
			r = add(vo, u);
		}
		return r;
	}

	public static boolean add(RequestVO vo, Token u) {
		boolean r = false;
		DataVO m = DataVO.toDataVO(vo);
		r = add(Operator.toInt(m.get("REF_PROJECT_PARKING_ID")), Operator.toInt(m.get("ACTIVITY_ID")), m.get("LICENSE_PLATE"), m.getString("REG_STATE"), m.getString("VEHICLE_MAKE"), m.getString("VEHICLE_MODEL"), m.getString("VEHICLE_COLOR"), m.getString("VEHICLE_YEAR"), u.getId(), u.getIp());
		return r;
	}

	public static boolean add(int refprparkingid, int actid, DataVO[] d, int userid, String ip) {
		boolean r = true;
		for (int i=0; i<d.length; i++) {
			DataVO vo = d[i];
			String license = vo.getString("LICENSE_PLATE");
			String state = vo.getString("REG_STATE");
			String make = vo.getString("VEHICLE_MAKE");
			String model = vo.getString("VEHICLE_MODEL");
			String color = vo.getString("VEHICLE_COLOR");
			String year = vo.getString("VEHICLE_YEAR");
			add(refprparkingid, actid, license, state, make, model, color, year, userid, ip);
		}
		return r;
	}

	public static boolean add(int refprparkingid, int actid, String license, String state, String make, String model, String color, String year, int userid, String ip) {
		boolean r = false;
		String command = VehicleSQL.addVehicle(refprparkingid, actid, license, state, make, model, color, year, userid, ip);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}

	public static boolean update(RequestVO vo, Token u) {
		boolean r = false;
		DataVO m = DataVO.toDataVO(vo);
		String command = VehicleSQL.update(Operator.toInt(vo.getId()), m.getString("LICENSE_PLATE"), m.getString("REG_STATE"), m.getString("REG_EXP_DT"), m.getString("VEHICLE_MAKE"), m.getString("VEHICLE_MODEL"), m.getString("VEHICLE_COLOR"), Operator.toInt(m.getString("VEHICLE_YEAR")), m.getString("BLOCKED"), u.getId(), u.getIp());
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			r = db.update(command);
			db.clear();
		}
		return r;
	}



}















