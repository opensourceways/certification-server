/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.huawei.it.euler.ddd.service.company.cqe.CompanyApproveResultEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.CompanyMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.constant.StringConstant;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.Company;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.LicenseInfoVo;
import com.huawei.it.euler.model.vo.UserCompanyVo;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.third.CompanyVerifyClient;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.FileUtils;
import com.huawei.it.euler.util.StringPropertyUtils;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.ocr.v1.OcrClient;
import com.huaweicloud.sdk.ocr.v1.model.BusinessLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.region.OcrRegion;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * CompanyServiceImpl
 *
 * @since 2024/07/03
 */
@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareServiceImpl.class);

    private static final String COMPANY_INFO_DUPLICATE_REGISTER = "当前用户已认证过企业";

    private static final String COMPANY_INFO_UNDER_REVIEW = "当前用户提交的企业信息正在审核中";

    private static final String COMPANY_NOT_EXIST = "企业实名信息不存在";

    private static final String COMPANY_NOT_SUPPORT_APPROVAL = "当前企业已通过审核";

    private static final String COMPANY_VERIFY_FAILED = "企业工商注册信息系统校验不通过";

    private static final String FILE_TYPE_LOGO = "logo";

    private static final String FILE_TYPE_LICENSE = "license";

    private static final String SIGNATURE_ALGORITHM_SDK_HMAC_SHA256 = "SDK-HMAC-SHA256";

    private static final Integer COMPANY_INIT_NUM = 10000;

    @Value("${ocr.ak}")
    private String ocrAK;

    @Value("${ocr.sk}")
    private String ocrSK;

    @Value("${ocr.projectId}")
    private String ocrProjectId;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private SoftwareServiceImpl softwareService;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private CompanyVerifyClient companyVerifyClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public JsonResponse<String> registerCompany(CompanyVo companyVo, String uuid) {
        Company company = new Company();
        BeanUtils.copyProperties(companyVo, company);
        Company companyByCreditCode = companyMapper.findCompanyByCreditCode(companyVo.getCreditCode());
        if (companyByCreditCode != null) {
            company.setCompanyCode(companyByCreditCode.getCompanyCode());
        } else {
            company.setCompanyCode(COMPANY_INIT_NUM + companyMapper.countCompany());
        }
        Date currentTime = new Date();
        company.setUpdateTime(currentTime);
        company.setApplyTime(currentTime);
        company.setApprovalComment(StringUtils.EMPTY);
        company.setUserUuid(uuid);
        if (!companyVerifyClient.checkCompanyInfo(company.getCompanyName(), company.getCreditCode(),
            company.getLegalPerson())) {
            LOGGER.error("企业工商注册校验不通过:{}", company.getCompanyName());
            throw new ParamException(COMPANY_VERIFY_FAILED);
        }
        company.setIsCheckedPass(true);
        company.setStatus(0);
        Company existCompany = companyMapper.findCompanyByUserUuid(uuid);
        company.setCompanyMail(encryptUtils.aesEncrypt(company.getCompanyMail()));
        if (existCompany != null) {
            Integer status = existCompany.getStatus();
            if (status == 0) {
                return JsonResponse.failed(COMPANY_INFO_UNDER_REVIEW);
            } else if (status == 1) {
                return JsonResponse.failed(COMPANY_INFO_DUPLICATE_REGISTER);
            } else {
                companyMapper.updateCompany(company);
            }
        } else {
            companyMapper.insertCompany(company);
        }
        return JsonResponse.success();
    }

    @Override
    public CompanyVo findCompanyByCurrentUser(String uuid) {
        Company company = companyMapper.findCompanyByUserUuid(uuid);
        CompanyVo companyVo = new CompanyVo();
        if (company != null) {
            String companyMail =
                StringPropertyUtils.reduceEmailSensitivity(encryptUtils.aesDecrypt(company.getCompanyMail()));
            company.setCompanyMail(companyMail);
            BeanUtils.copyProperties(company, companyVo);
        }
        return companyVo;
    }

    @Override
    public String findCompanyNameByCurrentUser(String uuid) {
        return companyMapper.findCompanyNameByUserUuid(uuid);
    }

    @Override
    public IPage<UserCompanyVo> findCompaniesByCompanyNameAndStatus(String companyName, List<String> status,
        Page<UserCompanyVo> page) {
        List<Integer> statusInt;
        if (status == null || status.isEmpty()) {
            statusInt = null;
        } else {
            statusInt = status.stream().map(item -> {
                if ("审核中".equals(item)) {
                    return 0;
                } else if ("已通过".equals(item)) {
                    return 1;
                } else {
                    return 2;
                }
            }).collect(Collectors.toList());
        }
        if (StringUtils.isEmpty(companyName)) {
            companyName = null;
        }
        IPage<UserCompanyVo> companiesByCompanyNameAndStatus =
            companyMapper.findCompaniesByCompanyNameAndStatus(companyName, statusInt, page);
        for (UserCompanyVo companyVo : companiesByCompanyNameAndStatus.getRecords()) {
            String telephone = reduceSensitivity(companyVo.getTelephone(), StringConstant.TLE_PHONE);
            companyVo.setTelephone(telephone);
            String companyMail = reduceSensitivity(companyVo.getCompanyMail(), StringConstant.MAIL);
            companyVo.setCompanyMail(companyMail);
            String mail = reduceSensitivity(companyVo.getMail(), StringConstant.MAIL);
            companyVo.setMail(mail);
        }
        return companiesByCompanyNameAndStatus;
    }

    @Override
    public CompanyVo findCompanyByUserUuid(String uuid) {
        Company company = companyMapper.findCompanyByUserUuid(uuid);
        if (ObjectUtil.isEmpty(company)) {
            return null;
        }
        CompanyVo companyVo = new CompanyVo();
        String companyMail = reduceSensitivity(company.getCompanyMail(), StringConstant.MAIL);
        company.setCompanyMail(companyMail);
        BeanUtils.copyProperties(company, companyVo);
        return companyVo;
    }

    @Override
    @Transactional
    public JsonResponse<String> approveCompany(CompanyAuditVo companyAuditVo) throws Exception {
        if (companyAuditVo == null || companyAuditVo.getUserUuid() == null) {
            return JsonResponse.failed("请求参数不完整");
        }
        String userUuid = companyAuditVo.getUserUuid();
        Company dbCompany = companyMapper.findCompanyByUserUuid(userUuid);
        if (dbCompany == null) {
            return JsonResponse.failed(COMPANY_NOT_EXIST);
        }
        if (dbCompany.getStatus() != 0) {
            return JsonResponse.failed(COMPANY_NOT_SUPPORT_APPROVAL);
        }
        Integer newStatus = companyAuditVo.getResult() ? 1 : 2;
        dbCompany.setStatus(newStatus);
        dbCompany.setApprovalComment(companyAuditVo.getComment());
        companyMapper.updateCompany(dbCompany);
        // 审核结果发送给申请用户
        CompanyApproveResultEvent event = new CompanyApproveResultEvent(this, companyAuditVo);
        eventPublisher.publishEvent(event);
        return JsonResponse.success();
    }

    @Override
    public JsonResponse<FileModel> uploadLogo(MultipartFile file, String uuid) throws InputException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 2, "logo", uuid);
        softwareMapper.insertAttachment(fileModel);
        fileModel.setFilePath(null);
        return JsonResponse.success(fileModel);
    }

    @Override
    public JsonResponse<Map<String, Object>> uploadLicense(MultipartFile file, String uuid)
        throws InputException, IOException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 2, "license", uuid);
        softwareMapper.insertAttachment(fileModel);
        LicenseInfoVo licenseInfoVo = getLicenseInfo(getLicenseTextInfo(file));
        fileModel.setFilePath(null);
        Map<String, Object> map = new HashMap<>();
        map.put("fileInfo", fileModel);
        map.put("licenseInfo", licenseInfoVo);
        return JsonResponse.success(map);
    }

    private RecognizeBusinessLicenseResponse getLicenseTextInfo(MultipartFile file) throws IOException {
        ICredential auth = new BasicCredentials().withProjectId(ocrProjectId).withAk(ocrAK).withSk(ocrSK);
        OcrClient ocrClient =
            OcrClient.newBuilder().withCredential(auth).withRegion(OcrRegion.valueOf("cn-east-3")).build();
        RecognizeBusinessLicenseRequest request = new RecognizeBusinessLicenseRequest();
        BusinessLicenseRequestBody body = new BusinessLicenseRequestBody();
        String imageToBase64 = imageToBase64(file);
        body.withImage(imageToBase64);
        request.withBody(body);
        RecognizeBusinessLicenseResponse response = null;
        try {
            response = ocrClient.recognizeBusinessLicense(request);
        } catch (ConnectionException | RequestTimeoutException e) {
            log.error(e.toString());
        } catch (ServiceResponseException e) {
            log.error(String.valueOf(e.getHttpStatusCode()));
            log.error(e.getErrorCode());
            log.error(e.getErrorMsg());
        }
        return response;
    }

    private LicenseInfoVo getLicenseInfo(RecognizeBusinessLicenseResponse licenseTextInfo) {
        if (licenseTextInfo == null) {
            return null;
        }
        LicenseInfoVo licenseInfoVo = new LicenseInfoVo();
        setLicenseInfoVo(licenseInfoVo, licenseTextInfo);
        return licenseInfoVo;
    }

    private LicenseInfoVo getEmptyLicenseInfoVo() {
        LicenseInfoVo licenseInfoVo = new LicenseInfoVo();
        licenseInfoVo.setLegalPerson(StringUtils.EMPTY);
        licenseInfoVo.setCompanyName(StringUtils.EMPTY);
        licenseInfoVo.setAddress(StringUtils.EMPTY);
        licenseInfoVo.setCreditCode(StringUtils.EMPTY);
        licenseInfoVo.setExpirationDate(StringUtils.EMPTY);
        licenseInfoVo.setRegistrationDate(StringUtils.EMPTY);
        licenseInfoVo.setRegistrationCapital(StringUtils.EMPTY);
        return licenseInfoVo;
    }

    private void setLicenseInfoVo(LicenseInfoVo licenseInfoVo, RecognizeBusinessLicenseResponse licenseTextInfo) {
        licenseInfoVo.setAddress(licenseTextInfo.getResult().getAddress());
        licenseInfoVo.setCompanyName(licenseTextInfo.getResult().getName());
        licenseInfoVo.setCreditCode(licenseTextInfo.getResult().getRegistrationNumber());
        licenseInfoVo.setLegalPerson(licenseTextInfo.getResult().getLegalRepresentative());
        licenseInfoVo.setRegistrationCapital(licenseTextInfo.getResult().getRegisteredCapital());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String businessTerm = licenseTextInfo.getResult().getBusinessTerm();

        String[] licenseTermParts = businessTerm.split("至");
        if (licenseTermParts.length > 1) {
            String endDateString = licenseTermParts[1].trim();
            try {
                LocalDate endDate = LocalDate.parse(endDateString, formatter);
                licenseInfoVo.setExpirationDate(endDate.format(outputFormatter));
            } catch (DateTimeParseException e) {
                // 处理解析结束日期失败的情况
                log.error("Failed to parse end date: {}", endDateString);
            }
        }

        String foundDate = licenseTextInfo.getResult().getFoundDate();
        try {
            LocalDate startDate = LocalDate.parse(foundDate, formatter);
            licenseInfoVo.setRegistrationDate(startDate.format(outputFormatter));
        } catch (DateTimeParseException e) {
            // 处理解析开始日期失败的情况
            log.error("Failed to parse start date: {}", foundDate);
        }

    }

    private String imageToBase64(MultipartFile file) throws IOException {
        byte[] data;
        try (InputStream in = file.getInputStream()) {
            data = new byte[in.available()];
            int read = in.read(data);
        } catch (IOException e) {
            throw new IOException("图片转换报错");
        }
        return Base64.getEncoder().encodeToString(data);
    }

    @Override
    public void preview(String fileId, String uuid, HttpServletResponse response) throws InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        if (attachments == null) {
            throw new ParamException("无权限预览当前文件");
        }
        String fileType = attachments.getFileType();
        if (!Objects.equals(fileType, FILE_TYPE_LOGO) && !Objects.equals(fileType, FILE_TYPE_LICENSE)) {
            throw new ParamException("无权限预览当前文件");
        }
        if (!userService.isAttachmentPermission(uuid, attachments)) {
            throw new ParamException("无权限预览当前文件");
        }
        softwareService.previewImage(fileId, response);
    }

    @Override
    public void download(String fileId, String uuid, HttpServletResponse response)
        throws UnsupportedEncodingException, InputException {
        softwareService.downloadAttachments(fileId, response, uuid);
    }

    public String reduceSensitivity(String str, String strType) {
        str = encryptUtils.isEncrypted(str) ? encryptUtils.aesDecrypt(str) : str;
        if (Objects.equals(strType, StringConstant.TLE_PHONE)) {
            str = StringPropertyUtils.reducePhoneSensitivity(str);
        } else if (Objects.equals(strType, StringConstant.MAIL)) {
            str = StringPropertyUtils.reduceEmailSensitivity(str);
        }
        return str;
    }
}
