/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.request;

import java.util.Date;

import lombok.Data;

@Data
public class OsCreateRequest {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 系统名称
     */
    private String osName;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * 对应版本
     */
    private String relatedOsVersion;

    /**
     * 更新时间
     */
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    private Integer LastUpdatedBy;
}
