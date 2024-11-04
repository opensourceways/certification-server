/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HardwareWholeMachineServiceTest {

    private static final int HARDWARE_ID = 1;

    @InjectMocks
    private HardwareWholeMachineService wholeMachineService;

    @Test
    @DisplayName("板卡关联整机成功")
    public void testRefSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachineService.refBoardCard2WholeMachine(wholeMachine, boardCardList);
        Assertions.assertEquals("1", wholeMachine.getBoardCardIds());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals("1", card.getWholeMachineIds());
        }
    }

    @Test
    @DisplayName("删除整机成功-全部")
    public void testDeleteSuccessAll() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());
        boardCard.setWholeMachineIds("1");

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.delete(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(), card.getStatus());
        }
    }

    @Test
    @DisplayName("删除整机成功-部分")
    public void testDeleteSuccessPart() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_PASS.getValue());
        boardCard.setWholeMachineIds("1,2");

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.delete(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals("2", card.getWholeMachineIds());
        }
    }

    @Test
    @DisplayName("关闭整机成功-全部")
    public void testCloseSuccessAll() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.close(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(), card.getStatus());
        }
    }

    @Test
    @DisplayName("关闭整机成功-部分")
    public void testCloseSuccessPart() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_PASS.getValue());
        boardCard.setWholeMachineIds("1,2");

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.close(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals("2", card.getWholeMachineIds());
            Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), card.getStatus());
        }
    }

    @Test
    @DisplayName("通过整机成功-全部")
    public void testPassSuccessAll() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());
        boardCard.setWholeMachineIds("1");

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.pass(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), card.getStatus());
        }
    }

    @Test
    @DisplayName("通过整机成功-部分")
    public void testPassSuccessPart() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setWholeMachineIds("1,2");

        List<HardwareBoardCard> boardCardList = new ArrayList<>();
        boardCardList.add(boardCard);
        wholeMachine.setBoardCards(boardCardList);

        wholeMachineService.pass(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(), wholeMachine.getStatus());
        List<HardwareBoardCard> boardCards = wholeMachine.getBoardCards();
        for (HardwareBoardCard card : boardCards) {
            Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(), card.getStatus());
        }
    }

    private HardwareWholeMachine getWholeMachine(String status) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setHardwareFactoryZy("华为");
        wholeMachine.setHardwareFactoryEn("huawei");
        wholeMachine.setHardwareModel("TaiShan 200 (Model 2280)");
        wholeMachine.setOsVersion("openEuler 22.03 LTS");
        wholeMachine.setArchitecture("aarch64");
        wholeMachine.setDate("2020/12/04");
        wholeMachine.setStatus(status);
        return wholeMachine;
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
}
