package io.terminus.dalaran.camel.component.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQProducer extends DefaultProducer {

    private DefaultMQProducer producer;

    private RocketMQEndpoint endpoint;

    private final Logger logger = LoggerFactory.getLogger(RocketMQProducer.class);

    private final Integer MESSAGE_SHARDING_LIMIT = 100;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
            1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    private final String JSON_PATH_HEADER = "$.";

    public RocketMQProducer(RocketMQEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
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
        producer.setInstanceName(endpoint.getNameServer());
        producer.setNamesrvAddr(endpoint.getNameServer());
        producer.setSendMsgTimeout(endpoint.getTimeout());
        producer.setMaxMessageSize(41943040);
        producer.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (producer != null) {
            producer.shutdown();
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        List<Message> messages = buildMessage(exchange, endpoint.getMessageSharding());
        Boolean async = endpoint.getAsync();
        if (async) {
            for (Message message : messages) {
                producer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        exchange.getOut().setBody(JSON.toJSONString(new DalaranSendResult(sendResult, messages.size())));
                    }

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            int size = messages.size();
            if (size < MESSAGE_SHARDING_LIMIT) {
               send(exchange, messages);
            } else {
                executor.execute(() -> {
                    send(exchange, messages);
                });
            }
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
        String topic = parseExpression(endpoint.getTopic(), body);
        message.setTopic(topic);
        String tags = endpoint.getTags();
        if (StringUtils.isNotBlank(tags)) {
            tags = parseExpression(tags, body);
            message.setTags(tags);
        }
        if (body != null) {
            if (body instanceof byte[]) {
                message.setBody((byte[]) body);
            } else if (body instanceof String) {
                message.setBody(((String) body).getBytes());
            } else if (body instanceof JSON) {
                message.setBody(body.toString().getBytes());
            } else {
                throw new RuntimeException("no support body type;");
            }
        }
        return message;
    }

    private String parseExpression(String origin, Object body) {
        if (!StringUtils.startsWith(origin, JSON_PATH_HEADER)) {
            return origin;
        }
        if (body instanceof byte[]) {
            try {
                body = JSON.parseObject(IOUtils.toString((byte[])body));
            } catch (Exception e) {
                throw new RuntimeException("body parse error;");
            }
        }
        if (body instanceof String) {
            body = JSON.parseObject((String) body);
        }
        Object data = JSONPath.eval(body, origin);
        if (data == null) {
            return origin;
        }
        return data.toString();
    }

    private void send(Exchange exchange, List<Message> messages) {
        SendResult sendResult = new SendResult();
        for (Message message: messages) {
            try {
                sendResult = producer.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        exchange.getOut().setBody(JSON.toJSONString(new DalaranSendResult(sendResult, messages.size())));
    }
}
