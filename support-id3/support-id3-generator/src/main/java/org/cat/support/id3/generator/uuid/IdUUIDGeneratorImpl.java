package org.cat.support.id3.generator.uuid;

import java.util.UUID;

import org.cat.support.id3.generator.IIdGenerator;

public class IdUUIDGeneratorImpl implements IIdGenerator {

	@Override
	public long getLongId() {
		throw new UnsupportedOperationException("当前操作不被支持");
	}

	@Override
	public String getStrId() {
		//6b7b417c-0f3b-4fcd-91d0-a5868b7d05c6
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

}
