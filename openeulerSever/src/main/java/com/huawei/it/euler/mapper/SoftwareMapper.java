/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.model.entity.*;
import com.huawei.it.euler.model.query.AttachmentQuery;
import com.huawei.it.euler.model.vo.*;

/**
 * SoftwareMapper
 *
 * @since 2024/07/01
 */
@Repository
public interface SoftwareMapper {
    /**
     * 根据id查询
     *
     * @param id id
     * @return Software
     */
    Software findById(@Param("id") Integer id);

    /**
     * 更新软件信息
     *
     * @param software 软件信息
     */
    void updateSoftwareById(Software software);

    /**
     * 更新证书信息
     *
     * @param certificateInfoVo certificateInfoVo
     */
    void updateCertificationInfoById(CertificateInfoVo certificateInfoVo);

    /**
     * 新增软件信息
     *
     * @param software 软件信息
     * @return integer
     */
    Integer insertSoftware(Software software);

    /**
     * 更新状态
     *
     * @param software 状态
     */
    void updateSoftware(Software software);

    /**
     * 查询兼容性认证申请列表
     *
     * @param softwareQuery 筛选类型
     * @return 列表
     */
    List<SoftwareVo> getSoftwareList(@Param("offset") int offset, @Param("pageSize") int pageSize,
                                         @Param("software") SoftwareQuery softwareQuery);

    List<String> getSoftwareListOfProductType(@Param("software") SoftwareQuery softwareQuery);

    List<String> getSoftwareListOfTestOrganization(@Param("software") SoftwareQuery softwareQuery);

    List<String> getSoftwareListOfStatus(@Param("software") SoftwareQuery softwareQuery);

    Long countSoftwareList(@Param("software") SoftwareQuery softwareQuery);
    /**
     * 华为侧查询兼容性认证申请列表
     *
     * @param softwareQuery 筛选类型
     * @return 列表
     */
    List<SoftwareVo> getReviewSoftwareList(@Param("offset") int offset, @Param("pageSize") int pageSize,
        @Param("software") SoftwareQuery softwareQuery);

    List<String> getReviewSoftwareListOfProductType(@Param("software") SoftwareQuery softwareQuery);

    List<String> getReviewSoftwareListOfTestOrganization(@Param("software") SoftwareQuery softwareQuery);

    List<String> getReviewSoftwareListOfStatus(@Param("software") SoftwareQuery softwareQuery);

    Long countReviewSoftwareList(@Param("software") SoftwareQuery softwareQuery);

    List<SoftwareVo> getExportSoftwareList(@Param("software") SoftwareQuery softwareQuery);

    /**
     * 审核节点记录
     *
     * @param softwareId id
     * @return 审核列表
     */
    List<AuditRecordsVo> getAuditRecordsList(Integer softwareId);

    /**
     * 认证审核记录
     *
     * @param softwareId
     * @param nodeName
     * @param page
     * @return
     */
    IPage<AuditRecordsVo> getAuditRecordsListPage(@Param("softwareId") Integer softwareId,
        @Param("nodeName") String nodeName, IPage<AuditRecordsVo> page);

    /**
     * 证书信息确认查询
     *
     * @param softwareId 软件信息id
     * @return 证书信息
     */
    CertificateInfoVo certificateInfo(Integer softwareId);

    /**
     * 上传文件入库
     *
     * @param fileModel 附件信息
     */
    void insertAttachment(FileModel fileModel);

    /**
     * 更新签名
     *
     * @param fileModel 附件信息
     */
    void updateSign(FileModel fileModel);

    /**
     * 查询上传文件名称
     *
     * @param param 参数
     * @return 名称列表
     */
    List<AttachmentsVo> getAttachmentsNames(AttachmentQuery param);

    /**
     * 下载附件
     *
     * @param fileId 文件id
     * @return Attachments
     */
    Attachments downloadAttachments(String fileId);

    /**
     * 删除附件
     *
     * @param fileId 文件id
     * @return Integer
     */
    Integer deleteAttachments(String fileId);

    /**
     * 重新提交软件认证申请
     *
     * @param software 软件信息
     */
    void recommit(Software software);

    /**
     * 生成证书
     *
     * @param softwareId 软件id
     * @return GenerateCertificate
     */
    GenerateCertificate generateCertificate(Integer softwareId);

    /**
     * 获取签名路径
     *
     * @param softwareId 软件id
     * @return 路径
     */
    String getSignedFileId(Integer softwareId);

    List<Attachments> getCertificationIds(@Param("softwareIds") List<Integer> softwareIds);
    /**
     * 查询社区软件清单
     * 
     * @param vo 筛选条件
     * @return 社区软件清单集合
     */
    List<Compatibility> findCommunityCheckList(SoftwareFilterVo vo);

    /**
     * 查询osName
     *
     * @return 操作系统
     */
    List<String> findOsName();

    /**
     * 查询测试机构
     *
     * @return 测试机构
     */
    List<String> findTestOrganization();

    /**
     * 删除流程
     *
     * @return softwareId
     */
    Integer deleteSoftware(Integer id);
}
