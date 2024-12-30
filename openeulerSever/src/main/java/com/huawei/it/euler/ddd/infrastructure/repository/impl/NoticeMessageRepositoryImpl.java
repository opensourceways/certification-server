/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessageRepository;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.NoticeMessageMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.NoticeMessagePO;
import com.huawei.it.euler.ddd.infrastructure.repository.converter.NoticeMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 系统消息通知记录 持久化接口实现类
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Repository
public class NoticeMessageRepositoryImpl implements NoticeMessageRepository {

    @Autowired
    private NoticeMessageMapper mapper;

    @Autowired
    private NoticeMessageConverter converter;

    @Override
    public NoticeMessage record(NoticeMessage message) {
        NoticeMessagePO po = converter.toPO(message);
        mapper.insert(po);
        return converter.toDO(po);
    }
}
