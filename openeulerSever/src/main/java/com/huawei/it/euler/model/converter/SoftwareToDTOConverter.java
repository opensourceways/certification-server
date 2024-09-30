/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.dto.SoftwareDTO;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.util.Converter;

@Mapper
public  abstract class SoftwareToDTOConverter implements Converter<Software, SoftwareDTO> {
    public static final SoftwareToDTOConverter INSTANCE = Mappers.getMapper( SoftwareToDTOConverter.class );

    @Override
    @Mapping(target = "id", ignore = true)
    public abstract SoftwareDTO convert(Software source);
}

