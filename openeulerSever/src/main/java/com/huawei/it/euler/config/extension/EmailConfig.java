/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.config.extension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Component
public class EmailConfig {
    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    private static final String INTELNOTICETEMPLATE = "/docs/IntelNoticeEmailTemplate.html";

    @Value("${email.host}")
    private String host;

    @Value("${email.port}")
    private String port;

    @Value("${email.userName}")
    private String userName;

    @Value("${email.pwd}")
    private String password;

    /**
     * create email session instance after init.
     *
     * @return email session instance
     */
    private Session getMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
    }

    /**
     * send email to receivers, support multiple receiver and attachment
     *
     * @param receiverList   email receiver collection
     * @param subject        email subject
     * @param content        email content
     * @param attachmentList attachment collection, enable null
     */
    public void sendMail(List<String> receiverList, String subject, String content, List<String> attachmentList) {
        Session session = getMailSession();
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(userName));

            // Add multiple recipients
            for (String recipient : receiverList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            message.setSubject(subject);

            // Create the email body part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content,"text/html;charset=utf-8");

            // Create multipart for attachments
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            if (attachmentList != null && !attachmentList.isEmpty()) {
                for (String attachmentPath : attachmentList) {
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachmentPath);
                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                    attachmentBodyPart.setFileName(source.getName());
                    multipart.addBodyPart(attachmentBodyPart);
                }
            }

            // Set the email content to the multipart message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
        } catch (MessagingException e) {
            logger.error(String.format("email send error, parameters: receiverList-%s,subject-%s, exception==>", StringUtils.join(receiverList, ","), content), e);
        }
    }

    public String getIntelNoticeEmailContent(Map<String,String> dataMap) {
        ClassPathResource resource = new ClassPathResource(INTELNOTICETEMPLATE);
        try {
            String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);
            for (Map.Entry<String, String> stringStringEntry : dataMap.entrySet()) {
                String value = stringStringEntry.getValue();
                value = StringUtils.isEmpty(value) ? "--" : value;
                contentAsString = contentAsString.replace("${" + stringStringEntry.getKey() + "}", value);
            }
            return contentAsString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
