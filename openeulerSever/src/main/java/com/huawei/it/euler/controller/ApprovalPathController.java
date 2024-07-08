/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.model.vo.ApprovalPathSearchVo;
import com.huawei.it.euler.model.vo.ApprovalPathVo;
import com.huawei.it.euler.service.ApprovalPathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * ApprovalPathController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@RequestMapping("/approvalPath")
@Validated
public class ApprovalPathController {
    @Autowired
    private ApprovalPathService approvalPathService;

    /**
     * 分页查询审批路径
     *
     * @param approvalPathSearchVo approvalPathSearchVo
     * @return JsonResponse
     */
    @PostMapping("/getApprovalPathByPage")
    @PreAuthorize("hasRole('admin')")
    public JsonResponse<IPage<ApprovalPathVo>> findApprovalPathListByPage(
            @Valid @RequestBody ApprovalPathSearchVo approvalPathSearchVo) {
        Page<ApprovalPathVo> page = new Page<>(approvalPathSearchVo.getCurPage(), approvalPathSearchVo.getPageSize());
        IPage<ApprovalPathVo> approvalPathByPage =
                approvalPathService.findApprovalPathByPage(approvalPathSearchVo, page);
        return JsonResponse.success(approvalPathByPage);
    }
}
