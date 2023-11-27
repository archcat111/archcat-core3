package org.cat.core.web3.resp.generator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午3:59:25
 * @version 1.0
 * @description 输出（下载）的标准接口
 *
 */
public interface IDownloadGenerator {

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午4:35:23
	 * @version 1.0
	 * @description 用于提供下载文件的服务类使用，使其能够提供给客户端传输文件二进制数据的能力  
	 * @param file 需要给客户端发送的文件对象
	 * @param fileName 需要给客户端展示的文件名称
	 * @return {@linkplain ResponseEntity}
	 * @throws IOException
	 */
	public ResponseEntity<?> downloadFile(File file, String fileName) throws IOException;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午4:35:05
	 * @version 1.0
	 * @description 用于提供下载文件的服务类使用，使其能够提供给客户端传输文件二进制数据的能力
	 * 		注意：需要您自行择机关闭byteArrayOutputStream   
	 * @param byteArrayOutputStream 需要给客户端发送的文件对象
	 * @param fileName 需要给客户端展示的文件名称
	 * @param charset 字符集名称{@code Charset}
	 * @return {@linkplain ResponseEntity}
	 * @throws IOException
	 */
	public ResponseEntity<?> downloadFile(ByteArrayOutputStream byteArrayOutputStream, String fileName,Charset charset) throws IOException;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午4:34:53
	 * @version 1.0
	 * @description 用于提供下载文件的服务类使用，使其能够提供给客户端传输文件二进制数据的能力 
	 * 		注意：需要您自行择机关闭byteArrayOutputStream
	 * @param byteArrayOutputStream 需要给客户端发送的文件对象
	 * @param fileName 需要给客户端展示的文件名称
	 * @return {@linkplain ResponseEntity}
	 * @throws IOException
	 */
	public ResponseEntity<?> downloadFile(ByteArrayOutputStream byteArrayOutputStream, String fileName) throws IOException;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午4:34:19
	 * @version 1.0
	 * @description 用于提供下载二进制流的能力（将输入流直接作为输出流） 
	 * 		注意：需要您自行择机关闭inputStream和httpServletResponse.getOutputStream()的OutputStream
	 * @param inputStream 输入流
	 * @param httpServletResponse {@linkplain HttpServletResponse}
	 * @throws IOException
	 */
	public void downloadStream(InputStream inputStream,HttpServletResponse httpServletResponse) throws IOException;
	
}
