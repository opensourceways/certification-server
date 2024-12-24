/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.converter;

import com.huawei.it.euler.ddd.domain.notice.NoticeBoard;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.NoticeBoardPO;
import org.mapstruct.Mapper;

/**
 * 系统公告 po do转换器
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Mapper(componentModel = "spring")
public interface NoticeBoardConverter {
    NoticeBoardPO toPO(NoticeBoard noticeBoard);

    NoticeBoard toDO(NoticeBoardPO noticeBoardPO);
}