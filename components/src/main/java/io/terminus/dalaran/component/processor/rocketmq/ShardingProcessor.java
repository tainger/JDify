package io.terminus.dalaran.component.processor.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;

import java.util.List;

public class ShardingProcessor implements Processor, Traceable {

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        if (body instanceof Iterable) {
            List<Object> messages = (List) body;

        }
    }

    @Override
    public String getTraceLabel() {
        return "sharding processor";
    }
}
