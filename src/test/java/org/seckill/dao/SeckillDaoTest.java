package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.xml.transform.dom.DOMResult;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/6/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    @Resource
    private  SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {

        System.out.println("*************返回值="+seckillDao.reduceNumber(1000,new Date()));
    }

    @Test
    public void queryById() throws Exception {

        long id = 1000l;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.toString());
    }

    @Test
    public void queryAll() throws Exception {
        List<Seckill> list = seckillDao.queryAll(0,100);
        for (Seckill seckill : list){
            System.out.println(seckill.toString());
        }

    }

}