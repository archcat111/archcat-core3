package org.cat.support.db3.generator.druid.study;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerImpl;
import com.alibaba.druid.util.JdbcConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月13日 下午3:23:12
 * @version 1.0
 * @description 一个通用的连接池配置属性类
 * https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
 *
 */
@Getter
@Setter
public class DruidDemoProperties {
	//配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来
	//如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this)
	//另外配置此属性至少在1.0.5版本中是不起作用的，强行设置name会出错
	private String name = "default-druidDataSource";
	
	//////////////////////////////////////初始信息、连接配置////////////////////////////////////////////////////
	//这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
	//如果使用了一些自定义格式的url导致无法识别，就需要在这里做相应的配置
	private String driverClassName = JdbcConstants.MYSQL_DRIVER;
	//会自动根据url判断并填充该值，不需要用户自行设置，除非一些Druid不支持的类型
	private String dbTypeName; //默认值为null
	//连接数据库的url，不同数据库不一样
	//如：mysql : jdbc:mysql://10.20.153.104:3306/druid2
	//如：oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto
	private String url = null; 
	private String userName = null;
	private String password = null;
//	private PasswordCallback passwordCallback;
//	private String passwordCallbackClassName;
	
	//初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
	private int initialSize = 1; //默认值为0
	//最大连接池数量
	private int maxActive = 20; //默认值为8
	//已经不再使用，配置了也没有效果
//	private int maxIdle = 20; //默认值为8
	//最小连接池数量
	private int minIdle = 1; //默认值为0
	//获取连接时最大等待时间，单位毫秒
	//配置了maxWait之后，缺省启用公平锁，并发效率会有所下降
	//如果需要可以通过配置useUnfairLock属性为true使用非公平锁
	private int maxWaitMillis = 5000; //默认值为-1
	private boolean useUnfairLock = true; //默认值为null
	//取不到连接发生等待时阻塞的最大业务线程数
	private int maxWaitThreadCount = -1; //默认值为-1
	
	//用于创建连接的调度任务的数量
	//当设置的值的数量<1时，会报错
	private int maxCreateTaskCount = 3; //默认值为3
	
	////////////////////////////////////保活、线程生命周期、废弃线程时间、日志////////////////////////////////////
	
	//配置用来检测连接是否有效的SQL，要求是一个查询语句
	//如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用
	private String validationQuery = "select 1"; //默认值为null
	//检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
	//例如：在MySqlValidConnectionChecker中调用MySQL.ConnectionImpl的pingInternal()方法会使用这个参数
	//单位：秒
	private int validationQueryTimeout = 5; //默认值为-1
	//申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
	private boolean testOnBorrow = false; //默认值为false
	//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
	private boolean testOnReturn = false; //默认值为false
	//当应用向连接池申请连接，并且testOnBorrow为false时
	//如果该连接的空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
	//如果检测结果为无效, 则当前连接将被从连接池中去除掉
	//建议配置为true，不影响性能，并且保证安全性
	private boolean testWhileIdle = true; //默认值为true
	//如果该属性为true，使用lastExecTimeMillis赋值lastActiveTimeMillis
	private boolean checkExecuteTime = false; //默认值为false
	//1：初始化连接池时会填充到minIdle数量
	//2：连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作，即执行druid.validationQuery指定的查询SQL
	//3：当网络断开等原因产生的由ExceptionSorter检测出来的死连接被清除后，自动补充连接到minIdle数量
	private boolean keepAlive = true; //默认值为false
	//如果连接对象不在evictCheck区间内，且keepAlive属性为true，则判断该对象闲置时间是否超出keepAliveBetweenTimeMillis
	//若超出，则意味着该连接需要进行连接可用性检查，则将该对象放入keepAliveConnections队列
	//必须>=30秒且>timeBetweenEvictionRunsMillis
	private long keepAliveBetweenTimeMillis = 60 * 1000L; //默认值为1分钟
	//连接保持空闲而不被驱逐的最小时间
	private long minEvictableIdleTimeMillis = 60 * 30 * 1000L; // 默认值为30分钟
	//连接保持空闲而不被驱逐的最大时间
	private long maxEvictableIdleTimeMillis = 60 * 30 * 1000L * 7; //默认值为3.5小时
	//testWhileIdle的判断依据，详细看testWhileIdle属性的说明
	//DestroyConnectionThread线程启动检测连接池中连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接
	//注意1：需要保证在两个清理周期之内超时（空闲时长>数据库wait_time）链接一定会被清理掉
	//注意2：maxEvictableIdleTimeMillis-minEvictableIdleTimeMillis>timeBetweenEvictionRunsMillis
	//注意3：也可通过设置phyTimeoutMillis参数规避Druid池中存在数据库侧已主动关闭的无效链接的情况
	private long timeBetweenEvictionRunsMillis = 45 * 1000L; //默认值为1分钟
	
	
	//配置removeAbandoned对性能会有一些影响，建议怀疑存在泄漏之后再打开
	//打开该功能有一定的性能影响，不建议在生产环境中使用，仅用于连接泄露检测诊断
	//当removeAbandoned=true之后，可以在内置监控界面datasource.html中的查看ActiveConnection StackTrace属性
	////可以看到未关闭连接的具体堆栈信息，从而方便查出哪些连接泄漏了
	//如果你的应用配置了WebStatFilter，在内置监控页面weburi-detail.html中
	////查看JdbcPoolConnectionOpenCount和JdbcPoolConnectionCloseCount属性，如果不相等，就是泄漏了
	private boolean removeAbandoned = false; //默认值为false
	//在removeAbandoned开启的情况下，触发主动归还的时间间隔
	private int removeAbandonedTimeout = 300; //默认值为300秒
	//关闭abanded连接时是否输出到错误日志
	private boolean logAbandoned = false; //默认值为false
	
	//当数据库抛出一些不可恢复的异常时，抛弃连接
	//会根据不同的driver添加不同的exceptionSorter
	//例如：MySqlExceptionSorter
	private String exceptionSorter = null;
	//在验证连接的连通性时，会使用倒该类
	//会根据不同的driver添加不同的validConnectionChecker
	//例如：MySqlValidConnectionChecker
	private String validConnectionChecker = null;
	
	/////////////////////////////////SQL提交、SQL时长等/////////////////////////////////////
	
//	private String defaultCatalog = null; //MySQL中，schema和catalog是一个概念，表示数据库
	
	//物理连接初始化的时候执行的sql
	private List<String> connectionInitSqls = null; //默认值为null
		
	private boolean defaultAutoCommit = true; //自动提交
	private boolean defaultReadOnly = false; //是否只读
	
	//如果配置了该事务隔离级别，并且和MySQL的事务隔离级别不一样，则将以该隔离级别为准
	private Integer defaultTransactionIsolation = null; //默认值为null
	//事务执行时长的阈值
	//当transactionThresholdMillis>0并且事务实际执行时长>transactionThresholdMillis时，会在日志中打印出来
	private long transactionThresholdMillis = 0L; //默认值为0
	//查询的超时时长
	private int queryTimeout; //默认值为0，单位为秒
	//在获取事务查询超时时长时，如果该值设置<=0，则返回queryTimeout的值
	private int transactionQueryTimeout; //默认值为0，单位为秒
	
	///////////////////////////////////////////过滤器/////////////////////////////////////////////////////////
	
	//属性类型是字符串，通过别名的方式配置扩展插件
	//常用的插件有：
	////监控统计用的filter:stat
	////日志用的filter:log4j
	////防御sql注入的filter:wall
	private String filters = null; //默认值为null
	//类型是List<com.alibaba.druid.filter.Filter>
	//如果同时配置了filters和proxyFilters，是组合关系，并非替换关系
	private List<Filter> proxyFilters = null;
	private boolean clearFiltersEnable = true; //默认值为true
	
	//DruidDataSource是通过DruidDataSourceStatLoggerImpl来实现输入监控数据到日志的
	////输出日志的方法是logStats()，该方法调用getStatValueAndReset()获取需要输出的值
	////在init()中创建LogStasThread线程并start()
	////内部线程类LogStatsThread每隔timeBetweenLogStatsMillis会调用logStats()一次
	//默认值：DruidDataSrouce的父类DruidAbstractDataSource中的statLogger参数的默认实现就是DruidDataSourceStatLoggerImpl
	//可以自定义一个StatLogger，继承DruidDataSourceStatLoggerAdapter
	//DruidAbstractDataSource中可以设置自定义实现类的ClassName或者直接注入实例
	private String statLoggerClassName = null;
	private DruidDataSourceStatLogger statLogger = new DruidDataSourceStatLoggerImpl();
	//配置timeBetweenLogStatsMillis>0之后，DruidDataSource会定期把监控数据输出到日志中，单位：毫秒
	private long timeBetweenLogStatsMillis = 0L; //默认值：0
	
	///////////////////////////////////////////底层类///////////////////////////////////////////////////////////
	
	//初始化时开启异步创建连接
	//如果有initialSize数量较多时，打开会加快应用启动时间
	private boolean asyncInit = true; //默认值为false
	//是否异步关闭连接
	private boolean asyncCloseConnectionEnable = false; //默认值为false
	//获取连接出错时是否马上返回错误
	private boolean failFast = false; //默认值为false
	
	//是否允许访问底层连接
	//在源码中没有找到这个参数使用的地方，尴尬
	private boolean accessToUnderlyingConnectionAllowed = true; //默认值为true
	
	//不再使用，一个DruidDataSource只支持一个EvictionRun
//	private int numTestsPerEvictionRun = DruidDataSource.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
	
	//物理连接超时时间
	private long phyTimeoutMillis = -1; //默认值为-1
	//物理最大连接数，不建议配置，已有maxActive
	private long phyMaxUseCount = -1; //默认值为-1
	
	//创建连接的调度器
	//当有等待连接并且连接总数小于maxActive时，则会马上创建线程
	//在DruidDataSource中的createAndStartCreatorThread()方法中，如果该参数为null，则创建一个CreateConnectionThread用于创建连接
	private ScheduledExecutorService createScheduler;
	//销毁连接的调度器
	//该调度器执行的间隔timeBetweenEvictionRunsMillis
	//如果timeBetweenEvictionRunsMillis<=0，则调度默认执行间隔为1秒
	//在DruidDataSource中的createAndStartDestroyThread()方法中，如果该参数为null，则创建一个DestroyConnectionThread用于创建连接
	private ScheduledExecutorService destroyScheduler;
	
	//连接出错后重试时间间隔
	//源码中，线程使用了无参for循环再一直尝试进行数据源连接
	//当满足条件就会进行重试连接，errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0
	private long timeBetweenConnectErrorMillis = 5 * 1000L; //默认值为500毫秒
	//如果配置的timeBetweenConnectErrorMillis>0：
	////第N+1次开始重试创建createPhysicalConnection()需要等待timeBetweenConnectErrorMillis的间隔
	//假设设置该属性为1，那么第1次创建连接失败后马上就会开始第二次尝试，而第2次创建连接开始则需要等待timeBetweenConnectErrorMillis
	//如果配置的timeBetweenConnectErrorMillis<=0：
	////则每次都会马上开始是重新创建连接
	private int connectionErrorRetryAttempts = 1; //默认值为1
	//当errorCount>connectionErrorRetryAttempts并且timeBetweenConnectErrorMillis>0时
	//如果该值还是true，则不会进行连接重试
	private boolean breakAfterAcquireFailure = false; //默认值为false
	
	//当出现socket的读超时时，是否马上kill掉该连接
	private boolean killWhenSocketReadTimeout = false; //默认值为false
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//是否缓存preparedStatement，也就是PSCache
	//PSCache对支持游标的数据库性能提升巨大，比如说oracle
	//在mysql下建议关闭
	private boolean poolPreparedStatements = false; //默认值为false
	//要启用PSCache，必须配置大于0
	//在set大于0的该值时，poolPreparedStatements自动触发修改为true
	//在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
	private int maxPoolPreparedStatementPerConnectionSize = -1; //默认值为10
	//和maxPoolPreparedStatementPerConnectionSize可以理解为同一个参数
	private int maxOpenPreparedStatements = -1; //默认值为-1
	//	private boolean sharePreparedStatements = false;
	
	///////////////////////////////////////////////Web页面功能，监控//////////////////////////////////////////////
	//Web页面的重置统计数据是否启用
	private boolean resetStatEnable = true; //默认值为true
	//合并多个DruidDataSource的监控数据
	private boolean useGlobalDataSourceStat = false; //默认值为false
}
