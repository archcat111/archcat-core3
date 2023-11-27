package org.cat.support.security3.generator.session.study;

/**
 * 
 * @author 王云龙
 * @date 2022年8月16日 上午10:08:18
 * @version 1.0
 * @description 系统实现层面
 * 		本jar中实现了session的常量、异常、SessionId处理类
 * 	实际使用：
 * 		core-web：UserGenerator的接口
 * 		support-web：UserGenerator的实现
 * 		gateway：实现了userGeneratorFilter用来从前端接受SessionId、判断session登录、向后端传入userGenerator的数据到header
 * 		Session处理微服务：引入Spring-Session，配置SessionId处理类以及sessionAttrName
 * 		其他微服务：不引入Spring-Session，Feign从Request取数据传递给后端
 * 		直接暴漏的微服务：不引入Spring-Session，Feign从Header取SessionId调用Session处理微服务
 *
 * RedisSession实现(Http)：
 * 		RedisHttpSessionConfiguration的setRedisConnectionFactory方法获取一个RedisConnectionFactory来进行自己的初始化
 * 		优先使用标注了@SpringSessionRedisConnectionFactory的RedisConnectionFactory，没有则会从Spring容器中找普通的RedisConnectionFactory实例
 * RedisSession实现(Reactive)：
 * 		RedisWebSessionConfiguration的setRedisConnectionFactory方法获取一个ReactiveRedisConnectionFactory来进行自己的初始化
 * 		Reactive需要的ReactiveRedisConnectionFactory只有Lettuce提供了实现，Jedis没有提供实现
 * 		优先使用标注了@SpringSessionRedisConnectionFactory的RedisConnectionFactory，没有则会从Spring容器中找普通的RedisConnectionFactory实例
 * 		
 */
public class Impl {
	
}
