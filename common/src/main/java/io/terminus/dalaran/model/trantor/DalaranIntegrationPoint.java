package io.terminus.dalaran.model.trantor;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

@Data
public class DalaranIntegrationPoint {

    private String key;

    private String name;

    private String description;

    private MessageModel returnType;

    private MessageModel paramType;

}
