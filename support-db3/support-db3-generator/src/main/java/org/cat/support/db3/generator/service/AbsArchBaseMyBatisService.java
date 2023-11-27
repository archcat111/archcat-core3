package org.cat.support.db3.generator.service;

import org.cat.support.db3.generator.mybatis.MyBatisMapperScannerConfigurerHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionTemplateHolder;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbsArchBaseMyBatisService {
	@Autowired(required = false)
	private MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder;
	@Autowired(required = false)
	private MyBatisSqlSessionTemplateHolder myBatisSqlSessionTemplateHolder;
	@Autowired(required = false)
	private MyBatisMapperScannerConfigurerHolder myBatisMapperScannerConfigurerHolder;
}
