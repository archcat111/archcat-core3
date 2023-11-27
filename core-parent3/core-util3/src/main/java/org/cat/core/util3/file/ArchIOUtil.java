package org.cat.core.util3.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月6日 下午7:23:55
 * @version 1.0
 * @description 用于文件读取、文件写入、流处理的工具类
 *
 */
public class ArchIOUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:07:06
	 * @version 1.0
	 * @description 将输入流写入一个文件中（不包含验证） 
	 * 			该方案默认配置了输入缓存及输出缓存
	 * @param inputStream 输入流
	 * @param file 需要写出的文件
	 * @param autoClose 结束后是否关闭输入流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean inputStreamToFile(InputStream inputStream, File file, boolean autoClose) throws IOException {
		

		BufferedInputStream bis = new BufferedInputStream(inputStream);
		OutputStream os = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		byte[] buffer = new byte[1024];
		int i = 0;
		while ((i = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, i);
		}
		bos.flush();
		bos.close();
		os.close();
		bis.close();
		
		if (autoClose) {
			inputStream.close();
		}
		return true;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:10:30
	 * @version 1.0
	 * @description 将输入流写入一个文件中（不包含验证） 
	 * 		{@linkplain org.cat.core.util3.file.ArchIOUtil#inputStreamToFile(InputStream, File, boolean)}
	 * @param inputStream 输入流
	 * @param fileFullName 需要写出的文件
	 * @param autoClose 结束后是否关闭输入流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean inputStreamToFile(InputStream inputStream, String fileFullName, boolean autoClose) throws IOException {
		File outFile = new File(fileFullName);
		boolean result = inputStreamToFile(inputStream, outFile, autoClose);
		return result;
	}
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:08:01
	 * @version 1.0
	 * @description 将输入流写入一个文件中（包含验证） 
	 * 			该方案默认配置了输入缓存及输出缓存
	 * @param inputStream 输入流
	 * @param file 需要写出的文件
	 * @param autoClose 结束后是否关闭输入流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean inputStreamToFileAndVerify(InputStream inputStream, File file, boolean autoClose) throws IOException {
		if(file == null) {
			throw new IllegalArgumentException("file不能为null");
		}
		if(!file.exists()) {
			throw new IllegalArgumentException("文件["+file.getName()+"]不存在");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("文件["+file.getName()+"]已存在，并且不是一个文件");
		}
		
		boolean result = inputStreamToFile(inputStream, file, autoClose);
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:12:42
	 * @version 1.0
	 * @description 将输入流写入一个文件中（包含验证）  
	 * 		该方案默认配置了输入缓存及输出缓存
	 * @param inputStream 输入流
	 * @param fileFullName 需要写出的文件的全路径
	 * @param autoClose 结束后是否关闭输入流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean inputStreamToFileAndVerify(InputStream inputStream, String fileFullName, boolean autoClose) throws IOException  {
		File outFile = new File(fileFullName);
		boolean result = inputStreamToFileAndVerify(inputStream, outFile, autoClose);
		return result;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午8:28:41
	 * @version 1.0
	 * @description 将文件写入一个输出流（不包含验证） 
	 * @param file 源文件
	 * @param outputStream 输出流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean fileToOutputStream(File file, OutputStream outputStream) throws IOException {
		InputStream is = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(outputStream);
		byte[] buffer = new byte[1024];
		int i = 0;
		// bos.write(buffer);
		while ((i = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, i);
		}
		is.close();
		bis.close();
		bos.flush();
		outputStream.flush();
		bos.close();
		bis.close();
		return true;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:56:28
	 * @version 1.0
	 * @description 将文件写入一个输出流（不包含验证）   
	 * @param fileFullName 文件路径
	 * @param outputStream 输出流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean fileToOutputStream(String fileFullName, OutputStream outputStream) throws IOException {
		File file = new File(fileFullName);
		boolean result = fileToOutputStream(file, outputStream);
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:55:08
	 * @version 1.0
	 * @description 将文件写入一个输出流（包含验证）  
	 * @param file 源文件
	 * @param outputStream 输出流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean fileToOutputStreamAndVerify(File file, OutputStream outputStream) throws IOException {
		if(file == null) {
			throw new IllegalArgumentException("file不能为null");
		}
		if(!file.exists()) {
			throw new IllegalArgumentException("文件["+file.getName()+"]不存在");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("文件["+file.getName()+"]已存在，并且不是一个文件");
		}
		
		boolean result = fileToOutputStream(file, outputStream);
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午5:57:36
	 * @version 1.0
	 * @description 将文件写入一个输出流（包含验证） 
	 * @param fileFullName 源文件名
	 * @param outputStream 输出流
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean fileToOutputStreamAndVerify(String fileFullName, OutputStream outputStream) throws IOException {
		boolean result = fileToOutputStreamAndVerify(fileFullName, outputStream);
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午6:03:27
	 * @version 1.0
	 * @description 读取文件中的字符串（不包含验证） 
	 * @param file 源文件
	 * @param charset {@linkplain java.nio.charset.StandardCharsets}
	 * @return
	 * @throws IOException
	 */
	public static String getStr4File(File file, Charset charset) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		InputStreamReader isr = new InputStreamReader(bis, charset);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		while (br.ready()) {
			sb.append(br.readLine());
		}
		br.close();
		isr.close();
		bis.close();
		fis.close();

		return sb.toString();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月15日 上午10:12:50
	 * @version 1.0
	 * @description 读取文件中的字符串（不包含验证）   
	 * @param fileFullPath 源文件全路径
	 * @param charset {@linkplain java.nio.charset.StandardCharsets}
	 * @return
	 * @throws IOException
	 */
	public static String getStr4File(String fileFullPath, Charset charset) throws IOException {
		return getStr4File(new File(fileFullPath), charset);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月15日 上午10:13:47
	 * @version 1.0
	 * @description 读取文件中的字符串（不包含验证）  
	 * @param filePath 源文件路径
	 * @param fileName 源文件的文件名
	 * @param charset {@linkplain java.nio.charset.StandardCharsets}
	 * @return
	 * @throws IOException
	 */
	public static String getStr4File(String filePath, String fileName, Charset charset) throws IOException {
		return getStr4File(new File(filePath + "/" + fileName), charset);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月15日 上午10:18:10
	 * @version 1.0
	 * @description 将字符串写入文件中（不包含验证）   
	 * @param str 需要写入文件的字符串
	 * @param file 需要写入的文件
	 * @param charset {@linkplain java.nio.charset.StandardCharsets}
	 * @param isAppend 是否开启追加写入模式 
	 * 			{@linkplain FileOutputStream#FileOutputStream(File, boolean)}
	 * @return
	 * @throws IOException
	 */
	public static File setStrToFile(String str, File file,Charset charset,boolean isAppend) throws IOException {
		OutputStream os = new FileOutputStream(file, isAppend);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(bos, charset);
		BufferedWriter bw = new BufferedWriter(osw);
		
		bw.write(str);
		bw.flush();
		osw.flush();
		bos.flush();
		os.flush();
		bw.close();
		osw.close();
		bos.close();
		os.close();
		
		return file;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月15日 上午10:19:03
	 * @version 1.0
	 * @description 将字符串写入一个文件
	 * @param str
	 * @param fileFullName 需要写入的文件的全路径
	 * @param charset 字符集
	 * @param isAppend 是否追加
	 * @return
	 * @throws IOException
	 */
	public static File setStrToFile(String str, String fileFullName, Charset charset, boolean isAppend) throws IOException {
		File file = new File(fileFullName);
		return setStrToFile(str, file,charset,isAppend);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月16日 下午2:10:41
	 * @version 1.0
	 * @description 将Byte数组写入文件中  
	 * @param bytes 需要写入文件的byte数组
	 * @param file 需要写入的文件
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean setByteArr2File(byte[] bytes, File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(os);

		bos.write(bytes);
		
		bos.flush();
		os.flush();
		bos.close();
		os.close();
		
		return true;

	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月16日 下午2:13:01
	 * @version 1.0
	 * @description 将一个文件的内容拷贝到另一个文件中  
	 * @param sourceFileFullName 源文件全路径
	 * @param descFileFullName 目标文件全路径
	 * @return 成功or失败
	 * @throws IOException
	 */
	public static boolean fileToFile(String sourceFileFullName, String descFileFullName) throws IOException {
		InputStream inputStream = new FileInputStream(sourceFileFullName);
		boolean result=inputStreamToFile(inputStream, descFileFullName, true);
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月16日 下午2:14:07
	 * @version 1.0
	 * @description 将一个文件的内容拷贝到另一个临时文件中  
	 * @param sourceFileFullName 源文件全路径
	 * @param tempFilePrefix 临时文件前缀
	 * @param tempFileSuffix 临时文件后缀
	 * @param tmpDir 临时文件路径
	 * @return 临时文件，如果失败则返回null
	 * @throws IOException
	 */
	public static File file2TempFile(String sourceFileFullName, String tempFilePrefix, String tempFileSuffix, File tmpDir)
			throws IOException {
		File tempFile = File.createTempFile(tempFilePrefix, tempFileSuffix, tmpDir);
		boolean result=fileToFile(sourceFileFullName, tempFile.getPath());
		if(result){
			return tempFile;
		}
		tempFile.delete();
		return null;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午8:59:50
	 * @version 1.0
	 * @description ，如果字符集为空则默认为UTF-8
	 * @param outputStream 
	 * @param charsetName 
	 * @return OutputStreamWriter
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月16日 下午2:15:34
	 * @version 1.0
	 * @description 将字符输出流包装成字节输出流 
	 * @param outputStream 字符输出流
	 * @param charset 字符集 {@linkplain StandardCharsets}
	 * @return {@linkplain OutputStreamWriter}
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static OutputStreamWriter getOutputStreamWriter(OutputStream outputStream,Charset charset) {
		OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream, charset);
		return outputStreamWriter;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:01:43
	 * @version 1.0
	 * @description 将文件包装成字节输出流，如果字符集为空则默认为UTF-8 
	 * @param file 文件
	 * @param charset 字符集{@linkplain Charset}
	 * @return OutputStreamWriter
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static OutputStreamWriter getOutputStreamWriter(File file,Charset charset) throws FileNotFoundException {
		FileOutputStream fileOutputStream=new FileOutputStream(file);
		OutputStreamWriter outputStreamWriter=getOutputStreamWriter(fileOutputStream, charset);
		return outputStreamWriter;
	}
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:02:21
	 * @version 1.0
	 * @description 将文件包装成带缓存的字节输出流，如果字符集为空则默认为UTF-8  
	 * @param file 文件
	 * @param charset 字符集 {@linkplain Charset}
	 * @return BufferedWriter
	 * @throws FileNotFoundException
	 */
	public static BufferedWriter getBufferedWriter(File file,Charset charset) throws FileNotFoundException {
		OutputStreamWriter outputStreamWriter=getOutputStreamWriter(file,charset);
		BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
		return bufferedWriter;
	}
}
