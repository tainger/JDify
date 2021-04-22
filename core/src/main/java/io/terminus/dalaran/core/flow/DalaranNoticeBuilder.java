package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.alarm.NoticeMessage;

public interface DalaranNoticeBuilder {

    void sendEmail(NoticeMessage noticeMessage);

    void sendShortMessage(NoticeMessage noticeMessage);

    void sendDingMessage(NoticeMessage noticeMessage);

}
