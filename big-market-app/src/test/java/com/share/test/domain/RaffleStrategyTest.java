package com.share.test.domain;


import com.alibaba.fastjson.JSON;
import com.share.domain.strategy.model.entity.RaffleAwardEntity;
import com.share.domain.strategy.model.entity.RaffleFactorEntity;
import com.share.domain.strategy.service.IRaffleStrategy;
import com.share.domain.strategy.service.armory.IStrategyArmory;
import com.share.domain.strategy.service.armory.IStrategyDispatch;
import com.share.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    private IStrategyArmory strategyArmory;


    @Before
    public void setUp(){
        ReflectionTestUtils.setField(ruleWeightLogicFilter,"userScore",4500L);
        log.info("反射设置值完成");
    }

    @Before
    public void test_assembleArmory(){
        strategyArmory.assembleLotteryStrategy(10001L);
    }

    @Test
    public void test_performRaffle(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user1")
                .strategyId(10001L)
                .build();

        log.info("请求参数:{}", JSON.toJSONString(raffleFactorEntity));
        for (int i = 0; i < 100; i++) {
            log.info("第{}次抽奖:{}",i+1,JSON.toJSONString(raffleStrategy.performRaffle(raffleFactorEntity)));
        }
    }
}
