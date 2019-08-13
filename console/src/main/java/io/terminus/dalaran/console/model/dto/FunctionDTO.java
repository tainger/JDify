package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.dto.basic.BasicFunctionInfo;
import lombok.Data;

@Data
public class FunctionDTO extends BasicFunctionInfo {
    private String script;
}
