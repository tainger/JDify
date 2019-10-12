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
        if (endpoint.getUseAliCloudOns()) {
            service.getDefaultMQPullConsumer().setAccessChannel(AccessChannel.CLOUD);
        } else {
            service.getDefaultMQPullConsumer().setAccessChannel(AccessChannel.LOCAL);
        }

        service.registerPullTaskCallback("topic", (messageQueue, pullTaskContext) -> {
            MQPullConsumer consumer = pullTaskContext.getPullConsumer();
            try {
                long offset = consumer.fetchConsumeOffset(messageQueue, false);
                if (offset < 0) {
                    offset = 0;
                }
                PullResult result = null;
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
                    //todo process message
                    Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                    if (endpoint.) {

                    }
                    processor.process(exchange);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        service.start();


        if (StringUtils.isNotBlank(endpoint.getTags())) {
            service.getDefaultMQPullConsumer().subscribe(endpoint.getTopic(), endpoint.getTags());
        } else {
            consumer.subscribe(endpoint.getTopic(), "*");
        }
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            try {
                for (MessageExt message: messages) {
                    Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                    processor.process(exchange);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
