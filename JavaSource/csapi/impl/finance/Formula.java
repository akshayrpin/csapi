package csapi.impl.finance;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import csapi.utils.CsApiConfig;
import csapi.utils.Email;
import alain.core.utils.Logger;
import alain.core.utils.Numeral;
import alain.core.utils.Operator;
import bsh.EvalError;
import bsh.Interpreter;

public class Formula {

	public static void main1(String[] args) {
		// TODO Auto-generated method stub
		
		String s = " if (a>val) {o= val;} else { o=val;} ";
		System.out.println(StringEscapeUtils.escapeJava(s));
		Interpreter i = new Interpreter();
	
		try {
			i.set("a", 6); 	
			i.set("val", 24);
			i.set("c", 45);
			i.set("o", 0);
			i.eval(s);
			System.out.println(i.get("o"));
			
			//CASE A Units * Fee/Unit  <option value="A">Units * Fee/Unit</option>

			Logger.info("fee_calc_1=A,This is for fees that are based on number of units");
			i = new Interpreter();
			i.set("inputtype1", 100);
			i.set("inputtype2", 2);
			i.set("inputtype3", 0);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);
			
			s = " if(inputtype1>0) { o = inputtype1*inputtype2; } else { o =89; }";
			Logger.info("fee_calc_1=C,this is for a flat fee");
			i.eval(s);
			System.out.println("A::"+i.get("o"));
			
			
			//CASE B Units * Fee/Unit + Factor    <option value="B">Units * Fee/Unit + Factor</option>

			Logger.info("fee_calc_1=B,This is for fees that are based on number of units");
			//feeAmount = rs.getDouble("factor") + (rs.getDouble("fee_factor") * activityFee.getFeeUnits());
			i = new Interpreter();
			i.set("inputtype1", 10);
			i.set("inputtype2", 2);
			i.set("inputtype3", 20);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);
			
			s = " if(inputtype1>0) { o = Math.max(inputtype1,inputtype2) + inputtype3; } else { o =89; }";
			Logger.info("fee_calc_1=C,this is for a flat fee");
			i.eval(s);
			System.out.println("B::"+i.get("o"));

			
			
			//CASE C Factor (Flat Fee)   <option value="C" selected="selected">Factor (Flat Fee)</option>

			i = new Interpreter();
			i.set("inputtype1", 100);
			i.set("inputtype2", 0);
			i.set("inputtype3", 0);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);
			
			s = " if(inputtype1>0) { o = inputtype1; } else { o =89; }";
			Logger.info("fee_calc_1=C,this is for a flat fee");
			i.eval(s);
			System.out.println("C::"+i.get("o"));
			
			
		//case 'D': // Max of factor and fee_factor*units   <option value="D">Max(Units * Fee/Unit, Factor)</option>

			//feeAmount = Math.max(rs.getDouble("factor"), rs.getDouble("fee_factor") * activityFee.getFeeUnits());

			i = new Interpreter();
			i.set("inputtype1", 100);
			i.set("inputtype2", 10);
			i.set("inputtype3", 8);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);
			
			
			
			s = " if(inputtype1>0) { if(inputtype2 * inputtype3 > inputtype1){ o = inputtype2 * inputtype3; }  else { o =inputtype1; }}  else { o =89; }";
			Logger.info("fee_calc_1=D,this is for a flat fee");
			i.eval(s);
			System.out.println("D::"+i.get("o"));
			
		
		//case 'E': // Min of factor and fee_factor*units <option value="E">Max(Units * Fee/Unit, Factor)</option>

			//feeAmount = Math.min(rs.getDouble("factor"), rs.getDouble("fee_factor") * activityFee.getFeeUnits());
			i = new Interpreter();
			i.set("inputtype1", 100);
			i.set("inputtype2", 10);
			i.set("inputtype3", 8);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);
			
			//s = " if(inputtype1>0) { if(inputtype2 * inputtype3 < inputtype1){ o = inputtype2 * inputtype3; }  else { o =inputtype1; }}  else { o =89; }";
			s = " if(100>0) { if(10 * 8 < 100){ o = 10 * 8; }  else { o =100; }}  else { o =89; }";
			Logger.info("fee_calc_1=D,this is for a flat fee");
			i.eval(s);
			System.out.println("E::"+i.get("o"));
			

		//case 'F': // This is for fees that are based on number of units
			//feeAmount = rs.getDouble("factor") * activityFee.getFeeUnits();
			i = new Interpreter();
			/*i.set("inputtype1", 100);
			i.set("inputtype2", 10);
			i.set("inputtype3", 0);
			i.set("inputtype4", 0);
			i.set("inputtype5", 0);*/
			
			//s = "if(100>0) { o = 100*10; } else { o =89; }";
			s = " o = valuation ";
			Logger.info("fee_calc_1=D,this is for a flat fee");
			i.eval(s);
			System.out.println("F::"+i.get("o"));
			
			
			
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	/*public void calculateFeeAmount(Wrapper db, ActivityFee activityFee) throws Exception {
		logger.info("calculateFeeAmount(db, " + activityFee + ")");

		double feeAmount = 0.00;
		double[] subTotals = {};
		int levelId = 0;

		String sql = "";

		try {
			if (activityFee.getType() == 'B') { // Business Tax
				logger.debug("fee type is business tax");
				feeAmount = (activityFee.getFeeValuation() * 0.00125) * 1.7;
			} else {
				RowSet rs = new CachedRowSet();
				boolean calculateFee = true;

				sql = "select (fee_amnt-fee_paid) as balance from activity_fee where activity_id = " + activityFee.getActivityId() + " and fee_id = " + activityFee.getFeeId();
				logger.debug(sql);
				rs = new Wrapper().select(sql);
				if (rs.next()) {
					if (rs.getDouble("BALANCE") != 0.00d) {
						logger.debug("balance is greater than 0.00d ,calculate fee = true");
						calculateFee = true;
					} else {
						logger.debug("balance is 0.00d,calculate fee = false");
						calculateFee = false;
					}
				} else {
					logger.debug("Resultset does not have any values, calculate fee = true");
					calculateFee = true;
				}

				sql = "select * from fee f,activity a where act_id = " + activityFee.getActivityId() + " and fee_id = " + activityFee.getFeeId();
				logger.debug(sql);
				rs = new CachedRowSet();
				rs = new Wrapper().select(sql);
				if (rs.next()) {
					logger.info("Fee Calculate : " + activityFee.getFeeId() + ":" + rs.getString("fee_calc_1") + ": Fee Amount : " + activityFee.getFeeAmount() + ": Paid :" + activityFee.getFeePaid());

					if ((rs.getInt("STATUS") == 2) || (rs.getInt("STATUS") == 3) || (rs.getInt("STATUS") == 4) || (rs.getInt("STATUS") == 30) || (rs.getInt("STATUS") == 31)) {
						// If the fee has already been paid and Activity Status=Issued then do not recalculate it.
						logger.debug("fee has already been paid and Activity Status=Issued then do not recalculate it");
						feeAmount = Double.parseDouble(new DecimalFormat("0.00").format(activityFee.getFeeAmount()));
						logger.debug("FeeAmount is : " + feeAmount);
					} else {
						logger.debug("Recalculating fees...");
						double feeAmount1 = 0.0;
						double val = 0.0;
						RowSet frs1 = null;
						logger.debug("Valuation taken " + val);
						double specialValuation = 0.0;
						switch (rs.getString("fee_calc_1").charAt(0)) {
						case 'A': // This is for fees that are based on number of units
							logger.debug("fee_calc_1=A,This is for fees that are based on number of units");
							if (rs.getDouble("fee_factor") > 0.00) {
								logger.debug("Fee factor is > 0.00, calculating fee amount");
								feeAmount = rs.getDouble("fee_factor") * Math.max(1, activityFee.getFeeUnits());
								logger.debug("calculated fee amount is " + feeAmount);
							}

							break;

						case 'B': // This is for fees that are based on number of units
							logger.debug("fee_calc_1=B,This is for fees that are based on number of units");
							feeAmount = rs.getDouble("factor") + (rs.getDouble("fee_factor") * activityFee.getFeeUnits());

							break;

						case 'C': // this is for a flat fee
							logger.debug("fee_calc_1=C,this is for a flat fee");
							feeAmount = rs.getDouble("factor");

							break;

						case 'D': // Max of factor and fee_factor*units
							feeAmount = Math.max(rs.getDouble("factor"), rs.getDouble("fee_factor") * activityFee.getFeeUnits());

							break;

						case 'E': // Min of factor and fee_factor*units
							feeAmount = Math.min(rs.getDouble("factor"), rs.getDouble("fee_factor") * activityFee.getFeeUnits());

							break;

						case 'F': // This is for fees that are based on number of units
							feeAmount = rs.getDouble("factor") * activityFee.getFeeUnits();

							break;

						case 'G': // User Entering the Fee Amount
							feeAmount = activityFee.getFeeAmount();

							break;

						case 'H': // valuation * fee/unit
							logger.debug("Option H, valuation * fee/unit");
							feeAmount = rs.getDouble("valuation") * activityFee.getFeeUnits();

							break;

						case 'I': // valuation * factor
							feeAmount = rs.getDouble("valuation") * rs.getDouble("factor");
							logger.debug("The fee amount calculated is " + feeAmount);

							break;

						case 'J':

							*//**
							 * If feeInit > 0 then Mutilply the subtotal[feeInit] value by fee/unit Else Mutilply the subtotal[subTotalLevel] value by fee/unit
							 **//*
							logger.debug("Fee Amount before calculations: " + activityFee.getFeeAmount());
							logger.debug("fee_calc_1= J: If feeInit > 0 then Mutilply the subtotal[feeInit] value by fee/unit Else Mutilply the subtotal[subTotalLevel] value by fee/unit");
							logger.debug("feeInit is : " + activityFee.getFeeInit());
							logger.debug("subTotalLevel is : " + activityFee.getSubtotalLevel());

							subTotals = activityFee.getSubTotals();
							String subTotalsString = "";
							for (int i = 0; i < subTotals.length; i++) {
								subTotalsString += ("" + subTotals[i] + ",");
							}

							logger.debug("subTotals array is :" + subTotalsString);
							if (activityFee.getFeeInit() > 0) {
								logger.debug("Activity fee is GREATER than fee init");
								feeAmount = subTotals[activityFee.getFeeInit()];
								logger.debug("calculated fee amount is " + feeAmount);
							} else {
								logger.debug("Activity fee is LESSER than fee init");
								feeAmount = subTotals[activityFee.getSubtotalLevel()];
								logger.debug("calculated fee amount is " + feeAmount);
							}

							logger.debug("multiplying the fee amount by the factor");
							feeAmount *= activityFee.getFactor().doubleValue();
							logger.debug("The resulting fee amount is " + feeAmount);

							break;

						case 'Q': // this is for fees based on the fee lookup table
							logger.debug("fee_calc_1=Q,this is for fees based on the fee lookup table");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							RowSet frs = new Wrapper().select(sql);
							feeAmount = 0.0;
							find: while (frs.next()) {
								if (frs.getDouble("HIGH_RANGE") >= rs.getDouble("valuation")) {
									feeAmount = frs.getDouble("Result");638
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs.getDouble("Low_Range"), frs.getDouble("Over")) * frs.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs.close();

							break;

						case 'R':
						case 'S':
						case 'T':
							sql = "select *  from lkup_fee lf join lkup_fee_ref lfr on lf.lkup_fee=lfr.lkup_fee where lfr.fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.debug(sql);
							CachedRowSet urs = new CachedRowSet();
							urs.populate(new Wrapper().select(sql));
							feeAmount = 0.0;
							double increment = 0.0;
							double over = 0;
							double plus = 0;

							find: while (urs.next()) {
								if ((urs.getDouble("low_range") <= activityFee.getFeeUnits()) && (urs.getDouble("high_range") >= activityFee.getFeeUnits())) {
									feeAmount = urs.getDouble("Result");
									increment = activityFee.getFeeUnits() - urs.getDouble("low_range");
									plus = urs.getDouble("Plus");
									over = urs.getDouble("Over");

									break find;
								}
							}

							if (over > 0.0) {
								switch (rs.getString("fee_calc_1").charAt(0)) {
								case 'R':
									feeAmount += (increment / over * plus);

									break;

								case 'S': // Ceiling
									feeAmount += (StringUtils.divideCeiling(increment, over) * plus);

									break;

								case 'T': // Base
									feeAmount += (StringUtils.divideBase(increment, over) * plus);

									break;
								}
							}

							if (rs.getDouble("factor") > 0.0) {
								feeAmount *= rs.getDouble("factor");
							}

							break;

						case 'U':
							if (activityFee.getFeeInit() > 0) {
								levelId = activityFee.getFeeInit();
							} else {
								levelId = activityFee.getSubtotalLevel();
							}

							subTotals = activityFee.getSubTotals();
							logger.debug("LevelId : " + levelId);
							logger.debug("SubTotal : " + subTotals[levelId]);
							logger.debug("Factor : " + activityFee.getFactor().doubleValue());
							feeAmount = Math.max(subTotals[levelId], activityFee.getFactor().doubleValue());

							break;

						case 'V':
							logger.debug("case is V");
							if (activityFee.getFeeAmount() > activityFee.getFactor().doubleValue()) {
								feeAmount = activityFee.getFactor().doubleValue();
								logger.debug("amount is greater than factor, feeAmount is " + feeAmount);
							} else {
								feeAmount = activityFee.getFeeAmount();
								logger.debug("amount is NOT greater than factor, feeAmount is " + feeAmount);
							}

							break;

						case 'W': // this is for fees based on the fee lookup table

							// float permitFeeAmount = 0.0;
							double totalAmount = 0.0;
							subTotals = activityFee.getSubTotals();

							if (activityFee.getFeeInit() > 0) {
								levelId = activityFee.getFeeInit();
							} else {
								levelId = activityFee.getSubtotalLevel();
							}

							totalAmount = subTotals[levelId];

							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " and high_range >= " + totalAmount + " order by low_range";
							logger.info(sql);
							RowSet wrs = new Wrapper().select(sql);

							feeAmount = 0.0;
							if (wrs.next()) {
								feeAmount = wrs.getDouble("Result");
								if (wrs.getDouble("Over") > 0.0) {
									feeAmount += ((totalAmount - wrs.getDouble("Low_Range")) / wrs.getDouble("Over") * wrs.getDouble("Plus"));
								}

								if (rs.getDouble("factor") > 0.0) {
									feeAmount *= rs.getDouble("factor");
								}
							}

							wrs.close();

							break;

						case 'Y':

							*//**
							 * Mutilply the subtotal value by Subtotallevel and compare the result to Factor Return the highest
							 *//*

							// add code here to select the subtotal from the arrey object based on feeInit of activityFee obj
							logger.debug("before setting up amount for calc Y : " + feeAmount);
							logger.debug("subtotallevel is : " + activityFee.getSubtotalLevel());
							subTotals = activityFee.getSubTotals();
							feeAmount = subTotals[activityFee.getSubtotalLevel()];
							logger.debug("after setting up amount for calc Y : " + feeAmount);
							logger.debug("fee factor " + activityFee.getFactor().doubleValue());

							feeAmount *= activityFee.getFeeFactor().doubleValue();
							if (feeAmount < activityFee.getFactor().doubleValue()) {
								feeAmount = activityFee.getFactor().doubleValue();
							}

							break;

						case 'y':

							*//**
							 * Mutilply the subtotal value by fee/unit and compare the result to Factor Return the highest
							 *//*

							// add code here to select the subtotal from the arrey object based on feeInit of activityFee obj
							logger.debug("before setting up amount for calc y : " + feeAmount);
							logger.debug("feeInit is : " + activityFee.getFeeInit());
							subTotals = activityFee.getSubTotals();
							feeAmount = subTotals[activityFee.getFeeInit()];
							logger.debug("after setting up amount for calc y : " + feeAmount);
							logger.debug("fee factor " + activityFee.getFactor().doubleValue());

							feeAmount *= activityFee.getFeeFactor().doubleValue();
							if (feeAmount < activityFee.getFactor().doubleValue()) {
								feeAmount = activityFee.getFactor().doubleValue();
							}

							break;

						case 'a':

							*//**
							 * Mutilply the subtotal value by units and compare the result to Factor Return the highest
							 *//*

							// add code here to select the subtotal from the arrey object based on feeInit of activityFee obj
							logger.debug("before setting up amount for calc y : " + feeAmount);
							logger.debug("feeInit is : " + activityFee.getFeeInit());
							subTotals = activityFee.getSubTotals();
							feeAmount = subTotals[activityFee.getFeeInit()];
							logger.debug("subtotal amount is : " + feeAmount);
							logger.debug("after setting up amount for calc y : " + feeAmount);
							feeAmount *= activityFee.getFeeUnits();
							if (feeAmount < activityFee.getFeeFactor().doubleValue()) {
								feeAmount = activityFee.getFeeFactor().doubleValue();
							}

							break;

						// adding special fees for particular activities added June 2008 for Demolition --SUNIL

						case 'd': // this is for fees based on the fee lookup table
							logger.debug("Lookfee_calc_1=d,this is for special fees for Demolition based on the fee lookup table based ");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							val = rs.getDouble("Valuation");
							logger.debug("Valuation taken " + val);
							if (val < 1000000 && val > 0) {
								specialValuation = (val * 0.019); // 1.9%
								logger.debug("Valuation " + val + "after % 1.9% addded Special Value= " + specialValuation);
							} else if (val < 5000000 && val >= 1000000) {
								specialValuation = (val * 0.010); // 1.0%
								logger.debug("Valuation " + val + "after % 1.0% addded Special Value= " + specialValuation);
							} else if (val < 10000000 && val >= 5000000) {
								specialValuation = (val * 0.0025); // 0.25%
								logger.debug("Valuation " + val + "after % 0.25% addded Special Value= " + specialValuation);
							} else if (val >= 10000000) {
								specialValuation = (val * 0.003); // 0.30%
								logger.debug("Valuation " + val + "after % 0.30% addded Special Value= " + specialValuation);
							}
							find: while (frs1.next()) {

								if (frs1.getDouble("HIGH_RANGE") >= specialValuation) {
									feeAmount = frs1.getDouble("Result");
									logger.debug("Final Specal Valuation after % calcualtion " + specialValuation);
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(specialValuation - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is %%%%%%%%%%%%%" + feeAmount);
										feeAmount1 += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount1);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // End Demoliton fees

						// adding special fees for particular activities added June 2008 for Grading

						case 'g': // this is for fees based on the fee lookup table
							logger.debug("Lookfee_calc_1=d,this is for special fees for Demolition based on the fee lookup table based ");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							val = rs.getDouble("Valuation");
							logger.debug("Valuation taken " + val);
							if (val < 1000000 && val > 0) {
								specialValuation = (val * 0.025); // 2.5 %
								logger.debug("Valuation " + val + "after % 2.5 % addded Special Value= " + specialValuation);
							} else if (val < 5000000 && val >= 1000000) {
								specialValuation = (val * 0.020); // 2.0 %
								logger.debug("Valuation " + val + "after % 2.0 % addded Special Value= " + specialValuation);
							} else if (val < 10000000 && val >= 5000000) {
								specialValuation = (val * 0.020); // 2.0 %
								logger.debug("Valuation " + val + "after % 2.0 % addded Special Value= " + specialValuation);
							} else if (val >= 10000000) {
								specialValuation = (val * 0.040); // 4.0 %
								logger.debug("Valuation " + val + "after % 4.0 % addded Special Value= " + specialValuation);
							}
							find: while (frs1.next()) {

								if (frs1.getDouble("HIGH_RANGE") >= specialValuation) {
									feeAmount = frs1.getDouble("Result");
									logger.debug("Final Specal Valuation after % calcualtion " + specialValuation);
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(specialValuation - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is %%%%%%%%%%%%%" + feeAmount);
										feeAmount1 += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount1);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // End Grading fees

						// adding special fees for particular activities added June 2008 for Shoring

						case 's': // this is for fees based on the fee lookup table
							logger.debug("Lookfee_calc_1=d,this is for special fees for Demolition based on the fee lookup table based ");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							val = rs.getDouble("Valuation");
							logger.debug("Valuation taken " + val);
							if (val < 1000000 && val > 0) {
								specialValuation = (val * 0.045); // 4.5 %
								logger.debug("Valuation " + val + "after % 4.5 % addded Special Value= " + specialValuation);
							} else if (val < 5000000 && val >= 1000000) {
								specialValuation = (val * 0.040); // 4.0%
								logger.debug("Valuation " + val + "after % 4.0 % addded Special Value= " + specialValuation);
							} else if (val < 10000000 && val >= 5000000) {
								specialValuation = (val * 0.021); // 2.1%
								logger.debug("Valuation " + val + "after % 2.1 % addded Special Value= " + specialValuation);
							} else if (val >= 10000000) {
								specialValuation = (val * 0.045); // 4.5 %
								logger.debug("Valuation " + val + "after % 4.5 % addded Special Value= " + specialValuation);
							}
							find: while (frs1.next()) {

								if (frs1.getDouble("HIGH_RANGE") >= specialValuation) {
									feeAmount = frs1.getDouble("Result");
									logger.debug("Final Specal Valuation after % calcualtion " + specialValuation);
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(specialValuation - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is %%%%%%%%%%%%%" + feeAmount);
										feeAmount1 += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount1);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // End Shoring fees

						// adding special fees for particular activities added June 2008 for Fire Alarm

						case 'l': // this is for fees based on the fee lookup table
							logger.debug("Lookfee_calc_1=d,this is for special fees for Demolition based on the fee lookup table based ");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							val = rs.getDouble("Valuation");
							logger.debug("Valuation taken " + val);
							if (val < 1000000 && val > 0) {
								specialValuation = (val * 0.020); // 2.0 %
								logger.debug("Valuation " + val + "after % 2.0 % addded Special Value= " + specialValuation);
							} else if (val < 5000000 && val >= 1000000) {
								specialValuation = (val * 0.0035); // 0.35 %
								logger.debug("Valuation " + val + "after % 0.35 % addded Special Value= " + specialValuation);
							} else if (val < 10000000 && val >= 5000000) {
								specialValuation = (val * 0.0033); // 0.33%
								logger.debug("Valuation " + val + "after % 0.33 % addded Special Value= " + specialValuation);
							} else if (val >= 10000000) {
								specialValuation = (val * 0.005); // 0.50 %
								logger.debug("Valuation " + val + "after % 0.50 % addded Special Value= " + specialValuation);
							}
							find: while (frs1.next()) {

								if (frs1.getDouble("HIGH_RANGE") >= specialValuation) {
									feeAmount = frs1.getDouble("Result");

									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(specialValuation - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is %%%%%%%%%%%%%" + feeAmount);
										feeAmount1 += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount1);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // End Fire Alarm fees
						// adding special fees for particular activities added June 2008 for Fire Sprinkler

						case 'f': // this is for fees based on the fee lookup table
							logger.debug("Lookfee_calc_1=d,this is for special fees for Demolition based on the fee lookup table based ");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							val = rs.getDouble("Valuation");
							logger.debug("Valuation taken " + val);
							if (val < 1000000 && val > 0) {
								specialValuation = (val * 0.020); // 2.0%
								logger.debug("Valuation " + val + "after % 2.0 % addded Special Value= " + specialValuation);
							} else if (val < 5000000 && val >= 1000000) {
								specialValuation = (val * 0.0075); // 0.75%
								logger.debug("Valuation " + val + "after % 0.75 %  addded Special Value= " + specialValuation);
							} else if (val < 10000000 && val >= 5000000) {
								specialValuation = (val * 0.0075); // 0.75%
								logger.debug("Valuation " + val + "after % 0.75 %  addded Special Value= " + specialValuation);
							} else if (val >= 10000000) {
								specialValuation = (val * 0.0275); // 2.75%
								logger.debug("Valuation " + val + "after % 2.75 % addded Special Value= " + specialValuation);
							}
							find: while (frs1.next()) {

								if (frs1.getDouble("HIGH_RANGE") >= specialValuation) {
									feeAmount = frs1.getDouble("Result");
									logger.debug("Final Specal Valuation after % calcualtion " + specialValuation);
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(specialValuation - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is %%%%%%%%%%%%%" + feeAmount);
										feeAmount1 += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount1);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // End Fire Sprinkler fees
						// Start Permit Fee Formula July 2008

						case 'p': // this is for fees based on the fee lookup table
							logger.debug("fee_calc_1=Q,this is for fees based on the fee lookup table");
							sql = "select * from v_fee_lookup_value where " + " fee_id = " + activityFee.getFeeId() + " order by low_range";
							logger.info(sql);
							frs1 = new Wrapper().select(sql);
							feeAmount = 0.0;
							find: while (frs1.next()) {
								if (frs1.getDouble("HIGH_RANGE") >= rs.getDouble("valuation")) {
									feeAmount = frs1.getDouble("Result");
									logger.debug("high range is great than or equal to valuation--feeAmount got is " + feeAmount);
									if (frs1.getDouble("Over") > 0) {
										logger.debug("over is great than 0, so adding the formula");
										feeAmount += (StringUtils.divideCeiling(rs.getDouble("Valuation") - frs1.getDouble("Low_Range"), frs1.getDouble("Over")) * frs1.getDouble("Plus"));
										logger.debug("fee amount after calculating for over is " + feeAmount);
									}

									if (rs.getDouble("factor") > 0.0) {
										logger.debug("factor is greater than 0, so multipler formaula applied");
										feeAmount *= rs.getDouble("factor");
										logger.debug("the new fee amount after incorporating factor is " + feeAmount);
									}

									break find;
								}
							}

							frs1.close();

							break; // end Permit Fee

						default:
							break;
						}
					}
				}
			}

			feeAmount = Double.parseDouble(new DecimalFormat("0.00").format(feeAmount));

			activityFee.setFeeAmount(feeAmount);
			logger.info("Exiting getFeeAmount() " + feeAmount);

			return;
		} catch (Exception e) {
			logger.error("Error in FinanceAgent getFeeAmount() :" + e.getMessage());
			throw new Exception("Unable to calculate fee amount " + e.getMessage());
		}
	}*/
	
	
	public static double calculate(String formula) {
		double value=0.00;
		DecimalFormat df2 = new DecimalFormat(".##");
		try {
			Interpreter i = new Interpreter();
			
			/*JavaClassLoader c = new JavaClassLoader();
			//c.invokeClassMethod("csapi.impl.finance.Hello", "run22");
			//c.loadClass("alain.core.utils.Timekeeper");
			c.loadClass("csapi.impl.finance.Hello");
			i.setClassLoader(c);
			BshClassManager b = new BshClassManager();
			b.setClassLoader(c);
			NameSpace n = new NameSpace(b, "") ;
			i.setNameSpace(n);
			
			Logger.info(""+i.eval(formula, n));*/
			String classpath = CsApiConfig.getString("formula_path");//Formula.class.getProtectionDomain().getCodeSource().getLocation().getPath();//System.getProperty("java.class.path");
			if(!Operator.hasValue(classpath)){
				classpath = Formula.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			}
			
			classpath = Operator.replace(classpath, "%20", " ");
			classpath = classpath.replace("Formula.class", "sunil.bsh");
			Logger.info("############################################"+classpath);
			//i.source(classpath+"csapi/impl/finance/sunil.bsh");
			i.source(classpath);
			i.set("input1", 0);
			i.set("input2", 0);
			i.set("input3", 0);
			i.set("input4", 0);
			i.set("input5", 0);
			i.set("o", 0);
		
			
			
			
			
			Logger.info(formula);
			i.eval(formula);
			value = Operator.toDouble(i.get("o")+"");
			value = Operator.toDouble(df2.format(value));
			Logger.highlight(Operator.toString(value));
		}catch (Exception e) {
			e.printStackTrace();
			Logger.error(formula+"FORMULA ERROR  :" + e.getMessage());
			Email.notifyAdministrators("Formula Calculate", e.getMessage() +"</br>"+ formula);
			
		}
		return value;
	}
	
	
	
	public static int divideCeiling(double dividend, double divisor) {
        Logger.info("divideCeiling(" + dividend + ", " + divisor + ")");

        int value = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue();
        Logger.info("value returned is " + value);

        return value;
    }

    public static int divideBase(double dividend, double divisor) {
        return new Double((dividend >= 0) ? (dividend / divisor) : ((dividend - divisor + 1) / divisor)).intValue();
    }
    
    
    public static void main3(String args[]){
    	//String s = "if(6600.0>0 && 6600.0 <6000){ 	o= 245; 	if(0.1>0){ 		o *= 0.1; 	} 	 } else if(6600.0>6000 && 6600.0 <20000){ 	o= 226; 	 	o +=  Formula.divideCeiling(6600.0 - 6000, 1000 * 29.36); 	 	if(0.1>0){ 		o *= 0.1; 	}  }else if(6600.0>20000 && 6600.0 <50000){ 	o= 638; 	 	o +=  Formula.divideCeiling(6600.0 - 20000, 1000 * 17.12); 	if(0.1>0){ 		o *= 0.1; 	} 	 }else { 	o=0; }	";
    	String s =" o=0; if(21100.00>0 && 21100.00 <6000){ 	o= 245; 	if(0.1>0){ 		o *= 0.1; 	} 	 } else if(21100.00>6000 && 21100.00 <20000){ 	o= 226.96; 	System.out.println(\"o=\"+o); 	if(21100.00 >6000){ 		double dividend =0; 		dividend = 21100.00 - 6000; 		double divisor =0; 		divisor = 29360; 	System.out.println(\"dividend\"+dividend); System.out.println(\"divisor\"+divisor);		int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); 		o +=  (cvalue);  System.out.println(o+\"cc\"+cvalue);	} 	 	if(0.1>0){ 		o *= 0.1; 	}  }else if(21100.00>20000 && 21100.00 <50000){ 	o= random(); 	 	if(21100.00 >20000){ 			double dividend =0; 			dividend = 21100.00 - 20000; 			double divisor =0; 			divisor = 1000; 			int cvalue = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor)).intValue(); 			o +=  cvalue *17.12; 	} 	 	if(0.1>0){ 		o *= 0.1; 	} 	 }else { 	o=0; }	";
    	System.out.println("GOING ");
    	
    	int a = Numeral.random();
    	System.out.println(a+"s::"+calculate(s));
    	
    	System.out.println(divideCeiling(1100,17200));
    	/*double dividend = 6100;
    	double divisor = 29360;
    	double value = new Double((dividend >= 0) ? (((dividend + divisor) - 1) / divisor) : (dividend / divisor));
        Logger.info("value returned is " + value);*/
    	
    	
    }
    
    public static int random() {
       
        return 33;
    }
    
    public static void main4(String[] args) {
		String input = 
                  "User feename(234 21 su). Some more text feename(33432). This clientNum=100";

		
		//input = Operator.replace(input, "clientId(", "clientId=");
		//input = Operator.replace(input, " ", "");
		String[] parts = input.split("[\\(\\)]");
		for (String part : parts) {
		   // I print first "Your", in the second round trip "String"
		   System.out.println("part"+part);
		}
		//input = input.replaceAll(" /\(/" , "");
		System.out.println("input" +input);
		Pattern p = Pattern.compile("(feename[\\(\\)])[\\w+] ");
		Matcher m = p.matcher(input);

		StringBuffer result = new StringBuffer();
		while (m.find()) {
			System.out.println("Masking: " + m.group(2));
			m.appendReplacement(result, m.group(1) + "***masked***");
		}
		m.appendTail(result);
		System.out.println(result);
	}
    
    public static void main(String[] args) {
		
		
		try {
			
				JSONArray subs = new JSONArray();
				String formula = "if(input3>0){ o = subtotal(input3); } else {  o = subtotal(input4); } o *= input1";
				formula = formula.replace("input3", "5");
				double g = 78;
				String s = formula;
				s = Operator.replace(s, "subtotal(", "subtotal=");
				//s = Operator.replace(s, ")", "");
			//	s = Operator.replace(s, " ", "_X_B");
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
					subs.put(sub);
				}
				
					
				for(int i=0;i<subs.length();i++){
					Logger.info(subs.getJSONObject(i).getInt("levelid"));
					Logger.info(subs.getJSONObject(i).getString("original"));
				}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
