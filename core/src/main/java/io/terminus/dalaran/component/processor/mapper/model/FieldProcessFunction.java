package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/3/12
 */
@Data
public class FieldProcessFunction {

    private List<String> fieldsIn;

    private List<String> fieldsOut;

    private FunctionExecution execution; //function执行逻辑

}
