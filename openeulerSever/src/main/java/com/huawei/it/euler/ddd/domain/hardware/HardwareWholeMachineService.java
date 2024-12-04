/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 硬件-整机 服务实现类
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Slf4j
@Service
public class HardwareWholeMachineService {

    public HardwareWholeMachine refBoardCard2WholeMachine(HardwareWholeMachine wholeMachine, List<HardwareBoardCard> boardCardList) {
        List<Integer> boardCardIdList = boardCardList.stream().map(HardwareBoardCard::getId).toList();
        wholeMachine.setBoardCardIds(StringUtils.join(boardCardIdList,","));
        boardCardList = boardCardList.stream().map(boardCard -> boardCard.addWholeMachine(wholeMachine.getId())).toList();
        wholeMachine.setBoardCards(boardCardList);
        return wholeMachine;
    }

    public void delete(HardwareWholeMachine wholeMachine) {
        List<HardwareBoardCard> boardCardList = wholeMachine.getBoardCards();
        for (HardwareBoardCard boardCard : boardCardList) {
            boardCard.removeWholeMachine(wholeMachine.getId());
            if (StringUtils.isEmpty(boardCard.getWholeMachineIds())){
                boardCard.delete();
            }
        }
        wholeMachine.setBoardCards(boardCardList);
        wholeMachine.delete();
    }

    public void close(HardwareWholeMachine wholeMachine) {
        List<HardwareBoardCard> boardCardList = wholeMachine.getBoardCards();
        for (HardwareBoardCard boardCard : boardCardList) {
            boardCard.removeWholeMachine(wholeMachine.getId());
            if (HardwareValueEnum.NODE_TEMP.getValue().equals(boardCard.getStatus())){
                boardCard.close();
            }
        }
        wholeMachine.close();
    }

    public void pass(HardwareWholeMachine wholeMachine) {
        List<HardwareBoardCard> boardCardList = wholeMachine.getBoardCards();
        for (HardwareBoardCard boardCard : boardCardList) {
            if (HardwareValueEnum.NODE_TEMP.getValue().equals(boardCard.getStatus())){
                boardCard.pass();
            }
        }
        wholeMachine.pass();
    }
}
