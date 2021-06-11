package io.terminus.dalaran.core.resource.redis;

public class RedisUtil {

    private static final String SPLIT = ":";

    private static final String ALARM_ID = "alarm_id";

    private static final String ALARM_CONFIG = "alarm_config:trigger_id:";

    private static final String RELEASED_FLOW_INFO = "released_flow_info";

    private static final String RELEASED_FLOW_INFO_TIME_OUT = "released_flow_ids_time_out";

    private static final String RECORD_FLOW_ID = "log_record:flow_id";

    private static final String FAILURE_FILED_KEY = "failure";

    private static final String TIMEOUT_FILED_KEY = "timeout";

    private static final String IS_HAVE_ALARMED = "have_alarmed";

    private static final String NOTICE_QUEUE = "notice_queue";

    public static String getAlarmRuleKey(String alarmRuleId) {
        return ALARM_ID + SPLIT + alarmRuleId;
    }

    public static String getAlarmConfigKey(String triggerId) {
        return ALARM_CONFIG + triggerId;
    }

    public static String getReleasedFlowIdsKey() {
        return RELEASED_FLOW_INFO;
    }

    public static String getReleasedFlowIdsTimeOut() {
        return RELEASED_FLOW_INFO_TIME_OUT;
    }

    public static String getRecordFlowId(String flowId) {
        return RECORD_FLOW_ID + SPLIT + flowId;
    }

    public static String getFailureKey(String flowId, String time) {
        return RECORD_FLOW_ID + SPLIT + flowId + SPLIT + FAILURE_FILED_KEY + SPLIT + time;
    }

    public static String getTimeOutKey(String flowId, String time) {
        return RECORD_FLOW_ID + SPLIT + flowId + SPLIT + TIMEOUT_FILED_KEY + SPLIT + time;
    }

    public static String getIsHaveAlarmed(String flowId) {
        return IS_HAVE_ALARMED + SPLIT + flowId;
    }


    public static String getNoticeQueue() {
        return NOTICE_QUEUE;
    }


}
