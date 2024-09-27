/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SoftwareDisplayRepositoryImpl extends ServiceImpl<SoftwareDisplayMapper, SoftwareDisplayPO> {

    @Autowired
    SoftwareDisplayMapper softwareDisplayMapper;

}
