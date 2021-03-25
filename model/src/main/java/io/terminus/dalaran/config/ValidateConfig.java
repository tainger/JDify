package io.terminus.dalaran.config;

import lombok.Data;

@Data
public class ValidateConfig {

    private Integer maxLength;

    private Boolean onlyNumber;

    private Integer minNumber;

    private Integer maxNumber;
}
