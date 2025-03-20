package com.share.test.infrrastructure;

import com.alibaba.fastjson.JSON;
import com.share.infrastructure.persistent.dao.IAwardDao;
import com.share.infrastructure.persistent.po.Award;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 奖品持久化单元测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardDaoTest {

    @Autowired
    private IAwardDao awardDao;

    @Test
    public void test_queryAwardList(){
        List<Award> awardList = awardDao.queryAwardList();
        log.info("测试结果:{}", JSON.toJSONString(awardList));
    }
}
