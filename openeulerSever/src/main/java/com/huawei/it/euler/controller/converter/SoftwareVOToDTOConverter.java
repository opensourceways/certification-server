/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.dto.SoftwareDTO;
import com.huawei.it.euler.model.vo.SoftwareVo;
import com.huawei.it.euler.util.Converter;

@Mapper
public  abstract class SoftwareVOToDTOConverter implements Converter<SoftwareVo, SoftwareDTO> {
    public static final SoftwareVOToDTOConverter INSTANCE = Mappers.getMapper( SoftwareVOToDTOConverter.class );

    @Override
    @Mappings({@Mapping(source = "userUuid", target = "applicant"),
            @Mapping(source = "statusName", target = "status"),
            @Mapping(source = "certificationTime", target = "certificationTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "applicationTime", target = "applicationTime",dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    public abstract SoftwareDTO convert(SoftwareVo source);
}

