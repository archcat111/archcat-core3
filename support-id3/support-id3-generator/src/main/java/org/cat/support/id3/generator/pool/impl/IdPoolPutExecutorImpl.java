package org.cat.support.id3.generator.pool.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cat.core.util3.thread.NamingThreadFactory;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.id3.generator.pool.IIdPool;
import org.cat.support.id3.generator.pool.IIdPoolPutExecutor;
import org.cat.support.id3.generator.pool.handler.LPoolIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class IdPoolPutExecutorImpl implements IIdPoolPutExecutor{
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	private IIdPool idPool;
	private LPoolIdProvider poolIdProvider;
	private boolean usingSchedule;
	private Long scheduleInterval = 5 * 60L;
	
	private final AtomicBoolean running;
	private final IdPoolAtomicLong lastSecond;
	private final ExecutorService poolPaddingExecutors;//填充id的执行线程池
	private final ScheduledExecutorService poolPaddingSchedule;//填充id的调度线程
	
	private static final String WORKER_NAME = "IdPool-Padding-Worker";
	private static final String SCHEDULE_NAME = "IdPool-Padding-Schedule";
	
	public IdPoolPutExecutorImpl(IIdPool idPool, LPoolIdProvider poolIdProvider, boolean usingSchedule) {
		this.running = new AtomicBoolean(false);
		this.lastSecond = new IdPoolAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
		this.idPool = idPool;
		this.poolIdProvider = poolIdProvider;
		
		//初始化填充id的执行线程池
		int cores = Runtime.getRuntime().availableProcessors();
		this.poolPaddingExecutors = Executors.newFixedThreadPool(cores * 2, new NamingThreadFactory(WORKER_NAME));
		
		this.usingSchedule = usingSchedule;
		if(this.usingSchedule) {
			poolPaddingSchedule = Executors.newSingleThreadScheduledExecutor(new NamingThreadFactory(SCHEDULE_NAME));
		}else {
			poolPaddingSchedule = null;
		}
	}
	
	@Override
	public void setScheduleInterval(long scheduleInterval) {
        Assert.isTrue(scheduleInterval > 0, "Schedule interval must positive!");
        this.scheduleInterval = scheduleInterval;
    }
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午3:52:57
	 * @version 1.0
	 * @description 开始执行填充Id Pool的调度器
	 */
	@Override
	public void startPoolPaddingSchedule() {
        if (this.poolPaddingSchedule != null) {
        	poolPaddingSchedule.scheduleWithFixedDelay(() -> paddingIdToPool(), scheduleInterval, scheduleInterval, TimeUnit.SECONDS);
        }
    }
	
	@Override
	public void shutdown() {
		if (!this.poolPaddingExecutors.isShutdown()) {
            this.poolPaddingExecutors.shutdownNow();
        }

        if (this.poolPaddingSchedule != null && !this.poolPaddingSchedule.isShutdown()) {
            this.poolPaddingSchedule.shutdownNow();
        }
	}
	
	@Override
	public void asyncPaddingIdToPool() {
		this.poolPaddingExecutors.submit(this::paddingIdToPool);
	}
	
	/**
     * Padding buffer fill the slots until to catch the cursor
     */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午3:53:16
	 * @version 1.0
	 * @description 填充Id 到 Id Pool
	 */
	@Override
    public void paddingIdToPool() {
    	coreLogger.info("准备填充Id Pool，lastSecond:{}. {}", lastSecond.get(), this.idPool);

        //如果当前运行状态是false则将运行状态置为true
    	//如果当前运行状态是true则打日志并退出
        if (!running.compareAndSet(false, true)) {
        	coreLogger.info("当前Id Pool正在填充id中. {}", this.idPool);
            return;
        }

        // 当Id Pool已经填充满
        boolean isFullIdPool = false;
        while (!isFullIdPool) {
            List<Long> idList = poolIdProvider.provideIds(lastSecond.incrementAndGet());
            for (Long id : idList) {
            	isFullIdPool = !this.idPool.put(id);
                if (isFullIdPool) {
                    break;
                }
            }
        }

        //如果当前运行状态是false则将运行状态置为true
        running.compareAndSet(true, false);
        coreLogger.info("完成准备填充Id Pool，lastSecond:{}. {}", lastSecond.get(), this.idPool);
    }

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月14日 下午4:15:58
	 * @version 1.0
	 * @description 当前是否正在填充Id Pool 
	 * @return
	 */
    @Override
	public boolean isRunningPaddingIdPool() {
        return running.get();
    }

	@Override
	public String toString() {
		return "IdPoolPutExecutorImpl [idPool=" + idPool + ", usingSchedule="+ usingSchedule + 
				", scheduleInterval=" + scheduleInterval + ", running=" + running + ", lastSecond="
				+ lastSecond + "]";
	}
    
}
