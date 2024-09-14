/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.mapper.CompatibleDataMapper;
import com.huawei.it.euler.model.entity.CompatibleDataApproval;
import com.huawei.it.euler.model.entity.CompatibleDataInfo;
import com.huawei.it.euler.model.vo.ApprovalDataVo;
import com.huawei.it.euler.model.vo.CompatibleDataSearchVo;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import com.huawei.it.euler.model.vo.FileDataVo;
import com.huawei.it.euler.service.CompatibleDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;

/**
 * CompatibleDataController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/compatibleData")
public class CompatibleDataController {
    @Autowired
    private CompatibleDataService compatibleDataService;

    @Autowired
    private CompatibleDataMapper compatibleDataMapper;

    @Autowired
    private AccountService accountService;

    /**
     * 兼容性数据材料模板下载
     *
     * @param response response
     */
    @GetMapping("/download")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public void downloadDataTemplate(HttpServletResponse response) throws IOException {
        compatibleDataService.downloadDataTemplate(response);
    }

    /**
     * 上传兼容性数据表格
     *
     * @param file file
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/uploadTemplate")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<ExcelInfoVo> uploadTemplate(@RequestParam("file") @NotNull(message = "模板文件不能为空")
        MultipartFile file, HttpServletRequest request) throws InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return compatibleDataService.uploadTemplate(file, uuid);
    }

    /**
     * 读取兼容性数据表格
     *
     * @param response response
     * @param request request
     * @param fileId fileId
     * @return JsonResponse
     */
    @GetMapping("/upload")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<FileDataVo> readCompatibleData(HttpServletResponse response, HttpServletRequest request,
        @RequestParam("fileId") @NotBlank(message = "附件id不能为空") @Length(max = 50, message = "附件id超出范围")
        String fileId) throws InputException, IllegalAccessException, NoLoginException {
        UserInfo loginUser = accountService.getLoginUser(request);
        return compatibleDataService.readCompatibleData(loginUser, fileId);
    }

    /**
     * 分页筛选返回数据列表（伙伴侧华为侧共用）
     *
     * @param searchVo searchVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/findDataList")
    @PreAuthorize("hasAnyRole('OSV_user', 'flag_store')")
    public JsonResponse<Map<String, Object>> findDataList(
            @RequestBody @Valid CompatibleDataSearchVo searchVo, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return compatibleDataService.findDataList(searchVo, uuid);
    }

    /**
     * 查看单条数据详情（伙伴侧华为侧共用）
     *
     * @param dataId dataId
     * @return JsonResponse
     */
    @GetMapping("/getDataDetailInfo")
    @PreAuthorize("hasAnyRole('OSV_user', 'flag_store')")
    public JsonResponse<CompatibleDataInfo> getDataDetailInfo(
            @RequestParam("dataId") @NotNull(message = "数据id不能为空") Integer dataId) {
        CompatibleDataInfo dataInfo = compatibleDataMapper.selectDataByDataId(dataId);
        if (dataInfo == null) {
            return JsonResponse.failed("该数据不存在");
        }
        return JsonResponse.success(dataInfo);
    }

    /**
     * 查看单条数据审核详情（伙伴侧华为侧共用）
     *
     * @param dataId dataId
     * @return JsonResponse
     */
    @GetMapping("/getDataApprovalInfo")
    @PreAuthorize("hasAnyRole('OSV_user', 'flag_store')")
    public JsonResponse<CompatibleDataApproval> getDataApprovalInfo(
            @RequestParam("dataId") @NotNull(message = "数据id不能为空") Integer dataId) {
        CompatibleDataApproval approval = compatibleDataMapper.selectDataApproval(dataId);
        if (approval == null) {
            return JsonResponse.failed("该数据不存在");
        }
        return JsonResponse.success(approval);
    }

    /**
     * 华为侧审核
     *
     * @param vo vo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/approvalCompatibleData")
    @PreAuthorize("hasAnyRole('flag_store')")
    public JsonResponse<String> approvalCompatibleData(
            @RequestBody @Valid ApprovalDataVo vo, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return compatibleDataService.approvalCompatibleData(vo, uuid);
    }
}
