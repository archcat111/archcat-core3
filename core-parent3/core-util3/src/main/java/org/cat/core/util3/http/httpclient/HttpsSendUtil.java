package org.cat.core.util3.http.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.cat.core.util3.http.HttpConstants;
import org.cat.core.util3.http.HttpResponse;
import org.cat.core.util3.http.util.HttpParamUtil;

public class HttpsSendUtil extends BaseSendUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月21日 上午11:35:56
	 * @version 1.0
	 * @description 发送Https请求的核心方法 
	 * @param sslConnectionSocketFactory SSL连接Socket工厂
	 * @param httpRequestBase 如：HttpGet、HttpPost等
	 * @return
	 */
	public static HttpResponse sendHttps(SSLConnectionSocketFactory sslConnectionSocketFactory, HttpRequestBase httpRequestBase) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
		HttpResponse httpResponse = send(httpClientBuilder, httpRequestBase);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:17:37
	 * @version 1.0
	 * @description 发送https请求的基础方法 
	 * @param sslContext
	 * @param supportedProtocols
	 * @param supportedCipherSuites
	 * @param hostnameVerifier
	 * @param httpRequestBase HttpGet、HttpPost等
	 * @return
	 */
	protected static HttpResponse sendHttps(
			SSLContext sslContext, 
			String[] supportedProtocols,
			String[] supportedCipherSuites, 
			HostnameVerifier hostnameVerifier, 
			HttpRequestBase httpRequestBase) {
		//sslContext & supportedProtocols & supportedCipherSuites & hostnameVerifier --> sslConnectionSocketFactory
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
				sslContext,
				supportedProtocols, 
				supportedCipherSuites, 
				hostnameVerifier);
		HttpResponse httpResponse = sendHttps(sslConnectionSocketFactory, httpRequestBase);
		return httpResponse;
	}
	
	protected static HttpResponse sendHttps(
			KeyStore keyStore, 
			TrustStrategy trustStrategy, 
			String[] supportedProtocols,
			String[] supportedCipherSuites, 
			HostnameVerifier hostnameVerifier, 
			HttpRequestBase httpRequestBase) {
		
		//keyStore & trustStrategy --> sslContext
		SSLContext sslContext = null;
			try {
				if (keyStore != null) {
					sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, trustStrategy).build();
				} else {
					sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).build();
				}
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
				e.printStackTrace();
				return HttpResponse.createExceptionResp(e.getClass(), e.getMessage());
			}
		HttpResponse httpResponse = sendHttps(
				sslContext, 
				supportedProtocols, 
				supportedCipherSuites, 
				hostnameVerifier,
				httpRequestBase);
		return httpResponse;
	}
	
	protected static HttpResponse sendHttps(
			String keyStoreType, 
			File keyStoreFile, 
			String keyStorePassword,
			TrustStrategy trustStrategy, 
			String[] supportedProtocols, 
			String[] supportedCipherSuites,
			HostnameVerifier hostnameVerifier, 
			HttpRequestBase httpRequestBase) {
		// 加载keystore证书文件和密码
		//keyStoreType & keyStoreFile & keyStorePassword -->keyStore
		KeyStore keyStore = null;
		if(keyStoreType!=null && keyStoreFile!=null&&keyStorePassword!=null){
			try {
				keyStore = KeyStore.getInstance(keyStoreType);
				FileInputStream fileInputStream = new FileInputStream(keyStoreFile);
				keyStore.load(fileInputStream, keyStorePassword.toCharArray());
			} catch (Exception e) {
				e.printStackTrace();
				return HttpResponse.createExceptionResp(e.getClass(), e.getMessage());
			}
		}
		
		HttpResponse httpResponse = sendHttps(
				keyStore, 
				trustStrategy, 
				supportedProtocols, 
				supportedCipherSuites,
				hostnameVerifier, 
				httpRequestBase);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月5日 下午5:35:40
	 * @version 1.0
	 * @description 发送HTTPS请求，需要提供keyStoreFilePath以及keyStorePassword 
	 * 			keyStoreType：{@linkplain KeyStore#getDefaultType()}
	 * 			trustStrategy：{@linkplain HttpsSendUtil#getTrustStrategy4Default()}、{@linkplain HttpsSendUtil#getTrustStrategy4SelfSigned()}
	 * 			supportedProtocols：{@linkplain HttpConstants.Https#TSLv1}
	 * 			supportedCipherSuites：null
	 * 			hostnameVerifier：{@linkplain HttpsSendUtil#getHostnameVerifier4Default()}
	 * @param keyStoreFilePath 证书文件路径
	 * @param keyStorePassword 证书密码
	 * @param httpRequestBase HttpGet、HttpPost等
	 * @return {@linkplain HttpResponse}
	 */
	protected static HttpResponse sendHttpsBase(
			String keyStoreFilePath, 
			String keyStorePassword,
			HttpRequestBase httpRequestBase) {
		
		String keyStoreType = null;
		File keyStoreFile = null;
		if(keyStoreFilePath!=null){
			keyStoreType = KeyStore.getDefaultType();
			keyStoreFile = new File(keyStoreFilePath);
		}
		
		TrustStrategy trustStrategy = getTrustStrategy4Default();
		
		String[] supportedProtocols = new String[] { HttpConstants.Https.TSLv1 };
		String[] supportedCipherSuites = null;
		HostnameVerifier hostnameVerifier = getHostnameVerifier4Default();
		
		HttpResponse httpResponse = sendHttps(
				keyStoreType, 
				keyStoreFile, 
				keyStorePassword, 
				trustStrategy,
				supportedProtocols, 
				supportedCipherSuites, 
				hostnameVerifier, 
				httpRequestBase);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月5日 下午5:50:52
	 * @version 1.0
	 * @description 发送HTTPS请求，需要提供keyStoreFilePath以及keyStorePassword 
	 * 			keyStoreType：{@linkplain KeyStore#getDefaultType()}
	 * 			keyStoreFile：null
	 * 			keyStorePassword：null
	 * 			trustStrategy：{@linkplain HttpsSendUtil#getTrustStrategy4Default()}、{@linkplain HttpsSendUtil#getTrustStrategy4SelfSigned()}
	 * 			supportedProtocols：{@linkplain HttpConstants.Https#TSLv1}
	 * 			supportedCipherSuites：null
	 * 			hostnameVerifier：{@linkplain HttpsSendUtil#getHostnameVerifier4Default()}
	 * @param httpRequestBase HttpGet、HttpPost等
	 * @return {@linkplain HttpResponse}
	 */
	protected static HttpResponse sendHttpsBase(HttpRequestBase httpRequestBase) {
		
		String keyStoreFilePath = null; 
		String keyStorePassword = null;
		
		HttpResponse httpResponse = sendHttpsBase(
				keyStoreFilePath, 
				keyStorePassword, 
				httpRequestBase);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:22:19
	 * @version 1.0
	 * @description 主机名验证规则（允许所有） 
	 * @return
	 */
	protected static HostnameVerifier getHostnameVerifier4AllAllow() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		return hostnameVerifier;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:29:53
	 * @version 1.0
	 * @description 主机名验证规则（默认）（基于默认证书验证） 
	 * @return
	 */
	protected static HostnameVerifier getHostnameVerifier4Default() {
		HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
		return hostnameVerifier;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:30:18
	 * @version 1.0
	 * @description 主机名验证规则（基于指定URI，内部逻辑基于默认证书验证）  
	 * @param uri 需要进行主机名验证规则的URI
	 * @return
	 */
	protected static HostnameVerifier getHostnameVerifier4Uri(String uri) {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) {
				HostnameVerifier hostnameVerifier = getHostnameVerifier4Default();
				boolean result=hostnameVerifier.verify(uri, session);
				return result;
			}
		};
		return hostnameVerifier;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:44:18
	 * @version 1.0
	 * @description 证书信任策略（信任所有）  
	 * @return
	 */
	protected static TrustStrategy getTrustStrategy4AllAllow() {
		TrustStrategy trustStrategy = new TrustStrategy() {

			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		};
		return trustStrategy;
	}
	

	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:43:51
	 * @version 1.0
	 * @description 证书信任策略（自签证书信任） 
	 * @return
	 */
	protected static TrustStrategy getTrustStrategy4SelfSigned() {
		TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
		return trustStrategy;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午10:59:31
	 * @version 1.0
	 * @description 证书信任策略（默认）（自签证书信任）
	 * @return
	 */
	protected static TrustStrategy getTrustStrategy4Default() {
		return getTrustStrategy4SelfSigned();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月30日 下午3:02:18
	 * @version 1.0
	 * @description 下载文件  
	 * @param sslConnectionSocketFactory
	 * @param httpsUrl HTTPS URL
	 * @param fileFullName 文件保存的全路径
	 * @param overwrite 如果文件存在，是否删除重新生成
	 * @return File
	 */
	public static File download(SSLConnectionSocketFactory sslConnectionSocketFactory, String httpsUrl, String fileFullName, boolean overwrite) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
		HttpGet httpGet = new HttpGet(httpsUrl);
		return download(httpClientBuilder, httpGet, fileFullName, overwrite);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月30日 下午3:05:28
	 * @version 1.0
	 * @description 下载文件 
	 * @param sslConnectionSocketFactory
	 * @param httpsUrl HTTPS URL
	 * @param file 要保存下载资源的file
	 * @param overwrite 如果文件存在，是否删除重新生成
	 * @return File
	 */
	public static File download(SSLConnectionSocketFactory sslConnectionSocketFactory, String httpsUrl, File file, boolean overwrite) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
		HttpGet httpGet = new HttpGet(httpsUrl);
		return download(httpClientBuilder, httpGet, file.getPath(), overwrite);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月30日 下午3:13:54
	 * @version 1.0
	 * @description 上传文件，具体实现暂时没有考虑HTTPS，{@linkplain HttpSendUtil#upload(String, File, Map, Map)} 
	 * @param httpsUrl 上传文件的url
	 * @param file 需要上传的文件
	 * @param headers header
	 * @param textParams 需要上传的附加元数据（包括文件名等）
	 * @return
	 */
	@Deprecated
	public static HttpResponse upload(String httpsUrl, File file, Map<String, String> headers,
			Map<String, String> textParams) {
		HttpResponse httpResponse = HttpSendUtil.upload(httpsUrl, file, headers, textParams);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:52:56
	 * @version 1.0
	 * @description 发送POST请求 
	 * 		{@linkplain HttpsSendUtil#sendHttpsBase(HttpRequestBase)}
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public static HttpResponse sendHttpsPostContent(String httpsUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPost httpPost = new HttpPost(httpsUrl);
		ceateHeader(httpPost, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPost.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPost);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送post请求（不传递header）
	 * 		{@linkplain HttpsSendUtil#sendHttpsBase(HttpRequestBase)}
	 * @param httpsUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public static HttpResponse sendHttpsPostContent(String httpsUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpsPostContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:51:38
	 * @version 1.0
	 * @description 发送post请求（不传递header） 
	 * 		{@linkplain HttpsSendUtil#sendHttpsBase(HttpRequestBase)}
	 * @param httpsUrl url
	 * @param contentStr 默认为application/x-www-form-urlencoded
	 * 			{@linkplain ContentType}
	 * @return HttpResponse
	 */
	public  static HttpResponse sendHttpsPostContent(String httpsUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpsPostContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送post请求，传递JSON
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPostJson(String httpsUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonContentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpsPostContent(httpsUrl, headers,contentJsonStr, jsonContentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送post请求，传递json（不传递header） 
	 * @param httpsUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPostJson(String httpsUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpsPostJson(httpsUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:21:03
	 * @version 1.0
	 * @description 发送post请求，传递Form
	 * 			默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @param httpsUrl HTTP URL
	 * @param headers
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPostKeyValue(String httpsUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPost httpPost = new HttpPost(httpsUrl);
		ceateHeader(httpPost, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPost.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPost);
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
	 * @param httpsUrl HTTP URL
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPostKeyValue(String httpsUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpsPostKeyValue(httpsUrl, headers, params);
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
	public static HttpResponse sendHttpsPost(String httpsUrl) {
		Map<String, String> params = null;
		return sendHttpsPostKeyValue(httpsUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:55:08
	 * @version 1.0
	 * @description 发送PUT请求 
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpsPutContent(String httpsUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPut httpPut = new HttpPut(httpsUrl);
		ceateHeader(httpPut, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPut.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPut);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送put请求（不传递header）
	 * @param httpsUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpsPutContent(String httpsUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpsPutContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:52:43
	 * @version 1.0
	 * @description 发送put请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded
	 * 		{@linkplain ContentType}
	 * @param httpsUrl url
	 * @param contentStr 发送的内容
	 * @return
	 */
	public static HttpResponse sendHttpsPutContent(String httpsUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpsPutContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送put请求 
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPutJson(String httpsUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonCcontentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpsPutContent(httpsUrl, headers,contentJsonStr, jsonCcontentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送put请求（不传递header） 
	 * @param httpsUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPutJson(String httpsUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpsPutJson(httpsUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:35:12
	 * @version 1.0
	 * @description 发送put请求  
	 * 		contentType默认为application/x-www-form-urlencoded
	 * 		{@linkplain ContentType}
	 * @param httpsUrl url
	 * @param headers header
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPutKeyValue(String httpsUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPut httpPut = new HttpPut(httpsUrl);
		ceateHeader(httpPut, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPut.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPut);
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
	 * @param httpsUrl url
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPutKeyValue(String httpsUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpsPutKeyValue(httpsUrl, headers, params);
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
	public static HttpResponse sendHttpsPut(String httpsUrl) {
		Map<String, String> params = null;
		return sendHttpsPutKeyValue(httpsUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:55:08
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpsPatchContent(String httpsUrl, Map<String, String> headers,String contentStr, String contentType) {
		HttpPatch httpPatch = new HttpPatch(httpsUrl);
		ceateHeader(httpPatch, headers);
		StringEntity stringEntity = createBodyStringEntity(contentStr, contentType);
		httpPatch.setEntity(stringEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPatch);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午8:53:57
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header）
	 * @param httpsUrl url
	 * @param contentStr 发送的内容
	 * @param contentType 传递null默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 			{@linkplain ContentType}
	 * @return
	 */
	public static HttpResponse sendHttpsPatchContent(String httpsUrl, String contentStr, String contentType) {
		Map<String, String> headers = null;
		return sendHttpsPatchContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午11:53:18
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded，否则为用户自己的设置
	 * 		{@linkplain ContentType}
	 * @param httpsUrl url
	 * @param contentStr 发送的内容
	 * @return
	 */
	public static HttpResponse sendHttpsPatchContent(String httpsUrl, String contentStr) {
		Map<String, String> headers = null;
		String contentType = null;
		return sendHttpsPatchContent(httpsUrl,headers,contentStr,contentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:26:07
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * @param httpsUrl url
	 * @param headers header
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPatchJson(String httpsUrl, Map<String, String> headers,String contentJsonStr) {
		String jsonCcontentType=ContentType.APPLICATION_JSON.getMimeType();
		return sendHttpsPatchContent(httpsUrl, headers,contentJsonStr, jsonCcontentType);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:11:49
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * @param httpsUrl url
	 * @param contentJsonStr json字符串
	 * @return
	 */
	public static HttpResponse sendHttpsPatchJson(String httpsUrl, String contentJsonStr) {
		Map<String, String> headers = null;
		return sendHttpsPatchJson(httpsUrl, headers, contentJsonStr);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:07:37
	 * @version 1.0
	 * @description 发送PATCH请求 
	 * 		contentType默认为application/x-www-form-urlencoded， {@linkplain ContentType}
	 * @param httpsUrl url
	 * @param headers header
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPatchKeyValue(String httpsUrl, Map<String, String> headers, Map<String, String> params) {
		HttpPatch httpPatch = new HttpPatch(httpsUrl);
		ceateHeader(httpPatch, headers);
		UrlEncodedFormEntity urlEncodedFormEntity = createUrlEncodedFormEntity(params);
		httpPatch.setEntity(urlEncodedFormEntity);
		HttpResponse httpResponse = sendHttpsBase(httpPatch);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:09:55
	 * @version 1.0
	 * @description 发送PATCH请求（不传递header） 
	 * 		contentType默认为application/x-www-form-urlencoded， {@linkplain ContentType}
	 * @param httpsUrl url
	 * @param params 需要传递的key、value
	 * @return
	 */
	public static HttpResponse sendHttpsPatchKeyValue(String httpsUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpsPatchKeyValue(httpsUrl, headers, params);
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
	public static HttpResponse sendHttpsPatch(String httpsUrl) {
		Map<String, String> params = null;
		return sendHttpsPatchKeyValue(httpsUrl, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月29日 下午5:54:08
	 * @version 1.0
	 * @description 发送get请求 
	 * @param httpsUrl URL
	 * @param headers header
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpsGetKeyValue(String httpsUrl, Map<String, String> headers, Map<String, String> params) {
		String newHttpUrl=HttpParamUtil.urlAddParam(httpsUrl, params);
		HttpGet httpGet = new HttpGet(newHttpUrl);
		ceateHeader(httpGet, headers);
		HttpResponse httpResponse = sendHttpsBase(httpGet);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:51:10
	 * @version 1.0
	 * @description 发送get请求（不传递header）  
	 * @param httpsUrl URL
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpsGetKeyValue(String httpsUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		return sendHttpsGetKeyValue(httpsUrl, headers, params);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:52:05
	 * @version 1.0
	 * @description 发送get请求（不传递header）   
	 * @param httpsUrl URL
	 * @return
	 */
	public static HttpResponse sendHttpGet(String httpsUrl) {
		Map<String, String> params = null;
		return sendHttpsGetKeyValue(httpsUrl, params);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月22日 上午11:58:14
	 * @version 1.0
	 * @description 发送delete请求
	 *
	 * @param httpsUrl URL
	 * @param headers header
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpsDeleteKeyValue(String httpsUrl, Map<String, String> headers, Map<String, String> params) {
		String newHttpUrl=HttpParamUtil.urlAddParam(httpsUrl, params);
		HttpDelete httpDelete = new HttpDelete(newHttpUrl);
		ceateHeader(httpDelete, headers);
		HttpResponse httpResponse = sendHttpsBase(httpDelete);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:34:47
	 * @version 1.0
	 * @description 发送delete请求（不传递header）  
	 * @param httpsUrl url
	 * @param params 参数（会自动拼接在HRL后面）
	 * @return
	 */
	public static HttpResponse sendHttpsDeleteKeyValue(String httpsUrl, Map<String, String> params) {
		Map<String, String> headers = null;
		HttpResponse httpResponse = sendHttpsDeleteKeyValue(httpsUrl, headers, params);
		return httpResponse;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月12日 下午9:35:29
	 * @version 1.0
	 * @description 发送不带参数的delete请求 
	 * @param httpsUrl url
	 * @return
	 */
	public static HttpResponse sendHttpsDelete(String httpsUrl) {
		Map<String, String> params = null;
		return sendHttpsDeleteKeyValue(httpsUrl, params);
	}
	
}
