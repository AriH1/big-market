package com.share.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.domain.strategy.model.entity.StrategyEntity;
import com.share.domain.strategy.model.entity.StrategyRuleEntity;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.infrastructure.persistent.dao.IStrategyAwardDao;
import com.share.infrastructure.persistent.dao.IStrategyDao;
import com.share.infrastructure.persistent.dao.IStrategyRuleDao;
import com.share.infrastructure.persistent.po.Strategy;
import com.share.infrastructure.persistent.po.StrategyAward;
import com.share.infrastructure.persistent.po.StrategyRule;
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
    private IStrategyRuleDao StrategyRuleDao;

    @Autowired
    private IStrategyDao strategyDao;

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
     * @param key
     * @param rateRange
     * @param shuffleStrategyAwardSearchRateTables
     */
    @Override
    public void storeStrategyAwardSearchRateTables(String key, BigDecimal rateRange, HashMap<Object, Object> shuffleStrategyAwardSearchRateTables) {

        //范围值 如生成1000以内的随机数
        String rateRangekey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        redisService.setValue(rateRangekey , rateRange.intValue());
        //存储概率查找表
        String rateTableKey = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key;
        RMap<Object, Object> rateTable = redisService.getMap(rateTableKey);
        rateTable.putAll(shuffleStrategyAwardSearchRateTables);
    }

    @Override
    public Integer getRateRange(Long strategyId) {
        String rateRangeKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId;
        return JSON.parseObject(JSON.toJSONString(redisService.getValue(rateRangeKey)),Integer.class);
    }

    @Override
    public Integer getRateRange(String key) {
        String rateRangeKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        return redisService.getValue(rateRangeKey);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        String rateTable = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key;
        return redisService.getFromMap(rateTable, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntitiesByStrategyId(Long strategyId) {
        //构建策略查询key
        String strategyKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        //redis中查询
        StrategyEntity strategyEntity = JSON.parseObject(JSON.toJSONString(redisService.getValue(strategyKey)),StrategyEntity.class);
        ///有则反 无则数据库
        if(strategyEntity!=null)return strategyEntity;
        //数据库查询
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        //构造entity
        StrategyEntity strategyEntity1 = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        //存入redis
        redisService.setValue(strategyKey,strategyEntity1);
        return strategyEntity1;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRuleReq =new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        StrategyRule strategyRule = StrategyRuleDao.queryStrategyRule(strategyRuleReq);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
    }
}
