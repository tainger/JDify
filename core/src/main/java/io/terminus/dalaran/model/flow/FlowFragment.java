package io.terminus.dalaran.model.flow;

import lombok.Data;

import static io.terminus.dalaran.DalaranConstants.DELIMITER;
import static io.terminus.dalaran.DalaranConstants.FLOW_FRAGMENT_PREFIX;

@Data
public class FlowFragment extends BasicFlow {

    private String fragmentProcessorId;

    @Override
    public String getRouteId() {
        return FLOW_FRAGMENT_PREFIX + this.getId() + DELIMITER + fragmentProcessorId;
    }
}
