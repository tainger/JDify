package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/10
 */
@Data
public class SoapSchemaOperation {

    private String targetNamespace;

    private MessageModel header;

    private Map<String, Object> headerValues;
}
