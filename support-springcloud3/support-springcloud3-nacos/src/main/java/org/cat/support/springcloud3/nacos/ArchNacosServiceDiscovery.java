package org.cat.support.springcloud3.nacos;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.google.common.collect.Lists;

import cn.hutool.core.util.StrUtil;

public class ArchNacosServiceDiscovery extends NacosServiceDiscovery {
	
	private NacosDiscoveryProperties discoveryProperties;
	private NacosServiceManager nacosServiceManager;
	
	private ArchNacosDiscoveryProperties nacosDiscoveryPlusProperties = new ArchNacosDiscoveryProperties();
	
	public static final String DELIMITER = ".";

	public ArchNacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties,
			NacosServiceManager nacosServiceManager) {
		this(discoveryProperties, nacosServiceManager, null);
	}
	
	public ArchNacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties,
			NacosServiceManager nacosServiceManager, ArchNacosDiscoveryProperties nacosDiscoveryPlusProperties) {
		super(discoveryProperties, nacosServiceManager);
		this.discoveryProperties = discoveryProperties;
		this.nacosServiceManager = nacosServiceManager;
		Optional.ofNullable(nacosDiscoveryPlusProperties)
			.ifPresent( properties -> this.nacosDiscoveryPlusProperties = properties);
	}
	
	@Deprecated
	private NamingService namingService() {
		return nacosServiceManager
				.getNamingService(discoveryProperties.getNacosProperties());
	}
	
	/**
	 * 支持两种形式
	 * 1：对于nacos默认的调用，会调用getServices方法，该方法会更新serviceIdToGroup，即serviceId和group的对应关系
	 * 2：对于想要自行获取一个服务id的所有实例，可以传递<group>.<serviceId>.<扩展点>的字符串
	 */
	@Override
	public List<ServiceInstance> getInstances(String serviceId) throws NacosException {
		String realGroup = null;
		String realServiceId = serviceId;
		List<Instance> instances = null;
		//如果自定义配置中没有将restrictedToGroup设置为false，即默认值为true，则使用nacos默认逻辑
		//nacos默认逻辑，即：使用项目自身的group来获取实例
		if(this.nacosDiscoveryPlusProperties.isRestrictedToGroup()) {
			realGroup = this.discoveryProperties.getGroup();
		}else if(StringUtils.isBlank(this.nacosDiscoveryPlusProperties.getIncloudGroups())) {
			//如果自定义配置中没有配置允许跨的group，则使用项目自身的group
			realGroup = this.discoveryProperties.getGroup();
		}else {
			//如果需要单独拿到某个服务的所有节点，则可以使用arch-base.app-user3-impl.xxx的形式
			//第一个字符串为group，第二个字符串为服务id，第三个字符串为扩展点，当前无用
			//例如CommodityFeignClient的注解@FeignClient的name属性配置的参数为：arch-business.app-commodity3-impl..commodity
			////则这里的serviceId就是arch-business.app-commodity3-impl..commodity
			List<String> groupServiceList = StrUtil.splitTrim(serviceId, "."); 
			if(groupServiceList.size()==3 || groupServiceList.size()==2) {
				realGroup = groupServiceList.get(0);
				//自定义配置中允许跨的group有哪些
				List<String> groupList = StrUtil.splitTrim(this.nacosDiscoveryPlusProperties.getIncloudGroups(), ",");
				if(!groupList.contains(realGroup)) {
					throw new NacosException(0, "不允许访问group["+realGroup+"]的服务");
				}
				realServiceId = groupServiceList.get(1);
			}else {
				//如果是spring使用普通的serviceId访问
				realGroup = this.discoveryProperties.getGroup();
			}
		}
		
		if(StringUtils.isBlank(realGroup)) {
			return null;
		}
		//获取实例列表，同时加入serviceInfoUpdateService的定时监听任务
		//当实例变化的时候，会自动更新到serviceInfoHolder，不需要再访问nacos注册中心
		instances = namingService().selectInstances(realServiceId, realGroup, true, true);
		
		return hostToServiceInstanceList(instances, realServiceId);
	}
	
	//获取允许访问的所有group中的所有serviceId
	@Override
	public List<String> getServices() throws NacosException {
		String group = null;
		ListView<String> services = null;
		//如果自定义配置中没有将restrictedToGroup设置为false，即默认值为true，则使用nacos默认逻辑
		//nacos默认逻辑，即：使用项目自身的group来获取实例
		if(this.nacosDiscoveryPlusProperties.isRestrictedToGroup()) {
			group = this.discoveryProperties.getGroup();
			services = namingService().getServicesOfServer(1,Integer.MAX_VALUE, group);
			escapeServiceAddGroup(services, group);
		}else if(StringUtils.isBlank(this.nacosDiscoveryPlusProperties.getIncloudGroups())) {
			//如果自定义配置中没有配置允许跨的group，则使用项目自身的group
			group = this.discoveryProperties.getGroup();
			services = namingService().getServicesOfServer(1,Integer.MAX_VALUE, group);
			escapeServiceAddGroup(services, group);
		} else {
			//自定义配置中允许跨的group有哪些
			List<String> groupList = StrUtil.splitTrim(this.nacosDiscoveryPlusProperties.getIncloudGroups(), ",");
			//获取自定义配置中允许跨的每个group中存在的serviceId
			services = new ListView<>();
			services.setData(Lists.newArrayList());
			services.setCount(0);
			for (String group0 : groupList) {
				ListView<String> services0 = namingService().getServicesOfServer(1,Integer.MAX_VALUE, group0);
				escapeServiceAddGroup(services0, group0);
				if(services0.getCount()>0) {
					services.getData().addAll(services0.getData());
					services.setCount(services.getCount()+services0.getCount());
				}
			}
		}
		//将允许访问的所有group中的所有serviceId返回
		return services.getData();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月28日 上午11:02:36
	 * @version 1.0
	 * @description 
	 * 		原始的List中存储：app-user-impl, app-matter-impl 
	 * 		转换后List中存储：<group>.app-user-impl, <group>.app-matter-impl 
	 * @param services
	 * @param groupName
	 */
	private void escapeServiceAddGroup(ListView<String> services, String groupName) {
		List<String> newServiceNameList = Lists.newArrayList();
		services.getData().forEach(serviceName -> {
			newServiceNameList.add(groupName+DELIMITER+serviceName);
		});
		services.setData(newServiceNameList);
	}
	
}
