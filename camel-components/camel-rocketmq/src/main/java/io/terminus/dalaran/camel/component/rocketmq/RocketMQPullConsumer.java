package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.List;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQPullConsumer extends DefaultConsumer {

    private RocketMQEndpoint endpoint;

    private Processor processor;

    private RocketMQConfiguration configuration;

    private DefaultMQPushConsumer consumer;

    private MQPullConsumerScheduleService service;

    public RocketMQPullConsumer(RocketMQEndpoint endpoint, Processor processor, RocketMQConfiguration configuration) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
        this.configuration = configuration;
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        service.shutdown();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        RPCHook rpcHook = null;
        if (StringUtils.isNotBlank(endpoint.getAccessKey()) && StringUtils.isNotBlank(endpoint.getSecretKey())) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(endpoint.getAccessKey(), endpoint.getSecretKey()));
        }

        service = new MQPullConsumerScheduleService(endpoint.getGroupId(), rpcHook);
        service.getDefaultMQPullConsumer().setNamesrvAddr(endpoint.getNameServer());
        service.registerPullTaskCallback(endpoint.getTopic(), (messageQueue, pullTaskContext) -> {
            MQPullConsumer consumer = pullTaskContext.getPullConsumer();
            try {
                long offset = consumer.fetchConsumeOffset(messageQueue, false);
                if (offset < 0) {
                    offset = 0;
                }
                PullResult result;
                if (StringUtils.isNotBlank(endpoint.getTags())) {
                    result = consumer.pull(messageQueue, endpoint.getTags(), offset, 1);
                } else {
                    result = consumer.pull(messageQueue, "*", offset, 1);
                }
                if (result.getPullStatus() != PullStatus.FOUND) {
                    return;
                }

                List<MessageExt> messages = result.getMsgFoundList();
                for (MessageExt message: messages) {
                    Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                    processor.process(exchange);
                }
                

            } catch (Exception e) {

            }
        });


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
