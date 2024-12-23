/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy.impl;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.domain.notice.policy.SendPolicy;
import com.huawei.it.euler.ddd.domain.notice.primitive.ApproveEventNoticeVariable;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsProperties;
import com.huawei.it.euler.ddd.service.software.cqe.ApproveEvent;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.HandlerResultEnum;
import com.huawei.it.euler.model.enumeration.NodeEnum;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.ProcessVo;
import com.huawei.it.euler.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 测评业务驳回通知用户-手机短信
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
public class RejectToUserPhoneNoticePolicy implements SendPolicy {

    @Override
    public boolean canSend(UserInfo userInfo, ApplicationEvent event) {
        // 邮箱不存在
        if (!StringUtils.isEmpty(userInfo.getEmail()) || StringUtils.isEmpty(userInfo.getPhone())){
            return false;
        }
        // 非测评审批事件
        if (!(event instanceof ApproveEvent approveEvent)){
            return false;
        }
        ProcessVo processVo = approveEvent.getProcessVo();
        // 非驳回操作
        if (!HandlerResultEnum.REJECT.getId().equals(processVo.getHandlerResult())) {
            return false;
        }
        Software software = approveEvent.getSoftware();
        // 非用户节点
        return RoleEnum.USER.getRoleId().equals(software.getReviewRole());
    }

    @Override
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event) {
        ApproveEvent approveEvent = (ApproveEvent) event;
        Software software = approveEvent.getSoftware();
        ProcessVo processVo = approveEvent.getProcessVo();

        ApproveEventNoticeVariable variable = new ApproveEventNoticeVariable();
        variable.setUserName(userInfo.getUserName());
        variable.setProductName(software.getProductName());
        variable.setResult(HandlerResultEnum.findById(processVo.getHandlerResult()));
        variable.setStatus(NodeEnum.findById(software.getStatus()));

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setMsgType(MsgType.SOFTWARE_PROGRESS_NOTICE);
        noticeMessage.setCreateTime(LocalDateTime.now());
        noticeMessage.setReceiver(userInfo.getPhone());
        noticeMessage.setSendType(SendType.PHONE);
        SmsProperties smsProperties = SpringUtil.getBean("smsProperties", SmsProperties.class);
        noticeMessage.setTemplate(smsProperties.getProgressNoticeTemplateId());
        noticeMessage.setContent(variable.getPhoneTemplateParameters());
        noticeMessage.sendCreate();
        return noticeMessage;
    }
}
