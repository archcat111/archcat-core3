package org.cat.core.util3.excel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hpsf.IllegalPropertySetDataException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cat.core.util3.date.ArchDateTimeUtil;


/**
 * 
 * @author wangyunlong
 * @date 2017年8月6日 下午9:37:22
 * @version 1.0
 * @description Excel内容处理类
 *
 */
public class ExcelContentUtil {
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年4月10日 下午3:28:36
	 * @version 1.0
	 * @description 创建excel的一行标题，最上面（居中、粗体、微软雅黑、11号字）
	 *
	 * @param excel XSSFWorkbook
	 * @param sheet XSSFSheet
	 * @param titleArray 一个字符串数组
	 */
	public static void createTitleRow(XSSFWorkbook excel,XSSFSheet sheet,String[] titleArray){
		if(ArrayUtils.isEmpty(titleArray)){
			throw new IllegalArgumentException("titleArray不能为空");
		}
		XSSFRow titleRow =sheet.createRow(0);
		XSSFCellStyle titleStyle = ExcelStyleUtil.getCenterBold11Style(excel);
		
		for (int i = 0; i < titleArray.length; i++) {
			XSSFCell titleCell =titleRow.createCell(i);
			titleCell.setCellStyle(titleStyle);
			titleCell.setCellValue(titleArray[i]);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:40:38
	 * @version 1.0
	 * @description 创建excel的一行内容（居左、微软雅黑、11号字）  
	 * @param excel XSSFWorkbook
	 * @param sheet XSSFSheet
	 * @param rowNum 行号
	 * 			注意：rowNum从0开始算，一般第0行会被设置为TitleRow
	 * @param contentList 内容列表
	 */
	public static void createContentRow(XSSFWorkbook excel,XSSFSheet sheet,int rowNum,List<String> contentList){
		if(CollectionUtils.isEmpty(contentList)){
			throw new IllegalArgumentException("contentList不能为空");
		}
		XSSFRow row =sheet.createRow(rowNum);
		XSSFCellStyle style = ExcelStyleUtil.getLeft11Style(excel);
		
		for (int i = 0; i < contentList.size(); i++) {
			XSSFCell titleCell =row.createCell(i);
			titleCell.setCellStyle(style);
			titleCell.setCellValue(contentList.get(i));
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:42:05
	 * @version 1.0
	 * @description 创建excel的一行内容（居左、微软雅黑、11号字）   
	 * @param excel XSSFWorkbook
	 * @param sheet XSSFSheet
	 * @param rowNum 行号
	 * 			注意：rowNum从0开始算，一般第0行会被设置为TitleRow
	 * @param contentArray 内容列表
	 */
	public static void createContentRow(XSSFWorkbook excel,XSSFSheet sheet,int rowNum,String[] contentArray){
		if(ArrayUtils.isEmpty(contentArray)){
			throw new IllegalArgumentException("contentArray不能为空");
		}
		List<String> contentList = Arrays.asList(contentArray);
		createContentRow(excel, sheet, rowNum, contentList);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月12日 下午4:42:05
	 * @version 1.0
	 * @description 获取Excel某个单元格中的数据
	 * STRING--->String
	 * NUMERIC--->String
	 *
	 * @param xssfCell 单元格
	 * @return 字符串类型的内容
	 */
	public static String readCellContentToString(XSSFCell xssfCell){
		CellType xssfCellType=xssfCell.getCellTypeEnum();
		String result=null;
		switch (xssfCellType) {
		case STRING:
			result=xssfCell.getStringCellValue();
			break;
		case NUMERIC:
			double tempResult=xssfCell.getNumericCellValue();
			result=String.valueOf(tempResult);
		default:
			throw new IllegalPropertySetDataException("xssfCellType不是String也不是numeric");
		}
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:42:05
	 * @version 1.0
	 * @description 获取Excel某个单元格中的数据 
	 * STRING--->Double
	 * NUMERIC--->Double
	 * 
	 * @param xssfCell 单元格
	 * @return Double类型的内容
	 */
	public static Double readCellContentToDouble(XSSFCell xssfCell){
		CellType xssfCellType=xssfCell.getCellTypeEnum();
		Double result=null;
		switch (xssfCellType) {
		case STRING:
			String tempResult=xssfCell.getStringCellValue();
			result=Double.valueOf(tempResult);
			break;
		case NUMERIC:
			result=xssfCell.getNumericCellValue();
		default:
			throw new IllegalPropertySetDataException("xssfCellType不是String也不是numeric");
		}
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:42:05
	 * @version 1.0
	 * @description 获取Excel某个单元格中的数据  
	 * STRING--->Date
	 * NUMERIC--->Date
	 * 
	 * @param xssfCell 单元格
	 * @return Date类型的内容
	 */
	public static Date readCellContentToDate(XSSFCell xssfCell){
		CellType xssfCellType=xssfCell.getCellTypeEnum();
		Date result=null;
		switch (xssfCellType) {
		case STRING:
			String tempResult=xssfCell.getStringCellValue();
			result=ArchDateTimeUtil.getFormatDate(tempResult, ArchDateTimeUtil.FormatConstants.DATE_NUMBER);			
			break;
		case NUMERIC:
			result=xssfCell.getDateCellValue();
		default:
			throw new IllegalPropertySetDataException("xssfCellType不是String也不是numeric");
		}
		return result;
	}
	
}
