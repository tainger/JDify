package io.terminus.dalaran.message;

import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class SingleFieldMapping {

    private Map<String, String> mapping;

    private FieldProcessFunction function;
}
