/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import lombok.Getter;

/**
 * 系统消息通知记录状态枚举
 *
 * @author zhaoyan
 * @since 2024-12-18
 */
@Getter
public enum Status {
    FAILED(-1, "发送失败"),
    SUCCESS(1, "发送成功");

    private final Integer value;

    private final String desc;

    Status(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
