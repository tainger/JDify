package io.terminus.dalaran.core.model.converter.soap.model;

import io.terminus.dalaran.core.component.model.ServiceOperation;
import io.terminus.dalaran.core.model.HttpProtocol;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class SoapOperationConfig extends ServiceOperation {

    private String operation;

    private String portType;

    private String baseUrl;

    private HttpProtocol protocol;

    private String binding;
}
