package io.terminus.dalaran.model.converter.soap.model;

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

    private String location;

    private String modelRoot;

    private HttpProtocol protocol;

    private String targetNamespace;

    private String soapAction;
}
