package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.List;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQConsumer extends DefaultConsumer {

    private RocketMQEndpoint endpoint;

    private Processor processor;

    private RocketMQConfiguration configuration;

    private MQPullConsumerScheduleService service;

    private final String ROCKET_MQ_MANUAL_COMMIT = "ROCKET_MQ_MANUAL_COMMIT";

    public RocketMQConsumer(RocketMQEndpoint endpoint, Processor processor, RocketMQConfiguration configuration) {
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
        DefaultMQPullConsumer defaultMQPullConsumer = service.getDefaultMQPullConsumer();
        defaultMQPullConsumer.setNamesrvAddr(endpoint.getNameServer());
        if (endpoint.getUseAliCloudOns()) {
            defaultMQPullConsumer.setAccessChannel(AccessChannel.CLOUD);
        } else {
            defaultMQPullConsumer.setAccessChannel(AccessChannel.LOCAL);
        }
        service.registerPullTaskCallback(endpoint.getTopic(), (messageQueue, pullTaskContext) -> {
            MQPullConsumer consumer = pullTaskContext.getPullConsumer();
            try {
                long offset = consumer.fetchConsumeOffset(messageQueue, true);
                if (offset < 0) {
                    offset = 0;
                }
                PullResult result;
                // todo consumer目前配置写死，一次只拿一条消息，方便消费确认
                if (StringUtils.isNotBlank(endpoint.getTags())) {
                    result = consumer.pull(messageQueue, endpoint.getTags(), offset, 1);
                } else {
                    result = consumer.pull(messageQueue, "*", offset, 1);
                }
                if (result.getPullStatus() != PullStatus.FOUND) {
                    return;
                }

                List<MessageExt> messages = result.getMsgFoundList();
                if (messages == null || messages.size() == 0) {
                    return;
                }
                for (MessageExt message: messages) {
                    Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                    if (!endpoint.getAutoCommit()) {
                        RocketMQManualCommit commit = new RocketMQManualCommit(consumer, endpoint.getTopic(), messageQueue, result.getNextBeginOffset());
                        exchange.getIn().setHeader(ROCKET_MQ_MANUAL_COMMIT, commit);
                    } else {
                        consumer.updateConsumeOffset(messageQueue, result.getNextBeginOffset());
                    }
                    processor.process(exchange);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        service.start();
    }
}
