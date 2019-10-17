package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQProducer extends DefaultProducer {

    private DefaultMQProducer producer;

    private RocketMQEndpoint endpoint;

    public RocketMQProducer(RocketMQEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (producer == null) {
            RPCHook rpcHook = null;
            if (StringUtils.isNotBlank(endpoint.getAccessKey()) && StringUtils.isNotBlank(endpoint.getSecretKey())) {
                rpcHook = new AclClientRPCHook(new SessionCredentials(endpoint.getAccessKey(), endpoint.getSecretKey()));
            }
            producer = new DefaultMQProducer(endpoint.getGroupId(), rpcHook);
            if (endpoint.getUseAliCloudOns()) {
                producer.setAccessChannel(AccessChannel.CLOUD);
            } else {
                producer.setAccessChannel(AccessChannel.LOCAL);
            }
            producer.setNamesrvAddr(endpoint.getNameServer());
            producer.start();
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        producer.shutdown();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = buildMessage(exchange);
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                exchange.getOut().setBody(sendResult);
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private Message buildMessage(Exchange exchange) {
        Message msg = new Message();
        org.apache.camel.Message camelMsg = exchange.getIn();
        msg.setTopic(endpoint.getTopic());
        String tags = endpoint.getTags();
        if (StringUtils.isNotBlank(tags)) {
            msg.setTags(tags);
        }
        Object body = camelMsg.getBody();
        if (body != null) {
            msg.setBody(body.toString().getBytes());
        }
        return msg;
    }
}
