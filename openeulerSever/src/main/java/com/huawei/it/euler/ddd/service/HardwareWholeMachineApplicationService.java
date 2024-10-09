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
public class HardwareWholeMachineApplicationService {

    @Autowired
    private HardwareWholeMachineService wholeMachineService;

    @Autowired
    private HardwareBoardCardService boardCardService;

    @Autowired
    private HardwareApprovalNodeService approvalNodeService;

    @Transactional
    public HardwareWholeMachine insert(HardwareWholeMachine wholeMachine, String uuid) {
        boolean exist = wholeMachineService.exist(wholeMachine);
        if (exist) {
            String errMsg = String.format("当前整机[%s]已存在！", wholeMachine.toSimpleJsonString());
            throw new ParamException(errMsg);
        }

        List<HardwareBoardCard> saveBoardCardList = new ArrayList<>();
        List<HardwareBoardCard> boardCardList = wholeMachine.getBoardCardList();
        for (HardwareBoardCard hardwareBoardCard : boardCardList) {
            boolean cardExist = boardCardService.exist(hardwareBoardCard);
            if (cardExist){
                List<HardwareBoardCard> cardList = boardCardService.getList(hardwareBoardCard);
                saveBoardCardList.add(cardList.get(0));
            } else {
                boolean insert = boardCardService.insert(hardwareBoardCard);
                if (!insert) {
                    String errMsg = String.format("整机板卡[%s]申请失败！", hardwareBoardCard.toSimpleJsonString());
                    throw new BusinessException(errMsg);
                }
                saveBoardCardList.add(hardwareBoardCard);
            }
        }
        wholeMachine.setBoardCardList(saveBoardCardList);

        wholeMachine.setUserUuid(uuid);
        wholeMachine.setApplyTime(new Date());

        boolean insert = wholeMachineService.insert(wholeMachine);
        if (!insert){
            String errMsg = String.format("当前整机[%s]申请失败！", wholeMachine.toSimpleJsonString());
            throw new BusinessException(errMsg);
        }
        return wholeMachine;
    }

    @Transactional
    public HardwareWholeMachine insertPassed(HardwareWholeMachine wholeMachine, String uuid) {
        HardwareWholeMachine insert = insert(wholeMachine, uuid);
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHardwareId(wholeMachine.getId());
        approvalNode.setHandlerUuid(Integer.valueOf(uuid));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());
        approvalNode.setHandlerComment("管理员上传");
        approvalNodeService.insert(approvalNode.approval(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue()));
        return insert;
    }

    @Transactional
    public List<HardwareWholeMachine> batchInsert(List<HardwareWholeMachine> wholeMachineList, String uuid) {
        List<HardwareWholeMachine> unExistList = new ArrayList<>();
        outer:
        for (HardwareWholeMachine wholeMachine : wholeMachineList) {
            boolean exist = wholeMachineService.exist(wholeMachine);
            if (exist) {
                String errMsg = String.format("当前整机[%s]已存在！", wholeMachine.toSimpleJsonString());
                log.error(errMsg);
                continue;
            }
            List<HardwareBoardCard> saveBoardCardList = new ArrayList<>();
            List<HardwareBoardCard> boardCardList = wholeMachine.getBoardCardList();
            for (HardwareBoardCard hardwareBoardCard : boardCardList) {
                boolean cardExist = boardCardService.exist(hardwareBoardCard);
                if (cardExist) {
                    List<HardwareBoardCard> cardList = boardCardService.getList(hardwareBoardCard);
                    saveBoardCardList.add(cardList.get(0));
                } else {
                    boolean insert = boardCardService.insert(hardwareBoardCard);
                    if (!insert) {
                        String errMsg = String.format("整机板卡[%s]申请失败！", hardwareBoardCard.toSimpleJsonString());
                        log.error(errMsg);
                        continue outer;
                    }
                    saveBoardCardList.add(hardwareBoardCard);
                }
            }
            wholeMachine.setBoardCardList(saveBoardCardList);

            wholeMachine.setUserUuid(uuid);
            wholeMachine.setApplyTime(new Date());

            unExistList.add(wholeMachine);
        }
        boolean batchInsert = wholeMachineService.batchInsert(unExistList);
        if (!batchInsert) {
            throw new BusinessException("整机数据批量申请失败，请稍后重试！");
        }
        return unExistList;
    }

    @Transactional
    public List<HardwareWholeMachine> batchInsertPassed(List<HardwareWholeMachine> wholeMachineList, String uuid) {
        List<HardwareWholeMachine> batchInsert = batchInsert(wholeMachineList, uuid);
        HardwareApprovalNode approvalNode = new HardwareApprovalNode();
        approvalNode.setHandlerUuid(Integer.valueOf(uuid));
        approvalNode.setHandlerResult(HardwareValueEnum.RESULT_PASS.getValue());
        approvalNode.setHandlerComment("管理员批量上传");
        approvalNodeService.insert(approvalNode.approval(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue()));
        return batchInsert;
    }

    public HardwareWholeMachine getById(Integer id) {
        return wholeMachineService.getById(id);
    }

    public Page<HardwareWholeMachine> getPage(HardwareWholeMachineSelectVO selectVO) {
        return wholeMachineService.getPage(selectVO);
    }

    public void edit(HardwareWholeMachine wholeMachine) {
        wholeMachineService.updateById(wholeMachine);
    }

    public void apply(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineService.getById(approvalNode.getHardwareId());
        wholeMachineService.apply(wholeMachine);
        approvalNode.apply(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNodeService.insert(approvalNode);
    }

    public void approval(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineService.getById(approvalNode.getHardwareId());
        if (HardwareValueEnum.RESULT_PASS.getValue().equals(approvalNode.getHandlerResult())){
            wholeMachineService.pass(wholeMachine);
        } else if (HardwareValueEnum.RESULT_REJECT.getValue().equals(approvalNode.getHandlerResult())) {
            wholeMachineService.reject(wholeMachine);
        }
        approvalNode.approval(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        approvalNodeService.insert(approvalNode);
    }
}
