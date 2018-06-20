package com.lin.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lin.entity.SuccessKilled;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
	
	@Resource
	private SuccessKilledDao successKilledDao;

	@Test
	public void testInsertSuccessKilled() {
		long id = 1001L;
		long phone = 12345678900L;
		int insertCount = successKilledDao.insertSuccessKilled(id, phone);
		System.out.println("insertCount = " + insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id = 1001L;
		long phone = 12345678900L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}

}
