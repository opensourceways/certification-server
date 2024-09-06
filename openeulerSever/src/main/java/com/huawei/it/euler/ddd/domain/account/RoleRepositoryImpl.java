package com.huawei.it.euler.ddd.domain.account;

import com.huawei.it.euler.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void insert(RoleRelationship roleRelationship) {
        roleMapper.insertRef(roleRelationship);
    }

    @Override
    public List<String> findRoleByUuid(String uuid) {
        return roleMapper.findByUuid(uuid);
    }

    @Override
    public List<Role> findRoleInfoByUuid(String uuid) {
        return roleMapper.findRoleInfoByUuid(uuid);
    }

    @Override
    public List<String> findUuidByRoleId(int roleId) {
        return roleMapper.findUuidByRole(roleId);
    }
}
