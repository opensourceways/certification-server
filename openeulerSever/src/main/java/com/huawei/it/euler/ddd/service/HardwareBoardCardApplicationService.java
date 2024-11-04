/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HardwareBoardCardApplicationService {

    @Autowired
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Autowired
    private HardwareApprovalNodeRepositoryImpl approvalNodeRepository;

    @Autowired
    private HardwareFactory hardwareFactory;

    public InsertResponse insert(HardwareBoardCardAddCommand addCommand, String uuid) {
        InsertResponse response = new InsertResponse();

        HardwareBoardCard boardCard = hardwareFactory.createBoardCard(addCommand);

        response.setUnique(boardCard.toSimpleJsonString());

        HardwareBoardCard exist = boardCardRepository.getOne(boardCard);
        if (exist != null) {
            response.setSuccess(false);
            response.setMessage("当前板卡已存在！");
            return response;
        }

        boardCard.create(uuid);

        boardCardRepository.save(boardCard);

        response.setSuccess(true);
        response.setMessage("插入成功!");
        return response;
    }

    public BatchInsertResponse batchInsert(List<HardwareBoardCardAddCommand> addCommandList, String uuid) {
        List<InsertResponse> insertResponseList = addCommandList.stream().map(addCommand -> this.insert(addCommand, uuid)).toList();
        int successCount = (int) insertResponseList.stream().filter(InsertResponse::isSuccess).count();
        int failureCount = addCommandList.size() - successCount;
        BatchInsertResponse batchInsertResponse = new BatchInsertResponse();
        batchInsertResponse.setResults(insertResponseList);
        batchInsertResponse.setSuccessCount(successCount);
        batchInsertResponse.setFailureCount(failureCount);
        return batchInsertResponse;
    }

    public HardwareBoardCard getById(Integer id) {
        return boardCardRepository.find(id);
    }

    public List<HardwareBoardCard> getList(HardwareBoardCardSelectVO selectVO) {
        return boardCardRepository.getList(selectVO);
    }

    public Page<HardwareBoardCard> getPage(HardwareBoardCardSelectVO selectVO) {
        return boardCardRepository.getPage(selectVO);
    }

    public void edit(HardwareBoardCardEditCommand editCommand, String uuid) {
        HardwareBoardCard boardCard = hardwareFactory.createBoardCard(editCommand);
        HardwareBoardCard existBoardCard = boardCardRepository.getOne(boardCard);

        if (!boardCard.getId().equals(existBoardCard.getId())) {
            throw new BusinessException("板卡[" + existBoardCard.toSimpleJsonString() + "]已存在！");
        }

        if (!uuid.equals(boardCard.getUserUuid())){
            throw new BusinessException("无权限编辑该板卡数据！");
        }

        boardCard.edit();
        boardCardRepository.save(boardCard);
    }

    public void delete(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        if (!approvalNode.getHardwareId().toString().equals(boardCard.getUserUuid())){
            throw new BusinessException("无权限删除该板卡数据！");
        }

        approvalNode.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                boardCard.getStatus(), HardwareValueEnum.RESULT_DELETE.getValue());

        boardCard.delete();
        boardCardRepository.save(boardCard);
        approvalNodeRepository.save(approvalNode);
    }

    public void apply(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                boardCard.getStatus(), HardwareValueEnum.RESULT_PASS.getValue());

        boardCard.apply();
        boardCardRepository.save(boardCard);
        approvalNodeRepository.save(approvalNode);
    }

    public void close(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                boardCard.getStatus(), HardwareValueEnum.RESULT_CLOSE.getValue());

        boardCard.close();
        boardCardRepository.save(boardCard);
        approvalNodeRepository.save(approvalNode);
    }

    public void pass(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                boardCard.getStatus(), HardwareValueEnum.RESULT_PASS.getValue());

        boardCard.pass();
        boardCardRepository.save(boardCard);
        approvalNodeRepository.save(approvalNode);
    }

    public void reject(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                boardCard.getStatus(), HardwareValueEnum.RESULT_REJECT.getValue());

        boardCard.reject();
        boardCardRepository.save(boardCard);
        approvalNodeRepository.save(approvalNode);
    }
}
