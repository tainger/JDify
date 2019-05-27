package io.terminus.dalaran;

public final class DalaranConstants {

    public static final String DELIMITER = "-";

    public static final String CONVERT_TRACING_LOG = "dalaran_trigger_convert_log";
    public static final String TEST_FLOW_TRACING_LOG = "dalaran_test_flow_tracing_log";
    public static final String FLOW_TRACING_LOG = "dalaran_flow_tracing_log";
    public static final String PROCESSOR_TRACING_LOG = "dalaran_processor_tracing_log";
    public static final String TEST_FLOW_RECORD_ID_HEADER = "dalaran_test_flow_record_id";

    public static final String TEST_FLOW_PREFIX = "test" + DELIMITER;

    public static final String FLOW_PREFIX = "flow" + DELIMITER;
    public static final String FLOW_FRAGMENT_PREFIX = "fragment" + DELIMITER + FLOW_PREFIX;
    public static final String SUB_FLOW_PREFIX = "sub" + DELIMITER + FLOW_PREFIX;
    public static final String DIRECT_PREFIX = "direct:";
    public static final String TEST_FLOW_DIRECT_PREFIX = DIRECT_PREFIX + TEST_FLOW_PREFIX;

}
