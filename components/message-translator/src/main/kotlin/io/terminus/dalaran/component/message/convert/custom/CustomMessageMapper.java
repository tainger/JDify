package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.message.MessageMapping;
import org.apache.camel.model.ProcessorDefinition;

import java.util.function.Supplier;

/**
 * Created by jingdi on 2019/3/18
 */
@DalaranComponent(value = "custom-convert", configType = CustomMapperConfig.class)
public class CustomMessageMapper implements DalaranProcessor<CustomMapperConfig> {

    @Override
    public void configure(ProcessorDefinition route, CustomMapperConfig config) {
        CustomMapperProcessor processor = new CustomMapperProcessor();
        Supplier<MessageMapping> supplier = config::getMessageMapping;
        route.setHeader("MessageMapping", supplier);
        route.process(processor);
    }
}
