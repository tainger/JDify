package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.kafka.DalaranKafkaProducer;
import io.terminus.dalaran.component.processor.kafka.DalaranKafkaProducerConfig;
import io.terminus.dalaran.component.connector.KafkaConnector;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jingdi on 2019/6/25
 */
public class KafkaTest extends BasicProcessorTest {

    @Test
    public void testKafkaProcessor() {
        DalaranKafkaProducer producer = new DalaranKafkaProducer();

        KafkaConnector connector = new KafkaConnector();
        connector.setBrokers("127.0.0.1:9092");

        DalaranKafkaProducerConfig producerConfig = new DalaranKafkaProducerConfig();
        producerConfig.setConnector(connector);
        producerConfig.setTopic("dalaran");

        ProducerTemplate template = getProcessorTemplate(producer, producerConfig);
        Assert.assertNotNull(template);

        String message = "kafka message " + System.currentTimeMillis();
        Object result = template.requestBody(message);
        Assert.assertNotNull(result);
    }
}
