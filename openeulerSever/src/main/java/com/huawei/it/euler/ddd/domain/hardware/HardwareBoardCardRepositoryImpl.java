/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 硬件-板卡 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Service
public class HardwareBoardCardRepositoryImpl extends ServiceImpl<HardwareBoardCardMapper, HardwareBoardCardPO> implements IService<HardwareBoardCardPO> {

}
