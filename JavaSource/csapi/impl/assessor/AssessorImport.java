package csapi.impl.assessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import alain.core.db.Sage;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import csapi.common.Table;
import csapi.impl.admin.AdminAgent;
import csapi.utils.CsDeleteCache;

public class AssessorImport {

	private static BufferedWriter fileWriter;
	static int j = 0;

	public static boolean initialAssessorUpdate() {
		String table = Table.ASSESSORIMPORTLOG;

		Sage db = new Sage();
		boolean r = true;
		db.connect();

		updateAssessorImportLog("start", "START", "Assessor Import started.Checking configuration...", table);

		updateAssessorImportLog("start", "CONFIGURATION", "configuration setup successful.Starting import...", table);
		updateAssessorImportLog("progress", "BACKUP", "Deleting of assessor backup data complete", table);

		updateAssessorImportLog("progress", "BACKUP", "Backup of assessor tables data complete", table);

		// updateAssessorImportLog("progress", "DELETE", "Deleting of assessor tables
		// data complete",table);

		r = AdminAgent.updateData("UPDATE ASSESSOR_IMPORT_EXCEPTION SET ACTIVE='N', updated_date =current_timestamp WHERE ACTIVE='Y'");

		updateAssessorImportLog("progress", "FILE", "Start reading data from flat file", table);

		return r;
	}

	public static int assessorDataImport(String fileName, int processCount, int recordCount) {

		// start process in new thread
		try {

			if (processCount < recordCount) {
				// new Thread(new Runnable() {
				// public void run() {

				int k = 0;

				String readLine = "";
				BufferedReader input = null;
				String table = Table.ASSESSORIMPORTLOG;
				boolean r = true;
				boolean result = false;
				Sage db = new Sage();
				db.connect();
				PreparedStatement psAssessorData = null;
				PreparedStatement psAssessorOwner = null;
				PreparedStatement psAssessorSite = null;
				PreparedStatement psAssessorTax = null;
				PreparedStatement psAssessorException = null;
				//PreparedStatement psJobSchedule = null;

				String newFileName = fileName;
				FileInputStream fin = null;
				Date date = new Date();
				Timestamp ts = new Timestamp(date.getTime());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// j=0;
				StringBuffer sbAssessorData = new StringBuffer();
				StringBuffer sbAssessorOwner = new StringBuffer();
				StringBuffer sbAssessorSite = new StringBuffer();
				StringBuffer sbAssessorTax = new StringBuffer();
				//StringBuffer sbJobSchedule = new StringBuffer();

				try {

					/*if (processCount == 0) {
						
						sbJobSchedule = sbJobSchedule.append(
								"INSERT INTO JOB_SCHEDULE (LKUP_JOB_SCHEDULE_TYPE_ID, START_DATE, END_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						psJobSchedule = db.CONNECTION.prepareStatement(sbJobSchedule.toString());
						psJobSchedule.setInt(1, 1);
						psJobSchedule.setString(2, formatter.format(ts));
						psJobSchedule.setString(3, "");
						psJobSchedule.setString(4, "Y");
						psJobSchedule.setInt(5, 890);
						psJobSchedule.setString(6, formatter.format(ts));
						psJobSchedule.setInt(7, 890);
						psJobSchedule.setString(8, formatter.format(ts));
						psJobSchedule.setString(9, "");
						psJobSchedule.setString(10, "");
						psJobSchedule.execute();

					}*/  

					sbAssessorData = sbAssessorData.append(
							"INSERT INTO ASSESSOR_DATA (APN, HAZ_ABATE_CTY_KEY, HAZ_ABATE_INFO, PARTIAL_INTEREST, DOC_REASON_CODE, DATA1_SUBPART, DATA1_DESIGN_TYPE, DATA1_QUALITY, DATA1_YR_BUILT, DATA1_NO_UNITS, DATA1_NO_BDRMS, DATA1_NO_BATHS, DATA1_SQ_FT, DATA2_SUBPART, DATA2_DESIGN_TYPE, DATA2_QUALITY, DATA2_YR_BUILT, DATA2_NO_UNITS, DATA2_NO_BDRMS, DATA2_NO_BATHS, DATA2_SQ_FT, DATA3_SUBPART, DATA3_DESIGN_TYPE, DATA3_QUALITY, DATA3_YR_BUILT, DATA3_NO_UNITS, DATA3_NO_BDRMS, DATA3_NO_BATHS, DATA3_SQ_FT, DATA4_SUBPART, DATA4_DESIGN_TYPE, DATA4_QUALITY, DATA4_YR_BUILT, DATA4_NO_UNITS, DATA4_NO_BDRMS, DATA4_NO_BATHS, DATA4_SQ_FT, DATA5_SUBPART, DATA5_DESIGN_TYPE, DATA5_QUALITY, DATA5_YR_BUILT, DATA5_NO_UNITS, DATA5_NO_BDRMS, DATA5_NO_BATHS, DATA5_SQ_FT, RECORDING_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, END_BY, END_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?,?,?, ?, ?, ?,?, ?,?, ?,?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					psAssessorData = db.CONNECTION.prepareStatement(sbAssessorData.toString());

					sbAssessorOwner = sbAssessorOwner.append(
							"INSERT INTO ASSESSOR_OWNER (APN, FRST_OWNR_NAME, FRST_OWNR_NAME_OVR, MAIL_ADDR_HOUSE_NO, MAIL_ADDR_FRACTION, MAIL_ADDR_DIR, MAIL_ADDR_ST_NAME, MAIL_ADDR_UNIT, MAIL_ADDR_CTY_STA, MAIL_ADDR_ZIP, SPEC_NAME_LEGEND, SPEC_NAME_ASSESSEE, SECND_OWNR_NAME, RECORDING_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, END_BY, END_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)");
					psAssessorOwner = db.CONNECTION.prepareStatement(sbAssessorOwner.toString());

					sbAssessorSite = sbAssessorSite.append(
							"INSERT INTO ASSESSOR_SITE (APN, SITUS_HOUSE_NO, SITUS_FRACTION, SITUS_DIRECTION, SITUS_STREET_NAME, SITUS_UNIT, SITUS_CITY_STATE, SITUS_ZIP, ZONING_CODE, USE_CODE, LEGAL_DESC1, LEGAL_DESC2, LEGAL_DESC3, LEGAL_DESC4, LEGAL_DESC5, LEGAL_DESC6, RECORDING_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, END_BY, END_DATE) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					psAssessorSite = db.CONNECTION.prepareStatement(sbAssessorSite.toString());

					sbAssessorTax = sbAssessorTax.append(
							"INSERT INTO ASSESSOR_TAX (APN, TAX_RATE_AREA, AGENCY_CLASS_NO, LAND_CURR_ROLL_YR, LAND_CURR_VAL, IMP_CURR_ROLL_YR, IMP_CURR_VAL, TAX_STATUS_KEY, TAX_STAT_YR_SOLD, OWNERSHIP_CODE, EXEM_CLAIM_TYPE, PER_PROP_KEY, PER_PROP_VAL, PERL_PROP_EXEM_VAL, FIXTURE_VALUE, FIXTURE_EXEM_VAL, HOMEOWNER_NO_EXEM, HOMEOWNER_EXEM_VAL, REAL_ESTATE_EXEMPT, LAST_SL1_VER_KEY, LAST_SL1_SL_AMNT, LAST_SL1_SL_DT, LAST_SL2_VER_KEY, LAST_SL2_SL_AMNT, LAST_SL2_SL_DT, LAST_SL3_VER_KEY, LAST_SL3_SL_AMNT, LAST_SL3_SL_DT, RECORDING_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, END_BY, END_DATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					psAssessorTax = db.CONNECTION.prepareStatement(sbAssessorTax.toString());

					
					
					String ASSESSOR_SQL = "INSERT INTO ASSESSOR_IMPORT_EXCEPTION(APN, HAZ_ABATE_CTY_KEY, HAZ_ABATE_INFO, PARTIAL_INTEREST, DOC_REASON_CODE, DATA1_SUBPART, DATA1_DESIGN_TYPE, DATA1_QUALITY, DATA1_YR_BUILT, DATA1_NO_UNITS, DATA1_NO_BDRMS, DATA1_NO_BATHS, DATA1_SQ_FT, DATA2_SUBPART, DATA2_DESIGN_TYPE, DATA2_QUALITY, DATA2_YR_BUILT, DATA2_NO_UNITS, DATA2_NO_BDRMS, DATA2_NO_BATHS, DATA2_SQ_FT, DATA3_SUBPART, DATA3_DESIGN_TYPE, DATA3_QUALITY, DATA3_YR_BUILT, DATA3_NO_UNITS, DATA3_NO_BDRMS, DATA3_NO_BATHS, DATA3_SQ_FT, DATA4_SUBPART, DATA4_DESIGN_TYPE, DATA4_QUALITY, DATA4_YR_BUILT, DATA4_NO_UNITS, DATA4_NO_BDRMS, DATA4_NO_BATHS, DATA4_SQ_FT, DATA5_SUBPART, DATA5_DESIGN_TYPE, DATA5_QUALITY, DATA5_YR_BUILT, DATA5_NO_UNITS, DATA5_NO_BDRMS, DATA5_NO_BATHS, DATA5_SQ_FT, FRST_OWNR_NAME, FRST_OWNR_NAME_OVR, MAIL_ADDR_HOUSE_NO, MAIL_ADDR_FRACTION, MAIL_ADDR_DIR, MAIL_ADDR_ST_NAME, MAIL_ADDR_UNIT, MAIL_ADDR_CTY_STA, MAIL_ADDR_ZIP, SPEC_NAME_LEGEND, SPEC_NAME_ASSESSEE, SECND_OWNR_NAME, SITUS_HOUSE_NO, SITUS_FRACTION, SITUS_DIRECTION, SITUS_STREET_NAME, SITUS_UNIT, SITUS_CITY_STATE, SITUS_ZIP, ZONING_CODE, USE_CODE, LEGAL_DESC1, LEGAL_DESC2, LEGAL_DESC3, LEGAL_DESC4, LEGAL_DESC5, LEGAL_DESC6, TAX_RATE_AREA, AGENCY_CLASS_NO, LAND_CURR_ROLL_YR, LAND_CURR_VAL, IMP_CURR_ROLL_YR, IMP_CURR_VAL, TAX_STATUS_KEY, TAX_STAT_YR_SOLD, OWNERSHIP_CODE, EXEM_CLAIM_TYPE, PER_PROP_KEY, PER_PROP_VAL, PERL_PROP_EXEM_VAL, FIXTURE_VALUE, FIXTURE_EXEM_VAL, HOMEOWNER_NO_EXEM, HOMEOWNER_EXEM_VAL, REAL_ESTATE_EXEMPT, LAST_SL1_VER_KEY, LAST_SL1_SL_AMNT, LAST_SL1_SL_DT, LAST_SL2_VER_KEY, LAST_SL2_SL_AMNT, LAST_SL2_SL_DT, LAST_SL3_VER_KEY, LAST_SL3_SL_AMNT, LAST_SL3_SL_DT, RECORDING_DATE, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, CREATED_IP, UPDATED_IP, END_BY, END_DATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					psAssessorException = db.CONNECTION.prepareStatement(ASSESSOR_SQL);

					try {
						fin = new FileInputStream(newFileName);
					} catch (Exception e) {
						System.out.println("Export error:");
						e.printStackTrace();
					}

					// k=processCount;
					int pl = 0;

					input = new BufferedReader(new InputStreamReader(fin));

					String updateAD ="UPDATE ASSESSOR_DATA SET ACTIVE='N' , updated_date =current_timestamp WHERE ACTIVE='Y' and APN=?";
					PreparedStatement psUpdateAD = db.CONNECTION.prepareStatement(updateAD);
					String updateAO = "UPDATE ASSESSOR_OWNER SET ACTIVE='N' , updated_date =current_timestamp WHERE ACTIVE='Y' and APN=?";
					PreparedStatement psUpdateAO = db.CONNECTION.prepareStatement(updateAO);
					String updateAS = "UPDATE ASSESSOR_SITE SET ACTIVE='N' , updated_date =current_timestamp WHERE ACTIVE='Y' and APN=?";
					PreparedStatement psUpdateAS = db.CONNECTION.prepareStatement(updateAS);
					String updateAT = "UPDATE ASSESSOR_TAX SET ACTIVE='N' , updated_date =current_timestamp WHERE ACTIVE='Y' and APN=?";
					PreparedStatement psUpdateAT = db.CONNECTION.prepareStatement(updateAT);

					String apn = "";
					while (((readLine = input.readLine()) != null)) {
					
						pl++;

						if(pl>processCount)
						{
							System.out.println(" ******recordCount  " + recordCount + " processCount " + processCount
									+ " value of pl is " + pl);

							// insert data to assessor table.
							apn = readLine.substring(0, 10);

							String DATE_FORMAT = "yyyyMMdd";
							SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
							Calendar c1 = Calendar.getInstance(); // today
							String todayDate = sdf.format(c1.getTime());

							String ownerName = Operator.sqlEscape(readLine.substring(211, 243));
							String strNo = Operator.sqlEscape(readLine.substring(129, 134));
							String strMod = Operator.sqlEscape(readLine.substring(134, 137));
							String preDir = Operator.sqlEscape(readLine.substring(137, 138));
							String strName = Operator.sqlEscape(readLine.substring(139, 170));

							String unit = Operator.sqlEscape(readLine.substring(170, 178));
							String city = Operator.sqlEscape(readLine.substring(178, 202));
							String cityStr = Operator.sqlEscape(readLine.substring(178, 202));
							String zip = Operator.sqlEscape(readLine.substring(202, 211));

							String state = "";

							if (city.endsWith("BEVERLY HILLS")) {
								city = "BEVERLY HILLS CA";
							}

							if (city.lastIndexOf(' ') > 0) {
								state = city.substring(city.lastIndexOf(' ')).trim();

								// fix states
								if (state.equals("CALIF")) {
									state = "CA";
									city = city.substring(0, city.length() - 3);
								} else if (state.equals("TEXAS")) {
									state = "TX";
									city = city.substring(0, city.length() - 5);
								} else if (state.equals("ARIZ")) {
									state = "AZ";
									city = city.substring(0, city.length() - 4);
								} else if (city.endsWith(",CA") || city.endsWith("CAL") || city.endsWith("C A")
										|| city.endsWith("CAA")) {
									state = "CA";
									city = city.substring(0, city.length() - 3);
								} else if (city.endsWith("NEV") || city.endsWith("NVA")) {
									state = "NV";
									city = city.substring(0, city.length() - 5);
								} else if (city.endsWith("ILL")) {
									state = "IL";
									city = city.substring(0, city.length() - 1);
								} else if (city.endsWith("N Y")) {
									state = "NY";
									city = city.substring(0, city.length() - 3).trim();
								} else if (city.endsWith("N J")) {
									state = "NJ";
									city = city.substring(0, city.length() - 3).trim();
								} else if (city.endsWith("FL")) {
									state = "FL";
									city = city.substring(0, city.length() - 3).trim() + " ";
								} else if (city.endsWith("CA") && !state.equals("CA")) {
									state = "CA";
									city = city.substring(0, city.length() - 2).trim();
								} else {
									state = "CA";
									city = " Los Angeles ";
								}
							}

							
							String siteCity = Operator.sqlEscape(readLine.substring(96, 120));
							
							String sqlCount = "SELECT * FROM REF_LSO_APN WHERE LSO_TYPE IN ('L','O') AND ACTIVE='Y' AND APN='"+apn+"'";
							db.query(sqlCount);

							int count = 0;
							boolean countFlag = false;
							if (db.next()) {
								count = 1;
							}  

							//if ((cityStr.toUpperCase()).contains("BEVERLY HILLS")) {
						   if(count >0) 
						   {
							   
							   psUpdateAD.setString(1, apn);
							   psUpdateAD.execute(); 
							   psUpdateAO.setString(1, apn);
							   psUpdateAO.execute();
							   psUpdateAS.setString(1, apn);
							   psUpdateAS.execute();
							   psUpdateAT.setString(1, apn);
							   psUpdateAT.execute();
							   
								psAssessorData.setString(1, apn);
								psAssessorData.setString(2, Operator.sqlEscape(readLine.substring(357, 358)));
								psAssessorData.setString(3, Operator.sqlEscape(readLine.substring(358, 368)));
								psAssessorData.setString(4, Operator.sqlEscape(readLine.substring(387, 390)));
								psAssessorData.setString(5, Operator.sqlEscape(readLine.substring(390, 391)));
								psAssessorData.setString(6, Operator.sqlEscape(readLine.substring(505, 509)));
								psAssessorData.setString(7, Operator.sqlEscape(readLine.substring(509, 513)));
								psAssessorData.setString(8, Operator.sqlEscape(readLine.substring(513, 518)));
								psAssessorData.setString(9, Operator.sqlEscape(readLine.substring(518, 522)));
								psAssessorData.setString(10, Operator.sqlEscape(readLine.substring(522, 525)));
								psAssessorData.setString(11, Operator.sqlEscape(readLine.substring(525, 527)));
								psAssessorData.setString(12, Operator.sqlEscape(readLine.substring(527, 529)));
								psAssessorData.setString(13, Operator.sqlEscape(readLine.substring(529, 536)));
								psAssessorData.setString(14, Operator.sqlEscape(readLine.substring(536, 540)));
								psAssessorData.setString(15, Operator.sqlEscape(readLine.substring(540, 544)));
								psAssessorData.setString(16, Operator.sqlEscape(readLine.substring(544, 549)));
								psAssessorData.setString(17, Operator.sqlEscape(readLine.substring(549, 553)));
								psAssessorData.setString(18, Operator.sqlEscape(readLine.substring(553, 556)));
								psAssessorData.setString(19, Operator.sqlEscape(readLine.substring(556, 558)));
								psAssessorData.setString(20, Operator.sqlEscape(readLine.substring(558, 560)));
								psAssessorData.setString(21, Operator.sqlEscape(readLine.substring(560, 567)));
								psAssessorData.setString(22, Operator.sqlEscape(readLine.substring(567, 571)));
								psAssessorData.setString(23, Operator.sqlEscape(readLine.substring(571, 575)));
								psAssessorData.setString(24, Operator.sqlEscape(readLine.substring(575, 580)));
								psAssessorData.setString(25, Operator.sqlEscape(readLine.substring(580, 584)));
								psAssessorData.setString(26, Operator.sqlEscape(readLine.substring(584, 587)));
								psAssessorData.setString(27, Operator.sqlEscape(readLine.substring(587, 589)));
								psAssessorData.setString(28, Operator.sqlEscape(readLine.substring(589, 591)));
								psAssessorData.setString(29, Operator.sqlEscape(readLine.substring(591, 598)));
								psAssessorData.setString(30, Operator.sqlEscape(readLine.substring(598, 602)));
								psAssessorData.setString(31, Operator.sqlEscape(readLine.substring(602, 606)));
								psAssessorData.setString(32, Operator.sqlEscape(readLine.substring(606, 611)));
								psAssessorData.setString(33, Operator.sqlEscape(readLine.substring(611, 615)));
								psAssessorData.setString(34, Operator.sqlEscape(readLine.substring(615, 618)));
								psAssessorData.setString(35, Operator.sqlEscape(readLine.substring(618, 620)));
								psAssessorData.setString(36, Operator.sqlEscape(readLine.substring(620, 622)));
								psAssessorData.setString(37, Operator.sqlEscape(readLine.substring(622, 629)));
								psAssessorData.setString(38, Operator.sqlEscape(readLine.substring(629, 633)));
								psAssessorData.setString(39, Operator.sqlEscape(readLine.substring(633, 637)));
								psAssessorData.setString(40, Operator.sqlEscape(readLine.substring(637, 642)));
								psAssessorData.setString(41, Operator.sqlEscape(readLine.substring(642, 646)));
								psAssessorData.setString(42, Operator.sqlEscape(readLine.substring(646, 649)));
								psAssessorData.setString(43, Operator.sqlEscape(readLine.substring(649, 651)));
								psAssessorData.setString(44, Operator.sqlEscape(readLine.substring(651, 653)));
								psAssessorData.setString(45, Operator.sqlEscape(readLine.substring(653, 660)));
								psAssessorData.setString(46, todayDate);
								psAssessorData.setString(47, "Y");
								psAssessorData.setInt(48, 890);

								psAssessorData.setString(49, formatter.format(ts));
								psAssessorData.setInt(50, 890);
								psAssessorData.setString(51, formatter.format(ts));
								psAssessorData.setString(52, "");
								psAssessorData.setString(53, "");
								psAssessorData.setInt(54, 890);
								psAssessorData.setString(55, formatter.format(ts));
								psAssessorData.execute();

								psAssessorOwner.setString(1, apn);
								psAssessorOwner.setString(2, ownerName);
								psAssessorOwner.setString(3, Operator.sqlEscape(readLine.substring(243, 275)));
								psAssessorOwner.setString(4, strNo);
								psAssessorOwner.setString(5, strMod);
								psAssessorOwner.setString(6, preDir);
								psAssessorOwner.setString(7, strName);
								psAssessorOwner.setString(8, unit);
								psAssessorOwner.setString(9, city);
								psAssessorOwner.setString(10, zip);
								psAssessorOwner.setString(11, Operator.sqlEscape(readLine.substring(275, 280)));
								psAssessorOwner.setString(12, Operator.sqlEscape(readLine.substring(280, 312)));
								psAssessorOwner.setString(13, Operator.sqlEscape(readLine.substring(312, 344)));
								psAssessorOwner.setString(14, Operator.sqlEscape(readLine.substring(344, 352)));
								psAssessorOwner.setString(15, "Y");
								psAssessorOwner.setInt(16, 890);
								psAssessorOwner.setString(17, formatter.format(ts));
								psAssessorOwner.setInt(18, 890);
								psAssessorOwner.setString(19, formatter.format(ts));
								psAssessorOwner.setString(20, "");
								psAssessorOwner.setString(21, "");
								psAssessorOwner.setInt(22, 890);
								psAssessorOwner.setString(23, formatter.format(ts));

								psAssessorOwner.execute();

								psAssessorSite.setString(1, apn);
								psAssessorSite.setString(2, Operator.sqlEscape(readLine.substring(47, 52)));
								psAssessorSite.setString(3, Operator.sqlEscape(readLine.substring(52, 55)));
								psAssessorSite.setString(4, Operator.sqlEscape(readLine.substring(55, 56)));
								psAssessorSite.setString(5, Operator.sqlEscape(readLine.substring(56, 88)));
								psAssessorSite.setString(6, "");
								psAssessorSite.setString(7, Operator.sqlEscape(readLine.substring(96, 120)));
								psAssessorSite.setString(8, Operator.sqlEscape(readLine.substring(120, 129)));
								psAssessorSite.setString(9, Operator.sqlEscape(readLine.substring(368, 383)));
								psAssessorSite.setString(10, "");
								psAssessorSite.setString(11, "");
								psAssessorSite.setString(12, "");
								psAssessorSite.setString(13, "");
								psAssessorSite.setString(14, "");
								psAssessorSite.setString(15, "");
								psAssessorSite.setString(16, "");
								psAssessorSite.setString(17, Operator.sqlEscape(readLine.substring(344, 352)));
								psAssessorSite.setString(18, "Y");
								psAssessorSite.setInt(19, 890);
								psAssessorSite.setString(20, formatter.format(ts));
								psAssessorSite.setInt(21, 890);
								psAssessorSite.setString(22, formatter.format(ts));
								psAssessorSite.setString(23, "");
								psAssessorSite.setString(24, "");
								psAssessorSite.setInt(25, 890);
								psAssessorSite.setString(26, formatter.format(ts));
								psAssessorSite.execute();

								psAssessorTax.setString(1, apn);
								psAssessorTax.setString(2, Operator.sqlEscape(readLine.substring(10, 15)));
								psAssessorTax.setString(3, Operator.sqlEscape(readLine.substring(15, 21)));
								psAssessorTax.setString(4, Operator.sqlEscape(readLine.substring(21, 25)));
								psAssessorTax.setString(5, Operator.sqlEscape(readLine.substring(25, 34)));
								psAssessorTax.setString(6, Operator.sqlEscape(readLine.substring(34, 38)));
								psAssessorTax.setString(7, Operator.sqlEscape(readLine.substring(38, 47)));
								psAssessorTax.setString(8, Operator.sqlEscape(readLine.substring(352, 353)));
								psAssessorTax.setString(9, Operator.sqlEscape(readLine.substring(353, 357)));
								psAssessorTax.setString(10, Operator.sqlEscape(readLine.substring(391, 392)));
								psAssessorTax.setString(11, Operator.sqlEscape(readLine.substring(392, 393)));
								psAssessorTax.setString(12, Operator.sqlEscape(readLine.substring(393, 394)));
								psAssessorTax.setString(13, Operator.sqlEscape(readLine.substring(394, 403)));
								psAssessorTax.setString(14, Operator.sqlEscape(readLine.substring(403, 412)));
								psAssessorTax.setString(15, Operator.sqlEscape(readLine.substring(412, 421)));
								psAssessorTax.setString(16, Operator.sqlEscape(readLine.substring(421, 430)));
								psAssessorTax.setString(17, Operator.sqlEscape(readLine.substring(430, 433)));
								psAssessorTax.setString(18, Operator.sqlEscape(readLine.substring(433, 442)));
								psAssessorTax.setString(19, Operator.sqlEscape(readLine.substring(442, 451)));
								psAssessorTax.setString(20, Operator.sqlEscape(readLine.substring(451, 452)));
								psAssessorTax.setString(21, Operator.sqlEscape(readLine.substring(452, 461)));
								psAssessorTax.setString(22, Operator.sqlEscape(readLine.substring(461, 469)));
								psAssessorTax.setString(23, Operator.sqlEscape(readLine.substring(469, 470)));
								psAssessorTax.setString(24, Operator.sqlEscape(readLine.substring(470, 479)));
								psAssessorTax.setString(25, Operator.sqlEscape(readLine.substring(479, 487)));
								psAssessorTax.setString(26, Operator.sqlEscape(readLine.substring(487, 488)));
								psAssessorTax.setString(27, Operator.sqlEscape(readLine.substring(488, 497)));
								psAssessorTax.setString(28, Operator.sqlEscape(readLine.substring(497, 505)));
								psAssessorTax.setString(29, Operator.sqlEscape(readLine.substring(344, 352)));
								psAssessorTax.setString(30, "Y");
								psAssessorTax.setInt(31, 890);
								psAssessorTax.setString(32, formatter.format(ts));
								psAssessorTax.setInt(33, 890);
								psAssessorTax.setString(34, formatter.format(ts));
								psAssessorTax.setString(35, "");
								psAssessorTax.setString(36, "");
								psAssessorTax.setInt(37, 890);
								psAssessorTax.setString(38, formatter.format(ts));
								psAssessorTax.execute();

							} else {
								psAssessorException.setString(1, apn);
								psAssessorException.setString(2, Operator.sqlEscape(readLine.substring(357, 358)));
								psAssessorException.setString(3, Operator.sqlEscape(readLine.substring(358, 368)));
								psAssessorException.setString(4, Operator.sqlEscape(readLine.substring(387, 390)));
								psAssessorException.setString(5, Operator.sqlEscape(readLine.substring(390, 391)));
								psAssessorException.setString(6, Operator.sqlEscape(readLine.substring(505, 509)));
								psAssessorException.setString(7, Operator.sqlEscape(readLine.substring(509, 513)));
								psAssessorException.setString(8, Operator.sqlEscape(readLine.substring(513, 518)));
								psAssessorException.setString(9, Operator.sqlEscape(readLine.substring(518, 522)));
								psAssessorException.setString(10, Operator.sqlEscape(readLine.substring(522, 525)));
								psAssessorException.setString(11, Operator.sqlEscape(readLine.substring(525, 527)));
								psAssessorException.setString(12, Operator.sqlEscape(readLine.substring(527, 529)));
								psAssessorException.setString(13, Operator.sqlEscape(readLine.substring(529, 536)));
								psAssessorException.setString(14, Operator.sqlEscape(readLine.substring(536, 540)));
								psAssessorException.setString(15, Operator.sqlEscape(readLine.substring(540, 544)));
								psAssessorException.setString(16, Operator.sqlEscape(readLine.substring(544, 549)));
								psAssessorException.setString(17, Operator.sqlEscape(readLine.substring(549, 553)));
								psAssessorException.setString(18, Operator.sqlEscape(readLine.substring(553, 556)));
								psAssessorException.setString(19, Operator.sqlEscape(readLine.substring(556, 558)));
								psAssessorException.setString(20, Operator.sqlEscape(readLine.substring(558, 560)));
								psAssessorException.setString(21, Operator.sqlEscape(readLine.substring(560, 567)));
								psAssessorException.setString(22, Operator.sqlEscape(readLine.substring(567, 571)));
								psAssessorException.setString(23, Operator.sqlEscape(readLine.substring(571, 575)));
								psAssessorException.setString(24, Operator.sqlEscape(readLine.substring(575, 580)));
								psAssessorException.setString(25, Operator.sqlEscape(readLine.substring(580, 584)));
								psAssessorException.setString(26, Operator.sqlEscape(readLine.substring(584, 587)));
								psAssessorException.setString(27, Operator.sqlEscape(readLine.substring(587, 589)));
								psAssessorException.setString(28, Operator.sqlEscape(readLine.substring(589, 591)));
								psAssessorException.setString(29, Operator.sqlEscape(readLine.substring(591, 598)));
								psAssessorException.setString(30, Operator.sqlEscape(readLine.substring(598, 602)));
								psAssessorException.setString(31, Operator.sqlEscape(readLine.substring(602, 606)));
								psAssessorException.setString(32, Operator.sqlEscape(readLine.substring(606, 611)));
								psAssessorException.setString(33, Operator.sqlEscape(readLine.substring(611, 615)));
								psAssessorException.setString(34, Operator.sqlEscape(readLine.substring(615, 618)));
								psAssessorException.setString(35, Operator.sqlEscape(readLine.substring(618, 620)));
								psAssessorException.setString(36, Operator.sqlEscape(readLine.substring(620, 622)));
								psAssessorException.setString(37, Operator.sqlEscape(readLine.substring(622, 629)));
								psAssessorException.setString(38, Operator.sqlEscape(readLine.substring(629, 633)));
								psAssessorException.setString(39, Operator.sqlEscape(readLine.substring(633, 637)));
								psAssessorException.setString(40, Operator.sqlEscape(readLine.substring(637, 642)));
								psAssessorException.setString(41, Operator.sqlEscape(readLine.substring(642, 646)));
								psAssessorException.setString(42, Operator.sqlEscape(readLine.substring(646, 649)));
								psAssessorException.setString(43, Operator.sqlEscape(readLine.substring(649, 651)));
								psAssessorException.setString(44, Operator.sqlEscape(readLine.substring(651, 653)));
								psAssessorException.setString(45, Operator.sqlEscape(readLine.substring(653, 660)));
								psAssessorException.setString(46, ownerName);
								psAssessorException.setString(47, Operator.sqlEscape(readLine.substring(243, 275)));
								psAssessorException.setString(48, strNo);
								psAssessorException.setString(49, strMod);
								psAssessorException.setString(50, preDir);
								psAssessorException.setString(51, strName);
								psAssessorException.setString(52, unit);
								psAssessorException.setString(53, Operator.sqlEscape(readLine.substring(178, 202)));
								psAssessorException.setString(54, zip);
								psAssessorException.setString(55, Operator.sqlEscape(readLine.substring(275, 280)));
								psAssessorException.setString(56, Operator.sqlEscape(readLine.substring(280, 312)));
								psAssessorException.setString(57, Operator.sqlEscape(readLine.substring(312, 344)));
								psAssessorException.setString(58, "");
								psAssessorException.setString(59, Operator.sqlEscape(readLine.substring(52, 55)));
								psAssessorException.setString(60, Operator.sqlEscape(readLine.substring(55, 56)));
								psAssessorException.setString(61, Operator.sqlEscape(readLine.substring(56, 88)));
								psAssessorException.setString(62, "");
								psAssessorException.setString(63, Operator.sqlEscape(readLine.substring(96, 120)));
								psAssessorException.setString(64, Operator.sqlEscape(readLine.substring(120, 129)));
								psAssessorException.setString(65, Operator.sqlEscape(readLine.substring(368, 383)));
								psAssessorException.setString(66, "");
								psAssessorException.setString(67, "");
								psAssessorException.setString(68, "");
								psAssessorException.setString(69, "");
								psAssessorException.setString(70, "");
								psAssessorException.setString(71, "");
								psAssessorException.setString(72, "");
								psAssessorException.setString(73, Operator.sqlEscape(readLine.substring(10, 15)));
								psAssessorException.setString(74, Operator.sqlEscape(readLine.substring(15, 21)));
								psAssessorException.setString(75, Operator.sqlEscape(readLine.substring(21, 25)));
								psAssessorException.setString(76, Operator.sqlEscape(readLine.substring(25, 34)));
								psAssessorException.setString(77, Operator.sqlEscape(readLine.substring(34, 38)));
								psAssessorException.setString(78, Operator.sqlEscape(readLine.substring(38, 47)));
								psAssessorException.setString(79, Operator.sqlEscape(readLine.substring(352, 353)));
								psAssessorException.setString(80, Operator.sqlEscape(readLine.substring(353, 357)));
								psAssessorException.setString(81, Operator.sqlEscape(readLine.substring(391, 392)));
								psAssessorException.setString(82, Operator.sqlEscape(readLine.substring(392, 393)));
								psAssessorException.setString(83, Operator.sqlEscape(readLine.substring(393, 394)));
								psAssessorException.setString(84, Operator.sqlEscape(readLine.substring(394, 403)));
								psAssessorException.setString(85, Operator.sqlEscape(readLine.substring(403, 412)));
								psAssessorException.setString(86, Operator.sqlEscape(readLine.substring(412, 421)));
								psAssessorException.setString(87, Operator.sqlEscape(readLine.substring(421, 430)));
								psAssessorException.setString(88, Operator.sqlEscape(readLine.substring(430, 433)));
								psAssessorException.setString(89, Operator.sqlEscape(readLine.substring(433, 442)));
								psAssessorException.setString(90, Operator.sqlEscape(readLine.substring(442, 451)));
								psAssessorException.setString(91, Operator.sqlEscape(readLine.substring(451, 452)));
								psAssessorException.setString(92, Operator.sqlEscape(readLine.substring(452, 461)));
								psAssessorException.setString(93, Operator.sqlEscape(readLine.substring(461, 469)));
								psAssessorException.setString(94, Operator.sqlEscape(readLine.substring(469, 470)));
								psAssessorException.setString(95, Operator.sqlEscape(readLine.substring(470, 479)));
								psAssessorException.setString(96, Operator.sqlEscape(readLine.substring(479, 487)));
								psAssessorException.setString(97, Operator.sqlEscape(readLine.substring(487, 488)));
								psAssessorException.setString(98, Operator.sqlEscape(readLine.substring(488, 497)));
								psAssessorException.setString(99, Operator.sqlEscape(readLine.substring(497, 505)));
								psAssessorException.setString(100, todayDate);
								psAssessorException.setString(101, "Y");
								psAssessorException.setInt(102, 890);
								psAssessorException.setString(103, formatter.format(ts));
								psAssessorException.setInt(104, 890);
								psAssessorException.setString(105, formatter.format(ts));
								psAssessorException.setString(106, "");
								psAssessorException.setString(107, "");
								psAssessorException.setInt(108, 890);
								psAssessorException.setString(109, formatter.format(ts));
								psAssessorException.execute();

							}
							// }
							if (((pl % 1000) == 0)) {
								updateAssessorImportLog("progress", "ASSESSOR",
										"Finished inserting " + pl + " records from Flat File to Assessor table",
										table);
								updateAssessorImportLog("progress", "FILE",
										"Finished Reading " + pl + " records from Flat File", table);
								// return j;
								setJ(pl);
								break;
							}

							if (pl == recordCount) {
								updateAssessorImportLog("progress", "ASSESSOR",
										"Finished inserting " + pl + " records from Flat File to Assessor table",
										table);
								updateAssessorImportLog("progress", "FILE",
										"Finished Reading " + pl + " records from Flat File", table);
								setJ(pl);
								break;
							}

						}

						// pl++;

					}

					db.clear();
					if (psAssessorData != null)
						psAssessorData.close();
					if (psAssessorOwner != null)
						psAssessorOwner.close();
					if (psAssessorSite != null)
						psAssessorSite.close();
					if (psAssessorTax != null)
						psAssessorTax.close();
					if (recordCount <= processCount) {
						updateAssessorImportLog("complete", "IMPORT", "Assessor data import complete.", table);
						updateAssessorImportLog("complete", "ERROR",
								"ERROR Occurred and the assessor data import did not compelte.", table);
					}

					input.close();

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// }
				// }).start();
			}

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		// String sqlCount= "select count(*) as count from assessor_data where
		// active='Y'";
		// Logger.info(sqlCount);
		// Sage db = new Sage();
		// db.connect();
		// db.query(sqlCount);

		// int count=0;
		// boolean countFlag= false;
		// while (db.next()){
		// count =db.getInt("count");
		// }

		return getJ();
	}

	public static int processRecordsCount() {

//System.out.println(" Before returning **********" + count );
		// while(((count % 10) == 0) ||) {

		return 0;
	}

	public static boolean updateAssessorImportLog(String status, String tableType, String comments, String table) {
		boolean result = false;
		String sql = "";
		Sage db = new Sage();
		db.connect();

		sql = "INSERT INTO " + table
				+ "( STATUS,PROCESS_TYPE,PROCESS_DESCRIPTION,ACTIVE,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CREATED_IP,UPDATED_IP) VALUES ('"
				+ status + "','" + tableType + "','" + comments
				+ "','Y',890,CURRENT_TIMESTAMP,890,CURRENT_TIMESTAMP,'','')";

		result = db.update(sql);

		db.clear();
		CsDeleteCache.deleteCache();
		return result;
	}

	// Get Assessor logs
	public static ArrayList<MapSet> getAssessorLogs() {
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();

		sb.append(
				" SELECT AIL.*,U.FIRST_NAME + ' '+COALESCE(U.LAST_NAME, '') AS CREATED_NAME FROM ASSESSOR_IMPORT_LOG AIL JOIN USERS U ON AIL.CREATED_BY=U.ID WHERE AIL.ACTIVE='Y' ");

		a = AdminAgent.getList(sb.toString());

		return a;
	}

	// Clear Assessor logs
	public static boolean clearLogs() {
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		StringBuilder sb = new StringBuilder();
		boolean result = false;

		Sage db = new Sage();
        db.connect();
		sb.append(
				"UPDATE ASSESSOR_IMPORT_LOG SET ACTIVE='N',UPDATED_BY=890,UPDATED_DATE=current_timestamp WHERE ACTIVE='Y' ");
		result = db.update(sb.toString());

		db.clear();
		return result;
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static void exportException() throws IOException {
		try {
			Sage db = new Sage();
            db.connect();			
			String sql = "SELECT APN , FRST_OWNR_NAME , FRST_OWNR_NAME_OVR ,SITUS_HOUSE_NO, SITUS_FRACTION, SITUS_DIRECTION, SITUS_STREET_NAME, SITUS_UNIT, SITUS_CITY_STATE,SITUS_ZIP  FROM ASSESSOR_IMPORT_EXCEPTION where active='Y'";
			db.query(sql);
			String csvFileName = Config.getString("files.storage_path") + "/ASSESSOR_EXCEPTION_EXPORT.csv";
			FileWriter fileWriter = new FileWriter(csvFileName);
			BufferedWriter BuffFileWriter = new BufferedWriter(fileWriter);

			//fileWriter = new BufferedWriter(new FileWriter(csvFileName));

			String columns = "";

			String[] cols = db.COLUMNS;
			int j = 0;
			while (db.next()) {
				String line = "";
				for (int i = 0; i < cols.length; i++) {
					columns = columns + cols[i] + ",";
					int columnCount = cols.length;
					String valueString = "";

					valueString = db.getString(cols[i]);

					valueString = "\"" + escapeDoubleQuotes(valueString) + "\"";

					line = line.concat(valueString);

					if (i != columnCount) {
						line = line.concat(",");
					}

				}
				if (j == 0) {
					columns = columns.substring(0, columns.length() - 1);
					fileWriter.write(columns);

				}   
				BuffFileWriter.newLine();
				BuffFileWriter.write(line);
				j++;
			}

			db.clear();
			BuffFileWriter.close();
			//if(BuffFileWriter!=null) BuffFileWriter.flush();
	        //if(fileWriter!=null) fileWriter.flush();
		} catch (Exception e) {
			System.out.println("Export error:");
			e.printStackTrace();
		}
	}

	public static String getFileName(String baseName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String dateTimeInfo = dateFormat.format(new Date());
		return baseName.concat(String.format("_%s.csv", dateTimeInfo));
	}

	public static int getJ() {
		return j;
	}

	public static void setJ(int j) {
		AssessorImport.j = j;
	}

	private static String escapeDoubleQuotes(String value) {
		return value.replaceAll("\"", "\"\"");
	}
}
