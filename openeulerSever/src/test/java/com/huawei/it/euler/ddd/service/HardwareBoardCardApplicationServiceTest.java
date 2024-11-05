/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.exception.BusinessException;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HardwareBoardCardApplicationServiceTest {

    private static final String USER_UUID = "1";

    private static final int HARDWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareApprovalNodeRepositoryImpl approvalNodeRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareFactory hardwareFactory;

    @InjectMocks
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @Test
    @DisplayName("板卡插入成功")
    void testInsertSuccess() {
        HardwareBoardCardAddCommand addCommand = getBoardCardAddCommand();
        HardwareBoardCard boardCard = getBoardCard(null);

        Mockito.when(hardwareFactory.createBoardCard(addCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(null);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);

        InsertResponse insert = boardCardApplicationService.insert(addCommand, USER_UUID);

        Assertions.assertTrue(insert.isSuccess());
    }

    @Test
    @DisplayName("板卡插入失败-已存在")
    void testInsertFailedExist() {
        HardwareBoardCardAddCommand addCommand = getBoardCardAddCommand();
        HardwareBoardCard boardCard = getBoardCard(null);

        Mockito.when(hardwareFactory.createBoardCard(addCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);

        InsertResponse insert = boardCardApplicationService.insert(addCommand, USER_UUID);

        Assertions.assertFalse(insert.isSuccess());
        Assertions.assertEquals("当前板卡已存在！", insert.getMessage());
    }

    @Test
    @DisplayName("批量插入成功")
    void testBatchInsertSuccess() {
        HardwareBoardCardAddCommand addCommand = getBoardCardAddCommand();
        List<HardwareBoardCardAddCommand> addCommandList = new ArrayList<>();
        addCommandList.add(addCommand);

        HardwareBoardCard boardCard = getBoardCard(null);

        Mockito.when(hardwareFactory.createBoardCard(addCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(null);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);

        BatchInsertResponse batchInsertResponse = boardCardApplicationService.batchInsert(addCommandList, USER_UUID);

        Assertions.assertEquals(1, batchInsertResponse.getSuccessCount());
        Assertions.assertEquals(0, batchInsertResponse.getFailureCount());
    }

    @Test
    @DisplayName("批量插入失败")
    void testBatchInsertFailed() {
        HardwareBoardCardAddCommand addCommand = getBoardCardAddCommand();
        List<HardwareBoardCardAddCommand> addCommandList = new ArrayList<>();
        addCommandList.add(addCommand);

        HardwareBoardCard boardCard = getBoardCard(null);

        Mockito.when(hardwareFactory.createBoardCard(addCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);

        BatchInsertResponse batchInsertResponse = boardCardApplicationService.batchInsert(addCommandList, USER_UUID);

        Assertions.assertEquals(0, batchInsertResponse.getSuccessCount());
        Assertions.assertEquals(1, batchInsertResponse.getFailureCount());
    }

    @Test
    @DisplayName("查询对象成功")
    void testGetByIdSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(boardCardRepository.find(HARDWARE_ID)).thenReturn(boardCard);

        HardwareBoardCard byId = boardCardApplicationService.getById(HARDWARE_ID);

        Assertions.assertEquals(boardCard, byId);
    }

    @Test
    @DisplayName("查询列表成功")
    void testGetListSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        HardwareBoardCardSelectVO selectVO = getBoardCardSelectVO();

        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);

        List<HardwareBoardCard> boardCardList1 = boardCardApplicationService.getList(selectVO);

        Assertions.assertEquals(boardCardList, boardCardList1);
    }

    @Test
    @DisplayName("查询分页成功")
    void testGetPageSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Page<HardwareBoardCard> boardCardPage = new Page<>();
        boardCardPage.setRecords(boardCardList);

        HardwareBoardCardSelectVO selectVO = getBoardCardSelectVO();

        Mockito.when(boardCardRepository.getPage(selectVO)).thenReturn(boardCardPage);

        Page<HardwareBoardCard> boardCardPage1 = boardCardApplicationService.getPage(selectVO);

        Assertions.assertEquals(boardCardPage, boardCardPage1);
    }

    @Test
    @DisplayName("编辑成功")
    void testEditSuccess() {
        HardwareBoardCardEditCommand editCommand = getBoardCardEditCommand();
        HardwareBoardCard editBoardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        editBoardCard.setVendorID(editCommand.getVendorID());
        editBoardCard.setUserUuid(USER_UUID);

        Mockito.when(hardwareFactory.createBoardCard(editCommand)).thenReturn(editBoardCard);
        Mockito.when(boardCardRepository.getOne(editBoardCard)).thenReturn(editBoardCard);
        Mockito.when(boardCardRepository.save(editBoardCard)).thenReturn(editBoardCard);

        boardCardApplicationService.edit(editCommand, USER_UUID);

        Assertions.assertEquals(editCommand.getVendorID(), editBoardCard.getVendorID());
    }

    @Test
    @DisplayName("编辑失败-已存在")
    void testEditFailedExist() {
        HardwareBoardCardEditCommand editCommand = getBoardCardEditCommand();
        HardwareBoardCard existBoard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setId(HARDWARE_ID + 1);
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(hardwareFactory.createBoardCard(editCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(existBoard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.edit(editCommand, USER_UUID));

        Assertions.assertEquals("板卡[" + existBoard.toSimpleJsonString() + "]已存在！", businessException.getMessage());
    }

    @Test
    @DisplayName("编辑失败-无权限")
    void testEditFailedPermission() {
        HardwareBoardCardEditCommand editCommand = getBoardCardEditCommand();
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setUserUuid("2");

        Mockito.when(hardwareFactory.createBoardCard(editCommand)).thenReturn(boardCard);
        Mockito.when(boardCardRepository.getOne(boardCard)).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.edit(editCommand, USER_UUID));

        Assertions.assertEquals("无权限编辑该板卡数据！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除成功")
    void testDeleteSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        boardCardApplicationService.delete(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(), boardCard.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_DELETE.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_BOARD_CARD.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("删除失败-无权限")
    void testDeleteFailedPermission() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);
        approvalNode.setHardwareId(Integer.valueOf(USER_UUID));

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setUserUuid("2");

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> boardCardApplicationService.delete(approvalNode));

        Assertions.assertEquals("无权限删除该板卡数据！", businessException.getMessage());
    }

    @Test
    @DisplayName("申请成功")
    void testApplySuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        boardCardApplicationService.apply(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), boardCard.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_PASS.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_BOARD_CARD.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("关闭成功")
    void testCloseSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        boardCardApplicationService.close(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(), boardCard.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_CLOSE.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_BOARD_CARD.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("通过成功")
    void testPassSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        boardCardApplicationService.pass(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), boardCard.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_PASS.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_BOARD_CARD.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("驳回成功")
    void testRejectSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setUserUuid(USER_UUID);

        Mockito.when(boardCardRepository.find(approvalNode.getHardwareId())).thenReturn(boardCard);
        Mockito.when(boardCardRepository.save(boardCard)).thenReturn(boardCard);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        boardCardApplicationService.reject(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(), boardCard.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_REJECT.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_BOARD_CARD.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }


    private HardwareBoardCardAddCommand getBoardCardAddCommand() {
        HardwareBoardCardAddCommand boardCardAddCommand = new HardwareBoardCardAddCommand();
        boardCardAddCommand.setVendorID("15B3");
        boardCardAddCommand.setDeviceID("1015");
        boardCardAddCommand.setSvID("19E5");
        boardCardAddCommand.setSsID("D11B");
        boardCardAddCommand.setArchitecture("x86_64");
        boardCardAddCommand.setOs("openEuler 22.03 LTS");
        boardCardAddCommand.setDriverName("mlx5_core");
        boardCardAddCommand.setVersion("5.1-2.5.2");
        boardCardAddCommand.setType("NIC");
        boardCardAddCommand.setDriverSize("1.1M");
        boardCardAddCommand.setDate("2021.3.19");
        boardCardAddCommand.setSha256("12312313123");
        boardCardAddCommand.setDownloadLink("https://xx.rpm");
        boardCardAddCommand.setChipVendor("CV4 Lx EN");
        boardCardAddCommand.setBoardModel("SP333");
        boardCardAddCommand.setChipModel("CX4 Lx EN");
        boardCardAddCommand.setSecurityLevel("1");
        return boardCardAddCommand;
    }

    private HardwareBoardCard getBoardCard(String status) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setVendorID("15B3");
        boardCard.setDeviceID("1015");
        boardCard.setSvID("19E5");
        boardCard.setSsID("D11B");
        boardCard.setArchitecture("x86_64");
        boardCard.setOs("openEuler 22.03 LTS");
        boardCard.setDriverName("mlx5_core");
        boardCard.setVersion("5.1-2.5.2");
        boardCard.setType("NIC");
        boardCard.setDriverSize("1.1M");
        boardCard.setDate("2021.3.19");
        boardCard.setSha256("12312313123");
        boardCard.setDownloadLink("https://xx.rpm");
        boardCard.setChipVendor("CV4 Lx EN");
        boardCard.setBoardModel("SP333");
        boardCard.setChipModel("CX4 Lx EN");
        boardCard.setStatus(status);
        return boardCard;
    }

    private HardwareBoardCardSelectVO getBoardCardSelectVO() {
        HardwareBoardCardSelectVO boardCardSelectVO = new HardwareBoardCardSelectVO();
        boardCardSelectVO.setId(String.valueOf(HARDWARE_ID));
        boardCardSelectVO.setArchitecture("x86_64");
        boardCardSelectVO.setOs("openEuler 22.03 LTS");
        boardCardSelectVO.setDriverName("mlx5_core");
        boardCardSelectVO.setType("NIC");
        boardCardSelectVO.setDate("2021.3.19");
        boardCardSelectVO.setChipVendor("CV4 Lx EN");
        boardCardSelectVO.setChipModel("CX4 Lx EN");
        boardCardSelectVO.setBoardModel("SP333");
        boardCardSelectVO.setUserUuid(USER_UUID);
        boardCardSelectVO.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        Date now = new Date();
        boardCardSelectVO.setApplyTime(now);
        boardCardSelectVO.setUpdateTime(now);
        boardCardSelectVO.setCurrent(1);
        boardCardSelectVO.setSize(10);
        return boardCardSelectVO;
    }

    private HardwareBoardCardEditCommand getBoardCardEditCommand() {
        HardwareBoardCardEditCommand boardCardEditCommand = new HardwareBoardCardEditCommand();
        boardCardEditCommand.setId(HARDWARE_ID);
        boardCardEditCommand.setVendorID("1015");
        boardCardEditCommand.setDeviceID("1015");
        boardCardEditCommand.setSvID("19E5");
        boardCardEditCommand.setSsID("D11B");
        boardCardEditCommand.setArchitecture("x86_64");
        boardCardEditCommand.setOs("openEuler 22.03 LTS");
        boardCardEditCommand.setDriverName("mlx5_core");
        boardCardEditCommand.setVersion("5.1-2.5.2");
        boardCardEditCommand.setType("NIC");
        boardCardEditCommand.setDriverSize("1.1M");
        boardCardEditCommand.setDate("2021.3.19");
        boardCardEditCommand.setSha256("12312313123");
        boardCardEditCommand.setDownloadLink("https://xx.rpm");
        boardCardEditCommand.setChipVendor("CV4 Lx EN");
        boardCardEditCommand.setBoardModel("SP333");
        boardCardEditCommand.setChipModel("CX4 Lx EN");
        return boardCardEditCommand;
    }

    private HardwareApprovalNode getApprovalNode(String result) {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHandlerUuid(Integer.valueOf(USER_UUID));
        approvalNode.setHandlerComment("操作意见");
        approvalNode.setHandlerResult(result);
        return approvalNode;
    }
}