/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.converter;

import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.NoticeMessagePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

/**
 * 注释
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Mapper(componentModel = "spring",imports = {LocalDateTime.class})
public interface NoticeMessageConverter {
    @Mapping(target = "updateTime", expression = "java(LocalDateTime.now())")
    NoticeMessagePO toPO(NoticeMessage noticeMessage);

    NoticeMessage toDO(NoticeMessagePO noticeMessagePO);
}
