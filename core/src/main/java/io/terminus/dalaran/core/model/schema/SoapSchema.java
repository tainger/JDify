package io.terminus.dalaran.core.model.schema;

import io.terminus.dalaran.core.model.DalaranModelSchema;
import io.terminus.dalaran.core.model.ModelField;
import io.terminus.dalaran.core.model.converter.soap.model.SoapSchemaOperation;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/6
 */
@Data
public class SoapSchema implements DalaranModelSchema {

    private Map<String, ModelField> fields;

    private SoapSchemaOperation operationConfig;

    private String wsdlDoc;
}
