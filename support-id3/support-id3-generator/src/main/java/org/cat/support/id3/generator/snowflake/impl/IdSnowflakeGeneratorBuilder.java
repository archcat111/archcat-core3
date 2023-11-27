package org.cat.support.id3.generator.snowflake.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.support.id3.generator.snowflake.IIdSnowflakeGenerator;
import org.cat.support.id3.generator.snowflake.core.BitsAllocator;

public class IdSnowflakeGeneratorBuilder {
	
	private int deltaSecondBits = 28;
	private int workerIdBits = 22;
	private int sequenceBits = 13;
	
	private long workerId = 0;
//	private String startDateStr = "2021-10-20";
	private long startDateSeconds = TimeUnit.MILLISECONDS.toSeconds(1634659200000L);
	
	private IdSnowflakeGeneratorBuilder() {
	}

	public IdSnowflakeGeneratorBuilder setDeltaSecondBits(int deltaSecondBits) {
		this.deltaSecondBits = deltaSecondBits;
		return this;
	}

	public IdSnowflakeGeneratorBuilder setWorkerIdBits(int workerIdBits) {
		this.workerIdBits = workerIdBits;
		return this;
	}

	public IdSnowflakeGeneratorBuilder setSequenceBits(int sequenceBits) {
		this.sequenceBits = sequenceBits;
		return this;
	}
	
	public IdSnowflakeGeneratorBuilder setWorkerId(long workerId) {
		this.workerId = workerId;
		return this;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月13日 上午10:34:59
	 * @version 1.0
	 * @description 设置算法的起始时间
	 * @param startDateStr example：2021-10-20  
	 * @return
	 */
	public IdSnowflakeGeneratorBuilder setStartDateStr(String startDateStr) {
        if (StringUtils.isNotBlank(startDateStr)) {
//            this.startDateStr = startDateStr;
            long startDateSeconds = ArchDateTimeUtil.getFormatDate(startDateStr, ArchDateTimeUtil.FormatConstants.DATE_NORMAL).getTime();
            this.startDateSeconds = TimeUnit.MILLISECONDS.toSeconds(startDateSeconds);
        }
        return this;
    }
	
	public static IdSnowflakeGeneratorBuilder create() {
		IdSnowflakeGeneratorBuilder idSnowflakeGeneratorBuilder = new IdSnowflakeGeneratorBuilder();
		return idSnowflakeGeneratorBuilder;
	}
	
	public IIdSnowflakeGenerator build() {
		IdSnowflakeGeneratorImpl idSnowflakeGeneratorImpl = new IdSnowflakeGeneratorImpl();
		idSnowflakeGeneratorImpl.setWorkerId(this.workerId);
		idSnowflakeGeneratorImpl.setStartDateSeconds(startDateSeconds);
		
		BitsAllocator bitsAllocator = new BitsAllocator(this.deltaSecondBits, this.workerIdBits, this.sequenceBits);
		idSnowflakeGeneratorImpl.setBitsAllocator(bitsAllocator);
		return idSnowflakeGeneratorImpl;
	}
}
