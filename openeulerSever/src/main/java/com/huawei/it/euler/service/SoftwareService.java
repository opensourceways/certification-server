/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.*;

import jakarta.servlet.http.HttpServletResponse;

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
     * @param uuid uuid
     * @return 软件信息
     */
    Software findById(Integer id, String uuid);

    /**
     * 根据id查询软件认证信息
     *
     * @param id 软件id
     * @return 软件信息
     */
    Software findById(Integer id);
    /**
     * 证书初审
     *
     * @param software 软件信息
     * @param uuid uuid
     * @return 流程id
     */
    String reviewCertificate(Software software, String uuid) throws IOException;

    /**
     * 新增软件信息
     *
     * @param software 软件信息
     * @param uuid uuid
     */
    Integer createSoftware(Software software, String uuid) throws InputException, IOException;

    /**
     * 通用的审批流程
     *
     * @param vo         节点审批信息
     * @param Uuid       用户id
     * @param nodeStatus 节点状态
     * @return JsonResponse
     */
    String commonProcess(ProcessVo vo, String Uuid, Integer nodeStatus);

    /**
     * 撤销软件信息
     *
     * @param vo 审批信息
     * @param uuid uuid
     * @return 流程id
     */
    String withdrawSoftware(ProcessVo vo, String uuid);

    /**
     * 获取转审人员列表
     *
     * @param softwareId 软件id
     * @param uuid uuid
     * @return 转审人员列表
     */
    List<SimpleUserVo> transferredUserList(Integer softwareId, String uuid);

    /**
     * 查询兼容性认证申请列表
     *
     * @param softwareQueryRequest 筛选条件
     * @param uuid uuid
     * @return 列表
     */
    PageResult<SoftwareListVo> getSoftwareList(SoftwareQueryRequest softwareQueryRequest, String uuid);

    /**
     * 华为侧查询兼容性认证申请列表
     *
     * @param softwareQueryRequest 筛选条件
     * @param uuid uuid
     * @return 列表
     */
    PageResult<SoftwareListVo> getReviewSoftwareList(SoftwareQueryRequest softwareQueryRequest, String uuid);

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
     * @param uuid uuid
     * @return 列表
     */
    IPage<AuditRecordsVo> getAuditRecordsListPage(Integer softwareId, String nodeName, IPage<AuditRecordsVo> page,
        String uuid);

    /**
     * 证书信息确认查询
     *
     * @param softwareId 软件id
     * @param uuid uuid
     * @return 证书信息
     */
    CertificateInfoVo certificateInfo(Integer softwareId, String uuid);

    /**
     * 查询审批流节点信息
     *
     * @param softwareId 软件id
     * @param uuid uuid
     * @return 列表
     */
    List<AuditRecordsVo> getNodeList(Integer softwareId, String uuid);

    /**
     * 删除审批信息
     *
     * @param id 流程id
     * @param uuid uuid
     * @return 删除的流程id
     */
    String deleteSoftware(Integer id, String uuid);

    /**
     * 上传文件
     *
     * @param file 文件
     * @param softwareId 软件id
     * @param fileTypeCode 文件类型编码
     * @param fileType 文件类型
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<String> upload(MultipartFile file, Integer softwareId, Integer fileTypeCode, String fileType,
        String uuid) throws InputException, TestReportExceedMaxAmountException;

    /**
     * 查询上传文件名称
     *
     * @param softwareId 软件id
     * @param fileType 文件类型
     * @param uuid uuid
     * @return 文件信息
     */
    List<AttachmentsVo> getAttachmentsNames(Integer softwareId, String fileType, String uuid);

    /**
     * 文件下载
     *
     * @param fileId 文件id
     * @param uuid uuid
     * @param response response
     */
    void downloadAttachments(String fileId, HttpServletResponse response, String uuid)
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
     * @param uuid uuid
     */
    void deleteAttachments(String fileId, String uuid);

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
    void previewCertificateConfirmInfo(CertificateConfirmVo certificateConfirmVo, HttpServletResponse response)
        throws InputException, IOException;

    /**
     * 查询社区软件清单
     * 
     * @param vo 筛选条件
     * @return 社区软件清单集合
     */
    PageVo<CompatibilityVo> findCommunityCheckList(SoftwareFilterVo vo);

    FilterCriteriaVo filterCeriteria();
}
