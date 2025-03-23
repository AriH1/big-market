package com.share.domain.strategy.service.rule.factory;
import com.share.domain.strategy.model.entity.RuleActionEntity;
import com.share.domain.strategy.service.rule.ILogicFilter;
import com.share.domain.strategy.service.annotation.LogicStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilerMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilers){
        logicFilers.forEach(logic->{
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(),LogicStrategy.class);
            if(null!=strategy){
                logicFilerMap.put(strategy.logicMode().getCode(),logic);
            }
        });
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String,ILogicFilter<T>> openLogicFilter(){
        return (Map<String, ILogicFilter<T>>) (Map<?,?>) logicFilerMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),

        ;

        private final String code;
        private final String info;

    }



}
