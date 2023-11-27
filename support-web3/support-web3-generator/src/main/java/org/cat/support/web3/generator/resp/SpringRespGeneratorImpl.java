package org.cat.support.web3.generator.resp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.core.web3.util.ResultRespUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Setter;

/**
 * 
 * @author wangyunlong
 * @date 2018年8月24日 下午3:06:46
 * @version 1.0
 * @description 基于HttpServletResponse的PrintWriter进行输出控制标准接口的标准实现
 *
 */
public class SpringRespGeneratorImpl implements ISpringRespGenerator {
	
	@Setter private IExceptionIdGenerator exceptionIdGenerator;
	@Setter private IExceptionCodeGenerator exceptionCodeGenerator;

	@Override
	public <T> void doResponse(HttpServletResponse httpServletResponse, HttpStatus httpStatus, Throwable throwable, T businessRespBean) throws IOException {
		doResponse(httpServletResponse, httpStatus, null, throwable, businessRespBean);
		
	}
	
	@Override
	public <T> void doResponse(HttpServletResponse httpServletResponse, HttpStatus httpStatus, String contentType, Throwable throwable, T businessRespBean) throws IOException {
		ArchResultResp<T> resultResp=ResultRespUtil.createResultRespAndDoThrowable(exceptionIdGenerator, exceptionCodeGenerator, throwable);
		resultResp.setResponse(businessRespBean);
		
		httpServletResponse.setStatus(httpStatus.value());
		if(StringUtils.isNotBlank(contentType)) {
			httpServletResponse.setContentType(contentType);
		}
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		PrintWriter printWriter=httpServletResponse.getWriter();
		printWriter.write(ArchJsonUtil.toJson(resultResp));
		printWriter.flush();  
		printWriter.close();  
	}
	
	@Override
	public <T> ResponseEntity<ArchResultResp<T>> doResponse(HttpStatus httpStatus, Throwable throwable, T businessRespBean) {
		ArchResultResp<T> resultResp=ResultRespUtil.createResultRespAndDoThrowable(exceptionIdGenerator, exceptionCodeGenerator, throwable);
		resultResp.setResponse(businessRespBean);
		return ResponseEntity.status(httpStatus).body(resultResp);
	}
	
}
