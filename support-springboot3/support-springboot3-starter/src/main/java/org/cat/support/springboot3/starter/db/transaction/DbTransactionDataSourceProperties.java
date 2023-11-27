package org.cat.support.springboot3.starter.db.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbTransactionDataSourceProperties {
	private boolean enabled = true;
}
