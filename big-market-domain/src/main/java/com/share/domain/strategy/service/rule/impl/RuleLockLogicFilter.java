package com.share.domain.strategy.service.rule.impl;

import com.share.domain.strategy.model.entity.RuleActionEntity;
import com.share.domain.strategy.model.entity.RuleMatterEntity;
import com.share.domain.strategy.model.vo.RuleLogicCheckTypeVo;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.domain.strategy.service.annotation.LogicStrategy;
import com.share.domain.strategy.service.rule.ILogicFilter;
import com.share.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户抽奖n次后解锁奖品
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleCenterEntity> {

    @Autowired
    private IStrategyRepository repository;

    private Long userRaffleCount = 0L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-商品解锁 userId:{} strategyId:{} ruleModel:{}",ruleMatterEntity.getUserId(),ruleMatterEntity.getStrategyId(),ruleMatterEntity.getRuleModel());
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        Long raffleCount = Long.valueOf(ruleValue);
        if(userRaffleCount>=raffleCount){
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVo.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVo.ALLOW.getInfo())
                    .build();
        }
        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVo.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVo.TAKE_OVER.getInfo())
                .build();

    }
}
