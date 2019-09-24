package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/6/10
 */
@Data
public class SoapSchemaOperation {

    private String name;

    private String portType;

    private String binding;

    private String input;

    private String outPut;

    private String wsdl;

    private String baseUrl;

    private String modelRoot;

    private HttpProtocol protocol;
}
