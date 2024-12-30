/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software.cqe;

import com.huawei.it.euler.model.entity.Software;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 审核英特尔测评事件
 *
 * @author zhaoyan
 * @since 2024-12-26
 */
@Getter
public class ApproveIntelEvent extends ApplicationEvent {

    private final Software software;

    public ApproveIntelEvent(Object source, Software software) {
        super(source);
        this.software = software;
    }
}
