package io.terminus.dalaran.component.processor.multicast;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.BRANCH_FLOW_NAME_HEADER;
import static io.terminus.dalaran.DalaranConstants.SCATTER_GATHER_EXCHANGE;

public class ScatterGatherAggregationStrategy implements CompletionAwareAggregationStrategy {
    @Override
    public void onCompletion(Exchange exchange) {
        if (exchange != null) {
            Map<String, Object> dataMapper = (Map<String, Object>) exchange.removeProperty(SCATTER_GATHER_EXCHANGE);
            if (dataMapper != null) {
                exchange.getIn().setBody(dataMapper);
            }
        }
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<String, Object> dataMapper;
        if (oldExchange == null) {
            dataMapper = getMapper(newExchange);
        } else {
            dataMapper = getMapper(oldExchange);
        }

        if (newExchange != null) {
            String branchName = newExchange.getProperty(BRANCH_FLOW_NAME_HEADER, String.class);
            Object value = newExchange.getIn().getBody();
            if (value != null) {
                dataMapper.put(branchName, value);
            }
        }

        return oldExchange != null ? oldExchange : newExchange;
    }

    private Map<String, Object> getMapper(Exchange exchange) {
        Map<String, Object> dataMapper = exchange.getProperty(SCATTER_GATHER_EXCHANGE, Map.class);
        if (dataMapper == null) {
            dataMapper = new HashMap<>();
            exchange.setProperty(SCATTER_GATHER_EXCHANGE, dataMapper);
        }

        return dataMapper;
    }
}
