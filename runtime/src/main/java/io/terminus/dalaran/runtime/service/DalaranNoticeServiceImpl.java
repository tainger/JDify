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


    private String mailContent = "你的流程%s于%s时候 ，超时频次%s超出阈值%s上限，频次为:%s，失败频次%s超出阈值%s上限，频次为：%s。";

    @Override
    public void sendEmail(NoticeMessage noticeMessage){
        logger.error(noticeMessage.toString());
        EmailSendDTO emailSendDTO = new EmailSendDTO();
    // 发送邮箱，选填，不填写会取默认发送邮箱，填写会覆盖默认发送邮箱
        emailSendDTO.setSenderEmail("13017135085@163.com");
    // 邮件主题，选填，部分渠道要求必填，建议填写,500字符串以内
        emailSendDTO.setSubject("mule流程报警");
//    // 抄送列表，选填，最多1000
//        List<String> cc = new ArrayList<>();
//    // 密送列表，选填，最多1000
//        List<String> bcc = new ArrayList<>();
//    // 附件列表<附件名，附件url链接>，选填，最多10个。附件名不超过100个字符串
//        Map<String, String> attachments = new HashMap<>();
//    // 是否网页，isHtml为true邮件内容可以为html代码，邮件会自动显示成网页
        String[] contactWays = noticeMessage.getContactWays();
        String content = String.format(mailContent, noticeMessage.getFlowName(), noticeMessage.getCreateDate().toString(), noticeMessage.getIsTouchFailureAlarm()?"":"没有",
                noticeMessage.getFailureFrequency(), noticeMessage.getFailureCount(), noticeMessage.getCreateDate().toString(), noticeMessage.getIsTouchTimeOutAlarm()?"":"没有",
                noticeMessage.getTimeOutFrequency(), noticeMessage.getTimeOutCount());
        boolean isHtml = false;
        Response<String> result = emailSenderService.send(emailSendDTO, Arrays.asList(contactWays), content, isHtml);
        if (result.isSuccess())
            System.out.println("发送成功");
        else
            System.out.println("发送失败，错误码是：" + result.getError());
    }

    @Override
    public void sendShortMessage(NoticeMessage noticeMessage) {
        logger.error(noticeMessage.toString());
        //noticeCode为渠道模板的模板code
        String noticeCode = "1003";
        //keys填入模板定义的替换key
        List<String> keys = new ArrayList<String>();
        keys.add("flowName");
        keys.add("createDate");
        keys.add("isTouchFailureAlarm");
        keys.add("failureFrequency");
        keys.add("failureCount");
        keys.add("isTouchTimeOutAlarm");
        keys.add("timeOutFrequency");
        keys.add("timeOutCount");
        //content填入模板替换的value（与key顺序一致），阿里云渠道变量需小于20字符，且不能为url
        List<String> content = new ArrayList<String>();
        content.add(noticeMessage.getFlowName());
        content.add(noticeMessage.getCreateDate().toString());
        content.add(noticeMessage.getIsTouchFailureAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getFailureFrequency()));
        content.add(String.valueOf(noticeMessage.getFailureCount()));

        content.add(noticeMessage.getCreateDate().toString());
        content.add(noticeMessage.getIsTouchTimeOutAlarm()?"":"没有");
        content.add(String.valueOf(noticeMessage.getTimeOutFrequency()));
        content.add(String.valueOf(noticeMessage.getTimeOutCount()));
        String[] contactWays = noticeMessage.getContactWays();
        //多个手机号
        List<String> phoneNumbers = Arrays.asList(contactWays);
        Response<String> sendResult = smsSenderService.templateSend(phoneNumbers, keys, content, noticeCode);
        if (sendResult.isSuccess())
            System.out.println("发送成功");
        else
            System.out.println("发送失败，错误码是：" + sendResult.getError());
    }
}
