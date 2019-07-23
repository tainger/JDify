package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.converter.soap.model.SoapSchemaOperation;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/6
 */
@Data
public class SoapSchema extends DalaranModelSchema {

    private SoapSchemaOperation operationConfig;

    private String wsdlDoc;
}
