/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software;

import lombok.Data;

/**
 * 测评业务统计对象
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
@Data
public class SoftwareStatistics {

    /**
     * 业务数量
     */
    private Integer count;

    /**
     * 时间筛选对象
     */
    private String dateKey;

    /**
     * 日期周期
     */
    private String datePeriod;

    /**
     * 测评机构
     */
    private String testOrganization;


    /**
     * 产品类型
     */
    private String productType;




}
