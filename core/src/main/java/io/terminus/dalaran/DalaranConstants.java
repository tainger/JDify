package io.terminus.dalaran;

public final class DalaranConstants {

    public static final String CURRENT_FLOW_ID = "dalaran_flow_id";
    public static final String CURRENT_TRIGGER_ID = "dalaran_trigger_id";
    public static final String CURRENT_PROCESSOR_ID = "dalaran_processor_id";
    public static final String TRIGGER_TRACING_LOG = "dalaran_trigger_tracing_log";
    public static final String FLOW_TRACING_LOG = "dalaran_flow_tracing_log";

    public static final String FLOW_PREFIX = "flow-";
    public static final String TEST_FLOW_PREFIX = "test-flow-";
    public static final String TRIGGER_PREFIX = "trigger-";
    public static final String FLOW_CAMEL_URI_PREFIX = "direct:" + FLOW_PREFIX;
    public static final String TEST_FLOW_CAMEL_URI_PREFIX = "direct:" + TEST_FLOW_PREFIX;


}
