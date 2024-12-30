/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import org.springframework.context.ApplicationEvent;

/**
 * 消息发送策略
 *
 * @author zhaoyan
 * @since 2024-12-19
 */

public interface SendPolicy {

    /**
     * 是否符合发送要求
     * @param userInfo 接收人信息
     * @param event 事件
     * @return 检查结果
     */
    public boolean canSend(UserInfo userInfo, ApplicationEvent event);

    /**
     * 消息发送准备
     * @param userInfo 接收人信息
     * @param event 事件
     * @return 检查结果
     */
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event);
}
