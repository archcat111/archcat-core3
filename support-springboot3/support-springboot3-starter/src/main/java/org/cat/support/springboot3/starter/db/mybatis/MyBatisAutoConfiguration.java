package org.cat.support.springboot3.starter.db.mybatis;

import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.core.web3.util.SpringResourceUtil;
import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.constants.MyBatisConstants;
import org.cat.support.db3.generator.mybatis.ArchMyBatisException;
import org.cat.support.db3.generator.mybatis.MyBatisMapperScannerConfigurerHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionTemplateHolder;
import org.cat.support.springboot3.starter.db.druid.DruidAutoConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


/**
 * 
 * @author 王云龙
 * @date 2021年9月29日 下午2:36:01
 * @version 1.0
 * @description Mybatis初始化大致原理：
 *  创建SqlSessionFactoryBean传入Bean的packages以及xml的packages
 * 		SqlSessionFactoryBean调用getObject()方法创建SqlSessionFactory对象，这个方法内部会调用afterPropertiesSet()
 * 		afterPropertiesSet()中
 * 			如果configuration或者configLocation不为空，优先通过这两个对象创建一个新的Configuration对象
 * 			如果SqlSessionFactoryBean设置了
 * 				configurationProperties、
 * 				objectFactory、
 * 				objectWrapperFactory、
 * 				vfs、
 * 				defaultScriptingLanguageDriver、
 * 				cache
 * 				等属性则会set到新的Configuration对象中
 * 			(重)如果有配置typeAliasesPackage，则会扫描里面的Bean并将Class set到新的Configuration对象中的TypeAliasRegistry里
 * 			如果有配置plugins，则会set到targetConfiguration中
 * 			如果有配置typeHandlersPackage，则会扫描里面的类并将Class set到新的Configuration对象中的TypeHandlerRegistry里
 * 			如果有配置scriptingLanguageDrivers，则会实例 set到新的Configuration对象中
 * 			如果有配置DatabaseIdProvider，则会将实例 set到新的Configuration对象中
 * 			(重)新建一个Environment对象，set到新的Configuration对象中，新建Environment对象需要3个参数：
 * 				environment：默认值："SqlSessionFactoryBean"
 * 				如果transactionFactory==null则使用SpringManagedTransactionFactory，否则使用transactionFactory
 * 				使用set到SqlSessionFactoryBean的dataSource
 *			(重)如果有配置mapperLocations，则创建XMLMapperBuilder解析这些mapper.xml文件
 *				其中的bindMapperForNamespace()将解析<mapper>标签中的namespace（即：Mapper类全路径名）
 *				并将Mapper的Class set到新的Configuration对象中的MapperRegistry里
 *		SqlSessionFactoryBean最后创建DefaultSqlSessionFactory对象，并将Configuration set进去
 *		
 *		DefaultSqlSessionFactory的openSession()创建DefaultSqlSession，并set configuration, executor, autoCommit
 *		DefaultSqlSession的getMapper(Class<T> type)返回的Map就是configuration.getMapper(type, this)返回的Mapper
 *		configuration.getMapper(...)从MapperRegistry中获取根据Mapper的Class名获取mapperProxyFactory
 *		mapperProxyFactory.newInstance(sqlSession)返回Mapper的可用对象
 *
 *		将Mapper注入到Spring容器，可以通过@AutoWired来直接引入XxxMapper进行使用：
 *		即@MapperScan注解的实现原理：
 *		该注解import了MapperScannerRegistrar，该类初始化了一个MapperScannerConfigurer，并给该对象设置属性
 *			processPropertyPlaceHolders = true
 *			annotationClass，默认值：Annotation.class
 *			markerInterface，默认值：Class.class
 *			nameGenerator，默认值：BeanNameGenerator.class
 *			mapperFactoryBeanClass，默认值：MapperFactoryBean.class，是一个接口，会从Spring容器种获取实现类
 *			sqlSessionTemplateBeanName，默认值：""
 *			sqlSessionFactoryBeanName，默认值：""
 *			basePackage，最终传入格式：org.xxx.a, org.xxx.b, org.xxx.c，
 *				这个列表包含value、basePackages、basePackageClasses、MapperScan所注解的类所在的packageName
 *			lazyInitialization，默认值：""
 *			defaultScope，默认值：""
 *		通过DefinitionRegistry将MapperScannerConfigurer注入Spring容器
 *
 *		MapperScannerConfigurer如何将Mapper初始化并出入到Spring容器？
 *		MapperScannerConfigurer实现了BeanDefinitionRegistryPostProcessor接口
 *		即：配置类增加@MapperScan注解，注解import了MapperScannerConfigurer，该类的postProcessBeanDefinitionRegistry方法就会被Spring优先调用
 *		这个方法生成一个ClassPathMapperScanner对象并将直接的配置信息set到这个对象，调用这个对象的scan()方法，该方法会将Mapper注入到Spring容器
 *
 */
@Configuration
@ConditionalOnClass(SupportDb3ConditionalFlag.class)
@ConditionalOnMissingClass("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
@AutoConfigureAfter(DruidAutoConfiguration.class)
@ConditionalOnProperty(prefix = "cat.support3.db.mybatis", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(MyBatisesProperties.class)
public class MyBatisAutoConfiguration  extends AbsMyBatisAutoConfiguration{
	
	@Autowired
	private MyBatisesProperties myBatisesProperties;
	
	@ConditionalOnClass(MyBatisSqlSessionFactoryHolder.class)
	@ConditionalOnMissingBean({MyBatisSqlSessionFactoryHolder.class, SqlSessionFactory.class})
	@Bean
	public MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder() {
		MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder = new MyBatisSqlSessionFactoryHolder();
		
		Optional.ofNullable(this.myBatisesProperties.getMyBatisDataSources())
			.orElseThrow(() -> new ArchMyBatisException(this.logPrefix + "您打开了Mybatis的开关，但没有配置MyBatis的数据源"))
			.forEach((mybatisDataSourceName, myBatisProperties) -> {
				boolean enabled = myBatisProperties.isEnabled();
				if(enabled) {
					String sqlSessionFactoryBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_FACTORY_NAME_SUFFIX;
					String sqlSessionFactoryBeanName = mybatisDataSourceName+sqlSessionFactoryBeanNameSuffix;
					
					SqlSessionFactory sqlSessionFactory  = createMyBatisSqlSessionFactory(mybatisDataSourceName, myBatisProperties, sqlSessionFactoryBeanName);
					
					manageProps(myBatisSqlSessionFactoryHolder, mybatisDataSourceName, sqlSessionFactoryBeanName, myBatisProperties);
					myBatisSqlSessionFactoryHolder.put(sqlSessionFactoryBeanName, sqlSessionFactory);
				}else {
					coreLogger.info(this.logPrefix + "数据源["+mybatisDataSourceName+"]开关为关闭，不进行SqlSessionFactory的初始化");
				}
			});
		
		return myBatisSqlSessionFactoryHolder;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午2:51:09
	 * @version 1.0
	 * @description 创建SessionFactory 
	 * @param myBatisProperties
	 * @return
	 */
	private SqlSessionFactory createMyBatisSqlSessionFactory(String mybatisDataSourceName, MyBatisProperties myBatisProperties, String sqlSessionFactoryBeanName) {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		
		//数据源
		String dataSourceType = myBatisProperties.getDataSourceType();
		String dataSourceName = myBatisProperties.getDataSourceName();
		DataSource dataSource = findDataSource(mybatisDataSourceName, dataSourceType, dataSourceName);
		sqlSessionFactoryBean.setDataSource(dataSource);
		
		//Bean
		String[] mapperBeanPackagesArr = myBatisProperties.getMapperBeanPackages();
		String mapperBeanPackagesStr = StringUtils.join(mapperBeanPackagesArr, ",");
		sqlSessionFactoryBean.setTypeAliasesPackage(mapperBeanPackagesStr);
		
		//MapperSqlXml
		String[] mapperSqlXmlsArr = myBatisProperties.getMapperSqlXmls();
		Resource[] mapperLocations=SpringResourceUtil.resolveLocationsToResources(mapperSqlXmlsArr, false);
		sqlSessionFactoryBean.setMapperLocations(mapperLocations);
		
		
		//Mapper & MyBatis自己持有的MapperFactoryBean
		//在调用sqlSessionFactoryBean.getObject()会创建SqlSessionFactory自己持有的MapperProxy，但没有注入到Spring中
		//详情参考该类MyBatisAutoConfiguration上的注释
		
		//各类配置
		sqlSessionFactoryBean.setConfiguration(createMybatisConfiguration(myBatisProperties));
		
		SqlSessionFactory sqlSessionFactory = null;
		try {
			sqlSessionFactory = sqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			throw new ArchMyBatisException(logPrefix + "通过SqlSessionFactoryBean获取SqlSessionFactory失败", e);
		}
		
		ArchSpringBeanUtil.registerBean(applicationContext, sqlSessionFactoryBeanName, sqlSessionFactory);
		this.coreLogger.info(this.logPrefix+"将SqlSessionFactory["+sqlSessionFactoryBeanName+"]注入到Spring容器中");
		return applicationContext.getBean(sqlSessionFactoryBeanName, SqlSessionFactory.class);
	}
	
	private org.apache.ibatis.session.Configuration createMybatisConfiguration(MyBatisProperties myBatisProperties){
		org.apache.ibatis.session.Configuration myBatisConfiguration = new org.apache.ibatis.session.Configuration();
		
		super.fillingConfiguration(myBatisConfiguration, myBatisProperties);
		
		return myBatisConfiguration;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午6:06:56
	 * @version 1.0
	 * @description 初始化MyBatisSqlSessionTemplateHolder
	 * @param MyBatisSqlSessionFactoryHolder
	 * @return
	 */
	@ConditionalOnClass(MyBatisSqlSessionTemplateHolder.class)
	@ConditionalOnBean({MyBatisSqlSessionFactoryHolder.class})
	@ConditionalOnMissingBean({MyBatisSqlSessionTemplateHolder.class, SqlSessionTemplate.class})
	@Bean
	public MyBatisSqlSessionTemplateHolder myBatisSqlSessionTemplateHolder(MyBatisSqlSessionFactoryHolder MyBatisSqlSessionFactoryHolder) {
		MyBatisSqlSessionTemplateHolder myBatisSqlSessionTemplateHolder = new MyBatisSqlSessionTemplateHolder();
		
		Optional.ofNullable(this.myBatisesProperties.getMyBatisDataSources())
			.orElseThrow(() -> new IllegalArgumentException("您打开了Mybatis的开关，但没有配置MyBatis的数据源"))
			.forEach((mybatisDataSourceName, myBatisProperties) -> {
				boolean enabled = myBatisProperties.isEnabled();
				if(enabled) {
					String sqlSessionTemplateBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_TEMPLATE_NAME_SUFFIX;
					String sqlSessionTemplateBeanName = mybatisDataSourceName + sqlSessionTemplateBeanNameSuffix;
					SqlSessionTemplate sqlSessionTemplate = createSqlSessionTemplate(mybatisDataSourceName, myBatisProperties, MyBatisSqlSessionFactoryHolder, sqlSessionTemplateBeanName);
					myBatisSqlSessionTemplateHolder.put(sqlSessionTemplateBeanName, sqlSessionTemplate);
				}else {
					coreLogger.info(this.logPrefix + "数据源["+mybatisDataSourceName+"]开关为关闭，不进行SqlSessionTemplate的初始化");
				}
			});
		
//		myBatisMapperScannerConfigurerHolder(MyBatisSqlSessionFactoryHolder, myBatisSqlSessionTemplateHolder);
		
		return myBatisSqlSessionTemplateHolder;
	}
	
	@ConditionalOnClass(MyBatisMapperScannerConfigurerHolder.class)
	@ConditionalOnMissingBean({MyBatisMapperScannerConfigurerHolder.class, MapperScannerConfigurer.class})
	@ConditionalOnBean({MyBatisSqlSessionFactoryHolder.class, MyBatisSqlSessionTemplateHolder.class})
	@Bean
	public MyBatisMapperScannerConfigurerHolder myBatisMapperScannerConfigurerHolder() {
		
		MyBatisMapperScannerConfigurerHolder myBatisMapperScannerConfigurerHolder = new MyBatisMapperScannerConfigurerHolder();
		
		Optional.ofNullable(this.myBatisesProperties.getMyBatisDataSources())
			.orElseThrow(() -> new IllegalArgumentException("您打开了Mybatis的开关，但没有配置MyBatis的数据源"))
			.forEach((mybatisDataSourceName, myBatisProperties) -> {
				boolean enabled = myBatisProperties.isEnabled();
				if(enabled) {
					String mapperScannerConfigurerBeanNameSuffix = MyBatisConstants.BeanName.MAPPER_SCANNER_CONFIGURER_NAME_SUFFIX;
					String mapperScannerConfigurerBeanName = mybatisDataSourceName + mapperScannerConfigurerBeanNameSuffix;
					MapperScannerConfigurer mapperScannerConfigurer = createMapperScannerConfigurer(mybatisDataSourceName, myBatisProperties, mapperScannerConfigurerBeanName);
					myBatisMapperScannerConfigurerHolder.put(mapperScannerConfigurerBeanName, mapperScannerConfigurer);
				}else {
					coreLogger.info(this.logPrefix + "数据源["+mybatisDataSourceName+"]开关为关闭，不进行SqlSessionTemplate的初始化");
				}
			});
		
		return myBatisMapperScannerConfigurerHolder;
	}
	

	

}
