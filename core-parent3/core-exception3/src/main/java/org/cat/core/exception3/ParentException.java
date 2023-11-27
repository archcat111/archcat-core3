package org.cat.core.exception3;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.exception3.bean.ExceptionBean;
import org.cat.core.util3.json.ArchJsonUtil;

/**
 * 
 * @author 王云龙
 * @date 2021年7月9日 下午2:47:48
 * @version 1.0
 * @description 自定义普通异常的顶层父类
 *
 */
public abstract class ParentException extends Exception {

	private static final long serialVersionUID = 713616801491210431L;
	
	private ExceptionBean exceptionBean=new ExceptionBean();
	
	/**
	 * 使用exceptionCode和exceptionMsg初始化一个自定义普通异常
	 * exceptionId：默认为“-1”
	 * exceptionCode：如果不设置，则exceptionCode默认为“-1”
	 * exceptionMsg：如果不设置，则exceptionMsg默认为“未填写内容”
	 * @param exceptionCode 异常Code，一般用于定义某个场景的唯一Code，如：100100 {@linkplain org.cat.core.exception3.bean.ExceptionBean#setExceptionCode(String)}
	 * @param exceptionMsg 异常信息，用于异常错误的具体信息，如：在普通注册页面新增用户时用户已存在 {@linkplain org.cat.core.exception3.bean.ExceptionBean#setExceptionMsg(String)}
	 */
	public ParentException(String exceptionCode ,String exceptionMsg) {
		if(StringUtils.isNotBlank(exceptionCode)) {
			exceptionBean.setExceptionCode(exceptionCode);
		}
		if(StringUtils.isNotBlank(exceptionMsg)) {
			exceptionBean.setExceptionMsg(exceptionMsg);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月9日 下午2:59:26
	 * @version 1.0
	 * @description 设置一个新的exceptionId 
	 * @param exceptionId {@linkplain org.cat.core.exception3.bean.ExceptionBean#setExceptionId(String)}
	 */
	public void setExceptionId(String exceptionId) {
		if(StringUtils.isNotBlank(exceptionId)) {
			this.exceptionBean.setExceptionId(exceptionId);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月9日 下午2:59:48
	 * @version 1.0
	 * @description 给该异常设置一个项目标识
	 * @param projectExceptionCode	项目唯一标识 {@linkplain org.cat.core.exception3.bean.ExceptionBean#setExceptionProjectCode(String)}
	 */
	public void setProjectExceptionCode(String projectExceptionCode) {
		if(StringUtils.isNotBlank(projectExceptionCode)){
			this.exceptionBean.setExceptionProjectCode(projectExceptionCode);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午3:27:47
	 * @version 1.0
	 * @description 设置一个新的exceptionCode 
	 * @param exceptionCode {@linkplain ExceptionBean#setExceptionCode(String)}
	 */
	public void setExceptionCode(String exceptionCode) {
		if(StringUtils.isNotBlank(exceptionCode)) {
			this.exceptionBean.setExceptionCode(exceptionCode);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午3:29:39
	 * @version 1.0
	 * @description 设置一个新的exceptionmsg 
	 * @param exceptionMsg {@linkplain ExceptionBean#setExceptionMsg(String)}
	 */
	public void setExceptionMsg(String exceptionMsg) {
		if(StringUtils.isNotBlank(exceptionMsg)) {
			this.exceptionBean.setExceptionMsg(exceptionMsg);
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月9日 下午3:00:55
	 * @version 1.0
	 * @description 获取ExceptionBean 
	 * @return	ExceptionBean {@linkplain org.cat.core.exception3.bean.ExceptionBean}
	 */
	public ExceptionBean getExceptionBean(){
		return this.exceptionBean;
	}
	
	/**
	 * 重写Exception.getMessage()，将exceptionId、exceptionCode、exceptionMsg以json的格式返回
	 */
	@Override
	public String getMessage() {
		return this.toJson();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月9日 下午3:01:34
	 * @version 1.0
	 * @description 将exceptionId、exceptionCode、exceptionMsg转化为json格式
	 * @return
	 */
	private String toJson() {
		String responseStr=ArchJsonUtil.toJson(this.exceptionBean);
		return responseStr;
	}

}
