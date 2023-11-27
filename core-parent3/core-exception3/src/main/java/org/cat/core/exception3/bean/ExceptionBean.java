package org.cat.core.exception3.bean;

import java.io.Serializable;

import org.cat.core.exception3.constants.ExceptionConstants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 王云龙
 * @date 2021年7月9日 下午2:42:42
 * @version 1.0
 * @description 统一异常实体信息类
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class ExceptionBean implements Serializable {
	
	private static final long serialVersionUID = -7447689818750410956L;

	/**
	 * exception的Id，每次错误的id理论上都是全局唯一的
	 * 默认值为-1
	 */
	@JsonProperty("exception_id")
	private String exceptionId = ExceptionConstants.ParamValue.ID_NOT_WRITE;
	
	/**
	 * exception发生的项目的Code，用户可以自定义，如：6位字符串 or 唯一的项目名称
	 * 默认值为-1
	 */
	@JsonProperty("exception_project_code")
	private String exceptionProjectCode = ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE;
	
	/**
	 * exception的业务场景Code，该Code在同一个项目中应该全局唯一
	 * 如：“新增用户时，该用户已存在，添加失败”，其定义的exceptionCode可以为：100100
	 * 默认值为-1
	 */
	@JsonProperty("exception_code")
	private String exceptionCode = ExceptionConstants.ParamValue.CODE_NOT_WRITE;
	
	/**
	 * 具体的错误描述
	 * 如：exceptionCode为100100的错误描述为：“新增用户时，该用户已存在，添加失败”
	 * 默认值为"未填写内容"
	 */
	@JsonProperty("exception_msg")
	private String exceptionMsg = ExceptionConstants.ParamValue.EXCEPTION_MSG_NOT_WRITE;

}