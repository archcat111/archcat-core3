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
import org.cat.support.db3.generator.mybatis.plus.ArchMetaObjectHandler;
import org.cat.support.springboot3.starter.db.druid.DruidAutoConfiguration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

import cn.hutool.core.util.ArrayUtil;


/**
 * 
 * @author 王云龙
 * @date 2022年4月26日 下午4:13:05
 * @version 1.0
 * @description MyBatisPlus的初始化类
 *
 */
@Configuration
@ConditionalOnClass({SupportDb3ConditionalFlag.class, MybatisSqlSessionFactoryBean.class})
@AutoConfigureAfter(DruidAutoConfiguration.class)
@ConditionalOnProperty(prefix = "cat.support3.db.mybatis", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(MyBatisesProperties.class)
public class MyBatisPlusAutoConfiguration extends AbsMyBatisAutoConfiguration{
	
	@Autowired
	private MyBatisesProperties myBatisesProperties;
	
	@ConditionalOnClass(MyBatisSqlSessionFactoryHolder.class)
	@ConditionalOnMissingBean({MyBatisSqlSessionFactoryHolder.class, SqlSessionFactory.class})
	@Bean
	public MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder() {
		MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder = new MyBatisSqlSessionFactoryHolder();
		
		Optional.ofNullable(this.myBatisesProperties.getMyBatisDataSources())
			.orElseThrow(() -> new IllegalArgumentException("您打开了Mybatis的开关，但没有配置MyBatis的数据源"))
			.forEach((mybatisDataSourceName, myBatisProperties) -> {
				boolean enabled = myBatisProperties.isEnabled();
				if(enabled) {
					String sqlSessionFactoryBeanNameSuffix = MyBatisConstants.BeanName.SQL_SESSION_FACTORY_NAME_SUFFIX;
					String sqlSessionFactoryBeanName = mybatisDataSourceName+sqlSessionFactoryBeanNameSuffix;
					
					SqlSessionFactory sqlSessionFactory = createMyBatisSqlSessionFactory(mybatisDataSourceName, myBatisProperties, sqlSessionFactoryBeanName);
					
					manageProps(myBatisSqlSessionFactoryHolder, mybatisDataSourceName, sqlSessionFactoryBeanName, myBatisProperties);
					myBatisSqlSessionFactoryHolder.put(sqlSessionFactoryBeanName, sqlSessionFactory);
				}else {
					coreLogger.info(this.logPrefix + "数据源["+mybatisDataSourceName+"]开关为关闭，不进行SqlSessionFactory的初始化");
				}
			});
		
		return myBatisSqlSessionFactoryHolder;
	}
	
	private SqlSessionFactory createMyBatisSqlSessionFactory(String mybatisDataSourceName, MyBatisProperties myBatisProperties, String sqlSessionFactoryBeanName) {
		MybatisSqlSessionFactoryBean mybatisPlusSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
		
		//数据源
		String dataSourceType = myBatisProperties.getDataSourceType();
		String dataSourceName = myBatisProperties.getDataSourceName();
		DataSource dataSource = findDataSource(mybatisDataSourceName, dataSourceType, dataSourceName);
		mybatisPlusSqlSessionFactoryBean.setDataSource(dataSource);
		
		//Bean
		String[] mapperBeanPackagesArr = myBatisProperties.getMapperBeanPackages();
		String mapperBeanPackagesStr = StringUtils.join(mapperBeanPackagesArr, ",");
		mybatisPlusSqlSessionFactoryBean.setTypeAliasesPackage(mapperBeanPackagesStr);
		
		//MapperSqlXml
		String[] mapperSqlXmlsArr = myBatisProperties.getMapperSqlXmls();
		if(ArrayUtil.isNotEmpty(mapperSqlXmlsArr)) {
			Resource[] mapperLocations=SpringResourceUtil.resolveLocationsToResources(mapperSqlXmlsArr, true);
			mybatisPlusSqlSessionFactoryBean.setMapperLocations(mapperLocations);
		}
		
		//Mapper & MyBatis自己持有的MapperFactoryBean
		//在调用sqlSessionFactoryBean.getObject()会创建SqlSessionFactory自己持有的MapperProxy，但没有注入到Spring中
		//详情参考该类MyBatisAutoConfiguration上的注释
		
		//各类配置
		mybatisPlusSqlSessionFactoryBean.setConfiguration(createMybatisConfiguration(myBatisProperties));
		mybatisPlusSqlSessionFactoryBean.setGlobalConfig(createGlobalConfig(myBatisProperties));
				
		SqlSessionFactory sqlSessionFactory = null;
		try {
			sqlSessionFactory = mybatisPlusSqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			throw new ArchMyBatisException(logPrefix + "通过SqlSessionFactoryBean获取SqlSessionFactory失败", e);
		}
		
		ArchSpringBeanUtil.registerBean(applicationContext, sqlSessionFactoryBeanName, sqlSessionFactory);
		this.coreLogger.info(this.logPrefix+"将SqlSessionFactory["+sqlSessionFactoryBeanName+"]注入到Spring容器中");
		return applicationContext.getBean(sqlSessionFactoryBeanName, SqlSessionFactory.class);
	}
	
	private MybatisConfiguration createMybatisConfiguration(MyBatisProperties myBatisProperties) {
		MybatisConfiguration mybatisPlusConfiguration = new MybatisConfiguration();
		
		super.fillingConfiguration(mybatisPlusConfiguration, myBatisProperties);
				
		return mybatisPlusConfiguration;
	}
	
	private GlobalConfig createGlobalConfig(MyBatisProperties myBatisProperties) {
		GlobalConfig globalConfig = new GlobalConfig();
		globalConfig.setMetaObjectHandler(metaObjectHandler());
		globalConfig.setDbConfig(createDbConfig(myBatisProperties));
		
		return globalConfig;
	}
	
	private DbConfig createDbConfig(MyBatisProperties myBatisProperties) {
		DbConfig dbConfig = new DbConfig();
		//逻辑删除
		dbConfig.setLogicDeleteField(myBatisProperties.getLogicDeleteField());
		dbConfig.setLogicDeleteValue(myBatisProperties.getLogicDeleteValue());
		dbConfig.setLogicNotDeleteValue(myBatisProperties.getLogicNotDeleteValue());
		return dbConfig;
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
	
	@Bean
	public MetaObjectHandler metaObjectHandler() {
		ArchMetaObjectHandler archMetaObjectHandler = new ArchMetaObjectHandler();
		return archMetaObjectHandler;
	}
	

}
