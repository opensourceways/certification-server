/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.huawei.it.euler.config.extension.SmsConfig;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.model.entity.Software;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${sns.templateId}")
    private String templateId;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SmsConfig smsConfig;

    @TransactionalEventListener
    @Async
    public void phoneNoticeListener(RejectToUserNodeEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        if (StringUtils.isEmpty(userInfo.getEmail()) && StringUtils.isEmpty(userInfo.getPhone())) {
            String templateParas = "[\"" + userInfo.getUserName() + "\",\"" + software.getProductName() + "\"]";
//            String phone = userInfo.getPhone();
            String phone = "18765950879";
            smsConfig.sendNotification(templateId, phone, templateParas);
        }
    }

}