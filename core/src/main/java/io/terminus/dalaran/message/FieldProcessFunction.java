package io.terminus.dalaran.message;

import java.util.List;

/**
 * Created by jingdi on 2019/3/12
 */
public class FieldProcessFunction {

    private List<String> fieldsIn;

    private List<String> fieldsOut;

    private FunctionExecution execution; //function执行逻辑

    public FieldProcessFunction(List<String> fieldsIn, List<String> fieldsOut, FunctionExecution execution) {
        this.fieldsIn = fieldsIn;
        this.fieldsOut = fieldsOut;
        this.execution = execution;
    }

    public List<String> getFieldsIn() {
        return fieldsIn;
    }

    public void setFieldsIn(List<String> fieldsIn) {
        this.fieldsIn = fieldsIn;
    }

    public List<String> getFieldsOut() {
        return fieldsOut;
    }

    public void setFieldsOut(List<String> fieldsOut) {
        this.fieldsOut = fieldsOut;
    }

    public FunctionExecution getExecution() {
        return execution;
    }

    public void setExecution(FunctionExecution execution) {
        this.execution = execution;
    }
}
