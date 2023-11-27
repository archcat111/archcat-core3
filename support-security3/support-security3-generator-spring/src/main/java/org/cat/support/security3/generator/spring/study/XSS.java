package org.cat.support.security3.generator.spring.study;

/**
 * 
 * @author 王云龙
 * @date 2021年10月29日 上午10:53:48
 * @version 1.0
 * @description 跨站脚本攻击是指恶意攻击者往Web页面里插入恶意Script代码
 * 		当用户浏览该页之时，嵌入其中Web里面的Script代码会被执行，从而达到恶意攻击用户的目的
 * 
 * 		例如：留言板
 * 		留言板通常的任务就是把用户留言的内容展示出来
 * 		正常情况下，用户的留言都是正常的语言文字，留言板显示的内容也就没毛病
 * 		然而这个时候如果有人不按套路出牌，在留言内容中丢进去一行：
 * 		<code><script>alert(“hey!you are attacked”)</script></code>
 *		那么留言板界面的网页代码就会变成形如以下：
 *		<code>
 *		<html>
 *			<head>留言板</head>
 *			<body>
 *				<div id=”board”><script>alert(“hey!you are attacked”)</script></div>
 *			</body>
 *		</html>
 *		</code>
 *		当浏览器解析到用户输入的代码那一行时，浏览器并不知道这些代码改变了原本程序的意图，会照做弹出一个信息框
 *
 *		XSS的危害：
 *		1：窃取网页浏览中的cookie值：
 *		在网页浏览中我们常常涉及到用户登录，登录完毕之后服务端会返回一个cookie值
 *		这个cookie值相当于一个令牌，拿着这张令牌就等同于证明了你是某个用户
 *		如果你的cookie值被窃取，那么攻击者很可能能够直接利用你的这张令牌不用密码就登录你的账户
 *		如果想要通过script脚本获得当前页面的cookie值，通常会用到document.cookie
 *		cookie有其他验证措施如：Http-Only保证同一cookie不能被滥用
 *		2：劫持流量实现恶意跳转：
 *		这个很简单，就是在网页中想办法插入一句像这样的语句：
 *		<code><script>window.location.href="http://www.baidu.com";</script></code>
 *		早在2011年新浪就曾爆出过严重的xss漏洞，导致大量用户自动关注某个微博号并自动转发某条微博
 *
 *		常见的XSS利用与绕过：
 *		1：大小写绕过
 *		这个绕过方式的出现是因为网站仅仅只过滤了<script>标签，而没有考虑标签中的大小写并不影响浏览器的解释所致
 *		<code>http://192.168.1.102/xss/example2.php?name=<sCript>alert("hey!")</scRipt></code>
 *		利用过滤后返回语句再次构成攻击语句来绕过
 *		2：利用过滤后返回语句再次构成攻击语句来绕过
 *		假设：我们直接敲入script标签发现返回的网页代码中script标签被去除了，但其余的内容并没有改变
 *		于是我们就可以人为的制造一种巧合，让过滤完script标签后的语句中还有script标签（毕竟alert函数还在），像这样：
 *		<code>http://192.168.1.102/xss/example3.php?name=<sCri<script>pt>alert("hey!")</scRi</script>pt></code>
 *		3：并不是只有script标签才可以插入代码
 *		假设，前两种方式都没有成功，原因在于script标签已经被完全过滤，但能植入脚本代码的不止script标签
 *		例如这里我们用<img>标签做一个示范
 *		<code>http://192.168.1.102/xss/example4.php?name=<img src='w.123' onerror='alert("hey!")'></code>
 *		原因很简单，我们指定的图片地址根本不存在也就是一定会发生错误，这时候onerror里面的代码自然就得到了执行
 *		以下列举几个常用的可插入代码的标签：
 *		<code><a onmousemove=’do something here’></code>当用户鼠标移动时即可运行代码
 *		<code><div onmouseover=‘do something here’></code>当用户鼠标在这个块上面时即可运行
 *		4：编码脚本代码绕过关键字过滤
 *		有的时候，服务器往往会对代码中的关键字（如alert）进行过滤，这个时候我们可以尝试将关键字进行编码后再插入
 *		不过直接显示编码是不能被浏览器执行的，我们可以用另一个语句eval()来实现，eval()会将编码过的语句解码后再执行
 *		例如alert(1)编码过后就是：<code>\u0061\u006c\u0065\u0072\u0074(1)</code>
 *		所以构建出来的攻击语句如下：
 *		<code>http://192.168.1.102/xss/example5.php?name=<script>eval(\u0061\u006c\u0065\u0072\u0074(1))</script></code>
 *		5：主动闭合标签实现注入代码
 *		假设原来的html有如下代码：
 *		<code>
 *			<script>var $a="hacker"</script>
 *		</code>
 *		这个时候就要我们手动闭合掉两个双引号来实现攻击，因为javascript是一个弱类型的编程语言，变量的类型并没有明确定义
 *		<code>http://192.168.1.102/xss/example6.php?name=";alert("I amcoming again~");"</code>
 *		注入完成后，html的代码如下：
 *		<code>
 *			<script>var $a="";alert("I amcoming again~");""</script>
 *		</code>
 *		先是闭合引号，然后分号换行，加入代码，再闭合一个引号，搞定！
 *
 */
class XSS {

}
