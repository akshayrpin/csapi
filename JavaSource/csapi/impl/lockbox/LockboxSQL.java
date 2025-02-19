package csapi.impl.lockbox;

import csshared.vo.RequestVO;
import alain.core.security.Token;
import alain.core.utils.Operator;


public class LockboxSQL {



	

	public static String addLockbox(RequestVO vo,Token u) {
		StringBuilder sb = new StringBuilder();
		
		String TRANSACTION_NUMBER = vo.getExtras().get("TRANSACTION_NUMBER");
		String BATCH_NUMBER = vo.getExtras().get("BATCH_NUMBER");
		String ACCOUNT_NUMBER = vo.getExtras().get("ACCOUNT_NUMBER");
		String PAYMENT_AMOUNT = vo.getExtras().get("PAYMENT_AMOUNT");
		String CHECK_ACCOUNT = vo.getExtras().get("CHECK_ACCOUNT");
		String CHECK_NO = vo.getExtras().get("CHECK_NO");
		String DAYTIME_QTY = vo.getExtras().get("DAYTIME_QTY");
		String OVERNIGHT_QTY = vo.getExtras().get("OVERNIGHT_QTY");
		String PROCESS_ID = vo.getExtras().get("PROCESS_ID");
		
		sb.append(" INSERT INTO LOCKBOX_UPLOADS (");
		sb.append("TRANSACTION_NUMBER");
		sb.append(",");
		sb.append("ACCOUNT_NUMBER");
		sb.append(",");
		sb.append("CHECK_NO");
		sb.append(",");
		sb.append("DAYTIME_QTY");
		sb.append(",");
		sb.append("OVERNIGHT_QTY");
		sb.append(",");
		sb.append("PAYMENT_AMOUNT");
		sb.append(",");
		sb.append("PROCESS_ID");
		sb.append(",");
		sb.append("CREATED_BY");
		sb.append(",");
		sb.append("UPDATED_BY");
		sb.append(",");
		sb.append("BATCH_NUMBER");
		sb.append(",");
		sb.append("CHECK_ACCOUNT");
	
		
		sb.append(") OUTPUT Inserted.ID   VALUES (");
		sb.append("'").append(Operator.sqlEscape(TRANSACTION_NUMBER)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(ACCOUNT_NUMBER)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(CHECK_NO)).append("'");
		sb.append(",");
		sb.append("").append(DAYTIME_QTY).append("");
		sb.append(",");
		sb.append("").append(OVERNIGHT_QTY).append("");
		sb.append(",");
		sb.append("").append(Operator.toDouble(PAYMENT_AMOUNT)).append("");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(PROCESS_ID)).append("'");
		sb.append(",");
		sb.append("").append(u.getId()).append("");
		sb.append(",");
		sb.append("").append(u.getId()).append("");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(BATCH_NUMBER)).append("'");
		sb.append(",");
		sb.append("'").append(Operator.sqlEscape(CHECK_ACCOUNT)).append("'");
		sb.append(")");
		return sb.toString();
	}






}




