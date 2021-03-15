package io.terminus.dalaran.core.resource.redis;

public class RedisUtil {

    private static final String SPLIT = ":";

    private static final String ALARM_ID = "alarm:id";

    private static final String  Alarm_Config = "alarm_id:trigger_id:config";


    public static String getAlarmRuleKey(String alarmRuleId) {
        return  ALARM_ID + SPLIT + alarmRuleId;
    }


    public static String getAlarmConfigKey() {
        return  Alarm_Config;
    }

}
