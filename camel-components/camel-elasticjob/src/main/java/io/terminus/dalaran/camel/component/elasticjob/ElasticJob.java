package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticJob implements SimpleJob {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticJob.class);

    private ElasticJobEndpoint endpoint;

    public ElasticJob(ElasticJobEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Exchange exchange = null;
        try {
            exchange = endpoint.createExchange();
            exchange.setIn(new ElasticJobMessage(exchange, shardingContext));
            endpoint.getConsumerLoadBalancer().process(exchange);
        }catch (Exception e) {
            if (exchange != null) {
                LOG.error(CamelExchangeException.createExceptionMessage("Error processing exchange", exchange, e));
            } else {
                LOG.error("Failed to execute ElasticJob.", e);
            }
        }
    }
}
