/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HardwareBoardCardApplicationService {

    @Autowired
    private HardwareBoardCardService boardCardService;

    @Autowired
    private HardwareApprovalNodeService approvalNodeService;

    @Transactional
    public HardwareBoardCard insert(HardwareBoardCard boardCard, String uuid) {
        boolean exist = boardCardService.exist(boardCard);
        if (exist) {
            String errMsg = String.format("当前板卡[%s]已存在！", boardCard.toSimpleJsonString());
            throw new ParamException(errMsg);
        }

        boardCard.setUserUuid(uuid);
        boardCard.setApplyTime(new Date());

        return boardCardService.insert(boardCard);
    }

    @Transactional
    public List<HardwareBoardCard> batchInsert(List<HardwareBoardCard> boardCardList, String uuid){
        List<HardwareBoardCard> unExist = new ArrayList<>();
        for (HardwareBoardCard boardCard : boardCardList) {
            boolean exist = boardCardService.exist(boardCard);
            if (exist) {
                String errMsg = String.format("当前板卡[%s]已存在！", boardCard.toSimpleJsonString());
                log.error(errMsg);
                continue;
            }
            boardCard.setUserUuid(uuid);
            boardCard.setApplyTime(new Date());
            unExist.add(boardCard);
        }
        boolean batchInsert = boardCardService.batchInsert(unExist);
        if (!batchInsert){
            throw new BusinessException("板卡数据批量申请失败，请稍后重试！");
        }
        return unExist;
    }

    public HardwareBoardCard getById(Integer id) {
        return boardCardService.getById(id);
    }

    public Page<HardwareBoardCard> getPage(HardwareBoardCardSelectVO selectVO) {
        return boardCardService.getPage(selectVO);
    }

    public void edit(HardwareBoardCard boardCard) {
        HardwareBoardCard byId = boardCardService.getById(boardCard.getId());
        if (byId == null) {
            throw new ParamException("当前板卡数据不存在！");
        }
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(byId.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(byId.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行编辑操作！");
        }
        boardCardService.updateById(boardCard);
    }

    public void delete(HardwareApprovalNode approvalNode){
        HardwareBoardCard boardCard = boardCardService.getById(approvalNode.getHardwareId());
        if (boardCard == null) {
            throw new ParamException("当前板卡数据不存在！");
        }
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(boardCard.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(boardCard.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行删除操作！");
        }
        if (boardCard.getRefCount() > 0) {
            throw new BusinessException("当前板卡关联整机，无法删除！");
        }
        boardCardService.updateById(boardCard.delete());
    }

    public void apply(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardService.getById(approvalNode.getHardwareId());
        if (boardCard == null) {
            throw new ParamException("当前板卡数据不存在！");
        }
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(boardCard.getStatus())){
            throw new BusinessException("当前板卡数据状态无法进行申请操作！");
        }
        boardCardService.apply(boardCard);
        approvalNode.apply(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNodeService.insert(approvalNode);
    }

    public void approval(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardService.getById(approvalNode.getHardwareId());
        if (boardCard == null) {
            throw new ParamException("当前板卡数据不存在！");
        }
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(boardCard.getStatus())){
            throw new BusinessException("当前板卡数据状态无法进行审批操作！");
        }
        if (HardwareValueEnum.RESULT_PASS.getValue().equals(approvalNode.getHandlerResult())) {
            boardCardService.pass(boardCard);
        } else if (HardwareValueEnum.RESULT_REJECT.getValue().equals(approvalNode.getHandlerResult())) {
            boardCardService.reject(boardCard);
        }
        approvalNode.approval(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        approvalNodeService.insert(approvalNode);
    }
}
