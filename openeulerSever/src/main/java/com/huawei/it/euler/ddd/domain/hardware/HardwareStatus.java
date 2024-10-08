/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Getter;

/**
 * 硬件-状态枚举
 *
 * @author zhaoyan
 * @since 2024-10-08
 */
@Getter
public enum HardwareStatus {
    WAIT_APPLY("1","待提交"),
    WAIT_APPROVE("2","待审批"),
    PASS("3","已通过"),
    REJECT("-1","已驳回");
    private final String status;

    private final String statusName;

    HardwareStatus(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

}
