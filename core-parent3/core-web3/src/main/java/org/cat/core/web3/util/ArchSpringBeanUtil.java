package org.cat.core.web3.util;

import org.cat.core.web3.exception.BeanSpringUtilException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author 王云龙
 * @date 2021年8月31日 下午5:17:26
 * @version 1.0
 * @description 处理Spring Bean的工具类
 *
 */
public class ArchSpringBeanUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月31日 下午5:39:47
	 * @version 1.0
	 * @description 将一个Bean动态注册到Spring容器中 
	 * @param <T> 需要注册的Bean的类型
	 * @param applicationContext applicationContext
	 * @param beanName 需要注册到Spring容器中的Bean的名称
	 * @param clazz 需要注册的Bean的Class类型
	 * @param constructoArgValues 给需要注册的Bean的构造函数传入的参数值列表
	 * @return 返回该Bean在Spring容器中的实例
	 */
	public static <T> T registerBean(ApplicationContext applicationContext, String beanName, Class<T> clazz, Object...constructoArgValues) {
		if(applicationContext.containsBean(beanName)) {
			throw new BeanSpringUtilException("Spring容器中已经包含名为["+beanName+"]的Bean");
		}
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		if(constructoArgValues != null && constructoArgValues.length>0) {
			for (Object constructoArgValue : constructoArgValues) {
	            beanDefinitionBuilder.addConstructorArgValue(constructoArgValue);
	        }
		}
		
//		beanDefinitionBuilder.addAutowiredProperty(propertyValue)
//		beanDefinitionBuilder.addConstructorArgReference(beanName)
//		beanDefinitionBuilder.addPropertyReference(propertyName, beanName)
//		beanDefinitionBuilder.addPropertyValue(propertyName, propertyValue)
		
		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
		return applicationContext.getBean(beanName, clazz);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月22日 下午6:29:01
	 * @version 1.0
	 * @description 将一个Bean动态注册到Spring容器中 
	 * @param <T> 需要注册的Bean的类型
	 * @param applicationContext applicationContext
	 * @param beanName 需要注册到Spring容器中的Bean的名称
	 * @param clazz 需要注册的Bean的Class类型
	 * @return 返回该Bean在Spring容器中的实例
	 * @return
	 */
	public static <T> T registerBean(ApplicationContext applicationContext, String beanName, Class<T> clazz) {
		return registerBean(applicationContext, beanName, clazz, new Object[] {});
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月22日 下午2:01:01
	 * @version 1.0
	 * @description 将一个new好的bean以指定的名字注册到Spring容器
	 * @param <T>
	 * @param applicationContext
	 * @param beanName
	 * @param bean
	 */
	public static <T> void registerBean(ApplicationContext applicationContext, String beanName, T bean) {
		
		if(applicationContext.containsBean(beanName)) {
			throw new BeanSpringUtilException("Spring容器中已经包含名为["+beanName+"]的Bean");
		}

		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
		defaultListableBeanFactory.registerSingleton(beanName, bean);
	}
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月31日 下午5:57:35
	 * @version 1.0
	 * @description 从Spring容器中删除某个Bean 
	 * @param applicationContext ApplicationContext
	 * @param beanName 需要从从Spring容器中删除的Bean的名称
	 */
	public static void removeBean(ApplicationContext applicationContext, String beanName) {
		if(!applicationContext.containsBean(beanName)) {
			throw new BeanSpringUtilException("Spring容器中没有找到名为["+beanName+"]的Bean");
		}
		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
		defaultListableBeanFactory.removeBeanDefinition(beanName);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月22日 下午5:27:01
	 * @version 1.0
	 * @description 从spring容器中获取bean，如果不存在，则返回null 
	 * @param <T>
	 * @param applicationContext
	 * @param beanName
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(ApplicationContext applicationContext, String beanName, Class<T> clazz) {
		if(applicationContext.containsBean(beanName)) {
			T t = applicationContext.getBean(beanName, clazz);
			return t;
		}
		return null;
	}
}
