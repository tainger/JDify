package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticJob implements SimpleJob {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticJob.class);

    private ElasticJobEndpoint endpoint;

    private Processor processor;

    public ElasticJob(ElasticJobEndpoint endpoint, Processor processor) {
        this.endpoint = endpoint;
        this.processor = processor;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        LOG.info("execute test");
        Exchange exchange = endpoint.createExchange();
        try {
            processor.process(exchange);
        }catch (Exception e){
            LOG.info("process fail");
            e.printStackTrace();
        }
    }
}
