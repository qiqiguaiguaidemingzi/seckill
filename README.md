# seckill
一个结合redis的商品秒杀系统
=====

项目后台使用SSM框架，并使用redis来缓解高并发量问题，
同时前后端分离，前端只负责数据呈现并提供接口，接口格式符合RESTful规范

项目中redis对象的注入在src/main/resources/spring/spring-dao.xml配置文件中，使用构造注入方式
当copy、运行本项目时，需少许更改：
```
<bean id="redisDao" class="com.lin.dao.cache.RedisDao">
   <constructor-arg index="0" value="192.168.91.130"></constructor-arg>
   <constructor-arg index="1" value="6379"></constructor-arg>
</bean>
```
第一个参数"192.168.91.130"，请改成redis所在服务器对应的IP地址，
第二个参数"6379"，是redis默认端口号，如果未更改过redis配置文件，一般不需要更改


本项目的目的旨在熟悉SSM框架，学习redis数据库，谢谢！！
