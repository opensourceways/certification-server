/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 硬件-整机 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Repository
public class HardwareWholeMachineRepositoryImpl extends ServiceImpl<HardwareWholeMachineMapper, HardwareWholeMachinePO> implements IService<HardwareWholeMachinePO> {
    @Autowired
    private HardwareFactory hardwareFactory;

    public HardwareWholeMachine getOne(HardwareWholeMachine wholeMachine) {
        QueryWrapper<HardwareWholeMachinePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("factory_zy", wholeMachine.getHardwareFactoryZy());
        queryWrapper.eq("factory_en", wholeMachine.getHardwareFactoryEn());
        queryWrapper.eq("model", wholeMachine.getHardwareModel());
        queryWrapper.eq("os_version", wholeMachine.getOsVersion());
        queryWrapper.eq("architecture", wholeMachine.getArchitecture());
        queryWrapper.eq("date", wholeMachine.getDate());
        queryWrapper.in("status", HardwareValueEnum.activeStatusList());
        HardwareWholeMachinePO wholeMachinePO = this.getOne(queryWrapper);
        if (wholeMachinePO == null) {
            return null;
        }
        return hardwareFactory.createWholeMachine(wholeMachinePO);
    }

    private QueryWrapper<HardwareWholeMachinePO> createQueryWrapper(HardwareWholeMachineSelectVO selectVO){
        QueryWrapper<HardwareWholeMachinePO> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(selectVO.getId())) {
            queryWrapper.eq("id", selectVO.getId());
        }
        if (selectVO.getIdList() != null && !selectVO.getIdList().isEmpty()) {
            queryWrapper.in("id", selectVO.getIdList());
        }
        if (!StringUtils.isEmpty(selectVO.getArchitecture())) {
            queryWrapper.eq("architecture", selectVO.getArchitecture());
        }
        if (!StringUtils.isEmpty(selectVO.getHardwareFactory())) {
            queryWrapper.and(wrapper -> wrapper.like("factory_zy", selectVO.getHardwareFactory())
                    .or().like("factory_en", selectVO.getHardwareFactory()));
        }
        if (!StringUtils.isEmpty(selectVO.getHardwareModel())) {
            queryWrapper.like("model", selectVO.getHardwareModel());
        }
        if (!StringUtils.isEmpty(selectVO.getOsVersion())) {
            queryWrapper.like("os_version", selectVO.getOsVersion());
        }
        if (!StringUtils.isEmpty(selectVO.getCpu())) {
            queryWrapper.like("cpu", selectVO.getCpu());
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

    public List<HardwareWholeMachine> getList(HardwareWholeMachineSelectVO selectVO){
        QueryWrapper<HardwareWholeMachinePO> queryWrapper = createQueryWrapper(selectVO);
        List<HardwareWholeMachinePO> wholeMachinePOList = this.list(queryWrapper);
        return hardwareFactory.createWholeMachineList(wholeMachinePOList);
    }

    public Page<HardwareWholeMachine> getPage(HardwareWholeMachineSelectVO selectVO) {
        QueryWrapper<HardwareWholeMachinePO> queryWrapper = createQueryWrapper(selectVO);
        Page<HardwareWholeMachinePO> wholeMachinePOPage = new Page<>(selectVO.getCurrent(), selectVO.getSize());
        wholeMachinePOPage = this.page(wholeMachinePOPage, queryWrapper);

        Page<HardwareWholeMachine> wholeMachinePage = new Page<>();
        BeanUtils.copyProperties(wholeMachinePOPage, wholeMachinePage);
        wholeMachinePage.setRecords(hardwareFactory.createWholeMachineList(wholeMachinePOPage.getRecords()));
        return wholeMachinePage;
    }

    public HardwareWholeMachine find(Integer id){
        HardwareWholeMachinePO wholeMachinePO = this.getById(id);
        if (wholeMachinePO == null){
            throw new BusinessException("当前整机数据不存在！");
        }
        return hardwareFactory.createWholeMachine(wholeMachinePO);
    }

    public HardwareWholeMachine save(HardwareWholeMachine wholeMachine){
        HardwareWholeMachinePO wholeMachinePO = hardwareFactory.createWholeMachinePO(wholeMachine);
        this.saveOrUpdate(wholeMachinePO);
        return hardwareFactory.createWholeMachine(wholeMachinePO);
    }
}