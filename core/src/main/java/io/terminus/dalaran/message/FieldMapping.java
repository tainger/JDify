package io.terminus.dalaran.message;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/12
 */
public class FieldMapping {

    private Map<List<String>, List<String>> mapping;

    private FieldProcessFunction function;

    public FieldMapping(Map<List<String>, List<String>> mapping, FieldProcessFunction function) {
        this.mapping = mapping;
        this.function = function;
    }

    public Map<List<String>, List<String>> getMapping() {
        return mapping;
    }

    public void setMapping(Map<List<String>, List<String>> mapping) {
        this.mapping = mapping;
    }

    public FieldProcessFunction getFunction() {
        return function;
    }

    public void setFunction(FieldProcessFunction function) {
        this.function = function;
    }
}
