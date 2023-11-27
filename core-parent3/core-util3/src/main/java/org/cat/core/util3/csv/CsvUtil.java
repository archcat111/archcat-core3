package org.cat.core.util3.csv;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.cat.core.util3.file.ArchIOUtil;


public class CsvUtil {
	public static class Constants {
		public final static String NEWLINE="\r\n";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年5月22日 上午10:14:01
	 * @version 1.0
	 * @description 将数据写入文件中的一行
	 * @param rowData	需要写入一行的数据，数据之间会自动用","进行隔离，符合CSV格式
	 * @param bufferedWriter	输出到文件的对象
	 * @throws IOException
	 */
	private static void writeRow(List<Object> rowData, BufferedWriter bufferedWriter) throws IOException {
		int count = rowData.size();
		for (int i = 0; i < rowData.size(); i++) {
			StringBuffer sb = new StringBuffer();
			String rowStr = null;
			if (i == count - 1) {
				rowStr = sb.append(rowData.get(i)).toString();
			} else {
				rowStr = sb.append(rowData.get(i)).append(",").toString();
			}
			bufferedWriter.write(rowStr);
		}
		bufferedWriter.newLine();
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午3:29:56
	 * @version 1.0
	 * @description 将数据写入文件中的一行 
	 * @param rowData 需要写入一行的数据，数据之间会自动用","进行隔离，符合CSV格式
	 * @param byteArrayOutputStream 输出到文件的对象
	 * @throws IOException
	 */
	private static void writeRow(List<Object> rowData, ByteArrayOutputStream byteArrayOutputStream) throws IOException {
		int count = rowData.size();
		for (int i = 0; i < rowData.size(); i++) {
			StringBuffer sb = new StringBuffer();
			String rowStr = null;
			if (i == count - 1) {
				rowStr = sb.append(rowData.get(i)).toString();
			} else {
				rowStr = sb.append(rowData.get(i)).append(",").toString();
			}
			byteArrayOutputStream.write(rowStr.getBytes(StandardCharsets.UTF_8));
		}
		byteArrayOutputStream.write(Constants.NEWLINE.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年5月31日 上午10:35:02
	 * @version 1.0
	 * @description 将数据写入File，默认实现UTF-8（如果后期有需要再增加编码的处理）
	 *
	 * @param file 需要写入的CSV文件
	 * @param headDataList CSV文档的标题
	 * @param datasList CSV文档的内容部分，需要自行与标题进行列的对应
	 * @throws IOException 
	 */
	public static void writeCsv(File file, List<Object> headDataList, List<List<Object>> datasList) throws IOException {
		BufferedWriter bufferedWriter = ArchIOUtil.getBufferedWriter(file, StandardCharsets.UTF_8);
		if (headDataList != null) {
			writeRow(headDataList, bufferedWriter);
		}
		if (datasList != null) {
			for (int i = 0; i < datasList.size(); i++) {
				List<Object> rowData = datasList.get(i);
				writeRow(rowData, bufferedWriter);
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午3:42:10
	 * @version 1.0
	 * @description 将数据写入byteArrayOutputStream
	 * 		注意1：方法中已经对ByteArrayOutputStream执行了flush()
	 * 		注意2：方法中不会对ByteArrayOutputStream进行close()，需要根据自己的需求自行对其执行close()方法
	 * @param byteArrayOutputStream 输出到文件的对象
	 * @param headDataList CSV文档的标题
	 * @param datasList CSV文档的内容部分，需要自行与标题进行列的对应
	 * @throws IOException
	 */
	public static void writeCsv(ByteArrayOutputStream byteArrayOutputStream, List<Object> headDataList,
			List<List<Object>> datasList) throws IOException {
		byte[] utf8Bom={(byte)0xEF,(byte)0xBB,(byte)0xBF};
		byteArrayOutputStream.write(utf8Bom);
		if (headDataList != null) {
			writeRow(headDataList, byteArrayOutputStream);
		}
		if (datasList != null) {
			for (int i = 0; i < datasList.size(); i++) {
				List<Object> rowData = datasList.get(i);
				writeRow(rowData, byteArrayOutputStream);
			}
		}
		
		byteArrayOutputStream.flush();
	}

}
