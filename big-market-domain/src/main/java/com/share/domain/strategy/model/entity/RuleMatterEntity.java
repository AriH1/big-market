package com.share.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则过滤物料实体类 过滤的具体参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleMatterEntity {
    private String userId;
    private Long strategyId;
    private Integer awardId;
    private String ruleModel;
}
