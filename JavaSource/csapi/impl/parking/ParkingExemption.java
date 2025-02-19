package csapi.impl.parking;

import java.util.ArrayList;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.utils.objtools.Fields;
import csshared.vo.DataVO;
import csshared.vo.MessageVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class ParkingExemption extends ParkingActivity{

	ParkingExemption(RequestVO vo, Token u, ResponseVO r){
		super(vo, u, r);
		
		if(r.isValid()) {
// TODO: Test count below
//			r = maxExemption(vo, u, r);
		}
		if(r.isValid()) {
			r = ParkingAgent.checkValidTime(vo, u, r);
		}
	}

	@Override
	public ResponseVO saveActivity(RequestVO vo, Token u, ResponseVO r, DataVO m) {
		int size = Operator.toInt(m.get("QTY"));
		String group = m.get("GROUP");
		int id = 0;
		for (int i = size ; i > 0; i--) {
			String str = ActivityAgent.saveActivity(vo, u);
			id = Operator.toInt(str);
			if (id > 0) {
				ActivityAgent.updateActivity(Operator.toString(id), m.getString("PRINTED"), null);
				if(Operator.hasValue(group))
					ParkingAgent.saveVehicle(vo, u, r, id);
				
				r = ParkingAgent.sendEmail("activity", id, r, u, u.getEmail());
			}
			String ids = r.getInfo("IDS");
			StringBuilder sb = new StringBuilder();
			if (Operator.hasValue(ids)) {
				sb.append(ids);
				sb.append(",");
			}
			sb.append(id);
			r.addInfo("IDS", sb.toString());
		}
		if (id > 0) {
			r.setMessagecode("cs200");
			r.setType(ParkingAgent.getResponseType(vo, u, size, Operator.toString(id)));
			r.setId(id);
		} else {
			r.setMessagecode("cs400");
		}
		return r;
	}

	public static ResponseVO maxExemption(RequestVO vo, Token u, ResponseVO r) {
		String c = vo.getExtras().get("NO_OF_VEHICLES");
		int size = Operator.toInt(c);
		ObjVO v = Fields.getField(vo.data[0], "LKUP_ACT_TYPE_ID");
		String acttypeid = v.getValue();
		if (Operator.toInt(acttypeid) > 0) {
			String command = ParkingSQL.getActType(Operator.toInt(acttypeid));
			Sage db = new Sage();
			if (db.query(command) && db.next()) {
				String type = db.getString("TYPE");
				if (Operator.equalsIgnoreCase(type, "Daytime Exemption")) {
					boolean unlimited = false;
					int count = 0;
					int max = 0;
					command = ParkingSQL.count(Operator.toString(vo.getTypeid()));
					db.query(command);
					while (db.next()) {
						type = db.getString("TYPE");
						if (Operator.equalsIgnoreCase(type, "Preferential Parking")) {
							int ppcount = db.getInt("COUNT");
							int ppmax = db.getInt("DURATION_MAX");
							if (ppcount >= ppmax) {
								unlimited = true;
							}
						}
						if (Operator.equalsIgnoreCase(type, "Daytime Exemption")) {
							count = db.getInt("COUNT");
							max = db.getInt("DURATION_MAX");
						}
					}
					if (!unlimited) {
						int decount = count + 1;
						if (decount >= max) {
							MessageVO m = new MessageVO();
							m.setMessage("You will not be able to request, as you have exceded the limit.");
							ArrayList<MessageVO> mv = new ArrayList<MessageVO>();
							mv.add(m);
							r.setErrors(mv);
							r.setMessagecode("cs412");

						}
					}
				}
			}
			db.clear();
		}
		return r;
	}







}
