package io.terminus.dalaran.camel.component.elasticjob;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobAPIFactory;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

@Slf4j
public class ElasticJobConsumer extends DefaultConsumer {

    private ElasticJobEndpoint endpoint;

    private Processor processor;

    private CoordinatorRegistryCenter regCenter;


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
        new ScheduleJobBootstrap(createRegistryCenter(), new ElasticJob(endpoint, processor), createJobConfiguration()).shutdown();
        //这里需要先remove掉这个jobName在zk中的配置，否则会出现doStart后该jobName还是老的配置
        JobAPIFactory.createJobConfigurationAPI(endpoint.getServerLists(), endpoint.getNamespace(), null).removeJobConfiguration(endpoint.getJobName());
        if (regCenter != null) {
            regCenter.close();
        }
    }

    private CoordinatorRegistryCenter createRegistryCenter() {
        if (regCenter == null) {
            regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(endpoint.getServerLists(), endpoint.getNamespace()));
            regCenter.init();
        }
        return regCenter;
    }

    private JobConfiguration createJobConfiguration() {
        JobConfiguration jobConfig = JobConfiguration.newBuilder(endpoint.getJobName(), endpoint.getShardingTotalCount()).cron(endpoint.getCron()).jobErrorHandlerType("LOG").build();
        return jobConfig;
    }
}
