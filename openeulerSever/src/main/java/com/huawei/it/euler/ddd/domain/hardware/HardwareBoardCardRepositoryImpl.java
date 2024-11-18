/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.ddd.service.HardwareBoardCardSelectVO;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 硬件-板卡 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Service
public class HardwareBoardCardRepositoryImpl extends ServiceImpl<HardwareBoardCardMapper, HardwareBoardCardPO> implements IService<HardwareBoardCardPO> {

    @Autowired
    private HardwareFactory hardwareFactory;

    public HardwareBoardCard getOne(HardwareBoardCard boardCard) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("os", boardCard.getOs());
        queryWrapper.eq("architecture", boardCard.getArchitecture());
        queryWrapper.eq("board_model", boardCard.getBoardModel());
        queryWrapper.eq("chip_model", boardCard.getChipModel());
        queryWrapper.in("status", HardwareValueEnum.activeBoardCardStatusList());
        HardwareBoardCardPO boardCardPO = this.getOne(queryWrapper);
        if (boardCardPO == null){
            return null;
        }
        return hardwareFactory.createBoardCard(boardCardPO);
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
        if (!StringUtils.isEmpty(selectVO.getSecurityLevel())) {
            queryWrapper.eq("security_level", selectVO.getSecurityLevel());
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
        List<HardwareBoardCardPO> boardCardPOList = this.list(queryWrapper);
        return hardwareFactory.createBoardCardList(boardCardPOList);
    }

    public List<HardwareBoardCard> getList(HardwareBoardCard boardCard) {
        HardwareBoardCardSelectVO selectVO = hardwareFactory.createBoardCardSelectVO(boardCard);
        return this.getList(selectVO);
    }

    public Page<HardwareBoardCard> getPage(HardwareBoardCardSelectVO selectVO) {
        QueryWrapper<HardwareBoardCardPO> queryWrapper = createQueryWrapper(selectVO);
        Page<HardwareBoardCardPO> boardCardPOPage = new Page<>(selectVO.getCurrent(), selectVO.getSize());
        boardCardPOPage = this.page(boardCardPOPage, queryWrapper);
        Page<HardwareBoardCard> boardCardPage = new Page<>();
        BeanUtils.copyProperties(boardCardPOPage, boardCardPage);
        boardCardPage.setRecords(hardwareFactory.createBoardCardList(boardCardPOPage.getRecords()));
        return boardCardPage;
    }

    public List<HardwareBoardCard> findOrSaveTemp(List<HardwareBoardCard> boardCardList,String uuid){
        List<HardwareBoardCard> findList = new ArrayList<>();

        Map<Boolean, List<HardwareBoardCard>> existCheck = boardCardList.stream().
                collect(Collectors.groupingBy(boardCard -> this.getOne(boardCard) != null));

        List<HardwareBoardCard> unExistList = existCheck.get(false);
        if (unExistList != null && !unExistList.isEmpty()){
            List<HardwareBoardCard> saveBoardCardList = unExistList.stream().
                    map(boardCard -> this.save(boardCard.createTemp(uuid))).toList();
            findList.addAll(saveBoardCardList);
        }

        List<HardwareBoardCard> existList = existCheck.get(true);
        if (existList != null && !existList.isEmpty()){
            List<HardwareBoardCard> queryBoardCardList = existList.stream().
                    map(this::getOne).toList();
            findList.addAll(queryBoardCardList);
        }

        return findList;
    }

    public void clearRef(Integer id) {
        UpdateWrapper<HardwareBoardCardPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("whole_machine_ids", null);
        this.update(updateWrapper);
    }

    public HardwareBoardCard find(Integer id) {
        HardwareBoardCardPO boardCardPO = this.getById(id);
        if (boardCardPO == null){
            throw new BusinessException("当前板卡数据不存在！");
        }
        return hardwareFactory.createBoardCard(boardCardPO);
    }

    public HardwareBoardCard save(HardwareBoardCard boardCard){
        HardwareBoardCardPO boardCardPO = hardwareFactory.createBoardCardPO(boardCard);
        this.saveOrUpdate(boardCardPO);
        if (StringUtils.isEmpty(boardCardPO.getWholeMachineIds())){
            clearRef(boardCardPO.getId());
        }
        return hardwareFactory.createBoardCard(boardCardPO);
    }

    @Transactional
    public void saveBatch(List<HardwareBoardCard> boardCardList){
        List<HardwareBoardCardPO> boardCardPOList = hardwareFactory.createBoardCardPOList(boardCardList);
        this.saveOrUpdateBatch(boardCardPOList);
        for (HardwareBoardCardPO boardCardPO : boardCardPOList) {
            if (StringUtils.isEmpty(boardCardPO.getWholeMachineIds())){
                clearRef(boardCardPO.getId());
            }
        }
    }

}
