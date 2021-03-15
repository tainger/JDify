package io.terminus.dalaran.runtime.service;

import io.terminus.common.model.Response;
import io.terminus.dalaran.core.resource.entity.NoticeMessage;
import io.terminus.notice.api.dto.EmailSendDTO;
import io.terminus.notice.sender.email.service.EmailSenderService;
import io.terminus.notice.sender.sms.service.SmsSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DalaranNoticeServiceImpl implements DalaranNoticeService{

    private static final Logger logger = LoggerFactory.getLogger(DalaranNoticeServiceImpl.class);

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private SmsSenderService smsSenderService;


    @Override
    public void sendEmail(NoticeMessage noticeMessage){
        logger.error(noticeMessage.toString());
        EmailSendDTO emailSendDTO = new EmailSendDTO();
//        emailSendDTO.setSubject("mule流程报警");
        List<String> content = new ArrayList<>();
        content.add(noticeMessage.getFlowName());
        content.add(noticeMessage.getCreateDate().toString());
        content.add(noticeMessage.getIsTouchFailureAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getFailureFrequency()));
        content.add(String.valueOf(noticeMessage.getFailureCount()));
        content.add(noticeMessage.getIsTouchTimeOutAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getTimeOutFrequency()));
        content.add(String.valueOf(noticeMessage.getTimeOutCount()));
        String[] contactWays = noticeMessage.getContactWays();
        //todo 优化e-mail
        boolean isHtml = false;
        Response<String> result = emailSenderService.send("1111", Arrays.asList(contactWays), emailSendDTO, content);
        if (result.isSuccess())
            logger.error("发送成功");
        else
            logger.error("发送失败，错误码是：" + result.getError());
    }

    @Override
    public void sendShortMessage(NoticeMessage noticeMessage) {
        logger.error(noticeMessage.toString());
        String noticeCode = "8024";
        List<String> content = new ArrayList<String>();
        content.add(noticeMessage.getFlowName());
        content.add(noticeMessage.getCreateDate().toString());
        content.add(noticeMessage.getIsTouchFailureAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getFailureFrequency()));
        content.add(String.valueOf(noticeMessage.getFailureCount()));

        content.add(noticeMessage.getIsTouchTimeOutAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getTimeOutFrequency()));
        content.add(String.valueOf(noticeMessage.getTimeOutCount()));

        String[] contactWays = noticeMessage.getContactWays();
        //多个手机号
        List<String> phoneNumbers = Arrays.asList(contactWays);
        Response<String> sendResult = smsSenderService.send(phoneNumbers, content, noticeCode);
        if (sendResult.isSuccess())
            logger.error("发送成功");
        else
            logger.error("发送失败，错误码是：" + sendResult.getError());
    }
}
