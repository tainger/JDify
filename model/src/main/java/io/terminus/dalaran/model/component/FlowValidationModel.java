package io.terminus.dalaran.model.component;

import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.flow.FlowValidation;
import lombok.Data;

import java.util.List;

@Data
public class FlowValidationModel {

    private List<FlowValidation> flowValidation;

    private MessageModel lastModel;
}
