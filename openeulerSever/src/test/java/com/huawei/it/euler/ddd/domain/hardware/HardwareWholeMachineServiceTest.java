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
public class HardwareWholeMachineServiceTest {

    private static final int HARDWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareWholeMachineRepositoryImpl wholeMachineRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareFactory hardwareFactory;

    @InjectMocks
    private HardwareWholeMachineService wholeMachineService;

    @Test
    public void testExist() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(wholeMachineRepository.count(any())).thenReturn(1L);

        boolean exist = wholeMachineService.exist(wholeMachine);

        Assertions.assertTrue(exist);
    }

    @Test
    public void testGetList() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);

        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareWholeMachinePO> wholeMachinePOList = new ArrayList<>();
        wholeMachinePOList.add(wholeMachinePO);

        HardwareWholeMachineSelectVO selectVO = new HardwareWholeMachineSelectVO();
        selectVO.setId(String.valueOf(HARDWARE_ID));
        selectVO.setOsVersion("openEuler 22.03 LTS");
        selectVO.setArchitecture("x86_64");
        selectVO.setHardwareModel("SP333");
        selectVO.setDate("2020/12/04");

        Mockito.when(wholeMachineRepository.list((Wrapper<HardwareWholeMachinePO>) any())).thenReturn(wholeMachinePOList);
        Mockito.when(hardwareFactory.createWholeMachineList(wholeMachinePOList)).thenReturn(wholeMachineList);

        List<HardwareWholeMachine> nodeList = wholeMachineService.getList(selectVO);

        Assertions.assertEquals(wholeMachineList, nodeList);
    }

    @Test
    public void testGetPage() {
        HardwareWholeMachineSelectVO selectVO = new HardwareWholeMachineSelectVO();
        selectVO.setId(String.valueOf(HARDWARE_ID));
        selectVO.setOsVersion("openEuler 22.03 LTS");
        selectVO.setArchitecture("x86_64");
        selectVO.setHardwareModel("SP333");
        selectVO.setDate("2020/12/04");

        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);

        Page<HardwareWholeMachine> page = new Page<>(1, 10);
        page.setRecords(wholeMachineList);

        Page<HardwareWholeMachinePO> wholeMachinePOPage = new Page<>(selectVO.getCurrent(), selectVO.getSize());
        Mockito.when(wholeMachineRepository.page(any(), any())).thenReturn(wholeMachinePOPage);
        Mockito.when(hardwareFactory.createWholeMachineList(any())).thenReturn(wholeMachineList);

        Page<HardwareWholeMachine> boardCardPage = wholeMachineService.getPage(new HardwareWholeMachineSelectVO());
        Assertions.assertEquals(wholeMachineList, boardCardPage.getRecords());
    }

    @Test
    public void testGetById() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(wholeMachineRepository.getById(HARDWARE_ID)).thenReturn(wholeMachinePO);
        Mockito.when(hardwareFactory.createWholeMachine(wholeMachinePO)).thenReturn(wholeMachine);

        HardwareWholeMachine byId = wholeMachineService.getById(HARDWARE_ID);

        Assertions.assertEquals(wholeMachine, byId);
    }

    @Test
    public void testUpdateById() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine)).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.updateById(wholeMachinePO)).thenReturn(true);

        wholeMachine.setOsVersion("openEuler 20.03 LTS");
        wholeMachineService.updateById(wholeMachine);

        Assertions.assertEquals("openEuler 20.03 LTS", wholeMachine.getOsVersion());
    }

    @Test
    public void testInsert() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine)).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.save(wholeMachinePO)).thenReturn(true);
        Mockito.when(hardwareFactory.createWholeMachine(wholeMachinePO)).thenReturn(wholeMachine);

        boolean insert = wholeMachineService.insert(wholeMachine);

        Assertions.assertTrue(insert);
    }

    @Test
    public void testBatchInsert() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        wholeMachineList.add(wholeMachine);

        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

         List<HardwareWholeMachinePO> wholeMachinePOList = new ArrayList<>();
        wholeMachinePOList.add(wholeMachinePO);

        Mockito.when(hardwareFactory.createWholeMachinePOList(wholeMachineList)).thenReturn(wholeMachinePOList);
        Mockito.when(wholeMachineRepository.saveBatch(wholeMachinePOList)).thenReturn(true);

        boolean insert = wholeMachineService.batchInsert(wholeMachineList);

        Assertions.assertTrue(insert);
    }

    @Test
    public void testDelete() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_DELETE.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_DELETE.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine)).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.saveOrUpdate(wholeMachinePO)).thenReturn(true);

        wholeMachineService.delete(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_DELETE.getValue(),wholeMachine.getStatus());
    }

    @Test
    public void testApply() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine.delete())).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.saveOrUpdate(wholeMachinePO)).thenReturn(true);

        wholeMachineService.apply(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_WAIT_APPROVE.getValue(),wholeMachine.getStatus());
    }

    @Test
    public void testPass() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_PASS.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_PASS.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine.pass())).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.saveOrUpdate(wholeMachinePO)).thenReturn(true);

        wholeMachineService.pass(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_PASS.getValue(),wholeMachine.getStatus());
    }

    @Test
    public void testReject() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_REJECT.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_REJECT.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine)).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.saveOrUpdate(wholeMachinePO)).thenReturn(true);

        wholeMachineService.reject(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_REJECT.getValue(),wholeMachine.getStatus());
    }

    @Test
    public void testClose() {
        HardwareWholeMachine wholeMachine = getWholeMachine(HardwareValueEnum.NODE_CLOSE.getValue());
        HardwareWholeMachinePO wholeMachinePO = applywholeMachinePO(HardwareValueEnum.NODE_CLOSE.getValue());

        Mockito.when(hardwareFactory.createWholeMachinePO(wholeMachine)).thenReturn(wholeMachinePO);
        Mockito.when(wholeMachineRepository.saveOrUpdate(wholeMachinePO)).thenReturn(true);

        wholeMachineService.close(wholeMachine);

        Assertions.assertEquals(HardwareValueEnum.NODE_CLOSE.getValue(),wholeMachine.getStatus());
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

    private HardwareWholeMachinePO applywholeMachinePO(String status) {
        HardwareWholeMachinePO wholeMachinePO = new HardwareWholeMachinePO();
        wholeMachinePO.setId(HARDWARE_ID);
        wholeMachinePO.setFactoryZy("华为");
        wholeMachinePO.setFactoryEn("huawei");
        wholeMachinePO.setHardwareModel("TaiShan 200 (Model 2280)");
        wholeMachinePO.setOsVersion("openEuler 22.03 LTS");
        wholeMachinePO.setArchitecture("aarch64");
        wholeMachinePO.setDate("2020/12/04");
        wholeMachinePO.setStatus(status);
        return wholeMachinePO;
    }
}
