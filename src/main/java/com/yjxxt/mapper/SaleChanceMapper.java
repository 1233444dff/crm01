package com.yjxxt.mapper;

import com.yjxxt.base.BaseMapper;
import com.yjxxt.bean.SaleChance;
import com.yjxxt.query.SaleChanceQuery;

import java.util.List;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {
    Integer deleteByPrimaryKey(Integer id);

    int insert(SaleChance record);

    Integer insertSelective(SaleChance record);

    SaleChance selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKeySelective(SaleChance record);

    int updateByPrimaryKey(SaleChance record);


}