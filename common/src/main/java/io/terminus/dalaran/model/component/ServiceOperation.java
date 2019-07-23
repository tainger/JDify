package io.terminus.dalaran.model.component;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class ServiceOperation {

    private String operationKey;

    private MessageModel inModel;

    private MessageModel outModel;
}
