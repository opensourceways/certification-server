/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleQueryRequest;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract  class UserRoleQueryRequestToQueryConverter implements Converter<UserRoleQueryRequest, UserRoleQuery> {
    public static final UserRoleQueryRequestToQueryConverter INSTANCE = Mappers.getMapper(UserRoleQueryRequestToQueryConverter.class);
}
