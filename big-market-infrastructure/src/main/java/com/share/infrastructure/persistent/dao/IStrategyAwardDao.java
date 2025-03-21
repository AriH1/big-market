package com.share.infrastructure.persistent.dao;

import com.share.domain.strategy.model.entity.StrategyAwardEntity;
import com.share.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(long strategyId);
}
