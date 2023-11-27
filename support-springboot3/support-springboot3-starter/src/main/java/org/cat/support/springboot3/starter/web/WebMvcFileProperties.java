package org.cat.support.springboot3.starter.web;

import java.nio.charset.StandardCharsets;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.util.unit.DataSize;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月7日 上午10:14:43
 * @version 1.0
 * @description 文件上传处理器的相关配置
 *
 */
@Getter
@Setter
public class WebMvcFileProperties {
	private boolean enabled;
	/**
	 * 设置用于解析请求的默认字符编码，应用于各个部分的标题和表单字段
	 * 该参数的默认值为UTF-8，作用于{@linkplain CommonsMultipartResolver}
	 * {@linkplain CommonsMultipartResolver}的默认编码在不设置时，根据 Servlet 规范，默认值为 ISO-8859-1
	 * 注意：如果请求本身指定了字符编码，则请求编码将覆盖此设置
	 */
	private String headerEncoding = StandardCharsets.UTF_8.name(); //使用CommonsMultipartResolver时有用
	/**
	 * 当接收文件的大小小于该值，则会先写入到内存中，增加性能
	 * 当接收文件的大小大于该值，则不会写入内存中，而是直接写入磁盘中
	 * 该参数单位为：bytes，1KB=1024bytes=1024*8bit
	 * 默认值为 10240，即：10KB
	 * 写法：1024、1KB、2MB、2Mb ……
	 */
	private DataSize maxInMemorySize = DataSize.ofBytes(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD*20);
	/**
	 * 上传允许的最大大小(如果一次上传多个文件，该该值表示多少文件的总大小)，大于该大小，上传会被拒绝
	 * 该参数的默认值为10MB，作用于{@linkplain CommonsMultipartResolver}
	 * {@linkplain CommonsMultipartResolver}中该参数的默认值为-1，即不限制
	 * 写法：1024、1KB、2MB、2Mb ……
	 */
	private DataSize maxUploadSize = DataSize.ofBytes(1024*1024*10); //最大上传文件尺寸10MB
	/**
	 * 上传允许的最大大小(如果一次上传多个文件，该该值表示单独一个文件的大小)，大于该大小，上传会被拒绝
	 * 该参数的默认值为10MB，作用于{@linkplain CommonsMultipartResolver}
	 * {@linkplain CommonsMultipartResolver}中该参数的默认值为-1，即不限制
	 */
	private DataSize maxUploadSizePerFile = DataSize.ofBytes(1024*1024*10);
	
	/**
	 * 在 {@link CommonsMultipartFile#getOriginalFilename()} 中设置是否保留客户端发送的文件名，而不是剥离路径信息
	 * 默认值为“false”
	 */
	private boolean preserveFileName = false; //使用CommonsMultipartResolver时有用
	/**
	 * CommonsMultipartResolver 实现了 MultipartResolver 接口
	 * resolveMultipart() 方法，其中 resolveLazily 是判断是否要延迟解析文件
	 * 当 resolveLazily 为 flase 时，会立即调用 parseRequest() 方法对请求数据进行解析
	 * 		然后将解析结果封装到 DefaultMultipartHttpServletRequest 中
	 * 当 resolveLazily 为 true 时，会在 DefaultMultipartHttpServletRequest 的 initializeMultipart() 方法调用 parseRequest() 方法
	 * 		对请求数据进行解析，而 initializeMultipart() 方法又是被 getMultipartFiles() 方法调用，
	 * 		即当需要获取文件信息时才会去解析请求数据，这种方式用了懒加载的思想
	 */
	private boolean resolveLazily = false; 
	/**
	 * 设置用于临时存储大于配置的大小阈值的文件的目录
	 * 如：fileUpload/temp
	 * 如果不设置，默认是 servlet 容器的 Web 应用程序临时目录。
	 *@see org.springframework.web.util.WebUtils#TEMP_DIR_CONTEXT_ATTRIBUTE
	 *		即：javax.servlet.context.tempdir
	 *关于：javax.servlet.context.tempdir
	 *每一个servlet上下文都需要一个临时存储目录
	 *Servlet容器必须为每一个servlet上下文提供一个私有的临时目录，并且使它可以通过javax.servlet.context.tempdir上下文属性可用
	 *这些属性关联的对象必须是java.io.File类型
	 *System.getProperty("java.io.tmpdir")
	 */
	private String uploadTempDir = null;
}
