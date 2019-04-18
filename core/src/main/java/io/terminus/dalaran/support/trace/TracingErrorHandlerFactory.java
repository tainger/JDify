package io.terminus.dalaran.support.trace;

import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.model.DalaranTracingLog;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import org.apache.camel.processor.ErrorHandler;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.MessageHelper;

import static io.terminus.dalaran.DalaranConstants.FLOW_TRACING_LOG;
import static io.terminus.dalaran.DalaranConstants.TEST_FLOW;
import static io.terminus.dalaran.DalaranConstants.TRIGGER_TRACING_LOG;

public class TracingErrorHandlerFactory implements ErrorHandlerFactory {

    private final DalaranTraceLogger logger;

    public TracingErrorHandlerFactory(DalaranTraceLogger logger) {
        this.logger = logger;
    }

    @Override
    public Processor createErrorHandler(RouteContext routeContext, Processor processor) throws Exception {
        System.out.println("createErrorHandler:" + processor);
        return new DalaranErrorHandler(processor);
    }


    // TODO for test...
    private class DalaranErrorHandler implements ErrorHandler, Traceable {

        private Processor output;

        public DalaranErrorHandler(Processor output) {
            this.output = output;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            // TODO 理论上可以在这里做 tracing, 这样就不需要包前后的 processor 了, 回头可以看一下可行性
            System.out.println("before" + exchange.getIn().getBody() + "!!" + output);
            output.process(exchange);
            System.out.println("after" + exchange.getException() + "!!" + output);
            // TODO 当执行发成异常时, 记录未持久化的日志
            if (exchange.getException() != null) {
                exchange.getOut().copyFrom(exchange.getIn());

                String body = MessageHelper.extractBodyForLogging(exchange.getIn(), "");
                Boolean isTestFlow = exchange.getProperty(TEST_FLOW, Boolean.class);
                DalaranTracingLog flowTracingLog = exchange.getProperty(FLOW_TRACING_LOG, DalaranTracingLog.class);
                if (flowTracingLog != null) {
                    log(flowTracingLog, body, isTestFlow);
                }
                DalaranTracingLog triggerTracingLog = exchange.getProperty(TRIGGER_TRACING_LOG, DalaranTracingLog.class);
                if (triggerTracingLog != null) {
                    log(triggerTracingLog, body, isTestFlow);
                }
            }
        }

        private void log(DalaranTracingLog tracingLog, String body, Boolean isTestFlow) {
            if (tracingLog == null) {
                return;
            }
            tracingLog.setSuccessful(false);
            tracingLog.setOutputBody(body);
            tracingLog.setElapsed(System.currentTimeMillis() - tracingLog.getTimestamp());

            if (isTestFlow != null) {
                tracingLog.setTestFlow(isTestFlow);
            } else {
                tracingLog.setTestFlow(false);
            }
//            tracingLog.setOutputHeaders(exchange.getIn().getHeaders());

            logger.log(tracingLog);
        }

        @Override
        public String getTraceLabel() {
            return "DalaranErrorHandler";
        }
    }
}
