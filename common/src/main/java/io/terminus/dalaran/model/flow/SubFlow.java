package io.terminus.dalaran.model.flow;

import io.terminus.dalaran.DalaranConstants;
import lombok.Data;

@Data
public class SubFlow extends BasicFlow {

    @Override
    public String getRouteId() {
        return DalaranConstants.SUB_FLOW_PREFIX + this.getId();
    }
}
