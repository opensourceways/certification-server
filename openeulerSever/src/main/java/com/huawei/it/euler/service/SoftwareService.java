/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 软件认证
 *
 * @since 2024/06/29
 */
public interface SoftwareService {
    /**
     * 根据id查询软件认证信息
     *
     * @param id 软件id
     * @param cookieUuid uuid
     * @return 软件信息
     */
    Software findById(Integer id, String cookieUuid);

    /**
     * 更新软件信息
     *
     * @param software 软件信息
     * @param cookieUuid uuid
     * @param request request
     * @return
     */
    JsonResponse<String> updateSoftware(SoftwareVo software, String cookieUuid, HttpServletRequest request) throws IOException;

    /**
     * 新增软件信息
     *
     * @param software 软件信息
     * @param cookieUuid uuid
     * @param request request
     */
    void insertSoftware(Software software, String cookieUuid, HttpServletRequest request) throws InputException, IOException;

    /**
     * 软件认证审核流程
     *
     * @param vo 审核参数
     * @param cookieUuid uuid
     * @param request request
     * @return JsonResponse
     */
    JsonResponse<String> processReview(ProcessVo vo, String cookieUuid, HttpServletRequest request) throws IOException;

    /**
     * 获取转审人员列表
     *
     * @param softwareId 软件id
     * @param cookieUuid uuid
     * @return 转审人员列表
     */
    List<SimpleUserVo> transferredUserList(Integer softwareId, String cookieUuid);

    /**
     * 查询兼容性认证申请列表
     *
     * @param selectSoftwareVo 筛选条件
     * @param cookieUuid uuid
     * @return 列表
     */
    List<SoftwareListVo> getSoftwareList(SelectSoftwareVo selectSoftwareVo, String cookieUuid);

    /**
     * 华为侧查询兼容性认证申请列表
     *
     * @param selectSoftwareVo 筛选条件
     * @param cookieUuid uuid
     * @return 列表
     */
    List<SoftwareListVo> getReviewSoftwareList(SelectSoftwareVo selectSoftwareVo, String cookieUuid);

    /**
     * 认证审核记录
     *
     * @param softwareId 软件id
     * @return 列表
     */
    List<AuditRecordsVo> getAuditRecordsList(Integer softwareId);

    /**
     * 认证审核记录
     *
     * @param softwareId 软件id
     * @param nodeName 审核节点名称
     * @param page 分页信息
     * @param request request
     * @return 列表
     */
    IPage<AuditRecordsVo> getAuditRecordsListPage(Integer softwareId, String nodeName,
                                                  IPage<AuditRecordsVo> page, HttpServletRequest request);

    /**
     * 证书信息确认查询
     *
     * @param softwareId 软件id
     * @param request request
     * @return 证书信息
     */
    CertificateInfoVo certificateInfo(Integer softwareId, HttpServletRequest request);

    /**
     * 查询审批流节点信息
     *
     * @param softwareId 软件id
     * @return 列表
     */
    List<AuditRecordsVo> getNodeList(Integer softwareId);

    /**
     * 上传文件
     *
     * @param file 文件
     * @param softwareId 软件id
     * @param fileTypeCode 文件类型编码
     * @param fileType 文件类型
     * @param request request
     * @return JsonResponse
     */
    JsonResponse<String> upload(MultipartFile file, Integer softwareId, Integer fileTypeCode,
                                String fileType, HttpServletRequest request) throws InputException, TestReportExceedMaxAmountException;

    /**
     * 查询上传文件名称
     *
     * @param softwareId 软件id
     * @param fileType 文件类型
     * @param request request
     * @return 文件信息
     */
    List<AttachmentsVo> getAttachmentsNames(Integer softwareId, String fileType, HttpServletRequest request);

    /**
     * 文件下载
     *
     * @param fileId 文件id
     * @param request request
     * @param response response
     */
    void downloadAttachments(String fileId, HttpServletResponse response, HttpServletRequest request)
            throws UnsupportedEncodingException, InputException;

    /**
     * 文件预览
     *
     * @param fileId 文件id
     * @param response response
     */
    void previewImage(String fileId, HttpServletResponse response) throws InputException;

    /**
     * 附件删除
     *
     * @param fileId 文件id
     * @param request request
     */
    void deleteAttachments(String fileId, HttpServletRequest request);

    /**
     * 证书生成
     *
     * @param softwareId 软件id
     */
    void generateCertificate(Integer softwareId) throws IOException;

    /**
     * 证书预览
     *
     * @param softwareId 软件id
     * @param response response
     */
    void previewCertificate(Integer softwareId, HttpServletResponse response) throws InputException, IOException;

    /**
     * 证书确认预览
     *
     * @param certificateConfirmVo certificateConfirmVo
     * @param response response
     */
    void previewCertificateConfirmInfo(CertificateConfirmVo certificateConfirmVo, HttpServletResponse response) throws InputException, IOException;
}
