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

    public ElasticJobEndpoint(String uri, ElasticJobComponent component) {
        super(uri, component);
    }

    @Override
    protected String createEndpointUri() {
        return "elasticjob://" + serverLists + "." + namespace + "." + jobName + "." + cron;
    }

    public void addJobInScheduler () throws Exception{
        new ScheduleJobBootstrap(createRegistryCenter(), new ElasticJob(this), createJobConfiguration()).schedule();
        jobAdded.set(true);
    }

    private void removeJobInScheduler() throws Exception {
        jobOperateAPI.remove(jobName,serverLists);
        jobAdded.set(false);
    }

    private void disableJobInScheduler() throws Exception {
        if (jobPaused.get()) {
            return;
        }
        jobPaused.set(true);
        jobOperateAPI.disable(jobName,serverLists);
    }

    private void enableJobInScheduler() throws Exception {
        if (!jobPaused.get()) {
            return;
        }
        jobPaused.set(false);
        jobOperateAPI.enable(jobName,serverLists);
    }

    private CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverLists, namespace));
        regCenter.init();
        return regCenter;
    }

    private JobConfiguration createJobConfiguration() {
        return JobConfiguration.newBuilder(jobName, 1).cron(cron).jobErrorHandlerType("LOG").build();
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("elasticjob producer is not supported.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return super.createConsumer(processor);
    }

    @Override
    protected void doStart() throws Exception {
        addJobInScheduler();
    }

    @Override
    protected void doStop() throws Exception {
        removeJobInScheduler();
    }

    public LoadBalancer getConsumerLoadBalancer() {
        if (consumerLoadBalancer == null) {
            consumerLoadBalancer = new RoundRobinLoadBalancer();
        }
        return consumerLoadBalancer;
    }

    public void onConsumerStart(ElasticJobConsumer consumer) throws Exception{
        getConsumerLoadBalancer().addProcessor(consumer.getProcessor());
        if (!jobAdded.get()) {
            addJobInScheduler();
        } else {
            enableJobInScheduler();
        }
    }

    public void onConsumerStop(ElasticJobConsumer consumer) throws Exception{
        getConsumerLoadBalancer().removeProcessor(consumer.getProcessor());
        if (jobAdded.get()) {
            disableJobInScheduler();
        }
    }

}
