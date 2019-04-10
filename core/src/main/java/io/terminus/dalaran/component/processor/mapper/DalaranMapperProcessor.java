package io.terminus.dalaran.component.processor.mapper;

import com.github.drapostolos.typeparser.TypeParser;
import com.google.gson.Gson;
import io.terminus.dalaran.component.processor.mapper.jxpath.DalaranJXPathFactory;
import io.terminus.dalaran.component.processor.mapper.model.FieldType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.jxpath.JXPathContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class DalaranMapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Gson gson = new Gson();
        Map<String, String> messageMapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader("MessageMapping")), Map.class);
        Map<String, String> destination = gson.fromJson(gson.toJson(exchange.getIn().getHeader("destination")), Map.class);
        Map<String, Object> targetBody = (Map)(exchange.getIn().getBody());
        Map<String, Object> destinationBody = convertWithJXPath(messageMapping, destination, targetBody);
        exchange.getOut().setBody(destinationBody);
    }

    private Map<String, Object> convertWithJXPath(Map<String, String> messageMapping, Map<String, String> destination, Map<String, Object> targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);

        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());

        for (Map.Entry<String, String> entry: messageMapping.entrySet()) {
            Object target = targetContext.getValue(entry.getValue());
            destinationContext.createPathAndSetValue(entry.getKey(),
                    parse(target, FieldType.valueOf(destination.get(entry.getKey()))));
        }
        return ((Map<String, Object>)destinationContext.getContextBean());
    }

    private Object parse(Object target, FieldType destination) {
        String input = target.toString();
        TypeParser parser = TypeParser.newBuilder().build();
        switch (destination) {
            case INT:
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
}
