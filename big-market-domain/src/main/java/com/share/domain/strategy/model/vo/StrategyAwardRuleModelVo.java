package com.share.domain.strategy.model.vo;

import com.share.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.share.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖侧柜规则对象
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAwardRuleModelVo {
    private String ruleModels;
    public String[] raffleCenterRuleModelList(){
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = this.ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue : ruleModelValues) {
            if(DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)){
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }
}
