package com.huawei.it.euler.config.extension;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

@Component
public class EmailConfig {
    private static final Logger logger = LoggerFactory.getLogger(EmailConfig.class);

    @Value("email.host")
    private static String host;

    @Value("email.port")
    private static String port;

    @Value("email.userName")
    private static String userName;

    @Value("email.password")
    private static String password;

    /**
     * create email session instance after init.
     *
     * @return email session instance
     */
    private static Session getMailSession() {
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
            messageBodyPart.setText(content);

            // Create multipart for attachments
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            if (attachmentList != null && !attachmentList.isEmpty()){
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
}
