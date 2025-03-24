package com.share.domain.strategy.service.rule;

import com.share.domain.strategy.model.entity.RuleActionEntity;
import com.share.domain.strategy.model.entity.RuleMatterEntity;

/**
 * 逻辑过滤类
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);


}

