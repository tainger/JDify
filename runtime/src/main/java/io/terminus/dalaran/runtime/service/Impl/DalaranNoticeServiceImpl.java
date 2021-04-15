package io.terminus.dalaran.runtime.service.Impl;

import io.terminus.common.model.Response;
import io.terminus.dalaran.model.alarm.NoticeMessage;
import io.terminus.dalaran.runtime.service.DalaranNoticeService;
import io.terminus.notice.api.dto.EmailSendDTO;
import io.terminus.notice.sender.email.service.EmailSenderService;
import io.terminus.notice.sender.sms.service.SmsSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DalaranNoticeServiceImpl implements DalaranNoticeService {


    @Value("${noticeMessage.mailNoticeCode}")
    private String mailNoticeCode;

    @Value("${noticeMessage.SMSNoticeCode}")
    private String SMSNoticeCode;

    private static final Logger logger = LoggerFactory.getLogger(DalaranNoticeServiceImpl.class);

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private SmsSenderService smsSenderService;

    @Override
    public void sendEmail(NoticeMessage noticeMessage) {
        EmailSendDTO emailSendDTO = new EmailSendDTO();
        emailSendDTO.setSubject("mule流程报警");
        emailSendDTO.setSenderEmail("no-reply@terminus.io");
        List<String> contents = buildNoticeMessage(noticeMessage);
        String[] contactWays = noticeMessage.getContactWays();
        Response<String> result = emailSenderService.send(mailNoticeCode, Arrays.asList(contactWays), emailSendDTO, contents);
        if (result.isSuccess())
            logger.error("发送成功");
        else
            logger.error("发送失败，错误码是：" + result.getError());
    }

    @Override
    public void sendShortMessage(NoticeMessage noticeMessage) {
        List<String> keys = new ArrayList<>();
        keys.add("flowName");
        keys.add("createDate");
        keys.add("isTouchFailureAlarm");
        keys.add("failureFrequency");
        keys.add("failureCount");
        keys.add("isTouchTimeOutAlarm");
        keys.add("timeOutFrequency");
        keys.add("timeOutCount");
        List<String> contents = buildNoticeMessage(noticeMessage);
        List<String> phoneNumbers = Arrays.asList(noticeMessage.getContactWays());
        Response<String> sendResult = smsSenderService.templateSend(phoneNumbers, keys, contents, SMSNoticeCode);
        if (sendResult.isSuccess())
            logger.error("发送成功");
        else
            logger.error("发送失败，错误码是：" + sendResult.getError());
    }

    private List<String> buildNoticeMessage(NoticeMessage noticeMessage) {
        List<String> contents = new ArrayList<>();
        contents.add(noticeMessage.getFlowName());
        contents.add(noticeMessage.getCreateDate());
        contents.add(noticeMessage.getIsTouchFailureAlarm() ? "" : "没有");
        contents.add(String.valueOf(noticeMessage.getFailureFrequency()));
        contents.add(String.valueOf(noticeMessage.getFailureCount()));
        contents.add(noticeMessage.getIsTouchTimeOutAlarm() ? "" : "没有");
        contents.add(String.valueOf(noticeMessage.getTimeOutFrequency()));
        contents.add(String.valueOf(noticeMessage.getTimeOutCount()));
        return contents;
    }
}
