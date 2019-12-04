package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.rocketmq.client.producer.SendResult;

public class DalaranSendResult {

    private SendResult sendResult;

    private Integer total;

    public DalaranSendResult(SendResult sendResult, Integer total) {
        this.sendResult = sendResult;
        this.total = total;
    }

    public SendResult getSendResult() {
        return sendResult;
    }

    public void setSendResult(SendResult sendResult) {
        this.sendResult = sendResult;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
