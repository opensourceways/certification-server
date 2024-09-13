/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.extension.EmailConfig;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.exception.TestReportExceedMaxAmountException;
import com.huawei.it.euler.mapper.*;
import com.huawei.it.euler.model.constant.CompanyStatusConstant;
import com.huawei.it.euler.model.entity.*;
import com.huawei.it.euler.model.enumeration.IntelTestEnum;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.ApprovalPathNodeService;
import com.huawei.it.euler.service.ApprovalScenarioService;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.service.SoftwareService;
import com.huawei.it.euler.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private ApprovalPathNodeService approvalPathNodeService;

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
    private LogUtils logUtils;

    @Autowired
    private ApprovalScenarioService approvalScenarioService;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private AccountService accountService;

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
    public Software findById(Integer id, String uuid) {
        Software software = softwareMapper.findById(id);
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
    public JsonResponse<String> updateSoftware(SoftwareVo software, String uuid, HttpServletRequest request)
            throws IOException {
        Software softwareDb = softwareMapper.findById(software.getId());
        if (softwareDb.getStatus() != 6) {
            return JsonResponse.failed("该测评申请无法更新软件认证信息");
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
        JsonResponse<String> processJsonRep = processReview(processVo, uuid, request);
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
     * @param cpuVendor        硬件厂商
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
    public void insertSoftware(Software software, String uuid, HttpServletRequest request)
            throws InputException, IOException {
        CompanyVo companyVo = companyService.findCompanyByUserUuid(uuid);
        if (companyVo == null || !Objects.equals(companyVo.getStatus(), CompanyStatusConstant.COMPANY_PASSED)) {
            throw new InputException("申请兼容性测评服务需要完成企业实名认证");
        }
        // 校验是否签署协议
        Protocol protocol = protocolMapper.selectProtocolDesc(
                ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
        if (protocol == null || Objects.equals(protocol.getStatus(), 0)) {
            throw new InputException("申请兼容性测评服务需要签署技术测评协议");
        }
        software.setCompanyName(companyVo.getCompanyName());
        software.setCompanyId(companyVo.getId());
        // 设置软件信息表当前节点状态 1-认证申请
        software.setStatus(1);
        // reviewer为当前节点处理人
        software.setReviewer(uuid);
        Date date = new Date();
        software.setApplicationTime(date);
        software.setUpdateTime(date);
        software.setUserUuid(uuid);
        software.setProductName(software.getProductName().trim());
        software.setProductFunctionDesc(software.getProductFunctionDesc().trim());
        software.setUsageScenesDesc(software.getUsageScenesDesc().trim());
        software.setProductVersion(software.getProductVersion().trim());
        // 将算力平台和服务器类型list转为json字符串
        String hashRatePlatform = JSON.toJSON(software.getHashratePlatformList()).toString();
        software.setJsonHashRatePlatform(hashRatePlatform);
        checkParam(software);
        // 获取测评流程
        List<ApprovalPathNode> approvalPath = approvalScenarioService.findApprovalPath(software.getTestOrganization(), software.getCpuVendor());
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
            processReview(processVo, uuid, request);
            return;
        }
        // 设置节点表当前信息 1-认证申请
        setCurNode(uuid, id);
        // 设置节点表下一个节点状态 2-方案审核
        Node nextNode = new Node();
        setNextNode(software, nextNode, approvalPath);
        // 更新软件信息表
        updateSoftwareStatusAndReviewer(id, nextNode.getHandler(), 2, new Date(), "");
        // 发送邮件通知
        sendEmail(software, uuid);
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
    public JsonResponse<String> processReview(ProcessVo vo, String uuid, HttpServletRequest request) throws IOException {
        Node latestNode = new Node();
        Software software = new Software();
        JsonResponse<String> jsonResponse = checkProcessData(vo, latestNode, software, uuid);
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
            logUtils.insertAuditLog(request, uuid, "certificate", "generate", "generate certificate");
            generateCertificate(vo.getSoftwareId());
        }
        // 更新软件信息表
        updateSoftwareStatusAndReviewer(software.getId(), nextNode.getHandler(), nextNodeNameForNumber,
                new Date(), authenticationStatus);
        return JsonResponse.success();
    }

    private void updateSoftwareStatusAndReviewer(Integer id, String handler, Integer status,
                                                 Date date, String authenticationStatus) {
        SoftwareVo softwareVo = new SoftwareVo();
        softwareVo.setId(id);
        softwareVo.setReviewer(handler);
        softwareVo.setStatus(status);
        softwareVo.setUpdateTime(date);
        softwareVo.setAuthenticationStatus(authenticationStatus);
        softwareMapper.updateSoftwareStatusAndReviewer(softwareVo);
    }

    private JsonResponse<String> checkFinalNode(Node nextNode, int nextNodeNameForNumber,
                                                Software software, ProcessVo vo) {
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
            String handler = getHandler(nextNodeNameForNumber, vo, approvalPathNode, software.getUserUuid());
            nextNode.setHandler(handler);
            nodeMapper.insertNode(nextNode);
        }
        return JsonResponse.success();
    }

    private String getHandler(int nextNodeNameForNumber, ProcessVo vo, ApprovalPathNode approvalPathNode, String uuid) {
        String handler;
        if (nextNodeNameForNumber == 1 || nextNodeNameForNumber == 7) {
            handler = uuid;
        } else if (nextNodeNameForNumber == 3) {
            handler = approvalPathNode == null ? uuid : approvalPathNode.getUserUuid();
        } else {
            // 转审
            if (vo.getHandlerResult() == 3) {
                handler = vo.getTransferredUser();
            } else {
                // 非转审
                handler = approvalPathNode.getUserUuid();
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

    private JsonResponse<String> checkProcessData(ProcessVo vo, Node latestNode, Software software, String uuid) {
        Integer handlerResult = vo.getHandlerResult();
        if (StringUtils.isBlank(vo.getTransferredComments()) ||
                (handlerResult == 3 && StringUtils.isBlank(vo.getTransferredUser()))) {
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
        BeanUtils.copyProperties(softwareInDb, software);
        // 判断当前流程是否已经结束
        if (software.getStatus() == 9) {
            return JsonResponse.failedResult("审批流程结束");
        }
        // 校验当前用户是否是该节点的审核人
        if (handlerResult == 3) {
            if (vo.getTransferredUser().equals(uuid)) {
                return JsonResponse.failedResult("转审人不能为自己");
            }
            boolean isApprove = checkPermission(vo, uuid);
            if (!isApprove) {
                return JsonResponse.failedResult("该成员无权限审核");
            }
        }
        Node latestNodeInDb = nodeMapper.findLatestNodeById(vo.getSoftwareId());
        BeanUtils.copyProperties(latestNodeInDb, latestNode);
        if (!latestNode.getHandler().equals(uuid)) {
            return JsonResponse.failedResult("非法的审核人");
        }
        // 处理结果 0-待处理 1-通过 2-驳回 3-转审
        if (handlerResult < 1 || handlerResult > 3) {
            return JsonResponse.failedResult("非法的审核结果参数");
        }
        return JsonResponse.success();
    }

    private boolean checkPermission(ProcessVo vo, String uuid) {
        List<SimpleUserVo> simpleUserVos = transferredUserList(vo.getSoftwareId(), uuid);
        boolean isApprove = false;
        for (SimpleUserVo simpleUserVo : simpleUserVos) {
            if (Objects.equals(simpleUserVo.getUuid(), vo.getTransferredUser())) {
                isApprove = true;
            }
        }
        return isApprove;
    }

    @Override
    public List<SimpleUserVo> transferredUserList(Integer softwareId, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        ApprovalPathNode approvalPathNode =
                approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(software.getAsId(), software.getStatus());
        if (ObjectUtils.isEmpty(approvalPathNode)) {
            return new ArrayList<>();
        }
        List<UserInfo> userInfoList = accountService.getUserInfoList(approvalPathNode.getRoleId());
        List<SimpleUserVo> userVos = userInfoList.stream().map(item -> {
            SimpleUserVo simpleUserVo = new SimpleUserVo();
            BeanUtils.copyProperties(item, simpleUserVo);
            return simpleUserVo;
        }).toList();
        // 转审人不能为自己
        return userVos.stream().filter(item -> !item.getUuid().equals(uuid)).collect(Collectors.toList());
    }

    @Override
    public List<SoftwareListVo> getSoftwareList(SelectSoftwareVo selectSoftwareVo, String uuid) {
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        Company company = companyMapper.findRegisterSuccessCompanyByUserUuid(uuid);
        if (ObjectUtils.isEmpty(company)) {
            return new ArrayList<>();
        }
        // 通过所属公司名获取全部认证列表
        selectSoftware.setCompanyName(company.getCompanyName());
        List<SoftwareListVo> getAllSoftwareList = softwareMapper.getSoftwareList(selectSoftware);
        processFields(getAllSoftwareList, uuid);
        // 通过uuid直接查询该用户下所有认证列表
        selectSoftware.setApplicant(uuid);
        List<SoftwareListVo> currentSoftwareList = softwareMapper.getSoftwareList(selectSoftware);
        processFields(currentSoftwareList, uuid);
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
    public List<SoftwareListVo> getReviewSoftwareList(SelectSoftwareVo selectSoftwareVo, String uuid) {
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        selectSoftware.setApplicant(uuid);
        List<SoftwareListVo> reviewSoftwareList = softwareMapper.getReviewSoftwareList(selectSoftware);
        reviewSoftwareList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAuthenticationStatus())) {
                item.setStatus(item.getAuthenticationStatus());
            }
        });
        reviewSoftwareList.forEach(item -> {
            String status = item.getStatus();
            String cpuVendor = item.getCpuVendor();
            if (uuid.equals(item.getReviewerUuid())) {
                switch (status) {
                    case "测试阶段":
                        if (IntelTestEnum.CPU_VENDOR.getName().equals(cpuVendor)) {
                            item.setOperation("上传报告");
                            break;
                        }
                        break;
                    case "证书初审":
                        item.setOperation("证书初审");
                        break;
                    case "已完成":
                        item.setOperation("查看证书");
                        break;
                    case "方案审核":
                    case "报告初审":
                    case "报告复审":
                    case "证书签发":
                        item.setOperation("审核");
                        break;
                    case "证书确认已驳回":
                    case "证书初审已驳回":
                    case "报告复审已驳回":
                        item.setOperation("去处理");
                        break;
                    default:
                        break;
                }
            }
        });
        return reviewSoftwareList;
    }

    @Override
    public List<AuditRecordsVo> getAuditRecordsList(Integer softwareId) {
        return softwareMapper.getAuditRecordsList(softwareId);
    }

    @Override
    public IPage<AuditRecordsVo> getAuditRecordsListPage(Integer softwareId, String nodeName,
                                                         IPage<AuditRecordsVo> page, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        if (accountService.isPartner(uuid)) {
            if (software != null && !Objects.equals(uuid, software.getUserUuid())) {
                throw new ParamException("无权限查询该测评申请审核信息");
            }
        }
        return softwareMapper.getAuditRecordsListPage(softwareId, nodeName, page);
    }

    @Override
    public CertificateInfoVo certificateInfo(Integer softwareId, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        if (accountService.isPartner(uuid)) {
            if (software != null && !Objects.equals(uuid, software.getUserUuid())) {
                throw new ParamException("无权限查询该测评申请证书信息");
            }
        }
        return softwareMapper.certificateInfo(softwareId);
    }

    @Override
    public List<AuditRecordsVo> getNodeList(Integer softwareId) {
        Software software = softwareMapper.findById(softwareId);
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
        List<AuditRecordsVo> filterLatestNodes = latestNodes.stream()
                .filter(item -> item.getStatus() <= max)
                .collect(Collectors.toList());
        // 过滤默认节点列表中已审核节点
        List<AuditRecordsVo> unFinishedNodes = defaultNodes.stream()
                .filter(item -> item.getSoftwareStatus() > max).map(item -> {
                    AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
                    auditRecordsVo.setHandler(item.getUserUuid());
                    auditRecordsVo.setNodeName(item.getApprovalNodeName());
                    auditRecordsVo.setStatus(item.getSoftwareStatus());
                    return auditRecordsVo;
                }).toList();
        // 组合数据
        filterLatestNodes.addAll(unFinishedNodes);
        checkPartnerNode(filterLatestNodes, software);
        filterLatestNodes.parallelStream().forEach(item -> {
            UserInfo userInfo = accountService.getUserInfo(item.getHandler());
            if (userInfo != null && StringUtils.isNoneBlank(userInfo.getUserName())) {
                item.setHandlerName(userInfo.getUserName());
            } else {
                item.setHandlerName(item.getHandler());
            }
        });
        return filterLatestNodes.stream()
                .sorted(Comparator.comparing(AuditRecordsVo::getStatus))
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
                    .filter(item -> StringUtils.isEmpty(item.getHandlerTime()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                auditRecordsVo = collect.get(0);
            } else {
                // 筛选时间最大的数据
                auditRecordsVo = recordsMap.get(nodeName).stream()
                        .sorted(Comparator.comparing(AuditRecordsVo::getHandlerTime).reversed())
                        .collect(Collectors.toList())
                        .get(0);
            }
            latestNodes.add(auditRecordsVo);
        }
        return latestNodes;
    }

    private Integer getMaxNodeStatus(List<AuditRecordsVo> latestNodes) {
        List<AuditRecordsVo> auditRecordsVoList = latestNodes.stream().sorted(Comparator.comparing(AuditRecordsVo::getStatus).reversed()).toList();
        Integer maxStatus = auditRecordsVoList.get(0).getStatus();
        List<AuditRecordsVo> passRecordList = auditRecordsVoList.stream().filter(item -> "通过".equals(item.getHandlerResult()))
                .max(Comparator.comparing(AuditRecordsVo::getStatus)).stream().toList();
        boolean lastActionIsBack = false;
        for (AuditRecordsVo auditRecordsVo : auditRecordsVoList) {
            if ("已驳回".equals(auditRecordsVo.getHandlerResult())) {
                maxStatus = auditRecordsVo.getStatus();
                if (passRecordList.isEmpty() || auditRecordsVo.getHandlerTime().compareTo(passRecordList.get(0).getHandlerTime()) > 0) {
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
                                       String uuid) throws InputException, TestReportExceedMaxAmountException {
        Software software = softwareMapper.findById(softwareId);
        if (!uuid.equals(software.getReviewer())) {
            return JsonResponse.failed("不是当前处理人");
        }
        FileModel fileModel = fileUtils.uploadFile(file, softwareId, fileTypeCode, fileType, uuid);
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
    public List<AttachmentsVo> getAttachmentsNames(Integer softwareId, String fileType, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        if (software == null) {
            throw new ParamException("无权限获取该测评申请文件");
        }
        if (accountService.isPartner(uuid) && !Objects.equals(software.getUserUuid(), uuid)) {
            throw new ParamException("无权限获取该测评申请文件");
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("softwareId", softwareId);
        param.put("fileType", fileType);
        return softwareMapper.getAttachmentsNames(param);
    }

    @Override
    public void downloadAttachments(String fileId, HttpServletResponse response, String uuid)
            throws UnsupportedEncodingException, InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        // 如果登录账号为伙伴侧角色，需判断是否越权
        if (accountService.isPartner(uuid)) {
            if (attachments.getSoftwareId() != null) {
                int softwareId = Integer.parseInt(attachments.getSoftwareId());
                Software software = softwareMapper.findById(softwareId);
                if (software == null || !Objects.equals(software.getUserUuid(), uuid)) {
                    throw new ParamException("无权限下载当前文件");
                }
            } else {
                if (!Objects.equals(uuid, attachments.getUuid())) {
                    throw new ParamException("无权限下载当前文件");
                }
            }
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
    public void deleteAttachments(String fileId, String uuid) {
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
        String fileOwnerUuid = attachments.getUuid();
        if (fileOwnerUuid == null) {
            throw new ParamException("无权限删除当前文件");
        }
        if (!Objects.equals(uuid, fileOwnerUuid)) {
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
            throws IOException {
        GenerateCertificate certificate = softwareMapper.generateCertificate(softwareId);
        certificateGenerationUtils.previewCertificate(certificate, response);
    }

    @Override
    public void previewCertificateConfirmInfo(CertificateConfirmVo certificateConfirmVo, HttpServletResponse response)
            throws IOException {
        GenerateCertificate certificate = softwareMapper.generateCertificate(certificateConfirmVo.getSoftwareId());
        certificate.setProductVersion(certificateConfirmVo.getProductVersion());
        certificate.setOsName(certificateConfirmVo.getOsName());
        certificate.setOsVersion(certificateConfirmVo.getOsVersion());
        String hashRatePlatform = JSON.toJSON(certificateConfirmVo.getHashratePlatformList()).toString();
        certificate.setHashratePlatform(hashRatePlatform);
        checkCertificateInfo(certificate.getOsName(), certificate.getOsVersion(), hashRatePlatform);
        certificateGenerationUtils.previewCertificate(certificate, response);
    }

    /**
     * send test apply to innovation center
     *
     * @param software test info
     */
    private void sendEmail(Software software, String uuid) {
        ApprovalScenario approvalScenario = approvalScenarioService.findById(software.getAsId());
        if (approvalScenario == null) {
            return;
        }
        if (!"intel".equals(approvalScenario.getName())) {
            return;
        }
        List<UserInfo> userInfoList = accountService.getUserInfoList(8);
        List<String> receiverList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            if (!StringUtils.isEmpty(userInfo.getEmail())) {
                receiverList.add(userInfo.getEmail());
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

        UserInfo userInfo = accountService.getUserInfo(uuid);
        replaceMap.put("userName", userInfo.getUserName());
        replaceMap.put("userEmail", userInfo.getEmail());
        replaceMap.put("userPhone", userInfo.getPhone());
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
            BeanUtils.copyProperties(compatibility,compatibilityVo);
            return compatibilityVo;
        }).toList();
    }

    private List<CompatibilityVo> getUploadList(SoftwareFilterVo vo){
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
            BeanUtils.copyProperties(item,compatibilityVo);

            List<ComputingPlatformVo> computingPlatformVoList = new ArrayList<>();
            ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
            computingPlatformVo.setPlatformName(item.getServerPlatform());
            computingPlatformVo.setServerProvider(item.getServerSupplier());
            computingPlatformVo.setServerTypes(Arrays.asList(item.getServerModel()));
            computingPlatformVoList.add(computingPlatformVo);

            compatibilityVo.setType(item.getProductSubtype())
                    .setCompanyName(item.getCompanyName())
                    .setTestOrganization(item.getUploadCompany())
                    .setPlatformTypeAndServerModel(computingPlatformVoList)
                    .setOsName(item.getSystemName())
                    .setOsVersion(item.getSystemVersion());
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
