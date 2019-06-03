package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.model.ServiceOperation;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class SoapOperationConfig extends ServiceOperation {

    private String name;

    private String portType;

    private String binding;

    private String input;

    private String outPut;

    private String wsdl;
}
