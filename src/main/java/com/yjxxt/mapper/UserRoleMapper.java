package com.yjxxt.mapper;

import com.yjxxt.base.BaseMapper;
import com.yjxxt.bean.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
//统计用户角色
    int countUserOfRoleByUserId(Integer userId);
//删除用户角色
    int deleteUserOfRoleByUserId(Integer userId);
}