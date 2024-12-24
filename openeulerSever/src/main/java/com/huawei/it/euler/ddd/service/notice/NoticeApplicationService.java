/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.notice;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.ddd.domain.account.RoleService;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoard;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoardRepository;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.infrastructure.email.EmailResponse;
import com.huawei.it.euler.ddd.infrastructure.email.EmailService;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsResponse;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsService;
import com.huawei.it.euler.ddd.service.notice.cqe.NoticeBoardAddCommand;
import com.huawei.it.euler.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统消息通知 application service
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
@Service
public class NoticeApplicationService {

    @Autowired
    private NoticeBoardRepository noticeBoardRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Value("${origin.value}")
    private String origin;

    /**
     * 查询系统公告
     * @return 系统公告集合
     */
    public List<NoticeBoard> findActiveList(){
        return noticeBoardRepository.findActiveList();
    }

    /**
     * 发布系统公告
     * @param addCommand 发布命令
     * @return 系统公告
     */
    public NoticeBoard publish(NoticeBoardAddCommand addCommand){
        NoticeBoard noticeBoard = new NoticeBoard();
        noticeBoard.setNoticeInfo(addCommand.getNoticeInfo());
        noticeBoard.setExpireTime(addCommand.getExpireTime());
        noticeBoard.publish();
        noticeBoardRepository.publish(noticeBoard);

        List<String> allUuidList = roleService.findAllUuid();
        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(String.join(",", allUuidList));
        messageDTO.setType(KafkaMessageTemplate.TYPE_NOTICE);
        messageDTO.setContent(noticeBoard.getNoticeInfo());
        messageDTO.setRedirectUrl(KafkaMessageTemplate.URL_INDEX);
        sendKafka(messageDTO);

        return noticeBoard;
    }

    /**
     * kafka 消息发送
     * @param messageDTO kafka消息
     */
    public void sendKafka(KafKaMessageDTO messageDTO){
        messageDTO.setRedirectUrl(origin + messageDTO.getRedirectUrl());
        kafkaTemplate.send(KafkaMessageTemplate.TOPIC, JSONObject.toJSONString(messageDTO));
    }

    /**
     * 发送消息
     * @param noticeMessage 系统消息对象
     * @return 发送结果
     */
    public NoticeMessage sendNotice(NoticeMessage noticeMessage) {
        if (SendType.PHONE.equals(noticeMessage.getSendType())) {
            return sendSms(noticeMessage);
        } else if (SendType.EMAIL.equals(noticeMessage.getSendType())) {
            return sendEmail(noticeMessage);
        } else {
            return noticeMessage;
        }
    }

    /**
     * 通过邮件发送消息
     * @param noticeMessage 系统消息对象
     * @return 发送结果
     */
    private NoticeMessage sendEmail(NoticeMessage noticeMessage){
        String batchReceiver = noticeMessage.getReceiver();
        List<String> receiverList = Arrays.stream(batchReceiver.split(",")).toList();

        Map<String,String> replaceMap = JSON.parseObject(noticeMessage.getContent(),new TypeReference<HashMap<String,String>>(){});
        String content = EmailUtil.fillDataIntoTemplate(noticeMessage.getTemplate(), replaceMap);

        EmailResponse emailResponse = emailService.sendMail(receiverList,
                noticeMessage.getSubject(), content, null);
        if (emailResponse.isSuccess()){
            noticeMessage.sendSuccess();
        } else {
            noticeMessage.sendFailed(emailResponse.getDescription());
        }
        return noticeMessage;
    }

    /**
     * 通过手机发送消息
     * @param noticeMessage 系统消息对象
     * @return 发送结果
     */
    private NoticeMessage sendSms(NoticeMessage noticeMessage){
        SmsResponse smsResponse = smsService.sendNotification(noticeMessage.getTemplate(),
                noticeMessage.getReceiver(), noticeMessage.getContent());
        if (smsResponse.isSuccess()){
            noticeMessage.sendSuccess();
        } else {
            noticeMessage.sendFailed(smsResponse.getDescription());
        }
        return noticeMessage;
    }
}