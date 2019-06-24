package io.terminus.dalaran.component.processor.kafka;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.model.BodyType;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/16
 */
@Processor(value = "kafka-producer", configType = DalaranKafkaProducerConfig.class,
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Object,
        allowBodyTypes = {BodyType.JSON, BodyType.XML})
public class DalaranKafkaProducer implements DalaranProcessor<DalaranKafkaProducerConfig> {

    // TODO camel会将数组对象拆分，依次发到kafka，如果后续有mapper操作会导致类型转换出问题
    @Override
    public void configure(ProcessorDefinition route, DalaranKafkaProducerConfig config) {
        String uri = "kafka:" + config.getTopic()
                + "?brokers=" + config.getConnector().getBrokers();
        route.to(uri);
    }
}