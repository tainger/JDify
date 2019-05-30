package io.terminus.dalaran.core.flow.model;

import lombok.Data;

import static io.terminus.dalaran.core.DalaranConstants.FLOW_FRAGMENT_PREFIX;
import static io.terminus.dalaran.core.DalaranConstants.FLOW_PREFIX;

@Data
public class FlowFragment extends BasicFlow {

    private String fragmentId;

    @Override
    public String getRouteId() {
        return FLOW_PREFIX + this.getId() + FLOW_FRAGMENT_PREFIX + fragmentId;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }
}
