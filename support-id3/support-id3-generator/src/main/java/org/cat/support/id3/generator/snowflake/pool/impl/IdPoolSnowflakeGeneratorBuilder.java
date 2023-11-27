package org.cat.support.id3.generator.snowflake.pool.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.support.id3.generator.pool.IIdPool;
import org.cat.support.id3.generator.pool.IIdPoolPutExecutor;
import org.cat.support.id3.generator.pool.impl.IdPoolImpl;
import org.cat.support.id3.generator.pool.impl.IdPoolPutExecutorImpl;
import org.cat.support.id3.generator.snowflake.core.BitsAllocator;
import org.cat.support.id3.generator.snowflake.pool.IIdPoolSnowflakeGenerator;

public class IdPoolSnowflakeGeneratorBuilder {
	
	private int deltaSecondBits = 28;
	private int workerIdBits = 22;
	private int sequenceBits = 13;
	
	private long workerId = 0;
//	private String startDateStr = "2021-10-20";
	private long startDateSeconds = TimeUnit.MILLISECONDS.toSeconds(1634659200000L);
	
	/**Id Pool配置**/
	private int poolSizeBoostPower = 3; //用于计算IdPool的Size
	private int poolPaddingThresholdPercentage = 50; //当id剩余XX%时开始进行newId的填充
	
	/**填充调度配置**/
	private boolean usingSchedule = false;
	private Long scheduleInterval = 5 * 60L;
	
	private IdPoolSnowflakeGeneratorBuilder() {
	}

	public IdPoolSnowflakeGeneratorBuilder setDeltaSecondBits(int deltaSecondBits) {
		this.deltaSecondBits = deltaSecondBits;
		return this;
	}

	public IdPoolSnowflakeGeneratorBuilder setWorkerIdBits(int workerIdBits) {
		this.workerIdBits = workerIdBits;
		return this;
	}

	public IdPoolSnowflakeGeneratorBuilder setSequenceBits(int sequenceBits) {
		this.sequenceBits = sequenceBits;
		return this;
	}
	
	public IdPoolSnowflakeGeneratorBuilder setWorkerId(long workerId) {
		this.workerId = workerId;
		return this;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午2:19:21
	 * @version 1.0
	 * @description 设置算法的起始时间 
	 * @param startDateStr example：2021-10-20
	 * @return
	 */
	public IdPoolSnowflakeGeneratorBuilder setStartDateStr(String startDateStr) {
        if (StringUtils.isNotBlank(startDateStr)) {
//            this.startDateStr = startDateStr;
            long startDateSeconds = ArchDateTimeUtil.getFormatDate(startDateStr, ArchDateTimeUtil.FormatConstants.DATE_NORMAL).getTime();
            this.startDateSeconds = TimeUnit.MILLISECONDS.toSeconds(startDateSeconds);
        }
        return this;
    }
	
	public IdPoolSnowflakeGeneratorBuilder setPoolSizeBoostPower(int poolSizeBoostPower) {
		this.poolSizeBoostPower = poolSizeBoostPower;
		return this;
	}
	
	public IdPoolSnowflakeGeneratorBuilder setPoolPaddingThresholdPercentage(int poolPaddingThresholdPercentage) {
		this.poolPaddingThresholdPercentage = poolPaddingThresholdPercentage;
		return this;
	}
	
	public IdPoolSnowflakeGeneratorBuilder setUsingSchedule(boolean usingSchedule) {
		this.usingSchedule = usingSchedule;
		return this;
	}
	
	public IdPoolSnowflakeGeneratorBuilder setScheduleInterval(long scheduleInterval) {
		this.scheduleInterval = scheduleInterval;
		return this;
	}
	
	public static IdPoolSnowflakeGeneratorBuilder create() {
		IdPoolSnowflakeGeneratorBuilder idPoolSnowflakeGeneratorBuilder = new IdPoolSnowflakeGeneratorBuilder();
		return idPoolSnowflakeGeneratorBuilder;
	}
	
	public IIdPoolSnowflakeGenerator build() {
		IdPoolSnowflakeGeneratorImpl idPoolSnowflakeGeneratorImpl = new IdPoolSnowflakeGeneratorImpl();
		idPoolSnowflakeGeneratorImpl.setWorkerId(this.workerId);
		idPoolSnowflakeGeneratorImpl.setStartDateSeconds(startDateSeconds);
		
		BitsAllocator bitsAllocator = new BitsAllocator(this.deltaSecondBits, this.workerIdBits, this.sequenceBits);
		idPoolSnowflakeGeneratorImpl.setBitsAllocator(bitsAllocator);
		
		int poolSize = ((int) bitsAllocator.getMaxSequence() + 1) << poolSizeBoostPower;
		IIdPool idPool = new IdPoolImpl(poolSize, this.poolPaddingThresholdPercentage);
		idPool.setPoolArrivePaddingThresholdHandler(idPoolSnowflakeGeneratorImpl::poolArrivePaddingThresholdHandler);
		idPool.setPoolRejectedPutHandler(idPoolSnowflakeGeneratorImpl::poolRejectedPutHandler);
		idPool.setPoolRejectedTakeHandler(idPoolSnowflakeGeneratorImpl::poolRejectedTakeHandler);
		idPoolSnowflakeGeneratorImpl.setIdPool(idPool);
		
		IIdPoolPutExecutor idPoolPutExecutor = new IdPoolPutExecutorImpl(idPool, idPoolSnowflakeGeneratorImpl::getIdsForOneSecond, this.usingSchedule);
		idPoolPutExecutor.setScheduleInterval(this.scheduleInterval);
		idPoolPutExecutor.paddingIdToPool();
		idPoolPutExecutor.startPoolPaddingSchedule();
		idPoolSnowflakeGeneratorImpl.setIdPoolPutExecutor(idPoolPutExecutor);
		return idPoolSnowflakeGeneratorImpl;
	}
	
}
