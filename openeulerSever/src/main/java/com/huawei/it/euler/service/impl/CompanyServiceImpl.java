/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.CompanyMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.mapper.UserMapper;
import com.huawei.it.euler.model.constant.StringConstant;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.Company;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.LicenseInfoVo;
import com.huawei.it.euler.model.vo.UserCompanyVo;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.third.JwtTokenClient;
import com.huawei.it.euler.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.huawei.it.euler.service.impl.SoftwareServiceImpl.PARTNER_ROLE;

/**
 * CompanyServiceImpl
 *
 * @since 2024/07/03
 */
@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {
    private static final String COMPANY_INFO_DUPLICATE_REGISTER = "当前用户已认证过企业";

    private static final String COMPANY_INFO_UNDER_REVIEW = "当前用户提交的企业信息正在审核中";

    private static final String COMPANY_NOT_EXIST = "企业实名信息不存在";

    private static final String COMPANY_NOT_SUPPORT_APPROVAL = "当前企业已通过审核";

    private static final String COMPANY_VERIFY_FAILED = "企业工商注册信息系统校验不通过";

    private static final String FILE_TYPE_LOGO = "logo";

    private static final String FILE_TYPE_LICENSE = "license";

    private static final String X_HW_ID = "X-HW-ID";

    private static final String X_HW_APPKEY = "X-HW-APPKEY";

    @Value("${sns.appId}")
    private String hedsAppid;

    @Value("${sns.templateId}")
    private String templateId;

    @Value("${sns.messageUrl}")
    private String messageUrl;

    @Value("${sns.xHwAppKey}")
    private String xHwAppKey;

    @Value("${apigw.appId}")
    private String appId;

    @Value("${apigw.xHwAppKey}")
    private String romaXHwAppKey;

    @Value("${apigw.verifyCompanyInfoUrl}")
    private String verifyCompanyInfoUrl;

    @Value("${iam.tokenUrl}")
    private String tokenUrl;

    @Value("${iam.secret}")
    private String iamSecret;

    @Value("${iam.enterprise}")
    private String enterprise;

    @Value("${irs.ocrUrl}")
    private String ocrUrl;

    @Value("${irs.irsUrl}")
    private String irsUrl;

    @Value("${irs.projectName}")
    private String projectName;

    @Value("${irs.scene}")
    private String scene;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private SoftwareServiceImpl softwareService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private EncryptUtils encryptUtils;

    @Transactional
    @Override
    public JsonResponse<String> registerCompany(CompanyVo companyVo, String cookieUuid) throws InputException {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        EulerUser user = userMapper.findByUuid(userUuid);
        if (Objects.equals(user.getUseable(), 0)) {
            throw new InputException("您已注销账号！无法进行企业实名认证");
        }
        Company companyByCreditCode = companyMapper.findCompanyByCreditCode(companyVo.getCreditCode());
        if (companyByCreditCode != null) {
            return JsonResponse.failed("当前公司已认证");
        }
        Company company = new Company();
        BeanUtils.copyProperties(companyVo, company);
        Date currentTime = new Date();
        company.setUpdateTime(currentTime);
        company.setApplyTime(currentTime);
        company.setApprovalComment(StringUtils.EMPTY);
        company.setUserUuid(userUuid);
        if (!checkCompanyInfo(company)) {
            return JsonResponse.failed(COMPANY_VERIFY_FAILED);
        }
        company.setIsCheckedPass(true);
        company.setStatus(0);
        Company existCompany = companyMapper.findCompanyByUserUuid(userUuid);
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

    private Boolean checkCompanyInfo(Company company) {
        String companyName = company.getCompanyName();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(X_HW_ID, appId);
        httpHeaders.set(X_HW_APPKEY, romaXHwAppKey);
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerName", companyName);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(verifyCompanyInfoUrl, HttpMethod.GET, httpEntity, String.class, map);
        if (responseEntity.getStatusCodeValue() != 200) {
            return false;
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray listResult = jsonObject.getJSONArray("listResult");
        if (listResult.isEmpty()) {
            return false;
        }
        for (int i = 0; i < listResult.size(); i++) {
            JSONObject result = listResult.getJSONObject(i);
            String orgName = result.getString("orgName");
            String legalRep = result.getString("legalRep");
            String uscCode = result.getString("uscCode");
            if (Objects.equals(orgName, companyName) && Objects.equals(legalRep, company.getLegalPerson())
                    && Objects.equals(uscCode, company.getCreditCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompanyVo findCompanyByCurrentUser(String cookieUuid) {
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
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
    public String findCompanyNameByCurrentUser(String cookieUuid) {
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
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
        CompanyVo companyVo = new CompanyVo();
        if (company != null) {
            String companyMail = reduceSensitivity(company.getCompanyMail(), StringConstant.MAIL);
            company.setCompanyMail(companyMail);
            BeanUtils.copyProperties(company, companyVo);
        }
        return companyVo;
    }

    @Override
    public JsonResponse<String> approveCompany(CompanyAuditVo companyAuditVo) {
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
        sendEmailNotification(companyAuditVo);
        return JsonResponse.success();
    }

    private void sendEmailNotification(CompanyAuditVo companyAuditVo) {
        Company company = companyMapper.findCompanyByUserUuid(companyAuditVo.getUserUuid());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(X_HW_ID, hedsAppid);
        httpHeaders.set(X_HW_APPKEY, xHwAppKey);
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_id", hedsAppid);
        EulerUser user = userMapper.findByUuid(company.getUserUuid());
        map.put("mobiles", user.getTelephone());
        map.put("template_id", templateId);
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("status", companyAuditVo.getResult() ? "通过" : "驳回");
        templateParams.put("comment", companyAuditVo.getComment());
        map.put("template_params", JSONObject.toJSON(templateParams).toString());
        HttpEntity httpEntity = new HttpEntity(map, httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(messageUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        String respCode = jsonObject.getString("resp_code");
        if ("200".equals(respCode)) {
            log.info("Send message to {} success", user.getId());
        } else {
            log.info("Send message to {} failed", user.getId());
        }
    }

    @Override
    public JsonResponse<FileModel> uploadLogo(MultipartFile file, HttpServletRequest request) throws InputException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 2, "logo", request);
        softwareMapper.insertAttachment(fileModel);
        fileModel.setFilePath(null);
        return JsonResponse.success(fileModel);
    }

    @Override
    public JsonResponse<Map<String, Object>> uploadLicense(MultipartFile file, HttpServletRequest request)
            throws InputException, IOException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 2, "license", request);
        softwareMapper.insertAttachment(fileModel);
        String licenseInfo = getLicenseTextInfo(file);
        LicenseInfoVo licenseInfoVo = getLicenseInfo(licenseInfo);
        fileModel.setFilePath(null);
        Map<String, Object> map = new HashMap<>();
        map.put("fileInfo", fileModel);
        map.put("licenseInfo", licenseInfoVo);
        return JsonResponse.success(map);
    }

    private String getLicenseTextInfo(MultipartFile file) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", getHedsIamToken());
        HashMap<String, Object> params = new HashMap<>();
        params.put("scene", "NatureOCR");
        String imageToBase64 = imageToBase64(file);
        params.put("imageBase64", imageToBase64);
        HttpEntity httpEntity = new HttpEntity(params, httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(ocrUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        String status = jsonObject.getString("status");
        if (!"200".equals(status)) {
            log.error("License image OCR failed: {}",
                    CleanXSSUtils.replaceCRLF(String.valueOf(jsonObject.get("message"))));
            return StringUtils.EMPTY;
        }
        JSONArray data = jsonObject.getJSONArray("data");
        String classes = "";
        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            classes = item.getString("classes");
        }
        classes = classes.replace("[", "");
        classes = classes.replace("]", "");
        classes = classes.replace("\",\"", "");
        return classes;
    }

    private LicenseInfoVo getLicenseInfo(String licenseTextInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", getHedsIamToken());
        HashMap<String, Object> params = new HashMap<>();
        params.put("project_name", projectName);
        List<String> sceneList = new ArrayList<>();
        sceneList.add(scene);
        params.put("scene", sceneList);
        params.put("service", "text");
        params.put("tenant_name", hedsAppid);
        params.put("text", licenseTextInfo);
        HttpEntity httpEntity = new HttpEntity(params, httpHeaders);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(irsUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject bodyJson = JSONObject.parseObject(body);
        String message = bodyJson.getString("message");
        if (!"parase success".equals(message)) {
            log.error("Failed to extract information, error message: {}", CleanXSSUtils.replaceCRLF(message));
            return getEmptyLicenseInfoVo();
        }
        JSONObject resultJson = bodyJson.getJSONObject("result");
        JSONArray licenseInfoArray = resultJson.getJSONArray("LicenseIdentify");
        LicenseInfoVo licenseInfoVo = new LicenseInfoVo();
        for (int i = 0; i < licenseInfoArray.size(); i++) {
            JSONObject licenseInfo = licenseInfoArray.getJSONObject(i);
            String taskNameEn = licenseInfo.getString("task_name_en");
            JSONArray texts = licenseInfo.getJSONArray("text");
            String text = texts.isEmpty() ? StringUtils.EMPTY : texts.getString(0);
            setLicenseInfoVo(licenseInfoVo, taskNameEn, text);
        }
        return licenseInfoVo;
    }

    private String getHedsIamToken() {
        JwtTokenClient jwtTokenClient = new JwtTokenClient(tokenUrl, hedsAppid, iamSecret, hedsAppid, enterprise);
        return jwtTokenClient.getJwtToken();
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

    private void setLicenseInfoVo(LicenseInfoVo licenseInfoVo, String taskNameEn, String text) {
        if ("companyName".equals(taskNameEn)) {
            licenseInfoVo.setCompanyName(text);
        }
        if ("creditCode".equals(taskNameEn)) {
            licenseInfoVo.setCreditCode(text);
        }
        if ("address".equals(taskNameEn)) {
            licenseInfoVo.setAddress(text);
        }
        if ("legalPerson".equals(taskNameEn)) {
            licenseInfoVo.setLegalPerson(text);
        }
        if ("registrationCapital".equals(taskNameEn)) {
            licenseInfoVo.setRegistrationCapital(text);
        }
        if ("registrationDate".equals(taskNameEn)) {
            licenseInfoVo.setRegistrationDate(StringUtils.EMPTY);
        }
        if ("expirationDate".equals(taskNameEn)) {
            licenseInfoVo.setExpirationDate(StringUtils.EMPTY);
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
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    @Override
    public void preview(String fileId, HttpServletRequest request, HttpServletResponse response)
            throws InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        if (attachments == null) {
            throw new ParamException("无权限预览当前文件");
        }
        String fileType = attachments.getFileType();
        if (!Objects.equals(fileType, FILE_TYPE_LOGO) && !Objects.equals(fileType, FILE_TYPE_LICENSE)) {
            throw new ParamException("无权限预览当前文件");
        }
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        List<String> roles = softwareService.getRoles(uuid);
        if (PARTNER_ROLE.containsAll(roles)) {
            if (!Objects.equals(uuid, attachments.getUuid())) {
                throw new ParamException("无权限预览当前文件");
            }
        }
        softwareService.previewImage(fileId, response);
    }

    @Override
    public void download(String fileId, HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, InputException {
        softwareService.downloadAttachments(fileId, response, request);
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
