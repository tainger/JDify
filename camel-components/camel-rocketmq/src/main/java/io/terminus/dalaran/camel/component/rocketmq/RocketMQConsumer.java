package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQConsumer extends DefaultConsumer {

    private RocketMQEndpoint endpoint;

    private Processor processor;

    private RocketMQConfiguration configuration;

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
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(endpoint.getGroupId());
        if (StringUtils.isNotBlank(endpoint.getTags())) {
            consumer.subscribe(endpoint.getTopic(), endpoint.getTags());
        } else {
            consumer.subscribe(endpoint.getTopic(), "*");
        }
        consumer.setNamesrvAddr(endpoint.getNameServer());
        consumer.setVipChannelEnabled(false);
        consumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                msgs.forEach(messageExt -> {
                    try {
                        Exchange exchange = endpoint.createRocketMQExchange(messageExt.getBody());
                        processor.process(exchange);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
