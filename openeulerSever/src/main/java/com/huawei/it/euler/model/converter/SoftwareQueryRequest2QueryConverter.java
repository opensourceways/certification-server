/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.entity.SoftwareQuery;
import com.huawei.it.euler.model.vo.SoftwareQueryRequest;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract class SoftwareQueryRequest2QueryConverter implements Converter<SoftwareQueryRequest, SoftwareQuery> {
    public static final SoftwareQueryRequest2QueryConverter INSTANCE =
        Mappers.getMapper(SoftwareQueryRequest2QueryConverter.class);
}
