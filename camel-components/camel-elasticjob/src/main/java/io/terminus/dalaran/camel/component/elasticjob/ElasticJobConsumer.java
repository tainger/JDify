package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;

public class ElasticJobConsumer extends DefaultConsumer {

    @Autowired
    private JobOperateAPI jobOperateAPI;

    private ElasticJobEndpoint endpoint;

    private Processor processor;


    public ElasticJobConsumer(ElasticJobEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        new ScheduleJobBootstrap(createRegistryCenter(), new ElasticJob(endpoint, processor), createJobConfiguration()).schedule();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        jobOperateAPI.remove(endpoint.getJobName(), endpoint.getServerLists());
    }

    private CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(endpoint.getServerLists(), endpoint.getNamespace()));
        regCenter.init();
        return regCenter;
    }

    private JobConfiguration createJobConfiguration() {
        return JobConfiguration.newBuilder(endpoint.getJobName(), 1).cron(endpoint.getCron()).jobErrorHandlerType("LOG").build();
    }
}
