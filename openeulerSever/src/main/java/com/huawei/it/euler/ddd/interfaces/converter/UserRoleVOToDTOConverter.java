/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleDTO;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract  class UserRoleVOToDTOConverter implements Converter<UserRoleVO, UserRoleDTO> {
    public static final UserRoleVOToDTOConverter INSTANCE = Mappers.getMapper(UserRoleVOToDTOConverter.class);
}
