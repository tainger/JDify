package io.terminus.dalaran.component.message.convert;

import io.terminus.dalaran.BodyMode;
import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;

@Processor(value = "message-mapper", bodyMode = BodyMode.Object, configType = MessageMapperConfig.class)
public class MessageMapper implements DalaranProcessor<MessageMapperConfig> {

    private String DOZER_URI = "dozer?targetModel=%s&mappingFile=%s";

    public void configure(ProcessorDefinition route, MessageMapperConfig config) {
        // TODO from model type
//        Class unmarshalType = Class.forName(config.targetType);
//        route.unmarshal().json(JsonLibrary.Gson, unmarshalType);
        String uri = String.format(DOZER_URI, config.getTargetModel(), config.getMappingFile());
        route.to(uri);

        // TODO target model type
//        route.marshal().json(JsonLibrary.Gson);
    }
}
