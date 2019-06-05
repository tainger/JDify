package io.terminus.dalaran.component.processor.mq.kafka;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.model.BodyType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/16
 */
@Processor(value = "kafka-producer", configType = DalaranKafkaProducerConfig.class,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized,
        allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranKafkaProducer implements DalaranProcessor<DalaranKafkaProducerConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranKafkaProducerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getBrokers();
        route.to(uri);
    }
}