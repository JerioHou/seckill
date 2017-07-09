package org.seckill.service;

import jdk.nashorn.internal.runtime.RewriteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by Administrator on 2017/7/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list{}",list);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillService.getById(1000);
        logger.info("seckill{}",seckill);
    }

    @Test
    public void exportSeckillLogic() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            long userPhone = 12345678901L;
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id,userPhone,md5);
                logger.info("result={}",seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                e.printStackTrace();
            } catch (SeckillException e) {
                e.printStackTrace();
            }
        }  else {
            logger.warn("exposer={}",exposer);
        }
    }
    @Test
    public void executeSeckillByProcedure() throws Exception {
        Long seckillId = 1000L;
        Long phone = 12345678902L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        String md5 = exposer.getMd5();

        SeckillExecution seckillExecution = seckillService.executeSeckillByProcedure(seckillId,phone,md5);
        System.out.println(seckillExecution.toString());
    }

}