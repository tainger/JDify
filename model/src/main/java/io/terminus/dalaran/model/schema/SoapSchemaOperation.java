package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/10
 */
@Data
public class SoapSchemaOperation {

    private String targetNamespace = "http://schemas.xmlsoap.org/wsdl";

    private String prefix = "dalaran";

    private Boolean bodyContainsXmlns = false;

    private Boolean bodyContainsPrefix = false;

    private Boolean allContainsPrefix = false;

    private Boolean removeNullColumn = false;

    private Boolean buildBodyByField = false;

    private MessageModel header;

    private Map<String, Object> headerValues;
}
