/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.dto.SoftwareDTO;
import com.huawei.it.euler.model.vo.SoftwareListVo;
import com.huawei.it.euler.util.Converter;

@Mapper
public  abstract class SoftwareVOToDTOConverter implements Converter<SoftwareListVo, SoftwareDTO> {
    public static final SoftwareVOToDTOConverter INSTANCE = Mappers.getMapper( SoftwareVOToDTOConverter.class );
}

