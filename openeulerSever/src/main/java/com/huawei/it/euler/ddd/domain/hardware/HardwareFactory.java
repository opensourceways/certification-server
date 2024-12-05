/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.huawei.it.euler.ddd.interfaces.HardwareBoardCardDto;
import com.huawei.it.euler.ddd.interfaces.HardwareWholeMachineDto;
import com.huawei.it.euler.ddd.interfaces.HardwareWholeMachineListDto;
import com.huawei.it.euler.ddd.service.*;
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

    // approval node
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

    // hardware board card
    public HardwareBoardCard createBoardCard(HardwareBoardCardPO boardCardPO) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        BeanUtils.copyProperties(boardCardPO, boardCard);
        return boardCard;
    }

    public HardwareBoardCard createBoardCard(HardwareBoardCardAddCommand addCommand) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        BeanUtils.copyProperties(addCommand, boardCard);
        return boardCard;
    }

    public HardwareBoardCard createBoardCard(HardwareBoardCardEditCommand editCommand) {
        HardwareBoardCard boardCard = new HardwareBoardCard();
        BeanUtils.copyProperties(editCommand, boardCard);
        return boardCard;
    }

    public HardwareBoardCardAddCommand createAddCommand(HardwareBoardCardEditCommand editCommand) {
        HardwareBoardCardAddCommand addCommand = new HardwareBoardCardAddCommand();
        BeanUtils.copyProperties(editCommand, addCommand);
        return addCommand;
    }

    public HardwareBoardCardEditCommand createEditCommand(HardwareBoardCard boardCard) {
        HardwareBoardCardEditCommand editCommand = new HardwareBoardCardEditCommand();
        BeanUtils.copyProperties(boardCard, editCommand);
        return editCommand;
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

    // hardware whole machine
    public HardwareWholeMachine createWholeMachine(HardwareWholeMachinePO wholeMachinePO) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        BeanUtils.copyProperties(wholeMachinePO, wholeMachine);
        HardwareCompatibilityConfiguration compatibilityConfiguration = new HardwareCompatibilityConfiguration();
        BeanUtils.copyProperties(wholeMachinePO, compatibilityConfiguration);
        wholeMachine.setCompatibilityConfiguration(compatibilityConfiguration);
        return wholeMachine;
    }

    public HardwareWholeMachine createWholeMachine(HardwareWholeMachineAddCommand addCommand) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        BeanUtils.copyProperties(addCommand, wholeMachine);
        HardwareCompatibilityConfiguration compatibilityConfiguration = new HardwareCompatibilityConfiguration();
        BeanUtils.copyProperties(addCommand, compatibilityConfiguration);
        wholeMachine.setCompatibilityConfiguration(compatibilityConfiguration);
        wholeMachine.setBoardCards(addCommand.getBoardCardAddCommandList().stream().
                filter(HardwareBoardCardAddCommand::canCreated).map(this::createBoardCard).toList());
        return wholeMachine;
    }

    public HardwareWholeMachine createWholeMachine(HardwareWholeMachineEditCommand editCommand) {
        HardwareWholeMachine wholeMachine = new HardwareWholeMachine();
        BeanUtils.copyProperties(editCommand, wholeMachine);
        HardwareCompatibilityConfiguration compatibilityConfiguration = new HardwareCompatibilityConfiguration();
        BeanUtils.copyProperties(editCommand, compatibilityConfiguration);
        wholeMachine.setCompatibilityConfiguration(compatibilityConfiguration);
        wholeMachine.setBoardCards(editCommand.getBoardCardEditCommandList().stream().map(this::createBoardCard).toList());
        return wholeMachine;
    }

    public List<HardwareWholeMachine> createWholeMachineList(List<HardwareWholeMachinePO> wholeMachinePOList) {
        return wholeMachinePOList.stream().map(this::createWholeMachine).toList();
    }

    public HardwareWholeMachinePO createWholeMachinePO(HardwareWholeMachine wholeMachine) {
        HardwareWholeMachinePO wholeMachinePO = new HardwareWholeMachinePO();
        BeanUtils.copyProperties(wholeMachine, wholeMachinePO);
        BeanUtils.copyProperties(wholeMachine.getCompatibilityConfiguration(), wholeMachinePO);
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

    public List<HardwareWholeMachineListDto> createDtoList(List<HardwareWholeMachine> wholeMachineList) {
        List<HardwareWholeMachineListDto> listDtoList = new ArrayList<>();
        for (HardwareWholeMachine wholeMachine : wholeMachineList) {
            HardwareWholeMachineListDto listDto = new HardwareWholeMachineListDto();
            BeanUtils.copyProperties(wholeMachine, listDto);
            listDto.setCpu(wholeMachine.getCompatibilityConfiguration().getCpu());
            listDtoList.add(listDto);
        }
        return listDtoList;
    }

    public HardwareWholeMachineDto createDto(HardwareWholeMachine wholeMachine) {
        HardwareWholeMachineDto dto = new HardwareWholeMachineDto();
        BeanUtils.copyProperties(wholeMachine, dto);
        HardwareCompatibilityConfiguration compatibilityConfiguration = new HardwareCompatibilityConfiguration();
        BeanUtils.copyProperties(wholeMachine, compatibilityConfiguration);
        wholeMachine.setCompatibilityConfiguration(compatibilityConfiguration);
        return dto;
    }

    public HardwareBoardCardDto createDto(HardwareBoardCard boardCard) {
        HardwareBoardCardDto dto = new HardwareBoardCardDto();
        BeanUtils.copyProperties(boardCard, dto);
        return dto;
    }
}
