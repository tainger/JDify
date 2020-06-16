package io.terminus.dalaran.mapper.function.context;

import io.terminus.dalaran.mapper.annotation.ContainsContextFunction;
import io.terminus.dalaran.mapper.annotation.HiddenParam;
import io.terminus.dalaran.mapper.annotation.MappingFunction;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

@ContainsContextFunction
@MappingFunction(value = "GetValueFromContext", description = "获取context中的数据")
public class GetValueFromContext {

    public Object execute(String key, @HiddenParam Map<String, Object> context) {
        if (MapUtils.isEmpty(context) || !context.containsKey(key)) {
            return null;
        }
        return context.get(key);
    }
}
