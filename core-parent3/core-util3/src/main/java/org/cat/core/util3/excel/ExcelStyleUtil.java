package org.cat.core.util3.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cat.core.util3.string.ArchFontConstants;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月6日 下午9:36:54
 * @version 1.0
 * @description excel样式处理类
 *
 */
public class ExcelStyleUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:40:50
	 * @version 1.0
	 * @description 获取单元格样式（居中、粗体、微软雅黑、11号字）
	 * @param excel XSSFWorkbook
	 * @return XSSFCellStyle
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午5:18:09
	 * @version 1.0
	 * @description 获取单元格样式（居中、粗体、微软雅黑、11号字） 
	 * @param excel
	 * @return
	 */
	public static XSSFCellStyle getCenterBold11Style(XSSFWorkbook excel) {
		XSSFCellStyle style = excel.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFFont font = excel.createFont();
		font.setFontName(ArchFontConstants.WEI_RUAN_YAN_HEI);
		font.setFontHeightInPoints(ExcelConstants.FontSize.ELEVEN);
		font.setBold(true);

		style.setFont(font);
		
		return style;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午5:17:50
	 * @version 1.0
	 * @description 获取单元格样式（居左、微软雅黑、11号字）  
	 * @param excel XSSFWorkbook
	 * @return XSSFCellStyle
	 */
	public static XSSFCellStyle getLeft11Style(XSSFWorkbook excel) {
		XSSFCellStyle style = excel.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		
		XSSFFont font = excel.createFont();
		font.setFontName(ArchFontConstants.WEI_RUAN_YAN_HEI);
		font.setFontHeightInPoints(ExcelConstants.FontSize.ELEVEN);
		font.setBold(false);

		style.setFont(font);
		
		return style;
	}
	
	
}
