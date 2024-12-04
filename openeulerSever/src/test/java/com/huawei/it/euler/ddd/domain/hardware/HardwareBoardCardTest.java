/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import cn.hutool.core.date.DateUtil;
import com.huawei.it.euler.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HardwareBoardCardTest {

    private static final int HARDWARE_ID = 1;

    private static final String USER_ID = "1";

    @Test
    @DisplayName("创建对象成功")
    public void testCreateSuccess() {
        HardwareBoardCard boardCard = getBoardCard(null);

        boardCard.create(USER_ID);

        Assertions.assertEquals(USER_ID,boardCard.getUserUuid());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("创建临时对象成功")
    public void testCreateTempSuccess() {
        HardwareBoardCard boardCard = getBoardCard(null);

        boardCard.createTemp(USER_ID);

        Assertions.assertEquals(USER_ID,boardCard.getUserUuid());
        Assertions.assertEquals(HardwareValueEnum.NODE_TEMP.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("编辑对象成功")
    public void testEditSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());

        boardCard.edit();

        Date now = new Date();
        String nowStr = DateUtil.format(now, "yyyy-mm-dd");
        String editDateStr = DateUtil.format(boardCard.getUpdateTime(), "yyyy-mm-dd");
        Assertions.assertEquals(nowStr, editDateStr);
    }

    @Test
    @DisplayName("编辑对象失败")
    public void testEditFailed() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::edit);

        Assertions.assertEquals("当前板卡数据状态无法进行编辑操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除对象成功")
    public void testDeleteSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());

        boardCard.delete();

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("删除对象失败-状态不匹配")
    public void testDeleteFailedStatus() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::delete);

        Assertions.assertEquals("当前板卡数据状态无法进行删除操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除对象失败-关联整机")
    public void testDeleteFailedRef() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        boardCard.setWholeMachineIds(String.valueOf(HARDWARE_ID));

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::delete);

        Assertions.assertEquals("当前板卡关联整机，无法删除！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务申请成功")
    public void testApplySuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        boardCard.apply();

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("业务申请失败-状态不匹配")
    public void testApplyFailedStatus() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_TEMP.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::apply);

        Assertions.assertEquals("当前板卡数据状态无法进行申请操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务通过成功")
    public void testPassSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        boardCard.pass();

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("业务申请失败-状态不匹配")
    public void testPassFailedStatus() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::pass);

        Assertions.assertEquals("当前板卡数据状态无法进行审批操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务驳回成功")
    public void testRejectSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        boardCard.reject();

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("业务关闭失败-状态不匹配")
    public void testRejectFailedStatus() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_REJECT.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::reject);

        Assertions.assertEquals("当前板卡数据状态无法进行审批操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务关闭成功")
    public void testCloseSuccess() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        boardCard.close();

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(),boardCard.getStatus());
    }

    @Test
    @DisplayName("业务关闭失败-状态不匹配")
    public void testCloseFailedStatus() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_REJECT.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                boardCard::close);

        Assertions.assertEquals("当前板卡数据状态无法进行关闭操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("添加关联成功-初始化")
    public void testAddWholeMachineSuccessInit() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        boardCard.addWholeMachine(HARDWARE_ID);

        Assertions.assertEquals(String.valueOf(HARDWARE_ID),boardCard.getWholeMachineIds());
    }

    @Test
    @DisplayName("添加关联成功-追加")
    public void testRemoveWholeMachineSuccessAppend() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setWholeMachineIds("2");
        boardCard.addWholeMachine(HARDWARE_ID);

        Assertions.assertEquals("2,1",boardCard.getWholeMachineIds());
    }

    @Test
    @DisplayName("解除关联成功-清空")
    public void testRemoveWholeMachineSuccessInit() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setWholeMachineIds("1");
        boardCard.removeWholeMachine(HARDWARE_ID);

        Assertions.assertNull(boardCard.getWholeMachineIds());
    }

    @Test
    @DisplayName("解除关联成功-解除")
    public void testAddWholeMachineSuccessAppend() {
        HardwareBoardCard boardCard = getBoardCard(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        boardCard.setWholeMachineIds("1,2");
        boardCard.removeWholeMachine(HARDWARE_ID);

        Assertions.assertEquals("2",boardCard.getWholeMachineIds());
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
