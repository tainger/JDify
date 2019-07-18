package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import io.terminus.dalaran.component.processor.mapper.jsonPath.Converter;
import io.terminus.dalaran.component.processor.mapper.model.DalaranMappingConfig;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMappingField;
import io.terminus.dalaran.core.model.FieldType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Traceable;

import java.util.ArrayList;
import java.util.HashMap;

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
        SimpleMappingField destination = mappingConfig.getDestinationRoot();
        Object destinationBody;
        if (destination.getType() == FieldType.ARRAY) {
            destinationBody = new ArrayList<>();
        } else {
            destinationBody = new HashMap<>();
        }

        Converter converter = new Converter();
        converter.convert(mappingConfig, source, destinationBody);
        return destinationBody;
    }

    private Object parse(Object target, FieldType destination) {
        String input = target.toString();
        TypeParser parser = TypeParser.newBuilder().build();
        if (destination != null) {
            switch (destination) {
                case INTEGER:
                    return parser.parse(input, Integer.class);
                case LONG:
                    return parser.parse(input, Long.class);
                case SHORT:
                    return parser.parse(input, Short.class);
                case FLOAT:
                    return parser.parse(input, Float.class);
                case DOUBLE:
                    return parser.parse(input, Double.class);
                case NUMBER:
                    return parser.parse(input, Number.class);
                case BOOLEAN:
                    return parser.parse(input, Boolean.class);
                default:
                    return target;
            }
        }
        return target;
    }

    @Override
    public String getTraceLabel() {
        return "DalaranMapper";
    }
}
