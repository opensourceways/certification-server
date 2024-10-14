/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.service.HardwareBoardCardApplicationService;
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
public class HardwareBoardCardApplicationServiceTest {

    private static final String USER_UUID = "1";

    private static final int HARDWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardService boardCardService;

    @InjectMocks
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @Test
    @DisplayName("板卡插入成功")
    void testInsertSuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.insert(boardCard)).thenReturn(boardCard);

        HardwareBoardCard insert = boardCardApplicationService.insert(boardCard, USER_UUID);

        Assertions.assertEquals(1, insert.getId());
    }

    @Test
    @DisplayName("板卡插入失败-已存在")
    void testInsertFailedOfExist() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        Mockito.when(boardCardService.exist(any())).thenReturn(true);

        ParamException paramException = assertThrows(ParamException.class,
                () -> boardCardApplicationService.insert(boardCard, USER_UUID));
        Assertions.assertTrue(paramException.getMessage().matches("当前板卡(.*)已存在！"));
    }

    @Test
    @DisplayName("板卡插入失败-插入异常")
    void testInsertFailedOfInsert() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.insert(boardCard)).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.insert(boardCard, USER_UUID));
        Assertions.assertTrue(businessException.getMessage().matches("当前板卡(.*)申请失败！"));
    }

    @Test
    @DisplayName("板卡批量插入成功")
    void testBatchInsertSuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Mockito.when(boardCardService.exist(any())).thenReturn(false);
        Mockito.when(boardCardService.batchInsert(boardCardList)).thenReturn(true);

        List<HardwareBoardCard> batchInsert = boardCardApplicationService.batchInsert(boardCardList, USER_UUID);
        Assertions.assertEquals(1, batchInsert.size());
    }

    @Test
    @DisplayName("板卡单笔查询成功")
    void testGetByIdSuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);

        HardwareBoardCard byId = boardCardApplicationService.getById(HARDWARE_ID);
        Assertions.assertEquals(boardCard, byId);
    }

    @Test
    @DisplayName("板卡分页查询成功")
    void testGetPageSuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);

        Page<HardwareBoardCard> page = new Page<>(1, 10);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        page.setRecords(boardCardList);

        Mockito.when(boardCardService.getPage(any())).thenReturn(page);

        Page<HardwareBoardCard> boardCardPage = boardCardApplicationService.getPage(new HardwareBoardCardSelectVO());
        Assertions.assertEquals(boardCardList, boardCardPage.getRecords());
    }

    @Test
    @DisplayName("板卡删除成功")
    void testDeleteSuccess(){
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setRefCount(0);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);
        Mockito.doAnswer(invocation -> {
            boardCard.delete();
            return boardCard;
        }).when(boardCardService).updateById(boardCard);

        boardCardApplicationService.delete(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("板卡删除失败-数据不存在")
    void testDeleteFailedUnExist(){
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> boardCardApplicationService.delete(approvalNode));

        Assertions.assertEquals("当前数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("板卡删除失败-状态不匹配")
    void testDeleteFailedStatus() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.delete(approvalNode));

        Assertions.assertEquals("当前数据状态无法进行删除操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("板卡删除失败-被占用")
    void testDeleteFailedUsed() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setRefCount(1);

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.delete(approvalNode));

        Assertions.assertEquals("当前板卡关联整机，无法删除！", businessException.getMessage());
    }

    @Test
    @DisplayName("板卡编辑成功")
    void testEditSuccess(){
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setBoardModel("1");
        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);
        boardCard.setBoardModel("2");
        boardCardApplicationService.edit(boardCard);
        Assertions.assertEquals("2",boardCard.getBoardModel());
    }

    @Test
    @DisplayName("板卡申请成功")
    void testApplySuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);
        Mockito.doAnswer(invocation -> {
            boardCard.apply();
            return boardCard;
        }).when(boardCardService).apply(boardCard);

        boardCardApplicationService.apply(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("板卡申请失败-数据不存在")
    void testApplyFailedUnExist() {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> boardCardApplicationService.apply(approvalNode));

        Assertions.assertEquals("当前板卡数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("板卡申请失败-状态不匹配")
    void testApplyFailedStatus() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.apply(approvalNode));

        Assertions.assertEquals("当前板卡数据状态无法进行申请操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("板卡审批成功-通过")
    void testApprovalPassSuccess() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);
        Mockito.doAnswer(invocation -> {
            boardCard.pass();
            return boardCard;
        }).when(boardCardService).pass(boardCard);

        boardCardApplicationService.approval(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), boardCard.getStatus());
    }

    @Test
    @DisplayName("板卡审批成功-驳回")
    void testApprovalRejectSuccess(){
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_REJECT.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);
        Mockito.doAnswer(invocation -> {
            boardCard.reject();
            return boardCard;
        }).when(boardCardService).reject(boardCard);

        boardCardApplicationService.approval(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("板卡审批失败-数据不存在")
    void testApprovalFailedUnExist() {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(null);

        ParamException paramException = assertThrows(ParamException.class,
                () -> boardCardApplicationService.approval(approvalNode));

        Assertions.assertEquals("当前板卡数据不存在！", paramException.getMessage());
    }

    @Test
    @DisplayName("板卡申请失败-状态不匹配")
    void testApprovalFailedStatus() {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());

        Mockito.when(boardCardService.getById(any())).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.approval(approvalNode));

        Assertions.assertEquals("当前板卡数据状态无法进行审批操作！", businessException.getMessage());
    }
}