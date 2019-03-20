package io.terminus.dalaran.component.message.convert.custom;

import com.google.gson.Gson;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.DalaranComponent;
import io.terminus.dalaran.message.MessageMapping;
import io.terminus.dalaran.message.ModelType;
import org.apache.camel.Expression;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;

import java.util.Map;
import java.util.function.Supplier;

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
        convertIn(route, config.getTargetType());
        route.process(processor);
        route.marshal().json(JsonLibrary.Gson);
    }

    private void convertIn(ProcessorDefinition route, ModelType type) {
        switch (type) {
            case JSON:
                route.convertBodyTo(String.class);
                break;
            case XML:
                XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
                xmlJsonFormat.setForceTopLevelObject(true);
                route.marshal(xmlJsonFormat).convertBodyTo(String.class);
                break;
        }
    }

    private void convertOut(ProcessorDefinition route, ModelType type) {

    }
}
