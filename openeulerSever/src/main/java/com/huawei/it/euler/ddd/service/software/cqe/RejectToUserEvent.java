/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software.cqe;

import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.ProcessVo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 测评业务驳回到用户事件
 *
 * @author zhaoyan
 * @since 2024-12-11
 */
@Getter
public class RejectToUserEvent extends ApplicationEvent {

    private final Software software;

    private final ProcessVo processVo;

    public RejectToUserEvent(Object source, Software software, ProcessVo processVo) {
        super(source);
        this.software = software;
        this.processVo = processVo;
    }

}
