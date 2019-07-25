package io.terminus.dalaran.component.trigger.kafka.commit;

import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Processor(
        value = "kafka-commit",
        name = "Kafka 消费确认",
        order = 13,
        inputSerializeType = BodySerializeType.Serialized,
        outputSerializeType = BodySerializeType.Serialized
)
public class DalaranKafkaCommit implements DalaranProcessor {

    @Override
    public void configure(ProcessorDefinition route, Object config) {
        KafkaCommitProcessor processor = new KafkaCommitProcessor();
        route.process(processor);
    }
}
