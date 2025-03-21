package com.share.domain.strategy.service.armory;

import com.alibaba.fastjson.JSON;
import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 策略装配兵工厂 负责初始化策略计算
 */
@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory {
    @Autowired
    private IStrategyRepository repository;


    @Override
    public boolean assembleLotteryStrategy(Long strategyId){
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        if(strategyAwardEntities==null && strategyAwardEntities.isEmpty())return false;

        //2.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //3.获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.用1 % 0，0001 获取概率百分位 千分位 万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        ArrayList<Integer> strategyAwardSearchTables = new ArrayList<>(rateRange.intValue());
        //5.生成策略
        //向表中依次填充数据 比如 100个值，A商品概率位0.5，在表里填充50个A对应key为（1-50），随机数生成了1-50在表中get，则中将
        for (StrategyAwardEntity strategyAwardEntity:strategyAwardEntities) {
            //获取奖品id
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();

            //每个概率值需要存放到查询表的数量，循环填充
            for (int i = 0; i <rateRange.multiply(awardRate).setScale(0,RoundingMode.CEILING).intValue() ; i++) {
                strategyAwardSearchTables.add(awardId);
            }
        }
        //6.乱序
        Collections.shuffle(strategyAwardSearchTables);

        //7.放入hashmap
        HashMap<Object, Object> shuffleStrategyAwardSearchRateTables = new HashMap<>();
        for (int i = 0; i <strategyAwardSearchTables.size() ; i++) {
            shuffleStrategyAwardSearchRateTables.put(i,strategyAwardSearchTables.get(i));
        }

        //8.存入redis
        repository.storeStrategyAwardSearchRateTables(strategyId,rateRange,shuffleStrategyAwardSearchRateTables);
        return true;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        Integer rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssemble(strategyId,new SecureRandom().nextInt(rateRange));
    }
}
