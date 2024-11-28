/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice;

import lombok.Data;

import java.util.Date;

/**
 * 系统公告栏
 *
 * @author zhaoyan
 * @since 2024-11-28
 */
@Data
public class NoticeBoard {

    /**
     * 公告消息
     */
    String noticeInfo;

    /**
     * 是否有效状态
     */
    String isActive;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
