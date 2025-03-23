package com.share.domain.strategy.service.rule.impl;

import com.share.domain.strategy.model.entity.RuleActionEntity;
import com.share.domain.strategy.model.entity.RuleMatterEntity;
import com.share.domain.strategy.model.vo.RuleLogicCheckTypeVo;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.domain.strategy.service.rule.ILogicFilter;
import com.share.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.share.domain.strategy.service.annotation.LogicStrategy;
import com.share.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Autowired
    private IStrategyRepository repository;

    private Long userScore = 6500L;

    /**
     * 权重规则过滤；
     * 1. 权重规则格式；4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式；判断哪个范围符合用户的特定抽奖范围
     *
     * @param ruleMatterEntity 规则物料实体对象
     * @return 规则过滤结果
     */
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}",ruleMatterEntity.getUserId(),ruleMatterEntity.getStrategyId(),ruleMatterEntity.getRuleModel());

        String userId =ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if(null==analyticalValueGroup || analyticalValueGroup.isEmpty()){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVo.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVo.ALLOW.getInfo())
                    .build();
        }
        List<Long> analyticalSortKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortKeys);

        Long nextValue = analyticalSortKeys.stream()
                .filter(key->userScore>=key)
                .max(Long::compareTo)
                .orElse(null);
        if(null!=nextValue){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(analyticalValueGroup.get(nextValue))
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(RuleLogicCheckTypeVo.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVo.TAKE_OVER.getInfo())
                    .build();
        }
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVo.ALLOW.getCode())
                .info(RuleLogicCheckTypeVo.ALLOW.getInfo())
                .build();
    }


    private Map<Long,String> getAnalyticalValue(String ruleValue){
        String[] ruleValueGroup = ruleValue.split(Constants.SPACE);
        Map<Long,String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroup) {
            if(ruleValueKey == null && ruleValueKey.isEmpty()){
                return ruleValueMap;
            }
            String[] parts = ruleValueKey.split(Constants.COLON);
            if(parts.length!=2){
                throw new IllegalArgumentException("rule_weigh rule_rule invalid format(非法格式):"+ruleValueKey);
            }
            ruleValueMap.put(Long.valueOf(parts[0]),ruleValueKey);
        }
        return ruleValueMap;
    }
}
