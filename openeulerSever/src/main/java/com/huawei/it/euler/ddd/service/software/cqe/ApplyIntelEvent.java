/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software.cqe;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
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
public class ApplyIntelEvent extends ApplicationEvent {

    private final Software software;

    private final UserInfo applicant;

    public ApplyIntelEvent(Object source, Software software, UserInfo applicant) {
        super(source);
        this.software = software;
        this.applicant = applicant;
    }
}
