package io.terminus.dalaran.core.log;


import org.apache.camel.*;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.processor.DefaultErrorHandler;
import org.apache.camel.processor.RedeliveryPolicy;
import org.apache.camel.processor.exceptionpolicy.ExceptionPolicyStrategy;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.AsyncProcessorHelper;
import org.apache.camel.util.CamelLogger;

import java.util.concurrent.ScheduledExecutorService;

import static io.terminus.dalaran.DalaranConstants.*;

public class TracingErrorHandlerFactory extends DefaultErrorHandlerBuilder implements ErrorHandlerFactory {

    private final DalaranTraceLogger dalaranTraceLogger;

    public TracingErrorHandlerFactory(DalaranTraceLogger dalaranTraceLogger) {
        this.dalaranTraceLogger = dalaranTraceLogger;
    }

    @Override
    public Processor createErrorHandler(RouteContext routeContext, Processor processor) throws Exception {
        DefaultErrorHandler answer = new DalaranErrorHandler(routeContext.getCamelContext(), processor, getLogger(), getOnRedelivery(),
                getRedeliveryPolicy(), getExceptionPolicyStrategy(), getRetryWhilePolicy(routeContext.getCamelContext()),
                getExecutorService(routeContext.getCamelContext()), getOnPrepareFailure(), getOnExceptionOccurred());
        // configure error handler before we can use it
        configure(routeContext, answer);
        return answer;
    }

    // TODO for test...
    private class DalaranErrorHandler extends DefaultErrorHandler implements Traceable {

        /**
         * Creates the default error handler.
         *
         * @param camelContext                 the camel context
         * @param output                       outer processor that should use this default error handler
         * @param logger                       logger to use for logging failures and redelivery attempts
         * @param redeliveryProcessor          an optional processor to run before redelivery attempt
         * @param redeliveryPolicy             policy for redelivery
         * @param exceptionPolicyStrategy      strategy for onException handling
         * @param retryWhile                   retry while
         * @param executorService              the {@link ScheduledExecutorService} to be used for redelivery thread pool. Can be <tt>null</tt>.
         * @param onPrepareProcessor           a custom {@link Processor} to prepare the {@link Exchange} before
         *                                     handled by the failure processor / dead letter channel.
         * @param onExceptionOccurredProcessor a custom {@link Processor} to process the {@link Exchange} just after an exception was thrown.
         */
        public DalaranErrorHandler(CamelContext camelContext, Processor output, CamelLogger logger, Processor redeliveryProcessor, RedeliveryPolicy redeliveryPolicy, ExceptionPolicyStrategy exceptionPolicyStrategy, Predicate retryWhile, ScheduledExecutorService executorService, Processor onPrepareProcessor, Processor onExceptionOccurredProcessor) {
            super(camelContext, output, logger, redeliveryProcessor, redeliveryPolicy, exceptionPolicyStrategy, retryWhile, executorService, onPrepareProcessor, onExceptionOccurredProcessor);
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            AsyncProcessorHelper.process(this, exchange);
        }

        @Override
        public boolean process(Exchange exchange, AsyncCallback callback) {
            // TODO 理论上可以在这里做 tracing, 这样就不需要包前后的 processor 了, 回头可以看一下可行性
            super.process(exchange, callback);
            // TODO 当执行发成异常时, 记录未持久化的日志
            if (exchange.getException() != null) {
                String body = exchange.getException().toString();
                log(exchange, FLOW_TRACING_LOG, body);
                log(exchange, TEST_FLOW_TRACING_LOG, body);
                log(exchange, TEST_SUB_FLOW_TRACING_LOG, body);
                log(exchange, PROCESSOR_TRACING_LOG, body);
            }
            callback.done(true);
            return true;
        }

        private void log(Exchange exchange, String tracingKey, String body) {
            DalaranTracingLog tracingLog = exchange.getProperty(tracingKey, DalaranTracingLog.class);
            if (tracingLog == null) {
                return;
            }
            exchange.removeProperty(tracingKey);
            tracingLog.setSuccessful(false);
            tracingLog.setOutputBodyType("EXCEPTION");
            tracingLog.setOutputBody(body);
            tracingLog.setElapsed(System.currentTimeMillis() - tracingLog.getTimestamp());
//            tracingLog.setOutputHeaders(exchange.getIn().getHeaders());
            dalaranTraceLogger.log(tracingLog);
        }

        @Override
        public String getTraceLabel() {
            return "DalaranErrorHandler";
        }
    }
}
