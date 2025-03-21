package com.share.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.infrastructure.persistent.dao.IStrategyAwardDao;
import com.share.infrastructure.persistent.po.StrategyAward;
import com.share.infrastructure.persistent.redis.IRedisService;
import com.share.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Autowired
    private IStrategyAwardDao strategyAwardDao;

    @Autowired
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //先从redis中查询策略奖品列表
        //构造key
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        //从redis查询
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        //判断是否为空
        if(strategyAwardEntities!=null && !strategyAwardEntities.isEmpty()){
            //返回策略奖品列表
            return JSON.parseArray(strategyAwardEntities.toString(),StrategyAwardEntity.class);
        }
        //redis中为空无数据 从数据库查询
        //获得StrategyAward类型数据 需要转换
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward:strategyAwards){
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                        .strategyId(strategyAward.getStrategyId())
                        .awardId(strategyAward.getAwardId())
                        .awardCount(strategyAward.getAwardCount())
                        .awardCountSurplus(strategyAward.getAwardCountSurplus())
                        .awardRate(strategyAward.getAwardRate())
                        .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        //将数据放入redis中
        redisService.setValue(cacheKey,strategyAwardEntities);

        return strategyAwardEntities;

    }

    /**
     * 存储策略随机数范围值和概率查找表到redis
     * @param strategyId
     * @param rateRange
     * @param shuffleStrategyAwardSearchRateTables
     */
    @Override
    public void storeStrategyAwardSearchRateTables(Long strategyId, BigDecimal rateRange, HashMap<Object, Object> shuffleStrategyAwardSearchRateTables) {

        //范围值 如生成1000以内的随机数
        String rateRangekey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId;
        redisService.setValue(rateRangekey , rateRange.intValue());
        //存储概率查找表
        String rateTableKey = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId;
        RMap<Object, Object> rateTable = redisService.getMap(rateTableKey);
        rateTable.putAll(shuffleStrategyAwardSearchRateTables);
    }

    @Override
    public Integer getRateRange(Long strategyId) {
        String rateRangeKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId;
         return redisService.getValue(rateRangeKey);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int rateKey) {
        String rateTable = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId;
        return redisService.getFromMap(rateTable, rateKey);
    }
}
