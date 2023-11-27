package org.cat.core.web3.resp;

import java.util.Date;

import org.cat.core.exception3.ParentException;
import org.cat.core.exception3.ParentRuntimeException;
import org.cat.core.exception3.bean.ExceptionBean;
import org.cat.core.web3.constants.ResultRespContants;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ArchResultResp<T> extends AbsArchJsonResp {

	private static final long serialVersionUID = 8262436911538688064L;
	
	@JsonProperty("date")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date date=new Date();
	
	/**
	 * {@linkplain ResultRespContants.Status}
	 * {@linkplain ResultRespContants.Status#NORMAL}
	 * {@linkplain ResultRespContants.Status#SYSTEM_EXCEPTION}
	 * {@linkplain ResultRespContants.Status#BUSINESS_EXCEPTION}
	 * {@linkplain ResultRespContants.Status#UNKNOWN}（默认值）
	 */
	@JsonProperty("status")
	private String status=ResultRespContants.Status.UNKNOWN;
	
	@JsonProperty("exception")
	private ExceptionBean exceptionBean;
	
	@JsonProperty("response")
	private T response;
	
	@JsonIgnore
	public void setException(ParentException parentException){
		this.exceptionBean=parentException.getExceptionBean();
	}
	
	@JsonIgnore
	public void setException(ParentRuntimeException parentRuntimeException){
		this.exceptionBean=parentRuntimeException.getExceptionBean();
	}

}
