package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */
public interface SeckillService {

    /**
     * 查询所有秒杀产品的记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id获取单个秒杀产品
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口的地址
     * 否则输出系统时间和开始结束时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 秒杀操作结果
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     * 通过存储过程执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
