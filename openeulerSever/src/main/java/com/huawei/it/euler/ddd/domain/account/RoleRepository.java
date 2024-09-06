package com.huawei.it.euler.ddd.domain.account;

import java.util.List;

public interface RoleRepository {
    void insert(RoleRelationship roleRelationship);

    List<String> findRoleByUuid(String uuid);

    List<Role> findRoleInfoByUuid(String uuid);

    List<String> findUuidByRoleId(int roleId);
}
