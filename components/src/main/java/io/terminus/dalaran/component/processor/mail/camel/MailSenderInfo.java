package io.terminus.dalaran.component.processor.mail.camel;

import lombok.Data;

@Data
public class MailSenderInfo {

    private String orderId;

    private String uploadUrl;

    private String emailUrl;

    private String fileName;
}
