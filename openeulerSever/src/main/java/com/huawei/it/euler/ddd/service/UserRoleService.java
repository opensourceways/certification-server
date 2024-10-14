/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.domain.account.converter.UserRoleToVOConverter;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.domain.account.repository.UserRoleRepository;
import com.huawei.it.euler.model.vo.PageResult;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public PageResult<UserRoleVO> getAccountRoleListByQuery(String uuid, UserRoleQuery query, int curPage, int pageSize) {
        query.setUuid(uuid);
        Page<UserRole> page = userRoleRepository.getUserRolePage(query, curPage, pageSize);
        return UserRoleToVOConverter.INSTANCE.convert(page);
    }
}
