/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice;

import java.util.List;

/**
 * 系统公告 持久化接口
 *
 * @author zhaoyan
 * @since 2024-12-16
 */
public interface NoticeBoardRepository {
    /**
     * 查询有效系统公告，有效：active=y
     * @return 有效系统公告集合
     */
    public List<NoticeBoard> findActiveList();
}