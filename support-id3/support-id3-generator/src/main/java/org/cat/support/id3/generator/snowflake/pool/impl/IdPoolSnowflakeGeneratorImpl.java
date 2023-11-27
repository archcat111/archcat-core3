package org.cat.support.id3.generator.snowflake.pool.impl;

import java.util.ArrayList;
import java.util.List;

import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.id3.generator.pool.IIdPool;
import org.cat.support.id3.generator.pool.IIdPoolPutExecutor;
import org.cat.support.id3.generator.pool.exception.IdPoolException;
import org.cat.support.id3.generator.snowflake.impl.IdSnowflakeGeneratorImpl;
import org.cat.support.id3.generator.snowflake.pool.IIdPoolSnowflakeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdPoolSnowflakeGeneratorImpl extends IdSnowflakeGeneratorImpl implements IIdPoolSnowflakeGenerator {

	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	private IIdPool idPool;
	private IIdPoolPutExecutor idPoolPutExecutor;
	
	@Override
	public String getStrId() {
		long newId = this.getLongId();
		return Long.valueOf(newId).toString();
	}

	@Override
	public long getLongId() {
		long newId = idPool.take();
		return newId;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 上午11:53:18
	 * @version 1.0
	 * @description 获取当前一秒内的所有id，id个数和配置的sequenceBits有关
	 * @param currentSecond
	 * @return
	 */
	protected List<Long> getIdsForOneSecond(long currentSecond) {
        // Initialize result list size of (max sequence + 1)
        int listSize = (int) bitsAllocator.getMaxSequence() + 1;
        List<Long> idList = new ArrayList<>(listSize);

        // Allocate the first sequence of the second, the others can be calculated with the offset
        long firstSeqUid = bitsAllocator.allocate(currentSecond - startDateSeconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
        	idList.add(firstSeqUid + offset);
        }

        return idList;
    }

	@Override
	public void setIdPool(IIdPool idPool) {
		this.idPool = idPool;
	}

	@Override
	public void setIdPoolPutExecutor(IIdPoolPutExecutor idPoolPutExecutor) {
		this.idPoolPutExecutor = idPoolPutExecutor;
	}
	
	@Override
	public void destroy() {
		this.idPoolPutExecutor.shutdown();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午3:24:05
	 * @version 1.0
	 * @description 当Id Pool中的可用ID数量减少到阈值时的处理策略
	 */
	protected void poolArrivePaddingThresholdHandler(IIdPool idPool) {
		this.idPoolPutExecutor.asyncPaddingIdToPool();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午3:28:19
	 * @version 1.0
	 * @description Id Pool拒绝被填充ID时的处理策略 
	 * @param idPool
	 * @param id
	 */
	protected void poolRejectedPutHandler(IIdPool idPool, long id) {
    	coreLogger.warn("拒绝将id[{}]填充到Id Pool[{}]", id, idPool);
    }
    
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午3:28:54
	 * @version 1.0
	 * @description Id Pool拒绝get id时的处理策略 
	 * @param idPool
	 */
    protected void poolRejectedTakeHandler(IIdPool idPool) {
    	coreLogger.warn("拒绝从IdPool[{}]获取id", idPool);
        throw new IdPoolException("Rejected take buffer. " + idPool);
    }

	@Override
	public String toString() {
		return "IdPoolSnowflakeGeneratorImpl [idPool=" + idPool + ", idPoolPutExecutor=" + idPoolPutExecutor + "]";
	}
    
}
