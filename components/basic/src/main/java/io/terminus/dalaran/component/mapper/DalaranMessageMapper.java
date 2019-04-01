package io.terminus.dalaran.component.mapper;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/3/18
 */
@Processor(value = "mapper-convert", configType = DalaranMapperConfig.class, bodyMode = BodyMode.Object)
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranMapperConfig config) {
        DalaranMapperProcessor processor = new DalaranMapperProcessor();
        route.setHeader("MessageMapping", Builder.constant(config.getMessageMapping()));
        route.setHeader("target", Builder.constant(config.getTarget()));
        route.setHeader("destination", Builder.constant(config.getDestination()));
        route.process(processor);
    }
}
