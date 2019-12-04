package io.terminus.dalaran.service.soap;

import io.terminus.dalaran.component.common.HttpMethod;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/10
 */
@Data
public class SoapOperation {

    private String name;

    private String portType;

    private String input;

    private String inputHeader;

    private String output;

    private String outputHeader;

    private String path;

    private HttpMethod method;
}
