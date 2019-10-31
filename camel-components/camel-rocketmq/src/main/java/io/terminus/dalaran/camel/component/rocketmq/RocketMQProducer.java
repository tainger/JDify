package io.terminus.dalaran.camel.component.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

import java.util.ArrayList;
import java.util.List;

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
        List<Message> messages = buildMessage(exchange, endpoint.getMessageSharding());
        for (Message message : messages) {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    exchange.getOut().setBody(JSON.toJSON(sendResult));
                }

                @Override
                public void onException(Throwable e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private List<Message> buildMessage(Exchange exchange, Boolean messageSharding) {
        List<Message> messages = new ArrayList<>();
        Object body = exchange.getIn().getBody();
        if (body == null) {
            return messages;
        }
        Object json = JSON.toJSON(body);
        if (messageSharding && json instanceof JSONArray) {
            List<Object> msgs = (List) json;
            msgs.forEach(msg -> {
                messages.add(build(msg));
            });
        } else {
            messages.add(build(body));
        }
        return messages;
    }

    private Message build(Object body) {
        Message message = new Message();
        message.setTopic(endpoint.getTopic());
        String tags = endpoint.getTags();
        if (StringUtils.isNotBlank(tags)) {
            message.setTags(tags);
        }
        if (body != null) {
            if (body instanceof byte[]) {
                message.setBody((byte[]) body);
            } else if (body instanceof String) {
                message.setBody(((String) body).getBytes());
            } else if (body instanceof JSON) {
                message.setBody(body.toString().getBytes());
            } else if (body instanceof Object) {
                message.setBody(JSON.toJSONString(body).getBytes());
            } else {
                throw new RuntimeException("no support body type;");
            }
        }
        return message;
    }
}
