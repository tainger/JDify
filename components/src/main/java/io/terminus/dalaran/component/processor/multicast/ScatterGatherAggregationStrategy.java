package io.terminus.dalaran.component.processor.multicast;

import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.BRANCH_FLOW_NAME_HEADER;
import static io.terminus.dalaran.DalaranConstants.SCATTER_GATHER_EXCHANGE;

public class ScatterGatherAggregationStrategy implements CompletionAwareAggregationStrategy {
    @Override
    public void onCompletion(Exchange exchange) {
        if (exchange != null) {
            Map<String, Object> dataMapper = (Map<String, Object>) exchange.removeProperty(SCATTER_GATHER_EXCHANGE + exchange.getExchangeId());
            if (dataMapper != null) {
                List<Object> body = new ArrayList<>();
                for (Map.Entry<String, Object> entry: dataMapper.entrySet()) {
                    try {
                        body.add(transferData(entry.getValue()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                exchange.getIn().setBody(body);
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
        Map<String, Object> dataMapper = exchange.getProperty(SCATTER_GATHER_EXCHANGE + exchange.getExchangeId(), Map.class);
        if (dataMapper == null) {
            dataMapper = new HashMap<>();
            exchange.setProperty(SCATTER_GATHER_EXCHANGE + exchange.getExchangeId(), dataMapper);
        }

        return dataMapper;
    }

    private String transferData(Object in) throws Exception {
        if (in instanceof byte[]) {
            return JSON.parse((byte[])in).toString();
        }
        if (in instanceof String) {
            return (String)in;
        }
        return JSON.toJSONString(in);
    }
}
