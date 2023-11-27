package org.cat.support.db3.generator.constants;

import org.apache.shardingsphere.governance.repository.etcd.EtcdRepository;
import org.apache.shardingsphere.governance.repository.spi.RegistryCenterRepository;
import org.apache.shardingsphere.governance.repository.zookeeper.CuratorZookeeperRepository;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.swapper.strategy.ShardingStrategyConfigurationYamlSwapper;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 上午11:31:31
 * @version 1.0
 * @description Shardingsphere相关的常量
 *
 */
public class ShardingConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月17日 上午11:32:30
	 * @version 1.0
	 * @description 当需要将引入Shardingsphere-JDBC的应用服务纳入到Shardingsphere的集群管理中时
	 * 		就需要引入一个注册中心，Shardingsphere原生实现支持的注册中心有Zookeeper和Etcd
	 * 		实现类分别是{@linkplain CuratorZookeeperRepository}和{@linkplain EtcdRepository}
	 * 		这两个类都实现了接口{@linkplain RegistryCenterRepository}
	 * 		{@linkplain RegistryCenterRepository}继承了{@linkplain TypedSPI}
	 * 		{@linkplain TypedSPI}有一个方法是getType()
	 * 
	 * 		在使用GovernanceShardingSphereDataSourceFactory.createDataSource创建Datasource时
	 * 		需要传入参数GovernanceConfiguration，而构建GovernanceConfiguration时
	 * 		需要传入参数如：{@code new RegistryCenterConfiguration("ZooKeeper", "localhost:2181", new Properties());}
	 * 		在GovernanceShardingSphereDataSourceFactory.createDataSource中，会new GovernanceShardingSphereDataSource
	 * 		此时会创建createGovernanceFacade并调用其init()方法
	 * 		在该方法中会通过传入的GovernanceConfiguration创建RegistryCenterRepository
	 * 		在创建RegistryCenterRepository时会通过RegistryCenterConfiguration中用户传入的type和TypedSPIRegistry中已经注册
	 * 		的XxxRegistryCenterRepository的type进行比较确认具体的XxxRegistryCenterRepository
	 * 
	 * 		如CuratorZookeeperRepository中的getType()方法返回的就是"ZooKeeper"（不区分大小写，因为比较的时候都转换为了小写）
	 * 		{@code ShardingSphereServiceLoader.newServiceInstances(typedSPIClass).stream().filter(each -> each.getType().equalsIgnoreCase(type)).findFirst();}
	 *
	 */
	public static final class RegisterCenterName {
		public static final String AUTO = "auto";
		public static final String ZOOKEEPER = "ZooKeeper";
		public static final String ETCD = "etcd";
	}
	
	public static final class RegisterCenterClassName {
		public static final String ZOOKEEPER = "org.apache.shardingsphere.governance.repository.zookeeper.CuratorZookeeperRepository";
		public static final String ETCD = "org.apache.shardingsphere.governance.repository.etcd.EtcdRepository";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月18日 下午4:12:16
	 * @version 1.0
	 * @description 算法的类型
	 * 如：InlineShardingAlgorithm的typeName叫INLINE
	 * ShardingSphere内置的算法实现有：
	 * 		BoundaryBasedRangeShardingAlgorithm：BOUNDARY_RANGE
	 * 		VolumeBasedRangeShardingAlgorithm：VOLUME_RANGE
	 * 		AutoIntervalShardingAlgorithm：AUTO_INTERVAL
	 * 		ClassBasedShardingAlgorithm：CLASS_BASED
	 * 		HashModShardingAlgorithm：HASH_MOD
	 * 		InlineShardingAlgorithm：INLINE
	 * 		IntervalShardingAlgorithm：INTERVAL
	 * 		ModShardingAlgorithm：MOD
	 */
	public static final class ShardingAlgorithmType {
		public static final String BOUNDARY_RANGE = "BOUNDARY_RANGE";
		public static final String VOLUME_RANGE = "VOLUME_RANGE";
		public static final String COMPLEX_INLINE = "COMPLEX_INLINE";
		public static final String AUTO_INTERVAL = "AUTO_INTERVAL";
		public static final String CLASS_BASED = "CLASS_BASED";
		public static final String HINT_INLINE = "HINT_INLINE";
		public static final String HASH_MOD = "HASH_MOD";
		public static final String INLINE = "INLINE";
		public static final String INTERVAL = "INTERVAL";
		public static final String MOD = "MOD";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月18日 下午4:14:01
	 * @version 1.0
	 * @description 算法策略
	 * 		{@linkplain ShardingStrategyConfigurationYamlSwapper#swapToObject}
	 * 		{@linkplain StandardShardingStrategyConfiguration}
	 * 		{@linkplain ComplexShardingStrategyConfiguration}
	 * 		{@linkplain HintShardingStrategyConfiguration}
	 * 		{@linkplain NoneShardingStrategyConfiguration}
	 * 		默认实现为：根据用户配置的名称初始化具体的ShardingStrategy
	 * 		但是因为这些ShardingStrategyConfiguration没有默认的构造器并且不同的实现构造器也不一样
	 * 		因此只能通过判断配置文件中的ShardingStrategyName来初始化不同的ShardingStrategyConfiguration，而不能通过反射
	 * 		并且这些名字ShardingSphere并没有提供常量类，因此自己写一个
	 *
	 */
	public static final class ShardingStrategyName {
		public static final String STANDARD = "STANDARD";
		public static final String COMPLEX = "COMPLEX";
		public static final String HINT = "HINT";
		public static final String NONE = "NONE";
	}
	
	public static final class RwType {
		public static final String STATIC = "STATIC";
		public static final String DYNAMIC = "DYNAMIC";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月23日 下午4:19:22
	 * @version 1.0
	 * @description 读写分离中的多个读库之间的负载均衡算法类型
	 *
	 */
	public static final class RwLoadBalanceType {
		public static final String ROUND_ROBIN = "ROUND_ROBIN";
		public static final String RANDOM = "RANDOM";
		public static final String WEIGHT = "WEIGHT";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2022年4月13日 下午6:28:44
	 * @version 1.0
	 * @description 负载均衡器的名称
	 *
	 */
	public static final class RwLoadBalanceName {
		public static final String DEFAULT = "defaultLoadBalancer";
	}
}
