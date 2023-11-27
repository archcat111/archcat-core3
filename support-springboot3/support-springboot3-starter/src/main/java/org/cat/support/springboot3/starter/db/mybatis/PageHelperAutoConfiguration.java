package org.cat.support.springboot3.starter.db.mybatis;

import javax.annotation.PostConstruct;

import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.github.pagehelper.PageInterceptor;


/**
 * 
 * @author 王云龙
 * @date 2022年4月12日 下午4:11:03
 * @version 1.0
 * @description org.cat框架定义的基于springboot的mybatis.pagehelper自动配置
 *
 */
@Configuration
@ConditionalOnClass(value={SupportDb3ConditionalFlag.class,PageInterceptor.class})
@ConditionalOnProperty(prefix = "cat.support3.db.mybatis.pagehelper", name = "enabled", havingValue = "true", matchIfMissing = false)
@AutoConfigureAfter(MyBatisAutoConfiguration.class)
@EnableConfigurationProperties(value={PageHelperProperties.class})
public class PageHelperAutoConfiguration {
	
	@Autowired
	private PageHelperProperties pageHelperProperties;
	
	@Autowired
	private MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder;

	@PostConstruct
	public void addPageInterceptor(){
		this.myBatisSqlSessionFactoryHolder.getSqlSessionFactoryMapper().forEach((sqlSessionFactoryName, sqlSessionFactory) -> {
			PageInterceptor interceptor = new PageInterceptor();
			interceptor.setProperties(this.pageHelperProperties.getProperties());
			sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
		});
		
	}
}
