package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.shardingsphere.governance.repository.zookeeper.props.ZookeeperPropertyKey;
import org.cat.support.db3.generator.constants.ShardingConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午5:30:35
 * @version 1.0
 * @description ShardingSphereRegister的相关配置
 *	properties(Zookeeper)：{@linkplain ZookeeperPropertyKey}
 *	properties(ETCD)：{@linkplain EtcdPropertyKey}
 */
@Getter
@Setter
public class ShardingRegisterCenterProperties {
	private boolean enabled = true;
	
	private String type = ShardingConstants.RegisterCenterName.AUTO; 
	private String serverLists = null;
	private Map<String, String> props = Maps.newHashMap();
	private boolean overWrite = true;
	
}
