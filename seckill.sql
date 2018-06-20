# Host: localhost  (Version 5.5.22)
# Date: 2018-06-20 20:41:21
# Generator: MySQL-Front 6.0  (Build 2.20)


#
# Structure for table "seckill"
#

CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

#
# Data for table "seckill"
#

INSERT INTO `seckill` VALUES (1000,'1000元秒杀iphone6',97,'2018-03-11 16:59:19','2018-01-01 00:00:00','2019-01-02 00:00:00'),(1001,'800元秒杀ipad',199,'2018-03-11 16:59:19','2018-01-01 00:00:00','2019-01-02 00:00:00'),(1002,'6600元秒杀mac book pro',300,'2018-03-11 16:59:19','2016-01-01 00:00:00','2016-01-02 00:00:00'),(1003,'7000元秒杀iMac',400,'2018-06-20 16:59:19','2018-06-01 00:00:00','2018-07-01 00:00:00');

#
# Structure for table "success_killed"
#

CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

#
# Data for table "success_killed"
#

INSERT INTO `success_killed` VALUES (1000,12345678900,-1,'2018-03-22 18:28:53'),(1000,13750011111,0,'2018-05-18 12:32:16'),(1000,13750033111,0,'2018-05-05 18:03:48'),(1000,13750033171,0,'2018-05-05 18:08:08'),(1001,12345678900,0,'2018-03-22 18:40:08'),(1001,13750033171,0,'2018-05-20 18:19:48');
