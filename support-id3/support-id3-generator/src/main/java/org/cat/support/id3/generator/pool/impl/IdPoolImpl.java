package org.cat.support.id3.generator.pool.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.id3.generator.pool.IIdPool;
import org.cat.support.id3.generator.pool.exception.IdPoolException;
import org.cat.support.id3.generator.pool.handler.LPoolArrivePaddingThresholdHandler;
import org.cat.support.id3.generator.pool.handler.LPoolRejectedPutHandler;
import org.cat.support.id3.generator.pool.handler.LPoolRejectedTakeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdPoolImpl implements IIdPool{
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	private static final int START_POINT = -1; //pool中的游标起始位置
    private static final long CAN_PUT_FLAG = 0L;	//用来标记Pool中的某个槽是否可以set一个new id
    private static final long CAN_TAKE_FLAG = 1L; //用来标记Pool中的某个槽是中的id是否可以被应用程序获取
    
    private final int poolSize; //id pool的尺寸，如果用来填充snowflakeId，则最好是sequence的倍数
    
    private final long lastIndex; //pool中最后一个位置的索引
    private final long[] slots; //保存缓存id的存储结构
    private final IdPoolAtomicLong[] flags; //id pool中每个slot的状态，是可以put还是可以take
    
    private final AtomicLong tail = new IdPoolAtomicLong(START_POINT); //缓冲区游标可到的最后位置
    private final AtomicLong cursor = new IdPoolAtomicLong(START_POINT); //缓冲区中当前游标的位置
    private final int paddingThreshold; //会根据激活填充new id的百分比阈值以及缓冲区的size来计算剩余可用id的数量作为阈值
    
    private LPoolArrivePaddingThresholdHandler<IIdPool> poolArrivePaddingThresholdHandler;
    private LPoolRejectedPutHandler<IIdPool> poolRejectedPutHandler;
    private LPoolRejectedTakeHandler<IIdPool> poolRejectedTakeHandler;
    
    public IdPoolImpl(int poolSize, int paddingThresholdPercentage) {
    	this.poolSize = poolSize;
    	this.lastIndex = poolSize - 1;
    	this.slots = new long[poolSize];
    	this.flags = initFlags(poolSize);
    	
    	this.paddingThreshold = poolSize * (paddingThresholdPercentage / 100);
    	
	}
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年10月13日 下午5:43:38
     * @version 1.0
     * @description 将pool中的所有slot的表示都设置成可以进行put的状态 
     * @param poolSize
     * @return
     */
    private IdPoolAtomicLong[] initFlags(int poolSize) {
    	IdPoolAtomicLong[] flags = new IdPoolAtomicLong[poolSize];
        for (int i = 0; i < poolSize; i++) {
            flags[i] = new IdPoolAtomicLong(CAN_PUT_FLAG);
        }
        
        return flags;
    }
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年10月14日 上午10:22:29
     * @version 1.0
     * @description 给IdSnowflakePool填充id
     * 		这步操作的原子性由synchronized保证
     * 		注1：tail和cursor会一直增大
     * 		注2：nextTailIndex如果达到pooSize-1的index，则会回到0
     * @param id
     * @return
     */
    @Override
    public synchronized boolean put(long id) {
    	long currentTail = this.tail.get(); //当前已经设置id的最大index
    	long currentCursor = this.cursor.get();//当前获取id的游标index
    	
        long distance = currentTail - (currentCursor == START_POINT ? 0 : currentCursor);
        // 当前还可以使用的id的数量和pool size相同时，表示当前pool是满的，不允许再put id进来
        if (distance == this.poolSize - 1) {
        	this.poolRejectedPutHandler.rejectPut(this, id);
            return false;
        }
        
        //检查flag是否为CAN_PUT_FLAG，也从这可以看出这是一个环，当tailIndex达到最高时会回到0
        int nextTailIndex = calSlotIndex(currentTail + 1);
        //因为刚开始initFlags会将所有的slot的flag都设置成CAN_PUT_FLAG
        //当TailIndex每走一步，下面的代码会填充id并将flag变更为CAN_TAKE_FLAG
        //在这里如果tailIndex达到最大就又会回到0，而0的flag则是CAN_TAKE_FLAG
        //这步判断也就是表示所有的slot都被填充完了
        //假设正在使用过程红，现在pooSize是8，最大index则为7，当前的cursor为5
        //那么当tailIndex回到0并且走到4，而在5的时候会和上面描述的情况一样
        if (flags[nextTailIndex].get() != CAN_PUT_FLAG) {
        	this.poolRejectedPutHandler.rejectPut(this, id);
            return false;
        }
        
        //填充id，并设置flag为CAN_TAKE_FLAG
        slots[nextTailIndex] = id;
        flags[nextTailIndex].set(CAN_TAKE_FLAG);
        //take操作不能消费我们刚刚放的UID，直到这里进行(tail.incrementAndGet())
        tail.incrementAndGet();
        
        return true;
    }
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年10月13日 下午6:08:38
     * @version 1.0
     * @description 用槽序列计算槽索引（nextTailIndex % poolSize） 
     * 		lastIndex = poolSize - 1（poolSize必须为2的N次方，否则计算结果会比较诡异）
     * 		假设poolSize=8，则lastIndex=7，当nextTailIndex=5，则结果=5
     * 		假设poolSize=8，则lastIndex=7，当nextTailIndex=7，则结果=7
     * 		假设poolSize=8，则lastIndex=7，当nextTailIndex=8，则结果=0
     * 		假设poolSize=8，则lastIndex=7，当nextTailIndex=101，则结果=5
     * @param sequence
     * @return
     */
    protected int calSlotIndex(long nextTailIndex) {
        return (int) (nextTailIndex & lastIndex);
    }
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年10月14日 上午10:26:34
     * @version 1.0
     * @description 在下一个cursor处取环的 ID，这是使用原子游标的无锁操作 
     * 		在获取ID之前，要检查是否达到填充阈值
     * 		填充缓冲区操作将在另一个线程中触发
     * 		如果没有更多可用的 UID 可供使用，则将应用指定的 {@link poolRejectedTakeHandler}
     * @return
     */
    @Override
    public long take() {
    	//获取当前的游标
    	long currentCursor = cursor.get();
    	//获取下一个可利用的游标，如果游标已经到达tailIndex则nextCursor还是currentCursor，否则返回+1的游标
    	long nextCursor = cursor.updateAndGet(old -> old == tail.get() ? old : old + 1);
    	
    	//如果达到阈值，出发异步填充id
    	long currentTail = tail.get();
    	if (currentTail - nextCursor < paddingThreshold) {
    		coreLogger.info("达到Id填充的阈值，开始异步填充 threshold:{}. tail:{}, cursor:{}, rest:{}", paddingThreshold, currentTail,
                    nextCursor, currentTail - nextCursor);
    		this.poolArrivePaddingThresholdHandler.asyncPaddingIdToPool(this);
        }
    	
    	// 当光标以及达到可用id的尾部，即：没有更多的id可分配
    	if (nextCursor == currentCursor) {
    		this.poolRejectedTakeHandler.rejectPut(this);
        }

    	int nextCursorIndex = calSlotIndex(nextCursor);
    	if(!(flags[nextCursorIndex].get() == CAN_TAKE_FLAG)) {
    		throw new IdPoolException("光标不在可以取状态");
    	}
    	
    	//a：从下一个插槽获取 ID
    	long id = slots[nextCursorIndex];
    	//b：将下一个插槽标志设置为 CAN_PUT_FLAG
    	flags[nextCursorIndex].set(CAN_PUT_FLAG);
    	//注意：步骤a，b不能互换
    	//如果我们在获取slot的值之前设置flag，生产者可能会用一个新的 ID 覆盖槽
    	//这可能会导致消费者在绕圈走一圈后取两次 ID
    	
    	return id;
    	
    }

	

    @Override
	public void setPoolRejectedPutHandler(LPoolRejectedPutHandler<IIdPool> poolRejectedPutHandler) {
		this.poolRejectedPutHandler = poolRejectedPutHandler;
	}

    @Override
	public void setPoolRejectedTakeHandler(LPoolRejectedTakeHandler<IIdPool> poolRejectedTakeHandler) {
		this.poolRejectedTakeHandler = poolRejectedTakeHandler;
	}

    @Override
	public void setPoolArrivePaddingThresholdHandler(
			LPoolArrivePaddingThresholdHandler<IIdPool> poolArrivePaddingThresholdHandler) {
		this.poolArrivePaddingThresholdHandler = poolArrivePaddingThresholdHandler;
	}

	@Override
	public String toString() {
		return "IdPoolImpl [poolSize=" + poolSize + ", lastIndex=" + lastIndex + ", tail=" + tail + ", cursor=" + cursor + "]";
	}

}
