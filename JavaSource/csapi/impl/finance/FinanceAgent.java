package csapi.impl.finance;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.security.Token;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Numeral;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activity.ActivitySQL;
import csapi.impl.custom.CustomSQL;
import csapi.impl.deposit.DepositAgent;
import csapi.impl.deposit.DepositSQL;
import csapi.impl.divisions.DivisionsAgent;
import csapi.impl.general.DBBatch;
import csapi.impl.general.GeneralAgent;
import csapi.impl.people.PeopleSQL;
import csapi.impl.tasks.TasksImpl;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Fields;
import csapi.utils.objtools.Tools;
import csapi.utils.objtools.Types;
import csapi.utils.validate.ValidateItype;
import csshared.utils.ObjMapper;
import csshared.vo.BrowserHeaderVO;
import csshared.vo.BrowserItemVO;
import csshared.vo.BrowserSearchVO;
import csshared.vo.BrowserVO;
import csshared.vo.DivisionsList;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;
import csshared.vo.finance.DepositCreditVO;
import csshared.vo.finance.FeeVO;
import csshared.vo.finance.FeesGroupVO;
import csshared.vo.finance.FinanceVO;
import csshared.vo.finance.PaymentVO;
import csshared.vo.finance.StatementVO;

/**
 * @author svijay
 *
 */
public class FinanceAgent {

	public static String searchurl = Config.rooturl()+"/csapi/rest/finance/cashier/search";
	
	/*public static FinanceVO getFeesOld(RequestVO r){
		FinanceVO vo = new FinanceVO();
		TypeVO t = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption());
		if(Operator.toInt(r.getGroupid())>0){
			
		}else {
			
			String command = FinanceSQL.getFeeGroups(r);
			Sage db = new Sage();
			Sage db2 = new Sage();
			db.query(command);
			FeesGroupVO[] groups = new FeesGroupVO[db.size()];
			int count =0;
			while(db.next()){
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(db.getString("GROUP_NAME"));
				g.setGroupid(db.getInt("GROUP_ID"));
				g.setAmount(db.getDouble("FEE_AMOUNT"));
				g.setPaidamount(db.getDouble("FEE_PAID"));
				g.setBalancedue(db.getDouble("BALANCE_DUE"));
				
				command = FinanceSQL.getFeeGroupsFees(r,g.getGroupid());
				db2.query(command);
				FeeVO[] farr = new FeeVO[db2.size()];
				int fcount =0;
				while(db2.next()){
					FeeVO f = new FeeVO();
					f.setGroup(db2.getString("GROUP_NAME"));
					f.setGroupid(db2.getInt("GROUP_ID"));
					f.setFeeid(db2.getInt("ID"));
					f.setName(db2.getString("NAME"));
					f.setFormula(db2.getString("DEFINITION"));
					f.setRequired(db2.getString("REQ"));
					f.setStartdate(db2.getString("START_DATE"));
					f.setInput1(db2.getDouble("INPUT1"));
					f.setInput2(db2.getDouble("INPUT2"));
					f.setInput3(db2.getDouble("INPUT3"));
					f.setInput4(db2.getDouble("INPUT4"));
					f.setInput5(db2.getDouble("INPUT5"));
					f.setAmount(db2.getDouble("FEE_AMOUNT"));
					f.setPaidamount(db2.getDouble("FEE_PAID"));
					f.setBalancedue(db2.getDouble("BALANCE_DUE"));
					f.setFeedate(db2.getString("FEE_DATE"));
					farr[fcount] = f;
					fcount++;
					
				}
				g.setFees(farr);
				
				groups[count] = g;
				count++;
			
			}
			db2.clear();
			db.clear();
			
			vo.setGroups(groups);
			
		}
		vo.setTitle(t.getTitle());
		vo.setSubtitle(t.getSubtitle());
		vo.setAlert(t.getAlert());
		vo.setTypeid(r.getTypeid());
		vo.setType(r.getType());
		vo.setGroup(r.getGroup());
		vo.setGroupid(Operator.toInt(r.getGroupid()));
		vo.setGrouptype(r.getGrouptype());
		
		return vo;
	}
	
	public static TypeVO getFeesold(RequestVO r){
		Logger.info("getFees");
		TypeVO vo = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption());
		double amount =0;
		double paidamount =0;
		double balancedue =0;
		if(Operator.toInt(r.getGroupid())>0){
			
		}else {
			
			String command = FinanceSQL.getFeeGroups(r);
			Sage db = new Sage();
			Sage db2 = new Sage();
			db.query(command);
			
			
			
			FeesGroupVO[] groups = new FeesGroupVO[db.size()];
			int count =0;
			while(db.next()){
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(db.getString("GROUP_NAME"));
				g.setGroupid(db.getInt("GROUP_ID"));
				if(db.getDouble("ACTIVITY_FEE")>0){
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
				}
				
				if(g.getAmount()>0){
					amount = Operator.addDouble(g.getAmount(), amount);
				}
				if(g.getPaidamount()>0){
					paidamount = Operator.addDouble(g.getPaidamount(), paidamount);
				}
				command = FinanceSQL.getFeeGroupsFees(r,g.getGroupid());
				db2.query(command);
				FeeVO[] farr = new FeeVO[db2.size()];
				int fcount =0;
				while(db2.next()){
					FeeVO f = new FeeVO();
					f.setGroup(db2.getString("GROUP_NAME"));
					f.setGroupid(db2.getInt("GROUP_ID"));
					f.setFeeid(db2.getInt("FEE_ID"));
					f.setName(db2.getString("NAME"));
					f.setFormula(db2.getString("DEFINITION"));
					f.setRequired(db2.getString("REQ"));
					f.setStartdate(db2.getString("START_DATE"));
					f.setInput1(db2.getDouble("INPUT1"));
					f.setInput2(db2.getDouble("INPUT2"));
					f.setInput3(db2.getDouble("INPUT3"));
					f.setInput4(db2.getDouble("INPUT4"));
					f.setInput5(db2.getDouble("INPUT5"));
					f.setAmount(db2.getDouble("FEE_AMOUNT"));
					f.setPaidamount(db2.getDouble("FEE_PAID"));
					f.setBalancedue(db2.getDouble("BALANCE_DUE"));
					f.setFeedate(db2.getString("FEE_DATE"));
					f.setStatementdetailid(db2.getInt("ID"));
					
					f.setFinancemapid(db2.getInt("FINANCE_MAP_ID"));
					f.setAccount(db2.getString("ACCOUNT_NUMBER"));
					
					f.setFormulaId(db2.getInt("LKUP_FORMULA_ID"));
					f.setInputtype1(db2.getInt("INPUT_TYPE1"));
					f.setInputtype2(db2.getInt("INPUT_TYPE2"));
					f.setInputtype3(db2.getInt("INPUT_TYPE3"));
					f.setInputtype4(db2.getInt("INPUT_TYPE4"));
					f.setInputtype5(db2.getInt("INPUT_TYPE5"));
					
					f.setInputtypelabel1(db2.getString("INPUT_LABEL1"));
					f.setInputtypelabel2(db2.getString("INPUT_LABEL2"));
					f.setInputtypelabel3(db2.getString("INPUT_LABEL3"));
					f.setInputtypelabel4(db2.getString("INPUT_LABEL4"));
					f.setInputtypelabel5(db2.getString("INPUT_LABEL5"));
					
					f.setStartdate(db2.getString("START_DATE"));
					f.setExpdate(db2.getString("EXPIRATION_DATE"));
					f.setActive(db2.getString("ACTIVE"));
					
					farr[fcount] = f;
					fcount++;
					
				}
				g.setFees(farr);
				
				groups[count] = g;
				count++;
			
			}
			db2.clear();
			db.clear();
			
			if(groups.length>0){
				StatementVO[] svo = new StatementVO[1];
				StatementVO s = new StatementVO();
				s.setGroups(groups);
				
				s.setAmount(amount);
				s.setPaidamount(paidamount);
				s.setBalancedue(subDouble(amount, paidamount));
				
				svo[0] = s;
				vo.setStatements(svo);
			}
			//vo.setGroups(groups);
			
		}
		
		vo.setTypeid(r.getTypeid());
		vo.setType(r.getType());
	
		
		return vo;
	}*/
	
	public static TypeVO getFees(RequestVO r){
		Logger.info("getFees");
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO vo = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);
		double amount =0;
		double paidamount =0;
		double balancedue =0;
		Sage db = new Sage();
		Sage db2 = new Sage();
		
		try{
		if(Operator.toInt(r.getGroupid())>0){
			
		}else {
			
			String command = FinanceSQL.getFeeGroups(r);
			
			db.query(command);
			
			int sz = db.size();
			Logger.info(sz+"feeeeeeeeeeeeee");
			StatementVO s = new StatementVO();
			FeesGroupVO[] groups = new FeesGroupVO[db.size()-1];
			int count =0;
			while(db.next()){
				if(db.getInt("GROUP_ID")>0){
					FeesGroupVO g = new FeesGroupVO();
					g.setGroup(db.getString("GROUP_NAME"));
					g.setGroupid(db.getInt("GROUP_ID"));
				
					g.setAmount(db.getDouble("ACTIVITY_FEE"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
					g.setReviewAmount(db.getDouble("REVIEW_FEE"));
					
					/*if(db.getDouble("FEE_AMOUNT")>0){
						g.setAmount(db.getDouble("FEE_AMOUNT"));
						g.setPaidamount(db.getDouble("FEE_PAID"));
						g.setBalancedue(db.getDouble("BALANCE_DUE"));
					}*/
					
					/*if(g.getAmount()>0){
						amount = Operator.addDouble(g.getAmount(), amount);
					}
					if(g.getPaidamount()>0){
						paidamount = Operator.addDouble(g.getPaidamount(), paidamount);
					}*/
				
					command = FinanceSQL.getFeeGroupsFees(r,g.getGroupid());
					db2.query(command);
					FeeVO[] farr = new FeeVO[db2.size()];
					int fcount =0;
					while(db2.next()){
						FeeVO f = new FeeVO();
						f.setGroup(db2.getString("GROUP_NAME"));
						f.setGroupid(db2.getInt("GROUP_ID"));
						f.setFeeid(db2.getInt("FEE_ID"));
						f.setName(db2.getString("NAME"));
						//f.setFormula(db2.getString("DEFINITION"));
						f.setRequired(db2.getString("REQ"));
						f.setStartdate(db2.getString("START_DATE"));
						f.setInput1(db2.getDouble("INPUT1"));
						f.setInput2(db2.getDouble("INPUT2"));
						f.setInput3(db2.getDouble("INPUT3"));
						f.setInput4(db2.getDouble("INPUT4"));
						f.setInput5(db2.getDouble("INPUT5"));
						f.setAmount(db2.getDouble("FEE_AMOUNT"));
						f.setPaidamount(db2.getDouble("FEE_PAID"));
						f.setBalancedue(db2.getDouble("BALANCE_DUE"));
						f.setFeedate(db2.getString("FEE_DATE"));
						f.setStatementdetailid(db2.getInt("ID"));
						
						f.setFinancemapid(db2.getInt("FINANCE_MAP_ID"));
						f.setAccount(db2.getString("ACCOUNT_NUMBER"));
						
						f.setFormulaId(db2.getInt("LKUP_FORMULA_ID"));
						f.setInputtype1(db2.getInt("INPUT_TYPE1"));
						f.setInputtype2(db2.getInt("INPUT_TYPE2"));
						f.setInputtype3(db2.getInt("INPUT_TYPE3"));
						f.setInputtype4(db2.getInt("INPUT_TYPE4"));
						f.setInputtype5(db2.getInt("INPUT_TYPE5"));
						
						f.setInputtypelabel1(db2.getString("INPUT_LABEL1"));
						f.setInputtypelabel2(db2.getString("INPUT_LABEL2"));
						f.setInputtypelabel3(db2.getString("INPUT_LABEL3"));
						f.setInputtypelabel4(db2.getString("INPUT_LABEL4"));
						f.setInputtypelabel5(db2.getString("INPUT_LABEL5"));
						
						f.setStartdate(db2.getString("START_DATE"));
						f.setExpdate(db2.getString("EXPIRATION_DATE"));
						f.setActive(db2.getString("ACTIVE"));
						
						f.setEdit(db2.getString("EDIT"));
						f.setInputeditable1(db2.getString("INPUT_EDITABLE1"));
						f.setInputeditable2(db2.getString("INPUT_EDITABLE2"));
						f.setInputeditable3(db2.getString("INPUT_EDITABLE3"));
						f.setInputeditable4(db2.getString("INPUT_EDITABLE4"));
						f.setInputeditable5(db2.getString("INPUT_EDITABLE5"));
						
						
						
						farr[fcount] = f;
						fcount++;
						
					}
					g.setFees(farr);
					
					groups[count] = g;
					count++;
				}
				if(groups.length>0){
					s.setAmount(db.getDouble("ACTIVITY_FEE"));
					s.setPaidamount(db.getDouble("FEE_PAID"));
					s.setBalancedue(db.getDouble("BALANCE_DUE"));
					s.setReviewAmount(db.getDouble("REVIEW_FEE"));
					/*s.setAmount(amount);
				
					s.setPaidamount(paidamount);
					s.setBalancedue(subDouble(amount, paidamount));*/
				}
			}
			
			
			if(groups.length>0){
				StatementVO[] svo = new StatementVO[1];
				
				s.setGroups(groups);
				
				/*s.setAmount(amount);
				s.setPaidamount(paidamount);
				s.setBalancedue(subDouble(amount, paidamount));*/
				
				svo[0] = s;
				vo.setStatements(svo);
			}
			//vo.setGroups(groups);
			
		}
		
		vo.setTypeid(r.getTypeid());
		vo.setType(r.getType());
		
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		db2.clear();
		db.clear();
		
		return vo;
	}

	public static FeesGroupVO getFeesList(RequestVO r){
		Logger.info("getFeesList");
		FeesGroupVO g = new FeesGroupVO();
		
		try {
			Logger.info("FEE_DATE#########"+r.getExtras().get("FEE_DATE")+"");
			String feedate = r.getExtras().get("FEE_DATE");
			if(!Operator.hasValue(feedate)){
				Timekeeper k = new Timekeeper();
				feedate =  k.getString("YYYY/MM/DD");
			}
		
			//TypeVO t = Types.getType(r.getType(), r.getTypeid(), r.getEntity());
			if(Operator.toInt(r.getGroupid())>0){
				String divisionfee = divisionCheckIds(r);
				String command = FinanceSQL.getFeeList(r.getType(), r.getTypeid(), r.getEntity(),Operator.toInt(r.getGroupid()),feedate,divisionfee);
				Sage db = new Sage();
				db.query(command);
				FeeVO[] fees = new FeeVO[db.size()];
				int count =0;
				while(db.next()){
					FeeVO f = new FeeVO();
					f.setFeeid(db.getInt("ID"));
					f.setReffeeformulaid(db.getInt("RFF_ID"));
					
				
					
					String define = db.getString("DEFINITION");
					define = define.replaceAll("currentgroup",db.getString("GROUP_NAME"));
					define = define.replaceAll("currentfeegroup",db.getString("GROUP_NAME"));
					define = define.replaceAll("[\\t\\n\\r]+"," ");
					f.setFormula(define);
					f.setGroup(db.getString("GROUP_NAME"));
					f.setGroupid(db.getInt("GROUP_ID"));
					
					f.setFormulaId(db.getInt("LKUP_FORMULA_ID"));
					f.setRequired(db.getString("REQ"));
					f.setStartdate(db.getString("START_DATE"));
					f.setInput1(db.getDouble("INPUT1"));
					f.setInput2(db.getDouble("INPUT2"));
					f.setInput3(db.getDouble("INPUT3"));
					f.setInput4(db.getDouble("INPUT4"));
					f.setInput5(db.getDouble("INPUT5"));
					f.setFactor(db.getDouble("FACTOR"));
					
					f.setInputtype1(db.getInt("INPUT_TYPE1"));
					f.setInputtype2(db.getInt("INPUT_TYPE2"));
					f.setInputtype3(db.getInt("INPUT_TYPE3"));
					f.setInputtype4(db.getInt("INPUT_TYPE4"));
					f.setInputtype5(db.getInt("INPUT_TYPE5"));
					
					f.setAccountnumber(db.getString("MANUAL_ACCOUNT"));
					
					f.setInputtypelabel1(db.getString("INPUT_LABEL1"));
					f.setInputtypelabel2(db.getString("INPUT_LABEL2"));
					f.setInputtypelabel3(db.getString("INPUT_LABEL3"));
					f.setInputtypelabel4(db.getString("INPUT_LABEL4"));
					f.setInputtypelabel5(db.getString("INPUT_LABEL5"));
					
					f.setInputeditable1(db.getString("INPUT_EDITABLE1"));
					f.setInputeditable2(db.getString("INPUT_EDITABLE2"));
					f.setInputeditable3(db.getString("INPUT_EDITABLE3"));
					f.setInputeditable4(db.getString("INPUT_EDITABLE4"));
					f.setInputeditable5(db.getString("INPUT_EDITABLE5"));
					
					f.setStartdate(db.getString("START_DATE"));
					f.setExpdate(db.getString("EXPIRATION_DATE"));
					
					ObjectMapper mapper = new ObjectMapper();
					//mapper.set
					String json = mapper.writeValueAsString(f);
					f.setDescription(json);
					f.setName(db.getString("NAME"));
					
					fees[count] = f;
					count++;
				}
				g.setFees(fees);
				
				
			/*	//filter fees
				
				command = FinanceSQL.getFeeFilterList(r.getType(), r.getTypeid(), r.getEntity(),Operator.toInt(r.getGroupid()));
				
				List<FeeVO> list2 =  new ArrayList<FeeVO>();
				db.query(command);
					if(db.size()>0){
						while(db.next()){
							
							String opt = db.getString("OPT");
							int feeId = db.getInt("FEE_ID");
							int lkup_divisions_id = db.getInt("LKUP_DIVISIONS_ID");
							int FOUT_LKUP_DIVISIONS_ID = db.getInt("FOUT_LKUP_DIVISIONS_ID");
							
							if(feeId>0 && opt.equals("EQUAL")){
								if(lkup_divisions_id != FOUT_LKUP_DIVISIONS_ID){
									FeeVO f = new FeeVO();
									f.setFeeid(feeId);
									Logger.info(opt+" REMOVE "+feeId);
									list2.add(f);
								}
							}
							
							if(feeId>0 && opt.equals("NOTEQUAL")){
								if(lkup_divisions_id == FOUT_LKUP_DIVISIONS_ID){
									FeeVO f = new FeeVO();
									f.setFeeid(feeId);
									Logger.info(opt+" REMOVE "+feeId);
	
									list2.add(f);
								}
							}
							
							
						}
					
					}*/
				
				db.clear();
				
				/*if(list2.size()>0){
					
					FeeVO[] n = g.getFees();
					Logger.info(n.length+"DELETE LIST ::"+list2.size());
					int sz = n.length-list2.size();
					Logger.info(sz);
					FeeVO[] feesfinal = new FeeVO[sz];
					// Pass 2 - delete
					boolean yn = false;
					count = 0;
					 for (int i=0;i<n.length;i++){
						 FeeVO f = n[i];
						 for(int j=0;j<list2.size();j++){
							 if(f.getFeeid()==list2.get(j).getFeeid()){
								 Logger.info("REMOVING ::"+f.getFeeid());
								 yn=true;
							 }
							
						 }
						 if(!yn){
							 feesfinal[count] = f;
							 count++;
						 }
						 
	
					 }
					
					
					 
					Logger.info("AFTER REMOVE ::"+feesfinal.length);
					g.setFees(feesfinal);
				}*/
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}
		return g;
	}
	
	

	
	
	public static TypeVO calculate(RequestVO o, int userId){
		return calculate(o, userId, false);
	}
	
	public static TypeVO calculate(RequestVO o, int userId, boolean save){
		TypeVO r = new TypeVO();
		Logger.info("Calculate Fees");
		try {
			r.setTypeid(o.getTypeid());
			r.setType(o.getType());
			r.setEntity(o.getEntity());
			
	
			double valuation = getValuation(o.getType(),o.getTypeid());
		
			double total=0;
			boolean loop2 = false;
			boolean loop3 = false;
			boolean loop4 = false;
			boolean loop5 = false; // for subtotal
			int fgarrcounter =0;
			StatementVO[] svo = new StatementVO[1];
			
			
			//TODO query from DB check with ALAIN.
			//TODO custom field one query once available 
			
			
			
			
			
			StatementVO s = new StatementVO();
			s.setActivityid(o.getTypeid());
			FeesGroupVO[] fgarr = new FeesGroupVO[0];
			if(o.getStatements().length>0){
				fgarr = new FeesGroupVO[o.getStatements()[0].getGroups().length];
			
			
			for(int j=0;j<o.getStatements()[0].getGroups().length;j++){
				
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(o.getStatements()[0].getGroups()[j].getGroup());
				g.setGroupid(o.getStatements()[0].getGroups()[j].getGroupid());
				
				
				int feeslength =o.getStatements()[0].getGroups()[j].getFees().length;
				int farrcounter =0;
				FeeVO[] farr = new FeeVO[feeslength];
				Logger.info("feeslength****"+feeslength);
				
				for(int i=0;i<feeslength;i++){
					FeeVO f = o.getStatements()[0].getGroups()[j].getFees()[i];//data.getJSONObject(i);
					Logger.info(f.getGroup()+"INITIAL GROUP LOADING CHECK"+f.getName());
					f.setGroup(g.getGroup());
					f.setGroupid(g.getGroupid());
					int feeId = f.getFeeid();
					//TODO for online replace by query to get formulas from DB
					Logger.info(f.getFeedate()+"################");
					String formula = f.getFormula();
					f.setValuation(valuation);
					if(formula.indexOf("NAMEVALUE")>0){
						
						formula = getNameValue(formula);
						Logger.info("NAME VALUE FORMULA "+formula);
					}
					
					String feedate = Operator.replace(f.getFeedate(), "/", "-");
					
					
					double input1 = f.getInput1();
					double input2 = f.getInput2();
					double input3 = f.getInput3();
					double input4 = f.getInput4();
					double input5 = f.getInput5();
					double factor = f.getFactor();
					//double amount = Operator.toDouble(data.getJSONObject(i).getString("amount"));
					
					
					
					String newFormula = getFormula(o.getType(),o.getTypeid(),formula, Operator.toString(input1), Operator.toString(input2), Operator.toString(input3), Operator.toString(input4), Operator.toString(input5), Operator.toString(valuation),feedate,Operator.toString(factor));
					if(Operator.hasValue(newFormula)){
						Logger.info(feeId+":: index ::"+newFormula.indexOf("feename"));
						double value = 0;
						
					
						
						if(newFormula.indexOf("feename")>0){
							loop2= true;
							f.setModifiedformula(newFormula);
							
						}else if(newFormula.indexOf("sumoffeegroup")>0){
							loop3= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("totalallfees")>0){
							loop4= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("subtotal")>0){
							loop5= true;
							f.setModifiedformula(newFormula);
						}
						else {
							if(newFormula.indexOf("noofmonthsbetween")>0){
								newFormula = FormulaParser.noofmonthsbetween(newFormula);
							}
							value = Formula.calculate(newFormula);
						}
						Logger.info(feeId+":: AMOUNT ::"+value);
						
						f.setAmount(value);
					} else {
						loop2= true;
						//f.put("value", 0);
						f.setAmount(0);
					}
					//newdata.put(f);
					farr[farrcounter] = f;
					farrcounter++;
					
					
					
				}
				Logger.info("feeslength***after*"+farr.length);
				g.setFees(farr);
				fgarr[fgarrcounter] = g;
				fgarrcounter++;
			}
			s.setGroups(fgarr);
			//svo[0] = s;
			//r.setGroups(fgarr);
			
			
			//boolean keepdoing = false;
			
			
			
			if(loop2){
				Logger.info("::feename loop2 ::"+loop2);
				s.setGroups(getFeeNameFormula(o,fgarr));
			}
			
			if(loop5){
				Logger.info("::subtotal loop5 ::"+loop5);
				s.setGroups(getInitSubtotalFormula(o,fgarr));
			}
			
			if(loop3){
				Logger.info("::sumoffeegroup loop3 ::"+loop3);
				s.setGroups(getSumOfFeeGroupFormula(o,fgarr));
			}
			
			if(loop4){
				Logger.info("::totalfees loop4 ::"+loop3);
				s.setGroups(getTotalFeesFormula(o,fgarr));
			}
			
			
			s = getSubTotalsAndTotals(s);
			svo[0] = s;
			r.setStatements(svo);
			if(save){
				boolean result = saveFees(userId,r);
				
			}
			//r.setStatements(svo);
			}
		} catch (Exception e) {
			Logger.error("calculate ERROR  :" + e.getMessage());
		}
		
		
		
		return r;
		
	}
	
	
	
	public static boolean saveFees(int userId, FinanceVO o){
		boolean result = false;
		Logger.info("save Fees");
		try{
			
			String random = Operator.randomString(6);
			double total = o.getAmount();
			Timekeeper k = new Timekeeper();
			k.addDay(60);
			
			
			
			int statementId =0;
			String command = FinanceSQL.insertStatement(random, total, "", k, "N", userId); 
			Sage db = new Sage();
			result = db.update(command);
			
			db.query(FinanceSQL.getStatement(random, userId));
			if(db.next()){
				statementId = db.getInt("ID");
			}
			
			if(statementId>0){
				result = db.update(FinanceSQL.insertRefStatement("activity", o.getTypeid(), statementId, userId));
			}
			
			db.clear();
			
			if(statementId>0 && result){
				result = new DBBatch().insertStatementDetail(o.getGroups(), statementId,userId);
				Logger.info("STATEMENT_DETAIL INSERT "+result);
			}
			
		}catch (Exception e) {
			Logger.error("save ERROR  :" + e.getMessage());
		}
		return result;
	}
	
	
	public static boolean saveFees(int userId, TypeVO o){
		boolean result = false;
		try{
			
			
			String random = Operator.randomString(6);
			double total = o.getAmount();
			Timekeeper k = new Timekeeper();
			k.addDay(60);
			
			
			
			int statementId =0;
			String command = FinanceSQL.insertStatement(random, total, "new stat", k, "N", userId); 
			Sage db = new Sage();
			result = db.update(command);
			
			db.query(FinanceSQL.getStatement(random, userId));
			if(db.next()){
				statementId = db.getInt("ID");
			}
			
			if(statementId>0){
				String pattern = "ST_[YY]_XXXXXXXXX";
				String number = GeneralAgent.getNumber(statementId, pattern);
				String financelock = getTypeFinanceLock(o.getType(), o.getTypeid());
				result = db.update(" UPDATE STATEMENT SET STATEMENT_NUMBER = '"+number+"' , FINANCE_LOCK = '"+financelock+"' where ID = "+statementId+"");
				
				result = db.update(FinanceSQL.insertRefStatement(o.getType(), o.getTypeid(), statementId, userId));
			}
			
			
			
			if(statementId>0 && result){
				result = new DBBatch().insertStatementDetail(o.getStatements()[0].getGroups(), statementId, userId);
				Logger.info("STATEMENT_DETAIL INSERT "+result);
			}
			
			if(result){
				command = "update statement set FEE_AMOUNT=  (select sum(FEE_AMOUNT) from  statement_detail where statement_id = "+statementId+" group by statement_id) where ID ="+statementId;
				db.update(command);
				
				command = "update statement set PAID_AMOUNT=  (select sum(FEE_PAID) from  statement_detail where statement_id = "+statementId+" group by statement_id) where ID ="+statementId;
				db.update(command);
			
				command = "update statement set BALANCE_DUE= (FEE_AMOUNT- PAID_AMOUNT)  where ID = "+statementId;
				db.update(command);
			}
			db.clear();
			
		}catch (Exception e) {
			Logger.error("saveFee ERROR  :" + e.getMessage());
		}
		return result;
	}
	
	public static String getFormula(String type, int typeId,String formula,String input1, String input2, String input3, String input4, String input5, String valuation, String feedate,String factor){
		String s ="";
		
		try {
			
			 s= formula;
			 
			 s = Operator.replace(s, "input1", input1);
			 s = Operator.replace(s, "input2", input2);
			 s = Operator.replace(s, "input3", input3);
			 s = Operator.replace(s, "input4", input4);
			 s = Operator.replace(s, "input5", input5);
			// s = Operator.replace(s, "amount", amount);
			 s = Operator.replace(s, "valuation", valuation);
			 feedate = Operator.replace(feedate, "-", "/");
			 
			 s = Operator.replace(s, "feedate", feedate);
			 s = Operator.replace(s, "factor", factor);
			 //s = Operator.replace(s, "getCustomField(sqft)", getCustomField(typeId));
			
			 /*if(s.indexOf("sumoffeegroup")>0){
				 s ="";
			 }
			 
			 if(s.indexOf("feename")>0){
				 s ="";
			 }*/
			
				if(s.indexOf("_noofdays")>0){
					s = AutoPenalty.getForumulaNoofDays(typeId,s,feedate);
					
				}
			 
			 Logger.info("sunil's          formula" +s);
			 
			
			 
			
		} catch (Exception e) {
			Logger.error("getFormula ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	
	public static FeesGroupVO[] getInitSubtotalFormula(RequestVO r, FeesGroupVO[] fgarr){
		FeesGroupVO[] garr = new FeesGroupVO[fgarr.length];
		try{
		
			JSONArray newdata = new JSONArray();
			for(int j=0;j<fgarr.length;j++){
				int feeslength =fgarr[j].getFees().length;
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(f);
					JSONObject o = new JSONObject(json);
					newdata.put(o);
				}
			}
			
			JSONArray feedata = new JSONArray();
			for(int i=0;i<newdata.length();i++){
				JSONObject f = newdata.getJSONObject(i);
				if(f.has("modifiedformula")){
					String formula = newdata.getJSONObject(i).getString("modifiedformula");
					int feeid = newdata.getJSONObject(i).getInt("feeid");
					Logger.info(" SUBTOTAL"+formula);
					if(formula.indexOf("subtotal")>0){
						int groupid = newdata.getJSONObject(i).getInt("groupid");
						JSONArray subs = compareReplaceInitSubtotal(formula);
						for(int k=0;k<subs.length();k++){
							double amount = getInitsubtotalDb(r, groupid, subs.getJSONObject(k).getInt("levelid"));	
							
							if(amount>=0){
								
								formula = Operator.replace(formula, subs.getJSONObject(k).getString("original"), amount+"");
								formula = Operator.replace(formula, subs.getJSONObject(k).getString("originalzero"), amount+"");
								Logger.info(amount+"RESULTS FOUND IN DB FOR SUBTOTAL"+formula);
							}else {
								
								amount = getInitsubtotal(r,newdata, groupid, subs.getJSONObject(k).getInt("levelid"),feeid);
								formula = Operator.replace(formula, subs.getJSONObject(k).getString("original"), amount+"");
								formula = Operator.replace(formula, subs.getJSONObject(k).getString("originalzero"), amount+"");
								Logger.info(amount+"NO RESULTS IN DB FOR SUBTOTAL"+formula);
							}
							
						}
						
						double value = 0;
						value = Formula.calculate(formula);
						f.put("value", value);
						
					}
					
				}
				feedata.put(f);
			}
			
			for(int j=0;j<fgarr.length;j++){
				FeesGroupVO g = fgarr[j];
				int feeslength =fgarr[j].getFees().length;
				FeeVO[] farr= new FeeVO[feeslength];
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					if(Operator.hasValue(f.getModifiedformula())){
						double o = getFeeName(r,feedata, f.getFeeid());
						f.setAmount(o);
					}
					
					farr[i] =f;
				}
				g.setFees(farr);
				garr[j]= g;
			}
			
			
			//r.setGroups(fgarr);
		}catch (Exception e) {
			Logger.error("getInitSubtotalFormula calculate ERROR  :" + e.getMessage());
			//s ="";
		}
		return garr;
	}
	
	
	public static double getInitsubtotalDb(RequestVO r,int groupid, int levelid){
		double amount =-1;
		try{
			Logger.info(groupid+"getInitsubtotalDb"+levelid);
			
			if(levelid>0){
				levelid = levelid -1;
			}
			Sage db = new Sage();
			
			
			db.query(FinanceSQL.getInitSub(r, groupid));
			int counter = 0;
			while(db.next()){
				
				if(levelid==counter){
					amount = db.getDouble("FEE_AMOUNT");
				}
				counter++;
			}
			
			db.clear();
			
			
			
		}catch (Exception e) {
			Logger.error("getInitsubtotalDb calculate ERROR  :" + e.getMessage());
		
		}
		return amount;
		
	}
	
	public static double getInitsubtotal(RequestVO r,JSONArray g,int groupid,int levelId,int feeid){
		double amount =0;
		try{
			if(levelId>0){
				levelId = levelId -1;
			}
			
			Sage db = new Sage();
			/*StringBuilder sb = new StringBuilder();
			sb.append(" select SD.FEE_AMOUNT FROM STATEMENT_DETAIL SD ");
			sb.append(" JOIN STATEMENT S on SD.STATEMENT_ID = S.ID ");
			sb.append(" left outer join REF_ACT_STATEMENT RAS on S.ID = RAS.STATEMENT_ID ");
			sb.append(" where RAS.ACTIVITY_ID= ").append(r.getTypeid()).append(" AND SD.GROUP_ID= ").append(groupid);
			sb.append(" ORDER BY SD.ID ");*/
			
			db.query(FinanceSQL.getInitSub(r, groupid));
			int counter = 0;
			while(db.next()){
				
				/*if(levelid==counter){
					amount = db.getDouble("FEE_AMOUNT");
				}*/
				counter++;
			}
			
			db.clear();
			
			for(int i=0;i<g.length();i++){
				if(g.getJSONObject(i).getInt("groupid")==groupid){
					Logger.info(i+"CAME########## "+counter);
					if(counter==levelId){
						if(feeid==g.getJSONObject(i).getInt("feeid")){
							counter = counter-1;
						}
						amount = g.getJSONObject(i).getDouble("amount");
					}
					counter++;
					//r = g.getJSONObject(i);
					//break;
				}
			}
			
			
		}catch (Exception e) {
			Logger.error("getInitsubtotal value ERROR  :" + e.getMessage());
		
		}
		return amount;
		
	}
	
	public static FeesGroupVO[] getFeeNameFormula(RequestVO r,FeesGroupVO[] fgarr){
		FeesGroupVO[] garr = new FeesGroupVO[fgarr.length];
		try{
		
			JSONArray newdata = new JSONArray();
			for(int j=0;j<fgarr.length;j++){
				int feeslength =fgarr[j].getFees().length;
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(f);
					JSONObject o = new JSONObject(json);
					Logger.info(f.getAmount()+"NAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+f.getName());
					if(!Operator.hasValue(f.getName())){
						
						o.put("name", getFeeName(f.getFeeid()));
					}else {
						o.put("name", f.getName());
					}
					Logger.info(o.toString());
					newdata.put(o);

				}
			}
			Logger.info(newdata.toString());
			JSONArray feedata = new JSONArray();
			for(int i=0;i<newdata.length();i++){
				JSONObject f = newdata.getJSONObject(i);
				if(f.has("modifiedformula")){
					String formula = newdata.getJSONObject(i).getString("modifiedformula");
					if(formula.indexOf("feename")>0){
						formula = compareReplaceFeeName(r,newdata, formula, "feename");
						Logger.info(f.getString("name")+"feename formula ::"+formula);
						double value = 0;
						value = Formula.calculate(formula);
						Logger.info("value ------------ ::"+value);
						f.put("value", value);
						
					}
					
				}
				feedata.put(f);
			}
			
			for(int j=0;j<fgarr.length;j++){
				FeesGroupVO g = fgarr[j];
				int feeslength =fgarr[j].getFees().length;
				FeeVO[] farr= new FeeVO[feeslength];
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					if(Operator.hasValue(f.getModifiedformula())){
						/*JSONObject o = getFeeName(feedata, f.getFeeid());
						f.setAmount(o.getDouble("value"));*/
						double o = getFeeName(r,feedata, f.getFeeid());
						f.setAmount(o);
						
						Logger.info(f.getAmount()+"oooooooooooooooooooooooooooooooo"+o);
					}
					
					farr[i] =f;
				}
				g.setFees(farr);
				garr[j]= g;
			}
			
			
			//r.setGroups(fgarr);
		}catch (Exception e) {
			Logger.error("feeName calculate ERROR  :" + e.getMessage());
			//s ="";
		}
		return garr;
	}
	
	
	//TODO Custom Order before calculating 
	public static FeesGroupVO[] getSumOfFeeGroupFormula(RequestVO r,FeesGroupVO[] fgarr){
		FeesGroupVO[] garr = new FeesGroupVO[fgarr.length];
		try{
		
			JSONArray newdata = new JSONArray();
			for(int j=0;j<fgarr.length;j++){
				int feeslength =fgarr[j].getFees().length;
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					Logger.info(f.getGroup()+"##################");
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(f);
					JSONObject o = new JSONObject(json);
					newdata.put(o);
				}
			}
			
			JSONArray feedata = new JSONArray();
			for(int i=0;i<newdata.length();i++){
				JSONObject f = newdata.getJSONObject(i);
				if(f.has("modifiedformula")){
					String formula = newdata.getJSONObject(i).getString("modifiedformula");
					if(formula.indexOf("sumoffeegroup")>0){
						formula = compareReplaceSumOfFeeGroup(r,newdata, formula, "sumoffeegroup");
						Logger.info(f.getString("name")+"feename formula ::"+formula);
						double value = 0;
						value = Formula.calculate(formula);
						f.put("value", value);
						
					}
					
				}
				feedata.put(f);
			}
			
			for(int j=0;j<fgarr.length;j++){
				FeesGroupVO g = fgarr[j];
				int feeslength =fgarr[j].getFees().length;
				FeeVO[] farr= new FeeVO[feeslength];
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					if(Operator.hasValue(f.getModifiedformula())){
						/*JSONObject o = getFeeName(feedata, f.getFeeid());
						f.setAmount(o.getDouble("value"));*/
						double o = getFeeName(r,feedata, f.getFeeid());
						f.setAmount(o);
					}
					
					farr[i] =f;
				}
				g.setFees(farr);
				garr[j]= g;
			}
			
			
			//r.setGroups(fgarr);
		}catch (Exception e) {
			Logger.error("feeName calculate ERROR  :" + e.getMessage());
			//s ="";
		}
		return garr;
	}
	
	//TODO SINGLE Loop
	public static FeesGroupVO[] getTotalFeesFormula(RequestVO r,FeesGroupVO[] fgarr){
		FeesGroupVO[] garr = new FeesGroupVO[fgarr.length];
		try{
		
			JSONArray newdata = new JSONArray();
			for(int j=0;j<fgarr.length;j++){
				int feeslength =fgarr[j].getFees().length;
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(f);
					JSONObject o = new JSONObject(json);
					newdata.put(o);
				}
			}
			
			JSONArray feedata = new JSONArray();
			for(int i=0;i<newdata.length();i++){
				JSONObject f = newdata.getJSONObject(i);
				if(f.has("modifiedformula")){
					String formula = newdata.getJSONObject(i).getString("modifiedformula");
					if(formula.indexOf("totalfees")>0){
						formula = compareReplaceTotalAllFees(newdata, formula, "totalallfees");
						Logger.info(f.getString("name")+"feename formula ::"+formula);
						double value = 0;
						value = Formula.calculate(formula);
						f.put("value", value);
						
					}
					
				}
				feedata.put(f);
			}
			
			for(int j=0;j<fgarr.length;j++){
				FeesGroupVO g = fgarr[j];
				int feeslength =fgarr[j].getFees().length;
				FeeVO[] farr= new FeeVO[feeslength];
				for(int i=0;i<feeslength;i++){
					FeeVO f = fgarr[j].getFees()[i];
					if(Operator.hasValue(f.getModifiedformula())){
						/*JSONObject o = getFeeName(feedata, f.getFeeid());
						f.setAmount(o.getDouble("value"));*/
						double o = getFeeName(r,feedata, f.getFeeid());
						f.setAmount(o);
					}
					
					farr[i] =f;
				}
				g.setFees(farr);
				garr[j]= g;
			}
			
			
			//r.setGroups(fgarr);
		}catch (Exception e) {
			Logger.error("feeName calculate ERROR  :" + e.getMessage());
			//s ="";
		}
		return garr;
	}
	
	
	public static String compareReplaceInitSubtotal(double g,String formula, String compiler){
		String s = formula;
		try{
			s = Operator.replace(s, "subtotal(", "subtotal=");
			//s = Operator.replace(s, ")", "");
			s = Operator.replace(s, " ", "_X_B");
			Pattern p = Pattern.compile("(subtotal=)(\\w+)");
			Matcher m = p.matcher(s);
			Logger.info("compare replace before : " + s);
			
			StringBuffer result = new StringBuffer();
			while (m.find()) {
				String subtotal =  m.group(2);
				Logger.info("Masking: " +subtotal);
				String tfeename = Operator.replace(subtotal, "_X_B", " ");
				//JSONObject o = getFeeName(g, tfeename.trim());
				s = Operator.replace(s, "subtotal="+subtotal+")", g+"");
				m.appendReplacement(result, m.group(1) + "***masked***");
			}
			m.appendTail(result);
			s = Operator.replace(s, "_X_B"," ");
			Logger.info("compare replace after: " + s);
		}catch(Exception e){
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	
	 public static JSONArray compareReplaceInitSubtotal(String formula) {
		 JSONArray subs = new JSONArray();
		 try {
				String s = formula;
				s = Operator.replace(s, "subtotal(", "subtotal=");
		
				Pattern p = Pattern.compile("(subtotal=)(\\w+)");
				Matcher m = p.matcher(s);
				Logger.info("compare replace before : " + s);
				
				while (m.find()) {
					String subtotal =  m.group(2);
					Logger.info("Masking: " +subtotal);
					int levelid = Operator.toInt(subtotal);
					JSONObject sub = new JSONObject();
					sub.put("levelid", levelid);
					sub.put("original", "subtotal("+subtotal+")");
					sub.put("originalzero", "subtotal("+subtotal+".0)");
					subs.put(sub);
				}
				
				/*for(int i=0;i<subs.length();i++){
					Logger.info(subs.getJSONObject(i).getInt("levelid"));
					Logger.info(subs.getJSONObject(i).getString("original"));
				}*/
			
			} catch (Exception e) {
				Logger.error("compareReplace ERROR  :" + e.getMessage());
			}
			
			return subs;
			
		}
	 
	 
	 public static String compareReplaceNoofmonths(String formula) {
		 String s = formula;
		 try {
				
				s = Operator.replace(s, "noofmonthsbetween(", "noofmonthsbetween=");
		
				Pattern p = Pattern.compile("(noofmonthsbetween=)(\\w+)");
				Matcher m = p.matcher(s);
				Logger.info(m.find()+"compare replace before : " + s);
				
				while (m.find()) {
					String noofmonths =  m.group(4);
					Logger.info("Masking: " +noofmonths);
					//int levelid = Operator.toInt(subtotal);
					/*JSONObject sub = new JSONObject();
					sub.put("levelid", levelid);
					sub.put("original", "subtotal("+subtotal+")");
					sub.put("originalzero", "subtotal("+subtotal+".0)");
					subs.put(sub);*/
				}
				
				/*for(int i=0;i<subs.length();i++){
					Logger.info(subs.getJSONObject(i).getInt("levelid"));
					Logger.info(subs.getJSONObject(i).getString("original"));
				}*/
			
			} catch (Exception e) {
				Logger.error("compareReplace ERROR  :" + e.getMessage());
			}
			
			return s;
			
		}
	
	/*public static String compareReplaceFeeName(RequestVO r,JSONArray g,String formula, String compiler){
		String s = formula;
		try{
			s = Operator.replace(s, "feename(", "feename=");
			//s = Operator.replace(s, ")", "");
			s = Operator.replace(s, " ", "_X_B");
			Pattern p = Pattern.compile("(feename=)(\\w+)");
			Matcher m = p.matcher(s);
			Logger.info("compare replace before : " + s);
			String keyword = "feename";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
			
			StringBuffer result = new StringBuffer();
			while (m.find()) {
				String feename =  m.group(1);
				Logger.info("Masking: " +feename);
				String tfeename = Operator.replace(feename, "_X_B", " ");
				
				double amount = getFeeName(r, g, tfeename.trim());
				Logger.info(tfeename.trim()+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+amount);
				s = Operator.replace(s, "feename["+feename+"]", amount+"");
				Logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+s);
				m.appendReplacement(result, m.group(1) + "***masked***");
			}
			m.appendTail(result);
			s = Operator.replace(s, "_X_B"," ");
			Logger.info("compare replace after: " + s);
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}*/
	
	public static String compareReplaceFeeName(RequestVO r,JSONArray g,String formula, String compiler){
		String s = formula;
		try{
			String keyword = "feename";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
			
			StringBuffer result = new StringBuffer();
			ArrayList<String> a = new ArrayList<String>();
			StringBuilder all = new StringBuilder();
			while (m.find()) {
				String feename =  m.group(1);
				Logger.info("Masking: " +feename);
				String tfeename = Operator.replace(feename, "_X_B", " ");
				
				all.append("'"+Operator.sqlEscape(feename)+"'");
				all.append(",");
				a.add(tfeename);
				/*double amount = getFeeName(r, g, tfeename.trim());
				Logger.info(tfeename.trim()+"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+amount);
				s = Operator.replace(s, "feename["+feename+"]", amount+"");
				Logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+s);
				m.appendReplacement(result, m.group(1) + "***masked***");*/
			}
			//if(Operator.hasValue(all.toString())){
				all.append("'0'");
			//}
			HashMap<String,Double> h = getFeeNameAll(r, g, all.toString());
			Logger.info("COMPLETED ALL FEE ");
			m = p.matcher(s);
			/*while (m.find()) {
				String feename =  m.group(1);
				Logger.info("Masking 2: " +feename);*/
				for(String f:a){
					//String tfeename = Operator.replace(feename, "_X_B", " ");
					Logger.info(h.get(f)+"**************ARRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR***************"+f);
					double amount = 0;
					if(h.containsKey(f)){
						amount = h.get(f);
					}
					s = Operator.replace(s, "feename["+f+"]",amount+"");
					//Logger.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+s);
					
				}
				//m.appendReplacement(result, m.group(1) + "***masked***");
			//}
			//m.appendTail(result);
			s = Operator.replace(s, "_X_B"," ");
			Logger.info("compare replace after: " + s);
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	public static String compareReplaceSumOfFeeGroup(RequestVO r,JSONArray g,String formula, String compiler){
		String s = formula;
		try{
			//s = Operator.replace(s, "sumoffeegroup(", "sumoffeegroup=");
			//s = Operator.replace(s, ")", "");
			
		/*	s = Operator.replace(s, " ", "_X_B");
			
			Pattern p = Pattern.compile("(sumoffeegroup=)(\\w+)");
			Matcher m = p.matcher(s);
			Logger.info("compare replace before : " + s);*/
			
			String keyword = "sumoffeegroup";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
			
			StringBuffer result = new StringBuffer();
			while (m.find()) {
				String feename =  m.group(1);
				Logger.info("Masking: " +feename);
				String tfeename = Operator.replace(feename, "_X_B", " ");
				double value  = getSumofFeeGroup(r, g, tfeename.trim());
				Logger.info("sumoffeegroup::"+value);
				s = Operator.replace(s, "sumoffeegroup["+feename+"]", value+"");
				m.appendReplacement(result, m.group(1) + "***masked***");
			}
			m.appendTail(result);
			s = Operator.replace(s, "_X_B", " ");
			Logger.info("compare replace after: " + s);
		}catch(Exception e){
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	public static String compareReplaceTotalAllFees(JSONArray g,String formula, String compiler){
		String s = formula;
		try{
			Logger.info("compare replace before : " + s);
			double value  = getTotalAllFees(g);
			Logger.info("totalallfees::"+value);
			s = Operator.replace(s, "totalallfees", value+"");
			Logger.info("compare replace after: " + s);
		}catch(Exception e){
			Logger.error("compareReplace ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	public static String getFormula(String type, int typeId,String formula,JSONArray g){
		String s ="";
		
		try {
		
			if(formula.indexOf("feename")>0){
				// s = 
			 }
			
			
		} catch (Exception e) {
			Logger.error("getFormula ERROR  :" + e.getMessage());
			s ="";
		}
		return s;
	}
	
	
	//TODO for other valuation if needed
	public static double getValuation(String type, int typeId){
		double valuation =0.00;
		String command = ActivitySQL.valuation(typeId);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			db.query(command);
			if (db.next()) {
				valuation = db.getDouble("VALUATION_CALCULATED");
			}
			db.clear();
		}
		return valuation;
		
	}
	
	public static HashMap<String,Double> getFeeNameAll(RequestVO rv,JSONArray g,String all){
		HashMap<String,Double> h = new HashMap<String, Double>();
		
		Sage db = new Sage();
		try{
			
			db.query(FinanceSQL.getFeeNameAll(rv, all));
			HashMap<String,Double> hdb = new HashMap<String, Double>();
			while(db.next()){
				hdb.put(db.getString("NAME"), db.getDouble("FEE_AMOUNT"));
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(" select DISTINCT FEE_ID,F.NAME  from REF_FEE_GROUP RGF join FEE F  on  RGF.FEE_ID=F.ID where RGF.ACTIVE='Y' AND F.NAME IN ( ");
			sb.append(all);
			
			StringBuilder gs = new StringBuilder();
			for(int i=0;i<g.length();i++){
				gs.append(g.getJSONObject(i).getInt("groupid"));
				gs.append(",");
			}	
			gs.append("0");
			
			sb.append(" ) ");
			//TODO test 2/27/2020 heirarchy as per ALAIN new requirement for business to eliminate same group...
			//sb.append(" AND RGF.FEE_GROUP_ID IN (").append(gs.toString()).append(")");
			db.query (sb.toString());
			
		
			//Logger.info(feename+":: FINDING");
			
			Logger.info(g.toString());
			while(db.next()){
				String feename = db.getString("NAME");
				int id = db.getInt("FEE_ID");
				boolean found = false;
				double amount = 0;	
				for(int i=0;i<g.length();i++){
					//Logger.info(g.getJSONObject(i).getString("name"));
					//int id = 0;
					//sb.append(" AND RGF.FEE_GROUP_ID = "+g.getJSONObject(i).getInt("groupid"));
					//db.query( "select TOP 1 FEE_ID  from REF_FEE_GROUP RGF JOIN FEE F on RGF.FEE_ID= F.ID where RGF.ACTIVE='Y' AND F.NAME= '"+Operator.sqlEscape(feename)+"' AND RGF.FEE_GROUP_ID = "+g.getJSONObject(i).getInt("groupid"));
					/*if(db.next()){
						id = db.getInt("FEE_ID");
					}*/
					if(g.getJSONObject(i).getString("name").equalsIgnoreCase(feename)){
						if(g.getJSONObject(i).has("amount")){
							amount = g.getJSONObject(i).getDouble("amount");	
						}else if(g.getJSONObject(i).has("value")){
							amount = g.getJSONObject(i).getDouble("value");
						}
						//Logger.info("return "+r.toString());
						found = true;
						break;
					}
					Logger.info(feename+"---->"+amount);
					if(!found){
						if(g.getJSONObject(i).getInt("feeid")==id){
							if(g.getJSONObject(i).has("amount")){
								amount = g.getJSONObject(i).getDouble("amount");	
							}else if(g.getJSONObject(i).has("value")){
								amount = g.getJSONObject(i).getDouble("value");
							}
							//Logger.info("return "+r.toString());
							found = true;
							break;
						}
					}
					
					
				}
				if(hdb.containsKey(feename)){
					amount =  Operator.addDouble(amount, hdb.get(feename));
					
				}
				
					Logger.info(feename+"---->"+amount);
					h.put(feename, amount);
				}	
			
			
			for (Map.Entry<String, Double> item : h.entrySet()) {
			    String key = item.getKey();
			    double value = item.getValue();
			    Logger.highlight(key+"-KV->"+value);
			}
			
			
		}catch (Exception e) {
			Logger.error("getFeeName ERROR  :" + e.getMessage());
			h = new HashMap<String, Double>();
		}
		db.clear();
		return h;
		
	}
	//NOT USED 2/6 /19 as all feenames should be calcualted at once.
	public static double getFeeName(RequestVO rv,JSONArray g,String feename){
		double amount = 0;
		Sage db = new Sage();
		try{
			Logger.info(feename+":: FINDING");
			boolean found = false;
			Logger.info(g.toString());
			
			for(int i=0;i<g.length();i++){
				//Logger.info(g.getJSONObject(i).getString("name"));
				int id = 0;
				db.query( "select TOP 1 FEE_ID  from REF_FEE_GROUP RGF JOIN FEE F on RGF.FEE_ID= F.ID where RGF.ACTIVE='Y' AND F.NAME= '"+Operator.sqlEscape(feename)+"' AND RGF.FEE_GROUP_ID = "+g.getJSONObject(i).getInt("groupid"));
				if(db.next()){
					id = db.getInt("FEE_ID");
				}
				if(g.getJSONObject(i).getString("name").equalsIgnoreCase(feename)){
					if(g.getJSONObject(i).has("amount")){
						amount = g.getJSONObject(i).getDouble("amount");	
					}else if(g.getJSONObject(i).has("value")){
						amount = g.getJSONObject(i).getDouble("value");
					}
					//Logger.info("return "+r.toString());
					found = true;
					break;
				}
				
				if(!found){
					if(g.getJSONObject(i).getInt("feeid")==id){
						if(g.getJSONObject(i).has("amount")){
							amount = g.getJSONObject(i).getDouble("amount");	
						}else if(g.getJSONObject(i).has("value")){
							amount = g.getJSONObject(i).getDouble("value");
						}
						//Logger.info("return "+r.toString());
						found = true;
						break;
					}
				}
				
			}
			
			//if(!found){
				
				
				
				db.query(FinanceSQL.getFeeName(rv, feename));
				
				while(db.next()){
					amount =  Operator.addDouble(amount, db.getDouble("FEE_AMOUNT"));
					
				}
				
				
			//}
			
		}catch (Exception e) {
			Logger.error("getFeeName ERROR  :" + e.getMessage());
			amount = 0;
		}
		db.clear();
		return amount;
		
	}
	
	public static double getFeeName(RequestVO rv,JSONArray g,int feeId){
		double r = 0;
		try{
			Logger.info(feeId+":: FINDING");
			boolean found = false;
			for(int i=0;i<g.length();i++){
				Logger.info("g.getJSONObject(i).getInt(feeid)"+ g.getJSONObject(i).getInt("feeid"));
				if(g.getJSONObject(i).getInt("feeid")==feeId){
					Logger.info("g.getJSONObject(i).has(amount)"+ g.getJSONObject(i).has("amount"));
					if(g.getJSONObject(i).has("amount")){
						if(g.getJSONObject(i).getDouble("amount")!=0){
						r = g.getJSONObject(i).getDouble("amount");	
						Logger.info("g.getJSONObject(i).getDouble(amount)"+ g.getJSONObject(i).getDouble("amount"));
						}else {
							r = g.getJSONObject(i).getDouble("value");
						}
					}else {
						r = g.getJSONObject(i).getDouble("value");
						Logger.info("g.getJSONObject(i).getDouble(value)"+ g.getJSONObject(i).getDouble("value"));

					}
					found = true;
					break;
				}
			}
			Logger.info(r+"FOUND -------"+ found);
			
			if(!found){
				Sage db = new Sage();
				
				String command = FinanceSQL.getFeeId(rv,feeId);
				
				
				db.query(command);
				
				while(db.next()){
					r =  db.getDouble("FEE_AMOUNT");
					
				}
				
				db.clear();
			}
			
		}catch (Exception e) {
			Logger.error("getFeeName ERROR  :" + e.getMessage());
			r = 0;
		}
		return r;
		
	}
	
	
	public static String getFeeName(int feeId){
		String feename = "";
		Sage db = new Sage();
		
		String command = "select NAME FROM FEE WHERE ID = "+feeId;
		
		
		db.query(command);
		
		if(db.next()){
			feename =  db.getString("NAME");
			
		}
		
		db.clear();
		
		return feename;
	}
	
	
	public static double getSumofFeeGroup(RequestVO r,JSONArray g,String groupname){
		double t = 0;
		try{
			Sage db = new Sage();
			
			//sb.append(" ORDER BY SD.ID ");
			
			db.query(FinanceSQL.getGroupName(r, groupname));
			
			while(db.next()){
				t = Operator.addDouble(t, db.getDouble("FEE_AMOUNT"));
				
			}
			
			db.clear();
			
			for(int i=0;i<g.length();i++){
				if(g.getJSONObject(i).getString("group").equalsIgnoreCase(groupname)){
					if(g.getJSONObject(i).getDouble("amount")>0){
						t = Operator.addDouble(t, g.getJSONObject(i).getDouble("amount"));
					}
				}
			}
			Logger.info(t+"sumoffeegroup...........");
		}catch (Exception e) {
			Logger.error("getSumofFeeGroup ERROR  :" + e.getMessage());
			t=0;
		}
		return t;
		
	}
	
	
	public static double getSumofFeeGroup(FeesGroupVO g){
		double t = 0;
		try{
			
			for(int i=0;i<g.getFees().length;i++){
				t = Operator.addDouble(t, g.getFees()[i].getAmount());
			}
			
		}catch (Exception e) {
			Logger.error("getFormula ERROR  :" + e.getMessage());
			t=0;
		}
		return t;
		
	}
	
	
	public static FinanceVO getSubTotalsAndTotals1(FinanceVO g){
		double t = 0;
		try{
			for(int j=0;j<g.getGroups().length;j++){
				double s = 0;
				for(int i=0;i<g.getGroups()[j].getFees().length;i++){
					s = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
					t = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
				}
				g.getGroups()[j].setAmount(s);
			}
			g.setAmount(t);
			
		}catch (Exception e) {
			Logger.error("getSubTotals ERROR  :" + e.getMessage());
			
		}
		return g;
		
	}
	
	public static StatementVO getSubTotalsAndTotals(StatementVO g){
		double t = 0;
		try{
			for(int j=0;j<g.getGroups().length;j++){
				double s = 0;
				for(int i=0;i<g.getGroups()[j].getFees().length;i++){
					s = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
					t = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
				}
				g.getGroups()[j].setAmount(s);
			}
			g.setAmount(t);
			
		}catch (Exception e) {
			Logger.error("getSubTotals ERROR  :" + e.getMessage());
			
		}
		return g;
		
	}
	
	public static FinanceVO getSubTotals(FinanceVO g){
		
		try{
			for(int j=0;j<g.getGroups().length;j++){
				double t = 0;
				for(int i=0;i<g.getGroups()[j].getFees().length;i++){
					t = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
				}
				g.getGroups()[j].setAmount(t);
			}
			//g.setAmount(t);
			
		}catch (Exception e) {
			Logger.error("getSubTotals ERROR  :" + e.getMessage());
			
		}
		return g;
		
	}
	
	public static FinanceVO getTotals(FinanceVO g){
		double t = 0;
		try{
			for(int j=0;j<g.getGroups().length;j++){
				for(int i=0;i<g.getGroups()[j].getFees().length;i++){
					t = Operator.addDouble(t, g.getGroups()[j].getFees()[i].getAmount());
				}
			}
			g.setAmount(t);
			
		}catch (Exception e) {
			Logger.error("getTotals ERROR  :" + e.getMessage());
			t=0;
		}
		return g;
		
	}
	
	public static double getTotalAllFees(JSONArray g){
		double t = 0;
		try{
			for(int i=0;i<g.length();i++){
				t = Operator.addDouble(t, g.getJSONObject(i).getDouble("amount"));
			}
			
		}catch (Exception e) {
			Logger.error("getFormula ERROR  :" + e.getMessage());
			t=0;
		}
		return t;
		
	}
	
	
	public static ObjGroupVO details(String type, int typeid, int groupId, int setId) {
		ObjGroupVO result = new ObjGroupVO();
		String grp = "";
		String grpid = "";
		String grppublic = "";
		String multi = "N";
		
		String command ="";
		if(multi.equalsIgnoreCase("Y")){
			command = CustomSQL.details(type, typeid, groupId, setId);
		}else {
			command = CustomSQL.details(type, typeid, groupId);
		}
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			if (db.query(command) && db.size() > 0) {
				ObjVO[] os = new ObjVO[db.size()];
				int count = 0;
				while (db.next()) {
					if (db.hasValue("GROUP_NAME")) { grp = db.getString("GROUP_NAME"); }
					if (db.hasValue("GROUP_ID")) { grpid = db.getString("GROUP_ID"); }
					if (db.hasValue("GROUP_PUBLIC")) { grppublic = db.getString("GROUP_PUBLIC"); }
					ObjVO vo = new ObjVO();
					vo.setId(db.getInt("ID"));
					vo.setField(db.getString("NAME"));
					vo.setFieldid(db.getString("ID"));
					vo.setValue(db.getString("VALUE"));
					/*vo.setType("text");
					vo.setItype("String");*/
					vo.setType(db.getString("FIELD_TYPE"));
					vo.setItype(db.getString("FIELD_ITYPE"));
					os[count] = vo;
					count++;
				}
				result.setObj(os);
			}
			db.clear();
		}
		result.setType("finance");
		if (Operator.hasValue(grp)) { result.setGroup(grp); }
		if (Operator.hasValue(grppublic)) { result.setPub(grppublic); }
		//if (grpid > 0) { result.setId(grpid); }
		if (Operator.hasValue(grpid)) { result.setGroupid(grpid); }
		return result;
	}
	
	
	public static JSONArray sortJSONarray(JSONArray jsonArr){
		 JSONArray sortedJsonArray = new JSONArray();
		try{
			
		  
			Logger.info(jsonArr.toString());
		    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int i = 0; i < jsonArr.length(); i++) {
		        jsonValues.add(jsonArr.getJSONObject(i));
		    }
		    Collections.sort( jsonValues, new Comparator<JSONObject>() {
		        //You can change "Name" with "ID" if you want to sort by ID
		        private static final String KEY_NAME = "groupname";

		        @Override
		        public int compare(JSONObject a, JSONObject b) {
		            String valA = new String();
		            String valB = new String();

		            try {
		                valA = (String) a.get(KEY_NAME);
		                valB = (String) b.get(KEY_NAME);
		            } 
		            catch (JSONException e) {
		                //do something
		            }

		            return valA.compareTo(valB);
		            //if you want to change the sort order, simply use the following:
		            //return -valA.compareTo(valB);
		        }
		    });

		    for (int i = 0; i < jsonArr.length(); i++) {
		        sortedJsonArray.put(jsonValues.get(i));
		        Logger.info(jsonValues.get(i).getString("groupname"));
		    }
		    sortedJsonArray = compare(sortedJsonArray);
		    Logger.info(sortedJsonArray.toString());
		}catch(Exception e){
			Logger.error("sortJSONarray ERROR  :" + e.getMessage());
		}
		return sortedJsonArray;
	}
	
	public static JSONArray compare(JSONArray g){
		 JSONArray sortedJsonArray = new JSONArray();
		try{
			
		  
			HashSet<String> s = new HashSet<String>();
			JSONArray l = new JSONArray();
			for(int i=0;i<g.length();i++){
				
				if(!s.contains(g.getJSONObject(i).getString("groupname"))){
					s.add(g.getJSONObject(i).getString("groupname"));
					l.put(g.getJSONObject(i));
				}
				
			}
			
			Logger.info(l.length());
			
			String arr[] = new String[l.length()];
			String temp[] = new String[l.length()];
			for(int i=0;i<l.length();i++){
				arr[i] = l.getJSONObject(i).getString("groupname");
				Logger.info("***"+arr[i]);
				//if()
			}
			
			checkGroup(g, arr);
			
				
		   /* List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int i = 0; i < jsonArr.length(); i++) {
		        jsonValues.add(jsonArr.getJSONObject(i));
		    }*/
		    /*Collections.sort( jsonValues, new Comparator<JSONObject>() {
		        //You can change "Name" with "ID" if you want to sort by ID
		        private static final String KEY_NAME = "groupname";

		        @Override
		        public int compare(JSONObject a, JSONObject b) {
		            String valA = new String();
		            String valB = new String();

		            try {
		                valA = (String) a.get(KEY_NAME);
		                valB = (String) b.get(KEY_NAME);
		            } 
		            catch (JSONException e) {
		                //do something
		            }

		            return valA.compareTo(valB);
		            //if you want to change the sort order, simply use the following:
		            //return -valA.compareTo(valB);
		        }
		    });

		    for (int i = 0; i < jsonArr.length(); i++) {
		        sortedJsonArray.put(jsonValues.get(i));
		        Logger.info(jsonValues.get(i).getString("groupname"));
		    }
		    
		    Logger.info(sortedJsonArray.toString());*/
		}catch(Exception e){
			Logger.error("sortJSONarray ERROR  :" + e.getMessage());
		}
		return sortedJsonArray;
	}
	
	
	public static double checkGroup(JSONArray g,String[] arr){
		double t = 0;
		try{
			String temp[] = arr;
			Logger.info("arr[j]"+Arrays.toString(arr));
			String s = Arrays.toString(arr);
			String ts ="";
			for(int j =0; j<arr.length;j++){
				String gn = arr[j];
				//s = s;
				for(int i=0;i<g.length();i++){
					if(!g.getJSONObject(i).getString("groupname").equalsIgnoreCase(gn)){
						String formula = g.getJSONObject(i).getString("formula");
						if(formula.indexOf("sumoffeegroup("+gn)>0){
							
							temp = switchArr(temp,  g.getJSONObject(i).getString("groupname"),gn);
							
							/*s = Operator.replace(s, gn,g.getJSONObject(i).getString("groupname"));
							s = Operator.replace(s, g.getJSONObject(i).getString("groupname"),gn);*/
							Logger.info(formula+"********"+gn+"replacing .........."+g.getJSONObject(i).getString("groupname")+"---"+Arrays.toString(temp));
						}
					}else {
						//s = Operator.replace(s, gn, g.getJSONObject(i).getString("groupname"));
						temp = switchArr(temp, gn, gn);
					}
				}
			}
			Logger.info("Final order s"+s);
			for(int i=0;i<temp.length;i++){
				Logger.info("Final order"+temp[i]);
			}
			
		}catch (Exception e) {
			Logger.error("getFormula ERROR  :" + e.getMessage());
			t=0;
		}
		return t;
		
	}
	
	public static String[] switchArr(String[] tmp,String sw1,String sw2){
		String a[] = new String[tmp.length];
		int c =0;
		for(int j =0; j<tmp.length;j++){
			if(tmp[j].equalsIgnoreCase(sw1)){
				a[c] = sw2; 
				c++;
			}else if(tmp[j].equalsIgnoreCase(sw2)){
				a[c] = sw1;
				c++;
			}else {
				//a[j] = tmp[j];
			}
		}
		for(int j =0; j<tmp.length;j++){
			if(tmp[j].equalsIgnoreCase(sw1)){
				//a[j] = sw2;
			}else if(tmp[j].equalsIgnoreCase(sw2)){
				//a[j] = sw1;
			}else {
				a[c] = tmp[j];
				c++;
			}
		}
		
		
		
		return a;
	}
	
	
	public static BrowserVO browse(String id, String type){
		BrowserVO b = new BrowserVO();
		
		
		try {
			BrowserHeaderVO h = new BrowserHeaderVO();
			BrowserSearchVO srch = new BrowserSearchVO();
			srch.setUrl(searchurl);
			h.setSearch(srch);

			h.setLabel("CASHIER BROWSER ");
			h.setDataid(Operator.toString(id));
			//h.setFollow(id);
			String t = Operator.subString(id, 0, 1);
			String a = "A";
			id = Operator.subString(id, 1, id.length());
			if (Operator.hasValue(id)) {
				ArrayList<BrowserItemVO> r = new ArrayList<BrowserItemVO>();
				ArrayList<BrowserItemVO> i = new ArrayList<BrowserItemVO>();

				String command = "";
				
				if(t.equalsIgnoreCase("A")){
					command = FinanceSQL.searchCashierBrowse("A", id);
					a = "S";
				}
				else {
					command = FinanceSQL.searchCashierBrowse("", id);
				}
				if (Operator.hasValue(command)) {
					Sage db = new Sage();
					db.query(command);

					while(db.next()) {
						BrowserItemVO vo = new BrowserItemVO();
						vo.setTitle(db.getString("ACT_NBR"));
						vo.setId(a+db.getString("ID"));
						vo.setDataid(a+db.getString("ID"));
						//vo.setChildren(db.getInt("CHILDREN"));
						vo.setEntity("finance");
						vo.setType("finance");
						//vo.setSub("activity");
						vo.setLink("cart");
						vo.setDomain(Config.rooturl());
						if(!a.equals("S")){
							if(db.getInt("CHILDREN")>0){
								vo.setChildren(1);
							}
						}
						h.addParent(db.getString(id), "finance");
						i.add(vo);
					}

					db.clear();

					BrowserItemVO[] rs = r.toArray(new BrowserItemVO[r.size()]);
					b.setRoot(rs);
					BrowserItemVO[] is = i.toArray(new BrowserItemVO[i.size()]);
					b.setItems(is);
				}
			}

			b.setHeader(h);
			
		
		}
		catch (Exception e){
		}
		return b;
	}
	
	
	public static BrowserVO search(String searchField, int start, int end) {
		BrowserVO b = new BrowserVO();
		Sage db = new Sage();
		try {
			
			
			
			Logger.info("SEARCHING--- "+searchField);
			boolean payment = false;
			String command = FinanceSQL.searchCashier(searchField);
			db.query(command);
			if(db.size()<=0){
				
				command = FinanceSQL.searchCashierPayment(searchField);
				db.query(command);
				if(db.size()>0){
					payment = true;
				}else {
					command = FinanceSQL.searchCashierUser(searchField);
					db.query(command);
					/*if(db.size()>0){
						payment = true;
					}*/
				}
				
				
			}
			
			JSONObject p = new JSONObject();
			if(db.next()){
				p.put("PROJECT_ID", db.getInt("PROJECT_ID"));
				p.put("ACTIVITY_ID", db.getInt("ACTIVITY_ID"));
				p.put("STATEMENT_ID", db.getInt("STATEMENT_ID"));
				p.put("PROJECT_ID_A", "P"+db.getInt("PROJECT_ID"));
				p.put("ACTIVITY_ID_A", "A"+db.getInt("ACTIVITY_ID"));
				p.put("STATEMENT_ID_A", "S"+db.getInt("STATEMENT_ID"));
				p.put("TYPE", db.getString("TYPE"));
				
				p.put("PAYMENT_ID", db.getInt("PAYMENT_ID"));
				p.put("PAYMENT_ID_T", "T"+db.getInt("PAYMENT_ID"));
				
				
				p.put("USER_ID", db.getInt("USER_ID"));
				p.put("USERNAME_U", db.getString("USERNAME"));
			}
			
			
			
			
			BrowserHeaderVO h = new BrowserHeaderVO();
			ArrayList<BrowserItemVO> ro = new ArrayList<BrowserItemVO>();
			ArrayList<BrowserItemVO> i = new ArrayList<BrowserItemVO>();

			BrowserSearchVO srch = new BrowserSearchVO();
			srch.setUrl(searchurl);
			h.setSearch(srch);

			h.setLabel("CASHIER BROWSER");
			h.setQuery(searchField);
			/*h.setQuerytime(response.getQTime());
			h.setStatus(response.getStatus());*/
			
			if(p.has("TYPE")){
				if(p.getString("TYPE").equalsIgnoreCase("CP")){
					command = FinanceSQL.searchCashier(p.getString("TYPE"), p.getInt("PROJECT_ID"));
					h.addParent(p.getString("PROJECT_ID_A"), "finance");
					h.setDataid(p.getString("PROJECT_ID_A"));
					h.setFollow(h.getDataid());
				}
				 
				if(p.getString("TYPE").equalsIgnoreCase("CA")){
					command = FinanceSQL.searchCashier(p.getString("TYPE"), p.getInt("PROJECT_ID"));
					//command = FinanceSQL.searchCashierActivity(p.getString("TYPE"), p.getInt("ACTIVITY_ID"));
					h.addParent(p.getString("PROJECT_ID_A"), "finance");
					h.setDataid(p.getString("ACTIVITY_ID_A"));
					h.setFollow(h.getDataid());
				}
				 
				if(p.getString("TYPE").equalsIgnoreCase("CS")){
					command =  FinanceSQL.searchCashier(p.getString("TYPE"), p.getInt("ACTIVITY_ID"));
					h.addParent(p.getString("PROJECT_ID_A"), "finance");
					h.addParent(p.getString("ACTIVITY_ID_A"), "finance");
					h.setDataid(p.getString("STATEMENT_ID_A"));
					h.setFollow(h.getDataid());
				}
				
				if(p.getString("TYPE").equalsIgnoreCase("CT")){
					command =  FinanceSQL.searchCashierPayment(p.getString("PAYMENT_ID"));
					h.addParent(p.getString("PAYMENT_ID_T"), "finance");
					h.setDataid(p.getString("PAYMENT_ID_T"));
					h.setFollow(h.getDataid());
				}
				
				if(p.getString("TYPE").equalsIgnoreCase("CU")){
					command =  FinanceSQL.searchCashierUser(searchField);
					h.addParent(p.getString("USERNAME_U"), "finance");
					h.setDataid(p.getString("USERNAME_U"));
					h.setFollow(h.getDataid());
				}
				
			}
			db.query(command);
			
			int size = db.size();
			h.setFound(size);
			
			if(size < 1) {
				h.setMessage("No Results found");
			}
			
			HashSet<String> ps = new HashSet<String>();
			
			while (db.next()) {
				
				if(p.getString("TYPE").equalsIgnoreCase("CU")){
					
					BrowserItemVO o = new BrowserItemVO();
					o.setTitle(db.getString("USERNAME"));
					o.setDataid("U"+db.getString("USER_ID"));
					o.setId("U"+db.getString("USER_ID"));

					o.setEntity("finance");
					o.setType("finance");
					o.setDomain(Config.rooturl());
					o.setLink("reverse");
					
					if(db.getInt("CHILDREN")>0){
						o.setChildren(1);
					}else {
						o.setChildren(0);
					}
					ro.add(o);
				}
				
				if(p.getString("TYPE").equalsIgnoreCase("CT")){
					
					BrowserItemVO o = new BrowserItemVO();
					o.setTitle(db.getString("PAYMENT_ID"));
					o.setDataid("T"+db.getString("PAYMENT_ID"));
					o.setId("T"+db.getString("PAYMENT_ID"));

					o.setEntity("finance");
					o.setType("finance");
					o.setDomain(Config.rooturl());
					o.setLink("reverse");
					
					if(db.getInt("CHILDREN")>0){
						o.setChildren(1);
					}else {
						o.setChildren(0);
					}
					ro.add(o);
				}
				if(!ps.contains(db.getString("PROJECT_ID"))){
					ps.add(db.getString("PROJECT_ID"));
					BrowserItemVO o = new BrowserItemVO();
					o.setTitle(db.getString("PROJECT_NAME"));
					o.setDataid("P"+db.getString("PROJECT_ID"));
					o.setId("P"+db.getString("PROJECT_ID"));

					o.setEntity("finance");
					o.setType("finance");
					o.setDomain(Config.rooturl());
					o.setLink("cart");
					
					if(db.getInt("CHILDREN")>0){
						o.setChildren(1);
					}else {
						o.setChildren(1);
					}
					ro.add(o);
				}
				if(p.getString("TYPE").equalsIgnoreCase("CS")){
					BrowserItemVO o = new BrowserItemVO();
					o.setTitle(db.getString("STATEMENT_NBR"));
					o.setDataid("S"+db.getString("STATEMENT_ID"));
					o.setId("S"+db.getString("STATEMENT_ID"));
	
					o.setEntity("finance");
					o.setType("finance");
					o.setDomain(Config.rooturl());
					o.setLink("cart");
					i.add(o);
					
				
				} 
				else {
					BrowserItemVO o = new BrowserItemVO();
					o.setTitle(db.getString("ACT_NBR"));
					o.setDataid("A"+db.getString("ID"));
					o.setId("A"+db.getString("ID"));

					o.setEntity("finance");
					o.setType("finance");
					o.setDomain(Config.rooturl());
					o.setLink("cart");
					o.setChildren(1);
					
					if(db.getInt("CHILDREN")>0){
						o.setChildren(1);
					}

					i.add(o);
					
				}
				
			}
			
			BrowserItemVO[] rs = ro.toArray(new BrowserItemVO[ro.size()]);
			b.setRoot(rs);
			BrowserItemVO[] is = i.toArray(new BrowserItemVO[i.size()]);
			b.setItems(is);
			
			
			Logger.info(h+"##############HEADER ##########");
			h.setDataid("A"+2217163);
			b.setHeader(h);
			//b.setHeader(2217163);
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		db.clear();
		return b;
	}
	
	
	public static TypeVO cart(RequestVO r, Token u){
		
		TypeVO t = new TypeVO();
		try {
			String searched = "";
			String carttype = "";
			int projectid = -1;
			int actid = -1;
			int statementid = -1;
			String type = r.getType();
			int typeid = r.getTypeid();
			String reference = r.getReference();

			Logger.info(type+"SCANIN TO CART"+reference);
			String command = "";
			Sage db = new Sage();

			String id = r.getId();
			if (Operator.hasValue(id) && id.toLowerCase().startsWith("p")) {
				projectid = Operator.toInt(Operator.subString(r.getId(), 1, r.getId().length()));
				command = FinanceSQL.projectCart(projectid);
				carttype = "project";
			}
			else if (Operator.hasValue(id) && id.toLowerCase().startsWith("a")) {
				actid = Operator.toInt(Operator.subString(r.getId(), 1, r.getId().length()));
				command = FinanceSQL.activityCart1(actid);
				carttype = "activity";
			}
			else if (Operator.hasValue(id) && id.toLowerCase().startsWith("s")) {
				statementid = Operator.toInt(Operator.subString(r.getId(), 1, r.getId().length()));
				command = FinanceSQL.statementCart(statementid);
				carttype = "statement";
			}
			else if (Operator.hasValue(type) && Operator.equalsIgnoreCase(type, "project")) {
				if (typeid > 0) {
					projectid = typeid;
					command = FinanceSQL.projectCart(typeid);
					carttype = "project";
				}
				else if (Operator.hasValue(reference)) {
					command = FinanceSQL.projectCart(reference);
					carttype = "project";
				}
			}
			else if (Operator.hasValue(type) && Operator.equalsIgnoreCase(type, "activity")) {
				if (typeid > 0) {
					actid = typeid;
					command = FinanceSQL.activityCart1(typeid);
					carttype = "activity";
				}
				else if (Operator.hasValue(reference)) {
//					command = FinanceSQL.activityCart(reference);
//					carttype = "activity";

					command = FinanceSQL.activityCart1(reference);
					carttype = "activity";
				}
			}

			t.setEntity("finance");
			Logger.info(command);
			Logger.info(carttype);
			db.query(command);

			StatementVO sv[] = new StatementVO[db.size()];
			int s =0;
			while(db.next()){
				StatementVO svo = new StatementVO();
				svo.setProjectid(db.getInt("ID"));
				svo.setProjectname(db.getString("NAME"));
				
				svo.setActivityid(db.getInt("ACTIVITY_ID"));
				svo.setActivitynumber(db.getString("ACT_NBR"));
				
				svo.setActivitytype(db.getString("ACT_TYPE"));
				
				svo.setStatementid(db.getInt("STATEMENT_ID"));
				svo.setStatementnumber(db.getString("STATEMENT_NUMBER"));
				
				svo.setAmount(db.getDouble("FEE_AMOUNT"));
				svo.setPaidamount(db.getDouble("FEE_PAID"));
			
				svo.setBalancedue(db.getDouble("BALANCE_DUE"));
				svo.setInputamount(db.getDouble("BALANCE_DUE"));

				String tpe = db.getString("TYPE");
				svo.setType(tpe);
				StringBuilder sb = new StringBuilder();
				sb.append(tpe);
				if (Operator.equalsIgnoreCase(tpe, "P")) {
					sb.append(db.getInt("PROJECT_ID"));
				}
				else if (Operator.equalsIgnoreCase(tpe, "A")) {
					sb.append(db.getInt("ACTIVITY_ID"));
				}
				else if (Operator.equalsIgnoreCase(tpe, "S")) {
					sb.append(db.getInt("PROJECT_ID"));
				}
				svo.setSearched(sb.toString());
				
				sv[s] = svo;
				s++;
			}
			db.clear();
			
			
			
			/*StatementVO svh[] = new StatementVO[0];
			for(StatementVO s2: sv){
				int actId = s2.getActivityid();
			}*/
			
		
		if (Operator.equalsIgnoreCase(carttype, "statement")) {
			t.setStatements(getStatements(sv));
		}
		else if (Operator.equalsIgnoreCase(carttype, "activity")){
			t.setStatements(getActivityStatements(sv));
		}
		else if(Operator.equalsIgnoreCase(carttype, "project")){
			t.setStatements(getActivityStatements(sv));
		}
		
		t.setPayment(getPayments(u.getId()));
		
		} catch (Exception e){
			e.printStackTrace();
			t.setSubtitle(e.getMessage());
		}
		return t;
	}
	
	
	
	
	
	public static StatementVO[] getActivityStatements(StatementVO[] svo){
		Sage db = new Sage();
		Sage db2 = new Sage();
		
		try{
		if(svo.length>0){
			
			for(int i=0;i<svo.length;i++){
				int activityId = svo[i].getActivityid();
				int projectId = svo[i].getProjectid();
				String command ="";
				if(svo[i].getType().equalsIgnoreCase("P")){
					//command = FinanceSQL.getProjectStatement(projectId);
					command = FinanceSQL.calc("project", projectId,true);
					command += "select * from CALC WHERE FEE_AMOUNT > 0 ";
				}else {
					//command = FinanceSQL.getActivityStatement(activityId);
					command = FinanceSQL.calc("activity", activityId,true);
					command += "select * from CALC WHERE FEE_AMOUNT > 0 ";
				}
				db.query(command);
				FeesGroupVO[] groups = new FeesGroupVO[db.size()];
				int count =0;
				while(db.next()){
					FeesGroupVO g = new FeesGroupVO();
					g.setGroup(db.getString("GROUP_NAME"));
					g.setGroupid(db.getInt("GROUP_ID"));
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
					g.setInputamount(g.getBalancedue());
					/*g.setBalancedue(db.getDouble("FEE_AMOUNT"));
					g.setInputamount(db.getDouble("FEE_AMOUNT"));*/
					g.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+g.getGroupid());
					command = FinanceSQL.getStatementDetails(activityId,g.getGroupid(),svo[i].getType());
					db2.query(command);
					FeeVO[] farr = new FeeVO[db2.size()];
					int fcount =0;
					while(db2.next()){
						FeeVO f = new FeeVO();
						//f.setGroup(db2.getString("GROUP_NAME"));
						//f.setGroupid(db2.getInt("GROUP_ID"));
						
						f.setStatementdetailid(db2.getInt("ID"));
						f.setFeeid(db2.getInt("FEE_ID"));
						f.setName(db2.getString("NAME"));
						//f.setFormula(db2.getString("DEFINITION"));
						//f.setRequired(db2.getString("REQ"));
						//f.setStartdate(db2.getString("START_DATE")); 
					
						f.setAmount(db2.getDouble("FEE_AMOUNT"));
						f.setPaidamount(db2.getDouble("FEE_PAID"));
						
						//TODO Revert back
						f.setBalancedue(db2.getDouble("BALANCE_DUE"));
						f.setInputamount(db2.getDouble("BALANCE_DUE"));
						/*f.setBalancedue(db2.getDouble("FEE_AMOUNT"));
						f.setInputamount(db2.getDouble("FEE_AMOUNT"));*/
						
						f.setFeedate(db2.getString("FEE_DATE"));
						
						f.setFinancemapid(db2.getInt("FINANCE_MAP_ID"));
						f.setAccountnumber(db2.getString("ACCOUNT_NUMBER"));
						f.setKeycode(db2.getString("KEY_CODE"));
						f.setFund(db2.getString("FUND"));
						f.setBudgetunit(db2.getString("BUDGET_UNIT"));
						
						
						f.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+g.getGroupid()+"_"+f.getStatementdetailid());
						farr[fcount] = f;
						fcount++;
						
					}
					g.setFees(farr);
					
					groups[count] = g;
					count++;
				
				}
				svo[i].setGroups(groups);
				svo[i].setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid());
			}
			
		}
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
		db2.clear();
		db.clear();
		return svo;
		
	}

	public static StatementVO[] getStatements(StatementVO[] svo){
		if(svo.length>0){
			Sage db = new Sage();
			Sage db2 = new Sage();
			try{
			for(int i=0;i<svo.length;i++){
				int statementId = svo[i].getStatementid();
				String command = FinanceSQL.getStatement(statementId);
				db.query(command);
				FeesGroupVO[] groups = new FeesGroupVO[db.size()];
				int count =0;
				while(db.next()){
					FeesGroupVO g = new FeesGroupVO();
					g.setGroup(db.getString("GROUP_NAME"));
					g.setGroupid(db.getInt("GROUP_ID"));
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					//g.setBalancedue(db.getDouble("BALANCE_DUE"));
					//TODO Revert back
					//g.setBalancedue(db2.getDouble("BALANCE_DUE"));
					//g.setInputamount(db2.getDouble("BALANCE_DUE"));
					g.setBalancedue(db.getDouble("FEE_AMOUNT"));
					g.setInputamount(db.getDouble("FEE_AMOUNT"));
					g.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+statementId+"_"+g.getGroupid());
					command = FinanceSQL.getStatementDetails(statementId,g.getGroupid());
					db2.query(command);
					FeeVO[] farr = new FeeVO[db2.size()];
					int fcount =0;
					while(db2.next()){
						FeeVO f = new FeeVO();
						//f.setGroup(db2.getString("GROUP_NAME"));
						//f.setGroupid(db2.getInt("GROUP_ID"));
						
						f.setStatementdetailid(db2.getInt("ID"));
						f.setFeeid(db2.getInt("FEE_ID"));
						f.setName(db2.getString("NAME"));
						//f.setFormula(db2.getString("DEFINITION"));
						//f.setRequired(db2.getString("REQ"));
						//f.setStartdate(db2.getString("START_DATE")); 
					
						f.setAmount(db2.getDouble("FEE_AMOUNT"));
						f.setPaidamount(db2.getDouble("FEE_PAID"));
						//f.setBalancedue(db2.getDouble("BALANCE_DUE"));
						//TODO Revert back
						//f.setBalancedue(db2.getDouble("BALANCE_DUE"));
						//f.setInputamount(db2.getDouble("BALANCE_DUE"));
						f.setBalancedue(db2.getDouble("FEE_AMOUNT"));
						f.setInputamount(db2.getDouble("FEE_AMOUNT"));
						
						f.setFeedate(db2.getString("FEE_DATE"));
						
						f.setFinancemapid(db2.getInt("FINANCE_MAP_ID"));
						f.setAccountnumber(db2.getString("ACCOUNT_NUMBER"));
						f.setKeycode(db2.getString("KEY_CODE"));
						f.setFund(db2.getString("FUND"));
						f.setBudgetunit(db2.getString("BUDGET_UNIT"));
						
						
						f.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+statementId+"_"+g.getGroupid()+"_"+f.getStatementdetailid());
						farr[fcount] = f;
						fcount++;
						
					}
					g.setFees(farr);
					
					groups[count] = g;
					count++;
				
				}
				svo[i].setGroups(groups);
				svo[i].setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+statementId);
			}
			}catch (Exception e){
				Logger.error(e.getMessage());
			}
			db2.clear();
			db.clear();
		}
		return svo;
		
	}

	
	public static TypeVO getPaymentDetails(RequestVO vo) {
		TypeVO result = new TypeVO();
		result.setType("finance");
		PaymentVO g = FinanceFields.paymentdetails(Choices.getChoices(PeopleSQL.getActivityPeople(vo.getStatements())));
//		PaymentVO g = FinanceFields.paymentdetails(getPayees(vo));
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
	
	
	public static ResponseVO savePayment(ResponseVO o,RequestVO r, Token u){
		Logger.info(r.getPayment().getMethodname()+"$$$$$$$$$$$$$$$");
		
		if(Operator.equalsIgnoreCase(r.getPayment().getMethodname(),"Y")){
			Logger.info("DOING DEPOSITS ");
			return saveDepositCreditPayment(o, r, u);
		}
		return saveCommonPayment(o, r, u);
	}
	
	
	public static ResponseVO saveCommonPayment(ResponseVO o,RequestVO r, Token u){
		try{
			PaymentVO p = r.getPayment();
			p.setRevamount(p.getAmount());
			
			p.setTransactiontype(1);
			
			if(Operator.hasValue(p.getOnlinetranasactionnumber())){
				p.setPayeeid(u.getId());
				p.setMethod(2);
				
			}
			
			
			double amount = p.getAmount();
			double tamount = p.getAmount();
			JSONArray pdarr = new JSONArray();
			JSONArray adarr = new JSONArray();
			Logger.info("amount:::"+amount);
			if(amount>0){
				StatementVO s[] = r.getStatements();
				for(int i=0;i<s.length;i++){
					if(tamount>0){
					JSONObject ad = new JSONObject();
					ad.put("activityid", s[i].getActivityid());
					adarr.put(ad);
						for(int j=0;j<s[i].getGroups().length;j++){
							if(tamount>0){
								FeesGroupVO g = s[i].getGroups()[j];
								for(int k=0;k<g.getFees().length;k++){
									if(tamount>0){
										FeeVO f = g.getFees()[k];
										double feeamount = f.getInputamount();
										if(feeamount>tamount){
											JSONObject pd = new JSONObject();
											pd.put("statementdetailid", f.getStatementdetailid());
											pd.put("amount",tamount);
											pd.put("feeid", f.getFeeid());
											
											pd.put("financemapid", f.getFinancemapid());
											pd.put("keycode", f.getKeycode());
											pd.put("accountnumber", f.getAccountnumber());
											pd.put("budgetunit", f.getBudgetunit());
											pd.put("fund", f.getFund());
											
											pdarr.put(pd);
											tamount = 0;
										}else {
											tamount = subDouble(tamount,feeamount);
											JSONObject pd = new JSONObject();
											pd.put("statementdetailid", f.getStatementdetailid());
											pd.put("amount",feeamount);
											pd.put("feeid", f.getFeeid());
											
											
											pd.put("financemapid", f.getFinancemapid());
											pd.put("keycode", f.getKeycode());
											pd.put("accountnumber", f.getAccountnumber());
											pd.put("budgetunit", f.getBudgetunit());
											pd.put("fund", f.getFund());
											
											pdarr.put(pd);
										}
										
									}
								}	
							
							}
						}
					}
				}
			
			}
			
			boolean result = false;
			int paymentId =0;
			if(pdarr.length()>0){
				Sage db = new Sage();
				
				String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
				result = db.update(command);
				
				if(result){
					db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
					
					if(db.next()){
						paymentId = db.getInt("ID");
					}
					
					if(paymentId>0){
						for(int i=0;i<pdarr.length();i++){
							JSONObject f = pdarr.getJSONObject(i);
							f.put("paymentid", paymentId);
							if(f.getDouble("amount")>0){
								command = FinanceSQL.insertPaymentDetail(f, r.getIp(),  u.getId());
								Logger.info(command);
								result = db.update(command);
							}
						}
					}
				}
				
				//insert ref
				if(result && paymentId>0){
					/*for(int i=0;i<adarr.length();i++){
						JSONObject a = adarr.getJSONObject(i);
						
						command = FinanceSQL.insertRefPayment("activity",a.getInt("activityid"),paymentId, u.getId());
						if(Operator.hasValue(command)){
							result = db.update(command);
						}		
					}*/
					updatestatements(paymentId, u.getId());
					
				}
				
				
				db.clear();
			}
			
			
			if(result){
			 o.setType(getPaymentResponse(r.getStatements(), paymentId,r,u.getId()));
				
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("Problem while processing payment "+e.getMessage());
		}
		return o;
	}
	
	public static double subDouble(double a, double b){
		BigDecimal num1 = new BigDecimal(a+"");
		BigDecimal num2 = new BigDecimal(b+"");
 
		return(num1.subtract(num2).doubleValue());
	}

	
	
	public static TypeVO getPaymentResponse(StatementVO[] s,int paymentId,RequestVO vo,int userId){
		TypeVO t= new TypeVO();
		try{
			String command = "select pd.*,sd.statement_id from payment_detail pd join statement_detail sd on pd.STATEMENT_DETAIL_ID= sd.ID where payment_id="+paymentId;
			Sage db= new Sage();
			db.query(command);
			JSONArray pd = new JSONArray();
			
			while(db.next()){
				JSONObject pdt = new JSONObject();
				pdt.put("statementid", db.getInt("STATEMENT_ID"));
				pdt.put("statementdetailid", db.getInt("STATEMENT_DETAIL_ID"));
				pdt.put("amount", db.getDouble("AMOUNT"));
				pd.put(pdt);
			}
			
		
			
			
			db.clear();
			
			
			
			// clean session
			int counter =0;
			
			
			
			
			
			StatementVO sv[] = new StatementVO[s.length];
			
		
				for(int i=0;i<s.length;i++){
					boolean activity = false;
					double tsum = 0;
					FeesGroupVO [] sg = s[i].getGroups();
					for(int j=0;j<sg.length;j++){
						FeesGroupVO g = sg[j];
						boolean groups = false;
						double gsum = 0;
						for(int k=0;k<g.getFees().length;k++){
							FeeVO f = g.getFees()[k];
							int statementdetailid = f.getStatementdetailid();
							//Logger.info(s[i].getActivitynumber()+"before "+g.getGroup()+"--"+statementdetailid+"--"+gsum+"--"+groups+"--"+activity);
							double amount =0;
							for(int l=0;l<pd.length();l++){
								if(pd.getJSONObject(l).getInt("statementdetailid")== statementdetailid){
									amount = pd.getJSONObject(l).getDouble("amount");
									activity =true;
									groups =true;
									f.setPaidamount(Operator.addDouble(f.getPaidamount(), amount));
									f.setBalancedue(subDouble(f.getAmount(), f.getPaidamount()));
									//f.setInputamount(f.getBalancedue());
									f.setInputamount(subDouble(f.getInputamount(), amount));
									Logger.info("f.getBalancedue::"+f.getBalancedue());
									
									gsum = Operator.addDouble(gsum, amount);
									Logger.info("gsumadding::"+gsum+"##"+amount);
									tsum = Operator.addDouble(tsum, amount);
								
									
								}//end statementid match
							}	
							//Logger.info(s[i].getActivitynumber()+"after "+g.getGroup()+"--"+statementdetailid+"--"+gsum+"--"+groups+"--"+activity);	
						} //end fee	
						
						
						//if(groups){
						
								Logger.info(" before gsum::"+gsum+"##"+g.getAmount()+"##"+g.getPaidamount()+"###"+g.getBalancedue()+"###"+g.getInputamount());
								g.setPaidamount(Operator.addDouble(g.getPaidamount(), gsum));
								g.setBalancedue(subDouble(g.getAmount(), g.getPaidamount()));
								g.setInputamount(subDouble(g.getInputamount(), gsum));
								Logger.info(" after gsum::"+gsum+"##"+g.getPaidamount()+"###"+g.getAmount()+"####"+g.getBalancedue()+"###"+g.getInputamount());
						
						//}
					} //end groups
					
					//if(activity){
						
							//s[i].setPaidamount(subDouble(s[i].getPaidamount(), tsum));
							Logger.info(" before tsum::"+tsum+"##"+s[i].getAmount()+"##"+s[i].getPaidamount()+"###"+s[i].getBalancedue()+"###"+s[i].getInputamount());
							s[i].setPaidamount(Operator.addDouble(s[i].getPaidamount(), tsum));
							s[i].setBalancedue(subDouble(s[i].getAmount(), s[i].getPaidamount()));
							s[i].setInputamount(subDouble(s[i].getInputamount(), tsum));
							Logger.info(" after tsum::"+tsum+"##"+s[i].getAmount()+"##"+s[i].getPaidamount()+"###"+s[i].getBalancedue()+"###"+s[i].getInputamount());
						
					
					//}// end activity
							sv[i] = s[i];
					if(s[i].getInputamount()>0){		
						
						counter++;
					}
				}
				
				int c =0;
				StatementVO svc[] = new StatementVO[counter];
				for(int i=0;i<sv.length;i++){
					if(sv[i].getInputamount()>0){		
						svc[c] = sv[i];
						c++;
					}
				}
			
				t.setStatements(svc);
			
			//}
			t.setPayment(getPayments(userId));
			vo.setTypeid(paymentId);
			t.setTools(Tools.getTools(vo));
			
		
		}catch(Exception e){
			Logger.error("Problem while getPaymentResponse  "+e.getMessage());
		}
		return t;
	}
	
	
	
	public static PaymentVO[] getPayments(int userId){
		Sage db = new Sage();
		String command = FinanceSQL.getPaymentsByUser(userId);
		db.query(command);
		PaymentVO[] pv = new PaymentVO[db.size()];
		int counter =0;
		while(db.next()){
			PaymentVO  p = new PaymentVO();
			p.setAmount(db.getDouble("PAYMENT_AMOUNT"));
			p.setPaymentid(db.getInt("ID"));
			p.setMethod(db.getInt("LKUP_PAYMENT_METHOD_ID"));
			p.setMethodname(db.getString("METHOD_TYPE"));
			p.setPaymentdate(db.getString("PAYMENTDATE"));
			if(db.getInt("PAYEE_ID")>0){
				p.setOtherpayeename(db.getString("TEXT"));
			}else {
				p.setOtherpayeename(db.getString("PAYEE_DETAILS"));
			}
			p.setComment(db.getString("COMMENT"));
			p.setTransactiontypename(db.getString("TRANSACTIONTYPE"));
			if(counter==0){
				p.setStatements(getPaymentNumbers(db.getInt("ID")));
			}
			pv[counter] =p;
			counter++;
		}
		db.clear();
		
		return pv;
	}
	
	
	public static StatementVO[] getPaymentNumbers(int paymentId){
		Sage db = new Sage();
		StatementVO[] sv = new StatementVO[0];
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT DISTINCT A.ID , ACT_NBR,  ");
	
		sb.append(" LAT.DESCRIPTION as activity_type , ");
		sb.append(" LAS.DESCRIPTION as activity_status , ");
	
		sb.append(" A.DESCRIPTION as activity_description, ");
		sb.append(" A.PROJECT_ID as project_id, ");
				
		
		sb.append(" D.DEPT as activity_department ");
		sb.append( " from REF_ACT_PAYMENT RAP  ");
		sb.append( " left outer join ACTIVITY A on RAP.ACTIVITY_ID= A.ID ");
		sb.append(" left outer join LKUP_ACT_TYPE LAT ON A.LKUP_ACT_TYPE_ID=LAT.ID  ");
		sb.append(" left outer join DEPARTMENT D ON LAT.DEPARTMENT_ID=D.ID  ");
		sb.append(" LEFT OUTER JOIN LKUP_ACT_STATUS LAS on A.LKUP_ACT_STATUS_ID=LAS.ID ");
		sb.append(" where   PAYMENT_ID = ").append(paymentId);
		
		db.query(sb.toString());
		sv = new StatementVO[db.size()];
		int i =0;
		while(db.next()){
			StatementVO s = new StatementVO();
			s.setActivitynumber(db.getString("ACT_NBR"));
			s.setActivitystatus(db.getString("activity_status"));
			s.setActivitytype(db.getString("activity_type"));
			s.setProjectid(db.getInt("project_id"));
			s.setActivityid(db.getInt("id"));
			
			sv[i] = s;
			i++;
			
		}
		
		db.clear();
		
		return sv;
	}
	
	
	public static TypeVO paymentlist(RequestVO r){
		//TypeVO t = new TypeVO();
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO t =Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);
		
		try{
			Logger.info(r.getId()+"paylisting");
			StringBuilder sb = new StringBuilder();
			boolean project = r.getId().startsWith("P");
			boolean activity = r.getId().startsWith("A");
			boolean statement = r.getId().startsWith("S");
			boolean payment = r.getId().startsWith("T");
			boolean user = r.getId().startsWith("U");
			
			int id = Operator.toInt(Operator.subString(r.getId(), 1, r.getId().length()));
			
			if(user){
				sb.append(FinanceSQL.getDepositBalance(id));
				t.setType("user");
			}
			else
			if(payment){
				sb.append(FinanceSQL.getPayment(id));
				t.setType("payment");
			} else {
				/*sb.append(" select DISTINCT PY.*,M.METHOD_TYPE");
				
				
				sb.append(" from REF_ACT_PAYMENT R ");  
				sb.append(" join PAYMENT PY on R.PAYMENT_ID=PY.ID ");  
				sb.append(" join ACTIVITY A on R.ACTIVITY_ID= A.ID ");
				sb.append(" join PROJECT P on A.PROJECT_ID=P.ID ");
				sb.append(" join LKUP_PAYMENT_METHOD M on PY.LKUP_PAYMENT_METHOD_ID= M.ID ");
				sb.append(" left outer join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID ");
				
				sb.append(" join STATEMENT S on RAS.STATEMENT_ID=S.ID ");
				sb.append(" where S.ID is not null ");
				if(statement){
					sb.append(" AND ");
					sb.append(" S.ID=").append(Operator.subString(r.getId(), 1, r.getId().length()));
				}else if(activity){
					sb.append(" AND ");
					sb.append(" R.ACTIVITY_ID=").append(Operator.subString(r.getId(), 1, r.getId().length()));
					//sb.append(" group by P.ID,P.NAME,A.ID ,ACT_NBR");
				}else if(project){
					sb.append(" AND ");
					sb.append(" P.ID=").append(Operator.subString(r.getId(), 1, r.getId().length()));
					//sb.append(" group by P.ID,P.NAME,A.ID,ACT_NBR");
				}
				sb.append(" order by PY.CREATED_DATE DESC");*/
				
				sb.append(FinanceSQL.getPayments(r));
			}
		
			
			
			//t.setEntity("finance");
			
			String command = sb.toString();
			Sage db = new Sage();
			db.query(command);
			
			SubObjVO[] svp = Choices.getChoices("select ID,METHOD_TYPE as VALUE, METHOD_TYPE AS TEXT, '' as SELECTED   from LKUP_PAYMENT_METHOD where ACTIVE='Y' and REVERSE_MODE ='Y' order by METHOD_TYPE ");
			
			PaymentVO sv[] = new PaymentVO[db.size()];
			int s =0;
			while(db.next()){
				PaymentVO p = new PaymentVO();
				if(!user){
				
					p.setPaymentid(db.getInt("ID"));
					p.setAmount(db.getDouble("PAYMENT_AMOUNT"));
					p.setPayeeid(db.getInt("PAYEE_ID"));
					//p.setOtherpayeename(db.getString("PAYEE_DETAILS"));
					p.setOnline(db.getString("ONLINE"));
					p.setMethod(db.getInt("LKUP_PAYMENT_METHOD_ID"));
					p.setMethodname(db.getString("METHOD_TYPE"));
					p.setRevpaymentid(db.getInt("REV_PAYMENT_ID"));
					p.setRevamount(db.getDouble("REV_AMOUNT"));
					
					p.setOnlinetranasactionnumber(db.getString("ONLINE_TRANS_ID"));
					p.setTransactiontypename(db.getString("TRANSACTIONTYPE"));
					
					p.setPaymentdate(db.getString("PAYMENTDATE"));
					if(db.getInt("PAYEE_ID")>0){
						p.setOtherpayeename(db.getString("TEXT"));
					}else {
						p.setOtherpayeename(db.getString("PAYEE"));
					}
					Logger.info("*******ORHTER*********"+ p.getOtherpayeename());
					p.setComment(db.getString("COMMENT"));
					p.setMethods(svp); 
					//svo.setStatements(getActivityStatements(db.getInt("ID"),db.getDouble("AMOUNT"),r.getId()));
				
				} else {
				
					p.setPaymentid(db.getInt("USERS_ID"));
					p.setAmount(db.getDouble("AMOUNT"));
					p.setRevamount(db.getDouble("AMOUNT"));
					p.setPayeeid(db.getInt("USERS_ID"));
					p.setMethodname("REFUND DEPOSIT ");
					p.setPaymentdate(db.getString("PAYMENTDATE"));
					p.setTransactiontypename("REVERSE");
					p.setOtherpayeename(db.getString("TEXT"));
					p.setOnline("N");
					//p.setOtherpayeename(db.getString("PAYEE_DETAILS"));
					/*p.setOnline(db.getString("ONLINE"));
					p.setMethod(db.getInt("LKUP_PAYMENT_METHOD_ID"));
					p.setMethodname(db.getString("METHOD_TYPE"));
					p.setRevpaymentid(db.getInt("REV_PAYMENT_ID"));
					p.setRevamount(db.getDouble("REV_AMOUNT"));
					
					p.setOnlinetranasactionnumber(db.getString("ONLINE_TRANS_ID"));
					p.setTransactiontypename(db.getString("TRANSACTIONTYPE"));
					
					p.setPaymentdate(db.getString("PAYMENTDATE"));*/
					/*if(db.getInt("PAYEE_ID")>0){
						p.setOtherpayeename(db.getString("TEXT"));
					}else {
						p.setOtherpayeename(db.getString("PAYEE"));
					}*/
					Logger.info("*******ORHTER*********"+ p.getOtherpayeename());
					p.setComment(db.getString("COMMENT"));
					p.setMethods(svp); 
				}
				sv[s] = p;
				s++;
			}
			
			if(!payment && !user){
				sb = new StringBuilder();
				sb.append(" select SUM(PD.AMOUNT) as AMOUNT ");
				sb.append(" 		from payment p   ");
				sb.append(" join REF_ACT_PAYMENT RAP ON P.ID = RAP.PAYMENT_ID  ");
				sb.append(" JOIN REF_ACT_STATEMENT RAS on RAP.ACTIVITY_ID = RAS.ACTIVITY_ID ");
				sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID= SD.STATEMENT_ID  ");
				sb.append(" JOIN PAYMENT_DETAIL PD on P.ID= PD.PAYMENT_ID AND SD.ID= PD.STATEMENT_DETAIL_ID ");
				sb.append(" where RAP.ACTIVITY_ID=  ").append(r.getTypeid());
				command = sb.toString();
				db.query(command);
				if(db.next()){
					t.setAmount(db.getDouble("AMOUNT"));
				}
			}
			
			
			db.clear();
			
			t.setPayment(sv);
			
			} catch (Exception e){
				e.printStackTrace();
				t.setSubtitle(e.getMessage());
			}
			return t;
		}
	
	
	public static StatementVO[] getActivityStatements(int paymentId,double paymentamount,String searched){
		StatementVO[] svo = new StatementVO[0];
		Sage db = new Sage();
		Sage db2 = new Sage();
		if(paymentId>0){
			
			StringBuilder sb = new StringBuilder();
			sb.append(" select A.ID as  ACTIVITY_ID,ACT_NBR,P.ID,P.NAME,SUM(S.FEE_AMOUNT) as FEE_AMOUNT, SUM(S.PAID_AMOUNT) as FEE_PAID, SUM(S.BALANCE_DUE) as BALANCE_DUE ");
			sb.append(" from REF_ACT_PAYMENT R ");
			sb.append(" join activity a on R.ACTIVITY_ID=A.ID ");
			sb.append(" join project p on A.PROJECT_ID=P.ID ");
			sb.append(" left outer join REF_ACT_STATEMENT RAS on A.ID=RAS.ACTIVITY_ID ");
			sb.append(" left outer join STATEMENT S on RAS.STATEMENT_ID=S.ID ");
			sb.append(" where payment_id=").append(paymentId).append(" group by P.ID,P.NAME,A.ID ,ACT_NBR ");
			db.query(sb.toString());
			svo = new StatementVO[db.size()];
			int counter =0;
			while(db.next()){
				StatementVO sv = new StatementVO();
				sv.setProjectid(db.getInt("ID"));
				sv.setProjectname(db.getString("NAME"));
				sv.setActivityid(db.getInt("ACTIVITY_ID"));
				sv.setActivitynumber(db.getString("ACT_NBR"));
				sv.setStatementid(db.getInt("STATEMENT_ID"));
				sv.setStatementnumber(db.getString("STATEMENT_NUMBER"));
				
				sv.setAmount(db.getDouble("FEE_AMOUNT"));
				sv.setPaidamount(db.getDouble("FEE_PAID"));
				
				//TODO Revert back
				sv.setBalancedue(db.getDouble("BALANCE_DUE"));
				sv.setInputamount(db.getDouble("BALANCE_DUE"));
				/*sv.setBalancedue(db.getDouble("FEE_AMOUNT"));
				sv.setInputamount(db.getDouble("FEE_AMOUNT"));*/
				sv.setSearched(searched);
				svo[counter] = sv;
				counter++;
			}	
			
			
			for(int i=0;i<svo.length;i++){
				int activityId = svo[i].getActivityid();
				String command = FinanceSQL.getActivityPaymentStatement(activityId,paymentId);
				db.query(command);
				FeesGroupVO[] groups = new FeesGroupVO[db.size()];
				int count =0;
				while(db.next()){
					FeesGroupVO g = new FeesGroupVO();
					g.setGroup(db.getString("GROUP_NAME"));
					g.setGroupid(db.getInt("GROUP_ID"));
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
					g.setInputamount(g.getBalancedue());
					/*g.setBalancedue(db.getDouble("FEE_AMOUNT"));
					g.setInputamount(db.getDouble("FEE_AMOUNT"));*/
					g.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+g.getGroupid());
					command = FinanceSQL.getActivityPaymentStatementsDetails(paymentId,g.getGroupid());
					db2.query(command);
					FeeVO[] farr = new FeeVO[db2.size()];
					int fcount =0;
					while(db2.next()){
						FeeVO f = new FeeVO();
						//f.setGroup(db2.getString("GROUP_NAME"));
						//f.setGroupid(db2.getInt("GROUP_ID"));
						f.setPaymentdetailid(db2.getInt("PAYMENT_DETAIL_ID"));
						f.setStatementdetailid(db2.getInt("ID"));
						f.setFeeid(db2.getInt("FEE_ID"));
						f.setName(db2.getString("NAME"));
						//f.setFormula(db2.getString("DEFINITION"));
						//f.setRequired(db2.getString("REQ"));
						//f.setStartdate(db2.getString("START_DATE")); 
					
						f.setAmount(db2.getDouble("FEE_AMOUNT"));
						f.setPaidamount(db2.getDouble("FEE_PAID"));
						
						//TODO Revert back
						f.setBalancedue(db2.getDouble("BALANCE_DUE"));
						f.setInputamount(db2.getDouble("BALANCE_DUE"));
						/*f.setBalancedue(db2.getDouble("FEE_AMOUNT"));
						f.setInputamount(db2.getDouble("FEE_AMOUNT"));*/
						
						f.setFeedate(db2.getString("FEE_DATE"));
						f.setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid()+"_"+g.getGroupid()+"_"+f.getStatementdetailid());
						farr[fcount] = f;
						fcount++;
						
					}
					g.setFees(farr);
					
					groups[count] = g;
					count++;
				
				}
				svo[i].setGroups(groups);
				svo[i].setCombined(svo[i].getProjectid()+"_"+svo[i].getActivityid());
			}
			
		}
		db2.clear();
		db.clear();
		return svo;
		
	}
 	
	
	
	
	
	//handle reversal
	public static ResponseVO reversePayment(ResponseVO o,RequestVO r, Token u){
		try{
			TypeVO t = new TypeVO();
			PaymentVO p = r.getPayment();
			
			p.setRevpaymentid(p.getPaymentid());
			p.setTransactiontype(5);
			double amount = p.getAmount();
			boolean result = false;
			int paymentId =0;
			boolean user = false; 
			if(r.getRef().equals("user")){
				user = true;
			}
			Sage db = new Sage();
			
			String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
			result = db.update(command);
				
			if(result){
				db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
				
				if(db.next()){
					paymentId = db.getInt("ID");
				}
				
				
				boolean deposit = false;
				command = FinanceSQL.getMethodDetail(p.getMethod());
				db.query(command);
				if(db.next()){
					deposit = Operator.s2b(db.getString("APPLY_DEPOSIT"));
				}
				StringBuilder sb = new StringBuilder();
				if(paymentId>0){
						
						
						sb.append(" WITH Q AS (  ");
						sb.append(" select SUM(AMOUNT) as QAMOUNT,STATEMENT_DETAIL_ID FROM PAYMENT_DETAIL WHERE (PAYMENT_ID= ").append(p.getPaymentid()).append(" OR REV_PAYMENT_ID= ").append(p.getPaymentid()).append(" ) group by STATEMENT_DETAIL_ID   ");
						sb.append(" ) ");
						sb.append(" insert INTO payment_detail(payment_id,AMOUNT,STATEMENT_DETAIL_ID,REV_PAYMENT_ID,REV_TRANSACTION_ID,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
						sb.append(" (SELECT  ").append(paymentId).append(" AS payment_id,-QAMOUNT, Q.STATEMENT_DETAIL_ID, PAYMENT_ID, ID,").append(u.getId()).append(",").append(u.getId()).append(",FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND ");
						sb.append(" FROM payment_DETAIL  PD left outer JOIN Q on PD.STATEMENT_DETAIL_ID=Q.STATEMENT_DETAIL_ID WHERE payment_id= ").append(p.getPaymentid()).append(") ");
						result = db.update(sb.toString());
						
					
						
				}
				
				
				if(deposit){
					sb = new StringBuilder();
					sb.append(" insert INTO payment_detail(payment_id,AMOUNT,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
					sb.append(" (SELECT TOP 1  ").append(paymentId).append(" AS payment_id,").append(Math.abs(p.getAmount())).append(", ").append(u.getId()).append(",").append(u.getId()).append(",FM.ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND   from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null) ");
					result = db.update(sb.toString());
				}	
					
				
				
				db.clear();
				
				Logger.info("DPOSIT "+deposit);
				
				//insert ref
				if(result && paymentId>0){
					/*command = FinanceSQL.insertRefPayment("activity",r.getTypeid(),paymentId, u.getId());
					if(Operator.hasValue(command)){
						result = db.update(command);
					}*/
					
					
					
					updatestatements(paymentId,u.getId());
					
					if(deposit){
						result = DepositAgent.reversePayment(paymentId, u.getId(), r.getIp());
					}
					
				}
				
				
				
				
			}
			
			
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
	
	
	
	//handle reversal
		public static ResponseVO refundUserDeposit(ResponseVO o,RequestVO r, Token u){
			Sage db = new Sage();
			try{
				TypeVO t = new TypeVO();
				PaymentVO p = r.getPayment();
				p.setPayeeid(p.getPaymentid());
				p.setRevpaymentid(p.getPaymentid());
				p.setTransactiontype(5);
				double amount = p.getAmount();
				boolean result = false;
				int paymentId =0;
				boolean user = false; 
				if(r.getRef().equals("user")){
					user = true;
				}
				
				Logger.info(p.getPaymentid());
				
					
				String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
				result = db.update(command);
					
				if(result){
					db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
					
					if(db.next()){
						paymentId = db.getInt("ID");
					}
					
					
					
					if(paymentId>0){
						StringBuilder sb = new StringBuilder();
						
						
						sb.append(" insert INTO payment_detail(payment_id,AMOUNT,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
						sb.append(" (SELECT TOP 1  ").append(paymentId).append(" AS payment_id,").append(p.getAmount()).append(", ").append(u.getId()).append(",").append(u.getId()).append(",FM.ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND   from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null) ");
						result = db.update(sb.toString());
						
						sb = new StringBuilder();
						//sb.append(" UPDATE PAYMENT SET REV_PAYMENT_ID=").append(paymentId).append("  WHERE ID=").append(p.getPaymentid()).append("");
						//sb.append(" UPDATE PAYMENT SET  REV_AMOUNT = REV_AMOUNT ").append(p.getAmount()).append(" WHERE ID=").append(p.getPaymentid()).append("");
						//result = db.update(sb.toString());
					}
					
					if(paymentId>0){
					
						
						
						
						DepositCreditVO d = new DepositCreditVO ();
						d.setPaymentid(paymentId);
						d.setAmount(p.getAmount());
						d.setType(p.getTransactiontype());
						d.setComment(p.getComment());
						command =DepositSQL.insertDeposit(d, u.getId(), r.getIp());
						result = db.update(command);
						
						db.query(DepositSQL.getDepositId(r.getIp(), u.getId(), paymentId));
						int depositId =0;
						if(db.next()){
							depositId = db.getInt("ID");
						}
						
						if(depositId>0){
							command = DepositSQL.insertRefDeposit("users", p.getPayeeid(),depositId,  u.getId());
							result = db.update(command);
							
							
							command = "select stuff((select  ','+convert(varchar(100),DEPOSIT_ID) from REF_USERS_DEPOSIT where USERS_ID="+p.getPayeeid()+"  for xml path('')),1,1,'') as COMBINED ";
							db.query(command);
							if(db.next()){
								String depositIds = db.getString("COMBINED");
								command = "UPDATE DEPOSIT SET CURRENT_AMOUNT =0 WHERE ID IN ("+depositIds+") AND PARENT_ID=0 ";
								result = db.update(command);
							}
							
						}
					
						
					}
					
					
				
					
					
				}
				
				
				if(result){
					t.setPayment(FinanceAgent.getPayments(u.getId()));
					o.setType(t);
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
				Logger.error("Problem while processing payment ");
			}
			db.clear();
			return o;
		}
	
	
	
	public static boolean updatestatements(int paymentId,int userId){
		boolean result = false;
		try{
			Logger.info("ENTERED TO UPDATE STATEMENTS ");
			//String command = "select pd.*,sd.statement_id from payment_detail pd join statement_detail sd on pd.STATEMENT_DETAIL_ID= sd.ID where payment_id="+paymentId;
			
			StringBuilder sb = new StringBuilder();
			sb.append(" select pd.*,sd.statement_id , ");
			sb.append(" CASE WHEN RFF.MANUAL_ACCOUNT = 'Y' THEN SFM.ID ELSE FM.ID END AS U_FINANCE_MAP_ID, ");
			sb.append(" CASE WHEN RFF.MANUAL_ACCOUNT = 'Y' THEN SFM.KEY_CODE ELSE FM.KEY_CODE END AS U_KEY_CODE, ");
			sb.append(" CASE WHEN RFF.MANUAL_ACCOUNT = 'Y' THEN SFM.ACCOUNT_NUMBER ELSE FM.ACCOUNT_NUMBER END AS U_ACCOUNT_NUMBER, ");
			sb.append(" CASE WHEN RFF.MANUAL_ACCOUNT = 'Y' THEN SFM.BUDGET_UNIT ELSE FM.BUDGET_UNIT END AS U_BUDGET_UNIT, ");
			sb.append(" CASE WHEN RFF.MANUAL_ACCOUNT = 'Y' THEN SFM.FUND ELSE FM.FUND END AS U_FUND ");

			sb.append(" from payment_detail pd  ");
			sb.append(" join statement_detail sd on pd.STATEMENT_DETAIL_ID= sd.ID  ");
			sb.append(" join REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID= RFF.ID ");
			//sb.append("  join REF_FEE_GROUP RFG on SD.GROUP_ID= RFG.FEE_GROUP_ID AND SD.FEE_ID=RFG.FEE_ID  ");
					   
			
			sb.append(" left outer join FINANCE_MAP SFM on SD.FINANCE_MAP_ID=SFM.ID ");
			sb.append(" left outer join FINANCE_MAP FM on RFF.FINANCE_MAP_ID=FM.ID ");
			sb.append(" where payment_id= ").append(paymentId);
			
			String command = sb.toString();
			
			Sage db= new Sage();
			
			db.query(command);
			JSONArray pd = new JSONArray();
			ArrayList<String> pdaccounts = new ArrayList<String>();
			while(db.next()){
				JSONObject pdt = new JSONObject();
				pdt.put("statementid", db.getInt("STATEMENT_ID"));
				pdt.put("statementdetailid", db.getInt("STATEMENT_DETAIL_ID"));
				pdt.put("amount", db.getDouble("AMOUNT"));
				
				
				sb = new StringBuilder();
				sb.append(" UPDATE PAYMENT_DETAIL SET ");
				sb.append(" FINANCE_MAP_ID = ").append(db.getInt("U_FINANCE_MAP_ID"));
				sb.append(" ,");
				sb.append(" KEY_CODE = '").append(db.getString("U_KEY_CODE")).append("'");
				sb.append(" ,");
				sb.append(" BUDGET_UNIT = '").append(db.getString("U_BUDGET_UNIT")).append("'");
				sb.append(" ,");
				sb.append(" ACCOUNT_NUMBER = '").append(db.getString("U_ACCOUNT_NUMBER")).append("'");
				sb.append(" ,");
				sb.append(" FUND = '").append(db.getString("U_FUND")).append("'");
				sb.append(" WHERE ID = ").append(db.getInt("ID"));
				Logger.info(sb.toString());
				pdaccounts.add(sb.toString());
				
				
				pd.put(pdt);
			}
			Logger.info("PD ARRAY "+pd.length());
			
			for(String queries: pdaccounts){
				result = db.update(queries);
			}
		
			for(int i=0;i<pd.length();i++){
				JSONObject pdt = pd.getJSONObject(i);
				command = "update statement_detail set fee_paid= FEE_PAID+"+pdt.getDouble("amount")+" where ID = "+pdt.getInt("statementdetailid");
				result = db.update(command);
				
				command = "update statement_detail set BALANCE_DUE= (FEE_AMOUNT- FEE_PAID)  where ID = "+pdt.getInt("statementdetailid");
				result = db.update(command);
				
				
				
		
			}
			
		
			
		
			
			HashSet<String> statementIds = new HashSet<String>();
			for(int i=0;i<pd.length();i++){
				String statementId = pd.getJSONObject(i).getString("statementid");
				if(!statementIds.contains(statementId)){
					statementIds.add(statementId);
					command = "update statement set PAID_AMOUNT=  (select sum(FEE_PAID) from  statement_detail where statement_id = "+statementId+" group by statement_id) where ID ="+statementId;
					result = db.update(command);
				
					command = "update statement set BALANCE_DUE= (FEE_AMOUNT- PAID_AMOUNT)  where ID = "+statementId;
					result = db.update(command);
				}
			}
			
			
			
			JSONArray larr = new JSONArray();
			sb = new StringBuilder();
			sb.append(" select DISTINCT(ACTIVITY_ID) as ID,'activity' as TYPE from REF_ACT_STATEMENT where STATEMENT_ID in (");
			statementIds = new HashSet<String>();
			for(int i=0;i<pd.length();i++){
				String statementId = pd.getJSONObject(i).getString("statementid");
				if(!statementIds.contains(statementId)){
					statementIds.add(statementId);
					sb.append(statementId).append(",");
				}
			}
			sb.append(-1).append(")");
			
			sb.append(" union ");
			sb.append(" select DISTINCT(PROJECT_ID) as ID,'project' as TYPE from REF_PROJECT_STATEMENT where STATEMENT_ID in (");
			statementIds = new HashSet<String>();
			for(int i=0;i<pd.length();i++){
				String statementId = pd.getJSONObject(i).getString("statementid");
				if(!statementIds.contains(statementId)){
					statementIds.add(statementId);
					sb.append(statementId).append(",");
				}
			}
			sb.append(-1).append(")");
			
			
			db.query(sb.toString());
			
			
			while(db.next()){
				JSONObject level = new JSONObject();
				level.put("type",db.getString("TYPE"));
				level.put("typeid",db.getInt("ID"));
				larr.put(level);
			}
			boolean empty = true;
			StringBuilder acids = new StringBuilder();
			for(int i=0;i<larr.length();i++){
				String type = larr.getJSONObject(i).getString("type");
				int typeid = larr.getJSONObject(i).getInt("typeid");

				command = FinanceSQL.insertRefPayment(type, typeid, paymentId, userId);
				result = db.update(command);

				if (Operator.equalsIgnoreCase(type, "activity")) {
					if (!empty) { acids.append(","); }
					acids.append(typeid);
					empty = false;
				}
				
				CsDeleteCache.deleteProjectAndActivityCache(type, typeid, "finance");
				CsDeleteCache.deleteProjectAndActivityCache(type, typeid, "activities");
			}
			db.clear();
			if (Operator.hasValue(acids.toString())) {
				TasksImpl.runPayment(acids.toString(),userId);
				autoIssue(acids.toString(), userId);
			}
			
		}catch(Exception e){
			Logger.error("Problem while updatestatements "+e.getMessage());
		}
		
		return result;
	}
	
	
	
	public static ResponseVO saveDepositCreditPayment(ResponseVO o,RequestVO r, Token u){
		try{
			PaymentVO p = r.getPayment();
			String dpp[] = Operator.split(p.getCombined(),"_");
			p.setTransactiontype(2);
			p.setRevamount(p.getAmount());
			
			DepositCreditVO dp = new DepositCreditVO();
			dp.setLevel(dpp[0]);
			dp.setParentid(Operator.toInt(dpp[1]));
			dp.setAmount(Operator.toDouble(dpp[2]));
			dp.setType(Operator.toInt(dpp[3]));
			dp.setId(Operator.toInt(dpp[4]));
		
			
			//JSONArray adarr = new JSONArray();
			boolean result = false;
			int paymentId =0;
			boolean activity = Operator.equalsIgnoreCase(dp.getLevel(), "ACTIVITY");
			boolean project = Operator.equalsIgnoreCase(dp.getLevel(), "PROJECT");
			boolean user = Operator.equalsIgnoreCase(dp.getLevel(), "USER");
			
			String type ="";
			JSONArray pdarr = new JSONArray();
			if(activity) { pdarr = getPaymentDetailActivity(p.getAmount(), dp.getParentid(), r.getStatements()); type = "activity" ;}
			if(project) { pdarr = getPaymentDetailActivity(p.getAmount(), dp.getId(), r.getStatements()); type = "project" ;}
			if(user) { pdarr = getPaymentDetailActivity(p.getAmount(), dp.getId(), r.getStatements()); type = "users" ;}
			//if(activity) { pdarr = getPaymentDetailActivity(p.getAmount(), dp.getParentid(), r.getStatements());}
			
			
			if(pdarr.length()>0){
				Sage db = new Sage();
				
				String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
				result = db.update(command);
				
				if(result){
					db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
					
					if(db.next()){
						paymentId = db.getInt("ID");
					}
					
					if(paymentId>0){
						for(int i=0;i<pdarr.length();i++){
							JSONObject f = pdarr.getJSONObject(i);
							f.put("paymentid", paymentId);
							if(f.getDouble("amount")>0){
								command = FinanceSQL.insertPaymentDetail(f, r.getIp(),  u.getId());
								result = db.update(command);
							}		
						}
					}
				}
				
				//insert ref
				if(result && paymentId>0){
					/*command = FinanceSQL.insertRefPayment(type,dp.getParentid(),paymentId, u.getId());
					result = db.update(command);*/
					
					
					StringBuilder sb = new StringBuilder();
					sb.append(" insert INTO payment_detail(payment_id,AMOUNT,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
					sb.append(" (SELECT TOP 1  ").append(paymentId).append(" AS payment_id,-").append(p.getAmount()).append(", ").append(u.getId()).append(",").append(u.getId()).append(",FM.ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND   from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null) ");
					result = db.update(sb.toString());
					
				}
				
				
				db.clear();
				
				
			}
		
		
		
		if(result){
			 result = DepositAgent.insertUpdateDepositCredit(dp, p.getAmount(), paymentId, u.getId(), r.getIp(),type);
			 updatestatements(paymentId, u.getId());
			 
			 o.setType(getPaymentResponse(r.getStatements(), paymentId,r,u.getId()));
				
				
		}
			
			
			
			
		}catch(Exception e){
			Logger.error("Problem while processing saveDepositCreditPayment "+e.getMessage());
		}
		return o;
	}
	
	
	public static JSONArray getPaymentDetailActivity(double amount,int id,StatementVO s[]){
		
		JSONArray pdarr = new JSONArray();
		try{
			double tamount = amount;
		
			for(int i=0;i<s.length;i++){
				if(s[i].getActivityid()==id){	
					if(tamount>0){
						for(int j=0;j<s[i].getGroups().length;j++){
							if(tamount>0){
								FeesGroupVO g = s[i].getGroups()[j];
								for(int k=0;k<g.getFees().length;k++){
									if(tamount>0){
										FeeVO f = g.getFees()[k];
										double feeamount = f.getInputamount();
										if(feeamount>tamount){
											JSONObject pd = new JSONObject();
											pd.put("statementdetailid", f.getStatementdetailid());
											pd.put("amount",tamount);
											pd.put("feeid", f.getFeeid());
											pdarr.put(pd);
											tamount = 0;
										}else {
											tamount = subDouble(tamount,feeamount);
											JSONObject pd = new JSONObject();
											pd.put("statementdetailid", f.getStatementdetailid());
											pd.put("amount",feeamount);
											pd.put("feeid", f.getFeeid());
											pdarr.put(pd);
										}
									}
								}// end g.getFees	
							
							}
						}// end g.getGroups
					}
				}
			}
		}catch(Exception e){
			Logger.error("Problem while processing payment "+e.getMessage());
		}			
		return pdarr;
	}
	
	
	
	
	public static TypeVO showstatementpayment(RequestVO r){
		Token u = Token.retrieve(r.getToken(), r.getIp());
		TypeVO t = Types.getType(r.getType(), r.getTypeid(), r.getEntity(), r.getOption(), u);
		PaymentVO[] pa = new PaymentVO[0];
		int statementDetailId = Operator.toInt(r.getGroupid());
		if(statementDetailId>0){		
			Sage db = new Sage();
			String command = FinanceSQL.getPaymentStatementDetail(statementDetailId);
			db.query(command);
			pa = new PaymentVO[db.size()];
			int counter =0;
			SubObjVO[] s = Choices.getChoices("select ID,METHOD_TYPE as VALUE, METHOD_TYPE AS TEXT, '' as SELECTED   from LKUP_PAYMENT_METHOD where ACTIVE='Y' and REVERSE_MODE ='Y' order by METHOD_TYPE ");
			while(db.next()){
				PaymentVO p = new PaymentVO();
				p.setPaymentid(db.getInt("ID"));
				p.setMethodname(db.getString("METHOD"));
				p.setAmount(db.getDouble("AMOUNT"));
				p.setRevpaymentid(db.getInt("PAYMENT_ID"));
				p.setRevamount(db.getDouble("REV_AMOUNT"));
				p.setTransactiontypename(db.getString("TRANSACTIONTYPE"));
				p.setPayeeid(db.getInt("PAYEE_ID"));
				p.setPaymentdate(db.getString("PAYMENTDATE"));
				if(db.getInt("PAYEE_ID")>0){
					p.setOtherpayeename(db.getString("TEXT"));
				}else {
					p.setOtherpayeename(db.getString("PAYEE_DETAILS"));
				}
				p.setComment(db.getString("COMMENT"));
				
				p.setMethods(s);
				
				pa[counter] = p;
				counter++;
			}
			
			db.clear();
		}
		
		t.setPayment(pa);
		return t;
	}
	
	//TODO all levels if required currently sending feevo
	public static FeeVO[] showledgerpayment(RequestVO r){
		FeeVO[] fa = new FeeVO[0];
		PaymentVO p = r.getPayment();
		int paymentId = p.getPaymentid();
		if(paymentId>0){		
			Sage db = new Sage();
			String command = "select PD.ID,PD.AMOUNT,P.ID as STATMENT_DETAIL_ID,F.NAME,P.FEE_AMOUNT,P.BALANCE_DUE,A.ACT_NBR from PAYMENT_DETAIL PD join STATEMENT_DETAIL P on PD.STATEMENT_DETAIL_ID=P.ID join FEE F on P.FEE_ID= F.ID left outer join REF_ACT_STATEMENT RAP on P.STATEMENT_ID=RAP.STATEMENT_ID  left outer join ACTIVITY A on RAP.ACTIVITY_ID= A.ID where PD.PAYMENT_ID= "+paymentId+" ORDER BY A.ACT_NBR";
			db.query(command);
			fa = new FeeVO[db.size()];
			int counter =0;
			while(db.next()){
				FeeVO f = new FeeVO();
				f.setName(db.getString("NAME"));
				f.setPaymentid(paymentId);
				f.setPaymentdetailid(db.getInt("ID"));
				f.setStatementdetailid(db.getInt("STATMENT_DETAIL_ID"));
				
				f.setAmount(db.getDouble("FEE_AMOUNT"));
				f.setPaidamount(db.getDouble("AMOUNT"));
				f.setBalancedue(db.getDouble("BALANCE_DUE"));
				f.setDescription(db.getString("ACT_NBR"));
				
				fa[counter] = f;
				counter++;
			}
			
			db.clear();
		}
		
		//t.setPayment(fa);
		return fa;
	}
	
	
	
	public static ObjGroupVO printFinanceSummary(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		String command = FinanceSQL.summary(r.getType(), r.getTypeid(), 0);
		Sage db = new Sage();
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		db.query(command);
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(outstyle).append("\">FINANCE SUMMARY </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> FEE GROUP </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> AMOUNT </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> PAID </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> BALANCE </td>");
		sb.append("	</tr> \n");
		//sb.append("&nbsp;<hr/> \n");
		DecimalFormat fa = new DecimalFormat("#,##0.00"); 
		while(db.next()){
			if(db.getDouble("AMOUNT")>0){
				result = true;
				sb.append("	<tr> \n");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("GROUP_NAME")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(db.getDouble("AMOUNT"))).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(db.getDouble("PAID"))).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(db.getDouble("BALANCE"))).append("</td>");
				sb.append("	</tr> \n");
			}
			//sb.append("<fo:leader leader-alignment=\"reference-area\" leader-pattern=\"rule\" /> \n");
		}
		sb.append("</tbody>");
		sb.append("</table> \n");
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_financesummary");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	
	
	public static ObjGroupVO printFeeSummary(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		TypeVO fees = getFees(r);
		
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(outstyle).append("\">FEE </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		DecimalFormat fa = new DecimalFormat("#,##0.00"); 
		if(fees.getStatements().length>0){
			sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
			sb.append("<tbody>");
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(labelstyle).append("\"> GROUP </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> FEE </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> ACCOUNT </td>");
			//sb.append("	<td style=\"").append(labelstyle).append("\"> INPUT </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> AMOUNT </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> PAID </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> BALANCE DUE </td>");
			sb.append("	</tr> \n");
			
			StatementVO s = fees.getStatements()[0];
			for(int i=0;i<s.getGroups().length;i++){
				FeesGroupVO fg = s.getGroups()[i];
				
					for(int j=0;j<fg.getFees().length;j++){
						FeeVO f = fg.getFees()[j];
						if(f.getAmount()>0){
							result = true;
							sb.append("	<tr> \n");
							sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(fg.getGroup()).append("</td>");
							sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(f.getName()).append("</td>");
							sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(f.getAccount()).append("</td>");
							//sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(f.getInput3()).append("</td>");
							sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(f.getAmount())).append("</td>");
							sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(f.getPaidamount())).append("</td>");
							sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(f.getBalancedue())).append("</td>");
							sb.append("	</tr> \n");
						}
					}
			}
			
			sb.append("</tbody>");
			sb.append("</table> \n");
		}
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_feesummary");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	
	
	public static ObjGroupVO printFinanceLedger(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		String command = FinanceSQL.getPayments(r);
		Sage db = new Sage();
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;";
		
		db.query(command);
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(outstyle).append("\">PAYMENT SUMMARY </td>");
		sb.append("	</tr> \n");
		sb.append("</tbody>");
		sb.append("</table> \n");
		
		sb.append("<table align=\"center\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\" style=\"width:100%\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> TRANSACTION NO </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> METHOD </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> DATE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> PAID BY </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> AMOUNT </td>");
		sb.append("	</tr> \n");
		//sb.append("&nbsp;<hr/> \n");
		DecimalFormat fa = new DecimalFormat("#,##0.00"); 
		while(db.next()){
			result = true;
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("ID")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("METHOD_TYPE")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("PAYMENTDATE")).append("</td>");
			if(db.getInt("PAYEE_ID")>0){
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("TEXT")).append("</td>");
			}else {
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("PAYEE_DETAILS")).append("</td>");
			}
			sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(db.getDouble("PAYMENT_AMOUNT"))).append("</td>");
			
			sb.append("	</tr> \n");
			
			
			
		}
		sb.append("</tbody>");
		sb.append("</table> \n");
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_financeledger");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	
	
	public static ObjGroupVO printFinanceTransactionSummary(RequestVO r){
		ObjGroupVO g = new ObjGroupVO();
		String command = FinanceSQL.getPayment(Operator.toInt(r.getId()));
		Sage db = new Sage();
		
		String outstyle ="font-size:11pt; background-color:#C9C299; font-family:sans-serif; line-height:16pt; space-before.optimum:5pt; space-after.optimum:5pt; text-align:left;  font-weight:bold;";
		String labelstyle ="font-size:9pt;font-weight:bold;font-family:sans-serif;  border-bottom: 2px solid black;padding-top: 12px; padding-bottom: 12px; text-align: left; background-color: #777777; color: white;";
		String valuestyle ="font-size:9pt;font-family:sans-serif;border: 1px solid #ddd; padding: 8px;";
		String style = "<style> .transaction tr:nth-child(even){background-color: #f2f2f2;} </style>";
		db.query(command);
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		
		sb.append(style);
		
		sb.append("<table align=\"center\" style=\"font-family:  Arial, Helvetica, sans-serif; border-collapse: collapse;  width: 100%;\" class=\"transaction\"> \n");
		sb.append("<tbody>");
		sb.append("	<tr> \n");
		sb.append("	<td style=\"").append(labelstyle).append("\"> TRANSACTION NO. </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> METHOD </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> TYPE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> DATE </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> PAID BY </td>");
		sb.append("	<td style=\"").append(labelstyle).append("\"> AMOUNT </td>");
		sb.append("	</tr> \n");
		//sb.append("&nbsp;<hr/> \n");
		DecimalFormat fa = new DecimalFormat("#,##0.00"); 
		if(db.next()){
			result = true;
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(valuestyle).append("\" > ").append(db.getString("ID")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("METHOD_TYPE")+" "+db.getString("ACCOUNT_NO")).append("</td>");
			sb.append("	<td style=\"").append(valuestyle).append("white-space:nowrap;\" > ").append(db.getString("TRANSACTIONTYPE")).append("</td>");

			sb.append("	<td style=\"").append(valuestyle).append("white-space:nowrap;\" > ").append(db.getString("PAYMENTDATE")).append("</td>");
			if(db.getInt("PAYEE_ID")>0){
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("TEXT")).append("</td>");
			}else {
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("PAYEE_DETAILS")).append("</td>");
			}
			sb.append("	<td style=\"").append(valuestyle).append("white-space:nowrap;\" > $").append(fa.format(db.getDouble("PAYMENT_AMOUNT"))).append("</td>");
			
			sb.append("	</tr> \n");
			
			sb.append("</tbody>");
			sb.append("</table> \n");
			
			sb.append(" <table align=\"center\" style=\"font-family:  Arial, Helvetica, sans-serif; border-collapse: collapse;  width: 100%;\">");
			sb.append("</br> </br>");
			sb.append("<tbody>");
			sb.append("<tr>");
			sb.append("<td style=\"height: 20px;\"><span style=\"font-size:14px;\"><span style=\"font-family:Arial,Helvetica,sans-serif;\"><big><span style=\"color:#000;\">DETAILS</span></big></span></span></td>");
			sb.append("</tr>");
			sb.append("</tbody>");
			sb.append("</table>");

			//list
			sb.append("<table align=\"center\" style=\"font-family:  Arial, Helvetica, sans-serif; border-collapse: collapse;  width: 100%;\" class=\"transaction\"> \n");
			sb.append("<tbody>");
			sb.append("	<tr> \n");
			sb.append("	<td style=\"").append(labelstyle).append("\"> NUMBER </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> TYPE </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> GROUP </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> FEE </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> ACCOUNT </td>");
			sb.append("	<td style=\"").append(labelstyle).append("\"> PAID </td>");
			sb.append("	</tr> \n");
			
			command = FinanceSQL.getPaymentDetails(Operator.toInt(r.getId()));
			db.query(command);
			while(db.next()){
				sb.append("	<tr> \n");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("ACT_NBR")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("TYPE")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("GROUP_NAME")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("NAME")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> ").append(db.getString("ACCOUNT_NUMBER")).append("</td>");
				sb.append("	<td style=\"").append(valuestyle).append("\"> $").append(fa.format(db.getDouble("AMOUNT"))).append("</td>");
				sb.append("	</tr> \n");
			}
			
			sb.append("</tbody>");
			sb.append("</table> \n");
		}
		
		db.clear();
		
		
		ObjVO[] oa = new ObjVO[1];	
		ObjVO o = new ObjVO();
		o.setLabel(r.getType()+"_list_transactionsummary");
		if(result){
			o.setValue(sb.toString());
		}else {
			o.setValue("");
		}	
		oa[0] = o;
		g.setObj(oa);
	
		return g;
	}
	
	
	
	/*public static TypeVO autosave(String entity, String type, int typeId, int userId){
		return autosave(entity, type, typeId, "", userId,false);
	}
	
	public static TypeVO autosave(String entity, String type, int typeId, int userId,boolean plancheck){
		return autosave(entity, type, typeId, "", userId, plancheck);
	}*/
	
	public static TypeVO autosave(String entity, String type, int typeId, String feedatepick, int userId){
		boolean plancheck = false;
		Sage db = new Sage();
		db.query("select * from activity where PLAN_CHK_REQ ='Y' AND ID = "+typeId);
		if(db.next()){
			plancheck = true;
		}
		db.clear();
		
		return autosave(entity, type, typeId, feedatepick, userId, plancheck);
	}
	
	public static TypeVO autosave(String entity, String type, int typeId, String feedatepick, int userId, boolean plancheck){
		RequestVO o = new RequestVO();
		o.setTypeid(typeId);
		o.setType(type);
		o.setEntity(entity);
		Logger.info("START_DATE to FEE DATE"+feedatepick);
		if(!Operator.hasValue(feedatepick)){
			Timekeeper k = new Timekeeper();
			feedatepick =k.getString("YYYY/MM/DD");
		}
		Logger.info("START_DATE to FEE DATE AFTER INITIAL "+feedatepick);
		return autosave(o, feedatepick, userId, typeId, plancheck);
	}
	
	public static TypeVO autosave(RequestVO o, String feedatepick, int userId, int id, boolean plancheck){
		TypeVO r = new TypeVO();
		Logger.info("Calculate Fees");
		Sage db = new Sage();
		Sage db2 = new Sage();
		try {
			boolean save = true;
			
			o.setTypeid(id);
			
			r.setTypeid(o.getTypeid());
			r.setType(o.getType());
			r.setEntity(o.getEntity());
			
			
			
			//st
			String divisionfee = divisionCheckIds(o);
			
			String command = FinanceSQL.getFeeGroups(o);
			db.query(command);
			FeesGroupVO[] groups = new FeesGroupVO[db.size()];
			int count =0;
			while(db.next()){
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(db.getString("GROUP_NAME"));
				g.setGroupid(db.getInt("GROUP_ID"));
				if(db.getDouble("FEE_AMOUNT")>0){
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
				}
				
				/*if(g.getAmount()>0){
					amount = Operator.addDouble(g.getAmount(), amount);
				}
				if(g.getPaidamount()>0){
					paidamount = Operator.addDouble(g.getPaidamount(), paidamount);
				}*/
				
				//ff
				
			    command = FinanceSQL.getFeeList(r.getType(), r.getTypeid(), r.getEntity(),g.getGroupid(),feedatepick,true,plancheck,divisionfee);
				
				db2.query(command);
				FeeVO[] fees = new FeeVO[db2.size()];
				int fcount =0;
				while(db2.next()){
					FeeVO f = new FeeVO();
					f.setFeeid(db2.getInt("ID"));
					f.setReffeeformulaid(db2.getInt("RFF_ID"));
					
					f.setName(db2.getString("NAME"));
					
					String define = db2.getString("DEFINITION");
					define = define.replaceAll("currentgroup",db2.getString("GROUP_NAME"));
					define = define.replaceAll("currentfeegroup",db2.getString("GROUP_NAME"));
					define = define.replaceAll("[\\t\\n\\r]+"," ");
					f.setFormula(define);
					f.setGroup(db2.getString("GROUP_NAME"));
					f.setGroupid(db2.getInt("GROUP_ID"));
					
					f.setFormulaId(db2.getInt("LKUP_FORMULA_ID"));
					f.setRequired(db2.getString("REQ"));
					f.setStartdate(db2.getString("START_DATE"));
					f.setInput1(db2.getDouble("INPUT1"));
					f.setInput2(db2.getDouble("INPUT2"));
					f.setInput3(db2.getDouble("INPUT3"));
					f.setInput4(db2.getDouble("INPUT4"));
					f.setInput5(db2.getDouble("INPUT5"));
					
					
					f.setInputtype1(db2.getInt("INPUT_TYPE1"));
					f.setInputtype2(db2.getInt("INPUT_TYPE2"));
					f.setInputtype3(db2.getInt("INPUT_TYPE3"));
					f.setInputtype4(db2.getInt("INPUT_TYPE4"));
					f.setInputtype5(db2.getInt("INPUT_TYPE5"));
					
					f.setInputtypelabel1(db2.getString("INPUT_LABEL1"));
					f.setInputtypelabel2(db2.getString("INPUT_LABEL2"));
					f.setInputtypelabel3(db2.getString("INPUT_LABEL3"));
					f.setInputtypelabel4(db2.getString("INPUT_LABEL4"));
					f.setInputtypelabel5(db2.getString("INPUT_LABEL5"));
					
					f.setStartdate(db2.getString("START_DATE"));
					f.setExpdate(db2.getString("EXPIRATION_DATE"));
					
					f.setFeedate(feedatepick);
					
					ObjectMapper mapper = new ObjectMapper();
					//mapper.set
					String json = mapper.writeValueAsString(f);
					f.setDescription(json);
					
					
					fees[fcount] = f;
					fcount++;
				//eff
				
					
					
				}
				g.setFees(fees);
				
				groups[count] = g;
				count++;
			
			}
			
				
			StatementVO sv[] = new StatementVO[0]; 
			
			if(groups.length>0){
				sv = new StatementVO[1]; 
				StatementVO s = new StatementVO();
				s.setGroups(groups);
				sv[0] = s;
				o.setStatements(sv);		
			}
				
				
				
				
				
				
			
			
			
			
			
			//bef
	
			double valuation = getValuation(o.getType(),o.getTypeid());
		
			double total=0;
			boolean loop2 = false;
			boolean loop3 = false;
			boolean loop4 = false;
			boolean loop5 = false; // for subtotal
			int fgarrcounter =0;
			StatementVO[] svo = new StatementVO[1];
			StatementVO s = new StatementVO();
			s.setActivityid(o.getTypeid());
			FeesGroupVO[] fgarr = new FeesGroupVO[0];
			if(o.getStatements().length>0){
				fgarr = new FeesGroupVO[o.getStatements()[0].getGroups().length];
			
			
			for(int j=0;j<o.getStatements()[0].getGroups().length;j++){
				
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(o.getStatements()[0].getGroups()[j].getGroup());
				g.setGroupid(o.getStatements()[0].getGroups()[j].getGroupid());
				
				
				int feeslength =o.getStatements()[0].getGroups()[j].getFees().length;
				int farrcounter =0;
				FeeVO[] farr = new FeeVO[feeslength];
				Logger.info("feeslength****"+feeslength);
				
				for(int i=0;i<feeslength;i++){
					FeeVO f = o.getStatements()[0].getGroups()[j].getFees()[i];//data.getJSONObject(i);
					Logger.info(f.getGroup()+"INITIAL GROUP LOADING CHECK");
					f.setGroup(g.getGroup());
					f.setGroupid(g.getGroupid());
					int feeId = f.getFeeid();
					//TODO for online replace by query to get formulas from DB
					Logger.info(f.getFeedate()+"################");
					String formula = f.getFormula();
					f.setValuation(valuation);
					String feedate = Operator.replace(f.getFeedate(), "/", "-");
					
					
					double input1 = f.getInput1();
					double input2 = f.getInput2();
					double input3 = f.getInput3();
					double input4 = f.getInput4();
					double input5 = f.getInput5();
					double factor = f.getInput5();
					//double amount = Operator.toDouble(data.getJSONObject(i).getString("amount"));
					
					
					
					String newFormula = getFormula(o.getType(),o.getTypeid(),formula, Operator.toString(input1), Operator.toString(input2), Operator.toString(input3), Operator.toString(input4), Operator.toString(input5), Operator.toString(valuation),feedate,Operator.toString(factor));
					if(Operator.hasValue(newFormula)){
						Logger.info(feeId+":: index ::"+newFormula.indexOf("feename"));
						double value = 0;
						if(newFormula.indexOf("feename")>0){
							loop2= true;
							f.setModifiedformula(newFormula);
							
						}else if(newFormula.indexOf("sumoffeegroup")>0){
							loop3= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("totalallfees")>0){
							loop4= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("subtotal")>0){
							loop5= true;
							f.setModifiedformula(newFormula);
						}
						else {
							if(newFormula.indexOf("noofmonthsbetween")>0){
								newFormula = FormulaParser.noofmonthsbetween(newFormula);
							}
							value = Formula.calculate(newFormula);
						}
						Logger.info(feeId+":: AMOUNT ::"+value+"***"+f.getName());
						
						f.setAmount(value);
					} else {
						loop2= true;
						//f.put("value", 0);
						f.setAmount(0);
					}
					//newdata.put(f);
					farr[farrcounter] = f;
					farrcounter++;
					
					
					
				}
				Logger.info("feeslength***after*"+farr.length);
				g.setFees(farr);
				fgarr[fgarrcounter] = g;
				fgarrcounter++;
			}
			s.setGroups(fgarr);
			//svo[0] = s;
			//r.setGroups(fgarr);
			
			
			//boolean keepdoing = false;
			
			
			
			if(loop2){
				Logger.info("::feename loop2 ::"+loop2);
				s.setGroups(getFeeNameFormula(o,fgarr));
			}
			
			if(loop5){
				Logger.info("::subtotal loop5 ::"+loop5);
				s.setGroups(getInitSubtotalFormula(o,fgarr));
			}
			
			if(loop3){
				Logger.info("::sumoffeegroup loop3 ::"+loop3);
				s.setGroups(getSumOfFeeGroupFormula(o,fgarr));
			}
			
			if(loop4){
				Logger.info("::totalfees loop4 ::"+loop3);
				s.setGroups(getTotalFeesFormula(o,fgarr));
			}
			
			
			s = getSubTotalsAndTotals(s);
			svo[0] = s;
			r.setStatements(svo);
			if(save){
				boolean result = saveFees(userId,r);
				
			}
			//r.setStatements(svo);
			}
		} catch (Exception e) {
			Logger.error("calculate ERROR  :" + e.getMessage());
		}
		
		db2.clear();
		db.clear();
		
		return r;
		
	}
	
	
	public static ResponseVO saveCart(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		
		//TODO convert to TYPEVO to validate
		try{
			String cart = vo.getExtras().get("note");
			
			if(Operator.hasValue(cart)){
				TypeVO t = ObjMapper.toTypeObj(cart);
				Logger.info(t.getStatements().length+"#####"+t.getBalancedue()+"*****"+t.getInputamount()+"*******************"+cart);
				if(t.getInputamount()>0){
					Sage db = new Sage();
					/*String command = FinanceSQL.updateCart(u.getId(), "N", "N");
					db.update(command);*/
					String command = "";
					if(t.getCartId() <=0 ){
						command = FinanceSQL.insertCart(cart, u.getId(),t.getInputamount());
					}else{
						command = FinanceSQL.updateCart(t.getCartId(), "Y", "N", cart, t.getInputamount());
					}
					db.update(command);
					
					
					command = FinanceSQL.getCart(0, u.getId());
					db.query(command);
					if(db.next()){
						r.setMessagecode("cs200");
						r.setReference(db.getString("ID"));
						
					}
					
					
				
					db.clear();
				}else {
					r.setMessagecode("cs500");
					
					
				}
			
			}else {
				r.setMessagecode("cs500");
			}	
		}catch(Exception e){
			r.setMessagecode("cs500");
			e.printStackTrace();
		}
		return r;
	}
	
	public static ResponseVO updateCart(RequestVO vo,Token u){
		return updateCart(vo, u, "N");
	}
	
	public static ResponseVO updateCart(RequestVO vo,Token u,String active){
		ResponseVO r = new ResponseVO();
		
		//TODO convert to TYPEVO to validate
		try{
			String cart = vo.getExtras().get("note");
			int cartId = Operator.toInt(vo.getReference());
			if(Operator.hasValue(cart)){
				TypeVO t = ObjMapper.toTypeObj(cart);
				Logger.info(t.getStatements().length+"#####"+t.getBalancedue()+"*****"+t.getInputamount()+"*******************"+cart);
				if(t.getInputamount()>0  && cartId>0){
					Sage db = new Sage();
					String command = FinanceSQL.updateCart(cartId, active, "N",cart,t.getInputamount());
					boolean result = db.update(command);
					db.clear();
					
					if(result){
						r.setMessagecode("cs200");
						r.setReference(vo.getReference());
					}
					
				
					
				}else {
					r.setMessagecode("cs500");
					
					
				}
			
			}else {
				r.setMessagecode("cs500");
			}	
		}catch(Exception e){
			r.setMessagecode("cs500");
			e.printStackTrace();
		}
		return r;
	}
	
	public static ResponseVO updateCartAfterPay(RequestVO vo,Token u, String active,String paid){
		ResponseVO r = new ResponseVO();
		
		try{
			int cartId = Operator.toInt(vo.getReference());	
			Sage db = new Sage();
			String command = FinanceSQL.updateCartByCartId(cartId, u.getId(), active, paid, vo.getPayment().getComment());
			boolean result = db.update(command);
			
			command = FinanceSQL.insertPaymentResult(cartId, vo.getPayment().getOnlinetranasactionnumber(), u.getId(), vo.getPayment().getComment());
			result = db.update(command);
			db.clear();
			
			if(result){
				r.setMessagecode("cs200");
				r.setReference(vo.getReference());
			}	
		}catch(Exception e){
			r.setMessagecode("cs500");
			e.printStackTrace();
		}
		return r;
	}
	
	
	public static ResponseVO getCart(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		
		String s ="";
		//TODO convert to TYPEVO to validate
		boolean runonce = false;
		TypeVO t = new TypeVO();
		int cartId = Operator.toInt(vo.getReference());
		Sage db = new Sage();
		String command = FinanceSQL.getCart(cartId, u.getId());
		db.query(command);
		if(db.next()){
			s = db.getString("CART");
			if(Operator.hasValue(s)){
				t = ObjMapper.toTypeObj(StringEscapeUtils.unescapeJava(s));
				runonce = true;
				t.setCartId(db.getInt("ID"));
				t.setInputamount(db.getDouble("PAYMENT_AMOUNT"));
				t.setCreateddate(db.getString("CREATED_DATE"));
				t.setUpdateddate(db.getString("UPDATED_DATE"));
				r.setType(t);
				r.setMessagecode("cs200");
				
			}
		}
		
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		ArrayList<String> cart = new ArrayList<String>();
		if(runonce){
				for(int i=0;i<t.getStatements().length;i++){
					StatementVO sv = t.getStatements()[i];
					sb.append(sv.getActivityid()).append(",");
				}
			
			if(Operator.hasValue(sb.toString())){
				command = "select * from ACTIVITY A WHERE LKUP_ACT_STATUS_ID not in (30) AND A.ID in ("+sb.toString()+"0)";
				db.query(command);
				while(db.next()){
					a.add(db.getInt("ID"));
					
				}
				
			}	
		}
		
		db.clear();
		try{
			for(Integer id : a){
				vo.setType("activity");
				vo.setTypeid(id);
				
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				TypeVO v = FinanceAgent.cart(vo, u);
				s = mapper.writeValueAsString(v);
				cart.add(s);
				
			}
			Logger.info("CARRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR"+cart.size());
			if(cart.size()>0){
				String newcart = mergeMultiple(cart);
				vo.setReference(t.getCartId()+"");
				r = reloadCart(vo, u,newcart);
			}else {
				r = new ResponseVO();
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		return r;
	}
	
	
	public static ResponseVO reloadCart(RequestVO vo,Token u,String cart){
		ResponseVO r = new ResponseVO();
		Sage db = new Sage();
		HashMap<String,String> extras = new HashMap<String, String>();
		extras.put("note", cart);
		vo.setExtras(extras);
		ResponseVO ru = updateCart(vo, u,"Y");
		if(ru.getMessagecode().equals("cs200")){
			String s ="";
			TypeVO t = new TypeVO();
			int cartId = Operator.toInt(vo.getReference());
			
			String command = FinanceSQL.getCart(cartId, u.getId());
			db.query(command);
			if(db.next()){
				s = db.getString("CART");
				if(Operator.hasValue(s)){
					t = ObjMapper.toTypeObj(StringEscapeUtils.unescapeJava(s));
					t.setCartId(db.getInt("ID"));
					t.setInputamount(db.getDouble("PAYMENT_AMOUNT"));
					t.setCreateddate(db.getString("CREATED_DATE"));
					t.setUpdateddate(db.getString("UPDATED_DATE"));
					r.setType(t);
					r.setMessagecode("cs200");
					
				}
			}
		
		}
		db.clear();
		
		return r;
	}
	
	public static ResponseVO deleteCart(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		
		String s ="";
		//TODO convert to TYPEVO to validate
		int cartId = Operator.toInt(vo.getReference());
		if(cartId>0){
			Sage db = new Sage();
			String command = FinanceSQL.deleteCart(cartId);
			boolean result = db.update(command);
			db.clear();
			
			if(result){
				r.setMessagecode("cs200");
			}
		}else {
			r.setErrors(ValidateItype.getErrors(2, "reference is required", r.getReference()));
		}
		
		return r;
	}
	
	public static ResponseVO deletefee(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		String s ="";
		int sdId = Operator.toInt(vo.getReference());
		if(sdId>0 && u.getId()>0){
			Sage db = new Sage();
			String command = FinanceSQL.deletefee(sdId,u.getId());
			boolean result = db.update(command);
			
			int statementId =0;
			
			command = "select STATEMENT_ID from statement_detail where ID ="+sdId;
			db.query(command);
			if(db.next()){
				statementId = db.getInt("STATEMENT_ID");
			}
			
			if(statementId>0){
				command = "select sum(FEE_AMOUNT) as FEE_AMOUNT, sum(FEE_PAID) as PAID_AMOUNT, sum(BALANCE_DUE) as BALANCE_DUE from statement_detail where ACTIVE='Y' AND STATEMENT_ID="+statementId;
				db.query(command);
				double feeamount =0.00;
				double paidamount =0.00;
				double balancedue =0.00;
				if(db.next()){
					feeamount = db.getDouble("FEE_AMOUNT");
					paidamount = db.getDouble("PAID_AMOUNT");
					balancedue = db.getDouble("BALANCE_DUE");
				}
				
				command = "UPDATE STATEMENT set FEE_AMOUNT ="+feeamount+" , PAID_AMOUNT= "+paidamount+", BALANCE_DUE= "+balancedue+", UPDATED_DATE= CURRENT_TIMESTAMP, UPDATED_BY = "+u.getId()+" where ID ="+statementId;
				result = db.update(command);
				
				
			}

					
			
			db.clear();
			
			
			
			
			
			if(result){
				r.setMessagecode("cs200");
			}
		}else {
			r.setErrors(ValidateItype.getErrors(2, "reference is required", r.getReference()));
		}
		
		return r;
	}
	
	//TODO verify
	public static String getNameValue(String formula){
			
		try{
			String s = formula;
			String keyword = "NAMEVALUE";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
			int md =0;
			Sage db = new Sage();
			String command = "";
			while (m.find()) {
				String g =  m.group();
				String sp = g;
				sp = sp.replace("[", "");
				sp = sp.replace("]", "");
				sp = sp.replace(keyword, "");
				//sp = sp.substring(0,sp.indexOf("_"));
			
				Logger.info(sp);
				
				command = "select * from FEE_NAME_VALUE RF  where   (RF.EXPIRATION_DATE >= GETDATE() OR  RF.EXPIRATION_DATE is null)  AND LOWER(NAME) ='"+sp.toLowerCase()+"' AND ACTIVE='Y'";
				db.query(command);
				boolean sum  = false;
				if(db.next()){
					sum = Operator.s2b(db.getString("SUM_NAMES"));
					if(!sum){
						double v = db.getDouble("VALUE");
						String replace ="NAMEVALUE["+sp+"]";
						formula = Operator.replace(formula, replace, v+"");
					}
					
					
				}
				
				if(sum){
					
					command = "select sum(VALUE) as VALUE from FEE_NAME_VALUE RF  where    ACTIVE='Y' AND NAME ='"+sp+"' ";
					db.query(command);
					if(db.next()){
						double v = db.getDouble("VALUE");
						String replace ="NAMEVALUE["+sp+"]";
						formula = Operator.replace(formula, replace, v+"");
					}
				}
				
			}
			
			
			db.clear();
			Logger.info(formula);
		}
			catch(Exception e){
				
				e.printStackTrace();
			}
			return formula;
	}
	
	public static TypeVO  getInvoiceFinanceLock(TypeVO v, String type, int typeId){
		String res = "N";
		String command = FinanceSQL.getInvoiceFinanceLock(type, typeId);
		Sage db = new Sage();
		db.query(command);
		if(db.next()){
			res = db.getString("FINANCE_LOCK");
			v.setMessagecode(db.getString("FINANCE_LOCK"));
			v.setMessage(db.getString("USERNAME"));
		}
		db.clear();
		
		return v;
	}	
	
	public static String  getTypeFinanceLock(String type, int typeId){
		String res = "N";
		String command = FinanceSQL.getTypeFinanceLock(type, typeId);
		Sage db = new Sage();
		db.query(command);
		if(db.next()){
			res = db.getString("FINANCE_LOCK");
		}
		db.clear();
		
		return res;
	}	
	
	
	public static boolean updateInvoiceFinanceLock(String type, int typeId,int userId,boolean lock){
		boolean result = false;
		try{

			Sage db = new Sage();
			String command = FinanceSQL.updateInvoiceFinanceLock(type, typeId, userId,lock);
			result = db.update(command);
			db.clear();
			
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static ResponseVO getManualAccounts(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		
		//TODO convert to TYPEVO to validate
		try{
			TypeVO t = new TypeVO();
			
			Sage db = new Sage();
			String command = FinanceSQL.getManualAccounts(vo.getType(),vo.getTypeid());
			db.query(command);
			FeeVO[] fees = new FeeVO[db.SIZE];
			int i =0;
			while(db.next()){
				FeeVO f = new FeeVO();
				f.setName(db.getString("NAME"));
				f.setFinancemapid(db.getInt("ID"));
				f.setAccountnumber(db.getString("ACCOUNT_NUMBER"));
				f.setKeycode(db.getString("KEY_CODE"));
				f.setBudgetunit(db.getString("BUDGET_UNIT"));
				f.setFund(db.getString("FUND"));
				fees[i] = f;
				i++;		
				
			}
			db.clear();
			
			if(i>0){
				StatementVO[] statements = new StatementVO[1];	
				StatementVO v = new StatementVO();
				FeesGroupVO[] groups = new FeesGroupVO[1];
				
				FeesGroupVO g = new FeesGroupVO();
				g.setFees(fees);
				groups[0] = g;
				v.setGroups(groups);
				statements[0] = v;
				t.setStatements(statements);
			}
			
			r.setType(t);
		}catch(Exception e){
			r.setMessagecode("cs500");
			e.printStackTrace();
		}
		return r;
	}
	
	
	public static ResponseVO  extractFinanceRecords(RequestVO vo,Token u){
		ResponseVO r = new ResponseVO();
		StringBuilder sb = new StringBuilder();
		Sage db = new Sage();
		
		try{
			Timekeeper k = new Timekeeper();
			
			k.setDate(vo.getStartdate());
			int j = 0;
			if(k.MONTH()>6){
				j = k.MONTH()-6;
			}else {
				j = k.MONTH()+6;
			}
			Logger.info(vo.getStartdate()+"-------------------"+k.getString("MMDDYY")+"----------"+u.getUsername()+"---"+u.getId()+k.yy());
			String bs ="BS"+k.mm()+k.DAY()+k.yy();
			String dts =k.mm()+k.DAY()+k.yy();
			String command = FinanceSQL.extractFinanceRecords(Operator.toInt(vo.getReference()), k);
			db.query(command);
			while(db.next()){
					//sb.append(//--BS030718|OBC Financial Interface|0101704|41122|||0.00|1144.01|Y|030718|Administ|BS030718|18|9|Fees from OBC ");)
				sb.append("").append(bs).append("|OBC Financial Interface|").append(db.getString("budget_unit")).append("|");
				
				
				sb.append(db.getString("ACCOUNT_NUMBER")).append("|||").append(Math.abs(db.getDouble("DEBIT"))).append("|").append(Math.abs(db.getDouble("CREDIT"))).append("|");
				sb.append("Y|").append(dts).append("|").append(u.getUsername()).append("|").append(bs).append("|").append(k.yy()).append("|").append(j).append("|Fees from OBC ");
				sb.append("\n");
			}
			
			r.setProcessmessage(sb.toString());
			
			
			
		}catch(Exception e){
			r.setMessagecode("cs500");
			e.printStackTrace();
		}
		db.clear();
		return r;
	}
	
	
	
	public static ResponseVO saveLockBoxPayment(MapSet m,ArrayList<Integer> n, RequestVO r,Token u){
		ResponseVO o = new ResponseVO();
		try{
			
			PaymentVO p = new PaymentVO();
			p.setAmount(m.getDouble("PAYMENT_AMOUNT"));
			p.setRevamount(p.getAmount());
			p.setTransactiontype(1);
			p.setMethod(30);
			p.setNumber(m.getString("CHECK_NO"));
			p.setPayeeid(m.getInt("PAYEE_ID"));
			p.setCountername(m.getString("TRANSACTION_NUMBER"));
			int paymentId =0;
			double amount = p.getAmount();
			double tamount = p.getAmount();
			StatementVO s[] = new StatementVO[0];
			if(n.size()>0){
				s = new StatementVO[n.size()];
				int hh =0;
				for(int x=0;x<n.size();x++){
					int activityId = n.get(x);
					Logger.info("activityId"+activityId);
					r.setId("a"+activityId);
					TypeVO t = cart(r, u);	
					for(int i=0;i<t.getStatements().length;i++){
						StatementVO svo = t.getStatements()[i];
						s[hh]= svo;
						hh++;
					}
					
				}	
			}
			
			Sage db = new Sage();
			if(s.length>0){
				
				
				JSONArray pdarr = new JSONArray();
				JSONArray adarr = new JSONArray();
				
				if(amount>0){
				
					for(int i=0;i<s.length;i++){
						if(tamount>0){
						JSONObject ad = new JSONObject();
						ad.put("activityid", s[i].getActivityid());
						adarr.put(ad);
							for(int j=0;j<s[i].getGroups().length;j++){
								if(tamount>0){
									FeesGroupVO g = s[i].getGroups()[j];
									for(int k=0;k<g.getFees().length;k++){
										if(tamount>0){
											FeeVO f = g.getFees()[k];
											double feeamount = f.getInputamount();
											if(feeamount>tamount){
												JSONObject pd = new JSONObject();
												pd.put("statementdetailid", f.getStatementdetailid());
												pd.put("amount",tamount);
												pd.put("feeid", f.getFeeid());
												
												pd.put("financemapid", f.getFinancemapid());
												pd.put("keycode", f.getKeycode());
												pd.put("accountnumber", f.getAccountnumber());
												pd.put("budgetunit", f.getBudgetunit());
												pd.put("fund", f.getFund());
												
												pdarr.put(pd);
												tamount = 0;
											}else {
												tamount = subDouble(tamount,feeamount);
												JSONObject pd = new JSONObject();
												pd.put("statementdetailid", f.getStatementdetailid());
												pd.put("amount",feeamount);
												pd.put("feeid", f.getFeeid());
												
												
												pd.put("financemapid", f.getFinancemapid());
												pd.put("keycode", f.getKeycode());
												pd.put("accountnumber", f.getAccountnumber());
												pd.put("budgetunit", f.getBudgetunit());
												pd.put("fund", f.getFund());
												
												pdarr.put(pd);
											}
											
										}
									}	
								
								}
							}
						}
					}
				
				}
				
				boolean result = false;
				
				if(pdarr.length()>0){
					
					
					String command = FinanceSQL.insertPayment(p, u.getId(), r.getIp());
					result = db.update(command);
					
					if(result){
						db.query(FinanceSQL.getPaymentId(r.getIp(),  u.getId()));
						
						if(db.next()){
							paymentId = db.getInt("ID");
						}
						
						if(paymentId>0){
							for(int i=0;i<pdarr.length();i++){
								JSONObject f = pdarr.getJSONObject(i);
								f.put("paymentid", paymentId);
								if(f.getDouble("amount")>0){
									command = FinanceSQL.insertPaymentDetail(f, r.getIp(),  u.getId());
									Logger.info(command);
									result = db.update(command);
								}
							}
						}
					}
					
					//insert ref
					if(result && paymentId>0){
						/*for(int i=0;i<adarr.length();i++){
							JSONObject a = adarr.getJSONObject(i);
							
							command = FinanceSQL.insertRefPayment("activity",a.getInt("activityid"),paymentId, u.getId());
							if(Operator.hasValue(command)){
								result = db.update(command);
							}		
						}*/
						updatestatements(paymentId, u.getId());
						 command = "UPDATE LOCKBOX_UPLOADS SET ACTIVE='N' WHERE TRANSACTION_NUMBER = '"+Operator.sqlEscape(p.getCountername())+"'";
						 result = db.update(command);
					}
					
					
					
					
					
				}
				
			}
			
			
			
			db.clear();
		
			boolean h = handleOverPayment(paymentId, p, r.getIp(), u.getId());
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
			Logger.error("Problem while processing payment "+e.getMessage());
		}
		return o;
	}

	public static boolean autoIssue(String actids, int userid) {
		boolean r = false;
		String command = ActivitySQL.getAutoIssuedActivities(actids);
		if (Operator.hasValue(command)) {
			Sage db = new Sage();
			try {
				HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
				db.query(command);
				while (db.next()) {
					int actid = db.getInt("ID");
					int acttype = db.getInt("LKUP_ACT_TYPE_ID");
					m.put(actid, acttype);
				}
				for (Map.Entry<Integer, Integer> entry : m.entrySet()) {
					int actid = entry.getKey();
					int acttype = entry.getValue();
					command = ActivitySQL.getDefaultIssued(acttype);
					if (db.query(command) && db.next()) {
						int st = db.getInt("ID");
						command = ActivitySQL.updateStatus(actid, st, userid);
						db.update(command);
					}
				}
			}
			catch (Exception e) { }
			db.clear();
		}
		return r;
	}

 

	public static boolean handleOverPayment(int paymentId,PaymentVO p,String ip,int userId){
		boolean result = false;
		Sage db = new Sage();
		String command ="";
		try{
		//handle overpayment
		if(paymentId>0 && p.getPayeeid()>0){
			command = "select sum(AMOUNT) as AMOUNT from payment_detail where payment_id= "+paymentId;
			double amt = 0.00;
			db.query(command);
			if(db.next()){
				amt = db.getDouble("AMOUNT");
			}
			Logger.info("PAYMENT AMOUNT "+p.getAmount());
			Logger.info("PAYMENT DETAIL  AMOUNT "+amt);
			if(p.getAmount()>amt){
				double diffamt = Numeral.subtractDouble(p.getAmount(), amt);
				Logger.info("DIFFERENCE AMOUNT "+diffamt);
			
				if(diffamt>0){
					/*command = "UPDATE PAYMENT SET PAYMENT_AMOUNT="+amt+" where ID = "+paymentId;
					result = db.update(command);
					
					p.setAmount(diffamt);
					command = FinanceSQL.insertPayment(p, userId, ip);
					result = db.update(command);
					
					
					db.query(FinanceSQL.getPaymentId(ip, userId));
					
					if(db.next()){
						paymentId = db.getInt("ID");
					}*/
					
					
					StringBuilder sb = new StringBuilder();
					sb.append(" insert INTO payment_detail(payment_id,AMOUNT,CREATED_BY,UPDATED_BY,FINANCE_MAP_ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND) ");
					sb.append(" (SELECT TOP 1  ").append(paymentId).append(" AS payment_id,").append(Math.abs(diffamt)).append(", ").append(userId).append(",").append(userId).append(",FM.ID,KEY_CODE,BUDGET_UNIT,ACCOUNT_NUMBER,FUND   from REF_FEE_FORMULA RF JOIN FEE F on RF.FEE_ID= F.ID JOIN FINANCE_MAP FM  on RF.FINANCE_MAP_ID = FM.ID WHERE F.DEPOSIT_FEE = 'Y' AND RF.EXPIRATION_DATE is null) ");
					result = db.update(sb.toString());
					
					
					if(result){
						DepositCreditVO d = new DepositCreditVO();
						d.setParentid(0);
						d.setPaymentid(paymentId);
						d.setComment(p.getComment());
						d.setAmount(diffamt);
						d.setCurrentamount(diffamt);
						d.setType(1);
						command = DepositSQL.insertDeposit(d, userId, ip);
						result = db.update(command);
						
						
						db.query(DepositSQL.getDepositId(ip,  userId,paymentId));
						
						int depositId =0;
						if(db.next()){
							depositId = db.getInt("ID");
						}
						
						if(depositId>0){
							command = DepositSQL.insertRefDeposit("users",p.getPayeeid(),depositId, userId);
							result = db.update(command);
						}
					}
				}
				
			}
			
		}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return result;
	}

	//TODO parentid Feevo Sage connections 
	public static boolean updateFinanceStatement(RequestVO vo,Token u, String actId, int statementdetailid,FeeVO f){
		return updateFinance(vo, u,actId, statementdetailid,f);
	}

	public static boolean updateFinance(RequestVO vo,Token u,String actIds, int statementdetailid,FeeVO fst){
		boolean r = false;
		Sage db = new Sage();
		try{
			//if(statmentdetailId)
			StringBuilder sb = new StringBuilder();
			String[] as = CsTools.paramToString(actIds);
			//String as[] = Operator.split(actIds,"|");
			boolean additional = false;
			for(String activityId : as){
				sb = new StringBuilder();
				int actId = Operator.toInt(activityId);
				sb.append(" select SD.*,LF.DEFINITION,A.VALUATION_CALCULATED,F.NAME as NAME from  REF_ACT_STATEMENT RAS  ");
				sb.append(" JOIN ACTIVITY A on RAS.ACTIVITY_ID= A.ID ");
				sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
				sb.append(" JOIN REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID = RFF.ID ");
				sb.append(" JOIN LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID= LF.ID ");
				sb.append(" JOIN FEE F on RFF.FEE_ID= F.ID ");
				
				sb.append(" WHERE RAS.ACTIVITY_ID = ").append(actId);
				if(statementdetailid>0){
					sb.append(" AND SD.ID = ").append(statementdetailid);
				}else {
					sb.append(" AND LF.DEFINITION like '%valuation%' ");	
					sb.append(" AND SD.FEE_VALUATION <> A.VALUATION_CALCULATED ");
					sb.append(" AND SD.GROUP_ID > 4 ");
				}
				
				String command = sb.toString();
				String random = Operator.randomString(10);
			
				db.query(command);
				boolean doreverse = false;
				JSONArray sds = new JSONArray();
				FeeVO fa[] = new FeeVO[db.size()];
				int i = 0;
				double total =0.00;
				while(db.next()){
					//sdids.add(db.getString("ID"));
					JSONObject sd = new JSONObject();
					sd.put("ID", db.getInt("ID"));
					sd.put("FEE_AMOUNT", db.getDouble("FEE_AMOUNT"));
					sd.put("PAID_AMOUNT", db.getDouble("PAID_AMOUNT"));
					sd.put("BALANCE_DUE", db.getDouble("BALANCE_DUE"));
					sd.put("FEE_ID", db.getInt("FEE_ID"));
					if(db.getDouble("PAID_AMOUNT")>0){
						doreverse = true;
					}
					
					FeeVO f = new FeeVO();
					f.setGroupid(db.getInt("GROUP_ID"));
					String feedate = db.getString("FEE_DATE");
					Logger.info("############feedatesv#######initial###########"+feedate);
					feedate = Operator.replace(feedate, " 00:00:00", "");
					feedate = Operator.replace(feedate, "-", "/");
					feedate = Operator.replace(feedate, ".0", "");
					
					f.setCombined(actId+"");
				
					
					Logger.info("############feedatesv########################"+feedate);
					f.setFeedate(feedate.trim());
					f.setName(db.getString("NAME"));
					Logger.info("############db.getString(NAME)########################"+db.getString("NAME"));
					
					double valuation = db.getDouble("VALUATION_CALCULATED");
					
					if(statementdetailid>0){
						f.setInput1(fst.getInput1());
						f.setInput2(fst.getInput2());
						f.setInput3(fst.getInput3());
						f.setInput4(fst.getInput4());
						f.setInput5(fst.getInput5());
						
					}else {
						f.setInput1(db.getDouble("INPUT1"));
						f.setInput2(db.getDouble("INPUT2"));
						f.setInput3(db.getDouble("INPUT3"));
						f.setInput4(db.getDouble("INPUT4"));
						f.setInput5(db.getDouble("INPUT5"));
						
					}
					f.setValuation(valuation);
					
					f.setFeeid(db.getInt("FEE_ID"));
					f.setReffeeformulaid(db.getInt("REF_FEE_FORMULA_ID"));
					f.setFinancemapid(db.getInt("FINANCE_MAP_ID"));
					f.setAccount(db.getString("ACCOUNT_NUMBER"));
					
					f.setParentId(db.getInt("ID"));
					//f.set
					
					
					
					String newFormula = getFormula("activity",actId,db.getString("DEFINITION"), Operator.toString(f.getInput1()), Operator.toString(f.getInput2()), Operator.toString(f.getInput3()), Operator.toString(f.getInput4()), Operator.toString(f.getInput5()), Operator.toString(valuation),"",Operator.toString("0"));
					if(Operator.hasValue(newFormula)){
						double value = 0.00;
						
						if(newFormula.indexOf("feename")<=0){
							try{
								value = Formula.calculate(newFormula);
							}catch(Exception e){
								Logger.error(e.getMessage());
							}
						}
						f.setAmount(value);
						f.setBalancedue(value);
						f.setPaidamount(0.00);
						f.setFormula(newFormula);
						total = Operator.addDouble(total, value);
					}
					
					fa[i] = f;
					i++;
					sds.put(sd);
				}
				
				
				if(fa.length>0){
					
					
					FeeVO fa2[] = new FeeVO[fa.length];
					int j = 0;
					for(FeeVO f : fa){
						Logger.info(f.getFeeid());
						Logger.info(f.getFormula());
						Logger.info(f.getAmount()+"");
						Logger.info(f.getName()+"########GOING INDEX#####");
						
						if(f.getFormula().indexOf("feename")>0){
							if(!additional){
								additional = true;
							}
							double value = getFeeName(f, fa);
							Logger.info("value is ->>>"+value);
							f.setAmount(value);
							f.setBalancedue(value);
							
						}
						
						fa2[j] = f;
						j++;
					}
					
					
					
					
					TypeVO t = new TypeVO();
					t.setType("activity");
					t.setTypeid(actId);
					FeesGroupVO [] fg = new FeesGroupVO[1];
					FeesGroupVO	g = new FeesGroupVO();
					if(additional){
						g.setFees(fa2);
					}else {
						g.setFees(fa);
					}
					fg[0] = g;
					StatementVO sv[] = new StatementVO[1];
					StatementVO s = new StatementVO();
					s.setAmount(total);
					s.setBalancedue(total);
					s.setGroups(fg);
					sv[0] = s;
					t.setStatements(sv);
					
					r = saveFees(u.getId(), t);
					
					if(r){
						r = reverseall(vo,u, sds, actId);
						
						if(r){
							for(int k=0;k<sds.length();k++){
								JSONObject sd = sds.getJSONObject(k);
								int statementDetailId =sd.getInt("ID");
								command = "UPDATE STATEMENT_DETAIL SET ACTIVE='N' WHERE ID =  "+statementDetailId;
								db.update(command);
							}	
						}
					}
				}
			}
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		
		db.clear();
		return r;
	}
	
	
	public static boolean reverseall(RequestVO org, Token u, JSONArray sds,int actId){
		boolean r = false;
		Sage db = new Sage();
		
		try{
			
			if(sds.length()<=0){
				r = true;
			}
			JSONArray sdsn = new JSONArray();
			
			for(int i=0;i<sds.length();i++){
				JSONObject sd = sds.getJSONObject(i);
				int statementDetailId =sd.getInt("ID");
				
				String command = FinanceSQL.getPaymentStatementDetail(statementDetailId);
				db.query(command);
				
				JSONArray ads = new JSONArray();
				
				while(db.next()){
					if(db.getDouble("REV_AMOUNT")>0){
						
						JSONObject ad = new JSONObject();
						ad.put("PAYEE_ID", db.getInt("PAYEE_ID"));
						ad.put("REV_AMOUNT", db.getDouble("REV_AMOUNT"));
						ads.put(ad);
						
						PaymentVO p = new PaymentVO();
						p.setPaymentid(db.getInt("ID"));
						p.setMethodname(db.getString("METHOD"));
						
						p.setRevpaymentid(db.getInt("PAYMENT_ID"));
						p.setRevamount(db.getDouble("REV_AMOUNT"));
						p.setTransactiontypename(db.getString("TRANSACTIONTYPE"));
						p.setPayeeid(db.getInt("PAYEE_ID"));
						p.setPaymentdate(db.getString("PAYMENTDATE"));
						if(db.getInt("PAYEE_ID")>0){
							p.setOtherpayeename(db.getString("TEXT"));
						}else {
							p.setOtherpayeename(db.getString("PAYEE_DETAILS"));
						}
						p.setComment("AUTO REVERSE VALUATION");
						p.setAuto("Y");
						p.setAmount(-db.getDouble("REV_AMOUNT"));
						p.setMethod(7);
						p.setTransactiontype(5);
						
						RequestVO nav = new RequestVO();
						nav.setUsername(org.getUsername());
						nav.setToken(org.getToken());
						nav.setIp(org.getIp());
						nav.setGroupid(statementDetailId+"");
						nav.setType("activity");
						nav.setTypeid(actId);
						/*PaymentVO p = new PaymentVO();
						p.setPaymentid(map.getInt("PD_ID"));
						p.setRevpaymentid(map.getInt("P_ID"));
						p.setPayeeid(map.getInt("PAYEE_ID"));
						p.setAmount(map.getDouble("AMOUNT"));
						p.setTransactiontype(5);
						p.setMethod(map.getInt("P_METHOD"));
						p.setComment(map.getString("P_COMMENT"));*/
						nav.setPayment(p);
						boolean v = DepositAgent.reversePartialPayment(nav,u.getId());	
					
					}
				}
				
				sd.put("pays", ads);
				sdsn.put(sd);
			}
			if(sdsn.length()>0){
				payAltered(org, new ResponseVO(), actId, sdsn, u);
				r = true;
			}
		
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		return r;
	}
	
	
	public static void payAltered(RequestVO org, ResponseVO r,int actId, JSONArray sds,Token u){
		Sage db = new Sage();
		
		try{
			JSONArray sdsn = new JSONArray();
			
			
			StatementVO[] sv = new StatementVO[1];
			StatementVO  s = new StatementVO();
			s.setActivityid(actId);
			sv[0] = s;
			//String command = DepositSQL.getDepositPeople(sv);
			String command = DepositSQL.getDepositAmount(Operator.toString(actId), "0", sv);
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
			
			
			
			
			for(int i=0;i<sds.length();i++){
				JSONObject sd = sds.getJSONObject(i);
				
				int statementDetailId =sd.getInt("ID");
				int feeId =sd.getInt("FEE_ID");
				JSONArray ads = sd.getJSONArray("pays");
				
				for(int j=0;j<ads.length();j++){
					JSONObject ad = ads.getJSONObject(j);
					int payeeId = ad.getInt("PAYEE_ID");
					double amt = ad.getDouble("REV_AMOUNT");
					
					RequestVO v = new RequestVO();
					PaymentVO p = new PaymentVO();
					p.setAmount(amt);
					p.setMethod(7);
					p.setPayeeid(payeeId);
					p.setComment("AUTO DEPOSIT CHARGE");
					p.setAuto("Y");
					if(payeeId>0){
						for(DepositCreditVO dv : dvo){
							Logger.info(payeeId+"###############COMBINED##BEFORE ##"+dv.getParentid());
							Logger.info("###############COMBINED##BEFORE ##"+dv.getLevel());
							Logger.info("###############COMBINED##BEFORE ##"+dv.getType());
							if(dv.getParentid()== payeeId && dv.getLevel().equalsIgnoreCase("user") && dv.getType()==2 && dv.getId()==actId){
								String value = dv.getLevel()+"_"+dv.getParentid()+"_"+dv.getAmount()+"_"+dv.getType()+"_"+dv.getId();
								p.setCombined(value);
							}
						}
					}else {
						for(DepositCreditVO dv : dvo){
							Logger.info(payeeId+"###############COMBINED##BEFORE ##"+dv.getParentid());
							Logger.info("###############COMBINED##BEFORE ##"+dv.getLevel());
							Logger.info("###############COMBINED##BEFORE ##"+dv.getType());
							if(dv.getParentid()== actId && dv.getLevel().equalsIgnoreCase("activity") && dv.getType()==2 && dv.getId()==actId){
								String value = dv.getLevel()+"_"+dv.getParentid()+"_"+dv.getAmount()+"_"+dv.getType()+"_"+dv.getId();
								p.setCombined(value);
							}
						}
					}
					
					Logger.info("###############COMBINED##############"+p.getCombined());
					if(Operator.hasValue(p.getCombined())){
						v.setPayment(p);
						command  = "select ID from STATEMENT_DETAIL WHERE PARENT_ID="+statementDetailId;
						db.query(command);
						int sid =0;
						if(db.next()){
							sid = db.getInt("ID");
						}
						v.setStatements(build(feeId, actId, sid, p.getAmount()));
						saveDepositCreditPayment(r,v, u);
					}
				}
			}	
			
		}catch(Exception e){
			Logger.error(e.getMessage());
		}
		db.clear();
		
		
	}
	
	public static StatementVO[] build(int feeId,int actId, int statementdetailId,double amount){
		StatementVO[] sv = new StatementVO[1];
		StatementVO s = new StatementVO();
		s.setActivityid(actId);
		
		FeesGroupVO fgs[] = new FeesGroupVO[1];
		FeesGroupVO fg = new FeesGroupVO();
		fg.setGroupid(1);
		
		FeeVO[] fa = new FeeVO[1];
		FeeVO f = new FeeVO();
		f.setInputamount(amount);
		f.setStatementdetailid(statementdetailId);
		f.setFeeid(feeId);
		
		fa[0] = f;
		fg.setFees(fa);
		fgs[0] = fg;
		s.setGroups(fgs);
		sv[0] = s;
		
		
		return sv;
	}

	
	public static ResponseVO updateStatementDetailFinance(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		HashMap<String, String> extras = vo.getExtras();
		FeeVO f = new FeeVO();
		f.setInput1(Operator.toDouble(extras.get("INPUT1")));
		f.setInput2(Operator.toDouble(extras.get("INPUT2")));
		f.setInput3(Operator.toDouble(extras.get("INPUT3")));
		f.setInput4(Operator.toDouble(extras.get("INPUT4")));
		f.setInput5(Operator.toDouble(extras.get("INPUT5")));
		
		boolean j= 	updateFinanceStatement(vo, u, Operator.toString(vo.getTypeid()), Operator.toInt(vo.getId()), f);
		
		if(j){
			r.setMessagecode("cs208");
		}else {
			r.setMessagecode("cs500");
		}
		return r;
	}
	
	
	public static ResponseVO currentvaluation(int id){
		Logger.info("getFees");
	
		ResponseVO vo = new ResponseVO();
		Sage db = new Sage();
		db.query("select * from activity where ID = "+id);
		
		if(db.next()){
			vo.setReference(db.getString("VALUATION_CALCULATED"));
		}
		
		db.clear();
		
		return vo;
	}	
	
	public static ResponseVO updatecurrentvaluation(RequestVO vo, Token u){
		ResponseVO r = new ResponseVO();
		
		double val = Operator.toDouble(vo.getOption());
		Sage db = new Sage();
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE  ACTIVITY SET ");
		sb.append(" 	UPDATED_BY = ").append(u.getId());
		sb.append(" 	, ");
		sb.append(" 	UPDATED_DATE = getDate() ");
		sb.append(" 	, ");
		sb.append(" 	UPDATED_IP = '").append(Operator.sqlEscape(vo.getIp())).append("' ");
		sb.append(" , ");
		sb.append(" VALUATION_CALCULATED = ").append(val);
		
		sb.append(" WHERE ID  = ").append(vo.getTypeid());
		
		db.update(sb.toString());
		db.clear();
		
		boolean j= 	updateFinance(vo, u, Operator.toString(vo.getTypeid()), 0, null);
		
		if(j){
			r.setMessagecode("cs208");
		}else {
			r.setMessagecode("cs500");
		}
		return r;
	}
	
	
	public static ResponseVO getStatementDetail(int id){
		Logger.info("getFees");
	
		ResponseVO vo = new ResponseVO();
		Sage db2 = new Sage();
		try{
			
			TypeVO t = new TypeVO();
			
			StatementVO[] sv = new StatementVO[1];
			StatementVO s = new StatementVO();
			
			FeesGroupVO[] groups = new FeesGroupVO[1];
			FeesGroupVO g = new FeesGroupVO();
						
			String command = FinanceSQL.getStatementDetail(id);
				db2.query(command);
				FeeVO[] farr = new FeeVO[db2.size()];
				int fcount =0;
				if(db2.next()){
					FeeVO f = new FeeVO();
				/*	f.setGroup(db2.getString("GROUP_NAME"));
					f.setGroupid(db2.getInt("GROUP_ID"));*/
					f.setFeeid(db2.getInt("FEE_ID"));
					f.setName(db2.getString("NAME"));
					//f.setFormula(db2.getString("DEFINITION"));
					f.setRequired(db2.getString("REQ"));
					f.setStartdate(db2.getString("START_DATE"));
					f.setInput1(db2.getDouble("INPUT1"));
					f.setInput2(db2.getDouble("INPUT2"));
					f.setInput3(db2.getDouble("INPUT3"));
					f.setInput4(db2.getDouble("INPUT4"));
					f.setInput5(db2.getDouble("INPUT5"));
					f.setAmount(db2.getDouble("FEE_AMOUNT"));
					f.setPaidamount(db2.getDouble("FEE_PAID"));
					f.setBalancedue(db2.getDouble("BALANCE_DUE"));
					f.setFeedate(db2.getString("FEE_DATE"));
					f.setStatementdetailid(db2.getInt("ID"));
					
					f.setFinancemapid(db2.getInt("FINANCE_MAP_ID"));
					f.setAccount(db2.getString("ACCOUNT_NUMBER"));
					
					f.setFormulaId(db2.getInt("LKUP_FORMULA_ID"));
					f.setInputtype1(db2.getInt("INPUT_TYPE1"));
					f.setInputtype2(db2.getInt("INPUT_TYPE2"));
					f.setInputtype3(db2.getInt("INPUT_TYPE3"));
					f.setInputtype4(db2.getInt("INPUT_TYPE4"));
					f.setInputtype5(db2.getInt("INPUT_TYPE5"));
					
					f.setInputtypelabel1(db2.getString("INPUT_LABEL1"));
					f.setInputtypelabel2(db2.getString("INPUT_LABEL2"));
					f.setInputtypelabel3(db2.getString("INPUT_LABEL3"));
					f.setInputtypelabel4(db2.getString("INPUT_LABEL4"));
					f.setInputtypelabel5(db2.getString("INPUT_LABEL5"));
					
					f.setStartdate(db2.getString("START_DATE"));
					f.setExpdate(db2.getString("EXPIRATION_DATE"));
					f.setActive(db2.getString("ACTIVE"));
					
					f.setEdit(db2.getString("EDIT"));
					f.setInputeditable1(db2.getString("INPUT_EDITABLE1"));
					f.setInputeditable2(db2.getString("INPUT_EDITABLE2"));
					f.setInputeditable3(db2.getString("INPUT_EDITABLE3"));
					f.setInputeditable4(db2.getString("INPUT_EDITABLE4"));
					f.setInputeditable5(db2.getString("INPUT_EDITABLE5"));
					
					
					
					farr[fcount] = f;
					fcount++;
					
				}
					g.setFees(farr);
					
					groups[0] = g;
					
					s.setGroups(groups);
					sv[0] = s;
					t.setStatements(sv);
					vo.setType(t);
					
				
				
		
		/*vo.setTypeid(r.getTypeid());
		vo.setType(r.getType());*/
		
		} catch(Exception e){
			Logger.error(e.getMessage());
		}
		db2.clear();
		
		
		return vo;
	}
	
	
	/*public static boolean dofinance(String type, int oldId,int newId,String feeDate,int userId,String ip,boolean autoadd){
		if(autoadd){
			FinanceAgent.autosave("lso", "activity", newId, startdate, userId, Operator.s2b(planchkreq));
		}else{
			return copy(type, oldId, newId, feeDate, userId, ip);
		}
			
		
		
	}*/
	
	public static boolean copy(String type, int oldId,int newId,String feeDate,int userId,String ip){
		boolean result = false;
		TypeVO r = new TypeVO();
		Logger.info("Calculate Fees");
		Sage db = new Sage();
		Sage db2 = new Sage();
		RequestVO o = new RequestVO();
		try {
			boolean save = true;
			o.setType(type);
			o.setTypeid(oldId);
			o.setIp(ip);
		
			
			r.setTypeid(newId);
			r.setType(type);
			r.setEntity("finance");
			
			
			
			//st
			
			String divisionfilters = divisionCheckIds(o);
			
			String command = FinanceSQL.getFeeGroups(o);
			db.query(command);
			FeesGroupVO[] groups = new FeesGroupVO[db.size()];
			int count =0;
			while(db.next()){
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(db.getString("GROUP_NAME"));
				g.setGroupid(db.getInt("GROUP_ID"));
				if(db.getDouble("FEE_AMOUNT")>0){
					g.setAmount(db.getDouble("FEE_AMOUNT"));
					g.setPaidamount(db.getDouble("FEE_PAID"));
					g.setBalancedue(db.getDouble("BALANCE_DUE"));
				}
				
				/*if(g.getAmount()>0){
					amount = Operator.addDouble(g.getAmount(), amount);
				}
				if(g.getPaidamount()>0){
					paidamount = Operator.addDouble(g.getPaidamount(), paidamount);
				}*/
				
				//ff
				
			    command = FinanceSQL.getCopyFeeList(r.getType(), oldId, r.getEntity(),g.getGroupid(),feeDate,divisionfilters);
				
				db2.query(command);
				FeeVO[] fees = new FeeVO[db2.size()];
				int fcount =0;
				while(db2.next()){
					FeeVO f = new FeeVO();
					f.setFeeid(db2.getInt("ID"));
					f.setReffeeformulaid(db2.getInt("RFF_ID"));
					
					f.setName(db2.getString("NAME"));
					
					String define = db2.getString("DEFINITION");
					define = define.replaceAll("currentgroup",db2.getString("GROUP_NAME"));
					define = define.replaceAll("currentfeegroup",db2.getString("GROUP_NAME"));
					define = define.replaceAll("[\\t\\n\\r]+"," ");
					f.setFormula(define);
					f.setGroup(db2.getString("GROUP_NAME"));
					f.setGroupid(db2.getInt("GROUP_ID"));
					
					f.setFormulaId(db2.getInt("LKUP_FORMULA_ID"));
					f.setRequired(db2.getString("REQ"));
					f.setStartdate(db2.getString("START_DATE"));
					f.setInput1(db2.getDouble("INPUT1"));
					f.setInput2(db2.getDouble("INPUT2"));
					f.setInput3(db2.getDouble("INPUT3"));
					f.setInput4(db2.getDouble("INPUT4"));
					f.setInput5(db2.getDouble("INPUT5"));
					
					
					f.setInputtype1(db2.getInt("INPUT_TYPE1"));
					f.setInputtype2(db2.getInt("INPUT_TYPE2"));
					f.setInputtype3(db2.getInt("INPUT_TYPE3"));
					f.setInputtype4(db2.getInt("INPUT_TYPE4"));
					f.setInputtype5(db2.getInt("INPUT_TYPE5"));
					
					f.setInputtypelabel1(db2.getString("INPUT_LABEL1"));
					f.setInputtypelabel2(db2.getString("INPUT_LABEL2"));
					f.setInputtypelabel3(db2.getString("INPUT_LABEL3"));
					f.setInputtypelabel4(db2.getString("INPUT_LABEL4"));
					f.setInputtypelabel5(db2.getString("INPUT_LABEL5"));
					
					f.setStartdate(db2.getString("START_DATE"));
					f.setExpdate(db2.getString("EXPIRATION_DATE"));
					
					f.setFeedate(feeDate);
					
					ObjectMapper mapper = new ObjectMapper();
					//mapper.set
					String json = mapper.writeValueAsString(f);
					f.setDescription(json);
					
					
					fees[fcount] = f;
					fcount++;
				//eff
				
					
					
				}
				g.setFees(fees);
				
				groups[count] = g;
				count++;
			
			}
			
				
			StatementVO sv[] = new StatementVO[0]; 
			
			if(groups.length>0){
				sv = new StatementVO[1]; 
				StatementVO s = new StatementVO();
				s.setGroups(groups);
				sv[0] = s;
				o.setStatements(sv);		
			}
				
				
				
				
				
				
			
			
			
			
			
			//bef
	
			double valuation = getValuation(o.getType(),o.getTypeid());
		
			double total=0;
			boolean loop2 = false;
			boolean loop3 = false;
			boolean loop4 = false;
			boolean loop5 = false; // for subtotal
			int fgarrcounter =0;
			StatementVO[] svo = new StatementVO[1];
			StatementVO s = new StatementVO();
			s.setActivityid(o.getTypeid());
			FeesGroupVO[] fgarr = new FeesGroupVO[0];
			if(o.getStatements().length>0){
				fgarr = new FeesGroupVO[o.getStatements()[0].getGroups().length];
			
			
			for(int j=0;j<o.getStatements()[0].getGroups().length;j++){
				
				FeesGroupVO g = new FeesGroupVO();
				g.setGroup(o.getStatements()[0].getGroups()[j].getGroup());
				g.setGroupid(o.getStatements()[0].getGroups()[j].getGroupid());
				
				
				int feeslength =o.getStatements()[0].getGroups()[j].getFees().length;
				int farrcounter =0;
				FeeVO[] farr = new FeeVO[feeslength];
				Logger.info("feeslength****"+feeslength);
				
				for(int i=0;i<feeslength;i++){
					FeeVO f = o.getStatements()[0].getGroups()[j].getFees()[i];//data.getJSONObject(i);
					Logger.info(f.getGroup()+"INITIAL GROUP LOADING CHECK");
					f.setGroup(g.getGroup());
					f.setGroupid(g.getGroupid());
					int feeId = f.getFeeid();
					//TODO for online replace by query to get formulas from DB
					Logger.info(f.getFeedate()+"################");
					String formula = f.getFormula();
					f.setValuation(valuation);
					String feedate = Operator.replace(f.getFeedate(), "/", "-");
					
					
					double input1 = f.getInput1();
					double input2 = f.getInput2();
					double input3 = f.getInput3();
					double input4 = f.getInput4();
					double input5 = f.getInput5();
					double factor = f.getInput5();
					//double amount = Operator.toDouble(data.getJSONObject(i).getString("amount"));
					
					
					
					String newFormula = getFormula(o.getType(),o.getTypeid(),formula, Operator.toString(input1), Operator.toString(input2), Operator.toString(input3), Operator.toString(input4), Operator.toString(input5), Operator.toString(valuation),feedate,Operator.toString(factor));
					if(Operator.hasValue(newFormula)){
						Logger.info(feeId+":: index ::"+newFormula.indexOf("feename"));
						double value = 0;
						if(newFormula.indexOf("feename")>0){
							loop2= true;
							f.setModifiedformula(newFormula);
							
						}else if(newFormula.indexOf("sumoffeegroup")>0){
							loop3= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("totalallfees")>0){
							loop4= true;
							f.setModifiedformula(newFormula);
						}else if(newFormula.indexOf("subtotal")>0){
							loop5= true;
							f.setModifiedformula(newFormula);
						}
						else {
							if(newFormula.indexOf("noofmonthsbetween")>0){
								newFormula = FormulaParser.noofmonthsbetween(newFormula);
							}
							value = Formula.calculate(newFormula);
						}
						Logger.info(feeId+":: AMOUNT ::"+value);
						
						f.setAmount(value);
					} else {
						loop2= true;
						//f.put("value", 0);
						f.setAmount(0);
					}
					//newdata.put(f);
					farr[farrcounter] = f;
					farrcounter++;
					
					
					
				}
				Logger.info("feeslength***after*"+farr.length);
				g.setFees(farr);
				fgarr[fgarrcounter] = g;
				fgarrcounter++;
			}
			s.setGroups(fgarr);
			//svo[0] = s;
			//r.setGroups(fgarr);
			
			
			//boolean keepdoing = false;
			
			
			
			if(loop2){
				Logger.info("::feename loop2 ::"+loop2);
				s.setGroups(getFeeNameFormula(o,fgarr));
			}
			
			if(loop5){
				Logger.info("::subtotal loop5 ::"+loop5);
				s.setGroups(getInitSubtotalFormula(o,fgarr));
			}
			
			if(loop3){
				Logger.info("::sumoffeegroup loop3 ::"+loop3);
				s.setGroups(getSumOfFeeGroupFormula(o,fgarr));
			}
			
			if(loop4){
				Logger.info("::totalfees loop4 ::"+loop3);
				s.setGroups(getTotalFeesFormula(o,fgarr));
			}
			
			
			s = getSubTotalsAndTotals(s);
			svo[0] = s;
			r.setStatements(svo);
			if(save){
				result = saveFees(userId,r);
				
			}
			//r.setStatements(svo);
			}
		} catch (Exception e) {
			Logger.error("calculate ERROR  :" + e.getMessage());
		}
		
		db2.clear();
		db.clear();
		
		return result;
		
		
	}
	
	public static String divisionCheckIds(RequestVO r){
		StringBuilder sb = new StringBuilder();
		List<Integer> list2 =  divisionCheck(r);
		int i = 0;
		for(Integer feeId:list2){
			sb.append(feeId);
			sb.append(",");
			
		}
		sb.append("0");
		return sb.toString();
	}
	
	public static List<Integer> divisionCheck(RequestVO r){
		List<Integer> list2 =  new ArrayList<Integer>();
		DivisionsList l = DivisionsAgent.getDivisions("activity", r.getTypeid());
		String divids = l.divisionIds();
		Logger.info(divids);
		
		Sage db = new Sage();
		String command = "select * from ACTIVITY A JOIN REF_ACT_FEE_GROUP RAFG on A.LKUP_ACT_TYPE_ID = RAFG.LKUP_ACT_TYPE_ID WHERE RAFG.ACTIVE='Y' AND  A.ID = "+r.getTypeid();
		if(Operator.hasValue(r.getGroupid())){
			command += " AND RAFG.FEE_GROUP_ID="+r.getGroupid();
		}
		db.query(command);
		List<Integer> groups =  new ArrayList<Integer>();
		while(db.next()){
			int FEE_GROUP_ID = db.getInt("FEE_GROUP_ID");
			groups.add(FEE_GROUP_ID);
		}
		
		/*int lsoId =0;
		command = "select DISTINCT LSO_ID from ACTIVITY A JOIN REF_LSO_PROJECT RLP on A.PROJECT_ID = RLP.PROJECT_ID AND RLP.ACTIVE ='Y' WHERE A.ID = "+r.getTypeid();
		db.query(command);
		if(db.next()){
			lsoId = db.getInt("LSO_ID");
		}*/
		
		for(Integer groupId:groups){
			command = FinanceSQL.getFeeFilterList(divids,groupId);
			
			
			db.query(command);
				if(db.size()>0){
					while(db.next()){
						
						String opt = db.getString("OPT");
						int feeId = db.getInt("FEE_ID");
						int lkup_divisions_id = db.getInt("LKUP_DIVISIONS_ID");
						int FOUT_LKUP_DIVISIONS_ID = db.getInt("FOUT_LKUP_DIVISIONS_ID");
						
						if(feeId>0 && opt.equals("EQUAL")){
							if(lkup_divisions_id != FOUT_LKUP_DIVISIONS_ID){
								
								Logger.info(opt+" REMOVE "+feeId);
								list2.add(feeId);
							}
						}
						
						if(feeId>0 && opt.equals("NOTEQUAL")){
							if(lkup_divisions_id == FOUT_LKUP_DIVISIONS_ID){
								list2.add(feeId);
							}
						}
						
						
					}
				
				}
			
				
		}	
			
			
		db.clear();	
		
		return list2;
	}
	
	
	public static String mergeMultiple(ArrayList<String> t){
		String result = "";
		try{
			List<StatementVO> updatedList = new ArrayList<StatementVO>();
			HashSet<String> existing = new HashSet<String>();
			double inputamount = 0;
			
			for(String currentjson: t){
				TypeVO cartcurrent = ObjMapper.toTypeObj(currentjson);
				for (int i = 0; i < cartcurrent.getStatements().length; i++) {
					inputamount += cartcurrent.getStatements()[i].getInputamount();
					existing.add(cartcurrent.getStatements()[i].getActivitynumber());
					updatedList.add(cartcurrent.getStatements()[i]);
					Logger.info(cartcurrent.getStatements()[i].getActivitynumber() + "current ---- " + inputamount);
				}
				
			}	
		
			StatementVO[] sv = updatedList.toArray(new StatementVO[updatedList.size()]);

			TypeVO added = new TypeVO();
			added.setEntity("finance");
			added.setStatements(sv);
			added.setInputamount(inputamount);
			
			result = ObjMapper.toJson(added);
		}catch (Exception e){
			Logger.error(e.getMessage());
			result= "";
		}
		return result;
	}
	
	
	
	public static double getFeeName(FeeVO f,FeeVO[] fa){
		double r = 0;
		Sage db = new Sage();
		try{
			String formula = f.getFormula();
			for(FeeVO fav:fa){
				Logger.info("fav.getName()"+fav.getName());
				Logger.info(formula.indexOf("feename["+fav.getName()+"]"));
				if(formula.indexOf("feename["+fav.getName()+"]")>0){
					formula = Operator.replace(formula, "feename["+fav.getName()+"]", fav.getAmount()+"");
				}		
			}
			Logger.info("FOMRULA STAGE 1 ->"+formula);
			for(FeeVO fav:fa){
				if(formula.indexOf("feename")>0){
					
					String keyword = "feename";
					Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
					Matcher m = p.matcher(formula);
					
					StringBuffer result = new StringBuffer();
					ArrayList<String> a = new ArrayList<String>();
					StringBuilder all = new StringBuilder();
					while (m.find()) {
						String feename =  m.group(1);
						Logger.info("Masking: " +feename);
						
						StringBuilder sb = new StringBuilder();
						int actId = Operator.toInt(fav.getCombined());
						sb.append(" select TOP 1 SD.*,LF.DEFINITION,A.VALUATION_CALCULATED,F.NAME as FEE_NAME from  REF_ACT_STATEMENT RAS  ");
						sb.append(" JOIN ACTIVITY A on RAS.ACTIVITY_ID= A.ID ");
						sb.append(" JOIN STATEMENT_DETAIL SD on RAS.STATEMENT_ID=SD.STATEMENT_ID AND SD.ACTIVE='Y' ");
						sb.append(" JOIN REF_FEE_FORMULA RFF on SD.REF_FEE_FORMULA_ID = RFF.ID ");
						sb.append(" JOIN LKUP_FORMULA LF on RFF.LKUP_FORMULA_ID= LF.ID ");
						sb.append(" JOIN FEE F on RFF.FEE_ID= F.ID ");
						
						sb.append(" WHERE RAS.ACTIVITY_ID = ").append(actId);
						sb.append(" AND F.NAME = '").append(Operator.sqlEscape(feename)).append("'");
						db.query(sb.toString());
						if(db.next()){
							
							formula = Operator.replace(formula, "feename["+feename+"]", db.getString("FEE_AMOUNT"));
						}else {
							formula = Operator.replace(formula, "feename["+feename+"]", 0+"");
						}
					}
				}		
			}
			Logger.info("FOMRULA STAGE 2 ->"+formula);
			
			double value = 0.00;
			try{
				r = Formula.calculate(formula);
			}catch(Exception e){
				Logger.error(e.getMessage());
			}
			
		}catch (Exception e) {
			Logger.error("getFeeName ERROR  :" + e.getMessage());
			r = 0;
		}
		db.clear();
		return r;
		
	}
	
	
	
	
	

}























