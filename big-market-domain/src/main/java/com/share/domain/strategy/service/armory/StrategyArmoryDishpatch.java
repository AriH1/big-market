package com.share.domain.strategy.service.armory;

import com.alibaba.fastjson.JSON;
import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.domain.strategy.model.entity.StrategyEntity;
import com.share.domain.strategy.model.entity.StrategyRuleEntity;
import com.share.domain.strategy.repository.IStrategyRepository;
import com.share.types.common.Constants;
import com.share.types.enums.ResponseCode;
import com.share.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * 策略装配兵工厂 负责初始化策略计算
 */
@Service
@Slf4j
public class StrategyArmoryDishpatch implements IStrategyArmory,IStrategyDispatch{
    @Autowired
    private IStrategyRepository repository;


    @Override
    public boolean assembleLotteryStrategy(Long strategyId){
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        if(strategyAwardEntities==null && strategyAwardEntities.isEmpty())return false;
        //2.装配
        assembleLotteryStrategy(String.valueOf(strategyId),strategyAwardEntities);
        //3.权重策略配置 适用于rule_weight
        StrategyEntity strategyEntity = repository.queryStrategyEntitiesByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if(null==ruleWeight)return false;
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId,ruleWeight);

        if(null == strategyRuleEntity){
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        //解析数据
        Map<String, List<Integer>> ruleWeightValuesMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValuesMap.keySet();
        for(String key : keys){
            List<Integer> ruleWeightValues = ruleWeightValuesMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity->!ruleWeightValues.contains(entity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("-").concat(key),strategyAwardEntitiesClone);
        }

        return true;
    }


    private void assembleLotteryStrategy(String key,List<StrategyAwardEntity> strategyAwardEntities){
        //1.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //2.获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //3.用1 % 0，0001 获取概率百分位 千分位 万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        ArrayList<Integer> strategyAwardSearchTables = new ArrayList<>(rateRange.intValue());
        //4.生成策略
        //向表中依次填充数据 比如 100个值，A商品概率位0.5，在表里填充50个A对应key为（1-50），随机数生成了1-50在表中get，则中将
        for (StrategyAwardEntity strategyAwardEntity:strategyAwardEntities) {
            //获取奖品id
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();

            //每个概率值需要存放到查询表的数量，循环填充
            for (int i = 0; i <rateRange.multiply(awardRate.divide(totalAwardRate)).setScale(0,RoundingMode.CEILING).intValue() ; i++) {
                strategyAwardSearchTables.add(awardId);
            }
        }
        //5.乱序
        Collections.shuffle(strategyAwardSearchTables);

        //6.放入hashmap
        HashMap<Object, Object> shuffleStrategyAwardSearchRateTables = new HashMap<>();
        for (int i = 0; i <strategyAwardSearchTables.size() ; i++) {
            shuffleStrategyAwardSearchRateTables.put(i,strategyAwardSearchTables.get(i));
        }

        //7.存入redis
        repository.storeStrategyAwardSearchRateTables(key,rateRange,shuffleStrategyAwardSearchRateTables);
    }


    @Override
    public Integer getRandomAwardId(Long strategyId) {
        //分布式部署下，不一定为当前应用做的策略装配，也就是不一定保存到本地，而是分布式应用所以存入redis
        Integer rateRange = repository.getRateRange(strategyId);
        //通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId),new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("-").concat(ruleWeightValue);
        Integer rateRange = repository.getRateRange(key);
        return repository.getStrategyAwardAssemble(key,new SecureRandom().nextInt(rateRange));
    }


}
