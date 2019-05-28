package io.terminus.dalaran.model.flow;

import lombok.Data;

import static io.terminus.dalaran.DalaranConstants.SUB_FLOW_PREFIX;

@Data
public class SubFlow extends BasicFlow {

    @Override
    public String getRouteId() {
        return SUB_FLOW_PREFIX + this.getId();
    }
}
