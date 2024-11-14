/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import java.util.Date;

import lombok.Data;

/**
 * 产品类型 实体类
 *
 * @since 2024/07/02
 */
@Data
public class Product {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品类型子类型
     */
    private String productChildrenType;

    /**
     * 更新时间
     */
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    private String LastUpdatedBy;
}
