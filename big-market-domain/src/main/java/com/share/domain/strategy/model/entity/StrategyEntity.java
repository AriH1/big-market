package com.share.domain.strategy.model.entity;

import com.share.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Date;

/**
 * 策略实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    /** 抽奖策略id*/
    private Long strategyId;
    /** 抽奖策略描述*/
    private String strategyDesc;
    /** 策略规则模型 */
    private String ruleModels;

    public String[] ruleModels(){
        if(StringUtils.isBlank(ruleModels))return null;
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight(){
        String[] ruleModels = this.ruleModels();
        for (String ruleModel : ruleModels) {
            if("rule_weight".equals(ruleModel))return ruleModel;
        }
        return null;
    }
}
