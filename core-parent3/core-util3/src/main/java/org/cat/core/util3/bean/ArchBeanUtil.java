package org.cat.core.util3.bean;

import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

/**
 * 
 * @author 王云龙
 * @date 2022年4月6日 下午4:54:53
 * @version 1.0
 * @description bean处理工具
 *
 */
public class ArchBeanUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月7日 下午2:54:10
	 * @version 1.0
	 * @description  创建一个制定Class<C>的目标对象，并将source中的属性根据名称拷贝到这个新对象
	 * @param <C> 返回的目标对象的Class类型
	 * @param source 原始对象
	 * @param clazz 目标对象的Class类型
	 * @return 一个包含原始对象中同名属性的值的目标对象
	 */
	public static <C> C toBean(Object source, Class<C> clazz) {
		return BeanUtil.toBean(source, clazz);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月7日 下午2:56:02
	 * @version 1.0
	 * @description 创建一个制定Class<C>的目标对象，并将source中的属性根据名称拷贝到这个新对象(忽略大小写)
	 * @param <C> 返回的目标对象的Class类型
	 * @param source 原始对象
	 * @param clazz 目标对象的Class类型
	 * @return 一个包含原始对象中同名属性的值的目标对象(忽略大小写)
	 */
	public static <C> C toBeanIgnoreCase(Object source, Class<C> clazz) {
		CopyOptions copyOptions = CopyOptions.create().ignoreCase();
		return BeanUtil.toBean(source, clazz, copyOptions);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年8月9日 上午11:38:02
	 * @version 1.0
	 * @param <C>
	 * @description 对List中的bean的类型进行转换 
	 * @param sources
	 * @param clazz
	 * @return
	 */
	public static <C> List<C> toBeanListIgnoreCase(List<?> sources, Class<C> clazz){
		CopyOptions copyOptions = CopyOptions.create().ignoreCase();
		return BeanUtil.copyToList(sources, clazz, copyOptions);
	}
}
