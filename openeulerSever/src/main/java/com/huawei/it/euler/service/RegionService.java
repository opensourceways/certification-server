/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地区
 *
 * @since 2024/07/03
 */
public interface RegionService {
    /**
     * 查询所有国家名称
     */
    List<String> findAllCountryName();

    /**
     * 查询所有省份
     *
     * @return 省份列表
     */
    List<String> findAllProvince();

    /**
     * 根据省份查询市区
     *
     * @param province 省
     * @return 市、区
     */
    List<String> findCityByProvince(@Param("province") String province);
}
