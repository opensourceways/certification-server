/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import lombok.Getter;

/**
 * 发送方式枚举
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Getter
public enum MsgType {
    COMPANY_APPROVE("companyApproveResultNotice", "企业审核结果通知"),
    INTEL_APPLY_NOTICE("intelApplyNotice", "英特尔认证申请通知"),
    SOFTWARE_PROGRESS_NOTICE("softwareProgressNotice", "测评业务进度通知");

    private final String value;

    private final String desc;

    MsgType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}