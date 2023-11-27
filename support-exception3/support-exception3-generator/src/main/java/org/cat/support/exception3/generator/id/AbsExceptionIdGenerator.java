package org.cat.support.exception3.generator.id;

import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.support.id3.generator.IIdGenerator;

public abstract class AbsExceptionIdGenerator implements IExceptionIdGenerator {

	private IIdGenerator idGenerator;

	protected IIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(IIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

}
