exception3：
	生成ExceptionIdgenerator
	生成ExceptionCodeGenerator
	使用：在AbsRespController中注入ResultRespGeneratorImpl、SpringRespGeneratorImpl、WsRsRespGeneratorImpl时
			这些WebGenerator中需要注入ExceptionIdgenerator、ExceptionCodeGenerator
			
web3：
	有一个PingController
	？？？UserGenerator的实现
	
web3-log：
	eventlog的实现，直接记录日志，？？？除了记录日志以外，还会发送SpringEvent，在support-log中提供更多记录方式
	audit的实现，直接记录日志，？？？可以配置多种方式，还会发送SpringEvent，在support-log中提供更多记录方式

监控：
	有一个PingEndpoint
	有一个PingHealth

audit：
	？？？额外起包，再在SpringBootStarter添加autoConfiguration
	？？？写完MyBatis后，再在audit以及SpringBootStarter添加对应的数据库记录的相关功能

swagger：

id：
	原生的多种id生成器，然后再不全exception和log的随机id生成器
	
mysql：
	druidDataSource.setFailFast(true);

myBatis：

security：
	完成后，回头编写web3、mySQL等
	
LDAP：

Http：

ES：

mongodb：

redis：

email

image

Rest：
	有一个PingFeign
	
	
顺序：
	->：手动控制
	-->：自动控制
	db.druid -> db.sharding -> id
	id ->
		exception.id --> web.xxxGenerator
		log.id --> 
			audit.aspect
			event.aspect


-----------------------------

core-exception，需要一个support-exception，将Exception的Id生成器等放在这里，因为高级的id生成器需要support-id等的支持
	普通的id生成器等

同时支持普通数据源、Druid数据源池、ShardingSphere数据源池的MyBatis封装升级
jar之间的依赖关系，starter内部的不同部分的依赖关系
多种类型的Id生成器Holder、AuditLog的Id生成器、EventLog的Id生成器、DB的Id生成器、Exception的Id生成器的封装
support-exception的封装
Security的封装升级
LDAP的封装升级
UserGenerator的封装升级
Swagger
邮件、短信

Support-Security支持从配置中自定义过滤权限
	支持引入不同的组件会自动注入路径过滤权限，如Swagger

简单了解WebFlux
学习Security的WebFlux的实现
设计Security的配置，（适应前后端分离的情况）
1022，LDAP的封装升级
1022，UserGenerator的封装升级
1029，部署基础设施环境，其一个服务
1029，Swagger的封装升级
补全support id的配置
实现support-Log中的idGenerator
补全audit和event的IUserGenerator
BeanFactory的继承关系
ApplicationContext的继承关系
Spring初始化Bean有哪些方法，Generic...
allowedOrigins.stream().toArray(String[]::new)的语法和用法