package io.terminus.dalaran.camel.component.rocketmq;

import com.alibaba.fastjson.JSON;
import org.apache.camel.*;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

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

    private RocketMQContext context;

    public RocketMQEndpoint(RocketMQContext context) {
        this.context = context;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        RocketMQConsumer consumer = new RocketMQConsumer(this, processor, null);
//        this.configureConsumer(consumer);
        return consumer;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new RocketMQProducer(this);
    }

    @Override
    protected String createEndpointUri() {
        return "rocketmq://" + topic + "." + nameServer + "." + groupId;
    }

    public Exchange createRocketMQExchange(byte[] body) {
        Exchange exchange = super.createExchange();
        Message in = exchange.getIn();
        in.setBody(body);
        Message out = exchange.getOut();
        out.setBody(body);
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
}
