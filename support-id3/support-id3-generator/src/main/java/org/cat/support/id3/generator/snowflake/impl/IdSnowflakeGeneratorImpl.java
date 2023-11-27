package org.cat.support.id3.generator.snowflake.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.support.id3.generator.snowflake.IIdSnowflakeGenerator;
import org.cat.support.id3.generator.snowflake.core.BitsAllocator;
import org.cat.support.id3.generator.snowflake.exception.IdSnowflakeException;

/**
 * 
 * @author 王云龙
 * @date 2021年10月12日 下午3:19:08
 * @version 1.0
 * @description 生成一个可以保证分布式环境下全局唯一的64bits (long)的id
 * +------+----------------------+----------------+-----------+
 * | sign |     delta seconds    | worker node id | sequence  |
 * +------+----------------------+----------------+-----------+
 *   1bit          28bits              22bits         13bits
 * 		sign: 最高位bit为0
 * 		delta seconds: 接下来的28位。一个基于某个以秒为单位的时刻的增量秒数
 * 			例如：从(2021-10-20 00:00:00.000)开始，可支持8.7年，直到2030-04-20 21:24:16
 * 		worker id: 接下来的22位。基于数据库或者用其他手段分配的worker id（最好能够唯一标识当前节点），最大id约为420W
 * 		sequence: 接下来的13位。同一秒内生成的id的数量，13位表示最多生成8192个/s
 * 可以自行修改各组成部署所占的位数来适应不同的场景，总位数必须为63位(64-1)
 * 
 * 		startDateStr: format为：'yyyy-MM-dd'。默认值为'2021-10-20'
 *
 */
public class IdSnowflakeGeneratorImpl implements IIdSnowflakeGenerator {
	
	/**生成id的各个组成部分的分配位数**/
//	protected int deltaSecondBits = 28;
//	protected int workerIdBits = 22;
//    protected int sequenceBits = 13;
//    
    //ms: 1634659200000
//    protected String startDateStr = "2021-10-20";
    protected long startDateSeconds = TimeUnit.MILLISECONDS.toSeconds(1634659200000L);//转换为以秒为单位的时间戳
    protected long workerId;
    
    protected BitsAllocator bitsAllocator;
    
    /**nextId()会改变这些字段**/
    protected long lastSecond = -1L;
    protected long sequence = 0L;
    
	protected IdSnowflakeGeneratorImpl() {
	}

	@Override
	public String getStrId() {
		long newId = getLongId();
		return Long.valueOf(newId).toString();
	}
	
	@Override
	public long getLongId() {
		long newId = nextId();
		return newId;
	}

	@Override
	public String parseId(long id) {
		long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getTimestampBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (id << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (id << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = id >>> (workerIdBits + sequenceBits);

        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(startDateSeconds + deltaSeconds));
        String thatTimeStr = ArchDateTimeUtil.getFormatDateStr(thatTime, ArchDateTimeUtil.FormatConstants.DATETIME_NORMAL);

        // format as string
        return String.format("{\"id\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                id, thatTimeStr, workerId, sequence);
	}
	
	protected synchronized long nextId() {
		long currentSecond = getCurrentSecond();
		
		//如果当前的时钟小于最后一次生成id时记录的时钟，则抛出异常
		if (currentSecond < this.lastSecond) {
			long refusedSeconds = this.lastSecond - currentSecond;
			throw new IdSnowflakeException("当前的时钟小于最后一次生成id时记录的时钟 %d 秒", refusedSeconds);
		}
		
		//如果生成新id的时候，发现当前的时间戳和上一次生成id时的时间戳相同
		if (currentSecond == lastSecond) {//判断同一个时间戳中的序列是否用完，如用用完则等待下一毫秒继续生成新id
			this.sequence = (this.sequence + 1) & this.bitsAllocator.getMaxSequence();
			if (this.sequence == 0) {
				currentSecond = getNextSecond(lastSecond);
			}
		}else {//如果当前时间戳(秒)已经大于上次生成id时用到的时间戳(秒)，则将序列清零
			this.sequence = 0L;
		}
		
		//记录本次生成时间戳所使用的时间戳(秒)
		this.lastSecond = currentSecond;
		
		long newId = this.bitsAllocator.allocate(currentSecond - this.startDateSeconds, this.workerId, this.sequence);
		return newId;
	}
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月12日 下午6:01:27
	 * @version 1.0
	 * @description 获取当前时间戳（单位为秒） 
	 * @return
	 */
	private long getCurrentSecond() {
	    long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	    if (currentSecond - this.startDateSeconds > this.bitsAllocator.getMaxDeltaSeconds()) {
	        throw new IdSnowflakeException("时间戳部分的位数已经用完，拒绝新id的生成. Now: " + currentSecond);
	    }
	
	    return currentSecond;
	}
	 
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月12日 下午6:01:14
	 * @version 1.0
	 * @description 获取比当前时间戳大的下一个时间戳，单位为秒 
	 * 		如果获取的时间戳还是小于lastTimestamp，则会一直进行获取和等待直到当前时间大于lastTimestamp
	 * @param lastTimestamp
	 * @return 以秒为单位的时间戳
	 */
	private long getNextSecond(long lastTimestamp) {
	    long timestamp = getCurrentSecond();
	    while (timestamp <= lastTimestamp) {
	        timestamp = getCurrentSecond();
	    }
	
	    return timestamp;
	}

	public void setWorkerId(long workerId) {
		this.workerId = workerId;
	}
	
	public void setStartDateSeconds(long startDateSeconds) {
		this.startDateSeconds = startDateSeconds;
	}
	
	public void setBitsAllocator(BitsAllocator bitsAllocator) {
		this.bitsAllocator = bitsAllocator;
	}

	@Override
	public String toString() {
		return "IdSnowflakeGeneratorImpl [startDateSeconds=" + startDateSeconds + ", workerId=" + workerId
				+ ", bitsAllocator=" + bitsAllocator + ", lastSecond=" + lastSecond + ", sequence=" + sequence + "]";
	}
	
}
