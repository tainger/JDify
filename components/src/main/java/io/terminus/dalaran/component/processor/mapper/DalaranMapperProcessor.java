package io.terminus.dalaran.component.processor.mapper;

import io.terminus.dalaran.component.processor.mapper.jsonPath.Converter;
import io.terminus.dalaran.component.processor.mapper.model.DalaranMappingConfig;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor, Traceable {

    private final DalaranMappingConfig mappingConfig;

    public DalaranMapperProcessor(DalaranMappingConfig mappingConfig) {
        this.mappingConfig = mappingConfig;
    }

    @Override
    public void process(Exchange exchange) {
        Object source = exchange.getIn().getBody();
        Object destination = convert(mappingConfig, source);
        exchange.getOut().setBody(destination);
    }

    public Object convert(DalaranMappingConfig mappingConfig, Object source) {
        Map<String, Object> destination = Converter.convert(mappingConfig, source);
        return destination.get(MapperConstants.MODEL_ROOT);
    }

    @Override
    public String getTraceLabel() {
        return "DalaranMapper";
    }
}
