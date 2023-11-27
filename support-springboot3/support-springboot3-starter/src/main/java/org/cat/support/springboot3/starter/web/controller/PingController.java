package org.cat.support.springboot3.starter.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.cat.core.web3.controller.AbsArchRestRespController;
import org.cat.support.springboot3.starter.web.WebMvcAutoConfiguration;
import org.cat.support.springboot3.starter.web.WebMvcTestPingProperties;
import org.cat.support.springboot3.starter.web.WebMvcTestProperties;

import io.swagger.annotations.Api;

/**
 * @author 王云龙
 * @date 2021年9月6日 下午3:41:25
 * @version 1.0
 * @description 当配置
 * 		{@linkplain WebMvcTestProperties}、{@linkplain WebMvcTestPingProperties}
 * 		后
 * 		{@linkplain WebMvcAutoConfiguration}会通过特定方法动态初始化{@linkplain PingController}
 * 		该类主要是用于验证：
 * 		1：Controller是否真实起作用
 * 		2：从项目使用的角度真实判断项目存活并且可以提供服务
 */
@Api(description = "服务Ping接口")
public class PingController extends AbsArchRestRespController {
	
	public void ping() throws IOException{
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		PrintWriter printWriter=httpServletResponse.getWriter();
		printWriter.write("pong");
		printWriter.flush();  
		printWriter.close();  
	}
}
