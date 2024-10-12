package com.huawei.it.euler.ddd.infrastructure.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.repository.UserRoleRepository;
import com.huawei.it.euler.ddd.infrastructure.mapper.UserRoleMapper;

@Service
public class UserRoleDBRepository extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    public List<UserRole> list(){
        return null;
    }
}
