package csapi.impl.finance;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alain.core.utils.Logger;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;

public class FormulaParser {

	
	public static String noofmonthsbetween(String formula){
		String s = formula;
		try{
			String keyword = "noofmonthsbetween";
			Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
			Matcher m = p.matcher(s);
		
			int md =0;
			while (m.find()) {
				String noofmonths =  m.group();
				String original = noofmonths;
			
				String sp = noofmonths;
				sp = sp.replace("[", "");
				sp = sp.replace("]", "");
				sp = sp.replace(keyword, "");
				
				
				String spa[] = Operator.split(sp,",");
				
				Timekeeper from = new Timekeeper();	
				Timekeeper to = new Timekeeper();
				boolean ct = false;
				try{
					
					from = new Timekeeper(spa[0]);	
					to = new Timekeeper(spa[1]);
					if(spa[1].startsWith("profeeyear")){
						ct=true;
					}
				}catch(Exception e){ 
					
					//ct = true;
					
				}
				
				if(ct){
					String pro = spa[1];
					if(pro.startsWith("profeeyear")){
						String ts[] = Operator.split(pro,"-");
					
						Timekeeper c = new Timekeeper();
						c.setMonth(Operator.toInt(ts[1]));
						c.setDay(ts[2]);
						
						
						if(from.MONTH()>0 && from.MONTH()<=c.MONTH()){
							pro = Operator.replace(pro, "profeeyear", from.yyyy());
							c.setYear(from.YEAR());
						}else {
							c.setYear(from.YEAR()+1);
							pro = Operator.replace(pro, "profeeyear", to.yyyy());
							//to = c;//new Timekeeper(pro);
						}
						
						//Logger.info(from.MONTH()+"--"+ts[1]+"--"+ts[2]+"--"+c.MONTH()+"--"+c.yyyy());
						Logger.info(ct+"--"+from.getString("MM/DD/YYYY")+"--"+c.getString("MM/DD/YYYY"));
						Logger.info(from.DAY()+"----"+c.DAY());
						md = 12 - c.MONTH()+from.MONTH();
						if(from.DAY()<c.DAY()){
							md = md -1;
						}
						if(md>=12){
							md = md -12;
						}
						
					}
				}else {
					Logger.info(ct+"--"+from.getString("MM/DD/YYYY")+"--"+to.getString("MM/DD/YYYY"));
					md = 12 - to.MONTH()+from.MONTH();
				}
				
				
				s = Operator.replace(s, original, Operator.toString(md));
			}//end while
		
		} catch(Exception e){
			Logger.error("PROBLEM IN noofmonthsbetween ");
		}
		return s;
	}
	
	
	public static void main2(String args[]){
		  final Calendar today = new GregorianCalendar();
          int month = today.get(Calendar.MONTH) + 1;
          int year = today.get(Calendar.YEAR);
          int date = today.get(Calendar.DATE);
		int nbrMonths = 10 - month;
        if (nbrMonths <= 0) {
            nbrMonths += 12;
        }

        if (nbrMonths == 1) {
            if (date > 19) {
                nbrMonths = 0;
            }
        }
        int passedMonths = 9;
        System.out.println("set  passedMonths"+passedMonths);
        double thirdOvernightPermitFee=0.00;
		double feeFactorPerMonth = (7);
		BigDecimal feeFactorFloor = new BigDecimal(feeFactorPerMonth);
		feeFactorPerMonth = feeFactorFloor.setScale(2,BigDecimal.ROUND_FLOOR).doubleValue() * passedMonths;
		 System.out.println(feeFactorFloor.setScale(2,BigDecimal.ROUND_FLOOR).doubleValue()+"set fee feeFactorPerMonth"+feeFactorPerMonth);
		double FEE_FACTOR = 110.0000;
		double FACTOR = 48.50000;
		  double feeFactorInit = FEE_FACTOR;
		double feeFactor = feeFactorInit - feeFactorPerMonth; 



		System.out.println("Blah Blah Black Sheep have you any  wool :"+feeFactor);
        if ((feeFactor != 0) && (feeFactor < 28.00)) {
            feeFactor = 28.00;
        }

       System.out.println("set fee factor"+feeFactor);
        thirdOvernightPermitFee = FEE_FACTOR + FACTOR;
        System.out.println("set fee factor thirdOvernightPermitFee"+thirdOvernightPermitFee);
        
        feeFactor = (thirdOvernightPermitFee * nbrMonths) / 12;
        if ((feeFactor != 0) && (feeFactor < 28.00)) {
            feeFactor = 28.00;
        }
        System.out.println("set fee factor 22"+feeFactor);
        
        double input1 = 110.00;
        double input2 = 7;
        double o = 28.00;
      
        
        Timekeeper k = new Timekeeper();
        String start = "2018/10/01";
		k.setDate(start);
		
        int kmonth = k.MONTH();
        if(kmonth==10){
        	o = input1;
        }
        else if(kmonth==11){
        	o = input1 -input2;
        }
        else if(kmonth==12){
        	o = input1 -(input2*2);
        }
        else if(kmonth<10){
        	o = input1 - ((kmonth+2)*input2);
        }
        else {
        	o = 28.00;
        }
        
      
        System.out.println("o::"+o);
        

        
	}
	
	public static void main(String args[]){
		String s = "o = (         feename[Electrical Permit] +         feename[MEP Trade Plan Review] +         feename[Electrical Investigation Fee] +         feename[Plan Review Electrical per Hour] +         feename[Application Processing Fee] +         feename[Permit Processing Fee] +         feename[Expedited Plan Review] +         feename[Heavy Hauling] +         feename[Building Plan Review Hours] +         feename[Technology Fee - Review] +         feename[Technology Fee - Permit] +         feename[Trousdale Construction Vehicle Traffic Fee] + 	)* input1; 	 //input1 = the multiplying rate or unit. Enter 1 to get the sum of all fees.";
		String keyword = "feename";
		Pattern p = Pattern.compile(keyword+"\\[(.*?)\\]");
		Matcher m = p.matcher(s);
		
		StringBuffer result = new StringBuffer();
		while (m.find()) {
			String feename =  m.group(1);
			Logger.info("Masking: " +feename);
		}
	}
	
}
