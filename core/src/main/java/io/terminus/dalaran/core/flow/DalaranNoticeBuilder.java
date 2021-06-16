package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.alarm.NoticeMessage;

public interface DalaranNoticeBuilder {

    void sendEmail(NoticeMessage noticeMessage, String[] connectWays);

    void sendShortMessage(NoticeMessage noticeMessage, String[] connectWays);

    void sendDingMessage(NoticeMessage noticeMessage, String[] connectWays);

}
