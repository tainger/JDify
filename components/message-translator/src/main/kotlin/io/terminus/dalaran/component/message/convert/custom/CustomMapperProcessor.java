package io.terminus.dalaran.component.message.convert.custom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.terminus.dalaran.message.DalaranMessage;
import io.terminus.dalaran.message.MessageMapping;
import io.terminus.dalaran.message.SingleFieldMapping;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.lang.reflect.Type;
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
        MessageMapping messageMapping = gson.fromJson(gson.toJson(exchange.getIn().getHeader("MessageMapping")), MessageMapping.class);


        List<SingleFieldMapping> mappings = messageMapping.getSingleFieldMappings();

//        Map<String, Object> targetMessage = exchange.getIn().getBody(HashedMap.class);

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> targetBody = gson.fromJson(gson.toJson(exchange.getIn().getBody()), type);

        DalaranMessage targetMsg = messageMapping.getTarget();
        DalaranMessage destinationMsg = messageMapping.getDestination();

        Map<String, Object> destinationBody = buildDestinationBody(mappings, targetBody, targetMsg, destinationMsg);
        exchange.getOut().setBody(destinationBody);
    }

    private Map<String, Object> buildDestinationBody(List<SingleFieldMapping> mappings, Map<String, Object> targetBody, DalaranMessage targetMsg, DalaranMessage destinationMsg) {
        Map<String, Object> destinationBody = new HashMap<>();
        for (String destinationField : destinationMsg.getFields().keySet()) {
            for (SingleFieldMapping mapping : mappings) {
                Map<String, String> fieldsMapping = mapping.getMapping();
                if (fieldsMapping.containsKey(destinationField)) {
                    String targetField = fieldsMapping.get(destinationField);
                    Object fieldVal = targetBody.get(targetField);
                    destinationBody.put(destinationField, fieldVal);
                }
            }
        }
        return destinationBody;
    }
}
