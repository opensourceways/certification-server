/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import java.util.List;

public interface RoleRepository {
    void insert(RoleRelationship roleRelationship);

    /**
     * 根据用户uuid查找当前用户所有角色
     * @param uuid 用户uuid
     * @return 角色值集合
     */
    List<String> findRoleByUuid(String uuid);

    /**
     * 根据用户uuid查到当前用户所有角色
     * @param uuid 用户uuid
     * @return 角色对象集合
     */
    List<Role> findRoleInfoByUuid(String uuid);

    List<String> findUuidByRoleId(int roleId);

    List<String> findAllUuid();
}
