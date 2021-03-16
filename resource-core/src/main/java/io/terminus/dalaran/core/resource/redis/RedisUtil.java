package io.terminus.dalaran.core.resource.redis;

public class RedisUtil {

    private static final String SPLIT = ":";

    private static final String ALARM_ID = "alarm:id";

    private static final String  ALARM_CONFIG = "alarm_config:trigger_id:";

    private static final String  RELEASED_FLOW_IDS = "released_ids";


    public static String getAlarmRuleKey(String alarmRuleId) {
        return  ALARM_ID + SPLIT + alarmRuleId;
    }


    public static String getAlarmConfigKey(String triggerId) {
        return  ALARM_CONFIG + triggerId;
    }

    public static String getReleasedFlowIdsKey() {
        return  RELEASED_FLOW_IDS;
    }


}
