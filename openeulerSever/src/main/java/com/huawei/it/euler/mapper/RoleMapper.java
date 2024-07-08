/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import com.huawei.it.euler.model.vo.RoleVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色Mapper
 *
 * @since 2024/06/29
 */
@Repository
public interface RoleMapper {
    List<String> findByUserId(@Param("userId") Integer userId);

    List<RoleVo> findRoleInfoByUserId(@Param("userId") Integer userId);

    void insertDefaultRole(@Param("userId") Integer userId);

    List<Integer> findUserByRole(@Param("roleId") Integer roleId);
}
