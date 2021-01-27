package io.terminus.dalaran.camel.component.elasticjob;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.processor.loadbalancer.LoadBalancer;
import org.apache.camel.processor.loadbalancer.RoundRobinLoadBalancer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicBoolean;

@UriEndpoint(firstVersion = "1.0.0", scheme = "elasticjob", title = "ElasticJob", syntax = "elasticjob:jobName", label = "schedule")
public class ElasticJobEndpoint extends ProcessorEndpoint {

    @Autowired
    private JobOperateAPI jobOperateAPI;

    private LoadBalancer consumerLoadBalancer;

    private final AtomicBoolean jobAdded = new AtomicBoolean(false);

    private final AtomicBoolean jobPaused = new AtomicBoolean(false);

    @UriParam(description = "连接 ZooKeeper 服务器的列表", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String serverLists;

    @UriParam(description = "ZooKeeper 的命名空间", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String namespace;

    @UriParam(description = "作业名称", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String jobName;

    @UriParam(description = "CRON 表达式，用于控制作业触发时间", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String cron;

    @UriParam(description = "作业分片总数", javaType = "java.lang.Integer")
    @Metadata(required = "true")
    private Integer shardingTotalCount;

    public String getServerLists() {
        return serverLists;
    }

    public void setServerLists(String serverLists) {
        this.serverLists = serverLists;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getShardingTotalCount() {
        return shardingTotalCount;
    }

    public void setShardingTotalCount(Integer shardingTotalCount) {
        this.shardingTotalCount = shardingTotalCount;
    }

    public ElasticJobEndpoint() {
    }

    @Override
    protected String createEndpointUri() {
        return "elasticjob://" + serverLists + "." + namespace + "." + jobName + "." + cron + "." + shardingTotalCount;
    }

    @Override
    public Producer createProducer() {
        throw new UnsupportedOperationException("elasticjob producer is not supported.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        ElasticJobConsumer consumer = new ElasticJobConsumer(this, processor);
        return consumer;
    }

}
