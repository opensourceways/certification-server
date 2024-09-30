/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.security.LockCacheConfig;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.NodeEnum;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.impl.SoftwareServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;

/**
 * SoftwareController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
public class SoftwareController {
    @Autowired
    private SoftwareServiceImpl softwareService;

    @Autowired
    private LockCacheConfig lockCacheConfig;

    @Autowired
    private AccountService accountService;

    /**
     * 根据id查询软件认证详情
     *
     * @param id id
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/findById")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<Software> findById(@RequestParam("id") @NotNull(message = "认证id不能为空") Integer id,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        Software software = softwareService.findById(id, uuid);
        return JsonResponse.success(software);
    }

    /**
     * 申请评测
     *
     * @param software software
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/register")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<String> softwareRegister(@RequestBody @Valid Software software, HttpServletRequest request)
        throws InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = software.getCompanyCode() + "-" + software.getProductName() + "-" + uuid;
        lockCacheConfig.acquireLock(lockKey);
        Integer id = softwareService.createSoftware(software, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success(id.toString());
    }

    /**
     * 撤销评测
     *
     */
    @PostMapping("/software/withdraw-software")
    @PreAuthorize("hasAnyRole('user','euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<String> softwareWithdraw(@RequestBody @Validated ProcessVo processVo,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.withdrawSoftware(processVo, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * 删除评测
     *
     */
    @DeleteMapping("/software/delete-register")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<String> softwareDelete(@RequestParam("id") @NotNull(message = "认证id不能为空") Integer id,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(id);
        lockCacheConfig.acquireLock(lockKey);
        softwareService.deleteSoftware(id, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * program_review 方案审核
     */
    @PostMapping("/software/programReview")
    @PreAuthorize("hasAnyRole( 'euler_ic' , 'openatom_intel',  'admin')")
    public JsonResponse<String> programReview(@RequestBody @Validated ProcessVo processVo, HttpServletRequest request)
        throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.commonProcess(processVo, uuid, NodeEnum.PROGRAM_REVIEW.getId());
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * program_review 测试阶段
     */
    @PostMapping("/software/testingPhase")
    @PreAuthorize("hasAnyRole( 'user', 'euler_ic','openatom_intel',  'admin')")
    public JsonResponse<String> testingPhase(@RequestBody @Validated ProcessVo processVo, HttpServletRequest request)
        throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.testingPhase(processVo, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * reportReview 报告初审
     */
    @PostMapping("/software/reportReview")
    @PreAuthorize("hasAnyRole( 'euler_ic',  'admin')")
    public JsonResponse<String> reportReview(@RequestBody @Validated ProcessVo processVo, HttpServletRequest request)
        throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.reportReview(processVo, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * reportReview 报告复审
     */
    @PostMapping("/software/reportReReview")
    @PreAuthorize("hasAnyRole( 'report_review',  'admin')")
    public JsonResponse<String> reportReReview(@RequestBody @Validated ProcessVo processVo, HttpServletRequest request)
        throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.commonProcess(processVo, uuid, NodeEnum.REPORT_RE_REVIEW.getId());
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * 证书初审
     *
     * @param software softwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/certificateReview")
    @PreAuthorize("hasAnyRole('flag_store')")
    public JsonResponse<String> certificateReview(@RequestBody @Validated SoftwareVo software,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(software.getId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.reviewCertificate(software, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * certificateConfirmation 证书确认
     */
    @PostMapping("/software/certificateConfirmation")
    @PreAuthorize("hasAnyRole( 'user',  'admin')")
    public JsonResponse<String> certificateConfirmation(@RequestBody @Validated ProcessVo processVo,
        HttpServletRequest request) throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.certificateConfirmation(processVo, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * certificateConfirmation 证书签发
     */
    @PostMapping("/software/certificateIssuance")
    @PreAuthorize("hasAnyRole( 'certificate_issuance',  'admin')")
    public JsonResponse<String> certificateIssuance(@RequestBody @Validated ProcessVo processVo,
        HttpServletRequest request) throws Exception {
        String uuid = accountService.getLoginUuid(request);
        String lockKey = String.valueOf(processVo.getSoftwareId());
        lockCacheConfig.acquireLock(lockKey);
        softwareService.certificateIssuance(processVo, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * 获取转审人员列表
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/transferredUserList")
    @PreAuthorize("hasAnyRole('euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<List<SimpleUserVo>> transferredUserList(
        @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request)
        throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        List<SimpleUserVo> simpleUserVos = softwareService.transferredUserList(softwareId, uuid);
        return JsonResponse.success(simpleUserVos);
    }

    /**
     * 查询审批流节点信息
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/node")
    @PreAuthorize("hasAnyRole('euler_ic', 'program_review','report_review','certificate_issuance','openatom_intel', 'flag_store', 'user')")
    public JsonResponse<List<AuditRecordsVo>>
        node(@RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request)
            throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        List<AuditRecordsVo> nodeList = softwareService.getNodeList(softwareId, uuid);
        return JsonResponse.success(nodeList);
    }

    /**
     * 伙伴侧查询申请列表
     *
     * @param softwareQueryRequest selectSoftwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/softwareList")
    @PreAuthorize("hasAnyRole('user')")
    public JsonResponse<PageResult<SoftwareListVo>> getSoftwareList(
            @RequestBody @Valid SoftwareQueryRequest softwareQueryRequest, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        PageResult<SoftwareListVo> softwareList = softwareService.getSoftwareList(softwareQueryRequest, uuid);
        softwareList.getList().forEach(softwareListVo -> {
            List<ComputingPlatformVo> platformVos =
                JSONObject.parseArray(softwareListVo.getHashratePlatform()).toJavaList(ComputingPlatformVo.class);
            softwareListVo.setHashratePlatformList(platformVos);
            StringBuffer buffer = new StringBuffer();
            platformVos.stream().map(ComputingPlatformVo::getPlatformName)
                .forEach(item -> buffer.append(item).append("/"));
            softwareListVo.setHashratePlatformaNameList(buffer.substring(0, buffer.lastIndexOf("/")));
        });
        return JsonResponse.success(softwareList);
    }

    /**
     * 华为侧查询申请列表
     *
     * @param softwareQueryRequest selectSoftwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/reviewSoftwareList")
    @PreAuthorize("hasAnyRole( 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<PageResult<SoftwareListVo>> getReviewSoftwareList(
            @RequestBody @Valid SoftwareQueryRequest softwareQueryRequest, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        PageResult<SoftwareListVo> reviewSoftwareList = softwareService.getReviewSoftwareList(softwareQueryRequest, uuid);
        reviewSoftwareList.getList().forEach(softwareListVo -> {
            List<ComputingPlatformVo> platformVos =
                JSONObject.parseArray(softwareListVo.getHashratePlatform()).toJavaList(ComputingPlatformVo.class);
            softwareListVo.setHashratePlatformList(platformVos);
            StringBuffer buffer = new StringBuffer();
            platformVos.stream().map(ComputingPlatformVo::getPlatformName)
                .forEach(item -> buffer.append(item).append("/"));
            softwareListVo.setHashratePlatformaNameList(buffer.substring(0, buffer.lastIndexOf("/")));
        });
        return JsonResponse.success(reviewSoftwareList);
    }

    /**
     * 认证审核记录
     *
     * @param softwareId softwareId
     * @param nodeName nodeName
     * @param curPage curPage
     * @param pageSize pageSize
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/auditRecordsList")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<IPage<AuditRecordsVo>> getAuditRecordsList(
        @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
        @RequestParam("nodeName") String nodeName,
        @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
        @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空") @Range(min = 0, max = 100,
            message = "每页展示条数超出范围") Integer pageSize,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        IPage<AuditRecordsVo> page = new Page<>(curPage, pageSize);
        return JsonResponse.success(softwareService.getAuditRecordsListPage(softwareId, nodeName, page, uuid));
    }

    /**
     * 证书信息确认查询
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/certificateInfo")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<CertificateInfoVo> certificateInfo(
        @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request)
        throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return new JsonResponse<>(softwareService.certificateInfo(softwareId, uuid));
    }

    /**
     * 上传文件
     *
     * @param softwareId softwareId
     * @param file file
     * @param fileTypeCode fileTypeCode
     * @param fileType fileType
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/upload")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'openatom_intel', 'OSV_user')")
    public JsonResponse<String> upload(@RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("fileTypeCode") @NotNull(message = "文件类型编码不能为空") Integer fileTypeCode,
        @RequestParam("fileType") @NotBlank(message = "文件具体类型不能为空") String fileType, HttpServletRequest request)
        throws TestReportExceedMaxAmountException, InputException, NoLoginException {
        String lockKey = "upload-file-" + softwareId;
        String uuid = accountService.getLoginUuid(request);
        lockCacheConfig.acquireLock(lockKey);
        softwareService.upload(file, softwareId, fileTypeCode, fileType, uuid);
        lockCacheConfig.releaseLock(lockKey);
        return JsonResponse.success();
    }

    /**
     * 查询上传文件名称
     *
     * @param softwareId softwareI
     * @param fileType fileType
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/getAttachments")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<AttachmentsVo>> getAttachmentsNames(
        @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
        @RequestParam("fileType") @NotBlank(message = "文件具体类型不能为空") String fileType, HttpServletRequest request)
        throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return JsonResponse.success(softwareService.getAttachmentsNames(softwareId, fileType, uuid));
    }

    /**
     * 附件下载
     *
     * @param fileId fileId
     * @param response response
     * @param request request
     */
    @GetMapping("/software/downloadAttachments")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void downloadAttachments(@RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId,
        HttpServletResponse response, HttpServletRequest request)
        throws InputException, UnsupportedEncodingException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        softwareService.downloadAttachments(fileId, response, uuid);
    }

    /**
     * 删除附件
     *
     * @param fileId fileId
     * @param request request
     * @return JsonResponse
     */
    @DeleteMapping("/software/deleteAttachments")
    @PreAuthorize("hasAnyRole('user')")
    public JsonResponse<String> deleteAttachments(@RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId,
        HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        softwareService.deleteAttachments(fileId, uuid);
        return JsonResponse.success();
    }

    /**
     * 证书预览
     *
     * @param softwareId softwareId
     * @param response response
     */
    @GetMapping("/software/previewCertificate")
    @PreAuthorize("hasAnyRole('user', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void previewCertificate(@RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
        HttpServletResponse response) throws IOException {
        softwareService.previewCertificate(softwareId, response);
    }

    /**
     * 证书预览
     *
     * @param certificateConfirmVo certificateConfirmVo
     * @param response response
     */
    @PostMapping("/software/previewCertificateConfirmInfo")
    @PreAuthorize("hasAnyRole('user','euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void previewCertificateConfirmInfo(@Valid @RequestBody CertificateConfirmVo certificateConfirmVo,
        HttpServletResponse response) throws Exception {
        softwareService.previewCertificateConfirmInfo(certificateConfirmVo, response);
    }

    /**
     * 预览签名
     *
     * @param fileId fileId
     * @param response response
     */
    @GetMapping("/software/imagePreview")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void previewImage(@RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId,
        HttpServletResponse response) throws InputException {
        softwareService.previewImage(fileId, response);
    }

    @GetMapping("/software/filterCriteria")
    public JsonResponse filterCriteria() {
        return new JsonResponse(JsonResponse.SUCCESS_STATUS, JsonResponse.SUCCESS_MESSAGE,
            softwareService.filterCeriteria());
    }

    /**
     * 查询社区软件清单
     *
     * @param vo 筛选条件
     * @return 软件清单
     */
    @PostMapping("/software/communityChecklist")
    public JsonResponse<PageVo<CompatibilityVo>> communityCheckList(@RequestBody SoftwareFilterVo vo) {
        PageVo<CompatibilityVo> communityCheckList = softwareService.findCommunityCheckList(vo);
        return JsonResponse.success(communityCheckList);
    }

    /**
     * 附件下载
     *
     * @param softwareQueryRequest fileId
     * @param response response
     * @param request request
     */
    @PostMapping("/software/export")
    @PreAuthorize("hasAnyRole( 'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store')")
    public void export(@RequestBody @Valid SoftwareQueryRequest softwareQueryRequest,
        HttpServletResponse response, HttpServletRequest request) throws InputException, IOException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        softwareService.export(softwareQueryRequest, response, uuid);
    }
}
