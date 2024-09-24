/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.*;
import com.huawei.it.euler.model.constant.CompanyStatusConstant;
import com.huawei.it.euler.model.entity.*;
import com.huawei.it.euler.model.enumeration.ErrorCodes;
import com.huawei.it.euler.model.enumeration.HandlerResultEnum;
import com.huawei.it.euler.model.enumeration.NodeEnum;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.ApprovalPathNodeService;
import com.huawei.it.euler.service.ApprovalScenarioService;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.CertificateGenerationUtils;

/**
 * {@link SoftwareServiceImpl}测试类
 */
@ExtendWith(MockitoExtension.class)
class SoftwareServiceImplTest {
    private static final Integer USER_ID = 1;

    private static final String USER_UUID = "1";

    private static  final String USER_UUID_TRANSFORM = "2";

    private static final Integer SOFTWARE_ID = 1;

    private static final String TEST_SOFTWARE_ID = "1";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SoftwareMapper softwareMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserService userService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CompanyService companyService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CompanyMapper companyMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProtocolMapper protocolMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NodeMapper nodeMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApprovalPathNodeService approvalPathNodeService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MasterDataMapper masterDataMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ApprovalScenarioService approvalScenarioService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CertificateGenerationUtils certificateGenerationUtils;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RoleMapper roleMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AccountService accountService;

    @InjectMocks
    private SoftwareServiceImpl softwareServiceImpl;

    @Test
    @DisplayName("根据id查询软件成功")
    void testFindByIdSuccess() {
        Software software = initExpectResultSoftware();

        // setup
        Mockito.when(userService.isUserPermission(any(), any())).thenReturn(true);
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initResultSoftware());

        // run
        Software result = softwareServiceImpl.findById(USER_ID, USER_UUID);

        // verify
        Assertions.assertEquals(software, result);
    }

    @Test
    @DisplayName("根据id查询软件失败")
    void testFindByIdFail() {
        // setup
        Mockito.when(userService.isUserPermission(any(), any())).thenReturn(false);

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareServiceImpl.findById(USER_ID, USER_UUID);
        });
        // verify
        Assertions.assertEquals(ErrorCodes.UNAUTHORIZED.getMessage(), paramException.getMessage());
    }

    @Test
    @DisplayName("测评流程申请成功")
    void testCreateSoftwareSuccess() throws Exception {
        Software software = initTestSoftware();

        // setup
        Mockito.when(companyService.findCompanyByUserUuid(any())).thenReturn(initResultCompanyVo());
        Mockito.when(masterDataMapper.findOsByNameAndVersion(any(), any())).thenReturn(1);
        Mockito.when(masterDataMapper.findPlatformByParam(any(), any(), any())).thenReturn(1);
        Mockito.when(masterDataMapper.findProduct(any(), any())).thenReturn(1);
        Mockito.when(approvalScenarioService.findApprovalPath(any(), any())).thenReturn(initResultApprovalScenario());
        Mockito.when(protocolMapper.selectProtocolDesc(anyInt(), any())).thenReturn(initResultProtocol());
        Mockito.when(softwareMapper.insertSoftware(any())).thenReturn(initResultSoftware().getId());

        // run
        Integer id = softwareServiceImpl.createSoftware(software, USER_UUID);

        // verify
        Assertions.assertEquals(1, id);
    }

    @Test
    @DisplayName("测评通用流程成功")
    void testCommonProcess() {
        ProcessVo processVo = initAcceptProcess();
        Node node = new Node();
        ApprovalPathNode approvalPathNode = new ApprovalPathNode();

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initResultSoftware());
        Mockito.when(userService.isUserDataScopeByRole(anyInt(), any())).thenReturn(true);
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).updateNodeById(any());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(), anyInt())).thenReturn(node);
        Mockito.when(approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(anyInt(), anyInt()))
            .thenReturn(approvalPathNode);
        doNothing().when(nodeMapper).insertNode(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        String result =
            softwareServiceImpl.commonProcess(processVo, USER_UUID, NodeEnum.PROGRAM_REVIEW.getId());

        // verify
        Assertions.assertEquals(TEST_SOFTWARE_ID, result);
    }

    @Test
    @DisplayName("测评测试阶段成功")
    void testTestingPhase() {
        ProcessVo processVo = initAcceptProcess();
        Node node = new Node();
        ApprovalPathNode approvalPathNode = new ApprovalPathNode();
        Software software = initResultSoftware();
        software.setStatus(NodeEnum.TESTING_PHASE.getId());

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(userService.isUserDataScopeByRole(anyInt(), any())).thenReturn(true);
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).updateNodeById(any());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(), anyInt())).thenReturn(node);
        Mockito.when(approvalPathNodeService.findANodeByAsIdAndSoftwareStatus(anyInt(), anyInt()))
            .thenReturn(approvalPathNode);
        doNothing().when(nodeMapper).insertNode(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        JsonResponse<String> result = softwareServiceImpl.testingPhase(processVo, USER_UUID);

        // verify
        Assertions.assertEquals(JsonResponse.success(), result);
    }

    @Test
    @DisplayName("测评报告复审成功")
    void testReportReview() {
        ProcessVo processVo = initAcceptProcess();
        Node node = new Node();
        Software software = initResultSoftware();
        software.setStatus(NodeEnum.REPORT_REVIEW.getId());

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(userService.isUserDataScopeByRole(anyInt(), any())).thenReturn(true);
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).updateNodeById(any());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(), anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).insertNode(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        JsonResponse<String> result = softwareServiceImpl.reportReview(processVo, USER_UUID);

        // verify
        Assertions.assertEquals(JsonResponse.success(), result);
    }


    @Test
    @DisplayName("测评证书确认成功")
    void testCertificateConfirmation() {
        ProcessVo processVo = initAcceptProcess();
        Node node = new Node();
        Software software = initResultSoftware();
        software.setStatus(NodeEnum.CERTIFICATE_CONFIRMATION.getId());

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(userService.isUserDataScopeByRole(anyInt(), any())).thenReturn(true);
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).updateNodeById(any());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(), anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).insertNode(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        JsonResponse<String> result = softwareServiceImpl.certificateConfirmation(processVo, USER_UUID);

        // verify
        Assertions.assertEquals(JsonResponse.success(), result);
    }

    @Test
    @DisplayName("测评证书发放成功")
    void testCertificateIssuance() throws IOException {
        ProcessVo processVo = initAcceptProcess();
        Node node = new Node();
        Software software = initResultSoftware();
        software.setStatus(NodeEnum.CERTIFICATE_ISSUANCE.getId());

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(userService.isUserDataScopeByRole(anyInt(), any())).thenReturn(true);
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(node);
        doNothing().when(nodeMapper).updateNodeById(any());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(), anyInt())).thenReturn(node);
        doNothing().when(certificateGenerationUtils).generateCertificate(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        JsonResponse<String> result = softwareServiceImpl.certificateIssuance(processVo, USER_UUID);

        // verify
        Assertions.assertEquals(JsonResponse.success(), result);
    }

    @Test
    @DisplayName("测试转移用户列表")
    void testTransferredUserList() {
        SimpleUserVo simpleUserVo = new SimpleUserVo();
        simpleUserVo.setUuid(USER_UUID_TRANSFORM);
        simpleUserVo.setUsername("测试用户");
        List<SimpleUserVo> simpleUserVos = List.of(simpleUserVo);

        UserInfo userInfo = new UserInfo();
        userInfo.setUuid(USER_UUID_TRANSFORM);
        userInfo.setUserName("测试用户");
        List<UserInfo> eulerUsers = List.of(userInfo);
        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initResultSoftware());
        Mockito.when(roleMapper.findUserByRole(any(), any())).thenReturn(List.of(USER_UUID,USER_UUID_TRANSFORM));
        Mockito.when(accountService.getUserInfoList(any())).thenReturn(eulerUsers);

        // run
        List<SimpleUserVo> result = softwareServiceImpl.transferredUserList(1,USER_UUID);

        // verify
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(simpleUserVos, result);
    }

    @Test
    @DisplayName("测试获取软件列表")
     void testGetSoftwareList(){
        SelectSoftwareVo selectSoftwareVo = new SelectSoftwareVo();
        selectSoftwareVo.setPageNum(1);
        selectSoftwareVo.setPageSize(10);
        List<SoftwareListVo> currentSoftwareList = List.of(initTestSoftwareListVo());
        // setup
        Mockito.when(companyMapper.findRegisterSuccessCompanyByUserUuid(any())).thenReturn(initResultCompany());
        Mockito.when(softwareMapper.getSoftwareList(anyInt(),anyInt(),any())).thenReturn(currentSoftwareList);
        Mockito.when(softwareMapper.countSoftwareList(any())).thenReturn(1L);
        // run
        PageResult<SoftwareListVo> result = softwareServiceImpl.getSoftwareList(selectSoftwareVo,USER_UUID);

        // verify
        Assertions.assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("测试获取待测评软件列表")
    void testGetReviewSoftwareList() {
        SelectSoftwareVo selectSoftwareVo = new SelectSoftwareVo();
        selectSoftwareVo.setPageNum(1);
        selectSoftwareVo.setPageSize(10);
        List<SoftwareListVo> currentSoftwareList = List.of(initTestSoftwareListVo());
        // setup
        Mockito.when(companyMapper.findRegisterSuccessCompanyByUserUuid(any())).thenReturn(initResultCompany());
        Mockito.when(softwareMapper.getReviewSoftwareList(anyInt(),anyInt(),any())).thenReturn(currentSoftwareList);
        Mockito.when(softwareMapper.countReviewSoftwareList(any())).thenReturn(1L);
        // run
        PageResult<SoftwareListVo> result = softwareServiceImpl.getReviewSoftwareList(selectSoftwareVo,USER_UUID);

        // verify
        Assertions.assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("测试获取审核记录列表")
    void testGetAuditRecordsList(){
        AuditRecordsVo auditRecordsVo = new AuditRecordsVo();
        List<AuditRecordsVo> auditRecordsVos = List.of(auditRecordsVo);
        // setup
        Mockito.when(softwareMapper.getAuditRecordsList(anyInt())).thenReturn(auditRecordsVos);

        // run
        List<AuditRecordsVo> result = softwareServiceImpl.getAuditRecordsList(1);

        // verify
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("测试删除软件")
    void testDeleteSoftware(){
        Software software = initResultSoftware();
        software.setStatus(NodeEnum.APPLY.getId());

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(softwareMapper.deleteSoftware(anyInt())).thenReturn(TEST_SOFTWARE_ID);

        // run
        String result = softwareServiceImpl.deleteSoftware(SOFTWARE_ID,USER_UUID);

        // verify
        Assertions.assertEquals(TEST_SOFTWARE_ID, result);
    }

    @Test
    @DisplayName("删除软件失败，状态错误")
    void testDeleteSoftwareFail(){
        Software software = initResultSoftware();

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareServiceImpl.deleteSoftware(SOFTWARE_ID,USER_UUID);
        });

        // verify
        Assertions.assertEquals(ErrorCodes.APPROVAL_PROCESS_STATUS_ERROR.getMessage(), paramException.getMessage());
    }

    @Test
    @DisplayName("删除软件失败，用户错误")
    void testDeleteSoftwareFail2(){
        Software software = initResultSoftware();
        software.setUserUuid(USER_UUID_TRANSFORM);
        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareServiceImpl.deleteSoftware(SOFTWARE_ID,USER_UUID);
        });

        // verify
        Assertions.assertEquals(ErrorCodes.UNAUTHORIZED_OPERATION.getMessage(), paramException.getMessage());
    }

    @Test
    @DisplayName("测试撤回申请")
    void testWithdrawSoftware(){

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initResultSoftware());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(),anyInt())).thenReturn(initResultNode());
        Mockito.when(nodeMapper.findLatestNodeById(anyInt())).thenReturn(new Node());
        doNothing().when(nodeMapper).updateNodeById(any());
        doNothing().when(nodeMapper).insertNode(any());
        doNothing().when(softwareMapper).updateSoftware(any());

        // run
        String result = softwareServiceImpl.withdrawSoftware(initWithdrawProcess(),USER_UUID);
        // verify
        Assertions.assertEquals(TEST_SOFTWARE_ID, result);
    }

    @Test
    @DisplayName("撤回申失败，用户错误")
    void testWithdrawSoftwareFail(){

        // setup
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initResultSoftware());
        Mockito.when(nodeMapper.findLatestFinishedNode(anyInt(),anyInt())).thenReturn(initResultNode());

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareServiceImpl.withdrawSoftware(initWithdrawProcess(),USER_UUID_TRANSFORM);
        });

        // verify
        Assertions.assertEquals(ErrorCodes.UNAUTHORIZED_OPERATION.getMessage(), paramException.getMessage());

    }

    private SoftwareListVo initTestSoftwareListVo() {
        SoftwareListVo softwareVo = new SoftwareListVo();
        softwareVo.setProductVersion("test版本号");
        softwareVo.setOsName("openEuler");
        softwareVo.setOsVersion("20.03");
        softwareVo.setStatus(String.valueOf(NodeEnum.FINISHED.getId()));
        return softwareVo;
    }

    private Software initTestSoftware() {
        Software software = new Software();
        software.setProductName("测试用例名称");
        software.setUsageScenesDesc("测试用例描述");
        software.setProductVersion("test版本号");
        software.setCompanyName("test公司");
        ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
        computingPlatformVo.setPlatformName("兆芯");
        computingPlatformVo.setServerProvider("清华同方");
        computingPlatformVo.setServerTypes(List.of("超强Z520-M1"));
        software.setHashratePlatformList(List.of(computingPlatformVo));
        software.setOsName("openEuler");
        software.setOsVersion("20.03");
        software.setProductFunctionDesc("测试用例描述");
        software.setProductType("硬件/DIMM");
        software.setTestOrganization("openEuler社区");
        return software;
    }

    private Software initResultSoftware() {
        Software software = new Software();
        software.setId(1);
        software.setCompanyName("test公司");
        ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
        computingPlatformVo.setPlatformName("兆芯");
        computingPlatformVo.setServerProvider("清华同方");
        computingPlatformVo.setServerTypes(List.of("超强Z520-M1"));
        software.setHashratePlatformList(List.of(computingPlatformVo));
        software.setJsonHashRatePlatform(
            "[{\"serverTypes\":[\"超强Z520-M1\"],\"platformName\":\"兆芯\",\"serverProvider\":\"清华同方\"}]");
        software.setOsName("openEuler");
        software.setOsVersion("20.03");
        software.setProductFunctionDesc("测试用例描述");
        software.setProductType("硬件/DIMM");
        software.setTestOrganization("openEuler社区");
        software.setStatus(NodeEnum.PROGRAM_REVIEW.getId());
        software.setReviewer(USER_UUID);
        software.setUserUuid(USER_UUID);
        return software;
    }

    private Software initExpectResultSoftware() {
        Software software = initResultSoftware();
        software.setId(1);
        software.setCompanyName("test公司");
        ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
        computingPlatformVo.setPlatformName("兆芯");
        computingPlatformVo.setServerProvider("清华同方");
        computingPlatformVo.setServerTypes(List.of("超强Z520-M1"));
        software.setHashratePlatformList(List.of(computingPlatformVo));
        software.setOsName("openEuler");
        software.setOsVersion("20.03");
        software.setProductFunctionDesc("测试用例描述");
        software.setProductType("硬件/DIMM");
        software.setTestOrganization("openEuler社区");
        software.setPlatforms(List.of("兆芯/清华同方/超强Z520-M1"));
        return software;
    }

    private CompanyVo initResultCompanyVo() {
        CompanyVo companyVo = new CompanyVo();
        companyVo.setId(1);
        companyVo.setCompanyName("清华同方");
        companyVo.setCompanyCode(1);
        companyVo.setStatus(CompanyStatusConstant.COMPANY_PASSED);
        return companyVo;
    }

    private Company initResultCompany() {
        Company company = new Company();
        company.setId(1);
        company.setCompanyName("清华同方");
        company.setCompanyCode(1);
        company.setStatus(CompanyStatusConstant.COMPANY_PASSED);
        return company;
    }

    private Protocol initResultProtocol() {
        Protocol protocol = new Protocol();
        protocol.setId(1);
        protocol.setStatus(1);
        return protocol;
    }

    private List<ApprovalPathNode> initResultApprovalScenario() {
        ApprovalPathNode approvalPathNode = new ApprovalPathNode();
        approvalPathNode.setAsId(1);
        return List.of(approvalPathNode);
    }

    private ProcessVo initAcceptProcess() {
        ProcessVo process = new ProcessVo();
        process.setSoftwareId(1);
        process.setHandlerResult(HandlerResultEnum.ACCEPT.getId());
        process.setTransferredComments("通过");
        return process;
    }

    private ProcessVo initWithdrawProcess() {
        ProcessVo process = new ProcessVo();
        process.setSoftwareId(1);
        process.setHandlerResult(HandlerResultEnum.WITHDRAW.getId());
        return process;
    }

    private Node initResultNode() {
        Node node = new Node();
        node.setId(1);
        node.setHandler(USER_UUID);
        return node;
    }
    private ApprovalPathNode initResultApprovalPathNode() {
        ApprovalPathNode approvalPathNode = new ApprovalPathNode();
        approvalPathNode.setAsId(1);
        approvalPathNode.setUserUuid(USER_UUID);
        approvalPathNode.setRoleId(1);
        return approvalPathNode;
    }
}