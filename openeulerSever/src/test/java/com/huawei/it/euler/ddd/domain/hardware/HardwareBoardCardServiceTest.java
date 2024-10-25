/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HardwareBoardCardServiceTest {

    private static final int HARDWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareFactory hardwareFactory;

    @InjectMocks
    private HardwareBoardCardService boardCardService;

    @Test
    public void testExist() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(boardCardRepository.count(any())).thenReturn(1L);

        boolean exist = boardCardService.exist(boardCard);

        Assertions.assertTrue(exist);
    }

    @Test
    public void testGetListBySelectVO() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCardPO> boardCardPOList = new ArrayList<>();
        boardCardPOList.add(boardCardPO);

        HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
        selectVO.setId(String.valueOf(HARDWARE_ID));
        selectVO.setOs("openEuler 22.03 LTS");
        selectVO.setArchitecture("x86_64");
        selectVO.setBoardModel("SP333");
        selectVO.setChipModel("CX4 Lx EN");

        Mockito.when(boardCardRepository.list((Wrapper<HardwareBoardCardPO>) any())).thenReturn(boardCardPOList);
        Mockito.when(hardwareFactory.createBoardCardList(boardCardPOList)).thenReturn(boardCardList);

        List<HardwareBoardCard> nodeList = boardCardService.getList(selectVO);

        Assertions.assertEquals(boardCardList, nodeList);
    }

    @Test
    public void testGetListEntity() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCardPO> boardCardPOList = new ArrayList<>();
        boardCardPOList.add(boardCardPO);

        HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
        selectVO.setId(String.valueOf(HARDWARE_ID));
        selectVO.setOs("openEuler 22.03 LTS");
        selectVO.setArchitecture("x86_64");
        selectVO.setBoardModel("SP333");
        selectVO.setChipModel("CX4 Lx EN");

        Mockito.when(hardwareFactory.createBoardCardSelectVO(boardCard)).thenReturn(selectVO);
        Mockito.when(boardCardRepository.list((Wrapper<HardwareBoardCardPO>) any())).thenReturn(boardCardPOList);
        Mockito.when(hardwareFactory.createBoardCardList(boardCardPOList)).thenReturn(boardCardList);

        List<HardwareBoardCard> nodeList = boardCardService.getList(selectVO);

        Assertions.assertEquals(boardCardList, nodeList);
    }

    @Test
    public void testGetPage() {
        HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
        selectVO.setId(String.valueOf(HARDWARE_ID));
        selectVO.setOs("openEuler 22.03 LTS");
        selectVO.setArchitecture("x86_64");
        selectVO.setBoardModel("SP333");
        selectVO.setChipModel("CX4 Lx EN");

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        Page<HardwareBoardCard> page = new Page<>(1, 10);
        page.setRecords(boardCardList);

        Page<HardwareBoardCardPO> boardCardPOPage = new Page<>(selectVO.getCurrent(), selectVO.getSize());
        Mockito.when(boardCardRepository.page(any(), any())).thenReturn(boardCardPOPage);
        Mockito.when(hardwareFactory.createBoardCardList(any())).thenReturn(boardCardList);

        Page<HardwareBoardCard> boardCardPage = boardCardService.getPage(new HardwareBoardCardSelectVO());
        Assertions.assertEquals(boardCardList, boardCardPage.getRecords());
    }

    @Test
    public void testGetById() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(boardCardRepository.getById(HARDWARE_ID)).thenReturn(boardCardPO);
        Mockito.when(hardwareFactory.createBoardCard(boardCardPO)).thenReturn(boardCard);

        HardwareBoardCard byId = boardCardService.getById(HARDWARE_ID);

        Assertions.assertEquals(boardCard, byId);
    }

    @Test
    public void testUpdateById() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard)).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.updateById(boardCardPO)).thenReturn(true);

        boardCard.setOs("openEuler 20.03 LTS");
        boardCardService.updateById(boardCard);

        Assertions.assertEquals("openEuler 20.03 LTS", boardCard.getOs());
    }

    @Test
    public void testInsert() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard)).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.save(boardCardPO)).thenReturn(true);
        Mockito.when(hardwareFactory.createBoardCard(boardCardPO)).thenReturn(boardCard);

        HardwareBoardCard insert = boardCardService.insert(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(), insert.getStatus());
    }

    @Test
    public void testBatchInsert() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);

        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareBoardCardPO> boardCardPOList = new ArrayList<>();
        boardCardPOList.add(boardCardPO);

        Mockito.when(hardwareFactory.createBoardCardPOList(boardCardList)).thenReturn(boardCardPOList);
        Mockito.when(boardCardRepository.saveBatch(boardCardPOList)).thenReturn(true);

        boolean insert = boardCardService.batchInsert(boardCardList);

        Assertions.assertTrue(insert);
    }

    @Test
    public void testDelete() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_DELETE.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_DELETE.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard)).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.saveOrUpdate(boardCardPO)).thenReturn(true);

        boardCardService.delete(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),boardCard.getStatus());
    }

    @Test
    public void testApply() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard.delete())).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.saveOrUpdate(boardCardPO)).thenReturn(true);

        boardCardService.apply(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),boardCard.getStatus());
    }

    @Test
    public void testPass() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_PASS.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_PASS.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard.pass())).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.saveOrUpdate(boardCardPO)).thenReturn(true);

        boardCardService.pass(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(),boardCard.getStatus());
    }

    @Test
    public void testReject() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_REJECT.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_REJECT.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard)).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.saveOrUpdate(boardCardPO)).thenReturn(true);

        boardCardService.reject(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),boardCard.getStatus());
    }

    @Test
    public void testClose() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_CLOSE.getValue());
        HardwareBoardCardPO boardCardPO = applyBoardCardPO(HardwareValueEnum.NODE_CLOSE.getValue());

        Mockito.when(hardwareFactory.createBoardCardPO(boardCard)).thenReturn(boardCardPO);
        Mockito.when(boardCardRepository.saveOrUpdate(boardCardPO)).thenReturn(true);

        boardCardService.close(boardCard);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(),boardCard.getStatus());
    }

    private HardwareBoardCard getBoardCard(String status) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        boardCard.setId(HARDWARE_ID);
        boardCard.setOs("openEuler 22.03 LTS");
        boardCard.setArchitecture("x86_64");
        boardCard.setBoardModel("SP333");
        boardCard.setChipModel("CX4 Lx EN");
        boardCard.setStatus(status);
        return boardCard;
    }

    private HardwareBoardCardPO applyBoardCardPO(String status) {
        HardwareBoardCardPO boardCardPO = new HardwareBoardCardPO();
        boardCardPO.setId(HARDWARE_ID);
        boardCardPO.setOs("openEuler 22.03 LTS");
        boardCardPO.setArchitecture("x86_64");
        boardCardPO.setBoardModel("SP333");
        boardCardPO.setChipModel("CX4 Lx EN");
        boardCardPO.setStatus(status);
        return boardCardPO;
    }
}
