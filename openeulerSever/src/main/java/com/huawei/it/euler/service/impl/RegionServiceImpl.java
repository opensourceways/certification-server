/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.huawei.it.euler.mapper.RegionMapper;
import com.huawei.it.euler.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RegionServiceImpl
 *
 * @since 2024/07/04
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {
    @Autowired
    private RegionMapper regionMapper;

    @Override
    public List<String> findAllCountryName() {
        return regionMapper.findAllCountryName();
    }

    @Override
    public List<String> findAllProvince() {
        return regionMapper.findAllProvince();
    }

    @Override
    public List<String> findCityByProvince(String province) {
        return regionMapper.findCityByProvince(province);
    }
}
