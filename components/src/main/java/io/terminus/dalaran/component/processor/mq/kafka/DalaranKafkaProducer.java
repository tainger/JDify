package io.terminus.dalaran.component.processor.mq.kafka;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/16
 */
@Processor(value = "kafka-producer", configType = DalaranKafkaProducerConfig.class, serializedBody = true, allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranKafkaProducer implements DalaranProcessor<DalaranKafkaProducerConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranKafkaProducerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getBrokers();
        route.to(uri);
    }
}