/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.model.vo.HashRatePlatformVo;
import com.huawei.it.euler.model.vo.OsVo;
import com.huawei.it.euler.model.vo.ProductVo;
import com.huawei.it.euler.service.MasterDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * MasterDataController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
public class MasterDataController {
    @Resource
    private MasterDataService masterDataService;

    /**
     * 查询os
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllOs")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<List<OsVo>> findAllOs() {
        return JsonResponse.success(masterDataService.findAllOs());
    }

    /**
     * 查询算力平台
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllComputingPlatform")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<List<HashRatePlatformVo>> findAllComputingPlatform() {
        return JsonResponse.success(masterDataService.findAllComputingPlatform());
    }

    /**
     * 查询产品
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllProduct")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<List<ProductVo>> findAllProduct() {
        return JsonResponse.success(masterDataService.findAllProduct());
    }

    /**
     * 查询测试机构
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllInnovationCenter")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<List<String>> findAllInnovationCenter() {
        return JsonResponse.success(masterDataService.findAllInnovationCenter());
    }
}
