/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 测评业务驳回到用户节点事件 邮件通知监听器
 *
 * @author zhaoyan
 * @since 2024-12-11
 */
@Component
public class RejectToUserNodeEventNoticeByEmailListener {

    @TransactionalEventListener
    @Async
    public void emailNoticeListener(RejectToUserNodeEvent event) {
        if (!StringUtils.isEmpty(event.getUser().getEmail())) {
            System.out.println("send email notice user!" + event.getUser().getEmail()
                    + "；测评业务：" + event.getSoftware().getProductName()
                    + "；驳回意见：" + event.getProcessVo().getTransferredComments());
        }
    }

}
