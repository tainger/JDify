package io.terminus.dalaran.core.flow.model;

import io.terminus.dalaran.core.DalaranConstants;
import lombok.Data;

@Data
public class SubFlow extends BasicFlow {

    @Override
    public String getRouteId() {
        return DalaranConstants.SUB_FLOW_PREFIX + this.getId();
    }
}
