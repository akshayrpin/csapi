package csapi.impl.general;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.utils.CsReflect;
import csshared.vo.ObjVO;
import csshared.vo.TaskVO;
import csshared.vo.finance.FeeVO;
import csshared.vo.finance.FeesGroupVO;

public class DBBatch {

	public boolean insertCustom(String type, int typeid,ObjVO[] obj, int userId, int setId) throws Exception{
		if (!Operator.hasValue(type)) { return false; }
		String tableref = CsReflect.getTableRef(type)+"_FIELD_VALUE";
		String idref = CsReflect.getFieldIdRef(type);
		
		return insertCustom(type, typeid, obj, userId, setId, tableref, idref);
	}
	
	public boolean insertCustom(String type, int typeid, ObjVO[] obj, int userId, int setId, String table, String idref) throws Exception{
		boolean result = false;
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO ").append(table).append(" (").append(idref).append(",FIELD_ID,VALUE,VALUE_CHAR,VALUE_INT,VALUE_DATE,VALUE_DECIMAL,CREATED_BY,UPDATED_BY,SET_ID) VALUES (?,?,?,?,?,?,?,?,?,?)");
		
		
		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try {
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			int sz = obj.length;
			if(sz>0) {
				for(int i=0;i<sz;i++) {
					String value = obj[i].getValue();
					ps.setInt(1, typeid);
				    ps.setInt(2, Operator.toInt(obj[i].getFieldid()));
				    
				    ps.setString(3, value);
				    
				    String valuechar = "";
				    
				    
				    if(Operator.hasValue(value)){
				    	if(value.length()==1){ valuechar = value; 	}
				    }
				    ps.setString(4, Operator.sqlEscape(valuechar));
				    ps.setInt(5, Operator.toInt(value));
				   
				    
				    Timestamp timestamp = null; 
				    //if(l.getVO().getFieldDate()==null){
				    ps.setTimestamp(6, timestamp);
					/*}else {
						String s = l.getVO().getFieldDate().HOUR_OF_DAY()+":"+l.getVO().getFieldDate().MINUTE()+":"+l.getVO().getFieldDate().SECOND() ;
						timestamp = Timestamp.valueOf(l.getVO().getFieldDate().getString("YYYY-MM-DD")+" "+s);
						ps.setTimestamp(4, timestamp);
					}*/
				    Logger.info(value+"-->"+Operator.toDouble(value));
				    ps.setDouble(7, Operator.toDouble(value));
				    ps.setInt(8, userId);
				    ps.setInt(9, userId);
				    ps.setInt(10, setId);
				    ps.addBatch();
					
				}
			}
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
		}
		catch(Exception e){
			db.CONNECTION.rollback();
			Logger.error(e);
			throw e;
		}
		finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}
	
	
	/**
	 * @deprecated Use insertDivisions(String type, int typeid, ObjVO[] obj, int userId)
	 * @throws Exception
	 */
	public boolean insertDivisions(int typeid, ObjVO[] obj, int userId) throws Exception{
		return insertDivisions("lso", typeid, obj, userId);
	}

	public boolean insertDivisions(String type, int typeid, ObjVO[] obj, int userId) throws Exception{
		String tableref = CsReflect.getTableRef(type);
		String idref = CsReflect.getFieldIdRef(type);
		boolean result = false;
		
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO REF_").append(tableref).append("_DIVISIONS ( ").append(idref).append(", LKUP_DIVISIONS_ID, CREATED_BY, UPDATED_BY ) VALUES (?,?,?,?)");

		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try {
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			int sz = obj.length;
			if(sz>0){
				for(int i=0;i<sz;i++){
					String value = obj[i].getValue();
					ps.setInt(1, typeid);
				    ps.setInt(2, Operator.toInt(value));
				    ps.setInt(3, userId);
				    ps.setInt(4, userId);
				    ps.addBatch();
					
				}
			}
			ps.executeBatch();
			db.CONNECTION.commit();
			result =true;
		}
		catch(Exception e){
			db.CONNECTION.rollback();
			Logger.error("Error while field entry insert "+e.getMessage());
			throw e;
		}
		finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		db.clear();
		return result;
	}
	
	public boolean backupCustom(String type, int typeid,ObjVO[] obj, int userId) throws Exception{
		boolean result = false;
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO ACT_FIELD_VALUE (ACTIVITY_ID,FIELD_ID,VALUE,VALUE_CHAR,VALUE_INT,VALUE_DATE,VALUE_DECIMAL,CREATED_BY,UPDATED_BY) VALUES (?,?,?,?,?,?,?,?,?)");

		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try{
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			int sz = obj.length;
			if(sz>0){
				for(int i=0;i<sz;i++){
					String value = obj[i].getValue();
					ps.setInt(1, typeid);
				    ps.setInt(2, Operator.toInt(obj[i].getFieldid()));
				    ps.setString(3, Operator.sqlEscape(value));
				    
				    String valuechar = "";
				    
				    
				    if(Operator.hasValue(value)){
				    	if(value.length()==1){ valuechar = value; 	}
				    }
				    ps.setString(4, Operator.sqlEscape(valuechar));
				    ps.setInt(5, Operator.toInt(value));
				   
				    
				    Timestamp timestamp = null; 
				    //if(l.getVO().getFieldDate()==null){
				    ps.setTimestamp(6, timestamp);
					/*}else {
						String s = l.getVO().getFieldDate().HOUR_OF_DAY()+":"+l.getVO().getFieldDate().MINUTE()+":"+l.getVO().getFieldDate().SECOND() ;
						timestamp = Timestamp.valueOf(l.getVO().getFieldDate().getString("YYYY-MM-DD")+" "+s);
						ps.setTimestamp(4, timestamp);
					}*/
				    
				    ps.setDouble(7, Operator.toDouble(value));
				    ps.setInt(8, userId);
				    ps.setInt(9, userId);
				    ps.addBatch();
					
				}
			}
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
		}catch(Exception e){
			db.CONNECTION.rollback();
			System.err.println("Error while fieldentry insert "+e.getMessage());
			throw e;
		}finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}
	
	
	public boolean insertStatementDetail(FeesGroupVO[] fgarr, int statementId, int userId) throws Exception{
		boolean result = false;
		if(statementId==0) return false;
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO STATEMENT_DETAIL (");
		sb.append("STATEMENT_ID");
		sb.append(",");
		sb.append("FEE_ID");
		
		
		sb.append(",");
		sb.append("FEE_AMOUNT");
		sb.append(",");
		sb.append("FEE_PAID");
		sb.append(",");
		sb.append("BALANCE_DUE");
		sb.append(",");
		sb.append("COMMENTS");
		sb.append(",");
		sb.append("FEE_DATE");
		sb.append(",");
		sb.append("NET_TOTAL");
		sb.append(",");
		sb.append("INPUT1");
		sb.append(",");
		sb.append("INPUT2");
		sb.append(",");
		sb.append("INPUT3");
		sb.append(",");
		sb.append("INPUT4");
		sb.append(",");
		sb.append("INPUT5");
		sb.append(",");
		sb.append("CREATED_BY");
		sb.append(",");
		sb.append("UPDATED_BY");
		sb.append(",");
		sb.append("GROUP_ID");
		sb.append(",");
		sb.append("REF_FEE_FORMULA_ID");
		sb.append(",");
		sb.append("FINANCE_MAP_ID");
		sb.append(",");
		sb.append("ACCOUNT_NUMBER");
		sb.append(",");
		sb.append("PARENT_ID");
		sb.append(",");
		sb.append("FEE_VALUATION");
		/*sb.append(",");
		sb.append("UPDATED_IP");*/
		
		sb.append(") VALUES (");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		
	/*	sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");*/
	

		sb.append(")");
		
		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try{
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			
			for(int j=0;j<fgarr.length;j++){
				for(int i=0;i<fgarr[j].getFees().length;i++){
					FeeVO f = fgarr[j].getFees()[i];
					ps.setInt(1, statementId);
				    ps.setInt(2, f.getFeeid());
				    ps.setDouble(3, f.getAmount());
				    ps.setDouble(4, f.getPaidamount());
				    ps.setDouble(5, f.getAmount());
				    
				    //comments
				    ps.setString(6, "cc");
				    Timestamp timestamp = null; 
				    
				    if(Operator.hasValue(f.getFeedate())){
				    	String s = Operator.replace(f.getFeedate(), "/", "-");
				    	String d = s+" 00:00:00";
				    	
						timestamp = Timestamp.valueOf(d);
						
				    }
				    ps.setTimestamp(7,timestamp);
				    ps.setDouble(8, f.getBalancedue());
				    
				    ps.setDouble(9, f.getInput1());
				    ps.setDouble(10, f.getInput2());
				    ps.setDouble(11, f.getInput3());
				    ps.setDouble(12, f.getInput4());
				    ps.setDouble(13, f.getInput5());
				    Logger.info(f.getFeedate());
				    ps.setInt(14, userId);
				    ps.setInt(15, userId);
				    ps.setInt(16, f.getGroupid());
				    ps.setInt(17, f.getReffeeformulaid());
				    ps.setInt(18, f.getFinancemapid());
				    ps.setString(19, f.getAccount());
				    
				    ps.setInt(20, f.getParentId());
				    ps.setDouble(21, f.getValuation());
				    ps.addBatch();
					
				}
			}
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
		}catch(Exception e){
			db.CONNECTION.rollback();
			System.err.println("Error while fieldentry insert "+e.getMessage());
			throw e;
		}finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}
	
	
	
	public boolean insertReRoute(String order, int team, int userId) throws Exception{
		boolean result = false;
		if(!Operator.hasValue(order)){
			return false;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO REF_COMBOREVIEW_ROUTE (");
		sb.append("REF_COMBOREVIEW_ACTION_ID");
		sb.append(",");
		sb.append("REF_TEAM_ID");
		
		
		sb.append(",");
		sb.append("ORDR");
		sb.append(",");
		sb.append("CREATED_BY");
		sb.append(",");
		sb.append("UPDATED_BY");
		
		sb.append(") VALUES (");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
	
		
	/*	sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");*/
	

		sb.append(")");
		
		Sage db1 = new Sage();
		
		if(Operator.hasValue(order)){
			String command = "DELETE FROM REF_COMBOREVIEW_ROUTE where REF_COMBOREVIEW_ACTION_ID in ("+order+")";
			result = db1.update(command);
		}
		db1.clear();
		
		Sage db = new Sage();
		
		
		db.connect();
		PreparedStatement ps  = null;
		try{
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			Timekeeper k = new Timekeeper();
			String h = k.getString("YYYYMMDD");
			
			String o[] = Operator.split(order,",");
			
			for(int j=0;j<o.length;j++){
					ps.setInt(1, Operator.toInt(o[j]));
				    ps.setInt(2, team);
				    ps.setInt(3, Operator.toInt(h+j));
				    ps.setInt(4, userId);
				    ps.setInt(5, userId);
				  
				    ps.addBatch();
					
			
			}
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
			
			
		}catch(Exception e){
			db.CONNECTION.rollback();
			System.err.println("Error while fieldentry insert "+e.getMessage());
			throw e;
		}finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}
	
	
	public boolean insertLockUpload(ArrayList<HashMap<String, String>> al, String batchref, String random, int userId,String f) throws Exception{
		boolean result = false;
		
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO LOCKBOX_UPLOADS (");
		sb.append("TRANSACTION_NUMBER");
		sb.append(",");
		sb.append("ACCOUNT_NUMBER");
		
		
		sb.append(",");
		sb.append("DEPOSITDATE");
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
		sb.append(",");
		sb.append("FILE_NAME");
		
		sb.append(") VALUES (");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");
		
	/*	sb.append(",");
		sb.append("?");
		sb.append(",");
		sb.append("?");*/
	

		sb.append(")");
		
		
		
		Sage db = new Sage();
		
		
		db.connect();
		PreparedStatement ps  = null;
		try{
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			Timekeeper k = new Timekeeper();
			int size = al.size();
			System.out.println("size is "+size);
			for (int i=0; i<size; i++) {
				HashMap<String, String> map = al.get(i);
				String account_number = map.get("ACCOUNT NUMBER");
				String amount = map.get("Transaction Total");
				String transno = map.get("Transaction Number");
				String daytime = map.get("Misc Fld1");
				String overnight = map.get("Misc Fld2");
				String checkaccount = map.get("Check Account");
				String checknumber = map.get("Check Number");
				String date = map.get("Deposit Date");
				amount = Operator.replace(amount, "$", "");
				if(Operator.hasValue(transno)){
					ps.setString(1, transno);
					ps.setInt(2, Operator.toInt(account_number));
					ps.setString(3, date);
					ps.setString(4, checknumber);
					ps.setInt(5, Operator.toInt(daytime));
					ps.setInt(6, Operator.toInt(overnight));
					ps.setDouble(7, Operator.toDouble(amount));
					ps.setString(8, random);
					ps.setInt(9, userId);
					ps.setInt(10, userId);
					Logger.info(i+ "account_number"+account_number+"amount"+amount+"payrefererence"+transno+"daytime"+daytime+"overnight"+overnight+"date"+date);
					ps.setString(11, batchref);
					ps.setString(12, checkaccount);
					ps.setString(13, f);
					ps.addBatch();
				}
			}
			
			
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
			
			
		}catch(Exception e){
			db.CONNECTION.rollback();
			System.err.println("Error while fieldentry insert "+e.getMessage());
			throw e;
			
		}finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		Sage db1 = new Sage();
		
		
		String command = " UPDATE LOCKBOX_UPLOADS  SET PROJECT_ID = R.PROJECT_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_PARKING as  R on L.ACCOUNT_NUMBER = R.ID AND R.ACTIVE='Y' WHERE L.ACCOUNT_NUMBER >0 AND L.PROJECT_ID<=0";
		result = db1.update(command);
		
		command = " UPDATE LOCKBOX_UPLOADS  SET PAYEE_ID = U.USERS_ID FROM LOCKBOX_UPLOADS as  L INNER JOIN REF_PROJECT_USERS as  R on L.PROJECT_ID = R.PROJECT_ID AND R.ACTIVE='Y'"
				+ "JOIN REF_USERS AS U on R.REF_USERS_ID = U.ID AND U.LKUP_USERS_TYPE_ID=11"
				+ "WHERE L.ACCOUNT_NUMBER >0 AND L.PROJECT_ID>0 AND PROCESS_ID='"+random+"'";
		result = db1.update(command);
		
		db1.clear();
		
		return result;
	}
	
	
	
	
	public boolean updateTaskResults(TaskVO t,ArrayList<HashMap<String,String>> processids) throws Exception{
		boolean result = false;
		StringBuffer sb = new StringBuffer();
		int userId = 890;
		String repeat = "Y";
		if(t.isRepeat()){
			repeat = "R";
		}
		
		sb.append(" INSERT INTO TASKS_ACT_RESULTS (REF_ACT_TYPE_TASKS_ID,ACTIVITY_ID,RESULT,CREATED_BY,UPDATED_BY) VALUES (?,?,?,?,?)");
		
		
		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try {
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			for(HashMap<String,String> p :processids){
					
					ps.setInt(1, t.getId());
				    ps.setInt(2, Operator.toInt(p.get("ID")));
				    
				    ps.setString(3, repeat);
				    ps.setInt(4, userId);
				    ps.setInt(5, userId);
				    
				    ps.addBatch();
					
				}
			
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
		}
		catch(Exception e){
			db.CONNECTION.rollback();
			Logger.error(e);
			throw e;
		}
		finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}
	
	/*public boolean updateCommunication(ArrayList<HashMap<String,String>> notify) throws Exception{
		boolean result = false;
		StringBuffer sb = new StringBuffer();
		int userId = 890;
		sb.append(" INSERT INTO TASKS_ACT_RESULTS (REF_ACT_TYPE_TASKS_ID,ACTIVITY_ID,RESULT,CREATED_BY,UPDATED_BY) VALUES (?,?,?,?,?)");
		
		
		Sage db = new Sage();
		db.connect();
		PreparedStatement ps  = null;
		try {
			
			ps = db.CONNECTION.prepareStatement(sb.toString());
			db.CONNECTION.setAutoCommit(false);
			for(HashMap<String,String> p :processids){
					
					ps.setInt(1, t.getId());
				    ps.setInt(2, Operator.toInt(p.get("ID")));
				    ps.setString(3, "Y");
				    ps.setInt(4, userId);
				    ps.setInt(5, userId);
				    
				    ps.addBatch();
					
				}
			
			ps.executeBatch();
			db.CONNECTION.commit();
		
			result =true;
		}
		catch(Exception e){
			db.CONNECTION.rollback();
			Logger.error(e);
			throw e;
		}
		finally{
			ps.close();
			db.CONNECTION.setAutoCommit(true);
			db.CONNECTION.close();  
			db.clear();
		}
		
		return result;
	}*/

}
