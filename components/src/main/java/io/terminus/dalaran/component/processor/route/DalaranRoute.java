package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.model.ProcessorModel;
import lombok.Data;

import java.util.List;

@Data
public class DalaranRoute {

    private String displayName;

    private String expression;

    private List<ProcessorModel> pipeline;
}
