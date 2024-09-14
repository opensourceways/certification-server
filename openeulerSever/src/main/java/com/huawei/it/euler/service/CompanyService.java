/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.UserCompanyVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * CompanyService
 *
 * @since 2024/07/03
 */
public interface CompanyService {
    /**
     * 企业注册
     *
     * @param companyVo 企业信息
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<String> registerCompany(CompanyVo companyVo, String uuid) throws InputException;

    /**
     * 查询当前登录用户企业实名认证申请
     *
     * @param uuid uuid
     * @return 企业信息
     */
    CompanyVo findCompanyByCurrentUser(String uuid);

    /**
     * 查询当前登录用户认证的企业名称
     *
     * @param uuid uuid
     * @return 企业名称
     */
    String findCompanyNameByCurrentUser(String uuid);

    /**
     * 查询企业实名认证信息列表
     *
     * @param companyName 企业名
     * @param status 状态
     * @param page 页面
     * @return 企业实名信息列表
     */
    IPage<UserCompanyVo> findCompaniesByCompanyNameAndStatus(String companyName, List<String> status,
                                                             Page<UserCompanyVo> page);

    /**
     * 根据uuid查询企业详细信息
     *
     * @param uuid uuid
     * @return 企业信息
     */
    CompanyVo findCompanyByUserUuid(String uuid);

    /**
     * 审批企业认证申请
     *
     * @param companyAuditVo 审批结果
     * @return JsonResponse
     */
    JsonResponse<String> approveCompany(CompanyAuditVo companyAuditVo);

    /**
     * logo上传
     *
     * @param file 文件
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<FileModel> uploadLogo(MultipartFile file, String uuid) throws InputException;

    /**
     * 营业执照上传
     *
     * @param file 文件
     * @param uuid uuid
     * @return JsonResponse
     */
    JsonResponse<Map<String, Object>> uploadLicense(MultipartFile file, String uuid) throws InputException, IOException;

    /**
     * 预览logo和营业执照
     *
     * @param fileId 文件id
     * @param uuid uuid
     * @param response response
     */
    void preview(String fileId, String uuid, HttpServletResponse response) throws InputException;

    /**
     * 下载logo和营业执照
     *
     * @param fileId 文件id
     * @param uuid uuid
     * @param response response
     */
    void download(String fileId, String uuid, HttpServletResponse response) throws UnsupportedEncodingException, InputException;
}
