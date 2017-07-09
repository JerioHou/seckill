package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/6/29.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final String salt = "sdfsfw0-e,v,,dfe0-,-vm3-bever";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisDao redisDao;

    /**
     * 查询秒杀产品列表
     *
     * @return
     */
    public List<Seckill> getSeckillList() {
        List<Seckill> seckillList = seckillDao.queryAll(0, 5);
        return seckillList;
    }

    /**
     * 根据id查询秒杀产品
     *
     * @param seckillId
     * @return
     */
    public Seckill getById(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        return seckill;
    }

    public Exposer exportSeckillUrl(long seckillId) {

        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill =  seckillDao.queryById(seckillId);
        }
        if (null == seckill) {
            return new Exposer(false, seckillId);
        }else {
            redisDao.putSeckill(seckill);
        }

        Date endTime = seckill.getEndTime();
        Date startTime = seckill.getStartTime();
        Date nowTime = new Date();
        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;

    }

    @Transactional
    /**
     * 使用注解控制事务的有点
     * 1 开发团队达成一致约定，明确标注事务方法
     * 2 保证事务时间尽可能短，不要穿插其他网络操作，如RPC/HTTP请求，或者将这些网络操作剥离到事务方法外部
     * 3 不是所有的方法都需要事务，如只有一条修改操作，或者只读操作时，不需要事务
     *
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀
        Date nowTime = new Date();

        int updateCount = 0;
        try {
            int insertCount = successKilledDao.insertSuccesskilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeatKillException("重复秒杀");
            } else {
                updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    throw new SeckillCloseException("秒杀已结束");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException sce) {
            throw sce;
        } catch (RepeatKillException rke) {
            throw rke;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SeckillException("未知错误");
        }

    }

    /**
     * 使用触发器执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return  new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("killTime",killTime);
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("result",null);

        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map,"result",-2);
            if (result == 1){
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return  new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return  new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
}
