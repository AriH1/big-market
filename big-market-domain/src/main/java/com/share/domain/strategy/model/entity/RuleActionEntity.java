package com.share.domain.strategy.model.entity;

import com.share.domain.strategy.model.vo.RuleLogicCheckTypeVo;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleActionEntity <T extends RuleActionEntity.RaffleEntity>  {

    private String code = RuleLogicCheckTypeVo.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVo.ALLOW.getInfo();

    private String ruleModel;

    private T data;


    static public class RaffleEntity{

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode(callSuper = true)
    static public class RaffleBeforeEntity extends RaffleEntity{
        private Long strategyId;

        private String ruleWeightValueKey;

        private Integer awardId;

    }
    static public class RaffleCenterEntity extends RaffleEntity{

    }
    static public class RaffleAfterEntity extends RaffleEntity{

    }
}
