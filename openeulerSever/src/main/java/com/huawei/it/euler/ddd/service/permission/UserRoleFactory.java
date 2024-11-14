/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.permission;

import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 用户角色 工厂类
 *
 * @author zhaoyan
 * @since 2024-11-12
 */
@Component
public class UserRoleFactory {
    public UserRole toUserRole(UserRoleCommand command){
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(command, userRole);
        return userRole;
    }

    public UserRoleQuery toQuery(UserRoleCommand command){
        UserRoleQuery query = new UserRoleQuery();
        query.setUuid(command.getUuid());
        query.setRoleId(command.getRoleId());
        query.setDataScope(command.getDataScope());
        return query;
    }
}
