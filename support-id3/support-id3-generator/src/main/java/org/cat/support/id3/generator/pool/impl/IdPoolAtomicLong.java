package org.cat.support.id3.generator.pool.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author 王云龙
 * @date 2021年10月13日 下午5:07:42
 * @version 1.0
 * @description 表示填充的 {@link AtomicLong} 以防止 FalseSharing 问题
 * 		CPU 缓存一般为 64 字节，以下是填充后的缓存 
 * 		example：64 bytes = 8 bytes (object引用) + 6 * 8 bytes (填充位的long值) + 8 bytes (a long值)
 *
 */
public class IdPoolAtomicLong extends AtomicLong {
    private static final long serialVersionUID = -3415778863941386253L;

    /** 6 * 8 bytes **/
    public volatile long p1, p2, p3, p4, p5, p6 = 7L;

    public IdPoolAtomicLong() {
        super();
    }

    public IdPoolAtomicLong(long initialValue) {
        super(initialValue);
    }

    /**
     * 防止为清理未使用的填充引用而进行 GC 优化
     */
    public long sumPaddingToPreventOptimization() {
        return p1 + p2 + p3 + p4 + p5 + p6;
    }

}