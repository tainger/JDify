package io.terminus.dalaran.component.trigger.rocketmq.commit;

import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/5/20
 */
@Processor(
        value = "rocket-commit",
        name = "RocketMQ 消费确认",
        order = 19
)
public class DalaranRocketMQCommit implements DalaranProcessor {

    @Override
    public void configure(ProcessorDefinition route, Object config) {
        RocketMQCommitProcessor processor = new RocketMQCommitProcessor();
        route.process(processor);
    }
}
