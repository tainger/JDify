package io.terminus.dalaran.service.soap;

import lombok.Data;

/**
 * Created by jingdi on 2019/7/10
 */
@Data
public class SoapOperation {

    private String name;

    private String portType;

    private String input;

    private String output;
}
