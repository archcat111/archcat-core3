package org.cat.support.db3.generator.mybatis.plus;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

public class ArchMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
//		Date now = new Date();
//		this.setFieldValByName("createTimestamp", now, metaObject);
//		this.setFieldValByName("lastUpdateTimestamp", now, metaObject);
		
		LocalDateTime now = LocalDateTime.now();
		this.strictInsertFill(metaObject, "createTimestamp", LocalDateTime.class, now);
		this.strictInsertFill(metaObject, "lastUpdateTimestamp", LocalDateTime.class, now);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
//		Date now = new Date();
//		this.setFieldValByName("lastUpdateTimestamp", now, metaObject);
		
		LocalDateTime now = LocalDateTime.now();
		this.strictUpdateFill(metaObject, "lastUpdateTimestamp", LocalDateTime.class, now);
	}

}
