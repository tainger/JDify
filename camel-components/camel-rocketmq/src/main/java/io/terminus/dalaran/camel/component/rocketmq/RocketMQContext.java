package io.terminus.dalaran.camel.component.rocketmq;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQContext {

    private Map<RocketMQProducerId, RocketMQConfiguration> producerIdRocketMQConfigurations = new HashMap<>();

    private Map<RocketMQConsumerId, RocketMQConfiguration> consumerIdRocketMQConfigurations = new HashMap<>();



}
