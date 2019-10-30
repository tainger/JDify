package io.terminus.dalaran.component.trigger;

import io.terminus.dalaran.component.BasicTriggerTest;
import io.terminus.dalaran.component.connector.KafkaConnector;
import io.terminus.dalaran.component.trigger.kafka.DalaranKafkaConsumer;
import io.terminus.dalaran.component.trigger.kafka.DalaranKafkaConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jingdi on 2019/6/26
 */
public class KafkaTest extends BasicTriggerTest {

    private static final String BROKERS = "127.0.0.1:9092";

    private static final String TOPIC = "dalaran";


    @Test
    public void testKafkaConsumer() {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10);

        for (int i = 0; i < 10; i++) {
            try {
                blockingQueue.put("kafka test msg " + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", BROKERS);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer(properties);
        String message;
        try {
            while ((message = blockingQueue.take()) != null) {
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
                producer.send(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() {
        DalaranKafkaConsumer consumer = new DalaranKafkaConsumer();

        KafkaConnector connector = new KafkaConnector();
        connector.setBrokers(BROKERS);

        DalaranKafkaConsumerConfig consumerConfig = new DalaranKafkaConsumerConfig();
        consumerConfig.setConnector(connector);
        consumerConfig.setAutocommit(true);
        consumerConfig.setTopic(TOPIC);
        consumerConfig.setGroupId("dalaran");

        registerTrigger(consumer, consumerConfig);
    }

    @Override
    public Object process(Object param) throws Exception {
        return "kafka process " + param;
    }
}
