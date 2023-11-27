package org.cat.support.log3.generator.id;

import org.cat.core.web3.log.id.ILogIdGenerator;
import org.cat.support.id3.generator.IIdGenerator;

public abstract class AbsLogIdGenerator implements ILogIdGenerator {
	private IIdGenerator idGenerator;

	protected IIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
}
