package org.cat.support.security3.generator.spring.study;

/**
 * 
 * @author 王云龙
 * @date 2021年10月28日 下午6:14:23
 * @version 1.0
 * @description CSRF（Cross-site request forgery）
 * 		中文名称：跨站请求伪造，也被称为：one click attack/session riding，缩写为：CSRF/XSRF
 * 
 * 		攻击者盗用了你的身份，以你的名义发送恶意请求
 * 		CSRF能够做的事情包括：以你名义发送邮件，发消息，盗取你的账号，甚至于购买商品，虚拟货币转账......
 * 		造成的问题包括：个人隐私泄露以及财产安全
 * 
 * 		角色：存在CSRF漏洞的WebA、攻击者WebB、受害者User/WebA
 * 		1：User浏览并登录信任网站WebA
 * 		2：User验证通过，在用户处产生A的Cookie
 * 		3：User在没有登出的情况下访问攻击网站WebB
 * 		4：WebB要求访问第三方站点WebA，发出一个请求
 * 		5：User的浏览器根据WebB在第4步的请求，带着第2步产生的Cookie访问WebA
 * 		6：WebA不知道第5步的请求是User发出的还是WebB发出的，由于浏览器会自动带上User的Cookie
 * 			所以WebA会根据User的权限处理第5步的请求，这样WebB就达到了模拟用户操作的目的
 * 
 * 		防范：
 * 		1：Token验证
 * 		在每个HTTP请求里附加一部分信息是一个防御CSRF攻击的很好的方法，因为这样可以判断请求是否已经授权
 * 		这个“验证token”应该不能轻易的被未登录的用户猜测出来。如果请求里面没有这个验证token或者token不能匹配的话，服务器应该拒绝这个请求
 * 		token验证的方法可以用来防御登陆CSRF，但是开发者往往会忘记验证，因为如果没有登陆，就不能通过session来绑定CSRF token
 * 		网站要想用验证token的方式来防御登陆CSRF攻击的话，就必须先创建一个“前session”，这样才能部署CSRF的防御方案
 * 		Token的设计，有很多技术可以生成验证token：
 * 		1.1：session标识符
 * 		服务器在处理每一个请求时，都将用户的token与session标识符来匹配
 * 		如果攻击者能够猜测出用户的token，那么他就能登录用户的账户
 * 		1.2：独立session随机数
 * 		当用户第一次登陆网站的时候，服务器可以产生一个随机数并将它存储在用户的cookie里面
 * 		对于每一个请求，服务器都会将token与存储在cookie里的值匹配
 * 		这个方法不能防御主动的网络攻击，即使是整个web应用都使用的是HTTPS协议
 * 		1.3：依赖session随机数
 * 		有一个改进产生随机数的方法是将用户的session标识符与CSRF token建立对应关系后存储在服务端
 * 		服务器在处理请求的时候，验证请求中的token是否与session标识符匹配
 * 		这个方法有个不好的地方就是服务端必须要维护一个很大的对应关系表（哈希表）
 * 		1.4：session标识符的HMAC
 * 		有一种方法不需要服务端来维护哈希表，就是可以对用户的session token做一个加密后用作CSRF 的token
 * 		例如， Ruby on Rails的web程序一般都是使用的这种方法，而且他们是使用session标识符的HMAC来作为CSRF token的
 * 		只要所有的网站服务器都共享了HMAC密钥，那么每个服务器都可以验证请求里的CSRF token 是否与session标识符匹配
 * 		HMAC的特性能确保即使攻击者知道用户的CSRF token，也不能推断出用户的session标识符
 * 		2：Referer
 * 		大多数情况下，当浏览器发起一个HTTP请求，其中的Referer标识了请求是从哪里发起的
 * 		如果HTTP头里包含有Referer的时候，我们可以区分请求是同域下还是跨站发起的，因为Referer里标明了发起请求的URL
 * 		网站也可以通过判断有问题的请求是否是同域下发起的来防御CSRF攻击
 * 		不幸的是，通常Referer会包含有一些敏感信息，可能会侵犯用户的隐私。比如，Referer可以显示用户对某个私密网站的搜索和查询
 * 		3：Origin字段（建议）
 * 		为了防止CSRF的攻击，我们建议修改浏览器在发送POST请求的时候加上一个Origin字段
 * 		这个Origin字段主要是用来标识出最初请求是从哪里发起的
 * 		如果浏览器不能确定源在哪里，那么在发送的请求里面Origin字段的值就为空
 * 		隐私方面：这种Origin字段的方式比Referer更人性化，因为它尊重了用户的隐私
 * 		Origin字段里只包含是谁发起的请求，并没有其他信息 (通常情况下是方案，主机和活动文档URL的端口)
 * 		跟Referer不一样的是，Origin字段并没有包含涉及到用户隐私的URL路径和请求内容，这个尤其重要
 * 		并且，Origin字段只存在于POST请求，而Referer则存在于所有类型的请求
 * 
 * 		总结和建议：
 * 		1：登陆CSRF
 * 		建议使用严格的Referer验证策略来防御登陆CSRF
 * 		因为登陆的表单一般都是通过HTTPS发送，在合法请求里面的Referer都是真实可靠的
 * 		如果碰到没有Referer字段的登陆请求，那么网站应该直接拒绝以防御这种恶意的修改
 * 		2：HTTPS
 * 		对于那些专门使用HTTPS协议的网站
 * 		建议使用严格的Referer验证策略来防御CSRF攻击。对于那些有特定跨站需求的请求，网站应该建立一份白名单
 * 		3：第三方内容
 * 		如果网站纳入了第三方的内容，比如图像外链和超链接，网站应该使用一个正确的验证token 的框架，比如 Ruby-on-Rails
 * 		如果这样的一个框架效果不好的话，网站就应该花时间来设计更好的token 验证策略，可以用HMAC方法将用户的session与token 绑定到一起
 * 		4：对于更长远的建议
 * 		用Origin字段来替代Referer，因为这样既保留了既有效果，又尊重了用户的隐私
 *
 */
class CSRF {

}
