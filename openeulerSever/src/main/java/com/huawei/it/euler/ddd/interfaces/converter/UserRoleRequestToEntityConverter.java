/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.converter;

import com.huawei.it.euler.ddd.domain.account.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleRequest;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract  class UserRoleRequestToEntityConverter implements Converter<UserRoleRequest, UserRole> {
    public static final UserRoleRequestToEntityConverter INSTANCE = Mappers.getMapper(UserRoleRequestToEntityConverter.class);
}
