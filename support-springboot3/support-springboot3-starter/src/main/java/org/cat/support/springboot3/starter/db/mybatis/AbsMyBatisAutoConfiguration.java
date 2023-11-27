package org.cat.support.springboot3.starter.db.mybatis;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.db3.generator.constants.DataSourceConstants;
import org.cat.support.db3.generator.constants.MyBatisConstants;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.mybatis.ArchMyBatisException;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder.SqlSessionFactoryProps;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import cn.hutool.core.util.ArrayUtil;

public abstract class AbsMyBatisAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "MyBatis数据源初始化：";
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@Autowired(required = false)
	protected DruidDataSourceHolder druidDataSourceHolder;
	@Autowired(required = false)
	protected ShardingDataSourceHolder shardingDataSourceHolder;
	
	protected void fillingConfiguration(Configuration myBatisConfiguration, MyBatisProperties myBatisProperties) {
		//缓存
		myBatisConfiguration.setCacheEnabled(myBatisProperties.isCacheEnabled());
		myBatisConfiguration.setLocalCacheScope(myBatisProperties.getLocalCacheScope());
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午4:20:52
	 * @version 1.0
	 * @description 根据MyBatis中配置的dataSourceName从druidDataSourceHolder或者shardingDataSourceHolder获取DataSource实例
	 * @param mybatisDataSourceName	MyBatis数据源名称，在显示抛出异常信息时会使用到
	 * @param dataSourceType	根据该值去不同的DataSourceHolder中寻找DataSource
	 * @param dataSourceName		不同的DataSourceHolder中DataSourceName
	 * @return
	 */
	protected DataSource findDataSource(String mybatisDataSourceName, String dataSourceType, String dataSourceName) {
		if(StringUtils.isBlank(dataSourceType)) {
			throw new ArchMyBatisException(this.logPrefix + "数据源["+mybatisDataSourceName+"]必须设置dataSourceType");
		}
		if(StringUtils.isBlank(dataSourceName)) {
			throw new ArchMyBatisException(this.logPrefix + "数据源["+mybatisDataSourceName+"]必须设置dataSourceName");
		}
		DataSource dataSource = null;
		switch (dataSourceType.toLowerCase()) {
		case DataSourceConstants.Type.DRUID:
			dataSource = this.druidDataSourceHolder.get(dataSourceName);
			break;
		case DataSourceConstants.Type.SHARDING_SPHERE:
			dataSource = this.shardingDataSourceHolder.get(dataSourceName);
			break;
		default:
			throw new ArchMyBatisException(this.logPrefix + "数据源["+mybatisDataSourceName+"]必须设置合理的dataSourceType");
		}
		if(dataSource == null) {
			throw new ArchMyBatisException(this.logPrefix + "数据源["+mybatisDataSourceName+"]没有找到名为["+dataSourceName+"]的数据源");
		}
		return dataSource;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午6:02:26
	 * @version 1.0
	 * @description 创建SqlSessionTemplate对象 
	 * @param mybatisDataSourceName
	 * @param myBatisProperties
	 * @param myBatisSqlSessionFactoryHolder
	 * @return
	 */
	protected SqlSessionTemplate createSqlSessionTemplate(
			String mybatisDataSourceName, 
			MyBatisProperties myBatisProperties, 
			MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder,
			String sqlSessionTemplateBeanName) {
		
		SqlSessionFactory sqlSessionFactory = findSqlSessionFactory(mybatisDataSourceName, myBatisSqlSessionFactoryHolder);
		if(sqlSessionFactory==null) {
			throw new ArchMyBatisException(this.logPrefix + "数据源["+mybatisDataSourceName+"]中在初始化SqlSessionTemplate时通过["+mybatisDataSourceName+"]没有找到对应的SqlSessionFactory");
		}
		
		ExecutorType executorType = myBatisProperties.getExecutorType();
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory, executorType); 
		
		ArchSpringBeanUtil.registerBean(applicationContext, sqlSessionTemplateBeanName, sqlSessionTemplate);
		this.coreLogger.info(this.logPrefix+"将SqlSessionTemplate["+sqlSessionTemplateBeanName+"]注入到Spring容器中");
		return applicationContext.getBean(sqlSessionTemplateBeanName, SqlSessionTemplate.class);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午6:01:04
	 * @version 1.0
	 * @description 通过mybatisDataSourceName从myBatisSqlSessionFactoryHolder中获取 SqlSessionFactory实例
	 * @param mybatisDataSourceName
	 * @param myBatisSqlSessionFactoryHolder
	 * @return
	 */
	protected SqlSessionFactory findSqlSessionFactory(String mybatisDataSourceName, MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder) {
		String sqlSessionFactoryBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_FACTORY_NAME_SUFFIX;
		String sqlSessionFactoryBeanName = mybatisDataSourceName+sqlSessionFactoryBeanNameSuffix;
		SqlSessionFactory sqlSessionFactory = myBatisSqlSessionFactoryHolder.get(sqlSessionFactoryBeanName);
		return sqlSessionFactory;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月9日 下午6:28:21
	 * @version 1.0
	 * @description MapperScannerConfigurer属性的说明： 
	 * 		processPropertyPlaceHolders = true
	 *		annotationClass，默认值：Annotation.class	//这个注解指定的接口也要被扫描，这个是所有注解的顶层接口
	 *			会初始化AnnotationTypeFilter来进行过滤，如果该类有该注解或者该类的注解是该注解的子注解
	 *		markerInterface，默认值：Class.class	//继承这个接口的接口也要被扫描
	 *			会初始化AssignableTypeFilter来进行过滤
	 *			annotationClass和markerInterface满足其一即可
	 *		nameGenerator，默认值：BeanNameGenerator.class		//将扫描到的Mapper注册到Spring容器中时，生成Bean名称的策略组件
	 *		mapperFactoryBeanClass，默认值：MapperFactoryBean.class	//多数据源的时候可能用到这个
	 *		sqlSessionTemplateBeanName，默认值：""	//多数据源的时候可能用到这个
	 *		sqlSessionFactoryBeanName，默认值：""	//多数据源的时候可能用到这个
	 *		basePackage，最终传入格式：org.xxx.a, org.xxx.b, org.xxx.c，
	 *			这个列表包含：
	 *				value		//缺省属性（==basePackages），basePackages的别名
	 *				basePackages		//包路径下的接口被扫描注册（接口至少有一个方法），具体实现类（非接口）忽略
	 *				basePackageClasses	//指定类所在包下所有接口被扫描注册（接口至少有一个方法），具体实现类（非接口）忽略
	 *				MapperScan
	 *			所注解的类所在的packageName
	 *		lazyInitialization，默认值：""
	 *		defaultScope，默认值：""
	 * @param mybatisDataSourceName mybatisDataSource名称
	 * @param myBatisProperties
	 * @param mapperScannerConfigurerBeanName mapperScannerConfigurer应该在Spring容器中以及MyBatisMapperScannerConfigurerHolder中的名称
	 * @return
	 */
	protected MapperScannerConfigurer createMapperScannerConfigurer(
			String mybatisDataSourceName, 
			MyBatisProperties myBatisProperties, 
			String mapperScannerConfigurerBeanName) {
		
		String sqlSessionFactoryBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_FACTORY_NAME_SUFFIX;
		String sqlSessionFactoryBeanName = mybatisDataSourceName+sqlSessionFactoryBeanNameSuffix;
		String sqlSessionTemplateBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_TEMPLATE_NAME_SUFFIX;
		String sqlSessionTemplateBeanName = mybatisDataSourceName + sqlSessionTemplateBeanNameSuffix;
		
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setProcessPropertyPlaceHolders(false);
//		mapperScannerConfigurer.setAnnotationClass(Annotation.class);
//		mapperScannerConfigurer.setMarkerInterface(Class.class);
//		mapperScannerConfigurer.setNameGenerator(BeanUtils.instantiateClass(BeanNameGenerator.class));
//		mapperScannerConfigurer.setMapperFactoryBeanClass(MapperFactoryBean.class);
		mapperScannerConfigurer.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
		mapperScannerConfigurer.setSqlSessionTemplateBeanName(sqlSessionTemplateBeanName);
		mapperScannerConfigurer.setBasePackage(StringUtils.join(myBatisProperties.getMapperInterfacePackages()));
//		mapperScannerConfigurer.setLazyInitialization("false");
//		mapperScannerConfigurer.setDefaultScope(AbstractBeanDefinition.SCOPE_DEFAULT);
		
		mapperScannerConfigurer.setBeanName(mapperScannerConfigurerBeanName);
		mapperScannerConfigurer.setApplicationContext(this.applicationContext);
		
		//注入到Spring容器
		ArchSpringBeanUtil.registerBean(applicationContext, mapperScannerConfigurerBeanName, mapperScannerConfigurer);
		this.coreLogger.info(this.logPrefix+"将MapperScannerConfigurer["+mapperScannerConfigurerBeanName+"]注入到Spring容器中");
		
		AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
		mapperScannerConfigurer.postProcessBeanDefinitionRegistry(defaultListableBeanFactory);
		return applicationContext.getBean(mapperScannerConfigurerBeanName, MapperScannerConfigurer.class);
	}
	
	protected void manageProps(
			MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder, 
			String mybatisDataSourceName,
			String sqlSessionFactoryBeanName, 
			MyBatisProperties myBatisProperties) {
		SqlSessionFactoryProps sqlSessionFactoryProps = new SqlSessionFactoryProps();
		sqlSessionFactoryProps.setMyBatisDataSourceName(mybatisDataSourceName);
		sqlSessionFactoryProps.setDataSourceName(myBatisProperties.getDataSourceName());
		sqlSessionFactoryProps.setDataSourceType(myBatisProperties.getDataSourceType());
		
		String mapperBeanPackages = null;
		String[] mapperBeanPackagesArr = myBatisProperties.getMapperBeanPackages();
		if(mapperBeanPackagesArr!=null && mapperBeanPackagesArr.length>0) {
			mapperBeanPackages = ArrayUtil.join(mapperBeanPackagesArr, ",");
		}
		sqlSessionFactoryProps.setMapperBeanPackages(mapperBeanPackages);
		
		String mapperSqlXmls = null;
		String[] mapperSqlXmlsArr = myBatisProperties.getMapperSqlXmls();
		if(mapperSqlXmlsArr!=null && mapperSqlXmlsArr.length>0) {
			mapperSqlXmls = ArrayUtil.join(mapperSqlXmlsArr, ",");
		}
		sqlSessionFactoryProps.setMapperSqlXmls(mapperSqlXmls);
		
		String mapperInterfacePackages = null;
		String[] mapperInterfacePackagesArr = myBatisProperties.getMapperInterfacePackages();
		if(mapperInterfacePackagesArr!=null && mapperInterfacePackagesArr.length>0) {
			mapperInterfacePackages = ArrayUtil.join(mapperInterfacePackagesArr, ",");
		}
		sqlSessionFactoryProps.setMapperInterfacePackages(mapperInterfacePackages);
		
		sqlSessionFactoryProps.setExecutorType(myBatisProperties.getExecutorType());
		sqlSessionFactoryProps.setLocalCacheScope(myBatisProperties.getLocalCacheScope());
		sqlSessionFactoryProps.setLevel2Cache(myBatisProperties.isCacheEnabled());
		
		myBatisSqlSessionFactoryHolder.setSqlSessionFactoryProps(sqlSessionFactoryBeanName, sqlSessionFactoryProps);
	}
}
