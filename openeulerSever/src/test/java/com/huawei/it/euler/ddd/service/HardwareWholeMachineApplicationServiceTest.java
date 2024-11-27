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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HardwareWholeMachineApplicationServiceTest {

    private static final String USER_UUID = "1";

    private static final int HARDWARE_ID = 1;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareWholeMachineRepositoryImpl wholeMachineRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareApprovalNodeRepositoryImpl approvalNodeRepository;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareWholeMachineService wholeMachineService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareFactory hardwareFactory;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @InjectMocks
    private HardwareWholeMachineApplicationService wholeMachineApplicationService;


    @Test
    @DisplayName("板卡插入成功")
    void testInsertSuccess() {
        HardwareWholeMachineAddCommand addCommand = getWholeMachineAddCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        HardwareBoardCard boardCard = getBoardCard(null);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Mockito.when(hardwareFactory.createWholeMachine(addCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(null);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.findOrSaveTemp(wholeMachine.getBoardCards(),USER_UUID)).thenReturn(boardCardList);
        Mockito.when(wholeMachineService.refBoardCard2WholeMachine(wholeMachine,boardCardList)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.doNothing().when(boardCardRepository).saveBatch(wholeMachine.getBoardCards());

        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, USER_UUID);

        Assertions.assertTrue(insert.isSuccess());
    }

    @Test
    @DisplayName("板卡插入失败-已存在")
    void testInsertFailedExist() {
        HardwareWholeMachineAddCommand addCommand = getWholeMachineAddCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        HardwareBoardCard boardCard = getBoardCard(null);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Mockito.when(hardwareFactory.createWholeMachine(addCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(wholeMachine);

        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, USER_UUID);

        Assertions.assertFalse(insert.isSuccess());
        Assertions.assertEquals("当前整机已存在！", insert.getMessage());
    }

    @Test
    @DisplayName("板卡插入失败-sql")
    void testInsertFailedSql() {
        HardwareWholeMachineAddCommand addCommand = getWholeMachineAddCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        Mockito.when(hardwareFactory.createWholeMachine(addCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(null);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(null);

        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, USER_UUID);

        Assertions.assertFalse(insert.isSuccess());
        Assertions.assertEquals("当前整机新增失败！", insert.getMessage());
    }

    @Test
    @DisplayName("批量插入成功")
    void testBatchInsertSuccess() {
        HardwareWholeMachineAddCommand addCommand = getWholeMachineAddCommand();
        List<HardwareWholeMachineAddCommand> addCommandList = new ArrayList<>();
        addCommandList.add(addCommand);

        HardwareBoardCard boardCard = getBoardCard(null);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        Mockito.when(hardwareFactory.createWholeMachine(addCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(null);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.findOrSaveTemp(wholeMachine.getBoardCards(),USER_UUID)).thenReturn(boardCardList);
        Mockito.when(wholeMachineService.refBoardCard2WholeMachine(wholeMachine,boardCardList)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.doNothing().when(boardCardRepository).saveBatch(wholeMachine.getBoardCards());

        BatchInsertResponse batchInsertResponse = wholeMachineApplicationService.batchInsert(addCommandList, USER_UUID);

        Assertions.assertEquals(1, batchInsertResponse.getSuccessCount());
        Assertions.assertEquals(0, batchInsertResponse.getFailureCount());
    }

    @Test
    @DisplayName("批量插入失败")
    void testBatchInsertFailed() {
        HardwareWholeMachineAddCommand addCommand = getWholeMachineAddCommand();
        List<HardwareWholeMachineAddCommand> addCommandList = new ArrayList<>();
        addCommandList.add(addCommand);

        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        Mockito.when(hardwareFactory.createWholeMachine(addCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(wholeMachine);

        BatchInsertResponse batchInsertResponse = wholeMachineApplicationService.batchInsert(addCommandList, USER_UUID);

        Assertions.assertEquals(0, batchInsertResponse.getSuccessCount());
        Assertions.assertEquals(1, batchInsertResponse.getFailureCount());
    }

    @Test
    @DisplayName("查询对象成功")
    void testGetByIdSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        HardwareBoardCardSelectVO selectVO = getBoardCardSelectVO();

        Mockito.when(wholeMachineRepository.find(HARDWARE_ID)).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);

        HardwareWholeMachine byId = wholeMachineApplicationService.getById(HARDWARE_ID);

        Assertions.assertEquals(wholeMachine, byId);
    }

    @Test
    @DisplayName("查询分页成功")
    void testGetPageSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);

        Page<HardwareWholeMachine> wholeMachinePage = new Page<>();
        wholeMachinePage.setRecords(wholeMachineList);

        HardwareWholeMachineSelectVO selectVO = getWholeMachineSelectVO();

        Mockito.when(wholeMachineRepository.getPage(selectVO)).thenReturn(wholeMachinePage);

        Page<HardwareWholeMachine> wholeMachinePage1 = wholeMachineApplicationService.getPage(selectVO);

        Assertions.assertEquals(wholeMachinePage, wholeMachinePage1);
    }

    @Test
    @DisplayName("编辑成功")
    void testEditSuccess() {
        HardwareWholeMachineEditCommand editCommand = getWholeMachineEditCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setHardwareFactoryEn("huawei1");
        wholeMachine.setUserUuid(USER_UUID);

        HardwareWholeMachine editWholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        editWholeMachine.setUserUuid(USER_UUID);

        HardwareBoardCard editBoardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        editBoardCard.setUserUuid(USER_UUID);

        Mockito.when(hardwareFactory.createWholeMachine(editCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(editWholeMachine);
        Mockito.when(hardwareFactory.createBoardCard(editCommand.getBoardCardEditCommandList()
                .get(0))).thenReturn(editBoardCard);
        Mockito.when(boardCardRepository.getOne(editBoardCard)).thenReturn(editBoardCard);
        Mockito.when(boardCardRepository.save(editBoardCard)).thenReturn(editBoardCard);
        Mockito.doNothing().when(boardCardApplicationService).edit(editCommand.getBoardCardEditCommandList().get(0), USER_UUID);

        wholeMachineApplicationService.edit(editCommand, USER_UUID);

        Assertions.assertEquals(editCommand.getHardwareFactoryEn(), wholeMachine.getHardwareFactoryEn());
    }

    @Test
    @DisplayName("编辑失败-已存在")
    void testEditFailedExist() {
        HardwareWholeMachineEditCommand editCommand = getWholeMachineEditCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareWholeMachine editWholeMachine = getWholeMachine(null);
        editWholeMachine.setId(2);

        HardwareBoardCard editBoardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        editBoardCard.setUserUuid(USER_UUID);

        Mockito.when(hardwareFactory.createWholeMachine(editCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(editWholeMachine);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.edit(editCommand, USER_UUID));

        Assertions.assertEquals("板卡[" + editWholeMachine.toSimpleJsonString() + "]已存在！", businessException.getMessage());
    }

    @Test
    @DisplayName("编辑失败-无权限")
    void testEditFailedPermission() {
        HardwareWholeMachineEditCommand editCommand = getWholeMachineEditCommand();
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setUserUuid("2");

        Mockito.when(hardwareFactory.createWholeMachine(editCommand)).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.getOne(wholeMachine)).thenReturn(wholeMachine);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.edit(editCommand, USER_UUID));

        Assertions.assertEquals("无权限编辑该整机数据！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除成功")
    void testDeleteSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setUserUuid(USER_UUID);
        wholeMachine.setBoardCardIds("1");

        HardwareBoardCardSelectVO selectVO = this.getBoardCardSelectVO();
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);
        Mockito.doAnswer(invocation -> wholeMachine.delete()).when(wholeMachineService).delete(wholeMachine);
        Mockito.doNothing().when(boardCardRepository).saveBatch(wholeMachine.getBoardCards());
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        wholeMachineApplicationService.delete(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(), wholeMachine.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_DELETE.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("删除失败-无权限")
    void testDeleteFailedPermission() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);
        approvalNode.setHardwareId(Integer.valueOf(USER_UUID));

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setUserUuid("2");

        HardwareBoardCardSelectVO selectVO = this.getBoardCardSelectVO();
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> wholeMachineApplicationService.delete(approvalNode));

        Assertions.assertEquals("无权限编辑该整机数据！", businessException.getMessage());
    }

    @Test
    @DisplayName("申请成功")
    void testApplySuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachine.setUserUuid(USER_UUID);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        wholeMachineApplicationService.apply(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), wholeMachine.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_PASS.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("关闭成功")
    void testCloseSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        wholeMachine.setUserUuid(USER_UUID);
        wholeMachine.setBoardCardIds("1");

        HardwareBoardCardSelectVO selectVO = this.getBoardCardSelectVO();
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);
        Mockito.doAnswer(invocation -> wholeMachine.close()).when(wholeMachineService).close(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        wholeMachineApplicationService.close(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(), wholeMachine.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_CLOSE.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("通过成功")
    void testPassSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        wholeMachine.setUserUuid(USER_UUID);

        HardwareBoardCardSelectVO selectVO = this.getBoardCardSelectVO();
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(boardCardRepository.getList(selectVO)).thenReturn(boardCardList);
        Mockito.doAnswer(invocation -> {
            wholeMachine.pass();
            return wholeMachine;
        }).when(wholeMachineService).pass(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        wholeMachineApplicationService.pass(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), wholeMachine.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_PASS.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }

    @Test
    @DisplayName("驳回成功")
    void testRejectSuccess() {
        HardwareApprovalNode approvalNode = getApprovalNode(null);

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        wholeMachine.setUserUuid(USER_UUID);

        Mockito.when(wholeMachineRepository.find(approvalNode.getHardwareId())).thenReturn(wholeMachine);
        Mockito.when(wholeMachineRepository.save(wholeMachine)).thenReturn(wholeMachine);
        Mockito.when(approvalNodeRepository.save(approvalNode)).thenReturn(true);

        wholeMachineApplicationService.reject(approvalNode);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(), wholeMachine.getStatus());
        Assertions.assertEquals(HardwareValueEnum.RESULT_REJECT.getValue(), approvalNode.getHandlerResult());
        Assertions.assertEquals(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(), approvalNode.getHardwareType());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), approvalNode.getHandlerNode());
    }


    private HardwareWholeMachineAddCommand getWholeMachineAddCommand() {
        HardwareWholeMachineAddCommand wholeMachineAddCommand = new HardwareWholeMachineAddCommand();
        wholeMachineAddCommand.setHardwareFactoryZy("华为");
        wholeMachineAddCommand.setHardwareFactoryEn("huawei");
        wholeMachineAddCommand.setHardwareModel("TaiShan 200 (Model 2280)");
        wholeMachineAddCommand.setOsVersion("openEuler 22.03 LTS");
        wholeMachineAddCommand.setArchitecture("x86_64");
        wholeMachineAddCommand.setDate("2021.3.19");
        wholeMachineAddCommand.setFriendlyLink("https://xx.rpm");
        wholeMachineAddCommand.setProductInformation("https://xx.rpm");
        wholeMachineAddCommand.setCertificationTime("2021.3.19");
        wholeMachineAddCommand.setMainboardModel("A1B2C3");
        wholeMachineAddCommand.setBiosUefi("1.7");
        wholeMachineAddCommand.setCpu("Kunpeng-920");
        wholeMachineAddCommand.setRam("16*16G DIMMs");
        wholeMachineAddCommand.setPortsBusTypes("123");
        wholeMachineAddCommand.setVideoAdapter("1711");
        wholeMachineAddCommand.setHostBusAdapter("321");
        wholeMachineAddCommand.setHardDiskDrive("SP333");
        wholeMachineAddCommand.setSecurityLevel("0");
        HardwareBoardCardAddCommand boardCardAddCommand = getBoardCardAddCommand();
        List<HardwareBoardCardAddCommand> boardCardAddCommandList = new ArrayList<>();
        boardCardAddCommandList.add(boardCardAddCommand);
        wholeMachineAddCommand.setBoardCardAddCommandList(boardCardAddCommandList);
        return wholeMachineAddCommand;
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

    private HardwareWholeMachine getWholeMachine(String status) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setHardwareFactoryZy("华为");
        wholeMachine.setHardwareFactoryEn("huawei");
        wholeMachine.setHardwareModel("TaiShan 200 (Model 2280)");
        wholeMachine.setOsVersion("openEuler 22.03 LTS");
        wholeMachine.setArchitecture("x86_64");
        wholeMachine.setDate("2021.3.19");
        wholeMachine.setFriendlyLink("https://xx.rpm");
        wholeMachine.setBoardCardIds("1");
        wholeMachine.setCompatibilityConfiguration(getConfiguration());
        wholeMachine.setSecurityLevel("0");
        wholeMachine.setStatus(status);
        HardwareBoardCard boardCard = getBoardCard(status);
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);
        return wholeMachine;
    }

    private HardwareCompatibilityConfiguration getConfiguration(){
        HardwareCompatibilityConfiguration configuration = new HardwareCompatibilityConfiguration();
        configuration.setProductInformation("https://xx.rpm");
        configuration.setCertificationTime("2021.3.19");
        configuration.setMainboardModel("A1B2C3");
        configuration.setBiosUefi("1.7");
        configuration.setCpu("Kunpeng-920");
        configuration.setRam("16*16G DIMMs");
        configuration.setPortsBusTypes("123");
        configuration.setVideoAdapter("1711");
        configuration.setHostBusAdapter("321");
        configuration.setHardDiskDrive("SP333");
        return configuration;
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

    private HardwareWholeMachineSelectVO getWholeMachineSelectVO() {
        HardwareWholeMachineSelectVO wholeMachineSelectVO = new HardwareWholeMachineSelectVO();
        wholeMachineSelectVO.setId(String.valueOf(HARDWARE_ID));
        wholeMachineSelectVO.setOsVersion("openEuler 22.03 LTS");
        wholeMachineSelectVO.setArchitecture("x86_64");
        wholeMachineSelectVO.setCpu("mlx5_core");
        wholeMachineSelectVO.setDate("2021.3.19");
        wholeMachineSelectVO.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        wholeMachineSelectVO.setUserUuid(USER_UUID);
        Date now = new Date();
        wholeMachineSelectVO.setApplyTime(now);
        wholeMachineSelectVO.setUpdateTime(now);
        wholeMachineSelectVO.setCurrent(1);
        wholeMachineSelectVO.setSize(10);
        return wholeMachineSelectVO;
    }

    private HardwareBoardCardSelectVO getBoardCardSelectVO() {
        HardwareBoardCardSelectVO boardCardSelectVO = new HardwareBoardCardSelectVO();
        boardCardSelectVO.setIdList(Collections.singletonList(String.valueOf(HARDWARE_ID)));
        return boardCardSelectVO;
    }

    private HardwareWholeMachineEditCommand getWholeMachineEditCommand() {
        HardwareWholeMachineEditCommand wholeMachineEditCommand = new HardwareWholeMachineEditCommand();
        wholeMachineEditCommand.setHardwareFactoryZy("华为");
        wholeMachineEditCommand.setHardwareFactoryEn("huawei1");
        wholeMachineEditCommand.setHardwareModel("TaiShan 200 (Model 2280)");
        wholeMachineEditCommand.setOsVersion("openEuler 22.03 LTS");
        wholeMachineEditCommand.setArchitecture("x86_64");
        wholeMachineEditCommand.setDate("2021.3.19");
        wholeMachineEditCommand.setFriendlyLink("https://xx.rpm");
        wholeMachineEditCommand.setProductInformation("https://xx.rpm");
        wholeMachineEditCommand.setCertificationTime("2021.3.19");
        wholeMachineEditCommand.setMainboardModel("A1B2C3");
        wholeMachineEditCommand.setBiosUefi("1.7");
        wholeMachineEditCommand.setCpu("Kunpeng-920");
        wholeMachineEditCommand.setRam("16*16G DIMMs");
        wholeMachineEditCommand.setPortsBusTypes("123");
        wholeMachineEditCommand.setVideoAdapter("1711");
        wholeMachineEditCommand.setHostBusAdapter("321");
        wholeMachineEditCommand.setHardDiskDrive("SP333");
        wholeMachineEditCommand.setSecurityLevel("0");
        HardwareBoardCardEditCommand boardCardEditCommand = getBoardCardEditCommand();
        List<HardwareBoardCardEditCommand> boardCardEditCommandList = new ArrayList<>();
        boardCardEditCommandList.add(boardCardEditCommand);
        wholeMachineEditCommand.setBoardCardEditCommandList(boardCardEditCommandList);
        return wholeMachineEditCommand;
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