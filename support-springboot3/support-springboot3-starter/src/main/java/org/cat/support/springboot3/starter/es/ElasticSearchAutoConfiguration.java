package org.cat.support.springboot3.starter.es;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpHost;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.search3.generator.SupportSearch3ConditionalFlag;
import org.cat.support.search3.generator.es.ESClientHolder;
import org.cat.support.search3.generator.exception.ArchSearch3Exception;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@ConditionalOnClass({SupportSearch3ConditionalFlag.class, ElasticsearchClient.class})
@ConditionalOnProperty(prefix = "cat.support3.search", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({SearchProperties.class})
public class ElasticSearchAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "ESClientHolder初始化：";
	
	@Autowired
	private SearchProperties searchProperties;
	@Autowired
	private ApplicationContext applicationContext;
	
	@ConditionalOnMissingBean(ESClientHolder.class)
	@Bean
	public ESClientHolder esClientHolder() {
		ESClientHolder esClientHolder = new ESClientHolder();
		Map<String, ESGeneratorProperties> elasticSearchProperties = searchProperties.getElasticSearch();
		
		if(MapUtils.isEmpty(elasticSearchProperties)) {
			return esClientHolder;
		}
		
		elasticSearchProperties.forEach((esName, esGeneratorProperties) -> {
			if(!esGeneratorProperties.isEnabled()) {
				return;
			}
			
			ElasticsearchClient elasticsearchClient = elasticsearchClient(esName, esGeneratorProperties);
			esClientHolder.put(esName, elasticsearchClient);
			this.coreLogger.info(this.logPrefix+"创建ElasticsearchClient：["+esName+"] 成功");
		});
		
		return esClientHolder;
	}
	
	private ElasticsearchClient elasticsearchClient(String esName, ESGeneratorProperties esGeneratorProperties) {
		List<Node> listNode = Lists.newArrayList();
		esGeneratorProperties.getNodes().forEach(propNode -> {
			Node node = new Node(new HttpHost(propNode.getHost(), propNode.getPort()));
			listNode.add(node);
		});
		if(listNode.size()==0) {
			throw new ArchSearch3Exception("创建ElasticsearchClient ["+esName+"]失败，请检查node配置");
		}
		RestClient restClient = RestClient.builder(listNode.toArray(new Node[listNode.size()])).build();
		JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper();
		TransportOptions transportOptions = null;
		ElasticsearchTransport elasticsearchTransport = new RestClientTransport(restClient, jacksonJsonpMapper, transportOptions);
		ElasticsearchClient elasticsearchClient = new ElasticsearchClient(elasticsearchTransport);
		
		ArchSpringBeanUtil.registerBean(applicationContext, esName, elasticsearchClient);
		return applicationContext.getBean(esName, ElasticsearchClient.class);
	}
}
