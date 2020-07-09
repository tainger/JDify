package io.terminus.dalaran.component.processor.mail.send;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.utils.OSSUtils;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;

import javax.activation.DataHandler;
import java.io.File;

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
        MailSenderInfo senderInfo = JSON.parseObject(JSON.toJSONString(in), MailSenderInfo.class);
        File file = OSSUtils.getFileFromOss(senderInfo.getUploadUrl(), ossAccount);
        String fileContent = FileUtils.readFileToString(file, "UTF-8");
        Message out = exchange.getOut();
        out.addAttachment(senderInfo.getFileName(), new DataHandler(fileContent,"plain/text"));
        out.setHeader(ComponentConstants.DALARAN_MAIL_TO, senderInfo.getEmailUrl());
    }
}
