package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.rocketmq.common.message.MessageQueue;

public class RocketMQManualCommit {

    private String topic;

    private MessageQueue messageQueue;

    private long offset;

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
