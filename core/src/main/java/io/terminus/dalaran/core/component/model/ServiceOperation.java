package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;

@Data
public class ServiceOperation {

    private String operationKey;

    private MessageModel inModel;

    private MessageModel outModel;
}
