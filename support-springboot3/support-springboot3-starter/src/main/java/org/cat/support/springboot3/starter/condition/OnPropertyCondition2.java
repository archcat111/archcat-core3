package org.cat.support.springboot3.starter.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionMessage.Style;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 
 * @author 王云龙
 * @date 2021年9月9日 下午2:43:23
 * @version 1.0
 * @description {@linkplain ConditionalOnProperty2}的实现类
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
public class OnPropertyCondition2 extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		List<AnnotationAttributes> allAnnotationAttributes = annotationAttributesFromMultiValueMap(
				metadata.getAllAnnotationAttributes(ConditionalOnProperty2.class.getName()));
		List<ConditionMessage> noMatch = new ArrayList<>();
		List<ConditionMessage> match = new ArrayList<>();
		for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
			ConditionOutcome outcome = determineOutcome(annotationAttributes, context.getEnvironment());
			(outcome.isMatch() ? match : noMatch).add(outcome.getConditionMessage());
		}
		if (!noMatch.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
		}
		return ConditionOutcome.match(ConditionMessage.of(match));
	}

	private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
			MultiValueMap<String, Object> multiValueMap) {
		List<Map<String, Object>> maps = new ArrayList<>();
		multiValueMap.forEach((key, value) -> {
			for (int i = 0; i < value.size(); i++) {
				Map<String, Object> map;
				if (i < maps.size()) {
					map = maps.get(i);
				}
				else {
					map = new HashMap<>();
					maps.add(map);
				}
				map.put(key, value.get(i));
			}
		});
		List<AnnotationAttributes> annotationAttributes = new ArrayList<>(maps.size());
		for (Map<String, Object> map : maps) {
			annotationAttributes.add(AnnotationAttributes.fromMap(map));
		}
		return annotationAttributes;
	}

	private ConditionOutcome determineOutcome(AnnotationAttributes annotationAttributes, PropertyResolver resolver) {
		Spec spec = new Spec(annotationAttributes);
		List<String> missingProperties = new ArrayList<>();
		List<String> nonMatchingProperties = new ArrayList<>();
		spec.collectProperties(resolver, missingProperties, nonMatchingProperties);
		if (!missingProperties.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
					.didNotFind("property", "properties").items(Style.QUOTE, missingProperties));
		}
		if (!nonMatchingProperties.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
					.found("different value in property", "different value in properties")
					.items(Style.QUOTE, nonMatchingProperties));
		}
		return ConditionOutcome
				.match(ConditionMessage.forCondition(ConditionalOnProperty.class, spec).because("matched"));
	}

	private static class Spec {

		private final String prefix;

		private final String havingValue;
		
		private final String[] names;

		private final boolean matchIfMissing;

		Spec(AnnotationAttributes annotationAttributes) {
			String prefix = annotationAttributes.getString("prefix").trim();
			if (StringUtils.hasText(prefix) && !prefix.endsWith(".")) {
				prefix = prefix + ".";
			}
			this.prefix = prefix;
			this.havingValue = annotationAttributes.getString("havingValue");
			this.names = getNames(annotationAttributes);
			this.matchIfMissing = annotationAttributes.getBoolean("matchIfMissing");
		}

		private String[] getNames(Map<String, Object> annotationAttributes) {
			String[] value = (String[]) annotationAttributes.get("value");
			String[] name = (String[]) annotationAttributes.get("name");
			Assert.state(value.length > 0 || name.length > 0,
					"The name or value attribute of @ConditionalOnProperty must be specified");
			Assert.state(value.length == 0 || name.length == 0,
					"The name and value attributes of @ConditionalOnProperty are exclusive");
			return (value.length > 0) ? value : name;
		}

		private void collectProperties(PropertyResolver resolver, List<String> missing, List<String> nonMatching) {
			for (String name : this.names) {
				String key = this.prefix + name; 
				if (resolver.containsProperty(key)) {//cat.support3.web.log.event.out
					//如果Yml中配置的value是String，判断是否全匹配，不匹配大小写
					if (!isMatch(resolver.getProperty(key), this.havingValue)) {
						nonMatching.add(name);
					}
				}else if(resolver.containsProperty(key+"[0]")) {//cat.support3.web.log.event.out[0]
					//如果Yml中配置的value是List<String>，判断是否包含该项，不匹配大小写
					List<String> propertyListValue = Lists.newArrayList();
					int index = 0;
					while(true) {
						String value = resolver.getProperty(key+"["+index+"]");
						if(value==null) {
							break;
						}
						propertyListValue.add(value);
						index++;
					}
					if (!isMatchListContainValue(propertyListValue, this.havingValue)) {
						nonMatching.add(name);
					}
				}
				else {
					if (!this.matchIfMissing) {
						missing.add(name);
					}
				}
			}
		}

		private boolean isMatch(String value, String requiredValue) {
			if (StringUtils.hasLength(requiredValue)) {
				return requiredValue.equalsIgnoreCase(value);
			}
			return !"false".equalsIgnoreCase(value);
		}
		
		/**
		 * 
		 * @author wangyunlong
		 * @date 2021年9月9日 下午2:56:42
		 * @version 1.0
		 * @description 判断List<String>中是否包含 requiredValue
		 * @param values
		 * @param requiredValue
		 * @return
		 */
		private boolean isMatchListContainValue(List<String> values, String requiredValue) {
			if (StringUtils.hasLength(requiredValue)) {
				for (int i = 0; i < values.size(); i++) {
					String value = values.get(i);
					if(requiredValue.equalsIgnoreCase(value)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("(");
			result.append(this.prefix);
			if (this.names.length == 1) {
				result.append(this.names[0]);
			}
			else {
				result.append("[");
				result.append(StringUtils.arrayToCommaDelimitedString(this.names));
				result.append("]");
			}
			if (StringUtils.hasLength(this.havingValue)) {
				result.append("=").append(this.havingValue);
			}
			result.append(")");
			return result.toString();
		}

	}

}
