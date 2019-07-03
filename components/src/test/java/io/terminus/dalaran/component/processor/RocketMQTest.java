package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.rocketmq.RocketMQProducer;
import io.terminus.dalaran.component.processor.rocketmq.RocketMQProducerConfig;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jingdi on 2019/6/25
 */
public class RocketMQTest extends BasicProcessorTest {

    @Test
    public void testRocketMQ() {
        RocketMQProducer producer = new RocketMQProducer();

        RocketMQProducerConfig producerConfig = new RocketMQProducerConfig();
        producerConfig.setTopic("dalaran");
//        producerConfig.setNameServer("127.0.0.1:9876");
        producerConfig.setProducerGroup("dalaran");

        ProducerTemplate template = getProcessorTemplate(producer, producerConfig);
        Assert.assertNotNull(template);

        String message = "rocketmq message " + System.currentTimeMillis();
        Object result = template.requestBody(message);
        Assert.assertNotNull(result);
    }
}
