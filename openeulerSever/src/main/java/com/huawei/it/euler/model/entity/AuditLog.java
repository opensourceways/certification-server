/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * AuditLog
 *
 * @since 2024/07/02
 */
@Data
@Accessors(chain = true)
public class AuditLog {
    private Date time;

    private String hostIp;

    private String uuid;

    private String module;

    private String operate;

    private String message;
}
