package com.share.domain.strategy.service.Raffle;

import com.share.domain.strategy.model.entity.RaffleAwardEntity;
import com.share.domain.strategy.model.entity.RaffleFactorEntity;
import com.share.domain.strategy.model.entity.RuleActionEntity;
import com.share.domain.strategy.model.entity.StrategyEntity;
import com.share.domain.strategy.model.vo.RuleLogicCheckTypeVo;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.domain.strategy.service.IRaffleStrategy;
import com.share.domain.strategy.service.armory.IStrategyDispatch;
import com.share.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.share.types.enums.ResponseCode;
import com.share.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.Escape;

/**
 * 抽奖策略抽象类
 */
@Slf4j
public abstract  class AbstractRaffleStrategy implements IRaffleStrategy {
    protected IStrategyRepository repository;
    protected IStrategyDispatch strategyDispatch;

    public AbstractRaffleStrategy(IStrategyRepository repository,IStrategyDispatch strategyDispatch){
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
    }
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        //参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if(null==userId||null==strategyId){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //2.策略查询
        StrategyEntity strategy = repository.queryStrategyEntitiesByStrategyId(strategyId);

        //3.抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckRaffleBeforeLogic(raffleFactorEntity, strategy.ruleModels());

        //被接管
        if(RuleLogicCheckTypeVo.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())){
            //如果黑名单
            if(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())){
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
                //权重规则抽奖
            }else if(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleActionEntity.getRuleModel())){
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                //根据规则的参数进行抽奖
                String ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();
                return RaffleAwardEntity.builder()
                        .awardId(strategyDispatch.getRandomAwardId(strategyId,ruleWeightValueKey))
                        .build();

            }

        }

        //无规则抽奖
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();


    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
