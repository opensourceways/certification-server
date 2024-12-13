/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.huawei.it.euler.config.extension.EmailConfig;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.model.entity.Software;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测评业务驳回到用户节点事件 邮件通知监听器
 *
 * @author zhaoyan
 * @since 2024-12-11
 */
@Component
public class RejectToUserNodeEventNoticeByEmailListener {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailConfig emailConfig;

    @TransactionalEventListener
    @Async
    public void emailNoticeListener(RejectToUserNodeEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        if (!StringUtils.isEmpty(userInfo.getEmail())) {
            List<String> receiverList = new ArrayList<>();
//            receiverList.add(userInfo.getEmail());
            receiverList.add("zhaoyan150@huawei.com");

            String subject = "测评业务驳回通知";

            Map<String, String> replaceMap = new HashMap<>();
            replaceMap.put("userName", userInfo.getUserName());
            replaceMap.put("productName", software.getProductName());
            replaceMap.put("reason", event.getProcessVo().getTransferredComments());

            String content = emailConfig.getRejectNoticeEmailContent(replaceMap);
            emailConfig.sendMail(receiverList, subject, content, new ArrayList<>());
        }
    }

}
