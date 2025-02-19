package csapi.impl.deposit;

import csapi.common.Choices;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.SubObjVO;
import csshared.vo.finance.PaymentVO;

public class DepositFields {

	public static final String TABLE_TYPE = "crosstab";

	public static ObjGroupVO summary() {
		ObjGroupVO r = new ObjGroupVO();
		r.setGroup("deposit");
		r.setType("finance");
		r.setGroupid("0");
		r.setType("deposit");
		r.setCrosstab("GROUPNAME");
		r.setFields(new String[] {"AMOUNT", "CURRENT_AMOUNT", "PAYEE"});
		r.setIndex(new String[] {"AMOUNT", "CURRENT_AMOUNT", "PAYEE"});
		r.setEditable(false);
		ObjVO[] o = new ObjVO[4];

		ObjVO vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("GROUPNAME");
		vo.setType("String");
		vo.setItype("text");
		vo.setField("GROUPNAME");
		vo.setRequired("Y");
		o[0] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("AMOUNT");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("AMOUNT");
		o[1] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CURRENT_AMOUNT");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("CURRENT_AMOUNT");
		vo.setLabel("CURRENT AMOUNT");
		o[2] = vo;

		vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("PAYEE");
		vo.setType("String");
		vo.setItype("String");
		vo.setField("PAYEE");
		vo.setLabel("PAYEE");
		o[3] = vo;

		/*vo = new ObjVO();
		vo.setId(-1);
		vo.setOrder(0);
		vo.setFieldid("CREDITS");
		vo.setType("currency");
		vo.setItype("currency");
		vo.setField("CREDITS");
		o[2] = vo;*/
		
		
		

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
		r.setMethods(payees);
		r.setMethods(Choices.getChoices("select ID,METHOD_TYPE as VALUE, METHOD_TYPE AS TEXT, '' as SELECTED   from LKUP_PAYMENT_METHOD where ACTIVE='Y' order by METHOD_TYPE "));
		r.setTransactiontypes(Choices.getChoices("select ID,TYPE as VALUE, TYPE AS TEXT, '' as SELECTED   from LKUP_PAYMENT_TRANSACTION_TYPE where ACTIVE='Y' order by TYPE "));
		r.setCounters(Choices.getChoices("select ID,COUNTER as VALUE, COUNTER AS TEXT, '' as SELECTED   from LKUP_PAYMENT_COUNTER where ACTIVE='Y' order by COUNTER "));
		r.setPayees(payees);
		r.setOnline("N");
		
	//	r.setAmount(amount);
		
		return r;
	}

	
}
