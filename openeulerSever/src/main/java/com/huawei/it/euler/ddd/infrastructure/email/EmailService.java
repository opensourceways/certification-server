package com.huawei.it.euler.ddd.infrastructure.email;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Properties;

@Component
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailProperties emailProperties;

    /**
     * create email session instance after init.
     *
     * @return email session instance
     */
    private Session getMailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", emailProperties.getHost());
        props.put("mail.smtp.port", emailProperties.getPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getUserName(), emailProperties.getPwd());
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
    public EmailResponse sendMail(List<String> receiverList, String subject, String content, List<String> attachmentList) {
        Session session = getMailSession();
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailProperties.getUserName()));

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
            return EmailResponse.sendSuccess();
        } catch (MessagingException e) {
            logger.error(String.format("email send error, parameters: receiverList-%s,subject-%s, exception==>", StringUtils.join(receiverList, ","), content), e);
            return EmailResponse.sendExceptionResponse(e.getMessage());
        }
    }
}
