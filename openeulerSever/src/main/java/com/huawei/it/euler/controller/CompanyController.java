/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.model.vo.CompanySearchVo;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.UserCompanyVo;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * CompanyController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
public class CompanyController {
    private static final String COMPANY_NOT_EXIST = "企业实名信息不存在";

    @Autowired
    private CompanyService companyService;

    /**
     * 提交企业实名认证信息
     *
     * @param companyVo companyVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/companies")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<String> registerCompany(@Validated({CompanyVo.companyAuthentication.class})
        @RequestBody CompanyVo companyVo, HttpServletRequest request) throws InputException {
        String cookieUuid = UserUtils.getCookieUuid(request);
        return companyService.registerCompany(companyVo, cookieUuid);
    }

    /**
     * 查询当前用户认证的企业信息
     *
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/companies/company/currentUser")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<CompanyVo> findCompanyByCurrentUser(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        return JsonResponse.success(companyService.findCompanyByCurrentUser(cookieUuid));
    }

    /**
     * 查询当前用户认证的企业信息
     *
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/companies/company/getCurrentUserCompanyName")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<String> findCompanyNameByCurrentUser(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String companyName = companyService.findCompanyNameByCurrentUser(cookieUuid);
        if (StringUtils.isEmpty(companyName)) {
            return JsonResponse.failed(COMPANY_NOT_EXIST);
        }
        return JsonResponse.success(companyName);
    }

    /**
     * 分页查询企业实名认证申请列表
     *
     * @param companySearchVo companySearchVo
     * @return JsonResponse
     */
    @PostMapping("/companies/findByNameAndStatus")
    @PreAuthorize("hasRole('china_region')")
    public JsonResponse<IPage<UserCompanyVo>> findCompaniesByCompanyNameAndStatus(
            @Valid @RequestBody CompanySearchVo companySearchVo) {
        Page<UserCompanyVo> page = new Page<>(companySearchVo.getCurPage(), companySearchVo.getPageSize());
        String companyName = companySearchVo.getCompanyName();
        List<String> status = companySearchVo.getStatus();
        return JsonResponse.success(companyService.findCompaniesByCompanyNameAndStatus(companyName, status, page));
    }

    /**
     * 根据用户信息查询企业信息
     *
     * @param userUuid userUuid
     * @return JsonResponse
     */
    @GetMapping("/companies/company")
    @PreAuthorize("hasAnyRole('china_region', 'admin')")
    public JsonResponse<CompanyVo> findCompanyByUserUuid(@RequestParam("userUuid")
        @NotBlank(message = "用户uuid不能为空") String userUuid) {
        return JsonResponse.success(companyService.findCompanyByUserUuid(userUuid));
    }

    /**
     * 审批企业实名认证申请
     *
     * @param companyAuditVo companyAuditVo
     * @return JsonResponse
     */
    @PostMapping("/companies/company/audit")
    @PreAuthorize("hasRole('china_region')")
    public JsonResponse<String> approveCompany(@Valid @RequestBody CompanyAuditVo companyAuditVo) {
        return companyService.approveCompany(companyAuditVo);
    }

    /**
     * 上传logo
     *
     * @param file file
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/companies/uploadLogo")
    @PreAuthorize("hasRole('user')")
    public JsonResponse<FileModel> uploadLogo(@RequestParam("file")
        @NotNull(message = "模板文件不能为空") MultipartFile file, HttpServletRequest request) throws InputException {
        return companyService.uploadLogo(file, request);
    }

    /**
     * 上传营业执照
     *
     * @param file file
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/companies/uploadLicense")
    @PreAuthorize("hasRole('user')")
    public JsonResponse uploadLicense(@RequestParam("file") @NotNull(message = "模板文件不能为空")
        MultipartFile file, HttpServletRequest request) throws InputException, IOException {
        return companyService.uploadLicense(file, request);
    }

    /**
     * 预览logo和营业执照
     *
     * @param fileId fileId
     * @param response response
     * @param request request
     */
    @GetMapping("/companies/imagePreview")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'OSV_user')")
    public void previewImage(@RequestParam("fileId") @NotBlank(message = "附件id不能为空")
        @Length(max = 50, message = "附件id超出范围") String fileId, HttpServletResponse response,
        HttpServletRequest request) throws InputException {
        companyService.preview(fileId, request, response);
    }

    /**
     * 下载logo和营业执照
     *
     * @param fileId fileId
     * @param response response
     * @param request request
     */
    @GetMapping("/companies/download")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public void downloadLogo(@RequestParam("fileId") @NotBlank(message = "附件id不能为空")
        @Length(max = 50, message = "附件id超出范围") String fileId, HttpServletResponse response,
        HttpServletRequest request) throws InputException, UnsupportedEncodingException {
        companyService.download(fileId, request, response);
    }
}
