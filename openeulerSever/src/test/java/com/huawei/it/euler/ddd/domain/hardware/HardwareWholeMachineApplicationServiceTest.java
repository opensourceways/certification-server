/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.service.HardwareWholeMachineApplicationService;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.exception.ParamException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HardwareWholeMachineApplicationServiceTest {

    private static final String USER_UUID = "1";

    private static final int HARDWARE_ID = 1;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardService boardCardService;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareWholeMachineService wholeMachineService;

    @InjectMocks
    private HardwareWholeMachineApplicationService wholeMachineApplicationService;

    @Test
    @DisplayName("整机插入成功")
    void testInsertSuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        Mockito.when(wholeMachineService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.insert(any())).thenReturn(true);
        Mockito.when(wholeMachineService.insert(wholeMachine)).thenReturn(true);

        HardwareWholeMachine insert = wholeMachineApplicationService.insert(wholeMachine, USER_UUID);

        Assertions.assertEquals(1, insert.getId());
    }

    @Test
    @DisplayName("整机插入失败-已存在")
    void testInsertFailedOfExist() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        Mockito.when(wholeMachineService.exist(any())).thenReturn(true);

        ParamException paramException = assertThrows(ParamException.class,
                () -> wholeMachineApplicationService.insert(wholeMachine, USER_UUID));
        Assertions.assertTrue(paramException.getMessage().matches("当前整机(.*)已存在！"));
    }

    @Test
    @DisplayName("整机插入失败-插入异常")
    void testInsertFailedOfInsert() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        Mockito.when(wholeMachineService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.insert(any())).thenReturn(true);
        Mockito.when(wholeMachineService.insert(wholeMachine)).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.insert(wholeMachine, USER_UUID));
        Assertions.assertTrue(businessException.getMessage().matches("当前整机(.*)申请失败！"));
    }

    @Test
    @DisplayName("整机批量插入成功")
    void testBatchInsertSuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);
        
        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);

        Mockito.when(wholeMachineService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.insert(any())).thenReturn(true);
        Mockito.when(wholeMachineService.batchInsert(wholeMachineList)).thenReturn(true);

        List<HardwareWholeMachine> batchInsert = wholeMachineApplicationService.batchInsert(wholeMachineList, USER_UUID);
        Assertions.assertEquals(1, batchInsert.size());
    }

    @Test
    @DisplayName("整机单笔查询成功")
    void testGetByIdSuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);

        HardwareWholeMachine byId = wholeMachineApplicationService.getById(HARDWARE_ID);
        Assertions.assertEquals(wholeMachine, byId);
    }

    @Test
    @DisplayName("整机分页查询成功")
    void testGetPageSuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);

        Page<HardwareWholeMachine> page = new Page<>(1, 10);
        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);
        page.setRecords(wholeMachineList);

        Mockito.when(wholeMachineService.getPage(any())).thenReturn(page);

        Page<HardwareWholeMachine> wholeMachinePage = wholeMachineApplicationService.getPage(new HardwareWholeMachineSelectVO());
        Assertions.assertEquals(wholeMachineList, wholeMachinePage.getRecords());
    }

    @Test
    @DisplayName("整机删除成功")
    void testDeleteSuccess(){
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setBoardCards(String.valueOf(HARDWARE_ID));

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);
        Mockito.doAnswer(invocation -> {
            wholeMachine.delete();
            return wholeMachine;
        }).when(wholeMachineService).updateById(wholeMachine);

        wholeMachineApplicationService.delete(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("整机删除失败-数据不存在")
    void testDeleteFailedUnExist(){
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> wholeMachineApplicationService.delete(approvalNode));

        Assertions.assertEquals("当前整机数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("整机删除失败-状态不匹配")
    void testDeleteFailedStatus() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.delete(approvalNode));

        Assertions.assertEquals("当前整机数据状态无法进行删除操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("整机编辑成功")
    void testEditSuccess(){
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setOsVersion("1");

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);
        wholeMachine.setOsVersion("2");
        wholeMachineApplicationService.edit(wholeMachine);
        Assertions.assertEquals("2",wholeMachine.getOsVersion());
    }

    @Test
    @DisplayName("整机申请成功")
    void testApplySuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);
        Mockito.doAnswer(invocation -> {
            wholeMachine.apply();
            return wholeMachine;
        }).when(wholeMachineService).apply(wholeMachine);

        wholeMachineApplicationService.apply(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("整机申请失败-数据不存在")
    void testApplyFailedUnExist() {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> wholeMachineApplicationService.apply(approvalNode));

        Assertions.assertEquals("当前整机数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("整机申请失败-状态不匹配")
    void testApplyFailedStatus() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.apply(approvalNode));

        Assertions.assertEquals("当前整机数据状态无法进行申请操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("整机审批成功-通过")
    void testApprovalPassSuccess() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);
        Mockito.doAnswer(invocation -> {
            wholeMachine.pass();
            return wholeMachine;
        }).when(wholeMachineService).pass(wholeMachine);

        wholeMachineApplicationService.approval(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), wholeMachine.getStatus());
    }

    @Test
    @DisplayName("整机审批成功-驳回")
    void testApprovalRejectSuccess(){
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);
        Mockito.doAnswer(invocation -> {
            wholeMachine.reject();
            return wholeMachine;
        }).when(wholeMachineService).reject(wholeMachine);

        wholeMachineApplicationService.approval(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("整机审批失败-数据不存在")
    void testApprovalFailedUnExist() {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> wholeMachineApplicationService.approval(approvalNode));

        Assertions.assertEquals("当前整机数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("整机审批失败-状态不匹配")
    void testApprovalFailedStatus() {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCardList(boardCardList);

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(wholeMachineService.getById(any())).thenReturn(wholeMachine);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.approval(approvalNode));

        Assertions.assertEquals("当前整机数据状态无法进行审批操作！", businessException.getMessage());
    }
}