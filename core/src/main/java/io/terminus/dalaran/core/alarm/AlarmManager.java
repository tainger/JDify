package io.terminus.dalaran.core.alarm;

import io.terminus.dalaran.model.alarm.NoticeMessage;

public interface AlarmManager {

    void alarm(NoticeMessage noticeMessage);
}
