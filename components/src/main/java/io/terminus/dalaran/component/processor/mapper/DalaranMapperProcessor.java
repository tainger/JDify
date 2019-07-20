package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.component.processor.mapper.jsonPath.Converter;
import io.terminus.dalaran.component.processor.mapper.model.DalaranMappingConfig;
import io.terminus.dalaran.component.processor.mapper.model.MapperConstants;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMappingField;
import io.terminus.dalaran.core.model.FieldType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;

import java.util.ArrayList;
import java.util.HashMap;
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
        Object targetBody = exchange.getIn().getBody();
        Object destinationBody = convert(mappingConfig, targetBody);
        exchange.getOut().setBody(destinationBody);
    }

    public Object convert(DalaranMappingConfig mappingConfig, Object source) {
        Map<String, Object> destination = new HashMap<>();
        Converter converter = new Converter();
        converter.convert(mappingConfig, source, destination);
        return destination.get(MapperConstants.MODEL_ROOT);
    }

    @Override
    public String getTraceLabel() {
        return "DalaranMapper";
    }
}
