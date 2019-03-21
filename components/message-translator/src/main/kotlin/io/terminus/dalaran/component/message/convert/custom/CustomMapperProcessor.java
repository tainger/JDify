package io.terminus.dalaran.component.message.convert.custom;

import com.google.gson.Gson;
import io.terminus.dalaran.component.message.convert.jxpath.DalaranJXPathFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.jxpath.JXPathContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class CustomMapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Gson gson = new Gson();
        Map<String, String> messageMapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader("MessageMapping")), Map.class);
        List<String> target = gson.fromJson(gson.toJson(exchange.getIn().getHeader("target")), List.class);
        List<String> destination = gson.fromJson(gson.toJson(exchange.getIn().getHeader("destination")), List.class);

//        Map<String, Object> targetMessage = exchange.getIn().getBody(HashedMap.class);

        Map<String, Object> targetBody = gson.fromJson((String) exchange.getIn().getBody(), Map.class);

//        Map<String, Object> destinationBody = buildDestinationBody(messageMapping, target, destination, targetBody);
        Map<String, Object> destinationBody = convertWithJXPath(messageMapping, destination, targetBody);
        exchange.getOut().setBody(destinationBody);
    }

//    private Map<String, Object> buildDestinationBody(List<SingleFieldMapping> mappings, Map<String, Object> targetBody, DalaranMessage targetMsg, DalaranMessage destinationMsg) {
//        Map<String, Object> destinationBody = new HashMap<>();
//        for (String destinationField : destinationMsg.getFields().keySet()) {
//            for (SingleFieldMapping mapping : mappings) {
//                Map<String, String> fieldsMapping = mapping.getMapping();
//                if (fieldsMapping.containsKey(destinationField)) {
//                    String targetField = fieldsMapping.get(destinationField);
//                    Object fieldVal = targetBody.get(targetField);
//                    destinationBody.put(destinationField, fieldVal);
//                }
//            }
//        }
//        return destinationBody;
//    }

    private Map<String, Object> buildDestinationBody(Map<String, String> messageMapping, List<String> target, List<String> destination, Map<String, Object> targetBody) {
        Map<String, Object> destinationBody = new HashMap<>();
        for (String destinationField : destination) {
            if (messageMapping.containsKey(destinationField)) {
                String targetField = messageMapping.get(destinationField);
                Object fieldVal = targetBody.get(targetField);
                destinationBody.put(destinationField, fieldVal);
            }
        }
        return destinationBody;
    }

    private Map<String, Object> convertWithJXPath(Map<String, String> messageMapping, List<String> destination, Map<String, Object> targetBody) {
        JXPathContext targetContext = JXPathContext.newContext(targetBody);

        Map<String, Object> destinationBody = new HashMap<>();
        JXPathContext destinationContext = JXPathContext.newContext(destinationBody);
        destinationContext.setFactory(new DalaranJXPathFactory());

        for (Map.Entry<String, String> entry: messageMapping.entrySet()) {
            Object ob = targetContext.getValue(entry.getValue());
            destinationContext.createPathAndSetValue(entry.getKey(), targetContext.getValue(entry.getValue()));
        }

        return ((Map<String, Object>)destinationContext.getContextBean());
    }

//    private Map<String, Object> convertTargetBody(Message message) {
//
//    }
}
