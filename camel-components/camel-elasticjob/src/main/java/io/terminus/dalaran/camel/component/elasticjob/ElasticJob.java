package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

public class ElasticJob implements SimpleJob {

    private ElasticJobEndpoint endpoint;

    private Processor processor;

    public ElasticJob(ElasticJobEndpoint endpoint, Processor processor) {
        this.endpoint = endpoint;
        this.processor = processor;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Exchange exchange = endpoint.createExchange();
        try {
            processor.process(exchange);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
