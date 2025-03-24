package com.share.domain.strategy.service;

import com.share.domain.strategy.model.entity.RaffleAwardEntity;
import com.share.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);

}
