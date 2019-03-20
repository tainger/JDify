package io.terminus.dalaran.message;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/12
 */
@Data
public class FieldMapping {

    private Map<List<String>, List<String>> mapping;

    private FieldProcessFunction function;

}
