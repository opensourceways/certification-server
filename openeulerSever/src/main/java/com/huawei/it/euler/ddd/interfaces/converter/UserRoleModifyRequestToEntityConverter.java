/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.converter;

import com.huawei.it.euler.ddd.interfaces.request.UserRoleModifyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleRequest;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract  class UserRoleModifyRequestToEntityConverter implements Converter<UserRoleModifyRequest, UserRole> {
    public static final UserRoleModifyRequestToEntityConverter INSTANCE = Mappers.getMapper(UserRoleModifyRequestToEntityConverter.class);
}
