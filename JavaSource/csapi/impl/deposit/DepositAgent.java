package csapi.impl.deposit;

import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.common.Choices;
import csapi.impl.finance.FinanceAgent;
import csapi.impl.finance.FinanceFields;
import csapi.impl.finance.FinanceSQL;
import csapi.impl.people.PeopleSQL;
import csapi.utils.objtools.Types;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.finance.DepositCreditVO;
import csshared.vo.finance.PaymentVO;
import csshared.vo.finance.StatementVO;

/**
 * @author svijay
 *
 */
public class DepositAgent {

	public static TypeVO getPaymentDetails(RequestVO vo) {
		Token u = Token.retrieve(vo.getToken(), vo.getIp());
		TypeVO result = Types.getType(vo.getType(), vo.getTypeid(), vo.getEntity(), vo.getOption(), u);
		//result.setType("finance");
		//PaymentVO g = FinanceFields.paymentdetails(getPayees(vo));
		if(vo.getType().equalsIgnoreCase("users")){
			PaymentVO g = FinanceFields.paymentdetails(Choices.getChoices(PeopleSQL.getUserPeople(vo.getTypeid())));
			
			PaymentVO[] p = new PaymentVO[1];
			p[0] = g;
			result.setPayment(p);
		}else {
			PaymentVO g = FinanceFields.paymentdetails(Choices.getChoices(PeopleSQL.getActivityPeople(vo.getTypeid())));
			
			PaymentVO[] p = new PaymentVO[1];
			p[0] = g;
			
			/*PaymentVO[] p = getPayments(userId);
			
			PaymentVO[] c= new PaymentVO[1+p.length];
			
			c[0] = g;
			for(int i=0;i<c.length;i++){
				if(i==0){
					c[i] = g;
				}else {
					c[i] = p[];
				}
			}
			*/
			
			result.setPayment(p);
		}
		return result;
	}
	
	public static SubObjVO[] getPayees(RequestVO vo){
		SubObjVO[] s = new SubObjVO[0];
		try{
			StringBuilder sb = new StringBuilder();
			
			StatementVO [] st = vo.getStatements();
			
			//TODO SQL
			Logger.info(st.length+"***activity people");
			int sz = st.length;
			for(int i=0;i<sz;i++){
				sb.append(PeopleSQL.details("activity", st[i].getActivityid(),-1));
				sb.append(" UNION ");
			}
			Logger.info(sb.toString());
			sb.append(PeopleSQL.details("activity", -10,-1));
			
			Sage db = new Sage();
			db.query(sb.toString());
			s = new SubObjVO[db.size()];
			int i =0;
			while(db.next()){
				SubObjVO v = new SubObjVO();
				v.setId(db.getInt("ID"));
				v.setText(db.getString("NAME"));
				v.setValue(db.getString("NAME"));
				s[i] = v;
				i++;
			}
			db.clear();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return s;
	}
	
	public static ResponseVO saveDeposit(ResponseVO o,RequestVO r, Token u){
		try{
			PaymentVO p = r.getPayment();
			//p.setTransactiontype(2);
			double amount = p.getAmount();
			TypeVO t = new TypeVO();
			
			
			boolean result = false;
			int paymentId =0;
			
			Sage db = new Sage();
			
			String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
			result = db.update(command);
			
			if(result){
				db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
				
				if(db.next()){
					paymentId = db.getInt("ID");
				}
			
				if(paymentId>0){
					JSONObject f = new JSONObject();
					f.put("paymentid", paymentId);
					f.put("statementdetailid", 0);
					f.put("amount", amount);
					f.put("feeid", 0);
					
					command = "select TOP 1 FM.*  from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null ";
					db.query(command);
					if(db.next()){
						//f.put("feeid", 0);
						f.put("financemapid", db.getInt("ID"));
						f.put("keycode", db.getString("KEY_CODE"));
						f.put("accountnumber", db.getString("ACCOUNT_NUMBER"));
						f.put("budgetunit", db.getString("BUDGET_UNIT"));
						f.put("fund", db.getString("FUND"));
					}
					
					command = FinanceSQL.insertPaymentDetail(f, r.getIp(),  u.getId());
					Logger.info(command);
					if(Operator.hasValue(command)){
						result = db.update(command);
					}
					
					
					
				}
				
			}
			
			//insert ref
			if(result && paymentId>0){
				command = FinanceSQL.insertRefPayment(r.getType(),r.getTypeid(),paymentId, u.getId());
				result = db.update(command);
				
				DepositCreditVO d = new DepositCreditVO();
				d.setParentid(0);
				d.setPaymentid(paymentId);
				d.setComment(p.getComment());
				d.setAmount(amount);
				d.setCurrentamount(amount);
				if(p.getTransactiontype()==4){
					d.setType(4);
				}else {
					d.setType(1);
				}
				
				command = DepositSQL.insertDeposit(d, u.getId(), r.getIp());
				result = db.update(command);
				
				
				db.query(DepositSQL.getDepositId(r.getIp(),  u.getId(),paymentId));
				
				int depositId =0;
				if(db.next()){
					depositId = db.getInt("ID");
				}
				
				if(depositId>0){
					command = DepositSQL.insertRefDeposit(r.getType(),r.getTypeid(),depositId, u.getId());
					result = db.update(command);
				}
				
			}
			
			db.clear();
			
		/*	Logger.info("CHECK "+result);
			Logger.info("CHECK "+paymentId);
			if(paymentId>0){
				
				FinanceAgent.updatestatements(paymentId, u.getId());
			}*/
			if(result){
				t.setPayment(FinanceAgent.getPayments(u.getId()));
				o.setType(t);
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("Problem while processing payment ");
		}
		return o;
	}
	
	public static TypeVO getDepositPayees(RequestVO r) {
		TypeVO result = new TypeVO();
		
		StatementVO[] s = r.getStatements();
		StringBuilder activity = new StringBuilder();
		StringBuilder project = new StringBuilder();
		for(int i=0;i<s.length;i++){
			Logger.info("*************"+s[i].getType());
			if(s[i].getType().equalsIgnoreCase("A")){
				activity.append(s[i].getActivityid()).append(",");
			}else {
				project.append(s[i].getActivityid()).append(",");
			}
		}
		activity.append("0");
		project.append("0");
		
		Sage db = new Sage();
		String command = DepositSQL.getDepositAmount(activity.toString(), project.toString(),s);
		db.query(command);
		
		DepositCreditVO[] dvo = new DepositCreditVO[db.size()];
		int count =0;
		while(db.next()){
			DepositCreditVO vo = new DepositCreditVO();
			vo.setAmount(db.getDouble("AMOUNT"));
			vo.setLevel(db.getString("TYPE"));
			vo.setTypename(db.getString("TYPENAME"));
			vo.setType(db.getInt("LKUP_DEPOSIT_TYPE_ID"));
			vo.setParentid(db.getInt("ID"));
			vo.setId(db.getInt("CONNECTID"));
			dvo[count] = vo;
			count++;
			
		}
		db.clear();
		
		result.setDepositcredits(dvo);
		return result;
	}
	
	
	public static boolean  insertUpdateDepositCredit(DepositCreditVO vo,double amount,int paymentId,int userId,String ip,String leveltype){
		Logger.info("insertUpdateDepositCredit");
		boolean result = false;
		try{
			
			String command = "";
			Sage db = new Sage();
			Sage db2 = new Sage();
			
			
			
			
			command = DepositSQL.getDepositAmount(vo.getParentid(), vo.getLevel(), vo.getType());
			db.query(command);
			//JSONArray darr = new JSONArray();
			double subamount =0;
			double tamount = amount;
			boolean loop= true;
			while(db.next()){
				if(loop){
					double damount = db.getDouble("AMOUNT");
					double dcurrentamount = db.getDouble("CURRENT_AMOUNT");
					subamount = FinanceAgent.subDouble(dcurrentamount,tamount);
					int id = db.getInt("ID");
					if(subamount>0){
						
						DepositCreditVO d = new DepositCreditVO();
						d.setAmount(-tamount);
						d.setType(6);
						d.setPaymentid(paymentId);
						d.setCurrentamount(0);
						d.setParentid(id);
						
						command = DepositSQL.insertDeposit(d, userId, ip);
						result = db2.update(command);
						
						
						db2.query(DepositSQL.getDepositId(ip, userId, paymentId));
						
						int depositId =0;
						if(db2.next()){
							depositId = db2.getInt("ID");
						}
						
						
						if(depositId>0){
							command = DepositSQL.insertRefDeposit(leveltype,vo.getParentid(),depositId, userId);
							result = db2.update(command);
						}
						
						//update deposit current
						result = db2.update(DepositSQL.updateDeposit(subamount, id));
						
						loop = false;
						
						
					}else {
						double dsubcurrent = dcurrentamount;
						//if(sub)
						tamount = FinanceAgent.subDouble(tamount,dsubcurrent);
						//tamount =  Math.abs(subamount);
						DepositCreditVO d = new DepositCreditVO();
						d.setAmount(-dsubcurrent);
						d.setType(6);
						d.setPaymentid(paymentId);
						d.setCurrentamount(0);
						d.setParentid(id);
						
						command = DepositSQL.insertDeposit(d, userId, ip);
						result = db2.update(command);
						
						
						db2.query(DepositSQL.getDepositId(ip, userId, paymentId));
						
						int depositId =0;
						if(db2.next()){
							depositId = db2.getInt("ID");
						}
						
						if(depositId>0){
							command = DepositSQL.insertRefDeposit(leveltype,vo.getParentid(),depositId, userId);
							result = db2.update(command);
						}
						//update deposit current
						result = db2.update(DepositSQL.updateDeposit(0, id));
						
					}
					
			
				}
			}
			
			db2.clear();
			db.clear();
			
			//}
			
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return result;
	}
	
	
	public static boolean reversePartialPayment(RequestVO r, int userId) {
		boolean result = false;
		try{
			PaymentVO p = r.getPayment();
			boolean deposit = false;
			
			
			
			if(Operator.toInt(r.getGroupid())>0 && p.getPaymentid()>0 && p.getRevpaymentid()>0){
				Sage db = new Sage();
				String command = FinanceSQL.insertPayment(p, userId, r.getIp());
				result = db.update(command);
				
				if(result){
					int paymentId = 0;
					db.query(FinanceSQL.getPaymentId(r.getIp(),  userId));
					
					if(db.next()){
						paymentId = db.getInt("ID");
					}
					
					if(paymentId>0){
						JSONObject f = new JSONObject();
						f.put("statementdetailid", r.getGroupid());
						f.put("amount",p.getAmount());
						f.put("revpaymentid", p.getRevpaymentid());
						f.put("revtransid", p.getPaymentid());
						f.put("paymentid", paymentId);
						
						command = FinanceSQL.insertPaymentDetail(f, r.getIp(), userId);
						result = db.update(command);
						
						command = FinanceSQL.getMethodDetail(p.getMethod());
						db.query(command);
						if(db.next()){
							deposit = Operator.s2b(db.getString("APPLY_DEPOSIT"));
						}
						
						if(deposit){
						
							
							
							
							DepositCreditVO d = new DepositCreditVO();
							d.setType(2);
							d.setAmount(Math.abs(p.getAmount()));
							d.setCurrentamount(d.getAmount());
							d.setParentid(0);
							d.setPaymentid(paymentId);
							
							command = DepositSQL.insertDeposit(d, userId, r.getIp());
							result = db.update(command);
							
							
							db.query(DepositSQL.getDepositId(r.getIp(), userId, paymentId));
							int depositId =0;
							if(db.next()){
								depositId = db.getInt("ID");
							}
							
							if(depositId>0){
								String type = r.getType();
								int typeId = r.getTypeid();
								
								if(p.getPayeeid()>0){
									type = "USERS";
									typeId = p.getPayeeid();
								}
								
								StringBuilder sb = new StringBuilder();
								sb.append(" insert INTO payment_detail(payment_id,AMOUNT,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
								sb.append(" (SELECT TOP 1  ").append(paymentId).append(" AS payment_id,").append(Math.abs(p.getAmount())).append(", ").append(userId).append(",").append(userId).append(",FM.ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND   from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null) ");
								result = db.update(sb.toString());
								
								
								command = DepositSQL.insertRefDeposit(type,typeId,depositId, userId);
								result = db.update(command);
								
								
								command = "update payment_detail set REV_AMOUNT= REV_AMOUNT+"+p.getAmount()+" where ID = "+p.getPaymentid();
								result = db.update(command);
								
								
								command = "update payment set REV_AMOUNT= REV_AMOUNT+"+p.getAmount()+" where ID = "+p.getRevpaymentid();
								result = db.update(command);
								
							
								
								result = FinanceAgent.updatestatements(paymentId, userId);
							}
						}else {
							command = "update payment_detail set REV_AMOUNT= REV_AMOUNT+"+p.getAmount()+" where ID = "+p.getPaymentid();
							result = db.update(command);
							
							
							command = "update payment set REV_AMOUNT= REV_AMOUNT+"+p.getAmount()+" where ID = "+p.getRevpaymentid();
							result = db.update(command);
							
						
							
							result = FinanceAgent.updatestatements(paymentId, userId);
						}
						
					}
				}
				
				db.clear();
			}	
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		return result;
		
	}
	
	
	
	public static boolean reversePayment(int paymentId, int userId, String ip) {
		boolean result = false;
		Sage db = new Sage();
		Sage db2 = new Sage();
		try{
			if(userId<=0){
				userId = 822;
			}
			if(paymentId>0 && userId>0){
			
				StringBuilder sb = new StringBuilder();
				sb.append(" select distinct RAS.ACTIVITY_ID,SUM(pd.AMOUNT) as AMOUNT from payment_detail pd "); 
				sb.append(" join statement_detail sd on pd.statement_detail_id = sd.id ");
				sb.append(" left outer join REF_ACT_STATEMENT RAS on sd.statement_id= RAS.STATEMENT_ID ");
				sb.append(" where pd.payment_id=").append(paymentId).append(" group by RAS.ACTIVITY_ID ");
				String command = sb.toString();
				db.query(command);
				//TODO insert +ve amount or not check with user requirement
				while(db.next()){
						
					DepositCreditVO d = new DepositCreditVO();
					d.setType(2);
					d.setAmount(Math.abs(db.getDouble("AMOUNT")));
					d.setCurrentamount(d.getAmount());
					d.setParentid(0);
					d.setPaymentid(paymentId);
					int activityId = db.getInt("ACTIVITY_ID");
					command = DepositSQL.insertDeposit(d, userId, ip);
					result = db2.update(command);
					
					
					db2.query(DepositSQL.getDepositId(ip, userId, paymentId));
					int depositId =0;
					if(db2.next()){
						depositId = db2.getInt("ID");
					}
					
					
					db2.query(FinanceSQL.getPayment(paymentId));
					int payeeId =0;
					if(db2.next()){
						payeeId = db2.getInt("PAYEE_ID");
					}
					
					if(depositId>0 && payeeId>0){
						command = DepositSQL.insertRefDeposit("users",payeeId,depositId, userId);
						result = db2.update(command);
						
					}
					
					if(depositId>0 && payeeId==0){
						command = DepositSQL.insertRefDeposit("activity",activityId,depositId, userId);
						result = db2.update(command);
						
					}
						
				
				}
				
				
			}	
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		db2.clear();
		db.clear();
		return result;
		
	}
	
	//TODO all levels with deposit/credit split
	public static DepositCreditVO[] getDepositCredits(String type, int typeId){
		DepositCreditVO[] da = new DepositCreditVO[0];
		
		Sage db = new Sage();
		db.query(DepositSQL.summary(type, typeId, 0));
		da = new DepositCreditVO[db.size()];
		int counter =0;
		while(db.next()){
			DepositCreditVO d = new DepositCreditVO();
			d.setLevel(db.getString("GROUPNAME"));
			d.setAmount(db.getDouble("AMOUNT"));
			da[counter] = d;
			counter++;
		}
		db.clear();
		
		return da;
		
	}
	
	
	public static TypeVO depositlist(RequestVO r) {
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO t = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);

		try{
		
			
			
			
			String command = DepositSQL.depositlist(r);
			Sage db = new Sage();
			db.query(command);
			
			
			
			DepositCreditVO sv[] = new DepositCreditVO[db.size()];
			int s =0;
			while(db.next()){
				DepositCreditVO svo = new DepositCreditVO();
				svo.setPaymentid(db.getInt("PAYMENT_ID"));
				svo.setParentid(db.getInt("PARENT_ID"));
				svo.setCreateddate(db.getString("CREATEDDATE"));
				svo.setId(db.getInt("ID"));
				svo.setAmount(db.getDouble("AMOUNT"));
				svo.setCurrentamount(db.getDouble("CURRENT_AMOUNT"));
				svo.setType(db.getInt("LKUP_DEPOSIT_TYPE_ID"));
				if(svo.getType()==1){
					svo.setTypename("Deposit");
				}
				if(svo.getType()==2){
					svo.setTypename("Credits");
				}
				if(svo.getType()==4){
					svo.setTypename("Surety Deposit");
				}
				 
				//svo.setStatements(getActivityStatements(db.getInt("ID"),db.getDouble("AMOUNT"),r.getId()));
				sv[s] = svo;
				s++;
			}
			db.clear();
			
			t.setDepositcredits(sv);
			
			} catch (Exception e){
				e.printStackTrace();
				t.setSubtitle(e.getMessage());
			}
			return t;
		}
	
	
	public static DepositCreditVO[] showdepositledger(RequestVO r){
		DepositCreditVO[] fa = new DepositCreditVO[0];
		PaymentVO p = r.getPayment();
		int paymentId = p.getPaymentid();
		if(paymentId>0){		
			Sage db = new Sage();
			
			String command = DepositSQL.depositdetail(paymentId);
			db.query(command);
			fa = new DepositCreditVO[db.size()];
			int counter =0;
			while(db.next()){
				DepositCreditVO svo = new DepositCreditVO();
				svo.setPaymentid(db.getInt("PAYMENT_ID"));
				svo.setParentid(db.getInt("PARENT_ID"));
				svo.setCreateddate(db.getString("CREATED_DATE"));
				svo.setId(db.getInt("ID"));
				svo.setAmount(db.getDouble("AMOUNT"));
				svo.setCurrentamount(db.getDouble("CURRENT_AMOUNT"));
				svo.setType(db.getInt("LKUP_DEPOSIT_TYPE_ID"));
				
				
				fa[counter] = svo;
				counter++;
			}
			
			db.clear();
		}
		
		
		return fa;
	}
	
	
	
	public static TypeVO getDepositOptions(RequestVO r,Token u) {
		
		TypeVO result = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);
		Sage db = new Sage();
		String command = DepositSQL.getDepositOptions(r);
		db.query(command);
		
		DepositCreditVO[] dvo = new DepositCreditVO[db.size()];
		int count =0;
		while(db.next()){
			DepositCreditVO vo = new DepositCreditVO();
			vo.setLevel(db.getString("LEVEL"));
			vo.setId(db.getInt("ID"));
			vo.setTypename(db.getString("LEVEL_REF")+" "+db.getString("TEXT"));
			dvo[count] = vo;
			count++;
			
		}
		db.clear();
		
		result.setDepositcredits(dvo);
		return result;
	}
 	
}
