/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.file.AttachmentRepositoryImpl;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.ddd.interfaces.HardwareWholeMachineDto;
import com.huawei.it.euler.ddd.interfaces.HardwareWholeMachineListDto;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import com.huawei.it.euler.util.FileUtils;
import com.huawei.it.euler.util.S3Utils;
import com.huaweicloud.sdk.core.utils.StringUtils;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private AttachmentRepositoryImpl attachmentRepository;

    @Autowired
    private S3Utils s3Utils;

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

        wholeMachine.create(uuid);

        HardwareWholeMachine insert = wholeMachineRepository.save(wholeMachine);
        if (insert == null) {
            response.setSuccess(false);
            response.setMessage("当前整机新增失败！");
            return response;
        }

        List<HardwareBoardCard> boardCardList = boardCardRepository.findOrSaveTemp(wholeMachine.getBoardCards(), uuid);
        insert = wholeMachineService.refBoardCard2WholeMachine(insert, boardCardList);

        wholeMachineRepository.save(insert);
        boardCardRepository.saveBatch(insert.getBoardCards());

        response.setUnique(String.valueOf(insert.getId()));
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
            List<HardwareWholeMachineBatchAddCommand> hardwareWholeMachineAddCommands = JSONObject.parseArray(jsonStr, HardwareWholeMachineBatchAddCommand.class);
            return batchInsert(hardwareWholeMachineAddCommands, uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public BatchInsertResponse batchInsert(List<HardwareWholeMachineBatchAddCommand> batchAddCommandList, String uuid) {
        List<HardwareWholeMachineAddCommand> addCommandList = batchAddCommandList.stream().map(batchAddCommand -> hardwareFactory.createWholeMachineAddCommand(batchAddCommand)).toList();
        List<InsertResponse> insertResponseList = addCommandList.stream().map(addCommand -> this.insert(addCommand, uuid)).toList();
        int successCount = (int) insertResponseList.stream().filter(InsertResponse::isSuccess).count();
        int failureCount = batchAddCommandList.size() - successCount;
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
        HardwareApprovalNodeSelectVO approvalNodeSelectVO = new HardwareApprovalNodeSelectVO();
        approvalNodeSelectVO.setHardwareId(id);
        approvalNodeSelectVO.setHardwareType(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue());
        List<HardwareApprovalNode> nodeList = approvalNodeRepository.getList(approvalNodeSelectVO);
        if (!nodeList.isEmpty()) {
            List<HardwareApprovalNode> orderdList = nodeList.stream().sorted(Comparator.comparing(HardwareApprovalNode::getHandlerTime).reversed()).toList();
            hardwareWholeMachine.setLastApproval(orderdList.get(0));
        }
        return hardwareWholeMachine;
    }

    public PageResult<HardwareWholeMachine> getPage(HardwareWholeMachineSelectVO selectVO) {
        Page<HardwareWholeMachine> wholeMachinePage = wholeMachineRepository.getPage(selectVO);
        JSONObject filter = wholeMachineRepository.getFilter(selectVO);
        PageResult<HardwareWholeMachine> pageResult = new PageResult<>();
        BeanUtils.copyProperties(wholeMachinePage, pageResult);
        pageResult.setFilterData(filter);
        return pageResult;
    }

    public List<String> getOs() {
        return wholeMachineRepository.getOs();
    }

    public void edit(HardwareWholeMachineEditCommand editCommand, String uuid) {
        HardwareWholeMachine wholeMachine = hardwareFactory.createWholeMachine(editCommand);
        HardwareWholeMachine existWholeMachine = wholeMachineRepository.getOne(wholeMachine);

        if (existWholeMachine == null) {
            existWholeMachine = wholeMachineRepository.find(editCommand.getId());
        }

        if (!wholeMachine.getId().equals(existWholeMachine.getId())) {
            throw new BusinessException("板卡[" + existWholeMachine.toSimpleJsonString() + "]已存在！");
        }

        if (!uuid.equals(existWholeMachine.getUserUuid())) {
            throw new BusinessException("无权限编辑该整机数据！");
        }

        List<HardwareBoardCardEditCommand> boardCardEditCommandList = editCommand.getBoardCardEditCommandList();
        List<String> removeRefBoardCardIdList = new ArrayList<>();
        List<HardwareBoardCardEditCommand> editBoardCard = boardCardEditCommandList.stream().filter(editCommand1 -> editCommand1.getId() > 0).toList();
        List<HardwareBoardCardEditCommand> saveBoardCard = boardCardEditCommandList.stream().filter(editCommand1 -> editCommand1.getId() == 0).toList();

        List<String> newBoardCardIdList = new ArrayList<>();

        String existBoardCardIds = existWholeMachine.getBoardCardIds();
        if (!StringUtils.isEmpty(existBoardCardIds)) {
            List<String> boardCardIdList = Arrays.stream(existBoardCardIds.split(",")).toList();
            if (!editBoardCard.isEmpty()) {
                newBoardCardIdList.addAll(editBoardCard.stream().map(editCommand1 -> String.valueOf(editCommand1.getId())).toList());

                removeRefBoardCardIdList.addAll(boardCardIdList.stream().filter(id -> !newBoardCardIdList.contains(id)).toList());
            }
        }

        if (!removeRefBoardCardIdList.isEmpty()) {
            HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
            selectVO.setIdList(removeRefBoardCardIdList);
            List<HardwareBoardCard> boardCardList = boardCardRepository.getList(selectVO);
            for (HardwareBoardCard boardCard : boardCardList) {
                boardCard.removeWholeMachine(editCommand.getId());
                try {
                    boardCard.delete();
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
            boardCardRepository.saveBatch(boardCardList);
        }

        if (!editBoardCard.isEmpty()) {
            for (HardwareBoardCardEditCommand boardCardEditCommand : editBoardCard) {
                try {
                    boardCardApplicationService.edit(boardCardEditCommand, uuid);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }

        if (!saveBoardCard.isEmpty()) {
            for (HardwareBoardCardEditCommand boardCardEditCommand : saveBoardCard) {
                HardwareBoardCardAddCommand addCommand = hardwareFactory.createAddCommand(boardCardEditCommand);
                if (addCommand.canCreated()) {
                    InsertResponse insert = boardCardApplicationService.insertTemp(addCommand, uuid);
                    if (!insert.isSuccess()) {
                        throw new BusinessException("关联板卡数据保存失败！");
                    }
                    newBoardCardIdList.add(insert.getUnique());
                }
            }
        }


        BeanUtils.copyProperties(editCommand, existWholeMachine);
        BeanUtils.copyProperties(editCommand, existWholeMachine.getCompatibilityConfiguration());
        existWholeMachine.setBoardCardIds(String.join(",", newBoardCardIdList));
        existWholeMachine.edit();
        wholeMachineRepository.save(existWholeMachine);
    }

    public void delete(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = getById(approvalNode.getHardwareId());

        if (!approvalNode.getHandlerUuid().toString().equals(wholeMachine.getUserUuid())) {
            throw new BusinessException("无权限编辑该整机数据！");
        }

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(), HardwareValueEnum.RESULT_DELETE.getValue());

        wholeMachineService.delete(wholeMachine);

        boardCardRepository.saveBatch(wholeMachine.getBoardCards());
        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void apply(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(), HardwareValueEnum.RESULT_PASS.getValue());

        wholeMachine.apply();

        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void close(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = getById(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(), HardwareValueEnum.RESULT_CLOSE.getValue());

        wholeMachineService.close(wholeMachine);

        boardCardRepository.saveBatch(wholeMachine.getBoardCards());
        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
    }

    public void pass(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = getById(approvalNode.getHardwareId());

        List<HardwareApprovalNode> boardApprovalList = new ArrayList<>();
        for (HardwareBoardCard boardCard : wholeMachine.getBoardCards()) {
            if (HardwareValueEnum.NODE_TEMP.getValue().equals(boardCard.getStatus())){
                HardwareApprovalNode boardApproval = new HardwareApprovalNode();
                BeanUtils.copyProperties(approvalNode,boardApproval);
                boardApproval.setHardwareId(boardCard.getId());
                boardApproval.action(HardwareValueEnum.TYPE_BOARD_CARD.getValue(),
                        boardCard.getStatus(), HardwareValueEnum.RESULT_PASS.getValue());
                boardCard.pass();
                boardApprovalList.add(boardApproval);
            }
        }

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(), HardwareValueEnum.RESULT_PASS.getValue());

        wholeMachineService.pass(wholeMachine);

        boardCardRepository.saveBatch(wholeMachine.getBoardCards());
        wholeMachineRepository.save(wholeMachine);
        approvalNodeRepository.save(approvalNode);
        approvalNodeRepository.saveBatch(boardApprovalList);
    }

    public void reject(HardwareApprovalNode approvalNode) {
        HardwareWholeMachine wholeMachine = wholeMachineRepository.find(approvalNode.getHardwareId());

        approvalNode.action(HardwareValueEnum.TYPE_WHOLE_MACHINE.getValue(),
                wholeMachine.getStatus(), HardwareValueEnum.RESULT_REJECT.getValue());

        wholeMachine.reject();

        wholeMachineRepository.save(wholeMachine);
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

    public HardwareWholeMachineDto getByIdForCompatibilityList(Integer id) {
        HardwareWholeMachine hardwareWholeMachine = wholeMachineRepository.find(id);
        HardwareBoardCardSelectVO selectVO = new HardwareBoardCardSelectVO();
        selectVO.setIdList(Arrays.stream(hardwareWholeMachine.getBoardCardIds().split(",")).toList());
        List<HardwareBoardCard> boardCardList = boardCardRepository.getList(selectVO);
        hardwareWholeMachine.setBoardCards(boardCardList);
        return hardwareFactory.createDto(hardwareWholeMachine);
    }

    public Page<HardwareWholeMachineListDto> pageForCompatibilityList(HardwareWholeMachineSelectVO selectVO) {
        selectVO.setSecurityLevel("0");
        selectVO.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        Page<HardwareWholeMachine> wholeMachinePage = wholeMachineRepository.getPage(selectVO);

        Page<HardwareWholeMachineListDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(wholeMachinePage, dtoPage);
        dtoPage.setRecords(hardwareFactory.createDtoList(wholeMachinePage.getRecords()));
        return dtoPage;
    }
}
