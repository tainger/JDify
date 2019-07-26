package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/24
 */
@Data
public class ServiceOperationModel {

    private MessageModel inModel;

    private String inputName;

    private MessageModel outModel;

    private String outputName;

    public ServiceOperationModel(MessageModel inModel, String inputName, MessageModel outModel, String outputName) {
        this.inModel = inModel;
        this.inputName = inputName;
        this.outModel = outModel;
        this.outputName = outputName;
    }
}
