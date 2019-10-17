package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQConsumer extends DefaultConsumer {

    private RocketMQEndpoint endpoint;

    private Processor processor;

    private RocketMQConfiguration configuration;

    private DefaultMQPushConsumer consumer;

    public RocketMQConsumer(RocketMQEndpoint endpoint, Processor processor, RocketMQConfiguration configuration) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
        this.configuration = configuration;
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        RPCHook rpcHook = null;
        if (StringUtils.isNotBlank(endpoint.getAccessKey()) && StringUtils.isNotBlank(endpoint.getSecretKey())) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(endpoint.getAccessKey(), endpoint.getSecretKey()));
        }
        consumer = new DefaultMQPushConsumer(endpoint.getGroupId(), rpcHook, new AllocateMessageQueueAveragely());
        consumer.setNamesrvAddr(endpoint.getNameServer());
        if (endpoint.getUseAliCloudOns()) {
            consumer.setAccessChannel(AccessChannel.CLOUD);
        } else {
            consumer.setAccessChannel(AccessChannel.LOCAL);
        }
        if (StringUtils.isNotBlank(endpoint.getTags())) {
            consumer.subscribe(endpoint.getTopic(), endpoint.getTags());
        } else {
            consumer.subscribe(endpoint.getTopic(), "*");
        }
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            msgs.forEach(messageExt -> {
                try {
                    Exchange exchange = endpoint.createRocketMQExchange(messageExt.getBody());
                    processor.process(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }

}
