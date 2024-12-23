/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice;

/**
 * 系统消息通知记录 持久化接口
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
public interface NoticeMessageRepository {
    public NoticeMessage record(NoticeMessage message);
}
