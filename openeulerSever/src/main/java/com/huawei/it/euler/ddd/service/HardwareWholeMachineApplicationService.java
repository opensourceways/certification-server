/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HardwareWholeMachineApplicationService {

    @Autowired
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Autowired
    private HardwareWholeMachineRepositoryImpl wholeMachineRepository;

    @Autowired
    private HardwareApprovalNodeRepositoryImpl approvalNodeRepository;

    @Autowired
    private HardwareWholeMachineService wholeMachineService;

    @Autowired
    private HardwareFactory hardwareFactory;

    @Transactional
    public InsertResponse insert(HardwareWholeMachineAddCommand addCommand, String uuid) {
        InsertResponse response = new InsertResponse();

        HardwareWholeMachine wholeMachine = hardwareFactory.createWholeMachine(addCommand);

        response.setUnique(wholeMachine.toSimpleJsonString());

        HardwareWholeMachine exist = wholeMachineRepository.getOne(wholeMachine);
        if (exist != null) {
            response.setSuccess(false);
            response.setMessage("当前整机已存在！");
            return response;
        }

        wholeMachine.setUserUuid(uuid);
        wholeMachine.setApplyTime(new Date());

        HardwareWholeMachine insert = wholeMachineRepository.save(wholeMachine);
        if (insert == null) {
            response.setSuccess(false);
            response.setMessage("当前整机新增失败！");
            return response;
        }

        List<HardwareBoardCard> boardCardList = boardCardRepository.findOrSaveTemp(wholeMachine.getBoardCards(),insert.getId());
        insert = wholeMachineService.refBoardCard2WholeMachine(insert, boardCardList);

        wholeMachineRepository.save(insert);
        boardCardRepository.saveBatch(insert.getBoardCards());

        response.setSuccess(true);
        response.setMessage("插入成功!");
        return response;
    }

    @Transactional
    public BatchInsertResponse batchInsert(List<HardwareWholeMachineAddCommand> addCommandList, String uuid) {
        List<InsertResponse> insertResponseList = addCommandList.stream().map(addCommand -> this.insert(addCommand, uuid)).toList();
        int successCount = (int) insertResponseList.stream().filter(InsertResponse::isSuccess).count();
        int failureCount = addCommandList.size() - successCount;
        BatchInsertResponse batchInsertResponse = new BatchInsertResponse();
        batchInsertResponse.setResults(insertResponseList);
        batchInsertResponse.setSuccessCount(successCount);
        batchInsertResponse.setFailureCount(failureCount);
        return batchInsertResponse;
    }

    public HardwareWholeMachine getById(Integer id) {
        HardwareWholeMachine hardwareWholeMachine = wholeMachineRepository.find(id);
        HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
        selectVO.setIdList(Arrays.stream(hardwareWholeMachine.getBoardCardIds().split(",")).toList());
        List<HardwareBoardCard> boardCardList = boardCardRepository.getList(selectVO);
        hardwareWholeMachine.setBoardCards(boardCardList);
        return hardwareWholeMachine;
    }

    public Page<HardwareWholeMachine> getPage(HardwareWholeMachineSelectVO selectVO) {
        return wholeMachineRepository.getPage(selectVO);
    }

    public void edit(HardwareWholeMachineEditCommand editCommand, String uuid) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(editCommand.getId());

        if (uuid.equals(wholeMachine.getUserUuid())) {
            throw new BusinessException("无权限编辑该整机数据");
        }

        List<HardwareBoardCardEditCommand> boardCardEditCommandList = editCommand.getBoardCardEditCommandList();
        for (HardwareBoardCardEditCommand boardCardEditCommand : boardCardEditCommandList) {
            HardwareBoardCard boardCard = boardCardRepository.getOne(hardwareFactory.createBoardCard(boardCardEditCommand));
            BeanUtils.copyProperties(boardCardEditCommand, boardCard);
            boardCard.edit();
            boardCardRepository.save(boardCard);
        }

        BeanUtils.copyProperties(editCommand, wholeMachine);
        wholeMachine.edit();
        wholeMachineRepository.save(wholeMachine);
    }

    public void delete(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = getById(approvalNode.getHardwareId());
        if (approvalNode.getHardwareId().toString().equals(wholeMachine.getUserUuid())){
            throw new BusinessException("无权限编辑该整机数据");
        }

        wholeMachineService.delete(wholeMachine);

        boardCardRepository.saveBatch(wholeMachine.getBoardCards());
        wholeMachineRepository.save(wholeMachine);
    }

    public void apply(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(),HardwareValueEnum.RESULT_PASS.getValue());

        wholeMachine.apply();

        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void close(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(),HardwareValueEnum.RESULT_PASS.getValue());

        wholeMachineService.close(wholeMachine);

        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void pass(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(),HardwareValueEnum.RESULT_PASS.getValue());

        wholeMachineService.pass(wholeMachine);

        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void reject(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(),HardwareValueEnum.RESULT_REJECT.getValue());

        wholeMachine.reject();

        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }
}
