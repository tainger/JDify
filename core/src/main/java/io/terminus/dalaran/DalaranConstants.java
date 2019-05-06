package io.terminus.dalaran;

public final class DalaranConstants {

    public static final String CONVERT_TRACING_LOG = "dalaran_trigger_convert_log";
    public static final String TEST_FLOW_TRACING_LOG = "dalaran_test_flow_tracing_log";
    public static final String FLOW_TRACING_LOG = "dalaran_flow_tracing_log";
    public static final String PROCESSOR_TRACING_LOG = "dalaran_processor_tracing_log";
    public static final String TEST_FLOW_RECORD_ID_HEADER = "dalaran_test_flow_record_id";

    public static final String FLOW_PREFIX = "flow-";
    public static final String TEST_FLOW_PREFIX = "test-flow-";
    public static final String FLOW_CAMEL_URI_PREFIX = "direct:" + FLOW_PREFIX;
    public static final String TEST_FLOW_CAMEL_URI_PREFIX = "direct:" + TEST_FLOW_PREFIX;


}
