package io.terminus.dalaran.core.log;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.TracingType;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.*;

/**
 * 尝试过 InterceptStrategy 和 TraceEventHandler, 最后决定自己写前后 processor 处理
 * 主要 CamelInternalProcessorAdvice 没有可用入口, 否则直接使用也是比较合适的
 */
@Slf4j
public class DalaranTracer {

    private final DalaranTraceLogger logger;

    private final TracingType tracingType;

    private final String processorId;

    private DalaranTracer(DalaranTraceLogger logger, TracingType tracingType, String processorId) {
        this.logger = logger;
        this.tracingType = tracingType;
        this.processorId = processorId;
    }

    public static DalaranTracer buildTracer(DalaranTraceLogger logger, TracingType tracingType) {
        return new DalaranTracer(logger, tracingType, null);
    }

    public static DalaranTracer buildTestSubFlowTracer(DalaranTraceLogger logger) {
        return new DalaranTracer(logger, TracingType.TestSubFlow, null);
    }

    public static DalaranTracer buildTestFlowTracer(DalaranTraceLogger logger) {
        return new DalaranTracer(logger, TracingType.TestFlow, null);
    }

    public static DalaranTracer buildFlowTracer(DalaranTraceLogger logger) {
        return new DalaranTracer(logger, TracingType.Flow, null);
    }

    public static DalaranTracer buildFlowSpanTracer(DalaranTraceLogger logger, String processorId) {
        return new DalaranTracer(logger, TracingType.Processor, processorId);
    }

    public static DalaranTracer buildConvertTracer(DalaranTraceLogger logger, String processorId) {
        return new DalaranTracer(logger, TracingType.Convert, processorId);
    }

    public static DalaranTracer buildSubFlowTracer(DalaranTraceLogger logger) {
        return new DalaranTracer(logger, TracingType.SubFlow, null);
    }

    public void before(RouteDefinition route, String modelType) {
        route.process(this.buildBeforeProcessor(modelType));
    }

    public void before(RouteDefinition route) {
        route.process(this.buildBeforeProcessor("UNKNOWN"));
    }

    public void after(RouteDefinition route, String modelType) {
        route.process(this.buildAfterProcessor(modelType));
    }


    public void after(RouteDefinition route) {
        route.process(this.buildAfterProcessor("UNKNOWN"));
    }

    // TODO async
    private Processor buildBeforeProcessor(String bodyModelType) {
        return new TraceBeforeProcessor(bodyModelType);
    }

    // TODO async
    private Processor buildAfterProcessor(String bodyModelType) {
        return new TraceAfterProcessor(bodyModelType);
    }

    private String getTracingLogPropertyKey() {
        switch (tracingType) {
            case Flow:
                return DalaranConstants.FLOW_TRACING_LOG;
            case TestFlow:
                return DalaranConstants.TEST_FLOW_TRACING_LOG;
            case TestSubFlow:
                return DalaranConstants.TEST_SUB_FLOW_TRACING_LOG;
            case Processor:
                return DalaranConstants.PROCESSOR_TRACING_LOG;
            case Convert:
                return DalaranConstants.CONVERT_TRACING_LOG;
            case SubFlow:
                return DalaranConstants.SUB_FLOW_TRACING_LOG;
            default:
                throw new RuntimeException("not support tracing type[" + tracingType + "]");
        }
    }

    private String checkLogSize(String body) {
        if (body != null && body.length() > 2796202) {
            return "large record.";
        }
        return body;
    }

    private String extractBody(Object body) {
        if (body == null) {
            return null;
        }
        if (body instanceof String) {
            return (String) body;
        }
        if (body instanceof byte[]) {
            return new String((byte[]) body);
        }
        if (body instanceof Map || body instanceof Iterable || body instanceof Serializable) {
            return JSON.toJSONString(body);
        }
        if (body instanceof StringEntity) {
            try {
                InputStream inputStream = ((StringEntity) body).getContent();
                List<String> data = IOUtils.readLines(inputStream);
                return StringUtils.join(data, System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (body instanceof InputStream) {
            try {
                return IOUtils.toString((InputStream)body, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return body.toString();
    }

    private class TraceBeforeProcessor implements Processor, Traceable {

        private final String bodyModelType;

        private TraceBeforeProcessor(String bodyModelType) {
            this.bodyModelType = bodyModelType;
        }

        @Override
        public void process(Exchange exchange) {
            // 保持输入输出不变
            exchange.getOut().copyFrom(exchange.getIn());

            String currentId = exchange.getExchangeId();
            DalaranTracingLog tracingLog = new DalaranTracingLog();
            boolean isMainFlow = TracingType.Flow == tracingType || TracingType.TestFlow == tracingType || TracingType.TestSubFlow == tracingType;
            tracingLog.setMain(isMainFlow);
            if (isMainFlow) {
                exchange.setProperty(LOG_MAIN_RECORD_ID, currentId);
            }

            String parentId = exchange.getProperty(CAMEL_CORRELATION_ID, String.class);
            String recordId = exchange.getProperty(LOG_MAIN_RECORD_ID, String.class);

            exchange.setProperty(getTracingLogPropertyKey() + currentId, tracingLog);
//            String testRecordId = exchange.getProperty(DalaranConstants.TEST_FLOW_RECORD_ID_HEADER, String.class);
//            if (testRecordId != null) {
//                tracingLog.setRecordId(testRecordId);
//            } else {
//                tracingLog.setRecordId(exchange.getExchangeId());
//            }
            if (recordId == null) {
                tracingLog.setRecordId(currentId);
            } else {
                tracingLog.setRecordId(recordId);
            }

            tracingLog.setTracingType(tracingType);
            tracingLog.setFlowId(exchange.getProperty(TRACING_FLOW_ID, Long.class));
            tracingLog.setModuleId(exchange.getProperty(TRACING_MODULE_ID, Long.class));
            tracingLog.setProcessorId(processorId);
            tracingLog.setTimestamp(System.currentTimeMillis());
            tracingLog.setInputBody(checkLogSize(extractBody(exchange.getIn().getBody())));
            tracingLog.setInputBodyType(bodyModelType);
        }

        @Override
        public String getTraceLabel() {
            switch (tracingType) {
                case Flow:
                    return "flow tracing before";
                case TestFlow:
                    return "test flow tracing before";
                case SubFlow:
                    return "sub flow tracing before";
                case Processor:
                    return "processor tracing before[processor(" + processorId + ")]";
                case Convert:
                    return "convert tracing before[processor(" + processorId + ")]";
                default:
                    return "DalaranTraceBefore";
            }
        }

        @Override
        public String toString() {
            return getTraceLabel();
        }
    }

    private class TraceAfterProcessor implements Processor, Traceable {

        private final String bodyModelType;

        private TraceAfterProcessor(String bodyModelType) {
            this.bodyModelType = bodyModelType;
        }

        @Override
        public void process(Exchange exchange) {
            // 保持输入输出不变
            exchange.getOut().copyFrom(exchange.getIn());
            String propertyKey = getTracingLogPropertyKey() + exchange.getExchangeId();
            DalaranTracingLog tracingLog = exchange.getProperty(propertyKey, DalaranTracingLog.class);
            if (tracingLog == null) {
                return;
            }
            exchange.removeProperty(propertyKey);
            tracingLog.setSuccessful(true);
            tracingLog.setOutputBody(checkLogSize(extractBody(exchange.getIn().getBody())));
            tracingLog.setOutputBodyType(bodyModelType);
            tracingLog.setElapsed(System.currentTimeMillis() - tracingLog.getTimestamp());
            logger.log(tracingLog);
        }

        @Override
        public String getTraceLabel() {
            switch (tracingType) {
                case Flow:
                    return "flow tracing after";
                case TestFlow:
                    return "test flow tracing after";
                case SubFlow:
                    return "sub flow tracing after";
                case Processor:
                    return "processor tracing after[processor(" + processorId + ")]";
                case Convert:
                    return "convert tracing after[processor(" + processorId + ")]";
                default:
                    return "DalaranTraceAfter";
            }
        }

        @Override
        public String toString() {
            return getTraceLabel();
        }
    }
}
