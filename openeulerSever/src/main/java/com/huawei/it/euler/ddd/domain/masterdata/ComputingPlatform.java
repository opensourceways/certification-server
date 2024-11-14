/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import java.util.Date;

import lombok.Data;

/**
 * 算力平台 实体对象
 *
 * @since 2024/07/02
 */
@Data
public class ComputingPlatform {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 算力平台
     */
    private String platformName;

    /**
     * 服务厂家
     */
    private String serverProvider;

    /**
     * 服务器类型
     */
    private String serverType;

    /**
     * 更新时间
     */
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    private String LastUpdatedBy;

}
