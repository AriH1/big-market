package com.share.test;

import com.share.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired
    private IRedisService redisService;

    @Test
    public void testRedis(){
        RMap<Object,Object> map = redisService.getMap("strategy_id_100001");
        map.put(1,101);
        map.put(2,101);
        map.put(3,101);
        map.put(4,102);
        map.put(5,102);
        map.put(6,102);
        map.put(7,103);
        map.put(8,104);
        map.put(9,105);
        map.put(10 ,106);
        log.info("测试结果:{}", redisService.getFromMap("strategy_id_100001", 1).toString());
    }

    @Test
    public void test() {
        log.info("测试完成");
    }

}
