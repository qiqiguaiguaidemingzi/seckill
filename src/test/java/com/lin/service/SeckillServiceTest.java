package com.lin.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lin.dto.Exposer;
import com.lin.dto.SeckillExecution;
import com.lin.entity.Seckill;
import com.lin.exception.RepeatKillException;
import com.lin.exception.SeckillCloseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list = {}", list);
	}

	@Test
	public void testGetById() {
		long id = 1000;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill = {}", seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long id = 1000;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		logger.info("exposer = {}", exposer);
		// exposer = Exposer [exposed=true,
		// md5=608580c05ae8edb7952e042cd754d135, seckillId=1000, now=0, start=0,
		// end=0]
	}

	@Test
	public void testExecuteSeckill() {
		long id = 1000;
		long phone = 13750033171L;
		String md5 = "608580c05ae8edb7952e042cd754d135";
		
		SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
		logger.info("result = {}", seckillExecution);
		

	}
	
	//测试代码完整逻辑，注意可重复执行
	@Test
	public void testSeckillLogic() {
		long id = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("exposer = {}", exposer);
			long phone = 13750033171L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
				logger.info("result = {}", seckillExecution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			} catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		}else {
			//秒杀未开启
			logger.warn("exposer = {}", exposer);
		}
		
		
	}

}
