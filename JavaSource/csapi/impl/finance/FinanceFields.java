package csapi.impl.finance;

import csapi.common.Choices;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;
import csshared.vo.finance.PaymentVO;

public class FinanceFields {

	public static final String TABLE_TYPE = "crosstab";

	
	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("finance");
		r.setType("finance");
		r.setGroupid("0");
		r.setCrosstab("GROUP_NAME");
		r.setFields(new String[] {"ACTIVITY_FEE","REVIEW_FEE","FEE_PAID","BALANCE_DUE"});
		r.setIndex(new String[] {"AMOUNT"});
		r.setEditable(false);
		ObjVO[] o = new ObjVO[5];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("GROUP_NAME");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("GROUP_NAME");
		vo.setRequired("Y");
		
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("ACTIVITY_FEE");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("ACTIVITY_FEE");
		vo.setLabel("ACTIVITY FEE");
		o[1] = vo;
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("REVIEW_FEE");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("REVIEW_FEE");
		vo.setLabel("REVIEW FEE");
		o[2] = vo;
		
		

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("FEE_PAID");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("FEE_PAID");
		vo.setLabel("FEE PAID");
		o[3] = vo;
		
		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("BALANCE_DUE");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("BALANCE_DUE");
		vo.setLabel("BALANCE DUE");
		o[4] = vo;
		

		r.setObj(o);
		return r;
	}

	public static ObjGroupVO info() {
		return summary();
	}
	
	
	public static PaymentVO paymentdetails(SubObjVO[] payees) {
		PaymentVO r = new PaymentVO();
		/*r.setGroup("finance");
		r.setType("finance");
		r.setGroupid("finance");*/
		
	//	r.setFields(new String[] {"NOTE","TYPE"});
	//	r.setIndex(new String[] {"NOTE","TYPE"});
		
		r.setPaymentid(0);
//		r.setMethods(payees);
		r.setMethods(Choices.getChoices("select ID,APPLY_DEPOSIT as VALUE, METHOD_TYPE AS TEXT, '' as SELECTED , CASH_FLAG as itype  from LKUP_PAYMENT_METHOD where ACTIVE='Y' and PAYMENT_MODE ='Y' order by METHOD_TYPE "));
		r.setTransactiontypes(Choices.getChoices("select ID,TYPE as VALUE, TYPE AS TEXT, '' as SELECTED   from LKUP_PAYMENT_TRANSACTION_TYPE where ACTIVE='Y' order by TYPE "));
		r.setCounters(Choices.getChoices("select ID,COUNTER as VALUE, COUNTER AS TEXT, '' as SELECTED   from LKUP_PAYMENT_COUNTER where ACTIVE='Y' order by COUNTER "));
		r.setPayees(payees);
		r.setOnline("N");
		
	//	r.setAmount(amount);
		
		return r;
	}

	
}
