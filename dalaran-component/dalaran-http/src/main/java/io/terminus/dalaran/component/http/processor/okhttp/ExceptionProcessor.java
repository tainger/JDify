package io.terminus.dalaran.component.http.processor.okhttp;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ExceptionProcessor implements Processor {

    private Exception exception;

    public ExceptionProcessor(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void process(Exchange exchange) {
        exchange.setException(exception);
    }
}
