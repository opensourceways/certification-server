/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessageRepository;
import com.huawei.it.euler.ddd.domain.notice.policy.SendManager;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.notice.NoticeApplicationService;
import com.huawei.it.euler.ddd.service.software.cqe.ApplyIntelTestEvent;
import com.huawei.it.euler.ddd.service.software.cqe.ApproveEvent;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.HandlerResultEnum;
import com.huawei.it.euler.model.enumeration.NodeEnum;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.ProcessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测评业务 application service
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Service
public class SoftwareApplicationService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private NoticeMessageRepository noticeMessageRepository;

    @Autowired
    private SendManager sendManager;

    @Autowired
    private NoticeApplicationService noticeApplicationService;

    /**
     * 英特尔测评业务申请事件处理，通知到英特尔审核人员
     * @param event 英特尔业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void intelApplyNotice(ApplyIntelTestEvent event) {
        List<UserInfo> userInfoList = accountService.getUserInfoList(RoleEnum.OPENATOM_INTEL.getRoleId());
        for (UserInfo userInfo : userInfoList) {
            NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
            noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
            noticeMessageRepository.record(noticeMessage);
        }
    }

    /**
     * 英特尔测评业务申请事件处理，发送kafka消息中心
     * @param event 英特尔业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void intelApplyNoticeKafka(ApplyIntelTestEvent event) {
        Software software = event.getSoftware();
        List<UserInfo> userInfoList = accountService.getUserInfoList(RoleEnum.OPENATOM_INTEL.getRoleId());
        String user = userInfoList.stream().map(UserInfo::getUuid).collect(Collectors.joining(","));
        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(user);
        messageDTO.setType(KafkaMessageTemplate.TYPE_TODO);
        messageDTO.setContent(KafkaMessageTemplate.CONTENT_TODO);
        messageDTO.setRedirectUrl(KafkaMessageTemplate.getSoftwareDetailUrl(String.valueOf(software.getId()),software.getProductName()));
        noticeApplicationService.sendKafka(messageDTO);
    }

    /**
     * 测评业务申请事件处理，驳回到伙伴节点场景， 通知伙伴
     * @param event 测评业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void progressNotice(ApproveEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
        noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
        noticeMessageRepository.record(noticeMessage);
    }

    /**
     * 测评业务申请事件处理，驳回到伙伴节点场景， 发送kafka消息中心
     * @param event 测评业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void progressNoticeKafka(ApproveEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        ProcessVo processVo = event.getProcessVo();

        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(userInfo.getUuid());
        messageDTO.setType(KafkaMessageTemplate.TYPE_NOTICE);
        String progressNoticeContent = KafkaMessageTemplate.getProgressNoticeContent(software.getProductName(),
                HandlerResultEnum.findById(processVo.getHandlerResult()), NodeEnum.findById(software.getStatus()));
        messageDTO.setContent(progressNoticeContent);
        messageDTO.setRedirectUrl(KafkaMessageTemplate.getSoftwareDetailUrl(String.valueOf(software.getId()),software.getProductName()));
        noticeApplicationService.sendKafka(messageDTO);
    }
}
