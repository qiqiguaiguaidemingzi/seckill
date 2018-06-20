package com.lin.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.lin.dao.SeckillDao;
import com.lin.dao.SuccessKilledDao;
import com.lin.dao.cache.RedisDao;
import com.lin.dto.Exposer;
import com.lin.dto.SeckillExecution;
import com.lin.entity.Seckill;
import com.lin.entity.SuccessKilled;
import com.lin.enums.SeckillStateEnum;
import com.lin.exception.RepeatKillException;
import com.lin.exception.SeckillCloseException;
import com.lin.exception.SeckillException;
import com.lin.service.SeckillService;

//@Component @Service @Repository
@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RedisDao redisDao;

	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	// md5盐值字符串，用于混淆MD5
	private final String salt = "asf4415AFDSA%^$&&(*&)";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//优化缓存
		/* *
		 * get form cache
		 * if null
		 * 		get db
		 * 		put cache
		 * locgoin
		 * */
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			}else{
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		// 系统当前的时间
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		// 转化特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	// 不让外界访问，定义为私有
	private String getMD5(long seckillId) {
		String base = seckillId + "/" + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	@Transactional
	/**
	 * 使用注解控制事务方法的优点：
	 * 1：开发团队达成一致约定，明确标注事务方法的编程风格
	 * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求，或者将其剥离到事务方法外部
	 * 3：不是所有的方法都需要事务，如只有一条修改操作，或只读操作不需要事务控制
	 * */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {

		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// 执行秒杀逻辑：减库存 + 记录购买行为
		Date nowTime = new Date();
		/**
		 * 此处先插入购买明细，再判断是否减库存，这样减少了update持有行级锁的时间
		 * */
		try {
			// 记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			// 唯一：seckillId, userPhone
			if (insertCount <= 0) {
				// 重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				// 减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// 没有更新到记录，从业务或者说客户的角度讲，就是秒杀已经关闭了
					throw new SeckillCloseException("seckill is closed");  //try中  throw 关键字抛出异常，下一步进入catch中
				} else {
					// 秒杀成功
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;	//因为方法中有throws SeckillCloseException，故在catch中将异常继续向上抛出，交给调用方法的对象
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 所有编译期异常，转化为运行期异常（为了出异常时，让spring自动帮我们进行rollback）
			throw new SeckillException("seckill inner error:" + e.getMessage());
		}

	}

}