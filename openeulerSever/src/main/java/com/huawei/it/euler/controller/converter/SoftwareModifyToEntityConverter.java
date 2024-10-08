/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.controller.request.SoftwareModifyRequest;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract class SoftwareModifyToEntityConverter implements Converter<SoftwareModifyRequest, Software> {
    public static final SoftwareModifyToEntityConverter INSTANCE =
        Mappers.getMapper(SoftwareModifyToEntityConverter.class);
}
