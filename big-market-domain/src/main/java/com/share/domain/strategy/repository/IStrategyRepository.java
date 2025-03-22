package com.share.domain.strategy.repository;

import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.domain.strategy.model.entity.StrategyEntity;
import com.share.domain.strategy.model.entity.StrategyRuleEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 策略的仓储接口
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(String key, BigDecimal rateRange, HashMap<Object, Object> shuffleStrategyAwardSearchRateTables);



    Integer getRateRange(Long strategyId);
    Integer getRateRange(String key);

    Integer getStrategyAwardAssemble(String key, int rateKey);


    StrategyEntity queryStrategyEntitiesByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);
}
