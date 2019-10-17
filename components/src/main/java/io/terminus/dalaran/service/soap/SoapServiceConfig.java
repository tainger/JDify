package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.model.soap.model.SoapOperationConfig;
import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/5/27
 */
@Data
public class SoapServiceConfig {

    private String wsdl;

    private List<SoapOperationConfig> configs;
}
