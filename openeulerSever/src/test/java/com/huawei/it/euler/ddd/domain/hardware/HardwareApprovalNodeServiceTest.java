/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
public class HardwareApprovalNodeServiceTest {

    private static final int HARDWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareApprovalNodeRepositoryImpl nodeRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HardwareFactory hardwareFactory;

    @InjectMocks
    private HardwareApprovalNodeService nodeService;

    @Test
    public void testGetList(){
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareApprovalNode> approvalNodeList = new ArrayList<>();
        approvalNodeList.add(approvalNode);

        HardwareApprovalNodePO approvalNodePO = new HardwareApprovalNodePO();
        approvalNodePO.setHardwareId(HARDWARE_ID);
        approvalNodePO.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNodePO.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        List<HardwareApprovalNodePO> nodePOList = new ArrayList<>();
        nodePOList.add(approvalNodePO);

        HardwareApprovalNodeSelectVO selectVO = new HardwareApprovalNodeSelectVO();
        selectVO.setHardwareId(HARDWARE_ID);
        selectVO.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());

        Mockito.when(nodeRepository.list((Wrapper<HardwareApprovalNodePO>) any())).thenReturn(nodePOList);
        Mockito.when(hardwareFactory.createApprovalNode(approvalNodePO)).thenReturn(approvalNode);

        List<HardwareApprovalNode> nodeList = nodeService.getList(selectVO);

        Assertions.assertEquals(approvalNodeList,nodeList);
    }

    @Test
    public void testInsert(){
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(HARDWARE_ID);
        approvalNode.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNode.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        HardwareApprovalNodePO approvalNodePO = new HardwareApprovalNodePO();
        approvalNodePO.setHardwareId(HARDWARE_ID);
        approvalNodePO.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNodePO.setHandlerNode(HardwareValueEnum.NODE_WAIT_APPLY.getValue());

        Mockito.when(hardwareFactory.createApprovalNodePO(approvalNode)).thenReturn(approvalNodePO);
        Mockito.when(nodeRepository.save(approvalNodePO)).thenReturn(true);

        boolean insert = nodeService.insert(approvalNode);

        Assertions.assertTrue(insert);
    }
}
