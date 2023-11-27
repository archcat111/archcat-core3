package org.cat.support.web3.generator.resp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cat.core.util3.string.ArchCharsets2;
import org.cat.core.web3.resp.generator.IDownloadGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午3:59:10
 * @version 1.0
 * @description 输出（下载）的标准接口的标准实现类
 *
 */
public class DownloadGeneratorImpl implements IDownloadGenerator {

	@Override
	public ResponseEntity<?> downloadFile(File file, String fileName) throws IOException {
		String downloadFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", downloadFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> downloadFile(ByteArrayOutputStream byteArrayOutputStream, String fileName,
			Charset charset) throws IOException {
		String downloadFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", downloadFileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//		byte[] baosStrByte=byteArrayOutputStream.toByteArray();
		String baosStr=byteArrayOutputStream.toString(charset.name());
		byte[] baosStrByte=baosStr.getBytes(charset.name());
		
		return new ResponseEntity<byte[]>(baosStrByte, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> downloadFile(ByteArrayOutputStream byteArrayOutputStream, String fileName) throws IOException {
		return downloadFile(byteArrayOutputStream, fileName, ArchCharsets2.GB_2312);
	}

	@Override
	public void downloadStream(InputStream inputStream, HttpServletResponse httpServletResponse) throws IOException {
		OutputStream outputStream=httpServletResponse.getOutputStream();
		IOUtils.copy(inputStream, outputStream);
		outputStream.flush();
	}

}
