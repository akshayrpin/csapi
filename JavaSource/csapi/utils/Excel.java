package csapi.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import alain.core.utils.Logger;
import alain.core.utils.Timekeeper;



public class Excel {


	public static ArrayList<HashMap<String, String>> read(String file) {
		ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
		try {
			XSSFWorkbook  wb = new XSSFWorkbook(file);
			
			XSSFSheet sheet = wb.getSheetAt(0);

			int rsize = sheet.getLastRowNum();
			String[] header = new String[0];

			if (rsize > 0) {
				XSSFRow hrow = (XSSFRow) sheet.getRow(0);
				int hsize = hrow.getLastCellNum();
				Logger.highlight(hsize);
				header = new String[hsize];
				for (int h=0; h<hsize; h++) {
					XSSFCell head = (XSSFCell) hrow.getCell(h);
					if (head.getCellType() == XSSFCell.CELL_TYPE_STRING) {
						header[h] = head.getStringCellValue();
					}
					else {
						try {
							header[h] = head.toString();
						} catch (Exception he) { }
					}
				}
				for (int r=1; r<=rsize; r++) {
					XSSFRow row = (XSSFRow) sheet.getRow(r);
					int csize = row.getLastCellNum();
					HashMap<String, String> m = new HashMap<String, String>();
					for (int c=0; c<csize; c++) {
						String field = header[c];
						try {
							XSSFCell cell = (XSSFCell) row.getCell(c);

							if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								m.put(field, cell.getStringCellValue());
							}
							else if(DateUtil.isCellDateFormatted(cell)) {
								Timekeeper d = new Timekeeper();
								d.setDate(cell.getDateCellValue());
					            m.put(field, d.getString("DATECODE"));
							}
							else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								String bd = new BigDecimal(cell.getNumericCellValue()).toPlainString();
								m.put(field, bd);
							}
							else {
								try {
									m.put(field, cell.toString());
								}
								catch (Exception ce) {}
							}
						}
						catch (Exception rce) { m.put(field, ""); }
					}
					al.add(m);
				}
			}
			wb.close();
		}
		catch (Exception e) { return readOld(file); }
		return al;
	}




	public static ArrayList<HashMap<String, String>> readOld(String file) {
		ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
		try {
		
			 Workbook wb = WorkbookFactory.create(new File(file));

			
			 Sheet  sheet = wb.getSheetAt(0);

			int rsize = sheet.getLastRowNum();
			String[] header = new String[0];

			if (rsize > 0) {
				HSSFRow hrow = (HSSFRow) sheet.getRow(0);
				int hsize = hrow.getLastCellNum();
				Logger.highlight(hsize);
				header = new String[hsize];
				for (int h=0; h<hsize; h++) {
					HSSFCell head = (HSSFCell) hrow.getCell(h);
					if (head.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						header[h] = head.getStringCellValue();
					}
					else {
						try {
							header[h] = head.toString();
						} catch (Exception he) { }
					}
				}
				for (int r=1; r<=rsize; r++) {
					HSSFRow row = (HSSFRow) sheet.getRow(r);
					int csize = row.getLastCellNum();
					HashMap<String, String> m = new HashMap<String, String>();
					for (int c=0; c<csize; c++) {
						String field = header[c];
						try {
							HSSFCell cell = (HSSFCell) row.getCell(c);

							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
								m.put(field, cell.getStringCellValue());
							}
							else if(DateUtil.isCellDateFormatted(cell)) {
								Timekeeper d = new Timekeeper();
								d.setDate(cell.getDateCellValue());
					            m.put(field, d.getString("DATECODE"));
							}
							else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								String bd = new BigDecimal(cell.getNumericCellValue()).toPlainString();
								m.put(field, bd);
							}
							else {
								try {
									m.put(field, cell.toString());
								}
								catch (Exception ce) {}
							}
						}
						catch (Exception rce) { m.put(field, ""); }
					}
					al.add(m);
				}
			}
			wb.close();
		}
		catch (Exception e) { Logger.error(e); }
		return al;
	}







}























