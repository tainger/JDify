package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/3/18
 */
@Processor(value = "mapper-convert", serializedBody = false, configType = DalaranMapperConfig.class)
public class DalaranMessageMapper implements DalaranProcessor<DalaranMapperConfig> {

    @Override
    public void configure(ProcessorDefinition route, DalaranMapperConfig config) {
        DalaranMapperProcessor processor = new DalaranMapperProcessor();
        route.setHeader(MapperConstants.MAPPER_CONFIG, Builder.constant(config));
        route.process(processor);
    }
}
