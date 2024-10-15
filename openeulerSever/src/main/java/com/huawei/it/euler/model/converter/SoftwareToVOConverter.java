/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.SoftwareVo;
import com.huawei.it.euler.util.Converter;

@Mapper
public abstract class SoftwareToVOConverter implements Converter<Software, SoftwareVo> {
    public static final SoftwareToVOConverter INSTANCE = Mappers.getMapper(SoftwareToVOConverter.class);
}
