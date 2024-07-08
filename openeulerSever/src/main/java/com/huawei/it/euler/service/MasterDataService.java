/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.huawei.it.euler.model.vo.HashRatePlatformVo;
import com.huawei.it.euler.model.vo.OsVo;
import com.huawei.it.euler.model.vo.ProductVo;

import java.util.List;

/**
 * MasterDataService
 *
 * @since 2024/07/03
 */
public interface MasterDataService {
    List<OsVo> findAllOs();

    List<ProductVo> findAllProduct();

    List<String> findAllInnovationCenter();

    List<HashRatePlatformVo> findAllComputingPlatform();
}
