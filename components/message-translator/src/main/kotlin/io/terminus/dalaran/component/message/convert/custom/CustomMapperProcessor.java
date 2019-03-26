package io.terminus.dalaran.component.message.convert.custom;

import com.github.drapostolos.typeparser.TypeParser;
import com.google.gson.Gson;
import io.terminus.dalaran.component.message.convert.custom.jxpath.DalaranJXPathFactory;
import io.terminus.dalaran.component.message.convert.custom.model.FieldType;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.jxpath.JXPathContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class CustomMapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Gson gson = new Gson();
        Map<String, String> messageMapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader("MessageMapping")), Map.class);
        Map<String, FieldType> target = gson.fromJson(gson.toJson(exchange.getIn().getHeader("target")), Map.class);
        Map<String, String> destination = gson.fromJson(gson.toJson(exchange.getIn().getHeader("destination")), Map.class);
//        Map<CustomMapperConfig.Field, CustomMapperConfig.Field> mapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader("mapping")), Map.class);
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
            Object origin = targetContext.getValue(entry.getValue());
            Object ob = parse(origin.toString(), FieldType.valueOf(destination.get(entry.getKey())));

            destinationContext.createPathAndSetValue(entry.getKey(),
                    parse(origin.toString(), FieldType.valueOf(destination.get(entry.getKey()))));
        }
        return ((Map<String, Object>)destinationContext.getContextBean());
    }

    private Object parse(String value, FieldType destination) {
        TypeParser parser = TypeParser.newBuilder().build();
        switch (destination) {
            case INT:
                return parser.parse(value, Integer.class);
            case LONG:
                return parser.parse(value, Long.class);
            case SHORT:
                return parser.parse(value, Short.class);
            case FLOAT:
                return parser.parse(value, Float.class);
            case DOUBLE:
                return parser.parse(value, Double.class);
            case NUMBER:
                return parser.parse(value, Number.class);
            case BOOLEAN:
                return parser.parse(value, Boolean.class);
            default:
                return value;
        }
    }
}
