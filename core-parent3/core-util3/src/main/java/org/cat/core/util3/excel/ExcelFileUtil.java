package org.cat.core.util3.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月6日 下午9:26:38
 * @version 1.0
 * @description Excel文件处理类
 *
 */
public class ExcelFileUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:02:55
	 * @version 1.0
	 * @description 根据输入流获取XSSFWorkbook对象（excel 2007以上） 
	 * @param inputStream 输入流
	 * @return XSSFWorkbook
	 * @throws IOException
	 */
	public static XSSFWorkbook readExcel(InputStream inputStream) throws IOException{
		XSSFWorkbook xssfWorkbook=new XSSFWorkbook(inputStream);
		return xssfWorkbook;
	} 
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:02:53
	 * @version 1.0
	 * @description 根据输入流获取XSSFWorkbook对象（excel 2007以上）,增加buffered处理 
	 * @param inputStream 输入流，注意：方法中会将inputStream包装为bufferedInputStream，不需要自行进行包装
	 * @return XSSFWorkbook
	 * @throws IOException
	 */
	public static XSSFWorkbook readExcelForBuffered(InputStream inputStream) throws IOException{
		BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
		XSSFWorkbook xssfWorkbook=readExcel(bufferedInputStream);
		return xssfWorkbook;
	} 
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:26:49
	 * @version 1.0
	 * @description 根据路径获取获取XSSFWorkbook对象（excel 2007以上）
	 * @param path 路径
	 * @return XSSFWorkbook
	 * @throws IOException
	 */
	public static XSSFWorkbook readExcel(String path) throws IOException{
		File file=new File(path);
		InputStream inputStream=new FileInputStream(file);
		return readExcel(inputStream);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午4:04:41
	 * @version 1.0
	 * @description 根据路径获取获取XSSFWorkbook对象（excel 2007以上） 
	 * 			注意：方法中会将inputStream包装为BufferedInputStream，不需要自行包装
	 * @param path 路径
	 * @return XSSFWorkbook
	 * @throws IOException
	 */
	public static XSSFWorkbook readExcelForBuffered(String path) throws IOException{
		File file=new File(path);
		InputStream inputStream=new FileInputStream(file);
		return readExcelForBuffered(inputStream);
	}
	
	
}
