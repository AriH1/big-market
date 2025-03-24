package com.share.test.domain;

import com.share.domain.strategy.service.armory.IStrategyArmory;
import com.share.domain.strategy.service.armory.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmory {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IStrategyDispatch strategyDispatch;

    @Before
    public void test_assembleArmory(){
        strategyArmory.assembleLotteryStrategy(10001L);
    }

    @Test
    public void test_getAssembleRandomVal(){
        for (int i = 0; i <100 ; i++) {
            log.info("第{}次抽奖:{}",i+1,strategyDispatch.getRandomAwardId(10001L));
        }
    }
    @Test
    public void test_getAssembleRandomValwithRuleWeight(){
        for (int i = 0; i <100 ; i++) {
            log.info("4000策略抽奖:{}:{}",i+1,strategyDispatch.getRandomAwardId(10001L,"4000:102,103,104,105,106,107,108,109"));
        }
        for (int i = 0; i <100 ; i++) {
            log.info("6000策略抽奖:{}:{}",i+1,strategyDispatch.getRandomAwardId(10001L,"6000:103,104,105,106,107,108,109"));
        }

    }
}
