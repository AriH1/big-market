package com.share.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVo {
    ALLOW("0000","放行，执行后续流程，不受规则引擎影响"),
    TAKE_OVER("0001","接管，后续流程受规则引擎影响"),;

    private final String code;
    private final String info;


}
