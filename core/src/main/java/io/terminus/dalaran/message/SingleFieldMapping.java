package io.terminus.dalaran.message;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
public class SingleFieldMapping {

    private Map<String, String> mapping;

    private FieldProcessFunction function;

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public FieldProcessFunction getFunction() {
        return function;
    }

    public void setFunction(FieldProcessFunction function) {
        this.function = function;
    }
}
