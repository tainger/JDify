package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;
import org.apache.camel.Processor;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class DalaranRoute extends RouteDefinition {

    private boolean serializedBody;

    private MessageModel lastOutModel;

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        for (FromDefinition input : getInputs()) {
            steps.add(input.toString());
        }
        for (ProcessorDefinition<?> output : this.getOutputs()) {
            if (output instanceof ProcessDefinition) {
                try {
                    Field processorField = ProcessDefinition.class.getDeclaredField("processor");
                    processorField.setAccessible(true);
                    Processor processor = (Processor) ReflectionUtils.getField(processorField, output);
                    steps.add(processor.toString());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else {
                steps.add(output.toString());
            }
        }
        return steps;
    }
}
