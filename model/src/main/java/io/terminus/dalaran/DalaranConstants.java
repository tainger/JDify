package io.terminus.dalaran;

public final class DalaranConstants {

    public static final String DELIMITER = "-";

    public static final String CONVERT_TRACING_LOG = "dalaran_trigger_convert_log";
    public static final String TEST_FLOW_TRACING_LOG = "dalaran_test_flow_tracing_log";
    public static final String TEST_SUB_FLOW_TRACING_LOG = "dalaran_test_sub_flow_tracing_log";
    public static final String SUB_FLOW_TRACING_LOG = "dalaran_sub_flow_tracing_log";
    public static final String FLOW_TRACING_LOG = "dalaran_flow_tracing_log";
    public static final String PROCESSOR_TRACING_LOG = "dalaran_processor_tracing_log";
    public static final String TEST_FLOW_RECORD_ID_HEADER = "dalaran_test_flow_record_id";
    public static final String BRANCH_FLOW_NAME_HEADER = "DalaranBranchFlowName";

    public static final String TRACING_FLOW_ID = "DalaranTracingFlowId";
    public static final String TRACING_MODULE_ID = "DalaranTracingModuleId";

    public static final String DALARAN_PROCESSOR = "DalaranProcessorRouteId-";



    public static final String SCATTER_GATHER_EXCHANGE = "DalaranScatterGatherExchange";
    public static final String DALARAN_CONTEXT_EXCHANGE = "DalaranContextExchange";
    public static final String CAMEL_CORRELATION_ID = "CamelCorrelationId";
    public static final String LOG_MAIN_RECORD_ID = "LogMainRecordId";

    public static final String LOG_MAIN_RECORD_HANDLED = "LogMainRecordHandled";

    public static final String CAMEL_MULTICAST_COMPLETE = "CamelMulticastComplete";

    public static final String DALARAN_CONTEXT_HEADER = "DalaranContextHeader";

    public static final String MODEL_ROOT = "root";

    public static final String TEST_FLOW_PREFIX = "test" + DELIMITER;
    public static final String TEST_SUB_FLOW_PREFIX = "test" + DELIMITER + "sub" + DELIMITER;

    public static final String FLOW_PREFIX = "flow" + DELIMITER;
    public static final String FLOW_FRAGMENT_PREFIX = DELIMITER + "fragment" + DELIMITER;
    public static final String SUB_FLOW_PREFIX = "sub" + DELIMITER + FLOW_PREFIX;
    public static final String DIRECT_PREFIX = "direct:";
    public static final String TEST_FLOW_DIRECT_PREFIX = DIRECT_PREFIX + TEST_FLOW_PREFIX;
    public static final String TEST_SUB_FLOW_DIRECT_PREFIX = DIRECT_PREFIX + TEST_SUB_FLOW_PREFIX;
    public static final String SUB_FLOW_DIRECT_PREFIX = DIRECT_PREFIX + SUB_FLOW_PREFIX;


    public static final String ENV_REPLACE_PREFIX = "${{";
    public static final String ENV_REPLACE_SUFFIX = "}}";

    public static final String AUTH_SIGN = "sign";

    public static final String AUTH_APP_KEY = "appKey";

    public static final String AUTH_APP_SECRET = "appSecret";

    public static final String OBJECT_MODEL_TYPE = "OBJECT";
    public static final String UNKNOWN_MODEL_TYPE = "UNKNOWN";

    public static final String DALARAN = "Dalaran";
    public static final String PARTNER = "Partner";

    public static final String TRIGGER = "Trigger";
    public static final String PROCESSOR = "Processor";
    public static final String FLOW_TEMPLATE = "FlowTemplate";
    public static final String SUB_FLOW_TEMPLATE = "SubFlowTemplate";

    public static final String MAPPER_CONVERT = "mapper-convert";
    public static final String SOAP_TYPE = "SOAP";

    public static final String DALARAN_EXPRESSION_HEADER = "DALARAN_EXPRESSION::";

    public static final String DALARAN_COMPONENT_DEFAULT_DESC = "";


    public static final String MODEL = "model";
    public static final String CONNECTOR = "connector";
    public static final String FUNCTION = "function";
    public static final String SERVICE = "service";
    public static final String SUB_FLOW = "subflow";
    public static final String PACKAGE = "package";





}
