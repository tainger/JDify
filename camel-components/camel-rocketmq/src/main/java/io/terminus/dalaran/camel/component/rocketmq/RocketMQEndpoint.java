package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.*;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

/**
 * Created by jingdi on 2019/6/14
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "rocketmq", title = "RocketMQ", syntax = "rocketmq:topic", label = "mq")
public class RocketMQEndpoint extends ProcessorEndpoint {

    @UriParam(description = "name server address", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String nameServer;

    @UriParam(description = "topic", javaType = "java.lang.String")
    @Metadata(required = "true")
    private String topic;

    @UriParam(description = "consumer/producer group", javaType = "java.lang.String")
    private String groupId;

    @UriParam(description = "consumer/producer tags", javaType = "java.lang.String")
    private String tags;

    @UriParam(description = "use ali cloud ons service", javaType = "java.lang.Boolean", defaultValue = "false")
    private Boolean useAliCloudOns;

    @UriParam(description = "ACL accessKey", javaType = "java.lang.String")
    private String accessKey;

    @UriParam(description = "ACL secretKey", javaType = "java.lang.String")
    private String secretKey;

    @UriParam(description = "auto commit", javaType = "java.lang.Boolean", defaultValue = "true")
    private String autoCommit;

    private RocketMQContext context;

    public RocketMQEndpoint(RocketMQContext context) {
        this.context = context;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        RocketMQConsumer consumer = new RocketMQConsumer(this, processor, null);
        return consumer;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new RocketMQProducer(this);
    }

    @Override
    protected String createEndpointUri() {
        return "rocketmq://" + topic + "." + tags + "." + nameServer + "." + groupId;
    }

    public Exchange createRocketMQExchange(byte[] body) {
        Exchange exchange = super.createExchange();
        Message in = exchange.getIn();
        in.setBody(body);
        return exchange;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public RocketMQContext getContext() {
        return context;
    }

    public void setContext(RocketMQContext context) {
        this.context = context;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Boolean getUseAliCloudOns() {
        return useAliCloudOns;
    }

    public void setUseAliCloudOns(Boolean useAliCloudOns) {
        this.useAliCloudOns = useAliCloudOns;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
