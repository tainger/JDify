package io.terminus.dalaran.config;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LimiterInfo {

    private String name;

    private final List<String> components = new ArrayList<>();

    private Integer order;

    private DalaranConfigField[] configFields;

    @JSONField(serialize = false)
    private Class classType;

    private String type;

    public void addComponent(String component) {
        components.add(component);
    }
}
