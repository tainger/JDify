package io.terminus.dalaran.service.soap;

import lombok.Data;
import java.util.List;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class SoapServiceConfig {

    private String wsdl;

    private List<SoapOperationConfig> soapOperations;
}
