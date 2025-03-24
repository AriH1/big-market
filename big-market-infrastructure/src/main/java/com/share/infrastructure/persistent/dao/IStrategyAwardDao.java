package com.share.infrastructure.persistent.dao;

import com.share.infrastructure.persistent.po.StrategyAward;
import com.share.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(long strategyId);

     String queryStrategyAwardRuleModels(StrategyAward strategyAward);
}
