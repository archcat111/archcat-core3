package org.cat.support.springboot3.starter.es;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ESGeneratorProperties {
	private boolean enabled = true;
	
	private List<Node> nodes;
	
	@Getter
	@Setter
	public static class Node {
		private String host;
		private Integer port;
	}
}
