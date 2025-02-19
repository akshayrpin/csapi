package csapi.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import csapi.impl.finance.FinanceAgent;
import csapi.impl.finance.Formula;
import csapi.impl.finance.FormulaParser;
import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class FormulaMigration {

	public static final String CPI = "    BigDecimal pct = new BigDecimal(factor); if(pct.doubleValue()>0) {  BigDecimal ONE_HUNDRED = new BigDecimal(100); BigDecimal base = new BigDecimal(o); o= base.multiply(pct).divide(ONE_HUNDRED); } ";
	
	public static boolean formulaMigrate(String env){
		boolean result = false;
		Sage db = new Sage(env);
		
		String command = "";
		boolean general = true;
		boolean Q= true;//v done
		boolean d= true;//v done
		boolean g= true;//v done
		boolean s= true;//v done
		boolean l= true;//v done
		boolean f= true;//v done
		boolean p= true; //v done
		
		boolean R= true;//v done
		boolean W= true; //9 total//v done
		boolean dot = true;
		String generalformulas = "A,B,C,D,G,H,I,V,Y,U,y,a,t,Z,J"; //15 count 
		boolean alreadydone = false;
		
		command = "select COUNT(*) as COUNT from LKUP_FORMULA WHERE ACTIVE='Y' ";
		db.query(command);
		if(db.next()){
			if(db.getInt("COUNT")>50){
				alreadydone = true;
			}
		}
		
	
		
		if(general){
			
			command = "update REF_FEE_FORMULA set INPUT_TYPE1 ='',INPUT_TYPE2 ='',INPUT_TYPE3 ='', INPUT_TYPE4  ='', INPUT_TYPE5  ='', INPUT_LABEL1 ='',INPUT_LABEL2 ='',INPUT_LABEL3 ='', INPUT_LABEL4  ='', INPUT_LABEL5  =''  where ID >0 ";
			result = db.update(command);
			
			//CASE A 
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 1,INPUT_TYPE3 ='2', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='I Integer Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('A'))";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 1,INPUT_TYPE3 ='7', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='D Decimal Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('A'))";
			result = db.update(command);
			
			
			
			//CASE B 
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 2,INPUT_TYPE3 ='2', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='I Integer Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('B'))";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 2,INPUT_TYPE3 ='7', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='D Decimal Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('B'))";
			result = db.update(command);
			
			//CASE C
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 3 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('C'))";
			result = db.update(command);
			
			//CASE D 
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 4,INPUT_TYPE3 ='2', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='I Integer Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('D'))";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 4,INPUT_TYPE3 ='7', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='D Decimal Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('D'))";
			result = db.update(command);
			
			
			
			
			//CASE G
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 5,INPUT_TYPE1 ='7', INPUT_LABEL1 ='AMOUNT' where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('G'))";
			result = db.update(command);
			
			
			//CASE H
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 6,INPUT_TYPE3 ='2', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='I Integer Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('H'))";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 6,INPUT_TYPE3 ='7', INPUT_LABEL3 ='UNIT' where FEE_INPUT_TYPE='D Decimal Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('H'))";
			result = db.update(command);
			
			
					
			//CASE I
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 7 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('I'))";
			result = db.update(command);
			
			
			//CASE J
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 8 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('J'))";
			result = db.update(command);
			
			//CASE V
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 11,INPUT_TYPE3 ='7', INPUT_LABEL3 ='AMOUNT' where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('V'))";
			result = db.update(command);
			
			
			
			//CASE y
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 14 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(LOWER('y'))";
			result = db.update(command);
			
			//CASE a
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 15,INPUT_TYPE5 ='2', INPUT_LABEL5 ='UNIT' where FEE_INPUT_TYPE='I Integer Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(LOWER('a'))";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 15,INPUT_TYPE5 ='7', INPUT_LABEL5 ='UNIT' where FEE_INPUT_TYPE='D Decimal Units' AND BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(LOWER('a'))";
			result = db.update(command);
			
			
			//CASE t
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 20 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(LOWER('t'))";
			result = db.update(command);
			
			
			//CASE Z
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 21 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('Z'))";
			result = db.update(command);
			
			//CASE Y
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 23 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('Y'))";
			result = db.update(command);
			
			//CASE U
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 22 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('U'))";
			result = db.update(command);
			
		
			//CASE R
			command = "UPDATE REF_FEE_FORMULA SET INPUT_TYPE3 ='7', INPUT_LABEL3 ='UNIT' where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('R'))";
			result = db.update(command);
			
			
		}
	
		if(dot){
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 3 where ID >0 AND ACT_TYPE IN ('DOTPPP','DOTCG','DOTOS','DOTTPP','DOTTON','DOTRON')";
			result = db.update(command);
			
			command = "update REF_FEE_FORMULA set LKUP_FORMULA_ID = 24,INPUT3 =FEE_SEQUENCE where ID >0 AND ACT_TYPE IN ('DOTON')";
			result = db.update(command);
			
		
		}	
		
		if(!alreadydone){
			if(Q){
					
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('Q'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('Q')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = caseQ("Q", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
				//	switch (db.getString("OLD_FORMULA_ID_CODE").charAt(0)) {
					//	case 'Q': // This is for fees that are based on number of units
						//	result = caseQ("Q", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"));
				
					//}	
					
				}
			
			}
			
			
			if(d){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('d'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('d')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = cased("d", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
			
			
			
			if(g){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('g'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('g')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = caseg("g", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
			
			if(s){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('s'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('s')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = cases("s", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
			
			
			if(l){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('l'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('l')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = casel("l", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
	
			if(f){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('f'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('f')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = casef("f", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
			
			
			if(p){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(Lower('p'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(Lower('p')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = casep("p", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
			
				}
			
			}
			
			
			if(R){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('R'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('R')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = caseR("R", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
			
			if(W){
				
				command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = 0 where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('W'))";
				result = db.update(command);
				
				command = "select *, CONVERT(VARCHAR(10),START_DATE,101)  as START_DATE_1, CONVERT(VARCHAR(10),EXPIRATION_DATE,101)  as EXPIRATION_DATE_1 from REF_FEE_FORMULA where LKUP_FORMULA_ID=0 and BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('W')) order by START_DATE";
				db.query(command);
				while(db.next()){
					result = caseW("W", db.getInt("ID"),db.getInt("FEE_CHART_GROUP_ID"), db.getString("START_DATE_1"), db.getString("EXPIRATION_DATE_1"),env);
					
				}
			
			}
		}
		db.clear();
		
		return result;
		
	}
	
	
	
	public static boolean caseQ(String oldformulaid, int id,int chartId, String startdate,String expdate,String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) = BINARY_CHECKSUM(UPPER('Q')) and M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE Q
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('Q')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				if(single){
					sb.append("\n   else if(valuation>").append(lowrange).append(" && valuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(valuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = valuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
					
					
					
				}
	
				
				if(!single){
					sb.append("\n  if(valuation>").append(lowrange).append(" && valuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
				
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			
			
			sb.append(CPI);
			
			if(single){
				String name = "Q  Lookup Result(Valuation) "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated Q "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("Q", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,true,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	public static boolean insertFormula(String casetype,int feechartid,String name,String definition,String desc, String startdate,String expdate,int chartId,int id,boolean uppercase, String env){
		int newformulaid =0;
		
		Sage db = new Sage(env);
		StringBuilder q = new StringBuilder();
		q.append(" INSERT INTO LKUP_FORMULA(NAME,DEFINITION,DESCRIPTION,CREATED_BY,CREATED_DATE,UPDATED_BY, UPDATED_DATE,M_FEE_CHART_GROUP_ID,M_START_DATE,M_EXPIRATION_DATE,M_FORMULA) VALUES (");
		q.append("'").append(Operator.sqlEscape(name)).append("'");
		q.append(",");
		q.append("'").append(Operator.sqlEscape(definition)).append("'");
		q.append(",");
		q.append("'").append(Operator.sqlEscape(desc)).append("'");
		q.append(",");
		q.append(890);
		q.append(",");
		q.append("CURRENT_TIMESTAMP");
		q.append(",");
		q.append(890);
		q.append(",");
		q.append("CURRENT_TIMESTAMP");
		q.append(",");
		q.append(chartId);
		q.append(",");
		q.append("'").append(startdate).append("'");
		q.append(",");
		if(expdate==null){
			q.append(expdate);
		}else {
			q.append("'").append(expdate).append("'");
		}
		q.append(",");
		q.append("'").append(Operator.sqlEscape(casetype)).append("'");
		q.append(")");
		
		boolean result = db.update(q.toString());
		if(result){
			q = new StringBuilder();
			q.append("select top 1 * from LKUP_FORMULA where  ID>0");
			q.append(" AND NAME='").append(Operator.sqlEscape(name)).append("'");
			q.append(" AND DEFINITION='").append(Operator.sqlEscape(definition)).append("'");
			q.append(" AND DESCRIPTION='").append(Operator.sqlEscape(desc)).append("'");
			q.append(" ORDER BY ID DESC");
			db.query(q.toString());
			
			if(db.next()){
				newformulaid = db.getInt("ID");
			}
		}
		
		if(newformulaid>0){
			Logger.info(casetype);
			q = new StringBuilder();
			q.append(" UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = ").append(newformulaid).append(" where   ID= ").append(id).append(" AND  ");
			if(uppercase){
				q.append(" BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('").append(Operator.sqlEscape(casetype)).append("'))");
			}else {
				q.append(" BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(LOWER('").append(Operator.sqlEscape(casetype)).append("'))");
			}
		
			result = db.update(q.toString());
		}
		db.clear();
		
		return result;
		
	}
	
	
	public static boolean caseW(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) = BINARY_CHECKSUM(UPPER('W')) and M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE W
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('W')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			
			sb.append("\n  double totalamount =0;  if(input3>0){ totalamount = subtotal(input3); } else { totalamount = subtotal(input4); }  ");
			
			String gname="";
			boolean single = false;
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				if(single){
					sb.append("\n   else if(totalamount>").append(lowrange).append(" && totalamount <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(").append(isover).append(">0){");
					sb.append("\n  		o += ((totalamount - ").append(lowrange).append(") / ").append(isover).append(" * ").append(plus).append(") ;");
					sb.append("\n  	}");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(totalamount>").append(lowrange).append(" && totalamount <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(").append(isover).append(">0){");
					sb.append("\n  		o += ((totalamount - ").append(lowrange).append(") / ").append(isover).append(" * ").append(plus).append(") ;");
					sb.append("\n  	}");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "W Lookup Result(if(init 0,subtotal[init],subtotal[subtotal])*factor "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated W "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("W", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,true,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	public static boolean caseR(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) = BINARY_CHECKSUM(UPPER('R')) and M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE R
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(UPPER('R')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				if(single){
					sb.append("\n   else if(input3>").append(lowrange).append(" && input3 <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n   double inc = input3- ").append(lowrange).append("; ");
					sb.append("\n  	if(").append(isover).append(">0){");
					sb.append("\n  		o += (inc/").append(isover).append(" * ").append(plus).append(" );");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				
				if(!single){
					sb.append("\n  if(input3>").append(lowrange).append(" && input3 <").append(highrange).append("){  ");
					sb.append("\n  	o = ").append(resultam).append("; ");
					
					sb.append("\n   double inc = input3- ").append(lowrange).append("; ");
					sb.append("\n  	if(").append(isover).append(">0){");
					sb.append("\n  		o += (inc/").append(isover).append(" * ").append(plus).append(" );");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
			
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "R Lookup Result(Units)*If(Factor 0,Factor,1)  "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated R "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("R", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,true,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	
	
	
	
	public static boolean cased(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) =BINARY_CHECKSUM(Lower('d')) AND M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE d
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('d')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			
			sb.append("\n  	double specialValuation= 0; ");
			sb.append("\n  if(valuation < 1000000 && valuation > 0){  ");
			sb.append("\n  	specialValuation= (valuation * 0.019);  ");// 1.9%
			sb.append("\n  } else if(valuation< 5000000 && valuation >= 1000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.010);  ");// 1.0%
			sb.append("\n  } else if(valuation < 10000000 && valuation >= 5000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0025);  "); // 0.25%
			sb.append("\n  } else if(valuation>= 10000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.003);  "); // 0.30%
			sb.append("\n  }  ");
			
			
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				if(single){
					sb.append("\n   else if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(specialValuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = specialValuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
			
				
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
		
			sb.append(CPI);
			if(single){
				String name = "d Lookup Result(Valuation*spl)_Demolition "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated d "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("d", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	public static boolean caseg(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) =BINARY_CHECKSUM(Lower('g')) AND M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE g
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('g')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			
			
			sb.append("\n  	double specialValuation= 0; ");
			sb.append("\n  if(valuation < 1000000 && valuation > 0){  ");
			sb.append("\n  	specialValuation= (valuation * 0.025);  ");// 2.5%
			sb.append("\n  } else if(valuation< 5000000 && valuation >= 1000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.020);  ");// 2.0%
			sb.append("\n  } else if(valuation < 10000000 && valuation >= 5000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.040);  "); // 2.0%
			sb.append("\n  } else if(valuation>= 10000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.040);  "); // 4.0 %
			sb.append("\n  }  ");
			
			
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				
				
				if(single){
					sb.append("\n   else if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(specialValuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = specialValuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "g  Lookup Result(Valuation*spl)_Grading  "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated g "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("g", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	
	public static boolean cases(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) =BINARY_CHECKSUM(Lower('s')) AND M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE s
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('s')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			
			
			
			sb.append("\n  	double specialValuation= 0; ");
			sb.append("\n  if(valuation < 1000000 && valuation > 0){  ");
			sb.append("\n  	specialValuation= (valuation * 0.045);  ");// 4.5%
			sb.append("\n  } else if(valuation< 5000000 && valuation >= 1000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.040);  ");// 4.0%
			sb.append("\n  } else if(valuation < 10000000 && valuation >= 5000000){ ");
			sb.append("\n  	specialValuation= (valuation *  0.021);  "); // 2.1%
			sb.append("\n  } else if(valuation>= 10000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.045);  "); // 4.5 %
			sb.append("\n  }  ");
			
			
			
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				
				
				if(single){
					sb.append("\n   else if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(specialValuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = specialValuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "s  Lookup Result(Valuation*spl)_Shoring  "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated s "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("s", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	public static boolean casel(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) =BINARY_CHECKSUM(Lower('l')) AND M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE l
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('l')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			
			
			sb.append("\n  	double specialValuation= 0; ");
			sb.append("\n  if(valuation < 1000000 && valuation > 0){  ");
			sb.append("\n  	specialValuation= (valuation * 0.020);  ");// 2.0 %
			sb.append("\n  } else if(valuation< 5000000 && valuation >= 1000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0035);  ");// 0.35 %
			sb.append("\n  } else if(valuation < 10000000 && valuation >= 5000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0033);  "); //0.33%
			sb.append("\n  } else if(valuation>= 10000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.005);  "); // 0.50 %
			sb.append("\n  }  ");
			
			
			
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
					
				
				if(single){
					sb.append("\n   else if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(specialValuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = specialValuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}	
				
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "l  Lookup Result(Valuation*spl)_Fire Alarm  "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated l "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("l", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	public static boolean casef(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) =BINARY_CHECKSUM(Lower('f')) AND M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE f
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) =BINARY_CHECKSUM(Lower('f')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			
			
			sb.append("\n  	double specialValuation= 0; ");
			sb.append("\n  if(valuation < 1000000 && valuation > 0){  ");
			sb.append("\n  	specialValuation= (valuation * 0.020);  ");// 2.0%
			sb.append("\n  } else if(valuation< 5000000 && valuation >= 1000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0075);  ");// 0.75%
			sb.append("\n  } else if(valuation < 10000000 && valuation >= 5000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0075);  "); // 0.75%
			sb.append("\n  } else if(valuation>= 10000000){ ");
			sb.append("\n  	specialValuation= (valuation * 0.0275);  "); // 2.75%
			sb.append("\n  }  ");
			
			
		
			
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				
				
				if(single){
					sb.append("\n   else if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(specialValuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = specialValuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(specialValuation>").append(lowrange).append(" && specialValuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
				
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
			sb.append(CPI);
			
			if(single){
				String name = "f  Lookup Result(Valuation*spl)_Fire Sprinkler "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated f "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("f", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	
	public static boolean casep(String oldformulaid, int id,int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "";
		boolean query = false;
		int lkupformulaid =0;
		String mstartdate="";
		String mexpdatedate="";
		StringBuilder f = new StringBuilder();
		f.append(" select top 1 ID,CONVERT(VARCHAR(10),FEE_CREATION_DATE,101)  as FEE_CREATION_DATE, CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101)  as FEE_EXPIRATION_DATE,FEE_CHART_GROUP_ID from FEE_CHART RFF WHERE RFF.FEE_CHART_GROUP_ID= ").append(chartId);
		f.append("  and ((RFF.FEE_CREATION_DATE <= '").append(startdate).append("' and RFF.FEE_EXPIRATION_DATE >= '").append(expdate).append("'  )  OR (RFF.FEE_CREATION_DATE <= '").append(startdate).append("' AND RFF.FEE_EXPIRATION_DATE is null ) )" );
		
		db.query(f.toString());
		if(db.next()){
			mstartdate = db.getString("FEE_CREATION_DATE");
			mexpdatedate = db.getString("FEE_EXPIRATION_DATE");
			query = true;
			if(!Operator.hasValue(mexpdatedate)){
				mexpdatedate = null;
			}
			
			command = "select * from LKUP_FORMULA where BINARY_CHECKSUM(M_FORMULA) = BINARY_CHECKSUM(Lower('p')) and M_FEE_CHART_GROUP_ID="+chartId+" " ;
			
			
			db.query(command);
			
			if(db.next()){
				lkupformulaid = db.getInt("ID");
			}
			
			
		}
		
		
		if(lkupformulaid>0){
			//CASE p
			command = "UPDATE REF_FEE_FORMULA SET LKUP_FORMULA_ID = "+lkupformulaid+" where BINARY_CHECKSUM(OLD_FORMULA_ID_CODE) = BINARY_CHECKSUM(Lower('p')) AND  FEE_CHART_GROUP_ID="+chartId+" AND ID="+id+" ";
			result = db.update(command);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!result && query){		
		
			if(mexpdatedate==null){
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C  join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where  CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}else {
				command = "select C.*,G.GROUP_NAME AS NAME  from  FEE_CHART C join FEE_CHART_GROUP G on C.FEE_CHART_GROUP_ID = G.ID where CONVERT(VARCHAR(10),FEE_CREATION_DATE,101) ='"+mstartdate+"' and CONVERT(VARCHAR(10),FEE_EXPIRATION_DATE,101) ='"+mexpdatedate+"'  and  FEE_CHART_GROUP_ID = "+chartId+" order by LOW_RANGE";
			}
			
			db.query(command);
			
			String gname="";
			boolean single = false;
			while(db.next()){
				
				double lowrange = db.getDouble("LOW_RANGE");
				double highrange = db.getDouble("HIGH_RANGE");
				double resultam = db.getDouble("RESULT");
				double plus = db.getDouble("PLUS");
				double isover = db.getDouble("ISOVER");
				
				
				
				if(single){
					sb.append("\n   else if(valuation>").append(lowrange).append(" && valuation <").append(highrange).append("){ ");
					sb.append("\n  	o= ").append(resultam).append(";");
					sb.append("\n  	if(valuation >").append(lowrange).append(" ){ ");		 		 		
					sb.append("\n  		double dividend =0; ");
					sb.append("\n  		dividend = valuation - ").append(lowrange).append(" ; ");
					sb.append("\n  		double divisor =0; ");
					sb.append("\n  		divisor = ").append(isover).append("; ");
					sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
					sb.append("\n  		o +=  cvalue *").append(plus).append("; ");
					sb.append("\n  	} ");
					sb.append("\n  	if(input1>0){ 		");
					sb.append("\n  		o *= input1; ");
					sb.append("\n  	}");
					sb.append("\n }");
				}
				
				if(!single){
					sb.append("\n  if(valuation>").append(lowrange).append(" && valuation <").append(highrange).append("){  ");
					sb.append("\n  	o= ").append(resultam).append("; ");
					sb.append("\n  	if(input1>0){");
					sb.append("\n  		o *= input1;");
					sb.append("\n  	}");
					sb.append("\n  }");
					single = true;
					gname= db.getString("NAME");
				}		
	
			}
			
			sb.append("\n  else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");
		
			sb.append(CPI);
			if(single){
				String name = "p (Lookup Result(Valuation)*FACTOR) "+mstartdate+" - "+mexpdatedate+" "+gname+" ";
				String desc = "Migrated p "+mstartdate+" - "+mexpdatedate+"";
				
				result = insertFormula("p", chartId, name, sb.toString(), desc, mstartdate, mexpdatedate,chartId, id,false,env);
				
				
			}
		}
		Logger.info(sb.toString());
		db.clear();
		return result;
		
		
			 	 		 		
	}
	
	
	
	
	
	
	public static boolean casep(String oldformulaid, int chartId, String startdate,String expdate, String env){
		boolean result = false;
		
		Sage db = new Sage(env);
		String command = "select F.NAME from FEE_CHART_GROUP F join FEE_CHART C on F.ID=C.FEE_CHART_GROUP_ID where C.ID in (3109,3141,3142,3143,3144,3145) and  F.ID = "+chartId;
		
		db.query(command);
		StringBuilder sb = new StringBuilder();
		
		
		
		
		boolean single = false;
		while(db.next()){
			
			double lowrange = db.getDouble("LOW_RANGE");
			double highrange = db.getDouble("HIGH_RANGE");
			double resultam = db.getDouble("RESULT");
			double plus = db.getDouble("PLUS");
			double isover = db.getDouble("ISOVER");
			
			if(!single){
				sb.append("\n  if(").append(highrange).append(">=valuation){  ");
				sb.append("\n  	o= ").append(resultam).append("; ");
				
				sb.append("\n  	if(").append(isover).append(">0){");
				sb.append("\n  		double dividend =0; ");
				sb.append("\n  		dividend = valuation - ").append(lowrange).append(" ; ");
				sb.append("\n  		double divisor =0; ");
				sb.append("\n  		divisor = ").append(isover).append("*").append(plus).append(" ; ");
				sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
				sb.append("\n  		o +=  cvalue; ");
				sb.append("\n  	}");
				
				sb.append("\n  	if(input1>0){ 		");
				sb.append("\n  		o *= input1; ");
				sb.append("\n  	}");
				
				
				sb.append("\n  }");
				single = true;
			}		
			
			if(single){
				sb.append("\n  else if(").append(highrange).append(">=valuation){  ");
				sb.append("\n  	o= ").append(resultam).append("; ");
				
				sb.append("\n  	if(").append(isover).append(">0){");
				sb.append("\n  		double dividend =0; ");
				sb.append("\n  		dividend = valuation - ").append(lowrange).append(" ; ");
				sb.append("\n  		double divisor =0; ");
				sb.append("\n  		divisor = ").append(isover).append("*").append(plus).append(" ; ");
				sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
				sb.append("\n  		o +=  cvalue; ");
				sb.append("\n  	}");
				
				sb.append("\n  	if(input1>0){ 		");
				sb.append("\n  		o *= input1; ");
				sb.append("\n  	}");
				
				
				sb.append("\n  }");
			}

		}
		
		sb.append("\n  else { ");
		sb.append("\n  		o=0; ");
		sb.append("\n  	}");
		db.clear();
		
		if(single){
			String name = "p  (Lookup Result(Valuation)*FACTOR)  "+startdate+"-"+expdate+" ";
			String desc = " migrated "+startdate+"-"+expdate+" ";
			
			//result = insertFormula("p", chartId, name, sb.toString(), desc, startdate, expdate);
			
			
		}
		
		return result;
	}
	
	
	public static void main(String args[]){
		double totalAmount =0;
		double totalAmount2 =0;
		int levelId =2;
		double[] subTotals = {10,20,60};
		totalAmount = subTotals[1];
		totalAmount2 = subTotals[2];
		
		//String f = " if(-5>0){ o = initlevel(input3); } else { o = subtotallevel(input4); } o *=4; ";
		String f = " int passedmonths = noofmonthsbetween[feedate,profeeyear-09-30]   ;   double feefactorpermonth = input3; feefactorpermonth= feefactorpermonth* passedmonths ;  double feefactor = input2 - feefactorpermonth;   o = feefactor; if(o>0 && o<28){ o = 28; } ";

		f = Operator.replace(f, "input1", "48.5");
		f = Operator.replace(f, "input2", "110.0");
		f = Operator.replace(f, "input3", "7");
		f = Operator.replace(f, "feedate", "2017-02-08");
		Timekeeper k = new Timekeeper();
		f = Operator.replace(f, "currentdate", k.getString("YYYY-MM-DD"));
	
		
		String g = FormulaParser.noofmonthsbetween(f);
		System.out.println(g);
		System.out.println("res::"+Formula.calculate(g));
		
		
		
		
	}
	//sample to write formula
			/*sb.append("\n  if(valuation>0 && valuation <6000){  ");
			sb.append("\n  	o= 245; ");
			sb.append("\n  	if(input1>0){");
			sb.append("\n  		o *= input1;");
			sb.append("\n  	}");
			sb.append("\n  } else if(valuation>6000 && valuation <20000){ ");
			sb.append("\n  	o= 226;");
			sb.append("\n  	if(valuation >6000){ ");		 		 		
			sb.append("\n  		double dividend =0; ");
			sb.append("\n  		dividend = valuation - 6000; ");
			sb.append("\n  		double divisor =0; ");
			sb.append("\n  		divisor = 1000*29.36; ");
			sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
			sb.append("\n  		o +=  cvalue; ");
			sb.append("\n  	} ");
			sb.append("\n  	if(input1>0){ 		");
			sb.append("\n  		o *= input1; ");
			sb.append("\n  	}");
			sb.append("\n  }else if(valuation>20000 && valuation <50000){ ");
			sb.append("\n  	o= 638; ");
			sb.append("\n  	if(valuation >20000){ 	");
			sb.append("\n  	double dividend =0; ");
			sb.append("\n  	dividend = valuation - 20000; ");
			sb.append("\n  	double divisor =0; ");
			sb.append("\n 		divisor = 1000*17.12;");
			
			sb.append("\n  		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); ");
			sb.append("\n  		o +=  cvalue; ");
			sb.append("\n  	} ");
			sb.append("\n  	if(input1>0){ 		");
			sb.append("\n  		o *= input1; ");
			sb.append("\n  	}");
			sb.append("\n  }else { ");
			sb.append("\n  		o=0; ");
			sb.append("\n  	}");*/
	
	
	
	//DOTON
	/*while (rs.next()) {
        FeeEdit fees = new FeeEdit();
        fees.setActType(rs.getString("ACT_TYPE"));
        fees.setFeeId(rs.getString("FEE_ID"));
        if (fees.getActType().equals("DOTON")) {
            double feeFactorInit = rs.getDouble("FEE_FACTOR");


			double feeFactorPerMonth = (rs.getDouble("FEE_SEQUENCE"));
			BigDecimal feeFactorFloor = new BigDecimal(feeFactorPerMonth);
			feeFactorPerMonth = feeFactorFloor.setScale(2,BigDecimal.ROUND_FLOOR).doubleValue() * passedMonths;

			double feeFactor = feeFactorInit - feeFactorPerMonth; 



			logger.debug("Blah Blah Black Sheep have you any  wool :"+feeFactor);
            if ((feeFactor != 0) && (feeFactor < 28.00)) {
                feeFactor = 28.00;
            }

            fees.setFeeFactor(StringUtils.d2s(feeFactor));
            thirdOvernightPermitFee = rs.getDouble("FEE_FACTOR") + rs.getDouble("FACTOR");
        }
        else {
            fees.setFeeFactor(StringUtils.d2s(rs.getDouble("FEE_FACTOR")));
        }

//		//Flooring fee factor
//		double feeFactorFloor = (rs.getDouble("FEE_FACTOR")/12)*10;
//		logger.debug("feeFactorFloor is "+feeFactorFloor);
//		BigDecimal feeFactorBD = new BigDecimal(feeFactorFloor);
//		feeFactor = feeFactorBD.setScale(2,BigDecimal.ROUND_CEILING).doubleValue();
//		logger.debug("feeFactor is "+feeFactor);
//                               
//		if ((feeFactor != 0) && (feeFactor < 17.00)) {
//			feeFactor = 17.00;
//		}

       
        feeList[i++] = fees;
     
        logger.debug("Added Fee : " + rs.getString("FEE_ID") + ":" + fees.getFeeFactor());
    	
    }

    // Add the prorated fee=141.50 for 3 rd Overnight to the list;
    FeeEdit fees = new FeeEdit();
    double feeFactor = (thirdOvernightPermitFee * nbrMonths) / 12;
    if ((feeFactor != 0) && (feeFactor < 28.00)) {
        feeFactor = 28.00;
    }

    fees.setFeeFactor(StringUtils.d2s(feeFactor));
    logger.debug("Added Fee : " + i + " - " + ":" + fees.getFeeFactor());
    feeList[i++] = fees;

    activityForm.setFeeList(feeList);
    activityForm.setNbrMonths("" + nbrMonths);

    List activityList = activityAgent.getActivityEditList(levelId);
    logger.debug("Activity list size " + activityList.size());
    request.setAttribute("activityList", activityList);
    activityForm.setActivityList(activityList);

    List vehicleList = vehicleAgent.getVehicleEdits(StringUtils.i2s(levelId));
    logger.debug("Vehicle list size " + vehicleList.size());
    activityForm.setVehicleList(vehicleList);

    List peopleList = peopleAgent.getPeopleEdits(StringUtils.i2s(levelId));
    logger.debug("People list size " + peopleList.size());
    activityForm.setPeopleList(peopleList);

    List peopleTypes = LookupAgent.getDotPeopleTypes();
    request.setAttribute("peopleTypes", peopleTypes);

    List statusTypes = LookupAgent.getDotStatusTypes();
    request.setAttribute("statusTypes", statusTypes);

    if (mapping.getAttribute() != null) {
        if ("request".equals(mapping.getScope())) {
            request.removeAttribute(mapping.getAttribute());
        }
        else {
            session.removeAttribute(mapping.getAttribute());
        }
    }*/
}
