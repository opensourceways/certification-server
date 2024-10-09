/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 硬件-板卡 服务实现类
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Service
public class HardwareBoardCardService {

    @Autowired
    private HardwareBoardCardRepositoryImpl boardCardRepository;

    @Autowired
    private HardwareFactory hardwareFactory;

    public boolean exist(HardwareBoardCard boardCard) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("os", boardCard.getOs());
        queryWrapper.eq("architecture", boardCard.getArchitecture());
        queryWrapper.eq("board_model", boardCard.getBoardModel());
        queryWrapper.eq("chip_model", boardCard.getChipModel());
        queryWrapper.in("status", HardwareValueEnum.activeStatusList());
        long count = boardCardRepository.count(queryWrapper);
        return count > 0;
    }

    private QueryWrapper<HardwareBoardCardPO> createQueryWrapper(HardwareBoardCardSelectVO selectVO) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(selectVO.getId())) {
            queryWrapper.eq("id", selectVO.getId());
        }
        if (selectVO.getIdList() != null && !selectVO.getIdList().isEmpty()) {
            queryWrapper.in("id", selectVO.getIdList());
        }
        if (!StringUtils.isEmpty(selectVO.getOs())) {
            queryWrapper.eq("os", selectVO.getOs());
        }
        if (!StringUtils.isEmpty(selectVO.getArchitecture())) {
            queryWrapper.eq("architecture", selectVO.getArchitecture());
        }
        if (!StringUtils.isEmpty(selectVO.getType())) {
            queryWrapper.eq("type", selectVO.getType());
        }
        if (!StringUtils.isEmpty(selectVO.getDriverName())) {
            queryWrapper.like("driver_name", selectVO.getDriverName());
        }
        if (!StringUtils.isEmpty(selectVO.getBoardModel())) {
            queryWrapper.like("board_model", selectVO.getBoardModel());
        }
        if (!StringUtils.isEmpty(selectVO.getChipModel())) {
            queryWrapper.like("chip_model", selectVO.getChipModel());
        }
        if (!StringUtils.isEmpty(selectVO.getChipVendor())) {
            queryWrapper.like("chip_vendor", selectVO.getChipVendor());
        }
        if (!StringUtils.isEmpty(selectVO.getUserUuid())) {
            queryWrapper.eq("user_uuid", selectVO.getUserUuid());
        }
        if (!StringUtils.isEmpty(selectVO.getStatus())) {
            queryWrapper.eq("status", selectVO.getStatus());
        }
        if (selectVO.getStatusList() != null && !selectVO.getStatusList().isEmpty()) {
            queryWrapper.in("status", selectVO.getStatusList());
        }
        return queryWrapper;
    }

    public List<HardwareBoardCard> getList(HardwareBoardCardSelectVO selectVO) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = createQueryWrapper(selectVO);
        List<HardwareBoardCardPO> boardCardPOList = boardCardRepository.list(queryWrapper);
        return hardwareFactory.createBoardCardList(boardCardPOList);
    }
    public List<HardwareBoardCard> getList(HardwareBoardCard boardCard) {
        HardwareBoardCardSelectVO selectVO = hardwareFactory.createBoardCardSelectVO(boardCard);
        QueryWrapper<HardwareBoardCardPO> queryWrapper = createQueryWrapper(selectVO);
        List<HardwareBoardCardPO> boardCardPOList = boardCardRepository.list(queryWrapper);
        return hardwareFactory.createBoardCardList(boardCardPOList);
    }

    public Page<HardwareBoardCard> getPage(HardwareBoardCardSelectVO selectVO) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = createQueryWrapper(selectVO);
        Page<HardwareBoardCardPO> boardCardPOPage = new Page<>(selectVO.getCurrent(), selectVO.getSize());
        boardCardPOPage = boardCardRepository.page(boardCardPOPage, queryWrapper);
        Page<HardwareBoardCard> boardCardPage = new Page<>();
        BeanUtils.copyProperties(boardCardPOPage, boardCardPage);
        boardCardPage.setRecords(hardwareFactory.createBoardCardList(boardCardPOPage.getRecords()));
        return boardCardPage;
    }

    public HardwareBoardCard getById(Integer id) {
        HardwareBoardCardPO boardCardPO = boardCardRepository.getById(id);
        return hardwareFactory.createBoardCard(boardCardPO);
    }

    public void updateById(HardwareBoardCard boardCard){
        HardwareBoardCardPO wholeMachinePO = hardwareFactory.createBoardCardPO(boardCard);
        boardCardRepository.updateById(wholeMachinePO);
    }

    public boolean insert(HardwareBoardCard boardCard) {
        HardwareBoardCardPO boardCardPO = hardwareFactory.createBoardCardPO(boardCard.create());
        return boardCardRepository.save(boardCardPO);
    }

    public boolean batchInsert(List<HardwareBoardCard> boardCardList) {
        boardCardList = boardCardList.stream().map(HardwareBoardCard::create).toList();
        List<HardwareBoardCardPO> boardCardPOList = hardwareFactory.createBoardCardPOList(boardCardList);
        return boardCardRepository.saveBatch(boardCardPOList);
    }

    public void apply(HardwareBoardCard boardCard) {
        HardwareBoardCardPO boardCardPO = hardwareFactory.createBoardCardPO(boardCard.apply());
        boardCardRepository.saveOrUpdate(boardCardPO);
    }

    public void pass(HardwareBoardCard boardCard) {
        HardwareBoardCardPO boardCardPO = hardwareFactory.createBoardCardPO(boardCard.pass());
        boardCardRepository.saveOrUpdate(boardCardPO);
    }

    public void reject(HardwareBoardCard boardCard) {
        HardwareBoardCardPO boardCardPO = hardwareFactory.createBoardCardPO(boardCard.reject());
        boardCardRepository.saveOrUpdate(boardCardPO);
    }


}
