package com.share.test.domain;


import com.alibaba.fastjson.JSON;
import com.share.domain.strategy.model.entity.RaffleAwardEntity;
import com.share.domain.strategy.model.entity.RaffleFactorEntity;
import com.share.domain.strategy.service.IRaffleStrategy;
import com.share.domain.strategy.service.armory.IStrategyArmory;
import com.share.domain.strategy.service.armory.IStrategyDispatch;
import com.share.domain.strategy.service.rule.impl.RuleLockLogicFilter;
import com.share.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.validator.PublicClassValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private RuleWeightLogicFilter ruleWeightLogicFilter;

    @Autowired
    private RuleLockLogicFilter ruleLockLogicFilter;

    @Autowired
    private IStrategyArmory strategyArmory;


    @Before
    public void setUp(){
        ReflectionTestUtils.setField(ruleWeightLogicFilter,"userScore",4500L);
        ReflectionTestUtils.setField(ruleLockLogicFilter,"userRaffleCount",6L);


    }

    @Before
    public void test_assembleArmory(){
//        strategyArmory.assembleLotteryStrategy(10001L);
        strategyArmory.assembleLotteryStrategy(100003L);
    }

//    @Test
//    public void test_performRaffle(){
//        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
//                .userId("user1")
//                .strategyId(10001L)
//                .build();
//
//        log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
//        for (int i = 0; i < 100; i++) {
//            log.info("第{}次抽奖:{}",i+1,JSON.toJSONString(raffleStrategy.performRaffle(raffleFactorEntity)));
//        }
//    }

    @Test
    public void test_center_RaflleRule_ruleLock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .strategyId(100003L)
                .userId("muz")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
        log.info("请求结果:{}", JSON.toJSONString(raffleAwardEntity));
    }
}
