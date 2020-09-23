package io.terminus.dalaran.component.processor.mail.dalaran;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.component.common.MailProtocol;
import io.terminus.dalaran.component.processor.mail.camel.DalaranMailSenderConfig;
import io.terminus.dalaran.component.processor.mail.camel.MailSenderInfo;
import io.terminus.dalaran.component.utils.OSSUtils;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class DalaranMailSenderProcessor implements Processor {

    private DalaranMailSenderConfig config;

    private OSSAccount ossAccount;

    public DalaranMailSenderProcessor(DalaranMailSenderConfig config, OSSAccount ossAccount) {
        this.config = config;
        this.ossAccount = ossAccount;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object in = exchange.getIn().getBody();
        if (config.isDynamicAddress()) {
            MailSenderInfo senderInfo = JSON.parseObject(JSON.toJSONString(in), MailSenderInfo.class);
            File file = OSSUtils.getFileFromOss(senderInfo.getUploadUrl(), ossAccount);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            send(senderInfo.getEmailUrl(), fileContent, "application/excel");
        } else {
            send(config.getSendTo(), JSON.toJSONString(in), "text/plain");
        }
    }

    private void send(String to, Object body, String type) {
        String host = config.getConnector().getHost();
        String from = config.getConnector().getFrom();
        final String username = config.getConnector().getUsername();
        final String password = config.getConnector().getPassword();

        Session session = Session.getInstance(buildProperties(host, config.getConnector().getProtocol()),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(to));
            if (StringUtils.isNotBlank(config.getCcTo())) {
                message.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(config.getCcTo()));
            }
            message.setSubject(config.getSubject());

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hello!");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(body, type));
            messageBodyPart.setFileName("DalaranFile");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport transport = session.getTransport("smtps");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties buildProperties(String host, MailProtocol protocol) {
        Properties props = new Properties();
        switch (protocol) {
            case SMTPS:
                props.put("mail.transport.protocol", "smtps");
                props.put("mail.smtps.auth", "true");
                props.put("mail.smtps.starttls.enable", "true");
                props.put("mail.smtps.ssl.trust", host);
                props.put("mail.host", host);
                props.put("mail.smtps.port", config.getConnector().getPort());
                props.setProperty("mail.smtps.ssl.protocols", "TLSv1.1 TLSv1.2");
                break;
        }
        return props;
    }
}
