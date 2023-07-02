//package io.terminus.dalaran.core.resource;
//
//import io.terminus.dalaran.core.flow.DalaranNoticeBuilder;
//import io.terminus.dalaran.core.resource.property.PropertyService;
//import io.terminus.dalaran.model.alarm.NoticeMessage;
//import lombok.extern.slf4j.Slf4j;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//public class DefaultDalaranNoticeBuilder implements DalaranNoticeBuilder {
//
//    private PropertyService propertyService;
//
//    private EmailSenderService emailSenderService;
//
//    private SmsSenderService smsSenderService;
//
//
//    public DefaultDalaranNoticeBuilder(
//            PropertyService propertyService, EmailSenderService emailSenderService,
//            SmsSenderService smsSenderService) {
//        this.propertyService = propertyService;
//        this.emailSenderService = emailSenderService;
//        this.smsSenderService = smsSenderService;
//    }
//
//    public void sendEmail(NoticeMessage noticeMessage, String[] connectWays) {
//        EmailSendDTO emailSendDTO = new EmailSendDTO();
//        emailSendDTO.setSubject("mule流程报警");
//        emailSendDTO.setSenderEmail("no-reply@terminus.io");
//        List<String> contents = buildNoticeMessage(noticeMessage);
//        Response<String> result = emailSenderService.send(propertyService.getMailNoticeCode(), Arrays.asList(connectWays), emailSendDTO, contents);
//        if (result.isSuccess())
//            log.error("发送成功");
//        else
//            log.error("发送失败，错误码是：" + result.getError());
//    }
//
//    public void sendShortMessage(NoticeMessage noticeMessage, String[] connectWays) {
//        List<String> keys = new ArrayList<>();
//        keys.add("flowName");
//        keys.add("createDate");
//        keys.add("isTouchFailureAlarm");
//        keys.add("failureFrequency");
//        keys.add("failureCount");
//        keys.add("isTouchTimeOutAlarm");
//        keys.add("timeOutFrequency");
//        keys.add("timeOutCount");
//        List<String> contents = buildNoticeMessage(noticeMessage);
//        Response<String> sendResult = smsSenderService.templateSend(Arrays.asList(connectWays), keys, contents, propertyService.getSMSNoticeCode());
//        if (sendResult.isSuccess())
//            log.error("发送成功");
//        else
//            log.error("发送失败，错误码是：" + sendResult.getError());
//    }
//
//    public void sendDingMessage(NoticeMessage noticeMessage, String[] connectWays) {
//        try {
//            for (String accessToken : connectWays) {
//                DingTalkClient dingTalkClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=" + accessToken);
//                OapiRobotSendRequest request = new OapiRobotSendRequest();
//                request.setMsgtype("text");
//                OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
//                String template = "Mule流程报警: 尊敬的用户:你的流程:%s于%s时候 ，" + "失败频次:%s超出阈值%d上限，频次为:%d，" +
//                        "超时频次:%s超出阈值%d上限，频次为:%d。";
//                String content = String.format(template, noticeMessage.getFlowName(), noticeMessage.getCreateDate(),
//                        noticeMessage.getIsTouchFailureAlarm() ? "" : "没有", noticeMessage.getFailureFrequency(), noticeMessage.getFailureCount(),
//                        noticeMessage.getIsTouchFailureAlarm() ? "" : "没有", noticeMessage.getTimeOutFrequency(), noticeMessage.getTimeOutCount());
//                text.setContent(content);
//                request.setText(text);
//                OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//                at.setIsAtAll(true);
//                request.setAt(at);
//                OapiRobotSendResponse execute = dingTalkClient.execute(request);
//                if (execute.isSuccess()) {
//                    log.error("发送成功");
//                } else {
//                    log.error("发送失败");
//                }
//            }
//        } catch (Exception e) {
//            log.error("ding send message error {}", e.getMessage());
//        }
//    }
//
//    private List<String> buildNoticeMessage(NoticeMessage noticeMessage) {
//        List<String> contents = new ArrayList<>();
//        contents.add(noticeMessage.getFlowName());
//        contents.add(noticeMessage.getCreateDate());
//        contents.add(noticeMessage.getIsTouchFailureAlarm() ? "" : "没有");
//        contents.add(String.valueOf(null == noticeMessage.getFailureFrequency() ? 0 : noticeMessage.getFailureFrequency()));
//        contents.add(String.valueOf(noticeMessage.getFailureCount()));
//        contents.add(noticeMessage.getIsTouchTimeOutAlarm() ? "" : "没有");
//        contents.add(String.valueOf(null == noticeMessage.getTimeOutFrequency() ? 0 : noticeMessage.getTimeOutFrequency()));
//        contents.add(String.valueOf(noticeMessage.getTimeOutCount()));
//        return contents;
//    }
//}
