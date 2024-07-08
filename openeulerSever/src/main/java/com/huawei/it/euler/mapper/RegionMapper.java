/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RegionMapper
 *
 * @since 2024/07/04
 */
@Repository
public interface RegionMapper {
    List<String> findAllCountryName();

    List<String> findAllProvince();

    List<String> findCityByProvince(@Param("province") String province);
}
