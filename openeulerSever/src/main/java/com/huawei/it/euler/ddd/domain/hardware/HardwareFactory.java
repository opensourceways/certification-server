/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 硬件-工厂类
 *
 * @author zhaoyan
 * @since 2024-10-08
 */
@Component
public class HardwareFactory {

    public HardwareApprovalNode createApprovalNode(HardwareApprovalNodePO approvalNodePO) {
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        BeanUtils.copyProperties(approvalNodePO, approvalNode);
        return approvalNode;
    }

    public HardwareApprovalNodePO createApprovalNodePO(HardwareApprovalNode approvalNode) {
        HardwareApprovalNodePO approvalNodePO = new HardwareApprovalNodePO();
        BeanUtils.copyProperties(approvalNode, approvalNodePO);
        return approvalNodePO;
    }

    public HardwareBoardCard createBoardCard(HardwareBoardCardPO boardCardPO) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        BeanUtils.copyProperties(boardCardPO, boardCard);
        return boardCard;
    }

    public List<HardwareBoardCard> createBoardCardList(List<HardwareBoardCardPO> boardCardPOList) {
        return boardCardPOList.stream().map(this::createBoardCard).toList();
    }

    public HardwareBoardCardPO createBoardCardPO(HardwareBoardCard boardCard) {
        HardwareBoardCardPO boardCardPO = new HardwareBoardCardPO();
        BeanUtils.copyProperties(boardCard, boardCardPO);
        return boardCardPO;
    }

    public List<HardwareBoardCardPO> createBoardCardPOList(List<HardwareBoardCard> boardCardList) {
        return boardCardList.stream().map(this::createBoardCardPO).toList();
    }

    public HardwareBoardCardSelectVO createBoardCardSelectVO(HardwareBoardCard boardCard) {
        HardwareBoardCardSelectVO boardCardSelectVO = new HardwareBoardCardSelectVO();
        BeanUtils.copyProperties(boardCard, boardCardSelectVO);
        return boardCardSelectVO;
    }

    public HardwareWholeMachine createWholeMachine(HardwareWholeMachinePO wholeMachinePO) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        BeanUtils.copyProperties(wholeMachinePO, wholeMachine);
        HardwareCompatibilityConfiguration compatibilityConfiguration = new HardwareCompatibilityConfiguration();
        BeanUtils.copyProperties(wholeMachinePO, compatibilityConfiguration);
        wholeMachine.setCompatibilityConfiguration(compatibilityConfiguration);
        return wholeMachine;
    }

    public List<HardwareWholeMachine> createWholeMachineList(List<HardwareWholeMachinePO> wholeMachinePOList) {
        List<HardwareWholeMachine> wholeMachineList = new ArrayList<>();
        for (HardwareWholeMachinePO wholeMachinePO : wholeMachinePOList) {
            HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
            BeanUtils.copyProperties(wholeMachinePO, wholeMachine);
            wholeMachineList.add(wholeMachine);
        }
        return wholeMachineList;
    }

    public HardwareWholeMachinePO createWholeMachinePO(HardwareWholeMachine wholeMachine) {
        HardwareWholeMachinePO wholeMachinePO = new HardwareWholeMachinePO();
        BeanUtils.copyProperties(wholeMachine, wholeMachinePO);
        BeanUtils.copyProperties(wholeMachine.getCompatibilityConfiguration(), wholeMachinePO);
        List<Integer> boardCardIdList = wholeMachine.getBoardCardList().stream().map(HardwareBoardCard::getId).toList();
        wholeMachinePO.setBoardCards(StringUtils.join(boardCardIdList, ","));
        return wholeMachinePO;
    }

    public List<HardwareWholeMachinePO> createWholeMachinePOList(List<HardwareWholeMachine> wholeMachineList) {
        return wholeMachineList.stream().map(this::createWholeMachinePO).toList();
    }

    public HardwareWholeMachineSelectVO createWholeMachineSelectVO(HardwareWholeMachine wholeMachine) {
        HardwareWholeMachineSelectVO wholeMachineSelectVO = new HardwareWholeMachineSelectVO();
        BeanUtils.copyProperties(wholeMachine, wholeMachineSelectVO);
        return wholeMachineSelectVO;
    }
}
