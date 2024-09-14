/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.security.LockCacheConfig;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.impl.SoftwareServiceImpl;
import com.huawei.it.euler.util.ListPageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<Software> findById(
            @RequestParam("id") @NotNull(message = "认证id不能为空") Integer id, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        Software software = softwareService.findById(id, uuid);
        if (software == null || (accountService.isPartner(uuid) && !Objects.equals(uuid, software.getUserUuid()))) {
            throw new ParamException("该用户无权访问当前信息");
        }
        return JsonResponse.success(software);
    }

    /**
     * 查询社区软件清单
     * @param vo 筛选条件
     * @return 软件清单
     */
    @PostMapping("/software/communityChecklist")
    public JsonResponse<PageVo<CompatibilityVo>> communityCheckList(@RequestBody SoftwareFilterVo vo) {
        PageVo<CompatibilityVo> communityCheckList = softwareService.findCommunityCheckList(vo);
        return JsonResponse.success(communityCheckList);
    }

    /**
     * 旗舰店证书确认
     *
     * @param software softwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/update")
    @PreAuthorize("hasAnyRole('flag_store')")
    public JsonResponse<String> update(
            @RequestBody @Validated SoftwareVo software, HttpServletRequest request) throws IOException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        return softwareService.updateSoftware(software, uuid, request);
    }

    /**
     * 提交软件认证
     *
     * @param software software
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/register")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<String> softwareRegister(
            @RequestBody @Valid Software software, HttpServletRequest request) throws IOException, InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        softwareService.insertSoftware(software, uuid, request);
        return JsonResponse.success();
    }

    /**
     * 软件认证审核
     *
     * @param processVo processVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/processReview")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<String> processReview(
            @RequestBody @Validated ProcessVo processVo, HttpServletRequest request) throws Exception {
        String uuid = accountService.getLoginUuid(request);
        return softwareService.processReview(processVo, uuid, request);
    }

    /**
     * 获取转审人员列表
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/transferredUserList")
    @PreAuthorize("hasAnyRole('sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<List<SimpleUserVo>> transferredUserList(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) throws NoLoginException {
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
    @PreAuthorize("hasAnyRole('sig_group', 'euler_ic','openatom_intel', 'flag_store', 'user')")
    public JsonResponse<List<AuditRecordsVo>> node(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        Software software = softwareService.findById(softwareId, uuid);
        if (accountService.isPartner(uuid)) {
            if (software != null && !Objects.equals(uuid, software.getUserUuid())) {
                throw new ParamException("无权限查询该测评申请节点信息");
            }
        }
        List<AuditRecordsVo> nodeList = softwareService.getNodeList(softwareId);
        return JsonResponse.success(nodeList);
    }

    /**
     * 伙伴侧查询申请列表
     *
     * @param selectSoftwareVo selectSoftwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/softwareList")
    @PreAuthorize("hasAnyRole('ROLE_user', 'user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<Map<String, Object>> getSoftwareList(
            @RequestBody @Valid SelectSoftwareVo selectSoftwareVo, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        List<SoftwareListVo> softwareList = softwareService.getSoftwareList(selectSoftwareVo, uuid);
        softwareList.forEach(softwareListVo -> {
            List<ComputingPlatformVo> platformVos =
                    JSONObject.parseArray(softwareListVo.getHashratePlatform()).toJavaList(ComputingPlatformVo.class);
            softwareListVo.setHashratePlatformList(platformVos);
            StringBuffer buffer = new StringBuffer();
            platformVos.stream()
                    .map(ComputingPlatformVo::getPlatformName)
                    .forEach(item -> buffer.append(item).append("/"));
            softwareListVo.setHashratePlatformaNameList(buffer.substring(0, buffer.lastIndexOf("/")));
        });
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("list",
                ListPageUtils.getListPage(softwareList, selectSoftwareVo.getPageNum(), selectSoftwareVo.getPageSize()));
        hashMap.put("total", softwareList.size());
        return JsonResponse.success(hashMap);
    }

    /**
     * 华为侧查询申请列表
     *
     * @param selectSoftwareVo selectSoftwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/reviewSoftwareList")
    @PreAuthorize("hasAnyRole('china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin')")
    public JsonResponse<Map<String, Object>> getReviewSoftwareList(
            @RequestBody @Valid SelectSoftwareVo selectSoftwareVo, HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        List<SoftwareListVo> reviewSoftwareList = softwareService.getReviewSoftwareList(selectSoftwareVo, uuid);
        reviewSoftwareList.forEach(softwareListVo -> {
            List<ComputingPlatformVo> platformVos =
                    JSONObject.parseArray(softwareListVo.getHashratePlatform()).toJavaList(ComputingPlatformVo.class);
            softwareListVo.setHashratePlatformList(platformVos);
            StringBuffer buffer = new StringBuffer();
            platformVos.stream()
                    .map(ComputingPlatformVo::getPlatformName)
                    .forEach(item -> buffer.append(item).append("/"));
            softwareListVo.setHashratePlatformaNameList(buffer.substring(0, buffer.lastIndexOf("/")));
        });
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("list", ListPageUtils.getListPage(reviewSoftwareList,
                selectSoftwareVo.getPageNum(), selectSoftwareVo.getPageSize()));
        hashMap.put("total", reviewSoftwareList.size());
        return JsonResponse.success(hashMap);
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store')")
    public JsonResponse<IPage<AuditRecordsVo>> getAuditRecordsList(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("nodeName") String nodeName,
            @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
            @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空")
            @Range(min = 0, max = 100, message = "每页展示条数超出范围") Integer pageSize, HttpServletRequest request) throws NoLoginException {
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store')")
    public JsonResponse<CertificateInfoVo> certificateInfo(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) throws NoLoginException {
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<String> upload(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileTypeCode") @NotNull(message = "文件类型编码不能为空") Integer fileTypeCode,
            @RequestParam("fileType") @NotBlank(message = "文件具体类型不能为空") String fileType,
            HttpServletRequest request) throws TestReportExceedMaxAmountException, InputException, NoLoginException {
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store')")
    public JsonResponse<List<AttachmentsVo>> getAttachmentsNames(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("fileType") @NotBlank(message = "文件具体类型不能为空") String fileType,
            HttpServletRequest request) throws NoLoginException {
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void downloadAttachments(
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId, HttpServletResponse response,
            HttpServletRequest request) throws InputException, UnsupportedEncodingException, NoLoginException {
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
    public JsonResponse<String> deleteAttachments(
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId, HttpServletRequest request) throws NoLoginException {
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void previewCertificate(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            HttpServletResponse response) throws InputException, IOException {
        softwareService.previewCertificate(softwareId, response);
    }

    /**
     * 证书预览
     *
     * @param certificateConfirmVo certificateConfirmVo
     * @param response response
     */
    @PostMapping("/software/previewCertificateConfirmInfo")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public void previewImage(
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId,
            HttpServletResponse response) throws InputException {
        softwareService.previewImage(fileId, response);
    }

    @GetMapping("/software/filterCriteria")
    public JsonResponse filterCriteria() {
        return new JsonResponse(JsonResponse.SUCCESS_STATUS,JsonResponse.SUCCESS_MESSAGE,softwareService.filterCeriteria());
    }
}
