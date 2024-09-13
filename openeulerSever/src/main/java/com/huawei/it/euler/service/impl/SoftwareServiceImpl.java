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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.huawei.it.euler.model.enumeration.*;
import com.huawei.it.euler.model.query.AttachmentQuery;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.*;
import com.huawei.it.euler.util.CertificateGenerationUtils;
import com.huawei.it.euler.util.FileUtils;
import com.huawei.it.euler.util.ListPageUtils;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SoftwareServiceImpl
 *
 * @since 2024/06/29
 */
@Service
@Transactional
public class SoftwareServiceImpl implements SoftwareService {

    private static  final Logger LOGGER = LoggerFactory.getLogger(SoftwareServiceImpl.class);

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
    private ApprovalScenarioService approvalScenarioService;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private AccountService accountService;

    @Override
    public Software findById(Integer id, String uuid) {
        Software software = softwareMapper.findById(id);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException(ErrorCodes.UNAUTHORIZED.getMessage());
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
    public JsonResponse<String> updateSoftware(SoftwareVo software, String uuid, HttpServletRequest request) {
        Software softwareDb = softwareMapper.findById(software.getId());
        if (softwareDb.getStatus() != 6) {
            return JsonResponse.failed("该测评申请无法更新软件认证信息");
        }
        if (!userService.isUserDataScopeByRole(Integer.valueOf(uuid), softwareDb)) {
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
        JsonResponse<String> processJsonRep = commonProcess(processVo, uuid, NodeEnum.CERTIFICATE_REVIEW.getId());
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
    public Integer createSoftware(Software software, String uuid) throws InputException {
        CompanyVo companyVo = companyService.findCompanyByUserUuid(uuid);
        if (companyVo == null || !Objects.equals(companyVo.getStatus(), CompanyStatusConstant.COMPANY_PASSED)) {
            throw new InputException(ErrorCodes.COMPANY_NOT_APPROVED.getMessage());
        }
        // 校验是否签署协议
        Protocol protocol =
            protocolMapper.selectProtocolDesc(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
        if (protocol == null || Objects.equals(protocol.getStatus(), 0)) {
            throw new InputException(ErrorCodes.PROTOCOL_NOT_SIGNED.getMessage());
        }
        initSoftware(software, uuid, companyVo);
        // 获取测评流程
        List<ApprovalPathNode> approvalPath =
            approvalScenarioService.findApprovalPath(software.getTestOrganization(), software.getCpuVendor());
        software.setAsId(approvalPath.get(0).getAsId());
        Integer softwareId = software.getId();
        // 判断是否存在id，如果已经存在id说明是驳回后重新提交，更新数据
        if (softwareId == null || softwareId == 0) {
            softwareMapper.insertSoftware(software);
            softwareId = software.getId();
        } else {
            softwareMapper.recommit(software);
            // 调用审核接口
            ProcessVo processVo = new ProcessVo();
            processVo.setSoftwareId(software.getId());
            processVo.setHandlerResult(1);
            processVo.setTransferredComments("通过");
            commonProcess(processVo, uuid, 1);
            return software.getId();
        }
        // 设置节点表当前信息 1-认证申请
        setCurNode(uuid, softwareId);
        // 设置节点表下一个节点状态 2-方案审核
        Node nextNode = new Node();
        setNextNode(software, nextNode, approvalPath);
        software.setStatus(NodeEnum.PROGRAM_REVIEW.getId());
        software.setReviewer(nextNode.getHandler());
        software.setReviewRole(approvalPath.get(0).getRoleId());
        // 更新软件信息表
        softwareMapper.updateSoftware(software);
        // 发送邮件通知
        sendEmail(software, uuid);
        return softwareId;
    }

    /**
     * 初始化测评流程信息
     * @param software 需要初始化的测评流程
     * @param uuid  用户uuid
     * @param companyVo 用户对应的公司信息
     * @return 测评流程
     */
    private Software initSoftware(Software software, String uuid,CompanyVo companyVo) {
        software.setCompanyName(companyVo.getCompanyName());
        software.setCompanyId(companyVo.getId());
        software.setCompanyCode(companyVo.getCompanyCode());
        // 设置软件信息表当前节点状态 1-认证申请
        software.setStatus(NodeEnum.APPLY.getId());
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
        software.setTestOrgId(CenterEnum.findByName(software.getTestOrganization()));
        // 将算力平台和服务器类型list转为json字符串
        String hashRatePlatform = JSON.toJSON(software.getHashratePlatformList()).toString();
        software.setJsonHashRatePlatform(hashRatePlatform);
        checkParam(software);
        return software;
    }

    private void setCurNode(String userUuid, Integer softwareId) {
        Node curNode = new Node();
        Date date = new Date();
        curNode.setNodeName(NodeEnum.APPLY.getName());
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
        nextNode.setNodeName(NodeEnum.PROGRAM_REVIEW.getName());
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

    public JsonResponse<String> commonProcess(ProcessVo vo, String uuid, Integer nodeStatus) {
        Software software = checkCommonProcess(vo, uuid);
        if (!Objects.equals(software.getStatus(), nodeStatus)) {
            LOGGER.error("审批阶段错误:id:{},status:{}", vo.getSoftwareId(), software.getStatus());
            throw new ParamException("审批阶段错误");
        }
        updateNextSoftware(vo,software, uuid);
        return JsonResponse.success();
    }

    public JsonResponse<String> testingPhase(ProcessVo vo, String uuid) {
        Software software = checkCommonProcess(vo, uuid);
        if (!Objects.equals(software.getStatus(), NodeEnum.TESTING_PHASE.getId())) {
            LOGGER.error("审批阶段错误:id:{},status:{}", vo.getSoftwareId(), software.getStatus());
            throw new ParamException("审批阶段错误");
        }
        if (vo.getHandlerResult() != 1) {
            return JsonResponse.failedResult("非法的审核结果参数");
        }
        AttachmentQuery attachmentQuery = new AttachmentQuery();
        attachmentQuery.setSoftwareId(software.getId());
        attachmentQuery.setFileType("testReport");
        List<AttachmentsVo> attachmentsVos = softwareMapper.getAttachmentsNames(attachmentQuery);
        if (CollectionUtil.isEmpty(attachmentsVos)) {
            return JsonResponse.failed("未上传测试报告");
        }
        updateNextSoftware(vo,software, uuid);
        return JsonResponse.success();
    }

    public JsonResponse<String> reportReview(ProcessVo vo, String uuid) {
        Software software = checkCommonProcess(vo, uuid);
        if (!Objects.equals(software.getStatus(), NodeEnum.REPORT_REVIEW.getId())) {
            LOGGER.error("审批阶段错误:id:{},status:{}", vo.getSoftwareId(), software.getStatus());
            throw new ParamException("审批阶段错误");
        }
        if (!Objects.equals(software.getReviewer(), uuid)) {
            LOGGER.error("审批人员错误:id:{},uuid:{}", vo.getSoftwareId(), uuid);
            throw new ParamException("审批人员错误");
        }
        updateNextSoftware(vo,software, uuid);
        return JsonResponse.success();
    }

    public JsonResponse<String> certificateConfirmation(ProcessVo vo, String uuid) {
        Software software = checkCommonProcess(vo, uuid);
        if (!Objects.equals(software.getStatus(), NodeEnum.CERTIFICATE_CONFIRMATION.getId())) {
            LOGGER.error("审批阶段错误:id:{},status:{}", vo.getSoftwareId(), software.getStatus());
            throw new ParamException("审批阶段错误");
        }
        updateNextSoftware(vo,software, uuid);
        return JsonResponse.success();
    }

    public JsonResponse<String> certificateIssuance(ProcessVo vo, String uuid) throws IOException {
        Software software = checkCommonProcess(vo, uuid);
        if (!Objects.equals(software.getStatus(), NodeEnum.CERTIFICATE_ISSUANCE.getId())) {
            LOGGER.error("审批阶段错误:id:{},status:{}", vo.getSoftwareId(), software.getStatus());
            throw new ParamException("审批阶段错误");
        }
        updateCurNode(vo, uuid);
        getNextNode(vo, software);
        if (software.getStatus() < NodeEnum.FINISHED.getId()) {
            addNextNode(software);
        } else if (software.getStatus().equals(NodeEnum.FINISHED.getId())) {
            generateCertificate(vo.getSoftwareId());
        }
        softwareMapper.updateSoftware(software);
        return JsonResponse.success();
    }

    private Software getNextNode(ProcessVo vo, Software software) {
        Integer nextNodeNumber = software.getStatus();
        switch (vo.getHandlerResult()) {
            case 1:
                if (IntelTestEnum.CPU_VENDOR.getName().equals(software.getCpuVendor()) && nextNodeNumber == 3) {
                    nextNodeNumber = 5;
                } else {
                    nextNodeNumber = software.getStatus() + 1;
                }
                software.setStatus(nextNodeNumber);
                getHandler(nextNodeNumber, software);
                break;
            case 2:
                if (IntelTestEnum.CPU_VENDOR.getName().equals(software.getCpuVendor()) && nextNodeNumber == 5) {
                    nextNodeNumber = 3;
                } else {
                    nextNodeNumber = software.getStatus() - 1;
                }
                software.setStatus(nextNodeNumber);
                software.setAuthenticationStatus(NodeEnum.findById(software.getStatus()) + "已驳回");
                getHandler(nextNodeNumber, software);
                break;
            case 3:
                software.setReviewer(vo.getTransferredUser());
                break;
            default:
                break;
        }
        return software;
    }

    private Software getHandler(int nextNodeNameForNumber, Software software) {
        if (nextNodeNameForNumber == NodeEnum.FINISHED.getId()) {
            return software;
        }
        if (nextNodeNameForNumber == NodeEnum.TESTING_PHASE.getId()) {
            if (IntelTestEnum.CPU_VENDOR.getName().equals(software.getCpuVendor())){
                Node node = nodeMapper.findLatestFinishedNode(software.getId(), NodeEnum.PROGRAM_REVIEW.getId());
                software.setReviewer(node.getHandler());
                software.setReviewRole(RoleEnum.EULER_IC.getRoleId());
            }else {
                software.setReviewer(software.getUserUuid());
                software.setReviewRole(RoleEnum.USER.getRoleId());
            }
            return software;
        }
        if (nextNodeNameForNumber == NodeEnum.APPLY.getId()
            || nextNodeNameForNumber == NodeEnum.CERTIFICATE_CONFIRMATION.getId()) {
            software.setReviewer(software.getUserUuid());
            software.setReviewRole(RoleEnum.USER.getRoleId());
            return software;
        }
        if (nextNodeNameForNumber == NodeEnum.REPORT_REVIEW.getId()) {
            Node node = nodeMapper.findLatestFinishedNode(software.getId(), NodeEnum.PROGRAM_REVIEW.getId());
            software.setReviewer(node.getHandler());
            software.setReviewRole(RoleEnum.EULER_IC.getRoleId());
            return software;
        }
        ApprovalPathNode approvalPathNode =
            approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(software.getAsId(), nextNodeNameForNumber);
        software.setReviewer(approvalPathNode.getUserUuid());
        software.setReviewRole(approvalPathNode.getRoleId());
        return software;
    }

    private Software checkCommonProcess(ProcessVo vo, String uuid) {
        Integer handlerResult = vo.getHandlerResult();
        if (handlerResult < 1 || handlerResult > 3) {
            LOGGER.error("非法的审核结果参数:id:{},result:{}", vo.getSoftwareId(), handlerResult);
            throw new ParamException("非法的审核结果参数:" + vo.getSoftwareId());
        }
        if (StringUtils.isBlank(vo.getTransferredComments())
            || (handlerResult.equals(HandlerResultEnum.TRANSFER.getId()) && StringUtils.isBlank(vo.getTransferredUser()))) {
            LOGGER.error("非法的审核参数:{}", vo.getSoftwareId());
            throw new ParamException("非法的审核参数:" + vo.getSoftwareId());
        }
        Software softwareInDb = softwareMapper.findById(vo.getSoftwareId());
        if (softwareInDb == null) {
            LOGGER.error("审核条目不存在:{}", vo.getSoftwareId());
            throw new ParamException("审核条目不存在:" + vo.getSoftwareId());
        }
        if (!userService.isUserDataScopeByRole(Integer.valueOf(uuid), softwareInDb)) {
            LOGGER.error("非法的审核人:{}", uuid);
            throw new ParamException("非法的审核人:" + uuid);
        }
        if (handlerResult.equals(HandlerResultEnum.TRANSFER.getId())) {
            if (vo.getTransferredUser().equals(uuid)) {
                LOGGER.error("转审人不能为自己:{}", uuid);
                throw new ParamException("转审人不能为自己:" + uuid);
            }
            if (!userService.isUserDataScopeByRole(Integer.valueOf(vo.getTransferredUser()), softwareInDb)) {
                LOGGER.error("转审人没有权限:{}", Integer.valueOf(vo.getTransferredUser()));
                throw new ParamException("转审人没有权限:{}" + Integer.valueOf(vo.getTransferredUser()));
            }
        }
        return softwareInDb;
    }

    private void updateNextSoftware(ProcessVo vo,Software software, String uuid) {
        updateCurNode(vo, uuid);
        getNextNode(vo, software);
        addNextNode(software);
        softwareMapper.updateSoftware(software);
    }

    private void updateCurNode(ProcessVo vo, String Uuid) {
        // 当前节点更新
        Node latestNode = nodeMapper.findLatestNodeById(vo.getSoftwareId());
        latestNode.setHandler(Uuid);
        latestNode.setHandlerResult(vo.getHandlerResult());
        latestNode.setTransferredComments(vo.getTransferredComments());
        latestNode.setHandlerTime(new Date());
        nodeMapper.updateNodeById(latestNode);
    }

    private void addNextNode(Software software) {
        Node node = new Node();
        node.setNodeName(NodeEnum.findById(software.getStatus()));
        node.setSoftwareId(software.getId());
        node.setHandlerResult(0);
        node.setStatus(software.getStatus());
        node.setUpdateTime(new Date());
        node.setHandler(software.getReviewer());
        nodeMapper.insertNode(node);
    }

    @Override
    public List<SimpleUserVo> transferredUserList(Integer softwareId, String uuid) {
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
        return userVos.stream().filter(item -> !item.getUuid().equals(uuid)).collect(Collectors.toList());
    }

    @Override
    public PageResult<SoftwareListVo> getSoftwareList(SelectSoftwareVo selectSoftwareVo, String uuid) {
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        int pageSize = selectSoftwareVo.getPageSize();
        int pageNum = selectSoftwareVo.getPageNum();
        int offset = (selectSoftwareVo.getPageNum() - 1) * selectSoftwareVo.getPageSize();
        Company company = companyMapper.findRegisterSuccessCompanyByUserUuid(uuid);
        if (ObjectUtils.isEmpty(company)) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        // 通过所属公司名获取全部认证列表
        selectSoftware.setCompanyName(company.getCompanyName());
        // 通过uuid直接查询该用户下所有认证列表
        selectSoftware.setApplicant(uuid);
        List<SoftwareListVo> currentSoftwareList = softwareMapper.getSoftwareList(offset, pageSize, selectSoftware);
        Long total = softwareMapper.countSoftwareList(selectSoftware);
        processFields(currentSoftwareList, uuid);
        return new PageResult<>(currentSoftwareList, total, pageNum, pageSize);
    }

    private void processFields(List<SoftwareListVo> currentSoftwareList, String userUuid) {
        currentSoftwareList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAuthenticationStatus())) {
                item.setStatus(item.getAuthenticationStatus());
            } else {
                item.setStatus(NodeEnum.findById(Integer.parseInt(item.getStatus())));
            }
            item.setReviewerName(accountService.getUserName(item.getReviewer()));
            item.setApplicantName(accountService.getUserName(item.getApplicant()));
        });
        currentSoftwareList.forEach(item -> {
            if (userUuid.equals(item.getReviewer())) {
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
    public PageResult<SoftwareListVo> getReviewSoftwareList(SelectSoftwareVo selectSoftwareVo, String uuid) {
        SelectSoftware selectSoftware = new SelectSoftware();
        BeanUtils.copyProperties(selectSoftwareVo, selectSoftware);
        selectSoftware.setDataScope(userService.getUserAllDateScope(Integer.valueOf(uuid)));
        int pageSize = selectSoftwareVo.getPageSize();
        int pageNum = selectSoftwareVo.getPageNum();
        int offset = (selectSoftwareVo.getPageNum() - 1) * selectSoftwareVo.getPageSize();
        List<SoftwareListVo> reviewSoftwareList =
            softwareMapper.getReviewSoftwareList(offset, pageSize, selectSoftware);
        Long total = softwareMapper.countReviewSoftwareList(selectSoftware);
        Map<Integer, List<Integer>> roleMap = userService.getUserAllRole(Integer.valueOf(uuid));
        updateSoftwareListStatus(reviewSoftwareList, roleMap);
        return new PageResult<>(reviewSoftwareList, total, pageNum, pageSize);
    }

    private void updateSoftwareListStatus(List<SoftwareListVo> softwareList, Map<Integer, List<Integer>> roleMap) {
        softwareList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAuthenticationStatus())) {
                item.setStatus(item.getAuthenticationStatus());
            } else {
                item.setStatus(NodeEnum.findById(Integer.parseInt(item.getStatus())));
            }
            item.setReviewerName(accountService.getUserName(item.getReviewer()));
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
        IPage<AuditRecordsVo> page, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限查询该测评申请审核信息");
        }
        IPage<AuditRecordsVo> iPage = softwareMapper.getAuditRecordsListPage(softwareId, nodeName, page);
        iPage.getRecords().forEach(item -> {
            item.setHandlerName(accountService.getUserName(item.getHandler()));
        });
        return iPage;
    }

    @Override
    public CertificateInfoVo certificateInfo(Integer softwareId, String uuid) {
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限查询该测评申请证书信息");
        }
        return softwareMapper.certificateInfo(softwareId);
    }

    @Override
    public List<AuditRecordsVo> getNodeList(Integer softwareId, String uuid) {
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
            }).toList();
        // 组合数据
        filterLatestNodes.addAll(unFinishedNodes);
        checkPartnerNode(filterLatestNodes, software);
        filterLatestNodes.parallelStream().forEach(item -> {
            item.setHandlerName(accountService.getUserName(item.getHandler()));
        });
        return filterLatestNodes.stream().sorted(Comparator.comparing(AuditRecordsVo::getStatus))
            .collect(Collectors.toList());
    }

    private List<AuditRecordsVo> checkPartnerNode(List<AuditRecordsVo> latestNodes, Software software) {
        boolean node3 =
            latestNodes.stream().anyMatch(item -> item.getNodeName().equals(NodeEnum.TESTING_PHASE.getName()));
        boolean node7 = latestNodes.stream()
            .anyMatch(item -> item.getNodeName().equals(NodeEnum.CERTIFICATE_CONFIRMATION.getName()));
        if (!node3) {
            AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
            auditRecordsVo.setNodeName(NodeEnum.TESTING_PHASE.getName());
            auditRecordsVo.setHandler(software.getUserUuid());
            auditRecordsVo.setStatus(3);
            latestNodes.add(auditRecordsVo);
        }
        if (!node7) {
            AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
            auditRecordsVo.setNodeName(NodeEnum.CERTIFICATE_CONFIRMATION.getName());
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
                    .sorted(Comparator.comparing(AuditRecordsVo::getHandlerTime).reversed()).toList().get(0);
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
        String uuid) throws InputException, TestReportExceedMaxAmountException {
        Software software = softwareMapper.findById(softwareId);
        if (!uuid.equals(software.getReviewer())) {
            return JsonResponse.failed("不是当前处理人");
        }
        FileModel fileModel = fileUtils.uploadFile(file, softwareId, fileTypeCode, fileType, uuid);
        AttachmentQuery attachmentQuery = new AttachmentQuery();
        attachmentQuery.setSoftwareId(softwareId);
        attachmentQuery.setFileType(fileType);
        List<AttachmentsVo> attachmentsVos = softwareMapper.getAttachmentsNames(attachmentQuery);
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
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException("无权限获取该测评申请文件");
        }
        AttachmentQuery attachmentQuery = new AttachmentQuery();
        attachmentQuery.setSoftwareId(softwareId);
        attachmentQuery.setFileType(fileType);
        return softwareMapper.getAttachmentsNames(attachmentQuery);
    }

    @Override
    public void downloadAttachments(String fileId, HttpServletResponse response, String uuid)
        throws UnsupportedEncodingException, InputException {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
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
    public void previewCertificate(Integer softwareId, HttpServletResponse response) throws IOException {
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
