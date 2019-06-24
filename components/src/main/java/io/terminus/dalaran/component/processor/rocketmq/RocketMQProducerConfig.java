package io.terminus.dalaran.component.processor.rocketmq;

import lombok.Data;

/**
 * Created by jingdi on 2019/6/19
 */
@Data
public class RocketMQProducerConfig {

    private String nameServer;

    private String topic;

    private String producerGroup;
}
