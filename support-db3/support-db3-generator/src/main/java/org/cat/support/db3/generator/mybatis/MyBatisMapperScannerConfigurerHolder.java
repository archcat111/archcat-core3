package org.cat.support.db3.generator.mybatis;

import org.cat.core.util3.map.SourceHolder;
import org.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午4:32:09
 * @version 1.0
 * @description MapperScannerConfigurer持有者
 *   implements BeanDefinitionRegistryPostProcessor
 */
public class MyBatisMapperScannerConfigurerHolder extends SourceHolder<MapperScannerConfigurer>{

//	@Override
//	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//		Map<String, MapperScannerConfigurer> sourceMapper = super.getSelfSourceMapper();
//		sourceMapper.forEach((myBatisMapperScannerName, mapperScannerConfigurer) -> {
//			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
//			beanDefinitionBuilder.addPropertyValue("processPropertyPlaceHolders", true);
//			beanDefinitionBuilder.addPropertyValue("sqlSessionFactoryBeanName", "myBatisUserDS-SqlSessionFactory");
//			beanDefinitionBuilder.addPropertyValue("basePackage", "org.cat.app.user3.impl.dao.mapper");
//			BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
//			registry.registerBeanDefinition(myBatisMapperScannerName, beanDefinition);
//		});
//	}
	
}
