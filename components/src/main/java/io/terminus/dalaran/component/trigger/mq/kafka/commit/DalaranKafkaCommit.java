package io.terminus.dalaran.component.trigger.mq.kafka.commit;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Processor(value = "kafka-commit", serializedBody = false)
public class DalaranKafkaCommit implements DalaranProcessor {

    @Override
    public void configure(ProcessorDefinition route, Object config) {
        KafkaCommitProcessor processor = new KafkaCommitProcessor();
        route.process(processor);
    }
}
