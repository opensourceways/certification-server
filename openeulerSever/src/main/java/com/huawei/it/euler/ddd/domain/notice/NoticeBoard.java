/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice;

import com.huawei.it.euler.ddd.domain.notice.primitive.ActiveStatus;
import lombok.Data;

import java.time.LocalDateTime;
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
    ActiveStatus isActive;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public void publish() {
        this.isActive = ActiveStatus.ACTIVE;
        this.updateTime = LocalDateTime.now();
    }

}
