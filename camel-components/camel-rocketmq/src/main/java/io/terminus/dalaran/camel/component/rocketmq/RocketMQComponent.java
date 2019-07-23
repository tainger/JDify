package io.terminus.dalaran.camel.component.rocketmq;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/14
 */
public class RocketMQComponent extends DefaultComponent {

    private final RocketMQContext context = new RocketMQContext();

    @Override
    protected Endpoint createEndpoint(String s, String s1, Map<String, Object> map) throws Exception {
        return new RocketMQEndpoint(context);
    }
}
