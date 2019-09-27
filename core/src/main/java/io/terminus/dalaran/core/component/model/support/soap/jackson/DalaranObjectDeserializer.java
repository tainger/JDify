package io.terminus.dalaran.core.component.model.support.soap.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/4
 */
public class DalaranObjectDeserializer extends UntypedObjectDeserializer {

    @Override
    protected Map<String,Object> mapObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException
    {
        JsonToken token = jsonParser.getCurrentToken();
        if (token == JsonToken.START_OBJECT) {
            token = jsonParser.nextToken();
        }
        if (token == JsonToken.END_OBJECT) {
            return new LinkedHashMap<String,Object>(2);
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        do {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            result.put(fieldName, handleMultipleValue(result, fieldName, deserialize(jsonParser, deserializationContext)));
        } while (jsonParser.nextToken() != JsonToken.END_OBJECT);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object handleMultipleValue(Map<String, Object> map, String key, Object value) {
        if (!map.containsKey(key)) {
            return value;
        }

        Object originalValue = map.get(key);
        if (originalValue instanceof List) {
            ((List) originalValue).add(value);
            return originalValue;
        }
        else {
            ArrayList newValue = new ArrayList();
            newValue.add(originalValue);
            newValue.add(value);
            return newValue;
        }
    }
}
