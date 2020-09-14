package io.terminus.dalaran.camel.component.rocketmq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;

import java.util.List;
import java.util.Properties;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQConsumer extends DefaultConsumer {

    private RocketMQEndpoint endpoint;

    private Processor processor;

    private RocketMQConfiguration configuration;

    private MQPullConsumerScheduleService service;

    private Consumer consumer;

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
        if (service != null) {
            service.shutdown();
        }
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (endpoint.getUseAliCloudOns()) {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.GROUP_ID, endpoint.getGroupId());
            properties.put(PropertyKeyConst.AccessKey, endpoint.getAccessKey());
            properties.put(PropertyKeyConst.SecretKey, endpoint.getSecretKey());
            properties.put(PropertyKeyConst.NAMESRV_ADDR, endpoint.getNameServer());

            consumer = ONSFactory.createConsumer(properties);

            String tag;
            if (StringUtils.isBlank(endpoint.getTags())) {
                tag = "*";
            } else {
                tag = endpoint.getTags();
            }
            consumer.subscribe(endpoint.getTopic(), tag, (message, context) -> {
                Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                try {
                    processor.process(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Action.CommitMessage;
            });
            consumer.start();
        } else {
            RPCHook rpcHook = null;
            if (StringUtils.isNotBlank(endpoint.getAccessKey()) && StringUtils.isNotBlank(endpoint.getSecretKey())) {
                rpcHook = new AclClientRPCHook(new SessionCredentials(endpoint.getAccessKey(), endpoint.getSecretKey()));
            }

            service = new MQPullConsumerScheduleService(endpoint.getGroupId(), rpcHook);
            DefaultMQPullConsumer defaultMQPullConsumer = service.getDefaultMQPullConsumer();
            defaultMQPullConsumer.setNamesrvAddr(endpoint.getNameServer());
            defaultMQPullConsumer.setAccessChannel(AccessChannel.LOCAL);

//            if (endpoint.getUseAliCloudOns()) {
//                defaultMQPullConsumer.setAccessChannel(AccessChannel.CLOUD);
//            } else {
//            }
            service.registerPullTaskCallback(endpoint.getTopic(), (messageQueue, pullTaskContext) -> {
                MQPullConsumer consumer = pullTaskContext.getPullConsumer();
                try {
                    long offset = consumer.fetchConsumeOffset(messageQueue, false);
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

                    switch (result.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> messages = result.getMsgFoundList();
                            if (messages == null || messages.size() == 0) {
                                return;
                            }
                            for (MessageExt message : messages) {
                                Exchange exchange = endpoint.createRocketMQExchange(message.getBody());
                                if (!endpoint.getAutocommit()) {
                                    RocketMQManualCommit commit = new RocketMQManualCommit(consumer, endpoint.getTopic(), messageQueue, result.getNextBeginOffset());
                                    exchange.getIn().setHeader(ROCKET_MQ_MANUAL_COMMIT, commit);
                                } else {
                                    consumer.updateConsumeOffset(messageQueue, result.getNextBeginOffset());
                                }
                                processor.process(exchange);
                            }
                            break;
                        case NO_MATCHED_MSG:
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                        default:
                            consumer.updateConsumeOffset(messageQueue, result.getNextBeginOffset());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            service.start();
        }
    }
}
