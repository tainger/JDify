package io.terminus.dalaran.component.trigger;

import io.terminus.dalaran.component.BasicTriggerTest;
import io.terminus.dalaran.component.connector.RocketMQConnector;
import io.terminus.dalaran.component.trigger.rocketmq.RocketMQConsumer;
import io.terminus.dalaran.component.trigger.rocketmq.RocketMQConsumerConfig;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jingdi on 2019/6/26
 */
public class RocketMQTest extends BasicTriggerTest {

    private static final String NAME_SERVER = "127.0.0.1:9876";

    private static final String TOPIC = "dalaran";

    private  DefaultMQProducer producer = new DefaultMQProducer("dalaran-producer");

    @Test
    public void testRocketMQConsumer() {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10);
        for (int i = 0; i < 10; i++) {
            try {
                blockingQueue.put("rocket message " + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String message;
        try {
            while ((message = blockingQueue.take()) != null) {
                Message msg = new Message();
                msg.setTopic(TOPIC);
                msg.setBody(message.getBytes());
                try {
                    producer.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() {
        RocketMQConsumer consumer = new RocketMQConsumer();

        RocketMQConsumerConfig consumerConfig = new RocketMQConsumerConfig();
        RocketMQConnector connector = new RocketMQConnector();
        connector.setNameServer(NAME_SERVER);
        consumerConfig.setConnector(connector);
        consumerConfig.setTopic(TOPIC);
        consumerConfig.setConsumerGroup("dalaran");

        registerTrigger(consumer, consumerConfig);

        producer.setVipChannelEnabled(false);
        producer.setNamesrvAddr(NAME_SERVER);
        try {
            producer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object process(Object param) throws Exception {
        return "process rocket mq message " + param;
    }
}
