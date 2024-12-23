/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.file.AttachmentRepositoryImpl;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.ddd.interfaces.HardwareBoardCardDto;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import com.huawei.it.euler.util.FileUtils;
import com.huawei.it.euler.util.S3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private AttachmentRepositoryImpl attachmentRepository;

    @Autowired
    private S3Utils s3Utils;


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

        boardCard = boardCardRepository.save(boardCard);

        response.setUnique(String.valueOf(boardCard.getId()));
        response.setSuccess(true);
        response.setMessage("插入成功!");
        return response;
    }

    public InsertResponse insertTemp(HardwareBoardCardAddCommand addCommand, String uuid) {
        InsertResponse response = new InsertResponse();

        HardwareBoardCard boardCard = hardwareFactory.createBoardCard(addCommand);

        response.setUnique(boardCard.toSimpleJsonString());

        HardwareBoardCard exist = boardCardRepository.getOne(boardCard);
        if (exist != null) {
            response.setSuccess(false);
            response.setMessage("当前板卡已存在！");
            return response;
        }

        boardCard.createTemp(uuid);

        boardCard = boardCardRepository.save(boardCard);

        response.setUnique(String.valueOf(boardCard.getId()));
        response.setSuccess(true);
        response.setMessage("插入成功!");
        return response;
    }

    public ExcelInfoVo uploadTemplate(MultipartFile file, String uuid) throws InputException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 1, "", uuid);
        String fileSize = file.getSize() / 1000 + "KB";
        attachmentRepository.save(fileModel);
        return new ExcelInfoVo(fileModel.getFileId(), fileModel.getFileName(), fileSize);
    }

    @Transactional
    public BatchInsertResponse readJsonData(String fileId, String uuid) {
        try (InputStream inputStream = s3Utils.downloadFile(fileId)) {
            if (inputStream == null) {
                throw new BusinessException("Json文件读取失败！");
            }

            String jsonStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            List<HardwareBoardCardEditCommand> hardwareBoardCardAddCommands = JSONObject.parseArray(jsonStr, HardwareBoardCardEditCommand.class);
            return batchInsert(hardwareBoardCardAddCommands, uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BatchInsertResponse batchInsert(List<HardwareBoardCardEditCommand> commandList, String uuid) {
        int successCount = 0;
        int failureCount = 0;
        List<InsertResponse> commandResponseList = new ArrayList<>();
        List<HardwareBoardCardEditCommand> editCommandList = commandList.stream().filter(command -> command.getId() > 0).toList();
        for (HardwareBoardCardEditCommand boardCardEditCommand : editCommandList) {
            InsertResponse response = new InsertResponse();
            response.setUnique(String.valueOf(boardCardEditCommand.getId()));
            try {
                edit(boardCardEditCommand, uuid);
                response.setSuccess(true);
                response.setMessage("操作成功!");
                successCount++;
            } catch (Exception e) {
                response.setSuccess(true);
                response.setMessage("操作失败!");
                failureCount++;
            }
            commandResponseList.add(response);
        }
        List<HardwareBoardCardAddCommand> addCommandList = commandList.stream().filter(command -> command.getId() == 0).map(hardwareFactory::createAddCommand).toList();
        if (!addCommandList.isEmpty()){
            List<InsertResponse> insertResponseList = addCommandList.stream().map(addCommand -> this.insert(addCommand, uuid)).toList();
            commandResponseList.addAll(insertResponseList);
            successCount += (int) insertResponseList.stream().filter(InsertResponse::isSuccess).count();
            failureCount += (int) insertResponseList.stream().filter(insertResponse -> !insertResponse.isSuccess()).count();
        }
        BatchInsertResponse batchInsertResponse = new BatchInsertResponse();
        batchInsertResponse.setResults(commandResponseList);
        batchInsertResponse.setSuccessCount(successCount);
        batchInsertResponse.setFailureCount(failureCount);
        return batchInsertResponse;
    }

    public HardwareBoardCard getById(Integer id) {
        HardwareBoardCard boardCard = boardCardRepository.find(id);
        HardwareApprovalNodeSelectVO approvalNodeSelectVO = new HardwareApprovalNodeSelectVO();
        approvalNodeSelectVO.setHardwareId(id);
        approvalNodeSelectVO.setHardwareType(HardwareValueEnum.TYPE_BOARD_CARD.getValue());
        List<HardwareApprovalNode> nodeList = approvalNodeRepository.getList(approvalNodeSelectVO);
        if (!nodeList.isEmpty()){
            List<HardwareApprovalNode> orderdList = nodeList.stream().sorted(Comparator.comparing(HardwareApprovalNode::getHandlerTime).reversed()).toList();
            boardCard.setLastApproval(orderdList.get(0));
        }
        return boardCard;
    }

    public List<HardwareBoardCard> getList(HardwareBoardCardSelectVO selectVO) {
        return boardCardRepository.getList(selectVO);
    }

    public PageResult<HardwareBoardCard> getPage(HardwareBoardCardSelectVO selectVO) {
        Page<HardwareBoardCard> boardCardPage = boardCardRepository.getPage(selectVO);
        JSONObject filter = boardCardRepository.getFilter(selectVO);
        PageResult<HardwareBoardCard> pageResult = new PageResult<>();
        BeanUtils.copyProperties(boardCardPage, pageResult);
        pageResult.setFilterData(filter);
        return pageResult;
    }

    public void edit(HardwareBoardCardEditCommand editCommand, String uuid) {
        HardwareBoardCard boardCard = hardwareFactory.createBoardCard(editCommand);
        HardwareBoardCard existBoardCard = boardCardRepository.getOne(boardCard);

        if (existBoardCard == null) {
            existBoardCard = boardCardRepository.find(editCommand.getId());
        }

        if (!boardCard.getId().equals(existBoardCard.getId())) {
            throw new BusinessException("板卡[" + existBoardCard.toSimpleJsonString() + "]已存在！");
        }

        if (!uuid.equals(existBoardCard.getUserUuid())) {
            throw new BusinessException("无权限编辑该板卡数据！");
        }

        HardwareBoardCardEditCommand boardCardEditCommand = hardwareFactory.createEditCommand(existBoardCard);
        if (boardCardEditCommand.equals(editCommand)){
            return;
        }

        BeanUtils.copyProperties(editCommand, existBoardCard);
        existBoardCard.edit();
        boardCardRepository.save(existBoardCard);
    }

    public void delete(HardwareApprovalNode approvalNode) {
        HardwareBoardCard boardCard = boardCardRepository.find(approvalNode.getHardwareId());

        if (!approvalNode.getHandlerUuid().toString().equals(boardCard.getUserUuid())) {
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

    @Transactional
    public void batchApproval(HardwareApprovalBatchCommand batchCommand) {
        List<Integer> hardwareIdList = batchCommand.getHardwareIdList();
        for (Integer hardwareId : hardwareIdList) {
            HardwareApprovalNode approvalNode = new HardwareApprovalNode();
            approvalNode.setHardwareId(hardwareId);
            BeanUtils.copyProperties(batchCommand, approvalNode);
            if (HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(batchCommand.getHandlerNode())) {
                if (HardwareValueEnum.RESULT_PASS.getValue().equals(batchCommand.getHandlerResult())) {
                    apply(approvalNode);
                } else if (HardwareValueEnum.RESULT_DELETE.getValue().equals(batchCommand.getHandlerResult())) {
                    delete(approvalNode);
                } else {
                    throw new BusinessException("当前节点无法执行该操作！");
                }
            } else if (HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(batchCommand.getHandlerNode())) {
                if (HardwareValueEnum.RESULT_PASS.getValue().equals(batchCommand.getHandlerResult())) {
                    pass(approvalNode);
                } else if (HardwareValueEnum.RESULT_REJECT.getValue().equals(batchCommand.getHandlerResult())) {
                    reject(approvalNode);
                } else if (HardwareValueEnum.RESULT_CLOSE.getValue().equals(batchCommand.getHandlerResult())) {
                    close(approvalNode);
                } else {
                    throw new BusinessException("当前节点无法执行该操作！");
                }
            } else {
                throw new BusinessException("当前节点无法操作！");
            }
        }
    }

    public Page<HardwareBoardCardDto> pageForCompatibilityList(HardwareBoardCardSelectVO selectVO) {
        selectVO.setSecurityLevel("0");
        selectVO.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        Page<HardwareBoardCard> boardCardPage = boardCardRepository.getPage(selectVO);

        Page<HardwareBoardCardDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(boardCardPage, dtoPage);
        dtoPage.setRecords(boardCardPage.getRecords().stream().map(hardwareFactory::createDto).toList());
        return dtoPage;
    }
}
