package org.cat.core.util3.http.httpclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.cat.core.util3.file.ArchFileUtil;
import org.cat.core.util3.file.ArchIOUtil;
import org.cat.core.util3.http.HttpConstants;
import org.cat.core.util3.http.HttpResponse;

/**
 * 
 * @author 王云龙
 * @date 2021年7月21日 上午11:32:04
 * @version 1.0
 * @description HTTP发送工具基类（HttpClient实现）,不对外公布，只有本包可用
 *
 */
class BaseSendUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月21日 上午11:29:05
	 * @version 1.0
	 * @description 发送Http请求的核心方法
	 * @param httpClientBuilder 通过httpClientBuilder.build()创建
	 * @param httpRequestBase 如：HttpGet、HttpPost等
	 * @return
	 */
	protected static HttpResponse send(HttpClientBuilder httpClientBuilder, HttpRequestBase httpRequestBase) {
		
		int status = HttpConstants.Status.NOT_WRITE;
		String responseContent = HttpConstants.Content.NOT_WRITE;
		Class<?> exceptionClass = HttpConstants.ExceptionClass.NOT_WRITE;
		String exceptionMsg = HttpConstants.ExceptionMsg.NOT_WRITE;
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		try{
			CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpRequestBase);
			HttpEntity httpEntity = closeableHttpResponse.getEntity();
			responseContent = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
			status = closeableHttpResponse.getStatusLine().getStatusCode();
		} catch (IOException e) {
			e.printStackTrace();
			return HttpResponse.createExceptionResp(e.getClass(), e.getMessage());
		}
		
		HttpResponse httpResponse = HttpResponse.createNotWriteHttpResponse();
		httpResponse.setStatus(status);
		httpResponse.setContent(responseContent);
		httpResponse.setExceptionClass(exceptionClass);
		httpResponse.setExceptionMsg(exceptionMsg);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午10:44:19
	 * @version 1.0
	 * @description 下载文件资源
	 * @param httpRequestBase HttpGet、HttpPost等
	 * @param fileFullName 保存文件的全路径
	 * @param overwrite 如果文件已经存在，是否删除重新生成
	 * @return 成功返回file，失败返回null
	 */
	protected static File download(HttpClientBuilder httpClientBuilder,  HttpRequestBase httpRequestBase, String fileFullName, boolean overwrite) {
		int status = HttpConstants.Status.NOT_WRITE;
		
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		try{
			CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpRequestBase);
			HttpEntity httpEntity = closeableHttpResponse.getEntity();
			status = closeableHttpResponse.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				InputStream inputStream = httpEntity.getContent();
				if (inputStream != null) {
					ArchFileUtil.mkFile(fileFullName, overwrite);
					File file = new File(fileFullName);
					boolean result = ArchIOUtil.inputStreamToFile(inputStream, file, true);
					if (result) {
						return file;
					}
					if (file != null) {
						file.deleteOnExit();
						throw new DownloadException("下载成功，status为"+status+"，但是再调用IOUtil.inputStreamToFile(inputStream, file, true)时返回result为false，已经将该写入文件删除");
					}
				}else {
					//inputStream == null;
				}
			}else {
				throw new DownloadException("下载失败，status为"+status);
			}
		} catch (ClientProtocolException e) {
			throw new DownloadException("下载失败，status为"+status, e);
		} catch (IOException e) {
			throw new DownloadException("下载失败，status为"+status, e);
		}
		return null;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:45:18
	 * @version 1.0
	 * @description 创建一个用于上传的HttpPost 
	 * @param httpUrl 用于上传的url
	 * @param file 需要上传的文件
	 * @param headers 需要上传的header
	 * @param textParams 需要上传的附加元数据（包括文件名等）
	 * @return HttpPost
	 */
	protected static HttpPost createUploadHttpPost(String httpUrl, File file, Map<String, String> headers,
			Map<String, String> textParams){
		HttpPost httpPost = new HttpPost(httpUrl);
		ceateHeader(httpPost, headers);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", file);
		if (textParams != null) {
			for (String key : textParams.keySet()) {
				builder.addTextBody(key, textParams.get(key));
			}
		}
		HttpEntity httpEntity = builder.build();
		httpPost.setEntity(httpEntity);
		
		return httpPost;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月21日 上午11:37:25
	 * @version 1.0
	 * @description 给requestBase设置header 
	 * @param requestBase 如：HttpGet、HttpPost等
	 * @param headers
	 */
	protected static void ceateHeader(HttpRequestBase requestBase, Map<String, String> headers) {
		if (headers != null) {
			for (String key : headers.keySet()) {
				requestBase.addHeader(key, headers.get(key));
			}
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:56:36
	 * @version 1.0
	 * @description 创建一个UrlEncodedFormEntity，用于传递key、value的数据，默认为application/x-www-form-urlencoded
	 * @param params 需要转化为UrlEncodedFormEntity的key、value
	 * @return UrlEncodedFormEntity
	 * @throws UnsupportedEncodingException
	 */
	protected static UrlEncodedFormEntity createUrlEncodedFormEntity(Map<String, String> params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (MapUtils.isNotEmpty(params)) {
			for (String key : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8);
		urlEncodedFormEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
		
		return urlEncodedFormEntity;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:49:31
	 * @version 1.0
	 * @description 创建一个StringEntity，用于POST、GET、PUT等requestBase发送的body里的字符串，默认为UTF-8
	 * @param contentStr 需要发送的字符串内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的
	 * @return StringEntity
	 */
	protected static StringEntity createBodyStringEntity(String contentStr, String contentType) {
		if (StringUtils.isBlank(contentStr)) {
			contentStr = "";
		}
		StringEntity stringEntity = new StringEntity(contentStr, StandardCharsets.UTF_8);
		if (contentType == null) {
			stringEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
		} else {
			stringEntity.setContentType(contentType);
		}
		return stringEntity;
	}
}
