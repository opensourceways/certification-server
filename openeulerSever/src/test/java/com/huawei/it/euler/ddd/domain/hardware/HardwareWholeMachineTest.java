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
public class HardwareWholeMachineTest {

    private static final int HARDWARE_ID = 1;

    private static final String USER_ID = "1";

    @Test
    @DisplayName("创建对象成功")
    public void testCreateSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(null);

        wholeMachine.create(USER_ID);

        Assertions.assertEquals(USER_ID,wholeMachine.getUserUuid());
        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPLY.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("编辑对象成功")
    public void testEditSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        wholeMachine.edit();

        Date now = new Date();
        String nowStr = DateUtil.format(now, "yyyy-mm-dd");
        String editDateStr = DateUtil.format(wholeMachine.getUpdateTime(), "yyyy-mm-dd");
        Assertions.assertEquals(nowStr, editDateStr);
    }

    @Test
    @DisplayName("编辑对象失败")
    public void testEditFailed() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::edit);

        Assertions.assertEquals("当前整机数据状态无法进行编辑操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除对象成功")
    public void testDeleteSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        wholeMachine.delete();

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("删除对象失败-状态不匹配")
    public void testDeleteFailedStatus() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::delete);

        Assertions.assertEquals("当前整机数据状态无法进行删除操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务申请成功")
    public void testApplySuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        wholeMachine.apply();

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("业务申请失败-状态不匹配")
    public void testApplyFailedStatus() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::apply);

        Assertions.assertEquals("当前整机数据状态无法进行申请操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务通过成功")
    public void testPassSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        wholeMachine.pass();

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("业务申请失败-状态不匹配")
    public void testPassFailedStatus() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::pass);

        Assertions.assertEquals("当前整机数据状态无法进行审批操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务驳回成功")
    public void testRejectSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        wholeMachine.reject();

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("业务关闭失败-状态不匹配")
    public void testRejectFailedStatus() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_REJECT.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::reject);

        Assertions.assertEquals("当前整机数据状态无法进行审批操作！", businessException.getMessage());
    }

    @Test
    @DisplayName("业务关闭成功")
    public void testCloseSuccess() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        wholeMachine.close();

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("业务关闭成功")
    public void testCloseSuccessByReject() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_REJECT.getValue());

        wholeMachine.close();

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(),wholeMachine.getStatus());
    }

    @Test
    @DisplayName("业务关闭失败-状态不匹配")
    public void testCloseFailedStatus() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        BusinessException businessException = assertThrows(BusinessException.class,
                wholeMachine::close);

        Assertions.assertEquals("当前整机数据状态无法进行关闭操作！", businessException.getMessage());
    }

    private HardwareWholeMachine getWholeMachine(String status) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        wholeMachine.setId(HARDWARE_ID);
        wholeMachine.setHardwareFactory("华为");
        wholeMachine.setHardwareFactoryEn("huawei");
        wholeMachine.setHardwareModel("泰山 200 (Model 2280)");
        wholeMachine.setHardwareModelEn("TaiShan 200 (Model 2280)");
        wholeMachine.setOsVersion("openEuler 22.03 LTS");
        wholeMachine.setArchitecture("aarch64");
        wholeMachine.setDate("2020/12/04");
        wholeMachine.setStatus(status);
        return wholeMachine;
    }
}
