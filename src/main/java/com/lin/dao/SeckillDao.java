package com.lin.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lin.entity.Seckill;

public interface SeckillDao {
	
	/**减库存
	 * @param seckillId
	 * @param killTime
	 * @return int 表示更新语句影响的行数，如果>=1，表示更新成功，=0则表示更新失败
	 */
	int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
	
	
	/**
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	
	/**根据偏移量查询秒杀商品列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
