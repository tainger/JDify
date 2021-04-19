package io.terminus.dalaran.core.resource.redis;

public class RedisUtil {

    private static final String SPLIT = ":";

    private static final String ALARM_ID = "alarm_id";

    private static final String  ALARM_CONFIG = "alarm_config:trigger_id:";

    private static final String  RELEASED_FLOW_IDS = "released_flow_ids";

    private static final String  RECORD_FLOW_ID = "log_record:flow_id";

    private static final String  FAILURE_FILED_KEY = "failure_filed";

    private static final String  TIMEOUT_FILED_KEY = "timeout_filed";

    private static final String  CURRENT_TIME = "current_time";

    private static final String  TIME_TO_MONITOR = "time_to_monitor";

    public static String getAlarmRuleKey(String alarmRuleId) {
        return  ALARM_ID + SPLIT + alarmRuleId;
    }


    public static String getAlarmConfigKey(String triggerId) {
        return  ALARM_CONFIG + triggerId;
    }

    public static String getReleasedFlowIdsKey() {
        return  RELEASED_FLOW_IDS;
    }

    public static String getRecordFlowId(String flowId) {
        return  RECORD_FLOW_ID + SPLIT + flowId;
    }

    public static String getFailureKey(String flowId, String time) {
        return RECORD_FLOW_ID + SPLIT + flowId + SPLIT + FAILURE_FILED_KEY + SPLIT + time;
    }

    public static String getTimeOutKey(String flowId, String time) {
        return  RECORD_FLOW_ID + SPLIT + flowId + SPLIT + TIMEOUT_FILED_KEY + SPLIT + time;
    }

    public static String getCurrentTime() {
        return  CURRENT_TIME;
    }

    public static String getTimeToMonitor() {
        return  TIME_TO_MONITOR;
    }



}
