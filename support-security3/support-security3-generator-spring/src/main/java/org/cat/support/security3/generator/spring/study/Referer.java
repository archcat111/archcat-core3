package org.cat.support.security3.generator.spring.study;

/**
 * 
 * @author 王云龙
 * @date 2021年10月29日 下午5:34:47
 * @version 1.0
 * @description Referer  是  HTTP  请求header 的一部分
 * 		当浏览器（或者模拟浏览器行为）向web 服务器发送请求的时候，头信息里有包含  Referer
 * 		比如我在www.google.com 里有一个www.baidu.com 链接，那么点击这个www.baidu.com ，它的header 信息里就有：
 * 		Referer=http://www.google.com
 * 
 * 		Referer  的正确英语拼法是referrer
 * 		由于早期HTTP规范的拼写错误，为了保持向后兼容就将错就错了
 * 		其它网络技术的规范企图修正此问题，使用正确拼法，所以目前拼法不统一
 * 		还有它第一个字母是大写
 * 
 * 		Referer的作用：
 * 		1：防盗链
 * 		我在www.google.com里有一个www.baidu.com链接，那么点击这个www.baidu.com，它的header信息里就有：
 * 		Referer=http://www.google.com
 * 		那么可以利用这个来防止盗链了，比如我只允许我自己的网站访问我自己的图片服务器
 * 		那我的域名是www.google.com，那么图片服务器每次取到Referer来判断一下是不是我自己的域名www.google.com
 * 		如果是就继续访问，不是就拦截
 * 		将这个http请求发给服务器后，如果服务器要求必须是某个地址或者某几个地址才能访问
 * 		而你发送的referer不符合他的要求，就会拦截或者跳转到他要求的地址，然后再通过这个地址进行访问
 * 		2：防止恶意请求
 * 		比如静态请求是*.html结尾的，动态请求是*.shtml，那么可以这么用，所有的*.shtml请求，必须 Referer为我自己的网站
 * 
 * 		空Referer是怎么回事？什么情况下会出现Referer？
 * 		Referer  头部的内容为空，或者，一个 HTTP  请求中根本不包含 Referer  头部
 * 		根据Referer的定义，它的作用是指示一个请求是从哪里链接过来
 * 		那么当一个请求并不是由链接触发产生的，那么自然也就不需要指定这个请求的链接来源
 * 		比如，直接在浏览器的地址栏中输入一个资源的URL地址，那么这种请求是不会包含 Referer  字段的
 * 		因为这是一个“凭空产生”的 HTTP  请求，并不是从一个地方链接过去的
 *
 */
class Referer {

}
