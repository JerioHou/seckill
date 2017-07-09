package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/7/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {
    @Autowired
    private  RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void redisTest() throws Exception {
        Long seckillId = 1000L;

        Seckill seckill = redisDao.getSeckill(seckillId);
        System.out.println("seckill_1="+seckill);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            System.out.println("seckill_2="+seckill);
            String result = redisDao.putSeckill(seckill);
            System.out.println("result="+result);
            seckill = redisDao.getSeckill(seckillId);
            System.out.println("seckill_3="+seckill);
        }
    }
}