package com.huawei.it.euler.ddd.domain.account.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;

public interface UserRoleRepository extends IService<UserRole> {

    Page<UserRole> getUserRolePage(UserRoleQuery query, int curPage, int pageSize);
}
