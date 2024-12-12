/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import org.apache.commons.lang3.StringUtils;
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
public class RejectToUserNodeEventNoticeByPhoneListener {

    @TransactionalEventListener
    @Async
    public void phoneNoticeListener(RejectToUserNodeEvent event) {
        if (StringUtils.isEmpty(event.getUser().getEmail()) && StringUtils.isEmpty(event.getUser().getPhone())) {
            System.out.println("send sns notice user!" + event.getUser().getPhone()
                    + "；测评业务：" + event.getSoftware().getProductName()
                    + "；驳回意见：" + event.getProcessVo().getTransferredComments());
        }
    }

}
