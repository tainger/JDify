package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.component.message.convert.custom.message.impl.DefaultMessageConvert;
import io.terminus.dalaran.component.message.convert.custom.message.MessageConvert;
import io.terminus.dalaran.component.message.convert.custom.model.ConvertType;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Created by jingdi on 2019/3/18
 */
@DalaranComponent(value = "custom-convert", configType = CustomMapperConfig.class)
public class CustomMessageMapper implements DalaranProcessor<CustomMapperConfig> {

    @Override
    public void configure(ProcessorDefinition route, CustomMapperConfig config) {
        CustomMapperProcessor processor = new CustomMapperProcessor();
        route.setHeader("MessageMapping", Builder.constant(config.getMessageMapping()));
        route.setHeader("target", Builder.constant(config.getTarget()));
        route.setHeader("destination", Builder.constant(config.getDestination()));
        MessageConvert messageConvert = new DefaultMessageConvert();
        messageConvert.convert(route, config.getTargetType(), ConvertType.TARGET);
        route.process(processor);
        messageConvert.convert(route, config.getDestinationType(), ConvertType.DESTINATION);
    }
}
