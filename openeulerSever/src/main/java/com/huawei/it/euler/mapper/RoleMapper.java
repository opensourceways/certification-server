/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.huawei.it.euler.ddd.domain.account.Role;
import com.huawei.it.euler.ddd.domain.account.RoleRelationship;
import com.huawei.it.euler.model.vo.RoleVo;

/**
 * 用户角色Mapper
 *
 * @since 2024/06/29
 */
@Repository
public interface RoleMapper {

    List<String> findByIdList(@Param("idList") List<Integer> idList);

    List<String> findByUserId(@Param("userId") Integer userId);

    List<Integer> findByUUID(@Param("uuid") Integer uuid);

    List<RoleVo> findRoleInfoByUserId(@Param("userId") Integer userId);

    List<String> findByUuid(@Param("uuid") String uuid);

    List<Role> findRoleInfoByUuid(@Param("uuid") String uuid);

    void insertDefaultRole(@Param("uuid") String uuid);

    List<String> findUuidByRole(@Param("roleId") Integer roleId);

    void insertRef(RoleRelationship roleRelationship);

    List<String> findUserByRole(@Param("roleId") Integer roleId,@Param("dataScope") Integer dataScope);

    List<RoleVo> findRoleByUserId(@Param("uuid") Integer uuid,@Param("roleId") Integer roleId);

    /**
     * 根据数据权限查找用户
     * @param dataScope 数据范围
     * @return 用户列表
     */
    List<Role> findUserByDataScope(@Param("dataScope") Integer dataScope);

    List<String> findAllUuid();
}
