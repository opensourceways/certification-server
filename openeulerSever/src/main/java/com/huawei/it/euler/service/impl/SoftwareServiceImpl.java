/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.extension.EmailConfig;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.mapper.*;
import com.huawei.it.euler.model.constant.CompanyStatusConstant;
import com.huawei.it.euler.model.entity.*;
import com.huawei.it.euler.model.enumeration.CenterEnum;
import com.huawei.it.euler.model.enumeration.IntelTestEnum;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.*;
import com.huawei.it.euler.util.*;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * SoftwareServiceImpl
 *
 * @since 2024/06/29
 */
@Service
@Transactional
@Slf4j
public class SoftwareServiceImpl implements SoftwareService {
    private static Map<Integer, String> NODE_NAME_MAP = new HashMap<>();

    private static final String FILE_TYPE_SIGN = "sign";

    private static final String FILE_TYPE_TEST_REPORT = "testReport";

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ApprovalPathNodeService approvalPathNodeService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private CertificateGenerationUtils certificateGenerationUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private MasterDataMapper masterDataMapper;

    @Autowired
    private ProtocolMapper protocolMapper;

    @Autowired
    private CompatibleDataMapper compatibleDataMapper;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private ApprovalScenarioService approvalScenarioService;

    @Autowired
    private EmailConfig emailConfig;

    static {
        NODE_NAME_MAP.put(1, "测评申请");
        NODE_NAME_MAP.put(2, "方案审核");
        NODE_NAME_MAP.put(3, "测试阶段");
        NODE_NAME_MAP.put(4, "报告初审");
        NODE_NAME_MAP.put(5, "报告复审");
        NODE_NAME_MAP.put(6, "证书初审");
        NODE_NAME_MAP.put(7, "证书确认");
        NODE_NAME_MAP.put(8, "证书签发");
    }

    @Override
    public Software findById(Integer id, String cookieUuid) {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        Software software = softwareMapper.findById(id);
        if (!userService.isUserPermission(Integer.valueOf(userUuid), software)) {
            throw new ParamException("该用户无权访问当前信息");
        }
        String jsonHashRatePlatform = software.getJsonHashRatePlatform();
        JSONArray jsonArray = JSON.parseArray(jsonHashRatePlatform);
        List<ComputingPlatformVo> computingPlatformVos = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ComputingPlatformVo computingPlatformVo = JSON.toJavaObject(jsonObject, ComputingPlatformVo.class);
            computingPlatformVos.add(computingPlatformVo);
        }
        software.setHashratePlatformList(computingPlatformVos);
        List<String> platformString = computingPlatformVos.stream().map(item -> {
            String platform = String.join("、", item.getServerTypes());
            return item.getPlatformName() + "/" + item.getServerProvider() + "/" + platform;
        }).collect(Collectors.toList());
        software.setPlatforms(platformString);
        return software;
    }

    @Override
    @Transactional
    public JsonResponse<String> updateSoftware(SoftwareVo software, String cookieUuid, HttpServletRequest request)
        throws IOException {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        Software softwareDb = softwareMapper.findById(software.getId());
        if (softwareDb.getStatus() != 6) {
            return JsonResponse.failed("该测评申请无法更新软件认证信息");
        }
       if (!userService.isUserDataScopeByRole(Integer.valueOf(userUuid), softwareDb)) {
            throw new ParamException("该用户无权访问当前信息");
        }
        List<ComputingPlatformVo> hashratePlatformList = software.getHashratePlatformList();
        String jsonHashRatePlatform = JSON.toJSON(hashratePlatformList).toString();
        // 校验OS、算力平台参数
        checkCertificateInfo(software.getOsName(), software.getOsVersion(), jsonHashRatePlatform);
        checkIntelParam(softwareDb.getCpuVendor(), softwareDb.getTestOrganization(), jsonHashRatePlatform);
        software.setJsonHashRatePlatform(jsonHashRatePlatform);
        // 调用流程审批接口，如果流程错误则不更新证书信息
        ProcessVo processVo = new ProcessVo();
        processVo.setSoftwareId(software.getId());
        processVo.setHandlerResult(1);
        processVo.setTransferredComments("通过");
        JsonResponse<String> processJsonRep = processReview(processVo, cookieUuid, request);
        if (!Objects.equals(processJsonRep.getCode(), JsonResponse.SUCCESS_STATUS)) {
            return processJsonRep;
        }
        softwareMapper.updateSoftwareById(software);
        // 证书信息修改
        CertificateInfoVo certificateInfoVo = new CertificateInfoVo();
        certificateInfoVo.setSoftwareId(software.getId());
        certificateInfoVo.setCertificateType("兼容性测试");
        String certificateInterests = softwareDb.getCompanyName() + "本证明及相关测评徽标使用权";
        certificateInfoVo.setCertificateInterests(certificateInterests);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        Calendar calendar = Calendar.getInstance();
        String startTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.YEAR, 3);
        String endTime = sdf.format(calendar.getTime());
        String validityPeriod = startTime + "-" + endTime;
        certificateInfoVo.setValidityPeriod(validityPeriod);
        softwareMapper.updateCertificationInfoById(certificateInfoVo);
        return JsonResponse.success();
    }

    private void checkCertificateInfo(String osName, String osVersion, String hashratePlatform) {
        Integer osCount = masterDataMapper.findOsByNameAndVersion(osName, osVersion);
        if (osCount == 0) {
            throw new ParamException("OS参数错误");
        }
        List<ComputingPlatformVo> platforms = JSONObject.parseArray(hashratePlatform, ComputingPlatformVo.class);
        for (ComputingPlatformVo platform : platforms) {
            for (String serverType : platform.getServerTypes()) {
                Integer platformCount = masterDataMapper.findPlatformByParam(platform.getPlatformName(),
                    platform.getServerProvider(), serverType);
                if (platformCount == 0) {
                    throw new ParamException("算力平台参数错误");
                }
            }
        }
    }

    /**
     * check intel test scenario param
     *
     * @param cpuVendor 硬件厂商
     * @param innovationCenter 测评中心
     * @param hashratePlatform 算例平台
     */
    private void checkIntelParam(String cpuVendor, String innovationCenter, String hashratePlatform) {
        if (!StringUtils.isEmpty(cpuVendor) && IntelTestEnum.CPU_VENDOR.getName().equals(cpuVendor)) {
            if (!IntelTestEnum.INNVOTATION_CENTER.getName().equals(innovationCenter)) {
                throw new ParamException("测试机构参数错误");
            }
            List<ComputingPlatformVo> platforms = JSONObject.parseArray(hashratePlatform, ComputingPlatformVo.class);
            for (ComputingPlatformVo platform : platforms) {
                if (!IntelTestEnum.HASHRATEPLATFORM.getName().equals(platform.getPlatformName())) {
                    throw new ParamException("算力平台参数错误");
                }
            }
        }
    }

    @Override
    public void insertSoftware(Software software, String cookieUuid, HttpServletRequest request)
        throws InputException, IOException {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        EulerUser user = userMapper.findByUuid(userUuid);
        if (Objects.equals(user.getUseable(), 0)) {
            throw new InputException("您已注销账号！无法申请技术测评");
        }
        CompanyVo companyVo = companyService.findCompanyByUserUuid(userUuid);
        if (companyVo == null || !Objects.equals(companyVo.getStatus(), CompanyStatusConstant.COMPANY_PASSED)) {
            throw new InputException("申请兼容性测评服务需要完成企业实名认证");
        }
        // 校验是否签署协议
        Protocol protocol =
            protocolMapper.selectProtocolDesc(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), userUuid);
        if (protocol == null || Objects.equals(protocol.getStatus(), 0)) {
            throw new InputException("申请兼容性测评服务需要签署技术测评协议");
        }
        software.setCompanyName(companyVo.getCompanyName());
        software.setCompanyId(companyVo.getId());
        // 设置软件信息表当前节点状态 1-认证申请
        software.setStatus(1);
        // reviewer为当前节点处理人
        software.setReviewer(userUuid);
        Date date = new Date();
        software.setApplicationTime(date);
        software.setUpdateTime(date);
        software.setUserUuid(userUuid);
        software.setProductName(software.getProductName().trim());
        software.setProductFunctionDesc(software.getProductFunctionDesc().trim());
        software.setUsageScenesDesc(software.getUsageScenesDesc().trim());
        software.setProductVersion(software.getProductVersion().trim());
        software.setTestOrgId(CenterEnum.findByName(software.getTestOrganization()).getId());
        // 将算力平台和服务器类型list转为json字符串
        String hashRatePlatform = JSON.toJSON(software.getHashratePlatformList()).toString();
        software.setJsonHashRatePlatform(hashRatePlatform);
        checkParam(software);
        // 获取测评流程
        List<ApprovalPathNode> approvalPath =
            approvalScenarioService.findApprovalPath(software.getTestOrganization(), software.getCpuVendor());
        software.setAsId(approvalPath.get(0).getAsId());
        Integer id = software.getId();
        // 判断是否存在id，如果已经存在id说明是驳回后重新提交，更新数据
        if (id == null || id == 0) {
            softwareMapper.insertSoftware(software);
            id = software.getId();
        } else {
            softwareMapper.recommit(software);
            // 调用审核接口
            ProcessVo processVo = new ProcessVo();
            processVo.setSoftwareId(software.getId());
            processVo.setHandlerResult(1);
            processVo.setTransferredComments("通过");
            processReview(processVo, cookieUuid, request);
            return;
        }
        // 设置节点表当前信息 1-认证申请
        setCurNode(userUuid, id);
        // 设置节点表下一个节点状态 2-方案审核
        Node nextNode = new Node();
        setNextNode(software, nextNode, approvalPath);
        // 更新软件信息表
        updateSoftwareStatusAndReviewer(id, nextNode.getHandler(), 2, new Date(), "", software.getTestOrgId(),
            approvalPath.get(0).getRoleId());
        // 发送邮件通知
        sendEmail(software, user);
    }

    private void setCurNode(String userUuid, Integer softwareId) {
        Node curNode = new Node();
        Date date = new Date();
        curNode.setNodeName(NODE_NAME_MAP.get(1));
        curNode.setStatus(1);
        // 设置处理结果：0-待处理 1-通过 2-驳回 3-转审
        curNode.setHandlerResult(1);
        // 当前节点处理人为申请人
        curNode.setHandler(userUuid);
        curNode.setHandlerTime(date);
        curNode.setSoftwareId(softwareId);
        curNode.setTransferredComments("通过");
        curNode.setUpdateTime(date);
        nodeMapper.insertNode(curNode);
    }

    private void setNextNode(Software software, Node nextNode, List<ApprovalPathNode> approvalPath) {
        nextNode.setNodeName(NODE_NAME_MAP.get(2));
        nextNode.setStatus(2);
        // 设置处理结果：0-待处理 1-通过 2-驳回 3-转审
        nextNode.setHandlerResult(0);
        // 查询处理人
        ApprovalPathNode approvalPathNode = approvalPath.get(0);
        nextNode.setHandler(approvalPathNode.getUserUuid());
        nextNode.setSoftwareId(software.getId());
        nextNode.setUpdateTime(new Date());
        nodeMapper.insertNode(nextNode);
    }

    private void checkParam(Software software) {
        checkCertificateInfo(software.getOsName(), software.getOsVersion(), software.getJsonHashRatePlatform());
        checkIntelParam(software.getCpuVendor(), software.getTestOrganization(), software.getJsonHashRatePlatform());
        String productTypeStr = software.getProductType();
        String[] productTypes = productTypeStr.split("/");
        if (productTypes.length != 2) {
            throw new ParamException("产品类型参数错误");
        }
        String productType = productTypes[0];
        String productChildrenType = productTypes[1];
        Integer product = masterDataMapper.findProduct(productType, productChildrenType);
        if (product == 0) {
            throw new ParamException("产品类型参数错误");
        }
    }

    @Override
    public JsonResponse<String> processReview(ProcessVo vo, String cookieUuid, HttpServletRequest request)
        throws IOException {
        Node latestNode = new Node();
        Software software = new Software();
        JsonResponse<String> jsonResponse = checkProcessData(vo, latestNode, software, cookieUuid);
        if (jsonResponse.getCode() == 400) {
            return jsonResponse;
        }
        Integer handlerResult = vo.getHandlerResult();
        // 设置当前节点信息
        Date date = new Date();
        updateLatestNode(handlerResult, latestNode, vo.getTransferredComments(), date);
        // 设置下一个节点信息
        Node nextNode = new Node();
        Map<String, Object> map;
        if (IntelTestEnum.CPU_VENDOR.getName().equals(software.getCpuVendor())) {
            map = getNextNodeNameForNumberAtIntel(handlerResult, software.getStatus());
        } else {
            map = getNextNodeNameForNumber(handlerResult, software.getStatus());
        }

        int nextNodeNameForNumber = Integer.parseInt(map.get("nextNodeNameForNumber").toString());
        String authenticationStatus = map.get("authenticationStatus").toString();
        // 判断是否是末节点
        JsonResponse<String> response = checkFinalNode(nextNode, nextNodeNameForNumber, software, vo);
        if (response.getCode() == 400) {
            return response;
        }
        if (nextNodeNameForNumber == 9) {
            // 证书签发节点已通过，需要生成证书
            String userUuid = encryptUtils.aesDecrypt(cookieUuid);
            logUtils.insertAuditLog(request, userUuid, "certificate", "generate", "generate certificate");
            generateCertificate(vo.getSoftwareId());
        }
        // 更新软件信息表
        updateSoftwareStatusAndReviewer(software.getId(), nextNode.getHandler(), nextNodeNameForNumber, new Date(),
            authenticationStatus, software.getTestOrgId(), software.getReviewRole());
        return JsonResponse.success();
    }

    private void updateSoftwareStatusAndReviewer(Integer id, String handler, Integer status, Date date,
        String authenticationStatus, Integer testOrgId, Integer reviewRole) {
        SoftwareVo softwareVo = new SoftwareVo();
        softwareVo.setId(id);
        softwareVo.setReviewer(handler);
        softwareVo.setReviewRole(reviewRole);
        softwareVo.setStatus(status);
        softwareVo.setUpdateTime(date);
        softwareVo.setAuthenticationStatus(authenticationStatus);
        softwareVo.setTestOrgId(testOrgId);
        softwareMapper.updateSoftwareStatusAndReviewer(softwareVo);
    }

    private JsonResponse<String> checkFinalNode(Node nextNode, int nextNodeNameForNumber, Software software,
        ProcessVo vo) {
        if (nextNodeNameForNumber <= 8) {
            String nodeName = NODE_NAME_MAP.get(nextNodeNameForNumber);
            nextNode.setStatus(nextNodeNameForNumber);
            if (StringUtils.isBlank(nodeName)) {
                return JsonResponse.failedResult("非法的请求");
            }
            // 查询处理人
            ApprovalPathNode approvalPathNode =
                approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(software.getAsId(), nextNodeNameForNumber);
            nextNode.setNodeName(nodeName);
            nextNode.setSoftwareId(software.getId());
            Date date = new Date();
            nextNode.setUpdateTime(date);
            nextNode.setHandlerResult(0);
            // 节点3、7的处理人就是申请人
            String handler = getHandler(nextNodeNameForNumber, vo, approvalPathNode, software);
            nextNode.setHandler(handler);
            nodeMapper.insertNode(nextNode);
        }
        return JsonResponse.success();
    }

    private String getHandler(int nextNodeNameForNumber, ProcessVo vo, ApprovalPathNode approvalPathNode,
        Software software) {
        String handler;
        if (nextNodeNameForNumber == 1 || nextNodeNameForNumber == 7) {
            handler = software.getUserUuid();
            software.setReviewRole(1);
        } else if (nextNodeNameForNumber == 3) {
            handler = approvalPathNode == null ? software.getUserUuid() : approvalPathNode.getUserUuid();
            software.setReviewRole(approvalPathNode == null ? 1 : approvalPathNode.getRoleId());
        } else {
            // 转审
            if (vo.getHandlerResult() == 3) {
                if (!userService.isUserDataScopeByRole(Integer.valueOf(vo.getTransferredUser()), software)) {
                    throw new ParamException("转审人不能是该角色的成员");
                }
                handler = vo.getTransferredUser();
            } else {
                // 非转审
                handler = approvalPathNode.getUserUuid();
                software.setReviewRole(approvalPathNode.getRoleId());
            }
        }
        return handler;
    }

    private Map<String, Object> getNextNodeNameForNumber(Integer handlerResult, Integer status) {
        Integer nextNodeNameForNumber = 0;
        String authenticationStatus = "";
        switch (handlerResult) {
            case 1:
                nextNodeNameForNumber = status + 1;
                break;
            case 2:
                nextNodeNameForNumber = status - 1;
                authenticationStatus = NODE_NAME_MAP.get(status) + "已驳回";
                break;
            case 3:
                nextNodeNameForNumber = status;
                break;
            default:
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("nextNodeNameForNumber", nextNodeNameForNumber);
        map.put("authenticationStatus", authenticationStatus);
        return map;
    }

    private Map<String, Object> getNextNodeNameForNumberAtIntel(Integer handlerResult, Integer status) {
        Integer nextNodeNameForNumber = 0;
        String authenticationStatus = "";
        switch (handlerResult) {
            case 1:
                nextNodeNameForNumber = status == 3 ? 5 : status + 1;
                break;
            case 2:
                nextNodeNameForNumber = status == 5 ? 3 : status - 1;
                authenticationStatus = NODE_NAME_MAP.get(status) + "已驳回";
                break;
            case 3:
                nextNodeNameForNumber = status;
                break;
            default:
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("nextNodeNameForNumber", nextNodeNameForNumber);
        map.put("authenticationStatus", authenticationStatus);
        return map;
    }

    private void updateLatestNode(Integer handlerResult, Node latestNode, String transferredComments, Date date) {
        latestNode.setHandlerResult(handlerResult);
        latestNode.setTransferredComments(transferredComments);
        latestNode.setHandlerTime(date);
        nodeMapper.updateNodeById(latestNode);
    }

    private JsonResponse<String> checkProcessData(ProcessVo vo, Node latestNode, Software software, String cookieUuid) {
        Integer handlerResult = vo.getHandlerResult();
        if (StringUtils.isBlank(vo.getTransferredComments())
            || (handlerResult == 3 && StringUtils.isBlank(vo.getTransferredUser()))) {
            return JsonResponse.failedResult("非法的审核参数");
        }
        Software softwareInDb = softwareMapper.findById(vo.getSoftwareId());
        if (softwareInDb == null) {
            return JsonResponse.failedResult("审核条目不存在");
        }
        if (softwareInDb.getStatus() == 3) {
            if (handlerResult != 1) {
                return JsonResponse.failedResult("非法的审核结果参数");
            }
            Map<String, Object> param = Maps.newHashMap();
            param.put("softwareId", softwareInDb.getId());
            param.put("fileType", "testReport");
            List<AttachmentsVo> attachmentsVos = softwareMapper.getAttachmentsNames(param);
            if (CollectionUtil.isEmpty(attachmentsVos)) {
                return JsonResponse.failed("未上传测试报告");
            }
        }
        if (softwareInDb.getStatus() == 7) {
            if (handlerResult == 1) {
                Map<String, Object> param = Maps.newHashMap();
                param.put("softwareId", softwareInDb.getId());
                param.put("fileType", "sign");
                List<AttachmentsVo> attachmentsVos = softwareMapper.getAttachmentsNames(param);
                if (CollectionUtil.isEmpty(attachmentsVos)) {
                    return JsonResponse.failed("未上传签名");
                }
            }
        }
        BeanUtils.copyProperties(softwareInDb, software);
        // 判断当前流程是否已经结束
        if (software.getStatus() == 9) {
            return JsonResponse.failedResult("审批流程结束");
        }
        // 校验当前用户是否是该节点的审核人
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (handlerResult == 3) {
            if (vo.getTransferredUser().equals(uuid)) {
                return JsonResponse.failedResult("转审人不能为自己");
            }
            boolean isApprove = checkPermission(vo, cookieUuid);
            if (!isApprove) {
                return JsonResponse.failedResult("该成员无权限审核");
            }
        }
        Node latestNodeInDb = nodeMapper.findLatestNodeById(vo.getSoftwareId());
        BeanUtils.copyProperties(latestNodeInDb, latestNode);
        if (!userService.isUserDataScopeByRole(Integer.valueOf(uuid), softwareInDb)) {
            return JsonResponse.failedResult("非法的审核人");
        }
        // 处理结果 0-待处理 1-通过 2-驳回 3-转审
        if (handlerResult < 1 || handlerResult > 3) {
            return JsonResponse.failedResult("非法的审核结果参数");
        }
        return JsonResponse.success();
    }

    private boolean checkPermission(ProcessVo vo, String cookieUuid) {
        List<SimpleUserVo> simpleUserVos = transferredUserList(vo.getSoftwareId(), cookieUuid);
        boolean isApprove = false;
        for (SimpleUserVo simpleUserVo : simpleUserVos) {
            if (Objects.equals(simpleUserVo.getUuid(), vo.getTransferredUser())) {
                isApprove = true;
            }
        }
        return isApprove;
    }

    @Override
    public List<SimpleUserVo> transferredUserList(Integer softwareId, String cookieUuid) {
        Software software = softwareMapper.findById(softwareId);
        ApprovalPathNode approvalPathNode =
            approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(software.getAsId(), software.getStatus());
        if (ObjectUtils.isEmpty(approvalPathNode)) {
            return new ArrayList<>();
        }
        List<Integer> userIdList = roleMapper.findUserByRole(approvalPathNode.getRoleId(), software.getTestOrgId());
        List<EulerUser> users = userMapper.findByUserId(userIdList);
        List<SimpleUserVo> userVos = users.stream().map(item -> {
            SimpleUserVo simpleUserVo = new SimpleUserVo();
            BeanUtils.copyProperties(item, simpleUserVo);
            return simpleUserVo;
        }).toList();
        // 转审人不能为自己
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        return userVos.stream().filter(item -> !item.getUuid().equals(uuid)).collect(Collectors.toList());
    }

    @Override
    public List<SoftwareListVo> getSoftwareList(SelectSoftwareVo selectSoftwareVo, String cookieUuid) {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        Company company = companyMapper.findRegisterSuccessCompanyByUserUuid(userUuid);
        if (ObjectUtils.isEmpty(company)) {
            return new ArrayList<>();
        }
        // 通过所属公司名获取全部认证列表
        selectSoftware.setCompanyName(company.getCompanyName());
        List<SoftwareListVo> getAllSoftwareList = softwareMapper.getSoftwareList(selectSoftware);
        processFields(getAllSoftwareList, userUuid);
        // 通过uuid直接查询该用户下所有认证列表
        selectSoftware.setApplicant(userUuid);
        List<SoftwareListVo> currentSoftwareList = softwareMapper.getSoftwareList(selectSoftware);
        processFields(currentSoftwareList, userUuid);
        if (CollectionUtil.isNotEmpty(selectSoftwareVo.getSelectMyApplication())) {
            return currentSoftwareList;
        }
        // 排序，将全部列表中所属当前用户的认证信息放置最前面
        getAllSoftwareList.removeAll(currentSoftwareList);
        currentSoftwareList.addAll(getAllSoftwareList);
        return currentSoftwareList;
    }

    private void processFields(List<SoftwareListVo> currentSoftwareList, String userUuid) {
        currentSoftwareList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAuthenticationStatus())) {
                item.setStatus(item.getAuthenticationStatus());
            }
        });
        currentSoftwareList.forEach(item -> {
            if (userUuid.equals(item.getReviewerUuid())) {
                String status = item.getStatus();
                String cpuVendor = item.getCpuVendor();
                switch (status) {
                    case "证书确认":
                        item.setOperation("确认");
                        break;
                    case "测试阶段":
                        if (!IntelTestEnum.CPU_VENDOR.getName().equals(cpuVendor)) {
                            item.setOperation("上传报告");
                            break;
                        }
                        break;
                    case "已完成":
                        item.setOperation("查看证书");
                        break;
                    case "方案审核已驳回":
                    case "报告初审已驳回":
                    case "证书签发已驳回":
                        item.setOperation("去处理");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public List<SoftwareListVo> getReviewSoftwareList(SelectSoftwareVo selectSoftwareVo, String cookieUuid) {
        Integer userUuid = Integer.valueOf(encryptUtils.aesDecrypt(cookieUuid));
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        selectSoftware.setDataScope(userService.getUserAllDateScope(userUuid));
        List<SoftwareListVo> reviewSoftwareList = softwareMapper.getReviewSoftwareList(selectSoftware);
        Map<Integer, List<Integer>> roleMap = userService.getUserAllRole(userUuid);
        updateSoftwareListStatus(reviewSoftwareList, roleMap);

        return reviewSoftwareList;
    }

    private void updateSoftwareListStatus(List<SoftwareListVo> softwareList, Map<Integer, List<Integer>> roleMap) {
        softwareList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAuthenticationStatus())) {
                item.setStatus(item.getAuthenticationStatus());
            }
            List<Integer> roleList = roleMap.getOrDefault(item.getReviewRole(), Collections.emptyList());
            if (!roleList.contains(item.getTestOrgId()) && !roleList.contains(0)) {
                return;
            }

            String operation = getOperation(item.getStatus(), item.getCpuVendor());
            item.setOperation(operation);
        });
    }

    private String getOperation(String status, String cpuVendor) {
        switch (status) {
            case "测试阶段":
                return IntelTestEnum.CPU_VENDOR.getName().equals(cpuVendor) ? "上传报告" : null;
            case "证书初审":
                return "证书初审";
            case "已完成":
                return "查看证书";
            case "方案审核":
            case "报告初审":
            case "报告复审":
            case "证书签发":
                return "审核";
            case "证书确认已驳回":
            case "证书初审已驳回":
            case "报告复审已驳回":
                return "去处理";
            default:
                return null;
        }
    }

    @Override
    public List<AuditRecordsVo> getAuditRecordsList(Integer softwareId) {
        return softwareMapper.getAuditRecordsList(softwareId);
    }

    @Override
    public IPage<AuditRecordsVo> getAuditRecordsListPage(Integer softwareId, String nodeName,
        IPage<AuditRecordsVo> page, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限查询该测评申请审核信息");
        }
        return softwareMapper.getAuditRecordsListPage(softwareId, nodeName, page);
    }

    @Override
    public CertificateInfoVo certificateInfo(Integer softwareId, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限查询该测评申请证书信息");
        }
        return softwareMapper.certificateInfo(softwareId);
    }

    @Override
    public List<AuditRecordsVo> getNodeList(Integer softwareId, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限查询该测评申请节点信息");
        }
        // 查询默认节点配置
        List<ApprovalPathNode> defaultNodes = approvalPathNodeService.findNodeByAsId(software.getAsId());

        // 查询审核节点
        List<AuditRecordsVo> auditRecordsList = getAuditRecordsList(softwareId);
        // 对审核节点按照节点名称分组
        Map<String, List<AuditRecordsVo>> recordsMap =
            auditRecordsList.stream().collect(Collectors.groupingBy(AuditRecordsVo::getNodeName));
        // 筛选每个节点中最新的数据
        List<AuditRecordsVo> latestNodes = getEveryNodeLatestData(recordsMap);
        // 判断审批流中最末端节点，如果有已驳回节点，最小的驳回节点就是最末节点，否则就是status最大的节点
        final int max = getMaxNodeStatus(latestNodes);
        // 过滤已审核节点
        List<AuditRecordsVo> filterLatestNodes =
            latestNodes.stream().filter(item -> item.getStatus() <= max).collect(Collectors.toList());
        // 过滤默认节点列表中已审核节点
        List<AuditRecordsVo> unFinishedNodes =
            defaultNodes.stream().filter(item -> item.getSoftwareStatus() > max).map(item -> {
                AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
                auditRecordsVo.setHandler(item.getUserUuid());
                auditRecordsVo.setNodeName(item.getApprovalNodeName());
                auditRecordsVo.setStatus(item.getSoftwareStatus());
                return auditRecordsVo;
            }).collect(Collectors.toList());
        // 组合数据
        filterLatestNodes.addAll(unFinishedNodes);
        checkPartnerNode(filterLatestNodes, software);
        filterLatestNodes.parallelStream().forEach(item -> {
            EulerUser user = userMapper.findByUuid(item.getHandler());
            if (user != null && StringUtils.isNoneBlank(user.getUsername())) {
                item.setHandlerName(user.getUsername());
            } else {
                item.setHandlerName(item.getHandler());
            }
        });
        return filterLatestNodes.stream().sorted(Comparator.comparing(AuditRecordsVo::getStatus))
            .collect(Collectors.toList());
    }

    private List<AuditRecordsVo> checkPartnerNode(List<AuditRecordsVo> latestNodes, Software software) {
        boolean node3 = latestNodes.stream().anyMatch(item -> item.getNodeName().equals(NODE_NAME_MAP.get(3)));
        boolean node7 = latestNodes.stream().anyMatch(item -> item.getNodeName().equals(NODE_NAME_MAP.get(7)));
        if (!node3) {
            AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
            auditRecordsVo.setNodeName(NODE_NAME_MAP.get(3));
            auditRecordsVo.setHandler(software.getUserUuid());
            auditRecordsVo.setStatus(3);
            latestNodes.add(auditRecordsVo);
        }
        if (!node7) {
            AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
            auditRecordsVo.setNodeName(NODE_NAME_MAP.get(7));
            auditRecordsVo.setHandler(software.getUserUuid());
            auditRecordsVo.setStatus(7);
            latestNodes.add(auditRecordsVo);
        }
        return latestNodes;
    }

    private List<AuditRecordsVo> getEveryNodeLatestData(Map<String, List<AuditRecordsVo>> recordsMap) {
        List<AuditRecordsVo> latestNodes = new ArrayList<>();
        for (Map.Entry<String, List<AuditRecordsVo>> entry : recordsMap.entrySet()) {
            AuditRecordsVo auditRecordsVo;
            String nodeName = entry.getKey();
            // 处理时间为空的一条数据是该节点最新数据
            List<AuditRecordsVo> collect = recordsMap.get(nodeName).stream()
                .filter(item -> StringUtils.isEmpty(item.getHandlerTime())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                auditRecordsVo = collect.get(0);
            } else {
                // 筛选时间最大的数据
                auditRecordsVo = recordsMap.get(nodeName).stream()
                    .sorted(Comparator.comparing(AuditRecordsVo::getHandlerTime).reversed())
                    .collect(Collectors.toList()).get(0);
            }
            latestNodes.add(auditRecordsVo);
        }
        return latestNodes;
    }

    private Integer getMaxNodeStatus(List<AuditRecordsVo> latestNodes) {
        List<AuditRecordsVo> auditRecordsVoList =
            latestNodes.stream().sorted(Comparator.comparing(AuditRecordsVo::getStatus).reversed()).toList();
        Integer maxStatus = auditRecordsVoList.get(0).getStatus();
        List<AuditRecordsVo> passRecordList =
            auditRecordsVoList.stream().filter(item -> "通过".equals(item.getHandlerResult()))
                .max(Comparator.comparing(AuditRecordsVo::getStatus)).stream().toList();
        boolean lastActionIsBack = false;
        for (AuditRecordsVo auditRecordsVo : auditRecordsVoList) {
            if ("已驳回".equals(auditRecordsVo.getHandlerResult())) {
                maxStatus = auditRecordsVo.getStatus();
                if (passRecordList.isEmpty()
                    || auditRecordsVo.getHandlerTime().compareTo(passRecordList.get(0).getHandlerTime()) > 0) {
                    lastActionIsBack = true;
                }
            } else if ("待处理".equals(auditRecordsVo.getHandlerResult()) && !lastActionIsBack) {
                maxStatus = auditRecordsVo.getStatus();
                break;
            }
        }
        return maxStatus;
    }

    @Override
    public JsonResponse<String> upload(MultipartFile file, Integer softwareId, Integer fileTypeCode, String fileType,
        HttpServletRequest request) throws InputException, TestReportExceedMaxAmountException {
        Software software = softwareMapper.findById(softwareId);
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (!uuid.equals(software.getReviewer())) {
            return JsonResponse.failed("不是当前处理人");
        }
        FileModel fileModel = fileUtils.uploadFile(file, softwareId, fileTypeCode, fileType, request);
        Map<String, Object> param = Maps.newHashMap();
        param.put("softwareId", softwareId);
        param.put("fileType", fileType);
        List<AttachmentsVo> attachmentsVos = softwareMapper.getAttachmentsNames(param);
        if (FILE_TYPE_SIGN.equals(fileType)) {
            if (CollectionUtil.isEmpty(attachmentsVos)) {
                softwareMapper.insertAttachment(fileModel);
            } else {
                softwareMapper.updateSign(fileModel);
            }
        } else if (FILE_TYPE_TEST_REPORT.equals(fileType)) {
            if (attachmentsVos.size() < 5) {
                softwareMapper.insertAttachment(fileModel);
            } else {
                throw new TestReportExceedMaxAmountException("上传的测试报告数量已超过5个");
            }
        } else {
            softwareMapper.insertAttachment(fileModel);
        }
        return JsonResponse.success(fileModel.getFileId());
    }

    @Override
    public List<AttachmentsVo> getAttachmentsNames(Integer softwareId, String fileType, HttpServletRequest request) {
        Software software = softwareMapper.findById(softwareId);
        if (software == null) {
            throw new ParamException("无权限获取该测评申请文件");
        }
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限获取该测评申请文件");
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("softwareId", softwareId);
        param.put("fileType", fileType);
        return softwareMapper.getAttachmentsNames(param);
    }

    @Override
    public void downloadAttachments(String fileId, HttpServletResponse response, HttpServletRequest request)
        throws UnsupportedEncodingException, InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (!userService.isAttachmentPermission(uuid, attachments)) {
            throw new ParamException("无权限下载当前文件");
        }
        fileUtils.downloadFile(fileId, attachments.getFileName(), response);
    }

    @Override
    public void previewImage(String fileId, HttpServletResponse response) throws InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        if (ObjectUtils.isNotEmpty(attachments)) {
            fileUtils.previewImage(fileId, response);
        }
    }

    @Override
    public void deleteAttachments(String fileId, HttpServletRequest request) {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        if (attachments == null) {
            throw new ParamException("未找到当前文件");
        }
        String fileType = attachments.getFileType();
        boolean isSignFile = Objects.equals(fileType, FILE_TYPE_SIGN);
        boolean isTestReportFile = Objects.equals(fileType, FILE_TYPE_TEST_REPORT);
        if (!isSignFile && !isTestReportFile) {
            throw new ParamException("无权限删除当前文件");
        }
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        String fileOwnerUuid = attachments.getUuid();
        if (fileOwnerUuid == null) {
            throw new ParamException("无权限删除当前文件");
        }
        if (!Objects.equals(userUuid, fileOwnerUuid)) {
            throw new ParamException("无权限删除当前文件");
        }
        Software software = softwareMapper.findById(Integer.parseInt(attachments.getSoftwareId()));
        Integer status = software.getStatus();
        if ((isSignFile && status != 7) || (isTestReportFile && status != 3)) {
            throw new ParamException("无权限删除当前文件");
        }
        softwareMapper.deleteAttachments(fileId);
    }

    @Override
    public void generateCertificate(Integer softwareId) throws IOException {
        certificateGenerationUtils.generateCertificate(softwareMapper.generateCertificate(softwareId));
    }

    @Override
    public void previewCertificate(Integer softwareId, HttpServletResponse response)
        throws InputException, IOException {
        GenerateCertificate certificate = softwareMapper.generateCertificate(softwareId);
        certificateGenerationUtils.previewCertificate(certificate, response);
    }

    @Override
    public void previewCertificateConfirmInfo(CertificateConfirmVo certificateConfirmVo, HttpServletResponse response)
        throws InputException, IOException {
        GenerateCertificate certificate = softwareMapper.generateCertificate(certificateConfirmVo.getSoftwareId());
        certificate.setProductVersion(certificateConfirmVo.getProductVersion());
        certificate.setOsName(certificateConfirmVo.getOsName());
        certificate.setOsVersion(certificateConfirmVo.getOsVersion());
        String hashRatePlatform = JSON.toJSON(certificateConfirmVo.getHashratePlatformList()).toString();
        certificate.setHashratePlatform(hashRatePlatform);
        checkCertificateInfo(certificate.getOsName(), certificate.getOsVersion(), hashRatePlatform);
        certificateGenerationUtils.previewCertificate(certificate, response);
    }

    public List<String> getRoles(String userUuid) {
        EulerUser user = userMapper.findByUuid(userUuid);
        if (user == null) {
            throw new ParamException("请登录");
        }
        List<RoleVo> roleVos = roleMapper.findRoleInfoByUserId(user.getId());
        return roleVos.stream().map(RoleVo::getRole).collect(Collectors.toList());
    }

    /**
     * send test apply to innovation center
     *
     * @param software test info
     */
    private void sendEmail(Software software, EulerUser applyUser) {
        ApprovalScenario approvalScenario = approvalScenarioService.findById(software.getAsId());
        if (approvalScenario == null) {
            return;
        }
        if (!"intel".equals(approvalScenario.getName())) {
            return;
        }
        List<Integer> userIdList = roleMapper.findUserByRole(8, software.getTestOrgId());
        List<EulerUser> eulerUserList = userMapper.findByUserId(userIdList);
        List<String> receiverList = new ArrayList<>();
        for (EulerUser eulerUser : eulerUserList) {
            if (!StringUtils.isEmpty(eulerUser.getMail())) {
                receiverList.add(eulerUser.getMail());
            }
        }

        String subject = "英特尔先进技术评测业务申请";

        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("companyName", software.getCompanyName());
        replaceMap.put("productName", software.getProductName());
        replaceMap.put("productFunctionDesc", software.getProductFunctionDesc());
        replaceMap.put("usageScenesDesc", software.getUsageScenesDesc());
        replaceMap.put("productVersion", software.getProductVersion());
        replaceMap.put("osName", software.getOsName());
        replaceMap.put("osVersion", software.getOsVersion());

        JSONArray jsonArray = JSON.parseArray(software.getJsonHashRatePlatform());
        List<ComputingPlatformVo> computingPlatformVos = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ComputingPlatformVo computingPlatformVo = JSON.toJavaObject(jsonObject, ComputingPlatformVo.class);
            computingPlatformVos.add(computingPlatformVo);
        }
        List<String> platformString = computingPlatformVos.stream().map(item -> {
            String platform = String.join("、", item.getServerTypes());
            return item.getPlatformName() + "/" + item.getServerProvider() + "/" + platform;
        }).toList();

        replaceMap.put("jsonHashRatePlatform", StringUtils.join(platformString, "<br>"));
        replaceMap.put("productType", software.getProductType());
        replaceMap.put("userName", applyUser.getUsername());
        replaceMap.put("userEmail", applyUser.getMail());
        replaceMap.put("userPhone", applyUser.getTelephone());
        String content = emailConfig.getIntelNoticeEmailContent(replaceMap);
        emailConfig.sendMail(receiverList, subject, content, new ArrayList<>());
    }

    @Override
    public PageVo<CompatibilityVo> findCommunityCheckList(SoftwareFilterVo vo) {
        List<String> datasource = vo.getDataSource();
        if (datasource == null || datasource.isEmpty()) {
            datasource = Arrays.asList("assessment", "upload");
        }
        List<CompatibilityVo> checkList = new ArrayList<>();
        if (datasource.contains("assessment")) {
            checkList = getCheckList(vo);
        }
        List<CompatibilityVo> uploadList = new ArrayList<>();
        if (datasource.contains("upload")) {
            uploadList = getUploadList(vo);
        }
        List<CompatibilityVo> allDataList = Stream.of(checkList, uploadList).flatMap(Collection::stream).toList();
        List<CompatibilityVo> listPage = ListPageUtils.getListPage(allDataList, vo.getPageNo(), vo.getPageSize());
        return new PageVo<>(allDataList.size(), listPage);
    }

    private List<CompatibilityVo> getCheckList(SoftwareFilterVo vo) {
        List<String> productType = vo.getProductType();
        List<Compatibility> communityCheckList = softwareMapper.findCommunityCheckList(vo);
        communityCheckList = communityCheckList.stream().filter(item -> {
            boolean checkType = true;
            if (!item.getType().contains("/")) {
                return false;
            }
            String[] split = item.getType().split("/");
            item.setType(split[1]);
            if (productType != null && !productType.isEmpty()) {
                for (String str : productType) {
                    checkType = split[0].contains(str);
                    if (checkType) {
                        break;
                    }
                }
            }
            return checkType;
        }).map(item -> {
            String jsonPlatformTypeAndServerModel = item.getJsonPlatformTypeAndServerModel();
            JSONArray jsonArray = JSON.parseArray(jsonPlatformTypeAndServerModel);
            List<ComputingPlatformVo> computingPlatformVoList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ComputingPlatformVo computingPlatformVo = JSON.toJavaObject(jsonObject, ComputingPlatformVo.class);
                computingPlatformVoList.add(computingPlatformVo);
            }
            item.setPlatformTypeAndServerModel(computingPlatformVoList);
            return item;
        }).toList();
        return communityCheckList.stream().map(compatibility -> {
            CompatibilityVo compatibilityVo = new CompatibilityVo();
            BeanUtils.copyProperties(compatibility, compatibilityVo);
            return compatibilityVo;
        }).toList();
    }

    private List<CompatibilityVo> getUploadList(SoftwareFilterVo vo) {
        List<String> productType = vo.getProductType();
        List<CompatibleDataInfo> communityUploadList = compatibleDataMapper.findCommunityUploadList(vo);
        communityUploadList = communityUploadList.stream().filter(item -> {
            boolean checkType = true;
            if (productType != null && !productType.isEmpty()) {
                for (String str : productType) {
                    checkType = item.getProductType().contains(str);
                    if (checkType) {
                        break;
                    }
                }
            }
            return checkType;
        }).toList();
        return communityUploadList.stream().map(item -> {
            CompatibilityVo compatibilityVo = new CompatibilityVo();
            BeanUtils.copyProperties(item, compatibilityVo);

            List<ComputingPlatformVo> computingPlatformVoList = new ArrayList<>();
            ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
            computingPlatformVo.setPlatformName(item.getServerPlatform());
            computingPlatformVo.setServerProvider(item.getServerSupplier());
            computingPlatformVo.setServerTypes(Arrays.asList(item.getServerModel()));
            computingPlatformVoList.add(computingPlatformVo);

            compatibilityVo.setType(item.getProductSubtype()).setCompanyName(item.getCompanyName())
                .setTestOrganization(item.getUploadCompany()).setPlatformTypeAndServerModel(computingPlatformVoList)
                .setOsName(item.getSystemName()).setOsVersion(item.getSystemVersion());
            return compatibilityVo;
        }).toList();
    }

    @Override
    public FilterCriteriaVo filterCeriteria() {
        List<String> osNames = softwareMapper.findOsName();
        List<String> testOrganizations = softwareMapper.findTestOrganization();
        FilterCriteriaVo filterCriteriaVo = new FilterCriteriaVo();
        filterCriteriaVo.setOsNames(osNames);
        filterCriteriaVo.setTestOrganizations(testOrganizations);
        return filterCriteriaVo;
    }
}
