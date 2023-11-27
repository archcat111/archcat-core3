package org.cat.core.util3.http.httpclient;

import java.io.File;
import java.util.Map;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.cat.core.util3.http.HttpResponse;
import org.cat.core.util3.http.util.HttpParamUtil;

/**
 * 
 * @author 王云龙
 * @date 2021年7月21日 上午11:27:05
 * @version 1.0
 * @description HTTP发送工具包
 *
 */
public class HttpSendUtil extends BaseSendUtil{
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午2:20:59
	 * @version 1.0
	 * @description 下载文件  
	 * @param httpUrl HTTP URL
	 * @param fileFullName 文件保存的全路径
	 * @param overwrite 如果文件存在，是否删除重新生成
	 * @return File
	 */
	public static File download(String httpUrl, String fileFullName, boolean overwrite) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		HttpGet httpGet = new HttpGet(httpUrl);
		return download(httpClientBuilder, httpGet, fileFullName, overwrite);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午2:22:37
	 * @version 1.0
	 * @description 下载文件 
	 * @param httpUrl HTTP URL
	 * @param file 要保存下载资源的file
	 * @param overwrite 如果文件存在，是否删除重新生成
	 * @return File
	 */
	public static File download(String httpUrl, File file, boolean overwrite) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		HttpGet httpGet = new HttpGet(httpUrl);
		return download(httpClientBuilder, httpGet, file.getPath(), overwrite);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:49:55
	 * @version 1.0
	 * @description 上传文件 
	 * @param httpUrl 上传文件的url
	 * @param file 需要上传的文件
	 * @param headers header
	 * @param textParams 需要上传的附加元数据（包括文件名等）
	 * @return
	 */
	public static HttpResponse upload(String httpUrl, File file, Map<String, String> headers,
			Map<String, String> textParams) {
		
		HttpPost httpPost=createUploadHttpPost(httpUrl, file, headers, textParams);
		HttpResponse httpResponse = sendHttp(httpPost);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月21日 上午11:30:15
	 * @version 1.0
	 * @description 发送Http请求的核心方法 
	 * @param httpRequestBase 如：HttpGet、HttpPost等
	 * @return {@linkplain HttpResponse}
	 */
	public static HttpResponse sendHttp(HttpRequestBase httpRequestBase) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		HttpResponse httpResponse = send(httpClientBuilder, httpRequestBase);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:52:56
	 * @version 1.0
	 * @description 发送POST请求 
	 * @param httpUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public static HttpResponse sendHttpPostContent(String httpUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPost httpPost = new HttpPost(httpUrl);
		ceateHeader(httpPost, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPost.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttp(httpPost);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送post请求（不传递header）
	 * @param httpUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public static HttpResponse sendHttpPostContent(String httpUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpPostContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:51:38
	 * @version 1.0
	 * @description 发送post请求（不传递header） 
	 * @param httpUrl url
	 * @param contentStr 默认为application/x-www-form-urlencoded
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public  static HttpResponse sendHttpPostContent(String httpUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpPostContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送post请求，传递JSON
	 * @param httpUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPostJson(String httpUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonContentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpPostContent(httpUrl, headers,contentJsonStr, jsonContentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送post请求，传递json（不传递header） 
	 * @param httpUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPostJson(String httpUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpPostJson(httpUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:21:03
	 * @version 1.0
	 * @description 发送post请求，传递Form
	 * 			默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @param httpUrl HTTP URL
	 * @param headers
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPostKeyValue(String httpUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPost httpPost = new HttpPost(httpUrl);
		ceateHeader(httpPost, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPost.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttp(httpPost);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:09:55
	 * @version 1.0
	 * @description 发送post请求，传递Form（不传递header） 
	 * 			默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @param httpUrl HTTP URL
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPostKeyValue(String httpUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpPostKeyValue(httpUrl, headers, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:52:58
	 * @version 1.0
	 * @description 发送post请求（不传递header）  
	 * 			默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @param httpUrl url
	 * @return
	 */
	public static HttpResponse sendHttpPost(String httpUrl) {
		Map<String, String> params = null;
		return sendHttpPostKeyValue(httpUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:55:08
	 * @version 1.0
	 * @description 发送PUT请求 
	 * @param httpUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpPutContent(String httpUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPut httpPut = new HttpPut(httpUrl);
		ceateHeader(httpPut, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPut.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttp(httpPut);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送put请求（不传递header）
	 * @param httpUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpPutContent(String httpUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpPutContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:52:43
	 * @version 1.0
	 * @description 发送put请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded
	 * 		{@linkplain ContentType}
	 * @param httpUrl url
	 * @param contentStr 发送的内容
	 * @return
	 */
	public static HttpResponse sendHttpPutContent(String httpUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpPutContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送put请求 
	 * @param httpUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPutJson(String httpUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonCcontentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpPutContent(httpUrl, headers,contentJsonStr, jsonCcontentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送put请求（不传递header） 
	 * @param httpUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPutJson(String httpUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpPutJson(httpUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:35:12
	 * @version 1.0
	 * @description 发送put请求  
	 * 		contentType默认为application/x-www-form-urlencoded
	 * 		{@linkplain ContentType}
	 * @param httpUrl url
	 * @param headers header
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPutKeyValue(String httpUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPut httpPut = new HttpPut(httpUrl);
		ceateHeader(httpPut, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPut.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttp(httpPut);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:09:55
	 * @version 1.0
	 * @description 发送put请求（不传递header） 
	 * 			contentType默认为application/x-www-form-urlencoded
	 * 			{@linkplain ContentType}
	 * @param httpUrl url
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPutKeyValue(String httpUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpPutKeyValue(httpUrl, headers, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:52:58
	 * @version 1.0
	 * @description 发送put请求（不传递header）  
	 * 			contentType默认为application/x-www-form-urlencoded
	 * 			{@linkplain ContentType}
	 * @param httpUrl url
	 * @return
	 */
	public static HttpResponse sendHttpPut(String httpUrl) {
		Map<String, String> params = null;
		return sendHttpPutKeyValue(httpUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:55:08
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * @param httpUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpPatchContent(String httpUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPatch httpPatch = new HttpPatch(httpUrl);
		ceateHeader(httpPatch, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPatch.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttp(httpPatch);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header）
	 * @param httpUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpPatchContent(String httpUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpPatchContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:53:18
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 		{@linkplain ContentType}
	 * @param httpUrl url
	 * @param contentStr 发送的内容
	 * @return
	 */
	public static HttpResponse sendHttpPatchContent(String httpUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpPatchContent(httpUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * @param httpUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPatchJson(String httpUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonCcontentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpPatchContent(httpUrl, headers,contentJsonStr, jsonCcontentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * @param httpUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpPatchJson(String httpUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpPatchJson(httpUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:07:37
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * 		contentType默认为application/x-www-form-urlencoded， {@linkplain ContentType}
	 * @param httpUrl url
	 * @param headers header
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPatchKeyValue(String httpUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPatch httpPatch = new HttpPatch(httpUrl);
		ceateHeader(httpPatch, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPatch.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttp(httpPatch);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:09:55
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded， {@linkplain ContentType}
	 * @param httpUrl url
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpPatchKeyValue(String httpUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpPatchKeyValue(httpUrl, headers, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:52:58
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header）  
	 * 			contentType默认为application/x-www-form-urlencoded， {@linkplain ContentType}
	 * @param httpUrl url
	 * @return
	 */
	public static HttpResponse sendHttpPatch(String httpUrl) {
		Map<String, String> params = null;
		return sendHttpPatchKeyValue(httpUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:54:08
	 * @version 1.0
	 * @description 发送get请求 
	 * @param httpUrl URL
	 * @param headers header
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpGetKeyValue(String httpUrl, Map<String, String> headers, Map<String, String> params) {
		String newHttpUrl=HttpParamUtil.urlAddParam(httpUrl, params);
		HttpGet httpGet = new HttpGet(newHttpUrl);
		ceateHeader(httpGet, headers);
		HttpResponse httpResponse = sendHttp(httpGet);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:51:10
	 * @version 1.0
	 * @description 发送get请求（不传递header）  
	 * @param httpUrl URL
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpGetKeyValue(String httpUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpGetKeyValue(httpUrl, headers, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:52:05
	 * @version 1.0
	 * @description 发送get请求（不传递header）   
	 * @param httpUrl URL
	 * @return
	 */
	public static HttpResponse sendHttpGet(String httpUrl) {
		Map<String, String> params = null;
		return sendHttpGetKeyValue(httpUrl, params);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月22日 上午11:58:14
	 * @version 1.0
	 * @description 发送delete请求
	 *
	 * @param httpUrl URL
	 * @param headers header
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpDeleteKeyValue(String httpUrl, Map<String, String> headers, Map<String, String> params) {
		String newHttpUrl=HttpParamUtil.urlAddParam(httpUrl, params);
		HttpDelete httpDelete = new HttpDelete(newHttpUrl);
		ceateHeader(httpDelete, headers);
		HttpResponse httpResponse = sendHttp(httpDelete);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:34:47
	 * @version 1.0
	 * @description 发送delete请求（不传递header）  
	 * @param httpUrl url
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpDeleteKeyValue(String httpUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		HttpResponse httpResponse = sendHttpDeleteKeyValue(httpUrl, headers, params);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:35:29
	 * @version 1.0
	 * @description 发送不带参数的delete请求 
	 * @param httpUrl url
	 * @return
	 */
	public static HttpResponse sendHttpDelete(String httpUrl) {
		Map<String, String> params = null;
		return sendHttpDeleteKeyValue(httpUrl, params);
	}
	
}
	

	
