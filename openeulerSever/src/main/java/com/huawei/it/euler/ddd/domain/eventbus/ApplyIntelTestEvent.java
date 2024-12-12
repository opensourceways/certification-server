/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.huawei.it.euler.model.entity.Software;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 申请英特尔测评事件
 *
 * @author zhaoyan
 * @since 2024-12-11
 */
@Getter
public class ApplyIntelTestEvent extends ApplicationEvent {

    private final Software software;

    public ApplyIntelTestEvent(Object source, Software software) {
        super(source);
        this.software = software;
    }
}
