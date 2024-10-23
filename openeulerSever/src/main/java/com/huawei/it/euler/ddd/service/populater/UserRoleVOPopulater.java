/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.ddd.service.populater;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.populater.Populater;

@Component
public class UserRoleVOPopulater implements Populater<UserRoleVO> {

    @Autowired
    private AccountService accountService;

    @Override
    public UserRoleVO populate(UserRoleVO source) {
        if (ObjectUtils.isEmpty(source)) {
            return null;
        }
        source.setRoleName(RoleEnum.findById(source.getRole()));
        return source;
    }

    @Override
    public List<UserRoleVO> populate(List<UserRoleVO> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        source.forEach(this::populate);
        return source;
    }
}
