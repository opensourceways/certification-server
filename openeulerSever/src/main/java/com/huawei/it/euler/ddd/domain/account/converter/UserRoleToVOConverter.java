/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract class UserRoleToVOConverter implements Converter<UserRole, UserRoleVO> {
    public static final UserRoleToVOConverter INSTANCE = Mappers.getMapper(UserRoleToVOConverter.class);
}
