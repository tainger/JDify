package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.common.message.MessageQueue;

public class RocketMQManualCommit {

    private MQPullConsumer consumer;

    private String topic;

    private MessageQueue messageQueue;

    private long offset;

    public MQPullConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MQPullConsumer consumer) {
        this.consumer = consumer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
