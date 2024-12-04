/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoard;
import com.huawei.it.euler.model.vo.HashRatePlatformVo;
import com.huawei.it.euler.model.vo.OsVo;
import com.huawei.it.euler.model.vo.ProductVo;
import com.huawei.it.euler.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * MasterDataController
 *
 * @since 2024/07/05
 */
@Tag(name = "系统数据接口", description = "系统数据接口")
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<OsVo>> findAllOs() {
        return JsonResponse.success(masterDataService.findAllOs());
    }

    /**
     * 查询算力平台
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllComputingPlatform")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<HashRatePlatformVo>> findAllComputingPlatform() {
        return JsonResponse.success(masterDataService.findAllComputingPlatform());
    }

    /**
     * 查询产品
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllProduct")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<ProductVo>> findAllProduct() {
        return JsonResponse.success(masterDataService.findAllProduct());
    }

    /**
     * 查询测试机构
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllInnovationCenter")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<List<String>> findAllInnovationCenter() {
        return JsonResponse.success(masterDataService.findAllInnovationCenter());
    }

    /**
     * 查询CPU硬件厂商
     *
     * @return JsonResponse
     */
    @GetMapping("/software/findAllCPUVendor")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<String>> findAllCPUVendor() {
        return JsonResponse.success(masterDataService.findAllCPUVendor());
    }

    /**
     * 系统公告消息
     *
     * @return JsonResponse
     */
    @Operation(summary = "系统公告消息")
    @GetMapping("/software/findNoticeBoardList")
    public JsonResponse<List<NoticeBoard>> findNoticeBoardList() {
        return JsonResponse.success(masterDataService.findNoticeBoardList());
    }
}
