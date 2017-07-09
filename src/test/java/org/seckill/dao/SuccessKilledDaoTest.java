package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/6/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccesskilled() throws Exception {
        System.out.println("返回值="+ successKilledDao.insertSuccesskilled(1000l,18137117917l));
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        SuccessKilled sk = successKilledDao.queryByIdWithSeckill(1000l,18137117917l);
        System.out.println(sk.toString());
        System.out.println("产品="+sk.getSeckill().toString());
    }

}