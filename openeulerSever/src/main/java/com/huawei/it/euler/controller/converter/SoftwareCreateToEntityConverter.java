/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.controller.request.SoftwareCreateRequest;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract class SoftwareCreateToEntityConverter implements Converter<SoftwareCreateRequest, Software> {
    public static final SoftwareCreateToEntityConverter INSTANCE =
        Mappers.getMapper(SoftwareCreateToEntityConverter.class);
}
