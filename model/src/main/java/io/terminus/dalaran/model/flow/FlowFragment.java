package io.terminus.dalaran.model.flow;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.FLOW_FRAGMENT_PREFIX;
import static io.terminus.dalaran.DalaranConstants.FLOW_PREFIX;

@Data
public class FlowFragment extends BasicFlow {

    private String fragmentId;

    private String inModelType = "OBJECT";

    private final Map<String, Object> properties = new HashMap<>();

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
