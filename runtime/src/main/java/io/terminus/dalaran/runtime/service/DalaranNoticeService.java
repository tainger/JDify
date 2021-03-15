package io.terminus.dalaran.runtime.service;


import io.terminus.dalaran.model.alarm.NoticeMessage;

public interface DalaranNoticeService {

    void sendEmail(NoticeMessage noticeMessage);

    void sendShortMessage(NoticeMessage noticeMessage);
}
