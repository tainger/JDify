package io.terminus.dalaran.console.model;

import io.terminus.dalaran.market.model.BasicResourceDTO;
import lombok.Data;

@Data
public class FlowTemplate extends BasicResourceDTO {

    private TemplateData data;
}
