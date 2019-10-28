package io.terminus.dalaran.component.processor.kafka;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/16
 */
@Processor(
        value = "kafka-producer",
        name = "Kafka 发送器",
        order = 13,
        bodyType = "JSON",
        configType = DalaranKafkaProducerConfig.class
)
public class DalaranKafkaProducer implements DalaranProcessor<DalaranKafkaProducerConfig> {

    // TODO camel会将数组对象拆分，依次发到kafka，如果后续有mapper操作会导致类型转换出问题
    @Override
    public void configure(ProcessorDefinition route, DalaranKafkaProducerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getConnector().getBrokers();
        route.to(uri);
    }
}