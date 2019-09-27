package io.terminus.dalaran.core.converter.soap.model;

import io.terminus.dalaran.model.HttpProtocol;
import io.terminus.dalaran.model.component.ServiceOperation;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class SoapOperationConfig extends ServiceOperation {

    private String operation;

    private String portType;

    private String location;

    private HttpProtocol protocol;

    private String binding;

    private String servicePort;

    private String targetNamespace;
}
