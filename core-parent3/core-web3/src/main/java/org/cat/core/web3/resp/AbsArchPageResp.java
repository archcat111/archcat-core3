package org.cat.core.web3.resp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月13日 下午10:38:14
 * @version 1.0
 * @description Response抽象基础类（用于分页数据展示），继承了序列化接口，同时显示了toJson方法
 * 例如：
 * public class MovieGetPageRespBean extends BasePageRespBean<MovieGetRespBean>{
 * 	private static final long serialVersionUID = -4058765229374560946L;
 * }
 * @param <T>
 */
@Getter
@Setter
public abstract class AbsArchPageResp<T> extends AbsArchJsonResp{
	private static final long serialVersionUID = 6790362688028070728L;
	
	private long count;
	private int offset;
	private int limit;
	private List<T> data;
	
}
