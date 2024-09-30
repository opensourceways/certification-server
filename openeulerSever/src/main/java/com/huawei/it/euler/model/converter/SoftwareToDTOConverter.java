package com.huawei.it.euler.model.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.huawei.it.euler.model.dto.SoftwareDTO;
import com.huawei.it.euler.model.entity.Software;

@Mapper
public  interface SoftwareToDTOConverter  {
    SoftwareToDTOConverter INSTANCE = Mappers.getMapper( SoftwareToDTOConverter.class );

    SoftwareDTO model2DTO(Software software);
}

