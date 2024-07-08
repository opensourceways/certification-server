/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.impl.SoftwareServiceImpl;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.ListPageUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.huawei.it.euler.service.impl.SoftwareServiceImpl.PARTNER_ROLE;

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
    private EncryptUtils encryptUtils;

    @Autowired
    private SoftwareMapper softwareMapper;

    /**
     * 根据id查询软件认证详情
     *
     * @param id id
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/findById")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<Software> findById(
            @RequestParam("id") @NotNull(message = "认证id不能为空") Integer id, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        Software software = softwareService.findById(id, cookieUuid);
        return JsonResponse.success(software);
    }

    /**
     * 旗舰店证书确认
     *
     * @param softwareVo softwareVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/update")
    @PreAuthorize("hasAnyRole('flag_store')")
    public JsonResponse<String> update(
            @RequestBody @Validated SoftwareVo softwareVo, HttpServletRequest request) throws IOException {
        String cookieUuid = UserUtils.getCookieUuid(request);
        return softwareService.updateSoftware(softwareVo, cookieUuid, request);
    }

    /**
     * 提交软件认证
     *
     * @param software software
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/software/register")
    @PreAuthorize("hasAnyRole('user')")
    public JsonResponse<String> softwareRegister(
            @RequestBody @Validated Software software, HttpServletRequest request) throws IOException, InputException {
        String cookieUuid = UserUtils.getCookieUuid(request);
        softwareService.insertSoftware(software, cookieUuid, request);
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<String> processReview(
            @RequestBody @Validated ProcessVo processVo, HttpServletRequest request) throws IOException {
        String cookieUuid = UserUtils.getCookieUuid(request);
        return softwareService.processReview(processVo, cookieUuid, request);
    }

    /**
     * 获取转审人员列表
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/transferredUserList")
    @PreAuthorize("hasAnyRole('sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<List<SimpleUserVo>> transferredUserList(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        List<SimpleUserVo> simpleUserVos = softwareService.transferredUserList(softwareId, cookieUuid);
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
    @PreAuthorize("hasAnyRole('sig_group', 'euler_ic', 'flag_store', 'user')")
    public JsonResponse<List<AuditRecordsVo>> node(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        Software software = softwareMapper.findById(softwareId);
        List<String> roles = softwareService.getRoles(uuid);
        if (PARTNER_ROLE.containsAll(roles)) {
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<Map<String, Object>> getSoftwareList(
            @RequestBody @Validated SelectSoftwareVo selectSoftwareVo, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        List<SoftwareListVo> softwareList = softwareService.getSoftwareList(selectSoftwareVo, cookieUuid);
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
    @PreAuthorize("hasAnyRole('china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin')")
    public JsonResponse<Map<String, Object>> getReviewSoftwareList(
            @RequestBody @Validated SelectSoftwareVo selectSoftwareVo, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        List<SoftwareListVo> reviewSoftwareList = softwareService.getReviewSoftwareList(selectSoftwareVo, cookieUuid);
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'flag_store')")
    public JsonResponse<IPage<AuditRecordsVo>> getAuditRecordsList(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("nodeName") String nodeName,
            @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
            @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空")
            @Range(min = 0, max = 100, message = "每页展示条数超出范围") Integer pageSize, HttpServletRequest request) {
        IPage<AuditRecordsVo> page = new Page<>(curPage, pageSize);
        return JsonResponse.success(softwareService.getAuditRecordsListPage(softwareId, nodeName, page, request));
    }

    /**
     * 证书信息确认查询
     *
     * @param softwareId softwareId
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/software/certificateInfo")
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'flag_store')")
    public JsonResponse<CertificateInfoVo> certificateInfo(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId, HttpServletRequest request) {
        return JsonResponse.success(softwareService.certificateInfo(softwareId, request));
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<String> upload(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileTypeCode") @NotNull(message = "文件类型编码不能为空") Integer fileTypeCode,
            @RequestParam("fileType") @NotNull(message = "文件具体类型不能为空") String fileType,
            HttpServletRequest request) throws TestReportExceedMaxAmountException, InputException {
        return softwareService.upload(file, softwareId, fileTypeCode, fileType, request);
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
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'flag_store')")
    public JsonResponse<List<AttachmentsVo>> getAttachmentsNames(
            @RequestParam("softwareId") @NotNull(message = "认证id不能为空") Integer softwareId,
            @RequestParam("fileType") @NotNull(message = "文件具体类型不能为空") String fileType,
            HttpServletRequest request) {
        return JsonResponse.success(softwareService.getAttachmentsNames(softwareId, fileType, request));
    }

    /**
     * 附件下载
     *
     * @param fileId fileId
     * @param response response
     * @param request request
     */
    @GetMapping("/software/downloadAttachments")
    @PreAuthorize("hasAnyRole('user', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public void downloadAttachments(
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId, HttpServletResponse response,
            HttpServletRequest request) throws InputException, UnsupportedEncodingException {
        softwareService.downloadAttachments(fileId, response, request);
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
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId, HttpServletRequest request) {
        softwareService.deleteAttachments(fileId, request);
        return JsonResponse.success();
    }

    /**
     * 证书预览
     *
     * @param softwareId softwareId
     * @param response response
     */
    @GetMapping("/software/previewCertificate")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public void previewCertificateConfirmInfo(@Valid @RequestBody CertificateConfirmVo certificateConfirmVo,
                                       HttpServletResponse response) throws InputException, IOException {
        softwareService.previewCertificateConfirmInfo(certificateConfirmVo, response);
    }

    /**
     * 预览签名
     *
     * @param fileId fileId
     * @param response response
     */
    @GetMapping("/software/imagePreview")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public void previewImage(
            @RequestParam("fileId") @NotBlank(message = "附件id不能为空") String fileId,
            HttpServletResponse response) throws InputException {
        softwareService.previewImage(fileId, response);
    }
}
