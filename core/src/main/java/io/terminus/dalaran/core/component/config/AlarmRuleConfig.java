package io.terminus.dalaran.core.component.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class AlarmRuleConfig {


    private TimeOutAlarm timeOutAlarm;

    private FailureAlarm failureAlarm;

    private Map<ChannelType, String[]> alarmChannel = new HashMap();

    @Data
    public class TimeOutAlarm{

        private Boolean isOpen;

        private Long elapse;

        private Long elapsedFrequency;
    }

    @Data
    public class FailureAlarm{

        private Boolean isOpen;

        private Long failureFrequency;
    }


    public enum ChannelType {
        mail, shortMessage, appPush
    }
}
