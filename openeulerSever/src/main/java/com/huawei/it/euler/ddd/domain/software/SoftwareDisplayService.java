/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.ErrorCodes;
import com.huawei.it.euler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoftwareDisplayService {

    @Autowired
    private SoftwareDisplayRepositoryImpl displayRepository;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private UserService userService;

    public boolean hidden(String uuid, Integer softwareId) {
        Software software = softwareMapper.findById(softwareId);
        if (!userService.isUserPermission(Integer.valueOf(uuid), software)) {
            throw new ParamException(ErrorCodes.UNAUTHORIZED.getMessage());
        }
        SoftwareDisplayPO softwareDisplayPO = new SoftwareDisplayPO();
        softwareDisplayPO.setUuid(uuid);
        softwareDisplayPO.setSoftwareId(softwareId);
        softwareDisplayPO.setProductName(software.getProductName());
        return displayRepository.save(softwareDisplayPO);
    }

    public boolean show(List<Integer> displayIdList) {
        return displayRepository.removeBatchByIds(displayIdList);
    }

    public Page<SoftwareDisplayPO> getHiddenPage(String uuid, String productName, int current, int size) {
        QueryWrapper<SoftwareDisplayPO> wrapper = new QueryWrapper<>();
        wrapper.eq("uuid", uuid);
        wrapper.like("productName", productName);
        Page<SoftwareDisplayPO> poPage = new Page<>(current, size);
        return displayRepository.page(poPage);
    }
}
