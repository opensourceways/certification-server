/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import java.util.Date;

import lombok.Data;

/**
 * 操作系统 实体类
 *
 * @since 2024/07/02
 */
@Data
public class Os {
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
     * 对应欧拉版本
     */
    private String relatedOsVersion;
    
    /**
     * 更新时间
     */
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    private String LastUpdatedBy;
}
