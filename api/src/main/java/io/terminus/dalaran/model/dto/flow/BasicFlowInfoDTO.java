package io.terminus.dalaran.model.dto.flow;

import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import lombok.Data;

@Data
public class BasicFlowInfoDTO extends BasicFlowInfo {

    private String moduleName;

}
